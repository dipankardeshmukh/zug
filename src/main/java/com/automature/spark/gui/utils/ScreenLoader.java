package com.automature.spark.gui.utils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.automature.spark.gui.controllers.ConsoleController;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ScreenLoader {

	private Stage stage;
	private Initializable controller;
	private Parent root;
	
	public ScreenLoader(String fxmlFile) throws IOException
	{
		loadFxml(fxmlFile);
	}
	
	public void loadFxml(String fxmlFile) throws IOException{
		FXMLLoader fxmlLoader = new FXMLLoader();
		try {
			URL url = getClass().getResource(fxmlFile);
			fxmlLoader.setLocation(url);
			root= fxmlLoader.load(url.openStream());
			controller =  fxmlLoader.getController();
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.setScene(scene);
		}catch (IOException ex) {
			ex.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Stage getStage() {
		return stage;
	}

	public Initializable getController() {
		return controller;
	}

	public Parent getRoot() {
		return root;
	}
	
	
}
