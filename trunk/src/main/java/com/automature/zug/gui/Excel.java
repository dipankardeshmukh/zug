package com.automature.zug.gui;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.automature.zug.util.Log;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.automature.zug.engine.AtomHandler;
import com.automature.zug.engine.SysEnv;
import com.automature.zug.util.ExtensionInterpreterSupport;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Excel {

	static String scriptLocation;
	private Vector data;
	private Vector  header;
	private String inputFileName;
	private String includeList;
	private ArrayList<String> testCaseID;
	private	boolean mainSheet=false;
	private	int actionColumn,VerifyColumn;
	private	FileInputStream inputFile;
	private Excel moleculeExcel;
 	public HashMap<Point, String> messageMap=new HashMap<Point, String>();
	
	public Excel getMoleculeExcel() {
		return moleculeExcel;
	}

	Excel(String inputFileName, boolean mainsheet)throws Exception{
	
		this.inputFileName=inputFileName;
		this.mainSheet=mainsheet;
		if(mainsheet){
			 testCaseID=new ArrayList<String>();
		}
	}
	
	public String getFileName(){
		String name=new File(inputFileName).getName();
		if(name.contains(".")){
			name=name.substring(0,name.lastIndexOf("."));
		}
		return  name;
	}
	
	public String getFullFileName(){
		return new File(inputFileName).getAbsolutePath();
	}
	
	public boolean isMainSheet() {
		return mainSheet;
	}

	public void setMainSheet(boolean mainSheet) {
		this.mainSheet = mainSheet;
	}

	public Vector getData() {
		return data;
	}

	public Vector getHeader() {
		return header;
	}

	public String getIncludeList() {
		return includeList;
	}

	public int getActionColumn() {
		return actionColumn;
	}

	public int getVerifyColumn() {
		return VerifyColumn;
	}

	private Workbook openFile() throws IOException, InvalidFormatException {
		
		File file = new File(inputFileName);
		inputFile = new FileInputStream(file);
		//POIFSFileSystem inputFileSystem = new POIFSFileSystem(inputFile);
		return WorkbookFactory.create(inputFile);	
	}
	
	private void closeFiles()throws IOException{
		inputFile.close();
	}
	
	private String getSheetName(){
		String name="";
		if(mainSheet){
			name="TestCases";
		}else{
			name="Molecules";
		}
		return name;
	}
	
	
	private void readHeader(Sheet mySheet){

		header=new Vector();
		Iterator rowIter = mySheet.rowIterator();
		header.add("Line");
		if(rowIter.hasNext()){
			Row myRow = (Row) rowIter.next();
			Iterator cellIter = myRow.cellIterator();
			int n=myRow.getLastCellNum();
			
			for(int i=0;i<=n;i++){

				Cell myCell=myRow.getCell(i);
				header.addElement(myCell==null?"":myCell);
				if(myCell!=null){
					if(myCell.getStringCellValue().equalsIgnoreCase("Action")){
						actionColumn=myCell.getColumnIndex();
					}else if(myCell.getStringCellValue().equalsIgnoreCase("Verify")){
						VerifyColumn=myCell.getColumnIndex();
					}
				}
			}
		}else{

		}
	}

	public ArrayList<String> getTestCaseIDs(){
		return testCaseID;
	}
	
	private void readData(Sheet mySheet){
		
		data = new Vector();
		Iterator rowIter = mySheet.rowIterator();
		rowIter.next();
		int line=1;
		AtomHandler ah=new AtomHandler(scriptLocation);
		while(rowIter.hasNext()){
			Row myRow = (Row) rowIter.next();
			Iterator cellIter = myRow.cellIterator();
			Vector cellStoreVector=new Vector();
			cellStoreVector.addElement(line);
			int n=myRow.getLastCellNum();
			{
				if(mainSheet){
					Cell myCell=myRow.getCell(0);
					if(myCell!=null){
						String testcaseID=myCell.getStringCellValue();
						if(testcaseID!=null && !testcaseID.isEmpty() && StringUtils.isNotBlank(testcaseID) && !testcaseID.equalsIgnoreCase("comment")){
							testCaseID.add(testcaseID);
						}
					}

				}
			}
			for(int i=0;i<=n;i++){
				Cell myCell=myRow.getCell(i);
				cellStoreVector.addElement(myCell==null?"":myCell);
				if(i==actionColumn||i==VerifyColumn){
					if(myCell!=null){
						String atom=myCell.getStringCellValue();
						String message=ah.verifyExistence(atom);
						if(!message.equals("ok")){
							messageMap.put(new Point(line-1,i),message);
						}
					}
				}
			}
			line++;
			data.addElement(cellStoreVector);
		}

	}
	
	private void readIncludeFileName(Sheet configSheet){
		Row row=configSheet.getRow(7);
		Cell cell=row.getCell(1);
		includeList=(cell!=null?cell.getStringCellValue():"");
	}
	
	private void readScriptLocation(Sheet configSheet){
		try{
			String iniSL=ExtensionInterpreterSupport.getNode("//root//configurations//scriptlocation");//.getNodesValues(scriptLocationsTag);
			Row row=configSheet.getRow(0);
			Cell cell=row.getCell(1);
			String location=(cell!=null?cell.getStringCellValue():"");
			if(iniSL!=null){
				location+=";"+iniSL+";"+new File(getFullFileName()).getParent();
			}else{
				location+=";"+new File(getFullFileName()).getParent();
			}
			String[] spliArr = location.split(";");
			String newLocation = "";
			String testSuiteLoc=new File(getFullFileName()).getParent();
			ArrayList<String> locations=new ArrayList<String>();
			for (String spits : spliArr) {
				if(spits==null || spits=="" || spits==" " || spits.isEmpty()){
					continue;
				}
				String[] temp0 = spits.split("\\\\");
				if (!temp0[0].contains(":")) {
					if (testSuiteLoc != null) {
						temp0[0] = testSuiteLoc;
						newLocation = temp0[0] + SysEnv.SLASH+ spits;
					} 
					locations.add(newLocation);
				}else{
					if(!locations.contains(spits)){
						locations.add(spits);
					}
				}
			}
			String str="";
			for(String loc:locations){
				str+=loc+";";
			}
			scriptLocation=str.substring(0, str.length()-1);
			scriptLocation=scriptLocation.replaceAll(";(;)+", ";");
			
		}catch(Exception e){
			Log.Error(e.getMessage());
		}
	}
	
	public void readSheet(){
	
		try{
			/** Get the test case sheet from workbook**/		
			Workbook _workBook=openFile();
			Sheet configSheet =_workBook.getSheet("Config");
			readIncludeFileName(configSheet);
			if(isMainSheet()){
				readScriptLocation(configSheet);
			}
			Sheet mySheet = _workBook.getSheet(getSheetName());
			readHeader( mySheet);
			readData(mySheet);	
			if(isMainSheet()){
				moleculeExcel =new Excel(inputFileName,false);
				moleculeExcel.readSheet();
				moleculeExcel.inputFileName="Molecules";
			}
			
		}catch (Exception e){
			Log.Error(e.getMessage());
		}finally{
			try{
				closeFiles();
			}catch(Exception e){
				Log.Error(e.getMessage());
			}
		}
	}
}
