package com.automature.zug.reporter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;


import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ActionOnDuplicate;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestImportance;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

import com.automature.zug.engine.Controller;
import com.automature.zug.engine.ExecutedTestCase;
import com.automature.zug.exceptions.ReportingException;
import com.automature.zug.util.Log;

public class TestLinkReporter implements Reporter{

	boolean isNewTestPlan=false;
	//	boolean isNewTestSuite=false;

	String testSuiteName=null;
	String dBhostName=null;
	String devKey=null;
	String projectName=null;
	String testPlanName=null;
	String buildName=null;
	TestLinkAPI api=null;
	TestProject tProject=null;
	TestPlan tPlan=null;
	TestSuite testSuite=null;
	Build build=null;
	String dbUserName =null;

	Hashtable<String,TestCase> testCases=new Hashtable<String,TestCase>();
	ReportTCResultResponse rtcrr=null;


	public TestLinkReporter(Hashtable ht){
		this.testSuiteName =(String)ht.get("testsuitename");
		this.dBhostName = (String)ht.get("dbhostname");
		this.devKey =(String)ht.get("dbuserpassword");
		this.projectName =(String)ht.get("testsuiterole");
		this.testPlanName=(String)ht.get("testplanname");
		this.buildName=(String)ht.get("buildname");
		this.dbUserName = (String)ht.get("dbusername");
	}

	public boolean connect()throws ReportingException{
		Controller.message("\n\nConnecting to Test Link"+dBhostName);
		try {
			URL testlinkURL = new URL(dBhostName);
			api = new TestLinkAPI(testlinkURL,devKey);
		} catch (MalformedURLException mue) {
			Log.Error("Error in connecting to the Test Link :"+mue.getMessage());
			System.exit(-1);
		}catch(TestLinkAPIException e){
			Log.Error("Controller/ConnectToTestLink : Exception while Connecting to TestLink "
					+ " of host "
					+ this.dBhostName
					+ " with user dev key"
					+ devKey + ". The Exception is " +e.getMessage());
			System.exit(-1);
		}
		return true;
	}

	private boolean validateTestProject() throws Exception{
		try{
			tProject=api.getTestProjectByName(projectName);
		}catch(TestLinkAPIException e){
			Log.Error("Error in finding the Project "+projectName+" Error Message "+e.getMessage());
			return false;
		}
		return true;
	}

	private TestSuite getTestSuite(int id){
		TestSuite tss[]=null;
		TestSuite ts=null;
		try{
			tss=api.getTestSuitesForTestSuite(id);
		}catch(Exception e){
			return ts;
		}
		for(TestSuite tst:tss){
			if(tst.getName().equalsIgnoreCase(testSuiteName)){
				return tst;
			}else{
				ts=getTestSuite(tst.getId());
			}
		}
		return ts;
	}

	private boolean validateTestSuite(){
		TestSuite tss[]=api.getFirstLevelTestSuitesForTestProject(tProject.getId());
		for(TestSuite tst:tss){
			if(tst.getName().equalsIgnoreCase(testSuiteName)){
				testSuite=tst;
				return true;
			}else{
				testSuite=getTestSuite(tst.getId());
				if(testSuite!=null){
					return true;
				}
			}
		}

		if(testSuite==null){
			Log.Error("Error: Couldn't find the test suite "+testSuiteName+" in the project "+tProject.getName());
			return false;
		}else{
			return true;
		}
	}

	private void validateTestCases(){
		try{
			TestCase tcs[]=api.getTestCasesForTestSuite(testSuite.getId(), true, null);
			for(TestCase tc:tcs){
				testCases.put(tc.getName(),tc);
			}
		}catch(TestLinkAPIException e){
			Log.Debug("TestLinkReporter/validateTestSuite:Exception-"+e.getMessage());
		}
	}

	private void validateTestPlan()throws TestLinkAPIException{
		try{
			tPlan=api.getTestPlanByName(testPlanName, projectName);
		}catch(TestLinkAPIException e){
			tPlan=api.createTestPlan(testPlanName, projectName, "", true, true);
			Controller.message("TestLinkReporter:TestPlan "+testPlanName +" not found system will create a test plan");
			this.setBuild();
			//build=api.createBuild(tPlan.getId(), tProject.getName()+"-B-"+d.getDay()+d.getMonth()+d.getYear(), "");
			isNewTestPlan=true;
			return;
		}	
		this.setBuild();
	}



	public boolean ValidateDatabaseEntries()throws InterruptedException,Exception{
		try{
			if(!this.validateTestProject()){
				return false;
			}
			if(!this.validateTestSuite()){
				return false;
			}
			this.validateTestPlan();
			this.validateTestCases();
			//	this.setTestCases();
		}catch(TestLinkAPIException e){
			Log.Error("Error :"+e.getMessage());
			throw e;
		}
		return true;
	}

	public void saveTestCaseResults(Hashtable ht)throws Exception {

	}

	public void heartBeat(String sessionid)throws ReportingException{

	}

