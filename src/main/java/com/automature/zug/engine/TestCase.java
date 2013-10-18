package com.automature.zug.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import java.util.List;

import java.util.Stack;

import org.apache.commons.lang.StringUtils;


import com.automature.zug.exceptions.ReportingException;
import com.automature.zug.gui.ZugGUI;
import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;


/**
 * Class to represent a test case. A test case contain a number of Actions and these actions can itself contain
 * a number of verifications
 */ 
class TestCase
{

	public String parentTestCaseID 	= null;
	public String nameSpace 		= null;
	public String stackTrace 		= null;
	public String testCaseID 		= null;
	public String testCaseDescription = null;
	public String user 				= null;
	public UserData userObj;
	/* DataStructure to represent multiple actions for a test case.*/
	public ArrayList<Action> actions 		= new ArrayList<Action>();
	/* Two properties of a Test Case 
   1. Is the Test Case an Automated Test Case or Not
   2. On Expansion, whether we need to run the test cases in paralle.
   By default the test case will be automated unless until it is marked as a Manual Test Case.*/
	public Boolean automated = true;
	public Boolean isConcurrentMoleculeCall=false;
	public Boolean concurrentExecutionOnExpansion = false;
	//This is the thread ID of the Test Case or the parent Process
	public String threadID = null;
	//   //Molecule argument Definition
	public ArrayList<String> _testcasemoleculeArgDefn=new ArrayList<String>();
	//public HashMap<String,String>_testcasemoleculekeyvalueDefn=new HashMap<String, String>();
	public HashMap<String,ArrayList<MultiValuedMacro>> mvm_macro_variable_map = new HashMap<String,ArrayList<MultiValuedMacro>>();
	public HashMap<String,String> mvm_value_map=new HashMap<String,String>(); 
	 boolean returnFlag=false;
	 public boolean breakpoint=false;
		public List breakpoints=null;
		
	static boolean errorOccured=false; 

	public TestCase(){

	}

	TestCase(TestCase tc){
		super();
		this.parentTestCaseID = tc.parentTestCaseID;
		this.nameSpace = tc.nameSpace;
		this.stackTrace = tc.stackTrace;
		this.testCaseID = tc.testCaseID;
		this.testCaseDescription = tc.testCaseDescription;
		this.user = tc.user;
		this.userObj =tc. userObj;
		//this.actions =tc. actions;
		this.automated = tc.automated;
		this.isConcurrentMoleculeCall = tc.isConcurrentMoleculeCall;
		this.concurrentExecutionOnExpansion = tc.concurrentExecutionOnExpansion;
		this.threadID =tc. threadID;
		this._testcasemoleculeArgDefn = tc._testcasemoleculeArgDefn;
		this.mvm_macro_variable_map =tc. mvm_macro_variable_map;
		this.mvm_value_map =tc. mvm_value_map;
		actions 		= new ArrayList<Action>();
		this.breakpoint=tc.breakpoint;
		this.breakpoints=tc.breakpoints;
	}
	
	public static void cleanUP(){
		errorOccured=false;
	}
	
	public boolean checkBreakPoint(int step){

		//System.out.println("step-"+step);
		step++;
	
			//	System.out.println("break point table"+Controller.breakpoints.keySet().toString());
			//	System.out.println("test case id"+this.testCaseID);
		
			if(this.breakpoint){
				//		System.out.println("break point");
				//	List steps=Controller.breakpoints.get(this.testCaseID);
				//	System.out.println(steps.toString());

				if(breakpoints!=null && breakpoints.contains(""+step)){
					//System.out.println("Step"+""+breakpoints.toString());
					//	System.out.println("["+this.stackTrace+"]:Break point,Please press the resume button from the debugger console");
					Controller.pause=true;

					return true;
				}
			}

		
		return false;
	}

	TestCase GenerateNewTestCaseID(int count) {
		Log.Debug("TestCase/GenerateNewTestCaseID: Start of function with a new TestCase. TestCase ID is "
				+ this.testCaseID + " and count = " + count);

		TestCase tempTestCase = new TestCase(this);
		tempTestCase.testCaseID = this.testCaseID + "\\" + count;
		Log.Debug("TestCase/GenerateNewTestCaseID: tempTestCase.testCaseID = "
				+ tempTestCase.testCaseID);

		tempTestCase.parentTestCaseID = tempTestCase.testCaseID;
		Log.Debug("TestCase/GenerateNewTestCaseID: tempTestCase.parentTestCaseID = "
				+ tempTestCase.parentTestCaseID);
		tempTestCase.stackTrace = tempTestCase.testCaseID;

		for (Action action : this.actions) {
			Action tempAction = new Action(action);
			tempAction.testCaseID = tempTestCase.testCaseID;
			tempAction.parentTestCaseID = tempTestCase.testCaseID;
			tempAction.stackTrace = tempTestCase.stackTrace;

			Log.Debug("TestCase/GenerateNewTestCaseID: Working on Action "
					+ action.name + " with Step Number as " + action.step);
			Log.Debug("TestCase/GenerateNewTestCaseID: Number of Action Arguments are : "
					+ action.arguments.size()
					+ " for action : "
					+ action.name);
			@SuppressWarnings("unused")
			ArrayList<String> tempActionArguments = new ArrayList<String>();

			tempAction.actualArguments = new ArrayList<String>(
					action.actualArguments);
			tempAction.arguments = new ArrayList<String>(
					action.arguments);

			Verification[] verifications = new Verification[action.verification
			                                                .size()];
			action.verification.toArray(verifications);
			Log.Debug("TestCase/GenerateNewTestCaseID: Number of verifications are : "
					+ verifications.length
					+ " for Action : "
					+ action.name);
			for(Verification verification:action.verification){
				Verification tempVerification = new Verification(verification);
				tempVerification.parentTestCaseID = tempTestCase.testCaseID;
				Log.Debug("TestCase/GenerateNewTestCaseID: tempVerification.parentTestCaseID =  : "
						+ tempVerification.parentTestCaseID);

				tempVerification.stackTrace = tempTestCase.stackTrace;

				Log.Debug("TestCase/GenerateNewTestCaseID: Number of Verification Arguments = "
						+ verification.arguments.size()
						+ " fon Verification " + verification.name);
				tempVerification.arguments = new ArrayList<String>(
						verification.arguments);
				tempVerification.actualArguments = new ArrayList<String>(
						verification.actualArguments);

				tempAction.verification.add(tempVerification);
			}
			tempTestCase.actions.add(tempAction);
		}

		Log.Debug("TestCase/GenerateNewTestCaseID: End of function with a new TestCase. TestCase ID is "
				+ this.testCaseID);
		return tempTestCase;
	}


	private HashMap<String, String> findVariablesValueForTestCase() {
		HashMap<String, String> variablevalueMap = new HashMap<String, String>();
		// message("The finding variables started "+test.testCaseID);
		Action test_action_arr[] = new Action[this.actions.size()];
		this.actions.toArray(test_action_arr);
		for (int i = 0; i < test_action_arr.length; i++) {
			Action testcase_actions = test_action_arr[i];
			// message("the varabless are "+testcase_actions.actionArguments+"\n Action Actual Arguments "+testcase_actions.actionActualArguments);
			//	for(Action testcase_actions:this.actions){
			if (testcase_actions.arguments.size() == testcase_actions.actualArguments
					.size()) {
				for (int j = 0; j < testcase_actions.arguments.size(); j++) {
					String variable_name = testcase_actions.actualArguments
							.get(j);
					// message("the varabless are "+variable_name);
					if (variable_name.startsWith("$$")) {
						if (variable_name.startsWith("$$%")
								&& variable_name.endsWith("%")) {
							// System.out.println("Variable name "+variable_name+" value "+testcase_actions.actionArguments.get(j));
							String contextvar_name = variable_name.replaceAll(
									"%", "");
							variablevalueMap.put(contextvar_name,
									testcase_actions.arguments.get(j));
							// dont do any thing
						} else {

							variablevalueMap.put(variable_name,
									testcase_actions.arguments.get(j));
						}
					} else if (variable_name.contains("=")) {
						// variable_name =
						// Excel.SplitOnFirstEquals(variable_name)[1];
						// message("The value variable "+Excel.SplitOnFirstEquals(variable_name).length);
						variable_name = Excel.SplitOnFirstEquals(variable_name).length > 1 ? Excel
								.SplitOnFirstEquals(variable_name)[1]
										: variable_name;
								if (variable_name.startsWith("$$")) {
									if (variable_name.startsWith("$$%")
											&& variable_name.endsWith("%")) {
										// dont do any thing
										String contextvar_name = variable_name
												.replaceAll("%", "");
										variablevalueMap
										.put(contextvar_name,
												testcase_actions.arguments
												.get(j));
									} else {
										variablevalueMap
										.put(variable_name,
												testcase_actions.arguments
												.get(j));
									}
								}
					}
				}
			}

			for (Verification verify : testcase_actions.verification) {
				if (verify.arguments.size() == verify.actualArguments
						.size()) {
					for (int k = 0; k < verify.actualArguments
							.size(); k++) {
						String variable_name = verify.actualArguments
								.get(k);
						if (variable_name.startsWith("$$")) {
							if (variable_name.startsWith("$$%")
									&& variable_name.endsWith("%")) {
								// dont do any thing
								String contextvar_name = variable_name
										.replaceAll("%", "");
								variablevalueMap.put(contextvar_name,
										verify.arguments.get(k));
							} else {
								variablevalueMap.put(variable_name,
										verify.arguments.get(k));
							}
						} else if (variable_name.contains("=")) {
							// variable_name =
							// Excel.SplitOnFirstEquals(variable_name)[1];
							//Controller. message("The value variable "+Excel.SplitOnFirstEquals(variable_name).length);
							variable_name = Excel
									.SplitOnFirstEquals(variable_name).length > 1 ? Excel
											.SplitOnFirstEquals(variable_name)[1]
													: variable_name;
											if (variable_name.startsWith("$$")) {
												if (variable_name.startsWith("$$%")
														&& variable_name.endsWith("%")) {
													// dont do any thing
													String contextvar_name = variable_name
															.replaceAll("%", "");
													variablevalueMap
													.put(contextvar_name,
															verify.arguments
															.get(k));
												} else {
													variablevalueMap
													.put(variable_name,
															verify.arguments
															.get(k));
												}
											}
						}
					}
				}
			}

		}

		Log.Debug("TestCase/findVariablesValueForTestCase: The Variable Map "
				+ variablevalueMap);
		return variablevalueMap;
	}






