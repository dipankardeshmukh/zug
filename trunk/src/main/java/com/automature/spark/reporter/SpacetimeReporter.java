package com.automature.spark.reporter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.NavigableMap;
import java.util.Set;

import javafx.application.Platform;

import org.apache.commons.lang.StringUtils;

import com.automature.ZermattClient;
import com.automature.spacetimeapiclient.SpacetimeClient;
import com.automature.spark.engine.ExecutedTestCase;
import com.automature.spark.engine.Spark;
import com.automature.spark.exceptions.ReportingException;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.util.Log;

public class SpacetimeReporter extends Reporter implements Retriever {
	private static ZermattClient client;
	public static String sessionid=null;
	String dBHostName= StringUtils.EMPTY;
	String dbUserName = StringUtils.EMPTY;
	String dbUserPassword = StringUtils.EMPTY;
	String testSuiteId=StringUtils.EMPTY;
	String testSuiteName=StringUtils.EMPTY;
	String testSuiteRole=StringUtils.EMPTY;
	String productId =StringUtils.EMPTY;
	String testPlanId = StringUtils.EMPTY;
	String testCycleId = StringUtils.EMPTY;
	String topologySetId = StringUtils.EMPTY;
	String buildId=StringUtils.EMPTY;
	private String TOPOSET;
	public static ArrayList<String> executedTestCases=new ArrayList<String>(); 
	public SpacetimeReporter(Hashtable ht) {
		this.dBHostName=(String)ht.get("dbhostname");
		this.dbUserName=(String)ht.get("dbusername");
		this.dbUserPassword=(String)ht.get("dbuserpassword");
		this.testSuiteId=(String)ht.get("testsuiteid");
		this.testSuiteName=(String)ht.get("testsuitename");
		this.testSuiteRole=(String)ht.get("testsuiterole");
		if(Spark.getController()!=null)
		{
			this.productId=Spark.getController().getProductId();
			this.testPlanId=Spark.getController().getTestPlanId();
			
			try{
				this.testCycleId=Spark.getController().getTestCycleId();
			}catch(StringIndexOutOfBoundsException ex){
			this.testCycleId=null;	
			}
			this.topologySetId=Spark.getController().getTopologySetId();
			
			try{
				this.buildId=Spark.getController().getBuildId();
			}catch(StringIndexOutOfBoundsException ex){
				this.buildId=null;	
			}
		}
	}
	@Override
	public boolean connect() throws ReportingException, Throwable {
		Log.Debug("Controller/ConnectToSpacetime : Start of Function ");
		try{
		client=new SpacetimeClient(dBHostName,dbUserName,dbUserPassword);
		sessionid=(client.getSessionId());
		Spark.sessionid=sessionid;
		return true;
		}catch(Exception ex)
		{
			return false;
		}
	}

	@Override
	public boolean ValidateDatabaseEntries() throws InterruptedException,
			Exception, Throwable {
		Log.Debug("Controller/ValidateDatabaseEntries : Start of function");
		try{
			
			heartBeat(sessionid);
		}
		catch(ReportingException re){
			System.err.println("\n\t"+re.getMessage()+"\n");
			return false;
		}
//		try{
//		if(StringUtils.isEmpty(ZugguiController.controller.getProduct().getText()) || 
//				StringUtils.isEmpty(ZugguiController.controller.getTestPlan().getText())||
//				StringUtils.isEmpty(ZugguiController.controller.getTestCycle().getText())||
//				StringUtils.isEmpty(ZugguiController.controller.getTopoSet().getText())||
//				StringUtils.isEmpty(ZugguiController.controller.getBuildTag().getText())
//				)
//		{
//			System.err.println("\n\tPlease check reporting configuration settings from Reporting pane and proceed\n");
//			
//			return false;
//		}
//		else
//		{
		return true;
//		}
//		}catch(Exception e){
//			e.printStackTrace();
//			System.err.println("\n\tPlease check reporting configuration settings from Reporting pane and proceed\n");
//			return false;
//		}
		
	}

	@Override
	public void heartBeat(String sessionid) throws InterruptedException,
			ReportingException {

		try {
			client.heartBeat(sessionid);
		} catch (Exception e) {
			testCycleCleanup(testCycleId,testSuiteName,productId);
		}
		
	}
	
