package com.automature.spark.gui.components;

import com.automature.spark.gui.Constants;

import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class ArgumentTextFieldTreeTableCell extends TextFieldTreeTableCell<ObservableList<String>, String>{
	
	private ArgumentHelper helper;

	public ArgumentTextFieldTreeTableCell(StringConverter<String> arg0,ArgumentHelper helper) {
		super(arg0);
		this.helper = helper;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (!isEmpty()&&item!=null) {
			if (item.startsWith("$")||item.contains("$")) {
				setTextFill(Color.web(Constants.macroColor));
			} else {
				setTextFill(Color.web(Constants.argumentColor));
			}
			setFont(new Font("Arial", 12));
			setText(item);
			String tooltipText=helper.getToolTipForArgs(item);
			Tooltip tooltip=getTooltip()!=null?getTooltip():new Tooltip();
			tooltip.setText(tooltipText);
			//tooltip.setFont(Font.font(toolTipFontSize));
			//Tooltip tooltip=new Tooltip(getToolTipForArgs(item));
			//tooltip.setFont(new Font("Arial", 12));
			setTooltip(tooltip);
		}

	}
	
}
