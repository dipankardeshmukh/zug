/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.components;

import java.util.ArrayList;
import java.util.List;

import com.automature.spark.gui.sheets.SheetSaver;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.util.Callback;

/**
 *
 * @author skhan
 */
public class SheetTab extends Tab{

	private String fileName;
	protected TableView<ObservableList<String>> tableView;
	private Callback<TableColumn<ObservableList<String>,String>, TableCell<ObservableList<String>,String>> cellFactory;
	private EventHandler<CellEditEvent<ObservableList<String>, String>> cellEditEventHandler;
	protected SheetSaver sheetSaver;
  
    public SheetTab(String string) {
        super(string);
        initialize();
    }
    
    
    
    public SheetSaver getSheetSaver() {
		return sheetSaver;
	}

	public void setSheetSaver(SheetSaver sheetSaver) {
		this.sheetSaver = sheetSaver;
		
	}

	private void initialize() {
		// TODO Auto-generated method stub
    	setClosable(false);
    	cellFactory =
                new Callback<TableColumn<ObservableList<String>,String>, TableCell<ObservableList<String>,String>>() {
                    public TableCell<ObservableList<String>,String> call(TableColumn<ObservableList<String>,String> p) {
                        return new EditableTableCell();
                    }
                }; 
        cellEditEventHandler = new EventHandler<CellEditEvent<ObservableList<String>, String>>() {
        			@Override
        			public void handle(
        					final CellEditEvent<ObservableList<String>, String> event) {

        				try {
        					final ObservableList<String> item = event.getRowValue();
        					      							
        					int i = event.getTablePosition().getColumn();
        					item.set(i, event.getNewValue());
        					getSheetSaver().SaveChange(event.getNewValue(), event.getTablePosition().getRow(), i);
        					// item.setName( event.getNewValue() );

        				} catch (Exception e) {
        					System.err.println("Error updating column "
        							+ e.getMessage());
        				}
        			}
        		};
        		
	}

	
	public List<List<String>> createEmptyList(int n,int m,int lastLineNo){
		List<List<String>> rows=new ArrayList<List<String>>();

		for(int i=0;i<n;i++){
			List<String> list=new ArrayList<String>(m);
			for(int j=0;j<m;j++){
				if(j==1){
					list.add((lastLineNo+i+1)+"");
				}else{
					list.add("");					
				}

			}
			rows.add(list);
		}
		return rows;

	}
	
	public String getFileName() {
  		return fileName;
  	}

  	public void setFileName(String fileName) {
  		this.fileName = fileName;
  	}
  	
     public ObservableList<ObservableList<String>> convertDataToObservableList(List<List<String>> data) {
        ObservableList<ObservableList<String>> csvData = FXCollections.observableArrayList();
        for (List<String> dataList : data) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (String rowData : dataList) {
                row.add(rowData==null?"":rowData);
            }
            csvData.add(row); // add each row to cvsData
        }
        return csvData;
    }
     public void addEditingProperties(TableColumn<ObservableList<String>,String> col){
    	 col.setCellFactory(cellFactory);
     }
     
      public void addHeaders(TableView<ObservableList<String>> tableView, List<String> headers) {
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
            addEditingProperties(col);
            col.setOnEditCommit(cellEditEventHandler);
        	col.setSortable(false);

            tableColumns[i] = col;

        }
        tableView.getColumns().addAll(tableColumns);
    }
      
       public void setRowFormatting(TableView<ObservableList<String>> table){
           
       }
    public void loadTabData(List<String> header, List<List<String>> data) {
        tableView = new TableView<>();
        tableView.setEditable(true);
        addHeaders(tableView, header);
        setRowFormatting(tableView);
        tableView.setItems(convertDataToObservableList(data));
        setContent(tableView);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
       
    }
    
    public void clearSelection(){
    	Platform.runLater(new Runnable()
    	{
    	    @Override
    	    public void run()
    	    {
    	    	tableView.requestFocus();
    	    	tableView.getSelectionModel().clearSelection();
    	    	tableView.getFocusModel();  	    
    	       // table.getSelectionModel().select(0);
    	       // table.getFocusModel().focus(0);
    	    }
    	});
    }
    
      
}
