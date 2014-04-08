package com.automature.zug.gui;

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
	
	private static final String SESSION_FILE= com.automature.zug.engine.SysEnv.LOGLOCATION + com.automature.zug.engine.SysEnv.SLASH + "ZUG Logs"+
				com.automature.zug.engine.SysEnv.SLASH+"sessConfig.ser";
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
			i.printStackTrace();
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
			i.printStackTrace();
			return;
		}catch(ClassNotFoundException c)
		{
			//System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}

	}

	public void addTestsuiteWithConfig(String testsuite,ArrayList<String> config) {
		session.addFileWithSetting(testsuite, config);

	}
	public void addTestsuite(String testsuite) {
		session.addFile(testsuite);
	}




}
