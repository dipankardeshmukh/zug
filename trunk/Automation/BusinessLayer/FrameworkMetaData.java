package BusinessLayer;


import java.util.ArrayList;
import java.util.List;

import logs.Log;

import org.hibernate.Transaction;

import DatabaseLayer.DBOperations;
import DatabaseLayer.DataClasses.IDataTable;


public class FrameworkMetaData {

	DBOperations  _dbOperation= null;
	Transaction _transaction=null;
	
	
	public FrameworkMetaData()
	{
		_dbOperation=new DBOperations();
	}
	
	 /// Returns meta data value for given key.
	public String GetValue(Name name) throws Exception
    {
        try
        {
        	 Log.Debug(this.toString() + "/GetValue: GetValue() function called.");
        	 DatabaseLayer.Frameworkmetadata frameworkMetaDataTable= new DatabaseLayer.Frameworkmetadata();
        	 frameworkMetaDataTable.setName(name.toString());
             Log.Debug(this.toString() + "/GetValue: Key-" + name.toString());
             _transaction= _dbOperation.BeginTransaction(_transaction);
             Log.Debug(this.toString() + "/GetValue: Select the key value.");
             
             List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
             selectedDataList =_dbOperation.Select(frameworkMetaDataTable);
             
             Log.Debug(this.toString() + "/GetValue: Select query executed to get value.");
             _dbOperation.CommitTransaction(_transaction);
             if (selectedDataList.size() == 0)
             {
                 throw new Exception("No value selected from FrameworkMetaData Table for given key: " + name.toString());
             }
             //Assumed that only one record will be selected.
             return selectedDataList.get(0) == null ?"": (( DatabaseLayer.Frameworkmetadata)selectedDataList.get(0)).getValue();
        	 
        }
        catch (Exception e)
        {
        	 _dbOperation.RollBackTransaction(_transaction);
             throw e;
		}
    }
	
}
/*
enum Name
{
    ArchiveLocation,
}*/