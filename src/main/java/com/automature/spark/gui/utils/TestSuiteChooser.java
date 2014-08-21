/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.utils;

import com.automature.spark.gui.SessionHandler;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author skhan
 */
public class TestSuiteChooser {

    private String fileName;
    private File testSuite;

    public File getTestSuite() {
        return testSuite;
    }

    public String chooseTestSuite1(SessionHandler sh) {
        return null;
     /*   final SwingNode swingNode = new SwingNode();
        JFileChooser fileChooser = new JFileChooser();
        
        RecentDirectoryPanel chooser = new RecentDirectoryPanel(sh.getSession().getDirectories(), fileChooser);
        fileChooser.setAccessory(chooser);
        if (fileName != null && !fileName.isEmpty()) {
            fileChooser.setCurrentDirectory(new File(fileName).getParentFile());
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                 ".xls", ".xlsx");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(filter);
       
        swingNode.setContent(fileChooser);
        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);
        Stage stage=new Stage();
        stage.setTitle("Swing in JavaFX");
        stage.setScene(new Scene(pane, 250, 150));
        stage.show();    
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = fileChooser.getSelectedFile().getAbsolutePath();
            sh.addTestsuite(fileName);
            return fileName;
        } else {
            return "";
        }*/
    }

    public File chooseTestSuite(Stage stage) {
        
       
        
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Test suite");
        if(testSuite!=null){
            fileChooser.setInitialDirectory(new File(testSuite.getParent()));
        }
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Microsoft Excel Documents", "*.xls", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
           testSuite=selectedFile;
           return testSuite;
        }else{
            return null;
        }
    }

    public String getFileName() {
        return fileName;
    }
    
    public String showSaveDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Test suite");
        if(testSuite!=null){
            fileChooser.setInitialDirectory(new File(testSuite.getParent()));
        }
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Microsoft Excel Documents", "*.xls", "*.xlsx"));
        File selectedFile= fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
         }else{
             return null;
         }
    }

    public void setLastTestSuite(String file){
    	File f=new File(file);
    	if(f.exists()){
    		testSuite=f;
    	}
    }
}
