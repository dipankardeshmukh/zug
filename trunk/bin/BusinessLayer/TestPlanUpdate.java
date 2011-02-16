package BusinessLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import logs.Log;

import org.hibernate.Transaction;

import DatabaseLayer.DBOperations;
import DatabaseLayer.Product;
import DatabaseLayer.Role;
import DatabaseLayer.Testplan;
import DatabaseLayer.DataClasses.IDataTable;

public class TestPlanUpdate
{
	DBOperations  _dbOperation= null;
	Transaction _transaction=null;
	
	
	private int _testSuiteListId;
    private int _topologySetListID;
    public int TopologySetID = 0;

	public TestPlanUpdate()
	{
		 _dbOperation=new DBOperations();
		 
	}
	
	public synchronized void Save(TestPlanUpdateData testPlanUpdateData)throws Exception 
	{
		Log.Debug("TestPlanUpdateData/Save : Save function called");
		if (testPlanUpdateData == null)
            return;
        try
        {
        	_transaction= _dbOperation.BeginTransaction(_transaction);
            Log.Debug("TestPlanUpdate/Dave : Check for TestPlan Entries and get testSuiteList_Id and topologySetList_Id");
            //Sets _testSuiteListId and _topologySetListID
            SetListIds(testPlanUpdateData.testPlanData.getTestPlanId());
           
            // Save TestSuite 
            Log.Debug("TestPlanUpdate/Save : Saving Test Suite");
            //Get TestPlan_Id.
            testPlanUpdateData.TestSuiteData.setProduct_id( Integer.parseInt( testPlanUpdateData.testPlanData.getProduct() ) );
            int testsuiteId = SaveTestSuite(testPlanUpdateData.TestSuiteData);
         
            Log.Debug("TestPlanUpdate/Save : Saved Test Suite.");
            
            if(testPlanUpdateData.TestSuiteData.getProduct_id()!=Integer.parseInt(testPlanUpdateData.testPlanData.getProduct()))
            {
            	Log.Error("TestPlanUpdate/Save : Test Suite and Test Plan do not belong to same product");
            	throw new Exception("Given Test Plan and Test Suite do not belong to same Product");
            }
            
            testPlanUpdateData.TestSuiteListData.testSuiteId = testsuiteId;
           //Save Test suite List entry
            Log.Debug("TestPlanUpdate/Save : Saving Test Suite list");
      
            SaveTestSuiteList(testPlanUpdateData.TestSuiteListData);
         
            
            Log.Debug("TestPlanUpdate/Save : Saved Test plan list");
          
            
            // Save Topology_Id in TopologySet table and get topologySet_Id list.
            Log.Debug("TestPlanUpdate/Save : Saving topology set");
            List<Integer> TopologySetIDList = new ArrayList<Integer>();
            TopologySetIDList = SaveTopologySet(testPlanUpdateData.TopologySets);
           
            if(TopologySetIDList.size() > 0)
                TopologySetID = TopologySetIDList.get(0);

            for (int i = 0; i < TopologySetIDList.size(); i++)
            {
              	testPlanUpdateData.TopologySetListData.get_topologySetIdList().add(TopologySetIDList.get(i));
            	
            }
            Log.Debug("TestPlanUpdate/Save : Saved topology set");
            
           // Save TopologySet_Id list in TopologySetList table and get topologySetList_Id.
            Log.Debug("TestPlanUpdate/Save : Saving topology set list");

            //Set _topologySetListId.
           // SaveTopologySetList(rRTPUpdateData.TopologySetListData);
            SaveTopologySetList(testPlanUpdateData.TopologySetListData, testPlanUpdateData.testPlanData.getTestPlanId());
          
            Log.Debug("TestPlanUpdate/Save : Saved topology set list.");

           // Update TestPlan entry by modifying or inserting new value for TestPlanList and TopologySetList
            Log.Debug("TestPlanUpdate/Save : Updating RRTP Table");
            _dbOperation.CommitTransaction(_transaction);
            _transaction= _dbOperation.BeginTransaction(_transaction);
            
            //UpdateRRTP(rRTPUpdateData.rrtpData.RRTP_Id, _testPlanListId, _topologySetListID);
            UpdateTestPlan(testPlanUpdateData.testPlanData.getTestPlanId(), _testSuiteListId, _topologySetListID);
        
            Log.Debug("TestPlanUpdate/Save : Updated RRTP Table");
     
            _dbOperation.CommitTransaction(_transaction);
                     
        }
        catch(Exception e)
        {
        	 _dbOperation.RollBackTransaction(_transaction);
             Log.Debug(this.toString() + "/Save: Rollback the transaction for saving TestPlanUpdate data.");
             throw e;
        }
	}
	
