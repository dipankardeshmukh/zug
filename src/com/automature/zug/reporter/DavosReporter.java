package com.automature.zug.reporter;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.automature.davos.engine.DavosClient;
import com.automature.davos.exceptions.DavosExecutionException;
import com.automature.zug.businesslogics.TestCaseResult;
import com.automature.zug.engine.ContextVar;
import com.automature.zug.engine.Controller;
import com.automature.zug.engine.ExecutedTestCase;
import com.automature.zug.engine.SysEnv;
import com.automature.zug.engine.TestSuite;
import com.automature.zug.exceptions.ReportingException;
import com.automature.zug.util.Log;
import com.automature.zug.util.Utility;
import com.automature.zug.util.ZipUtility;

public class DavosReporter implements Reporter {

	static DavosClient davosclient = null;
	String dBHostName= StringUtils.EMPTY;
	String dBName = StringUtils.EMPTY;
	String dbUserName = StringUtils.EMPTY;
	String dbUserPassword = StringUtils.EMPTY;
	String sessionid = null;
	String topologySetId = StringUtils.EMPTY;
	String testPlanId = StringUtils.EMPTY;
	String testPlanPath = StringUtils.EMPTY;
	String testCycleId = StringUtils.EMPTY;
	String topologySetName = StringUtils.EMPTY;
	String buildTag = StringUtils.EMPTY;
	String buildId=StringUtils.EMPTY;
	String buildNo = StringUtils.EMPTY;
	String testSuiteId=StringUtils.EMPTY;
	String testSuiteName=StringUtils.EMPTY;
	String testSuiteRole=StringUtils.EMPTY;
	private String TOPOSET;

	public DavosReporter(Hashtable ht){
		dBName=(String)ht.get("dbname");
		this.dbUserName = (String)ht.get("dbusername");
		this.dbUserPassword = (String)ht.get("dbuserpassword");
		this.dBHostName=(String)ht.get("dbhostname");
		this.sessionid = (String)ht.get("sessionid");
		this.topologySetId = (String)ht.get("topologysetid");
		testPlanId = (String)ht.get("testplanid");
		testPlanPath = (String)ht.get("testplanpath");
		this.testCycleId = (String)ht.get("testcycleid");
		topologySetName =(String)ht.get( "topologysetname");
		buildTag =(String)ht.get("buildtag");
		buildId = (String)ht.get("buildid");
		buildNo =(String)ht.get("buildno");
		testSuiteId=(String)ht.get("testsuiteid");
		testSuiteName=(String)ht.get("testsuitename");
		testSuiteRole=(String)ht.get("testsuiterole");
	}



	/***
	 * Function will verify if the TestPlan ID, Topology ID and other related
	 * stuff specified is correct and exists in the Result Database or not.
	 */

	public boolean ValidateDatabaseEntries() throws InterruptedException ,Exception{
		Log.Debug("Controller/ValidateDatabaseEntries : Start of function");
		try {
			// message("The Seesion Id  "+sessionid);

			if (StringUtils.isNotEmpty(testPlanPath) && StringUtils.isBlank(testPlanId)) {
				// controller.message("THe testplan path is "+controller.TestPlanPath);
				testPlanId = getTestPlanId();
			}

			if (StringUtils.isNotEmpty(topologySetName)	&& StringUtils.isBlank(topologySetId)) {
				topologySetId = getTopologySetId();
			}

			// controller.message("The build tag "+controller.BuildTag );

			if (StringUtils.isNotEmpty(buildTag)) {
				buildNo = saveBuildTag();
			}

			davosclient.heartBeat(sessionid);

			try {
				ContextVar.setContextVar("ZUG_TESTPLANID",testPlanId);
				ContextVar.setContextVar("ZUG_TOPOLOGYSETID",topologySetId);
				if(StringUtils.isNotBlank(testPlanId)||StringUtils.isNotEmpty(testPlanId))
					davosclient.validate_Testplan(testPlanId);
				davosclient.validate_Topologyset(topologySetId);
				if (StringUtils.isNotBlank(testCycleId)	|| StringUtils.isNotEmpty(testCycleId)) {
					if(StringUtils.isNotBlank(testPlanId)||StringUtils.isNotEmpty(testPlanId))
						validateTestCycle(testPlanId,testCycleId);

				}
				else
				{
					//TODO validate testcycle without testplan
				}
			} catch (DavosExecutionException e) {
				Log.Error("Error in Validating Data: " + e.getMessage());
				System.exit(1);
			}

		} catch (DavosExecutionException e) {
			e.printStackTrace();
		}
		return true;
	}


