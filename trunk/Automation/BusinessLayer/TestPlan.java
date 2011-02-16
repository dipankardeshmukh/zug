package BusinessLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logs.Log;

import org.hibernate.Session;
import org.hibernate.Transaction;

import DatabaseLayer.DBOperations;
import DatabaseLayer.Testplan;
import DatabaseLayer.DataClasses.IDataTable;

public class TestPlan {

	DBOperations  _dbOperation= null;
	Transaction _transaction=null;
	
	
	
	public TestPlan()
	{
		_dbOperation=new DBOperations();
	}
	
	
	public synchronized void  save(TestPlanData testPlanData)throws Exception
	{
		if(testPlanData==null)
			return;
		
		if(testPlanData.testPlanId==0)
		{
			Log.Error("TestPlan/Save : TestPlanId is blank");
			throw new Exception("TestPlan Id is blank");
		}
		
		try
		{
			
			DatabaseLayer.Testplan testPlan= new DatabaseLayer.Testplan(); 
			
			_transaction=_dbOperation.BeginTransaction(_transaction);
			
				testPlan.setTestPlanId(testPlanData.testPlanId);
				Log.Debug("TestPlan/Save : TestplanID="+testPlanData.testPlanId);
			
				Log.Debug("TestPlan/Save : Check whether TestplanIDIs exists in Testplan Table");
				List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
				selectedDataList = 	(List<IDataTable>)_dbOperation.Select(testPlan);
				
				Log.Debug("TestPlan/Save : Select query executed to check Testplan entry is already exist in database");
				if (selectedDataList == null)
	            {
	                throw new Exception("No Records are selected from " + testPlan.getClass().getName().toString() + " Where TestplanID =" + testPlan.getTestPlanId());
	            }
				 else if (selectedDataList.size() == 0)
	             {
					 testPlan.setTestPlanId(testPlanData.testPlanId);
					
					 DatabaseLayer.Testplantemplate testplantemplate= new  DatabaseLayer.Testplantemplate(testPlanData.testplantemplate_id);
					 testPlan.setTestplantemplate(testplantemplate);
					 
					 // DatabaseLayer.Topologyset topologyset= new DatabaseLayer.Topologyset(testPlanData.topologyset_id);
					 // testPlan.setTopologySetListId(topologyset);
					 
					// testPlan.testplantemplate.setTemplateId(testPlanData.testplantemplate_id);
					// testPlan.topologyset.setTopologySetId(testPlanData.topologyset_id);
					 
					// testPlan.setTestplantemplate(testPlanData.testplantemplate);
					 //testPlan.setTopologyset(testPlanData.topologyset);
					 testPlan.setTestSuiteListId(testPlanData.testSuiteListId);
					 testPlan.setCreationDate(testPlanData.creationDate);
					 testPlan.setModificationDate(testPlanData.modificationDate);
					 testPlan.setCreatedBy(testPlanData.createdBy);
					 testPlan.setModifiedBy(testPlanData.modifiedBy);
					 testPlan.setStatus(testPlanData.status);
					 //testPlan.setProduct(testPlanData.product);
					 testPlan.setComment(testPlanData.comment);
					 testPlan.setTopologySetListId(testPlanData.topologySetListId);
					 testPlan.setCodeName(testPlanData.codeName);
					 
					 Testplan testplanarray[]={testPlan};
					 int rowsAffected = _dbOperation.Insert(testplanarray);
					 
					 
					 if (rowsAffected == 0)
	                     throw new Exception("Unable To Insert Data InTo Table Testplan");
					 
					 else
	                 {
	                     Log.Debug("TestPlan/Save : Testplan ID " + testPlan.getTestPlanId() + "is successfuly inserted into database");
	                     _dbOperation.CommitTransaction(_transaction);
	                 }
	                     
	             }//rows affected are greater than zero
				 else if (selectedDataList.size() > 0)
	             {
	                  Log.Debug("TestPlan/Save : Testplan ID " + testPlan.getTestPlanId() + " is already present in Testplan table");
	                  _dbOperation.RollBackTransaction(_transaction);
	                  throw new Exception("Testplan ID " + testPlan.getTestPlanId() + " is already exixst");
	
	             }
			
        }
		catch(Exception ee)
		{
			_dbOperation.RollBackTransaction(_transaction);
            Log.Debug("TestPlan/Save : Exception in save function, Rollback Transaction ");
            Log.Error("TestPlan//Save :Exception \r\n" + ee.getMessage());
            throw ee;
		}
		
	}
	
	// check whether particular entry is exist into database 
    // If RRTP ID is exist into table RRTP then it will return true ,else it will return false
	
