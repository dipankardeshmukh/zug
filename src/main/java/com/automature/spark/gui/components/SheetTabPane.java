/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.components;

import com.automature.spark.gui.components.TreeTableSheetTab;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.gui.sheets.MacroSheet;
import com.automature.spark.gui.sheets.SpreadSheet;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 *
 * @author skhan
 */
public class SheetTabPane extends TabPane {

	private static ZugguiController controller;
    private SheetTab testCaseTab, moleculeTab, configTab, macroTab;
    private SpreadSheet currentSpreadSheet;

    public SheetTabPane() {
        super();
        /*configTab = new SheetTab("Config");
        macroTab = new SheetTab("Macros");
        testCaseTab = new TestCaseTab("Test Cases");
        moleculeTab = new MoleculeTab("Molecules");
        getTabs().add(configTab);
        getTabs().add(macroTab);
        getTabs().add(testCaseTab);
        getTabs().add(moleculeTab);*/
        setPrefSize(700, 700);
        setMinSize(500, 500);
    }

  

	public void loadPanes(SpreadSheet sp) {
        try {
        	this.getTabs().clear();
        	currentSpreadSheet=sp;
        	getTabs().add(sp.getConfigSheet().getSheetTab());
        	getTabs().add(sp.getMacroSheet().getSheetTab());
        	getTabs().add(sp.getTestCasesSheet().getSheetTab());
        	getTabs().add(sp.getMoleculesSheet().getSheetTab());
        	/*TreeTableSheetTab  tcTab=new TestCaseTreeTableSheetTab("Test Cases");
        	tcTab.loadTabData(sp.getTestCasesSheet().getHeader(), sp.getTestCasesSheet().getData());
        	tcTab.setFileName(sp.getAbsolutePath());
        	getTabs().add(tcTab);
        	TreeTableSheetTab  molTab=new MoleculeTreeTableSheetTab("Molecules");
        	molTab.loadTabData(sp.getMoleculesSheet().getHeader(), sp.getMoleculesSheet().getData());
        	molTab.setFileName(sp.getAbsolutePath());
        	getTabs().add(molTab);*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



	public void clearSelections() {
		// TODO Auto-generated method stub
		ObservableList<Tab> tabs=getTabs();
		for(Tab t:tabs){
			((SheetTab)t).clearSelection();
		}
		
	}



	public SpreadSheet getCurrentSpreadSheet() {
		return currentSpreadSheet;
	}



	public static ZugguiController getController() {
		return controller;
	}



	public static void setController(ZugguiController controller) {
		SheetTabPane.controller = controller;
	}
    
	public void showMoleculeTab(){
		getSelectionModel().select(3);
		getSelectionModel().getSelectedItem();
	}
	public void showMoleculeTab(String id){
		getSelectionModel().select(3);
		MoleculeTreeTableSheetTab tab=(MoleculeTreeTableSheetTab)getSelectionModel().getSelectedItem();
		tab.expandCurrentTestCase(id);
		
	}

   
}
