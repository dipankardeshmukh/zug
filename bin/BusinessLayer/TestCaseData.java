package BusinessLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestCaseData {
	 private List<Variable> _variableList = new ArrayList<Variable>();
	 private String _testSuiteName;
	 private Integer _testSuiteId;
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
	public Integer get_testSuiteId() {
		return _testSuiteId;
	}
	public void set_testSuiteId(Integer suiteId) {
		_testSuiteId = suiteId;
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
