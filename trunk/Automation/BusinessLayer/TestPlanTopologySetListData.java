package BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class TestPlanTopologySetListData {
	private int _topologySetListID;
	private List<Integer> _topologySetIdList = new ArrayList<Integer>();
	public int get_topologySetListID() {
		return _topologySetListID;
	}
	public void set_topologySetListID(int setListID) {
		_topologySetListID = setListID;
	}
	public List<Integer> get_topologySetIdList() {
		return _topologySetIdList;
	}
	public void set_topologySetIdList(List<Integer> setIdList) {
		_topologySetIdList = setIdList;
	}
}
