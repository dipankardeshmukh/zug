package com.automature.zug.gui.sheets;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Vector;


public class MoleculesSheet {

    private Vector header;
    private Vector data;
    private	int actionColumn;
    private int verifyColumn;

    public void readHeader(Sheet sheet){

        header=new Vector();
        Iterator it = sheet.rowIterator();
        header.add("Line");
        if(it.hasNext()){
            Row row = (Row) it.next();
            int n=row.getLastCellNum();

            for(int i=0;i<=n;i++){

                Cell myCell=row.getCell(i);
                header.addElement(myCell==null?"":myCell);
                if(myCell!=null){
                    if(myCell.getStringCellValue().equalsIgnoreCase("Action")){
                        actionColumn=myCell.getColumnIndex();
                    }else if(myCell.getStringCellValue().equalsIgnoreCase("Verify")){
                        verifyColumn=myCell.getColumnIndex();
                    }
                }
            }
        }
    }

    public void readData(Sheet mySheet){

        data = new Vector();
        Iterator it = mySheet.rowIterator();
        it.next();
        int line=1;
        //AtomHandler ah=new AtomHandler(scriptLocation);
        while(it.hasNext()){
            Row myRow = (Row) it.next();

            Vector cellStoreVector=new Vector();
            cellStoreVector.addElement(line);
            int n=myRow.getLastCellNum();

            for(int i=0;i<=n;i++){
                Cell myCell=myRow.getCell(i);
                cellStoreVector.addElement(myCell==null?"":myCell);
                /*if(i==actionColumn||i==verifyColumn){
                    if(myCell!=null){
                        String atom=myCell.getStringCellValue();
                        String message=ah.verifyExistence(atom);
                        if(!message.equals("ok")){
                            messageMap.put(new Point(line-1,i),message);
                        }
                    }
                }*/
            }
            line++;
            data.addElement(cellStoreVector);
        }

    }

    public JPanel getPanel(){

        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(data, header);

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        return panel;
    }

}
