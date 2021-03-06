package com.automature.spark.engine;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

public class GTuple {
	
	
	public String parentTestCaseID = null;

	public String nameSpace = null;

	public String stackTrace = null;

	// The test case ID to which this Action belongs
	public String testCaseID = null;
	public ArrayList<String> arguments;

	/*
	 * Datastructure to store multiple actionArguments. Note that they are the
	 * Actual Arguments, without any Manipulation.
	 */
	public ArrayList<String> actualArguments = new ArrayList<String>();
	public Boolean isNegative = false;
	boolean isIgnore = false;
	public int lineNumber = 0;
	public String sheetName = null;
	public String name = null;
	public Boolean isComment = false;
	public UserData userObj;
	String property="";
	TestCase parent;

	public GTuple() {
		super();
		arguments = new ArrayList<String>();
	}

	public GTuple(GTuple gt) {
		// System.out.println("Inside Gtuple");

		this.parentTestCaseID = gt.parentTestCaseID;
		this.nameSpace = gt.nameSpace;
		this.stackTrace = gt.stackTrace;
		this.testCaseID = gt.testCaseID;
		// this.arguments = gt.arguments;
		this.actualArguments = gt.actualArguments;
		this.isNegative = gt.isNegative;
		this.lineNumber = gt.lineNumber;
		this.sheetName = gt.sheetName;
		this.name = gt.name;
		this.isComment = gt.isComment;
		this.userObj = gt.userObj;
		this.isIgnore = gt.isIgnore;
		this.property=gt.property;
		arguments = new ArrayList<String>();
		this.parent=gt.parent;
	}
	
	public void putExceptionMessage(Exception ex){
	//	System.out.println("putting exception message:stack trace"+this.stackTrace);
	//	System.out.println("putting exception message:parentTestCaseID"+this.parentTestCaseID);
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
	}
	
