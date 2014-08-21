package com.automature.spark.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.automature.spark.engine.ContextVar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditVariableController implements Initializable {

	private Stage stage;
	
	@FXML
	private TextField keyField;
	
	@FXML
	private TextArea valueTextArea;
	
	@FXML
	private Button saveButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private VBox editVBox;
	
	@FXML
	private Pane alertPane;
	@FXML
	private Label errorLabel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void showStage(String key,String value, double X, double Y){
		if(ContextVar.checkIfContexVarExists(key)){
			if(!(value.length()<VariableController.MAX_VALUE_SIZE-1)){
				try{
					value=ContextVar.getContextVar(key);				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			keyField.setText(key);
			valueTextArea.setText(value);
			editVBox.setVisible(true);
			alertPane.setVisible(false);
		}else{
			errorLabel.setText("variable "+key+" does not exists.It may have been unset by test case or automatically unset by Spark (local variable)");
			editVBox.setVisible(false);
			alertPane.setVisible(true);
			
		}
		
		stage.setX(X-40);
		stage.setY(Y-40);
		stage.show();
		
	}
	
	public void hide(){
		stage.hide();
	}
	
	public void save(){
		try {
			ContextVar.setContextVar(keyField.getText(), valueTextArea.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hide();
	}
	
}
