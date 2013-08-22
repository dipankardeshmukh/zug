package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class ExcelHandler {

	private String inputFileName;
	boolean isMainSheet=true;
	
	private  HashMap<String, String> externalSheets ;
	private  LinkedHashMap<String, Sheet> sheets = new LinkedHashMap<String, Sheet>();
	
	ExcelHandler(String inputFileName,HashMap<String,String> sm,boolean mainSheet){
		this.inputFileName=inputFileName;
		externalSheets=sm;
		this.isMainSheet=mainSheet;
		readSheets();
	}
	
	ExcelHandler(String inputFileName)throws Exception{
		this.inputFileName=inputFileName;
		externalSheets = new HashMap<String, String>();
		readSheets();
	}
	
	public HashMap<String,String> getExternalSheets(){
		return externalSheets;
	}
	
	
	
	private void readIncludedSheets(){
		Set s=sheets.keySet();
		Iterator it=s.iterator();
		ArrayList<String> al=new ArrayList<String>();
		//for main sheet and molecule
		it.next();
		if(isMainSheet){
			it.next();
		}
		while(it.hasNext()){
			al.add(sheets.get(it.next()).getIncludeList());
		}
		for(String include:al){
			if(!include.isEmpty() ){
				AddToExternalSheets(include);
			}
		}
	}
	
	private void readSheets(){
		try{
			Sheet mainSheet=new Sheet(inputFileName,isMainSheet);
			mainSheet.readSheet();
			sheets.put(mainSheet.getFileName(), mainSheet);
			externalSheets.put(mainSheet.getFileName(), mainSheet.getFullFileName());
			if(isMainSheet){
				Sheet moleculeSheet=mainSheet.getMoleculeSheet();
				sheets.put(moleculeSheet.getFileName(), moleculeSheet);
			}
			AddToExternalSheets(mainSheet.getIncludeList());
			readIncludedSheets();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String getFullPath(String file){
		if (file.contains(":")
				|| file.startsWith("/")) {
			return file;	
			// System.out.println(commandline_include+":"+nSMkey);
		} else {
			String actualPath = "";
			{
				File inputxlsfile = new File(
						inputFileName);				
				if(file.contains("..")){
					int lastIndex=file.lastIndexOf("..")+1;
					String temp[]=file.split("\\..");
					int upperH=temp.length;	
					String p=inputxlsfile.getParent();
					for(int i=1;i<upperH-1;i++){
						p=p.substring(0, p.lastIndexOf("\\"));		//		System.out.println("p="+p);
					}
					file=p+file.substring(lastIndex+1,file.length());
					actualPath +=file;

				}
				else{
					actualPath += inputxlsfile.getParent()
							+ "\\"
							+ file;

				}
				return actualPath;
			}
		}
	}
	
	private void AddToExternalSheets(String value) {
		String[] qualifiedPaths = value.split(",");

		for (int i = 0; i < qualifiedPaths.length; i++) {
			if (qualifiedPaths[i].isEmpty()) {
				continue;
			}
			String fileName=getFullPath(qualifiedPaths[i]);
			File f = new File(fileName);
			if (f.exists()) {
				String fileKey = f.getName();
				if (externalSheets.containsKey(fileKey)) {
				
				} else {
					externalSheets.put(fileKey, fileName);
					try{
						Sheet sheet=new Sheet(fileName,false);
						sheet.readSheet();
						sheets.put(fileKey, sheet);
					}catch(Exception e){
						System.err.print("ExcelHandler/AddToExternalSheets:Exception "+e.getMessage()+"\nCause:"+e.getCause());
					}
				}
			} else {
				System.err.println(fileName + " file doesn't exists");
			}
		}
	}
	
	public HashMap<String,Sheet> getSheets(){
		return sheets;
	}
	

}