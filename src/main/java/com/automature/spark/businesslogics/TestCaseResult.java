package com.automature.spark.businesslogics;

import java.util.Date;

public class TestCaseResult {
	 private String _testCaseId;
     //private String _testSuiteName;
     private int _testSuiteId;
     private Date _executionDate;
     private String _testEngineerName;
     private String _status;
     private String _comments;
     private String _buildNo;
     private int _testExecution_Time;
     private PerformanceExecutionDetailTable _performanceExecutionDetailTable;
   
	public String get_testCaseId() {
		return _testCaseId;
	}
	public void set_testCaseId(String caseId) {
		_testCaseId = caseId;
	}
	/*public String get_testSuiteName() {
		return _testSuiteName;
	}
	public void set_testSuiteName(String suiteName) {
		_testSuiteName = suiteName;
	}*/
	public int get_testSuiteId() {
		return _testSuiteId;
	}
	public void set_testSuiteId(int suiteId) {
		_testSuiteId = suiteId;
	}
	public Date get_executionDate() {
		return _executionDate;
	}
	public void set_executionDate(Date date) {
		_executionDate = date;
	}
	public String get_testEngineerName() {
		return _testEngineerName;
	}
	public void set_testEngineerName(String engineerName) {
		_testEngineerName = engineerName;
	}
	public String get_status() {
		return _status;
	}
	public void set_status(String _status) {
		this._status = _status;
	}
	public String get_comments() {
		return _comments;
	}
	public void set_comments(String _comments) {
		this._comments = _comments;
	}
	
	public String get_buildNo() {
		return _buildNo;
	}
	public void set_buildNo(String no) {
		_buildNo = no;
	}
	public int get_testExecution_Time() {
		return _testExecution_Time;
	}
	public void set_testExecution_Time(int execution_Time) {
		_testExecution_Time = execution_Time;
	}
	public PerformanceExecutionDetailTable get_performanceExecutionDetailTable() {
		return _performanceExecutionDetailTable;
	}
	public void set_performanceExecutionDetailTable(
			PerformanceExecutionDetailTable executionDetailTable) {
		_performanceExecutionDetailTable = executionDetailTable;
	}   
}
