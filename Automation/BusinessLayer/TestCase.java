package BusinessLayer;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import logs.Log;

import org.hibernate.Transaction;

import DatabaseLayer.DBOperations;
import DatabaseLayer.Testcase;
import DatabaseLayer.Testsuite;
import DatabaseLayer.Testsuitevariables;
import DatabaseLayer.DataClasses.IDataTable;

public class TestCase {

	DBOperations  _dbOperation= null;
	Transaction _transaction=null;
	
	
	
	public TestCase()
	{
		_dbOperation=new DBOperations();
	}
	
	 // Insert testcases from same testsuite into database if not exist.
	
	public synchronized void Save(TestCaseData[] testCaseDatas) throws Exception
	{
	    Log.Debug(this.toString() + "/Save: TestCase save called.");
	    if (testCaseDatas == null)
        {
            return;
        }
//        //Begin Transaction.
//	    _transaction=_dbOperation.BeginTransaction(_transaction);

        for (int i = 0; i < testCaseDatas.length; i++)
        {
            try
            {
                TestCaseData testCaseData = testCaseDatas[i];

            
                Log.Debug("TestCase/Save: Transaction begin for saving the testcase.");

                //Get testSuite_Id for given TestSuiteName.
                //int testSuiteId = new BusinessLayer.TestSuite().GetTestSuiteId(testCaseData.get_testSuiteName());
                int testSuiteId = testCaseData.get_testSuiteId().intValue();

                if (testSuiteId==0)
                {
                	try
                	{
	                	throw new Exception("TestCase/Save: Cant Insert into TestCase Table: Foreign Key Violation");
	                	
                	}
                	catch(Exception e)
                	{
                		_dbOperation.RollBackTransaction(_transaction);
	                    Log.Debug(this.toString() + "/Save: Transaction Rollback for saving the testcase.");
	                    Log.Error("Cant Insert into TestCase Table: Foreign Key Violation ");
	                    throw e;
                	}
                	
                }
                Log.Debug("TestSuiteID: "+ testSuiteId);
                Log.Debug(this.toString() + "/Save: TestSuiteId: " + testSuiteId);

                
                
                
              //Begin Transaction.
        	    _transaction=_dbOperation.BeginTransaction(_transaction);
        	    
        	    
        	    
        	    
        	    
                //Insert the testcase into TestCase table depends on testcase exist or not in table.
                int testCaseId = SaveTestCaseTable(testCaseData, testSuiteId);
                Log.Debug("testcase id"+testCaseId);
                int TestSuiteVariable_Id=0;
                int variable_values_id=0;
                for (int j = 0; j < testCaseData.get_variableList().size(); j++)
                {
                	if (i == 0)
                    {
                        //All testcases from same testsuite so each has same variables but different value for variables.
                        //So variables will be saved in TestSuiteVariables and TestSuiteVariableSet table
                        //for first testcase only.

                        Log.Debug(this.toString() +
                            "/Save: Save TestCase Variable Name: " + testCaseData.get_variableList().get(i).get_name() +
                            ", Value: " +  testCaseData.get_variableList().get(j).get_value()+ " for first test case of test suite.");
                        
                        Log.Debug("/Save: Save TestCase Variable Name: " + testCaseData.get_variableList().get(i).get_name() +
                            ", Value: " +  testCaseData.get_variableList().get(j).get_value()+ " for first test case of test suite.");
                        //Insert TestSuiteVariables entry if variable exist or not in table.
                        TestSuiteVariable_Id = SaveTestSuiteVariables(testCaseData.get_variableList().get(j));
                   
                        
                      //Insert TestSuiteVariableSet table entry depend on TestPLanID and Variable name exist 
                        //in table or not.
                        SaveTestSuiteVariableSet(testSuiteId, TestSuiteVariable_Id, testCaseData.get_variableList().get(j));
                   
                        Log.Debug(this.toString() + "/Save: Variable saved.");
                        
                        
                    }
                	 //Insert data into VariableValues table if its entry exist or not in table.
                    variable_values_id=SaveVariableValues(TestSuiteVariable_Id,testCaseData.get_variableList().get(j));
                    //Insert the testcase identifer, variable and its value into TestCase_VariableValue table
                    //depends on testcase identifer, variable and its value exist or not in table.
                    SaveTestCaseVariableValue(testCaseData.get_variableList().get(j), testCaseId,variable_values_id);
                   
                }
                Log.Debug(this.toString() + "/Save: Transaction commited for saving the testcase.");
            }
            catch(Exception e)
            {
            	_dbOperation.RollBackTransaction(_transaction);
                Log.Debug(this.toString() + "/Save: Transaction Rollback for saving the testcase.");
                throw e;
            }
        }
        //Commit transaction.
        _dbOperation.CommitTransaction(_transaction);
	}
	
