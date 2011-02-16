package BusinessLayer;

import java.util.Date;

public class TestPlanData {
	 int testPlanId;
	 int testplantemplate_id;
	 int topologyset_id;
	 Integer testSuiteListId;
	 String testPlanShortDesc;
	 String testPlanLongDesc;
	 Date creationDate;
	 Date modificationDate;
	 String createdBy;
	 String modifiedBy;
	 String setupScript;
	 String tearDownScript;
	 String status;
	 String product;
	 String comment;
	 Integer topologySetListId;
	 String codeName;
	 
	 
	public int getTestPlanId() {
		return testPlanId;
	}
	public void setTestPlanId(int testPlanId) {
		this.testPlanId = testPlanId;
	}
	public int getTestplantemplate_id() {
		return testplantemplate_id;
	}
	public void setTestplantemplate_id(int testplantemplate_id) {
		this.testplantemplate_id = testplantemplate_id;
	}
	public int getTopologyset_id() {
		return topologyset_id;
	}
	public void setTopologyset_id(int topologyset_id) {
		this.topologyset_id = topologyset_id;
	}
	public Integer getTestSuiteListId() {
		return testSuiteListId;
	}
	public void setTestSuiteListId(Integer testSuiteListId) {
		this.testSuiteListId = testSuiteListId;
	}
	public String getTestPlanShortDesc() {
		return testPlanShortDesc;
	}
	public void setTestPlanShortDesc(String testPlanShortDesc) {
		this.testPlanShortDesc = testPlanShortDesc;
	}
	public String getTestPlanLongDesc() {
		return testPlanLongDesc;
	}
	public void setTestPlanLongDesc(String testPlanLongDesc) {
		this.testPlanLongDesc = testPlanLongDesc;
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
	public String getSetupScript() {
		return setupScript;
	}
	public void setSetupScript(String setupScript) {
		this.setupScript = setupScript;
	}
	public String getTearDownScript() {
		return tearDownScript;
	}
	public void setTearDownScript(String tearDownScript) {
		this.tearDownScript = tearDownScript;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getTopologySetListId() {
		return topologySetListId;
	}
	public void setTopologySetListId(Integer topologySetListId) {
		this.topologySetListId = topologySetListId;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
}
