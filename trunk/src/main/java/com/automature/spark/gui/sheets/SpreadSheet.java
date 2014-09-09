package com.automature.spark.gui.sheets;


import com.automature.spark.beans.MacroColumn;
import com.automature.spark.engine.BuildInAtom;
import com.automature.spark.engine.Spark;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.gui.utils.Excel;
import com.automature.spark.util.ExtensionInterpreterSupport;


import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SpreadSheet {

	private static String tempLocation=com.automature.spark.engine.SysEnv.LOGLOCATION + File.separator + "ZUG Logs";
	private String fileName;
	private File tempFile;
	private String absolutePath;

	private ConfigSheet configSheet;
	private MacroSheet macroSheet;
	private TestCasesSheet testCasesSheet;
	private MoleculesSheet moleculesSheet;
	private static Map<String, SpreadSheet> uniqueSheets = new HashMap<String, SpreadSheet>();
	private HashMap<String, SpreadSheet> includeFiles = new HashMap<String, SpreadSheet>();
	private static List<String> scriptLocations = new ArrayList<String>();

	private FileInputStream inputFile;

	public String getAbsolutePath() {
		return absolutePath;
	}

	public ConfigSheet getConfigSheet() {
		return configSheet;
	}

	public MacroSheet getMacroSheet() {
		return macroSheet;
	}

	public TestCasesSheet getTestCasesSheet() {
		return testCasesSheet;
	}

	public MoleculesSheet getMoleculesSheet() {
		return moleculesSheet;
	}

	public HashMap<String, SpreadSheet> getIncludeFiles() {
		return includeFiles;
	}

	public String getFileName() {
		return fileName;
	}

	public SpreadSheet getIncludeFile(String file, Set<String> readFiles) {

		readFiles.add(file);
		if (includeFiles.containsKey(file)) {

			return includeFiles.get(file);

		} else {

			Iterator it = includeFiles.keySet().iterator();

			while (it.hasNext()) {
				String currentFile = (String) it.next();
				if (readFiles.contains(currentFile)) {
					System.out.println("WARNING: Recursive include file detected! Ignoring file: " + currentFile);
				} else {
					SpreadSheet sh = includeFiles.get(currentFile);
					return sh.getIncludeFile(file, readFiles);
				}
			}
			return null;  // Include File not found
		}
	}

	public SpreadSheet() {

	}

	public void readConfigIncludeFiles() throws Exception {

		Iterator<List<String>> it = configSheet.getData().iterator();

		while (it.hasNext()) {

			List<String> row = null;
			row = it.next();
			//System.out.println(row);
			Iterator<String> cell = row.iterator();

			if (cell.hasNext()) {

				if (cell.next().toString().equalsIgnoreCase("include") && cell.hasNext()) {

					String[] listOfFiles = cell.next().toString().split(";|,");
					
					for (String s : listOfFiles) {

						if (s == null || s.isEmpty()) {
							continue;
						}
						if (!uniqueSheets.containsKey(s)) {

							File fileToRead = new File(s);
							
							if (!fileToRead.isAbsolute()) {
								File f = new File(this.absolutePath);
								s = f.getParent() + File.separator + s;
							}
							File f=new File(s);
							if(!f.exists()){
								System.err.println(s +"does not exist.");
								continue;
							}
							if (!uniqueSheets.containsKey(s)) {
								SpreadSheet sh = new SpreadSheet();
								uniqueSheets.put(s, sh);
								if (sh.readSpreadSheet(s)) {
									includeFiles.put(s, sh);     
								}else{
								
									uniqueSheets.remove(s);
								}
							} else {
								System.out.println("\nWARNING: Recursive include file detected! Ignoring file: " + s + " included by " + this.absolutePath);
							}
						} else {
							System.out.println("\nWARNING: Recursive include file detected! Ignoring file: " + s + " included by " + this.absolutePath);
						}

						
					}
				}

			}
		}

	}

	public void readIncludeFiles(HashMap<String, String> nameSpace, String[] files) throws Exception {
		for (String s : files) {
			if (s == null || s.isEmpty()) {
				continue;
			}
			String fileNamespace = nameSpace.get(s);

			if (fileNamespace == null) {
				fileNamespace = s;
			}
			//System.out.println("name space "+fileNamespace);
			readIncludeFile(fileNamespace, s);

		}
	}

	public SpreadSheet getIncludeFile(String file) {
		/*  if (uniqueSheets.containsKey(file)) {
        	System.out.println("returning from include file  ");
            return uniqueSheets.get(file);

        }*/
		//return null;

		if (includeFiles.containsKey(file)) {

			return includeFiles.get(file);

		} else {

			Iterator it = includeFiles.keySet().iterator();

			while (it.hasNext()) {
				String currentFile = (String) it.next();
				SpreadSheet sh = includeFiles.get(currentFile);
				SpreadSheet sp= sh.getIncludeFile(file);
				if(sp==null){
					continue;
				}else{
					return sp;
				}

			}
			return null;  // Include File not found
		}
	}

	public void readIncludeFile(String namespace, String fileName) throws Exception {
		//System.out.println("reading file "+fileName);
		SpreadSheet sp = ZugguiController.spreadSheet.getIncludeFile(namespace, new HashSet<String>());

		if (sp != null) {

			//ZugGUI.message("\nWARNING: Recursive include file detected! Ignoring file: " + fileName + " included by " + this.absolutePath);
		} else {

			File fileToRead = new File(fileName);
			if (!fileToRead.isAbsolute()) {
				File f = new File(this.absolutePath);
				String tempFileName = f.getParent() + File.separator + fileName;
				if (fileName.equalsIgnoreCase(namespace)) {
					namespace = tempFileName;
				}
				fileName = tempFileName;
			}
			if (!uniqueSheets.containsKey(fileName)) {
				SpreadSheet sh = new SpreadSheet();
				if (sh.readSpreadSheet(fileName)) {
					//	System.out.println("file included "+namespace);
					includeFiles.put(namespace, sh);
					uniqueSheets.put(namespace, sh);
				}
			} else {
				System.out.println("\nWARNING: Recursive include file detected! Ignoring file: " + fileName + " included by " + this.absolutePath);
			}

		}
	}

	public boolean readSpreadSheet(String filePath) throws Exception {

		if (!new File(filePath).isAbsolute()) {
			throw new Exception("SpreadSheet/readSpreadSheet expects file paths to be absolute");
		}

		if (!new File(filePath).exists()) {
			System.out.println("Spreadsheet/readSpreadSheet: Could not find file - " + filePath);
			return false;
		}

		try {
			absolutePath = filePath;
			
			
			final File file = new File(filePath);
			createTempFile(file.getName());
			try {
				FileUtils.copyFile(file, tempFile);						
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fileName = file.getName();

			inputFile = new FileInputStream(tempFile);
			Workbook wb = WorkbookFactory.create(inputFile);

			configSheet = new ConfigSheet(wb.getSheet("Config"), tempFile.getAbsolutePath(),absolutePath);
			configSheet.readData();
			scriptLocations.addAll(configSheet.getScriptLocations());
			if(!scriptLocations.contains(file.getParent())){
				scriptLocations.add(file.getParent());
			}
		//	configSheet.setTempFilePath(tempFile.getAbsolutePath());			
						

			readConfigIncludeFiles();

			macroSheet = new MacroSheet(wb.getSheet("Macros"), tempFile.getAbsolutePath(),absolutePath);

			macroSheet.readHeader();
			macroSheet.readData();
		//	macroSheet.setTempFilePath(tempFile.getAbsolutePath());

			testCasesSheet = new TestCasesSheet(wb.getSheet("TestCases"), tempFile.getAbsolutePath(),absolutePath);
			testCasesSheet.readHeader();
			testCasesSheet.readData();
		//	testCasesSheet.setTempFilePath(tempFile.getAbsolutePath());

			moleculesSheet = new MoleculesSheet(wb.getSheet("Molecules"), tempFile.getAbsolutePath(),absolutePath);
			moleculesSheet.readHeader();
			moleculesSheet.readData();
		//	moleculesSheet.setTempFilePath(tempFile.getAbsolutePath());
			
			



		} catch (FileNotFoundException f) {
			System.out.println("\nWARNING: The file could not be found - " + filePath);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return true;
	}

	private void createTempFile(String fileName) {
		// TODO Auto-generated method stub
		int n=0;
		String tmp="temp";
		
		do{
			File f=new File(tempLocation+File.separator+tmp+n+fileName);
			if(f.exists()){
				if(f.delete()){
					tempFile= f;
					return;
				}
			}else{
				tempFile= f;
				return;
			}
			n++;
		}while(n<10);
	}

	public boolean verifyExistence(String step) throws Exception {
		return true;
/*		if (step.startsWith("&")) {

			if (step.contains(".")) {

				String nameSpaceMolecule[] = step.split("\\.");
				String nameSpace = nameSpaceMolecule[0].replaceFirst("&", "");
				String mol = step.split("\\.")[1];

				for (String path : ZugguiController.spreadSheet.includeFiles.keySet()) {

					File fName = new File(path);

					if (fName.getName().contains(nameSpace)) {

						SpreadSheet sp = this.getIncludeFile(path, new HashSet<String>());
						if (sp != null) {
							return sp.getMoleculesSheet().moleculeExist(mol);
						}
					}
				}

				return false;

			} else {

				return this.getMoleculesSheet().moleculeExist(step.replaceFirst("&",""));
			}

		} else if (step.startsWith("@")) {

			String iniSL = null;
			try {
				iniSL = ExtensionInterpreterSupport.getNode("//root//configurations//scriptlocation");
				String[] locations = iniSL.split(";");
				for (String loc : locations) {

					File f = new File(loc + File.separator + step.replaceFirst("@", ""));
					if (f.exists()) {
						return true;
					}

				}

			} catch (Exception e) {
				System.out.println("\nException while reading script location " + e.getMessage());
			}

			return false;

		} else if (step.contains(".")) {

			try {

				String pkgName = step.split("\\.")[0];
				String atomName = step.split("\\.")[1];

				if(Zug.invokeAtoms.containsKey(pkgName.toLowerCase())){
					return Zug.invokeAtoms.get(pkgName.toLowerCase()).methodExists(atomName);
				}
			} catch (Exception e) {

				throw new Exception("Exception while verifying in-process atom: " + step + e.getMessage());
			}

			return false;

		} else {
			if(BuildInAtom.buildIns.contains(step.toLowerCase())){
				return true;
			}
			return false;
		}
*/

	}


	private void removeBreakPoints(SpreadSheet sp){

		sp.getMoleculesSheet().removeAllBreakPoints();
		Iterator it = sp.getIncludeFiles().keySet().iterator();
		while(it.hasNext()){
			String key = it.next().toString();
			SpreadSheet sheet = sp.getIncludeFiles().get(key);
			removeBreakPoints(sheet);
		}   

	}

	public void removeAllBreakPoints() {
		getTestCasesSheet().removeAllBreakPoints();
		removeBreakPoints(this);         
	}

	public void releaseResources() {
		uniqueSheets.clear();
		scriptLocations.clear();
		if (this.inputFile != null) {
			try {
				inputFile.close();
				inputFile=null;
				FileUtils.deleteQuietly(tempFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block		
			}
		}
		Iterator it = includeFiles.keySet().iterator();
		while (it.hasNext()) {
			SpreadSheet sh = (SpreadSheet) includeFiles.get(it.next());
			sh.releaseResources();
		}
	}

	public void removeIncludeSheet(String sheet) {
		// TODO Auto-generated method stub

		includeFiles.remove(sheet);
		includeFiles.remove(sheet.substring(0, sheet.lastIndexOf(".")));
		uniqueSheets.remove(sheet);
		uniqueSheets.remove(sheet.substring(0, sheet.lastIndexOf(".")));
	}
	
	public static void putUniqueSheet(String fileName,SpreadSheet sp){
		uniqueSheets.put(fileName, sp);
	}

	public static Map<String, SpreadSheet> getUniqueSheets() {
		return uniqueSheets;
	}

	
	
	public void save(){
		try {
			FileUtils.copyFile(tempFile, new File(absolutePath));						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error saving file "+absolutePath+"\t"+e.getMessage());
		}

	}
	public void saveAs(String fileName){
		try {
			FileUtils.copyFile(tempFile, new File(fileName));
		//	absolutePath=fileName;
		//	this.fileName=new File(fileName).getName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error saving file "+absolutePath+"\t"+e.getMessage());
		}
	}

	public static List<String> getScriptLocations() {
		return scriptLocations;
	}
	
	public MacroColumn getMacroColumn(){
		return new MacroColumn(getFileName(), getMacroSheet().getMacroCols());
	}
	
}
