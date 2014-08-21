package com.automature.spark.gui.components;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import com.automature.spark.engine.Spark;

public class MoleculeTreeTableSheetTab extends TreeTableSheetTab {

	
	public MoleculeTreeTableSheetTab(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}	
	
	@Override
	protected void setColumnFormattingForIDs(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub
		col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>,String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override public TextFieldTreeTableCell<ObservableList<String>, String> call(final TreeTableColumn<ObservableList<String>, String> p) {
				return new TextFieldTreeTableCell<ObservableList<String>, String>(new MyStringConverter()) {

					@Override public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						 if (!isEmpty()) {
                             setTextFill(Color.web("#00CCCC"));
                             setFont(new Font("Arial", 12));
                             setText(item);
                         }
					}
				};
			}
		});
		col.setEditable(true);
	

	}
	
	
	public void loadTabData(List<String> headers, List<List<String>> data){
		  super.loadTabData(headers, data);
		//  addContextMenu("Molecule");
       
	}
	
	public void setBreakPoint(String nameSpace,String row){
   	  //System.out.println("name space in sheet "+nameSpace+"molecules \t row="+String.valueOf(row+1));
         Spark.setBreakPoint(nameSpace+"molecules", row);    	
     }
     public void UnSetBreakPoint(String nameSpace,String row){
     	 Object[] rows = new Object[1];
          rows[0] = String.valueOf(row);
         Spark.removeBreakPoints(nameSpace+"molecules", rows);    	
     }
     
   /*  public void highLightRow(final int n){
       	
       //	final TableColumn tc=((TableColumn)tableView.getColumns().get(0));
       	Platform.runLater(new Runnable()
       	{
       	    @Override
       	    public void run()
       	    {
				 table.getSelectionModel().select(currentExecutionRow,table.getColumns().get(0));
				 table.getFocusModel().focus(currentExecutionRow, table.getColumns().get(0));
				 currentExecutionRow++;

       	    }
       	});
       }*/
}
