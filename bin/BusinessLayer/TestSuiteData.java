package BusinessLayer;

import java.util.Date;

public class TestSuiteData {
	Integer testSuiteId;
	String role;
	int product;
	 String testSuiteName;
	 String comment;
	 Date creationDate;
	 Date modificationDate;
	 String createdBy;
	 String modifiedBy;
	 String status;
	 Integer elapsedTimeMin;
	 Integer elapsedTimeMax;
	 String testPlanFilePath;
	 Integer initializationTime;
	 Integer executionTime;
	public Integer getTestSuiteId() {
		return testSuiteId;
	}
	public void setTestSuiteId(Integer testSuiteId) {
		this.testSuiteId = testSuiteId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getProduct() {
		return product;
	}
	public void setProduct(int product) {
		this.product = product;
	}

	public String getTestSuiteName() {
		return testSuiteName;
	}
	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getElapsedTimeMin() {
		return elapsedTimeMin;
	}
	public void setElapsedTimeMin(Integer elapsedTimeMin) {
		this.elapsedTimeMin = elapsedTimeMin;
	}
	public Integer getElapsedTimeMax() {
		return elapsedTimeMax;
	}
	public void setElapsedTimeMax(Integer elapsedTimeMax) {
		this.elapsedTimeMax = elapsedTimeMax;
	}
	public String getTestPlanFilePath() {
		return testPlanFilePath;
	}
	public void setTestPlanFilePath(String testPlanFilePath) {
		this.testPlanFilePath = testPlanFilePath;
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
	 
}
