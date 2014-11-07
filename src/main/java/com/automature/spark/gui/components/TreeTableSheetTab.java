package com.automature.spark.gui.components;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.controlsfx.dialog.Dialogs;

import com.automature.spark.beans.ActionInfoBean;
import com.automature.spark.beans.ExistenceMessageBean;
import com.automature.spark.engine.AtomHandler;
import com.automature.spark.engine.Spark;
import com.automature.spark.gui.Constants;
import com.automature.spark.gui.MacroEvaluator;
import com.automature.spark.gui.sheets.SpreadSheet;
import com.automature.spark.gui.utils.GuiUtils;
import com.sun.corba.se.impl.orbutil.closure.Constant;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class TreeTableSheetTab extends SheetTab {

	protected TreeItem<ObservableList<String>> root;
	protected TreeTableView<ObservableList<String>> table;
	protected Set<BreakPointTreeTableCell> breakPoints;
	protected Set<String> breakPointsLineNo;
	protected int currentExecutionRow;
	private AtomHandler atomHandler;
	private Font tooltipFont;
	private Label expandAllCheckBox;
	private double toolTipFontSize = 13;
	ContextMenu menu;
	MenuItem addMI;
	MenuItem copyMI;
	MenuItem pasteMI;
	private MacroEvaluator macroEvaluator;
	private ActionHelper actionHelper;
	private AutoCompleteFilter autoCompleteActionFilter;
	private AutoCompleteFilter autoCompleteArgFilter;
	private ArgumentHelper argHelper;
	private int actionArguments=0;
	private int verifyArguments=0;

	//2 for root column and line no. 4 for id,description,property step
	private static int additonalColumns=2+4;

	private EventHandler<CellEditEvent<ObservableList<String>, String>> cellEditEventHandler;

	public TreeTableSheetTab(String string) {

		super(string);
		// TODO Auto-generated constructor stub
		initialize();

	}

	public void expandAllTreeItem(final boolean expand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ObservableList<TreeItem<ObservableList<String>>> ti = root
						.getChildren();
				for (TreeItem<ObservableList<String>> treeItem : ti) {
					treeItem.setExpanded(expand);
				}

			}

		});
	}

	private void initialize() {

		table = new TreeTableView<>();
		breakPointsLineNo = new HashSet<>();
		final ImageView expandAllImageView = new ImageView(
				new Image(
						TreeTableSheetTab.class
						.getResourceAsStream("/com/automature/spark/gui/resources/icons/black/glyph_pointer_right.png")));
		final ImageView collapseAllImageView = new ImageView(
				new Image(
						TreeTableSheetTab.class
						.getResourceAsStream("/com/automature/spark/gui/resources/icons/black/glyph_pointer_down.png")));
		// breakPoints=new HashSet<>();

		expandAllCheckBox = new Label();
		Tooltip tooltip = new Tooltip("Expand all");
		// tooltip.setFont(Font.font(toolTipFontSize));
		expandAllCheckBox.setTooltip(tooltip);
		expandAllCheckBox.setGraphic(expandAllImageView);
		expandAllCheckBox.setAlignment(Pos.BOTTOM_LEFT);
		expandAllCheckBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Label cbox = (Label) event.getSource();
				if (cbox.getGraphic() == expandAllImageView) {
					expandAllTreeItem(true);
					cbox.setGraphic(collapseAllImageView);
					Tooltip tooltip = getTooltip() != null ? getTooltip()
							: new Tooltip("Hide all");

					// tooltip.setFont(Font.font(toolTipFontSize));
					expandAllCheckBox.setTooltip(tooltip);

				} else {
					expandAllTreeItem(false);
					cbox.setGraphic(expandAllImageView);
					Tooltip tooltip = getTooltip() != null ? getTooltip()
							: new Tooltip("Expand all");
					// tooltip.setFont(Font.font(toolTipFontSize));

					expandAllCheckBox.setTooltip(tooltip);
				}
			}
		});
		cellEditEventHandler = new EventHandler<CellEditEvent<ObservableList<String>, String>>() {
			@Override
			public void handle(
					final CellEditEvent<ObservableList<String>, String> event) {

				try {
					final ObservableList<String> item = event.getRowValue()
							.getValue();
					
					int col = event.getTreeTablePosition().getColumn();
					int row = event.getTreeTablePosition().getRow();

					event.getTreeTableView().requestFocus();
					event.getTreeTableView().getFocusModel()
					.focus(event.getTreeTablePosition());
					item.set(col, event.getNewValue());
					getSheetSaver().SaveChange(event.getNewValue(),
							Integer.parseInt(item.get(1)), col);

					// item.setName( event.getNewValue() );

				} catch (Exception e) {
					System.err
					.println("Error updating sheet " + e.getMessage());
				}
			}
		};

		copyMI = new MenuItem("copy");
		pasteMI = new MenuItem("paste");
		menu = new ContextMenu();

	}

	protected void setRowFormatting() {
		table.setRowFactory(new Callback<TreeTableView<ObservableList<String>>, TreeTableRow<ObservableList<String>>>() {
			@Override
			public TreeTableRow<ObservableList<String>> call(
					TreeTableView<ObservableList<String>> tableView) {
				final TreeTableRow<ObservableList<String>> row = new TreeTableRow<ObservableList<String>>() {
					@Override
					public void updateItem(ObservableList<String> rowData,
							boolean empty) {

						// System.out.println("data "+rowData);
						if (rowData != null) {
							super.updateItem(rowData, empty);
							if (rowData.get(2) != null
									&& rowData.get(2).toString()
									.equalsIgnoreCase("comment")) {
								setStyle("-fx-background-color: "
										+ Constants.commentColor + ";");
							} else {
								if (Integer.parseInt((String) rowData.get(1)) % 2 == 0) {
									setStyle("-fx-background-color: "
											+ Constants.treeTableRowColor
											+ ";\n"
											+ "    -fx-background-insets: 0, 0 0 1 0;\n"
											+ "    -fx-padding: 0.0em; /* 0 */\n"
											+ "    -fx-text-fill: -fx-text-inner-color;");
								} else {
									setStyle("-fx-background-color: "
											+ Constants.treeTableRowAltColor
											+ ";\n"
											+ "    -fx-background-insets: 0, 0 0 1 0;");
								}
							}
						}
					}
				};

				return row;
			}
		});
	}
	public void setColDefaultProperties(TreeTableColumn<ObservableList<String>, String> col,final int i){

		if (i == 0 || i == 1) {
			col.setMinWidth(35.0);
			col.setPrefWidth(35.0);
			// col.setMaxWidth(30.0);
		} else {
			col.setMinWidth(75.0);
			col.setPrefWidth(100.0);
			col.setOnEditCommit(cellEditEventHandler);
		}

		col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<String>, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(
					CellDataFeatures<ObservableList<String>, String> p) {
				ObservableList<String> list = p.getValue().getValue();

				if (list != null && i < list.size()) {
					SimpleStringProperty ssp = new SimpleStringProperty(p
							.getValue().getValue().get(i));
					return ssp;
				} else {
					return new SimpleStringProperty("");
				}

			}
		});
		col.setSortable(false);
	}

	protected void addHeaders(List<String> headers) {
		// for(String header:headers){
		for (int i = 0; i < headers.size(); i++) {
			String header = headers.get(i);

			TreeTableColumn<ObservableList<String>, String> col = new TreeTableColumn<>(
					WordUtils.capitalizeFully(header));
			if (i == 1) {
				setColumnFormattingForLine(col);
			} else if (header.endsWith(" ID")) {
				setColumnFormattingForIDs(col);

			} else if (header.equalsIgnoreCase("Action")
					|| header.equalsIgnoreCase("Verify")) {
				setColumnFormattingForActions(col);

			} else if (header.toLowerCase().startsWith("actionarg_")) {
				actionArguments++;
				setColumnFormattingForArgs(col);
			}else if (header.toLowerCase().startsWith("verifyarg_")) {
				verifyArguments++;
				setColumnFormattingForArgs(col);
			} else if (i == 0) {
				setColumnFormattingForFirstLine(col);

			} else {
				setColumnFormattingForOthers(col);
			}
			setColDefaultProperties(col,i);

			table.getColumns().add(col);
		}
	}

	private void setColumnFormattingForOthers(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub
		col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override
			public TextFieldTreeTableCell<ObservableList<String>, String> call(
					final TreeTableColumn<ObservableList<String>, String> p) {
				return new TextFieldTreeTableCell<>(new MyStringConverter());
			}
		});
		col.setEditable(true);
	}

	protected void setColumnFormattingForFirstLine(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub
		col.setGraphic(expandAllCheckBox);
		col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override
			public TreeTableCell<ObservableList<String>, String> call(
					final TreeTableColumn<ObservableList<String>, String> p) {
				return new TreeTableCell<>();
			}
		});
		col.setEditable(false);

	}

	protected void setColumnFormattingForArgs(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub

		col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override
			public TextFieldTreeTableCell<ObservableList<String>, String> call(
					final TreeTableColumn<ObservableList<String>, String> p) {
				return new ArgumentTextFieldTreeTableCell(
						new MyStringConverter(), autoCompleteArgFilter,
						argHelper);

			}
		});
		col.setEditable(true);
		// col.addEventHandler(TreeTableColumn..EDIT_COMMIT_EVENT, arg1);

	}

	@Deprecated
	public String getToolTipForArgs(String item) {
		if (StringUtils.isBlank(item)) {
			return "";
		}
		if (item.startsWith("$")) {// &&!item.startsWith("$$")){
			String tooltip = macroEvaluator
					.getMacroValue(((SheetTabPane) getTabPane())
							.getCurrentSpreadSheet(), item);
			return tooltip.equals(item) ? null : tooltip;// getMacroValue(item);
		} else if (item.contains("=") && item.contains("$")) {
			String[] temp = item.split("=", 2);
			String arg1 = null;
			String arg2 = null;

			if (temp[1].startsWith("$")) {
				// arg1=getMacroValue(temp[0]);
				arg1 = macroEvaluator.getMacroValue(
						((SheetTabPane) getTabPane()).getCurrentSpreadSheet(),
						temp[0]);
			}
			if (temp.length == 2) {
				if (temp[1].startsWith("$")) {
					// arg2= getMacroValue(temp[1]);
					arg2 = macroEvaluator.getMacroValue(
							((SheetTabPane) getTabPane())
							.getCurrentSpreadSheet(), temp[1]);

				}
			}

			return (arg1 != null && arg2 != null) ? arg1 + "=" + arg2
					: (arg1 == null ? arg2 : arg1);

		}

		return null;
	}

	@Deprecated
	public String getMacroValue(String macro) {
		SpreadSheet sp = null;
		String macroSign = macro.startsWith("$$") ? "$$" : "$";
		if (!macro.contains(".")) {

			sp = ((SheetTabPane) getTabPane()).getCurrentSpreadSheet();// SpreadSheet.getUniqueSheets().get(getFileName());

		} else {
			String fileName = macro.substring(1, macro.indexOf("."));
			macro = macroSign + macro.substring(macro.indexOf(".") + 1);
			Iterator it = SpreadSheet.getUniqueSheets().keySet().iterator();
			while (it.hasNext()) {
				String fileUQ = (String) it.next();
				String file = new File(fileUQ).getName();
				if (file != null) {
					if (file.substring(0, file.lastIndexOf("."))
							.equalsIgnoreCase(fileName)) {
						sp = SpreadSheet.getUniqueSheets().get(fileUQ);
						break;
					}
				}
			}
		}
		if (sp != null) {
			return sp.getMacroSheet().getMacroValue(macro.toLowerCase());
		}

		return null;
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

	protected void setColumnFormattingForActions(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub

		col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override
			public TextFieldTreeTableCell<ObservableList<String>, String> call(
					final TreeTableColumn<ObservableList<String>, String> p) {
				TextFieldTreeTableCell<ObservableList<String>, String> tx = new AutoCompleteActionTextFieldTreeTableCell(
						new MyStringConverter(), autoCompleteActionFilter,
						actionHelper);

				return tx;

			}
		});
		col.setEditable(true);

		// col.addEventHandler(TreeTableColumn,ev);

	}

	@Deprecated
	public ExistenceMessageBean getActionMessageBean(String item) {
		if (StringUtils.isBlank(item)) {
			return null;
		}
		if (item.trim().startsWith("&")) {
			boolean includeMolecule = true;
			String moleculeName = item.substring(1);
			SpreadSheet sp = null;
			String fileName = null;
			if (!moleculeName.contains(".")) {
				includeMolecule = false;
				sp = ((SheetTabPane) getTabPane()).getCurrentSpreadSheet();// SpreadSheet.getUniqueSheets().get(getFileName());
			} else {
				fileName = moleculeName.substring(0, moleculeName.indexOf("."));
				moleculeName = moleculeName
						.substring(moleculeName.indexOf(".") + 1);
				sp = SpreadSheet.findSpreadSheet(fileName);
				/*
				 * Iterator it =
				 * SpreadSheet.getUniqueSheets().keySet().iterator(); while
				 * (it.hasNext()) { String fileUQ = (String) it.next(); String
				 * file = new File(fileUQ).getName(); if (file != null) { if
				 * (file.substring(0, file.lastIndexOf("."))
				 * .equalsIgnoreCase(fileName)) { sp =
				 * SpreadSheet.getUniqueSheets().get(fileUQ); break; } } }
				 */
			}
			if (sp != null) {
				ActionInfoBean aib = sp.getMoleculesSheet().moleculeExist(
						moleculeName);
				if (aib != null) {
					ExistenceMessageBean emb = new ExistenceMessageBean(true,
							aib.getArguments());
					return emb;
				} else {
					String message;
					if (includeMolecule) {
						message = "could not find " + moleculeName + " in "
								+ fileName + "sheet";
					} else {
						message = "could not find " + moleculeName
								+ " in this testsuite's molecule sheet";
					}
					ExistenceMessageBean emb = new ExistenceMessageBean(false,
							message);
					return emb;
				}
			}
			return null;
		} else {

			return atomHandler.verifyExistence(item.trim());
		}

	}

	protected void setColumnFormattingForIDs(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub
	}

	protected void setColumnFormattingForLine(
			TreeTableColumn<ObservableList<String>, String> col) {
		// TODO Auto-generated method stub
		col.setCellFactory(new Callback<TreeTableColumn<ObservableList<String>, String>, TreeTableCell<ObservableList<String>, String>>() {
			@Override
			public TreeTableCell<ObservableList<String>, String> call(
					final TreeTableColumn<ObservableList<String>, String> p) {
				BreakPointTreeTableCell bp = new BreakPointTreeTableCell();
				bp.addEventHandler(MouseEvent.MOUSE_CLICKED,
						new BreakPointMouseClickEventHandler());
				return bp;
				/*
				 * return new TreeTableCell<ObservableList<String>, String>() {
				 * 
				 * @Override public void updateItem(String item, boolean empty)
				 * { super.updateItem(item, empty); if (!isEmpty()) {
				 * 
				 * setFont(new Font("Arial", 12)); setText(item); }
				 * 
				 * } };
				 */
			}
		});

		// col.addEventHandler(MouseEvent.MOUSE_CLICKED, n);

	}

	public TreeItem<ObservableList<String>> getLastTreeItem() {
		TreeItem<ObservableList<String>> item = root.getChildren().get(
				root.getChildren().size() - 1);
		if (item.isLeaf()) {
			return item;
		} else {
			return item.getChildren().get(item.getChildren().size() - 1);
		}
	}

	public void addComment() {
		int m = table.getColumns().size();
		TreeItem<ObservableList<String>> lastItem = getLastTreeItem();
		int line = Integer.parseInt(lastItem.getValue().get(1)) - 1;
		line++;
		/*
		 * boolean isEmpty=true; List<String>
		 * obList=lastItem.getValue().subList(2, lastItem.getValue().size());
		 * for(String item:obList){ if(StringUtils.isNotBlank(item)){
		 * //System.out.println("item is not empty "+item); isEmpty=false;
		 * line++; } } if(isEmpty){
		 * lastItem.getParent().getChildren().remove(lastItem); }
		 */
		// root.getChildren().get(root.getChildren().size()-1).getValue().get(1);//table.getRoot().getChildren().get(table.getRoot().getChildren().size()-1).getValue().get(1);
		ObservableList<ObservableList<String>> commentList = convertDataToObservableList(createEmptyList(
				1, m, line));
		commentList.get(0).set(2, "comment");
		getSheetSaver().addCommentRow(line);
		TreeItem<ObservableList<String>> comment = new TreeItem<ObservableList<String>>(
				commentList.get(0));
		root.getChildren().add(comment);
		// table.getRoot().getChildren().add(comment);

	}

	// adding new test case or molecule
	public void addtestCase(int n) {
		addComment();
		int m = table.getColumns().size();
		String line = getLastTreeItem().getValue().get(1);// root.getChildren().get(root.getChildren().size()-1).getValue().get(1);//table.getRoot().getChildren().get(table.getRoot().getChildren().size()-1).getValue().get(1);
		ObservableList<ObservableList<String>> list = convertDataToObservableList(createEmptyList(
				n, m, Integer.parseInt(line)));
		getSheetSaver().addRows(Integer.parseInt(line), m);
		TreeItem<ObservableList<String>> root = new TreeItem<ObservableList<String>>(
				list.get(0));
		for (int i = 1; i < list.size(); i++) {
			TreeItem<ObservableList<String>> testStep = new TreeItem<ObservableList<String>>(
					list.get(i));
			root.getChildren().add(testStep);
		}
		this.root.getChildren().add(root);
		// table.getRoot().getChildren().add(root);
	}

	public void decreaseTeststep(TreeItem<ObservableList<String>> treeItem) {
		if (treeItem != null) {
			ObservableList<String> list = treeItem.getValue();
			try {
				int n = Integer.parseInt(list.get(1));
				n--;
				list.set(1, String.valueOf(n));
			} catch (NumberFormatException nfe) {

			}
			decreaseTeststep(treeItem.nextSibling());
		}
	}

	public void decreaseTeststeps(
			TreeItem<ObservableList<String>> parentTreeItem) {

		if (parentTreeItem != null) {
			ObservableList<String> list = parentTreeItem.getValue();
			try {
				int n = Integer.parseInt(list.get(1));
				n--;
				list.set(1, String.valueOf(n));
				if (parentTreeItem.getChildren().size() > 0) {
					decreaseTeststep(parentTreeItem.getChildren().get(0));
				}

			} catch (NumberFormatException nfe) {

			}
			decreaseTeststeps(parentTreeItem.nextSibling());

		}
	}

	public void increaseTeststep(TreeItem<ObservableList<String>> treeItem) {
		if (treeItem != null) {
			ObservableList<String> list = treeItem.getValue();
			try {
				int n = Integer.parseInt(list.get(1));
				n++;
				list.set(1, String.valueOf(n));
			} catch (NumberFormatException nfe) {

			}
			increaseTeststep(treeItem.nextSibling());
		}
	}

	public void increaseTeststeps(
			TreeItem<ObservableList<String>> parentTreeItem) {

		if (parentTreeItem != null) {
			ObservableList<String> list = parentTreeItem.getValue();
			try {
				int n = Integer.parseInt(list.get(1));
				n++;
				list.set(1, String.valueOf(n));
				if (parentTreeItem.getChildren().size() > 0) {
					increaseTeststep(parentTreeItem.getChildren().get(0));
				}

			} catch (NumberFormatException nfe) {

			}
			increaseTeststeps(parentTreeItem.nextSibling());
		}
	}

	public TreeItem<ObservableList<String>> getEmptyTreeItem(){
		ObservableList<String> list=FXCollections.observableArrayList();
		for(int i=0;i<table.getColumns().size();i++){
			list.add("");
		}
		return new TreeItem<ObservableList<String>>(list);

	}

	public void deleteTestStep(){
		TreeItem<ObservableList<String>> treeItem = table
				.getSelectionModel().getSelectedItem();
		if(treeItem!=null){
			if (treeItem.isLeaf()) {
				int colIndex=Integer.parseInt(treeItem.getValue().get(1));
				TreeItem<ObservableList<String>> parent = treeItem
						.getParent();
				TreeItem<ObservableList<String>> sibling = treeItem
						.nextSibling();
				decreaseTeststep(sibling);
				parent.getChildren().remove(treeItem);
				decreaseTeststeps(parent.nextSibling());
				getSheetSaver().deleteRow(colIndex);
			}else{
				GuiUtils.showMessage("Cannot delete a test case or molecule(first test step)");
			}
		}else{
			GuiUtils.showMessage("Please select a test step to delete");
		}
	}

	public void addTeststep(){
		TreeItem<ObservableList<String>> treeItem = table
				.getSelectionModel().getSelectedItem();
		if(treeItem!=null){

			TreeItem<ObservableList<String>>  newItem=getEmptyTreeItem();
			int colIndex=Integer.parseInt(treeItem.getValue().get(1))+1;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if(getSheetSaver().addRow(colIndex)){
						newItem.getValue().set(1, String.valueOf(colIndex));
						if(!treeItem.isLeaf()){
							int index=0;
							treeItem.getChildren().add(index, newItem);
							increaseTeststep(newItem.nextSibling());
							increaseTeststeps(treeItem.nextSibling());
						}

						else{
							TreeItem<ObservableList<String>> parent = treeItem
									.getParent();
							int index=parent.getChildren().indexOf(treeItem);
							parent.getChildren().add(index+1, newItem);
							increaseTeststep(newItem.nextSibling());
							increaseTeststeps(parent.nextSibling());
						}
					}
				}
			});
		}else{
			GuiUtils.showMessage("Please select a test step first after which test step will be inserted");
		}
	}

	public void addColumn(int position,String columnHeader){


		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				table.setRoot(null);
				// TODO Auto-generated method stub
				try{
					TreeTableColumn<ObservableList<String>, String> col=new TreeTableColumn<>(
							WordUtils.capitalizeFully(columnHeader));


					table.getColumns().add(position, col);
					Iterator<TreeItem<ObservableList<String>>> it=root.getChildren().iterator();
					while(it.hasNext()){
						TreeItem<ObservableList<String>> item=it.next();
						item.getValue().add(position,"");
						Iterator<TreeItem<ObservableList<String>>> childIt=item.getChildren().iterator();
						while(childIt.hasNext()){
							TreeItem<ObservableList<String>> child=childIt.next();
							child.getValue().add(position,"");

						}		
					}
					setColumnFormattingForArgs(col);
					setColDefaultProperties(col,position);

				}catch(Exception e){

				}finally{
					table.setRoot(root);	
					for(int i=position;i<table.getColumns().size();i++){
						TreeTableColumn<ObservableList<String>, String> treeCol=(TreeTableColumn<ObservableList<String>, String>) table.getColumns().get(i);
						setColDefaultProperties(treeCol,i);
					}
					table.requestLayout();
					
				}


			}
		});

	}

	public void addVerifyColumn(){
		verifyArguments++;
		if(getSheetSaver().addColumn(additonalColumns+1+actionArguments+verifyArguments, "Verifyarg_"+verifyArguments)){
			addColumn(additonalColumns+1+actionArguments+verifyArguments, "Verifyarg_"+verifyArguments);
		}
	}

	public void addActionColumn(){
		actionArguments++;
		if(getSheetSaver().addActionColumn(additonalColumns+actionArguments, "Actionarg_"+actionArguments)){
			addColumn(additonalColumns+actionArguments, "Actionarg_"+actionArguments);
		}
		/*TreeTableColumn<ObservableList<String>, String> col=new TreeTableColumn<>(
				WordUtils.capitalizeFully("Actionarg_"+actionArguments));
		table.getColumns().add(additonalColumns+actionArguments, col);
		Iterator<TreeItem<ObservableList<String>>> it=root.getChildren().iterator();
		while(it.hasNext()){
			TreeItem<ObservableList<String>> item=it.next();
			item.getValue().add(additonalColumns+actionArguments,"");
			Iterator<TreeItem<ObservableList<String>>> childIt=item.getChildren().iterator();
			while(childIt.hasNext()){
				TreeItem<ObservableList<String>> child=childIt.next();
				child.getValue().add(additonalColumns+actionArguments,"");

			}		
		}*/
	}



	public void addContextMenu(final String name) {
		ContextMenu menu = new ContextMenu();
		MenuItem addMI = new MenuItem("Add " + name);
		MenuItem addTestStepMI = new MenuItem("Add Test step");
		MenuItem deleteTestStepMI = new MenuItem("Remove Test step");
		MenuItem actionArgMI = new MenuItem("Add action arg column ");
		MenuItem verifyArgMI = new MenuItem("Add verify arg column ");
		menu.getItems().add(addMI);
		menu.getItems().add(addTestStepMI);
		menu.getItems().add(deleteTestStepMI);
		menu.getItems().add(actionArgMI);
		menu.getItems().add(verifyArgMI);

		actionArgMI.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addActionColumn();

			}
		});
		verifyArgMI.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addVerifyColumn();

			}
		});

		deleteTestStepMI.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deleteTestStep();

			}
		});
		addTestStepMI.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {				
				addTeststep();

			}
		});
		table.setContextMenu(menu);
		addMI.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				final Stage dialogStage = new Stage();
				dialogStage.initStyle(StageStyle.UTILITY);
				dialogStage.initModality(Modality.WINDOW_MODAL);
				Pane pane = new Pane();
				pane.setPrefHeight(105.0);
				pane.setPrefWidth(248.0);
				Label label = new Label("Enter Number of test step");
				label.setLayoutX(21.0);
				label.setLayoutY(14.0);

				// label.setAlignment(Pos.BASELINE_CENTER);
				final TextField textField = new TextField("2");
				// textField.setMinWidth(30);
				// textField.setMaxWidth(30);
				textField.setPrefSize(51.0, 17.0);
				textField.setLayoutX(182.0);
				textField.setLayoutY(11.0);

				GuiUtils.addOnlyNumberPropertyInTextField(textField);
				Button yesBtn = new Button("Ok");
				yesBtn.setLayoutX(139.0);
				yesBtn.setLayoutY(63.0);
				yesBtn.setMinWidth(40.0);
				yesBtn.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						dialogStage.close();
						String text = textField.getText();
						if (StringUtils.isNotBlank(text)) {
							int n = Integer.parseInt(text);
							addtestCase(n);
						}

					}
				});
				Button noBtn = new Button("Cancel");
				noBtn.setLayoutX(183.0);
				noBtn.setLayoutY(63.0);
				noBtn.setMinWidth(40.0);

				noBtn.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						dialogStage.close();

					}
				});

				pane.getChildren().addAll(label, textField, yesBtn, noBtn);
				pane.effectProperty().set(new Blend());
				pane.setStyle("-fx-background-color: white;");

				pane.setVisible(true);
				dialogStage.setScene(new Scene(pane));
				dialogStage.show();
			}
		});
	}

	public void loadTabData(List<String> headers, List<List<String>> data) {

		atomHandler = new AtomHandler(SpreadSheet.getScriptLocations());
		actionHelper = new ActionHelper(atomHandler, getFileName());
		autoCompleteActionFilter = new AutoCompleteActionFilter(actionHelper);
		macroEvaluator = new MacroEvaluator(getFileName());
		argHelper = new ArgumentHelper(getFileName());
		autoCompleteArgFilter = new AutoCompleteArgFilter(argHelper);
		addHeaders(headers);
		ObservableList<ObservableList<String>> list = convertDataToObservableList(data);
		addData(list);
		setRowFormatting();
		table.setRoot(root);
		table.setVisible(true);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.getSelectionModel().setCellSelectionEnabled(true);
		table.setEditable(true);
		table.setShowRoot(false);
		table.setTableMenuButtonVisible(true);

		// table.setTreeColumn(table.getColumns().get(1));
		setContent(table);

	}

	protected void addData(ObservableList<ObservableList<String>> data) {

		root = new TreeItem<ObservableList<String>>();

		TreeItem<ObservableList<String>> comment;
		if (data.size() > 0) {
			comment = new TreeItem<>();
			comment.setValue(data.get(0));
			// root.setValue(data.get(0));
			root.getChildren().add(comment);
		} else {
			return;
		}
		if (data.size() < 2) {
			return;
		}
		TreeItem<ObservableList<String>> tc = new TreeItem<>(data.get(1));
		// tc.expandedProperty().bind(expandAllCheckBox.selectedProperty());
		boolean isComment = false;
		for (int i = 2; i < data.size(); i++) {
			ObservableList<String> list = data.get(i);
			if (StringUtils.isBlank(list.get(2))) {
				tc.getChildren()
				.add(new TreeItem<ObservableList<String>>(list));
			} else {

				if (list.get(2).equalsIgnoreCase("comment")) {
					comment = new TreeItem<ObservableList<String>>(list);
					isComment = true;
				} else {
					root.getChildren().add(tc);
					if (isComment) {
						root.getChildren().add(comment);
						isComment = false;
					}
					tc = new TreeItem<ObservableList<String>>(list);
					// tc.expandedProperty().bind(expandAllCheckBox.selectedProperty());
				}
			}

		}
		root.getChildren().add(tc);
		root.setExpanded(true);

	}

	public void removeAllBreakPoints() {

		breakPointsLineNo.clear();
		table.setRoot(null);
		table.layout();
		table.setRoot(root);
		/*
		 * Iterator<BreakPointTreeTableCell> it=breakPoints.iterator();
		 * while(it.hasNext()){ BreakPointTreeTableCell tc=it.next();
		 * tc.clearBreakPoints();
		 * 
		 * } breakPoints.clear();
		 */

	}

	protected void setBreakPoint(String nameSpace, String row) {
		try {
			Spark.setBreakPoint(nameSpace, String.valueOf(row));
		} catch (Exception e) {

		}
	}

	protected void UnSetBreakPoint(String nameSpace, String row) {
		Object[] rows = new Object[1];
		rows[0] = String.valueOf(row);
		Spark.removeBreakPoints(nameSpace, rows);
	}

	public void clearSelection() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				table.requestFocus();
				table.getSelectionModel().clearSelection();
				table.getFocusModel();
				// table.getSelectionModel().select(0);
				// table.getFocusModel().focus(0);
			}
		});
	}

	protected class BreakPointTreeTableCell extends
	TreeTableCell<ObservableList<String>, String> {

		public BreakPointTreeTableCell() {
			super();
			// TODO Auto-generated constructor stub
		}

		private boolean breakPoint = false;

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			}
			// if (!isEmpty()) {
			else {
				TreeTableRow<ObservableList<String>> row = getTreeTableRow();
				// System.out.println("row "+row.getTreeItem());
				setFont(new Font("Arial", 12));
				setText(getString());
				// System.out.println(breakPointsLineNo+"\t"+item);

				setAlignment(Pos.CENTER_RIGHT);
				if (breakPointsLineNo.contains(item)) {
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					setGraphic(new ImageView(
							new Image(
									getClass()
									.getResourceAsStream(
											"/com/automature/spark/gui/resources/icons/black/glyph_breakpoint.png"))));
				} else {
					setGraphic(null);
					setContentDisplay(ContentDisplay.TEXT_ONLY);
				}
			}

		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}

		public void handleBreakPoint() {

			final String nameSpace = FilenameUtils.removeExtension(
					new File(getFileName()).getName()).toLowerCase();
			if (breakPointsLineNo.contains(getItem())) {
				breakPointsLineNo.remove(getItem());
				UnSetBreakPoint(nameSpace, getText());
			} else {
				breakPointsLineNo.add(getItem());
				setBreakPoint(nameSpace, getText());
			}
			/*
			 * if(breakPoint){ breakPoint=false; breakPoints.remove(this);
			 * UnSetBreakPoint(nameSpace, getText());
			 * 
			 * }else{ breakPoint=true; setBreakPoint(nameSpace, getText());
			 * breakPoints.add(this);
			 * 
			 * }
			 */
			updateItem(getItem(), isEmpty());
		}

		public void clearBreakPoints() {
			if (breakPoint) {
				breakPointsLineNo.remove(getItem());
				updateItem(getItem(), isEmpty());
			}
		}

	}

	public void setCurrentTestCase(int n) {
		currentExecutionRow = n;
	}

	public void setAndExpandCurrentTestCase(final String id) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ObservableList<TreeItem<ObservableList<String>>> ti = root
						.getChildren();
				for (TreeItem<ObservableList<String>> treeItem : ti) {
					if (treeItem.getValue().get(2).equalsIgnoreCase(id)) {
						treeItem.setExpanded(true);
						setCurrentTestCase(table.getRow(treeItem));
					}
				}
			}

		});

	}

	public void expandCurrentTestCase(final String id) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ObservableList<TreeItem<ObservableList<String>>> ti = root
						.getChildren();
				for (TreeItem<ObservableList<String>> treeItem : ti) {
					if (treeItem.getValue().get(2).equalsIgnoreCase(id)) {
						treeItem.setExpanded(true);
						int n = table.getRow(treeItem);

						selectRow(n);
					}
				}
			}

		});

	}

	public void selectRow(final int n) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				table.requestFocus();
				table.scrollTo(n);
				table.getSelectionModel().select(n, table.getColumns().get(0));
				table.getFocusModel().focus(n, table.getColumns().get(0));

			}

		});
	}

	public void highLightRow(final int n) {

		// final TableColumn tc=((TableColumn)tableView.getColumns().get(0));
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				table.requestFocus();
				table.scrollTo(n > 8 ? n - 8 : 0);
				table.getSelectionModel().select(currentExecutionRow,
						table.getColumns().get(0));
				table.getFocusModel().focus(currentExecutionRow,
						table.getColumns().get(0));
				currentExecutionRow++;
			}
		});
	}

	protected class BreakPointMouseClickEventHandler implements
	EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			// TODO Auto-generated method stub

			if (event.getClickCount() == 1) {
				BreakPointTreeTableCell breakPointCell = (BreakPointTreeTableCell) event
						.getSource();
				// breakPointsLineNo.add(breakPointCell.getText());
				breakPointCell.handleBreakPoint();
				event.consume();

			}
		}
	}

}
