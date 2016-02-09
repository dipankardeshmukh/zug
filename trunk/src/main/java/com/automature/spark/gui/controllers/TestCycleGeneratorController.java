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

public class TestCycleGeneratorController  implements Initializable{
	@FXML
	 private TextField name;
	@FXML
	  private ComboBox topoSetDropDown;
	@FXML
	  private ComboBox buildTagDropDown;
	@FXML
	  private Label errTxtLbl;
	@FXML
	  private Button submit;
	@FXML
	  private Button cancel;
	  
	  private static Stage stage;

	  public static TestCycleGeneratorController controller;
	  
	  public static boolean isBuildGeneratorOpenedFromTestCycleGenerator=false;
@Override
public void initialize(URL arg0, ResourceBundle arg1) {
	
	controller=this;
	
	name.setOnMouseClicked(event->{
		if(event.getClickCount()==2)
		{
			try {
				name.setText("TC : "+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
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
	topoSetDropDown.getItems().addAll(FXCollections.observableList(ZugguiController.reporter.getTopoSetsByTestPlanId(	ZugguiController.controller.getTestPlan().getText().substring(ZugguiController.controller.getTestPlan().getText().lastIndexOf('(')+1, ZugguiController.controller.getTestPlan().getText().lastIndexOf(')')))));
	buildTagDropDown.getItems().addAll(FXCollections.observableList(ZugguiController.reporter.getBuildsByProductId(	ZugguiController.controller.getProduct().getText().substring(ZugguiController.controller.getProduct().getText().lastIndexOf('(')+1, ZugguiController.controller.getProduct().getText().lastIndexOf(')')))));
	submit.setOnAction(event->{
		if(name.getText().equals("")||topoSetDropDown.getSelectionModel().getSelectedItem()==null)
		{
			errTxtLbl.setText("TestCyle description and topology set value should not empty");
			return;
		}
		ZugguiController.controller.getTestCycle().setText(name.getText());
		ZugguiController.controller.getTopoSet().setText(topoSetDropDown.getSelectionModel().getSelectedItem().toString());
		try{
		ZugguiController.controller.getBuildTag().setText(buildTagDropDown.getSelectionModel().getSelectedItem().toString());
		}catch(Exception ex){}
		
		ZugguiController.controller.setAllReporterPaneControlsEnabled();
		stage.close();
	});
	cancel.setOnAction(event->{
		ZugguiController.controller.setAllReporterPaneControlsEnabled();
		if(!ZugguiController.controller.getTopoSet().getText().equals(""))
		ZugguiController.controller.getTopoSet().setDisable(false);
		else
		ZugguiController.controller.getTopoSet().setDisable(true);	
		
		if(!ZugguiController.controller.getBuildTag().getText().equals(""))
		{
		ZugguiController.controller.getBuildTag().setDisable(false);
		ZugguiController.controller.getCreateBuildTagBtn().setDisable(false);
		}
		else
		{
		ZugguiController.controller.getBuildTag().setDisable(true);
		ZugguiController.controller.getCreateBuildTagBtn().setDisable(true);
		}
		stage.close();
	});
	
}

public void createBuildTag() {
	
	ZugguiController.controller.createBuildTag();

	isBuildGeneratorOpenedFromTestCycleGenerator=true;
}

public ComboBox getBuildTagDropDown() {
	return buildTagDropDown;
}

public static void setStage(Stage stage) {
	TestCycleGeneratorController.stage = stage;
}
}
