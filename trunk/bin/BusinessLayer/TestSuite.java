package BusinessLayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import logs.Log;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Transaction;

import DatabaseLayer.DBOperations;
import DatabaseLayer.Role;
import DatabaseLayer.Product;
import DatabaseLayer.Testsuite;
import DatabaseLayer.DataClasses.IDataTable;

public class TestSuite
{

	DBOperations  _dbOperation= null;
	Transaction _transaction=null;
	
	
	TestSuite()
	{
		_dbOperation=new DBOperations();
	}
	
	public synchronized void Save(TestSuiteData testSuiteData)throws Exception
	{
		 Log.Debug("TestSuite/Save : Save function called");
		if (testSuiteData ==null)
			return;
		try
		{
			DatabaseLayer.Testsuite testSuiteTable= new DatabaseLayer.Testsuite();
			
			_transaction=_dbOperation.BeginTransaction(_transaction);
			
			testSuiteTable.setTestSuiteName(testSuiteData.testSuiteName);
			Log.Debug("TestSuite/Save : Test Suite Name ="+testSuiteTable.getTestSuiteName());
			
			Log.Debug("TestSuite/Save :Check whether TestsuiteId Is exist in Testsuite Table");
			List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			selectedDataList = _dbOperation.Select(testSuiteTable);
			Log.Debug("TestSuite/Save :Select query executed to check TestSuite entry is already exist in database");
			_dbOperation.CommitTransaction(_transaction);
			if (selectedDataList == null)
            {
                throw new Exception("No Records are selected from " + testSuiteTable.toString() + " Where TestsuiteId =" + testSuiteTable.getTestSuiteName());
            }//if selecteddatalist is null
			
            else if (selectedDataList.size() == 0)
            {
            	_transaction=_dbOperation.BeginTransaction(_transaction);
            	
            	DatabaseLayer.Role role = new Role(Integer.getInteger(testSuiteData.getRole()));
            	testSuiteTable.setRole(role);//Integer.getInteger(testSuiteData.role));
            	
            	//DatabaseLayer.Product product = new Product(Integer.getInteger(testPlanData.getProduct()))
            	
            	//testSuiteTable.role.setRoleId(testSuiteData.role);
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
            	 int rowsAffected=0;
            	if(!StringUtils.isEmpty(testSuiteTable.getRole().getRoleName())) //( == 0))// Need to check it - Gurpreet !StringUtils.isEmpty(testSuiteTable.getRole())))
            	{            		 
            		 DatabaseLayer.Testsuite testSuitearray[]={testSuiteTable};
            		 rowsAffected = _dbOperation.Insert(testSuitearray);
            		 if (rowsAffected == 0)
                                throw new Exception("Unable To Insert Data InTo Table Testsuite");
                     else
                     {
                           Log.Debug("TestSuite/Save : Test SuiteID " + testSuiteTable.getTestSuiteId() +"is successfuly inserted into database");
                           _dbOperation.CommitTransaction(_transaction);   
                     }
            	}
            	
            	
            }
            else if (selectedDataList.size() >0)
            {
            	_transaction=_dbOperation.BeginTransaction(_transaction);
            	testSuiteTable.setTestSuiteId(((Testsuite)selectedDataList.get(0)).getTestSuiteId());
            	//Automation.Role role = new Role(testSuiteData.role);
             	testSuiteTable.setRole(((Testsuite)selectedDataList.get(0)).getRole());
             	testSuiteTable.setTestSuiteName(testSuiteData.testSuiteName);
             	testSuiteTable.setComment(testSuiteData.comment);
             	testSuiteTable.setCreationDate(((Testsuite)selectedDataList.get(0)).getCreationDate());
             	testSuiteTable.setModificationDate(testSuiteData.modificationDate);
             	testSuiteTable.setCreatedBy(testSuiteData.createdBy);
             	testSuiteTable.setModifiedBy(testSuiteData.modifiedBy);
             	testSuiteTable.setStatus(testSuiteData.status);
             	testSuiteTable.setElapsedTimeMin(testSuiteData.elapsedTimeMin);
             	testSuiteTable.setElapsedTimeMax(testSuiteData.elapsedTimeMax);
             	testSuiteTable.setTestSuiteFilePath(testSuiteData.testPlanFilePath);
             	testSuiteTable.setInitializationTime(testSuiteData.initializationTime);
             	testSuiteTable.setExecutionTime(testSuiteData.executionTime);
            	 
             	DatabaseLayer.Testsuite oldTestsuite=testSuiteTable;
            	 
            	// oldTestsuite.AssignTo(testSuiteTable);
            	 //testSuiteTable.AssignTo(oldTestsuite);
            	 //oldTestsuite.setTestSuiteName(testSuiteData.testSuiteName);
            	 oldTestsuite.setModificationDate(new Date());
            	 //testSuiteTable.setModificationDate(new Date());
            	 //int rowsAffected = _dbOperation.Update(testSuiteTable, oldTestsuite);
            	 int rowsAffected = _dbOperation.Update(oldTestsuite);
            	 if (rowsAffected == 0)
                     throw new Exception("Unable to update Data from TestSuite.");
            	 
            	  else
                  {
                      Log.Debug("TestSuite/Save : Test SuiteID " + testSuiteTable.getTestSuiteId() +
                          " is already present in Testsuite table");
                      _dbOperation.CommitTransaction(_transaction);
                  }
            	 
            }
				
			 
		}
		catch(Exception e)
		{
		       _dbOperation.RollBackTransaction(_transaction);
               Log.Debug("TestSuite/Save : Exception in save function, Rollback Transaction ");
               throw e;
        
		}
	}

