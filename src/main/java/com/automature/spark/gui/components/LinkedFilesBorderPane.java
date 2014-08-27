/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.components;

import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.gui.sheets.SpreadSheet;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author skhan
 */
public class LinkedFilesBorderPane extends BorderPane {

	private TreeItem<String> getSheetTree(SpreadSheet sp) {
		TreeItem<String> rootItem = new TreeItem<String>(sp.getAbsolutePath());// ,
		// rootIcon);
		Iterator it = sp.getIncludeFiles().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			SpreadSheet sheet = sp.getIncludeFiles().get(key);
			rootItem.getChildren().add(getSheetTree(sheet));
		}
		return rootItem;

	}

	public void loadLinkedFiles(SpreadSheet sp,
			final ZugguiController guiController) {
		TreeItem<String> rootItem = getSheetTree(sp);
		TreeView<String> treeView = new TreeView<String>(rootItem);
		treeView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<String>>() {

					@Override
					public void changed(
							ObservableValue<? extends TreeItem<String>> ov,
							TreeItem<String> oldValue, TreeItem<String> newValue) {
						TreeItem<String> selectedItem = (TreeItem<String>) newValue;
						if (newValue != null) {
							guiController.showSpreadSheet(newValue.getValue());
						}
					}

				});
		setCenter(treeView);
	}

}
