/// DBProvider.java
/// This File provides the session factory object

package DatabaseLayer;
import logs.Log;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DBProvider
{
	
	private org.hibernate.SessionFactory sessionFactory;	
	Configuration cfg = null; 
//	AnnotationConfiguration anotationcfg;
	private static DBProvider _dbprovider;
	/*DBConnection dbconnection = new DBConnection();
	private String _userName;
	 private String _password;
	 private String _dbName;
	 private String _dbHostName;
	 
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
	}*/
	private DBProvider()
	{  
		super();
		
	}	
	
	public void buildSession(DBConnection conn)
	{
		cfg = new Configuration().configure();
		
		//	anotationcfg= new AnnotationConfiguration();
		//sessionFactory= anotationcfg.configure().buildSessionFactory();
		//	cfg.setProperty(propertyName, value)
	
	     String url= "jdbc:mysql://"+conn.get_dbHostName()+":3306/"+conn.get_dbName();
//		 String url= "jdbc:mysql://"+conn.get_dbHostName()+":4050/"+conn.get_dbName();
		 Log.Debug("DBProvider:buildSession -> Url is :"+ url);
		 cfg.setProperty("hibernate.connection.url",url);
		 cfg.setProperty("hibernate.connection.username",conn.get_userName());
		 
		 if(conn.get_password()!=null)
			 cfg.setProperty("hibernate.connection.password",conn.get_password());
		 else
			 cfg.setProperty("connection.password","");
				
		sessionFactory = cfg.buildSessionFactory();
		
	}
	public static DBProvider getDBProviderObject()
    {
      if (_dbprovider == null)
            	  _dbprovider = new DBProvider();
      return _dbprovider;
    }

  
	public SessionFactory getInstance()
	{	
		return sessionFactory;
	}  
	/**   * Opens a session and will not bind it to a session context   * @return the session   */
	
	public Session openSession()
	{	
		return sessionFactory.openSession();	
	}
	
	/**   * Returns a session from the session context. If there is no session in the context it opens a session,   * stores it in the context and returns it.	 * This factory is intended to be used with a hibernate.cfg.xml	 * including the following property <property	 * name="current_session_context_class">thread<.property> This would return	 * the current open session or if this does not exist, will create a new	 * session	 * 	 * @return the session	 */	
	public Session getCurrentSession()
	{
		return sessionFactory.getCurrentSession();	
	}
	
	/**   * closes the session factory   */
	public void close()
	{		
		if (sessionFactory != null)			
			sessionFactory.close();	
			sessionFactory = null;		
	}
}




