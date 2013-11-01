package com.automature.zug.gui.sheets;

import com.automature.zug.gui.CustomTableCellRenderer;
import com.automature.zug.gui.CustomTableRowRenderer;
import com.automature.zug.gui.ZugGUI;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class MoleculesSheet {

    private Vector header;
    private Vector data;
    private ArrayList<String> moleculeID = new ArrayList<String>();
    private	int actionColumn;
    private int verifyColumn;
    private HashMap<Point, String> missingActionMap=new HashMap<Point, String>();

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

            Cell cell=myRow.getCell(0);
            if(cell!=null){
                String moleculeId=cell.getStringCellValue();
                if(moleculeId!=null && !moleculeId.isEmpty() && StringUtils.isNotBlank(moleculeId) && !moleculeId.equalsIgnoreCase("comment")){
                    moleculeID.add(moleculeId);
                }
            }

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

    public JPanel getPanel() throws Exception {

        JPanel panel = new JPanel(new BorderLayout());

        for(int k=0;k<data.size();k++){

            Vector myRow = (Vector) data.get(k);

            //if(myRow.get(actionColumn).toString().equalsIgnoreCase("") || myRow.get(verifyColumn).toString().equalsIgnoreCase("")) continue;

            int n=myRow.size();

            for(int i=0;i<n;i++){

                Object myCell= myRow.get(i);

                if(i==actionColumn||i==verifyColumn){
                    if(myCell!=null && !ZugGUI.spreadSheet.verifyExistence(myCell.toString()))
                        missingActionMap.put(new Point(k,i),"Missing definition");
                }

            }

        }

        JTable table = new JTable(data, header);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn column = null;

        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);

        for (int i = 1; i <table.getColumnCount(); i++) {

            column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(new CustomTableRowRenderer());

            if(i==actionColumn||i==verifyColumn){
                column.setCellRenderer(new CustomTableCellRenderer(missingActionMap));
                column.setPreferredWidth(150);
            }


        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        return panel;
    }

    public boolean moleculeExist(String name){

        if(moleculeID.contains(name))return true;
        else return false;

    }

}
