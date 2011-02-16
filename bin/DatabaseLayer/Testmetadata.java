/// Testmetadata.java
/// This File contains setter getter methods needed for Hibernate mapping

package DatabaseLayer;
import DatabaseLayer.DataClasses.IDataTable;
import logs.Log;
public class Testmetadata implements java.io.Serializable, IDataTable  {

	 Integer testMetaDataId;
	 String name;
	 String value;

	public Testmetadata() {
	}

	public Testmetadata(String name, String value) {
		this.name = name;
		this.value = value;
	}
	public Testmetadata(Integer testMetaDataId,String name, String value) {
		
		this.testMetaDataId=testMetaDataId;
		this.name = name;
		this.value = value;
	}
	public Integer getTestMetaDataId() {
		return this.testMetaDataId;
	}

	public void setTestMetaDataId(Integer testMetaDataId) {
		this.testMetaDataId = testMetaDataId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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
		
		str="Test MetaData Id:"+getTestMetaDataId()+"\n";
		str+="Name: "+getName()+"\n";
		str+="Value: "+getValue()+"\n";
	
		Log.Debug("Testmetadata:printString : + " + str);
		return str;
	}

}
