package com.automature.spark.gui;



import com.automature.spark.engine.Spark;
import com.automature.spark.exceptions.ReportingException;
import com.automature.spark.gui.controllers.GuiController;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.gui.utils.ApplicationLauncher;
import com.automature.spark.gui.utils.GuiUtils;

import java.io.InputStream;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author skhan
 */
public class ZugGui  extends Application {

	private static ZugguiController controller;

	public static GuiController getController() {
		return controller;
	}

	@Override
	public void start(Stage stage) throws Exception {

		String fxml="/com/automature/spark/gui/resources/zuggui.fxml";
		FXMLLoader loader = new FXMLLoader();
		InputStream in = ZugGui.class.getResourceAsStream(fxml);
		loader.setBuilderFactory(new JavaFXBuilderFactory());
		loader.setLocation(ZugGui.class.getResource(fxml));
		AnchorPane page=null;
		try {
			page = (AnchorPane) loader.load(in);
			controller=loader.getController();
			Spark.setController(controller);
		}catch(Exception e){
			e.printStackTrace();
			return;
		}finally {
			if(in!=null){
				in.close();
			}else{ 
				System.err.println("Error : Could not load zuggui.fxml");
			}
		} 

		Scene scene = new Scene(page);       
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
					try {
						if(Spark.opts.dbReporting)
						Spark.reporter.heartBeat(Spark.sessionid);
					} catch (Exception e) {
					} 
					
				System.exit(0);
			}
		});
		stage.setTitle("SPARK");
		stage.getIcons().add(new Image(ZugGui.class.getResourceAsStream("/com/automature/spark/gui/resources/icons/Spark.png")));
		ApplicationLauncher.setHostServices(getHostServices());
		GuiUtils.setStage(stage);
		controller.setParams(getParameters().getRaw());
	}


	/**
	 * @param args the command line arguments
	 */

}
