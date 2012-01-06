package BusinessLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestCycleData {
	 Integer testCycleId;
	 int testplan_id;
	 Date startedDate;
	 Date endDate;
	 Integer initializationTime;
	 Integer executionTime;
	 Date modificationDate;
	 private List<TopologySetResultData> _topologySetResultDataList = new ArrayList<TopologySetResultData>();
	 
	public List<TopologySetResultData> get_topologySetResultDataList() {
		return _topologySetResultDataList;
	}
	public void set_topologySetResultDataList(
			List<TopologySetResultData> setResultDataList) {
		_topologySetResultDataList = setResultDataList;
	}
	public Integer getTestCycleId() {
		return testCycleId;
	}
	public void setTestCycleId(Integer testCycleId) {
		this.testCycleId = testCycleId;
	}
	public int getTestplan_id() {
		return testplan_id;
	}
	public void setTestplan_id(int testplan_id) {
		this.testplan_id = testplan_id;
	}
	public Date getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	 
}