	public int getRoleID(String roleName) throws Exception
	{
		int roleID = 0;
		try
		{
			DatabaseLayer.Role role = new DatabaseLayer.Role();
			role.setRoleName(roleName);
			List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			selectedDataList = (List<IDataTable>)_dbOperation.Select(role);
			
			if (selectedDataList == null)
            {
                throw new Exception("No record select from Role Table Where RoleName: " + roleName);
            }
            else if (selectedDataList.size() == 0)
            {
                throw new Exception("Given Role " + roleName + " does not exist in Role table");
            }
            else if (selectedDataList.size() > 0)
            { 
           	 
	           	 if((((DatabaseLayer.Role)selectedDataList.get(0)).getRoleId())!=0)
	           	 {
	           	       //Set role id.
	           		 roleID = ((DatabaseLayer.Role)selectedDataList.get(0)).getRoleId();           		 
	           	 }
			}
		}
		catch(Exception e)
		{
			throw e;
		}
			return roleID;// Integer.toString(roleID);
	}
	 // Get TopologySetList and TestsuiteList id.
	 public void SetListIds(int testPlanId) throws Exception
	 {
		 if(testPlanId!=0)
		 {
			 try
             {
				 DatabaseLayer.Testplan testPlanTable= new DatabaseLayer.Testplan();
				 Log.Debug(this.toString() + "/TestPlanIdExist: TestPlanIdExist() function called ");
				 testPlanTable.setTestPlanId(testPlanId);
				 Log.Debug(this.toString() + "/TestPlanIdExist TestPlanId: " + testPlanId);
				 
				 //Check TestPlanID entry exist in table.
                 Log.Debug(this.toString() + "/TestPlanIdExist : Check TestPlanId entry exist in TestPlan Table or not.");
                 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
                 selectedDataList = (List<IDataTable>)_dbOperation.Select(testPlanTable);
                 Log.Debug(this.toString() + "/TestPlanIdExist: Select query executed to check TestPlanId exist or not.");

                 if (selectedDataList == null)
                 {
                     throw new Exception("No record select from RRTP Table Where RRTPId: " + testPlanId);
                 }
                 else if (selectedDataList.size() == 0)
                 {
                     throw new Exception("Given TestPlanId " + testPlanId + " does not exist in TestPlan table");
                 }
                 else if (selectedDataList.size() > 0)
                 { 
                	 
                	 if((((DatabaseLayer.Testplan)selectedDataList.get(0)).getTestSuiteListId())!=0)
                	 {
                	       //Set TestPlanList id.
                		 _testSuiteListId=((DatabaseLayer.Testplan)selectedDataList.get(0)).getTestSuiteListId();
                		 
                	 }
                	 if((((DatabaseLayer.Testplan)selectedDataList.get(0)).getTopologySetListId())!=0)
                	 {
                		 //Set TopologySetList id.
                		 _topologySetListID =((DatabaseLayer.Testplan)selectedDataList.get(0)).getTopologySetListId();
                	 }
                 }
             }
			 catch(Exception e)
			 {
				 throw e;
			 }
		 }
		 else
         {
             throw new Exception("TestPlanId is blank");
         }
	 }
	
