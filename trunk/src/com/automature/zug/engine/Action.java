package com.automature.zug.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

public class Action extends GTuple {

	public String step = null;

	public ArrayList<Verification> verification;
	public Boolean isActionNegative = false;
	// Action Description
	public String actionDescription = null;
	// Action Property
	public String actionProperty = null;
	public UserData userObj;
	// Molecule argument Definition
	public ArrayList<String> _actionmoleculeArgDefn = new ArrayList<String>();
	public ArrayList<String> actionActualargsDuplicateRemoved = new ArrayList<String>();
	public HashMap<String, String> _mvmmap = new HashMap<String, String>();

	public Action() {
		verification = new ArrayList<Verification>();

	}

	public Action(Action act) {
		super(act);
		this.step = act.step;
		this.verification = act.verification;
		this.isActionNegative = act.isActionNegative;
		this.actionDescription = act.actionDescription;
		this.actionProperty = act.actionProperty;
		this.userObj = act.userObj;
		this._actionmoleculeArgDefn = act._actionmoleculeArgDefn;
		this.actionActualargsDuplicateRemoved = act.actionActualargsDuplicateRemoved;
		this._mvmmap = act._mvmmap;
		verification = new ArrayList<Verification>();

	}
	
	public void run(){
		String testCaseID = this.testCaseID;
		try{
			boolean exceptionOccured=true;
			String threadID = (String) TestSuite.threadIdForTestCases
					.get(this.stackTrace);
			try {
				Log.Debug("Action/run : Name of the Action is : " + this.name
						+ " for TestCaseID  : " + testCaseID);
				super.run(threadID);
				exceptionOccured=false;
				if(this.isActionNegative){
					throw new Exception(
							String.format(
									"\n\nException: The test step  passed while property is set to negative-action or neg-action."));
				}	
			}catch(Exception e){
				if (this.isActionNegative && exceptionOccured) {
					if (!StringUtils
							.isBlank(TestSuite.errorMessageDuringTestCaseExecution
									.get(this.parentTestCaseID))) {
						TestSuite.errorMessageDuringTestCaseExecution
						.put(this.parentTestCaseID,StringUtils.EMPTY);
					}
					ContextVar.setContextVar("ZUG_ACTION_EXCEPTION",
							Argument.NormalizeVariable(
									String.format(
											"\n\nException in Action %s (%s:%s).\n\t Message: %s",
											this.name,
											this.sheetName,
											this.lineNumber,
											e.getMessage()), threadID));


				}else {						
					throw new Exception(
							String.format(
									"\n\nException in Action %s (%s:%s).\n\t Message: %s",
									this.name, this.sheetName,
									this.lineNumber+1, e.getMessage()));
				}
			}
			for (int j = 0; j < this.verification.size(); j++) {
				if (StringUtils.isBlank(verification.get(j).name)) {
					continue;
				}
				this.verification.get(j).run(threadID);
			}
		}catch(Exception e){
			if(Controller.opts.ignore){
				Controller.message("Ignore testcases is on:Exception message"+e.getMessage());
				if (!StringUtils
						.isBlank(TestSuite.errorMessageDuringTestCaseExecution
								.get(this.parentTestCaseID))) {
					TestSuite.errorMessageDuringTestCaseExecution
					.put(this.parentTestCaseID,StringUtils.EMPTY);
				}
			}else{
				this.putExceptionMessage(e);
			}	
			
		}
	}
	
	
	/*
	public void run() {
		
		String testCaseID = this.testCaseID;
		try {
			String threadID = (String) TestSuite.threadIdForTestCases
					.get(this.stackTrace);
		
			Log.Debug("Action/RunAction : Name of the Action is : " + this.name
					+ " for TestCaseID  : " + testCaseID);
			if (this.name.startsWith("&")) {
				// Run an Abstract Test Case
				String abstractTestCaseName = Utility.TrimStartEnd(this.name,
						'&', 1);
				Log.Debug(String
						.format("Action/RunAction: Verifying if the abstract TestCase Exists in the sheet : %s ",
								abstractTestCaseName));

				// Check if the Abstract TestCase ID Exists
				if (TestSuite.abstractTestCase.get(Excel.AppendNamespace(
						abstractTestCaseName, this.nameSpace)) != null) {
					ArrayList<String> tempList = new ArrayList<String>();
					for (int i = 0; i < this.arguments.size(); i++) {
						Log.Debug("Action/RunAction: Working on Action  Argument : "
								+ i);
						String actionVal = this.arguments.get(i).toString();
						Log.Debug("Action/RunAction: Working on Action  Argument : "
								+ i
								+ " With  actionVal : "
								+ actionVal
								+ " && NormalizeVariable = "
								+ Argument.NormalizeVariable(actionVal,
										threadID));
						tempList.add(Argument.NormalizeVariable(actionVal,
								threadID));
					}

					Log.Debug(String
							.format("Action/RunAction: Calling  RunAbstractTestCase for Abstract TestCase ID as : %s and action.parentTestCaseID = %s .",
									abstractTestCaseName, this.parentTestCaseID));
				
					Molecule tempActntestcase = TestSuite.abstractTestCase
							.get(Excel.AppendNamespace(abstractTestCaseName,
									this.nameSpace));
					tempActntestcase.setArguments(tempList);
					tempActntestcase.setCallingtTestCaseSTACK(this.stackTrace);
					tempActntestcase.setParentTestCaseID(this.parentTestCaseID);
					try {
						Integer.parseInt(this.step);
						tempActntestcase.isConcurrentMoleculeCall = true;
					} catch (NumberFormatException ne) {
						// Do nothing
					}
					boolean exceptionOccured=true;
					
					try{
						Action a=tempActntestcase.actions.get(0);
						if(a.name.startsWith("#define")){
							if(a.arguments.size()!=tempList.size()){
								Controller.message("Exception:No of arguments Mismatched.The molecule "+tempActntestcase.testCaseID+" takes "+a.arguments.size()+" arguments\nNumber of arguments passed is "+tempList.size());
								throw new Exception("No of arguments Mismatched.The molecule "+tempActntestcase.testCaseID+" takes "+a.arguments.size()+" arguments\nNumber of arguments passed is "+tempList.size());
							}
						}
						tempActntestcase.run();
						exceptionOccured=false;
						if(this.isNegative||this.isActionNegative){
							throw new Exception(
									String.format(
											"\n\nException: The test step  passed while property is set to negative or negative-action or neg-action."));
						}
					}
					catch(Exception e){
						if (this.isNegative && exceptionOccured) {
							ContextVar
							.setContextVar(
									"ZUG_EXCEPTION",
									Argument.NormalizeVariable(
											String.format(
													"\n\nException in Action %s (%s:%s).\n\t Message: %s",
													this.name, this.sheetName,
													this.lineNumber,
													e.getMessage()), threadID));
							if (!StringUtils
									.isBlank(TestSuite.errorMessageDuringTestCaseExecution
											.get(this.parentTestCaseID))) {
								TestSuite.errorMessageDuringTestCaseExecution
								.put(this.parentTestCaseID,StringUtils.EMPTY);
							}
							
							
						} else if (this.isActionNegative && exceptionOccured) {
							if (!StringUtils
									.isBlank(TestSuite.errorMessageDuringTestCaseExecution
											.get(this.parentTestCaseID))) {
								TestSuite.errorMessageDuringTestCaseExecution
								.put(this.parentTestCaseID,StringUtils.EMPTY);
							}
							ContextVar
								.setContextVar(
										"ZUG_ACTION_EXCEPTION",
										Argument.NormalizeVariable(
												String.format(
														"\n\nException in Action %s (%s:%s).\n\t Message: %s",
														this.name,
														this.sheetName,
														this.lineNumber,
														e.getMessage()), threadID));
							
					
						}else if(this.isIgnore && exceptionOccured){
							if (!StringUtils
									.isBlank(TestSuite.errorMessageDuringTestCaseExecution
											.get(this.parentTestCaseID))) {
								TestSuite.errorMessageDuringTestCaseExecution
								.put(this.parentTestCaseID,StringUtils.EMPTY);
							}
							Controller.message(
									String.format(
											"\nIgnore is true\nException in Action %s (%s:%s).\n\t Message: %s",
											this.name, this.sheetName,
											this.lineNumber, e.getMessage()));
						}	else {						
								throw new Exception(
										String.format(
												"\n\nException in Action %s (%s:%s).\n\t Message: %s",
												this.name, this.sheetName,
												this.lineNumber, e.getMessage()));
							}
					}
					Log.Debug(String
							.format("Action/RunAction: Successfully executed  RunAbstractTestCase for Abstract TestCase ID as : %s ",
									abstractTestCaseName));

					Log.Debug(String
							.format("Action/RunAction : Calling RunVerification for action %s....",
									this.name));
					Log.Debug(String
							.format("Action/RunVerification : Number of Verifications for %s Action is %s",
									this.name, this.verification.size()));
					for (int j = 0; j < this.verification.size(); j++) {
						// Verification verification = verifications[j];

						if (StringUtils.isBlank(verification.get(j).name)) {
							continue;
						}
						this.verification.get(j).run(threadID);
					}
					Log.Debug(String
							.format("Action/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
									this.name));

				} else {
					throw new Exception(
							"Action/RunAction : Unrecognized Molecule ["
									+ abstractTestCaseName
									+ "] Action specified for TestCase ID # "
									+ testCaseID + " which is located at Line "
									+ this.lineNumber + 1 + " of Sheet "
									+ this.sheetName + ".");
				}
			} else {
				Log.Debug("Action/RunAction : Running Command " + this.name
						+ " for TestCase ID as : " + testCaseID);

				AtomHandler ah = new AtomHandler();
				boolean exceptionOccured=true;
				try{
					ah.handle(this, threadID);
					exceptionOccured=false;
					if(this.isNegative||this.isActionNegative){
						throw new Exception(
								String.format(
										"\n\nException: The test step  passed while property is set to negative or negative-action or neg-action."));
					}
				}catch(Exception e){
					if (this.isNegative && exceptionOccured) {
						ContextVar
						.setContextVar(
								"ZUG_EXCEPTION",
								Argument.NormalizeVariable(
										String.format(
												"\n\nException in Action %s (%s:%s).\n\t Message: %s",
												this.name, this.sheetName,
												this.lineNumber,
												e.getMessage()), threadID));
					} else if (this.isActionNegative && exceptionOccured) {
							ContextVar
							.setContextVar(
									"ZUG_ACTION_EXCEPTION",
									Argument.NormalizeVariable(
											String.format(
													"\n\nException in Action %s (%s:%s).\n\t Message: %s",
													this.name,
													this.sheetName,
													this.lineNumber,
													e.getMessage()), threadID));
						}else if(this.isIgnore && exceptionOccured ){
							Controller.message(
									String.format(
											"\nIgnore is true\nException in Action %s (%s:%s).\n\t Message: %s",
											this.name, this.sheetName,
											this.lineNumber, e.getMessage()));
						}
					else {
							
							throw new Exception(
									String.format(
											"\n\nException in Action %s (%s:%s).\n\t Message: %s",
											this.name, this.sheetName,
											this.lineNumber, e.getMessage()));
							
						}
				}
				Log.Debug(String
						.format("Action/RunAction : Calling RunVerification for action %s....",
								this.name));

				Log.Debug(String
						.format("Action/RunVerification : Number of Verifications for %s Action is %s",
								this.name, this.verification.size()));
				for (int j = 0; j < this.verification.size(); j++) {
					if (StringUtils.isBlank(verification.get(j).name)) {
						continue;
					}
					this.verification.get(j).run(threadID);
				}
				Log.Debug(String
						.format("Action/RunAction : SUCCESSFULLY Executed  RunVerification for action %s....",
								this.name));

			}
		} catch (Exception ex) {
		//	if(this.isIgnore=false){
			if (StringUtils
					.isBlank(TestSuite.errorMessageDuringMoleculeCaseExecution
							.get(this.stackTrace))) {
				TestSuite.errorMessageDuringMoleculeCaseExecution
				.put(this.stackTrace,
						((String) TestSuite.errorMessageDuringMoleculeCaseExecution
								.get(this.stackTrace))
								+ "\n\t"
								+ ex.getMessage());			
			}
			
			if (StringUtils
					.isBlank(TestSuite.errorMessageDuringTestCaseExecution
							.get(this.parentTestCaseID))) {
				TestSuite.errorMessageDuringTestCaseExecution
				.put(this.parentTestCaseID,
						((String) TestSuite.errorMessageDuringTestCaseExecution
								.get(this.parentTestCaseID))
								+ "\n\t"
								+ ex.getMessage());
			}
		//	}
		//	else{
		//		Controller.message(ex.getMessage());
		//	}

		}

	}*/

}
