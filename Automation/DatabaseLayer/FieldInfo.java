/// FieldInfo.java
/// This File is used as a data structure 

package DatabaseLayer;

public class FieldInfo {

	String fieldName;
	Class fieldClass;
	Object value;
	
	public FieldInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public FieldInfo(String fName,Class<?> c, Object obj )
	{
		fieldName=fName;
		this.fieldClass=c;
		value=obj;
					
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class getFieldClass() {
		return fieldClass;
	}

	public void setFieldClass(Class fieldClass) {
		this.fieldClass = fieldClass;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}
