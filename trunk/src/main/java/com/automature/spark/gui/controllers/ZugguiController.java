/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.controllers;

import com.automature.spark.beans.ActionInfoBean;
import com.automature.spark.beans.MacroColumn;
import com.automature.spark.engine.Spark;
import com.automature.spark.engine.TestCase;
import com.automature.spark.gui.Constants;
import com.automature.spark.gui.Expression;
import com.automature.spark.gui.ExpressionEvaluator;
import com.automature.spark.gui.RuntimeOptionBuilder;
import com.automature.spark.gui.SessionHandler;
import com.automature.spark.gui.ZugGui;
import com.automature.spark.gui.components.FloatingStage;
import com.automature.spark.gui.components.LinkedFilesBorderPane;
import com.automature.spark.gui.components.MoleculeTreeTableSheetTab;
import com.automature.spark.gui.components.SheetTabPane;
import com.automature.spark.gui.components.TestCaseTreeTableSheetTab;
import com.automature.spark.gui.components.TreeTableSheetTab;
import com.automature.spark.gui.sheets.SpreadSheet;
import com.automature.spark.gui.utils.ApplicationLauncher;
import com.automature.spark.gui.utils.ScreenLoader;
import com.automature.spark.gui.utils.TestSuiteChooser;
import com.automature.spark.util.Log;
import com.automature.spark.util.Utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author skhan
 */
public class ZugguiController implements Initializable ,GuiController{

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private ImageView collpaseButtonImgView;
	@FXML
	private Label label;
	@FXML
	private ToolBar toolBar;
	//tool bar button
	@FXML
	private Button collapsebutton;
	@FXML
	private Button openTSButton;
	@FXML
	private Button reloadTSButton;
	@FXML
	private Button copyButton;
	@FXML
	private Button pasteButton;
	@FXML
	private Button settingButton;

	//panes
	@FXML
	private ScrollPane rightPane;

	//@FXML
	//	private AnchorPane rightPane;
	@FXML
	private SplitPane splitPane;
	@FXML
	private LinkedFilesBorderPane linkedFiles;
	@FXML
	private SheetTabPane sheetTabPane;
	@FXML
	private Pane leftPaneVBox;
	@FXML
	private HBox statusHBox;
	@FXML
	private Label statusBarLabel;
	@FXML
	private Pane environmentPane;

	//Menu options
	@FXML
	private MenuBar menuBar;
	//File menu items
	@FXML
	private MenuItem newMI;
	@FXML
	private MenuItem openMI;
	@FXML
	private Menu recentlyUsedMenu;
	@FXML
	private MenuItem closeMI;
	@FXML
	private MenuItem saveMI;
	@FXML
	private MenuItem saveAsMI;
	@FXML
	private MenuItem revertMI;
	@FXML
	private MenuItem exitMI;
	@FXML
	private Menu preferences;

	//Run Debug Menu
	@FXML
	private Menu runMI;
	@FXML
	private MenuItem runTestMI;
	@FXML
	private MenuItem nextStepMI;
	@FXML
	private MenuItem resumeMI;
	@FXML
	private MenuItem stopMI;
	@FXML
	private CheckMenuItem exprEvaluatorMI;
	@FXML
	private MenuItem removeBPMI;

	//Edit menu
	@FXML
	private Menu editMenu;

	@FXML
	private MenuItem undoMI;
	@FXML
	private MenuItem redoMI;
	@FXML
	private MenuItem cutMI;
	@FXML
	private MenuItem copyMI;
	@FXML
	private MenuItem pasteMI;
	@FXML
	private MenuItem deleteMI;
	@FXML
	private MenuItem selectAllMI;
	@FXML
	private MenuItem deSelectMI;
	@FXML
	private MenuItem findMI;

	//View Menu
	@FXML
	private Menu viewMenu;
	@FXML
	private CheckMenuItem viewConsoleCMI;

	//Help Menu
	@FXML
	private Menu helpMenu;

