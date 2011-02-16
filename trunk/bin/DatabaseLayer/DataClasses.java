
package DatabaseLayer;

public class DataClasses {

	public interface IDataTable
    {
         // Returns new object with same value for public properties. Its deep cloning of object.
         public void AssignTo(IDataTable iTable);
         public String printString();
    }
	
}

