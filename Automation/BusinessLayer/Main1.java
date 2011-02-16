package BusinessLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import DatabaseLayer.DataClasses.IDataTable;

public class Main1 {

	public static void main(String args[]) throws Exception
	{
		BusinessLayer.Initialize i= new Initialize();
		
		String dbname="test";
		String dbhost="lap883";
		String uname="root";
		String pass="";
		i.setDatbaseName(dbname);
		i.setHostName(dbhost);
		i.setUserName(uname);
		i.setPassword(pass);
		
		i.InitializeDbConnection();
		
		
		
		//TESTING FOR TESTPLAN
		//EXCEPTION FOR ROLLBACK TRANSACTION REMOVE IT 
		
		BusinessLayer.TestPlan testplan= new TestPlan();
		
		TestPlanData testplandata= new TestPlanData();
		
		testplandata.setTestPlanId(7);
		testplandata.setTestplantemplate_id(1);
		testplandata.setTopologyset_id(1);
		//testplandata.setTopologySetListId(1);
		testplandata.setTestSuiteListId(1);
		testplandata.setTestPlanLongDesc("Long desc1");
		testplandata.setTestPlanShortDesc("Short desc1");
		testplandata.setCreationDate(new Date());
		testplandata.setModificationDate(new Date());
		testplandata.setCreatedBy("created by");
		testplandata.setModifiedBy("modified by");
		testplandata.setSetupScript("setup script1");
		testplandata.setTearDownScript("tearDownScript");
		testplandata.setStatus("status");
		testplandata.setProduct("product");
		testplandata.setComment("comment");
		testplandata.setTopologySetListId(1);
		testplandata.setCodeName("codeName");
		
		
		testplan.save(testplandata);
		
		boolean bool=testplan.TestplanIDExist(2);
		System.out.println("Test Plan id exists"+ bool);
		
		List <Integer> list= testplan.SelectTestplan();
		
		for (Iterator iter = list.iterator(); iter.hasNext();) 
        {          	      
        	try
        	{
        		int obj = (Integer)iter.next();
        		System.out.println("Objects "+obj);
        		
        		
        	}catch(Exception e)
        	{
        		
        		throw new Exception("DBOperations/ Select : Exception Occured ");
        	}
	
        }
	
		
	
		
		//TESTING FOR TESTSUITE
		
		
		BusinessLayer.TestSuite testSuite= new TestSuite();
		BusinessLayer.TestSuiteData testSuiteData = new TestSuiteData();
		
		//testSuiteData.setRole(1);
		testSuiteData.setTestSuiteName("testSuiteName2");
		testSuiteData.setComment("comment1");
		//testSuiteData.setCreationDate(new Date());
		testSuiteData.setModificationDate(new Date());
		testSuiteData.setCreatedBy("createdBy1");
		testSuiteData.setModifiedBy("modifiedBy1");
		testSuiteData.setStatus("status1");
		testSuiteData.setElapsedTimeMax(12);
		testSuiteData.setElapsedTimeMin(10);
		testSuiteData.setTestPlanFilePath("testPlanFilePath");
		testSuiteData.setInitializationTime(1);
		testSuiteData.setExecutionTime(1);
		
		testSuite.Save(testSuiteData);
		List <String> list1=testSuite.SelectTestSuite();
	
		int id=testSuite.GetTestSuiteId("testSuiteName3");
		System.out.println("Test ID"+id);
	
		
		  
		
		 
		//TESTING FOR TOPOLOGY TABLE
		
		
		 
      BusinessLayer.TopologyData topologyData = new TopologyData();
      topologyData.setTopologyId(6);
      topologyData.setRole(1);
      topologyData.setMachinecatalog(1);
      topologyData.setRationale(" rationale");
      topologyData.setOsArchitecture("osArchitecture");
      topologyData.setOsVersion("osVersion");
      topologyData.setOsLanguageId("osLanguageId");
      //topologyData.setTopoComponantId("topoComponantId");
      topologyData.setOsServicePack("osServicePack");
      topologyData.setBaseVmIdentifier("baseVmIdentifier");
      
      BusinessLayer.Topology topology= new Topology();
      
      topology.Save(topologyData);
      boolean bool1=topology.TopologyExist(3);
      System.out.println("TopologyExist :"+bool1);

		
		//TESTING FOR TESTCASE 
	
		    
		
		 List<BusinessLayer.Variable> variables = new ArrayList<BusinessLayer.Variable>();
			
		  for (int i1 =0 ; i1<3; i1++)
		  {
			  BusinessLayer.Variable var = new BusinessLayer.Variable();
	          var.set_name("name"+i1);
	          var.set_value("VAlue"+i1);
	          var.set_description("desrcription"+i1);
	          variables.add(var);
		  }
		  
		  TestCaseData testCaseData= new TestCaseData();
		  testCaseData.set_variableList(variables);
		  testCaseData.set_testSuiteName("testSuiteName1");
		  testCaseData.set_testCaseIdentifier("caseIdentifier1");
		  testCaseData.set_description("description1");
		  testCaseData.set_creationDate(new Date());
		 testCaseData.setModificationDate(new Date());
		 testCaseData.setTestSteps("testSteps1");
		 
		 BusinessLayer.TestCase testCase= new TestCase();
		 testCase.Save(testCaseData);
		
		
		//Testing For TestCycle
/*
		List<Integer> list= new ArrayList<Integer>();
		BusinessLayer.TestCycle testCycle= new TestCycle();
		list=testCycle.SelectTestCycle();
		for (Iterator iter = list.iterator(); iter.hasNext();) 
        {     
			try
        	{
        		int obj = (Integer)iter.next();
        		System.out.println("TestCycleID "+obj);
        		
        		
        	}catch(Exception e)
        	{
        		
        		throw new Exception("DBOperations/ Select : Exception Occured ");
        	}
        }
		
		boolean bool=testCycle.TestCycleExist(1);
		System.out.println("TestCycleExist:"+bool);
		bool=testCycle.TestCycleExist(2);
		System.out.println("TestCycleExist:"+bool);
		bool=testCycle.TestCycleExist(3);
		System.out.println("TestCycleExist:"+bool);
		bool=testCycle.TestCycleExist(4);
		System.out.println("TestCycleExist:"+bool);

		TopologyDetail topologyDetail = new TopologyDetail();
		topologyDetail.set_roleID(1);
		topologyDetail.set_machineHostId(1);
		topologyDetail.set_topologyId(1);
		topologyDetail.set_buildNumber("build number");
		
		TopologyDetail topologyDetail1 = new TopologyDetail();
		topologyDetail1.set_roleID(1);
		topologyDetail1.set_machineHostId(1);
		topologyDetail1.set_topologyId(2);
		topologyDetail1.set_buildNumber("build number1");
		
		TestCaseResult testCaseResult = new TestCaseResult();
		PerformanceExecutionDetailTable performanceExecutionDetailTable = new PerformanceExecutionDetailTable();
		
		performanceExecutionDetailTable.set_performanceExecutionDetailId("executionDetailId");
		
		performanceExecutionDetailTable.set_elapssedMin(1);
		performanceExecutionDetailTable.set_elapssedMax(3);
		performanceExecutionDetailTable.set_average(23.9F);
		performanceExecutionDetailTable.set_95Percentage(45);
		
		
		testCaseResult.set_testCaseId("caseIdentifier2");
		testCaseResult.set_testSuiteName("test1");
		testCaseResult.set_executionDate(new Date());
		testCaseResult.set_testEngineerName("engineerName1");
		testCaseResult.set_status("status1");
		testCaseResult.set_comments("_comments1");
		testCaseResult.set_buildNo("build no1");
		testCaseResult.set_testExecution_Time(1);
		testCaseResult.set_performanceExecutionDetailTable(performanceExecutionDetailTable);
		
		
		PerformanceExecutionDetailTable performanceExecutionDetailTable1 = new PerformanceExecutionDetailTable();
		performanceExecutionDetailTable.set_performanceExecutionDetailId("executionDetailId1");
		
		performanceExecutionDetailTable.set_elapssedMin(11);
		performanceExecutionDetailTable.set_elapssedMax(31);
		performanceExecutionDetailTable.set_average(23.5F);
		performanceExecutionDetailTable.set_95Percentage(50);
		
		TestCaseResult testCaseResult1 = new TestCaseResult();
		testCaseResult1.set_testCaseId("caseidentifier1");
		testCaseResult1.set_testSuiteName("testSuiteName1");
		testCaseResult1.set_executionDate(new Date());
		testCaseResult1.set_testEngineerName("engineerName1");
		testCaseResult1.set_status("status1");
		testCaseResult1.set_comments("_comments1");
		testCaseResult1.set_buildNo("build no1");
		testCaseResult1.set_testExecution_Time(1);
		testCaseResult1.set_performanceExecutionDetailTable(performanceExecutionDetailTable1);
		
		TopologySetResultData topologySetResultData= new TopologySetResultData();
		List<TestCaseResult> _testCaseResultList = new ArrayList<TestCaseResult>();
		_testCaseResultList.add(testCaseResult);
		_testCaseResultList.add(testCaseResult1);
		
		List<TopologyDetail> _topologyDetailList = new ArrayList<TopologyDetail>();
		_topologyDetailList.add(topologyDetail1);
		_topologyDetailList.add(topologyDetail);
		
		
		topologySetResultData.set_testCaseResultList(_testCaseResultList);
		topologySetResultData.set_topologyDetailList(_topologyDetailList);
		
		TestCycleData testCycleData = new TestCycleData();
	
		testCycleData.setTestplan_id(1);
		testCycleData.setStartedDate( new Date());
		testCycleData.setEndDate(new Date());
		testCycleData.setInitializationTime(1);
		testCycleData.setExecutionTime(1);
		testCycleData.setModificationDate(new Date());
		//TopologySetResultData;
		List<TopologySetResultData> _topologySetResultDataList = new ArrayList<TopologySetResultData>();
		_topologySetResultDataList.add(topologySetResultData);
		testCycleData.set_topologySetResultDataList(_topologySetResultDataList);
		
		BusinessLayer.TestCycle testCycle2= new TestCycle();
		testCycle2.Save(testCycleData);
		*/
		
		//Testing for framework meta data
		/*
		BusinessLayer.Name name = Name.ArchiveLocation;
		BusinessLayer.FrameworkMetaData metaData= new FrameworkMetaData();
		String value= metaData.GetValue(name);
		System.out.println("Value"+value);
		*/
		
		
		//Testing for Testplan update
	
		/*TestPlanTestSuiteListData testSuiteListData = new TestPlanTestSuiteListData();
		testSuiteListData.setTestSuiteId(1);
		testSuiteListData.setTestSuiteListId(1);
		
		
		TestPlanTopologySetData TestPlanTopologySetData1= new TestPlanTopologySetData();
		TestPlanTopologySetData1.get_topologyList().add(1);
		TestPlanTopologySetData1.get_topologyList().add(2);
		
		
		TestPlanTopologySetData TestPlanTopologySetData2= new TestPlanTopologySetData();
		TestPlanTopologySetData2.get_topologyList().add(1);
		TestPlanTopologySetData2.get_topologyList().add(2);
		
		TestPlanTestSuiteData testPlanTestSuiteData = new TestPlanTestSuiteData();
		
		testPlanTestSuiteData.setRole_id(1);
		testPlanTestSuiteData.setTestSuiteName("testSuiteName2");
		testPlanTestSuiteData.setComment("Comments");
		testPlanTestSuiteData.setCreationDate(new Date());
		testPlanTestSuiteData.setModificationDate(new Date());
		testPlanTestSuiteData.setCreatedBy("createdBy3");
		testPlanTestSuiteData.setModifiedBy("modifiedBy3");
		testPlanTestSuiteData.setStatus("status3");
		testPlanTestSuiteData.setElapsedTimeMin(3);
		testPlanTestSuiteData.setElapsedTimeMax(33);
		testPlanTestSuiteData.setTestPlanFilePath("testPlanFilePath");
		
		testPlanTestSuiteData.setInitializationTime(13);
		testPlanTestSuiteData.setExecutionTime(14);
		
		TestPlanUpdateData testPlanUpdateData= new TestPlanUpdateData();
		
		testPlanUpdateData.TestSuiteData= testPlanTestSuiteData;
		testPlanUpdateData.TestSuiteListData=testSuiteListData;
		testPlanUpdateData.TopologySets.add(TestPlanTopologySetData1);
		testPlanUpdateData.TopologySets.add(TestPlanTopologySetData2);
		BusinessLayer.TestPlanData plan= new TestPlanData();
		plan.setTestPlanId(1);
		testPlanUpdateData.testPlanData=plan;
		
		TestPlanUpdate planUpdate= new TestPlanUpdate();
		planUpdate.Save(testPlanUpdateData);*/
		
	}
}