	public synchronized void Save(TestCaseData testCaseData) throws Exception
    {
           Save(new TestCaseData[] { testCaseData });
    }
	  /// Insert data into VariableValues table if its entry exist or not in table.
	 private synchronized int  SaveVariableValues(int TestSuiteVariable_Id, Variable variable) throws Exception
	 {
		 DatabaseLayer.Variablevalues variableValuesTable= new  DatabaseLayer.Variablevalues();
		 int variable_value_id=0;
	     Log.Debug(this.toString() + "/SaveVariableValues: SaveVariableValues() function called to save Variable and its "
                   + " value.");
	     /*
	      * in .net code the name is the column which is not here in 
	      * variablevalue table instead TestSuiteVariables_Id 
	      * is the column. and in testsuite name column is present 
	      */
	     
	     DatabaseLayer.Testsuitevariables testsuitevariables= new Testsuitevariables(TestSuiteVariable_Id);
	     variableValuesTable.setTestsuitevariables(testsuitevariables);
	     Log.Debug(this.toString() + "/SaveVariableValues: TestSuiteVariable_Id: " + TestSuiteVariable_Id);
	     
	     variableValuesTable.setVariableValue(variable.get_value());
	     Log.Debug(this.toString() + "/SaveVariableValues: VariableValue: " + variableValuesTable.getVariableValue());
	    
	     //Check VariableValues has entry in table.
	     Log.Debug(this.toString() + "/SaveVariableValues: Check variable entry exist in " +
                 variableValuesTable.toString() + " or not.");
	     
	     List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
	     selectedDataList = (List<IDataTable>)_dbOperation.Select(variableValuesTable);

	     Log.Debug(this.toString() + "/SaveVariableValues: Select query executed for variable entry exist in " +
                 variableValuesTable.toString() + " or not.");
	     
	     if (selectedDataList == null)
         {
             throw new Exception("Unable to select VariableName: " + variable.get_name() + ", VariableValue: " + variable.get_value() +
                 " from " + variableValuesTable.toString());
         }

         if (selectedDataList.size() == 0)
         {
             Log.Debug(this.toString() + "/SaveVariableValues: Insert TestPlanID and Variable Name into " +
                 variableValuesTable.toString());
             //Insert test suite id and variable Name in Table.
             DatabaseLayer.Variablevalues array[]={variableValuesTable};
             int rowAffected = _dbOperation.Insert(array);
             if (rowAffected == 0)
             {
                 throw new Exception("Unable to insert VariableName: " + variable.get_name() +
                     ", VariableValue: " + variable.get_value() + " into " + variableValuesTable.toString());
             }
             else
             {
                 Log.Debug(this.toString() + "/SaveVariableValues: Variable inserted successfully in " +
                     variableValuesTable.toString());
             }
             variable_value_id= variableValuesTable.getVariableValuesId();
             Log.Debug(this.toString() + "/SaveVariableValues:  variablevalueid:"+ variable_value_id);
             Log.Debug(this.toString() + "/SaveVariableValues:  variablevalueid:"+ variable_value_id);
         }
         else if (selectedDataList.size() == 1)
         {
             Log.Debug(this.toString() + "/SaveVariableValues: Variable record already exist in " +
                 variableValuesTable.toString());
             variable_value_id= ((DatabaseLayer.Variablevalues)selectedDataList.get(0)).getVariableValuesId();
             
             Log.Debug(this.toString() + "/SaveVariableValues:  variablevalueid:"+ variable_value_id);
             Log.Debug(this.toString() + "/SaveVariableValues:  variablevalueid:"+ variable_value_id);
            
             //    //Update VariableValues Table entry.
             //    //Assumed that there will be unique entry for each VariableValues.
             //    VariableValuesTable oldVariableValuesTableEntry = (VariableValuesTable)selectedDataList[0];

             //    int rowAffected = _dbOperation.Update(variableValuesTable, oldVariableValuesTableEntry);
             //    if (rowAffected == 0)
             //    {
             //        //Log(Unable to update)
             //    }
             //    else
             //    {
             //        //Log(update is successful.)
             //    }
         }
         else if (selectedDataList.size() > 1)
         {
             throw new Exception("VariableName: " + variable.get_name() + ", VariableValue: " + variable.get_value() +
                 " is not unique in " + variableValuesTable.toString());
        }
		 
		 return variable_value_id;
	 }
	 
	 
	  // Insert TestSuiteVariableSet table entry if TestPlanID and Variable name exist in table or not.
	 private synchronized void SaveTestSuiteVariableSet(int testSuiteId,int TestSuiteVariable_Id, Variable variable) throws Exception
	 {
		 DatabaseLayer.Testsuitevariableset testSuiteVariableSetTable= new  DatabaseLayer.Testsuitevariableset();
		 Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: SaveTestSuiteVariableSet() function called for saving " +
                    "variable in TestSuiteVariableSetTable");
		 
