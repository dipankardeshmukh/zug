/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import com.automature.spark.beans.ActionInfoBean;
import com.automature.spark.beans.MacroColumn;
import com.automature.spark.beans.TestCaseStatus;
import com.automature.spark.engine.Spark;
import com.automature.spark.engine.TestCase;
import com.automature.spark.exceptions.ReportingException;
import com.automature.spark.gui.Constants;
import com.automature.spark.gui.ExpressionEvaluator;
import com.automature.spark.gui.ReporterPaneChildWindow;
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
import com.automature.spark.gui.utils.GuiUtils;
import com.automature.spark.gui.utils.ScreenLoader;
import com.automature.spark.gui.utils.TestSuiteChooser;
import com.automature.spark.reporter.SpacetimeReporter;
import com.automature.spark.util.ExtensionInterpreterSupport;
import com.automature.spark.util.Log;
import com.automature.spark.util.Styles;
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
	private CheckBox dbReportingCB;
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
	private static ConsoleController zugGuiConsole;
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
	public String productId;
	public int listElementFinalIndex=10;
	public int pageNumber=1;
	public boolean isPopupOpened=false;
	public static ZugguiController controller;
	
	public static SpacetimeReporter reporter;

	public Hashtable connectionParam=new Hashtable();
	
	private static String[] switchesToConfigureTest;

	@FXML
	private TextField product;
	@FXML
	private TextField testPlan;
	@FXML
	private TextField testCycle;
	@FXML
	private TextField topoSet;
	@FXML
	private TextField buildTag;
	@FXML
	private TableView testExecutionResults;
	@FXML
	private TableColumn testCase;
	@FXML
	private TableColumn result;
	@FXML
	private TableColumn timeOfExecution;
	@FXML
	private Button createTestCycleBtn;
	@FXML
	private Button createBuildTagBtn;
	
	private ReporterPaneChildWindow popup;
	

    public ObservableList<TestCaseStatus> rowData = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
		controller=this;
		try{
			sessionHandler = new SessionHandler();
			sessionHandler.retriveSession();
			tsChooser = new TestSuiteChooser();
			sheetTabPane = new SheetTabPane();
			SheetTabPane.setController(this);
			optionBuilder=new RuntimeOptionBuilder();
			popup=new ReporterPaneChildWindow();
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
		optionBuilder.registerDbReportingCheckBox(dbReportingCB , "-dbreporting");
		optionBuilder.registerCheckBox(ignoreCB, "-ignore");
		optionBuilder.registerCheckBox(atomExecTimeCB, "-atomexectime");
		optionBuilder.registerRepeatOptions(repeatCheckBox,repeatCountRadioButton,repeatDurationRadioButton,repeatCountTextBox,repeatDurationTextBox,repeatChoiceBox);
		optionBuilder.registerEnvironmentOptions(testSuiteCB, environmentCB, useDefaultCB, macroTF);
		optionBuilder.registerReportingOptions(product, testPlan, testCycle, topoSet, buildTag);
		optionBuilder.registerTestExecutionResults(testExecutionResults,testCase,result,timeOfExecution);
	}
	public boolean initReportingConfigurations(){
		boolean isDbConfigured=true;
		String tpName="";
		HashMap<String,String> products=new HashMap<String,String>();
		
		ExtensionInterpreterSupport testINI = new ExtensionInterpreterSupport();
		try {
			String hostName="",userName="",userPassword="";
			if(SpreadSheet.connectionParam.size()==0)
			{
			hostName=testINI.getNode(Spark.db_host_xml_tag_path);
		    userName=testINI.getNode(Spark.db_user_xml_tag_path);
			userPassword=testINI.getNode(Spark.db_password_xml_tag_path);
			if(!hostName.startsWith("http://"))
				hostName="http://"+hostName;
			connectionParam.put("dbhostname", hostName);
			connectionParam.put("dbusername", userName);
			connectionParam.put("dbuserpassword", userPassword);
			isDbConfigured=true;
			}
			else if(SpreadSheet.connectionParam.size()!=0)
			isDbConfigured=true;
			else
			{
				System.err.println("\nPlease check SpaceTime configurations in Spark.ini or specify db configuration credentials in testsuite\n\n");
				isDbConfigured=false;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if(isDbConfigured)
		{
		if(SpreadSheet.connectionParam.size()!=0)
		reporter=new SpacetimeReporter(SpreadSheet.connectionParam);	
		else	
		reporter=new SpacetimeReporter(connectionParam);
		
		try {
		if(!reporter.connect())
		{
			System.err.println("\nPlease check SpaceTime configurations in Spark.ini or specify db configuration credentials in testsuite\n\n");
			isDbConfigured=false;
			return isDbConfigured;
		}
		} catch (Throwable e1) {
			e1.printStackTrace();
		} 
		
		ArrayList<String> listOftestPlans=new ArrayList<String>();
		ArrayList<String> listOftestCycles=new ArrayList<String>();
		ArrayList<String> listOfTopoSets=new ArrayList<String>();
		ArrayList<String> listOfBuilds=new ArrayList<String>();
		product.setOnMouseClicked(event->{
			if(isPopupOpened)
				return;
			reconnectToDBifConnectionIsLost();
				popup.displayListView(reporter.getProductList(),product,"Products",reporter,listOftestPlans,null,event);
				this.testPlan.setDisable(true);
				this.testPlan.setText("");
				this.testCycle.setDisable(true);
				this.testCycle.setText("");
				this.topoSet.setDisable(true);
				this.topoSet.setText("");
				this.buildTag.setDisable(true);
				this.buildTag.setText("");
				this.createTestCycleBtn.setDisable(true);
				this.createBuildTagBtn.setDisable(true);
		});

		testPlan.setOnMouseClicked(event->{
			if(isPopupOpened)
				return;
			reconnectToDBifConnectionIsLost();
			popup.displayListView(listOftestPlans,testPlan,"Testplans",reporter,listOftestCycles,null, event);
			this.testCycle.setDisable(true);
			this.testCycle.setText("");
			this.topoSet.setDisable(true);
			this.topoSet.setText("");
			this.buildTag.setDisable(true);
			this.buildTag.setText("");
//			this.createTestCycleBtn.setDisable(true);
		});
		testCycle.setOnMouseClicked(event->{
			if(isPopupOpened)
				return;
			reconnectToDBifConnectionIsLost();
			popup.displayListView(listOftestCycles,testCycle,"TestCycles",reporter,listOfTopoSets,listOfBuilds, event);

    		createTestCycleBtn.setDisable(true);
        	createBuildTagBtn.setDisable(true);
			this.topoSet.setDisable(true);
			
//			this.topoSet.setText("");
			this.buildTag.setDisable(true);
//			this.buildTag.setText("");
		});
		topoSet.setOnMouseClicked(event->{
			if(testCycle.getText().lastIndexOf("(")<=0)
				return;
			if(isPopupOpened)
				return;
			reconnectToDBifConnectionIsLost();
			popup.displayListView(listOfTopoSets,topoSet,"TopologyStets",reporter,null,null, event);

    		createTestCycleBtn.setDisable(true);
        	createBuildTagBtn.setDisable(true);
			this.buildTag.setDisable(true);
//			this.buildTag.setText("");
		});
		
		buildTag.setOnMouseClicked(event->{
			if(testCycle.getText().lastIndexOf("(")<=0)
				return;
			if(isPopupOpened)
				return;
			reconnectToDBifConnectionIsLost();
			popup.displayListView(listOfBuilds,buildTag,"Builds",reporter,null,null, event);

    		createTestCycleBtn.setDisable(true);
        	createBuildTagBtn.setDisable(true);
		});
		}
		return isDbConfigured;
	}
	
	private void reconnectToDBifConnectionIsLost() {
		if(reporter.sessionid==null)
			try {
				reporter.connect();
			} catch (ReportingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
					
					zugGuiConsole=console;
					
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

	public void createTestCycle(){
		
		setAllReporterPaneControlsDisabled();
//		if(ZugguiController.controller.getCreateBuildTagBtn().isDisable())
//		createBuildTagBtn.setDisable(true);
//		
//		createTestCycleBtn.setDisable(true);
//		testCycle.setDisable(true);
//		topoSet.setDisable(true);
//		buildTag.setDisable(true);
		
		ScreenLoader loader;
		try {
			loader = new ScreenLoader("/com/automature/spark/gui/resources/TestCycleGenerator.fxml");
			loader.getStage().initOwner(splitPane.getScene().getWindow());
			loader.getStage().initStyle(StageStyle.UTILITY);
			loader.getStage().setTitle("Create TestCycle");
			loader.getStage().setResizable(false);
			loader.getStage().setAlwaysOnTop(true);
			TestCycleGeneratorController.setStage(loader.getStage());
			loader.getStage().setOnCloseRequest(event->{
				
				ZugguiController.controller.setAllReporterPaneControlsEnabled();
				if(!ZugguiController.controller.getTopoSet().getText().equals(""))
				ZugguiController.controller.getTopoSet().setDisable(false);
				else
				ZugguiController.controller.getTopoSet().setDisable(true);	
				
				if(!ZugguiController.controller.getBuildTag().getText().equals(""))
				{
				ZugguiController.controller.getBuildTag().setDisable(false);
				ZugguiController.controller.getCreateBuildTagBtn().setDisable(false);
				}
				else
				{
				ZugguiController.controller.getBuildTag().setDisable(true);
				ZugguiController.controller.getCreateBuildTagBtn().setDisable(true);
				}
				
			});
			loader.getStage().show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void setAllReporterPaneControlsDisabled() {
		
		product.setDisable(true);
		testPlan.setDisable(true);
		testCycle.setDisable(true);
		topoSet.setDisable(true);
		buildTag.setDisable(true);
		createBuildTagBtn.setDisable(true);
		createTestCycleBtn.setDisable(true);
		
	}
	
	public void setAllReporterPaneControlsEnabled() {
		
		product.setDisable(false);
		testPlan.setDisable(false);
		testCycle.setDisable(false);
		topoSet.setDisable(false);
		buildTag.setDisable(false);
		createBuildTagBtn.setDisable(false);
		createTestCycleBtn.setDisable(false);
		
	}

	public void createBuildTag() {
		
		setAllReporterPaneControlsDisabled();
		
		TestCycleGeneratorController.isBuildGeneratorOpenedFromTestCycleGenerator=false;
		
		createBuildTagBtn.setDisable(true);
		buildTag.setDisable(true);
		ScreenLoader loader;
		try {
			loader = new ScreenLoader("/com/automature/spark/gui/resources/BuildGenerator.fxml");
			loader.getStage().initOwner(splitPane.getScene().getWindow());
			loader.getStage().initStyle(StageStyle.UTILITY);
			loader.getStage().setTitle("Create BuildTag");
			loader.getStage().setResizable(false);
			loader.getStage().setAlwaysOnTop(true);
			BuildTagGeneratorController.setStage(loader.getStage());
			loader.getStage().setOnCloseRequest(event->{

				if(!TestCycleGeneratorController.isBuildGeneratorOpenedFromTestCycleGenerator)
					ZugguiController.controller.setAllReporterPaneControlsEnabled();
				
			});
			loader.getStage().show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
			GuiUtils.showMessage("Error reading testSuite ", e);
			//System.err.println("Error reading testSuite " + e.getMessage());
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
				
//				removeBPMI.setDisable(b);
//				product.setDisable(b);
//				testPlan.setDisable(b);
//				testCycle.setDisable(b);
//				topoSet.setDisable(b);
//				buildTag.setDisable(b);
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
		
		if((dbReportingCB.isSelected()) && (StringUtils.isEmpty(ZugguiController.controller.getProduct().getText()) || 
				StringUtils.isEmpty(ZugguiController.controller.getTestPlan().getText())||
				StringUtils.isEmpty(ZugguiController.controller.getTestCycle().getText())||
				StringUtils.isEmpty(ZugguiController.controller.getTopoSet().getText())||
				StringUtils.isEmpty(ZugguiController.controller.getBuildTag().getText())
				))
		{
			System.err.println("\nPlease check reporting configuration settings from Reporting pane and proceed\n");
			return;
			
		}
		TestCase.testCaseNumber=1;
		ZugguiController.getZugGuiConsole().getProgressBar().setProgress(0.0);
		ZugguiController.getZugGuiConsole().getProgressBar().lookup(".bar").setStyle(Styles.greenBackground);
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
		if(switchesToConfigureTest!=null)
			for (int i = 0; i < switchesToConfigureTest.length; i++) {
				if(switchesToConfigureTest[i].substring(switchesToConfigureTest[i].indexOf("=")+1).equals(""))
				{
					continue;
				}
				params.add(switchesToConfigureTest[i]);
			}
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
				exprEvaluatorMI.setDisable(b);
				dbReportingCB.setDisable(!b);
				runButton.setDisable(!b);
				runTestMI.setDisable(!b);
				
				if(dbReportingCB.isSelected())
				{
				product.setDisable(!b);
				testPlan.setDisable(!b);
				testCycle.setDisable(!b);
				topoSet.setDisable(!b);
				buildTag.setDisable(!b);
				createBuildTagBtn.setDisable(!b);
				createTestCycleBtn.setDisable(!b);
				}
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
	public static String[] getSwitches() {
		return switchesToConfigureTest;
	}
	public static void setSwitches(String[] switches) {
		ZugguiController.switchesToConfigureTest = switches;
	}
	@Override
	public String getProductId() {
		if(product.getText().equals(""))
			return "";
		else
		return product.getText().substring(product.getText().lastIndexOf(" (")+2, product.getText().lastIndexOf(")"));
	}

	@Override
	public String getTestPlanId() {
		if(testPlan.getText().equals(""))
			return "";
		else
		return testPlan.getText().substring(testPlan.getText().lastIndexOf(" (")+2, testPlan.getText().lastIndexOf(")"));
	}

	@Override
	public String getTestCycleId() {
		if(testCycle.getText().equals(""))
			return "";
		else
		return testCycle.getText().substring(testCycle.getText().lastIndexOf(" (")+2, testCycle.getText().lastIndexOf(")"));
	}
    
	public String getTestCycleDesc() {
		if(testCycle.getText().equals(""))
			return "";
		else
		try{
		return testCycle.getText().substring(0, testCycle.getText().lastIndexOf(" ("));
			}catch (Exception e) {
				return testCycle.getText();
			}
	}
	
	public String getBuildTagDesc() {
		if(buildTag.getText().equals(""))
			return "";
		else
		try{
		return buildTag.getText().substring(0, buildTag.getText().lastIndexOf(" ("));
			}catch (Exception e) {
				return buildTag.getText();
			}
	}
	
	@Override
	public String getTopologySetId() {
		if(topoSet.getText().equals(""))
			return "";
		else
		return topoSet.getText().substring(topoSet.getText().lastIndexOf(" (")+2, topoSet.getText().lastIndexOf(")"));
	}

	@Override
	public String getBuildId() {
		if(buildTag.getText().equals(""))
			return "";
		else
		return buildTag.getText().substring(buildTag.getText().lastIndexOf(" (")+2, buildTag.getText().lastIndexOf(")"));
	}
	
	public TextField getProduct() {
		return product;
	}
	
	public TextField getTestPlan() {
		return testPlan;
	}
	
	public TextField getTestCycle() {
		return testCycle;
	}
	
	public TextField getTopoSet() {
		return topoSet;
	}
	
	public TextField getBuildTag() {
		return buildTag;
	}
	
	public Button getCreateTestCycleBtn() {
		return createTestCycleBtn;
	}
	
	public Button getCreateBuildTagBtn() {
		return createBuildTagBtn;
	}
	
	public TableView getTestExecutionResults() {
		return testExecutionResults;
	}
	
	public TableColumn getTestCaseColumn() {
		return testCase;
	}
	
	public TableColumn getResultColumn() {
		return result;
	}
	public void setTestExecutionResultsDefaultStyle() {
		Platform.runLater(new Runnable() {

			@Override
		    public void run() {
				try{
					Set<Node> nodes=testExecutionResults.lookupAll("TableRow");
				for (Node n: nodes) {
				      if (n instanceof TableRow) {
				    	  TableRow row = (TableRow) n;
				    	  try{
				    	  	row.setStyle(Styles.tableRowDefultStyle);
				    	  }catch(Exception e){}
				      }
				      }
					}catch(Exception e){}
		    }
		});
	}
	
	public void setReportingPaneFieldsDefault(){
	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				product.setText("");
				testPlan.setText("");
				testCycle.setText("");
				topoSet.setText("");
				buildTag.setText("");
				
				product.setDisable(true);
				testPlan.setDisable(true);
				testCycle.setDisable(true);
				topoSet.setDisable(true);
				buildTag.setDisable(true);
				createBuildTagBtn.setDisable(true);
				createTestCycleBtn.setDisable(true);
			}
		});
	}
	
	public void setReportingPaneFieldsEnable(){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				product.setDisable(false);
			}
		});
		
	}
	
	public static ConsoleController getZugGuiConsole() {
		return zugGuiConsole;
	}
	
	public ConsoleController getConsole() {
		return console;
	}
	
	public RuntimeOptionBuilder getOptionBuilder() {
		return optionBuilder;
	}
}