/// DBOperations.java
/// This File contains methods for database operation- Insert , Select ,Update, BeginTransaction, RollBackTransaction and commit Transaction

package DatabaseLayer;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logs.Log;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.*;

import DatabaseLayer.DataClasses.IDataTable;

@SuppressWarnings("serial")
public class DBOperations implements Serializable
{
	Session sess = null;

	DBProvider _dbProvider= DBProvider.getDBProviderObject();

//	private org.hibernate.SessionFactory sessionFactory;
	
	int recursionLevel = 0;
		
	// Begin the Transaction.
	public synchronized Transaction BeginTransaction(Transaction tx)throws Exception
	{
		Log.Debug("DBOperations/Begin Transaction: Function Entered \n\n");
		try
		{
			sess = _dbProvider.getInstance().getCurrentSession(); 
			tx = sess.beginTransaction(); 
			if(sess.isConnected())
			{
				Log.Debug("DBOperations/Begin Transaction: Connection is open\n\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			try 
			{
					Thread.sleep(10000);
			} 
			catch (InterruptedException e1) 
			{
				
				//e1.printStackTrace();
				
				Log.Debug("EXception :"+e);
			}
			if (recursionLevel <= 3)
            {   
				Log.Debug("DBOperations/ Begin Transaction:Trying to open connection with"+recursionLevel+"times\n\n" );
                recursionLevel++;
                
                BeginTransaction(tx);
            }
            else
            {
                //After 3 level recursion if exception occurred then it will throw.
            	try 
            	{
					throw e;
				} 
            	catch (Exception e1)
            	{
					
					//e1.printStackTrace();
					Log.Debug("Exception "+e);
					Log.Error("DBOperations/ Begin Transaction: Connection could not be opened\n\n");
					Log.Error("DBOperations/ Begin Transaction: Exception:" +e.getMessage()+"\n\n");
				}
            }
					
		}
		
		Log.Debug("DBOperations/ Begin Transaction: Function Exited \n\n");
		
		return tx;
        }
	  // Commit the Transaction.

	public synchronized void CommitTransaction(Transaction tr) throws Exception
    {
		
		Log.Debug("DBOperations/ Commit Transaction: Function Entered \n\n");
		try
		{
			tr.commit();
		}
		catch(ConstraintViolationException e)
		{
			Log.Error("DBOperations/ Commit Transaction: Failed");
			Log.Error("Please send the Debug Log to Automature Inc. along with the database schema");
			throw new Exception("DBOperations/ Commit : Constraint Violation in Database ");
		}
		catch(Exception e)
		{
			Log.Error("DBOperations/ Commit Transaction: Exception occured :  "+ e.getClass().toString() + " - "+ e.getMessage()+"\n\n");
			Log.Error("Please send the Debug Log to Automature Inc. along with the database schema");
			throw new Exception("DBOperations/ Commit Transaction: Exception occured");
			
		}
		
		Log.Debug("DBOperations/ Commit Transaction: Function Exited \n\n");
    }

	  // RollBack the Transaction.
	public synchronized void RollBackTransaction(Transaction tr)throws Exception
    {
		Log.Debug("DBOperations/ RollBack Transaction: Function Entered \n\n");
      try
      {
		//tr.rollback();
      }
		catch(Exception e)
		{
			Log.Error("DBOperations/ RollBack Transaction: Exception occured"+ e.getMessage()+"\n\n");
			throw new Exception("DBOperations/ RollBack Transaction: Exception occured");
		}
		Log.Debug("DBOperations/ RollBack Transaction: Function Exited \n\n");
    }
	//close session
	public synchronized void closeSession()throws Exception
	{
		Log.Debug("DBOperations/ Close Session: Function Entered \n\n");
		Session sess = null;
		try
		{
		 sess = _dbProvider.getCurrentSession();
		 sess.close();
		}
		catch(Exception e)
		{
			Log.Error("DBOperations/ Close Session: Exception occured"+ e.getMessage()+"\n\n");
			throw new Exception("DBOperations/ Close Session: Exception occured");
		}
		
		Log.Debug("DBOperations/ Close Session: Function Exited \n\n");
	}
	
	// Insert array of data objects in database and return count of rows affected. 
	
	public synchronized int Insert(IDataTable[] iDataTableObjects)throws Exception
	{
		Log.Debug("DBOperations/ Insert : Function Entered \n\n");
		
		Session sess = null;
		sess = _dbProvider.getCurrentSession();
		//sess.setFlushMode(FlushMode.)
		int rows=0; 
		try
		{
		 for(int i=0; i<iDataTableObjects.length; i++)
		 {
			 String str=iDataTableObjects[i].getClass().getName();
			 
			 Log.Debug("DBOperations/Insert: Inserting to table "+ str);
			 //inserts into table
			 sess.save(iDataTableObjects[i]);
			 //sess.saveOrUpdate(iDataTableObjects[i]);
			 //sess.flush();
			 
			 rows++;
		 
		 }
		 //sess.flush();
		}
		catch(Exception e)
		{
			Log.Error("DBOperations/ Insert: Exception occured"+ e.getMessage()+"\n\n");
			
		}
		Log.Debug("Number of rows affected: "+ rows);
		Log.Debug("DBOperations: Insert : No of rows inserted "+rows+"\n\n");
		
		Log.Debug("DBOperations: Insert : Function Exited \n\n");
		
		return rows;
	}
	
		
	// Returns collection of data object which satisfy the condition for value set in properties. 
	// Use different object of data class to specify different selection condition.
    // This overload support the condition like columnName1='Value1', columnName2='Value2' AND columnName1='Value1'.
	
	public synchronized List<IDataTable> Select(IDataTable iDataTable)throws Exception 
	{ 
		List<IDataTable> iDataTableList = new ArrayList<IDataTable>();
		List<IDataTable> iDataTableList1 = new ArrayList<IDataTable>();
		
		boolean checkJoin=false;
		
		try
		{
		Class c=iDataTable.getClass();
		Field[] fields = c.getDeclaredFields();
		Class type;
		String name;
		List <FieldInfo> fInfo= new ArrayList<FieldInfo>();
	
		Class className=iDataTable.getClass();
		
		for(Field f:fields)
		{
			Object obj=f.get(iDataTable);
			
			if(null!=obj){
			
				type= f.getType();
			    name = f.getName();
			    
			    FieldInfo fieldInfo=new FieldInfo(name,type,obj);
			    fInfo.add(fieldInfo);    		    
			}
		      
		}
		String selectQuery="from "+ iDataTable.getClass().getName() +" as idatatable ";;
		selectQuery+=whereClause(iDataTable,fInfo);
		//selectQuery+= "join idatatable.testsuite test1 where test1.testSuiteId=1 ";
		//new code
			
		selectQuery=selectQuery.trim();
		//if property is not set then All the records from the table will be fetched
		if(selectQuery.substring(selectQuery.length()-5, selectQuery.length()).equalsIgnoreCase("where"))
		{
			selectQuery=selectQuery.substring(0, selectQuery.length()-5);
			selectQuery=selectQuery.trim();
		}
		
		Log.Debug("DBOperations/Select :Select query is :" + selectQuery);
		//To execute the select command
		
		if(selectQuery.indexOf("join")!=-1)
		{
			checkJoin=true;
		}
		 sess = null;
		sess = _dbProvider.getCurrentSession();
		
		
		 try
		 {
			
			 Log.Debug("````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````");
			 Query query = sess.createQuery(selectQuery);
			 iDataTableList = query.list();
			 Log.Debug("````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````````");
			 if (checkJoin==true)
			 {
				 for(Iterator it=iDataTableList.iterator();it.hasNext();)
				 {
					    Object[] row = (Object[]) it.next();
					    IDataTable obj1= (IDataTable)row[0];
					    iDataTableList1.add(obj1);
				 }
				 for (Iterator iter = iDataTableList1.iterator(); iter.hasNext();) 
			     {          	      
			        	try
			        	{
			        		IDataTable obj = (IDataTable)iter.next();
			        		String selectResult=obj.printString();
			        		Log.Debug("DBOperations/ Select : Result \n\n"+selectResult);
			        		
			        	}catch(Exception e)
			        	{
			        		Log.Error("DBOperations/ Select 1: Exception Occured "+e.getMessage()+"\n\n");
			        		throw new Exception("DBOperations/ Select 1: Exception Occured ");
			        	}
				
			     }
			 }
		
		 //add the selected object to the collection
			 else
			 {
			 	  for (Iterator iter = iDataTableList.iterator(); iter.hasNext();) 
			 	  {          	      
			 		 IDataTable obj= (IDataTable)iter.next();
			 		  iDataTableList1.add(obj);
			 	  }
		       //Iteratation to write the select result to the Log file
			       for (Iterator iter = iDataTableList1.iterator(); iter.hasNext();) 
			       {          	      
			        	try
			        	{
			        		IDataTable obj = (IDataTable)iter.next();
			        		String selectResult=obj.printString();
			        		Log.Debug("DBOperations/ Select : Result \n\n"+selectResult);
			        		
			        	}catch(Exception e)
			        	{
			        		Log.Error("DBOperations/ Select 2: Exception Occured "+e.getMessage()+"\n\n");
			        		throw new Exception("DBOperations/ Select 2: Exception Occured ");
			        	}
				
			        }
			 }
		 }
		 catch(Exception e)
		 {
			 
			 Log.Error("DBOperations/ Select 3: Exception Occured "+e.getMessage()+"\n\n");
     		throw new Exception("DBOperations/ Select 3: Exception Occured ");
		 }
		
		}
		catch(Exception e)
		{
			Log.Error("DBOperations/ Select 4: Exception Occured "+e.getMessage()+"\n\n");
    		throw new Exception("DBOperations/ Select 4: Exception Occured ");
			
		}
		
		return iDataTableList1;
	    
	}

	//Returns the where clause which can be used in select and update queries
	//returns String "where columnName1=value1 and columnName2=value2
	
	public String join(IDataTable iDataTable,List <FieldInfo> fInfo) throws IllegalArgumentException, IllegalAccessException
	{
		String join=" join idatatable.";
		//selectQuery+= "join idatatable.testsuite test1 where test1.testSuiteId=1 and";
		
		Class c=iDataTable.getClass();
		Field[] fields = c.getDeclaredFields();
		Class type;
		String name;
		String fieldType ;
		Object value;
		List <FieldInfo> fInfotarget= new ArrayList<FieldInfo>();
	
		//Class className=iDataTable.getClass();
		String classname=iDataTable.getClass().getName();
		
		for(Field f:fields)
		{
			Object obj=f.get(iDataTable);
			
			if(null!=obj){
			
				
				if( obj instanceof Integer){
					Integer integer = (Integer)obj;
					int intvalue = integer.intValue();
					if(intvalue==0)
						break;
				}
				if( obj instanceof Byte){
					Byte by = (Byte)obj;
					byte bytevalue = by.byteValue();
					if(bytevalue==0)
						break;
				}

				type= f.getType();
			    name = f.getName();
			    
			    FieldInfo fieldInfo=new FieldInfo(name,type,obj);
			    fInfotarget.add(fieldInfo);    		    
			}
		      
		}
		for(FieldInfo list: fInfo )
		{
		
			if(list.getFieldClass().getName()==classname)
			{
				join+=list.getFieldName()+" ";
				join+=" "+list.getFieldClass().getSimpleName()+" where "+list.getFieldClass().getSimpleName()+".";
			}
		}
		
		for(FieldInfo list: fInfotarget )
		{
			join+="";
			if(list.getFieldClass().getName()=="java.lang.String")
			{
				join+=list.getFieldName()+"=";
				join+="'"+list.getValue().toString()+"'"; 
				join+=" and ";
			}
			else if (list.getFieldClass().getName()== "java.util.Date")
			{
				join+=list.getFieldName()+"=";
				join+="'"+list.getValue().toString()+"'"; 
				join+=" and ";
			}
			else
			{
					
				if(list.getFieldClass().getName()!="java.util.Set")
				{
					
					join+=list.getFieldName()+"=";
					
					
					join+=list.getValue().toString();
					join+=" and ";
				}
			}
		}
		
		/*join=join.trim();
		if(join.substring(join.length()-3, join.length()).equalsIgnoreCase("and"))
		join=join.substring(0, join.length()-3);
		join=join.trim();
		*/
		return join;	
	}
	public String whereClause(IDataTable iDataTable, List <FieldInfo> fInfo) throws IllegalArgumentException, IllegalAccessException
	{
		//String query="From "+iDataTable.getClass().toString()+" ";
		String query ="";
		String join =" ";
		boolean flag=false;
		for(FieldInfo list: fInfo )
		{
			query+=" ";
			if(list.getFieldClass().getName()=="java.lang.String")
			{
				query+="idatatable."+list.getFieldName()+"=";
				query+="'"+list.getValue().toString()+"'"; 
				query+=" and ";
			}
			else if (list.getFieldClass().getName()== "java.util.Date")
			{
				query+="idatatable."+list.getFieldName()+"=";
				query+="'"+list.getValue().toString()+"'"; 
				query+=" and ";
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Role")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Role role= (DatabaseLayer.Role) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(role,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Machinecatalog")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Machinecatalog machinecatalog= (Machinecatalog) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(machinecatalog,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			
			else if(list.getFieldClass().getName()=="DatabaseLayer.Topology")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Topology topology= (Topology) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(topology,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Topologyset")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Topologyset topologyset= (Topologyset) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(topologyset,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Testsuite")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Testsuite testsuite= (Testsuite) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(testsuite,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Product")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Product product= (Product) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(product,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Testcycletopologyset")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Testcycletopologyset testcycletopologyset= (Testcycletopologyset) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(testcycletopologyset,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Testcycle")
			{
				//int testsuiteid= Integer.parseInt(list.getValue());
				DatabaseLayer.Testcycle testcycle= (Testcycle) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(testcycle,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Testplan")
			{
				
				DatabaseLayer.Testplan testplan= (Testplan) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(testplan,fInfo);
				
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Testsuitevariables")
			{
				DatabaseLayer.Testsuitevariables testsuitevariables= (Testsuitevariables) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(testsuitevariables,fInfo);
			
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			else if(list.getFieldClass().getName()=="DatabaseLayer.Testcase")
			{
				DatabaseLayer.Testcase testcase= (Testcase) list.getValue();
				String tempquery="";
				String substring[] = null;
				join=join(testcase,fInfo);
			
				if(flag==true)
				{
					substring=query.split("where");
					tempquery=substring[0];
					tempquery+=join +" "+substring[1];
				}
				if(query.equals(" "))
				{
					if(flag==true)
						query=tempquery;
					else
						query=join;
				}
				else
				{
					if(flag==true)
						query=tempquery;
					else
						query=join+" "+query;
				}
				flag=true;
				
			}
			
			else
			{
				query+=" ";
				
				if(list.getFieldClass().getName()!="java.util.Set")
				{
					query+="idatatable."+list.getFieldName()+"=";
					query+=list.getValue().toString();
					query+=" and ";
				}
			}
		}
		if(flag ==false)
		{
			query=" where "+query;
		}
		
		query=query.trim();
		if(query.substring(query.length()-3, query.length()).equalsIgnoreCase("and"))
		query=query.substring(0, query.length()-3);
		query=query.trim();
		return query;
		
	}
	
	//Returns the set clause which can be used in select and update queries
	//returns String "set columnName1=value1 and columnName2=value2
	@SuppressWarnings("deprecation")
	public String setClause(IDataTable iDataTable, List <FieldInfo> fInfo)
	{
		String query ="set "; 
		
		for(FieldInfo list: fInfo )
		{
			query+=" ";
			
			
			
			if(list.getFieldClass().getName()=="java.lang.String")
			{
				
				query+="idatatable."+list.getFieldName()+"=";
				query+="'"+list.getValue().toString()+"'"; 
				query+=" and ";
			}
			else if (list.getFieldClass().getName()== "java.util.Date")
			{
				query+="idatatable."+list.getFieldName()+"=";
				query+="'"+list.getValue()+"'"; 
				query+=" and ";
			}
			else
			{
				query+=" ";
				
				if(list.getFieldClass().getName()!="java.util.Set")
				{
					query+=list.getFieldName()+"=";
					query+=list.getValue().toString();
					query+=" and ";
				}
			}
		}
		query=query.trim();
		if(query.substring(query.length()-3, query.length()).equalsIgnoreCase("and"))
		query=query.substring(0, query.length()-3);
		query=query.trim();
		return query;
		
	}
	
	// Returns collection of data object which satisfy the where clause.
    // Use for complex WhereClause where it contains columns from given data class type or 
    // to select all records from same table. Complex WhereClause means it contains &lt;, &gt;, between, or etc. operations.
   
	public synchronized List<IDataTable> Select(IDataTable iDataTable, String whereclause)throws Exception
	{ 
		List<IDataTable> iDataTableList = new ArrayList<IDataTable>();
		List<IDataTable> iDataTableList1 = new ArrayList<IDataTable>();
		try
		{
		Class c=iDataTable.getClass();
		Field[] fields = c.getDeclaredFields();
		Class type;
		String name;
		String fieldType ;
		Object value;
		List <FieldInfo> fInfo= new ArrayList<FieldInfo>();
	
		Class className=iDataTable.getClass();	
		
		for(Field f:fields)
		{
			Object obj=f.get(iDataTable);
			
			if(null!=obj){
			
				type= f.getType();
			    name = f.getName();
			    
			    FieldInfo fieldInfo=new FieldInfo(name,type,obj);
			    fInfo.add(fieldInfo);    		    
			}
		      
		}
		String str="from "+ iDataTable.getClass().getName() +" as idatatable ";
		str+= whereClause(iDataTable,fInfo);
		boolean checkJoin=false;
		String substring[]=null;
		//if property is not set in the object
		if(str.substring(str.length()-5, str.length()).equalsIgnoreCase("where"))
		{
			if(whereclause.indexOf("join")!=-1)
			{
				substring=str.split("where");
				str=substring[0]+" "+whereclause;
				
			}
			else
			{
				str+=" "+whereclause;
			}
		}
		else
		{
			str += " and "+whereclause;
			
		}
		//if where clause is null and property is not set 
		if(whereclause==null || whereclause=="")
		{
			str.trim();
			
			if(str.substring(str.length()-5, str.length()).equalsIgnoreCase("where"))
			{
				str=str.substring(0, str.length()-5);
				str=str.trim();
			}
			
			
		}
		
			
		//Log.Debug("Str:"+str);
		
		//To execute the select command
		

		if(str.indexOf("join")!=-1)
		{
			checkJoin=true;
		}
		Session sess = null;
		sess = _dbProvider.getCurrentSession();
		
		
		 try
		 {
		 iDataTableList = sess.createQuery(str).list();
		if(checkJoin==true)
		{
			for(Iterator iter=iDataTableList.iterator();iter.hasNext();)
			{
				Object[] row = (Object[])iter.next();
				IDataTable obj1=(IDataTable)row[0];
				IDataTable obj2=(IDataTable)row[1];
				iDataTableList1.add(obj1);
			}
		}
		else
			
		 {//add the selected object to the collection
	        for (Iterator iter = iDataTableList.iterator(); iter.hasNext();) 
	        {          	      
	        	
	        	//iDataTableList1.add(iter.next());
	        	iDataTableList1.add((IDataTable)iter.next());
	        	
	        }
		 
	       //Iteratation to write the select result to the Log file
	        for (Iterator iter = iDataTableList1.iterator(); iter.hasNext();) 
	        {          	      
	        	try
	        	{
	        		IDataTable obj = (IDataTable)iter.next();
	        		String selectResult=obj.printString();
	        		Log.Debug("DBOperations/ Select : Result \n\n"+selectResult);
	        	}catch(Exception e)
	        	{
	        		Log.Error("DBOperations/ Select 11: Exception Occured "+e.getMessage()+"\n\n");
	        		throw new Exception("DBOperations/ Select 11: Exception Occured ");
	        	}
		
	        }
		 }
		 }
		 catch(Exception e)
		 {
			 Log.Error("DBOperations/ Select 12: Exception Occured "+e.getMessage()+"\n\n");
     		throw new Exception("DBOperations/ Select 12: Exception Occured ");
		 }
		}
		catch(Exception e)
		{
			Log.Error("DBOperations/ Select 13: Exception Occured "+e.getMessage()+"\n\n");
    		throw new Exception("DBOperations/ Select 13: Exception Occured ");
			
		}
		return iDataTableList1;
	    
	}

	public synchronized int Update(IDataTable newIdataTable,String whereClause)throws Exception
	{
		int rowCount =0;
		
		try
		{
			//Lists the new data to be set 
			Class newclass=newIdataTable.getClass();
			
			Field[] fields = newclass.getDeclaredFields();
			
			Class type;
			String name;
			String fieldType ;
			Object value;
			
			List <FieldInfo> setlist= new ArrayList<FieldInfo>();
		
			
			Class className=newIdataTable.getClass();
			for(Field f:fields)
			{
				Object obj=f.get(newIdataTable);
				
				if(null!=obj){
				
					type= f.getType();
				    name = f.getName();
				    
				    FieldInfo fieldInfo=new FieldInfo(name,type,obj);
				    setlist.add(fieldInfo);    		    
				}
			      
			}
			String setString=null;
			//If set list is not empty then create set clause			
			if(!setlist.isEmpty())
			{
				
				setString=setClause(newIdataTable,setlist);
				Log.Debug("The set clause is "+ setString);
				Log.Debug("DBOperations/ Update :The set clause is"+setString);
				
			}
			
			String updateQuery="Update "+ newIdataTable.getClass().getName() +" as idatatable ";
			if(setString!=null)
			updateQuery+=" "+setString;
			
			updateQuery=updateQuery.trim();
			//if property is not set then All the records from the table will be fetched
			if(updateQuery.substring(updateQuery.length()-3, updateQuery.length()).equalsIgnoreCase("set"))
			{
				Log.Error("DBOperations/ Update : Exception Occured \n\n");
		    	throw new Exception("DBOperations/ Update :No property set for column value in "+newIdataTable.getClass().getName());
			}
			
			if(whereClause!=null)
				updateQuery+=" "+"where " +whereClause;
			
			Log.Debug("DBOperations/ Update :Update query is"+updateQuery);
			Log.Debug("Update query is"+updateQuery);
			
			//Execute update query
			Session sess = null;
			sess = _dbProvider.getCurrentSession();
			Query query = sess.createQuery(updateQuery);
			rowCount = query.executeUpdate();
			
		}
		catch(Exception e)
		{
			Log.Error("DBOperations/ Update : Exception Occured "+e.getMessage()+"\n\n");
    		throw new Exception("DBOperations/ Update : Exception Occured ");
		}
		return rowCount;
		
	}
	public synchronized int Update(IDataTable newIdataTable, IDataTable oldIdataTable)throws Exception 
	{
		int rowCount =0;
		try
		{
			//Lists the new data to be set 
			Class newclass=newIdataTable.getClass();
			
			Field[] fields = newclass.getDeclaredFields();
			
			Class type;
			String name;
			String fieldType ;
			Object value;
			
			List <FieldInfo> setlist= new ArrayList<FieldInfo>();
		
			
			Class className=newIdataTable.getClass();
			for(Field f:fields)
			{
				Object obj=f.get(newIdataTable);
				
				if(null!=obj){
				
					type= f.getType();
				    name = f.getName();
				    
				    FieldInfo fieldInfo=new FieldInfo(name,type,obj);
				    setlist.add(fieldInfo);    		    
				}
			      
			}
			
			//Lists the old data to be replaced
			Class oldclass=newIdataTable.getClass();
			
			Field[] oldfields = oldclass.getDeclaredFields();
			
			Class oldtype;
			String oldname;
			String oldfieldType ;
			Object oldvalue;
			
			List <FieldInfo> wherelist= new ArrayList<FieldInfo>();
		
			
			Class oldclassName=oldIdataTable.getClass();
			for(Field f:oldfields)
			{
				Object obj=f.get(oldIdataTable);
				
				if(null!=obj){
				
					type= f.getType();
				    name = f.getName();
				    
				    FieldInfo fieldInfo=new FieldInfo(name,type,obj);
				    wherelist.add(fieldInfo);    		    
				}
			      
			}
			String setString=null;
			String wherestring=null;
			
			if(!setlist.isEmpty())
			{
				
				setString=setClause(newIdataTable,setlist);
				Log.Debug("The set clause is "+ setString);
				Log.Debug("DBOperations/ Update :Update set clause is"+setString);
			}
			
			if(!wherelist.isEmpty())
			{
				wherestring=whereClause(oldIdataTable,wherelist);
				Log.Debug("The where clause is"+wherestring);
				Log.Debug("DBOperations/ Update :Update where clause is"+wherestring);
			}
			
			String updateQuery="Update "+ newIdataTable.getClass().getName() +" as idatatable ";
			if(setString!=null)
			updateQuery+=" "+setString;
			
			updateQuery=updateQuery.trim();
			//if property is not set then All the records from the table will be fetched
			if(updateQuery.substring(updateQuery.length()-3, updateQuery.length()).equalsIgnoreCase("set"))
			{
				updateQuery=updateQuery.substring(0, updateQuery.length()-3);
				updateQuery=updateQuery.trim();
				Log.Debug("The set proprty is not set ");
				Log.Debug("DBOperations/Update: OldRecord and NewRecord is same.");
				return 0;
			}
			
			if(wherestring!=null)
			updateQuery+=" "+wherestring;
			
			updateQuery=updateQuery.trim();
			
			//if property is not set then All the records from the table will be updated
			if(updateQuery.substring(updateQuery.length()-5, updateQuery.length()).equalsIgnoreCase("where"))
			{
				updateQuery=updateQuery.substring(0, updateQuery.length()-5);
				updateQuery=updateQuery.trim();
			}
			Log.Debug("UPadate query is: "+updateQuery);
			Log.Debug("DBOperations/Update: UPadate query is: "+updateQuery);
			
			//Execute update query
			Session sess = null;
			sess = _dbProvider.getCurrentSession();
			Query query = sess.createQuery(updateQuery);
			rowCount = query.executeUpdate();
	    }
		catch(Exception e)
		{
			Log.Error("DBOperations/ Update : Exception Occured "+e.getMessage()+"\n\n");
    		throw new Exception("DBOperations/ Update : Exception Occured ");
			
		}
		return rowCount;
		
	}

	public synchronized int Update(IDataTable iDataTableObjects) throws Exception
	{   
		Session sess = null;
		int rows=0; 
		
		Log.Debug("DBOperations/ Update : Function Entered  \n\n");
		
		sess = _dbProvider.getCurrentSession();
		try 
		{     
			 	 String str=iDataTableObjects.getClass().getName();
				 Log.Debug("updating the table:"+ str);
				 //sess.delete(iDataTableObjects[i]);
				 sess.update(iDataTableObjects);
				 
				 rows++;
				 Log.Debug("DBOperations/ Update : Updating the table"+ str+"\n\n");
		}
		catch (Exception e)
		{
			Log.Error("DBOperations/ Update : Exception Occured "+e.getMessage()+"\n\n");
    		throw new Exception("DBOperations/ Update : Exception Occured ");
		}	
		return rows;
	}
	
	public synchronized int merge(IDataTable iDataTableObjects) throws Exception
	{   
		Session sess = null;
		int rows=0; 
		
		Log.Debug("DBOperations/ Merge : Function Entered  \n\n");
		
		sess = _dbProvider.getCurrentSession();
		try 
		{     
			 	 String str=iDataTableObjects.getClass().getName();
				 Log.Debug("updating the table:"+ str);
				 //sess.delete(iDataTableObjects[i]);
				 sess.merge(iDataTableObjects);
				 
				 rows++;
				 Log.Debug("DBOperations/ Merge : Updating the table"+ str+"\n\n");
		}
		catch (Exception e)
		{
			Log.Error("DBOperations/ Merge : Exception Occured "+e.getMessage()+"\n\n");
    		throw new Exception("DBOperations/ Merge : Exception Occured ");
		}	
		return rows;
	}

	public Session getSess() {
		if (sess == null) 
			sess = _dbProvider.getInstance().getCurrentSession(); 
		
		return sess;
	}

	/*
	public synchronized void Delete(IDataTable[] iDataTableObjects)
	{   
		Session sess = null;
		
		Log.Result("Delete : Function Entered \n\n");
		Log.Error("Delete : Function Entered \n\n");
		Log.Debug("Delete : Function Entered  \n\n");
		
		sess = _dbProvider.getCurrentSession();
		try 
		{     
			 for(int i=0; i<iDataTableObjects.length; i++)
			 {
				 String str=iDataTableObjects[i].getClass().getName();
				 Log.Debug("Deleting from table:"+ str);
				 sess.delete(iDataTableObjects[i]);
				 
				 Log.Debug("Delete : Deleting the table"+ str+"\n\n");
				 Log.Result("Delete : Deleting the table "+str +"\n\n");
			 }
		  
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		Log.Result("Delete : Function Exited \n\n");
		Log.Error("Delete : Function Exited \n\n");
		Log.Debug("Delete : Function Exited \n\n");
		
	}
	
	*/
	
}
	
	
	