	 private int SaveTestSuite(TestPlanTestSuiteData testSuiteData) throws Exception
	 {
		 if (testSuiteData == null)
             return 0;
         try
         {
        	 DatabaseLayer.Testsuite testSuiteTable= new  DatabaseLayer.Testsuite();
        	 testSuiteTable.setTestSuiteName(testSuiteData.testSuiteName);
        	 //testSuiteTable.setProduct(testSuiteData.getProduct_id());
        	 testSuiteTable.setProduct(new Product(testSuiteData.getProduct_id()));
        	 
        	 Log.Debug("TestSuite/Save : TestSuiteName = " + testSuiteTable.getTestSuiteName());
             Log.Debug("TestSuite/Save : Check whether TestSuite Name exists in TestSuite Table");
             List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
             selectedDataList = (List<IDataTable>)_dbOperation.Select(testSuiteTable);
             Log.Debug("TestSuite/Save : Select query executed to check does TestSuite entry already exist in database");
             
             if (selectedDataList == null)
             {
                 throw new Exception("No Records are selected from " + testSuiteTable.toString() +
                     " Where TestSuiteId =" + testSuiteTable.getTestSuiteId());
             }
             else if (selectedDataList.size() == 0)
             {
            	 if(testSuiteData.testSuiteId!=null)
            	 {
            		 testSuiteTable.setTestSuiteId(testSuiteData.testSuiteId);
            	 }
            	 DatabaseLayer.Role role= new Role(getRoleID(testSuiteData.getRoleName()));
            	 //testSuiteTable.setRole(testSuiteData.role_id);
            	 
            	 DatabaseLayer.Product product= new Product(testSuiteData.getProduct_id());
            	 //Integer product= testSuiteData.getProduct_id();
            	 testSuiteTable.setProduct(product);
 
            	 testSuiteTable.setRole(role);//getRoleID(testSuiteData.getRoleName()));//.role.setRoleId(testSuiteData.role_id);
            	 testSuiteTable.setTestSuiteName(testSuiteData.testSuiteName);
            	 testSuiteTable.setComment(testSuiteData.comment);
            	 testSuiteTable.setCreationDate(testSuiteData.creationDate);
            	 testSuiteTable.setModificationDate(testSuiteData.modificationDate);
            	 testSuiteTable.setCreatedBy(testSuiteData.createdBy);
            	 testSuiteTable.setModifiedBy(testSuiteData.modifiedBy);
            	 testSuiteTable.setStatus(testSuiteData.status);
            	 testSuiteTable.setElapsedTimeMin(testSuiteData.elapsedTimeMin);
            	 testSuiteTable.setElapsedTimeMax(testSuiteData.elapsedTimeMax);
            	 testSuiteTable.setTestSuiteFilePath(testSuiteData.testPlanFilePath);
            	 testSuiteTable.setInitializationTime(testSuiteData.initializationTime);
            	 testSuiteTable.setExecutionTime(testSuiteData.executionTime);
            	 
            	 DatabaseLayer.Testsuite array[]={testSuiteTable};
            	 int rowsAffected = _dbOperation.Insert(array);
            	 
            	 if (rowsAffected == 0)
                     throw new Exception("Unable To Insert Data inTo Table TestSuite");
                 else
                 {
                     Log.Debug("TestSuite/Save : TestSuiteId " + testSuiteTable.getTestSuiteId()+
                         "is successfuly inserted into database");
                 }
             }
             
             else if (selectedDataList.size() > 0)
             {
            	 ((DatabaseLayer.Testsuite)selectedDataList.get(0)).AssignTo(testSuiteTable);
            	 
            	 testSuiteTable.setModificationDate(new Date());
            	 
            	             	 
            	// int rowsAffected = _dbOperation.Update(testSuiteTable, ((Automation.Testsuite)selectedDataList.get(0)));
            	 
            	 //int rowsAffected = _dbOperation.Update(testSuiteTable);
            	 int rowsAffected = _dbOperation.merge(testSuiteTable);
            	 if (rowsAffected == 0)
                     throw new Exception("Unable to update Data from TestSuite.");
                 else
                 {
                     Log.Debug("TestSuite/Save : TestPlan_Id " + testSuiteTable.getTestSuiteId() +
                         " is already present in Testsuite table");
                 }
             }
             
             return testSuiteTable.getTestSuiteId();
         }
         catch(Exception e)
         {
        	 Log.Debug("TestSuite/Save : Exception in save function, Rollback Transaction ");
             throw e;
         }
		 
	 }
	 
	 private synchronized void SaveTestSuiteList(TestPlanTestSuiteListData testSuiteListData) throws Exception
	 {
		 Log.Debug(this.toString() + "/SaveTestSuiteList: GetTestSuiteListId");
		 if(_testSuiteListId!=0)
		 {
			 testSuiteListData.setTestSuiteListId(_testSuiteListId);
		 }
		 SaveTestSuiteListEntry(testSuiteListData);
		
	 }
	