	public boolean connect() throws ReportingException {
		Log.Debug("Controller/ConnectToDavos : Start of Function ");
		boolean connnect_flag = false;
		try {

			Controller.message("Connecting to DAVOS --> URL: " +dBHostName
					+ " User: " + dbUserName);
			davosclient = new DavosClient(dBHostName,
					dbUserName, dbUserPassword);
			sessionid = davosclient.getSessionId();
			ContextVar.setContextVar("ZUG_DBSESSIONID",sessionid);
			Log.Debug("Davos Connection Successful. Session ID: " + sessionid);
			// TODO create DavosClient instance global variable.dbHOst dbname
			// Session id for Davos connection
			ContextVar.setContextVar("ZUG_SESSIONID", sessionid);
			connnect_flag = true;
		} catch (DavosExecutionException dd) {

			Log.Error("Controller/ConnectToDavos : Exception while Connecting to Davos "
					+ dBName
					+ " of host "
					+ dBHostName
					+ " with user "
					+ dbUserName + ". The Exception is " + dd.getMessage());
			dd.printStackTrace();
			connnect_flag = false;
			System.exit(1);

		} catch (Exception ex) {
			Log.Error("Controller/ConnectToDavos : Exception while connecting to Davos "
					+ dBName
					+ " of host "
					+ dBHostName
					+ " with user "
					+ dbUserName + ". The Exception is " + ex.getMessage());
			Log.Debug("Controller/ConnectToDavos : End of Function. Function returns FALSE.");
			connnect_flag = false;
		}

		Log.Debug("Controller/ConnectToDavos : End of Function. Function returns TRUE.");

		return connnect_flag;
	}


	private String getTestPlanId()
			throws Exception, InterruptedException {
		String result = null;
		try {
			if (testPlanPath.contains(":")) {
				result = davosclient.testplanpath_write(testPlanPath);
				return result;
			} else {
				throw new DavosExecutionException(
						"Test plan name Must contain ':'");
			}
		} catch (DavosExecutionException ex) {
			Log.Error("Controller/getTestPlanId:DavosExecutionException "
					+ ex.getMessage());

			result = ex.getLocalizedMessage();
			System.exit(1);
			return result;
		}

	}

	/**
	 * gets the topologyset id from topologysetname
	 * 
	 * @param String
	 *            topologysetname return String the topologysetid
	 */
	private String getTopologySetId()
			throws Exception, InterruptedException {
		String result = null;
		try {
			if (topologySetName.length() > 0) {
				result = davosclient.topologyset_read(topologySetName);
				return result;
			} else {
				throw new DavosExecutionException("Topology Set Name is null");
			}
		} catch (DavosExecutionException x) {
			Log.Error("Controller/getTopologySetId:DavosExecutionException "
					+ x.getMessage());
			result = x.getMessage();
			System.exit(1);
			return result;

		}
	}


	/*
	 * Saves the build tag
	 * 
	 * @param Testplan id and the build tag name
	 */

	private String saveBuildTag()
			throws Exception, InterruptedException {
		Log.Debug("Controller/saveBuildTag: TestPlanid " + testPlanId
				+ " Build tag " + buildTag);
		try {
			return davosclient.buildtag_write(testPlanId, buildTag);
		} catch (DavosExecutionException ee) {
			Log.Error("Controller/saveBuildTag: Exception: " + ee.getMessage());
			System.exit(1);
			return ee.getMessage();
		}
	}



	private String validateTestCycle(String testplan, String testCycle)
			throws Exception, InterruptedException {
		String testcylepresent = null;

		try {

			testcylepresent = davosclient.validate_TestCycleByTestPlanID(
					testplan, testCycle);

		} catch (DavosExecutionException de) {
			Log.Error("Davos execution Exception " + de.getMessage());
			System.exit(1);
		} catch (Exception e) {
			Log.Error("Controller/validatetestcycleid:: Exception occured "
					+ e.getMessage());
		}

		return testcylepresent;
	}


