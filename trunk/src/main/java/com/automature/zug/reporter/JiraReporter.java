package com.automature.zug.reporter;



import com.automature.jira.exceptions.JiraExecutionException;
import com.automature.zug.engine.Controller;
import com.automature.zug.engine.ExecutedTestCase;
import com.automature.zug.exceptions.ReportingException;
import com.automature.zug.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import com.automature.jira.engine.JiraClient;
/**
 * Created with IntelliJ IDEA.
 * User: Automature
 * Date: 1/6/14
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class JiraReporter  implements Reporter {

	public class IssueDetails{
		String issue_id;
		String execution_id;

		public IssueDetails(String issue, String execution){
			this.issue_id = issue;
			this.execution_id = execution;
		}
	}

	static HashMap<String, IssueDetails> issues_reported;

	String hostName=null;
	String userName=null;
	String userPassword=null;

	String testplandetails=null;

	String projectName=null;
	String projectId="-1";

	String versionName=null;
	String versionId="-1";


	String testcycleName=null;
	String testcycleId=null;

	String testsuiteName=null;
	String buildName=null;

	String testId=null;
	JiraClient api;
	
	protected final static String PASS="1";
	protected final static String FAIL="2";

	public JiraReporter(Hashtable ht) throws Exception{
		Controller.message("\n\nConnecting to Jira" + hostName);
		this.hostName = (String)ht.get("dbhostname");
		this.userName = (String)ht.get("dbusername");
		this.userPassword =(String)ht.get("dbuserpassword");

		this.testplandetails=(String)ht.get("testplanpath");

		this.testsuiteName=(String)ht.get("testsuitename");

		String[] objects = this.testplandetails.split(":");
		switch(objects.length){

		case 2:
			this.projectName = objects[0];
			this.versionName = objects[1];
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
			this.testcycleName = dateFormat.format(date)+"-"+timeFormat.format(calendar.getTime());        	
			break;

		case 3:
			this.projectName = objects[0];
			this.versionName = objects[1];
			this.testcycleName = objects[2];
			break;

		default:
			Log.Error("JiraReporter/JiraReporter() : Test plan path not defined property to report to jira. Path : "+testplandetails);
			throw new ReportingException("Test plan path not defined property to report to jira. Please refer to the latest documentation.");
		}

		if(ht.get("buildtag")== null || ht.get("buildtag").toString().isEmpty()){
			this.buildName = "build not mentioned.";
		}else{
			this.buildName = (String)ht.get("buildtag");
		}

		issues_reported = new HashMap<String, IssueDetails>();
	}

	@Override
	public boolean connect() throws ReportingException, Throwable {
		Log.Debug("JiraReporter/connect() : Start of Function ");
		Controller.message("\n\nConnecting to Jira" + hostName);
		boolean connect_flag = false;
		try {
			api = new JiraClient(hostName,userName,userPassword);
			connect_flag = true;
		}catch(JiraExecutionException e){
			Log.Error("JiraReporter/connect() : Exception while Connecting to Jira "
					+ " of host "
					+ this.hostName
					+ " with user "+ this.userName + " and password "
					+ userPassword + ". The Exception is " +e.getMessage());            	
		}
		return connect_flag;
	}

	@Override
	public boolean ValidateDatabaseEntries() throws InterruptedException, Exception, Throwable {
		Log.Debug("JiraReporter/ValidateDatabaseEntries() : Validating database entries.");
		try{
			this.projectId = api.getProductId(projectName);
			this.versionId = api.getVersionId(projectName, versionName);
			this.testId = api.createMeta(projectId);
			this.testcycleId = api.createTestCycle(testcycleName, buildName, projectId, versionId);
		}catch(JiraExecutionException e){
			Log.Error("JiraReporter/ValidateDatabaseEntries() : Error while validating database entries. The Exception is "+e.getMessage());   
			return false;
		}
		return true;  //To change body of implemented methods use File | Settings | File Templates.
	}


	@Override
	public void SaveTestCaseResultEveryTime(ExecutedTestCase etc) throws ReportingException,Exception {
		Log.Debug("JiraReporter/SaveTestCaseResultEveryTime() : Saving test case result for "+etc.testCaseID);
		String issueId = null, executionId = null, statusCode = null; 
		IssueDetails obj;
		if (issues_reported.containsKey(etc.testCaseID)){
			obj = issues_reported.get(etc.testCaseID);
			issueId = obj.issue_id;
			executionId = obj.execution_id;
		}else{
			issueId = getIssueId(etc);
		}
		if(executionId == null){
			Log.Debug("JiraReporter/SaveTestCaseResultEveryTime() : Creating new execution record for "+etc.testCaseID);
			try{
				executionId = api.testExecution_write(issueId, testcycleId, projectId, versionId);
			}catch(JiraExecutionException f){
				Log.Error("JiraReporter/SaveTestCaseResultEveryTime() : Error while creating test execution record for "+etc.testCaseID+". \n"+f.getMessage());
				throw new ReportingException("Error while creating test execution record for "+etc.testCaseID+". \n"+f.getMessage());
			}
			Log.Debug("JiraReporter/SaveTestCaseResultEveryTime() : New execution record created successfully for "+etc.testCaseID);
		}
		if (issues_reported.containsKey(etc.testCaseID)){
			if( etc.testCaseStatus.equalsIgnoreCase("pass")){
				statusCode = PASS;
			}else if( etc.testCaseStatus.equalsIgnoreCase("fail")){
				statusCode = FAIL;
			}else
			{
				Log.Error("JiraReporter/SaveTestCaseResultEveryTime() : Unrecognised outcome for issue "+etc.testCaseID+". Outcome = "+etc.testCaseStatus);
				throw new ReportingException("Unrecognised outcome for issue "+etc.testCaseID+". Outcome = "+etc.testCaseStatus);
			}
			Log.Debug("JiraReporter/SaveTestCaseResultEveryTime() : Setting outcome for issue "+etc.testCaseID +" in execution id = "+executionId);
			try{
				api.testExecution_setOutcome(executionId, statusCode, etc.testCaseExecutionComments);
			}catch(JiraExecutionException f){
				Log.Error("JiraReporter/SaveTestCaseResultEveryTime() : Failure while setting test case result for issue "+etc.testCaseID+" in execution id = "+executionId+" \n"+f.getMessage());
				throw new ReportingException("Failure while setting test case result for issue "+etc.testCaseID+" in execution id = "+executionId+" \n"+f.getMessage());    			
			}
			Log.Debug("JiraReporter/SaveTestCaseResultEveryTime() : Outcome set to "+statusCode+" for issue "+etc.testCaseID +" in execution id = "+executionId);
			issues_reported.remove(etc.testCaseID);
		}else{
			issues_reported.put(etc.testCaseID, new IssueDetails(issueId, executionId));
		}
	}


	private String getIssueId(ExecutedTestCase etc)throws Exception{
		Log.Debug("JiraReporter/getIssueId() : Create new Issue or return the existing issue for Issue Summary "+etc.testCaseID);
		String mod_summary = this.testsuiteName+":"+etc.testCaseID;  
		etc.testCaseID=mod_summary;
		String issueId = null;
		try{
			Log.Debug("JiraReporter/getIssueId() : Searching for existing issue with summary "+mod_summary);
			issueId = api.getIssueId(projectId, mod_summary);
		}catch(JiraExecutionException e){
			Log.Debug("JiraReporter/getIssueId() : "+mod_summary+" issue does not exists!");
			Log.Debug("JiraReporter/getIssueId() : Creating new issue with summary "+mod_summary);
			try{
				issueId = api.createIssue(projectId, versionId, mod_summary, etc.testcasedescription);
			}catch(JiraExecutionException f){
				Log.Error("JiraReporter/getIssueId() : Error while creating new Issue : "+mod_summary+"\nException : "+f.getMessage());
				throw new ReportingException("Error while creating new Issue : "+mod_summary+"\nException : "+f.getMessage());
			}
		}
		Log.Debug("JiraReporter/getIssueId() : Issus Summary : "+mod_summary+", Issue Id :"+issueId);    	
		return issueId;
	}


	@Override
	public void saveTestCaseResults(Hashtable ht) throws ReportingException, Exception {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void heartBeat(String sessionid) throws InterruptedException, ReportingException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void archiveLog() throws ReportingException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void saveTestCaseVariables(HashMap<String, String> variablemap, String testcase_id, String testsuite_name) throws ReportingException, InterruptedException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void destroySession(String sessionId) throws ReportingException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setTestCycleTopologySetValues(String env_list) throws ReportingException {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