	public List<String> SelectTestSuite() throws Exception
	{
		 List<String> TestsuiteList=new ArrayList<String>();
		 
		 try
		 {
			 DatabaseLayer.Testsuite testSuiteTable=new DatabaseLayer.Testsuite();
			 _transaction=_dbOperation.BeginTransaction(_transaction);
		     List<IDataTable> selectedDataList =new ArrayList<IDataTable>();
		     selectedDataList = _dbOperation.Select(testSuiteTable);
		     
		     for (Iterator iter = selectedDataList.iterator(); iter.hasNext();) 
		        {     
	            	
	               	try
		        	{
		        		IDataTable obj = (IDataTable)iter.next();
		        		DatabaseLayer.Testsuite testsuiteObject=(DatabaseLayer.Testsuite)obj;
		        		
		        		TestsuiteList.add(testsuiteObject.getTestSuiteName());
		        		
		        		Log.Debug("TestSuite/SelectTestSuite : Testsuite Name: "+testsuiteObject.getTestSuiteName());
		        		// _dbOperation.CommitTransaction(_transaction);
		        		        		
		        	}catch(Exception e)
		        	{
		        		Log.Error("TestSuite/SelectTestSuite :Exception :" + e.getMessage());
		        		throw e;
		        	}
			
		        }
		     _dbOperation.CommitTransaction(_transaction);
		 }
		 catch(Exception e)
		 {
			 Log.Error("TestSuite/SelectTestSuite :Exception :" + e.getMessage());
     		throw e;
		 }
		 return TestsuiteList;
	}
	
	 public int GetTestSuiteId(String testSuiteName)
	 {
		 try
		 {
			 DatabaseLayer.Testsuite testSuiteTable= new DatabaseLayer.Testsuite();
			 testSuiteTable.setTestSuiteName(testSuiteName);
			 
			 _transaction=_dbOperation.BeginTransaction(_transaction);
			 
			 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			 selectedDataList=_dbOperation.Select(testSuiteTable);
			 
			 _dbOperation.CommitTransaction(_transaction);
			 
			 if (selectedDataList == null)
	             return 0;
			 else if(selectedDataList.size()>1)
			 {
				 throw new Exception("TestSuite/GetTestSuiteId: TestSuite_Name: " + testSuiteName + " is not unique.");
			 }
			 
			 DatabaseLayer.Testsuite tempTestSuite= new DatabaseLayer.Testsuite();
			 tempTestSuite=( DatabaseLayer.Testsuite) selectedDataList.get(0);
			 return tempTestSuite.getTestSuiteId();
			 		 
		 }
		 catch(Exception e)
		 {
			  Log.Error("TestSuite/GetTestSuiteId: Exception: " + e.getMessage());
		 }
		return 0;
		 
	 }
	
}
/*
class TestSuiteData
{
	Integer testSuiteId;
	int role;
	 String testSuiteName;
	 String comment;
	 Date creationDate;
	 Date modificationDate;
	 String createdBy;
	 String modifiedBy;
	 String status;
	 Integer elapsedTimeMin;
	 Integer elapsedTimeMax;
	 String testPlanFilePath;
	 Integer initializationTime;
	 Integer executionTime;
	public Integer getTestSuiteId() {
		return testSuiteId;
	}
	public void setTestSuiteId(Integer testSuiteId) {
		this.testSuiteId = testSuiteId;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getTestSuiteName() {
		return testSuiteName;
	}
	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getElapsedTimeMin() {
		return elapsedTimeMin;
	}
	public void setElapsedTimeMin(Integer elapsedTimeMin) {
		this.elapsedTimeMin = elapsedTimeMin;
	}
	public Integer getElapsedTimeMax() {
		return elapsedTimeMax;
	}
	public void setElapsedTimeMax(Integer elapsedTimeMax) {
		this.elapsedTimeMax = elapsedTimeMax;
	}
	public String getTestPlanFilePath() {
		return testPlanFilePath;
	}
	public void setTestPlanFilePath(String testPlanFilePath) {
		this.testPlanFilePath = testPlanFilePath;
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
	 
}
*/
//gets the current date time in specific format......
class DateUtils {
	  public static final String DATE_FORMAT_NOW = "yyyyMMdd-HHmmss";

	  public String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());
	  }
}