	@Override
	public void testCycleCleanup(String tcid,String tsname,String pid) throws InterruptedException,
			ReportingException {
     try {
		connect();
	} catch (Throwable e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		try {
		client.testCycleCleanup(tcid,tsname,pid);
		} catch (Exception e) {
			throw new ReportingException(e.getMessage());
		}
	destroySession(sessionid);
	}
	
	@Override
	public void testCycleClearTestCases(String tcid,String tsname,String pid) throws InterruptedException,
	ReportingException {

		try {
		client.testCycleClearTcs(tcid,tsname,pid);
		} catch (Exception e) {
			throw new ReportingException(e.getMessage());
		}
	}
	
	@Override
	public void SaveTestCaseResultEveryTime(ExecutedTestCase etc)
			throws Exception, ReportingException {
	if(testCycleId==null)
	{
		//create new testcycle if not exists
		if(buildId==null)
		{
			Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : Creating build tag if not exists");
			buildId=client.buildtag_write(testPlanId, ZugguiController.controller.getBuildTagDesc());
			Platform.runLater(new Runnable() {
				public void run() {
					ZugguiController.controller.getBuildTag().setText(ZugguiController.controller.getBuildTag().getText()+" ("+buildId+")");
				}
			});
		}
		Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : Creating testcycle if not exists");
		testCycleId=client.testCycle_write(testPlanId, ZugguiController.controller.getTestCycleDesc(), "", "", "0", "0", buildId);
		
		Platform.runLater(new Runnable() {
			public void run() {
				ZugguiController.controller.getTestCycle().setText(ZugguiController.controller.getTestCycle().getText()+" ("+testCycleId+")");
			}
		});
	}
	Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : Start of the Function");
	etc.testCaseCompletetionTime=new Date();
	String resp=client.testExecutionDetails_write(etc.testCaseID, etc.testcasedescription, testCycleId, etc.testCaseStatus, buildId, String.valueOf(etc.timeToExecute), "", etc.testCaseCompletetionTime.toString(), testSuiteName, topologySetId, testSuiteRole, etc.testCaseExecutionComments);//(executedTestCaseData.get(s).testCaseID, executedTestCaseData.get(s).testcasedescription, testCycleId, executedTestCaseData.get(s).testCaseStatus, buildId, String.valueOf(executedTestCaseData.get(s).timeToExecute), "", new SimpleDateFormat("yyyy-MM-dd").format(executedTestCaseData.get(s).testCaseCompletetionTime), testSuiteName, topologySetId, testSuiteRole , executedTestCaseData.get(s).testCaseExecutionComments);
	if(!executedTestCases.contains(resp))
	executedTestCases.add(resp);
	Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : End of the Function");
	}

	@Override
	public void archiveLog() throws ReportingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveTestCaseVariables(HashMap<String, String> variablemap,
			String testcase_id, String testsuite_name)
			throws ReportingException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroySession(String sessionId) throws ReportingException {
		// TODO Auto-generated method stub
		try {
			client.destorySession(sessionId);
			sessionid=null;
			} catch (Exception e) {
				System.err.println(sessionid+"#############################"+sessionId);
				throw new ReportingException(e.getMessage());
			}
	}

	@Override
	public void setTestCycleTopologySetValues(String env_list)
			throws ReportingException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void saveTestCaseResults(NavigableMap<String, ExecutedTestCase> executedTestCaseData)
			throws  Exception {
		Log.Debug("Controller/SaveTestCaseResult : Start of the Function");
		
		Log.Debug("Controller/SaveTestCaseResult : Saving TestCycle ID "
				+ testCycleId + " and Test Plan ID " + testPlanId
				+ " of Test Plan " + testSuiteName);
		
		Spark.message("Saving Result for TestCycle ID " + testCycleId
				+ " and Test Plan ID " +testPlanId + " of Test Plan "
				+ testSuiteName);
		Spark.message("\n-----------------------------------------------------------------------------------------------------------------------");
		Spark.message("\n-----------------------------------------------------------------------------------------------------------------------");

		if (executedTestCaseData.size() > 0) {
	
			Log.Debug("Controller/SaveTestCaseResult : Number of TestCase Status to add is "
					+ executedTestCaseData.size());
			
			Spark.message("\nFollowing are the Details of the TestCases Result getting added to the "
					+ dBHostName + "/" + " through Davos Web Service.");// TODO

			Spark.message("\nTestCase ID \t\t Status \t\t Time Taken(In mili-seconds) \t\t Comments\n ");
			
			Set<String> TestCaseKey = executedTestCaseData.keySet();
			for (String s : TestCaseKey) {
				
				client.testExecutionDetails_write(executedTestCaseData.get(s).testCaseID, executedTestCaseData.get(s).testcasedescription, testCycleId, executedTestCaseData.get(s).testCaseStatus, buildId, String.valueOf(executedTestCaseData.get(s).timeToExecute), "", executedTestCaseData.get(s).testCaseCompletetionTime.toString(), testSuiteName, topologySetId, testSuiteRole , executedTestCaseData.get(s).testCaseExecutionComments);
				
				Spark.message("\n" + executedTestCaseData.get(s).testCaseID + "\t\t"
						+ executedTestCaseData.get(s).testCaseStatus+ "\t\t"
						+ executedTestCaseData.get(s).timeToExecute+ "\t\t"
						+ executedTestCaseData.get(s).testCaseExecutionComments);
			}
			Spark.message("\n-----------------------------------------------------------------------------------------------------------------------");
			Spark.message("\n-----------------------------------------------------------------------------------------------------------------------");

			Log.Debug("Controller/SaveTestCaseResult : End of the Function");
		}
	}
	@Override
	public ArrayList<String> getProductList() {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getProductList();
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public ArrayList<String> getTestPlanList(String pid) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getTestPlanListForProduct(pid);
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public ArrayList<String> getTestCycleList(String pid,String testPlanName) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getTestCycleListForProduct(pid, testPlanName);
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public ArrayList<String> getTestCycleTopologysetList(String tcid) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getTestCycleTopologySets(tcid);
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public ArrayList<String> getBuildTagsForTestCycle(String tcid) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getBuildTagForTestCycle(tcid);
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	@Override
	public ArrayList<String> getTopoSetsByTestPlanId(String tpid) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getTopoSetsByTestPlanId(tpid);
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public ArrayList<String> getBuildsByProductId(String pid) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getBuildsByPid(pid);
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
