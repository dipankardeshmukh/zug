package com.automature.zug.gui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;



public class CheckListItem {
	private String  label;
	private boolean isSelected = false;

	public CheckListItem(String label){
		this.label = label;
	}
	public boolean isSelected(){
		return isSelected;
	}
	public void setSelected(boolean isSelected){
		this.isSelected = isSelected;
	}
	public String toString(){
		return label;
	}
	   
}

