package com.automature.zug.gui.sheets;

import com.automature.zug.gui.ZugGUI;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
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


    public List<String> getScriptLocations() {

        String scriptLocations = null;
        Iterator it = data.iterator();

        while (it.hasNext()){

            Vector row = (Vector) it.next();

            if(row.get(0).toString().equalsIgnoreCase("ScriptLocation") && row.get(1)!=null && !row.get(1).toString().isEmpty()){
                scriptLocations = row.get(1).toString();
            }

        }

        List<String> list = new ArrayList<String>();

        String[] locations = scriptLocations.split(";");

        for(String loc : locations){

            File f = new File(loc);

            if(f.exists()){                              // TODO: Update for relative paths

                ZugGUI.message("[WARNING] Invalid ScriptLocation : "+ loc);
                continue;

            }

            list.add(loc);
        }

        return list;
    }


}
