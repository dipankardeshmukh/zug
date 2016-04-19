/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui;

import java.io.File;







import javafx.scene.control.cell.PropertyValueFactory;







import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;












import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor.MAROON;












import com.automature.spacetimeapiclient.SpacetimeClient;
import com.automature.spark.beans.MacroColumn;
import com.automature.spark.beans.TestCaseStatus;
import com.automature.spark.engine.Spark;
import com.automature.spark.engine.TestCase;
import com.automature.spark.gui.components.TestCaseTab;
import com.automature.spark.gui.components.TestCaseTreeTableSheetTab;
import com.automature.spark.gui.controllers.ConsoleController;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.gui.sheets.SpreadSheet;
import com.automature.spark.gui.utils.GuiUtils;
import com.automature.spark.reporter.SpacetimeReporter;








import com.automature.spark.util.Styles;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.scene.control.Hyperlink;

/**
 *
 * @author sdatta
 */
public class RuntimeOptionBuilder {

	private Set<String> params;
	private Set<String> checkBoxParams;
	public static String verbose="-verbose";
	public static String debug="-debugger";
	private EventHandler checkBoxEvent;
	private SpreadSheet spreadSheet;

	private CheckBox repeatCheckBox;

	private RadioButton repeatCountRadioButton;

	private RadioButton repeatDurationRadioButton;

	private ChoiceBox<String> repeatChoiceBox;

	private TextField repeatCountTextBox;

	private TextField repeatDurationTextBox;
	private CheckBox useDefaultCB;
	private TextField macroTF;
	private ComboBox<MacroColumn> testSuiteCB;
	private ChoiceBox<String> environmentCB;
	
	private Map<String,String> macroCols;

	private TextField listProducts;
	private TextField listTestPlans;
	private TextField listTestCycles;
	private TextField listTopoSet;
	private TextField listBuildTag;

	private TableView testExecutionResults;
	private TableColumn testCase;
	private TableColumn result;
	private TableColumn timeOfExecution;

