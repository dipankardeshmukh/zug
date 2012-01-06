package DatabaseLayer;

// Generated Nov 2, 2009 5:59:33 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import logs.Log;
import DatabaseLayer.DataClasses.IDataTable;

/**
 * Testcase generated by hbm2java
 */
public class Testcase implements java.io.Serializable, IDataTable {

	/**
	 * 
	 */
	Integer testCaseId;
	Testsuite testsuite;
	String testCaseIdentifier;
	String description;
	Date creationDate;
	Date modificationDate;
	String testSteps;
	String verifySteps;
	Set<Testcasevariablevalue> testcasevariablevalues = new HashSet<Testcasevariablevalue> (0);
	Set<Testexecutiondetails> testexecutiondetailses = new HashSet<Testexecutiondetails> (0);
	Set<Testcasetorequirementsxref> testcasetorequirementsxrefs = new HashSet<Testcasetorequirementsxref> (0);
	Set<Testcaseignorelist> testcaseignorelists = new HashSet<Testcaseignorelist> (0);

	public Testcase() {
	}

	public Testcase(int testCaseId, Testsuite testsuite,
			String testCaseIdentifier, String verifySteps) {
		this.testCaseId = testCaseId;
		this.testsuite = testsuite;
		this.testCaseIdentifier = testCaseIdentifier;
		this.verifySteps = verifySteps;
	}

	public Testcase(int testCaseId, Testsuite testsuite,
			String testCaseIdentifier, String description, Date creationDate,
			Date modificationDate, String testSteps, String verifySteps,
			Set<Testcasevariablevalue> testcasevariablevalues, Set<Testexecutiondetails> testexecutiondetailses,
			Set<Testcasetorequirementsxref> testcasetorequirementsxrefs, Set<Testcaseignorelist> testcaseignorelists) {
		this.testCaseId = testCaseId;
		this.testsuite = testsuite;
		this.testCaseIdentifier = testCaseIdentifier;
		this.description = description;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.testSteps = testSteps;
		this.verifySteps = verifySteps;
		this.testcasevariablevalues = testcasevariablevalues;
		this.testexecutiondetailses = testexecutiondetailses;
		this.testcasetorequirementsxrefs = testcasetorequirementsxrefs;
		this.testcaseignorelists = testcaseignorelists;
	}

	public Testcase(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public Integer getTestCaseId() {
		return this.testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public Testsuite getTestsuite() {
		return this.testsuite;
	}

	public void setTestsuite(Testsuite testsuite) {
		this.testsuite = testsuite;
	}

	public String getTestCaseIdentifier() {
		return this.testCaseIdentifier;
	}

	public void setTestCaseIdentifier(String testCaseIdentifier) {
		this.testCaseIdentifier = testCaseIdentifier;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return this.modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getTestSteps() {
		return this.testSteps;
	}

	public void setTestSteps(String testSteps) {
		this.testSteps = testSteps;
	}

	public String getVerifySteps() {
		return this.verifySteps;
	}

	public void setVerifySteps(String verifySteps) {
		this.verifySteps = verifySteps;
	}

	public Set<Testcasevariablevalue> getTestcasevariablevalues() {
		return this.testcasevariablevalues;
	}

	public void setTestcasevariablevalues(Set<Testcasevariablevalue> testcasevariablevalues) {
		this.testcasevariablevalues = testcasevariablevalues;
	}

	public Set<Testexecutiondetails> getTestexecutiondetailses() {
		return this.testexecutiondetailses;
	}

	public void setTestexecutiondetailses(Set<Testexecutiondetails> testexecutiondetailses) {
		this.testexecutiondetailses = testexecutiondetailses;
	}

	public Set<Testcasetorequirementsxref> getTestcasetorequirementsxrefs() {
		return this.testcasetorequirementsxrefs;
	}

	public void setTestcasetorequirementsxrefs(Set<Testcasetorequirementsxref> testcasetorequirementsxrefs) {
		this.testcasetorequirementsxrefs = testcasetorequirementsxrefs;
	}

	public Set<Testcaseignorelist> getTestcaseignorelists() {
		return this.testcaseignorelists;
	}

	public void setTestcaseignorelists(Set<Testcaseignorelist> testcaseignorelists) {
		this.testcaseignorelists = testcaseignorelists;
	}

	@Override
	public void AssignTo(IDataTable table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String printString() {
		String str=null;		
		
		str="Test Case Id:"+getTestCaseId()+"\n";
		str+="Testsuite ID: "+testsuite.getTestSuiteId()+"\n";
		str+="Test Case Identifier:"+getTestCaseIdentifier()+"\n";
		str+="Description:"+getDescription()+"\n";
		str+="Creation Date: "+getCreationDate()+"\n";
		str+="Modification Date: "+getModificationDate()+"\n";
		str+="Test Steps: "+getTestSteps()+"\n";
		
		Log.Debug("Testcase:printString : + " + str);
		
		return str;
	}

}
