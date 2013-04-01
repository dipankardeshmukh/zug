package com.automature.zug.engine;

import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;

import com.automature.davos.exceptions.DavosExecutionException;
import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

public class TestSuite {
	
	TestCase[] testcases;
	static String testSuitName = StringUtils.EMPTY;
	static	String testSuiteId =  StringUtils.EMPTY;
	static String testSuitRole = StringUtils.EMPTY;
	
	static String baseTestCaseID = StringUtils.EMPTY;
	private static String testsToRepeat = StringUtils.EMPTY;
	static Hashtable<String, String> errorMessageDuringTestCaseExecution = new Hashtable<String, String>();
	static Hashtable<String, String> errorMessageDuringMoleculeCaseExecution = new Hashtable<String, String>();
	static Hashtable<String, String> threadIdForTestCases = new Hashtable<String, String>();
	static boolean initWorkedFine = true;
	static Hashtable<String, Molecule> abstractTestCase = new Hashtable<String, Molecule>();
	static Hashtable<String, Prototype> prototypeHashTable = null;
	static Boolean _testPlanStopper = false;
	static Hashtable _testStepStopper = new Hashtable();
	static String testcasenotran;
	static Hashtable<String, ExecutedTestCase> executedTestCaseData = new Hashtable<String, ExecutedTestCase>();
	
	public void run() throws Exception,
	DavosExecutionException {
		Log.Debug("TestSuite/RunTestCaseForMain : Start of function");
		System.out.println("\n*** Number of TestCase in Chur Sheet "
				+ testcases.length + " ***\n ");
		System.out.println("\n*** Start Executing the testcases ***\n ");

		// Harness Specific ContextVariable to store AH_TPSTARTTIME = Timestamp
		// when Test Plan execution started
		// ZUG Specific ContextVariable to store ZUG_TPSTARTTIME = Timestamp
		// when Test Plan execution started
		ContextVar.setContextVar("ZUG_TPSTARTTIME", Utility.dateAsString());

		if (Controller.opts.repeatDurationSpecified) {
			int count = 1;
			// Normally we will log the debug logs unless one want to turn that
			// OFF
			// Specifically for the case of Longevity
			Controller.opts.isLongevityOn = true;

			Log.TurnOFFDebugLogs = true;

			HiPerfTimer testPlanStartTime = new HiPerfTimer();

			testPlanStartTime.Start();
			boolean testcasenotfound = false;
			for (TestCase test : testcases) {
				// If this is a cleanup Step, then dont run it now, that should
				// be handled at the end.
				// Or if a test case has no actions to execute, then dont
				// execute the test case.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)) {
					continue;
				}

				// If TestCaseId is specified in the command prompt, then make
				// sure, that
				// the current executing test case is also specified.

				if (test.testCaseID.compareToIgnoreCase("init") != 0) {
					if (StringUtils.isNotBlank(Controller.opts.manualTestCaseID)) {

						if (!Controller.opts.manualTestCaseID.toLowerCase().contains(
								test.testCaseID.toLowerCase().trim())) {
							// if
							// (!manualTestCaseID.equalsIgnoreCase(test.testCaseID.trim()))
							// {
							testcasenotfound = true;
							continue;
						} else {
							testcasenotfound = false;
						}
					}
				}

				if ((initWorkedFine == true)
						|| (initWorkedFine == false && (test.testCaseID
								.compareToIgnoreCase("cleanup") == 0))) {
					try {
						if (test.automated) {
							Log.Debug("TestSuite/Main : TestCase ID "
									+ test.testCaseID
									+ " is a Automated TestCase. Calling controller.RunTestCase...");
							// Function to run and Execute the TestCase

							if (test.testCaseID.compareToIgnoreCase("init") == 0) {

								TestCase tempTest = test.GenerateNewTestCaseID(count);
								// message("The generated Id 0a "+tempTest.testCaseID);
								tempTest.run();
							} else {
								// message("The generated Id 0b "+test.testCaseID);
								test.run();
							}
						} else {
							Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
							Controller.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
									+ test.testCaseID
									+ ". This is a Manual TestCase.");
							Controller.	message("********************************************************************************** ");
						}

					} catch (Exception e) {
						if (test.testCaseID.compareToIgnoreCase("init") == 0) {
							initWorkedFine = false;
							Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is an Initialization and it failed.");

						}
					}
				}

				if (!Controller.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+Controller. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}
			}
			// if(testcasenotfound)
			// message(manualTestCaseID+" The testcase is not Present in Chur Sheet");
			testPlanStartTime.Stop();

			Controller.opts.repeatDurationLong -= testPlanStartTime.Duration();

