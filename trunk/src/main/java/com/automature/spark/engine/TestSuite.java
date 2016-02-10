package com.automature.spark.engine;

import java.util.Hashtable;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.automature.davos.exceptions.DavosExecutionException;
import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

public class TestSuite {

	TestCase[] testcases;
	public static String testSuitName = StringUtils.EMPTY;
	static	String testSuiteId =  StringUtils.EMPTY;
	static String testSuitRole = StringUtils.EMPTY;

	static String baseTestCaseID = StringUtils.EMPTY;
	private static String testsToRepeat = StringUtils.EMPTY;
	static Hashtable<String, String> errorMessageDuringTestCaseExecution = new Hashtable<String, String>();
	static Hashtable<String, String> errorMessageDuringMoleculeCaseExecution = new Hashtable<String, String>();
	static Hashtable<String, String> threadIdForTestCases = new Hashtable<String, String>();

	static boolean initWorkedFine = true;
    static boolean initExecuted = false;

	static Hashtable<String, Molecule> abstractTestCase = new Hashtable<String, Molecule>();
	static Hashtable<String, Prototype> prototypeHashTable = null;
	static Boolean _testPlanStopper = false;
	static Hashtable _testStepStopper = new Hashtable();
	static String testcasenotran;
	public static NavigableMap<String, ExecutedTestCase> executedTestCaseData = new TreeMap<String, ExecutedTestCase>();
	static boolean implicitTCMolecule=false;
	static boolean implicitTSMolecule=false;
	static String implicitTSMoleculeName="Zstep_Verify".toLowerCase();
	static String implicitTCMoleculeName="Zcase_Verify".toLowerCase();
	static boolean firstTestCaseExecuted=false;
	static boolean implicitEnvMolecule=false;
	private static String implicitEnvMoleculeName="zenv_sensor"; 


	private static void clearStaticMembers(){

		errorMessageDuringTestCaseExecution.clear();
		errorMessageDuringMoleculeCaseExecution.clear();
		threadIdForTestCases.clear();
		abstractTestCase.clear(); 
		_testStepStopper.clear();
		executedTestCaseData.clear();
		implicitTSMoleculeName="zstep_verify";
		implicitTCMoleculeName="zcase_verify";
		implicitEnvMoleculeName="zenv_sensor";
		testSuitName = StringUtils.EMPTY;
		testSuiteId =  StringUtils.EMPTY;
		testSuitRole = StringUtils.EMPTY;
		testsToRepeat = StringUtils.EMPTY;
		initWorkedFine = true;

	}

	static void cleanUP(){

		clearStaticMembers();
		initWorkedFine = true;
		_testPlanStopper = false;
		implicitTCMolecule=false;
		implicitTSMolecule=false;
		firstTestCaseExecuted=false;
		implicitEnvMolecule=false;
	}

	private void implicitTestCaseCall(TestCase test)throws Exception{

		if(implicitTCMolecule && !(test.testCaseID.toLowerCase().equalsIgnoreCase("cleanup")) && !(test.testCaseID.toLowerCase().equalsIgnoreCase("init"))){
			Molecule implicitMolecule=abstractTestCase.get(implicitTCMoleculeName);
			implicitMolecule.setCallingtTestCaseSTACK(test.stackTrace);
			implicitMolecule.setParentTestCaseID(test.parentTestCaseID);
			implicitMolecule.run();
		}
		if(implicitEnvMolecule && !firstTestCaseExecuted){
			Molecule implicitMolecule=abstractTestCase.get(implicitEnvMoleculeName);
			implicitMolecule.setCallingtTestCaseSTACK(test.stackTrace);
			implicitMolecule.setParentTestCaseID(test.parentTestCaseID);
			implicitMolecule.run();

			if(Spark.opts.dbReporting)
			{
				try{
					//Controller.reporter.setTestCycleTopologySetValues(ContextVar.getContextVar("ZEnv_Values"));
					if(!ContextVar.getContextVar("ZEnv_Values").isEmpty())
					 Spark.reporter.setTestCycleTopologySetValues(ContextVar.getContextVar("ZEnv_Values"));
					else{
						Log.Error("ZEnv_Values list is empty");
					}
				}
				catch(Exception e)
				{
					Log.Error("Exeption while saving environment list :"+e.getMessage());
				}
			}
		}
	}

	private void initializeImplicitCallsValues(){
		implicitTCMoleculeName=Excel.mainNameSpace+"."+implicitTCMoleculeName;
		implicitTSMoleculeName=Excel.mainNameSpace+"."+implicitTSMoleculeName;
		implicitEnvMoleculeName=Excel.mainNameSpace+"."+implicitEnvMoleculeName;

		if(abstractTestCase.containsKey(implicitTCMoleculeName)){
			implicitTCMolecule=true;
			//		System.out.println("implicitTCMolecule= "+implicitTCMolecule);
		}
		if(abstractTestCase.containsKey(implicitTSMoleculeName)){
			implicitTSMolecule=true;
			//		System.out.println("implicitTSMolecule="+implicitTSMolecule);
		}
		if(abstractTestCase.containsKey(implicitEnvMoleculeName)){
			//		System.out.println("implicitENVMolecule="+implicitEnvMoleculeName);
			implicitEnvMolecule=true;
		}
		    //      System.out.println("implicit values initialized");
	}