	 private synchronized int GetTestSuiteListId(TestPlanTestSuiteListData testSuiteListData) throws Exception
	 {
		Log.Debug(this.toString() + "/GetTestSuiteListId: GetTestSuiteListId() called.");
		
		//First here check ,previously any testsuitelist is exist with same testsuite and containing only test suite.
		if(_testSuiteListId==0)
		{
				DatabaseLayer.TestsuiteTestsuitelistXref selecteTestPlanListIds=new DatabaseLayer.TestsuiteTestsuitelistXref();
				selecteTestPlanListIds.setTestsuiteId(testSuiteListData.testSuiteId);

				Log.Debug(this.toString() + "/GetTestSuiteListId: Select TestSuiteListId for given TestSuiteId: " +
						selecteTestPlanListIds.getTestsuitelistId());
		         //Get all TestPlanListId for given TestPlanId.
				 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
				 selectedDataList = (List<IDataTable>)_dbOperation.Select(selecteTestPlanListIds);
				 if (selectedDataList == null)
                 {
                     throw new Exception("Unable to selecte TestPlanListId for TestPlan_Id:" +
                           selecteTestPlanListIds.getTestsuitelistId());
                 }
				 else if (selectedDataList.size()> 0)
				 {
					 //Here we got list of  testSuitelist rows  which contains given test plan in it
					 for (int i = 0; i < selectedDataList.size(); i++)
                     {
                         //Get list of TestPlan_Id for each TestPlanListId.
						 DatabaseLayer.TestsuiteTestsuitelistXref selectTestSuite= new DatabaseLayer.TestsuiteTestsuitelistXref();
						 selectTestSuite.setTestsuitelistId(((DatabaseLayer.Testsuitelist)selectedDataList.get(0)).getTestSuiteListId());

						 Log.Debug(this.toString() + "/GetTestSuiteListId: Select TestSuiteIds for given TestSuiteList_Id: " +
	                                selectTestSuite.getTestsuitelistId());
					     //check whether this testSuitelistId have only one testSuite it it.
						 List<IDataTable> dataList = new ArrayList<IDataTable>();
						 dataList = (List<IDataTable>)_dbOperation.Select(selectTestSuite);
						  if (dataList.size() == 1)
						  {
							//TestSuiteListId exist for given TestsuiteId. So we can reuse it.

                              Log.Debug(this.toString() + "/GetTestSuiteListId: TestSuiteListId: " +
                                 ((DatabaseLayer.TestsuiteTestsuitelistXref)selectedDataList.get(i)).getTestsuitelistId()
                            		  + " can be reuse.");
                              return  ((DatabaseLayer.TestsuiteTestsuitelistXref)selectedDataList.get(i)).getTestsuitelistId();
 						  }
						  
                     	}
				 }
				 
		}

        //No TestPlanListId found for given TestPlanId.                
        return 0;
	 }
	
	 private synchronized void SaveTestSuiteListEntry(TestPlanTestSuiteListData testSuiteListData) throws Exception
	 {
		  Log.Debug("TestPlanUpdate/SaveTestPlanList : SaveTestPlanListEntry function called");
		  if (testSuiteListData == null)
              return;
		  try
          {
			  DatabaseLayer.TestsuiteTestsuitelistXref testSuiteListtable= new DatabaseLayer.TestsuiteTestsuitelistXref();
			  //TestsuitelistId id = new TestsuitelistId();
			  testSuiteListtable.setTestsuitelistId(testSuiteListData.testSuiteListId);
			  testSuiteListtable.setTestsuiteId(testSuiteListData.testSuiteId);
			 // testSuiteListtable.setId(id);
			  
			  //testSuiteListtable.id.setTestSuiteListId(testSuiteListData.testSuiteListId);
			  //testSuiteListtable.id.setTestSuiteId(testSuiteListData.testSuiteId);
			 
			  Log.Debug("TestPlanUpdate/SaveTestPlanListEntry : TestSuiteListId = " + testSuiteListtable.getTestsuitelistId());
			  Log.Debug("TestPlanUpdate/SaveTestPlanListEntry : Check whether TestSuiteListId Is exist in TestSuiteList Table");
			  
			  List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			  selectedDataList = (List<IDataTable>)_dbOperation.Select(testSuiteListtable);
			  Log.Debug("TestPlanUpdate/SaveTestPlanListEntry : Select query executed to check testsuiteList entry is already exist in database");
			  
			  if (selectedDataList == null)
              {
                  throw new Exception("No Records are selected from " + testSuiteListtable.toString() +
                      " Where TestSuiteListId =" + testSuiteListtable.getTestsuitelistId()+ " AND " +
                      "TestSuite_Id=" + testSuiteListtable.getTestsuiteId());
              }

              if (selectedDataList.size() == 0)
              {
            	  DatabaseLayer.TestsuiteTestsuitelistXref array[] = {testSuiteListtable};
            	  int rowsAffected = _dbOperation.Insert(array);
            	  if (rowsAffected == 0)
                      throw new Exception("Unable To Insert Data InTo Table TestSuiteList");
                  else
                  {
                      Log.Debug("TestPlanUpdate/SaveTestSuiteListEntry : TestSuiteListId " + testSuiteListtable.getTestsuitelistId()+
                          "is successfuly inserted into database");
                      
                  }
              }
              else
              {
                  Log.Debug("TestPlanUpdate/SaveTestSuiteListEntry :TestsuiteListId :" + testSuiteListtable.getTestsuitelistId()+
                      " is already present in TestPlanlist table");
              }
               
          }
		  catch(Exception e)
		  {
			  
			  Log.Error("TestPlanUpdate/SaveTestSuiteListEntry :Exception in save function, Rollback Transaction ");
              throw new Exception("TestPlanUpdate/SaveTestSuiteListEntry :Exception in save function, Rollback Transaction. Exception : " + e.toString());
		  
		  }
	 }

