package com.automature.zug.engine;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;

public class Verification extends GTuple {
	
	boolean isVerifyNegative=false;
	public Verification() {
		super();
	
	}

	public Verification(Verification ver) {
		super(ver);
		this.isVerifyNegative = ver.isVerifyNegative;
	}
	
	//public Boolean isVerifyNegative = false;

	public void run(String threadID){
		try{
			boolean exceptionOccured=true;
			try {
				Log.Debug("Verification/run : Name of the Action is : " + this.name
						+ " for TestCaseID  : " + testCaseID);
				super.run(threadID);
				exceptionOccured=false;
				if(this.isVerifyNegative){
					throw new Exception(
							String.format(
									"\n\nException: The test step  passed while property is set to negative-verify or neg-verify."));
				}
				
			}catch(Exception e){
				if (this.isVerifyNegative && exceptionOccured) {
					if (!StringUtils
							.isBlank(TestSuite.errorMessageDuringTestCaseExecution
									.get(this.parentTestCaseID))) {
						TestSuite.errorMessageDuringTestCaseExecution
						.put(this.parentTestCaseID,StringUtils.EMPTY);
					}
					ContextVar.setContextVar("ZUG_ACTION_EXCEPTION",
							Argument.NormalizeVariable(
									String.format(
											"\n\nException in Verification %s (%s:%s).\n\t Message: %s",
											this.name,
											this.sheetName,
											this.lineNumber,
											e.getMessage()), threadID));

				}else {						
					throw new Exception(
							String.format(
									"\n\nException in Verification %s (%s:%s).\n\t Message: %s",
									this.name, this.sheetName,
									this.lineNumber+1, e.getMessage()));
				}
			}
		
		}catch(Exception e){
			this.putExceptionMessage(e);
		}
	}
	/*
	
	void run(String threadID) throws Exception {
		try {
			Log.Debug(String.format(
					"Verification/RunVerification : Working on Verification %s",
					this.name));

			if (this.name.startsWith("&")) {
				// Run an Abstract Test Case
				String abstractTestCaseName = Utility.TrimStartEnd(this.name,
						'&', 1);
				Log.Debug(String
						.format("Verification/RunVerification: Verifying if the abstract TestCase Exists in the sheet : %s ",
								abstractTestCaseName));

				// Check if the Abstract TestCase ID Exists
				if (TestSuite.abstractTestCase.get(Excel.AppendNamespace(
						abstractTestCaseName, this.nameSpace)) != null) {
					Log.Debug(String
							.format("Verification/RunVerification: Calling  RunAbstractTestCase for Abstract TestCase ID as : %s & action.parentTestCaseID = %s .",
									abstractTestCaseName, this.parentTestCaseID));

					ArrayList<String> tempList = new ArrayList<String>();
					for (int i = 0; i < this.arguments.size(); ++i) {
						Log.Debug("Verification/RunVerification: Working on Verification  Argument : "
								+ i + " for action : " + this.name);
						String verificationVal = this.arguments.get(i)
								.toString();
						Log.Debug("Verification/RunVerification: Working on Verification  Argument : "
								+ i
								+ " With  verificationVal : "
								+ verificationVal
								+ " && NormalizeVariable = "
								+ Argument.NormalizeVariable(verificationVal,
										threadID));
						tempList.add(Argument.NormalizeVariable(
								verificationVal, threadID));
					}
					Molecule tempActntestcase = TestSuite.abstractTestCase
							.get(Excel.AppendNamespace(abstractTestCaseName,
									this.nameSpace));
					tempActntestcase.setArguments(tempList);
					tempActntestcase.setCallingtTestCaseSTACK(this.stackTrace);
					tempActntestcase.setParentTestCaseID(this.testCaseID);
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
						if(this.isNegative||this.isVerifyNegative){
							throw new Exception(
									String.format(
											"\n\nException: The test step  passed while property is set to negative or negative-verification or neg-verify."));
						}
					}
					catch(Exception e){
						 if ((this.isNegative || this.isVerifyNegative)
								&& exceptionOccured) {
							
							ContextVar
							.setContextVar(
									"ZUG_VERIFY_EXCEPTION",
									Argument.NormalizeVariable(
											String.format(
													"\n\nException in Verification %s (%s:%s).\n\tMessage: %s",
													this.name,
													this.sheetName,
													this.lineNumber,
													e.getMessage()), threadID));
							if (!StringUtils
									.isBlank(TestSuite.errorMessageDuringTestCaseExecution
											.get(this.parentTestCaseID))) {
								TestSuite.errorMessageDuringTestCaseExecution
								.put(this.parentTestCaseID,StringUtils.EMPTY);
							}
						
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
						} else {
							
								throw new Exception(
										String.format(
												"\n\nException in Verification %s (%s:%s).\n\t Message: %s",
												this.name, this.sheetName,
												this.lineNumber, e.getMessage()));
								
							}
					}
					Log.Debug(String
							.format("Verification/RunVerification: Successfully executed  RunAbstractTestCase for Abstract TestCase ID as : %s ",
									abstractTestCaseName));
				} else {
					throw new Exception(
							"Verification/RunVerification : Unrecognized Molecule ["
									+ abstractTestCaseName
									+ "] Verification specified for TestCase ID # "
									+ testCaseID + " which is located at Line "
									+ this.lineNumber + 1 + " of Sheet "
									+ this.sheetName + ".");
				}
			} else {
				Log.Debug("Verification/RunVerification : Running Command "
						+ this.name + " for TestCase ID as : " + testCaseID);

				AtomHandler ah = new AtomHandler();
				boolean exceptionOccured=true;
				try{
					ah.handle(this, threadID);
					exceptionOccured=false;
					if(this.isNegative||this.isVerifyNegative){
						throw new Exception(
								String.format(
										"\n\nException: The test step  passed while property is set to negative or negative-verification or neg-verify."));
					}
				}	
				catch(Exception e){
					if (this.isNegative && exceptionOccured) {
						ContextVar
						.setContextVar(
								"ZUG_EXCEPTION",
								Argument.NormalizeVariable(
										String.format(
												"\n\nException in Verification %s (%s:%s).\n\t Message: %s",
												this.name, this.sheetName,
												this.lineNumber,
												e.getMessage()), threadID));
					} else if ((this.isNegative || this.isVerifyNegative)
							&& exceptionOccured) {
						ContextVar
						.setContextVar(
								"ZUG_VERIFY_EXCEPTION",
								Argument.NormalizeVariable(
										String.format(
												"\n\nException in Verification %s (%s:%s).\n\tMessage: %s",
												this.name,
												this.sheetName,
												this.lineNumber,
												e.getMessage()), threadID));
					} else if(this.isIgnore && exceptionOccured){
						Controller.message(
								String.format(
										"\nIgnore is true\nException in Action %s (%s:%s).\n\t Message: %s",
										this.name, this.sheetName,
										this.lineNumber, e.getMessage()));
					}else {
							throw new Exception(
									String.format(
											"\n\nException in Verification %s (%s:%s).\n\t Message: %s",
											this.name, this.sheetName,
											this.lineNumber, e.getMessage()));
							
						}
				}

				Log.Debug(String
						.format("Verification/RunVerification : SUCCESSFULLY Executed  RunVerification  %s....",
								this.name));

			}
		} catch (Exception ex) {
		//	if(this.isIgnore==false){
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
		//	}else{
		//		Controller.message(ex.getMessage());
		//	}
		}
	}*/
}