	public RuntimeOptionBuilder() {
		super();
		macroCols=new HashMap<String,String>();
		checkBoxParams=new HashSet<String>();
		checkBoxEvent = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (event.getSource() instanceof CheckBox) {
					CheckBox chk = (CheckBox) event.getSource();
					if(chk.isSelected()){
						checkBoxParams.add((String)chk.getUserData());		            	
					}else{
						checkBoxParams.remove(chk.getUserData());

					}
				}
			}
		};
		

	}

	public Set<String> getRuntimeParams(){
		params=new HashSet<String>();
		addTestCaseIds();
		getRepeatOptions();
		addEnvironmentOptions();
		params.addAll(checkBoxParams);
		return params;
	}

	private void addEnvironmentOptions() {
		// TODO Auto-generated method stub
		StringBuilder tmp=new StringBuilder("-macroColumn=");
		if(useDefaultCB.isSelected()){
			if(macroCols.containsKey(spreadSheet.getFileName())){
				String value=macroCols.get(spreadSheet.getFileName());
				ObservableList<MacroColumn> oList=testSuiteCB.getItems();
				for(MacroColumn mc:oList){
					tmp.append(mc.getName()+":"+value+",");
				}
				
			}else{
				//System.err.print("Main test suite environment is not selected.Skipping environment selection");
			}
			
		}else{
			Iterator<String> it=macroCols.keySet().iterator();
			while(it.hasNext()){
				String file=it.next();
				String value=macroCols.get(file);
				tmp.append(file+":"+value+",");
			}
		}
		String macroColOpt=tmp.toString();
		if(!macroColOpt.equals("-macroColumn=")){
			params.add(macroColOpt.substring(0,macroColOpt.length()-1));
		}
		String macroText=macroTF.getText();
		if(macroText!=null && !StringUtils.isBlank(macroText)){
			String str[]=macroText.split(" -");
			for(String val:str){
				params.add(val.startsWith("-")?val:"-"+val);
			}
			//params.add(macroText);
		}
	}

	public void registerCheckBox(CheckBox checkBox,String value){
		checkBox.setUserData(value);
		checkBox.setOnAction(checkBoxEvent);
	}
	
	public void registerDbReportingCheckBox(CheckBox dbReportingCB,
			String value) {
		// TODO Auto-generated method stub
		dbReportingCB.setUserData(value);
		
		dbReportingCB.setOnAction(event->{
			try{
				if(dbReportingCB.isSelected())
				ConsoleController.controller.clear();
			}
			catch(Exception e){}
			if (event.getSource() instanceof CheckBox) {
				CheckBox chk = (CheckBox) event.getSource();
				if(chk.isSelected()){
					checkBoxParams.add((String)chk.getUserData());	
					
					Task<Boolean> task = new Task<Boolean>() {
					    @Override protected Boolean call() throws Exception {
//					    	if(SpacetimeReporter.sessionid!=null)
//					    	{
//					    		System.out.println("\nAlready connected to Spacetime\n\n");
//					    		ZugguiController.controller.setReportingPaneFieldsEnable();
//					    		return true;
//					    	}
					    	System.out.println("Connecting to Spacetime.\n\nThe Reporter panel will be enabled after the connection is established\n\nPlease Wait .....");
					    	Boolean b = null;
//					    	if(SpacetimeReporter.sessionid==null)
					    		try{
								b=ZugguiController.controller.initReportingConfigurations();
					    		}catch (Exception e) {
									b=false;
								}
						    if(b)
						    {
						    	System.out.println("Successfully connected to Spacetime");
						    	
						    	ZugguiController.controller.setReportingPaneFieldsEnable();
						    	
						    }
						    else
						    {
						    	System.err.println("Connection failed .....\n\nContinuing without reporting .....");
						    	SpacetimeReporter.sessionid=null;
						    	Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										dbReportingCB.setSelected(false);
										checkBoxParams.remove(chk.getUserData());
									}
								});
						    	ZugguiController.controller.setReportingPaneFieldsDefault();
						    }
						    return b;
					    }
					};
					Thread t=new Thread(task);
					t.start();
					
				}else{
					checkBoxParams.remove(chk.getUserData());
					if(ZugguiController.controller.getTestPlan().isDisable()||ZugguiController.controller.getTestCycle().isDisable())
						ZugguiController.controller.setReportingPaneFieldsDefault();
				}
			}
		});
		
	}
	
	public void registerSpreadSheet(SpreadSheet spreadSheet){
		this.spreadSheet=spreadSheet;
		refreshEnvironmentOptions();

	}

	public void addTestCaseIds(){
		if(!((TestCaseTreeTableSheetTab)spreadSheet.getTestCasesSheet().getSheetTab()).isSelectAll()){
			List<String> testCaseId=((TestCaseTreeTableSheetTab)spreadSheet.getTestCasesSheet().getSheetTab()).getSelectedTestCaseIds();
			if(testCaseId.size()>0){
				String ids=testCaseId.toString().replaceAll(" ","");
				ids="-testcaseid="+ids.substring(1,ids.length()-1);
				params.add(ids);
			}
		}
	}

	public void registerRepeatOptions(CheckBox repeatCheckBox,
			RadioButton repeatCountRadioButton,
			RadioButton repeatDurationRadioButton,
			TextField repeatCountTextBox, TextField repeatDurationTextBox,
			ChoiceBox<String> repeatChoiceBox) {
		this.repeatCheckBox = repeatCheckBox;
		this.repeatCountRadioButton = repeatCountRadioButton;
		this.repeatDurationRadioButton = repeatDurationRadioButton;
		this.repeatChoiceBox = repeatChoiceBox;
		this.repeatCountTextBox = repeatCountTextBox;
		this.repeatDurationTextBox = repeatDurationTextBox;
		initializeRepeatOptions();
		// TODO Auto-generated method stub

	}

	private void initializeRepeatOptions() {
		// TODO Auto-generated method stub
		repeatCountRadioButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(repeatCountRadioButton.isSelected()){
					repeatCountTextBox.setDisable(false);
					repeatDurationTextBox.setDisable(true);
					repeatChoiceBox.setDisable(true);
				}else{
					repeatCountTextBox.setDisable(true);

				}
			}
		});
		repeatDurationRadioButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				if(repeatDurationRadioButton.isSelected()){
					repeatDurationTextBox.setDisable(false);
					repeatChoiceBox.setDisable(false);
					repeatCountTextBox.setDisable(true);
				}else{
					repeatDurationTextBox.setDisable(true);
					repeatChoiceBox.setDisable(true);
				}
			}
		});

		addOnlyNumberPropertyInTextField(repeatCountTextBox);
		addOnlyNumberPropertyInTextField(repeatDurationTextBox);
		repeatChoiceBox.setItems(FXCollections.observableArrayList(
			    "seconds", "minutes", 
			    "hours", "days")
		);
		repeatChoiceBox.getSelectionModel().select(3);
		
	}
	
	public boolean isRepeat(){
		
		if(repeatCheckBox.isSelected()){
			return true;
		}else{
			return false;
		}
		
	}
	
	public void getRepeatOptions(){
		if(repeatCheckBox.isSelected()){
			if(repeatCountRadioButton.isSelected()){
				params.add("-repeat");
				params.add("-count="+repeatCountTextBox.getText());
			}else if(repeatDurationRadioButton.isSelected()){
				String n=repeatDurationTextBox.getText();
				String unit=repeatChoiceBox.getSelectionModel().getSelectedItem();
				params.add("-repeat");
				if(unit.equals("days")){
					unit=n+"d";
				}else if(unit.equals("hours")){
					unit=n+"h";
				}
				else if(unit.equals("minutes")){
					unit=n+"m";
				}else {
					unit=n+"s";
				}
				params.add("-Duration="+unit);
			}	
		}
	}
	
	public void addOnlyNumberPropertyInTextField(final TextField field){
		GuiUtils.addOnlyNumberPropertyInTextField(field);
	}	
	
	public void registerEnvironmentOptions(ComboBox<MacroColumn> testSuiteCB,ChoiceBox<String> environmentCB,CheckBox useDefaultCB,TextField macroTF){
		this.testSuiteCB=testSuiteCB;
		this.environmentCB=environmentCB;
		this.useDefaultCB=useDefaultCB;
		this.macroTF=macroTF;
		initializeEnvironmentOptions();
		
		
	}
	
	
	public void registerReportingOptions(TextField product,TextField testPlan,TextField testCycle,TextField topoSet,TextField buildTag){
		this.listProducts=product;
		this.listProducts.setDisable(true);
		this.listProducts.setEditable(false);
		this.listTestPlans=testPlan;
		this.listTestPlans.setDisable(true);
		this.listTestPlans.setEditable(false);
		this.listTestCycles=testCycle;
		this.listTestCycles.setDisable(true);
		this.listTestCycles.setEditable(false);
		this.listTopoSet=topoSet;
		this.listTopoSet.setDisable(true);
		this.listTopoSet.setEditable(false);
		this.listBuildTag=buildTag;
		this.listBuildTag.setDisable(true);
		this.listBuildTag.setEditable(false);
	}
	

	public void registerTestExecutionResults(TableView testExecutionResults, TableColumn testCase, TableColumn result, TableColumn timeOfExecution) {
		//initialize table view
		this.testExecutionResults=testExecutionResults;
		
		this.testCase=testCase;
		this.testCase.setCellValueFactory(new PropertyValueFactory<TestCaseStatus,String>("testCase"));
		
		this.result=result;
		this.result.setCellValueFactory(new PropertyValueFactory<TestCaseStatus,String>("result"));
		this.result.setCellFactory(col->{
			 return new TableCell<TestCaseStatus,String>(){
				 
				  @Override
				  protected void updateItem(String item,boolean empty) {
					  super.updateItem(item, empty);
					  if (item == null || empty) {
			                setText("");
			                setStyle("");
			            } else {
				            
			            	setText(item);
			            	
			            	if(item.equalsIgnoreCase("running"))
			            		setTextFill(Color.DARKBLUE);
			            	else if(item.equalsIgnoreCase("pass"))
			            	{
			            		((Hyperlink)testCase.getCellData(getIndex())).setId(item);
			            		setTextFill(Color.GREEN);
			            	}
			            	else
			            	{
			            		((Hyperlink)testCase.getCellData(getIndex())).setId(item);
			            		setTextFill(Color.RED);
			            	}
			            }
				}
				  
			 };
		});

		this.timeOfExecution=timeOfExecution;
		this.timeOfExecution.setCellValueFactory(new PropertyValueFactory<TestCaseStatus,String>("time"));
		this.timeOfExecution.setCellFactory(col->{
			
			 return new TableCell<TestCaseStatus,Long>(){
				 
				  @Override
				  protected void updateItem(Long item,boolean empty) {
					  super.updateItem(item, empty);
					  if (item == null  || empty ) {
						  setText("");
			                setStyle("");
						  return;
			            } else{
			            if(getTableRow().getStyle().equals(Styles.tableRowColorOnClick))
			            	getTableRow().setStyle(Styles.tableRowColorOnClick);
			            else	
			            	getTableRow().setStyle(Styles.tableRowDefultStyle);
			            setText(String.valueOf(item));
			            	setTextFill(Color.DARKBLUE);
			            	
			            }
				}
				  
			 };
		});
	}
	private void initializeEnvironmentOptions(){
		
		testSuiteCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MacroColumn>() {

			@Override
			public void changed(ObservableValue<? extends MacroColumn> arg0,
					MacroColumn oldValue, MacroColumn newValue) {
				// TODO Auto-generated method stub
				environmentCB.getItems().clear();
				if(newValue!=null){
					environmentCB.getItems().addAll(newValue.getCols());
					String selectedEnv=macroCols.get(newValue.getName());
					if(selectedEnv!=null){
						environmentCB.getSelectionModel().select(selectedEnv);
					}
				}				
			}
		});
		environmentCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String oldValue, String newValue) {
				// TODO Auto-generated method stub
				MacroColumn selectedCBItem=testSuiteCB.getSelectionModel().getSelectedItem();
				if(newValue!=null){
					macroCols.put(selectedCBItem.getName(), newValue);
					String selectedFile=testSuiteCB.getSelectionModel().getSelectedItem().getName();
					if(selectedFile!=null&&!spreadSheet.getFileName().equalsIgnoreCase(selectedFile)){
						useDefaultCB.setSelected(false);
					}
				}
				
			}
		});
	}
	
	public void refreshEnvironmentOptions(){
		testSuiteCB.getItems().clear();
		environmentCB.getItems().clear();
		macroCols.clear();
		Iterator<String> it = SpreadSheet.getUniqueSheets().keySet().iterator();
		while (it.hasNext()) {			
			SpreadSheet sp= SpreadSheet.getUniqueSheets().get(it.next());
			testSuiteCB.getItems().add(sp.getMacroColumn());
			//environmentCB.getItems().addAll(sp.getMacroColumn().getCols());
		}
	}
	
	public Set<String> getCheckBoxParams() {
		return checkBoxParams;
	}
	
}