	void run(String threadID) throws Exception {
		boolean exceptionOccured = true;
	//	System.out.println("isNegative: "+this.isNegative);
		try {
		//	System.out.println("thread id"+threadID);
			if (this.name.startsWith("&")) {
				// Run an Abstract Test Case
				String abstractTestCaseName = Utility.TrimStartEnd(this.name,
						'&', 1);
				Log.Debug(String
						.format("GTuple/run: Verifying if the abstract TestCase Exists in the sheet : %s ",
								abstractTestCaseName));
				// Check if the Abstract TestCase ID Exists
				if (TestSuite.abstractTestCase.get(Excel.AppendNamespace(
						abstractTestCaseName, this.nameSpace)) != null) {
					ArrayList<String> tempList = new ArrayList<String>();
					for (int i = 0; i < this.arguments.size(); i++) {
						Log.Debug("GTuple/run: Working on Action  Argument : "
								+ i);
						String actionVal = this.arguments.get(i).toString();
						Log.Debug("GTuple/run: Working on Action  Argument : "
								+ i
								+ " With  actionVal : "
								+ actionVal
								+ " && NormalizeVariable = "
								+ Argument.NormalizeVariable(actionVal,
										threadID,this));
						tempList.add(Argument.NormalizeVariable(actionVal,
								threadID,this));
					}

					Log.Debug(String
							.format("GTuple/run: Calling  RunAbstractTestCase for Abstract TestCase ID as : %s and action.parentTestCaseID = %s .",
									abstractTestCaseName, this.parentTestCaseID));

					Molecule tempMol = TestSuite.abstractTestCase
							.get(Excel.AppendNamespace(abstractTestCaseName,
									this.nameSpace));
					Molecule tempActntestcase=new Molecule(tempMol);
//					if(Controller.opts.debugger){
//						Controller.breakpoints.containsKey(Excel.AppendNamespace(abstractTestCaseName,
//									this.nameSpace));
//						tempActntestcase.breakpoint=true;
//						tempActntestcase.breakpoints=Controller.breakpoints.get(Excel.AppendNamespace(abstractTestCaseName,
//								this.nameSpace));
//					}
					tempActntestcase.setArguments(tempList);
					tempActntestcase.setCallingtTestCaseSTACK(this.stackTrace);
					tempActntestcase.setParentTestCaseID(this.parentTestCaseID);
					tempActntestcase.parent=this.parent;
					tempActntestcase.actions=tempMol.actions;
					if (this instanceof Action) {
						try {
							Action act = (Action) this;
							Integer.parseInt(act.step);
							tempActntestcase.isConcurrentMoleculeCall = true;
						} catch (NumberFormatException ne) {
							// Do nothing
						}
					}
				//	System.out.println("isConcurrentMoleculeCall"+tempActntestcase.isConcurrentMoleculeCall);
				//	System.out.println("concurrentExecutionOnExpansion"+tempActntestcase.concurrentExecutionOnExpansion);
					Action a = tempActntestcase.actions.get(0);
					if (a.name.startsWith("#define")) {
						if (a.arguments.size() != tempList.size()) {
							Spark
							.message("Exception:No of arguments Mismatched.The molecule "
									+ tempActntestcase.testCaseID
									+ " takes "
									+ a.arguments.size()
									+ " arguments\nNumber of arguments passed is "
									+ tempList.size());
							throw new Exception(
									"No of arguments Mismatched.The molecule "
											+ tempActntestcase.testCaseID
											+ " takes "
											+ a.arguments.size()
											+ " arguments\nNumber of arguments passed is "
											+ tempList.size());
						}
					}
					boolean silentExecOn=false;
					try{
						if(this.property!=null &&this.property.toLowerCase().contains("silent")){
							silentExecOn=true;
							Spark.message("Running Molecule : "
									+ tempActntestcase.stackTrace+" with silent mode on");
							Spark.silentMolExecution=true;
							
						}
						tempActntestcase.run();	
						if(silentExecOn){
							silentExecOn=false;
							Spark.silentMolExecution=false;
							Spark.message("Successfully Executed Molecule : "
									+ tempActntestcase.stackTrace+" with silent mode on");
						}
					}finally{
						if(silentExecOn){
							Spark.silentMolExecution=false;
						}
					}
					
					exceptionOccured = false;
					if (this.isNegative) {
						throw new Exception(
								String.format("\n\nException: The test step  passed while property is set to negative."));
					}

					Log.Debug(String
							.format("Action/RunAction: Successfully executed  RunAbstractTestCase for Abstract TestCase ID as : %s ",
									abstractTestCaseName));
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
				Log.Debug("GTuple/run : Running Command " + this.name
						+ " for TestCase ID as : " + testCaseID);

				AtomHandler ah = new AtomHandler();

				ah.handle(this, threadID);
				exceptionOccured = false;
				if (this.isNegative) {
					throw new Exception(
							String.format("\n\nException: The test step  passed while property is set to negative"));
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			if (this.isNegative && exceptionOccured) {
				ContextVar
				.setContextVar(
						"ZUG_EXCEPTION",
						Argument.NormalizeVariable(
								String.format(
										"\n\nException  %s (%s:%s).\n\t Message: %s",
										this.name, this.sheetName,
										this.lineNumber, e.getMessage()),
										threadID,this));
			
				if (!StringUtils
						.isBlank(TestSuite.errorMessageDuringTestCaseExecution
								.get(this.parentTestCaseID))) {
					TestSuite.errorMessageDuringTestCaseExecution
					.put(this.parentTestCaseID,StringUtils.EMPTY);
				}
			}else if(this.isIgnore  && exceptionOccured){
				Spark.message(String.format(
						"\n[%s] Exception in "+this.getClass().getSimpleName()+" %s (%s:%s).\n\t Message: %s",
						this.stackTrace,this.name, this.sheetName,
						(this.lineNumber+1), e.getMessage()));
				if (!StringUtils
						.isBlank(TestSuite.errorMessageDuringTestCaseExecution
								.get(this.parentTestCaseID))) {
					TestSuite.errorMessageDuringTestCaseExecution
					.put(this.parentTestCaseID,StringUtils.EMPTY);
				}
			} else {
				throw e;
			}
		}
	}
}
