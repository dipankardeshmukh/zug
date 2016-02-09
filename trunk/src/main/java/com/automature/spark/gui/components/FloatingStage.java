package com.automature.spark.gui.components;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//Need to add more functionality so re arrange title panes by simply drag and drop
public class FloatingStage {

	private static final String TAB_DRAG_KEY = "titledpane";
	private Parent floaterPane;
	private Pane originalParent;
	private VBox pane;
	private Stage stage;
	private double eventX,eventY;
	int position;



	public FloatingStage(Parent floaterPane) {
		super();
		this.floaterPane = floaterPane;
		this.originalParent=(Pane)floaterPane.getParent();
		pane=new VBox();
		Scene scene = new Scene(pane);
		stage = new Stage();
		stage.setScene(scene);
		stage.hide();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				stage.hide();
				if(pane.getChildren().contains(floaterPane)){
					moveToParent();
				}
			}
		});
		stage.initStyle(StageStyle.UTILITY);
		stage.setMinHeight(340);
		stage.setMinWidth(300);
		if(floaterPane instanceof TitledPane){
			stage.setTitle("SPARK - "+((TitledPane) floaterPane).getText());
		}
		
		if(((TitledPane) floaterPane).getText().contains("Execution Summary"))
		stage.setMaxHeight(420);
		else
		stage.setMaxHeight(350);
		
		stage.setMaximized(false);
		//((TitledPane)floaterPane).prefHeightProperty().bindBidirectional(pane.prefHeightProperty());
		//stage.setMaxHeight(((TitledPane)floaterPane).getHeight());
		addEvents();
	}

	public void moveToScene(){
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				originalParent.getChildren().remove(floaterPane);

				pane.getChildren().clear();
				if(floaterPane instanceof TitledPane && ((TitledPane) floaterPane).getText().contains("Execution Summary")){
					((TitledPane) floaterPane).setMinHeight(400.0);
				}
				pane.getChildren().add(floaterPane);
				if(floaterPane instanceof TitledPane){
					((TitledPane) floaterPane).setExpanded(true);
				}
				stage.setX(eventX);
				stage.setY(eventY);
				stage.show();
			}
		});
	}

	public void moveToParent(){
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				pane.getChildren().clear();
				originalParent.getChildren().add(floaterPane);
				if(floaterPane instanceof TitledPane){
					((TitledPane) floaterPane).setExpanded(false);
				}		
				stage.hide();

			}
		});
	}

	/*public void transferFromParent(){

		if(pane.contains(eventY, eventY)){
			moveToParent();
		}else if(originalParent.contains(eventY, eventY)){
			moveToScene();
		}
	}
	 */

	public boolean checkWithInParentBounds(double layoutX,double layoutY){
		if ((layoutX >= 0) && (layoutX >= (floaterPane.getParent().getLayoutBounds().getWidth()))) {
			//floaterPane.setLayoutX(layoutX);
			return false;
		}
		if ((layoutY >= 0) && (layoutY >= (floaterPane.getParent().getLayoutBounds().getHeight()))) {
			//floaterPane.setLayoutY(layoutY);
			return false;
		}
		return true;

	}

	public void addEvents(){
		floaterPane.setOnMouseDragged(new EventHandler<MouseEvent>() {                
			@Override public void handle(MouseEvent event) {
				floaterPane.toFront();
				//		System.out.println("event "+event.getSceneX()+" "+event.getSceneY()+" "+event.getScreenX()+" "+event.getScreenY()+" "+event.getX()+" "+event.getY());

				eventX = event.getScreenX() ;
				eventY = event.getScreenY() ;
				if(originalParent.getChildren().contains(floaterPane)&&!checkWithInParentBounds(eventX, eventY)){
					moveToScene();
				}/*else if(pane.getChildren().contains(floaterPane)&&originalParent.contains(eventY, eventY)){
					moveToParent();
				}*/
				//floaterPane.setLayoutX(layoutX);
				//floaterPane.setLayoutY(layoutY);
				event.consume();
			}
		});
		floaterPane.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				/* drag was detected, start a drag-and-drop gesture*/
				/* allow any transfer mode */
				Dragboard db = floaterPane.startDragAndDrop(TransferMode.MOVE);
			//	ClipboardContent clipboardContent = new ClipboardContent();
			//	clipboardContent.putString(TAB_DRAG_KEY);
				event.consume();
			}
		});
		/*	floaterPane.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(final DragEvent event) {
				Dragboard db = event.getDragboard();
				boolean success = false;
				System.out.println("tp dropping drag");
				event.setDropCompleted(success);
				event.consume();
			}
		});*/
		/*	originalParent.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                final Dragboard dragboard = event.getDragboard();
                if (dragboard.hasString()
                        && TAB_DRAG_KEY.equals(dragboard.getString())
                       ) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }
        });
		originalParent.setOnDragDropped(new EventHandler<DragEvent>(){

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                	System.out.println("transferring");
                	transferFromParent();
                }
			}

		});*/
		floaterPane.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* the drag and drop gesture ended */
				/* if the data was successfully moved, clear it */
				/*	System.out.println("floaterPane drag done");
		        if (event.getTransferMode() == TransferMode.MOVE) {
		          //  source.setText("");
                	System.out.println("transferring");
		        	transferFromParent();
		        }*/
				event.consume();
			}
		});
	}

}
