package com.automature.spark.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
	    updateInitConfigForTestsuite(testsuite,config);
		session.addFileWithSetting(testsuite, config);
	}
	
	private void updateInitConfigForTestsuite(String testsuite,
			ArrayList<String> config) {
		if(testsuite.contains("\\"))
			testsuite=testsuite.replace("\\", "\\\\");
		File f=new File(System.getProperty("user.dir")+File.separator+"dbconfig.log");
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {}
		String strToWrite="";
		Scanner sc = null;
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {}
		boolean b=false;
		while(sc.hasNextLine())
		{
			String s=sc.nextLine();
			if(s.startsWith("\""+testsuite+"\""+":"))
			{
				s=StringUtils.replace(s, s.split(":\\{")[1], ""+config.toString()+"}");
				b=true;
			}
			strToWrite=strToWrite+s+"\n";
		}
		try{
			FileWriter out = new FileWriter(f.getAbsolutePath());
			if(b)
			out.write(strToWrite);
			else
			out.write(strToWrite+"\""+testsuite+"\""+":{"+config.toString()+"}\n");
			out.close();
		}catch(Exception ex){}
//System.err.println(dbConfigForTestSuite(testsuite));
	}
	public String dbConfigForTestSuite(String testsuite) {
		String s1="";
		if(testsuite.contains("\\"))
			testsuite=testsuite.replace("\\", "\\\\");
		File f=new File(System.getProperty("user.dir")+File.separator+"dbconfig.log");
		if(f.exists())
		{
			Scanner sc = null;
			try {
				sc = new Scanner(f);
			} catch (FileNotFoundException e) {}
			while(sc.hasNextLine())
			{
				String s=sc.nextLine();
				if(s.startsWith("\""+testsuite+"\""+":"))
				{
					s1 = s.split(":\\{")[1];
					s1="{"+s1;
				}
			}
		}
		return StringUtils.substringBetween(s1, "{[","]}");
	}
	public void addTestsuite(String testsuite) {
		session.addFile(testsuite);
	}

}