	//Side bar components
	//TestControl components
	@FXML
	private VBox titlesVBox;
	@FXML
	private VBox testControlVBox;
	@FXML
	private CheckBox verboseCB;
	@FXML
	private CheckBox noAutoRecoveryCB;
	@FXML
	private CheckBox noVerifyCB;
	@FXML
	private CheckBox defaultMacroColumnCB;
	@FXML
	private CheckBox ignoreCB;
	@FXML
	private CheckBox atomExecTimeCB;
	@FXML
	private Button runButton;
	@FXML
	private Button resumeButton;
	@FXML
	private Button nextStepButton;
	@FXML
	private Button abortTestButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Pane repeatOptionPane;
	@FXML
	private CheckBox repeatCheckBox;
	@FXML
	private RadioButton repeatCountRadioButton;
	@FXML
	private RadioButton repeatDurationRadioButton;
	@FXML
	private ChoiceBox<String> repeatChoiceBox;
	@FXML
	private TextField repeatCountTextBox;
	@FXML
	private TextField repeatDurationTextBox;
	@FXML
	private ToggleGroup group;
	//Environment Title pane components
	@FXML
	private CheckBox useDefaultCB;
	@FXML
	private TextField macroTF;
	@FXML
	private ComboBox<MacroColumn> testSuiteCB;
	@FXML
	private ChoiceBox<String> environmentCB;


	@FXML
	private VBox variableVBox;
	@FXML
	private AnchorPane variableAPane;
	@FXML
	private TitledPane testControlTPane;
	private SessionHandler sessionHandler;
	private TestSuiteChooser tsChooser;
	public static SpreadSheet spreadSheet;
	private Stage consoleStage;
	private ConsoleController console;
	private StartuppageController spcController;
	private Pane startUpPane;

	private LicenseController licenseController;

	private List<String> args;
	private RuntimeOptionBuilder optionBuilder;
	private EventHandler<ActionEvent> fileClickEvent;
	private Task task;
	private Stack<TestCase> currentStack; 
	private SimpleBooleanProperty expressionEvaluatorMode=new SimpleBooleanProperty(false);
	private ExpressionEvaluator expressionEvaluator;
	private ExpressionEvaluatorController expressionEvaluatorController;

