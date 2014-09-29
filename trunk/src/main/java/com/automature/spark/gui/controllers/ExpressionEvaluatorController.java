package com.automature.spark.gui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.engine.AtomHandler;
import com.automature.spark.gui.Constants;
import com.automature.spark.gui.Expression;
import com.automature.spark.gui.ExpressionEvaluator;
import com.automature.spark.gui.components.ActionHelper;
import com.automature.spark.gui.components.ArgumentHelper;
import com.automature.spark.gui.components.ArgumentTextFieldTreeTableCell;
import com.automature.spark.gui.components.AutoCompleteActionFilter;
import com.automature.spark.gui.components.AutoCompleteTextFieldTreeTableCell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.automature.spark.gui.components.MyStringConverter;
import com.automature.spark.gui.sheets.SpreadSheet;
import com.automature.spark.gui.utils.GuiUtils;


public class ExpressionEvaluatorController implements Initializable{

	@FXML
	private TreeTableView<ObservableList<String>> table;
	@FXML
	private Button clearButton;
	@FXML
	private Button cancelButon;
	@FXML
	private Button evaluateButton;
	
	private Stage stage;
	private EventHandler<CellEditEvent<ObservableList<String>, String>> cellEditEventHandler;
	
	private ExpressionEvaluator expressionEvaluator;
	private ArgumentHelper argHelper;
	private ZugguiController rootController;

	
	
	public void setRootController(ZugguiController rootController) {
		this.rootController = rootController;
	}
	public void setExpressionEvaluator(ExpressionEvaluator expressionEvaluator) {
		this.expressionEvaluator = expressionEvaluator;
	}
	public void setStage(Stage stage) {
		this.stage = stage;

	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		try{
				cellEditEventHandler = new EventHandler<CellEditEvent<ObservableList<String>, String>>() {
				@Override
				public void handle(
						final CellEditEvent<ObservableList<String>, String> event) {

					try {

					//	System.out.println("event "+event);
						final ObservableList<String> item = event.getRowValue()
								.getValue();
						int col = event.getTreeTablePosition().getColumn();
						int row = event.getTreeTablePosition().getRow();
						
						event.getTreeTableView().requestFocus();
						event.getTreeTableView().getFocusModel()
						.focus(event.getTreeTablePosition());
					//	System.out.println("row "+row+"\tcol "+col);
						item.set(col, event.getNewValue());
					//	System.out.println("item "+item);
					} catch (Exception e) {
						System.err.println("Error updating sheet "
								+ e.getMessage());
					}
				}
			};
			intializeTable();
			setRowFormatting();
			setTableData();
			table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			table.getSelectionModel().setCellSelectionEnabled(true);
			table.setEditable(true);
			table.setShowRoot(false);
			//intializeTable();
		}catch(Throwable e){
			e.printStackTrace();
		}

	}
	
	public void setRowFormatting(){
		table.setRowFactory(new Callback<TreeTableView<ObservableList<String>>, TreeTableRow<ObservableList<String>>>() {
			@Override
			public TreeTableRow<ObservableList<String>> call(
					TreeTableView<ObservableList<String>> tableView) {
				final TreeTableRow<ObservableList<String>> row = new TreeTableRow<ObservableList<String>>() {
					@Override
					public void updateItem(ObservableList<String> rowData,
							boolean empty) {

						// System.out.println("data "+rowData);
						if (rowData != null) {
							super.updateItem(rowData, empty);

							if (getIndex() % 2 == 0) {
								setStyle("-fx-background-color: "+Constants.treeTableRowColor+";\n"
										+ "    -fx-background-insets: 0, 0 0 1 0;\n"
										+ "    -fx-padding: 0.0em; /* 0 */\n"
										+ "    -fx-text-fill: -fx-text-inner-color;");
							} else {
								setStyle("-fx-background-color: "+Constants.treeTableRowAltColor+";\n"
										+ "    -fx-background-insets: 0, 0 0 1 0;");
							}

						}
					}
				};
				return row;
			}
		});

	}
	
