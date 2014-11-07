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
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.automature.spark.gui.utils.GuiUtils;

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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			GuiUtils.showMessage("Error saving changes",e.getMessage() , e);
			//System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
		}
	}

	public boolean addCommentRow(int n){
		FileOutputStream outFile;
		n+=rowOffset;
		try {
			Row row=sheet.getRow(n);

			if(row==null){
				row=sheet.createRow(n);
				Iterator<Cell> it=row.cellIterator();
				int i=0;
				CellStyle style=sheet.getRow(1).getCell(0).getCellStyle();

				while(it.hasNext()){
					it.next();
					Cell cell = row.createCell(i++);

					//cell.setCellStyle(style);
				}

			}
			Cell cell = row.getCell(0);
			if (cell != null) {
				cell.setCellValue("comment");
			}

			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			GuiUtils.showMessage("Error adding comment row", e.getMessage(), e);
			//System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
			return false;
		}
		return true;
	}

	public boolean addRows(int n,int rowSize){
		FileOutputStream outFile;
		try {
			n+=rowOffset;
			for(int i=0;i<rowSize;i++){
				Row row=sheet.createRow(n+i);

			}
			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			GuiUtils.showMessage("Error adding rows", e.getMessage(), e);
		//	System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
			return false;
		}
		return true;
	}

	public boolean addRow(int n){
		FileOutputStream outFile;
		try {
			n+=rowOffset;
			sheet.shiftRows(n, sheet.getLastRowNum(), 1);
			Row row=sheet.createRow(n);
			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			GuiUtils.showMessage("Error adding row ", e.getMessage(), e);
			//System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
			return false;
		}
		return true;
	}

	public boolean deleteRow(int n){
		FileOutputStream outFile;
		try {
			n+=rowOffset;
			Row row=sheet.getRow(n);
			//sheet.shiftRows(n, sheet.getLastRowNum(), -1);
			sheet.removeRow(row);
			sheet.shiftRows(n+1, sheet.getLastRowNum(), -1);

			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			GuiUtils.showMessage("Error deleting row ", e.getMessage(), e);
			//System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
			return false;
		}
		return true;

	}

	public String getFileName() {
		return fileName;
	}

	public void cloneCell(Cell cNew, Cell cOld){
		if(cNew!=null&&cOld!=null){
			cNew.setCellComment( cOld.getCellComment() );
			cNew.setCellStyle( cOld.getCellStyle() );

			switch ( cNew.getCellType() ){
			case Cell.CELL_TYPE_STRING:{
				cNew.setCellValue( cOld.getStringCellValue() );
				break;
			}

			case Cell.CELL_TYPE_BOOLEAN:{
				cNew.setCellValue( cOld.getBooleanCellValue() );
				break;
			}
			case Cell.CELL_TYPE_NUMERIC:{
				cNew.setCellValue( cOld.getNumericCellValue() );
				break;
			}
			case Cell.CELL_TYPE_ERROR:{
				cNew.setCellValue( cOld.getErrorCellValue() );
				break;
			}
			case Cell.CELL_TYPE_FORMULA:{
				cNew.setCellFormula( cOld.getCellFormula() );
				break;
			}
			default:{
				RichTextString richvalue=cOld.getRichStringCellValue();
				if(richvalue!=null){
					cNew.setCellValue(richvalue);	
				}
				break;
			}
			}
		}
	}

	public boolean addActionColumn(int position, String columnHeader){
		shiftColumnRight(position);
		return addColumn(position, columnHeader);
	}

	public void shiftColumnRight(int position){
		position += colOffSet;
		for(Row row:sheet){
			int n=row.getLastCellNum();
			if(n>0){
				if(n>100){
					n=100;
				}
				Cell cell=row.createCell(n);
				for(int i=n;i>position;i--){
					Cell cell1=row.getCell(i);
					Cell cell2=row.getCell(i-1);
					cloneCell(cell1,cell2);
				}
			}

		}		
	}

	public boolean addColumn(int position, String columnHeader) {
		// TODO Auto-generated method stub
		position += colOffSet;
		FileOutputStream outFile;
		try {
			Row firstRow=sheet.getRow(0);

			Cell header=firstRow.createCell(position);
			Cell sibCell=firstRow.getCell(position-2);
			if(sibCell!=null){
				header.setCellStyle(sibCell.getCellStyle());
			}
			header.setCellValue(columnHeader);
			int n=sheet.getLastRowNum();
			for(int i=1;i<=n;i++){
				Row row=sheet.getRow(i);
				if(row!=null){
					Cell cell=row.createCell(position);
					Cell siblingRow=row.getCell(position-2);
					if(siblingRow!=null){
						cell.setCellStyle(siblingRow.getCellStyle());								
					}
				}
			}
			outFile = new FileOutputStream(new File(fileName));
			sheet.getWorkbook().write(outFile);
			outFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			GuiUtils.showMessage("Error adding column ", e.getMessage(), e);
		//	e.printStackTrace();
		//	System.err.println("Error saving file "+fileName+"\t"+e.getMessage());
			return false;
		}
		return true;
	}
}