	public void saveTestCaseResults(Hashtable executedTestCaseData) throws Exception {
		Log.Debug("Controller/SaveTestCaseResult : Start of the Function");

		// BusinessLayer.TestCycle testCycle = new BusinessLayer.TestCycle();
		String buildNumber =buildNo;

		Log.Debug("Controller/SaveTestCaseResult : Saving TestCycle ID "
				+ testCycleId + " and Test Plan ID " + testPlanId
				+ " of Test Plan " + testSuiteName);

		Controller.message("Saving Result for TestCycle ID " + testCycleId
				+ " and Test Plan ID " +testPlanId + " of Test Plan "
				+ testSuiteName);
		Controller.message("\n-----------------------------------------------------------------------------------------------------------------------");
		Controller.message("\n-----------------------------------------------------------------------------------------------------------------------");


		//
		// // need to change for test plan name
		Log.Debug("Controller/SaveTestCaseResult : TopologyName = "
				// + testCasesExecutedMachine.get_topologyId()
				+ " BuildNumber = "
				+ " will be saved.");

		if (executedTestCaseData.size() > 0) {
			ExecutedTestCase[] executedTestCase = new ExecutedTestCase[executedTestCaseData
			                                                           .size()];

			int count = 0;
			Set<String> TestCaseKey = executedTestCaseData.keySet();
			for (String s : TestCaseKey) {
				executedTestCase[count++] = (ExecutedTestCase) executedTestCaseData
						.get(s);
			}

			Log.Debug("Controller/SaveTestCaseResult : Number of TestCase Status to add is "
					+ executedTestCase.length);

			if (executedTestCase.length > 0) {
				Controller.message("\nFollowing are the Details of the TestCases Result getting added to the "
						+ dBHostName + "/" + " through Davos Web Service.");// TODO

				Controller.message("\nTestCase ID \t Status \t Time Taken(In mili-seconds) \t Comments\n ");
			}

			for (int i = 0; i < executedTestCase.length; i++) {
				TestCaseResult testCaseResult = new TestCaseResult();
				testCaseResult.set_testCaseId(executedTestCase[i].testCaseID);
				if (StringUtils.isNotBlank(testSuiteId)) {
					testCaseResult.set_testSuiteId(Integer.valueOf(testSuiteId)
							.intValue());
				}
				testCaseResult.set_testEngineerName("Automation");
				testCaseResult
				.set_executionDate(executedTestCase[i].testCaseCompletetionTime);
				testCaseResult
				.set_testExecution_Time(executedTestCase[i].timeToExecute);
				testCaseResult.set_status(executedTestCase[i].testCaseStatus);
				testCaseResult
				.set_comments(executedTestCase[i].testCaseExecutionComments);

				testCaseResult.set_buildNo(StringUtils.EMPTY);
				if (StringUtils.isNotBlank(buildNumber)) {
					testCaseResult.set_buildNo(buildNumber);
				}

				// Log.Debug("Controller/SaveTestCaseResult : TestCaseId = "+testCaseResult.get_testCaseId()+" TestSuiteId = "+testCaseResult.get_testSuiteName()+" Status = "+testCaseResult.get_status()+" and Comments = "+testCaseResult.get_comments()+" will be saved.");
				Log.Debug("Controller/SaveTestCaseResult : TestCaseIdentifier = "
						+ testCaseResult.get_testCaseId()
						+ " TestSuiteId = "
						+ testCaseResult.get_testSuiteId()
						+ " Status = "
						+ testCaseResult.get_status()
						+ " and Comments = "
						+ testCaseResult.get_comments() + " will be saved.");
				// topologyResultData.get_testCaseResultList().add(testCaseResult);
				try{
				if(StringUtils.isNotBlank(testPlanId)||StringUtils.isNotEmpty(testPlanId))
				{	Log.Debug("Controller/SaveTestCaseResult : Sending test data to Davos with testplanid="+testPlanId+" testcycledescription="+ContextVar.getContextVar("ZUG_TCYCLENAME")+" initializationtime="+Controller.initializationTime+" testexecutiontime="+testCaseResult.get_testExecution_Time());
				testCycleId = davosclient.testCycle_write(testPlanId,
						ContextVar.getContextVar("ZUG_TCYCLENAME"), "", "",
						new Integer(Controller.initializationTime).toString(),
						new Integer(testCaseResult.get_testExecution_Time())
				.toString());
				}
				else
				{
					Log.Debug("Controller/SaveTestCaseResult : Sending test data to Davos with testplanid="+testPlanId+" testcycledescription="+ContextVar.getContextVar("ZUG_TCYCLENAME")+" initializationtime="+Controller.initializationTime+" testexecutiontime="+testCaseResult.get_testExecution_Time());
					testCycleId=davosclient.testCycle_write(testPlanId,
							ContextVar.getContextVar("ZUG_TCYCLENAME"), "", "",
							new Integer(Controller.initializationTime).toString(),
							new Integer(testCaseResult.get_testExecution_Time())
					.toString());
				}
				}catch(DavosExecutionException e){
					throw new Exception(e.getMessage());
				}
				//TODO testcycle checking.

				Controller.message("\n" + testCaseResult.get_testCaseId() + "\t"
						+ testCaseResult.get_status() + "\t"
						+ testCaseResult.get_testExecution_Time() + "\t"
						+ testCaseResult.get_comments());
				if (testCycleId == null) {
					Log.Debug("Controller/SaveTestCaseResultEveryTime : TestCycle=Null");
					// // message("No testcycle provided  ");
				} else {

					Log.Debug("Controller/SaveTestCaseResultEveryTime : TestCycle="
							+ testCycleId);

				}

			}
		}
		Controller.message("\n-----------------------------------------------------------------------------------------------------------------------");
		Controller.message("\n-----------------------------------------------------------------------------------------------------------------------");

		Log.Debug("Controller/SaveTestCaseResult : End of the Function");
	}
	public void SaveTestCaseResultEveryTime(ExecutedTestCase etc)
			throws ReportingException,Exception {
		Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : Start of the Function");
		String buildNumber = buildNo;// Get the build number from Davos ...
		// ("The Build Tag is "+BuildTag+" The build no "+BuildNo);
		TestCaseResult testCaseResult = new TestCaseResult();
		testCaseResult.set_testCaseId(etc.testCaseID);
		// testCaseResult.set_testSuiteName(testSuitName);
		if (StringUtils.isNotBlank(testSuiteId)) {
			testCaseResult.set_testSuiteId(Integer.valueOf(testSuiteId));
		}
		testCaseResult.set_testEngineerName("Automation");
		testCaseResult.set_executionDate(etc.testCaseCompletetionTime);
		testCaseResult.set_testExecution_Time(etc.timeToExecute);
		testCaseResult.set_status(etc.testCaseStatus);
		testCaseResult.set_comments(etc.testCaseExecutionComments);

		testCaseResult.set_buildNo(StringUtils.EMPTY);
		if (StringUtils.isNotBlank(buildNumber)) {
			testCaseResult.set_buildNo(buildNumber);
		}
		String	testexecutiondetailid;
	/*	System.out.println("TC-ID:"+testCaseResult.get_testCaseId()+"\ndesc:"+ etc.testcasedescription+
				"\nTC-id:"+testCycleId+"\nStatus: "+ testCaseResult.get_status()+"\nBuildNo:"+buildNumber+
				"\nExec Time:"+new Integer(testCaseResult.get_testExecution_Time())
				.toString()+ ""+ ""+"\nTS name:"+testSuiteName+"\ntpid"+ topologySetId
				+"\nts role"+testSuiteRole+"\nTc comment:"+ testCaseResult.get_comments());*/
		try{
		if (StringUtils.isNotBlank(testCycleId)) {
			
			Log.Debug("ExecutedTestCaseData/SaveTestCaseEveryTime :: Davos TestExecutionDetail_write call using TestCaseId="
					+ testCaseResult.get_testCaseId()
					+ " TestCycleId="
					+testCycleId + "---TIMESTAMP:: " + Utility.dateNow());
			if (StringUtils.isBlank(etc.testcasedescription)) {
				etc.testcasedescription = "";
			}
			ContextVar.setContextVar("ZUG_TCYCLENAME",
					davosclient.getTestCycleDescriptionByID(testCycleId));
			testexecutiondetailid = davosclient.testExecutionDetails_write(
					testCaseResult.get_testCaseId(), etc.testcasedescription,
					testCycleId, testCaseResult.get_status(), buildNumber,
					new Integer(testCaseResult.get_testExecution_Time())
					.toString(), "", "",testSuiteName, topologySetId,
					testSuiteRole, testCaseResult.get_comments());
			// variables=davosclient.variables_write(testCaseResult.get_testCaseId(),""
			// ,"" );

		} else {

			Log.Debug("ExecutedTestCaseData/SaveTestCaseEveryTime:: Davos TestCycle_Write call using TestPlanID="
					+ testPlanId
					+ " TestCycleDescription=TC_"
					+ Utility.dateAsString()
					+ "---TIMESTAMP:: "
					+ Utility.dateNow());
			// Harness specific TestCycle name
			ContextVar.setContextVar("ZUG_TCYCLENAME",
					"TC_" + Utility.dateAsString());
			testCycleId = davosclient.testCycle_write(testPlanId, ContextVar
					.getContextVar("ZUG_TCYCLENAME"), "", "", new Integer(
							Controller.initializationTime).toString(),
							new Integer(testCaseResult.get_testExecution_Time())
			.toString());
			Log.Debug("ExecutedTestCaseData/SaveTestCaseEveryTime:: Davos TestExecutionDetail_write call using TestCaseId="
					+ testCaseResult.get_testCaseId()
					+ " TestCycleId="
					+testCycleId + "---TIMESTAMP:: " + Utility.dateNow());
			testexecutiondetailid =davosclient.testExecutionDetails_write(
					testCaseResult.get_testCaseId(), etc.testcasedescription,
					testCycleId, testCaseResult.get_status(), buildNumber,
					new Integer(testCaseResult.get_testExecution_Time())
					.toString(), "", "",testSuiteName,topologySetId,
					testSuiteRole, testCaseResult.get_comments());
		}
		}catch(DavosExecutionException de){
			throw new ReportingException(de.getMessage());
		}
		Log.Debug(String
				.format("ExecutedTestCaseData/SaveTestCaseResultEveryTime : TestCaseId = %s Status = %s and Comments = %s is saved. The TestExecutionID=%s",
						testCaseResult.get_testCaseId(),
						testCaseResult.get_status(),
						testCaseResult.get_comments(), testexecutiondetailid));
		// // Harness Specific ContextVariable to store TestCycle ID
		ContextVar.setContextVar("ZUG_TCYCID", testCycleId);
		ContextVar.setContextVar("ZUG_TESTCYCLEID",testCycleId);
		// // Harness Specific ContextVariable to store TestExecutionDetail ID
		ContextVar.setContextVar("ZUG_TSEXDID", testexecutiondetailid);
		// Harness Specific Topologysetid
		ContextVar.setContextVar("ZUG_TOPOSET", "" + topologySetId);
		TOPOSET = ContextVar.getContextVar("ZUG_TOPOSET");
		Controller.TOPOSET=TOPOSET;
		Log.Debug("ExecutedTestCaseData/SaveTestCaseResultEveryTime : End of the Function");
	}



