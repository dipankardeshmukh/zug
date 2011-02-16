/// Testsuitetag.java
/// This File contains setter getter methods needed for Hibernate mapping

package DatabaseLayer;
import DatabaseLayer.DataClasses.IDataTable;
import logs.Log;
public class Testsuitetag implements java.io.Serializable, IDataTable  {

	 Integer testSuiteTagId;
	 Testsuite testsuite;
	 String tagName;
	 String value;

	public Testsuitetag() {
	}

	public Testsuitetag(Testsuite testsuite, String tagName, String value) {
		this.testsuite = testsuite;
		this.tagName = tagName;
		this.value = value;
	}

	public Testsuitetag(Integer testSuiteTagId, Testsuite testsuite, String tagName, String value) {
		this.testSuiteTagId=testSuiteTagId;
		this.testsuite = testsuite;
		this.tagName = tagName;
		this.value = value;
	}

	public Integer getTestSuiteTagId() {
		return this.testSuiteTagId;
	}

	public void setTestSuiteTagId(Integer testSuiteTagId) {
		this.testSuiteTagId = testSuiteTagId;
	}

	public Testsuite getTestsuite() {
		return this.testsuite;
	}

	public void setTestsuite(Testsuite testsuite) {
		this.testsuite = testsuite;
	}

	public String getTagName() {
		return this.tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
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
		str="Test Suite Tag Id: "+getTestSuiteTagId()+"\n";
		str+="Testsuite ID:"+ testsuite.getTestSuiteId()+"\n";
		str+="Tag Name: "+getTagName()+"\n";
		str+="Value: "+getValue()+"\n";
	
		Log.Debug("Testsuitetag:printString : + " + str);
		return str;
	}


}
