package com.automature.zug.gui.components;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class MacroColumns {

	String name;
	CheckListItem []columns;
	public CheckListItem[] getColumns() {
		return columns;
	}
	public MacroColumns(String name,String []names){
		this.name=name;
		columns=new CheckListItem[names.length];
		for(int i=0;i<names.length;i++){
			columns[i]=new CheckListItem(names[i]);
		}
		
	}
	public MacroColumns(String name,List<String> names){
		this.name=name;
		columns=new CheckListItem[names.size()];
		for(int i=0;i<names.size();i++){
			columns[i]=new CheckListItem(names.get(i));
		}	
	}
	@Override
	public String toString() {
		return "MacroColumns [name=" + name + ", columns="
				+ Arrays.toString(columns) + "]";
	}
	
	public String getFileName(){
		File f=new File(name);
		return f.getName();
	}
	
}
