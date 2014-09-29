package com.automature.spark.gui.components;

import java.util.Set;

import org.apache.commons.lang.StringUtils;








import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Callback;

public class AutoCompleteTextField extends TextField {

	private final static int TITLE_HEIGHT = 28;
	private AutoCompleteFilter filter;
	private Popup popup;
	private ChangeListener<String> listChangeListener;


	public void setFilter(AutoCompleteFilter filter) {
		this.filter = filter;
	}

	public AutoCompleteTextField(){
		initialize();
	}

	public AutoCompleteTextField(String arg0) {
		super(arg0);
		initialize();
		// TODO Auto-generated constructor stub
	}

	private void initialize() {
		// TODO Auto-generated method stub
		popup = new Popup();
		popup.setAutoHide(true);

		listChangeListener=new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, 
					String old_val, String new_val) {
				if(StringUtils.isNotBlank(new_val)){
					AutoCompleteTextField.this.setText(new_val);
					hidePopup();
				}
			}
		};


		this.positionCaret(getText().length());
	}

	public  ListView<String> getAutoCompleteComponent(){
		ListView<String> listview=new ListView<>();

		listview.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> p) {
				//A simple ListCell containing only Label
				final ListCell<String> cell = new ListCell<String>() {
					@Override
					public void updateItem(String item,
							boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.toString());
							Tooltip tp=getTooltip()!=null?getTooltip():new Tooltip();
							tp.setText(item);
							setTooltip(tp);
						}
					}
				};     
				return cell;
			}
		});
		EventHandler<KeyEvent> event=new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getCode() == KeyCode.DOWN) {
					//System.out.println("Focused Traversal");
					if (popup.isShowing()&&!listview.isFocused()) {
						listview.requestFocus();
						listview.getSelectionModel().select(0);
					}
				}
			}


		};
		this.addEventFilter(KeyEvent.KEY_RELEASED, event);

		EventHandler listEvent=new EventHandler() {

			@Override
			public void handle(Event evt) {
				// TODO Auto-generated method stub
				if (evt.getEventType() == KeyEvent.KEY_RELEASED) {
				//	System.out.println("inside class event");
					KeyEvent t = (KeyEvent)evt;

					if (t.getCode() == KeyCode.ENTER) {
						if(popup.isShowing()){
							String str=listview.getSelectionModel().getSelectedItem();
							if(str!=null){
								setText(str);
							}
							refersh();
							listview.getItems().clear();
							hidePopup();
						//	evt.consume();
						}

					} else if (t.getCode() == KeyCode.UP) {
						if (listview.getSelectionModel().getSelectedIndex() == 0) {

						}
					}

				}else if (evt.getEventType() == MouseEvent.MOUSE_RELEASED) {

					setText(listview.getSelectionModel().getSelectedItem().toString());
					refersh();
					listview.getItems().clear();
					hidePopup();
				}
			}
		};
		listview.setOnMouseReleased(listEvent);
		listview.setOnKeyReleased(listEvent);
		listview.getSelectionModel().clearSelection();
		listview.getFocusModel().focus(-1);
		return listview;
	}

	public void refersh(){
		requestFocus();
		requestLayout();
		end();
	}


	public void showAutoCompleteText(){

		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ListView<String> listview=getAutoCompleteComponent();
				Set<String> filteredResult=filter.getFilteredResult(getText()!=null?getText():"");
				if(filteredResult!=null){
					listview.getItems().addAll(filteredResult);

					if (listview.getItems().size() > 6) {
						listview.setPrefHeight((6 * 24));
					} else {
						listview.setPrefHeight((listview.getItems().size() * 30));
					}
					showPopup(listview);
				}else{
					hidePopup();
				}
			}

		});
	}

	public void showPopup(Node node) {


		double x=getWindow().getX() + localToScene(0, 0).getX() + getScene().getX();
		double y=getWindow().getY() + localToScene(0, 0).getY() + getScene().getY() + TITLE_HEIGHT;		
		popup.getContent().clear();
		popup.getContent().add(node);
		popup.show(getWindow(),x,y);

	}

	/**
	 * This function hides the popup containing listview
	 */
	public void hidePopup() {

		popup.hide();

	}

	public Window getWindow() {
		return getScene().getWindow();
		//return textField.getScene().getWindow();
	}

}
