/***
 * http://stackoverflow.com/questions/1719594/iterative-cartesian-product-in-java - Code directly copied from this website with 
 * very minor modifications.
 */
package ZUG;
import java.util.*;

class Tuple<T> {
    private List<T> list = new ArrayList<T>();

    public void add(T t) { list.add(t); }

    public void addAll(Tuple<T> subT) {
        for (T t : subT.list) {
            list.add(t);
        }
    }
    
    public Object[] ToArray()
    {
    	return list.toArray();
    }

    public String toString() {
        String result = "(";

        for (T t : list) { result += t + ", "; }

        result = result.substring(0, result.length() - 2);
        result += " )";

        return result;
    } 
}


public class CartesianProduct{
	
	public static <T> List<Tuple<T>> cartesianProduct(ArrayList<ArrayList<T>> sets) {
	    List<Tuple<T>> tuples = new ArrayList<Tuple<T>>();
	    
	    if(sets.size() == 0)
	    	return null;

	    if (sets.size() == 1) {
	        ArrayList<T> set = sets.get(0);
	        for (T t : set) {
	            Tuple<T> tuple = new Tuple<T>();
	            tuple.add(t);    
	            tuples.add(tuple);
	        }
	    } else {
	        ArrayList<T> set = sets.remove(0);
	        List<Tuple<T>> subTuples = cartesianProduct(sets);
	        for (Tuple<T> subTuple : subTuples) {
	            for (T t : set) {
	                Tuple<T> tuple = new Tuple<T>();
	                tuple.add(t);
	                tuple.addAll(subTuple);
	                tuples.add(tuple);
	            }
	        }
	    }

	    return tuples;
	}

	public static <T> List<Tuple<T>> indexedProduct (ArrayList<ArrayList<T>> sets) {
		List<Tuple<T>> tuples = new ArrayList<Tuple<T>>();
		
	    if(sets.size() == 0)
	    	return null;
		
	    if (sets.size() == 1) {
	        ArrayList<T> set = sets.get(0);
	        for (T t : set) {
	            Tuple<T> tuple = new Tuple<T>();
	            tuple.add(t);    
	            tuples.add(tuple);
	        }
	    } else {
	        ArrayList<T> set = sets.remove(0);
	        List<Tuple<T>> subTuples = indexedProduct(sets);
	        if(set.size()!=1 && subTuples.size()!=1)
	        {
	        	for (int i=0;i<subTuples.size();i++)
		        {
		        	Tuple<T> tuple = new Tuple<T>();
		        	tuple.add(set.get(i));
		        	tuple.addAll(subTuples.get(i));
		            tuples.add(tuple);
		        }
	        }
	        else if(set.size()==1 && subTuples.size()!=1)
	        {
	        	for(int i=0;i<subTuples.size();i++)
	        	{
	        		Tuple<T> tuple = new Tuple<T>();
	        		tuple.add(set.get(0));
	        		tuple.addAll(subTuples.get(i));
	        		tuples.add(tuple);
	        	}
	        }
	        else if(set.size()!=1 && subTuples.size()==1)
	        {
	        	for(int i=0;i<set.size();i++)
	        	{
	        		Tuple<T> tuple = new Tuple<T>();
	        		tuple.add(set.get(i));
	        		tuple.addAll(subTuples.get(0));
	        		tuples.add(tuple);
	        	}
	        }
	        else
	        {
	        	Tuple<T> tuple = new Tuple<T>();
        		tuple.add(set.get(0));
        		tuple.addAll(subTuples.get(0));
        		tuples.add(tuple);
	        }
	    }

	    return tuples;

	}


}