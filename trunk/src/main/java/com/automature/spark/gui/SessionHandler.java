package com.automature.spark.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SessionHandler {
	/**
	 * 
	 */
	
	private static final String SESSION_FILE= com.automature.spark.engine.SysEnv.LOGLOCATION + com.automature.spark.engine.SysEnv.SLASH + "ZUG Logs"+
				com.automature.spark.engine.SysEnv.SLASH+"sessConfig.ser";
	private static final long serialVersionUID = 1L;
	private Session session;

	public Session getSession() {
		return session;
	}

	public void saveSession() {
		try
		{
			FileOutputStream fileOut =
					new FileOutputStream(SESSION_FILE);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(session);
			out.close();
			fileOut.close();
			//System.out.println("Serialized data is saved in "+SESSION_FILE);
		}catch(IOException i)
		{
			System.err.println("[WARN] :Error saving the session "+i.getMessage());
		}

	}

	public Session getActiveSession() {
		return session;
	}

	public void retriveSession() {
		//System.out.println("retriving session");
		try
		{
			File f=new File(SESSION_FILE);
			if(f==null || !f.exists()){
				session=new Session();
//				f.createNewFile();
				//System.out.println("new session created,file location ");//+f.getAbsolutePath());
				return;
			}
		//	System.out.println("session file location "+f.getAbsolutePath());
			FileInputStream fileIn = new FileInputStream(SESSION_FILE);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			session = (Session) in.readObject();
		//	System.out.println("session"+session);
			in.close();
			fileIn.close();
		}catch(IOException i)
		{
			System.err.println("[WARN] :Error retriving the session "+i.getMessage());
			
		}catch(ClassNotFoundException c)
		{
			//System.out.println("Employee class not found");
			System.err.println("[WARN] :Error retriving the session "+c.getMessage());
		
		}finally{
			if(session==null){
				session=new Session();
			}
		}

	}

	public void addTestsuiteWithConfig(String testsuite,ArrayList<String> config) {
		session.addFileWithSetting(testsuite, config);

	}
	public void addTestsuite(String testsuite) {
		session.addFile(testsuite);
	}

}
