package com.automature.spark.gui.sheets;


import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;









import com.automature.spark.gui.components.MacroTab;
import com.automature.spark.gui.components.SheetTab;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MacroSheet extends GenericSheet{

    private List<String> header;
    private List<List<String>> data;
    private Map<String,String> macros=new HashMap<String,String>();
    private List<String> macroCols;

    public MacroSheet(Sheet sheet, String filePath,String originalFileName) {
    	super(sheet,filePath,originalFileName);	
		// TODO Auto-generated constructor stub
	}

	public void readHeader(){

        header=new ArrayList<String>();
        macroCols=new ArrayList<String>();
        Iterator it = sheet.rowIterator();
      //  header.add("");
        if(it.hasNext()){
            Row row = (Row) it.next();
            int n=row.getLastCellNum();

            for(int i=0;i<n;i++){

                Cell myCell=row.getCell(i);
                String headerValue=GetCellValueAsString(myCell);
                header.add(headerValue);
               
                if(headerValue!=null && !headerValue.isEmpty() && !headerValue.equalsIgnoreCase("Comment")&& !headerValue.equalsIgnoreCase("macro name")){
                	macroCols.add(headerValue);
                }

            }
        }
  //      System.out.println("Macro header size "+header.size());
    }

    public void readData(){

        data = new ArrayList();
        Iterator it = sheet.rowIterator();
        it.next();
        int line=1;
        int headerSize=header.size();
    //    System.out.println("Macro headerSize "+headerSize);
        while(it.hasNext()){

            Row row = (Row) it.next();
            List<String> singleRow =new ArrayList<String>();
     //       singleRow.add(line+"");
            int n=row.getLastCellNum();
            
            
            for(int i=0;singleRow.size()<headerSize;i++){
                Cell cell=row.getCell(i);
                singleRow.add(cell==null?" ":GetCellValueAsString(cell));
            }
            if(StringUtils.isNotBlank(singleRow.get(0))){
            	macros.put(singleRow.get(0).toLowerCase(), singleRow.get(1).length()>50?singleRow.get(1).substring(0,50)+"..":singleRow.get(1));
            }
            line++;
      //       System.out.println("Macro "+singleRow.size());
            data.add(singleRow);
        }

    }

     public List<List<String>> getData(){
        return data;
    }
    
    public List<String> getHeader(){
    	return header;
    }
    
    @Override
	 public SheetTab getSheetTab(){
   	if(sheetTab==null){
   		sheetTab=new MacroTab("Macros");
   		sheetTab.loadTabData(getHeader(),getData());
   		sheetTab.setSheetSaver(new SheetSaver(sheet, 1, 0,getFilePath()));
   		sheetTab.setFileName(getOriginalFile());
   	}
   	return sheetTab;
   }    
    
    public String getMacroValue(String macro){
		return macros.get(macro);
	}

	public List<String> getMacroCols() {
		return macroCols;
	}
	
	public Map getMacros() {
		return macros;
	}
	
    
    
}
