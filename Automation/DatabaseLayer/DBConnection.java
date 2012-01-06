package DatabaseLayer;


public class DBConnection {

	 private String _userName;
	 private String _password;
	 private String _dbName;
	 private String _dbHostName;
	 private String _port;
	 DBProvider _Dbprovider;
	 
	public String get_userName() {
		return _userName;
	}
	public void set_userName(String name) {
		_userName = name;
	}
	public String get_password() {
		return _password;
	}
	public void set_password(String _password) {
		this._password = _password;
	}
	public String get_dbName() {
		return _dbName;
	}
	public void set_dbName(String name) {
		_dbName = name;
	}
	public String get_dbHostName() {
		return _dbHostName;
	}
	public void set_dbHostName(String hostName) {
		if(hostName.contains(":"))
		{
			String tempSplit[]=null;
			tempSplit=hostName.split(":");
			_dbHostName=tempSplit[0];
		}
		else {
			_dbHostName=hostName;
		}
	}
	public void set_dbPort(String port)
	{
		
		if(port.contains(":"))
		{
			String tempSplit[]=null;
			tempSplit=port.split(":");
			_port=tempSplit[1];
		}
		else
		{
			_port="3306";
		}
	}
	public String get_port()
	{
		return _port;
	}
	
	 public void InitializeDbConnection()
	 {
		
		 _Dbprovider= DBProvider.getDBProviderObject();
		 _Dbprovider.buildSession(this);
	 }
	
}
