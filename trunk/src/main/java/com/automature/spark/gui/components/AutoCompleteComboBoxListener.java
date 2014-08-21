package com.automature.spark.gui.components;

import com.automature.spark.engine.ContextVar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

	private ComboBox comboBox;
	private StringBuilder sb;
	private ObservableList<T> data;
	private boolean moveCaretToPos = false;
	private int caretPos;
	private RadioMenuItem showCVMI;
	private RadioMenuItem showLVMI;
	private RadioMenuItem allMI;

	public AutoCompleteComboBoxListener(final ComboBox comboBox, RadioMenuItem showCVMI, RadioMenuItem showLVMI, RadioMenuItem allMI) {
		this.comboBox = comboBox;
		sb = new StringBuilder();
		data = comboBox.getItems();

		this.comboBox.setEditable(true);
		this.comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				comboBox.hide();
			}
		});
		this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
		this.allMI=allMI;
		this.showCVMI=showCVMI;
		this.showLVMI=showLVMI;
	
		//  this.comboBox.setStyle(".combo-box .arrow, .combo-box .arrow-button{ -fx-background-color: transparent;}");

	}

	public void setComboBoxData(){

	}

	@Override
	public void handle(KeyEvent event) {

		if(event.getCode() == KeyCode.UP) {
			caretPos = -1;
		//	System.out.println("UP");
			moveCaret(comboBox.getEditor().getText().length());
			return;
		} else if(event.getCode() == KeyCode.DOWN) {
		//	System.out.println("down");
			if(!comboBox.isShowing()) {
				comboBox.show();
			}
			caretPos = -1;
			moveCaret(comboBox.getEditor().getText().length());
			return;
		} else if(event.getCode() == KeyCode.BACK_SPACE) {
		//	System.out.println("bk space ");
			moveCaretToPos = true;
			caretPos = comboBox.getEditor().getCaretPosition();
		} else if(event.getCode() == KeyCode.DELETE) {
		//	System.out.println("delete");
			moveCaretToPos = true;
			caretPos = comboBox.getEditor().getCaretPosition();
		}

		if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
				|| event.isControlDown() || event.getCode() == KeyCode.HOME
				|| event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
			return;
		}

		ObservableList list = FXCollections.observableArrayList();
		try {
			if(allMI.isSelected()){
				data=FXCollections.observableArrayList(ContextVar.getAllContextVar());
			}else if(showCVMI.isSelected()){
				data=FXCollections.observableArrayList(ContextVar.getContextVariables());
			}else{
				data=FXCollections.observableArrayList(ContextVar.getLocalVariables());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i=0; i<data.size(); i++) {
			if(data.get(i).toString().toLowerCase().startsWith(
					AutoCompleteComboBoxListener.this.comboBox
					.getEditor().getText().toLowerCase())) {
				list.add(data.get(i));
			}else{
				if(allMI.isSelected()||showLVMI.isSelected()){
					String var=data.get(i).toString().toLowerCase();
					if(var.contains(".")){
						String []tmp=var.split("\\.");
						if(tmp.length>1){
							if(tmp[1].startsWith(AutoCompleteComboBoxListener.this.comboBox
									.getEditor().getText().toLowerCase())){
								list.add(data.get(i));
							}
						}
					}
				}
			}
		}
		String t = comboBox.getEditor().getText();
		comboBox.setItems(list);
		comboBox.getEditor().setText(t);
		if(!moveCaretToPos) {
			caretPos = -1;
		}
		moveCaret(t.length());
		if(!list.isEmpty()) {
			comboBox.show();
		}
	}

	private void moveCaret(int textLength) {
		if(caretPos == -1) {
			comboBox.getEditor().positionCaret(textLength);
		} else {
			comboBox.getEditor().positionCaret(caretPos);
		}
		moveCaretToPos = false;
	}
}
