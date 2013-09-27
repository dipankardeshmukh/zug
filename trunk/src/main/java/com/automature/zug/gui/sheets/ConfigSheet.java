package com.automature.zug.gui.sheets;

import com.automature.zug.gui.ZugGUI;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ConfigSheet {

    private Vector data;


    public String getTestSuiteName() {
        return "";
    }

    public void setTestSuiteName(String testSuiteName) {
        //this.testSuiteName = testSuiteName;
    }

    public String getTestSuiteRole() {
        return "";
    }

    public void setTestSuiteRole(String testSuiteRole) {
        //this.testSuiteRole = testSuiteRole;
    }



    public JPanel getPanel(){

        JPanel panel = new JPanel(new BorderLayout());
        Vector header = new Vector();
        header.add("Line");
        header.add("");
        header.add("");
        JTable table = new JTable(data,header);

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);
        return panel;
    }


    public void readData(Sheet sheet) throws Exception {

        data = new Vector();
        Iterator it = sheet.rowIterator();
        int line=1;

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

    public Vector getData(){
        return data;
    }


}
