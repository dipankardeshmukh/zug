package BusinessLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import logs.Log;

import org.hibernate.Transaction;

import ZUG.Controller;

import DatabaseLayer.DBOperations;
import DatabaseLayer.Role;
import DatabaseLayer.Testcase;
import DatabaseLayer.Testcycle;
import DatabaseLayer.Testcycletopologyset;
import DatabaseLayer.Testplan;
import DatabaseLayer.Testsuite;
import DatabaseLayer.DataClasses.IDataTable;

public class TestCycle {
	DBOperations  _dbOperation= null;
	Transaction _transaction=null;
	
	
	private Integer _testCycleMessage;
	
	 public Integer get_testCycleMessage() {
		return _testCycleMessage;
	}

	public void set_testCycleMessage(Integer cycleMessage) {
		_testCycleMessage = cycleMessage;
	}

	public TestCycle()
     {
		 _dbOperation=new DBOperations();
         _testCycleMessage = 0;
     }
	 
	 public List<Integer> SelectTestCycle()throws Exception 
	 {
		 List<Integer> TestCycleList = new ArrayList<Integer>();
		 
		 try
		 {
			DatabaseLayer.Testcycle testCycleTable= new DatabaseLayer.Testcycle();
			_transaction=_dbOperation.BeginTransaction(_transaction);
		    List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
		    selectedDataList = _dbOperation.Select(testCycleTable);
			 
		    for (Iterator iter = selectedDataList.iterator(); iter.hasNext();) 
	        {     
            	
               	try
	        	{
	        		IDataTable obj = (IDataTable)iter.next();
	        		DatabaseLayer.Testcycle testCycleObject=(DatabaseLayer.Testcycle)obj;
	        		TestCycleList.add(testCycleObject.getTestCycleId());
	        		Log.Debug("TestCycle/SelectTestcycle : TestCycle ID: "+testCycleObject.getTestCycleId());
	        		
	        		
	        	}catch(Exception e)
	        	{
	        		Log.Error("TestCycle/SelectTestcycle :Exception :" + e.getMessage());
	        		throw e;
	        	}
		    }
		    _dbOperation.CommitTransaction(_transaction);
		 }
		 catch(Exception e)
		 {
			 Log.Error("TestCycle/SelectTestCycleId : Exception :" + e.getMessage());
             return TestCycleList;
		 }
		 return TestCycleList;
	 }
	
	 public synchronized void Save(TestCycleData testCycleData)throws Exception
	 {
		 Log.Debug(this.toString() + "/Save: Save() function called to save the topologyResultData.");
		
		  if (testCycleData == null)
              return;
		  try
          {
              //Begin Transaction.
			  _transaction=_dbOperation.BeginTransaction(_transaction);
             Log.Debug("TestCycle/Save: Transaction begin for saving TestCycle data.");
                      
            testCycleData.testCycleId=SaveTestCycleTable(testCycleData);
            
            Log.Debug("Test Cycle ID is :"+ testCycleData.testCycleId);
            
            //Save testResults for each Toplogy.
             for (int i = 0; i < testCycleData.get_topologySetResultDataList().size(); i++)
             {
            	 TopologySetResultData topologySetResultData = testCycleData.get_topologySetResultDataList().get(i);
                 //Insert TopologyDetial into TestCycleDetail table.
            	 int testCycleTopologySetId = SaveTestCycleTopologySetTable(testCycleData.getTestCycleId(),
                         topologySetResultData.get_topologyDetailList());
            	 // check if testCycleTopologySetId == 0 is returned before using this value if 0 is returned dont use
            
            	//Insert or Update TestCase result into TestExecutionDetails Table.
            	 SaveTestExecutionDetailsTable(testCycleTopologySetId, topologySetResultData.get_testCaseResultList());
             }
             //Commit transaction.
            _dbOperation.CommitTransaction(_transaction);
            Log.Debug(this.toString() + "/Save: Commit the transaction for saving TestCycle data.");
         
          }
		  catch(Exception e)
		  {
			   Log.Error(this.toString() + "/Save:Exception:" + e.toString());
               Log.Debug(this.toString() + "/Save: Rollback the transaction for saving TestCycle data.");
               _dbOperation.RollBackTransaction(_transaction);
               throw e;
            
		  }
	 }
	 
	/// Insert or Update testcase reult into TestExecutionDetail table.
	 private synchronized void SaveTestExecutionDetailsTable(int testCycleTopologySetId, List<TestCaseResult> testCaseResultList) throws Exception
	 {
		 Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: SaveTestExecutionDetailsTable() function called " +
         " for saving TestExecution detail.");
		 if (testCaseResultList == null)
         {
             return;
         }

