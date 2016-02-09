package com.automature.spark.gui.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;







import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BuildTagGeneratorController  implements Initializable{
	@FXML
	 private TextField name;
	@FXML
	  private Label errTxtLbl;
	@FXML
	  private Button submit;
	@FXML
	  private Button cancel;
	  
	  private static Stage stage;
   
@Override
public void initialize(URL arg0, ResourceBundle arg1) {
	name.setOnMouseClicked(event->{
		if(event.getClickCount()==2)
		{
			try {
				name.setText("Build : "+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	});
	name.textProperty().addListener(new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> arg0,
				String oldVal, String newVal) {
			if(newVal.contains("(") || newVal.contains(")") || newVal.endsWith(" "))
				name.setText(oldVal);
		}
		
	});
	submit.setOnAction(event->{
		if(name.getText().equals(""))
		{
			errTxtLbl.setText("BuildTag description should not empty");
			return;
		}
		
		if(!TestCycleGeneratorController.isBuildGeneratorOpenedFromTestCycleGenerator)
		{
			
		ZugguiController.controller.getBuildTag().setText(name.getText());

		}
		else
		{
			TestCycleGeneratorController.controller.getBuildTagDropDown().getItems().add(name.getText());
			
			TestCycleGeneratorController.controller.getBuildTagDropDown().getSelectionModel().select(name.getText());
		}
		if(!TestCycleGeneratorController.isBuildGeneratorOpenedFromTestCycleGenerator)
		ZugguiController.controller.setAllReporterPaneControlsEnabled();
		
		stage.close();
	});
	cancel.setOnAction(event->{
		
		if(!TestCycleGeneratorController.isBuildGeneratorOpenedFromTestCycleGenerator)
		ZugguiController.controller.setAllReporterPaneControlsEnabled();
		
		stage.close();
	});
	
}
public static void setStage(Stage stage) {
	BuildTagGeneratorController.stage = stage;
}
}
