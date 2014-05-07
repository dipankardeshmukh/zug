package com.automature.zug.gui.sheets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.security.AccessController;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class SheetSaver {

	private Sheet sheet;
	private int rowOffset;
	private int colOffSet;
	private String fileName;

	public SheetSaver(Sheet sheet, int rowOffset, int colOffSet, String fileName) {
		super();
		this.sheet = sheet;
		this.rowOffset = rowOffset;
		this.colOffSet = colOffSet;
		this.fileName = fileName;
	}

	public void SaveChange(String value, int row, int col) {

		FileOutputStream outFile;
		try {
			row += rowOffset;
			col += colOffSet;
		//	System.out.println("row =" + row + "\tcol =" + col);
			Row roww = sheet.getRow(row);
			if (roww == null) {
				roww = sheet.createRow(row);
			}
			Cell cell = roww.getCell(col);
			if (cell == null) {
				cell = roww.createCell(col);
			}
			cell.setCellValue(value);
			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
		}
	}
}
