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
											e.getMessage()), threadID,this));


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
	
	public String toString(){
		String act=this.testCaseID+"\t\t"+this.name+"\t"+this.arguments.toString();
		for(Verification v:this.verification){
			act=act+"\t"+v.name+"\t"+v.arguments.toString();
		}
		return act;
	}
}
