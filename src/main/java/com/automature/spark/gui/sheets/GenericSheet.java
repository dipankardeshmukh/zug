package com.automature.spark.gui.sheets;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.automature.spark.gui.components.SheetTab;



public abstract class GenericSheet {
	
	 Sheet sheet;
	 SheetTab sheetTab;
	 private String filePath;
	 private String originalFile;
	
	 
	public GenericSheet(Sheet sheet,String filePath,String originalFile) {
		super();
		this.sheet = sheet;
		this.filePath=filePath;
		this.originalFile=originalFile;
	}

	public abstract void readData()throws Exception;
        
        String GetCellValueAsString(Cell cell) {
		if(cell==null){
			return "";
		}
		if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return Boolean.toString(cell.getBooleanCellValue());
		}
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return Double.toString(cell.getNumericCellValue());
		}
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {

			return cell.getStringCellValue();
		}
		if (cell.getCellType() == Cell.CELL_TYPE_BLANK
				|| cell.getCellType() == Cell.CELL_TYPE_ERROR) {
			// System.out.println(" get cell value "+cell.getCellType());
			return "";
		}

		return "";
	}
        
        public SheetTab getSheetTab(){
        	return sheetTab;
        }

		public String getFilePath() {
			return filePath;
		}

		public String getOriginalFile() {
			return originalFile;
		}

		public void reloadData(){
			
		}
		
		public boolean isSheetTabCreated(){
			return sheet==null?false:true;
		}
        
        
}
