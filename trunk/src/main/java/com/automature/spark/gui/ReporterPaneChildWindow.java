package com.automature.spark.gui;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.gui.controllers.ConsoleController;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.reporter.SpacetimeReporter;
import com.automature.spark.util.Styles;
public class ReporterPaneChildWindow {
public void displayListView(ArrayList<String> items, TextField text,String type, SpacetimeReporter reporter, ArrayList<String> ob, ArrayList<String> ob1, MouseEvent event2){
	if(type.equals("Testplans"))
	{

	ZugguiController.controller.getCreateTestCycleBtn().setDisable(true);
 	ZugguiController.controller.getCreateBuildTagBtn().setDisable(true);
	}
		VBox contentPane=new VBox();
		HBox submitPanel=new HBox();
		
		submitPanel.setPadding(new Insets(5));
		submitPanel.setStyle(Styles.submitPanelStyle);
		submitPanel.setSpacing(5);

		Button submit=new Button("Submit");
		submitPanel.getChildren().add(submit);
		
		Button prev=new Button("<<< Prev");
		submitPanel.getChildren().add(prev);
		
		Button next=new Button("Next >>>");
		submitPanel.getChildren().add(next);
		

		Button cancel=new Button("Cancel");
		submitPanel.getChildren().add(cancel);
		
		text.setDisable(true);
		ObservableList data = 
		        FXCollections.observableArrayList();
		       
		Stage stage=new Stage();
		ZugguiController.controller.pageNumber=1;
		try{
		if(items.size()==0)
		{
			if(type.equals("Testplans"))
        	{
					items.addAll(reporter.getTestPlanList(getTestInParenthesis(ZugguiController.controller.getProduct()),InetAddress.getLocalHost().getHostAddress()));
				
        	}
        	else if(type.equals("TestCycles"))
        	{
        		items.addAll(reporter.getTestCycleList(getTestInParenthesis(ZugguiController.controller.getProduct()),(ZugguiController.controller.getTestPlan().getText()),InetAddress.getLocalHost().getHostAddress()));
        	}
        	else if(type.equals("TopologyStets"))
        	{
        		items.addAll(reporter.getTestCycleTopologysetList(getTestInParenthesis(ZugguiController.controller.getTestCycle())));
        	}
        	else if(type.equals("Builds"))
        	{
        		items.addAll(reporter.getBuildTagsForTestCycleAndTopologyset(getTestInParenthesis(ZugguiController.controller.getTopoSet()),getTestInParenthesis(ZugguiController.controller.getTestCycle())));
        	}
		}
		}catch(Exception e){}
		int num=(int)Math.ceil(Double.parseDouble(Double.toString(items.size()))/10);
		stage.setTitle("List Of "+type +" [ List "+ZugguiController.controller.pageNumber+" of "+num+" ]");        
        
        final ListView listView = new ListView(data);
        listView.setPrefSize(300, 400);
        listView.setEditable(false);
        ArrayList<String> listOfVisibles=new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
			if(i%10==0 && i!=0)
			{
				ZugguiController.controller.listElementFinalIndex=i;
				break;
			}
			else
				listOfVisibles.add(items.get(i));
		}
        data.addAll(
             listOfVisibles
        );
         if(data.size()==0)
         {
//        	 Stage dialogStage = new Stage(StageStyle.UTILITY);
//        	 dialogStage.initModality(Modality.WINDOW_MODAL);
//        	 Button b=new Button("OK");
//        	 dialogStage.setScene(new Scene(VBoxBuilder.create().
//        	     children(new Text("No "+type+" exist"), b).
//        	     alignment(Pos.CENTER).padding(new Insets(5)).build()));
//        	 dialogStage.show();
        	 ConsoleController.controller.clear();
        	 System.err.println("\n\tNo "+type+" exist");
        	 return;
         }
        listView.setItems(data);
               
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add(listView);
        contentPane.getChildren().addAll(rootPane,submitPanel);
        
