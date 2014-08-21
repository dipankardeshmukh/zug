/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.components;

import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.gui.sheets.SpreadSheet;

import java.util.Iterator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author skhan
 */
public class LinkedFilesBorderPane extends BorderPane{
    
    
    private TreeItem<String> getSheetTree(SpreadSheet sp){
        TreeItem<String> rootItem = new TreeItem<String> (sp.getAbsolutePath());//, rootIcon);
        Iterator it = sp.getIncludeFiles().keySet().iterator();
        while(it.hasNext()){
            String key = it.next().toString();
            SpreadSheet sheet = sp.getIncludeFiles().get(key);
            rootItem.getChildren().add(getSheetTree(sheet));
        }
        return rootItem;   
        
    }
    
    
    
    public void loadLinkedFiles(SpreadSheet sp,final ZugguiController guiController){
        TreeView<String> treeView=new TreeView<String>(getSheetTree(sp));
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>(){

            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> ov, TreeItem<String> oldValue, TreeItem<String> newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                guiController.showSpreadSheet(newValue.getValue());
            }
    
        });
        setCenter(treeView);
        /*tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            TreeItem treeItem = (TreeItem)newValue;
            System.out.println("Selected item is" + treeItem);
        }
    });*/
     /*       
    tree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
    @Override
    public TreeCell<String> call(TreeView<String> paramP) {
      return new TreeCell<String>(){
           @Override
           protected void updateItem(String paramT, boolean paramBoolean) {
                super.updateItem(paramT, paramBoolean);
                if(!isEmpty()){
                     setGraphic(new Label(paramT));
                     final TreeCell<String> this$ = this;
                     this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                          @Override
                          public void handle(MouseEvent event) {
                               if(event.getClickCount()==2){
                                    // Getting the node value.
                                    String nodeValue = this$.getItem();
                                              
                                    // Opening a new stage by passing this value.
                                    Group root = new Group();
                                    root.getChildren().add(new Label(nodeValue));
                                    Scene scene = new Scene(root, 300, 300, Color.AQUA);
                                    Stage stg = new Stage();
                                    stg.setScene(scene);
                                    stg.show();
                               }
                          }
                     });
                }
           }
      };
    }
 });*/
    }

  
}
