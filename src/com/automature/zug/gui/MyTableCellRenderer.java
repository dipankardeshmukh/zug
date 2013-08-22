package com.automature.zug.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class MyTableCellRenderer extends  DefaultTableCellRenderer {

	HashMap<Point,String> map;
	public MyTableCellRenderer(HashMap<Point,String> map){
		this.map=map;
	}
	
  public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
	  	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);     
    	setText(value !=null ? value.toString() : "");
    	Set s=map.keySet();
		Iterator it=s.iterator();
		while(it.hasNext()){
			Point p=(Point)it.next();
			int row=(int)p.getX(),col=(int)p.getY()+1;
			if(row==rowIndex && vColIndex==col){
			//	System.out.println("Setting tool tip for "+row+" "+col);
				setToolTipText(map.get(p));
				setForeground(Color.RED);
				return this;
			}
		}
		setForeground(Color.BLACK);
		setToolTipText("");
	//	setForeground(Color.blue);
		
        return this;
    }
}

