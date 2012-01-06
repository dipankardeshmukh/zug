package BusinessLayer;

import logs.Log;
import DatabaseLayer.DBConnection;

public class Initialize 
{
	
	private static DBConnection _dbConnection= new DBConnection();
	
	
	public static String getUserName()
	{
		return _dbConnection.get_userName();
		
	}
	public static void setUserName(String Username)
	{
	
		if(_dbConnection.get_userName()==null)
		{
			_dbConnection.set_userName(Username);
			
		}
	}
	
	public static String getPassword()
	{
	
		return _dbConnection.get_password();
		
	}
	public static void setPassword(String Password)
	{
	
		if(_dbConnection.get_password()==null)
		{
			_dbConnection.set_password(Password);
			
		}
	}
	
	public static String getHostName()
	{
	
		return _dbConnection.get_dbHostName();
		
	}
	public static void setHostName(String hostname)
	{
	
		if(_dbConnection.get_dbHostName()==null)
		{
			_dbConnection.set_dbHostName(hostname);
			_dbConnection.set_dbPort(hostname);
			
		}
	}
	
	public static String getDatbaseName()
	{
		
		return _dbConnection.get_dbHostName();
		
	}
	public static void setDatbaseName(String Dbname)
	{
		
		if(_dbConnection.get_dbName()==null)
		{
			_dbConnection.set_dbName(Dbname);
			
		}
	}
	 public static void InitializeDbConnection()
     {
	
		 Log.Debug("BusinessLayer.Initialize.InitializeDbConnection(): Function started.");
		
		/* _dbConnection.set_dbHostName(getHostName());
		 _dbConnection.set_dbName(getDatbaseName());
		 _dbConnection.set_password(getPassword());
		 _dbConnection.set_userName(getUserName());*/
		 _dbConnection.InitializeDbConnection();
         Log.Debug("Initialize.InitializeDbConnection(): Function finished.");
     }
}
