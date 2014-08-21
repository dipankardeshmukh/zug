package com.automature.spark.gui.sheets;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.automature.spark.gui.components.SheetTab;






import java.io.File;
import java.util.*;

public class ConfigSheet extends GenericSheet {

    private List<List<String>> data;
    private ArrayList header;
  

    public ConfigSheet(Sheet sheet, String fileName,String originalFileName) {
    	super(sheet,fileName,originalFileName);	
		// TODO Auto-generated constructor stub
	   readHeader();
    }

    public String getTestSuiteName() {

        String testsuiteName = null;
        Iterator it = data.iterator();

        while (it.hasNext()) {

            Vector row = (Vector) it.next();

            if (row.get(0).toString().equalsIgnoreCase("Test Suite Name") && row.get(1) != null && !row.get(1).toString().isEmpty()) {
                testsuiteName = row.get(1).toString();
            }

        }

        return testsuiteName;
    }

    public void readHeader() {

        header = new ArrayList<String>();
 

      //  header.add("");  // breakpoint column
        header.add(""); // line number column;
        header.add("");

    }

    public void setTestSuiteName(String testSuiteName) {
        //this.testSuiteName = testSuiteName;
    }

    public String getTestSuiteRole() {
        return "";
    }

    public void setTestSuiteRole(String testSuiteRole) {
        //this.testSuiteRole = testSuiteRole;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void readData() throws Exception {

        data = new ArrayList<List<String>>();
        Iterator it = sheet.rowIterator();
        int line = 1;

        while (it.hasNext()) {

            Row row = (Row) it.next();

            ArrayList<String> singleRow = new ArrayList<String>();
         //   singleRow.add(line + "");
            // int n=row.getLastCellNum();
            int n = 2;
            for (int i = 0; i < n; i++) {
                Cell cell = row.getCell(i);
                singleRow.add(GetCellValueAsString(cell));
            }
         
            line++;
        //    System.out.println("config  " + singleRow.size());
            data.add(singleRow);
        }
    }

    public List<String> getScriptLocations() {

        String scriptLocations = null;
        Iterator<List<String>> it = data.iterator();
    
        while (it.hasNext()) {

            List<String> row =  it.next();

            if (row.get(0).toString().equalsIgnoreCase("ScriptLocation") && row.get(1) != null && !row.get(1).toString().isEmpty()) {
                scriptLocations = row.get(2).toString();
            }

        }
        List<String> list = new ArrayList<String>();
        
        if(scriptLocations==null){
        	return list;
        }
        String[] locations = scriptLocations.split(";");

        for (String loc : locations) {

            File f = new File(loc);

            if (!f.exists()) {                              // TODO: Update for relative paths

                System.out.println("[WARNING] Invalid ScriptLocation : " + loc);
                continue;

            }

            list.add(loc);
        }

        return list;
    }

    public List<String> getHeader() {
        return header;
    }

	@Override
	 public SheetTab getSheetTab(){
    	if(sheetTab==null){
    		sheetTab=new SheetTab("Config");
    		sheetTab.loadTabData(getHeader(),getData());
    		sheetTab.setSheetSaver(new SheetSaver(sheet, 0, 0, getFilePath()));
    		sheetTab.setFileName(getOriginalFile());
    	}
    	return sheetTab;
    }    
	
	
    
}
