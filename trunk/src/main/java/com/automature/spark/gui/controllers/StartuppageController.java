/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.controllers;

import com.automature.spark.gui.Constants;
import com.automature.spark.gui.utils.ApplicationLauncher;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author skhan
 */
public class StartuppageController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label openLabel;
    @FXML
    private Label helpLabel;
    @FXML
    private Label userCommLabel;
    @FXML
    private Label faqLabel;
    @FXML
    private VBox filesLabelBox;

    EventHandler mouseOver;
    EventHandler mouseExit;
    private Color mouserOverColor;
    private Color defaultColor;
    private ZugguiController rootController;

    public void setRootController(ZugguiController rootController) {
        this.rootController = rootController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	try{
    	
        mouserOverColor = Color.web(Constants.startPageLabelTextMouseOverColor);
        defaultColor = Color.web(Constants.startPageLabelTextdefaultColor);

        mouseOver = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Label label = (Label) e.getSource();
                label.setScaleX(1.1);
                label.setScaleY(1.1);
                label.setTextFill(mouserOverColor);
            }
        };
        mouseExit = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Label label = (Label) e.getSource();
                label.setScaleX(1);
                label.setScaleY(1);
                label.setTextFill(defaultColor);
            }
        };
        new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {

            }
        };
        openLabel.setOnMouseEntered(mouseOver);
        openLabel.setOnMouseExited(mouseExit);
        openLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Label label = (Label) e.getSource();
                List<String> params = new ArrayList<String>();
                params.add((String) label.getUserData());
                rootController.commandsFromStartUpPage(Constants.openTestSuiteChooserCommand, params);
            }
        });

        helpLabel.setOnMouseEntered(mouseOver);
        helpLabel.setOnMouseExited(mouseExit);
        helpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                ApplicationLauncher.launchUserCommunity();
            }
        });
        userCommLabel.setOnMouseEntered(mouseOver);
        userCommLabel.setOnMouseExited(mouseExit);
        userCommLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                ApplicationLauncher.launchUserCommunity();
            }
        });
        faqLabel.setOnMouseEntered(mouseOver);
        faqLabel.setOnMouseExited(mouseExit);
        faqLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                ApplicationLauncher.launchFAQs();

            }
        });
    	}catch(Exception e){
    		System.err.println("Error : Initializing Start Up Page.\nError message  "+e.getMessage()+"\nError Trace :"+e.getStackTrace());
    	}

    }

    public void addRecentlyUsedFile(Set<String> files) {
        Object[] rufiles = files.toArray();
        Font font = new Font("Arial Bold", 11);
        for (int i =  rufiles.length-1, j=0; j < 4 && i >= 0; i--) {
            File f = new File((String) rufiles[i]);
            if (f.exists()) {
                j++;
                Label label = new Label("   " + (j ) + "." + f.getName());
                label.setUserData((String) rufiles[i]);
                filesLabelBox.getChildren().add(label);
                label.setOnMouseEntered(mouseOver);
                label.setOnMouseExited(mouseExit);
                label.setTextFill(defaultColor);
                label.setFont(font);
                label.setMinHeight(20);
                label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        Label label = (Label) e.getSource();
                        List<String> params = new ArrayList<String>();
                        params.add((String) label.getUserData());
                        rootController.commandsFromStartUpPage(Constants.launchTestSuiteCommand, params);
                    }
                });
            }
        }
    }

}