			// Make sure we run all the test cases, till the repeatDurationLong
			// is more than ONE minute = 1000 * 60 - this is not a magic number
			// :-).
			// We should not run the initialization and cleanup step in this
			// case.
			while (Controller.opts.repeatDurationLong > 1000 * 60) {

				testPlanStartTime.Start();
				count++;
				for (TestCase test : testcases) {
					if (!Controller.opts.debugMode) {
						if (_testPlanStopper) // Sleep for 500 ms, but wake up
							// immediately
						{
							Log.Error("TestSuite/RunTestCaseForMain : TestCase "
									+ test.testCaseID
									+ " took longer time to execute. Test Plan Time Specified = "
									+Controller. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
									+ " seconds  is over.");
							break;
						}
					}

					// If the test case have no action or is a Initialization or
					// a cleanup Step then Ignore the Test Case.
					if (test.actions.size() <= 0
							|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)
							|| (test.testCaseID.compareToIgnoreCase("init") == 0)) {
						continue;
					}

					// If TestCaseId is specified in the prompt, then make sure,
					// that
					// the current executing test case is also specified.
					if (test.testCaseID.compareTo("init") != 0) {

						if (StringUtils.isNotBlank(Controller.opts.manualTestCaseID)) {
							if (!Controller.opts.manualTestCaseID.toLowerCase().contains(
									test.testCaseID.toLowerCase().trim())) {
								continue;
							}
						}
					}

					// Make sure we run only those test cases, who are specified
					// for repeatation.
					// Other test cases are not repeated :-).
					// If nothing is specified, then all the test cases are
					// repeated.
					if (StringUtils.isNotBlank(testsToRepeat)) {
						if (!testsToRepeat.contains(test.testCaseID.trim())) {
							continue;
						}
					}

					if ((initWorkedFine == true)) {
						try {
							if (test.automated) {
								Log.Debug(String
										.format("TestSuite/Main : TestCase ID {%s} is a Automated TestCase. Calling controller.RunTestCase... ",
												test.testCaseID));
								// Function to run and Execute the TestCase
								if (!(test.testCaseID
										.compareToIgnoreCase("init") == 0)) {
									TestCase tempTest = test.GenerateNewTestCaseID(count);

									tempTest.run();
								} else {

									test.run();
								}

							} else {
								Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
								Controller.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
										+ test.testCaseID
										+ ". This is a Manual TestCase.");
								Controller.	message("********************************************************************************** ");
							}
						} catch (Exception e) {
							if (test.testCaseID.compareToIgnoreCase("init") == 0) {
								initWorkedFine = false;
								Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is an Initialization and it failed.");
							}
						}
					}

				}
				testPlanStartTime.Stop();
				Controller.opts.repeatDurationLong -= testPlanStartTime.Duration();
			}

			// At the end Run the Cleanup Step
			for (TestCase test : testcases) {
				if (!Controller.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified ="
								+Controller. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}

				// If the test case have no action or is not a cleanup Step,
				// then Ignore it.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") != 0)) {
					continue;
				}

				try {
					if (test.automated) {
						Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a Automated TestCase. Calling controller.RunTestCase... ");
						// Function to run and Execute the TestCase

						test.run();

					} else {
						Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
						Controller.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
								+ test.testCaseID
								+ ". This is a Manual TestCase.");
						Controller.message("********************************************************************************** ");
					}

				} catch (Exception e) {
				}
			}
		} // In the normal Scenario - when the RepeatDuration is not specified,
		// or
		// the
		// RepeatCount is specified, then use the following code snippet -
		// the
		// code
		// or the control will follow the below
		else {
			int count = 1;
			if (Controller.opts.repeatCount > 1) {
				// Normally we will log the debug logs unless one want to turn
				// that OFF
				// Specifically for the case of Longevity
				Controller.opts.isLongevityOn = true;
				Log.TurnOFFDebugLogs = true;
				Controller.message("Repeat Count is on   " +Controller. opts.isLongevityOn);
			}
			boolean testcasenotpresent = false;
			for (TestCase test : testcases) {
				// If this is a cleanup Step, then dont run it now, that should
				// be handled at the end.
				// Or if a test case has no actions to execute, then dont
				// execute the test case.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)) {
					continue;
				}

				// If TestCaseId is specified in the command prompt, then make
				// sure, that
				// the current executing test case is also specified.

				if (test.testCaseID.compareToIgnoreCase("init") != 0) {
					if (StringUtils.isNotBlank(Controller.opts.manualTestCaseID)) {
						// message("The manual commandline 1b " +
						// manualTestCaseID + "\n The Testcase ids " +
						// test.testCaseID);
						if (!Controller.opts.manualTestCaseID.toLowerCase().contains(
								test.testCaseID.toLowerCase().trim())) {
							// if(!manualTestCaseID.equalsIgnoreCase(test.testCaseID.trim())){

							testcasenotpresent = true;

							continue;
						} else {
							testcasenotpresent = false;
						}

					}
				}
				// message("The testcase step comming alpha " +
				// test.testCaseID);

				if ((initWorkedFine == true)
						|| (initWorkedFine == false && (test.testCaseID
								.compareToIgnoreCase("cleanup") == 0))) {
					try {
						if (test.automated) {
							Log.Debug("TestSuite/Main : TestCase ID "
									+ test.testCaseID
									+ " is a Automated TestCase. Calling controller.RunTestCase... ");
							// Function to run and Execute the TestCase
							if (!(test.testCaseID.compareToIgnoreCase("init") == 0)
									&&Controller. opts. isLongevityOn) {
								TestCase tempTest = test.GenerateNewTestCaseID(count);
								// message("The Testcases to match1a " +
								// tempTest.testCaseID);
								tempTest.run();
							} else {
								test.threadID = (String.valueOf(Thread
										.currentThread().getId()));
							}
							// message("The generated Id 2a " +
							// test.testCaseID);
							test.run();
						} else {
							Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
							Controller.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
									+ test.testCaseID
									+ ". This is a Manual TestCase.");
							Controller.	message("********************************************************************************** ");
						}

					} catch (Exception e) {
						if (test.testCaseID.compareToIgnoreCase("init") == 0) {
							initWorkedFine = false;
							Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is an Initialization and it failed.");

						}
					}
				}

				if (!Controller.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+Controller. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}
			}
			// if (testcasenotpresent) {
			// //message(manualTestCaseID +
			// " The testcase is not Present in Chur Sheet");
			// }
			Controller.	opts.repeatCount--;

			// Make sure we run all the test cases, till the RepeatCount is
			// equal to 0.
			// We should not run the initialization and cleanup step in this
			// case.
			while (Controller.opts.repeatCount > 0) {
				count++;
				for (TestCase test : testcases) {
					if (!Controller.opts.debugMode) {
						if (_testPlanStopper) // Sleep for 500 ms, but wake up
							// immediately
						{
							Log.Error("TestSuite/RunTestCaseForMain : TestCase "
									+ test.testCaseID
									+ " took longer time to execute. Test Plan Time Specified = "
									+Controller. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
									+ " seconds  is over.");
							break;
						}
					}

					// If the test case have no action or is a Initialization or
					// a cleanup Step then Ignore the Test Case.
					if (test.actions.size() <= 0
							|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)
							|| (test.testCaseID.compareToIgnoreCase("init") == 0)) {
						continue;
					}

					// If TestCaseId is specified in the command prompt, then
					// make sure, that
					// the current executing test case is also specified.
					if (test.testCaseID.compareToIgnoreCase("init") != 0) {
						// message("The manual commandline 1c" +
						// manualTestCaseID + "\n The Testcase ids " +
						// test.testCaseID);
						if (StringUtils.isNotBlank(Controller.opts.manualTestCaseID)) {
							if (!Controller.opts.manualTestCaseID.toLowerCase().contains(
									test.testCaseID.toLowerCase().trim())) {
								continue;
							}
						}
					}

					// Make sure we run only those test cases, who are specified
					// for repeatation.
					// Other test cases are not repeated :-).
					// If nothing is specified, then all the test cases are
					// repeated.
					if (StringUtils.isNotBlank(testsToRepeat)) {
						if (!testsToRepeat.contains(test.testCaseID.trim())) {
							continue;
						}
					}

					if ((initWorkedFine == true)) {
						try {
							if (test.automated) {
								Log.Debug("TestSuite/Main : TestCase ID "
										+ test.testCaseID
										+ " is a Automated TestCase. Calling controller.RunTestCase... ");
								// Function to run and Execute the TestCase

								if (!(test.testCaseID
										.compareToIgnoreCase("init") == 0)
										&& Controller.opts.isLongevityOn) {
									TestCase tempTest = test.GenerateNewTestCaseID(count);
									// message("The Testcases to match1b " +
									// tempTest.testCaseID);
									tempTest.run();
								} else {
									test.run();
								}
							} else {
								Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
								Controller.message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
										+ test.testCaseID
										+ ". This is a Manual TestCase.");
								Controller.message("********************************************************************************** ");
							}

						} catch (Exception e) {
							if (test.testCaseID.compareToIgnoreCase("init") == 0) {
								initWorkedFine = false;
								Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is an Initialization and it failed.");
							}
						}
					}

				}
				Controller.opts.repeatCount--;
			}

			// At the end Run the Cleanup Step
			for (TestCase test : testcases) {
				if (!Controller.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+ Controller.ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}

				// If the test case have no action or is not a cleanup Step,
				// then Ignore it.
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") != 0)) {
					continue;
				}

				try {
					if (test.automated) {
						Log.Debug("TestSuite/Main : TestCase ID "
								+ test.testCaseID
								+ " is a Automated TestCase. Calling controller.RunTestCase... ");
						// Function to run and Execute the TestCase

						test.run();
					} else {
						Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
						Controller.message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
								+ test.testCaseID
								+ ". This is a Manual TestCase.");
						Controller.	message("********************************************************************************** ");
					}

				} catch (Exception e) {
					// Log.Error("TestSuite/RunTestCaseForMain : Error occured, Excetion message is : "
					// + e.getMessage());
					throw new Exception(
							"TestSuite/RunTestCaseForMain : Error occured, Excetion message is : "
									+ e.getMessage());
				}
			}
		}

		Log.Debug("TestSuite/RunTestCaseForMain : End of function");
	}



}
