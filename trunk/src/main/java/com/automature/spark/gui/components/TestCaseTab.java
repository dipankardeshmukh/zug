/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.components;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;





import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.automature.spark.engine.Spark;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author skhan
 */

public class TestCaseTab extends SheetTab {

	private CheckBox cb;
	private List<CheckBox> testCaseList = new ArrayList<CheckBox>();
	protected Set<TableCell> breakPoints=new HashSet<TableCell>();


	public TestCaseTab(String string) {
		super(string);
		//this();

	}

	public boolean isSelectAll(){
		if(cb.isSelected()){
			return true;
		}else{
			return false;
		}
	}

	public List<String> getSelectedTestCaseIds(){
		List<String> testCases=new ArrayList<String>();
		for(CheckBox cbox:testCaseList){
			if(cbox!=null&&cbox.isSelected()){
				testCases.add((String)cbox.getUserData());
			}
		}
		return testCases;
	}



	public void setColumnFormattingForActions(TableColumn col) {

		col.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell call(TableColumn param) {
				return new TableCell<ObservableList, String>() {

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (!isEmpty()) {
							if (item.startsWith("&")) {
								setTextFill(Color.web("#679900"));
								// setStyle("-fx-text-fill : #679900;");
							} else {
								setTextFill(Color.web("#00B258"));
								// setStyle("-fx-text-fill : #00B258;");
							}
							setFont(new Font("Arial", 12));
							setText(item);
						}
					}
				};
			}
		});
	}

	public void setColumnFormattingForArgs(TableColumn col) {

		col.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell call(TableColumn param) {
				return new TableCell<ObservableList, String>() {

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (!isEmpty()) {
							if (item.startsWith("$")) {
								setTextFill(Color.web("#4400CC"));
							} else {
								setTextFill(Color.web("#E60099"));
							}
							setFont(new Font("Arial", 12));
							setText(item);
						}
					}
				};
			}
		});

	}

	public void setColumnFormattingForIDs(TableColumn col) {
		cb = new CheckBox();
		cb.setUserData(col);
		cb.setOnAction(handleSelectAllCheckbox());
		col.setGraphic(cb);
		col.setCellFactory(
				new Callback<TableColumn<String, TestCaseId>, TableCell<String, TestCaseId>>() {
					@Override
					public
					TableCell<String, TestCaseId> call(TableColumn<String, TestCaseId> p) {
						CheckBoxTableCell<String, TestCaseId> checkBox = new CheckBoxTableCell<String, TestCaseId>();

						return checkBox;
					}
				});

	}

	public void setColumnFormattingForLine(TableColumn col) {
		col.setCellFactory(new Callback<TableColumn, TableCell>() {
			public TableCell call(TableColumn p) {
				final TableCell cell = new TableCell<ObservableList, String>() {
					
					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty ? null : getString());
						
					//	System.out.println("BP "+breakPoints+"\nthis"+this);
						if(breakPoints.contains(this)){
							setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/com/automature/zug/gui/resources/icons/black/glyph_breakpoint.png"))));
						}else{
							 setGraphic(null);
						}
					/*	String bp=(String)getUserData();
                         if(getGraphic()!=null){
                        	 System.out.println(item +" graphics is set\t"+bp);

                         }
                         if(bp==null||bp.equals("F")){
                        	 setGraphic(null);

                         }else if(bp.equals("T")){
                             if(getGraphic()==null){
                            	 setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/com/automature/zug/gui/resources/icons/black/glyph_breakpoint.png"))));
                             }
                         }else{
                        	 setGraphic(null);
                         }*/
					}

					private String getString() {
						return getItem() == null ? "" : getItem().toString();
					}
				};

				cell.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						// TODO Auto-generated method stub
						if (event.getClickCount() == 1) {
							final String row=cell.getText();
							//System.out.println("row "+row);
							final String nameSpace = FilenameUtils.removeExtension(new File(getFileName()).getName()).toLowerCase();
							TableCell cell = (TableCell) event.getSource();
							String bp=(String)getUserData();
							if (cell.getGraphic() == null&&(bp==null||bp.equals("F"))) {//c.getText() == null || !c.getText().equals("T")) {
								
								cell.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/com/automature/zug/gui/resources/icons/black/glyph_breakpoint.png"))));
								cell.setUserData("T");
								setBreakPoint(nameSpace, row);
								breakPoints.add(cell);

							} else {       	
								cell.setGraphic(null);
								cell.setUserData("F");
								breakPoints.remove(cell);
								UnSetBreakPoint(nameSpace, row);
							}
							//handleBreakPoint(c);
							//event.consume();
						}
					}
				
				});
				
				/*cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (event.getClickCount() == 1) {
							TableCell c = (TableCell) event.getSource();
							event.consume();
							handleBreakPoint(c);
							

						}
					}
				});*/
				return cell;
			}
		});
	}

	public void setBreakPoint(String nameSpace,String row){
		Spark.setBreakPoint(nameSpace, String.valueOf(row));    	
	}
	public void UnSetBreakPoint(String nameSpace,String row){
		Object[] rows = new Object[1];
		rows[0] = String.valueOf(row);
		Spark.removeBreakPoints(nameSpace, rows);    	
	}

	public void handleBreakPoint(final TableCell cell){
		final String row=cell.getText();
		
		final String nameSpace = FilenameUtils.removeExtension(new File(getFileName()).getName()).toLowerCase();
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				String bp=(String)getUserData();
				if (cell.getGraphic() == null&&(bp==null||bp.equals("F"))) {//c.getText() == null || !c.getText().equals("T")) {
										
					cell.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/com/automature/zug/gui/resources/icons/black/glyph_breakpoint.png"))));
					cell.setUserData("T");
					setBreakPoint(nameSpace, row);
					breakPoints.add(cell);

				} else {       	
					cell.setGraphic(null);
					cell.setUserData("F");
					breakPoints.remove(cell);
					UnSetBreakPoint(nameSpace, row);
				}
			//	System.out.println(breakPoints);


				//cell.updateTableColumn(cell.getTableColumn());
			}
		});


	}

	public void addHeaders(TableView<ObservableList<String>> tableView, List<String> headers) {
		//  System.out.println("add test case header");
		TableColumn[] tableColumns = new TableColumn[headers.size()];
		headers.set(0, "line");
		// System.out.println("headers size" + headers.size() + " headers " + headers);
		for (int i = 0; i < headers.size(); i++) {
			final int index = i;
			String columName = headers.get(i);
			TableColumn col = new TableColumn(columName);
			if (i == 0) {
				setColumnFormattingForLine(col);
			} else if (columName.endsWith(" ID")) {
				setColumnFormattingForIDs(col);
			} else if (columName.equalsIgnoreCase("Action") || columName.equalsIgnoreCase("Verify")) {
				setColumnFormattingForActions(col);
			} else if (columName.toLowerCase().startsWith("actionarg_") || columName.toLowerCase().startsWith("verifyarg_")) {
				setColumnFormattingForArgs(col);
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

			if (i == 0) {
				col.setMinWidth(26.0);
			} else {
				col.setMinWidth(75.0);
			}
			col.setOnEditCommit(new EventHandler<CellEditEvent<ObservableList, String>>() {
				@Override
				public void handle(CellEditEvent<ObservableList, String> t) {
					//System.out.println(t.getTableView().getItems().get(t.getTablePosition().getRow()));
					// ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setFirstName(t.getNewValue());
				}
			});
			tableColumns[i] = col;

		}
		tableView.getColumns().addAll(tableColumns);
	}

	private EventHandler<ActionEvent> handleSelectAllCheckbox() {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CheckBox cb = (CheckBox) event.getSource();
				TableColumn column = (TableColumn) cb.getUserData();
				if (cb.isSelected()) {
					Object ob = column.getUserData();
					for (CheckBox cbox : testCaseList) {
						cbox.setSelected(true);
						//   System.out.println("text " + cbox.getUserData());
					}

				} else {
					Object ob = column.getUserData();
					for (CheckBox cbox : testCaseList) {
						cbox.setSelected(false);
						//    System.out.println("text " + cbox.getUserData());
					}
				}
			}
		};
	}

	public void setRowFormatting(TableView table) {
		table.setRowFactory(new Callback<TableView<ObservableList>, TableRow<ObservableList>>() {
			@Override
			public TableRow<ObservableList> call(TableView<ObservableList> tableView) {
				final TableRow<ObservableList> row = new TableRow<ObservableList>() {
					@Override
					protected void updateItem(ObservableList rowData, boolean empty) {
						super.updateItem(rowData, empty);
						//System.out.println("data "+rowData);
						if (rowData != null ){

							if (rowData.get(1) != null && rowData.get(1).toString().equalsIgnoreCase("comment")) {
								//System.out.println("data " + rowData);
								//   this.setBackground(new Background(new BackgroundFill(Color.web("#DEE1E3"), CornerRadii.EMPTY, Insets.EMPTY)));
								//                          getStyleClass().add("table-row-commentrow");
								//                          applyCss();
								//  setStyle("table-row-commentrow");
								setStyle("-fx-background-color: #DEE1E3;");
								//getStyleClass().add("table-row-commentrow");
							} else {
								if (Integer.parseInt((String) rowData.get(0)) % 2 == 0) {
									//}.table-row-cell:selected {-fx-background-color: #A8F5FF;};
									//-fx-background-color: -fx-table-cell-border-color, -fx-control-inner-background-alt giving problem in java 7
									setStyle("-fx-background-color: #bbbbbb, white;\n"
											+ "    -fx-background-insets: 0, 0 0 1 0;\n"
											+ "    -fx-padding: 0.0em; /* 0 */\n"
											+ "    -fx-text-fill: -fx-text-inner-color;");
								} else {
									setStyle("-fx-background-color: #bbbbbb, derive(white,-2%);\n"
											+ "    -fx-background-insets: 0, 0 0 1 0;");
								}
								// this.setBackground(new Background(new BackgroundFill(Color.web("#FAFAFA"), CornerRadii.EMPTY, new Insets(0, 0010,0,0))));
								//getStyleClass().remove("table-row-commentrow");
							}
						}
					}
				};

				return row;
			}
		});
	}

	
	
	public class CheckBoxTableCell<S, T> extends TableCell<S, T> {

		private final CheckBox checkBox;
		private ObservableValue<T> ov;

		public CheckBoxTableCell() {
			this.checkBox = new CheckBox();
			this.checkBox.setAlignment(Pos.CENTER_LEFT);
			testCaseList.add(this.checkBox);
			setAlignment(Pos.CENTER_LEFT);
			setGraphic(checkBox);
		}

		@Override
		public void updateItem(T item, boolean empty) {
			String tcId = (String) item;
			if (empty && item == null) {
				setText(null);
				setGraphic(null);
			} else if (tcId == null || tcId.isEmpty() || tcId.equals(" ") || tcId.trim().equals("comment")) {
				setText(tcId);
				setGraphic(null);
				setTextFill(Color.web("#535757"));
			} else {
				// System.out.println("colored tcId :" + tcId + ":");
				setTextFill(Color.web("#00CCCC"));
				setFont(new Font("Arial", 12));
				setText((String) item);
				setGraphic(checkBox);
				checkBox.setUserData(item);
				if (cb != null) {
					if (cb.isSelected()) {
						checkBox.setSelected(true);
					} else {
						checkBox.setSelected(false);
					}
				}

				//checkBox.selectedProperty().unbindBidirectional(c);
				/* if (ov instanceof BooleanProperty) {
                 checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);

                 }
                 ov = getTableColumn().getCellObservableValue(getIndex());
                 if (ov instanceof BooleanProperty) {
                 checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
                 }*/
				//checkBox.selectedProperty().elected
				//getTableColumn().get
				// checkBox.setText(((SimpleStringProperty)label).getValue());
			}
		}
	}

	class TestCaseId {

		private String id;
		private boolean checked;
		private SimpleBooleanProperty selected;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public BooleanProperty selectedProperty() {
			return selected;
		}

		public boolean getSelected() {
			return selected.get();
		}

		public void setSelected(boolean selected) {
			this.selected.set(selected);
		}

	}

	public void removeAllBreakPoints(){
		
		Iterator<TableCell> it=breakPoints.iterator();
		while(it.hasNext()){
			TableCell tc=it.next();
			tc.setGraphic(null);

		}
		breakPoints.clear();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void highLightRow(final int n){

		final TableColumn tc=((TableColumn)tableView.getColumns().get(0));
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				tableView.requestFocus();
				if(n>0){
					//	tableView.getSelectionModel().select(n-1, (TableColumn)tableView.getColumns().get(0));
					//	tableView.getFocusModel().focus(n-1, tc);  	    
				}
				tableView.getSelectionModel().select(n-1, (TableColumn)tableView.getColumns().get(0));
				tableView.getFocusModel().focus(n-1, tc);  	    
				// table.getSelectionModel().select(0);
				// table.getFocusModel().focus(0);
			}
		});
	}

}
