/***
 * XMLUtility.java
 * This is the Utility class which provides functions for different uses...
 *
 */
package com.automature.spark.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.management.OperatingSystemMXBean;


//import sun.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import com.automature.spark.util.Log;

import java.lang.management.ManagementFactory;

public class Utility {

	public static final String DATE_FORMAT_NOW = "yyyyMMdd-HHmmss";
	public static final String DATE_FORMAT_NOW_1 = "yyyy:MM:dd-HH:mm:ss";
	public static final int MB = 1024 * 1024;
	public static final long MEM_1MB_bytes = 1048576;
	public static final long MEM_1GB_bytes = 1073741824;
	public static com.sun.management.OperatingSystemMXBean osMbean;

	/**
	 * gets the current date time in specific format......
	 * @return current date time in  yyyyMMdd-HHmmss format
	 */
	public static String dateAsString() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}
	/**
	 *
	 * @return date as string
	 */
	public static String getCurrentDateAsString()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW_1);
		return sdf.format(cal.getTime());
	}
	public static Date dateNow() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return cal.getTime();
	}

	public static List<String> removeDuplicatesFromArrayList(List<String> duplicateList)
	{
		//System.out.println("duplicate list "+duplicateList);
		int size=duplicateList.size();
		int out=0;
		final Set<String> encountered=new HashSet<String>();
		for(Iterator<String> iter=duplicateList.iterator();iter.hasNext();)
		{
			final String obj = iter.next();
			final boolean first=encountered.add(obj);
			if(!first)
			{
				iter.remove();
			}

		}
		List<String> uniqueList=new ArrayList<String>();
		for (Iterator iterator = encountered.iterator(); iterator.hasNext();) {
			String element = (String) iterator.next();
			uniqueList.add(element);

		}

		//System.out.println("unique list "+uniqueList);
		return uniqueList;

	}
	/**
	 *
	 * @param list
	 * @param elemnt
	 * @param pos_index
	 * @return final arraylist
	 */
	public static ArrayList<?> addingElementToArrayList(ArrayList<?> list,Object elemnt,int pos_index)
	{
		//System.out.println("Method is called with "+pos_index);
		ArrayList<Object> finalList=new ArrayList<Object>();
		//Implementing the arraylist addition code.
		//System.out.println("The total size is "+list.size());
		Object temstorelm=list.get(pos_index);


		for(int i=0;i<list.size();i++)
		{

			if(i==pos_index)
			{
				finalList.add(pos_index, elemnt);
				finalList.add(pos_index+1, temstorelm);
			}
			else{
				finalList.add(list.get(i));
			}

		}

		//System.out.println("Final List size is "+finalList.size());
		return finalList;
	}

	/**
	 *   This function will Trim the given character from the string from start or end depending upon the value of the start
	 *   @param stringToTrim String which need to be trim.
	 *   @param charToTrim Character to be remove from string.
	 *   @param start value can be 0 or anything. If start=0, end character will be trimmed.
	 *   											If start <> 0, start character will be trimmed. 
	 */
	public static String TrimStartEnd(String stringToTrim, char charToTrim, int start) {
		//trim from end
		if (start == 0) {
			while (stringToTrim.length() >= 2 && stringToTrim.endsWith(String.valueOf(charToTrim))) {
				stringToTrim = stringToTrim.substring(0, stringToTrim.length() - 1);
			}

		} else {
			while (stringToTrim.length() >= 2 && stringToTrim.startsWith(String.valueOf(charToTrim))) {
				stringToTrim = stringToTrim.substring(1, stringToTrim.length());
			}
		}

		if ((stringToTrim.length() == 1) && (stringToTrim.charAt(0) == charToTrim)) {
			stringToTrim = "";
		}

		return stringToTrim;
	}// function TrimStartEnd End.

	/// joins a particular string between the strings in the array of string 
	public static String join(String delimeter, String[] strArray, int start, int end) {
		StringBuilder stringToReturn = new StringBuilder();

		for (int i = 0; i < start; i++) {
			stringToReturn.append(strArray[i]);
		}
		for (int i = start; i < end; i++) {
			stringToReturn.append(strArray[i]);
			if (i != strArray.length - 1)//not putting any delimeter after the last string
			{
				stringToReturn.append(delimeter);
			}
		}
		for (int i = end; i < strArray.length; i++)//previously it was length-1 now its not
		{
			stringToReturn.append(strArray[i]);
		}
		return stringToReturn.toString();
	}

	/**
	 * Delete the directory and its content
	 * @param path Directory to be delete
	 * @return success or failure
	 */
	public static boolean deleteDirectory(File path) throws Exception {
		if (path.exists()) {
			File files[] = path.listFiles();
			if (path.isDirectory()) {
				for (int i = 0; i < files.length; i++) {
					//System.gc();
					if (files[i].isDirectory()) {
						if (!deleteDirectory(files[i])) {
							System.out.println("Could not delete directory : " + files[i].getName());
						}
					} else {
						try {
							if (!files[i].delete()) {
								System.out.println("Could not delete file : " + files[i].getAbsolutePath());
							}

							// print attributes

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		//System.gc();
		return path.delete();
	}

	/**
	 * Copies the source file to given destination.
	 * @param source File to be copy.
	 * @param Destination Destination File path.
	 */
	public static void copyFile(String source, String Destination) throws IOException {
		InputStream from = null;
		OutputStream to = null;
		try {
			from = new FileInputStream(new File(source));
			to = new FileOutputStream(new File(Destination));

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytesRead);
				to.flush();
			}
		} catch (IOException iOException) {
			Log.Debug("Utility/copyFile():Unable to copy file. Error message is : " + iOException.getMessage());
			Log.Error("Utility/copyFile():Unable to copy file. Error message is : " + iOException.getMessage());
			throw iOException;
		} finally {
			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Recursive way of copying a file from Source to Destination.
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void CopyAll(String src, String dst)
			throws IOException {

		Log.Debug("Utility/CopyAll : Start of function with Source Directory as " + src + " and Destination directory as : " + dst);
		File srcPath = new File(src);
		File dstPath = new File(dst);

		if (srcPath.exists()) {
			if (srcPath.isDirectory()) {
				if (!dstPath.exists()) {
					Log.Debug("Utility/CopyAll : Destination Directory " + dstPath + " doesnot exists. Creating the Destination Directory");
					dstPath.mkdir();
					Log.Debug("Utility/CopyAll : Destination Directory " + dstPath + " created Successfully");
				} else {
					Log.Debug("Utility/CopyAll : Destination Directory " + dstPath + " Already Exists.");
				}

				String files[] = srcPath.list();
				for (int i = 0; i < files.length; i++) {
					Log.Debug("Utility/CopyAll : Working on SubDirectory/File - " + srcPath.getPath() + "\\" + files[i]);
					CopyAll(srcPath.getPath() + "\\" + files[i], dstPath.getPath() + "\\" + files[i]);
				}
			} else {
				Log.Debug("Utility/CopyAll : Copying File " + src + " to Destination " + dst + ".");
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
		return;
	}

	/**
	 * Copy the contents of source directory to Destination directory
	 * @param src Path of the Source Directory
	 * @param dst Path of the Destination Directory
	 * @throws Exception
	 */
	public static void CopyDirectory(String src, String dst) throws Exception {
		Log.Debug("Utility/CopyDirectory : Start of function with Source Directory as " + src + " and Destination directory as : " + dst);
		if (new File(src).exists()) {
			try {
				CopyAll(src, dst);
			} catch (Exception Ex) {
				Log.Debug("Utility/CopyDirectory : Error occured while copying the directoty. Error message is - " + Ex.getMessage());
				Log.Error("Utility/CopyDirectory : Error occured while copying the directoty. Error message is - " + Ex.getMessage());
				throw new Exception("Utility/CopyDirectory : Error occured while copying the directoty. Error message is - " + Ex.getMessage());
			}
		} else {
			String error = "Utility/CopyDirectory : Source Path doesnot exists : " + src;
			Log.Error(error);
			throw new Exception(error);
		}
		Log.Debug("Utility/CopyDirectory : End of function with Source Directory as " + src + " and Destination directory as : " + dst);

	}

	/**
	 * Replaces each escape character in string with \<escape character>
	 *
	 * @param value string in which escape characters to be replace.
	 * @return string with all escape character replaced by \<escape character>
	 * e.g Biel: will be returned as Biel\:
	 */
	public static String EscapeVectorChars(String value) {
		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < value.length(); index++) {
			char c = value.charAt(index);
			switch (c) {
			case ';':
			case '|':
			case '[':
			case ']':
			case '\\':
				sb.append("\\");
				break;
			}
			sb.append(c);

		}

		return sb.toString();
	}

	/**
	 * Delete content of directory
	 * @param Dirpath Directory which is to be clean.
	 * @return void
	 */
	public static void CleanDir(String Dirpath) throws Exception {
		Log.Debug("Utility/CleanDir : Start of function with Directory path as " + Dirpath);
		File path = new File(Dirpath);
		try {
			if (path.exists()) {
				File files[] = path.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						Log.Debug("Utility/CleanDir : Deleting file  " + files[i].getPath());
						files[i].delete();
					}
				}
			} else {
				Log.Debug("Utility/CleanDir : Directory does not exist with name  " + Dirpath);
			}
		} catch (Exception Ex) {
			Log.Debug("Utility/CleanDir : Error occured while cleaning directory  " + Dirpath);
			Log.Error("Utility/CleanDir : Error occured while cleaning directory  " + Dirpath);
			throw Ex;
		}
		return;
	}

	/***
	 * Function to check the Memory usage and show the Maximum Memory used
	 *
	 * @param runtime
	 *              Runtime class object
	 * @return the Maximum memory of as String
	 */
	public static String getMaxJVMMemorySize(Runtime runtime) {
		Double result = new Double(runtime.maxMemory() / MB);
		return result.toString();


	}



	/***
	 * Function to check the Memory usage and show the Used Memory used
	 *
	 * @param runtime
	 *              Runtime class object
	 * @return the Maximum memory of as String
	 */
	public static String getUsedJVMMemory(Runtime runtime) {

		Double result = new Double((runtime.totalMemory() - runtime.freeMemory()) / MB);

		return result.toString();

	}

	/*** Function to check the Memory usage and show the Used Memory used
	 *
	 * @param runtime
	 *              Runtime class object
	 * @return the Maximum memory of as String
	 */
	public static String getFreeJVMMemory(Runtime runtime) {
		Double result = new Double(runtime.freeMemory() / MB);

		return result.toString();

	}

	/**
	 *getPhysicalMemory method to get the Machine Physical Memory in GB or MB
	 *
	 * @return String
	 *
	 */
	public static String getPhysicalMemory() {
		String physical_memory = null;
		osMbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		long physical_memory_bytes = osMbean.getTotalPhysicalMemorySize();
		if(physical_memory_bytes>MEM_1MB_bytes && physical_memory_bytes<MEM_1GB_bytes)
		{
			Integer physical_mem_mb=(int)physical_memory_bytes/(1024*1024);
			physical_memory=physical_mem_mb.toString()+"MB";
		}
		else if(physical_memory_bytes>MEM_1GB_bytes)
		{
			Integer physical_mem_gb=(int)physical_memory_bytes/(1024*1024*1024);
			physical_memory=physical_mem_gb.toString()+"GB";
		}


		return physical_memory;
	}
	/**
	 *getCpuUsage method to get the Machine Cpu Usage in
	 *
	 * @return String
	 *
	 *
	 */
	public static String getCpuUsage()
	{
		String cpu_usage;
		osMbean=(com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

		cpu_usage=new Long(osMbean.getProcessCpuTime()).toString()+" nanosec";



		return cpu_usage;
	}
	/**
	 *getCpuUsage method to get the Machine Cpu Usage in
	 *
	 */
	public static String getFreePhysicalMemory()
	{
		String physical_free_memory = null;
		osMbean=(com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		long physical_free_memory_bytes = osMbean.getFreePhysicalMemorySize();
		if(physical_free_memory_bytes>MEM_1MB_bytes && physical_free_memory_bytes<MEM_1GB_bytes)
		{
			Integer physical_mem_mb=(int)physical_free_memory_bytes/(1024*1024);
			physical_free_memory=physical_mem_mb.toString()+"MB";
		}
		else if(physical_free_memory_bytes>MEM_1GB_bytes)
		{
			Integer physical_mem_gb=(int)physical_free_memory_bytes/(1024*1024*1024);
			physical_free_memory=physical_mem_gb.toString()+"GB";
		}



		return physical_free_memory;
	}
	/**
	 * get numeric match from String  
	 */
	public static String getNumericMatch(String content)
	{
		Pattern patner=Pattern.compile("[0-9]");
		Matcher matcher = patner.matcher(content);
		String matching="";
		if(matcher.find())
		{
			matching=content.substring(matcher.start(),matcher.regionEnd());
			//System.out.println("Matched "+matcher.group() + " "+content.substring(matcher.start(),matcher.regionEnd()));
		}
		return matching;
	}
	/**
	 *
	 * @param list
	 * @param elemnt
	 * @param pos_index
	 * @return finalList
	 */

	public static ArrayList<?> insertElementToArrayList(ArrayList<?> list,Object elemnt,int pos_index)
	{

		ArrayList<Object> finalList=new ArrayList<Object>();
		//Implementing the arraylist addition code.
		Object temstorelm=list.get(pos_index);

		System.out.println("The total size is "+list.size());
		for(int i=0;i<list.size();i++)
		{

			if(i==pos_index)
			{
				finalList.add(pos_index, elemnt);
				finalList.add(pos_index+1, temstorelm);
			}
			else{
				finalList.add(list.get(i));
			}

		}


		return finalList;
	}
	/**
	 *
	 * @param list
	 * @param pos_index
	 * @return finalList
	 */
	public static ArrayList<?> deleteElementFromArrayList(ArrayList<?> list,int pos_index)
	{
		ArrayList<Object> finalList=new ArrayList<Object>();
		Object tempobjelemt=list.get(pos_index);
		System.out.println("The Total size is "+list.size());
		for(int i=0;i<list.size();i++)
		{
			if(i==pos_index)
			{

			}else
			{
				finalList.add(list.get(i));
			}
		}
		return  finalList;
	}

	public static String getAppdataDirectory(){

		String OS = System.getProperty("os.name").toUpperCase();

		if (OS.toLowerCase().contains("windows")){
			return System.getenv("APPDATA");
		}
		else if (OS.toLowerCase().contains("mac os x")) {
			return System.getenv("HOME") + "/Library/Application " + "Support";
		}
		else if (OS.toLowerCase().contains("linux")) {
			return System.getenv("HOME");
		}

		return System.getProperty("user.dir");

	}
}
