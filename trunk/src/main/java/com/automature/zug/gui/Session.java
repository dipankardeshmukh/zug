package com.automature.zug.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Session implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,ArrayList<String>> recentlyUsedFiles;
	private LinkedHashSet<String> recentlyUsedDirectories;
	
	public Session() {
		recentlyUsedFiles=new LinkedHashMap<String,ArrayList<String>>();
		recentlyUsedDirectories=new LinkedHashSet<String>();
	}
	
	public void addFile(String filename) {
		addDirectory(filename);
		recentlyUsedFiles.put(filename, null);
	}
	
	public void addFileWithSetting(String fileName,ArrayList<String> al) {
		addDirectory(fileName);
		recentlyUsedFiles.put(fileName, al);
	}
	
	public void addDirectory(String fileName){
		//System.out.println("file added "+ fileName.substring(0, fileName.lastIndexOf("\\")));
		recentlyUsedDirectories.add(fileName.substring(0, fileName.lastIndexOf("\\")));
	}

	
	public Set<String> getTestSuiteFiles() {
		// TODO Auto-generated method stub
		return recentlyUsedFiles.keySet();
	}
	
	public Set<String> getDirectories(){
		return recentlyUsedDirectories;
	}

	@Override
	public String toString() {
		return "Session [recentlyUsedFiles=" + recentlyUsedFiles
				+ ", recentlyUsedDirectories=" + recentlyUsedDirectories + "]";
	}
	
	

	
}
