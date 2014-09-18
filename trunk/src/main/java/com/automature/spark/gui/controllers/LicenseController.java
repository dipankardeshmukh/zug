package com.automature.spark.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.automature.spark.engine.Spark;
import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.StageStyle;

public class LicenseController implements Initializable{

	private Stage stage;

	@FXML
	private TextField textfield; 
	@FXML
	private Button browseButton;
	@FXML
	private Button okButton;
	@FXML
	private Label messageLabel;

	private String errorMessage;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub


	}


	public void validate(){
		if(!validateLicence()){
			messageLabel.setText(errorMessage);
			stage.show();
		}else{
			stage.hide();
			messageLabel.setText("");
			okButton.setDisable(true);
		}
	}
	
	public void showLicenseValidator(){
		if(!validateLicence()){
			messageLabel.setText(errorMessage);
		}
		stage.show();
	}

	private boolean validateLicence() {

		try {
			com.automature.spark.license.LicenseValidator licenseValid = new com.automature.spark.license.LicenseValidator();
			if (licenseValid.matchMac() == false) {
				errorMessage="Please get a valid license for your machine";
				return false;
			}
			if (licenseValid.isDateValid() == false) {
				errorMessage="The License of SPARK has expired. Please renew.Import a valid license file";
				return false;
			}
		} catch (Exception e) {
			errorMessage="Failed to validate your License copy.Please import a valid license. ";
			return false;
		}
		return true;
	}

	public void chooseKeyFile(){

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose License");

		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Key file", "Spark License.key","Zug License.key"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {

			textfield.setText(selectedFile.getAbsolutePath());
			okButton.setDisable(false);
			
		}else{
			validate();
		}
	}


	public void setStage(Stage stage) {
		this.stage = stage;
		this.stage.initModality(Modality.APPLICATION_MODAL);
		this.stage.initStyle(StageStyle.UTILITY);
		this.stage.setWidth(450);
		this.stage.setHeight(350);
		this.stage.setResizable(false);
		this.stage.setTitle("License Validation failed!");
	}

	public void saveLicenseFile(){
		try {
			Utility.copyFile(textfield.getText(), System.getProperty("user.dir")+File.separator+"Spark License.key");
			textfield.setText("");
			validate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error importing license file to Spark Directory :Error message "+e.getMessage());
		}
	}

}
