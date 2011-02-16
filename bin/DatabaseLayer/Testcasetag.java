/// Testcasetag.java
/// This File contains setter getter methods needed for Hibernate mapping

package DatabaseLayer;
import DatabaseLayer.DataClasses.IDataTable;
import logs.Log;
public class Testcasetag implements java.io.Serializable, IDataTable {

	 Integer testCaseTagId;
	 Testcase testcase;
	 String attribute;
	 String value;

	public Testcasetag() {
	}

	public Testcasetag(Integer testCaseTagId,Testcase testcase, String attribute, String value) {
		this.testCaseTagId=testCaseTagId;
		this.testcase = testcase;
		this.attribute = attribute;
		this.value = value;
	}
	public Testcasetag(Testcase testcase, String attribute, String value) {
		this.testcase = testcase;
		this.attribute = attribute;
		this.value = value;
	}
	public Integer getTestCaseTagId() {
		return this.testCaseTagId;
	}

	public void setTestCaseTagId(Integer testCaseTagId) {
		this.testCaseTagId = testCaseTagId;
	}

	public Testcase getTestcase() {
		return this.testcase;
	}

	public void setTestcase(Testcase testcase) {
		this.testcase = testcase;
	}

	public String getAttribute() {
		return this.attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public void AssignTo(IDataTable iTable)
    {
    	
    }
	public String printString()
	{
		String str=null;

		str="TestCase TagId: "+getTestCaseTagId()+"\n";
		str+="Testcase ID:"+testcase.getTestCaseId()+"\n";
		str+="Attribute: "+getAttribute()+"\n";
		str+="Value: "+getValue()+"\n";
		
		Log.Debug("TestcaseTag:printString : + " + str);
		
		return str;
	}
}