	public void saveTestCaseVariables(HashMap<String, String> variablemap,
			String testcase_id, String testsuite_name)
					throws ReportingException, InterruptedException {
		Set<String> variable_key_set = variablemap.keySet();
		Iterator<String> var_key_iterate = variable_key_set.iterator();
		// message("error trail coming to here?? ");
		try{
			while (var_key_iterate.hasNext()) {
				String variable_key = var_key_iterate.next();
				Log.Debug("TestCase/saveTestCaseVariables: The variable key saved "
					+ variable_key
					+ " the value "
					+ variablemap.get(variable_key));
				davosclient.variables_write(testsuite_name, testcase_id,
					variable_key, variablemap.get(variable_key));
			}	
		}catch(DavosExecutionException e){
			throw new ReportingException(e.getMessage());
		}
			
		
	}


	/***
	 * Archive Harness and Product log files.
	 */
	public void archiveLog() throws ReportingException {

		String archiveLogLocation = StringUtils.EMPTY;
		try {
			// Log.Debug("Controller/ArchiveLog() : Function Started.");
			try{

				archiveLogLocation = davosclient.zermattConfig_read("Archivelocation");
			}catch(DavosExecutionException e){
				throw new ReportingException(e.getMessage());
			}

			File ArchivePath = new File(archiveLogLocation);

			if (!ArchivePath.isAbsolute()) {
				Log.Error("Controller/ArchiveLog : Log is not archived. Archive Location: "
						+ archiveLogLocation + " is not valid path.");

				return;
			}

			archiveLogLocation = String.format("%s\\%s\\%s\\%s\\%s",
					archiveLogLocation,testPlanId,testCycleId,testSuiteId,TOPOSET);

			String exactDateTime = Utility.dateAsString();
			// Create date time directory.
			archiveLogLocation = String.format("%s\\%s", archiveLogLocation,
					exactDateTime);

			String zugArchive = String.format("%s\\Zug", archiveLogLocation);
			String productArchive = String.format("%s\\Application",
					archiveLogLocation);

			File zugArchivePath = new File(zugArchive);
			File productArchivePath = new File(productArchive);
			if (!zugArchivePath.exists()) {
				// Harness Archive Directory does not exist.
				zugArchivePath.mkdirs();
			}

			if (!productArchivePath.exists()) {
				// Product Archive Directory does not exist.
				productArchivePath.mkdirs();
			}

			Log.Debug(" Copying and zipping log files in : " + zugArchive);
			for (int i = 0; i < Log.HarnessLogFileList.size(); i++) {
				String filePath = Log.HarnessLogFileList.get(i);
				File file = new File(filePath);
				if (file.exists()) {
					String destinationPath = String.format("%s\\%s",
							zugArchive, file.getName());
					Utility.copyFile(filePath, destinationPath);
				}
			}

			// Copy the product log files to product archive directory.
			for (int i = 0; i < Controller.productLogFiles.length; i++) {
				File file = new File(Controller.productLogFiles[i]);

				// TODO - need to check on this

				if (file.exists()) {
					String fileName = file.getName().substring(0,
							file.getName().indexOf("."));
					String destinationPath = productArchive + SysEnv.SLASH + fileName
							+ ".txt";
					Utility.copyFile(Controller.productLogFiles[i], destinationPath);
				}
			}

			// Zip the copied log files in archive location

			// new ZipUtility().zip(archiveLogLocation, new
			// File(archiveLogLocation).getParent()+ exactDateTime + ".zip");

			String zipTmpLocation = System.getenv(SysEnv.ZIP_DIR) + SysEnv.SLASH
					+ exactDateTime + ".zip";
			new ZipUtility().zip(zugArchive, zipTmpLocation);

			// Utility.deleteDirectory(new File(archiveLogLocation));
			if (Utility.deleteDirectory(new File(zugArchive))) {
				zugArchivePath.mkdirs();
			}
			Utility.copyFile(zipTmpLocation, zugArchive +SysEnv. SLASH + exactDateTime
					+ ".zip");
			System.out.println("Finished creating zip file in : " + zugArchive);
			Utility.deleteDirectory(new File(zipTmpLocation));

			if (productArchivePath.list().length != 0) {
				String zipProductLocation = System.getenv(SysEnv.ZIP_DIR) +SysEnv. SLASH
						+ "Product_" + exactDateTime + ".zip";
				new ZipUtility().zip(productArchive, zipProductLocation);
				if (Utility.deleteDirectory(new File(productArchive))) {
					productArchivePath.mkdirs();
				}
				Utility.copyFile(zipProductLocation, productArchive +SysEnv. SLASH
						+ "Product_" + exactDateTime + ".zip");
				Utility.deleteDirectory(new File(zipProductLocation));
			}

		} catch (Exception e) {
			System.out.println("Exception occured while archiving log on "
					+ archiveLogLocation + ": " + e.getMessage());
		}
	}
	
	public void heartBeat(String sessionId)throws ReportingException,InterruptedException{
		try{
			davosclient.heartBeat(sessionid);
		}catch(DavosExecutionException e){
			throw new ReportingException(e.getMessage());
		}
	}


}
