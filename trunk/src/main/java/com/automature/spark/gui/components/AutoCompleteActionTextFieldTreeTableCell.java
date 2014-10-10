package com.automature.spark.gui.components;

import com.automature.spark.beans.ExistenceMessageBean;
import com.automature.spark.gui.Constants;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class AutoCompleteActionTextFieldTreeTableCell extends

AutoCompleteTextFieldTreeTableCell{

	private ActionHelper actionHelper;

	public AutoCompleteActionTextFieldTreeTableCell(StringConverter<String> arg0,
			AutoCompleteFilter filter, ActionHelper actionHelper) {
		super(arg0,filter);
		this.actionHelper = actionHelper;

	}

	

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (!isEmpty() && item != null) {
			if (item.startsWith("&")) {
				setTextFill(Color.web(Constants.moleculeCallColor));
				EventHandler<MouseEvent> mouseHoverEvent = new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						// TODO Auto-generated method stub
						if (event.isControlDown() && !isUnderline()) {
							setTextFill(Color
									.web(Constants.startPageLabelTextMouseOverColor));
							setScaleX(1.05);
							setScaleX(1.05);
							// setUnderline(true);
						}
					}
				};
				setOnMouseEntered(mouseHoverEvent);
				setOnMouseMoved(mouseHoverEvent);
				setOnMouseExited(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						// TODO Auto-generated method stub

						// setTextFill(Color
						// .web(Constants.moleculeCallColor));
						setScaleX(1);
						setScaleX(1);
						// setUnderline(false);
						updateItem(getItem(), isEmpty());
					}
				});
				setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						// TODO Auto-generated method stub
						if (event.isControlDown() && !isUnderline()) {
							// System.out.println(.getClass().getName());
							showMolecule(getItem());
						}
					}
				});

				// setStyle("-fx-text-fill : #679900;");
			} else {
				setTextFill(Color.web(Constants.atomColor));

				// setStyle("-fx-text-fill : #00B258;");
			}
			setFont(new Font("Arial", 12));
			setText(item);
			ExistenceMessageBean emb = actionHelper.getActionMessageBean(item);// getActionMessageBean(item);
			// if(getTooltip()==null){
			if (emb != null) {
				Tooltip tooltip = getTooltip() != null ? getTooltip()
						: new Tooltip();
				tooltip.setText(emb.getMessage());
				// tooltip.setFont(Font.font(toolTipFontSize));
				// Tooltip tooltip=new Tooltip(emb.getMessage());
				// tooltip.setFont(new Font("Arial", 12));
				setTooltip(tooltip);
				if (emb.isExists()) {
					setUnderline(false);
				} else {
					setTextFill(Color.web(Constants.missingDefColor));
					setUnderline(true);
				}
			}
			// }
		}
	}

	public void showMolecule(final String molecule) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SheetTabPane.getController().showMoleculeInSheetView(
						molecule.substring(1));
			}
		});
	}

}
