package com.automature.spark.reporter;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.NavigableMap;
import java.util.Set;

import javafx.application.Platform;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.automature.ZermattClient;
import com.automature.davos.exceptions.DavosExecutionException;
import com.automature.spacetimeapiclient.SpacetimeClient;
import com.automature.spark.engine.ContextVar;
import com.automature.spark.engine.ExecutedTestCase;
import com.automature.spark.engine.Spark;
import com.automature.spark.exceptions.ReportingException;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

public class SpacetimeReporter extends Reporter implements Retriever {
	private static ZermattClient client;
	public static SpacetimeReporter reporter;
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
	String buildName=StringUtils.EMPTY;
	private String TOPOSET;
	private String testCycleDesc;
	public static ArrayList<String> executedTestCases=new ArrayList<String>(); 
	public SpacetimeReporter(Hashtable ht) {
		this.dBHostName=(String)ht.get("dbhostname");
		this.dbUserName=(String)ht.get("dbusername");
		this.dbUserPassword=(String)ht.get("dbuserpassword");
		
		if(!Spark.guiFlag)
		try {
			if(!connect())
				printMessageAndExit("connection to remote machine could not be established using spacetime");
		} catch (Throwable e2) {}
		
		this.testSuiteId=(String)ht.get("testsuiteid");
		this.testSuiteName=(String)ht.get("testsuitename");
		this.testSuiteRole=(String)ht.get("testsuiterole");
		if(Spark.getController()!=null && Spark.guiFlag)
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
		else
		{
			if(!ht.get("testplan").toString().isEmpty()){
			try {
				updateMachineIp();
			} catch (ReportingException e2) {
			}
			String testplan=ht.get("testplan").toString();
			if(ht.get("testplan").toString().contains("(") && ht.get("testplan").toString().contains(")"))
			{
				String args[]=StringUtils.substringsBetween(ht.get("testplan").toString(), "(", ")");
				for (int i = 0; i < args.length; i++) {
					if(args[i].contains(":"))
					{
						testplan=testplan.replace("("+args[i]+")", args[i].replace(":", "$#$$$"));
					}
				}
			}
			String tpargs[]=testplan.split(":");
			for (int i = 0; i < tpargs.length; i++) {
				if(tpargs[i].contains("$#$$$"))
					tpargs[i]=tpargs[i].replace("$#$$$", ":");
				if(tpargs[i].startsWith("(") && tpargs[i].endsWith(")"))
					tpargs[i]=StringUtils.substringBetween(tpargs[i], "(", ")");
			}
			if(tpargs.length != 3)
			{
				printMessageAndExit("Invalid testplan path");
			}
			
			//getting product id from name
			this.productId=tpargs[0];

			try {
				if(client==null)
				connect();
			} catch (Throwable e) {}
			try {
				ArrayList<String> al=client.getProductList();
				for (int i = 0; i < al.size(); i++) {
					if(al.get(i).toLowerCase().startsWith(productId.toLowerCase()+" ("))
						productId=StringUtils.substringBetween(al.get(i).toLowerCase(), " (", ")");
				}
				if(!NumberUtils.isNumber(productId))
				{
				printMessageAndExit("The specified product does not exist.");
				}
				
			} catch (Exception e) {}

			//getting testplan id from name
			this.testPlanId=tpargs[1];

			try {
				if(client==null)
				connect();
			} catch (Throwable e) {}
			try {
				ArrayList<String> al=client.getTestPlanListForProduct(productId,InetAddress.getLocalHost().getHostAddress());
				for (int i = 0; i < al.size(); i++) {
					if(al.get(i).toLowerCase().startsWith(testPlanId.toLowerCase()+" ("))
						testPlanId=StringUtils.substringBetween(al.get(i).toLowerCase(), " (", ")");
				}
				if(!NumberUtils.isNumber(testPlanId))
				{
				printMessageAndExit("Either the specified testplan not exist or the machine is not associated with the topology set for this testplan");
				}
				
			} catch (Exception e) {}

			//getting testcycle id from name
			this.testCycleId=tpargs[2];
				
			try {
				if(client==null)
				connect();
			} catch (Throwable e) {}
			try {

				String testPlanName=null;
				ArrayList<String> al=null;
				if(testPlanName==null)
				{
				al=(client.getTestPlanListForProduct(productId,InetAddress.getLocalHost().getHostAddress()));
				for (int i = 0; i < al.size(); i++) {
					if(al.get(i).contains("("+testPlanId+")"))
					{
						testPlanName=al.get(i);
					}
				}
				}

				al=client.getTestCycleListForProduct(productId, testPlanName,InetAddress.getLocalHost().getHostAddress());
				
				for (int i = 0; i < al.size(); i++) {
					if(al.get(i).toLowerCase().startsWith(testCycleId.toLowerCase()+" ("))
						testCycleId=StringUtils.substringBetween(al.get(i).toLowerCase(), " (", ")");
				}
				if(!NumberUtils.isNumber(testCycleId))
				{
				this.testCycleDesc=	this.testCycleId;
				this.testCycleId=null;
				}
				
			} catch (Exception e) {}
			
			//getting topology set id from name
			this.topologySetId=(String)ht.get("topologySet".toLowerCase());
			if(testCycleId!=null)
			{
			try {
				if(client==null)
				connect();
			} catch (Throwable e) {}
			try {
				ArrayList<String> al=client.getTestCycleTopologySets(testCycleId,InetAddress.getLocalHost().getHostAddress());
				for (int i = 0; i < al.size(); i++) {
					if(al.get(i).toLowerCase().startsWith(topologySetId.toLowerCase()+" ("))
						topologySetId=StringUtils.substringBetween(al.get(i).toLowerCase(), " (", ")");
				}
				if(!NumberUtils.isNumber(topologySetId))
				{
				printMessageAndExit("The specified topologyset does not exist.");
				}
				
			} catch (Exception e) {}
			}
			else
			{
				try {
					ArrayList<String> al=client.getTopoSetsByTestPlanId(testPlanId);
					for (int i = 0; i < al.size(); i++) {
						if(al.get(i).toLowerCase().startsWith(topologySetId.toLowerCase()+" ("))
						{
							topologySetId=StringUtils.substringBetween(al.get(i).toLowerCase(), " (", ")");
						}
					}
					if(!NumberUtils.isNumber(topologySetId))
					{
					printMessageAndExit("The specified topologyset does not exist.");
					}
				} catch (Exception e) {}
			}
//			if(testCycleId!=null)
			try {
				buildName=(String)ht.get("buildname");
				ArrayList<String> al=(client.getBuildTagForTestCycleAndTopologyset(topologySetId, testCycleId));
				if(al.size()==1 && StringUtils.isEmpty(buildName))
				this.buildId=StringUtils.substringBetween(al.get(0)," (",")");
				else if(al.size()==1 && !StringUtils.isEmpty(buildName))
				this.buildId=null;
				else
				{
					if(StringUtils.isEmpty(buildName))
						printMessageAndExit("More than one build tag exists for given testcycle and topologyset.\nPlease provide -buildname option");
					for(String s:al)
					{
						if(s.toLowerCase().startsWith(buildName.toLowerCase()+" ("))
							this.buildId=StringUtils.substringBetween(s," (",")");
					}
				}
			} catch (Exception e1) {
				if(StringUtils.isEmpty((String)ht.get("buildname")))
					printMessageAndExit("Missing required value : buildname for new testcycle"
							+ "\nUse -help/-h for Usage information");
					else
					{
					try {
						buildName=(String)ht.get("buildname");
						ArrayList<String> al=client.getBuildTagForTopologyset(topologySetId);
						for(String s:al){
						if(s.toLowerCase().startsWith(buildName.toLowerCase()+" ("))	
							buildId=StringUtils.substringBetween(s, " (", ")");
						}
					} catch (Exception e) {e.printStackTrace();}
					if(StringUtils.isEmpty(buildId))
					buildId=null;
					}
			}
//			else
//			{
//				if(StringUtils.isEmpty((String)ht.get("buildname")))
//				printMessageAndExit("Missing required value : buildname for new testcycle"
//						+ "\nUse -help/-h for Usage information");
//				else
//				{
//				buildId=null;
//				buildName=(String)ht.get("buildname");
//				}
//			}
//			if(!NumberUtils.isNumber(buildId))
//			{
//			try {
//				if(client==null)
//				connect();
//			} catch (Throwable e) {}
//			try {
//				ArrayList<String> al=client.getBuildTagForTestCycleAndTopologyset(topologySetId,testCycleId);
//				for (int i = 0; i < al.size(); i++) {
//					if(al.get(i).toLowerCase().startsWith(buildId.toLowerCase()+" ("))
//						buildId=StringUtils.substringBetween(al.get(i).toLowerCase(), " (", ")");
//				}
//				if(!NumberUtils.isNumber(buildId))
//				{
//				System.err.println("The specified buildid does not exist.\n\n");
//				System.err.println("Exiting ZUG");
//				System.exit(0);
//				}
//				
//			} catch (Exception e) {}
//			}
		}
			else
			{

				testPlanId=ht.get("testplanid").toString();
				testCycleId=ht.get("testcycleid").toString();
				topologySetId=ht.get("topologysetid").toString();
				if(topologySetId.isEmpty())
				{
					try {
						topologySetId=client.getTopoSetsByTestPlanId(testPlanId).get(0);
					} catch (Exception e) {
						printMessageAndExit("Error while getting topologySetId for testPlanId "+testPlanId);
					}
				}
				if(testPlanId.isEmpty() || testCycleId.isEmpty() || topologySetId.isEmpty())
				{
					printMessageAndExit("Command Line switch testPlanId with testCycleId and topologySetId is mandatory");
				}
				if(!ht.get("buildid").toString().isEmpty())
					buildId=ht.get("buildid").toString();
				else
				try {
					ArrayList<String> buildIds=client.getBuildTagForTestCycleAndTopologyset(topologySetId, testCycleId);
					if(buildIds.size()>0)
					buildId=StringUtils.substringBetween(buildIds.get(0), " (", ")");
					else
					{
						System.err.println("No buildTag exists for testcycleId : "+testCycleId+" and topologySetId : "+topologySetId);
						System.err.println("Creating new buildTag");	
						buildName="Build : "+new Date();
						buildId=client.buildtag_write(testPlanId, buildName);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					System.err.println("Error while getting buildId for testcycleId : "+testCycleId+" and topologySetId : "+topologySetId+" : "+e.getMessage());
//					System.err.println("Creating new buildTag");
//					try {
//						buildName="Build : "+new Date();
//						buildId=client.buildtag_write(testPlanId, buildName);
//					} catch (DavosExecutionException | InterruptedException e1) {
//						// TODO Auto-generated catch block
						printMessageAndExit("Error while creating buildId for testcycleId "+testCycleId+" and "+topologySetId+" : "+e.getMessage());
//					}
				}
				if(buildId.isEmpty())
					printMessageAndExit("Error while getting buildId for testcycleId "+testCycleId+" and  and topologySetId : "+topologySetId);
			}
		}
		
	}
	private void printMessageAndExit(String msg) {
		System.err.println("\n\n"+msg+"\n\n"+"Exiting Zug");
		
		System.exit(0);
	}
	@Override
	public boolean connect() throws ReportingException, Throwable {
		Log.Debug("Controller/ConnectToSpacetime : Start of Function ");
		try{
		client=new SpacetimeClient(dBHostName,dbUserName,dbUserPassword);
		sessionid=(client.getSessionId());
		Spark.sessionid=sessionid;
		reporter=this;
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
		try{
			setContextVar("ZUG_TESTPLANID",testPlanId);
			setContextVar("ZUG_TOPOLOGYSETID",topologySetId);
			setContextVar("ZUG_DBSESSIONID",sessionid);
			setContextVar("ZUG_SESSIONID", sessionid);
			//setContextVar("ZUG_TCYCLENAME",davosclient.getTestCycleDescriptionByID(testCycleId));
			//setContextVar("ZUG_TCYCLENAME","TC_" + Utility.dateAsString());
			if(testCycleId!=null)
			{
			setContextVar("ZUG_TCYCID", testCycleId);
			setContextVar("ZUG_TESTCYCLEID",testCycleId);
			}
			setContextVar("ZUG_TOPOSET", "" + topologySetId);
			setContextVar("ZUG_DBHOST", "" + dBHostName);
		}catch(Exception e){
			Log.Error("Error while Setting Context Vars : " + e.getMessage());
			throw new Throwable("Error while Setting Context Vars :" + e.getMessage());
		}
		if(!Spark.guiFlag)
		{
			try{
			String testPlanName=null;
			ArrayList<String> al=null;
			int x=0;
			if(testCycleId!=null)
			al=client.getTestCycleTopologySets(testCycleId,InetAddress.getLocalHost().getHostAddress());
			else
			al=client.getTopoSetsByTestPlanId(testPlanId);
			
			for (int i = 0; i < al.size(); i++) {
				if(al.get(i).contains("("+topologySetId+")"))
				{
					x++;
				}
			}
			if(x==0)
			{
				System.out.println("\nInvalid topologyset for testplan\n");
				return false;
			}
			if(buildId!=null)
			{
			if(testCycleId!=null)
			al=client.getBuildTagForTestCycleAndTopologyset(topologySetId,testCycleId);
			else
			al=client.getBuildTagForTopologyset(topologySetId);	
			x=0;
			for (int i = 0; i < al.size(); i++) {
				if(al.get(i).contains("("+buildId+")"))
				{
					x++;
				}
			}
			if(x==0)
			{
				buildId=null;
				return true;
			}
			}
			}catch(Exception e){
				return false;
			}
		
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
	public void testCycleClearTestCases(String pid,String tpsid,String tcid,String bldid,String tsname) throws InterruptedException,
	ReportingException {

		try {
		client.testCycleClearTcs(pid,tpsid,tcid,bldid,tsname);
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
			if(Spark.guiFlag)
			buildId=client.buildtag_write(testPlanId, ZugguiController.controller.getBuildTagDesc());
			else
			buildId=client.buildtag_write(testPlanId,buildName);
			
			if(Spark.guiFlag)
			Platform.runLater(new Runnable() {
				public void run() {
					ZugguiController.controller.getBuildTag().setText(ZugguiController.controller.getBuildTag().getText()+" ("+buildId+")");
				}
			});
		}
		Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : Creating testcycle if not exists");
		if(Spark.guiFlag)
		testCycleId=client.testCycle_write(testPlanId, ZugguiController.controller.getTestCycleDesc(), "", "", "0", "0", buildId);
		else
			testCycleId=client.testCycle_write(testPlanId,this.testCycleDesc, "", "", "0", "0", buildId);	
		
		if(testCycleId!=null)
		{
		setContextVar("ZUG_TCYCID", testCycleId);
		setContextVar("ZUG_TESTCYCLEID",testCycleId);
		}
		
		if(Spark.guiFlag)
		Platform.runLater(new Runnable() {
			public void run() {
				ZugguiController.controller.getTestCycle().setText(ZugguiController.controller.getTestCycle().getText()+" ("+testCycleId+")");
			}
		});
	}
	else if(testCycleId!=null && buildId==null)
	{
		Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : Creating build tag if not exists");
		if(Spark.guiFlag)
		buildId=client.buildtag_write(testPlanId, ZugguiController.controller.getBuildTagDesc());
		else
		buildId=client.buildtag_write(testPlanId,buildName);
		
		if(Spark.guiFlag)
		Platform.runLater(new Runnable() {
			public void run() {
				ZugguiController.controller.getBuildTag().setText(ZugguiController.controller.getBuildTag().getText()+" ("+buildId+")");
			}
		});
	}
	Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : Start of the Function");
	etc.testCaseCompletetionTime=new Date();
	String testexecutiondetailid=client.testExecutionDetails_write(etc.testCaseID, etc.testcasedescription, testCycleId, etc.testCaseStatus, buildId, String.valueOf(etc.timeToExecute), "", etc.testCaseCompletetionTime.toString(), testSuiteName, topologySetId, testSuiteRole, etc.testCaseExecutionComments);//(executedTestCaseData.get(s).testCaseID, executedTestCaseData.get(s).testcasedescription, testCycleId, executedTestCaseData.get(s).testCaseStatus, buildId, String.valueOf(executedTestCaseData.get(s).timeToExecute), "", new SimpleDateFormat("yyyy-MM-dd").format(executedTestCaseData.get(s).testCaseCompletetionTime), testSuiteName, topologySetId, testSuiteRole , executedTestCaseData.get(s).testCaseExecutionComments);
	if(!executedTestCases.contains(testexecutiondetailid))
	executedTestCases.add(testexecutiondetailid);
	
		setContextVar("ZUG_TCID", etc.testCaseID);

		setContextVar("ZUG_TSEXDID", testexecutiondetailid);	
		
//		if(ContextVar.getContextVar("ZUG_TSEXDIDS")==null)
//		setContextVar("ZUG_TSEXDIDS", testexecutiondetailid);	
//		else
//		setContextVar("ZUG_TSEXDIDS", ContextVar.getContextVar("ZUG_TSEXDIDS")+":"+testexecutiondetailid);
		
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
					+ dBHostName + "/" + " through Spacetime Web Service.");// TODO

			Spark.message("\nTestCase ID \t\t Status \t\t Time Taken(In mili-seconds) \t\t Comments\n ");
//			Set<String> TestCaseKey = executedTestCaseData.keySet();
			for (String s : Spark.executedTestCases) {
				
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
	public ArrayList<String> getTestPlanList(String pid,String ip) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getTestPlanListForProduct(pid,ip);
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).equals(""))
					list.remove(i);
			}
			if(list.size()==0)
				System.err.println("No testplan exists for the selected product.");
			return list;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	@Override
	public ArrayList<String> getTestCycleList(String pid,String testPlanName,String ip) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getTestCycleListForProduct(pid, testPlanName,ip);
			if(list.size()==0)
				System.err.println("No testcycle exists for the selected testplan.");
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
			ArrayList<String> list=client.getTestCycleTopologySets(tcid,InetAddress.getLocalHost().getHostAddress());
			if(list.size()==0)
				System.err.println("No topologyset is associated with selected testcycle.");
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	@Override
	public ArrayList<String> getBuildTagsForTestCycleAndTopologyset(String pid,String topoid,String tcid) {
		// TODO Auto-generated method stub
		try {
			ArrayList<String> list=client.getBuildTagForTestCycleAndTopologyset(topoid, tcid);
			if(list.size()==0)
				list=getBuildsByProductId(pid);
			if(list.size()==0)
				System.err.println("No buildtag is associated with selected testcycle.");
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
	@Override
	public void testCycleClearTestCases(String testSuitName) throws ReportingException {
		try {
			testCycleClearTestCases(productId,topologySetId,testCycleId,buildId, testSuitName);
		} catch (Exception e) {
			throw new ReportingException(e.getMessage());
		}
	}
	@Override
	public void updateMachineIp() throws ReportingException {
		try {
			client.updateMachineIp(InetAddress.getLocalHost().getHostName(), InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			throw new ReportingException(e.getMessage());
		}
	}
	@Override
	public void setContextVar(String key,String value) throws Exception {
			ContextVar.setContextVar(key,value);
	}
}
