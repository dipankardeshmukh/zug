package DatabaseLayer;

// Generated Nov 2, 2009 5:59:33 PM by Hibernate Tools 3.2.4.GA

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import logs.Log;
import DatabaseLayer.DataClasses.IDataTable;

/**
 * Testcycle generated by hbm2java
 */
@SuppressWarnings("serial")
public class Testcycle implements java.io.Serializable, IDataTable {

	Integer testCycleId;
	Testplan testplan;
	Date startedDate;
	Integer initializationTime;
	Integer executionTime;
	Date modificationDate;
	String testCycleDescription;
	Set<Testcycledetail> testcycledetails = new HashSet<Testcycledetail> (0);
	Set<Testcyclesummary> testcyclesummaries = new HashSet<Testcyclesummary> (0);
	Set<Testcycletopologyset> testcycletopologysets = new HashSet<Testcycletopologyset> (0);

	public Testcycle() {
	}

	public Testcycle(int testCycleId, Testplan testplan) {
		this.testCycleId = testCycleId;
		this.testplan = testplan;
	}

	public Testcycle(int testCycleId, Testplan testplan, Date startedDate,
			Integer initializationTime, Integer executionTime,
			Date modificationDate, String testCycleDescription,
			Set<Testcycledetail> testcycledetails, Set<Testcyclesummary> testcyclesummaries,
			Set<Testcycletopologyset> testcycletopologysets) {
		this.testCycleId = testCycleId;
		this.testplan = testplan;
		this.startedDate = startedDate;
		this.initializationTime = initializationTime;
		this.executionTime = executionTime;
		this.modificationDate = modificationDate;
		this.testCycleDescription = testCycleDescription;
		this.testcycledetails = testcycledetails;
		this.testcyclesummaries = testcyclesummaries;
		this.testcycletopologysets = testcycletopologysets;
	}

	public Testcycle(int testcycleId) {
		this.testCycleId = testcycleId;
	}

	public Integer getTestCycleId() {
		return this.testCycleId;
	}

	public void setTestCycleId(int testCycleId) {
		this.testCycleId = testCycleId;
	}

	public Testplan getTestplan() {
		return this.testplan;
	}

	public void setTestplan(Testplan testplan) {
		this.testplan = testplan;
	}

	public Date getStartedDate() {
		return this.startedDate;
	}

	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}

	public Integer getInitializationTime() {
		return this.initializationTime;
	}

	public void setInitializationTime(Integer initializationTime) {
		this.initializationTime = initializationTime;
	}

	public Integer getExecutionTime() {
		return this.executionTime;
	}

	public void setExecutionTime(Integer executionTime) {
		this.executionTime = executionTime;
	}

	public Date getModificationDate() {
		return this.modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getTestCycleDescription() {
		return this.testCycleDescription;
	}

	public void setTestCycleDescription(String testCycleDescription) {
		this.testCycleDescription = testCycleDescription;
	}

	public Set<Testcycledetail> getTestcycledetails() {
		return this.testcycledetails;
	}

	public void setTestcycledetails(Set<Testcycledetail> testcycledetails) {
		this.testcycledetails = testcycledetails;
	}

	public Set<Testcyclesummary> getTestcyclesummaries() {
		return this.testcyclesummaries;
	}

	public void setTestcyclesummaries(Set<Testcyclesummary> testcyclesummaries) {
		this.testcyclesummaries = testcyclesummaries;
	}

	public Set<Testcycletopologyset> getTestcycletopologysets() {
		return this.testcycletopologysets;
	}

	public void setTestcycletopologysets(Set<Testcycletopologyset> testcycletopologysets) {
		this.testcycletopologysets = testcycletopologysets;
	}

	@Override
	public void AssignTo(IDataTable table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String printString() {
		String str=null;
		
		str="Test Cycle Id: "+getTestCycleId()+"\n";
		str+="Testplan ID:"+testplan.getTestPlanId()+"\n";
		str+="Started Date:"+getStartedDate()+"\n";
		str+="Initialization Time: "+getInitializationTime()+"\n";
		str+="Execution Time: "+getExecutionTime()+"\n";
		str+="Modification Date: "+getModificationDate()+"\n";
	
		Log.Debug("TestCycleID:printString : + " + str);
		
		return str;
	}

}