        submit.setOnAction(event->{
        	try{
        	text.setText(listView.getSelectionModel().getSelectedItem().toString());
        	}catch(Exception e){
        		try{
        			if(!text.getText().equals(""))
                	text.getParent().getChildrenUnmodifiable().get(text.getParent().getChildrenUnmodifiable().lastIndexOf(text)+2).setDisable(false);
                	}catch(Exception ex){}
        		stage.close();
            	ZugguiController.controller.isPopupOpened=false;
            	text.setDisable(false);
            	closeAction(type, text);
            	return;
        	}
        	text.setDisable(false);
        	if(ob!=null)
        	ob.clear();
        	
        	if(ob1!=null)
        	ob1.clear();
        	try{
        	if(type.equals("Products"))
        	{
        		ZugguiController.controller.productId=text.getText().substring(text.getText().lastIndexOf("(")+1, text.getText().lastIndexOf(")"));
        	    ob.addAll(reporter.getTestPlanList(ZugguiController.controller.productId,InetAddress.getLocalHost().getHostAddress()));
        	}
        	else if(type.equals("Testplans"))
        	{
        	ob.addAll(reporter.getTestCycleList(ZugguiController.controller.productId, text.getText(),InetAddress.getLocalHost().getHostAddress()));
        	ZugguiController.controller.getCreateTestCycleBtn().setDisable(false);
        	}
        	else if(type.equals("TestCycles"))
        	{
        		ob.addAll(reporter.getTestCycleTopologysetList( text.getText().substring(text.getText().lastIndexOf("(")+1, text.getText().lastIndexOf(")")) ));
//        		ob1.addAll(reporter.getBuildTagsForTestCycle(text.getText().substring(text.getText().lastIndexOf("(")+1, text.getText().lastIndexOf(")"))));
        		if(ob.size()>0)
        		{
        		ZugguiController.controller.getTopoSet().setDisable(false);
        		ZugguiController.controller.getTopoSet().setText(ob.get(0));
        		ob1.addAll(reporter.getBuildTagsForTestCycleAndTopologyset(StringUtils.substringBetween(ob.get(0), " (", ")"),StringUtils.substringBetween(text.getText(), " (", ")")));
	        		if(ob1.size()>0)
	        		{
	        		ZugguiController.controller.getBuildTag().setDisable(false);
	        		ZugguiController.controller.getBuildTag().setText(ob1.get(0));
		        	ZugguiController.controller.getCreateBuildTagBtn().setDisable(false);
	        		}
        		}
        		else if(ob.size()<=0)
        		{
        			ZugguiController.controller.getTopoSet().setText("");
        			ZugguiController.controller.getBuildTag().setText("");
        		}
	        	ZugguiController.controller.getCreateTestCycleBtn().setDisable(false);
        	}
        	else
        	{
        		if(type.equals("TopologyStets"))
    			{
    				ob.addAll(reporter.getBuildTagsForTestCycleAndTopologyset(StringUtils.substringBetween(text.getText(), " (", ")"),StringUtils.substringBetween(ZugguiController.controller.getTestCycle().getText(), " (", ")")));
    				ZugguiController.controller.getBuildTag().setText(ob.get(0));
    			}
        		ZugguiController.controller.getCreateTestCycleBtn().setDisable(false);
	        	ZugguiController.controller.getCreateBuildTagBtn().setDisable(false);
        	}
        	}catch(Exception e){}
        	try{
        	text.getParent().getChildrenUnmodifiable().get(text.getParent().getChildrenUnmodifiable().lastIndexOf(text)+2).setDisable(false);
        	}catch(Exception ex){}
        	stage.close();
        	ZugguiController.controller.isPopupOpened=false;
        });
        
        next.setOnAction(event->{
        	
        	if(ZugguiController.controller.listElementFinalIndex%10!=0)
        		ZugguiController.controller.listElementFinalIndex=ZugguiController.controller.listElementFinalIndex+(10-(ZugguiController.controller.listElementFinalIndex%10));
        	if(ZugguiController.controller.listElementFinalIndex>=items.size())
        		return;
        	listOfVisibles.clear();
        	 for (int i = ZugguiController.controller.listElementFinalIndex; i < items.size(); i++) {
        		if(i==items.size()-1)
        			ZugguiController.controller.listElementFinalIndex=items.size();
     			if(i%10==0 && i!=ZugguiController.controller.listElementFinalIndex)
     			{
     				ZugguiController.controller.listElementFinalIndex=i;
     				break;
     			}
     			else
     				listOfVisibles.add(items.get(i));
     		}
        	 
             listView.getItems().clear();
             listView.getItems().addAll(FXCollections.observableArrayList(listOfVisibles));
             ZugguiController.controller.pageNumber++;
             stage.setTitle("List Of "+type +" [ List "+ZugguiController.controller.pageNumber+" of "+num+" ]");
        });
        
