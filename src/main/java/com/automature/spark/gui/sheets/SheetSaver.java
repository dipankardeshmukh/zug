package com.automature.spark.gui.sheets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.security.AccessController;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
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
			System.out.println("file "+fileName);
			System.out.println("row =" + row + "\tcol =" + col +" value "+value);
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

	public void addCommentRow(int n){
		FileOutputStream outFile;
		n+=rowOffset;
		try {
			Row row=sheet.getRow(n);
			if(row==null){
				System.out.println("row does not exists");
				row=sheet.createRow(n);
				Iterator<Cell> it=sheet.getRow(n-1).cellIterator();
				int i=0;
				CellStyle style=sheet.getRow(1).getCell(0).getCellStyle();
				
				while(it.hasNext()){
					it.next();
					Cell cell = row.createCell(i++);
					//cell.setCellStyle(style);
				}

			}else{
				System.out.println("Row exists");
			}
			Cell cell = row.getCell(0);
			if (cell != null) {
				cell.setCellValue("comment");
				System.out.println("setting cell value ");
			}
			
			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
		}
	}

	public void addRows(int n,int rowSize){
		FileOutputStream outFile;
		try {
			n+=rowOffset;
			for(int i=0;i<rowSize;i++){
				Row row=sheet.createRow(n+i);
			}
			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
		}
	}


	public String getFileName() {
		return fileName;
	}
}