	// Returns TopologySetIdList which contains topologySet_Ids.
	 private synchronized List<Integer>SaveTopologySet(List<TestPlanTopologySetData> topologySets) throws Exception
	 {
		 if (topologySets == null)
             return null;
         try
         {
             List<Integer> topologySetIdList = new ArrayList<Integer>();
             String whereClause = "";
             //Check if topologySet_Id exists in TopologySet table for given topology_id list.
             for (int i = 0; i < topologySets.size(); i++)
             {
                 DatabaseLayer.TopologyTopologysetXref topologySetXrefTable= new  DatabaseLayer.TopologyTopologysetXref ();
                 for (int j = 0; j <topologySets.get(i).get_topologyList().size(); j++)
                 {
                	 //Create Where clause for finding same topology set which contains same topology.
                     //Create where clause as follows:

                     // topology_id=1 And
                     // topologyset_id in (SELECT topologyset_id From TopologySet Where topology_id=Topo2) And
                     // topologyset_id in (SELECT topologyset_id From TopologySet Where topology_id=Topo4)                
                	 if (j == 0)
                     {
                		 whereClause= "idatatable.topologyId="+(topologySets.get(i)).get_topologyList().get(j)+ " ";;
                		 //whereClause = "topology.topologyId =" + (topologySets.get(i)).get_topologyList().get(j)+ " ";
                     }
                	 else
                     {
                		 whereClause += "and idatatable.topologysetId in (select idatatable"+j+".topologysetId "+
                		 "from TopologyTopologysetXref idatatable"+j+" where idatatable"+j+".topologyId= "+(topologySets.get(i)).get_topologyList().get(j)+")";
                		 
                		 //whereClause += " AND topologySetId in (SELECT topologySetId "+
                		 //"From Topologyset Where topology.topologyId= "+(topologySets.get(i)).get_topologyList().get(j)+" "; 
                	 }
                	 
                 }
                 Log.Debug("TestPlanUpdate/Whereclause to select TopologySet Id: " + whereClause);

                 //Find topologySet for given topologies.
                 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
                 selectedDataList = (List<IDataTable>)_dbOperation.Select(topologySetXrefTable,
                         whereClause);
                 
                 boolean generateNextTopologySetId = false;
                 if (selectedDataList == null)
                 {
                     throw new Exception("No Records are selected from " + topologySetXrefTable.toString());
                 }
                 else if (selectedDataList.size() > 0)
                 {
                	 //Now check selected TopologySet having equal number of topologies as that of required.
                	 for (int j = 0; j < selectedDataList.size(); j++)
                     {
                        DatabaseLayer.TopologyTopologysetXref selectedTopologyXrefSetTable = new  DatabaseLayer.TopologyTopologysetXref();
                        //Object[] row= ()selectedDataList.get(j);
                        
                        selectedTopologyXrefSetTable.setTopologysetId(((DatabaseLayer.TopologyTopologysetXref)selectedDataList.get(j)).getTopologysetId());
                        //DatabaseLayer.Topologyset set=(DatabaseLayer.Topologyset)(selectedDataList.get(j));
                        //int id=set.getTopologySetId();
                        //selectedTopologySetTable.setTopologySetId(id);
                        List<IDataTable> selectedTopologySet = new ArrayList<IDataTable>();
                        selectedTopologySet = (List<IDataTable>)_dbOperation.Select(
                        		selectedTopologyXrefSetTable);
                        if (selectedTopologySet.size() == topologySets.get(i).get_topologyList().size())
                        {
                        	//Selected TopologySet having equal number of topologies. So we can reuse the TopologySet_Id.
                        	//Collection of topologyset_Id in TopologySet table. Table contains TopologySet_Id with
                            //topology_id.
                        	topologySets.get(i).set_topologySetID( ((DatabaseLayer.TopologyTopologysetXref)selectedTopologySet.get(j)).getTopologysetId());
                        	 //Collection of topologyset_Id for TopologySetList table.
                        	 topologySetIdList.add((topologySets.get(i)).get_topologySetID());
                        	 generateNextTopologySetId = false;
                             break;
                        }
                        else
                        {
                            //For given topologies, TopologySet does not exist in TopologySetTable.
                            generateNextTopologySetId = true;
                        }
                     }
                 }
                 else
                 {
                     //No entry exist for given topologies (i.e. TopologySet) in TopologySetTable.
                     generateNextTopologySetId = true;
                 }

                 if (generateNextTopologySetId)
                 {
                     //If topologySet_Id not exist then create new topologySet_Id.
                   //  string topologySetId = _dbOperation.GetNextId(typeof(TopologySetTable));
                    // topologySets[i].TopologySetID = topologySetId;
                     //topologySetIdList.Add(topologySetId);
                 }
                 
             }//end Check topologySet_Id exists in TopologySet table for given topology_id list.
             
             //To save in TopologySet table.
             for (int i = 0; i < topologySets.size(); i++)
             {
                 for (int j = 0; j < topologySets.get(i).get_topologyList().size(); j++)
                 {
                	 DatabaseLayer.TopologyTopologysetXref topologySetXrefTable = new DatabaseLayer.TopologyTopologysetXref();
                     //Get TopologySet_Id.
                	 if(topologySets.get(i).get_topologySetID()!=0 )
                	 {
                	 topologySetXrefTable.setTopologysetId(topologySets.get(i).get_topologySetID());
                	 Log.Debug("TestPlanUpdate/SaveTopologySet : TopologySetId = " + topologySets.get(i).get_topologySetID() +
                             " , TopologyID = " + topologySets.get(i).get_topologyList().get(j));
                	 }
                	 DatabaseLayer.Topology topology= new DatabaseLayer.Topology();
                	 int topologyId= topologySets.get(i).get_topologyList().get(j);
                	 topology.setTopologyId(topologyId);
                	 topologySetXrefTable.setTopologyId(topologyId);
                	 
                	 Log.Debug("TestPlanUpdate/SaveTopologySet : Check whether TopologySetID exists in TopologySet Table");

                         //To Check TopologySet has Topology entry.
                     
                     List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
                     selectedDataList = (List<IDataTable>)_dbOperation.Select(topologySetXrefTable);
                     
                     Log.Debug("TestPlanUpdate/SaveTopologySet :" +
                     " Select query executed to check topologyID entry is already exist in database");
                     if (selectedDataList == null)
                     {
                         throw new Exception("No Records are selected from " + topologySetXrefTable.toString());
                     }
                     else if (selectedDataList.size() == 0)
                     {
                    	 DatabaseLayer.TopologyTopologysetXref array[]={topologySetXrefTable};
                    	 int rowsAffected = _dbOperation.Insert(array);
                    	 
                    	 if (rowsAffected == 0)
                             throw new Exception("Unable To Insert Data InTo Table TopologySetTable");
                         else
                         {
                             Log.Debug("TestPlanUpdate/SaveTopologySet :  TopologySetID " + topologySetXrefTable.getTopologysetId()
                                 + "is successfuly inserted into database");
                         }
                    	 
                    	 //adding topologysetID into  topologySetIdList which is just inserted
                    	 
                         topologySetIdList.add(topologySetXrefTable.getTopologysetId());
                    	 
                     }
                     else if (selectedDataList.size() > 0)
                     {
                         Log.Debug("TestPlanUpdate/SaveTopologySet : TopologySetID " + topologySetXrefTable.getTopologysetId() +
                             "TopologyID=" + topologySetXrefTable.getTopologyId() + " is already present in TopologySet table");
                     }
                 }//for
             }//for
             return topologySetIdList;
         }
         catch(Exception e)
         {
        	 Log.Debug("TestPlanUpdate/SaveTopologySet: Exception in function." + e.getMessage());
             throw e;
         }
 }
	
