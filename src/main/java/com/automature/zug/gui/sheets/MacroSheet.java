package com.automature.zug.gui.sheets;

import com.automature.zug.engine.AtomHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class MacroSheet {

    private Vector header;
    private Vector data;

    public void readHeader(Sheet sheet){

        header=new Vector();
        Iterator it = sheet.rowIterator();
        header.add("");
        if(it.hasNext()){
            Row row = (Row) it.next();
            int n=row.getLastCellNum();

            for(int i=0;i<=n;i++){

                Cell myCell=row.getCell(i);
                header.addElement(myCell==null?"":myCell);

            }
        }
    }

    public void readData(Sheet sheet){

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

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        return panel;
    }
}
