package com.automature.spark.gui.utils;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.controlsfx.dialog.Dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GuiUtils {
	
	private static Stage stage;
	
	
	
	public static void setStage(Stage stage) {
		GuiUtils.stage = stage;
	}

	public static void addOnlyNumberPropertyInTextField(final TextField field){
		field.lengthProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {              

				if(newValue.intValue() > oldValue.intValue()){
					char ch = field.getText().charAt(oldValue.intValue());
					//Check if the new character is the number or other's
					if(!(ch >= '0' && ch <= '9' )){       		                 
						//if it's not number then just setText to previous one
						field.setText(field.getText().substring(0,field.getText().length()-1)); 
					}
				}
			}

		});
	}
	
	public static String getNameSpace(String file){
		
		if(StringUtils.isBlank(file)){
			return "";
		}else{
			File f=new File(file);
			String fileName=f.getName();
			return fileName.substring(0, fileName.indexOf('.'));
		}
	}
	
	public static void showMessage(String message){
		Dialogs.create()
        .owner(stage)
        .title("Information Dialog")
        .masthead(null)
        .message(message)
        .showInformation();
	}
	
	public static void showMessage(String message,Exception e){
		Dialogs.create()
        .owner(stage)
        .title("Exception Dialog")
        .masthead(null)
        .message("Ooops, there was an exception!")
        .showException(e);
	}
	
	public static void showMessage(String messageTitle,String message,Exception e){
		Dialogs.create()
        .owner(stage)
        .title("Exception Dialog")
        .masthead(messageTitle)
        .message("Ooops, there was an exception!")
        .showException(e);
	}
	
}