	// Set _topologySetListID.
	 private synchronized void SaveTopologySetList(TestPlanTopologySetListData testPlanTopologySetListData, int testPlanId) throws Exception
	 {
		  if (testPlanTopologySetListData == null)
              return;
		  try
		  {
			  //Save topologySetListId with list of TopologySetID in TopologySetListTable if not exist.
			  SaveTopologySetListEntries(_topologySetListID, testPlanTopologySetListData, testPlanId);
			  //todo
		  }
		  catch(Exception e)
		  {
			  Log.Debug("TestPlanUpdate/SaveTopologySetList: Exception in function." + e.getMessage());
              throw e;
		  }
         
	 }
	 
	// Get topologySetListId from TopologySetList table for given list of TopologySet_Ids.
	 private int GetTopologySetListId(TestPlanTopologySetListData testPlanTopologySetListData) throws Exception
	 {
		 String whereClause = "";

		 //check topologySetList_Id is exist from TopologySetList Table for given topologySet_Id list.
		 Log.Debug(this.toString() + "/GetTopologySetListId: Function called.");
		 
		 DatabaseLayer.TopologysetTopologysetlistXref topologySetListXrefTable= new DatabaseLayer.TopologysetTopologysetlistXref ();
		 
		 for (int i = 0; i < testPlanTopologySetListData.get_topologySetIdList().size(); i++)
         {
               //To select topologySetList_Id for given toplogySet_Ids.
               //Where clause to get is as follows:
               // topology_id='1' And
               // topologyset_id in (SELECT topologyset_id From TopologySet Where topology_id='Topo2') And
               // topologyset_id in (SELECT topologyset_id From TopologySet Where topology_id='Topo4')                
			 if (i == 0)
             {
				 whereClause= " Topologyset.topologySetId= "+ testPlanTopologySetListData.get_topologySetIdList().get(i)+" ";
			 }
			 else
             {
				 whereClause += " AND Topologysetlist.topologySetListId in (SELECT Topologysetlist.topologySetListId From Automation.Topologysetlist "+
				 " Where  Topologysetlist.topologySetListId= "+ testPlanTopologySetListData.get_topologySetIdList().get(i);
               
             }
         
         }
		 Log.Debug(this.toString() + "/GetTopologySetListId: Where Clause to select TopologySetListId: " +
	                whereClause);
	      //Find topologySetListId for given TopologySetIds.
		 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
		 
		 selectedDataList = (List<IDataTable>)_dbOperation.Select(topologySetListXrefTable,
	                whereClause);

         boolean generateNextTopologySetListId = false;
         if (selectedDataList == null)
         {
             throw new Exception("No Records are selected from " + topologySetListXrefTable.toString());
         }
         else if (selectedDataList.size() > 0)
         {
             Log.Debug(this.toString() + "/GetTopologySetListId: Check selected TopologySetListId having equal number " +
                 "of TopologySetId.");
             //Select query will return topologySetList_Id for given list of topolgySet_Id.

             //Now check selected TopologySetListId having equal number of TopologySetId.
             for (int i = 0; i < selectedDataList.size(); i++)
             {
            	 DatabaseLayer.TopologysetTopologysetlistXref selectedTopologySetListXrefTable= new  DatabaseLayer.TopologysetTopologysetlistXref();
            	 selectedTopologySetListXrefTable.setTopologysetlistId((( DatabaseLayer.TopologysetTopologysetlistXref)selectedDataList.get(i)).getTopologysetlistId());
                 
            	 //Get list of TopologySetId for TopologySetListID.
            	  Log.Debug(this.toString() + "/GetTopologySetListId: Get list of TopologySetId for TopologySetListID: " +
                           selectedTopologySetListXrefTable.getTopologysetlistId());
            	  List<IDataTable> selectedTopologySetList = new ArrayList<IDataTable>();
            	  selectedTopologySetList = (List<IDataTable>)_dbOperation.Select(
                          selectedTopologySetListXrefTable);
            	  
            	  if (selectedTopologySetList.size() == testPlanTopologySetListData.get_topologySetIdList().size())
                  {
                      //Selected TopologySetListId having required list of topologySetId.
                      //So we can reuse the TopologySetListId.
            		  testPlanTopologySetListData.set_topologySetListID(((DatabaseLayer.Topologysetlist)selectedDataList.get(i)).getTopologySetListId());
            		  Log.Debug(this.toString() + "/GetTopologySetListId: TopologySetListID: " +
                      testPlanTopologySetListData.get_topologySetListID()+ " can reuse.");
            		  generateNextTopologySetListId = false;
            		  break;
            		  
                  }
            	  else
                  {
                      //For given list of TopologySetId, no topologySetList entry exist.
                      generateNextTopologySetListId = true;
                  }
             }//for
             
         }
         else
         {
             //No topologySetListId exist in TopologySetList table for given list of topologySetid.
             generateNextTopologySetListId = true;
         }
        /* if (generateNextTopologySetListId)
         {
             //Create new topologySetList_Id                
             rrtpTopologySetListData.TopologySetListID = _dbOperation.GetNextId(typeof(TopologySetListTable));
             Log.Debug(this.ToString() + "/GetTopologySetListId: No TopologySetListID found for given List of TopologySetID."
                 + "Generated TopologySetListID: " + rrtpTopologySetListData.TopologySetListID);
         }
         */
		 
         return testPlanTopologySetListData.get_topologySetListID();
	 }