        prev.setOnAction(event->{
        	if(stage.getTitle().contains(" [ List 1 of"))
        		return;
        	listOfVisibles.clear();
        	int x=ZugguiController.controller.listElementFinalIndex-(ZugguiController.controller.listElementFinalIndex%10)-1;
        	if ((ZugguiController.controller.listElementFinalIndex%10)==0) {
				x=x-10;
			}
        	if(x<0)
        		x=ZugguiController.controller.listElementFinalIndex;
        	 for (int i = x; ; i--) {
     			if(i==x-10 && i!=x)
     			{     				
     				if(i<0)
     					ZugguiController.controller.listElementFinalIndex=10;
     				else
     					ZugguiController.controller.listElementFinalIndex=i+10;
     				break;
     			}
     			else
     				try{
     				listOfVisibles.add(items.get(i));
     				}catch(Exception e){}
     		}
        	 if(ZugguiController.controller.listElementFinalIndex<0)
        		 ZugguiController.controller.listElementFinalIndex=10;
        		 
             listView.getItems().clear();
             Collections.reverse(listOfVisibles);
             listView.getItems().addAll(FXCollections.observableArrayList(listOfVisibles));
             ZugguiController.controller.pageNumber--;
             stage.setTitle("List Of "+type +" [ List "+ZugguiController.controller.pageNumber+" of "+num+" ]");
        });
        
        cancel.setOnAction(event->{
        	try{
    			if(!text.getText().equals(""))
            	text.getParent().getChildrenUnmodifiable().get(text.getParent().getChildrenUnmodifiable().lastIndexOf(text)+2).setDisable(false);
            	}catch(Exception ex){}
        	closeAction(type, text);
        	stage.close();
        });
        stage.setOnCloseRequest(event->{
        	try{
    			if(!text.getText().equals(""))
            	text.getParent().getChildrenUnmodifiable().get(text.getParent().getChildrenUnmodifiable().lastIndexOf(text)+2).setDisable(false);
            	}catch(Exception ex){}
        	closeAction(type, text);
        });
        
        stage.setScene(new Scene(contentPane, 400, 300));
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
        ZugguiController.controller.isPopupOpened=true;
	}

 private String getTestInParenthesis(TextField tf) {
	try{
	return tf.getText().substring(tf.getText().lastIndexOf(" (")+2, tf.getText().lastIndexOf(")"));
	}catch(Exception e)
	{
		return "";
	}
}

private void closeAction(String type,TextField text){
	 if(type.equals("Testplans"))
		{
		if(!text.getText().equals(""))
		ZugguiController.controller.getCreateTestCycleBtn().setDisable(false);
		
		}
   else if(type.equals("TestCycles"))
		{
		
 		ZugguiController.controller.getCreateTestCycleBtn().setDisable(false);
 		if(!ZugguiController.controller.getBuildTag().getText().equals(""))
 			ZugguiController.controller.getCreateBuildTagBtn().setDisable(false);
     	if(!text.getText().equals(""))
     		ZugguiController.controller.getTopoSet().setDisable(false);
     	if(!ZugguiController.controller.getBuildTag().getText().equals(""))
     		ZugguiController.controller.getBuildTag().setDisable(false);
     	
 	}
	else if(type.equals("TopologyStets")||type.equals("Builds")){
		
		ZugguiController.controller.getCreateTestCycleBtn().setDisable(false);
 	ZugguiController.controller.getCreateBuildTagBtn().setDisable(false);
 	
 	if(!text.getText().equals(""))
 		ZugguiController.controller.getBuildTag().setDisable(false);
 	
	}
	ZugguiController.controller.isPopupOpened=false;
	text.setDisable(false);	
 }
}
