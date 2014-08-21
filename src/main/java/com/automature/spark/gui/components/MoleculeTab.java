/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.components;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.automature.spark.engine.Spark;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 *
 * @author skhan
 */
public class MoleculeTab extends TestCaseTab {

  
    public MoleculeTab(String string) {
        super(string);
    }
    
      public void setColumnFormattingForIDs(TableColumn col){
          col.setCellFactory(new Callback<TableColumn, TableCell>() {
                            public TableCell call(TableColumn param) {
                                return new TableCell<ObservableList, String>() {

                                    @Override
                                    public void updateItem(String item, boolean empty) {
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
     
      
/*
    public void addHeaders(TableView tableView, List<String> headers) {

        TableColumn[] tableColumns = new TableColumn[headers.size()];
        // System.out.println("headers size" + headers.size() + " headers " + headers);
        for (int i = 0; i < headers.size(); i++) {
            final int index = i;
            String columName = headers.get(i);
            TableColumn col = new TableColumn(columName);
             if (i == 1) {
                col.setCellFactory(new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        TableCell cell = new TableCell<ObservableList, String>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? null : getString());
                            }

                            private String getString() {
                                return getItem() == null ? "" : getItem().toString();
                            }
                        };

                        cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                if (event.getClickCount() == 1) {
                                    TableCell c = (TableCell) event.getSource();
                                    if (c.getGraphic()==null){//c.getText() == null || !c.getText().equals("T")) {
                                        Image img = new Image(getClass().getResourceAsStream("/com/automature/zug/gui/resources/icons/black/glyph_breakpoint.png"));
                                        ImageView breakpointImg = new ImageView();
                                        breakpointImg.setImage(img);
                                        c.setGraphic(breakpointImg);
                                    } else {
                                        c.setGraphic(null);
                                    }
                                }
                            }
                        });
                        return cell;
                    }
                });
            } else if (columName.equalsIgnoreCase("Molecule ID")) {
                
            } else if (columName.equalsIgnoreCase("Action") || columName.equalsIgnoreCase("Verify")) {
                col.
                        setCellFactory(new Callback<TableColumn, TableCell>() {
                            public TableCell call(TableColumn param) {
                                return new TableCell<ObservableList, String>() {

                                    @Override
                                    public void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (!isEmpty()) {
                                            setTextFill(Color.web("#EDE061"));
                                            setFont(new Font("Arial", 12));
                                            setText(item);
                                        }
                                    }
                                };
                            }
                        });
            } else if (columName.toLowerCase().startsWith("actionarg_") || columName.toLowerCase().startsWith("verifyarg_")) {
                col.
                        setCellFactory(new Callback<TableColumn, TableCell>() {
                            public TableCell call(TableColumn param) {
                                return new TableCell<ObservableList, String>() {

                                    @Override
                                    public void updateItem(String item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (!isEmpty()) {
                                            setTextFill(Color.web("#EDBD61"));
                                            setFont(new Font("Arial", 12));
                                            setText(item);
                                        }
                                    }
                                };
                            }
                        });
            }
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
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

            if (i == 0 || i == 1) {
                col.setMinWidth(26.0);
            } else {
                col.setMinWidth(75.0);
            }

            tableColumns[i] = col;

        }
        tableView.getColumns().addAll(tableColumns);
    }*/
      
      public void highLightRow(final int n){
      	
      	final TableColumn tc=((TableColumn)tableView.getColumns().get(0));
      	Platform.runLater(new Runnable()
      	{
      	    @Override
      	    public void run()
      	    {
      	    	tableView.requestFocus();
      	    	
      	    	tableView.getSelectionModel().select(n-1, (TableColumn)tableView.getColumns().get(0));
      	    	tableView.getFocusModel().focus(n-1, tc);  	    
      	       // table.getSelectionModel().select(0);
      	       // table.getFocusModel().focus(0);
      	    }
      	});
      }
}