	public boolean TestplanIDExist(Integer testplanID) throws Exception
	{
		boolean result = false;
		try
		{
			
			_transaction=_dbOperation.BeginTransaction(_transaction);
            Testplan testPlan= new Testplan(); 
            Log.Debug(this.toString()+"/TestplanIDExist: TestplanIDExist() function called");
            testPlan.setTestPlanId(testplanID);
            Log.Debug(this.toString()+"/TestplanIDExist Testplan ID:"+testplanID);
            
          //Check Testplan ID entry exist in table.
            Log.Debug(this.toString() + "/TestplanIDExist : Check testplanID entry exist in Testplan Table or not.");
             List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
            selectedDataList =	 (List<IDataTable>)_dbOperation.Select(testPlan);
            Log.Debug(this.toString() + "/TestplanIDExist: Select query executed to check TestplanID exist or not.");
           
            if (selectedDataList == null)
            {
                result = false;
                throw new Exception("No record select from Testplan Table Where Testplan Id: " + testplanID);
            }
            else if (selectedDataList.size() == 0)
            {
                result = false;
            }
            else if (selectedDataList.size()> 0)
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
	
	public Testplan getTestplan (Integer testplanid) {
		try {
			Session sess = _dbOperation.getSess();
			_transaction=_dbOperation.BeginTransaction(_transaction);
			Testplan tp = (Testplan) _dbOperation.Select(new Testplan(), "testplan_id="+testplanid.toString()).get(0);
			_dbOperation.CommitTransaction(_transaction);
			return tp;
		}
		catch(Exception e)
		{
			Log.Debug("Exception :" + e);
			//_dbOperation.RollBackTransaction(_transaction);
            // return false;
			e.printStackTrace();
       
		
		}
		return null;
	}
	
	public TestPlanData getTestPlanData( Integer id ) {
		
		Testplan t = this.getTestplan( id );
		TestPlanData d = new TestPlanData();
		
		 d.setTestSuiteListId(t.getTestSuiteListId());
		 d.setCreationDate(t.getCreationDate());
		 d.setModificationDate(t.getModificationDate());
		 d.setCreatedBy(t.getCreatedBy());
		 d.setModifiedBy(t.getModifiedBy());
		 d.setStatus(t.getStatus());
		 
		 d.setProduct(t.getProductId().toString());
		 d.setComment(t.getComment());
		 d.setTopologySetListId(t.getTopologySetListId());
		 d.setCodeName(t.getCodeName());
		 d.setTestPlanId(t.getTestPlanId());
		
		return d;
		
	}
	
	public List<Integer> SelectTestplan() throws Exception
    {
        List<Integer> testplanList = new ArrayList<Integer>();
        try
        {

            Testplan testPlan = new Testplan();

            _transaction=_dbOperation.BeginTransaction(_transaction);
            List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
            
            selectedDataList =_dbOperation.Select(testPlan);
           
            for (Iterator iter = selectedDataList.iterator(); iter.hasNext();) 
	        {     
            	
               	try
	        	{
	        		IDataTable obj = (IDataTable)iter.next();
	        		Testplan testplanObject=(Testplan)obj;
	        		testplanList.add(testplanObject.getTestPlanId());
	        		Log.Debug("TestPlan/SelectTestplan : TestPlan ID: "+testplanObject.getTestPlanId());
	        		
	        		        		
	        	}catch(Exception e)
	        	{
	        		Log.Error("TestPlan/SelectTestplan :Exception :" + e.getMessage());
	        		throw e;
	        	}
		
	        }
            _dbOperation.CommitTransaction(_transaction);

        }
        catch (Exception e)
        {
        	Log.Error("TestPlan/SelectTestplan :Exception :" + e.getMessage());
            return testplanList;
        }
        return testplanList;
    }

	 // Returns testPlan_Id for given testPlan name
	
	/*public int GetTestPlanId(String testPlanName)throws Exception
	{
		try
        {
            Automation.Testplan testPlanTable = new Automation.Testplan();
            testPlanTable.setTestPlanName(testPlanName);
            _dbOperation.BeginTransaction(_transaction);
            List<IDataTable> selectedDataList = null;
            selectedDataList = (List<IDataTable>)_dbOperation.Select(testPlanTable);
            _dbOperation.CommitTransaction(_transaction);

            if (selectedDataList == null)
                return 0;
            else if (selectedDataList.size() > 1)
            {
                throw new Exception("TestPlan/GetTestPlanId: testPlan_Name: " + testPlanName + " is not unique.");
            }
            return ((Automation.Testplan)selectedDataList.get(0)).getTestPlanId();
            
        }
		catch (Exception e)
	    {
	            Log.Error("TestPlan/SelectTestPlanId: Exception: " + e.getMessage());
	    }
	    return 0;
	    
	}*/
}
/*
	
class TestPlanData
{
	int testPlanId;
	 int testplantemplate_id;
	 int topologyset_id;
	 Integer testSuiteListId;
	 String testPlanShortDesc;
	 String testPlanLongDesc;
	 Date creationDate;
	 Date modificationDate;
	 String createdBy;
	 String modifiedBy;
	 String setupScript;
	 String tearDownScript;
	 String status;
	 String product;
	 String comment;
	 Integer topologySetListId;
	 String codeName;
	 
	 
	public int getTestPlanId() {
		return testPlanId;
	}
	public void setTestPlanId(int testPlanId) {
		this.testPlanId = testPlanId;
	}
	public int getTestplantemplate_id() {
		return testplantemplate_id;
	}
	public void setTestplantemplate_id(int testplantemplate_id) {
		this.testplantemplate_id = testplantemplate_id;
	}
	public int getTopologyset_id() {
		return topologyset_id;
	}
	public void setTopologyset_id(int topologyset_id) {
		this.topologyset_id = topologyset_id;
	}
	public Integer getTestSuiteListId() {
		return testSuiteListId;
	}
	public void setTestSuiteListId(Integer testSuiteListId) {
		this.testSuiteListId = testSuiteListId;
	}
	public String getTestPlanShortDesc() {
		return testPlanShortDesc;
	}
	public void setTestPlanShortDesc(String testPlanShortDesc) {
		this.testPlanShortDesc = testPlanShortDesc;
	}
	public String getTestPlanLongDesc() {
		return testPlanLongDesc;
	}
	public void setTestPlanLongDesc(String testPlanLongDesc) {
		this.testPlanLongDesc = testPlanLongDesc;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getSetupScript() {
		return setupScript;
	}
	public void setSetupScript(String setupScript) {
		this.setupScript = setupScript;
	}
	public String getTearDownScript() {
		return tearDownScript;
	}
	public void setTearDownScript(String tearDownScript) {
		this.tearDownScript = tearDownScript;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getTopologySetListId() {
		return topologySetListId;
	}
	public void setTopologySetListId(Integer topologySetListId) {
		this.topologySetListId = topologySetListId;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
}
*/