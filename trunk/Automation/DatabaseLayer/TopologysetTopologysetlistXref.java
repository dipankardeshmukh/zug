package DatabaseLayer;

import logs.Log;
import DatabaseLayer.DataClasses.IDataTable;

// Generated Nov 2, 2009 5:59:33 PM by Hibernate Tools 3.2.4.GA

/**
 * TopologysetTopologysetlistXref generated by hbm2java
 */
@SuppressWarnings("serial")
public class TopologysetTopologysetlistXref implements java.io.Serializable, IDataTable {

	Integer topologysetTopologysetlistXrefId;
	Integer topologysetId;
	Integer topologysetlistId;

	public TopologysetTopologysetlistXref() {
	}

	public TopologysetTopologysetlistXref(int topologysetTopologysetlistXrefId,
			int topologysetId, int topologysetlistId) {
		this.topologysetTopologysetlistXrefId = topologysetTopologysetlistXrefId;
		this.topologysetId = topologysetId;
		this.topologysetlistId = topologysetlistId;
	}

	public Integer getTopologysetTopologysetlistXrefId() {
		return this.topologysetTopologysetlistXrefId;
	}

	public void setTopologysetTopologysetlistXrefId(
			int topologysetTopologysetlistXrefId) {
		this.topologysetTopologysetlistXrefId = topologysetTopologysetlistXrefId;
	}

	public Integer getTopologysetId() {
		return this.topologysetId;
	}

	public void setTopologysetId(int topologysetId) {
		this.topologysetId = topologysetId;
	}

	public int getTopologysetlistId() {
		return this.topologysetlistId;
	}

	public void setTopologysetlistId(int topologysetlistId) {
		this.topologysetlistId = topologysetlistId;
	}

	@Override
	public void AssignTo(IDataTable table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String printString() {
		String str=null;
		
		str="Topology Set List Id"+getTopologysetlistId()+"\n";
		str+="Topologyset ID:"+getTopologysetId()+"\n";

		Log.Debug("Topologysetlist:printString : + " + str);
		return str;
	}

}
