package com.automature.spark.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ResourceBundle;











import com.automature.spark.engine.ContextVar;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class VariableController implements Initializable {

	
	public static int MAX_VALUE_SIZE = 100;
	@FXML
	// private TableView<Map.Entry<String,String>> tableView;
	private TableView tableView;
	@FXML
	private Button reloadVariablesButton;

	@FXML
	private Button addVariableButton;

	@FXML
	private Button copyButton;

	@FXML
	private VBox variableVBox;

	@FXML
	private RadioMenuItem showCVMI;

	@FXML
	private RadioMenuItem showLVMI;

	@FXML
	private RadioMenuItem allMI;
	
	private EditVariableController editVariableController;
	
	private List<String> watchList =new ArrayList<>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		//.getClass().getName());
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
			System.err.println(ConsoleController.class.getName()+"\t"+ ex);

		}
	
	}


	public void loadVariables() {
		try {
			Map<String, String> variables = null;
			if (showCVMI.isSelected()) {
				variables = ContextVar.getAllContextVariables();
			} else if (showLVMI.isSelected()) {
				variables = ContextVar.getAllLocalVariables();
			} else {
				variables = ContextVar.getAllVariables();
			}

			ObservableList<Map.Entry<String, String>> listData = FXCollections
					.observableArrayList(variables.entrySet());
			tableView.setItems(listData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addVariable(){
	//ditVariableController.showStage("", "",addVariableButton.,addVariableButton.getBoundsInParent().getMinY());
	}

}