	// Save the entries in TopologySetList table if enrty not exist.
	 private synchronized void SaveTopologySetListEntries(int topologySetListId, TestPlanTopologySetListData testPlanTopologySetListData, int testplanid) throws Exception
	 {
		  for (int i = 0; i < testPlanTopologySetListData.get_topologySetIdList().size(); i++)
          {
			  DatabaseLayer.TopologysetTopologysetlistXref topologySetListXrefTable = new DatabaseLayer.TopologysetTopologysetlistXref();
			  if(topologySetListId!=0)
			  {
				  topologySetListXrefTable.setTopologysetlistId(topologySetListId);
			  }
			  
			 DatabaseLayer.Topologyset topologyset= new DatabaseLayer.Topologyset();
			 topologyset.setTopologySetId(testPlanTopologySetListData.get_topologySetIdList().get(i));
			 topologySetListXrefTable.setTopologysetId(testPlanTopologySetListData.get_topologySetIdList().get(i));
			  
			 Log.Debug("TestPlanUpdate/SaveTopologySetListEntries : TopologySetList_Id = "
					  + topologySetListXrefTable.getTopologysetlistId()
	                  + " , TopologySetID = " + topologySetListXrefTable.getTopologysetId());
			 
	         Log.Debug("TestPlanUpdate/SaveTopologySetListEntries : " +
	                    "Check whether TopologySetListID exists in TopologySet Table");
	         
	          List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
	          selectedDataList = (List<IDataTable>)_dbOperation.Select(topologySetListXrefTable);

	           Log.Debug("TestPlanUpdate/SaveTopologySetList : " +
               "Select query executed to check topologysetListID entry is already exist in database");

	           if (selectedDataList == null)
	           {
	        	   throw new Exception("No Records are selected from " + topologySetListXrefTable.toString());
	           }
	           else if (selectedDataList.size() == 0)
	           {
	           
	        	   DatabaseLayer.TopologysetTopologysetlistXref array[]={topologySetListXrefTable};
	        	   int rowsAffected = _dbOperation.Insert(array);

                   if (rowsAffected == 0)
                       throw new Exception("Unable To Insert Data InTo Table TopologySetList ");
                   else
                   {
                       Log.Debug("TestPlanUpdate/SaveTopologySetList :  TopologySetList_Id " +
                    		   topologySetListXrefTable.getTopologysetlistId() + "is successfuly inserted into database");

                   }//rows affected are greater than zero
	           }
	           else if (selectedDataList.size() > 0)
               {
                   Log.Debug("TestPlanUpdate/SaveTopologySetList : TopologySetList_Id " +
                		   topologySetListXrefTable.getTopologysetlistId()+ " and TopologySetID: " + topologySetListXrefTable.getTopologysetId()+
                       " is already present in TopologySetList table.");
               }
	           			  
          }
	 }

