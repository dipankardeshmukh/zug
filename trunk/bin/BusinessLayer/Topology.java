package BusinessLayer;


import java.util.ArrayList;
import java.util.List;

import logs.Log;

import org.hibernate.Transaction;

import DatabaseLayer.DBOperations;
import DatabaseLayer.Machinecatalog;
import DatabaseLayer.Role;
import DatabaseLayer.DataClasses.IDataTable;
public class Topology
{
	DBOperations  _dbOperation= null;
	Transaction _transaction=null;
	
	public Topology()
	{
		_dbOperation=new DBOperations();
	}
	
	public void Save(TopologyData topologyData) throws Exception
	{
		 if (topologyData == null)
         {
             return;
         }
		 try
		 {
			DatabaseLayer.Topology topologyTable=new DatabaseLayer.Topology();
			
			if(topologyData.topologyId!=0)
			{
			topologyTable.setTopologyId(topologyData.topologyId);
			Log.Debug("Topology/Save : TopologyId="+topologyData.topologyId);
			Log.Debug("Topology/Save : Check whether TopologyId exists in Topology Table");
			}
			else
			{
				Log.Error("Topology/Save : Topogy ID is blank");
				throw new Exception("Topology/Save : Topogy ID cant be null");
			}
			
			_transaction=_dbOperation.BeginTransaction(_transaction);
			
			List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			selectedDataList = 	(List<IDataTable>)_dbOperation.Select(topologyTable);
			
			Log.Debug("Topology/Save : Select query executed to check Topology entry is already exist in database");
		
			if (selectedDataList == null)
            {
                throw new Exception("No Records are selected from " + topologyTable.getClass().getName().toString() + " Where TopologyId="+topologyData.topologyId);
            }
			else if (selectedDataList.size() == 0)
            {
				
				 topologyTable.setTopologyId(topologyData.topologyId);
				 
				 DatabaseLayer.Machinecatalog machinecatalog=new Machinecatalog(topologyData.machinecatalog);
				 topologyTable.setMachinecatalog(machinecatalog);
				 
				 DatabaseLayer.Role role= new Role(topologyData.role);
				 topologyTable.setRole(role);
				 
				 topologyTable.setRationale(topologyData.rationale);
				 topologyTable.setOsArchitecture(topologyData.osArchitecture);
				 topologyTable.setOsVersion(topologyData.osVersion);
				 topologyTable.setOsLanguageId(topologyData.osLanguageId);
				 topologyTable.setOsServicePack(topologyData.osServicePack);
				 topologyTable.setBaseVmIdentifier(topologyData.baseVmIdentifier);
				
				 DatabaseLayer.Topology topoarray[]={topologyTable};
				 int rowsAffected = _dbOperation.Insert(topoarray);
				 
				 
				 if (rowsAffected == 0)
                     throw new Exception("Unable To Insert Data InTo Table Topology");
				 
				 else
                 {
                     Log.Debug("Topology/Save : Topology ID " + topologyTable.getTopologyId() + "is successfuly inserted into database");
                     _dbOperation.CommitTransaction(_transaction);
                 }//rows affected are gretter than zero
				 
             }// if selecteddatalist.count = 0
			 else if(selectedDataList.size() > 0)
             {
                 Log.Debug("Topology/Save : Topology ID " + topologyTable.getTopologyId() + " is already present in Topology table");
                 _dbOperation.RollBackTransaction(_transaction);
                 throw new Exception("Topology ID " + topologyTable.getTopologyId() + " is already exixst");

             }
		 }
		 catch(Exception e)
		 {
			 _dbOperation.RollBackTransaction(_transaction);
			 Log.Debug("Topology/Save : Exception in save function, Rollback Transaction ");
             throw e;
		 }
	}
	
	public boolean TopologyExist(int TopologyId) throws Exception
	{
		boolean result = false;
		try
		{			
			_transaction=_dbOperation.BeginTransaction(_transaction);
			DatabaseLayer.Topology topologyTable=new DatabaseLayer.Topology();
			
			Log.Debug(this.toString() + "/TopologyExist: TopologyExist() function called ");
			
			topologyTable.setTopologyId(TopologyId);
			Log.Debug(this.toString() + "/TopologyExist Topology ID: " + TopologyId);
			
			//Check TopologyId entry exist in table.
			 Log.Debug(this.toString() + "/TopologyExist : Check TopologyId entry exist in Topology Table or not.");
			 List<IDataTable> selectedDataList = new ArrayList<IDataTable>();
			 selectedDataList =	 (List<IDataTable>)_dbOperation.Select(topologyTable);
			 Log.Debug(this.toString() + "/TopologyExist: Select query executed to check TopologyId exist or not.");
			 
			 if (selectedDataList == null)
             {
                 result = false;
                 throw new Exception("No record select from Topology Table Where TopologyId: " + TopologyId);
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
