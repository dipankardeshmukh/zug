package com.automature.zug.gui.sheets;

import org.apache.poi.ss.usermodel.Sheet;



public abstract class GenericSheet {
	
	 SheetSaver sheetSaver;
	 Sheet sheet;
	
	public GenericSheet(Sheet sheet,SheetSaver sheetSaver) {
		super();
		this.sheetSaver = sheetSaver;
		this.sheet = sheet;
	}

	public abstract void readData()throws Exception;
}
