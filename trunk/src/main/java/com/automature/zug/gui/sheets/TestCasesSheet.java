package com.automature.zug.gui.sheets;

import com.automature.zug.engine.AtomHandler;
import com.automature.zug.gui.MyTableCellRenderer;
import com.automature.zug.gui.ZugGUI;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.xml.stream.events.Comment;
import java.awt.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class TestCasesSheet {


    private Vector header;
    private Vector data;
    private ArrayList<String> testCaseIDs = new ArrayList<String>();
    private	int actionColumn;
    private int verifyColumn;
    private HashMap<Point, String> missingActionMap=new HashMap<Point, String>();


    public HashMap getMissingActionMap(){
        return missingActionMap;
    }

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
                        actionColumn=myCell.getColumnIndex()+1;                         // +1 because the first element int he row is line number
                    }else if(myCell.getStringCellValue().equalsIgnoreCase("Verify")){
                        verifyColumn=myCell.getColumnIndex()+1;
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

        while(it.hasNext()){

            Row myRow = (Row) it.next();

            Vector cellStoreVector=new Vector();
            cellStoreVector.addElement(line);
            int n=myRow.getLastCellNum();

            Cell cell=myRow.getCell(0);
            if(cell!=null){
                String testcaseID=cell.getStringCellValue();
                if(testcaseID!=null && !testcaseID.isEmpty() && StringUtils.isNotBlank(testcaseID) && !testcaseID.equalsIgnoreCase("comment")){
                    testCaseIDs.add(testcaseID);
                }
            }

            for(int i=0;i<=n;i++){

                Cell myCell=myRow.getCell(i);
                cellStoreVector.addElement(myCell==null?"":myCell);
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

        for (int i = 1; i <table.getColumnCount(); i++) {

            column = table.getColumnModel().getColumn(i);

            if(i==actionColumn||i==verifyColumn){

                column.setCellRenderer(new MyTableCellRenderer(missingActionMap));
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane);

        return panel;
    }

    public ArrayList<String> getTestCaseIds(){

        return testCaseIDs;
    }

}