	private boolean sheetLoaded=false;
	private Stage expressionStage;
	private boolean expressionRefresh;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
		try{
			sessionHandler = new SessionHandler();
			sessionHandler.retriveSession();
			tsChooser = new TestSuiteChooser();
			sheetTabPane = new SheetTabPane();
			SheetTabPane.setController(this);
			optionBuilder=new RuntimeOptionBuilder();
			repeatCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> ov,
						Boolean old_val, Boolean new_val) {
					if(new_val){
						repeatOptionPane.setDisable(false);
					}else{
						repeatOptionPane.setDisable(true);
					}
				}
			});
			fileClickEvent=new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					try {
						MenuItem mi=(MenuItem)e.getSource();
						File f = new File(mi.getText());
						if (f.exists()) {
							loadTestSuite(mi.getText());
							reInitializeLeftPane();
						} else {
							//need to provide a dialoge
							System.err.println(f.getAbsoluteFile() + " does not exists");
						}
					} catch (Exception ex) {
						System.err.println("Error "+ex.getMessage());
						Logger.getLogger(ZugguiController.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			};


			initializeStartupBehavior();
			registerRunTimeEntities();
			currentStack=new Stack<>();
			variableVBox.prefHeightProperty().bind(variableAPane.heightProperty());
			variableVBox.prefWidthProperty().bind(variableAPane.widthProperty());
			initializeLisenceValidator();
			
		}catch(Exception e){
			System.err.println("Error : Initializing GUI.\nError message  "+e.getMessage()+"\nError Trace :"+e.getStackTrace());
			//throw e;
			e.printStackTrace();
		}
	}
	
	public void refreshExpressionEvaluator(){
		if(expressionEvaluatorController!=null){
			if(expressionStage.isShowing()){
				expressionEvaluatorController.intializeTable();
			}
		}
	}

	private void intializeExpressionEvaluator(){
		try{
			if(expressionEvaluatorController==null){
				ScreenLoader loader=new ScreenLoader("/com/automature/spark/gui/resources/ExpressionEvaluator.fxml");
				expressionStage = loader.getStage();
				//expressionStage.setOnCloseRequest(arg0);
				expressionEvaluatorController=(ExpressionEvaluatorController)loader.getController();
				expressionEvaluatorController.setRootController(ZugguiController.this);
				expressionEvaluatorController.setStage(expressionStage);
				ExpressionEvaluator evaluator=new ExpressionEvaluator();
				expressionEvaluatorMode.bindBidirectional(evaluator.getExpressionEvaluatorMode());
				expressionEvaluatorController.setExpressionEvaluator(evaluator);
				expressionStage.initStyle(StageStyle.UTILITY);
				expressionStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					
					@Override
					public void handle(WindowEvent arg0) {
						// TODO Auto-generated method stub
						exprEvaluatorMI.setSelected(false);
						expressionStage.hide();
						
					}
				});
				
				expressionRefresh=false;
			}/*else if(expressionRefresh){
				expressionRefresh=false;
				expressionEvaluatorController.intializeTable();
			}*/else{
				expressionEvaluatorController.intializeTable();
			}
			
		}catch(Exception e){
			System.err.println("Error : Loading expression evaluator "+e.getMessage());
		}

	}
	
	public void showExpressionEvaluator(){
		if(expressionStage!=null&&expressionStage.isShowing()){
			//intializeExpressionEvaluator();
			expressionStage.hide();
		}else{
			intializeExpressionEvaluator();
			expressionStage.show();
		}
	}
	
	public void hideExpressionEvaluator(){
		if(expressionStage!=null){
			exprEvaluatorMI.setSelected(false);
			expressionStage.hide();
		}
	}
	
	
	
	private void registerRunTimeEntities() {
		// TODO Auto-generated method stub
		optionBuilder.registerCheckBox(verboseCB, "-verbose");
		optionBuilder.registerCheckBox(noAutoRecoveryCB, "-noautorecover");
		optionBuilder.registerCheckBox(noVerifyCB, "-noverify");
		optionBuilder.registerCheckBox(ignoreCB, "-ignore");
		optionBuilder.registerCheckBox(atomExecTimeCB, "-atomexectime");
		optionBuilder.registerRepeatOptions(repeatCheckBox,repeatCountRadioButton,repeatDurationRadioButton,repeatCountTextBox,repeatDurationTextBox,repeatChoiceBox);
		optionBuilder.registerEnvironmentOptions(testSuiteCB, environmentCB, useDefaultCB, macroTF);

	}

	public void initializeStartupBehavior() {
		intializeStartUpPage();
		initializeConsole();
		intitializeStartUpComponents();
	}

	public void intializeStartUpPage() {

		try {
			
			FXMLLoader fxmlLoader1 = new FXMLLoader();
			URL url1 = getClass().getResource("/com/automature/spark/gui/resources/startuppage.fxml");
			fxmlLoader1.setLocation(url1);
			startUpPane = (Pane) fxmlLoader1.load(url1.openStream());
			spcController = (StartuppageController) fxmlLoader1.getController();
			spcController.setRootController(this);
			// Scene startScene = new Scene(p1);
			leftPaneVBox.getChildren().add(startUpPane);
			Set<String> files = sessionHandler.getSession().getTestSuiteFiles();
			spcController.addRecentlyUsedFile(files);
			startUpPane.setOnDragOver(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					Dragboard db = event.getDragboard();
					if (db.hasFiles()) {
						event.acceptTransferModes(TransferMode.COPY);
					} else {
						event.consume();
					}
				}
			});

			// Dropping over surface
			startUpPane.setOnDragDropped(new EventHandler<DragEvent>() {
				@Override
				public void handle(DragEvent event) {
					Dragboard db = event.getDragboard();
					boolean success = false;
					if (db.hasFiles()) {
						success = true;
						String filePath = null;
						for (File file : db.getFiles()) {
							filePath = file.getAbsolutePath();
							String extension = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
							if (extension.equals("xls") || extension.equals("xlsx")) {
								List<String> params = new ArrayList<String>();
								params.add(filePath);
								commandsFromStartUpPage(Constants.launchTestSuiteCommand, params);
								break;
							} else {
								continue;
							}
						}
					}
					event.setDropCompleted(success);
					event.consume();
				}
			});
			// spcController.addRecentlyUsedFile(files);
		} catch (IOException ex) {
			Logger.getLogger(ConsoleController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void openLicenseValidator(){
		Platform.runLater(new Runnable() {
			public void run() {
				licenseController.showLicenseValidator();	
			}
		});
	}

	public void initializeLisenceValidator(){


		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {				// TODO Auto-generated method stub
					ScreenLoader loader=new ScreenLoader("/com/automature/spark/gui/resources/LicenseDialogue.fxml");
					licenseController=(LicenseController)loader.getController();
					loader.getStage().initOwner(splitPane.getScene().getWindow());
					licenseController.setStage(loader.getStage());
					licenseController.validate();						
				}catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println("Error launching License validator dialogue. "+e.getMessage());
				}
			}
		});


	} 



	public void initializeConsole() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {				// TODO Auto-generated method stub
					ScreenLoader loader=new ScreenLoader("/com/automature/spark/gui/resources/console.fxml");
					console=(ConsoleController)loader.getController();
					consoleStage=loader.getStage();
					consoleStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent event) {
							consoleStage.hide();
							viewConsoleCMI.setSelected(false);
						}
					});
					consoleStage.show();
					consoleStage.setTitle("SPARK Console - ");
					console.redirectSystemStreams();
					consoleStage.getIcons().add(new Image(ZugGui.class.getResourceAsStream("/com/automature/spark/gui/resources/icons/Spark.png")));						
				}catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println("Error launching Console. "+e.getMessage());
				}
			}
		});
		
		/*FXMLLoader fxmlLoader = new FXMLLoader();
	try {
		URL url = getClass().getResource("/com/automature/spark/gui/resources/console.fxml");
		fxmlLoader.setLocation(url);
		Pane p = (Pane) fxmlLoader.load(url.openStream());
		console = (ConsoleController) fxmlLoader.getController();
		Scene scene = new Scene(p);
		consoleStage = new Stage();
		consoleStage.setScene(scene);


	} catch (IOException ex) {
		Logger.getLogger(ConsoleController.class.getName()).log(Level.SEVERE, null, ex);

	}*/
	}

	public void collapseRightPane() {

		if (rightPane.isVisible()) {
			collpaseButtonImgView.setImage(new Image(ZugguiController.class.getResourceAsStream("/com/automature/spark/gui/resources/icons/black/glyph_sidebar-expand.png")));
			if (collapsebutton.getTooltip() != null) {
				collapsebutton.getTooltip().setText("View side bar");
			} else {
				collapsebutton.setTooltip(new Tooltip("View side bar"));
			}

			splitPane.getItems().remove(rightPane);
			rightPane.setVisible(false);
		} else {
			collpaseButtonImgView.setImage(new Image(ZugguiController.class.getResourceAsStream("/com/automature/spark/gui/resources/icons/black/glyph_sidebar-collapse.png")));
			if (collapsebutton.getTooltip() != null) {
				collapsebutton.getTooltip().setText("Hide side bar");
			} else {
				collapsebutton.setTooltip(new Tooltip("Hide side bar"));
			}
			splitPane.getItems().add(1, rightPane);
			splitPane.setDividerPosition(0, 0.7181628392484343);
			rightPane.setVisible(true);
		}
	}

	public void loadTestSuite(String fileName) throws Exception {
		if (spreadSheet != null) {
			spreadSheet.releaseResources();
		}
		spreadSheet = new SpreadSheet();
		spreadSheet.readSpreadSheet(fileName);
		SpreadSheet.putUniqueSheet(fileName, spreadSheet);
		sheetTabPane.loadPanes(spreadSheet);
		linkedFiles.loadLinkedFiles(spreadSheet, this);
		optionBuilder.registerSpreadSheet(spreadSheet);
		removeAllBreakPointsFromZug();
		sessionHandler.addTestsuite(fileName);
		sessionHandler.saveSession();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				loadRecentlyUsedFilesInMenu();
				
			}
		});
		refreshExpressionEvaluator();
		//	MenuItem mi=new MenuItem(fileName);
		//	mi.setOnAction(fileClickEvent);
		//	recentlyUsedMenu.getItems().add(0, mi);
		setDisableNoTestSuiteLoadComonents(false);
	}

	public boolean showTestSuiteChooser() {

		File file = tsChooser.chooseTestSuite(null);//.getFileName();

		if (file != null) {
			try {
				loadTestSuite(file.getAbsolutePath());
				reInitializeLeftPane();
			} catch (Exception e) {
				System.err.println("Error reading testSuite " + e.getMessage());
			}
			return true;

		} else {
			return false;
		}

	}

	public void showSpreadSheet(final String fileName) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if (spreadSheet.getAbsolutePath().equalsIgnoreCase(fileName)) {
					sheetTabPane.loadPanes(spreadSheet);
					sheetTabPane.showTestCaseTab();
					refreshExpressionEvaluator();
				} else {
					SpreadSheet sp=spreadSheet.getIncludeFile(fileName);
					if(sp!=null){
						sheetTabPane.loadPanes(spreadSheet.getIncludeFile(fileName));
						sheetTabPane.showMoleculeTab();
						refreshExpressionEvaluator();
					}
				}
			}
		});      
	}

	public void reloadTestSuite() {
		try {
			/*File f = tsChooser.getTestSuite();
			if (f != null) {
				loadTestSuite(f.getAbsolutePath());

			}
			 */
			if(spreadSheet!=null){
				loadTestSuite(spreadSheet.getAbsolutePath());
			}

		} catch (Exception e) {
			System.err.println("Error reading testSuite " + e.getMessage());
		}
	}

	private void intitializeStartUpComponents() {
		loadRecentlyUsedFilesInMenu();
		setDisableNoTestSuiteLoadComonents(true);
		ObservableList titlePanes=titlesVBox.getChildren();
		titlePanes.forEach( p -> new FloatingStage((Parent)p) );


	}

	private void setDisableNoTestSuiteLoadComonents(final boolean b){
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				reloadTSButton.setDisable(b);
				copyButton.setDisable(b);
				pasteButton.setDisable(b);
				saveMI.setDisable(b);
				saveAsMI.setDisable(b);
				revertMI.setDisable(b);
				closeMI.setDisable(b);
				editMenu.setDisable(b);
				undoMI.setDisable(b);
				redoMI.setDisable(b);
				cutMI.setDisable(b);
				copyMI.setDisable(b);
				pasteMI.setDisable(b);
				deleteMI.setDisable(b);
				selectAllMI.setDisable(b);
				deSelectMI.setDisable(b);
				findMI.setDisable(b);
				runButton.setDisable(b);
				runTestMI.setDisable(b);
				testControlVBox.setDisable(b);
				environmentPane.setDisable(b);
				
				removeBPMI.setDisable(b);
			}
		});
	}

	private void loadRecentlyUsedFilesInMenu() {
		recentlyUsedMenu.getItems().clear();
		Set<String> files = sessionHandler.getSession().getTestSuiteFiles();
		Object []fileList=files.toArray();
		//for (String file : files) {
		for(int i=fileList.length-1,j=0;i>=0&&j<10;i--,j++){
			String file=(String)fileList[i];
			final MenuItem menuItem = new MenuItem(file);
			if(recentlyUsedMenu.getItems().contains(menuItem)){
				continue;
			}
			recentlyUsedMenu.getItems().add(menuItem);
			menuItem.setOnAction(fileClickEvent);
			/*menuItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					try {

						File f = new File(menuItem.getText());
						if (f.exists()) {
							loadTestSuite(menuItem.getText());
						} else {
							//need to provide a dialoge
							System.err.println(f.getAbsoluteFile() + " does not exists");
						}
					} catch (Exception ex) {
						System.err.println("Error "+ex.getMessage());
						Logger.getLogger(ZugguiController.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			});*/
		}

	}





	public void showConsole() {
		if (viewConsoleCMI.isSelected()) {
			consoleStage.show();
		} else {
			consoleStage.hide();
		}
	}


	public void commandsFromStartUpPage(String command, List<String> argument) {
		if (command.equals(Constants.launchTestSuiteCommand)) {
			try {
				reInitializeLeftPane();
				loadTestSuite(argument.get(0));
				tsChooser.setLastTestSuite(argument.get(0));
			} catch (Exception ex) {
				Logger.getLogger(ZugguiController.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else if (command.equals(Constants.openTestSuiteChooserCommand)) {
			boolean chosen = showTestSuiteChooser();
			if (chosen) {
				reInitializeLeftPane();
			}
		}

	}

	public void reInitializeLeftPane() {
		//        System.out.println(leftPaneVBox.getChildren().remove(startUpPane));
		// leftPaneVBox.

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				sheetLoaded=true;
				splitPane.getItems().set(0, sheetTabPane);
				//splitPane.setDividerPositions(.35);
			}
		});

		//      leftPaneVBox.getChildren().add(sheetTabPane);
		// sheetTabPane.
	}
	public void exit(){
		System.exit(0);
	}

	public void showRunningTestCase(String testCaseID, boolean b) {
		// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.


		((TreeTableSheetTab)spreadSheet.getTestCasesSheet().getSheetTab()).setAndExpandCurrentTestCase(testCaseID);
	}

	public void showRunningTestStep(int lineNumber) {
		((TestCaseTreeTableSheetTab)spreadSheet.getTestCasesSheet().getSheetTab()).highLightRow(lineNumber);
		// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void updateTestCaseStatus(String testCaseID, boolean b) {
		// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void showRunningMoleculeStep(String molecules, int lineNumber, int start) {
		try{
			((MoleculeTreeTableSheetTab)spreadSheet.getMoleculesSheet().getSheetTab()).highLightRow(lineNumber);

		}catch(Exception e){
			//System.err.println(e.getMessage());
		}
	}
	@Override
	public void showRunningMolecule(String MoleculeID, String nameSpace,
			boolean b) {
		// TODO Auto-generated method stub

		//System.out.println(currentStack);
		if(nameSpace.equals("main")){
			((TreeTableSheetTab)spreadSheet.getMoleculesSheet().getSheetTab()).setAndExpandCurrentTestCase(MoleculeID);
		}else{
			try{
				SpreadSheet sp=spreadSheet.getIncludeFile(nameSpace);
				((TreeTableSheetTab)sp.getMoleculesSheet().getSheetTab()).setAndExpandCurrentTestCase(MoleculeID);

			}catch(Exception e){
				//System.err.println(e.getMessage());
			}
		}
		String appendString="";
		if(!nameSpace.equals("main")){
			appendString=nameSpace+".";
		}
		
	}


	public void clearSelection(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Iterator it = SpreadSheet.getUniqueSheets().keySet().iterator();
				while (it.hasNext()) {
					String file = (String) it.next();
					SpreadSheet	sp = SpreadSheet.getUniqueSheets().get(file);
					if(sp.getTestCasesSheet().isSheetTabCreated()){
						sp.getTestCasesSheet().getSheetTab().clearSelection();
					}
					if(sp.getMoleculesSheet().isSheetTabCreated()){
						sp.getMoleculesSheet().getSheetTab().clearSelection();
					}
				}
			}
		});

	}

	public void launchZug(){

		console.clear();
		consoleStage.setTitle("SPARK Console - "+spreadSheet.getFileName());
		final List<String> params=new ArrayList<String>();
		for (String param:args){
			if(param.startsWith("-pwd")){
				params.add(param);
			}
		}

		params.add(spreadSheet.getAbsolutePath());
		params.addAll(optionBuilder.getRuntimeParams());
		params.add("-debugger");
		clearSelection();
		//sheetTabPane.clearSelections();
		setDisableRunTimeOptions(false);
		//System.out.println(params);
		Task task = new Task<Void>() {
			@Override public Void call() {
				try {
					Spark.runTests(params.toArray(new String[params.size()]));
					System.out.println("Execution Finished.");

				} catch(Throwable t){
					Log.Error(t.getMessage());
				}finally{
					setDisableRunTimeOptions(true);
				}
				return null;
			}
		};
		/*  ProgressBar bar = new ProgressBar();

		   bar.progressProperty().bind(task.progressProperty());
		   ProgressIndicator pin = new ProgressIndicator();
		   pin.progressProperty().bind(task.progressProperty());
		   statusHBox.getChildren().clear();
		   statusHBox.getChildren().add(statusBarLabel);
		   statusHBox.getChildren().add(1,bar);
		   statusHBox.getChildren().add(2,pin);*/
		this.task=task;
		new Thread(task).start();
	}


	private void setDisableRunTimeOptions(final boolean b) {
		// TODO Auto-generated method stub
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				resumeButton.setDisable(b);
				nextStepButton.setDisable(b);
				pauseButton.setDisable(b);
				abortTestButton.setDisable(b);
				nextStepMI.setDisable(b);
				resumeMI.setDisable(b);
				stopMI.setDisable(b);
				//	variableVBox.setDisable(b);
				exprEvaluatorMI.setDisable(b);
				runButton.setDisable(!b);
				runTestMI.setDisable(!b);
				if(b==true){
					hideExpressionEvaluator();
				}


			}
		});
	}

	public void resumeTestSuiteExecution(){
		//System.out.println("resume signal sent");
		Spark.setResumeSignal();    	
	}

	public void runNextTestStep(){
		//System.out.println("step over signal sent");
		Spark.setStepOver();    	
	}

	public void stopTestSuiteExecution(){
		if(task.isRunning()){
			Spark.stopExecution();
		}
	}

	@Override
	public void setParams(List<String> args) {
		this.args=args;
	}


	public void removeAllBreakPoints(){
		if(spreadSheet!=null){
			spreadSheet.removeAllBreakPoints();
		}
		removeAllBreakPointsFromZug();
	}

	public void removeAllBreakPointsFromZug(){
		Spark.removeAllBreakPoints();
	}

	public void saveCurrentTestSuite(){
		sheetTabPane.getCurrentSpreadSheet().save();
	}

	public void saveAs(){
		String fileName=tsChooser.showSaveDialog();
		if(fileName!=null){
			sheetTabPane.getCurrentSpreadSheet().saveAs(fileName);			
		}
	}

	public void revertToLastSaveState(){
		reloadTestSuite();
	}

	public void showMoleculeInSheetView(String moleculeName){
		SpreadSheet sp=null;
		//System.out.println(moleculeName);
		boolean includeMolecule=true;
		if(!moleculeName.contains(".")){
			sp=sheetTabPane.getCurrentSpreadSheet();
			includeMolecule=false;
		}else{
			String fileName=moleculeName.substring(0, moleculeName.indexOf("."));
			//	System.out.println("fileName "+fileName);
			moleculeName=moleculeName.substring(moleculeName.indexOf(".")+1 );
			//	System.out.println("moleculeName " +moleculeName);
			//	System.out.println(SpreadSheet.getUniqueSheets());
			Iterator it = SpreadSheet.getUniqueSheets().keySet().iterator();

			while (it.hasNext()) {
				String fileUQ=(String)it.next();
				String file=new File(fileUQ).getName();
				//	System.out.println("file "+file);
				if(file!=null){
					if(file.substring(0, file.lastIndexOf(".")).equalsIgnoreCase(fileName)){
						sp=SpreadSheet.getUniqueSheets().get(fileUQ);
						break;
					}
				}
			}
		}
		if(sp!=null){
			ActionInfoBean aib=sp.getMoleculesSheet().moleculeExist(moleculeName);
			if(aib==null){
				System.err.println(moleculeName+" does not exists");
			}else{
				int n=aib.getLineNo();

				if(includeMolecule){
					sheetTabPane.loadPanes(sp);
				}
				sheetTabPane.showMoleculeTab(moleculeName);
			}
		}else{
			//System.out.println("sp is null");
		}
	}

	private void ensureVisible(ScrollPane pane, Node node) {

		double width = pane.getContent().getBoundsInLocal().getWidth();
		double height = pane.getContent().getBoundsInLocal().getHeight();

		double x = node.getBoundsInParent().getMaxX();
		double y = node.getBoundsInParent().getMaxY();

		// scrolling values range from 0 to 1
		pane.setVvalue(y/height);
		pane.setHvalue(x/width);

		// just for usability
		node.requestFocus();
	}

	public void showLogs(){
		ApplicationLauncher.showLogs();
	}
	public void pauseExecution(){
		Spark.setPauseSignal();
		
	}

	@Override
	public synchronized void setCurrentTestCase(TestCase testCase) {
		// TODO Auto-generated method stub
		currentStack.push(testCase);
	}
	
	

	@Override
	public synchronized void removeTestCase(TestCase testCase) {
		// TODO Auto-generated method stub
		currentStack.pop();
	}

	@Override
	public boolean isExpressionEvaluatorMode() {
		// TODO Auto-generated method stub
		return expressionEvaluatorMode.get();
	}
	
	public SpreadSheet getCurrentSpreadSheet(){
		return sheetTabPane.getCurrentSpreadSheet();
	}
}