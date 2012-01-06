package BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class TestPlanTopologySetData {
	private int _topologySetID;
	private List<Integer> _topologyList = new ArrayList<Integer>();
	public int get_topologySetID() {
		return _topologySetID;
	}
	public void set_topologySetID(int setID) {
		_topologySetID = setID;
	}
	public List<Integer> get_topologyList() {
		return _topologyList;
	}
	public void set_topologyList(List<Integer> list) {
		_topologyList = list;
	}
}