	public void setTableData(){
		TreeItem<ObservableList<String>> root = new TreeItem<>();
		List<String> tmplist = new ArrayList<String>();
		for(int i=0;i<=20;i++){
			tmplist.add("");
		}
		List<List<String>> lists=new ArrayList<>();
		lists.add(tmplist);
		lists.add(tmplist);
		lists.add(tmplist);
		lists.add(tmplist);
		ObservableList<ObservableList<String>> obLists=convertDataToObservableList(lists);

		root.getChildren().add(new TreeItem<ObservableList<String>>(obLists.get(0)));
		root.getChildren().add(new TreeItem<ObservableList<String>>(obLists.get(1)));
		root.getChildren().add(new TreeItem<ObservableList<String>>(obLists.get(2)));
		root.getChildren().add(new TreeItem<ObservableList<String>>(obLists.get(3)));
		
		root.setExpanded(true);
		table.setRoot(root);
	}
	
	
	public void intializeTable(){

		if(rootController==null){
			intializeTable(ZugguiController.spreadSheet);
		}else{
			intializeTable(rootController.getCurrentSpreadSheet());			
		}

	}
	public void intializeTable(SpreadSheet spreadSheet){
		
		ActionHelper actionHelper=new ActionHelper(new AtomHandler(SpreadSheet.getScriptLocations()),spreadSheet.getAbsolutePath());
		argHelper = new ArgumentHelper(spreadSheet.getAbsolutePath());
		AutoCompleteActionFilter filter=new AutoCompleteActionFilter(actionHelper);
		TreeTableColumn<ObservableList<String>,String> actCol=(TreeTableColumn<ObservableList<String>, String>) table.getColumns().get(0);
		actCol.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override
			public TextFieldTreeTableCell<ObservableList<String>, String> call(
					final TreeTableColumn<ObservableList<String>, String> p) {
				TextFieldTreeTableCell<ObservableList<String>, String> tx =new 
						AutoCompleteTextFieldTreeTableCell(new MyStringConverter(), filter, actionHelper);// new TextFieldTreeTableCell<ObservableList<String>, String>(//new AutoCompleteTextFieldTreeTableCell(new MyStringConverter(), actionHelper);
				return tx;
			}
		});

		actCol.setEditable(true);
		actCol.setOnEditCommit(cellEditEventHandler);
		actCol.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<ObservableList<String>, String> p) {
				ObservableList<String> list = p.getValue().getValue();
			//	System.out.println("list "+list);
				if (list != null && 0 < list.size()) {
					SimpleStringProperty ssp = new SimpleStringProperty(p
							.getValue().getValue().get(0));
					return ssp;
				} else {
					return new SimpleStringProperty("");
				}

			}
		});
		for(int i=1;i< table.getColumns().size();i++){
			TreeTableColumn<ObservableList<String>,String> col=(TreeTableColumn<ObservableList<String>, String>) table.getColumns().get(i);
			col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
				@Override
				public TextFieldTreeTableCell<ObservableList<String>, String> call(
						final TreeTableColumn<ObservableList<String>, String> p) {
					return new ArgumentTextFieldTreeTableCell(new MyStringConverter(),argHelper);
				}
			});

			col.setSortable(false);
			col.setOnEditCommit(cellEditEventHandler);
			final int j=i;
			
			col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
				public ObservableValue<String> call(
						CellDataFeatures<ObservableList<String>, String> p) {
					ObservableList<String> list = p.getValue().getValue();
					//System.out.println("list "+list);
					if (list != null && j < list.size()) {
						SimpleStringProperty ssp = new SimpleStringProperty(p
								.getValue().getValue().get(j));
						return ssp;
					} else {
						return new SimpleStringProperty("");
					}

				}
			});
			col.setEditable(true);
		}
		if(stage!=null){
			stage.setTitle("Spark - Expression Evalutor : "+GuiUtils.getNameSpace(spreadSheet.getFileName()));
		}
	}
	
	public void evaluateExpression(){
		try{
		evaluateButton.setDisable(true);
		ObservableList<TreeItem<ObservableList<String>>> rows=table.getRoot().getChildren();
		List<Expression> expressions=new ArrayList<>(); 
		for(TreeItem<ObservableList<String>> item:rows){
			ObservableList<String> row=item.getValue();
			if(row.get(0)!=null&&StringUtils.isNotBlank(row.get(0))){
				ArrayList<String> args=new ArrayList<>();
				for(int i=1;i<row.size();i++){
					if(row.get(i)!=null&&StringUtils.isNotBlank(row.get(i))){
						args.add(row.get(i));
					}
				}
				expressions.add(new Expression(row.get(0), args));
			}
		}
		if(expressions.size()>0){
			expressionEvaluator.setMacroEvaluator(argHelper.getMacroEvaluator());
			expressionEvaluator.evaluateExpression(expressions);
		}
		}catch(Exception e){
			System.err.println("Error: Executing Expression Evaluator."+e.getMessage());
		}finally{
			evaluateButton.setDisable(false);			
		}

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
}
