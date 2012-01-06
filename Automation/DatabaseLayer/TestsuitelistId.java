/// TestsuitelistId.java
/// This File contains setter getter methods needed for Hibernate mapping

package DatabaseLayer;
import DatabaseLayer.DataClasses.IDataTable;
import logs.Log;
public class TestsuitelistId implements java.io.Serializable, IDataTable {

	 int testSuiteListId;
	 Integer testSuiteId;

	public TestsuitelistId() {
	}

	public TestsuitelistId(int testSuiteListId) {
		this.testSuiteListId = testSuiteListId;
	}

	public TestsuitelistId(int testSuiteListId, Integer testSuiteId) {
		this.testSuiteListId = testSuiteListId;
		this.testSuiteId = testSuiteId;
	}

	public int getTestSuiteListId() {
		return this.testSuiteListId;
	}

	public void setTestSuiteListId(int testSuiteListId) {
		this.testSuiteListId = testSuiteListId;
	}

	public Integer getTestSuiteId() {
		return this.testSuiteId;
	}

	public void setTestSuiteId(Integer testSuiteId) {
		this.testSuiteId = testSuiteId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TestsuitelistId))
			return false;
		TestsuitelistId castOther = (TestsuitelistId) other;

		return (this.getTestSuiteListId() == castOther.getTestSuiteListId())
				&& ((this.getTestSuiteId() == castOther.getTestSuiteId()) || (this
						.getTestSuiteId() != null
						&& castOther.getTestSuiteId() != null && this
						.getTestSuiteId().equals(castOther.getTestSuiteId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getTestSuiteListId();
		result = 37
				* result
				+ (getTestSuiteId() == null ? 0 : this.getTestSuiteId()
						.hashCode());
		return result;
	}
	public void AssignTo(IDataTable iTable)
    {
    	
    }
	public String printString()
	{
		String str=null;

		str="Test Suite List Id: "+getTestSuiteListId()+"\n";
		str+="Test Suite Id: "+getTestSuiteId()+"\n";
		
		Log.Debug("TestsuitelistId:printString : + " + str);
		
		return str;
	}
}
