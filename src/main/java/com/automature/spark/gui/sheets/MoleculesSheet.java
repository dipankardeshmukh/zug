package com.automature.spark.gui.sheets;

import java.awt.Point;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.automature.spark.beans.ActionInfoBean;
import com.automature.spark.gui.components.MoleculeTreeTableSheetTab;
import com.automature.spark.gui.components.SheetTab;
import com.automature.spark.gui.components.TreeTableSheetTab;









import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MoleculesSheet extends GenericSheet {

    private List<String> header;
    private List<List<String>> data;
    private Map<String,ActionInfoBean> moleculeIDs = new HashMap<String,ActionInfoBean>();
    private int actionColumn;
    private int verifyColumn;
 

    public MoleculesSheet(Sheet sheet, String filePath,String originalFileName) {
    	super(sheet,filePath,originalFileName);	
		// TODO Auto-generated constructor stub
	}

    public void readHeader() {

        header = new ArrayList<String>();
        Iterator it = sheet.rowIterator();

        header.add("");
        header.add("");

        if (it.hasNext()) {
            Row row = (Row) it.next();
            int n = row.getLastCellNum();

            for (int i = 0; i <= n; i++) {

                Cell myCell = row.getCell(i);
                header.add(myCell == null ? "" : GetCellValueAsString(myCell));
                if (myCell != null) {
                    if (myCell.getStringCellValue().equalsIgnoreCase("Action")) {
                        actionColumn = myCell.getColumnIndex() + 2;
                    } else if (myCell.getStringCellValue().equalsIgnoreCase("Verify")) {
                        verifyColumn = myCell.getColumnIndex() + 2;
                    }
                }
            }
        }
       // System.out.println("Molecule header size " + header.size() + "\n headers " + header);
    }

    public void readData() {

        data = new ArrayList<List<String>>();
        Iterator it = sheet.rowIterator();
        it.next();
        int line = 1;
        //AtomHandler ah=new AtomHandler(scriptLocation);
        int headerSize = header.size();
      
        while (it.hasNext()) {

            Row myRow = (Row) it.next();

            List<String> cellStoreVector = new ArrayList<String>();
          
            cellStoreVector.add("");
            cellStoreVector.add(line + ""); 

            int n = myRow.getLastCellNum();

            Cell cell = myRow.getCell(0);
            boolean molecule=false;
            String moleculeId =null;
            int molLineNo=0;
            if (cell != null) {
                 moleculeId = GetCellValueAsString(cell);
                if (moleculeId != null && !moleculeId.isEmpty() && !moleculeId.isEmpty() && !moleculeId.equalsIgnoreCase("comment")) {
                    //moleculeIDs.put(moleculeId.toLowerCase(),line+"");
                	molLineNo=line;
                    molecule=true;
                }
            }
            String arguments="";
            List<String> args=new ArrayList<String>();
             for (int i = 0; cellStoreVector.size()<headerSize; i++) {
                Cell myCell = myRow.getCell(i);
                String value=GetCellValueAsString(myCell);
                cellStoreVector.add(value);
              /*  if(i==actionColumn||i==verifyColumn){
                 if(myCell!=null ){
                     //   else if(!ZugGUI.spreadSheet.verifyExistence(myCell.toString()))
                       //     missingActionMap.put(new Point(k,i),"Missing definition");
                    }
                 }*/
                if(myCell!=null ){
                    if(molecule && i>0 && StringUtils.isNotBlank(value)){
                    	args.add(value);
                    	arguments+=value+",";
                    }
              
                }
            }
            line++;
            if(molecule){
            	if(arguments.length()>2){
                	arguments="("+arguments.substring(0,arguments.length()-1)+")";            		
            	}
            	ActionInfoBean aib=new ActionInfoBean(moleculeId,arguments,molLineNo,args);
      
            	moleculeIDs.put(moleculeId.toLowerCase(), aib);
            }
            data.add(cellStoreVector);
        }

    }

    public List<List<String>> getData() {
        return data;
    }

    public List<String> getHeader() {
        return header;
    }

    public ActionInfoBean moleculeExist(String name) {

      
            return moleculeIDs.get(name.toLowerCase());
 

    }
    
    
    public Map<String, ActionInfoBean> getMoleculeIDs() {
		return moleculeIDs;
	}

	public SheetTab getSheetTab(){
    	if(sheetTab==null){
    		sheetTab=new  MoleculeTreeTableSheetTab("Molecules");
    		sheetTab.setSheetSaver( new SheetSaver(sheet, 0, -2, getFilePath()));
//    		sheetTab.setFileName(getTempFilePath());
    		sheetTab.setFileName(getOriginalFile());
    		sheetTab.loadTabData(getHeader(),getData());
    		
    	}
    	return sheetTab;
    }
    public void removeAllBreakPoints(){
        if(sheetTab!=null){
     	   ((TreeTableSheetTab)sheetTab).removeAllBreakPoints();
        }
     }

}
