package com.automature.spark.engine;


import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Molecule extends TestCase {

	ArrayList<String> arguments = new ArrayList<String>();
	//String parentTestCaseID;
	String callingtTestCaseSTACK;
	//int argsize;
	public Molecule(){
		
	}
	public Molecule(TestCase tc){
		super(tc);
	}
	public Molecule(Molecule mol) {
		super(mol);
		this.arguments = mol.arguments;
	//	this.parentTestCaseID = mol.parentTestCaseID;
		this.callingtTestCaseSTACK = mol.callingtTestCaseSTACK;
	}

	public void setArguments(ArrayList<String> arguments) {
		this.arguments = arguments;
	}

	public void setParentTestCaseID(String parentTestCaseID) {
		this.parentTestCaseID = parentTestCaseID;
	}

	public void setCallingtTestCaseSTACK(String callingtTestCaseSTACK) {
		this.callingtTestCaseSTACK = callingtTestCaseSTACK;
	}
	
	void implicitTestStepCall(){
		String implicitTSMoleculeName=TestSuite.implicitTSMoleculeName.substring(TestSuite.implicitTSMoleculeName.indexOf(".")+1);
		String implicitTCMoleculeName=TestSuite.implicitTCMoleculeName.substring(TestSuite.implicitTCMoleculeName.indexOf(".")+1);
		if(TestSuite.implicitTSMoleculeName.equalsIgnoreCase(this.testCaseID) || implicitTSMoleculeName.equalsIgnoreCase(this.testCaseID) || this.stackTrace.contains(implicitTSMoleculeName)||this.stackTrace.toLowerCase().contains("init")||this.stackTrace.toLowerCase().contains("cleanup")||this.stackTrace.toLowerCase().contains(implicitTCMoleculeName)){
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

	private void removeAndShowExceptionMessage(Action act){
		if(act.property.equalsIgnoreCase("ROS") && StringUtils
				.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
			returnFlag=true;
			
		}else if(act.property.equalsIgnoreCase("ROF") && !StringUtils
				.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
			returnFlag=true;
			Spark.message(String.format(
					"\n[%s] Exception in "+act.getClass().getSimpleName()+" %s (%s:%s).\n\t Message: %s",
					act.stackTrace,act.name, act.sheetName,
					(act.lineNumber+1),TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace)));
			//Controller.message(TestSuite.errorMessageDuringMoleculeCaseExecution
			//				.get(act.stackTrace));
			if (!StringUtils
					.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution
							.get(act.stackTrace))) {
				TestSuite.errorMessageDuringMoleculeCaseExecution
				.put(act.stackTrace,StringUtils.EMPTY);			
			}
			
			if (!StringUtils
					.isBlank(TestSuite.errorMessageDuringTestCaseExecution
							.get(act.parentTestCaseID))) {
				TestSuite.errorMessageDuringTestCaseExecution
				.put(act.parentTestCaseID,StringUtils.EMPTY);
			}
		}	
	}
	
	private void runExpandedMolecule() {
		Log.Debug("Molecule/RunExpandedTestCaseForMolecule : Start of Function.");

		String jointVarComb = this.testCaseID.replace("_", " ");
		if (StringUtils.isNotBlank(jointVarComb)) {
			Spark
			.message("******************************************************************************** ");

		}
		// Now run each of the Actions mentioned here...and try running it.
		Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  Number of Actions to run is : "
				+ this.actions.size() + " for TestCase ID : " + this.testCaseID);

		Hashtable<String, String> stepsKeys = new Hashtable<String, String>();
		// After getting the Actions Store the Steps somewhere...
		for (int i = 0; i < this.actions.size(); i++) {
			Action action = this.actions.get(i);

			Log.Debug("Molecule/RunExpandedTestCaseForMolecule: Storing the Steps in a HashTable. Step Number = "
					+ action.step);
			stepsKeys.put(action.step, StringUtils.EMPTY);
			Log.Debug("Molecule/RunExpandedTestCaseForMolecule: Successfully stored Step Number = "
					+ action.step + " to the HashTable");
		}

		String errorDuringTestCaseExecution = StringUtils.EMPTY;
		// First Clear the Stack.
		Stack<String> actionsForCleanup = new Stack<String>();

		int count = 0;
		ArrayList<Thread> ThreadPool = new ArrayList<Thread>();
		String stepNumber = StringUtils.EMPTY;

		if (this.actions.size() > 0) {
			stepNumber = this.actions.get(0).step;
		}
		if(Spark.guiFlag){
			String nameSpace="";
			if(this.nameSpace.equalsIgnoreCase(Excel.mainNameSpace)){
				nameSpace="main";
			}else{
				nameSpace=this.nameSpace;
			}
			Spark.guiController.setCurrentTestCase(this);
			Spark.guiController.showRunningMolecule(this.testCaseID,nameSpace, true);
		}
		try {
			int start=this.actions.get(0).lineNumber;
			for (int i = 0; i < this.actions.size(); i++) {
				if(Spark.stop){
					return;
				}
				
				final Action act = this.actions.get(i);
				count++;

				if(Spark.guiFlag){
                                    
					if(act.nameSpace.equalsIgnoreCase(Excel.mainNameSpace)){
						Spark.guiController.showRunningMoleculeStep("Molecules", act.lineNumber,start);
					}else{
						Spark.guiController.showRunningMoleculeStep(act.nameSpace, act.lineNumber,start);
					}
				}
				if(Spark.opts.debugger&&!Spark.guiController.isExpressionEvaluatorMode()){	
					String name="";
					//System.out.println("Name space ="+act.nameSpace);
					//name=act.nameSpace.equalsIgnoreCase(Excel.mainNameSpace)?"Molecules":act.nameSpace;
				
				//	System.out.println("break points "+Controller.breakpoints);

				    name="molecules";
			//		System.out.println("name space "+act.nameSpace+name);
					List<Integer> al=Spark.breakpoints.get(act.nameSpace+name);
			//		System.out.println("break points "+al);
					if(al!=null){
						if(al.contains(act.lineNumber)){
							//	Controller.sendMessageToDebugger((Object)act);
							Spark.setPauseSignal();
							Spark.checkDebuggerSignal();
						}else if(Spark.stepOver){
							//if(!act.name.startsWith("#define"))
							//	Controller.sendMessageToDebugger((Object)act);
							Spark.setPauseSignal();
							Spark.checkDebuggerSignal();
						}
					}else if(Spark.stepOver){
						//if(!act.name.startsWith("#define"))
						//	Controller.sendMessageToDebugger((Object)act);
						Spark.setPauseSignal();
						Spark.checkDebuggerSignal();
					}else {
						Spark.checkDebuggerSignal();
					}
				}	
				if (StringUtils.isBlank(act.name)) {
					continue;
				}

				Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  Action : "
						+ act.name + " has Step Number as : " + act.step);

				// If the action Steps contain an "i" and there is a cleanup
				// step for it then add that to the stack.
				if (act.step.endsWith("i")
						&& (stepsKeys.containsKey(act.step.substring(0,
								act.step.length() - 1) + "c") || stepsKeys
								.containsKey(act.step.substring(0,
										act.step.length() - 1)
										+ "C"))) {
					Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  Action : "
							+ act.name
							+ " with Step : "
							+ act.step
							+ " is an Initialization Action. A cleanup exist for this action ..so pusing this to the STACK");
					actionsForCleanup.push(act.step.substring(0,
							act.step.length() - 1));
				}

				// If this is a Cleanup action then break and run the cleanup at
				// the end.
				if (act.step.endsWith("c")
						&& actionsForCleanup.contains(act.step.substring(0,
								act.step.length() - 1))) {
					Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  Action : "
							+ act.name
							+ " with Step : "
							+ act.step
							+ " is an Cleanup Action/STEP. so breaking and moving to cleanup");
					count--;
					break;
				}

				Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  Calling RunAction for Action : "
						+ act.name + " for TestCase ID : " + this.testCaseID);

				if (stepNumber.equals(act.step)
						&& StringUtils.isNotBlank(act.step)) {
					Thread thread = new Thread(new Runnable() {

						public void run() {
							act.run();
							implicitTestStepCall();
							
							if(act.property.equalsIgnoreCase("ROS") && StringUtils
									.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
								returnFlag=true;
								
							}else if(act.property.equalsIgnoreCase("ROF") && !StringUtils
									.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
								returnFlag=true;
								removeAndShowExceptionMessage(act);

							}
						}
					});
					// thread.IsBackground = true;
					thread.start();
					ThreadPool.add(thread);
				} else {
					// Wait for all the Threads to finish.
					for (int t = 0; t < ThreadPool.size(); ++t) {
						((Thread) ThreadPool.get(t)).join();
					}

					ThreadPool.clear();
					if(returnFlag){
						return;
					}
					// In case we get an Exception then Dont run any more
					// processes
					if ((StringUtils
							.isNotBlank(TestSuite.errorMessageDuringMoleculeCaseExecution
									.get(this.stackTrace)))) {
						count--;
						// Remove this element as this is not executed.
						if (act.step.endsWith("i")
								&& (stepsKeys.containsKey(act.step.substring(0,
										act.step.length() - 1) + "c") || stepsKeys
										.containsKey(act.step.substring(0,
												act.step.length() - 1)
												+ "C"))) {
							actionsForCleanup.pop();
						}
						break;
					}

					// The new step number is the current one
					stepNumber = act.step;
					Thread thread = new Thread(new Runnable() {

						public void run() {
							act.run();
							implicitTestStepCall();
							if(act.property.equalsIgnoreCase("ROS") && StringUtils
									.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
								returnFlag=true;
								
							}else if(act.property.equalsIgnoreCase("ROF") && !StringUtils
									.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
								returnFlag=true;
								removeAndShowExceptionMessage(act);
							}
						}
					});
					thread.start();
					ThreadPool.add(thread);
				}

				Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  RunAction executed successfully for Action : "
						+ act.name + " for TestCase ID : " + this.testCaseID);
			}

			for (int t = 0; t < ThreadPool.size(); ++t) {
				((Thread) ThreadPool.get(t)).join();
			}
		}catch(InterruptedException e){
			//e.printStackTrace();
			Log.ErrorInLog("Error_Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
					+ this.testCaseID + ".Thread Interrupted or Test Step timed out" + e.getMessage());
			errorDuringTestCaseExecution += "Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
					+ this.testCaseID + ".Thread Interrupted or Test Step timed out" + e.getMessage();
		}
		catch (Exception ex) {
//			System.out.println("Error:"+ex.getMessage()+ex.getCause()+ex.getClass().getName());
			Log.ErrorInLog("Error_Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
					+ this.testCaseID + "." + ex.getMessage());
			errorDuringTestCaseExecution += "Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
					+ this.testCaseID + "." + ex.getMessage();

		} finally {
			boolean cleanupActionStarted = false;
			Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  Number of testcases steps successfully executed are "
					+ count + " for TestCase ID : " + this.testCaseID);
			// In the finally iterate over all the cleanup steps that one would
			// like to perform
			// before the test case is finally said to be done..
			//
			if(returnFlag){
				return;
			}
			if ((actionsForCleanup.size() > 0 && !TestSuite._testPlanStopper && !((Boolean) TestSuite._testStepStopper
					.get(this.parentTestCaseID)))
					|| (actionsForCleanup.size() > 0 && Spark.opts.doCleanupOnTimeout)) {
				Spark
				.message("\n******************** Cleanup Action Started for Test Case : "
						+ this.stackTrace
						+ ". ***************************");
				for (int i = count; i < this.actions.size(); ++i) {
					if ((TestSuite._testPlanStopper || ((Boolean)TestSuite._testStepStopper
							.get(this.parentTestCaseID)))
							&& !Spark.opts.doCleanupOnTimeout) {
						break;
					}

					final Action act = this.actions.get(i);

					Log.Debug("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup:  Working on Action  : "
							+ act.name
							+ " for TestCase ID : "
							+ this.testCaseID);
					// We agreed that even if there are Init steps after a
					// cleanup, then that should work as it is.
					// This was required by Urvish initially for
					// Apply-Change-Remove test plan
					// And was accepted by Gilbert Hayes.
					// Gurpreet Anand
					if (!act.step.endsWith("c") && cleanupActionStarted) {
						try {
							Log.Debug("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup:  Calling RunAction for Action : "
									+ act.name
									+ " for TestCase ID : "
									+ this.testCaseID);

							// If the action Steps contain an "i" and there is a
							// cleanup step for it then add that to the stack.
							if (act.step.endsWith("i")
									&& (stepsKeys
											.containsKey(act.step.substring(0,
													act.step.length() - 1)
													+ "c") || stepsKeys
													.containsKey(act.step.substring(0,
															act.step.length() - 1)
															+ "C"))) {
								Log.Debug("Molecule/RunExpandedTestCaseForMolecule:  Action : "
										+ act.name
										+ " with Step : "
										+ act.step
										+ " is an Initialization Action. A cleanup exist for this action ..so pusing this to the STACK");
								actionsForCleanup.push(act.step.substring(0,
										act.step.length() - 1));
							}

							if (stepNumber.equals(act.step)
									&& StringUtils.isNotBlank(act.step)) {
								Thread thread = new Thread(new Runnable() {
									public void run() {
										act.run();
										implicitTestStepCall();
										if(act.property.equalsIgnoreCase("ROS") && StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											
										}else if(act.property.equalsIgnoreCase("ROF") && !StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											removeAndShowExceptionMessage(act);
										
										}
									}
								});
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							} else {
								// Wait for all the Threads to finish.
								for (int t = 0; t < ThreadPool.size(); ++t) {
									((Thread) ThreadPool.get(t)).join();
								}

								ThreadPool.clear();
								if(returnFlag){
									return;
								}
								// The new step number is the current one
								stepNumber = act.step;
								Thread thread = new Thread(new Runnable() {
									public void run() {
										act.run();
										implicitTestStepCall();
										if(act.property.equalsIgnoreCase("ROS") && StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											
										}else if(act.property.equalsIgnoreCase("ROF") && !StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											removeAndShowExceptionMessage(act);
										}
									}
								});
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							}

							Log.Debug("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup:  RunAction executed successfully for Action : "
									+ act.name
									+ " for TestCase ID : "
									+ this.testCaseID);
						}catch(InterruptedException e){
							Log.ErrorInLog("Error_Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
									+ this.testCaseID + ".Thread Interrupted or Test Step timed out" + e.getMessage());
							errorDuringTestCaseExecution += "Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
									+ this.testCaseID + ".Thread Interrupted or Test Step timed out" + e.getMessage();
						}catch (Exception ex) {
							Log.ErrorInLog("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case "
									+ this.testCaseID + "." + ex.getMessage());
							errorDuringTestCaseExecution += "Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case "
									+ this.testCaseID + "." + ex.getMessage();
						}
					} else if (act.step.endsWith("c")
							&& !actionsForCleanup.contains(act.step.substring(
									0, act.step.length() - 1))) {
						Log.Debug("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup: For Action  : "
								+ act.name
								+ " Initialization is not Done. So Skipping it for TestCase ID : "
								+ this.testCaseID);
						continue;
					} else if ((act.step.endsWith("c")
							&& actionsForCleanup.contains(act.step.substring(0,
									act.step.length() - 1))
									&& !TestSuite._testPlanStopper && !((Boolean) TestSuite._testStepStopper
											.get(this.parentTestCaseID)))
											|| (act.step.endsWith("c")
													&& actionsForCleanup
													.contains(act.step.substring(0,
															act.step.length() - 1)) && Spark.opts.doCleanupOnTimeout)) {
						// This marks the start for one Cleanup Action.
						cleanupActionStarted = true;
						try {
							Log.Debug("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup:  Calling RunAction for Action : "
									+ act.name
									+ " for TestCase ID : "
									+ this.testCaseID);

							if (stepNumber.equals(act.step)
									&& StringUtils.isNotBlank(act.step)) {
								Thread thread = new Thread(new Runnable() {

									public void run() {
										act.run();
										implicitTestStepCall();
										if(act.property.equalsIgnoreCase("ROS") && StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											
										}else if(act.property.equalsIgnoreCase("ROF") && !StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											removeAndShowExceptionMessage(act);
										}
									}
								});
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							} else {
								// Wait for all the Threads to finish.
								for (int t = 0; t < ThreadPool.size(); ++t) {
									((Thread) ThreadPool.get(t)).join();
								}

								ThreadPool.clear();
								if(returnFlag){
									return;
								}
								// The new step number is the current one
								stepNumber = act.step;
								Thread thread = new Thread(new Runnable() {

									public void run() {
										act.run();
										implicitTestStepCall();
										if(act.property.equalsIgnoreCase("ROS") && StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											
										}else if(act.property.equalsIgnoreCase("ROF") && !StringUtils
												.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution.get(act.stackTrace))){
											returnFlag=true;
											removeAndShowExceptionMessage(act);
										}
									}
								});
								// thread.IsBackground = true;
								thread.start();
								ThreadPool.add(thread);
							}
							Log.Debug("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup:  RunAction executed successfully for Action : "
									+ act.name
									+ " for TestCase ID : "
									+ this.testCaseID);
						}catch(InterruptedException e){
							Log.ErrorInLog("Error_Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
									+ this.testCaseID + ".Thread Interrupted or Test Step timed out" + e.getMessage());
							errorDuringTestCaseExecution += "Molecule/RunExpandedTestCaseForMolecule: Exception while running test case "
									+ this.testCaseID + ".Thread Interrupted or Test Step timed out" + e.getMessage();
						} catch (Exception ex) {
							Log.ErrorInLog(String
									.format("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case %s.%s",
											this.testCaseID, ex.getMessage()));
							errorDuringTestCaseExecution += String
									.format("Molecule/RunExpandedTestCaseForMolecule - Finally/Cleanup: Exception while running test case %s.%s",
											this.testCaseID, ex.getMessage());
						}
					}
					String nameSpace="";
					if(this.nameSpace.equalsIgnoreCase(Excel.mainNameSpace)){
						nameSpace="main";
					}else{
						nameSpace=this.nameSpace;
					}
					Spark.guiController.removeTestCase(this);
					Spark.guiController.showRunningMolecule(this.testCaseID,nameSpace, false);
				}

				for (int t = 0; t < ThreadPool.size(); ++t) {
					try {
						((Thread) ThreadPool.get(t)).join();
					} catch (InterruptedException e) {
						Log.Error("Molecule/RunExpandedTestCases : Interrupted Exception in calling join() on thread pool");
						e.printStackTrace();
					}
				}
				Spark
				.message("\n******************** Cleanup Action Finished for Test Case : "
						+ this.stackTrace
						+ ". ***************************");
			}
		}

		if (StringUtils.isNotBlank(errorDuringTestCaseExecution)) {
			Log.Debug("Molecule/RunExpandedTestCaseForMolecule: There were exception raised while running TestCase = "
					+ this.testCaseID
					+ " with Exception message as : "
					+ errorDuringTestCaseExecution);
			TestSuite.errorMessageDuringMoleculeCaseExecution
			.put(this.stackTrace,
					((String) TestSuite.errorMessageDuringMoleculeCaseExecution
							.get(this.stackTrace))
							+ errorDuringTestCaseExecution);
		}
	}

	private void runMolecule() throws Exception {

		if (this.automated == false) {
			Log.Debug("Molecule/RunTestCaseForMolecule : TestCase ID {0} is a MANUAL TestCase. Automation Framework is ignoring this TestCase."
					+ this.testCaseID);
			Spark
			.message("\n\nSTATUS : IGNORING(Not Running) FOR TestCase ID : {0}. This is a Manual TestCase."
					+ this.testCaseID);
			return;
		}

		Log.Debug("Molecule/RunTestCaseForMolecule: Start of function with a new TestCase. TestCase ID is "
				+ this.testCaseID);

		Log.Debug("Molecule/RunTestCaseForMolecule: Calling ExpandTestCase With TestCase ID is "
				+ this.testCaseID);
		TestCase[] expandedTestCases = this.ExpandTestCase(false);
		Log.Debug("Molecule/RunTestCaseForMolecule: After Expansion for TestCase ID is "
				+ this.testCaseID
				+ " the number of expanded test case is : "
				+ expandedTestCases.length);

		ArrayList<Thread> ThreadPool = new ArrayList<Thread>();

		for (int i = 0; i < expandedTestCases.length; i++) {
		//	System.out.println("Type casting");
			Molecule test = (Molecule)expandedTestCases[i];
			if(Spark.opts.debugger){
				if(this.breakpoint){
					test.breakpoint=true;
					test.breakpoints=this.breakpoints;
				}	
			}	
		//	System.out.println("After tyrpe casting");
		//	System.out.println("Mol runMol tempPTCI"+test.parentTestCaseID);
		//	System.out.println("Mol runMol ST"+test.stackTrace);
			TestSuite.errorMessageDuringMoleculeCaseExecution.put(
					test.stackTrace, StringUtils.EMPTY);
		}

		// These are not kept in a try catch block. This is to make sure that
		// any time an Exception happens
		// in an Molecule, then don't proceed as they are considered as part of
		// one Abstract Test Case
		// and are not expanded like other TestCases in the TestCases sheet for
		// the MultiValued Macro.
		for (int i = 0; i < expandedTestCases.length; i++) {
			if(Spark.stop){
				return;
			}
			final Molecule test = (Molecule)expandedTestCases[i];
			// Start a new thread and run the expanded test case on it
			if (this.concurrentExecutionOnExpansion == true) {
				Thread thread = new Thread(new Runnable() {

					public void run() {
						test.runExpandedMolecule();
						test.clearResources();
					}
				});

				thread.start();
				// set as background thread
				TestSuite.threadIdForTestCases.put(test.stackTrace, ""
						+ thread.getId());

				ThreadPool.add(thread);
			} else {
				if (StringUtils.isBlank(test.threadID)) {
					TestSuite.threadIdForTestCases.put(test.stackTrace,
							StringUtils.EMPTY);
				} else // Assign the Parent Test Case Thread.
				{
					String tid = "";
					// message("the moleculecall flag "+test1.isConcurrentMoleculeCall);
					if (this.isConcurrentMoleculeCall) {
						tid = "" + Thread.currentThread().getId();
					} else {
						tid = test.threadID;
					}

					TestSuite.threadIdForTestCases.put(test.stackTrace, tid);
				}

				test.runExpandedMolecule();
				
				test.clearResources();
			}

			if (StringUtils
					.isNotBlank(TestSuite.errorMessageDuringMoleculeCaseExecution
							.get(test.stackTrace))
							&& this.concurrentExecutionOnExpansion == false) {
				String errorMsg = (((String) TestSuite.errorMessageDuringMoleculeCaseExecution
						.get(test.stackTrace)));
				TestSuite.errorMessageDuringMoleculeCaseExecution.put(
						test.stackTrace, StringUtils.EMPTY);
				throw new Exception(errorMsg);
			}
		}

		// Wait for all the Threads i.e. expanded test cases to finish.
		for (int t = 0; t < ThreadPool.size(); ++t) {
			((Thread) ThreadPool.get(t)).join();
		}

		String error = StringUtils.EMPTY;
		for (int i = 0; i < expandedTestCases.length; i++) {
			Molecule test = (Molecule)expandedTestCases[i];
			error += (((String) TestSuite.errorMessageDuringMoleculeCaseExecution
					.get(test.stackTrace)));
			TestSuite.errorMessageDuringMoleculeCaseExecution.put(
					test.stackTrace, StringUtils.EMPTY);
		}

		if (StringUtils.isNotBlank(error)) {
			throw new Exception(error);
		}

		Log.Debug("Molecule/RunTestCaseForMolecule: End of function with TestCase ID is "
				+ this.testCaseID);
	}

	private ArrayList<String> sortListByLength(ArrayList<String> unsortedList) {

		ArrayList<String> sortedlist = new ArrayList<String>();
		sortedlist = unsortedList;
		// This logic is failing if argument length are similar
		Comparator<String> reverseLengthCompare = new Comparator<String>() {
			@Override
			public int compare(String string1, String string2) {

				Integer s1len = string1.length();
				Integer s2len = string2.length();
				return s2len.compareTo(s1len);
			}

		};
		Collections.sort(sortedlist, reverseLengthCompare);

		return sortedlist;
	}

	/**
	 * Method to get the index of Positional argument
	 * 
	 * @return indexno Integer
	 */
	// TODO this method is total broken discuss and change it
	private int getMoleculeDefinitionIndex(ArrayList<String> moleculearglist,
			String valuetomatch) throws Exception {
		int count_index = 0;
		boolean argnotfound = true;
		// create a reverse order list checking with the String inputs lengths
		Log.Debug(String
				.format("Molecule/RunAbstractTestCase/getMoleculeDefinitionIndex:: method started with %s , %s",
						moleculearglist, valuetomatch));
		ArrayList<String> sortedbylenlist = new ArrayList<String>();
		sortedbylenlist.addAll(moleculearglist);
		sortedbylenlist = sortListByLength(sortedbylenlist);
		Log.Debug(String
				.format("Molecule/RunAbstractTestCase/getMoleculeDefinitionIndex:: Actual list %s , The sorted list %s",
						moleculearglist, sortedbylenlist));
		String molecule_arg = null;
		for (int i = 0; i < sortedbylenlist.size(); i++) {
			molecule_arg = sortedbylenlist.get(i);
			if (valuetomatch.contains("#" + molecule_arg)) {
				count_index = i;
				argnotfound = false;
				break;
			}

			else {
				argnotfound = true;

			}

		}
		if (argnotfound) {
			throw new Exception(
					"Argument not present in Molecule Definition:: "
							+ valuetomatch.replaceAll("#", ""));
		}
		String finalValueIndexToRetrive = sortedbylenlist.get(count_index);
		Log.Debug(String
				.format("Molecule/RunAbstractTestCase/getMoleculeDefinitionIndex:: method execution ended index no. %s",
						moleculearglist.indexOf(sortedbylenlist
								.get(count_index))));
		return moleculearglist.indexOf(sortedbylenlist.get(count_index));
	}

	/**
	 * Function to check the consistent of the arguments.
	 * 
	 * @return boolean true or false
	 */
	private boolean isParameterPassingConsistent() throws Exception {
		boolean equalsPresent = true, equalsNotPresent = true;
		// Looping to get every arguments from the arraylist
		for (String args : arguments) {
			// Checking every argument if it contains any = or not
			if (args.contains("=")) {
				// Putting the proposition logic Boolean AND operation
				equalsPresent = true && equalsPresent;
				equalsNotPresent = false && equalsNotPresent;

			} else {
				if (arguments.size() != this._testcasemoleculeArgDefn.size()) {
					throw new Exception(
							"Named Argument size mismatch between pass by value and the definition :\n\tNo of Arguments in Molecule Definition: "
									+ this._testcasemoleculeArgDefn.size()
									+ "\n\tNo of Arguments Passed to the Molecule: "
									+ arguments.size());
				}
				equalsNotPresent = true && equalsNotPresent;
				equalsPresent = false && equalsPresent;
			}

		}
		// Boolean OR Operation
		return equalsPresent || equalsNotPresent;
	}

	private String replaceStringOnly(String source, String searchS,
			String replaceS) throws Exception {
		StringBuffer bufferString = new StringBuffer();

		try {
			Pattern pattrn = Pattern.compile("(\\b" + searchS.toLowerCase()
					+ ")(?)");

			Matcher matchr = pattrn.matcher(source.toLowerCase());

			while (matchr.find()) {

				replaceS = Matcher.quoteReplacement(replaceS);

				matchr.appendReplacement(bufferString, replaceS);
			}
			matchr.appendTail(bufferString);
			// message("After Source: "+source+" Search: "+searchS+" Replace: "+replaceS+" replaced: "+bufferString.toString());
		} catch (Exception e) {
			Log.Error("Molecule/RunAbstractTestCase/replaceStringOnly:: Error message: "
					+ e.getMessage()
					+ "\nSource: "
					+ source
					+ "\tSearchString: "
					+ searchS
					+ "\tReplaceString: "
					+ replaceS);
			throw new Exception(
					"Molecule/RunAbstractTestCase/replaceStringOnly:: Error message: "
							+ e.getMessage() + "\nSource: " + source
							+ "\tSearchString: " + searchS
							+ "\tReplaceString: " + replaceS);
		}
		return bufferString.toString();
	}
	
	private String replaceString(String source, String searchS,
			String replaceS) throws Exception {
		StringBuffer bufferString = new StringBuffer();

		try {
			
			Pattern pattrn = Pattern.compile("(" + searchS.toLowerCase()
					+ ")(?)");
			Matcher matchr = pattrn.matcher(source.toLowerCase());

			while (matchr.find()) {

				replaceS = Matcher.quoteReplacement(replaceS);

				matchr.appendReplacement(bufferString, replaceS);
			}
			matchr.appendTail(bufferString);
			// message("After Source: "+source+" Search: "+searchS+" Replace: "+replaceS+" replaced: "+bufferString.toString());
		} catch (Exception e) {
			Log.Error("Molecule/RunAbstractTestCase/replaceStringOnly:: Error message: "
					+ e.getMessage()
					+ "\nSource: "
					+ source
					+ "\tSearchString: "
					+ searchS
					+ "\tReplaceString: "
					+ replaceS);
			throw new Exception(
					"Molecule/RunAbstractTestCase/replaceStringOnly:: Error message: "
							+ e.getMessage() + "\nSource: " + source
							+ "\tSearchString: " + searchS
							+ "\tReplaceString: " + replaceS);
		}
		return bufferString.toString();
	}
	

	private boolean checkArgumentDefinition(String moleculearg)

	{
		Log.Debug(String
				.format("Molecule/RunAbstractTestCase/checkArgumentDefinition:: method started with %s , %s",
						arguments, moleculearg));
		boolean result = false;
		for (String mol_arg : arguments) {
			if (StringUtils.countMatches(mol_arg, "=") > 0) {
				if (mol_arg.toLowerCase().startsWith(
						moleculearg.toLowerCase() + "=")) {
					result = true;
					break;
				}

				else if (StringUtils.countMatches(moleculearg,
						Excel.SplitOnFirstEquals(mol_arg)[0]) > 0) {

					result = true;
					break;
				}
			}
			// Find a elegant logic to check if the argument name is not present
			// it silently pass the #arg to molecule
		}

		Log.Debug(String
				.format("Molecule/RunAbstractTestCase/checkArgumentDefinition:: method execution ended result %s",
						result));
		return result;
	}

	/**
	 * 
	 * @param moleculeDefn
	 * @param callarg
	 * @return true && true
	 */
	private boolean checkIfPositionalArgument(ArrayList<String> moleculeDefn,
			String callarg) {
		boolean argdontcontainsequals = false;
		boolean moldefncontainscallarg = false;
		if (arguments.size() == moleculeDefn.size()) {

			for (String args : arguments) {
				if (args.contains("=")) {
					argdontcontainsequals = false;
					break;
				} else {
					argdontcontainsequals = true;
				}
			}
			for (String molargs : moleculeDefn) {
				if (molargs.toLowerCase().contains(callarg.toLowerCase())) {
					moldefncontainscallarg = true;
					break;
				} else if (callarg.toLowerCase()
						.contains(molargs.toLowerCase())) {
					moldefncontainscallarg = true;
					break;
				}

			}
		}
		Log.Debug(String
				.format("Molecule/RunAbstractTestCase/checkIfPositionalArgument:: moleculedefinition flag %s , argument dont contain equals %s , molecule defintion list %s , argument passing list %s ",
						moldefncontainscallarg, argdontcontainsequals,
						moleculeDefn, arguments));
		return argdontcontainsequals && moldefncontainscallarg;
	}

	private boolean checkIfNamedArgument(String actionValue, String actionname) {
		boolean namedArgFlag = false;
		if (actionValue.toLowerCase().startsWith("#")
				|| actionValue.toLowerCase().startsWith("%#")) {
			namedArgFlag = true;

		}
		if (actionValue.toLowerCase().contains("=#")
				|| actionValue.toLowerCase().contains("=##")
				|| actionValue.toLowerCase().contains("=%#")) {
			namedArgFlag = true;
		} else if (StringUtils.countMatches(actionValue, "#") >= 1) {
			if (actionname.equalsIgnoreCase("SetContextVar")
					|| actionname.equalsIgnoreCase("AppendToContextVar")) {
				String argmntvalarr[] = Excel.SplitOnFirstEquals(actionValue);
				if (argmntvalarr.length == 2) {
					if (argmntvalarr[0].trim().endsWith("##")) {
						namedArgFlag = false;
						Log.Debug("checkIfNamedArgument: These are ThreadSafe Context Variable Initiation "
								+ actionValue);
					}
					if (argmntvalarr[1].trim().endsWith("##%")) {
						namedArgFlag = false;
						Log.Debug("checkIfNamedArgument: These are ThreadSafe Context Variable call in a BuiltInAtom(SetContextVar or AppendToContextVar) "
								+ actionValue);
					}

				}

			}
			if (StringUtils.endsWith(actionValue.trim(), "##")
					|| StringUtils.endsWith(actionValue.trim(), "##%")) {
				namedArgFlag = false;
				Log.Debug("checkIfNamedArgument: These are ThreadSafe Context Variable "
						+ actionValue);

			}

			else {

				if (actionValue.toCharArray().length > 1) {
					namedArgFlag = true;
				} else {
					namedArgFlag = false;
				}
			}
		}
		return namedArgFlag;
	}
	public String checkAndReplaceKeyArgDef(String actionVal) throws Exception{

		if (actionVal!=null&&actionVal.contains("=")) {
			String key = Excel.SplitOnFirstEquals(actionVal)[0];
			String value = Excel.SplitOnFirstEquals(actionVal)[1];
			if(key!=null&&key.startsWith("#")){
				for (String molecule_arg : arguments) {

					String temp_value_split[] = Excel
							.SplitOnFirstEquals(molecule_arg);

					if(actionVal.contains("=")&&StringUtils.countMatches(
							key.replace("#", "").toLowerCase(),
							temp_value_split[0].toLowerCase()) >= 1){

						key=key.replace("#", "");
						if (temp_value_split[0]
								.equalsIgnoreCase(key)) {
							if (temp_value_split.length < 2) {
								throw new Exception(
										"Molecule/RunAbstractTestCase: Formal argument value is Empty:: "
												+ temp_value_split[0]);
							}
							String val2=Excel.SplitOnFirstEquals(actionVal)[1];
							actionVal=temp_value_split[1]+"="+val2;
						}
					}
				}
			}
		}
		return actionVal;
	}
	void run() throws Exception {
		Log.Debug("Molecule/RunAbstractTestCase: Start of function with a new TestCase. TestCase ID is "
				+ this.testCaseID
				+ " and parentTestCaseID = "
				+ parentTestCaseID);
	

		Molecule tempTestCase = new Molecule(this);

		tempTestCase.threadID = (String) TestSuite.threadIdForTestCases
				.get(callingtTestCaseSTACK);
		tempTestCase.stackTrace = callingtTestCaseSTACK + "&" + this.stackTrace;
		tempTestCase.parentTestCaseID = parentTestCaseID;
		Action[] actions = new Action[this.actions.size()];
		this.actions.toArray(actions);
		Log.Debug("Molecule/RunAbstractTestCase: Number of Actions are : "
				+ actions.length + " for testcase : " + this.testCaseID);
		for (Action action : this.actions) {
			Action tempAction = new Action(action);
			tempAction.stackTrace = tempTestCase.stackTrace;
			tempAction.testCaseID = tempTestCase.testCaseID;
			tempAction.parentTestCaseID = parentTestCaseID;

			Log.Debug("Molecule/RunAbstractTestCase: Working on Action "
					+ action.name + " with Step Number as  " + action.step);
			Log.Debug("Molecule/RunAbstractTestCase: Number of Action Arguments are : "
					+ action.arguments.size() + " for action : " + action.name);

			ArrayList<String> tempActionArguments = new ArrayList<String>();
			for (int i = 0; i < action.arguments.size(); ++i) {
				Log.Debug("Molecule/RunAbstractTestCase: Working on Action Argument : "
						+ i + " for action : " + action.name);
				String actionVal = action.arguments.get(i).toString();
				Log.Debug("Molecule/RunAbstractTestCase: actionVal[" + i
						+ "] = " + actionVal + " for action : " + action.name);
				if (actionVal.toLowerCase().contains("$input_arg")
						|| actionVal.toLowerCase().contains("$$input_arg")) {
					boolean isThisAContextVar = false;
					Log.Debug("Molecule/RunAbstractTestCase: actionVal[" + i
							+ "] = " + actionVal
							+ " is an Input Argument for action : "
							+ action.name);

					if (actionVal.contains("=")) {
						Log.Debug("Molecule/RunAbstractTestCase:  actionVal["
								+ i + "] = " + actionVal
								+ " contains an = sign ");
						String[] splitActionVal = Excel
								.SplitOnFirstEquals(actionVal);

						String tempActionVal = splitActionVal[1];

						try {
							if (tempActionVal.startsWith("%")
									&& tempActionVal.endsWith("%")) {
								tempActionVal = Utility.TrimStartEnd(
										tempActionVal, '%', 0);
								tempActionVal = Utility.TrimStartEnd(
										tempActionVal, '%', 1);
								isThisAContextVar = true;
							}

							if (actionVal.toLowerCase().contains("$$input_arg")) {
								tempActionVal = arguments.get(
										Integer.parseInt(tempActionVal
												.substring(11)) - 1).toString();
							} else {
								tempActionVal = arguments.get(
										Integer.parseInt(tempActionVal
												.substring(10)) - 1).toString();
							}
							Log.Debug("Molecule/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ tempActionVal
									+ " is an Input Argument for action : "
									+ action.name);

							if (tempActionVal.contains("=")) {
								tempActionVal = Excel
										.SplitOnFirstEquals(tempActionVal)[1];
							}

							Log.Debug("Molecule/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ tempActionVal
									+ " is an Input Argument for action : "
									+ action.name);
						} catch (Exception e) {
							tempActionVal = StringUtils.EMPTY;
							Log.Debug("Molecule/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ actionVal
									+ " is an Input Argument for action : "
									+ action.name);
						}

						if (!isThisAContextVar) {
							actionVal = splitActionVal[0] + "=" + tempActionVal;
						} else {
							actionVal = splitActionVal[0] + "=%"
									+ tempActionVal + "%";
						}
					} else {
						try {
							if (actionVal.startsWith("%")
									&& actionVal.endsWith("%")) {
								actionVal = Utility.TrimStartEnd(actionVal,
										'%', 0);
								actionVal = Utility.TrimStartEnd(actionVal,
										'%', 1);
								isThisAContextVar = true;
							}

							if (actionVal.toLowerCase().contains("$$input_arg")) {
								actionVal = arguments.get(
										Integer.parseInt(actionVal
												.substring(11)) - 1).toString();
							} else {
								actionVal = arguments.get(
										Integer.parseInt(actionVal
												.substring(10)) - 1).toString();
							}

							if (isThisAContextVar) {
								actionVal = "%" + actionVal + "%";
							}

							Log.Debug("Molecule/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ actionVal
									+ " is an Input Argument for action : "
									+ action.name);
						} catch (Exception e) {
							actionVal = StringUtils.EMPTY;
							Log.Debug("Molecule/RunAbstractTestCase: actionVal["
									+ i
									+ "] = "
									+ actionVal
									+ " is an Input Argument for action : "
									+ action.name);
						}
					}
				} else if (checkIfNamedArgument(actionVal, action.name)) {
					// check for firstoccurrance of # string

					String key = null, value = null;
					// TODO the work for INDEXD MACROOO checking fails the code.
					// Document the behavior to use named argument for = values
					boolean isThisAContextVar = false, foundFormalArg = false, isKeyEnabled = false, isThisContextVarTypeAtom = false;
					if (action.name.equalsIgnoreCase("appendtocontextvar")
							|| action.name.equalsIgnoreCase("setcontextvar")) {
						isThisContextVarTypeAtom = true;
					}
					Log.Debug(String
							.format("Molecule/RunAbstractTestCase:: Action Name %s , Action value %s, Argument list %s",
									action.name, actionVal, arguments));
					// Checking for the arguments if they are consistent
					if (isParameterPassingConsistent()) {

						String token;
						if (actionVal.contains("=")) {
							key = Excel.SplitOnFirstEquals(actionVal)[0];
							value = Excel.SplitOnFirstEquals(actionVal)[1];
							token = value;
							isKeyEnabled = true;
						} else {
							token = actionVal;
							value = actionVal.toLowerCase();
						}
						if (value.startsWith("%") && value.endsWith("%")) {
							value = value.replaceAll("%", "");
							isThisAContextVar = true;
						}
						if (checkArgumentDefinition(value.replaceAll("#", ""))) {
							value = value.replaceAll("#", "");
							for (String molecule_arg : arguments) {
								String temp_value_split[] = Excel
										.SplitOnFirstEquals(molecule_arg);
								if (StringUtils.countMatches(
										value.toLowerCase(),
										temp_value_split[0].toLowerCase()) >= 1) {
									if (temp_value_split[0]
											.equalsIgnoreCase(value)) {
										if (temp_value_split.length < 2) {
											throw new Exception(
													"Molecule/RunAbstractTestCase: Formal argument value is Empty:: "
															+ temp_value_split[0]);
										}
										if (isKeyEnabled) {
											if (isThisAContextVar) {

												actionVal = key + "=%"
														+ temp_value_split[1]
																+ "%";
											} else {
												actionVal = key + "="
														+ temp_value_split[1];
											}
										} else {
											if (isThisAContextVar) {
												actionVal = "%"
														+ temp_value_split[1]
																+ "%";
											} else {
												actionVal = temp_value_split[1];
											}

										}

										foundFormalArg = true;
										break;

									} else {
										/**/
										//System.out.println("token "+token+"\t#"+temp_value_split[0]+"\t"+ temp_value_split[1]);
										/*token.replace("#"+temp_value_split[0], temp_value_split[1]).replace(
												"#", "");*/	
										actionVal=replaceString(token, "#"+temp_value_split[0], temp_value_split[1]).replace(
												"#", "");
										//System.out.println("actionVal "+actionVal);
										if (isThisContextVarTypeAtom
												&& (StringUtils.isNotBlank(key) || StringUtils
														.isNotEmpty(key))) {

											actionVal = key + "=" + actionVal;
										}
									}

								}
							}
						} else if (checkIfPositionalArgument(
								this._testcasemoleculeArgDefn,
								value.replaceAll("#", ""))) {

							int indexNo = getMoleculeDefinitionIndex(
									this._testcasemoleculeArgDefn, value);
							Log.Debug(String
									.format("Molecule/RunAbstractTestCase:: Index no %s for Positional Argument list %s",
											indexNo, arguments.get(indexNo)));
							if (indexNo < 0) {
								throw new Exception(
										String.format(
												"Molecule/RunAbstractTestCase: %s is not present in molecule definition %s ",
												value,
												this._testcasemoleculeArgDefn));
							}
							actionVal = replaceStringOnly(token,
									this._testcasemoleculeArgDefn.get(indexNo),
									arguments.get(indexNo)).replace("#", "");
							if (isKeyEnabled) {
								actionVal = key + "=" + actionVal;
							}
							Log.Debug(String
									.format("Molecule/RunAbstractTestCase:: Final replaced positional action argument is %s",
											actionVal));
						} else {
							Log.Debug(String
									.format("Molecule/RunAbstractTestCase:: %s Not a Moleculue definition. ",
											value));
						}
						actionVal=checkAndReplaceKeyArgDef(actionVal);

					} else {
						throw new Exception(
								"Argument Passing Not Consistent:Either use named argument passing or positional argument passing. ");
					}
				}
				try {
				//	Excel readExcel = new Excel();
					if (action.arguments.get(i).toString().toLowerCase()
							.contains("$$input_arg")) {
						actionVal = Spark.readExcel
								.FindInMacroAndEnvTableForSingleVector(
										actionVal, action.nameSpace);
					}
					// TODO have to put some check if the Value is comming $$#
					// for named argument.>>>
					Log.Debug("Molecule/RunAbstractTestCase: actionVal[" + i
							+ "] = " + actionVal
							+ " is the Final Value for action : " + action.name);

					tempAction.arguments.add(actionVal);
				} catch (Exception e) {
					throw new Exception("Molecule/RunAbstractTestCase: "
							+ e.getMessage());
				}
			}

			Verification[] verifications = new Verification[action.verification
			                                                .size()];
			action.verification.toArray(verifications);
			Log.Debug("Molecule/RunAbstractTestCase: Number of verifications are : "
					+ verifications.length + " for Action : " + action.name);
			for (Verification verification : action.verification) {
				Log.Debug("Molecule/RunAbstractTestCase: Working on Verification {0}. "
						+ verification.name);
				Verification tempVerification = new Verification(verification);
				tempVerification.testCaseID = tempTestCase.testCaseID;
				tempVerification.parentTestCaseID = this.parentTestCaseID;
				tempVerification.stackTrace = tempTestCase.stackTrace;
				Log.Debug("Molecule/RunAbstractTestCase: Number of Verification Arguments = "
						+ verification.arguments.size()
						+ " for Verification "
						+ verification.name);
				for (String verificationVal : verification.arguments) {

					if (verificationVal.toLowerCase().contains("$input_arg")
							|| verificationVal.toLowerCase().contains(
									"$$input_arg")) {
						boolean isThisAContextVar = false;
						if (verificationVal.contains("=")) {
							String[] splitVerificationVal = Excel
									.SplitOnFirstEquals(verificationVal);

							String tempVerificationVal = splitVerificationVal[1];

							try {
								if (tempVerificationVal.startsWith("%")
										&& tempVerificationVal.endsWith("%")) {
									tempVerificationVal = Utility.TrimStartEnd(
											tempVerificationVal, '%', 0);
									tempVerificationVal = Utility.TrimStartEnd(
											tempVerificationVal, '%', 1);
									isThisAContextVar = true;
								}

								if (verificationVal.toLowerCase().contains(
										"$$input_arg")) {
									tempVerificationVal = arguments
											.get(Integer
													.parseInt(tempVerificationVal
															.substring(11)) - 1)
															.toString();
								} else {
									tempVerificationVal = arguments
											.get(Integer
													.parseInt(tempVerificationVal
															.substring(10)) - 1)
															.toString();
								}

								if (tempVerificationVal.contains("=")) {
									tempVerificationVal = Excel
											.SplitOnFirstEquals(tempVerificationVal)[1];
								}

							} catch (Exception e) {
								tempVerificationVal = StringUtils.EMPTY;
							}

							if (!isThisAContextVar) {
								verificationVal = splitVerificationVal[0] + "="
										+ tempVerificationVal;
							} else {
								verificationVal = splitVerificationVal[0]
										+ "=%" + tempVerificationVal + "%";
							}

						} else {
							try {
								if (verificationVal.startsWith("%")
										&& verificationVal.endsWith("%")) {
									verificationVal = Utility.TrimStartEnd(
											verificationVal, '%', 0);
									verificationVal = Utility.TrimStartEnd(
											verificationVal, '%', 1);
									isThisAContextVar = true;
								}

								if (verificationVal.toLowerCase().contains(
										"$$input_arg")) {
									verificationVal = arguments.get(
											Integer.parseInt(verificationVal
													.substring(11)) - 1)
													.toString();
								} else {
									verificationVal = arguments.get(
											Integer.parseInt(verificationVal
													.substring(10)) - 1)
													.toString();
								}

								if (isThisAContextVar) {
									verificationVal = "%" + verificationVal
											+ "%";
								}

							} catch (Exception e) {
								verificationVal = StringUtils.EMPTY;

							}
						}
					}

					else if (checkIfNamedArgument(verificationVal,
							verification.name)) {
						String key = null, value = null;
						Log.Debug("Molecule/RunAbstractTestCase:: Named Argument Verification Molecule Block execution strated");
						boolean isThisAContextVar = false, foundFormalArg = false, isKeyEnabled = false;
						// message("Runabstract:: Actionss--"+actionVal);
						// Checking for the arguments if they are consistent
						if (isParameterPassingConsistent()) {

							String token;
							if (verificationVal.contains("=")) {
								key = Excel.SplitOnFirstEquals(verificationVal)[0];
								value = Excel
										.SplitOnFirstEquals(verificationVal)[1];
								token = value;
								isKeyEnabled = true;
							} else {
								token = verificationVal;
								value = verificationVal.toLowerCase();
							}
							// message("RUNABSTRACT::-1 Key value- "+key);
							if (value.startsWith("%") && value.endsWith("%")) {
								value = value.replaceAll("%", "");
								isThisAContextVar = true;
							}
							value = value.replaceAll("#", "");
							// message("RUNABSTRACT::1a Key value- "+value+"\targumentss "+argumentValues);
							Log.Debug(String
									.format("Molecule/RunAbstractTestCase:: Working on named argument %s. The argument List from testacse %s",
											value, arguments));
							if (checkArgumentDefinition(value.replaceAll("#",
									""))) {
								for (String molecule_arg : arguments) {
									String temp_value_split[] = Excel
											.SplitOnFirstEquals(molecule_arg);
									// message("RUNABSTRACT::2 Key value- "+value+"\targumentss "+argumentValues);
									if (temp_value_split[0]
											.equalsIgnoreCase(value)) {
										if (temp_value_split.length < 2) {
											throw new Exception(
													"Molecule/RunAbstractTestCase: Formal argument value is Empty:: "
															+ temp_value_split[0]);
										}
										if (isKeyEnabled) {
											if (isThisAContextVar) {
												verificationVal = key + "=%"
														+ temp_value_split[1]
																+ "%";
											} else {
												verificationVal = key + "="
														+ temp_value_split[1];
											}
										} else {
											if (isThisAContextVar) {
												verificationVal = "%"
														+ temp_value_split[1]
																+ "%";
											} else {
												verificationVal = temp_value_split[1];
											}

										}

										// message("RUNABSTRACT:: step forloop "+actionVal);
										foundFormalArg = true;
										break;

									}
								}
								if (!foundFormalArg) {
									throw new Exception(
											"Formal Argument Not found");
								}
							} else if (checkIfPositionalArgument(
									this._testcasemoleculeArgDefn,
									value.replaceAll("#", ""))) {

								// message("the verification args "+argumentValues+" verivals "+verificationVal+" value "+value);

								int indexNo = getMoleculeDefinitionIndex(
										this._testcasemoleculeArgDefn, "#"
												+ value);
								Log.Debug(String
										.format("Molecule/RunAbstractTestCase:: Index No. %s for argument Values %s",
												indexNo, arguments.get(indexNo)));
								// message("Indexno " + indexNo
								// +" RUNABSTRACT:: " + );
								if (indexNo < 0) {
									throw new Exception(
											String.format(
													"Molecule/RunAbstractTestCase: %s is not present in molecule definition %s ",
													value,
													this._testcasemoleculeArgDefn));
								}
								// message("Index is > 0 "+actionVal);
								verificationVal = replaceStringOnly(
										token,
										this._testcasemoleculeArgDefn
										.get(indexNo),
										arguments.get(indexNo))
										.replace("#", "");
								if (isKeyEnabled) {
									verificationVal = key + "="
											+ verificationVal;
								}
								Log.Debug(String
										.format("Molecule/RunAbstractTestCase:: Replaced final verification value %s",
												verificationVal));
							} else {
								Log.Debug(String
										.format("Molecule/RunAbstractTestCase:: %s is not a Named Argument Defined in Molecule Definition.",
												value));
							}
							verificationVal=checkAndReplaceKeyArgDef(verificationVal);
						} else {
							throw new Exception(
									"Argument Passing Not Consistent:Either use named argument passing or positional argument passing");
						}
						// message("RUNABSTRACT:: The Action Valuee "+actionVal+"\nThe index is "+test._testcasemoleculeArgDefn.indexOf(value)+"\t The TestCase ArrayList "+test._testcasemoleculeArgDefn);
					}

					try {
						if (verificationVal.toLowerCase().contains(
								"$$input_arg")) {
							verificationVal =Spark.readExcel
									.FindInMacroAndEnvTableForSingleVector(
											verificationVal,
											verification.nameSpace);
						}
						// TODO have to put some check if the Value is comming
						// $$# for named argument.>>>

						tempVerification.arguments.add(verificationVal);
					} catch (Exception e) {
						// Log.Error("Molecule/RunAbstractTestCase: "+e.getMessage());
						throw new Exception("Molecule/RunAbstractTestCase: "
								+ e.getMessage());
					}
				}
				tempVerification.parent=tempTestCase;
				tempAction.verification.add(tempVerification);
			}
			tempAction.parent=tempTestCase;
			tempTestCase.actions.add(tempAction);
		}

		try {

            Spark.message("\n********************************************************************************");
			Spark.message("Running Molecule : "
					+ tempTestCase.stackTrace);
			// Now everything is ready. Call the RunTestCase Function.
			tempTestCase.isConcurrentMoleculeCall = this.isConcurrentMoleculeCall;
		//	System.out.println("Mol run tempPTCI"+tempTestCase.parentTestCaseID);
		//	System.out.println("Mol run ST"+tempTestCase.stackTrace);
			tempTestCase.runMolecule();
		} catch (Exception ex) {
			Log.Error("Exception while running Abstract Test Case "
					+ tempTestCase.stackTrace + " is : " + ex.getMessage());

			throw new Exception(ex.getMessage());
			// }
		} finally {
            Spark.message("********************************************************************************");
			Spark.message("Molecule Execution Finished : " + tempTestCase.stackTrace);
		}
		Log.Debug("Molecule/RunAbstractTestCase: End of function with a new TestCase. TestCase ID is "
				+ this.testCaseID);
	}

}
