package com.automature.zug.gui;

import com.automature.zug.gui.sheets.SpreadSheet;
import org.jdesktop.swingx.JXTable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.*;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;

public class SheetDisplayPane extends JPanel {

	private JTabbedPane tabbedPane; 
	private  HashMap<String, String> externalSheets;
	public static ArrayList testCaseIds;
    private SpreadSheet sp;

	SheetDisplayPane(SpreadSheet sh) throws Exception {

        sp = sh;
		tabbedPane = new JTabbedPane(JTabbedPane.VERTICAL);
		tabbedPane.setBorder(new LineBorder(new Color(0, 0, 0)));

        addTab("Config",null,sh.getConfigSheetPanel(),null);
        addTab("Macros",null,sh.getMacroSheetPanel(),null);
        addTab("TestCases",null,sh.getTestCasesSheetPanel(),null);
        addTab("Molecules",null,sh.getMoleculesSheetPanel(),null);

        if(sh.getAbsolutePath().equalsIgnoreCase(ZugGUI.spreadSheet.getAbsolutePath())){
            tabbedPane.setSelectedIndex(2);
        }else{
            tabbedPane.setSelectedIndex(3);
        }

/*		try{
			ExcelHandler ex=new ExcelHandler(fileName);
			HashMap map=ex.getSheets();
			Set s=map.keySet();
			Iterator it=s.iterator();
			ZugGUI.removeAllTabs();
			while(it.hasNext()){
				Excel sht=(Excel)map.get(it.next());
				if(sht.isMainSheet()){
					testCaseIds=sht.getTestCaseIDs();
				//	System.out.println("sdp:"+testCaseIds);
				}
				JPanel panel=getPanel(sht);
				addTab(sht.getAbsolutePath(), null,panel, null);
			}
			externalSheets=ex.getExternalSheets();
		}catch(Exception e){
			Log.Error(e.getMessage());
			System.err.println("SheetDisplayPane Exception "+e.getMessage()+"\nCause "+e.getCause());
		}*/

		this.setLayout(new BorderLayout());
		this.setBackground(Color.white);
		this.add(tabbedPane, BorderLayout.CENTER);
	}

    public SpreadSheet getSpreadSheet(){
        return sp;
    }
	private JPanel getPanel(Excel excel){

		JPanel panel = new JPanel(new BorderLayout());
		//table = new JTable(dataHolder,(Vector)dataHolder.firstElement());
		final HashMap<Point,String> map= excel.messageMap;
		JXTable table = new JXTable(excel.getData(), excel.getHeader());

		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn column = null;


		int actionColumn= excel.getActionColumn();
		int verifyColumn= excel.getVerifyColumn();

		for (int i = 1; i <table.getColumnCount(); i++) {			
			column = table.getColumnModel().getColumn(i);
/*			if(i==(actionColumn)||i==(actionColumn-1)){
				column.setPreferredWidth(120);
			}else{
				column.setPreferredWidth(200);
			}*/

			if(i==actionColumn+1||i==verifyColumn+1){
			//	column.setCellRenderer(new CustomTableCellRenderer(excel.messageMap));
				column.setCellRenderer(new CustomTableCellRenderer(excel.messageMap));
			}
		}

		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);
		return panel;
	}

	private void addTab(String title,Icon icon,Component component,String tip){
		tabbedPane.addTab(title, icon, component, tip);
	}

	public void addExtraSheets(HashMap<String, String> nameSpace, String[] sheets){

		try{

			for(String sheet:sheets){
				if(!externalSheets.containsValue(sheet)){
					ExcelHandler ex=new ExcelHandler(sheet,externalSheets,false);
					HashMap map=ex.getSheets();
					Set s=map.keySet();
					Iterator it=s.iterator();
					while(it.hasNext()){
						Excel sht=(Excel)map.get(it.next());
						JPanel panel=getPanel(sht);
						if(nameSpace.containsKey(sht.getFullFileName())){
							if(getTab(nameSpace.get(sht.getFullFileName()))==-1)
								addTab(nameSpace.get(sht.getFullFileName()), null,panel, "Included file");
						}else{
							if(getTab(sht.getFileName())==-1)
								addTab(sht.getFileName(), null,panel, "Included file");
						}
					}
					externalSheets=ex.getExternalSheets();	
				}
			}	
		}catch(Exception e){
			System.err.println("SheetDisplayPane/AddExtraSHeet Error while adding runtime sheet tabs "+e.getMessage()+"\nCause "+e.getCause());
		}
	}

	public void showRunningTestStep(int n) throws Exception {

        //ZugGUI.getDisplayPane().bringSheetDisplayPane(ZugGUI.spreadSheet);

        tabbedPane.setSelectedIndex(2);
		JPanel jp=(JPanel)tabbedPane.getComponentAt(2);
		JScrollPane sp=(JScrollPane)jp.getComponent(0);
		JViewport viewport = sp.getViewport(); 
		JTable table = (JTable)viewport.getView();
		table.setRowSelectionInterval(0,n-1);


	}

    public void showRunningMoleculeStep(String name,int n,int start){
        //	if(n!=0)
        try{
            int pos=getTab(name);
            tabbedPane.setSelectedIndex(pos);
            JTable table = getTable(pos);
            table.setRowSelectionInterval(start,n-1);
        }catch(Exception e){
            //Log.Error(e.getMessage());
        }
    }

	public int getTab(String name){

		int tabs=tabbedPane.getTabCount();
		for(int i=0;i<tabs;i++){
			String tabName=tabbedPane.getTitleAt(i);
			if(tabName.equalsIgnoreCase(name)){
				return i; 
			}
		}
		return -1;
	}

	public void showTab(String name){

		int pos=getTab(name);
		if(pos>-1)
			tabbedPane.setSelectedIndex(pos);
	}

	private JTable getTable(int pos){

		if(pos>-1){
			JPanel jp=(JPanel)tabbedPane.getComponentAt(pos);
			JScrollPane sp=(JScrollPane)jp.getComponent(0);
			JViewport viewport = sp.getViewport(); 
			JTable table = (JTable)viewport.getView();
			return table;
		}else{
			return null;	
		}
	}



	public void showSelectedStep(String name,int n){
		try{
			int pos=getTab(name);
			tabbedPane.setSelectedIndex(pos);
			JTable table = getTable(pos);
			table.setRowSelectionInterval(n-1, n-1);
		}catch(Exception e){
			//	Log.Error(e.getMessage());
		}
	}

	public ArrayList getSheetNames(){
		int tabs=tabbedPane.getTabCount();
		ArrayList<String> names=new ArrayList<String>();
		for(int i=0;i<tabs;i++){
			String tabName=tabbedPane.getTitleAt(i);
			if(tabName.contains(".")){
				tabName=tabName.substring(0,tabName.lastIndexOf("."));
			}
			names.add(tabName);
		}
		return names;
	}

	public int getMaxLine(String name){
		try{
			if(name.contains(".")){
				name=name.substring(0,name.lastIndexOf("."));
			}
			int pos=getTab(name);
			JTable table = getTable(pos);
			return table.getRowCount();
		}catch(Exception e){
			System.err.println("SheetDisplayPane/getMaxLine Exception "+e.getMessage()+"\nCause "+e.getCause());
		}
		return 0;
	}
}
