package com.automature.spark.gui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class AutoCompleteTextFieldTreeTableCell extends

TextFieldTreeTableCell<ObservableList<String>, String>{
	
	private TextField textField;
	private AutoCompleteFilter filter;
	
	public AutoCompleteTextFieldTreeTableCell(StringConverter<String> arg0,
			AutoCompleteFilter filter) {
		super(arg0);
		this.filter = filter;
		

	}
	
	@Override
	public void cancelEdit() {
		// TODO Auto-generated method stub
		super.cancelEdit();
		setText((String) getItem());
		setGraphic(null);
	}

	@Override
	public void startEdit() {
		// TODO Auto-generated method stub
		super.startEdit();

		if (textField == null) {
			createTextField();
		}
		setText(null);
		setGraphic(textField);
		textField.selectAll();
	}

	private void createTextField() {

		textField = new AutoCompleteTextField(getString());
		((AutoCompleteTextField) textField).setFilter(filter);
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				if (!arg2) {
					commitEdit(textField.getText());
				}
			}
		});
		textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				//System.out.println("key event generated " + t.getCode());
				if (t.getCode() == KeyCode.ENTER) {
					String value = textField.getText();
				//	System.out.println(getTreeTableRow());
					if (value != null) {
						commitEdit(value);
					} else {
						commitEdit(null);
					}
					((AutoCompleteTextField) textField).hidePopup();
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
					((AutoCompleteTextField) textField).hidePopup();
				} else if (t.getCode() == KeyCode.DOWN
						|| t.getCode() == KeyCode.UP) {
					// this event handled within the AutoCompleteTextField
				} else {
					((AutoCompleteTextField) textField).showAutoCompleteText();
					// autocomplete code goes here
				}
			}

		});
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}

}
