package DatabaseLayer;


public class DBConnection {

	 private String _userName;
	 private String _password;
	 private String _dbName;
	 private String _dbHostName;
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
		_dbHostName = hostName;
	}
	
	
	 public void InitializeDbConnection()
	 {
		
		 _Dbprovider= DBProvider.getDBProviderObject();
		 _Dbprovider.buildSession(this);
	 }
	
}