		 DatabaseLayer.Testsuitevariables testsuitevariables= new Testsuitevariables(TestSuiteVariable_Id);
		 testSuiteVariableSetTable.setTestsuitevariables(testsuitevariables);
		 Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: Testsuitevariables ID: " + TestSuiteVariable_Id);
		 
		 DatabaseLayer.Testsuite testsuite=new Testsuite(testSuiteId);
		 testSuiteVariableSetTable.setTestsuite(testsuite);
		 Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: TestSuiteID: " + testSuiteId);

		 testSuiteVariableSetTable.setVariableName(variable.get_name());
	     Log.Debug(this.toString() + "/SaveTestPlanVariableSet: VariableName: " + testSuiteVariableSetTable.getVariableName());

         Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: Check Variable entry exist or ont in " +
        		 testSuiteVariableSetTable.toString());
         //Check already variable has entry in table.
         List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
         selectedDataList = (List<IDataTable>)_dbOperation.Select(testSuiteVariableSetTable);
         
         Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: Select query executed to check variable entry exist or "
                 + "not.");
         if (selectedDataList == null)
         {
             //If selectedDataList is null then its unable to select from table.
             throw new Exception("Unable to select TestSuiteId: " + testSuiteId + ", " +
                 " Variable Name: " + variable.get_name() + " from " + testSuiteVariableSetTable.toString());
         }
         else if (selectedDataList.size() == 0)
         {
             Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: Save Variable in " +
            		 testSuiteVariableSetTable.toString() + " because its does not exist in table");
             //Variable description is not useful property for selecting entry from TestSuiteVariableSet Table.
             testSuiteVariableSetTable.setTestPlanConstraint(variable.get_description());
             Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: VariableDescription: " +
            		 testSuiteVariableSetTable.getTestPlanConstraint());
             testSuiteVariableSetTable.setVariableAssociationDate(new Date());

             Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: VariableAssociationDate: " +
            		 testSuiteVariableSetTable.getVariableAssociationDate());
             

             //Insert test suite id and variable Name in Table.
             
             DatabaseLayer.Testsuitevariableset array[]={testSuiteVariableSetTable};
             int rowAffected = _dbOperation.Insert(array);
             if (rowAffected == 0)
             {
                 throw new Exception("Unable to insert TestSuiteID: " + testSuiteId + ", " +
                     " Variable Name: " + variable.get_name() + " into " + testSuiteVariableSetTable.toString());
             }
             else
             {
                 Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: Variable inserted successfully in " +
                		 testSuiteVariableSetTable.toString());
             }


         }
         else if (selectedDataList.size() == 1)
         {
             Log.Debug(this.toString() + "/SaveTestSuiteVariableSet: Variable record already exist in " +
            		 testSuiteVariableSetTable.toString());
             //    //Update TestSuiteVariableSet Table entry.
             //    //Assumed that there will be unique entry for each TestSuiteVariableSet.
             //    TestSuiteVariableSetTable oldTestSuiteVariableSetTableEntry = (TestSuiteVariableSetTable)selectedDataList[0];

             //    int rowAffected = _dbOperation.Update(testSuiteVariableSetTable, oldTestSuiteVariableSetTableEntry);
             //    if (rowAffected == 0)
             //    {
             //        //Log(Unable to update)
             //    }
             //    else
             //    {
             //        //Log(update is successful.)
             //    }
         }
         else if (selectedDataList.size() > 1)
         {
             throw new Exception("TestSuiteID: " + testSuiteId + ", " + " Variable Name: " + variable.get_name() +
                 " is not unique in " + testSuiteVariableSetTable.toString());
         }
        
         
		 
	 }
	
	
	  /// Insert TestSuiteVariables table entry if variable name entry exist or not in table.
	 private synchronized int SaveTestSuiteVariables(Variable variable) throws Exception
	 {
		 int TestSuiteVariable_Id=0;
		 DatabaseLayer.Testsuitevariables testSuiteVariablesTable= new DatabaseLayer.Testsuitevariables();
	     Log.Debug(this.toString() + "/SaveTestSuiteVariables: SaveTestSuiteVariables() called for saving variable in " +
	    		 "testSuiteVariablesTable");
	     testSuiteVariablesTable.setVariableName(variable.get_name());
	     Log.Debug(this.toString() + "/SaveTestSuiteVariables: VariableName: " + testSuiteVariablesTable.getVariableName());
	     Log.Debug(this.toString() + "/SaveTestSuiteVariables: Check variable entry exist in " +
	    		 testSuiteVariablesTable.toString() + " or not.");
	     List<IDataTable> selectedDataList =  new ArrayList<IDataTable>();
	     selectedDataList = (List<IDataTable>)_dbOperation.Select(testSuiteVariablesTable);

         Log.Debug(this.toString() + "/SaveTestSuiteVariables: Select query executed for variable entry exist in " +
        		 testSuiteVariablesTable.toString() + " or not.");

         //Check already VariableName exist in TestSuiteVariables Table.
         if (selectedDataList == null)
         {
             //If selectedDataList is null then its unable to select from table.
             throw new Exception("Unable to select Variable Name: " + variable.get_name() +
                 " from TestSuiteVariables Table" );
         }
         else if (selectedDataList.size() == 0)
         {
             Log.Debug(this.toString() + "/SaveTestSuiteVariables: Save the Variable in " +
            		 testSuiteVariablesTable.toString() + " because its entry does not exist.");

             //Insert variable, its description and creation date in TestSuiteVariables Table.
             testSuiteVariablesTable.setDescription(variable.get_description());
             Log.Debug(this.toString() + "/SaveTestSuiteVariables: VariableShortDesc: " +
            		 testSuiteVariablesTable.getDescription());
             
             DatabaseLayer.Testsuitevariables array[]={testSuiteVariablesTable};
             int rowAffected = _dbOperation.Insert(array);
             if (rowAffected == 0)
             {
                 throw new Exception("Unable to insert Variable Name: " + testSuiteVariablesTable.getVariableName()+ " " +
                     " Varaible Description: " + testSuiteVariablesTable.getDescription() + " into  TestSuiteVariables Table");
             }
             else
             {
                 Log.Debug(this.toString() + "/SaveTestSuiteVariables: Variable inserted successfully in " +
                		 testSuiteVariablesTable.toString());
                 TestSuiteVariable_Id=testSuiteVariablesTable.getTestSuiteVariableId();
             }
         }
         else if (selectedDataList.size() == 1)
         {
             Log.Debug(this.toString() + "/SaveTestSuiteVariables: Variable record already exist in " +
            		 testSuiteVariablesTable.toString());

             TestSuiteVariable_Id= ((DatabaseLayer.Testsuitevariables)selectedDataList.get(0)).getTestSuiteVariableId();
             Log.Debug(this.toString() + "/SaveTestSuiteVariables: TestSuiteVariable_Id:  " +
            		 TestSuiteVariable_Id);
             
             //    //Update TestSuiteVariables Table entry.
             //    //Assumed that there will be unique entry for each TestSuiteVariables.
             //    TestSuiteVariablesTable oldTestSuiteVariablesTableEntry = (TestSuiteVariablesTable)seletedDataList[0];

             //    int rowAffected = _dbOperation.Update(testSuiteVariablesTable, oldTestSuiteVariablesTableEntry);
             //    if (rowAffected == 0)
             //    {
             //        //Log(Unable to update)
             //    }
             //    else
             //    {
             //        //Log(update is successful.)
             //    }
         }
         else if (selectedDataList.size() > 1)
         {
             throw new Exception("Variable Name: " + variable.get_name() + " " +
                 " Varaible Description: " + variable.get_description()+ " is not unique in " +
                 testSuiteVariablesTable.toString());
             
         }
         return TestSuiteVariable_Id;
	 }

	 private synchronized int SaveTestCaseTable(TestCaseData testCaseData, int testSuiteId) throws Exception
	 {
		 int testcaseID=0;
		DatabaseLayer.Testcase testCaseTable= new DatabaseLayer.Testcase();
        Log.Debug(this.toString() + "/SaveTestCaseTable: SaveTestCaseTable() called for saving testcase " +
                "in testCaseTable");
        
        DatabaseLayer.Testsuite testsuite= new Testsuite(testSuiteId);
        testCaseTable.setTestsuite(testsuite);
        Log.Debug(this.toString() + "/SaveTestCaseTable: TestsuiteID: " + testCaseTable.getTestsuite().getTestSuiteId());
        
        testCaseTable.setTestCaseIdentifier(testCaseData.get_testCaseIdentifier());
        Log.Debug(this.toString() + "/SaveTestCaseTable: TestCaseIdentifier: " + testCaseTable.getTestCaseIdentifier());
        Log.Debug(this.toString() + "/SaveTestCaseTable: Check testcase entry exist in table or not.");
        
        //Check already TestCase has entry in table.
        List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
        selectedDataList = (List<IDataTable>)_dbOperation.Select(testCaseTable);
        //Log.Debug(this.toString() + "/SaveTestCaseTable: Select query executed to check testcase entry exist or not.");

        if (selectedDataList == null)
        {
            //If selectedDataList is null then its unable to select from table.
            throw new Exception("Unable to select testcase where TestCaseIdentifier: " +
                testCaseTable.getTestCaseId() + " TestSuiteID: " + testCaseTable.getTestsuite().getTestSuiteId() +
                " TestCaseId: " + testCaseTable.getTestCaseIdentifier() + " from " + testCaseTable.toString());
            
        }
        else if (selectedDataList.size() == 0)
        {
            Log.Debug(this.toString() + "/SaveTestCaseTable: Save testcase in " + testCaseTable.toString() +
                " because its entry does not exist in table.");

            //Following properties of testcase not useful for selecting unique testcase.
            testCaseTable.setDescription(testCaseData.get_description());
            Log.Debug(this.toString() + "/SaveTestCaseTable: Description: " + testCaseTable.getDescription());
            
            testCaseTable.setCreationDate(testCaseData.get_creationDate());
            Log.Debug(this.toString() + "/SaveTestCaseTable: CreationDate: " + testCaseTable.getCreationDate());
           
            testCaseTable.setModificationDate(new Date());
            Log.Debug(this.toString() + "/SaveTestCaseTable: ModificationDate: " + testCaseTable.getModificationDate());
            
            testCaseTable.setTestSteps(testCaseData.getTestSteps());
            Log.Debug(this.toString() + "/SaveTestCaseTable: TestSteps: " + testCaseTable.getTestSteps());
            //Insert TestCase into Table.
            DatabaseLayer.Testcase array[]={testCaseTable};
            int rowAffected = _dbOperation.Insert(array);
            if (rowAffected == 0)
            {
                throw new Exception("Unable to insert testcase where TestCaseIdentifier: " +
                    testCaseTable.getTestCaseId()+ " TestSuiteID: " + testCaseTable.getTestsuite().getTestSuiteId() +
                    " TestCaseId: " + testCaseTable.getTestCaseIdentifier() + " into " + testCaseTable.toString());
            }
            else
            {
                Log.Debug(this.toString() + "/SaveTestCaseTable: TestCase successfully inserted in " +
                    testCaseTable.toString());
                testcaseID=testCaseTable.getTestCaseId();
            }
            
            return testcaseID;
        }
        else if (selectedDataList.size()== 1)
        {
            Log.Debug(this.toString() + "/SaveTestCaseTable: Testcase record already exist in " +
                testCaseTable.toString());
            
          //  Automation.Testsuite obj= (Automation.Testsuite)selectedDataList.get(0);
            
            return (((DatabaseLayer.Testcase)selectedDataList.get(0)).getTestCaseId());
          //Update is commented because update of test case is not allowed. You have to insert new testcase.
            //It's helpful for report generation because once testcase excuted its should not modify.
            
            //    //Update Testcase entry. 
            //    //Assumed that there will be unique entry for each testcase.
            //    TestCaseTable oldTestCaseTableEntry = (TestCaseTable)selectedDataList[0];

            //    int rowAffected = _dbOperation.Update(testCaseTable, oldTestCaseTableEntry);

            //    if (rowAffected == 0)
            //    {
            //        //Log("Unable to update testcase where TestCaseIdentifier: " +
            //        //    testCaseTable.TestCaseIdentifier + " TestSuiteId: " + testCaseTable.TestSuiteId +
            //        //    " TestCaseId: " + testCaseTable.TestCaseId + " into " + testCaseTable.ToString());
            //    }
            //    else
            //    {
            //        //Log(Successful update.)
            //    }
            
        }
        else if (selectedDataList.size() > 1)
        {
            throw new Exception("TestCase is not unique where TestCaseIdentifier: " +
                testCaseTable.getTestCaseId()+ " TestSuiteID: " + testCaseTable.getTestsuite().getTestSuiteId() +
                " TestCaseId: " + testCaseTable.getTestCaseIdentifier() + " in " + testCaseTable.toString());
            
        }
		
		 return 0;
		 
	 }

	 /// Insert the testcase variable name and its value depeand on entry exist or not in table.
	 
	 private synchronized void SaveTestCaseVariableValue(Variable variable, int testCaseId,int variable_values_id) throws Exception
	 {
		 DatabaseLayer.Testcasevariablevalue testCaseVariableValueTable= new DatabaseLayer.Testcasevariablevalue();
		Log.Debug(this.toString() + "/SaveTestCaseVariableValue: SaveTestCaseVariableValue() function called for " +
                 "saving Variable in " + testCaseVariableValueTable.toString());

		DatabaseLayer.Testcase testcase= new Testcase(testCaseId);
		testCaseVariableValueTable.setTestcase(testcase);
		
        Log.Debug(this.toString() + "/SaveTestCaseVariableValue: TestCaseIdentifier" +
             testCaseVariableValueTable.getTestcase().getTestCaseId());
         
         testCaseVariableValueTable.setVariableValuesId(variable_values_id);
         Log.Debug(this.toString() + "/SaveTestCaseVariableValue: VariableID: "
                 + testCaseVariableValueTable.getVariableValuesId());
         
         //testCaseVariableValueTable.VariableValue = variable.Value;
         //Log.Debug(this.ToString() + "/SaveTestCaseVariableValue: VariableValue: "
          //   + testCaseVariableValueTable.VariableValue);
 
         Log.Debug(this.toString() + "/SaveTestCaseVariableValue: Check variable entry exist in table or not.");
         //Check already TestCaseIdentifier, Variable Name and its value is there in table or not.
         List<IDataTable> selectedDataList =  new ArrayList<IDataTable>();
         selectedDataList = (List<IDataTable>)_dbOperation.Select(testCaseVariableValueTable);
         
         Log.Debug(this.toString() + "/SaveTestCaseVariableValue: Select query executed to check entry exist or not.");
         
         if (selectedDataList == null)
         {
             //If selectedDataList is null then its unable to select from table.
             throw new Exception("Unable to select testcase identifier where TestCaseId: " + testCaseId +
                 " VariableID: " + variable_values_id + " " +
                 testCaseVariableValueTable.toString());
         }
         if (selectedDataList.size() == 0)
         {
             Log.Debug(this.toString() + "/SaveTestCaseVariableValue: Save testcase variable with its value in " +
                 testCaseVariableValueTable.toString() + " because its entry does not exist in table.");

             //Insert TestCase variable Name and its value in table.
             DatabaseLayer.Testcasevariablevalue array[]= {testCaseVariableValueTable};
             int rowAffected = _dbOperation.Insert(array);
             if (rowAffected == 0)
             {
                 throw new Exception("Unable to insert TestCaseId: " + testCaseId +
                     " VariableID: " + variable_values_id + 
                     " into  TestCaseVariableValue Table" );
             }
             else
             {
                 Log.Debug(this.toString() + "/SaveTestCaseVariableValue: TestCase variable with its value inserted " +
                     " successfully in TestCaseVariableValue Table");
                 
             }
         }
         else if (selectedDataList.size() == 1)
         {
             Log.Debug(this.toString() + "/SaveTestCaseVariableValue: TestCase variable with its value already exist in " +
                 testCaseVariableValueTable.toString());
//           //Update TestCaseVariableValue Table entry.
             //    //Assumed that there will be unique entry for each TestCaseVariableValue.
             //    TestCaseVariableValueTable oldTestCaseVariableValueTableEntry =
             //        (TestCaseVariableValueTable)selectedDataList[0];

             //    int rowAffected = _dbOperation.Update(testCaseVariableValueTable, oldTestCaseVariableValueTableEntry);
             //    if (rowAffected == 0)
             //    {
             //        //Log(Unable to update)
             //    }
             //    else
             //    {
             //        //Log(update is successful.)
             //    }
         }
         else if (selectedDataList.size() > 1)
         {
             throw new Exception("TestCaseId: " + testCaseId + " VariableID: " +variable_values_id +
                  "is not unique in TestCaseVariableValue Table");
         }
		 
	 }
}
/*
class Variable
{
	
	private String _name;
    private String _value;
    private String _description;
    
    
	
	public String get_name() {
		return _name;
	}
	public void set_name(String _name) {
		this._name = _name;
	}
	public String get_value() {
		return _value;
	}
	public void set_value(String _value) {
		this._value = _value;
	}
	public String get_description() {
		return _description;
	}
	public void set_description(String _description) {
		this._description = _description;
	}
    
}
class TestCaseData
{
	 private List<Variable> _variableList = new ArrayList<Variable>();
	 private String _testSuiteName;
	 private String _testCaseIdentifier;
	 private String _description;
	 private Date _creationDate;
	 private Date modificationDate;
	 private String testSteps;
	 
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	public String getTestSteps() {
		return testSteps;
	}
	public void setTestSteps(String testSteps) {
		this.testSteps = testSteps;
	}
	public List<Variable> get_variableList() {
		return _variableList;
	}
	public void set_variableList(List<Variable> list) {
		_variableList = list;
	}
	public String get_testSuiteName() {
		return _testSuiteName;
	}
	public void set_testSuiteName(String suiteName) {
		_testSuiteName = suiteName;
	}
	public String get_testCaseIdentifier() {
		return _testCaseIdentifier;
	}
	public void set_testCaseIdentifier(String caseIdentifier) {
		_testCaseIdentifier = caseIdentifier;
	}
	public String get_description() {
		return _description;
	}
	public void set_description(String _description) {
		this._description = _description;
	}
	public Date get_creationDate() {
		return _creationDate;
	}
	public void set_creationDate(Date date) {
		_creationDate = date;
	}
	 

}
*/
