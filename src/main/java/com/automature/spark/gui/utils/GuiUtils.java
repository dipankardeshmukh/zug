package com.automature.spark.gui.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class GuiUtils {
	
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
}