	 private synchronized void UpdateTestPlan(int testPlanID, int TestSuiteListID, int TopologySetListID) throws Exception
	 {
		   try
           {
			   DatabaseLayer.Testplan testPlanTable= new  DatabaseLayer.Testplan();
			   testPlanTable.setTestPlanId(testPlanID);

               List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
               selectedDataList = (List<IDataTable>)_dbOperation.Select(testPlanTable);
               
               if (selectedDataList.size() > 0)
               {
            	   if(TestSuiteListID!=((DatabaseLayer.Testplan)selectedDataList.get(0)).getTestSuiteListId() ||
            			   TopologySetListID!=  ((DatabaseLayer.Testplan)selectedDataList.get(0)).getTopologySetListId() )
            	   {
            		 //If testPlanList_Id and topologySetList_Id is newly created then update testPlan.
            		   
            		   DatabaseLayer.Testplan newTestPlanTable= new DatabaseLayer.Testplan();
            		  // selectedDataList.get(0).AssignTo(newTestPlanTable);
            		   newTestPlanTable= (Testplan) selectedDataList.get(0);
            		   newTestPlanTable.setTestSuiteListId(TestSuiteListID);
            		   newTestPlanTable.setTopologySetListId(TopologySetListID);
            		   newTestPlanTable.setModificationDate(new Date());
            		   int rowsaffected = _dbOperation.merge(newTestPlanTable);
            		  // int rowsaffected = _dbOperation.Update(newTestPlanTable, (Automation.Testplan)selectedDataList.get(0));
            		   if (rowsaffected > 0)
                       {
                           Log.Debug("TestPlanUpdate/UpdateTestPlan : Test Plan table is updated ");
                       }
                       else
                       {
                           Log.Error("TestPlanUpdate/UpdateTestPlan : Test Plan table is not updated ");
                       }
            	   }
               }
               else
               {
                   throw new Exception("TestPlanID " + testPlanID + " is not present in database");
               }
               
               
           }
		   catch(Exception e)
		   {  
			   Log.Error("RRTPUpdate/RRTPUpdate : Exception in updateing RRTP table for RRTPID " + testPlanID +
                   " message" + e.getMessage());
          
           throw e;
			   
		   }
	 }
	 
}