	/***
	 * This will be executed on a separate thread..This will run the expanded
	 * test case.
	 * 
	 *
	 */
	
	private void runExpandedTestCase() throws 
	ReportingException,Exception,Throwable {

		Log.Debug("TestCase/RunExpandedTestCase : Start of Function.");



		Log.Debug("TestCase/RunExpandedTestCase : Setting ZUG_TCID as ->"
				+ this.testCaseID);
		// Harness Specific ContextVariable to store Generated TestCase ID
		ContextVar.setContextVar("ZUG_TCID"+Thread.currentThread().getId(), this.testCaseID);
		Log.Debug("TestCase/RunExpandedTestCase : Successfully SET ZUG_TCID as ->"
				+ this.testCaseID);

		// Make sure that the Errors are removed for all the Test Cases at the
		// start.
		TestSuite.errorMessageDuringTestCaseExecution.put(this.parentTestCaseID,
				StringUtils.EMPTY);
		HiPerfTimer tm = new HiPerfTimer();
		try {
			Log.Debug("TestCase/RunExpandedTestCase : Running TestCase ID "
					+ this.testCaseID);
			Controller.message("******************************************************************************** ");
			Controller.message("\nRunning TestCase ID " + this.testCaseID
					+ " On(Current Date): " + Utility.getCurrentDateAsString());

			// Harness Specific ContextVariable to store AH_TCSTARTTIME
			// ZUG Specific ContextVariable to store ZUG_TCSTARTTIME = Timestamp
			// when Test Case execution started
			ContextVar.setContextVar("ZUG_TCSTARTTIME"+Thread.currentThread().getId(), Utility.dateAsString());
			// Method Return Variable names and Values.
			// message("Testcase is coming to RunExpandTestCase:"+ContextVar.getContextVar("ZUG_TCSTARTTIME")
			// );
			// If the testCase is not an Init or Cleanup Step then only Save the
			// TestCase Result to the Framework Database.
			if (!(TestSuite.baseTestCaseID.compareToIgnoreCase("cleanup") == 0 || TestSuite.baseTestCaseID
					.compareToIgnoreCase("init") == 0)) {
				ExecutedTestCase tData = new ExecutedTestCase();
				tData.testCaseID = this.testCaseID;
				tData.timeToExecute = 0;
				tData.testCaseExecutionComments = StringUtils.EMPTY;
				tData.testCaseStatus = "running";
				if (StringUtils.isNotBlank(this.testCaseDescription)) {
					tData.testcasedescription = this.testCaseDescription;
				} else {
					tData.testcasedescription = "";
				}
				TestSuite.	executedTestCaseData.put(tData.testCaseID, tData);


				if (Controller.opts.dbReporting == true) {
					// If the testCase is not an Init or Cleanup Step then only
					// Save the TestCase to the Result Database.
					if (!(TestSuite.baseTestCaseID.compareToIgnoreCase("cleanup") == 0 ||TestSuite. baseTestCaseID
							.compareToIgnoreCase("init") == 0)) {
						Log.Debug(String
								.format("TestCase/RunExpandedTestCase : Saving Expanded Testcase ID %s with Description %s to Result Davos.",
										this.testCaseID,
										this.testCaseDescription));
						// SaveTestCase(test.testCaseID,
						// test.testCaseDescription);

						
						try {
							Controller.reporter.SaveTestCaseResultEveryTime(tData);
							Log.Debug(String
									.format("TestCase/RunExpandedTestCase : SUCCESSFULLY SAVED Expanded Testcase ID %s with Description %s to Result Davos.",
											this.testCaseID,
											this.testCaseDescription));

						} catch (ReportingException de) {
							Log.Error(String
									.format("Failure in sending request to Davos.\n Davos Call: testexecutiondetail/write \nRequests sent: testcaseid=%s,testcasedescription=%s,testcycleid=%s,testcaseresult=%s ..... topologysetid=%s \nError Message: %s",
											tData.testCaseID,
											tData.testcasedescription,
											Controller.opts.testCycleId, tData.testCaseStatus,
											Controller.opts.topologySetId, de.getMessage()));
							if(Controller.guiFlag){
								throw new Throwable();
							}else{
								System.exit(1);
							}
						}
						try {
							Controller.reporter.saveTestCaseVariables(
									this.findVariablesValueForTestCase(),
									this.testCaseID, TestSuite.testSuitName);
						} catch (ReportingException de) {
							Log.Error(String
									.format("Failure in sending request to Davos.\n Davos Call: variables/write \nRequests sent: testsuitename=%s,testcaseidentifier=%s .... \nError Message: %s ",
											TestSuite.testSuitName, this.testCaseID,
											de.getMessage()));
							if(Controller.guiFlag){
								throw new Throwable();
							}else{
								System.exit(1);
							}
						}catch(Exception e){
							System.out.println("Exception caught");
							e.printStackTrace();
						}
					} else {
						Log.Debug(String
								.format("TestCase/RunExpandedTestCase : Testcase ID %s is of type Initialization/Cleanup",
										this.testCaseID));
					}
				}
			} else {
				Log.Debug(String
						.format("TestCase/RunExpandedTestCase : Testcase ID %s is of type Initialization/Cleanup",
								this.testCaseID));
			}

			// Now run each of the Actions mentioned here...and try running it.
			Action[] actions = new Action[this.actions.size()];
			this.actions.toArray(actions);
			Log.Debug("TestCase/RunExpandedTestCase:  Number of Actions to run is : "
					+ actions.length + " for TestCase ID : " + this.testCaseID);

			Hashtable<String, String> stepsKeys = new Hashtable<String, String>();
			// After getting the Actions Store the Steps somewhere...
			for (int i = 0; i < actions.length; i++) {
				Action action = actions[i];
				//System.out.println("Action args"+action.arguments);
				//message("Actions are coming "+action.actionName+" with argument "+action.actionArguments+" action step "+action.step);
				Log.Debug(String
						.format("TestCase/RunExpandedTestCase: Storing the Steps in a HashTable. Step Number = "
								+ action.step));
				stepsKeys.put(action.step, StringUtils.EMPTY);
				Log.Debug(String
						.format("TestCase/RunExpandedTestCase: Successfully stored Step Number = %s to the HashTable",
								action.step));
			}

			String errorDuringTestCaseExecution = StringUtils.EMPTY;
			// First Clear the Stack.
			Stack<String> actionsForCleanup = new Stack<String>();

			int count = 0;
			ArrayList<Thread> ThreadPool = new ArrayList<Thread>();
			String stepNumber = StringUtils.EMPTY;
			if (actions.length > 0) {
				// message("what are the steps "+actions[0].step);
				stepNumber = actions[0].step;
			}

			try {
				for (int i = 0; i < actions.length; i++) {
					final Action action = actions[i];
					count++;
					try{
						if(Controller.guiFlag){
                            Controller.gui.showRunningTestCase(action.testCaseID);
							Controller.gui.showRunningTestStep(action.lineNumber);
						}
					}catch(Exception e){
						//e.printStackTrace();
					}
					if (StringUtils.isBlank(action.name)) {
						continue;
					}
					if(Controller.stop)
					{
						return;
					}
					if(Controller.opts.debugger){
					/*	try{
							if(Controller.opts.debugger){
								sheetTableMapper stm=new sheetTableMapper();
								stm.setTestCaseTable(this);
								Controller.gui.getDebugger().setTestCaseData(stm.data, stm.colheader,stm.breakPoints);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						Controller.gui.getDebugger().currentTestStep(i);*/	
						
						
					//	if(checkBreakPoint(i)){
						ArrayList al=Controller.breakpoints.get(Excel.mainNameSpace);
						
						if(al!=null){
							//System.out.println("array list"+al.toString());
						//	System.out.println(action.lineNumber);
							if(al.contains(action.lineNumber)){
								//Controller.sendMessageToDebugger((Object)action);
							//	System.out.println("pausing execution");
								Controller.setPauseSignal();
								Controller.checkDebuggerSignal();
							}else if(Controller.stepOver){
								Controller.setPauseSignal();
								//Controller.sendMessageToDebugger((Object)action);
								Controller.checkDebuggerSignal();
							}
						}else if(Controller.stepOver){
							Controller.setPauseSignal();
							//Controller.sendMessageToDebugger((Object)action);
							Controller.checkDebuggerSignal();
						}
					}	

					Log.Debug("TestCase/RunExpandedTestCase:  Action : "
							+ action.name + " has Step Number as : "
							+ action.step);

					// If the action Steps contain an "i" and there is a cleanup
					// step for it then add that to the stack.
					if (action.step.endsWith("i")
							&& (stepsKeys.containsKey(action.step.substring(0,
									action.step.length() - 1) + "c") || stepsKeys
									.containsKey(action.step.substring(0,
											action.step.length() - 1) + "C"))) {
						Log.Debug("TestCase/RunExpandedTestCase:  Action : "
								+ action.name
								+ " with Step : "
								+ action.step
								+ " is an Initialization Action. A cleanup exist for this action ..so pusing this to the STACK");
						actionsForCleanup.push(action.step.substring(0,
								action.step.length() - 1));

					}

					// If this is a Cleanup action then break and run the
					// cleanup at the end.
					if (action.step.endsWith("c")
							&& actionsForCleanup.contains(action.step
									.substring(0, action.step.length() - 1))) {
						Log.Debug("TestCase/RunExpandedTestCase:  Action : "
								+ action.name
								+ " with Step : "
								+ action.step
								+ " is an Cleanup Action/STEP. so breaking and moving to cleanup");
						count--;

						break;
					}

					Log.Debug("TestCase/RunExpandedTestCase:  Calling RunAction for Action : "
							+ action.name
							+ " for TestCase ID : "
							+ this.testCaseID);

					// If this step is equivalent to the previous step then
					// Create a Thread and Run the same
					if (stepNumber.equals(action.step)
							&& StringUtils.isNotBlank(action.step)) {
						final Action tempAction = action;
						Thread thread = new Thread(new Runnable() {

							public void run() {
								tempAction.run();
								
								implicitTestStepCall();
							}
						});
						// thread.IsBackground = true;
						// During the Launching of a new Action....always reset
						// this Value...
						TestSuite._testStepStopper.put(this.parentTestCaseID, false);

						// Context Variable to store Timestamp when Test Step
						// execution started
						ContextVar.setContextVar("ZUG_TSSTARTTIME"+Thread.currentThread().getId(),
								Utility.dateAsString());
						thread.start();

						ThreadPool.add(thread);

					} // otherwise Wait for the Previous Threads to Complete and
					// then Start with the new process...
					else {
						// Wait for all the Threads to finish.
						for (int t = 0; t < ThreadPool.size(); ++t) {
							if (Controller.opts.debugMode) {
								((Thread) ThreadPool.get(t)).join();
							} else {
								((Thread) ThreadPool.get(t))
								.join(Integer
										.parseInt(Controller.ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);
								if (((Thread) ThreadPool.get(t)).isAlive()) {
										((Thread) ThreadPool.get(t)).interrupt();
									TestSuite._testStepStopper.put(this.parentTestCaseID,
											true);
								}
							}
						}
						// Lastly wait for the Threads running to get Over
						/*for (int t = 0; t < ThreadPool.size(); ++t) {
							((Thread) ThreadPool.get(t)).join();
						}*/

						ThreadPool.clear();

						// In case we get an Exception then Dont run any more
						// processes
						if (StringUtils
								.isNotBlank((((String) TestSuite.errorMessageDuringTestCaseExecution
										.get(this.parentTestCaseID))))) {
							// If the action Steps contain an "i" and there is a
							// cleanup step for it then remove that from the
							// stack.

							if (action.step.endsWith("i")
									&& (stepsKeys.containsKey(action.step
											.substring(0,
													action.step.length() - 1)
													+ "c") || stepsKeys
													.containsKey(action.step
															.substring(
																	0,
																	action.step
																	.length() - 1)
																	+ "C"))) {
								actionsForCleanup.pop();
							}

							break;
						}

						// The new step number is the current one
						stepNumber = action.step; 
						Thread thread = new Thread(new Runnable() {

							public void run() {
								action.run();
								implicitTestStepCall();
							}
						});
						// thread.IsBackground = true;
						// During the Launching of a new Action....always reset
						// this Value...
						TestSuite._testStepStopper.put(this.parentTestCaseID, false);

						// Context Variable to store Timestamp when Test Step
						// execution started
						ContextVar.setContextVar("ZUG_TSSTARTTIME"+Thread.currentThread().getId(),
								Utility.dateAsString());

						thread.start();
						ThreadPool.add(thread);

					}

					Log.Debug("TestCase/RunExpandedTestCase:  RunAction executed successfully for Action : "
							+ action.name
							+ " for TestCase ID : "
							+ this.testCaseID);

				}

				// Wait until oThread finishes. Join also has overloads
				// that take a millisecond interval or a TimeSpan object.
				for (int t = 0; t < ThreadPool.size(); ++t) {
					if (Controller.opts.debugMode) {
						((Thread) ThreadPool.get(t)).join();
					} else {
						((Thread) ThreadPool.get(t))
						.join(Integer
								.parseInt(Controller.ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);
						if (((Thread) ThreadPool.get(t)).isAlive()) {
							((Thread) ThreadPool.get(t)).interrupt();
							TestSuite._testStepStopper.put(this.parentTestCaseID, true);
						}

					}

				}

			/*	// Lastly wait for the Threads running to get Over
				for (int t = 0; t < ThreadPool.size(); ++t) {
					((Thread) ThreadPool.get(t)).join();
				}*/

				if (StringUtils
						.isNotBlank(((String) TestSuite.errorMessageDuringTestCaseExecution
								.get(this.parentTestCaseID)))) {
					Log.ErrorInLog(String
							.format("TestCase/RunExpandedTestCase: Exception while running test case %s.%s",
									this.testCaseID,
									(String) TestSuite.errorMessageDuringTestCaseExecution
									.get(this.parentTestCaseID)));
					errorDuringTestCaseExecution = String.format(
							"Exception while running test case %s.%s",
							this.testCaseID,
							(String)TestSuite. errorMessageDuringTestCaseExecution
							.get(this.parentTestCaseID));
				}

			} catch (Exception ex) {
				Log.ErrorInLog(String
						.format("TestCase/RunExpandedTestCase: Exception while running test case %s.%s",
								this.testCaseID, ex.toString()));
				errorDuringTestCaseExecution += String.format(
						"Exception while running test case %s.%s",
						this.testCaseID, ex.getMessage());
			} finally {
				boolean cleanupActionStarted = false;
				TestSuite._testStepStopper.put(this.parentTestCaseID, false);

				Log.Debug("TestCase/RunExpandedTestCase:  Number of testcases steps successfully executed are "
						+ count + " for TestCase ID : " + this.testCaseID);
				// In the finally iterate over all the cleanup steps that one
				// would like to perform
				// before the test case is finally said to be done..
				// if ((actionsForCleanup.Count > 0 &&
				// !_testPlanStopper.WaitOne(1, false)) ||
				// (actionsForCleanup.Count > 0 && doCleanupOnTimeout))
				if ((actionsForCleanup.size() > 0 && !TestSuite._testPlanStopper)
						|| (actionsForCleanup.size() > 0 &&Controller. opts.doCleanupOnTimeout)) {
					Controller.message("\n******************** Cleanup Action For TestCase : "
							+ this.stackTrace
							+ " Started. ***************************");

					for (int i = count; i < actions.length; ++i) {

						final Action action = actions[i];

						Log.Debug("TestCase/RunExpandedTestCase - Finally/Cleanup:  Working on Action  : "
								+ action.name
								+ " for TestCase ID : "
								+ this.testCaseID);

						if (!action.step.endsWith("c") && cleanupActionStarted) {// changes
							// done
							// from
							// cleanupActionStarted
							// from
							// !cleanupActionStarted
							try {
								Log.Debug("TestCase/RunExpandedTestCase - Finally/Cleanup:  Calling RunAction for Action : "
										+ action.name
										+ " for TestCase ID : "
										+ this.testCaseID);

								// If the action Steps contain an "i" and there
								// is a cleanup step for it then add that to the
								// stack.
								if (action.step.endsWith("i")
										&& (stepsKeys
												.containsKey(action.step
														.substring(
																0,
																action.step
																.length() - 1)
																+ "c") || stepsKeys
																.containsKey(action.step
																		.substring(
																				0,
																				action.step
																				.length() - 1)
																				+ "C"))) {
									Log.Debug("TestCase/RunExpandedTestCase:  Action : "
											+ action.name
											+ " with Step : "
											+ action.step
											+ " is an Initialization Action. A cleanup exist for this action ..so pushing this to the STACK");
									actionsForCleanup.push(action.step
											.substring(0,
													action.step.length() - 1));
								}
								if (stepNumber.equals(action.step)
										&& StringUtils.isNotBlank(action.step)) {
									Thread thread = new Thread(new Runnable() {

										public void run() {
											action.run();
											implicitTestStepCall();
										}
									});
									// thread.IsBackground = true;

									// During the Launching of a new
									// Action....always reset this Value...
									TestSuite._testStepStopper.put(this.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME"+Thread.currentThread().getId(),
											Utility.dateAsString());

									thread.start();
									ThreadPool.add(thread);

								} // otherwise Wait for the Previous Threads to
								// Complete and then Start with the new
								// process...
								else {
									// Wait for all the Threads to finish.
									for (int t = 0; t < ThreadPool.size(); ++t) {
										if (Controller.opts.debugMode) {
											((Thread) ThreadPool.get(t)).join();
										} else {
											((Thread) ThreadPool.get(t))
											.join(Integer
													.parseInt(Controller.ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);

											if (((Thread) ThreadPool.get(t))
													.isAlive()) {
												((Thread) ThreadPool.get(t)).interrupt();
											
												TestSuite._testStepStopper.put(
														this.parentTestCaseID,
														true);
											}
										}

									}

									// Lastly wait for the Threads running to
									// get Over
								/*	for (int t = 0; t < ThreadPool.size(); ++t) {
										((Thread) ThreadPool.get(t)).join();
									}*/

									ThreadPool.clear();

									// The new step number is the current one
									stepNumber = action.step;
									Thread thread = new Thread(new Runnable() {

										public void run() {
											action.run();
											implicitTestStepCall();
								
										}
									});
									// thread.IsBackground = true;
									// During the Launching of a new
									// Action....always reset this Value...
									TestSuite._testStepStopper.put(this.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME"+Thread.currentThread().getId(),
											Utility.dateAsString());
									thread.start();
									ThreadPool.add(thread);

								}

								Log.Debug("TestCase/RunExpandedTestCase - Finally/Cleanup:  RunAction executed successfully for Action : "
										+ action.name
										+ " for TestCase ID : "
										+ this.testCaseID);
							} catch (Exception e) {
								Log.ErrorInLog(String
										.format("TestCase/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												this.testCaseID, e.getMessage()));
								errorDuringTestCaseExecution += String
										.format("TestCase/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												this.testCaseID, e.getMessage());
							}
						} else if (action.step.endsWith("c")
								&& !actionsForCleanup
								.contains(action.step.substring(0,
										action.step.length() - 1))) {
							Log.Debug("TestCase/RunExpandedTestCase - Finally/Cleanup: For Action  : "
									+ action.name
									+ " Initialization is not Done. So Skipping it for TestCase ID : "
									+ this.testCaseID);

							continue;
						} else if ((action.step.endsWith("c")
								&& actionsForCleanup
								.contains(action.step.substring(0,
										action.step.length() - 1)) && !TestSuite._testPlanStopper)
										|| (action.step.endsWith("c")
												&& actionsForCleanup
												.contains(action.step
														.substring(
																0,
																action.step
																.length() - 1)) && Controller.opts.doCleanupOnTimeout)) {
							// This marks the start for one Cleanup Action.
							cleanupActionStarted = true;

							try {
								Log.Debug("TestCase/RunExpandedTestCase - Finally/Cleanup:  Calling RunAction for Action : "
										+ action.name
										+ " for TestCase ID : "
										+ this.testCaseID);

								if (stepNumber.equals(action.step)
										&& StringUtils.isNotBlank(action.step)) {
									Thread thread = new Thread(new Runnable() {

										public void run() {
											action.run();
											implicitTestStepCall();
										}
									});
									// thread.IsBackground = true;

									// During the Launching of a new
									// Action....always reset this Value...
									TestSuite._testStepStopper.put(this.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME"+Thread.currentThread().getId(),
											Utility.dateAsString());

									thread.start();
									ThreadPool.add(thread);

								} // otherwise Wait for the Previous Threads to
								// Complete and then Start with the new
								// process...
								else {
									// Wait for all the Threads to finish.
									for (int t = 0; t < ThreadPool.size(); ++t) {
										if (Controller.opts.debugMode) {
											((Thread) ThreadPool.get(t)).join();
										} else {
											((Thread) ThreadPool.get(t))
											.join(Integer
													.parseInt(Controller.ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);
											if (((Thread) ThreadPool.get(t))
													.isAlive()) {
												((Thread) ThreadPool.get(t)).interrupt();
											
												TestSuite._testStepStopper.put(
														this.parentTestCaseID,
														true);
											}
										}

									}

									// Lastly wait for the Threads running to
								/*	// get Over
									for (int t = 0; t < ThreadPool.size(); ++t) {
										((Thread) ThreadPool.get(t)).join();
									}*/

									ThreadPool.clear();

									// The new step number is the current one
									stepNumber = action.step;
									Thread thread = new Thread(new Runnable() {

										public void run() {
											action.run();
											implicitTestStepCall();
										}
									});
									// thread.IsBackground = true;
									// During the Launching of a new
									// Action....always reset this Value...
									TestSuite._testStepStopper.put(this.parentTestCaseID,
											false);

									// Context Variable to store Timestamp when
									// Test Step execution started
									ContextVar.setContextVar("ZUG_TSSTARTTIME"+Thread.currentThread().getId(),
											Utility.dateAsString());
									thread.start();
									ThreadPool.add(thread);

								}

								// RunAction(action);
								Log.Debug("TestCase/RunExpandedTestCase - Finally/Cleanup:  RunAction executed successfully for Action : "
										+ action.name
										+ " for TestCase ID : "
										+ this.testCaseID);
							} catch (Exception exp) {
								Log.ErrorInLog(String
										.format("TestCase/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												this.testCaseID,
												exp.getMessage()));
								errorDuringTestCaseExecution += String
										.format("TestCase/RunExpandedTestCase - Finally/Cleanup: Exception while running test case %s.%s",
												this.testCaseID,
												exp.getMessage());
							}
						}
					}

					for (int t = 0; t < ThreadPool.size(); ++t) {
						if (Controller.opts.debugMode) {
							((Thread) ThreadPool.get(t)).join();
						} else {
							((Thread) ThreadPool.get(t))
							.join(Integer
									.parseInt(Controller.ReadContextVariable("ZUG_TESTSTEP_TIMEOUT")) * 1000);

							if (((Thread) ThreadPool.get(t)).isAlive()) {
								((Thread) ThreadPool.get(t)).interrupt();
							
								TestSuite._testStepStopper.put(this.parentTestCaseID,
										true);
							}
						}
					}

					// Lastly wait for the Threads running to get Over
				/*for (int t = 0; t < ThreadPool.size(); ++t) {
						((Thread) ThreadPool.get(t)).join();
					}*/
					// message("The total teststepperput\t"+_testStepStopper);
					Controller.message("\n******************** Cleanup Action Finished For TestCase : "
							+ this.stackTrace + ". ***************************");
				}
			}
			tm.Stop();
			if (StringUtils.isNotBlank(errorDuringTestCaseExecution)) {
				Log.Debug("TestCase/RunExpandedTestCase: There were exception raised while running TestCase = "
						+ this.testCaseID
						+ " with Exception message as : "
						+ errorDuringTestCaseExecution);
				throw new Exception(errorDuringTestCaseExecution);
			}

			// If everything went well then LOG THAT the test case has PASSED.
			Log.Debug(String.format(
					"TestCase/RunExpandedTestCase : TestCase ID %s PASSED ",
					this.testCaseID));
			Controller.message(String.format(
					"\n\nSTATUS : PASS FOR  TestCase ID %s ",
					this.testCaseID + " On(Current Date): "
							+ Utility.getCurrentDateAsString()));

			// If the testCase is not an Init or Cleanup Step then only Save the
			// TestCase Result to the Framework Database.
			if (!(TestSuite.baseTestCaseID.compareToIgnoreCase("cleanup") == 0 ||TestSuite. baseTestCaseID
					.compareToIgnoreCase("init") == 0)) {
				ExecutedTestCase tData = new ExecutedTestCase();
				tData.testCaseCompletetionTime = Utility.dateNow();
				tData.testCaseID = this.testCaseID;
				tData.timeToExecute = (int) tm.Duration();
				tData.testCaseExecutionComments = StringUtils.EMPTY;
				tData.testCaseStatus = "pass";
				tData.testcasedescription=this.testCaseDescription;
				// if(!verbose)
				// {
				// showTestCaseResultEveryTime(tData);
				// }
				// And then Add the same at the last.
				TestSuite.executedTestCaseData.put(tData.testCaseID, tData);

				if (Controller.opts.dbReporting) {
					// message("Data is going to be saved through davos");
					Controller.reporter.SaveTestCaseResultEveryTime(tData);
					// message("Data is saved through davos");
				}
			}
		} catch (Exception ex) {
			//System.out.println("Inside test case exception");
			String failureReason = String
					.format("Status FAILED FOR Worksheet %s TestCase ID (%s:%s). Exception MESSAGE IS : \n%s .\nCause:%s",
							this.nameSpace, this.parentTestCaseID,
							this.testCaseID, ex.getMessage(), ex.getClass());
			Controller.	message("\n******************** Error Messages For Test Case "
					+ this.parentTestCaseID + " ***************************");
			if (Controller.opts.verbose)
				Log.Error(failureReason);
			Controller.		message("\n******************* Error Messages End For Test Case "
					+ this.parentTestCaseID + " **************************");
			Controller.		message(String.format(
					"\n\nSTATUS : FAIL FOR TestCase ID %s ",
					this.testCaseID + " On(Current Date): "
							+ Utility.getCurrentDateAsString()));

			// If the testCase is not an Init or Cleanup Step then only Save the
			// TestCase Result to the Framework Database.
			if (!(TestSuite.baseTestCaseID.compareToIgnoreCase("cleanup") == 0 ||TestSuite.baseTestCaseID
					.compareToIgnoreCase("init") == 0)) {
				ExecutedTestCase tData = new ExecutedTestCase();
				tData.testCaseCompletetionTime = Utility.dateNow();
				tData.testCaseID = this.testCaseID;
				tData.timeToExecute = (int) tm.Duration();
				// tData.testCaseExecutionComments =
				// (String)errorMessageDuringTestCaseExecution.get(test.parentTestCaseID)
				// + ex.getMessage();
				tData.testcasedescription=this.testCaseDescription;
				tData.testCaseExecutionComments = ex.getMessage();
				tData.testCaseStatus = "fail";
				// if(!verbose)
				// {
				// showTestCaseResultEveryTime(tData);
				// }
				// And then Add the same at the last.

				TestSuite.	executedTestCaseData.put(tData.testCaseID, tData);

				if (Controller.opts.dbReporting) {
					Controller.reporter.SaveTestCaseResultEveryTime(tData);
				}
			} else {
				TestSuite.initWorkedFine = false;
			}

		} finally {
			Controller.message("********************************************************************************");

		}
	}




	/**
	 * Check the command line Testcase contains ,
	 * 
	 * @param tempcom
	 * 
	 * @return testcase
	 */
	private TestCase checkWithCommandLineInput(TestCase tempcom) {

		// Implement the code
		TestCase command_line_testcase = new TestCase();
		command_line_testcase.testCaseID = "Not Present";
		// message("checkWithCommandLineIfComma:: The , manualtestcase " +
		// manualTestCaseID);
		String cmdTestCases[] = Controller.opts.manualTestCaseID.split(",");
		for (String manual : cmdTestCases) {
			// message("checkWithCommandLineIfComma " + manual +
			// " tempt Testcase " + tempcom.testCaseID);
			if (tempcom.parentTestCaseID.equalsIgnoreCase(tempcom.testCaseID)) {
				if (manual.equalsIgnoreCase(tempcom.testCaseID)) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1a " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.testCaseID.toLowerCase().trim()
						.contains(manual.toLowerCase())) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1b " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.testCaseID.equalsIgnoreCase("init")
						|| tempcom.testCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				} else if (tempcom.parentTestCaseID.equalsIgnoreCase("init")
						|| tempcom.parentTestCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				}

			} else {
				if (manual.equalsIgnoreCase(tempcom.parentTestCaseID)) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1a " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.parentTestCaseID.toLowerCase().trim()
						.contains(manual.toLowerCase())) {
					command_line_testcase = tempcom;
					// message("checkWithCommandLineIfComma 1b " + manual +
					// " testcase " + tempcom.testCaseID);
					break;
				} else if (tempcom.testCaseID.equalsIgnoreCase("init")
						|| tempcom.testCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				} else if (tempcom.parentTestCaseID.equalsIgnoreCase("init")
						|| tempcom.parentTestCaseID.equalsIgnoreCase("cleanup")) {
					command_line_testcase = tempcom;
					break;
				}
			}
		}
		// message("the testcase id evrytime "+tempcom.testCaseID);
		return command_line_testcase;

	}


	private boolean checkIfMoleculeNeedToBeExpanded(String molcallarg,
			ArrayList<String> moldeflist) {
		Log.Debug(String.format("TestCase/ExpandTestCase/checkIfMoleculeNeedToBeExpanded:: Molecule named argument definition list %s, variable called %s",moldeflist,molcallarg ));
		boolean moldefexists=false;
		//if(molcallarg.toLowerCase().contains("##")&&(!molcallarg.endsWith("##")||!molcallarg.endsWith("##%")||!molcallarg.endsWith("#")))
		if(molcallarg.startsWith("##")){
			for(String moldefarg:moldeflist)
			{
				//if(moldefarg.toLowerCase().contains(molcallarg))

				if(moldefarg.equalsIgnoreCase(molcallarg.replaceAll("##","")))
				{
					moldefexists=true;
					break;
				}
			}
		}
		Log.Debug(String.format("TestCase/ExpandTestCase/checkIfMoleculeNeedToBeExpanded:: final return : %s",moldefexists));
		return moldefexists;
	}

	private String GetTheActualValue(String entireValue) throws Exception {
		String tempValue = StringUtils.EMPTY;
			//Controller. message("\nTestCase/enitre value "+entireValue);
		if (entireValue.contains("=")) {
			Log.Debug("TestCase/GetTheActualValue : entireValue contains an = sign ");
			String[] splitVariableToFind = Excel
					.SplitOnFirstEquals(entireValue);
			// //TODO I need to Check here what is going on with the Values

			Log.Debug("TestCase/GetTheActualValue : Length of  splitVariableToFind = "
					+ splitVariableToFind.length);
			if (splitVariableToFind.length <= 1) {
				Log.Debug("TestCase/GetTheActualValue : End of function with variableToFind = "
						+ entireValue + " and its value is -> " + entireValue);

				return entireValue;
			}

			tempValue = splitVariableToFind[1];
			// message("as temp val have = "+tempValue);

			if (tempValue.startsWith("$$") && tempValue.endsWith("%")) {
				// message("EQUAL contextvariablemvm "+tempValue);
				tempValue = Utility.TrimStartEnd(tempValue, '$', 1);
				tempValue = tempValue.replaceAll("%", "");
				tempValue = ContextVar.getContextVar(tempValue);
				// message("EQUAL contextvariablemvm value"+tempValue);
			} else if (tempValue.startsWith("$$") && tempValue.contains("#")) {
				//	Controller. message("\nGetValue:/This is Indexed = " + tempValue);
				//tempValue = Utility.TrimStartEnd(tempValue, '$', 1);
				tempValue = Excel._indexedMacroTable.get(tempValue.substring(1)
						.toLowerCase());
					

				//	System.out.println("\nIndexed table value"+Excel._indexedMacroTable);
				// message("GetValue:/This is Indexed The Value = " +
				// tempValue);
			}else if(tempValue.startsWith("$") && tempValue.contains("#")){
				//System.out.println("IF entire value="+tempValue.toLowerCase());
				tempValue = Excel._indexedMacroTable.get(tempValue.toLowerCase());
			}else {
				// message("The entire value unchanged "+entireValue);
				tempValue = entireValue;
			}
			Log.Debug("TestCase/GetTheActualValue : variableToFind = "
					+ tempValue);
		} // First Check in the Indexed Variable
		else if (entireValue.startsWith("$$") && entireValue.contains("#")) {
			//	entireValue = Utility.TrimStartEnd(entireValue, '$', 1);
			//	Controller. message("\nGetValue:/This is a entire Indexed\t" + entireValue);
			tempValue = Excel._indexedMacroTable.get(entireValue.substring(1).toLowerCase());

			//	System.out.println("temp value from indexed table"+tempValue);
			//	System.out.println("\nIndexed table value"+Excel._indexedMacroTable);
			// /message("GetValue:/This is a Indexed\t" + tempValue);

		} else if (entireValue.startsWith("$$%") && entireValue.endsWith("%")) {
			entireValue = Utility.TrimStartEnd(entireValue, '$', 1);
			entireValue = entireValue.replaceAll("%", "");
			tempValue = ContextVar.getContextVar(entireValue);
		}else if (entireValue.startsWith("$") && entireValue.contains("#")) {
			//	System.out.println("In else entire value="+entireValue.toLowerCase());
			tempValue = Excel._indexedMacroTable.get(entireValue.toLowerCase());
			//	System.out.println(Excel._indexedMacroTable);
		}
		else {
			// message("TempVal=EntireVal "+tempValue);
			tempValue = entireValue;
		}

		Log.Debug("TestCase/GetTheActualValue : End of function with variableToFind = "
				+ entireValue + " and its value is -> " + tempValue);

//		Controller.message("The tempvalue "+tempValue);
		if(tempValue==null){
			//	System.out.println("entire value");
			tempValue=entireValue;
		}
		return tempValue;
	}


	private String GetActualCombination(String entireValue,
			String valueToSubstitute) {

		String tempValue = StringUtils.EMPTY;
		//Controller. message("GetActualComb:: 1a "+entireValue+" valusubs "+valueToSubstitute);
		if (entireValue.contains("=")) {
			Log.Debug("TestCase/GetActualCombination : entireValue contains an = sign ");
			 if(entireValue.startsWith("$~$")&&entireValue.endsWith("$~$")){
	                return valueToSubstitute;
	          }
			String[] splitVariableToFind = Excel
					.SplitOnFirstEquals(entireValue);

			Log.Debug("TestCase/GetActualCombination : Length of  splitVariableToFind = "
					+ splitVariableToFind.length);
			if (splitVariableToFind.length <= 1) {
				Log.Debug("TestCase/GetActualCombination : End of function with variableToFind = "
						+ entireValue
						+ " and its value is -> "
						+ valueToSubstitute);
				//Controller. message("GetActualComb:: 1b "+entireValue+" valusubs "+valueToSubstitute);
				//		Controller. message("value returned"+valueToSubstitute);
				return valueToSubstitute;
			}
			if (valueToSubstitute.contains("=")) {
				tempValue = valueToSubstitute;
				// message("GetActualComb:: GGHH"+tempValue);
			} else {
				tempValue = splitVariableToFind[0] + "=" + valueToSubstitute;
			}
			// message("GetActualComb:: 1c "+tempValue);
		} // First Check in the Context Variable
		else {
			tempValue = valueToSubstitute;
			// message("GetActualComb:: 1d "+tempValue);
		}

		Log.Debug("TestCase/GetActualCombination : End of function with variableToFind = "
				+ entireValue + " and its value is -> " + tempValue);
		// message("GetActualComb:: 1e "+tempValue);
		//	Controller. message("value returned"+tempValue);
		return tempValue;
	}

	TestCase[] ExpandTestCase(boolean fromTestCase)
			throws Exception {
		Log.Debug("TestCase/ExpandTestCase: Start of function with TestCase ID is "
				+ this.testCaseID);
		// TODO work on this part change utility addingelement in arraylist
		// method .. its not using action class objects
		// HashMap<String, String> mvm_vector_map = new HashMap<String,
		// String>();

		// write the subroutine here.
		//TestCase test=addSetContextVarMVMAction(test1);
		ArrayList<ArrayList<String>> allActionVerificationArgs = new ArrayList<ArrayList<String>>();
		Action[] allActions = new Action[this.actions.size()];

		// test.actions.toArray(allActions);

		Log.Debug("TestCase/ExpandTestCase: Number of Actions are : "
				+ allActions.length + " for testcase : " + this.testCaseID);

		int count1 = -1;
		Hashtable<Integer, String> multiValuedVariablePosition = new Hashtable<Integer, String>();

		for (int j = 0; j < this.actions.size(); j++) {

			Action action = this.actions.get(j);
			Log.Debug("TestCase/ExpandTestCase: Working on Action  : "
					+ action.name);
			// //TODO put checking if testcase have no actio argument then at
			// least print any message or put the exception
			// message("Action argument size? "+action.actionArguments.size()+" Argument Valuess "+action.actionArguments);
			// message("The Action names "+action.actionName+" having testcaseid "+action.parentTestCaseID
			// +" Arguments "+action.actionArguments);
			if (action.arguments.size() > 0) {
				// removeDuplicateMVMVariables(action);
				for (int i = 0; i < action.arguments.size(); ++i) {

					count1++;
					//Controller.message("ExpandTest:/cheks to Macro action args exp\t" + action.arguments);
					String tempVal = GetTheActualValue((String) (action.arguments
							.get(i)));
					//	System.out.println("Temp val"+tempVal);
					if (action.actualArguments.size() == action.arguments
							.size()) {
						// put in a hashmap
						// key=tempval value=actlArg
						//if (action.actionActualArguments.get(i).startsWith("$$")|| action.actionActualArguments.get(i).startsWith("##")) {
						if (action.actualArguments.get(i).startsWith("$$")|| checkIfMoleculeNeedToBeExpanded(action.actualArguments.get(i),this._testcasemoleculeArgDefn)) {
							tempVal = tempVal + "$~$";
						} else if (action.actualArguments.get(i)
								.contains("=")) {
							String[] split_actual_arg = Excel
									.SplitOnFirstEquals(action.actualArguments
											.get(i));
							if (split_actual_arg.length > 1	&& (split_actual_arg[1].startsWith("$$") || checkIfMoleculeNeedToBeExpanded(split_actual_arg[1],this._testcasemoleculeArgDefn) )) {
								tempVal = tempVal + "$~$";
							}
						}

					}

					if (tempVal == null) {
						Log.Error("TestCase/ExpandedTestCase : Variable -> "
								+ action.arguments.get(i) + " Value -> "
								+ tempVal
								+ " NullValueException \n In TesrCaseId: "
								+ action.testCaseID);
						throw new Exception(
								"TestCase/ExpandedTestCase : Variable -> "
										+ action.arguments.get(i)
										+ " Value -> "
										+ tempVal
										+ " NullValueException \n In TesrCaseId: "
										+ action.testCaseID);
					}
					if(!tempVal.startsWith("$~$")){
						String tempindexcheckin[] = Excel
								.SplitOnFirstEquals(tempVal);
						if (tempindexcheckin.length == 2) {
							if (tempindexcheckin[1].startsWith("$~$")
									&& tempindexcheckin[1].endsWith("$~$")) {
								// message("indexmacro checking: from "+tempVal+" to "+tempindexcheckin[1]);
								tempVal = tempindexcheckin[1];
							}
						}
					}
					if ((tempVal.startsWith("$~$"))
							&& (tempVal.endsWith("$~$"))) {
						tempVal = StringUtils.replace(tempVal, "$~$", "");
						// tempVal=tempVal.replaceAll("~","");
						String val = Utility.TrimStartEnd(tempVal, '#', 1);
						val = Utility.TrimStartEnd(val, '#', 0);
						val = Utility.TrimStartEnd(val, '#', 0);
						val = Utility.TrimStartEnd(val, '#', 1);
						Log.Debug("TestCase/ExpandTestCase: Working on Argument ["
								+ i
								+ "] = "
								+ val
								+ " of Action: "
								+ action.name);
						allActionVerificationArgs.add(new ArrayList<String>(
								Arrays.asList(val.split(","))));

						// Test Case Variable table not getting updated for one
						// valued MVM - Bug Fix
						// if (val.Split(',').Length > 1)
						multiValuedVariablePosition.put(count1,
								StringUtils.EMPTY);
					} else {

						allActionVerificationArgs.add(new ArrayList<String>(
								Arrays.asList(new String[] { tempVal })));
					}
				}
			} else {
				// message("No Arguments : Molecule called then "+action.actionName);
				allActionVerificationArgs.add(new ArrayList<String>(Arrays
						.asList(new String[] { "~$Somevalue$~" })));

			}
			// message("Expands :: action arguments "+action.actionArguments);
			Verification[] verifications = new Verification[action.verification
			                                                .size()];
			action.verification.toArray(verifications);
			Log.Debug("TestCase/ExpandTestCase: Number of verifications are : "
					+ verifications.length
					+ " for Action : "
					+ action.name);
			for (int k = 0; k < verifications.length; k++) {
				Verification verification = verifications[k];

				if (verification.name == null) {
					continue;
				}
				Log.Debug("TestCase/ExpandTestCase: Working on verification : "
						+ verification.name
						+ " for Action : "
						+ action.name);
				if (verification.arguments.size() > 0) {
					for (int l = 0; l < verification.arguments
							.size(); ++l) {
						count1++;

						String tempVal2 = GetTheActualValue((String) (verification.arguments
								.get(l)));
						if (verification.actualArguments.size() == verification.arguments
								.size()) {
							// put in a hashmap
							// key=tempval value=actlArg
							if (verification.actualArguments.get(l).startsWith("$$")|| checkIfMoleculeNeedToBeExpanded(verification.actualArguments.get(l), this._testcasemoleculeArgDefn)) {
								tempVal2 = tempVal2 + "$~$";
							} else if (verification.actualArguments
									.get(l).contains("=")) {
								String[] split_actual_arg = Excel
										.SplitOnFirstEquals(verification.actualArguments
												.get(l));
								if (split_actual_arg.length > 1
										&& (split_actual_arg[1]
												.startsWith("$$") || checkIfMoleculeNeedToBeExpanded(split_actual_arg[1], this._testcasemoleculeArgDefn))) {
									tempVal2 = tempVal2 + "$~$";
								}
							}

						}
						if (tempVal2 == null) {
							Log.Error("TestCase/ExpandedTestCase : Variable -> "
									+ verification.arguments.get(l)
									+ " Value -> "
									+ tempVal2
									+ " NullValueException \n In TesrCaseId: "
									+ verification.testCaseID);
							throw new Exception(
									"TestCase/ExpandedTestCase : Variable -> "
											+ verification.arguments
											.get(l)
											+ " Value -> "
											+ tempVal2
											+ " NullValueException \n In TesrCaseId: "
											+ verification.testCaseID);
						}
						if ((tempVal2.startsWith("$~$#")
								&& tempVal2.endsWith("#$~$"))||(tempVal2.startsWith("$~$")
										&& tempVal2.endsWith("$~$"))) {
							tempVal2 = StringUtils.replace(tempVal2, "$~$", "");
							String val = Utility.TrimStartEnd(tempVal2, '#', 1);
							val = Utility.TrimStartEnd(val, '#', 0);
							val = Utility.TrimStartEnd(val, '#', 1);
							val = Utility.TrimStartEnd(val, '#', 0);
							Log.Debug("TestCase/ExpandTestCase: Working on Argument ["
									+ j
									+ "] = "
									+ val
									+ " of verification  :"
									+ verification.name);
							allActionVerificationArgs
							.add(new ArrayList<String>(Arrays
									.asList(val.split(","))));
							multiValuedVariablePosition.put(count1, "");
						} else {
							allActionVerificationArgs
							.add(new ArrayList<String>(Arrays
									.asList(new String[] { tempVal2 })));
						}
					}
				} else {
					allActionVerificationArgs.add(new ArrayList<String>(Arrays
							.asList(new String[] { "~$Somevalue$~" })));
				}
			}

		}		List<Tuple<String>> resultAfterIndexed = CartesianProduct
				.indexedProduct(allActionVerificationArgs);

		List<Tuple<String>> result = new ArrayList<Tuple<String>>();

		for (Tuple<String> tempResult : resultAfterIndexed) {
			allActionVerificationArgs = new ArrayList<ArrayList<String>>();
			ArrayList<String> tempResultList = new ArrayList<String>();
			Object[] actualValue = tempResult.ToArray();

			for (int q = 0; q < actualValue.length; ++q) {
				tempResultList.add((String) actualValue[q]);
			}
			// message("temp result list "+tempResultList);
			count1 = -1;
			try {
				for (String tempVal : tempResultList) {
					count1++;
					if (tempVal.endsWith("$~$")) {
						tempVal = StringUtils.replace(tempVal, "$~$", "");
						int ind=tempVal.indexOf("{",0);
						if(ind<0){
							String val = StringUtils.replace(tempVal, "{", "");
							val = StringUtils.replace(val, "}", "");
							//	System.out.println("Val :"+val);
							Log.Debug("TestCase/ExpandTestCase: Working on Argument with value = "
									+ val + " of Action: ");
							allActionVerificationArgs.add(new ArrayList<String>(
									Arrays.asList(val.split(","))));
						}else{
							String tmp=tempVal.substring(0, ind);
							if(tmp.contains("=")){
								String str[]=tempVal.split("=", 2);
								String val = StringUtils.replace(str[1], "{", "");
								val = StringUtils.replace(val, "}", "");
								String vals[]=val.split(",");
								for(int i=0;i<vals.length;i++){
									vals[i]=str[0]+"="+vals[i];
									//	System.out.println("values-"+vals[i]);
								}
								Log.Debug("TestCase/ExpandTestCase: Working on Argument with value = "
										+ vals.toString() + " of Action: ");
								allActionVerificationArgs.add(new ArrayList<String>(
										Arrays.asList(vals)));

							}else{
								String val = StringUtils.replace(tempVal, "{", "");
								val = StringUtils.replace(val, "}", "");
								//	System.out.println("Val :"+val);
								Log.Debug("TestCase/ExpandTestCase: Working on Argument with value = "
										+ val + " of Action: ");
								allActionVerificationArgs.add(new ArrayList<String>(
										Arrays.asList(val.split(","))));

							}
						}	
						multiValuedVariablePosition.put(count1,
								StringUtils.EMPTY);
					} else {
						// message("is it comming to this place??? ");
						if (!tempVal.equalsIgnoreCase("~$Somevalue$~")) {
							allActionVerificationArgs
							.add(new ArrayList<String>(Arrays
									.asList(new String[] { tempVal })));
						}
					}

					// }
				}

				result.addAll(CartesianProduct
						.cartesianProduct(allActionVerificationArgs));

			} catch (OutOfMemoryError e) {
				TestSuite.testcasenotran = this.testCaseID + "\nError: " + e.getMessage()
						+ "(memory) exceeded";
				Log.Error("["
						+ this.testCaseID
						+ "] Combination Of MVM values Exceeded permisible limit :: Not Comapatibile \nError: "
						+ e.getMessage() + "(memory) exceeded");
				throw new Exception(
						"Combination Of MVM values Exceeded permisible limit :: Not Comapatibile \nError: ");
			}
		}

		ArrayList<TestCase> tempTestCases = new ArrayList<TestCase>();
		// message("Coming to this end. 7 "+result);
		if (result != null) {
			int size = result.size();
			Object[] tempResult = result.toArray();
			// Object [] tempResult = allActionVerificationArgs.toArray();
			for (int p = 0; p < size; ++p) {
				Tuple<String> subList = (Tuple<String>) tempResult[p];
				TestCase tempTestCase;
				if(fromTestCase){
					tempTestCase = new TestCase(this);
				}
				else{
					tempTestCase = new Molecule(this);
				}
				ArrayList<String> tempCollection = new ArrayList<String>();
				Object[] actualValue = subList.ToArray();
				for (int q = 0; q < actualValue.length; ++q) {
					tempCollection.add((String) actualValue[q]);
				}

				/*
				 * while(subList.hasMoreElements()) { String
				 * val=subList.nextElement(); tempCollection.add(val); }
				 */

				String[] tempTestCaseVar = new String[tempCollection.size()];
				tempCollection.toArray(tempTestCaseVar);
				int count = 0;
				Log.Debug("TestCase/ExpandTestCase: Number of Actions are : "
						+ actions.size() + " for testcase : "
						+ tempTestCase.testCaseID);

				for (int j = 0; j < actions.size(); ++j) {
					Action action = actions.get(j);
					try{
						int val=Integer.valueOf(action.step);
						//message("Integer Value Step "+val);
						if(action.name.startsWith("&"))
						{
							//message("flag value changing "+action.actionName);
							this.isConcurrentMoleculeCall=true;
						}
					}catch(NumberFormatException ne)
					{
						//do nothing
					}
					for (int i = 0; i < action.arguments.size(); ++i) {

						if (multiValuedVariablePosition.containsKey(count)) {
							String testcase_partial_id = tempTestCaseVar[count]
									.toLowerCase();
							if (action.name
									.equalsIgnoreCase("appendtocontextvar")
									|| action.name
									.equalsIgnoreCase("setcontextvar")
									|| action.name.startsWith("&")) {
								// message("Coming to this end. 10 "+action.actionName);
								String real_value[] = Excel
										.SplitOnFirstEquals(testcase_partial_id);
								// message("Coming to this end. 10 a "+testcase_partial_id);
								String actual_value[] = Excel
										.SplitOnFirstEquals(action.actualArguments
												.get(i));
								// message("Coming to this end. 10 b "+actual_value[0]+" and real "+real_value[0]);
								if (real_value.length > 0) {
									if (real_value[0]
											.equalsIgnoreCase(actual_value[0])) {
										// message("Before Change "+tempTestCaseVar[count]+" ID "+testcase_partial_id);
										testcase_partial_id = StringUtils
												.replace(testcase_partial_id,
														actual_value[0] + "=",
														"");
										if (testcase_partial_id.contains("=")) {
											testcase_partial_id = Excel
													.SplitOnFirstEquals(testcase_partial_id)[1];
										}
										// message("After change "+tempTestCaseVar[count]+" ID "+testcase_partial_id);

										// message("Coming to this end. 10 c "+testcase_partial_id);
									} else if (action.actualArguments
											.get(i).contains("=")) {
										String realVal[] = Excel
												.SplitOnFirstEquals(testcase_partial_id);
										if (realVal.length > 1) {
											testcase_partial_id = realVal[1];

										}
									}
								} else {

									testcase_partial_id = null;
								}

							}
							tempTestCase.testCaseID += "\\"
									+ testcase_partial_id;
						}
						// message("Coming to this end. 11 "+count);
						count++;
					}

					Log.Debug("TestCase/ExpandTestCase: Number of verifications are : "
							+ action.verification.size()
							+ " for Action : "
							+ action.name);
					for (int cnt = 0; cnt < action.verification.size(); ++cnt) {
						Verification verification = action.verification.get(cnt);

						Log.Debug("TestCase/ExpandTestCase: Working on Verification  "
								+ verification.name);

						for (int i = 0; i < verification.arguments
								.size(); ++i) {
							if (multiValuedVariablePosition.containsKey(count)) {
								String testcase_partial_id = tempTestCaseVar[count]
										.toLowerCase();
								if (action.name
										.equalsIgnoreCase("appendtocontextvar")) {
									String real_value[] = Excel
											.SplitOnFirstEquals(testcase_partial_id);
									String actual_value[] = Excel
											.SplitOnFirstEquals(action.actualArguments
													.get(i));
									if (real_value[0]
											.equalsIgnoreCase(actual_value[0])) {
										// message("Before Change "+tempTestCaseVar[count]+"  "+testcase_partial_id);
										testcase_partial_id = StringUtils
												.replace(testcase_partial_id,
														actual_value[0] + "=",
														"");
										// message("After change "+tempTestCaseVar[count]+"  "+testcase_partial_id);
									}

								} else if (verification.actualArguments
										.get(i).contains("=")) {
									String real_value[] = Excel
											.SplitOnFirstEquals(testcase_partial_id);
									if (real_value.length > 1) {
										testcase_partial_id = real_value[1];
									}
								}
								tempTestCase.testCaseID += "\\"
										+ testcase_partial_id;

							}

							count++;
						}
					}
				}

				if (fromTestCase) {

					tempTestCase.parentTestCaseID = tempTestCase.testCaseID;
					Log.Debug("TestCase/ExpandTestCase: fromTestCaseSheet= TRUE so tempTestCase.parentTestCaseID  = "
							+ tempTestCase.parentTestCaseID);

					tempTestCase.stackTrace = tempTestCase.testCaseID;
					Log.Debug("TestCase/ExpandTestCase: fromTestCaseSheet= TRUE so tempTestCase.stackTrace  = "
							+ tempTestCase.stackTrace);
				} else {
					tempTestCase.parentTestCaseID = this.parentTestCaseID;
					Log.Debug("TestCase/ExpandTestCase: fromTestCaseSheet= FALSE so tempTestCase.parentTestCaseID  = "
							+ tempTestCase.parentTestCaseID);

					tempTestCase.stackTrace = tempTestCase.stackTrace.replace(
							this.testCaseID, tempTestCase.testCaseID);
					Log.Debug("TestCase/ExpandTestCase: fromTestCaseSheet= TRUE so tempTestCase.stackTrace  = "
							+ tempTestCase.stackTrace);

				}

				count = 0;
				// message("EXPNDD:: arguments .. "+actions[1].actionArguments);
				for(Action action:actions){
					Action tempAction = new Action(action);

					tempAction.testCaseID = tempTestCase.testCaseID;
					tempAction.parentTestCaseID = tempTestCase.parentTestCaseID;
					tempAction.stackTrace = tempTestCase.stackTrace;
					ArrayList<String> arg=new ArrayList<String>();
					//		System.out.println("Action args:"+action.arguments);
					for(String argA:action.arguments){
						//for (int i = 0; i < action.arguments.size(); ++i) {
						arg.add(GetActualCombination((String) argA,tempTestCaseVar[count++]));
					}
					//	System.out.println("Action args after:"+arg);
					tempAction.arguments.addAll(arg);
					Log.Debug("TestCase/ExpandTestCase: Number of verifications are : "
							+ action.verification.size()
							+ " for Action : "
							+ action.name);
					for(Verification verification:action.verification){	
						Log.Debug("TestCase/ExpandTestCase: Working on Verification "
								+ verification.name);
						Verification tempVerification = new Verification(verification);
						tempVerification.testCaseID = tempTestCase.testCaseID;
						tempVerification.parentTestCaseID = tempTestCase.parentTestCaseID;

						ArrayList<String> argv=new ArrayList<String>();
						for(String argVer:verification.arguments){
							argv.add(GetActualCombination(argVer,tempTestCaseVar[count++]));
						}
						tempVerification.arguments.addAll(argv);
						tempAction.verification.add(tempVerification);
					}
					tempTestCase.actions.add(tempAction);
				}

				if (Controller.opts.manualTestCaseID.contains(",")) {
					if (checkWithCommandLineInput(tempTestCase).testCaseID
							.equalsIgnoreCase("Not Present")) {
					} else {
						tempTestCases
						.add(checkWithCommandLineInput(tempTestCase));

					}

				} else {
					// message("Not Comma Separted command Line");
					if (checkWithCommandLineInput(tempTestCase).testCaseID
							.equalsIgnoreCase("Not Present")) {
					} else {
						tempTestCases
						.add(checkWithCommandLineInput(tempTestCase));
					}
				}

			}
		}

		if (tempTestCases.size() == 0) {
			Log.Debug("TestCase/ExpandTestCase: Returning only 1 testcase after Expansion. End of function with TestCase ID is "
					+ this.testCaseID);
			//	System.out.println("returning this");
			return new TestCase[] { this };
		}
		if (Controller.opts.excludeTestCaseID != null) {
			ArrayList<TestCase> removeTestCase = new ArrayList<TestCase>();
			if (Controller.opts.excludeTestCaseID.contains(",")) {

				String excludeTCID[] =Controller. opts.excludeTestCaseID.split(",");

				for (int i = 0; i < excludeTCID.length; i++) {
					for (TestCase tt : tempTestCases) {
						if (tt.testCaseID.equalsIgnoreCase(excludeTCID[i])) {
							removeTestCase.add(tt);

						}
					}
				}

			} else {
				for (TestCase tt : tempTestCases) {
					if (tt.testCaseID.equalsIgnoreCase(Controller.opts.excludeTestCaseID)) {
						removeTestCase.add(tt);
						break;
					}
				}
			}

			tempTestCases.removeAll(removeTestCase);
		}

		Log.Debug("TestCase/ExpandTestCase: Returning "
				+ tempTestCases.size()
				+ " testcase after Expansion. End of function with TestCase ID is "
				+ this.testCaseID);

		TestCase[] tempT;
		if(fromTestCase){
			tempT=new TestCase[tempTestCases.size()];
		}
		else{
			tempT=new Molecule[tempTestCases.size()];
		}
		return tempTestCases.toArray(tempT);
	}

	void implicitTestStepCall(){
		String implicitTSMoleculeName=TestSuite.implicitTSMoleculeName.substring(TestSuite.implicitTSMoleculeName.indexOf(".")+1);
		if(this.testCaseID.equalsIgnoreCase("init")||this.testCaseID.equalsIgnoreCase("cleanup")){
			return;
		}
		if(TestSuite.implicitTSMolecule){
			Molecule implicitMolecule=TestSuite.abstractTestCase.get(TestSuite.implicitTSMoleculeName);
			implicitMolecule.setCallingtTestCaseSTACK(this.stackTrace);
			implicitMolecule.setParentTestCaseID(this.parentTestCaseID);
			try{
				implicitMolecule.run();
			}catch(Exception e){

			}
		}
	}

	void run() throws 
	ReportingException,Exception,Throwable {
		Controller.checkDebuggerSignal();
		if (this.automated == false) {
			Log.Debug("TestCase/RunTestCase : TestCase ID "
					+ this.testCaseID
					+ " is a MANUAL TestCase. Automation Framework is ignoring this TestCase.");
			Controller.message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : "
					+ this.testCaseID + ". This is a Manual TestCase.");
			return;
		}

		Log.Debug("TestCase/RunTestCase: Start of function with a new TestCase. TestCase ID is "
				+ this.testCaseID);

		// Harness Specific ContextVariable to store BaseTestCase ID
		ContextVar.setContextVar("ZUG_BASETCID", this.testCaseID);
		TestSuite.baseTestCaseID = this.testCaseID;

		// Added Context Variable basically to get the User Information
		if (this.userObj != null) {
			// Harness Specific ContextVariable to store UserName
			ContextVar.setContextVar("UserName", this.userObj.userName);
			// Harness Specific ContextVariable to store UserPassword
			ContextVar
			.setContextVar("UserPassword", this.userObj.userPassword);
			// Harness Specific ContextVariable to store userDomain
			ContextVar.setContextVar("UserDomain", this.userObj.domain);

			Log.Debug("TestCase/RunTestCase: Set Context Variables for User with Value as UserName = "
					+ this.userObj.userName
					+ " userPassword = "
					+ this.userObj.userPassword
					+ " domain = "
					+ this.userObj.domain);
		} else {
			// Harness Specific ContextVariable to store UserName
			ContextVar.setContextVar("UserName", "");
			// Harness Specific ContextVariable to store UserPassword
			ContextVar.setContextVar("UserPassword", "");
			// Harness Specific ContextVariable to store userDomain
			ContextVar.setContextVar("UserDomain", "");
			Log.Debug("TestCase/RunTestCase: UserObj is null, so setting the context Variables to String.Empty.");
		}

		Log.Debug("TestCase/RunTestCase: Calling ExpandTestCase With TestCase ID is "
				+ this.testCaseID);
		//	System.out.println("Test Case id"+this.testCaseID);
//		for(Action act:this.actions){
//			System.out.println("name : "+act.name);
//			for(String arg:act.arguments){
//				System.out.println(arg);
//			}
//		}
		TestCase[] expandedTestCases=null;
		try{
			expandedTestCases = this.ExpandTestCase( true);
		}catch(Exception e){
			//	e.printStackTrace();
			expandedTestCases=new TestCase[1];
			expandedTestCases[0]=this;
		}
		//	System.out.println("ETC size "+expandedTestCases.length);
		Log.Debug("TestCase/RunTestCase: After Expansion for TestCase ID is "
				+ this.testCaseID + " the number of expanded test case is : "
				+ expandedTestCases.length);

		ArrayList<Thread> ThreadPool = new ArrayList<Thread>();

		for (TestCase test : expandedTestCases) {
			if(errorOccured){
				throw new Throwable();
			}
			/*if(Controller.opts.debugger){
				if(Controller.breakpoints.containsKey(this.testCaseID)){
					test.breakpoint=true;
					test.breakpoints=Controller.breakpoints.get(this.testCaseID);
				}	
			}	
			*/
			if(Controller.stop){
				return;
			}
			final TestCase test2 = test;
			if (this.concurrentExecutionOnExpansion == true) {
				Thread thread = new Thread(new Runnable() {

					public void run(){

						try {
							try {
								test2.runExpandedTestCase();
							} catch (ReportingException ex) {
								Log.Error("TestCase/RunTestCase: Exception when calling RunExpandedTestCase with exception message as : "
										+ ex.getMessage());
							}
						} catch (Exception ex) {
							Log.Error("TestCase/RunTestCase: Exception when calling RunExpandedTestCase with exception message as : "
									+ ex.getMessage());
						}catch (Throwable ex) {
							errorOccured=true;
						}
					}
				});
				thread.start();
				TestSuite.threadIdForTestCases.put(test.stackTrace, "" + thread.getId());
				ThreadPool.add(thread);
			} else {
				if (StringUtils.isNotBlank(test.threadID)) {
					TestSuite.threadIdForTestCases.put(test.stackTrace, test.threadID);

				} else // Assigning the Parent Test Case Thread.
				{
					TestSuite.threadIdForTestCases
					.put(test.stackTrace, StringUtils.EMPTY);
				}

				test.runExpandedTestCase();
			}
		}
		// Wait for all the Threads i.e. expanded test cases to finish.
		for (int t = 0; t < ThreadPool.size(); ++t) {
			((Thread) ThreadPool.get(t)).join();
		}
		

		Log.Debug("TestCase/RunTestCase: End of function with TestCase ID is "
				+ this.testCaseID);
	}


}	
