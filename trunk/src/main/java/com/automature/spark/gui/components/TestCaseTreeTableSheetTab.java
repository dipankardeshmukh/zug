package com.automature.spark.gui.components;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.automature.spark.gui.Constants;
import com.automature.spark.gui.components.TreeTableSheetTab.MyStringConverter;
import com.automature.spark.gui.utils.GuiUtils;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.effect.Blend;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class TestCaseTreeTableSheetTab extends TreeTableSheetTab {

	private CheckBox selectAllCheckBox;
	
	private Map<String, CheckBox> testCaseList = new HashMap<>();
	private Map<String, CheckBox> testCaseIdMap = new HashMap<>();

	
	public TestCaseTreeTableSheetTab(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setColumnFormattingForIDs(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub
		selectAllCheckBox = new CheckBox();
		selectAllCheckBox.setUserData(col);
		selectAllCheckBox.setOnAction(handleSelectAllCheckbox());
		col.setGraphic(selectAllCheckBox);
		col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override
			public TextFieldTreeTableCell<ObservableList<String>, String> call(
					final TreeTableColumn<ObservableList<String>, String> p) {
				return new TestCaseIdTextFieldTreeTableCell(
						new MyStringConverter());
			}
		});
	}
	
	

	/*public void highLightRow(final int n) {

		// final TableColumn tc=((TableColumn)tableView.getColumns().get(0));
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				table.requestFocus();
				 table.getSelectionModel().select(currentExecutionRow,table.getColumns().get(0));
				 table.getFocusModel().focus(currentExecutionRow, table.getColumns().get(0));
				 currentExecutionRow++;
				}
		});
	}*/

	public boolean isSelectAll() {
		if (selectAllCheckBox.isSelected()) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> getSelectedTestCaseIds() {
		List<String> testCases = new ArrayList<String>();
		Iterator<String> it = testCaseIdMap.keySet().iterator();
		while (it.hasNext()) {
			testCases.add(it.next());
		}
		/*
		 * for(CheckBox cbox:testCaseList){ if(cbox!=null&&cbox.isSelected()){
		 * testCases.add((String)cbox.getUserData()); } }
		 */
		return testCases;
	}

	private EventHandler<ActionEvent> handleSelectAllCheckbox() {
		// TODO Auto-generated method stub
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CheckBox cb = (CheckBox) event.getSource();
				// TableColumn column = (TableColumn) cb.getUserData();
				if (cb.isSelected()) {
					// Object ob = column.getUserData();
					Iterator<String> it = testCaseList.keySet().iterator();
					while (it.hasNext()) {
						testCaseList.get(it.next()).setSelected(true);
					}
					/*
					 * for (CheckBox cbox : testCaseList) {
					 * cbox.setSelected(true); // System.out.println("text " +
					 * cbox.getUserData()); }
					 */
				} else {
					// Object ob = column.getUserData();
					testCaseIdMap.clear();
					Iterator<String> it = testCaseList.keySet().iterator();
					while (it.hasNext()) {
						testCaseList.get(it.next()).setSelected(false);
					}
					/*
					 * for (CheckBox cbox : testCaseList) {
					 * cbox.setSelected(false); // System.out.println("text " +
					 * cbox.getUserData()); }
					 */
				}
			}
		};
	}
	
	
	
	public void loadTabData(List<String> headers, List<List<String>> data){
		  super.loadTabData(headers, data);
		//  addContextMenu("Test Case");
         
	}

	class TestCaseIdTextFieldTreeTableCell extends
			TextFieldTreeTableCell<ObservableList<String>, String> {

		private CheckBox checkBox;

		public TestCaseIdTextFieldTreeTableCell() {
			EventHandler<MouseDragEvent> mouseEvent=new EventHandler<MouseDragEvent>() {  
				  
                @Override  
                public void handle(MouseDragEvent event) {
                	//System.out.println("mouse drag event "+getIndex()+"\t"+ getTableColumn());
                	getTableColumn().getTreeTableView().getFocusModel().focus(getIndex(), getTableColumn());
                    getTableColumn().getTreeTableView().getSelectionModel().select(getIndex(), getTableColumn());  
                }  
                
            };
			setOnDragDetected(new EventHandler<MouseEvent>() {  
                @Override  
                public void handle(MouseEvent event) {  
                    startFullDrag();  
                 //   System.out.println("mouse event "+getIndex()+"\t"+ getTableColumn());
                    getTableColumn().getTreeTableView().getFocusModel().focus(getIndex(), getTableColumn());
                    getTableColumn().getTreeTableView().getSelectionModel().select(getIndex(), getTableColumn());  
                }  
            });  
           setOnMouseDragEntered(mouseEvent);
           setOnMouseDragOver(mouseEvent);
           
			// TODO Auto-generated constructor stub
		}

		public TestCaseIdTextFieldTreeTableCell(StringConverter<String> arg0) {
			super(arg0);
			
			EventHandler<MouseDragEvent> mouseDragEvent=new EventHandler<MouseDragEvent>() {  
				  
                @Override  
                public void handle(MouseDragEvent event) {
                	//System.out.println("mouse drag event "+getIndex()+"\t"+ getTableColumn());
                	getTableColumn().getTreeTableView().getFocusModel().focus(getIndex(), getTableColumn());
                    getTableColumn().getTreeTableView().getSelectionModel().select(getIndex(), getTableColumn());  
                }  
                
            };
            EventHandler<MouseEvent> mouseEvent= new EventHandler<MouseEvent>() {  
                @Override  
                public void handle(MouseEvent event) {  
                    startFullDrag();  
                    //System.out.println("mouse event "+getIndex()+"\t"+ getTableColumn());
                    getTableColumn().getTreeTableView().getFocusModel().focus(getIndex(), getTableColumn());
                    getTableColumn().getTreeTableView().getSelectionModel().select(getIndex(), getTableColumn());  
                }  
            };
			setOnDragDetected(mouseEvent); 
			setOnMouseDragged(mouseEvent); 
		
			
           setOnMouseDragEntered(mouseDragEvent);
           setOnMouseDragOver(mouseDragEvent);
         
			// TODO Auto-generated constructor stub
		}

		@Override
		public void updateItem(String item, boolean empty) {
			// TODO Auto-generated method stub
			super.updateItem(item, empty);
			if (empty && item == null) {
				setText(null);
				setGraphic(null);
			} else if (item == null || item.isEmpty() || item.equals(" ")
					|| item.trim().equals("comment")) {
				setText(item);
				setGraphic(null);
				setTextFill(Color.web("#535757"));
			} else {
				// System.out.println("colored tcId :" + tcId + ":");
				setTextFill(Color.web(Constants.testcaseColor));
				setFont(new Font("Arial", 12));
				setText((String) item);
				if (checkBox == null) {
					checkBox = new CheckBox();
					checkBox.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							CheckBox cb = (CheckBox) event.getSource();
							if (cb.isSelected()) {
								testCaseIdMap.put(getItem(), cb);
							} else {
								testCaseIdMap.remove(getItem());
							}

						}
					});
				}
				if (selectAllCheckBox != null && selectAllCheckBox.isSelected()) {
					checkBox.setSelected(true);
				} else {
					if (testCaseIdMap.containsKey(item)) {
						checkBox.setSelected(true);
					} else {
						checkBox.setSelected(false);
					}
				}

				testCaseList.put(getItem(), checkBox);
				setGraphic(checkBox);
				checkBox.setUserData(item);
			}

		}
	}
}
