package com.automature.spark.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import com.automature.spark.engine.ContextVar;
import com.automature.spark.gui.components.AutoCompleteComboBoxListener;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class VariableController1 implements Initializable {


	@FXML
	// private TableView<Map.Entry<String,String>> tableView;
	private TableView tableView;
	@FXML
	private Button reloadVariablesButton;

	@FXML
	private Button addVariableButton;

	@FXML
	private VBox variableVBox;

	@FXML
	private RadioMenuItem showCVMI;

	@FXML
	private RadioMenuItem showLVMI;

	@FXML
	private RadioMenuItem allMI;

	@FXML
	private ComboBox<String> searchBox;

	private List<String> watchList =new ArrayList<>();

	private EditVariableController editVariableController;

	public static int MAX_VALUE_SIZE = 100;
	public static int MAX_LIST_SIZE=5;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		try{
			ObservableList<TableColumn<Entry<String, String>, String>> columns=tableView.getColumns();
			columns.get(0).setCellValueFactory(
					(TableColumn.CellDataFeatures<Map.Entry<String,String>, String> p) -> 
					new SimpleStringProperty(p.getValue().getKey()));

			columns.get(1).setCellValueFactory(
					(TableColumn.CellDataFeatures<Map.Entry<String,String>, String> p) -> 
					new SimpleStringProperty(p.getValue().getValue().length()>MAX_VALUE_SIZE?p.getValue().getValue().substring(0, MAX_VALUE_SIZE):p.getValue().getValue()));
			tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					if(event.getClickCount()==2){
						Map.Entry<String,String> map=(Map.Entry<String,String>)tableView.getSelectionModel().getSelectedItem();
						if(map!=null){
							editVariableController.showStage(map.getKey(), map.getValue(),event.getScreenX(),event.getScreenY());
						}
					}

				}

			});	

			FXMLLoader fxmlLoader = new FXMLLoader();
			try {
				URL url = getClass().getResource("/com/automature/spark/gui/resources/editvariable.fxml");
				fxmlLoader.setLocation(url);
				Pane p = (Pane) fxmlLoader.load(url.openStream());
				editVariableController = (EditVariableController) fxmlLoader.getController();
				Scene scene = new Scene(p);
				Stage editVariableStage = new Stage();
				editVariableStage.setScene(scene);
				editVariableController.setStage(editVariableStage);
				editVariableStage.hide();
				editVariableStage.initStyle(StageStyle.UNDECORATED);
				editVariableStage.initModality(Modality.APPLICATION_MODAL);
				editVariableStage.setOnHiding(new EventHandler<WindowEvent>() {

					@Override
					public void handle(WindowEvent arg0) {
						// TODO Auto-generated method stub
						loadVariables();

					}
				});
			} catch (IOException ex) {
				System.err.println(ConsoleController.class.getName()+"\t:Error loading Editing pane :"+ ex);

			}

			new AutoCompleteComboBoxListener<String>(searchBox,showCVMI,showLVMI,allMI);
		}catch(Exception e){
			System.err.println("Error : Initializing Variable GUI.\nError message  "+e.getMessage()+"\t\nError Trace :"+e.getStackTrace());
		}

	}

	public void loadVariables() {
		try {
			String vars=watchList.toString();
			vars=vars.substring(1, vars.length()-1);
			Map<String, String> variables = ContextVar.getVariables(vars);
			ObservableList<Map.Entry<String, String>> listData = FXCollections
					.observableArrayList(variables.entrySet());
			tableView.setItems(listData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addVariable(){
		String variable=searchBox.getSelectionModel().getSelectedItem();
		if(watchList.size()>MAX_LIST_SIZE){
			watchList.remove(0);
		}
		watchList.add("'"+variable+"'");
		loadVariables();
	}
}
