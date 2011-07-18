package BusinessLayer;

import java.util.ArrayList;
import java.util.List;

import logs.Log;

import org.hibernate.Transaction;

import ZUG.*;

import DatabaseLayer.DBOperations;
import DatabaseLayer.TopologyTopologysetXref;
import DatabaseLayer.DataClasses.IDataTable;

public class Topologyset
{
	DBOperations  _dbOperation= null;
	Transaction _transaction=null;

	public Topologyset()
	{
		_dbOperation=new DBOperations();
	}
	public boolean TopologysetExist(int TopologysetId) throws Exception
	{
		boolean result = false;
		try
		{			
			_transaction=_dbOperation.BeginTransaction(_transaction);
			DatabaseLayer.Topologyset topologysetTable=new DatabaseLayer.Topologyset();
			
			Log.Debug(this.toString() + "/TopologysetExist: TopologysetExist() function called ");
			
			topologysetTable.setTopologySetId(TopologysetId);
			Log.Debug(this.toString() + "/TopologysetExist Topologyset ID: " + TopologysetId);
			
			//Check TopologysetId entry exist in table.
			 Log.Debug(this.toString() + "/TopologysetExist : Check TopologysetId entry exist in Topologyset Table or not.");
			 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			 selectedDataList =	 (List<IDataTable>)_dbOperation.Select(topologysetTable);
			 Log.Debug(this.toString() + "/TopologysetExist: Select query executed to check TopologysetId exist or not.");
			 if (selectedDataList == null)
             {   result = false;
                 throw new Exception("No record select from Topologyset Table Where TopologysetId: " + TopologysetId);
             }
             else if (selectedDataList.size() == 0)
             {   result = false;
             }
             else if (selectedDataList.size() > 0)
             {   result = true;
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
	/* this method reads TopologyTopologysetXref table with the specified topologyset id 
	 * It returns list of topology Ids belonging in that topologyset  
	 */
	public List<Integer> ReadTopologySetXref(int TopologysetId) throws Exception {
		try{
			_transaction=_dbOperation.BeginTransaction(_transaction);
			DatabaseLayer.TopologyTopologysetXref topologysetXrefTable=new DatabaseLayer.TopologyTopologysetXref();
			

			Log.Debug(this.toString() + "/Topologyset: ReadTopologysetXref() function called ");
			
			topologysetXrefTable.setTopologysetId(TopologysetId);
			Log.Debug(this.toString() + "/ReadTopologysetXref Topologyset ID: " + TopologysetId);
			
			//Check TopologysetId entry exist in table.
			 Log.Debug(this.toString() + "/ReadTopologysetXref : Check TopologysetId entry exist in TopologyTopologysetXref Table or not.");
			 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			 selectedDataList =	 (List<IDataTable>)_dbOperation.Select(topologysetXrefTable);
			 Log.Debug(this.toString() + "/ReadTopologysetXref: Select query executed to check TopologysetId exist or not.");
			 if (selectedDataList == null)
             {   
                 throw new Exception("No record select from TopologyTopologysetXref Table Where TopologysetId: " + TopologysetId);
             }
             else if (selectedDataList.size() == 0)
             {
            	 throw new Exception("No record select from TopologyTopologysetXref Table Where TopologysetId: " + TopologysetId);
             }
             else if (selectedDataList.size() > 0)
             {   // This block will retrieve topologies in the topologyset
            	 List<Integer> topologyIDs = new ArrayList<Integer>();
            	 for(int i=0 ; i < selectedDataList.size(); i++)
            	 {
            		 TopologyTopologysetXref tObj = (TopologyTopologysetXref)selectedDataList.get(i);
            		 int tid = tObj.getTopologyId();
            		 topologyIDs.add(tid);
            	 }
            	 
            	 Log.Debug("/ReadTopologysetXref: Selected TopologyIds : "+topologyIDs.toString());
            	 return topologyIDs;
             }
             _dbOperation.CommitTransaction(_transaction);
             return null;
		}
		catch(Exception e) 
		{
			_dbOperation.RollBackTransaction(_transaction);
			throw e;
		}
	}

	
}