	private void setBuild()throws TestLinkAPIException{
		try{
			Build builds[]=api.getBuildsForTestPlan(tPlan.getId());
			for(Build b:builds){
				if(b.getName().equalsIgnoreCase(buildName)){
					build=b;					
					return;
				}
			}
		}catch(Exception e){
			//Log.Error("\nException "+e.getMessage());
		}
		if(buildName!=null && !buildName.isEmpty()){
			build=api.createBuild(tPlan.getId(), buildName, "");
		}else{
//			Date d=new Date();
			Calendar c=new GregorianCalendar();
			
			buildName=tProject.getName()+"-B-"+ c.get(Calendar.HOUR_OF_DAY)+c.get(Calendar.DAY_OF_MONTH)+ c.get(Calendar.MONTH)+c.get(Calendar.YEAR);
			build=api.createBuild(tPlan.getId(),buildName , "");
		}
		Controller.message("TestLinkReporter:Build name was not provided or the build name doesn't exists System has created a new build: "+build.getName());
	}


	private TestCase getTestCase(ExecutedTestCase etc)throws Exception{
		TestCase tc=null; 
		boolean isNewTC=false;

		if(testCases.containsKey(etc.testCaseID)){
			tc=testCases.get(etc.testCaseID);
			//	System.out.println("found the test case in DS");
		}else{
			String summary=etc.testcasedescription;
			//System.out.println("testCasecomments"+etc.testCaseExecutionComments);
			if(summary==null||summary.isEmpty()){
				summary="none";
			}
			try{
				tc=api.createTestCase(etc.testCaseID, testSuite.getId(), tProject.getId(), this.dbUserName,summary , null,"", TestImportance.MEDIUM, ExecutionType.AUTOMATED, 0, 1, false, ActionOnDuplicate.BLOCK);
			}catch(Exception e){
				e.printStackTrace();
			}
			//	System.out.println("test case not found so new is created");
			isNewTC=true;
		}
		//}
		//if(isNewTestSuite || isNewTestPlan || isNewTC){
		if( isNewTestPlan || isNewTC ){
			addTestCaseToTestPlan(tc);
		}
		return tc;
	}

	private void addTestCaseToTestPlan(TestCase tc)throws TestLinkAPIException{
		TestImportance ti=tc.getTestImportance();
		if(ti==null){
			ti=TestImportance.MEDIUM;
		}
		Integer version=tc.getVersion();
		if(version==null){
			version=new Integer(1);
		}
		//	System.out.println("adding new test case");
		api.addTestCaseToTestPlan(tProject.getId(), tPlan.getId(), tc.getId(),version,new Integer(1), tc.getOrder(),ti.getValue());
		//	System.out.println("Test case added");
	}

	public void SaveTestCaseResultEveryTime(ExecutedTestCase etc)throws Exception{
		if(!etc.testCaseStatus.equals("pass") &&!etc.testCaseStatus.equals("fail") ){
			return;
		}
	//	System.out.println(etc.testCaseID);
		TestCase tc=getTestCase(etc);
		String notes="";
		ExecutionStatus status=null;
		if(etc.testCaseStatus.equals("pass")){
			status=ExecutionStatus.PASSED;
			//double timeToExecute=(etc.timeToExecute/1000;
			notes="Time to execute :"+etc.timeToExecute/1000.0+" sec\nCompletion Time:"+etc.testCaseCompletetionTime;
		}else if(etc.testCaseStatus.equals("fail")){
			status=ExecutionStatus.FAILED;
			notes=etc.testCaseExecutionComments;
		}
		try{
			rtcrr=api.reportTCResult(tc.getId(), tc.getInternalId(), tPlan.getId(),status, build.getId(), build.getName(), notes, true, "", 0, "", null, true);
		}catch(Exception e){
			//		System.out.println("Exception"+e.getMessage());
			try{
				addTestCaseToTestPlan(tc);
				rtcrr=api.reportTCResult(tc.getId(), tc.getInternalId(), tPlan.getId(),ExecutionStatus.PASSED, build.getId(), build.getName(), notes, true, "", 0, "", null, true);
			}catch(Exception ex){
				ex.printStackTrace();
			}

		}

	}
	public void archiveLog() throws ReportingException {
	/*	int eid;
		try{
			eid=rtcrr.getExecutionId();
		}catch(Exception e){
			return;
		}
		for (int i = 0; i < Log.HarnessLogFileList.size(); i++) {
			String filePath = Log.HarnessLogFileList.get(i);

			try{	
				File file = new File(filePath);
				if (file.exists()) {
					String line=null;
					String bFile=null;

					String fileContent = null;

					try {
						byte[] byteArray = FileUtils.readFileToByteArray(file);
						fileContent = new String(Base64.encodeBase64(byteArray));
					} catch (IOException e) {
						e.printStackTrace( System.err );
						continue;//System.exit(-1);
					}
					if(fileContent.isEmpty()){
						Controller.message(file.getName()+" is empty");
						continue;
					}
					try{
						Attachment attachment = api.uploadExecutionAttachment(eid, "Logs", "", file.getName(), "text/plain", fileContent);
						System.out.println( attachment.getFileName()+" uploaded");
					}catch(TestLinkAPIException e){
						System.out.println("TestLinkReporter:Error uploading attachement "+file.getName()+" error message:"+e.getMessage());
					}


				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}*/
		
	}

	public void saveTestCaseVariables(HashMap<String, String> variablemap,
			String testcase_id, String testsuite_name)
					throws ReportingException, InterruptedException {

	}
	
	public void destroySession(String sessionId)throws ReportingException{
		
	}

}
