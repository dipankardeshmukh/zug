package com.automature.zug.gui.sheets;

import com.automature.zug.engine.Controller;
import com.automature.zug.gui.CustomTableCellRenderer;
import com.automature.zug.gui.CustomTableRowRenderer;
import com.automature.zug.gui.ZugGUI;
import com.automature.zug.gui.actionlistener.SheetTableModelListener;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class MoleculesSheet extends GenericSheet{

    private Vector header;
    private Vector data;
    private ArrayList<String> moleculeIDs = new ArrayList<String>();
    private	int actionColumn;
    private int verifyColumn;
    private HashMap<Point, String> missingActionMap=new HashMap<Point, String>();

    JTable table = null;

    public MoleculesSheet(Sheet sheet, String filePath) {
		// TODO Auto-generated constructor stub
    	super(sheet,new SheetSaver(sheet, 1, -2,filePath));
	}

	public void readHeader(){

        header=new Vector();
        Iterator it = sheet.rowIterator();

        header.add("");
        header.add("");

        if(it.hasNext()){
            Row row = (Row) it.next();
            int n=row.getLastCellNum();

            for(int i=0;i<=n;i++){

                Cell myCell=row.getCell(i);
                header.addElement(myCell==null?"":myCell);
                if(myCell!=null){
                    if(myCell.getStringCellValue().equalsIgnoreCase("Action")){
                        actionColumn=myCell.getColumnIndex()+2;
                    }else if(myCell.getStringCellValue().equalsIgnoreCase("Verify")){
                        verifyColumn=myCell.getColumnIndex()+2;
                    }
                }
            }
        }
    }

    public void readData(){

        data = new Vector();
        Iterator it = sheet.rowIterator();
        it.next();
        int line=2;
        //AtomHandler ah=new AtomHandler(scriptLocation);
        while(it.hasNext()){

            Row myRow = (Row) it.next();

            Vector cellStoreVector=new Vector();

            cellStoreVector.addElement("");
            cellStoreVector.addElement(line);

            int n=myRow.getLastCellNum();

            Cell cell=myRow.getCell(0);

            if(cell!=null){
                String moleculeId=cell.getStringCellValue();
                if(moleculeId!=null && !moleculeId.isEmpty() && StringUtils.isNotBlank(moleculeId) && !moleculeId.equalsIgnoreCase("comment")){
                    moleculeIDs.add(moleculeId.toLowerCase());
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
                    if(myCell!=null ){
                        if(myCell.toString().startsWith("&") && !myCell.toString().contains(".")){
                            if(!moleculeExist(myCell.toString().replace("&","")))
                                missingActionMap.put(new Point(k,i),"Missing definition");
                        }
                        else if(!ZugGUI.spreadSheet.verifyExistence(myCell.toString()))
                            missingActionMap.put(new Point(k,i),"Missing definition");
                    }
                }

            }

        }

        UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
        table = new JTable(data, header){

            public boolean isCellEditable(int row,int column){
                if(column==0 || column==1) return false;
                return true;
            }
            public Class getColumnClass(int column) {
                Class clazz = String.class;
                switch (column) {
                    case 0:
                        clazz = Icon.class;
                        break;
                }
                return clazz;
            }
        }; ;

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn column = null;

        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);

        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(30);

        for (int i = 2; i <table.getColumnCount(); i++) {

            column = table.getColumnModel().getColumn(i);
            column.setCellRenderer(new CustomTableRowRenderer());

            if(i==actionColumn||i==verifyColumn){
                column.setCellRenderer(new CustomTableCellRenderer(missingActionMap));
            }

            for(int j=0;j<table.getRowCount();j++){
                if(table.getValueAt(j,i)==null){
                    table.setValueAt("",j,i);
                }
            }

            ZugGUI.spreadSheet.adjustColumnSizes(table,i,10);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                try {
                    JTable target = (JTable)e.getSource();
                    //int row = target.getSelectedRow();
                    int row = target.rowAtPoint(e.getPoint());
                    int column = target.getSelectedColumn();

                    if(column==0 && !target.getValueAt(row,2).toString().equalsIgnoreCase("comment")){

                        File file = new File(ZugGUI.getVisibleSpreadSheet().getAbsolutePath());
                        String nameSpace = FilenameUtils.removeExtension(file.getName()).toLowerCase();

                        if(target.getValueAt(row,0).getClass().getName().equalsIgnoreCase("javax.swing.ImageIcon")){
                            target.setValueAt("",row,0);
                            Object[] rows = new Object[1];
                            rows[0] = String.valueOf(row+1);
                            Controller.removeBreakPoints(nameSpace+"molecules", rows);
                        }else{

                            ImageIcon breakpointIcon = new ImageIcon("Images/breakpoint.png");
                            target.setValueAt(breakpointIcon,row,0);
                            Controller.setBreakPoint(nameSpace+"molecules", String.valueOf(row+1));

                        }
                    }
                } catch (Exception e1) {
                    // ignore click events outside the table
                }

            }
        });
        table.getModel().addTableModelListener(new SheetTableModelListener(sheetSaver));
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        return panel;
    }

    public boolean moleculeExist(String name){

        if(moleculeIDs.contains(name.toLowerCase()))return true;
        else return false;

    }

    public void removeAllBreakPoints(){
        if(table!=null)
            for(int i=0;i<table.getRowCount();i++)
                table.setValueAt("",i,0);

    }
}