    public long waitForInitToComplete(long duration) throws InterruptedException {


        boolean initPresent = false;
        long initStartTime = System.currentTimeMillis();
        long timeOut = System.currentTimeMillis() + (duration * 1000);

        for (TestCase test : testcases) {
            if(test.testCaseID.equalsIgnoreCase("init"))
                initPresent=true;
        }

        if(initPresent){
            while(!initExecuted || (timeOut < System.currentTimeMillis())){
                Thread.sleep(10*1000);
            }
        }

        return System.currentTimeMillis() - initStartTime;

    }

	public void run() throws 
	DavosExecutionException,Exception,Throwable {
		//System.out.println("Test Suite run:"+Excel._indexedMacroTable.toString());
		//System.out.println("Test Suite run:"+Excel._macroSheetHashTable.toString());
		Log.Debug("TestSuite/RunTestCaseForMain : Start of function");
		Spark.message("\n*** Number of TestCase : "
				+ testcases.length + " ***\n ");
		Spark.message("\n*** Starting Execution of TestCases ***\n ");

		initializeImplicitCallsValues();

		// Harness Specific ContextVariable to store AH_TPSTARTTIME = Timestamp
		// when Test Plan execution started
		// ZUG Specific ContextVariable to store ZUG_TPSTARTTIME = Timestamp
		// when Test Plan execution started
		ContextVar.setContextVar("ZUG_TPSTARTTIME", Utility.dateAsString());

		if (Spark.opts.repeatDurationSpecified) {
			int count = 1;
			// Normally we will log the debug logs unless one want to turn that
			// OFF
			// Specifically for the case of Longevity
			Spark.opts.isLongevityOn = true;

			Log.TurnOFFDebugLogs = true;

			HiPerfTimer testPlanStartTime = new HiPerfTimer();

			testPlanStartTime.Start();
			boolean testcasenotfound = false;
			for (TestCase test : testcases) {
				// If this is a cleanup Step, then dont run it now, that should
				// be handled at the end.
				// Or if a test case has no actions to execute, then dont
				// execute the test case.
				if(Spark.stop){
					return;
				}
				if (test.actions.size() <= 0
						|| (test.testCaseID.compareToIgnoreCase("cleanup") == 0)) {
					continue;
				}

				// If TestCaseId is specified in the command prompt, then make
				// sure, that
				// the current executing test case is also specified.

				if (test.testCaseID.compareToIgnoreCase("init") != 0) {
					if (StringUtils.isNotBlank(Spark.opts.manualTestCaseID)) {
						/*
						if (!Controller.opts.manualTestCaseID.toLowerCase().equals(
								test.testCaseID.toLowerCase().trim())) {	
						 */						// if
						// (!manualTestCaseID.equalsIgnoreCase(test.testCaseID.trim()))
						// {

						if(!Spark.opts.testCaseIds.contains(test.testCaseID.toLowerCase().trim()))	{
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
								this.implicitTestCaseCall(tempTest);

							} else {
								// message("The generated Id 0b "+test.testCaseID);
							
								test.run();
								this.implicitTestCaseCall(test);
								TestSuite.firstTestCaseExecuted=true;
							}
						} else {
							Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
							Spark.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
									+ test.testCaseID
									+ ". This is a Manual TestCase.");
							Spark.	message("********************************************************************************** ");
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

				if (!Spark.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+Spark. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}
			}
			// if(testcasenotfound)
			// message(manualTestCaseID+" The testcase is not Present in Chur Sheet");
			testPlanStartTime.Stop();

			Spark.opts.repeatDurationLong -= testPlanStartTime.Duration();

			// Make sure we run all the test cases, till the repeatDurationLong
			// is more than ONE minute = 1000 * 60 - this is not a magic number
			// :-).
			// We should not run the initialization and cleanup step in this
			// case.
			while (Spark.opts.repeatDurationLong > 1000 * 60) {

				testPlanStartTime.Start();
				count++;
				for (TestCase test : testcases) {
					if (!Spark.opts.debugMode) {
						if (_testPlanStopper) // Sleep for 500 ms, but wake up
							// immediately
						{
							Log.Error("TestSuite/RunTestCaseForMain : TestCase "
									+ test.testCaseID
									+ " took longer time to execute. Test Plan Time Specified = "
									+Spark. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
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

						if (StringUtils.isNotBlank(Spark.opts.manualTestCaseID)) {
							//if (!Controller.opts.manualTestCaseID.toLowerCase().equals(
							//	test.testCaseID.toLowerCase().trim())) {
							if(!Spark.opts.testCaseIds.contains(test.testCaseID.toLowerCase().trim()))	{
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
									this.implicitTestCaseCall(tempTest);
								} else {
									test.run();
									this.implicitTestCaseCall(test);
								}
								TestSuite.firstTestCaseExecuted=true;

							} else {
								Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
								Spark.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
										+ test.testCaseID
										+ ". This is a Manual TestCase.");
								Spark.	message("********************************************************************************** ");
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
				Spark.opts.repeatDurationLong -= testPlanStartTime.Duration();
			}

			// At the end Run the Cleanup Step
			for (TestCase test : testcases) {
				if (!Spark.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified ="
								+Spark. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
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
						this.implicitTestCaseCall(test);
						TestSuite.firstTestCaseExecuted=true;

					} else {
						Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
						Spark.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
								+ test.testCaseID
								+ ". This is a Manual TestCase.");
						Spark.message("********************************************************************************** ");
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
			if (Spark.opts.repeatCount > 1) {
				// Normally we will log the debug logs unless one want to turn
				// that OFF
				// Specifically for the case of Longevity
				Spark.opts.isLongevityOn = true;
				Log.TurnOFFDebugLogs = true;
				Spark.message("Repeat Count is on   " +Spark. opts.isLongevityOn);
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
					if (StringUtils.isNotBlank(Spark.opts.manualTestCaseID)) {
						// message("The manual commandline 1b " +
						// manualTestCaseID + "\n The Testcase ids " +
						// test.testCaseID);
						//	if (!Controller.opts.manualTestCaseID.toLowerCase().equals(
						//		test.testCaseID.toLowerCase().trim())) {
						// if(!manualTestCaseID.equalsIgnoreCase(test.testCaseID.trim())){
						if(!Spark.opts.testCaseIds.contains(test.testCaseID.toLowerCase().trim()))	{
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
									&&Spark. opts. isLongevityOn) {
								TestCase tempTest = test.GenerateNewTestCaseID(count);
								// message("The Testcases to match1a " +
								// tempTest.testCaseID);
								tempTest.run();
								this.implicitTestCaseCall(tempTest);
							} else {
								test.threadID = (String.valueOf(Thread
										.currentThread().getId()));
							}
							// message("The generated Id 2a " +
							// test.testCaseID);
						
							test.run();
							this.implicitTestCaseCall(test);
							TestSuite.firstTestCaseExecuted=true;
						} else {
							Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
									+ test.testCaseID
									+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
							Spark.	message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
									+ test.testCaseID
									+ ". This is a Manual TestCase.");
							Spark.	message("********************************************************************************** ");
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

				if (!Spark.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+Spark. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
								+ " seconds  is over.");
						break;
					}
				}
			}
			// if (testcasenotpresent) {
			// //message(manualTestCaseID +
			// " The testcase is not Present in Chur Sheet");
			// }
			Spark.	opts.repeatCount--;

			// Make sure we run all the test cases, till the RepeatCount is
			// equal to 0.
			// We should not run the initialization and cleanup step in this
			// case.
			while (Spark.opts.repeatCount > 0) {
				count++;
				for (TestCase test : testcases) {
					if (!Spark.opts.debugMode) {
						if (_testPlanStopper) // Sleep for 500 ms, but wake up
							// immediately
						{
							Log.Error("TestSuite/RunTestCaseForMain : TestCase "
									+ test.testCaseID
									+ " took longer time to execute. Test Plan Time Specified = "
									+Spark. ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
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
						if (StringUtils.isNotBlank(Spark.opts.manualTestCaseID)) {
							//	if (!Controller.opts.manualTestCaseID.toLowerCase().equals(
							//		test.testCaseID.toLowerCase().trim())) {
							if(!Spark.opts.testCaseIds.contains(test.testCaseID.toLowerCase().trim()))	{
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
										&& Spark.opts.isLongevityOn) {
									TestCase tempTest = test.GenerateNewTestCaseID(count);
									// message("The Testcases to match1b " +
									// tempTest.testCaseID);
									tempTest.run();
									this.implicitTestCaseCall(tempTest);
								} else {
									test.run();
									this.implicitTestCaseCall(test);
								}
								TestSuite.firstTestCaseExecuted=true;
							} else {
								Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
										+ test.testCaseID
										+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
								Spark.message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
										+ test.testCaseID
										+ ". This is a Manual TestCase.");
								Spark.message("********************************************************************************** ");
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
				Spark.opts.repeatCount--;
			}

			// At the end Run the Cleanup Step
			for (TestCase test : testcases) {
				if (!Spark.opts.debugMode) {
					if (_testPlanStopper) // Sleep for 500 ms, but wake up
						// immediately
					{
						Log.Error("TestSuite/RunTestCaseForMain : TestCase "
								+ test.testCaseID
								+ " took longer time to execute. Test Plan Time Specified = "
								+ Spark.ReadContextVariable("ZUG_TESTSUITE_TIMEOUT")
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
						this.implicitTestCaseCall(test);
						TestSuite.firstTestCaseExecuted=true;
					} else {
						Log.Debug("TestSuite/RunTestCaseForMain : TestCase ID "
								+ test.testCaseID
								+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
						Spark.message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
								+ test.testCaseID
								+ ". This is a Manual TestCase.");
						Spark.	message("********************************************************************************** ");
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
