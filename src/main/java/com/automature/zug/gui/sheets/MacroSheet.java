package com.automature.zug.gui.sheets;

import com.automature.zug.engine.AtomHandler;
import com.automature.zug.gui.actionlistener.SheetTableModelListener;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.TableModel;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class MacroSheet extends GenericSheet{

    private Vector header;
    private Vector data;
    private List<String> headers;

    public MacroSheet(Sheet sheet, String filePath) {
		// TODO Auto-generated constructor stub
    	super(sheet,new SheetSaver(sheet, 1, -1,filePath));
	}

	public void readHeader(){

        header=new Vector();
        headers=new ArrayList<String>();
        Iterator it = sheet.rowIterator();
        header.add("");
        if(it.hasNext()){
            Row row = (Row) it.next();
            int n=row.getLastCellNum();

            for(int i=0;i<=n;i++){

                Cell myCell=row.getCell(i);
                header.addElement(myCell==null?"":myCell);
                String headerValue=myCell==null?"":myCell.getStringCellValue();
                if(headerValue!=null && !headerValue.isEmpty() && !headerValue.equalsIgnoreCase("Macro Name")&& !headerValue.equalsIgnoreCase("Comment")){
                	headers.add(headerValue);
                }

            }
        }
    }

    public void readData(){

        data = new Vector();
        Iterator it = sheet.rowIterator();
        it.next();
        int line=2;

        while(it.hasNext()){

            Row row = (Row) it.next();
            Vector singleRow =new Vector();
            singleRow.addElement(line);
            int n=row.getLastCellNum();

            for(int i=0;i<=n;i++){
                Cell cell=row.getCell(i);
                singleRow.addElement(cell==null?"":cell);
            }
            line++;
            data.addElement(singleRow);
        }

    }

    public JPanel getPanel(){

        JPanel panel = new JPanel(new BorderLayout());

        UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
        JTable table = new JTable(data, header);

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);
        table.getColumnModel().getColumn(3).setPreferredWidth(400);
        table.getModel().addTableModelListener(new SheetTableModelListener(sheetSaver));
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        return panel;
    }
    
    public List<String> getHeader(){
    	return headers;
    }
}
