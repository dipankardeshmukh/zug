package com.automature.spark.gui.components;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;

public class MacroTab extends SheetTab{

	private  ToggleGroup group ;
	private boolean firstColSet;
	
	
	public MacroTab(String string) {
		super(string);
		
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	private void initialize(){
		group= new ToggleGroup();
	}
	@Override
	public void addEditingProperties(TableColumn<ObservableList<String>,String> col){
   	 	super.addEditingProperties(col);
   	 	/*if(!col.getText().equalsIgnoreCase("comment")&&!col.getText().equalsIgnoreCase("comments")&&!col.getText().equalsIgnoreCase("macro name")){
   	 		RadioButton rb=new RadioButton();
   	 		col.setGraphic(rb);
   	 		if(!firstColSet){
   	 			rb.setSelected(true);
   	 		}
   	 		rb.setToggleGroup(group);
   	 	}*/	
    }
	
	  /* public void addHeaders(TableView<ObservableList<String>> tableView, List<String> headers) {
	        TableColumn[] tableColumns = new TableColumn[headers.size()];
	       // System.out.println("headers size" + headers.size() + " headers " + headers);
	        for (int i = 0; i < headers.size(); i++) {
	            final int index = i;
	            String columName = headers.get(i);
	            TableColumn<ObservableList<String>,String> col = new TableColumn<>(columName);

	            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
	                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
	                    //System.out.println("param values "+param);
	                    try {
	                        SimpleStringProperty ssp = new SimpleStringProperty(param.getValue().get(index).toString());
	                        return ssp;
	                    } catch (Exception e) {
	                        SimpleStringProperty ssp = new SimpleStringProperty(" ");
	                        return ssp;
	                    }
	                }
	            });
	            if(i==0){
	                col.setMinWidth(26.0);
	            }else{
	                col.setMinWidth(75.0);
	            }
	        	col.setSortable(false);

	            tableColumns[i] = col;

	        }
	        tableView.getColumns().addAll(tableColumns);
	    }*/
}