         for (int i = 0; i < testCaseResultList.size(); i++)
         {
        	 
        	 TestCaseResult testCaseResult = testCaseResultList.get(i);
             /// if the status of test case is null then don't add the row in to TestExecution table
        	 if (testCaseResult.get_status()==null)
                 continue;
        	 DatabaseLayer.Testexecutiondetails testExecutionDetailsTable=new DatabaseLayer.Testexecutiondetails();
        	
        	DatabaseLayer.Testcycletopologyset testcycletopologyset = new Testcycletopologyset(testCycleTopologySetId); 
        	testExecutionDetailsTable.setTestcycletopologyset(testcycletopologyset);
        	Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: TestCycleTopologySetId: " +
                       testExecutionDetailsTable.getTestcycletopologyset().getTestCycleTopologySetId());
        	 
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: Find TestCase Identifier.");
             
             DatabaseLayer.Testcase testCaseTable = new  DatabaseLayer.Testcase();
             testCaseTable.setTestCaseIdentifier(testCaseResult.get_testCaseId());
        	 
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: TestCaseIdentifier: " +
              testCaseTable.getTestCaseIdentifier());
             
             //Get testSuite_Id for given testSuite name.
             
         
             BusinessLayer.TestSuite testSuite= new BusinessLayer.TestSuite();
             //int testsuiteid= testSuite.GetTestSuiteId(testCaseResult.get_testSuiteName());
             int testsuiteid=testCaseResult.get_testSuiteId();
             Log.Debug("Test suite id:"+testsuiteid);
             
             _transaction=_dbOperation.BeginTransaction(_transaction);
             
             
             
             
             
             //place begin transaction
             DatabaseLayer.Testsuite testsuite= new Testsuite(testsuiteid);    
             //testsuite.setTestSuiteId(new BusinessLayer.TestSuite().GetTestSuiteId(testCaseResult.get_testSuiteName()));
             testCaseTable.setTestsuite(testsuite);
             
             //testCaseTable.testsuite.setTestSuiteId(new BusinessLayer.TestSuite().GetTestSuiteId(testCaseResult.get_testSuiteName()));
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: TestSuiteID: " + testCaseTable.getTestsuite().getTestSuiteId());
             List<IDataTable> selectedTestCaseList =new ArrayList<IDataTable>();
             
             selectedTestCaseList = (List<IDataTable>)_dbOperation.Select(testCaseTable);
             
             if (selectedTestCaseList == null || selectedTestCaseList.size() == 0)
             {
                 throw new Exception("No record found for TestCaseId: " + testCaseTable.getTestCaseId()+
                     ", TestSuiteID: " + testCaseTable.getTestsuite().getTestSuiteId() + " from " + testCaseTable.toString());
             }
             
             //Assumed that TestCase record will be Unique.
             
             DatabaseLayer.Testcase testcase= new Testcase(((DatabaseLayer.Testcase)selectedTestCaseList.get(0)).getTestCaseId());
             testExecutionDetailsTable.setTestcase(testcase);
             //testExecutionDetailsTable.testcase.setTestCaseId(((Automation.Testcase)selectedTestCaseList.get(0)).getTestCaseId());
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: TestCaseId: " +
                     testExecutionDetailsTable.getTestcase().getTestCaseId());

             
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: Check testcase result record exist or not in " +
                     testExecutionDetailsTable.toString());
             
             
             List<IDataTable> selectedTestExecutionResult = new ArrayList<IDataTable>();
             selectedTestExecutionResult = (List<IDataTable>)_dbOperation.Select(
                     testExecutionDetailsTable);
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: Select query executed to find the testcase " +
             "result record.");
             
             // Following properties not useful to select testcase result.
             testExecutionDetailsTable.setTestEngineer(testCaseResult.get_testEngineerName());
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: TestEngineer: " +
                     testExecutionDetailsTable.getTestEngineer());
             
             testExecutionDetailsTable.setStatus(testCaseResult.get_status());
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: Status: " + testExecutionDetailsTable.getStatus());

             testExecutionDetailsTable.setExecutionDate(testCaseResult.get_executionDate());
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: ExecutionDate: " +
                     testExecutionDetailsTable.getExecutionDate());
             
             testExecutionDetailsTable.setComments(testCaseResult.get_comments());
             Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: Comments: " +
                     testExecutionDetailsTable.getComments());
             
             testExecutionDetailsTable.setBuildNumber(testCaseResult.get_buildNo());
             Log.Debug((this.toString() + "/SaveTestExecutionDetailsTable: BuildNo: " +
                     testExecutionDetailsTable.getBuildNumber()));
             
             testExecutionDetailsTable.setTestExecutionTime(testCaseResult.get_testExecution_Time());
             Log.Debug((this.toString() + "/SaveTestExecutionDetailsTable: TestExecutionTime: " +
                     testExecutionDetailsTable.getTestExecutionTime()));

             //Find PerformanceExecutionDetailId from PerformanceExecutionDetailTable
             //Currently not supported.
             //testExecutionDetailsTable.PerformanceExecutionDetailId = testCaseResult.PerformanceExecutionDetailTable;
             if (selectedTestExecutionResult == null)
             {
                 //Unable to select testcase result.
                 throw new Exception("Select query not executed successfully for TestCycleTopologySetId: " +
                     testExecutionDetailsTable.getTestExecutionDetailsId()+
                     ", TestCaseId: " + testExecutionDetailsTable.getTestcase().getTestCaseId()+
                     " from " + testExecutionDetailsTable.toString());
             }          
             else if (selectedTestExecutionResult.size() == 0)
             {
            	//Insert testcase result.
            	 //Create new id for TestExecutionDetails_Id.
                 //testExecutionDetailsTable.TestExecutionDetailsId = _dbOperation.GetNextId(typeof(
                   //  TestExecutionDetailsTable));
                
                 Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: Insert testcase result into " +
                      testExecutionDetailsTable.toString());
                 
                 DatabaseLayer.Testexecutiondetails array[]={testExecutionDetailsTable};
                 int rowAffected = _dbOperation.Insert(array);
             
                 if (rowAffected == 0)
                 {
                     throw new Exception("No record inserted into " + testExecutionDetailsTable.toString());
                 }
                 else
                 {
                    Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: record successfully inserted into "
                         + testExecutionDetailsTable.toString());
                 }
             }
             else if (selectedTestExecutionResult.size() == 1)
             {
                 //Update testcase result.
            	 DatabaseLayer.Testexecutiondetails oldTestExecutionDetailsTable=(DatabaseLayer.Testexecutiondetails)selectedTestExecutionResult.get(0);
                //Its update operation. So only TestExecutionDetailsId is not going to change. Also here assumed that
                //TestCycleTopologySet_Id and TestCase_Id is not changed.
            	//testExecutionDetailsTable.setTestExecutionDetailsId(oldTestExecutionDetailsTable.getTestExecutionDetailsId());
            	 testExecutionDetailsTable.setTestExecutionDetailsId(oldTestExecutionDetailsTable.getTestExecutionDetailsId());
            	 
            	 if(testExecutionDetailsTable.compare(oldTestExecutionDetailsTable)!=true)
            	 {
            		// Log.Debug(testExecutionDetailsTable.hashCode());
            		 //Log.Debug(oldTestExecutionDetailsTable.hashCode());
            		 
            		 int rowAffected = _dbOperation.merge(testExecutionDetailsTable);
                 	
            		// int rowAffected = _dbOperation.Update(testExecutionDetailsTable,oldTestExecutionDetailsTable);
                	 if (rowAffected == 0)
                     {
                        Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: No record updated from " +
                             testExecutionDetailsTable.toString());
                     }
                	 else
                     {
                        Log.Debug(this.toString() + "/SaveTestExecutionDetailsTable: record successfully updated from "
                             + testExecutionDetailsTable.toString());
                        
                     }
            	 }
             }
             else if (selectedTestExecutionResult.size() > 1)
             {
                 //Testcase result is not unique.
                 throw new Exception("TestCycleTopologySetId: " + testExecutionDetailsTable.getTestcycletopologyset().getTestCycleTopologySetId()+
                     ", TestCaseId: " + testExecutionDetailsTable.getTestcase().getTestCaseId() + " is not unique in " +
                     testExecutionDetailsTable.toString());
             }
        }
	 }
	 
	 
	// Insert TestCycle Details in TestCycleTopologySet table if entry does not exist and returns TestCycleTopologySet_Id.
	
	 private synchronized int SaveTestCycleTopologySetTable(int testcycleId, List<TopologyDetail> topologyDetailList)throws Exception 
	 {
		   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: SaveTestCycleDetailTable() " +
           "function called for saving testcycle detail.");
		   
		   if (topologyDetailList == null)
		   {
			   return 0;
		   }
		   int testCycleTopologySetId = 0;
		   
		// Find TestCycleTopologySet_Id from TestCycleTopologySet table for given list of topology_Ids..
		   StringBuilder whereClause = new StringBuilder();
		  
		   DatabaseLayer.Testcycletopologyset testcycleTopologySet=new DatabaseLayer.Testcycletopologyset();
		 
		   // selectQuery+= "join idatatable.testsuite test1 where test1.testSuiteId=1 and";
           //whereClause.append(" testcycleTopologySet.testcycle.testCycleId = " + testcycleId + " ");
		   whereClause.append(" join idatatable.testcycle test1 where test1.testCycleId =" + testcycleId + " ");
           for (int i = 0; i < topologyDetailList.size(); i++)
           {
        	 //Find "Topology_Id" exist in "Topology" table.
	        	Log.Debug("TestsCycle/SaveTestCycleTopologySetTable : " +
	                       "Find Topology_ID exist in Topology Table for topology: " + topologyDetailList.get(i).get_topologyId());
	        	DatabaseLayer.Topology topologyTable=new DatabaseLayer.Topology();
	        	
	        	topologyTable.setTopologyId(topologyDetailList.get(i).get_topologyId());
	        	
	        	List<IDataTable> selectedTopologyDataList = new ArrayList<IDataTable>();
	        	selectedTopologyDataList = _dbOperation.Select(topologyTable);
	        
	            if (selectedTopologyDataList == null || selectedTopologyDataList.size() == 0)
                {
                    throw new Exception("No record select from " + topologyTable.toString() +
                        " where Topology Id: " + topologyTable.getTopologyId());
                }
	            else
                {
                   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: Successfully selected the record from" +
                        topologyTable.toString() + " for topology: " + topologyDetailList.get(0).get_topologyId());
                }
	            //select * from `test`.`testcycletopologyset` where TestCycle_Id=5 and TestCycleTopologySet_Id in 
	            //(Select TestCycleTopologySet_Id From testcycletopologyset Where Topology_Id=3);
	           
	            
	            //select * from `test`.`testcycletopologyset` join `test`.`testcycle` where `test`.`testcycle`.TestCycle_Id=5
	            //and TestCycleTopologySet_Id in
	            //(Select `test`.`testcycletopologyset`.TestCycleTopologySet_Id From `test`.`testcycletopologyset` join
	            // `test`.`topology` where `test`.`topology`.Topology_Id=3);
	            
	            //whereClause.append(" and idatatable.testCycleTopologySetId in (Select testcycleTopologySet.testCycleTopologySetId " +
                //        " From Automation.Testcycletopologyset Where testcycleTopologySet.topology.topologyId=" + topologyDetailList.get(i).get_topologyId() + ")");
           
	            // selectQuery+= "join idatatable.testsuite test1 where test1.testSuiteId=1 and";
	            
	            whereClause.append(" and idatatable.testCycleTopologySetId in (Select idatatable1.testCycleTopologySetId " +
	             " From DatabaseLayer.Testcycletopologyset idatatable1 join idatatable1.topology  test2 Where test2.topologyId=" + topologyDetailList.get(i).get_topologyId() + ")");
	            
	            
	            //from testcycleTopologySet idatable join idatatable.testcycle test1 where test1.testCycleId =5
	            //and idatatable.testCycleTopologySetId in (Select idatatable1.testCycleTopologySetId 
	             // From Automation.Testcycletopologyset idatatable1 join idatatable1.topology  test2 Where test2.topologyId=" + topologyDetailList.get(i).get_topologyId() + ")");
		            
           }
           List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
           
           selectedDataList = (List<IDataTable>)_dbOperation.Select(testcycleTopologySet,
        		   	whereClause.toString());
           if (selectedDataList == null)
           {
               throw new Exception("No Records are selected from " + testcycleTopologySet.toString());
           }
           else if (selectedDataList.size() > 0)
           {
               //Assumed that all entries are same in selectedDataList.
        	   DatabaseLayer.Testcycletopologyset testcycletopologyset2= ((DatabaseLayer.Testcycletopologyset)selectedDataList.get(0));
        	   testCycleTopologySetId= testcycletopologyset2.getTestCycleTopologySetId();
        	   return testCycleTopologySetId;
               //return ((Automation.Testcycletopologyset)selectedDataList.get(0)).getTestCycleTopologySetId();
           }
           
           
         //Save TestCycleTopologySet_Id into TestCycleTopologySet table for given list of topology_Ids.
           for (int i = 0; i < topologyDetailList.size(); i++)
           {
        	   DatabaseLayer.Testcycletopologyset testCycleTopologySetTable=new DatabaseLayer.Testcycletopologyset();
        	
        	   DatabaseLayer.Testcycle testcycle= new Testcycle(testcycleId);
        	   testCycleTopologySetTable.setTestcycle(testcycle);
        	   
        	   //testCycleTopologySetTable.testcycle.testCycleId=testcycleId;
        	   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: TestcycleId: " +
                testCycleTopologySetTable.getTestcycle().getTestCycleId());
        	   
        	   TopologyDetail topologyDetail = topologyDetailList.get(i);
        	
        	   DatabaseLayer.Topology topology= new DatabaseLayer.Topology(topologyDetailList.get(i).get_topologyId());
        	   testCycleTopologySetTable.setTopology(topology);
        	   
        	   DatabaseLayer.Topologyset topologyset = new DatabaseLayer.Topologyset(Integer.parseInt(Controller.TOPOSET));
        	   testCycleTopologySetTable.setTopologyset(topologyset);
        	   
        	   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: TopologyId: " + 
        			   testCycleTopologySetTable.getTopology().getTopologyId());
        	   
        	   //////change rolename with role
        	   DatabaseLayer.Role rolename= new Role(Integer.parseInt(topologyDetailList.get(i).get_role()));
        	   List<IDataTable> selectedDataListrole = new ArrayList<IDataTable>();
  			   selectedDataListrole=_dbOperation.Select(rolename);
  			   
  			   DatabaseLayer.Role role= new Role();
  			   role= (DatabaseLayer.Role)selectedDataListrole.get(0);
  			   testCycleTopologySetTable.setRole(rolename);
        	   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: RoleName: " + testCycleTopologySetTable.getRole().getRoleName());

               //Check if topology entry already exists or not.
               Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: Check topology entry exist or not in " +
                    testCycleTopologySetTable.toString());
               List<IDataTable> selectedTestCycleTopologySetList = new ArrayList<IDataTable>();
               selectedTestCycleTopologySetList = (List<IDataTable>)_dbOperation.Select(
                       testCycleTopologySetTable);
               Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: Select query executed successfully to select " +
               "topology entry.");
               if (selectedTestCycleTopologySetList == null)
               {
                   throw new Exception("No record selected for TestcycleId: " + testCycleTopologySetTable.getTestcycle().getTestCycleId() +
                       ", " + "TestCycleTopologySetId: " + testCycleTopologySetTable.getTestCycleTopologySetId() +
                       ", MachineCatalogId: " + testCycleTopologySetTable.getMachinecatalog().getMachineCatalogId() + ", RoleName: " +
                       testCycleTopologySetTable.getRole().getRoleId() + " from " + testCycleTopologySetTable.toString());
                   
               }
               else if (selectedTestCycleTopologySetList.size() == 0)
               {
                   //Insert TestCycleTopologySet into TestCycleTopologySet table.
            	   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: Insert TestCycleDetail into " +
                           testCycleTopologySetTable.toString());
            	   testCycleTopologySetTable.setBuildNo(topologyDetailList.get(i).get_buildNumber());
            	   DatabaseLayer.Testcycletopologyset array[]={testCycleTopologySetTable};
            	   int rowAffected = _dbOperation.Insert(array); 
            	   if (rowAffected == 0)
                   {
                       //Unable to insert testcycledetail.
                       throw new Exception("Unable to insert TestcycleId: " + testCycleTopologySetTable.getTestcycle().getTestCycleId() + ", " +
                           "TestCycleDetailId: " + testCycleTopologySetTable.getTestCycleTopologySetId() + ", MachineCatalogId: " +
                           testCycleTopologySetTable.getMachinecatalog().getMachineCatalogId() + ", RoleName: " 
                           + testCycleTopologySetTable.getRole().getRoleId()+
                           " into " + testCycleTopologySetTable.toString());
                   }
            	   else
                   {
                       //Successfully inserted testcycledetail.
            		   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: " +
                           "TestCycleDetail successfully inserted into " + testCycleTopologySetTable.toString());
                   }
            	  //select the testcycletopologySetID which is just inserted
           
                   testCycleTopologySetId= testCycleTopologySetTable.getTestCycleTopologySetId();
         
               }
               else if (selectedTestCycleTopologySetList.size() == 1)
               {
            	   Log.Debug(this.toString() + "/SaveTestCycleTopologySetTable: TestCycleDetail already exist in " +
                       testCycleTopologySetTable.toString());
            	   
            	   testCycleTopologySetId=((DatabaseLayer.Testcycletopologyset)selectedTestCycleTopologySetList.get(0)).getTestCycleTopologySetId();
               }
               else if (selectedTestCycleTopologySetList.size() > 1)
               {
                   //TestCycleDetail is not unique.                        
                   throw new Exception("TestcycleId: " + testCycleTopologySetTable.getTestcycle().getTestCycleId()+ ", " +
                       "TestCycleDetailId: " + testCycleTopologySetTable.getTestCycleTopologySetId() + ", MachineCatalogId: " +
                       testCycleTopologySetTable.getMachinecatalog().getMachineCatalogId() + ", RoleName: " + testCycleTopologySetTable.getRole().getRoleId() +
                       " is not unique in " + testCycleTopologySetTable.toString());
               }
               
        	   
           }//end of for
           return testCycleTopologySetId;

	 }
	 // Insert TestCycleId into TestCycle table if entry does not exist in table.
	 //need to be modified if testcycleid is not provided. 
	 //this code will check if testplan id exists in the table   
	 private synchronized Integer SaveTestCycleTable(TestCycleData testCycleData) throws Exception
	 {
		int testcycleID=0;
		DatabaseLayer.Testcycle testCycleTable=new DatabaseLayer.Testcycle();
	    Log.Debug("TestCycle/: () function called for saving TestCycle data"
                 + " in table.");
	    if(testCycleData.testCycleId!=null)
	    	testCycleTable.setTestCycleId(testCycleData.testCycleId);
	    if(testCycleData.testCycleId!=null)
	    {
	    	_testCycleMessage = testCycleTable.getTestCycleId();
	    	Log.Debug(this.toString() + "/: TestCycleId: " + testCycleTable.getTestCycleId());
	    
	    	DatabaseLayer.Testplan testplan=new Testplan(testCycleData.testplan_id);
		    testCycleTable.setTestplan(testplan);
		    Log.Debug("TestCycle/: TestPlan ID: " + testCycleTable.getTestplan().getTestPlanId());
	       
	        //Check TestCycle entry exist in table.
	        Log.Debug(this.toString() + "/: Check TestCycleId entry exist in  TestCycle Table or not.");
	  
	        List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
	        selectedDataList = _dbOperation.Select(testCycleTable);
	        Log.Debug(this.toString() + "/: Select query executed to check TestCycleId exist or not.");
	        
	        testCycleTable.setExecutionTime(testCycleData.executionTime);
	        Log.Debug(this.toString() + "/: ExecutionTime: " + testCycleTable.getExecutionTime());
	        
	        testCycleTable.setInitializationTime(testCycleData.initializationTime);
	        Log.Debug(this.toString() + "/: InitializationTime: " + testCycleTable.getInitializationTime());
	      
	        if (selectedDataList == null)
	        {
	            throw new Exception("No record select from " + testCycleTable.toString() +
	                " Where TestCycleId: " + testCycleTable.getTestCycleId() + ", TestPlan ID: " + testCycleTable.getTestplan().getTestPlanId());
	       
	        }
	        else if (selectedDataList.size() == 0)
	        {
	            //Insert testCycleId in the table.
	           Log.Debug(this.toString() + "/: Insert testCycleId into " + testCycleTable.toString());
	
	            //TestCycle StartDate and EndDate is not useful for selecting entry from TestCycle table.
	            //testCycleTable.StartedDate = testCycleData.StartDate;
	           testCycleTable.setStartedDate(new Date());
	           Log.Debug(this.toString() + "/: StartedDate: " + testCycleTable.getStartedDate());
	           
	           //testCycleTable.ModificationDate = testCycleData.ModificationDate;
	           testCycleTable.setModificationDate(new Date());
	           Log.Debug(this.toString() + "/: ModificationDate: " + testCycleTable.getModificationDate());
	           
	            
	           DatabaseLayer.Testcycle testcycleArray[]={testCycleTable};
	           int rowAffected = _dbOperation.Insert(testcycleArray);
	           
	           if (rowAffected == 0)
	           {
	               throw new Exception("Unable to insert TestCyleId: " + testCycleTable.getTestCycleId() +
	                   ", TestPlanID: " + testCycleTable.getTestplan().getTestPlanId()+ " into testCycle Table");
	               
	           }
	           else
	           {
	              Log.Debug(this.toString() + "/: TestCycleId is successfully inserted in " +
	                   testCycleTable.toString());
	              testcycleID=testCycleTable.getTestCycleId();
	           }
	        }
	        else if (selectedDataList.size() == 1)
	        {
		            Log.Debug(this.toString() + "/: TestCycleId: " + testCycleTable.getTestCycleId() +
		                    ", TestPlanID: " + testCycleTable.getTestplan().getTestPlanId() + " already exist in " + testCycleTable.toString());
		
		            //Update TestCycle result.
		            DatabaseLayer.Testcycle newTestCycleTable = (DatabaseLayer.Testcycle)selectedDataList.get(0);
		           
		            //Its update operation. So only TestExecutionDetailsId is not going to change. Also here assumed that
		            //TestCycleTopologySet_Id and TestCase_Id is not changed.
		           /*testCycleTable.setTestCycleId(oldTestCycleTable.getTestCycleId());
		           testCycleTable.setStartedDate(oldTestCycleTable.getStartedDate());
		           testCycleTable.setModificationDate(new Date());
		           int rowAffected = _dbOperation.Update(testCycleTable, oldTestCycleTable);
		           */
		           
		          //  oldTestCycleTable.setTestCycleId(((Automation.Testcycle)selectedDataList.get(0)).getTestCycleId());
		            newTestCycleTable.setModificationDate(new Date());
		            newTestCycleTable.setExecutionTime(new Integer(testCycleTable.getExecutionTime()));
		            int rowAffected = _dbOperation.Update(newTestCycleTable);
		            if (rowAffected == 0)
		           {
		               Log.Debug(this.toString() + "/: No record updated from " +
		                    testCycleTable.toString());
		           }
		           else
		           {
		               Log.Debug(this.toString() + "/: record successfully updated from "
		                    + testCycleTable.toString());
		               testcycleID=newTestCycleTable.getTestCycleId();
		               
		           }
	         }
	        else if (selectedDataList.size() > 1)
	        {
	            throw new Exception("TestCycleId: " + testCycleTable.getTestCycleId() +
	                " TestPlanID: " + testCycleTable.getTestplan().getTestPlanId() + " is not unique in " + testCycleTable.toString());
	            
	        }
	    }
	    else
	    {
	    	DatabaseLayer.Testplan testplan=new Testplan(testCycleData.testplan_id);
		    testCycleTable.setTestplan(testplan);
		    Log.Debug("TestCycle/: TestPlan ID: " + testCycleTable.getTestplan().getTestPlanId());
	       
		    testCycleTable.setExecutionTime(testCycleData.executionTime);
	        Log.Debug(this.toString() + "/: ExecutionTime: " + testCycleTable.getExecutionTime());
	        
	        testCycleTable.setInitializationTime(testCycleData.initializationTime);
	        Log.Debug(this.toString() + "/: InitializationTime: " + testCycleTable.getInitializationTime());
	    
	        //Insert testCycleId in the table.
	        Log.Debug(this.toString() + "/: Insert testCycleId into " + testCycleTable.toString());
	
	        //TestCycle StartDate and EndDate is not useful for selecting entry from TestCycle table.
	        //testCycleTable.StartedDate = testCycleData.StartDate;
	        testCycleTable.setStartedDate(new Date());
	        Log.Debug(this.toString() + "/: StartedDate: " + testCycleTable.getStartedDate());
	           
	        //testCycleTable.ModificationDate = testCycleData.ModificationDate;
	        testCycleTable.setModificationDate(new Date());
	        Log.Debug(this.toString() + "/: ModificationDate: " + testCycleTable.getModificationDate());
	           
	        DatabaseLayer.Testcycle testcycleArray[]={testCycleTable};
	        int rowAffected = _dbOperation.Insert(testcycleArray);
	           
	        if (rowAffected == 0)
	        {
	               throw new Exception("Unable to insert TestCyleId: " + testCycleTable.getTestCycleId() +
	                   ", TestPlanID: " + testCycleTable.getTestplan().getTestPlanId()+ " into testCycle Table");
	               
	        }
	        else
	        {
	              Log.Debug(this.toString() + "/: TestCycleId is successfully inserted in " +
	                   testCycleTable.toString());
	              
	              testcycleID=testCycleTable.getTestCycleId();
	        }
	    }
	    
	    _testCycleMessage = testcycleID;
    	
        return testcycleID;
	 }
	 
	
	 public boolean TestCycleExist(int TestCycleID) throws Exception
	 {
		 try
		 {
			 Log.Debug(this.toString() + "/TestCycleExist () function called ");
             boolean result = false;
             _transaction=_dbOperation.BeginTransaction(_transaction);
            DatabaseLayer.Testcycle  tcyTable= new DatabaseLayer.Testcycle();
            tcyTable.setTestCycleId(TestCycleID);
            Log.Debug(this.toString() + "/TestCycleExist TestCycleID: " + TestCycleID);
            Log.Debug(this.toString() + "/TestCycleExist : Check TestCycleID entry exist in TestCycle Table or not.");
            
            List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
            selectedDataList = (List<IDataTable>)_dbOperation.Select(tcyTable);
            
            Log.Debug(this.toString() + "/TestCycleExist: Select query executed to check testCycle exist or not.");

            if (selectedDataList == null)
            {
                result = false;
                throw new Exception("No record select from TestCycle Table Where TestCycleID: " + TestCycleID);
            }
            else if (selectedDataList.size() == 0)
            {
                result = false;
            }
            else if (selectedDataList.size() > 0)
            {
                result = true;
            }

            _dbOperation.CommitTransaction(_transaction);
            return result;

		 }
		 catch(Exception e)
		 {
			 _dbOperation.RollBackTransaction(_transaction);
             // return false;
             throw e;
		 }
	 }
	 
	
}
/*
class TestCycleData
{
	 Integer testCycleId;
	 int testplan_id;
	 Date startedDate;
	 Date endDate;
	 Integer initializationTime;
	 Integer executionTime;
	 Date modificationDate;
	 private List<TopologySetResultData> _topologySetResultDataList = new ArrayList<TopologySetResultData>();
	 
	public List<TopologySetResultData> get_topologySetResultDataList() {
		return _topologySetResultDataList;
	}
	public void set_topologySetResultDataList(
			List<TopologySetResultData> setResultDataList) {
		_topologySetResultDataList = setResultDataList;
	}
	public Integer getTestCycleId() {
		return testCycleId;
	}
	public void setTestCycleId(Integer testCycleId) {
		this.testCycleId = testCycleId;
	}
	public int getTestplan_id() {
		return testplan_id;
	}
	public void setTestplan_id(int testplan_id) {
		this.testplan_id = testplan_id;
	}
	public Date getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getInitializationTime() {
		return initializationTime;
	}
	public void setInitializationTime(Integer initializationTime) {
		this.initializationTime = initializationTime;
	}
	public Integer getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(Integer executionTime) {
		this.executionTime = executionTime;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	 
}

class TopologySetResultData
{
    private List<TopologyDetail> _topologyDetailList = new ArrayList<TopologyDetail>();
    
    private List<TestCaseResult> _testCaseResultList = new ArrayList<TestCaseResult>();

	public List<TopologyDetail> get_topologyDetailList() {
		return _topologyDetailList;
	}

	public void set_topologyDetailList(List<TopologyDetail> detailList) {
		_topologyDetailList = detailList;
	}

	public List<TestCaseResult> get_testCaseResultList() {
		return _testCaseResultList;
	}

	public void set_testCaseResultList(List<TestCaseResult> caseResultList) {
		_testCaseResultList = caseResultList;
	}
   
}

class TopologyDetail
{
	private int _roleID;
	private int _machineHostId;
    private int _topologyId;  
    private String _buildNumber;
	
	public int get_roleID() {
		return _roleID;
	}
	public void set_roleID(int _roleid) {
		_roleID = _roleid;
	}
	public int get_machineHostId() {
		return _machineHostId;
	}
	public void set_machineHostId(int hostId) {
		_machineHostId = hostId;
	}
	public int get_topologyId() {
		return _topologyId;
	}
	public void set_topologyId(int id) {
		_topologyId = id;
	}
	public String get_buildNumber() {
		return _buildNumber;
	}
	public void set_buildNumber(String number) {
		_buildNumber = number;
	}
	
	
    
}
// TestCaseReult has executed testcase result information.
class TestCaseResult
{
	 private String _testCaseId;
     private String _testSuiteName;
     private Date _executionDate;
     private String _testEngineerName;
     private String _status;
     private String _comments;
     private String _buildNo;
     private int _testExecution_Time;
     private PerformanceExecutionDetailTable _performanceExecutionDetailTable;
   
	public String get_testCaseId() {
		return _testCaseId;
	}
	public void set_testCaseId(String caseId) {
		_testCaseId = caseId;
	}
	public String get_testSuiteName() {
		return _testSuiteName;
	}
	public void set_testSuiteName(String suiteName) {
		_testSuiteName = suiteName;
	}
	public Date get_executionDate() {
		return _executionDate;
	}
	public void set_executionDate(Date date) {
		_executionDate = date;
	}
	public String get_testEngineerName() {
		return _testEngineerName;
	}
	public void set_testEngineerName(String engineerName) {
		_testEngineerName = engineerName;
	}
	public String get_status() {
		return _status;
	}
	public void set_status(String _status) {
		this._status = _status;
	}
	public String get_comments() {
		return _comments;
	}
	public void set_comments(String _comments) {
		this._comments = _comments;
	}
	
	public String get_buildNo() {
		return _buildNo;
	}
	public void set_buildNo(String no) {
		_buildNo = no;
	}
	public int get_testExecution_Time() {
		return _testExecution_Time;
	}
	public void set_testExecution_Time(int execution_Time) {
		_testExecution_Time = execution_Time;
	}
	public PerformanceExecutionDetailTable get_performanceExecutionDetailTable() {
		return _performanceExecutionDetailTable;
	}
	public void set_performanceExecutionDetailTable(
			PerformanceExecutionDetailTable executionDetailTable) {
		_performanceExecutionDetailTable = executionDetailTable;
	}   
     
}
class PerformanceExecutionDetailTable
{
    private String _performanceExecutionDetailId;
    private int _elapssedMin;
    private int _elapssedMax;
    private float _average;
    private int _95Percentage;
	public String get_performanceExecutionDetailId() {
		return _performanceExecutionDetailId;
	}
	public void set_performanceExecutionDetailId(String executionDetailId) {
		_performanceExecutionDetailId = executionDetailId;
	}
	public int get_elapssedMin() {
		return _elapssedMin;
	}
	public void set_elapssedMin(int min) {
		_elapssedMin = min;
	}
	public int get_elapssedMax() {
		return _elapssedMax;
	}
	public void set_elapssedMax(int max) {
		_elapssedMax = max;
	}
	public float get_average() {
		return _average;
	}
	public void set_average(float _average) {
		this._average = _average;
	}
	public int get_95Percentage() {
		return _95Percentage;
	}
	public void set_95Percentage(int percentage) {
		_95Percentage = percentage;
	}
 }
*/