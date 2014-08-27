package com.automature.spark.gui.sheets;





import java.awt.Point;

import org.apache.poi.ss.usermodel.*;

import com.automature.spark.gui.components.SheetTab;
import com.automature.spark.gui.components.TestCaseTab;
import com.automature.spark.gui.components.TestCaseTreeTableSheetTab;
import com.automature.spark.gui.components.TreeTableSheetTab;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class TestCasesSheet extends GenericSheet{

    private List header;
    private List<List<String>> data;
    private ArrayList<String> testCaseIDs = new ArrayList<String>();
    private	int actionColumn;
    private int verifyColumn;
    private HashMap<Point, String> missingActionMap=new HashMap<Point, String>();
    
  

    public TestCasesSheet(Sheet sheet, String fileName,String originalFileName) {
    	super(sheet,fileName,originalFileName);	
		// TODO Auto-generated constructor stub
	}
    
    public HashMap getMissingActionMap(){
        return missingActionMap;
    }

    public void readHeader(){

        header=new ArrayList();
        Iterator it = sheet.rowIterator();

        header.add("");  // breakpoint column
        header.add("");  // line number column

        if(it.hasNext()){
            Row row = (Row) it.next();
            int n=row.getLastCellNum();

            for(int i=0;i<=n;i++){

                Cell myCell=row.getCell(i);
                header.add(myCell==null?"":GetCellValueAsString(myCell));
                if(myCell!=null){
                    if(GetCellValueAsString(myCell).equalsIgnoreCase("Action")){
                        actionColumn=myCell.getColumnIndex()+2;                         // +2 because the first element int he row is the breakpoint column and second is line number
                    }else if(GetCellValueAsString(myCell).equalsIgnoreCase("Verify")){
                        verifyColumn=myCell.getColumnIndex()+2;
                    }
                }
            }
        }
        // System.out.println("TestCase header size "+header.size()+"\n headers "+header);
    }

    public void readData(){

        data = new ArrayList();
        Iterator it = sheet.rowIterator();
        it.next();
        int line=1;  // 1 is taken by the column header and to match the line numbers with actual spreadsheet
        int headerSize=header.size();
      //  System.out.println("TestCase headerSize "+headerSize);
        while(it.hasNext()){

            Row myRow = (Row) it.next();

            List<String> cellStoreVector=new ArrayList<String>();
            int n=myRow.getLastCellNum();
          
            cellStoreVector.add("");        // breakpoint column
            cellStoreVector.add(line+"");      // line number column

           

            Cell cell=myRow.getCell(0); // first column of the actual spreadsheet

            if(cell!=null){
                String testcaseID=GetCellValueAsString(cell);
                if(testcaseID!=null && !testcaseID.isEmpty() && !testcaseID.isEmpty() && !testcaseID.equalsIgnoreCase("comment")){
                    testCaseIDs.add(testcaseID);
                }
            }

            for(int i=0;cellStoreVector.size()<headerSize;i++){
                Cell myCell=myRow.getCell(i);
                cellStoreVector.add(GetCellValueAsString(myCell));
            }

            line++;
        //      System.out.println("TestCase  "+cellStoreVector.size());
            data.add(cellStoreVector);
        }

    }

  

    public ArrayList<String> getTestCaseIds(){

        return testCaseIDs;
    }

    public void removeAllBreakPoints(){
       if(sheetTab!=null){
    	   ((TreeTableSheetTab)sheetTab).removeAllBreakPoints();
       }
    }
    
    public SheetTab getSheetTab(){
    	if(sheetTab==null){
    		sheetTab=new TestCaseTreeTableSheetTab("Test Cases");
    		sheetTab.loadTabData(getHeader(),getData());
    		sheetTab.setSheetSaver(new SheetSaver(sheet, 0, -2,getFilePath()));
    		sheetTab.setFileName(getOriginalFile());
    	 }
    	return sheetTab;
    }
    
    
    public List<List<String>> getData(){
        return data;
    }
     public List<String> getHeader(){
        return header;
    }

}
