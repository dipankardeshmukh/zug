/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.gui.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.gui.components.FloatingStage;
import com.automature.spark.util.Log;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author skhan
 */
public class ConsoleController implements Initializable {

	/**
	 * Initializes the controller class.
	 */
	@FXML
	private TextFlow console;
	@FXML
	private Button clearButton;
	@FXML
	private ScrollPane scrollPane;

	@FXML
	private TextField searchTextBox;
	@FXML
	private Button searchButton;
	@FXML
	private Button formatButton;
	@FXML
	private AnchorPane consoleHolder;
	@FXML
	private VBox consoleVbox;
	@FXML
	private Button copyAllButton;

	private TextArea textArea;

	private boolean format = true;
	private int start = 0;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
		try {

			redirectSystemStreams();
			textArea = new TextArea();
			textArea.setEditable(false);
			textArea.setPrefSize(580, 360);
			console.heightProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> arg0,
						Number arg1, Number arg2) {
					// TODO Auto-generated method stub
					scrollPane.setVvalue(scrollPane.getVmax());
					textArea.setPrefHeight(console.getHeight());

				}

			});
			console.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> arg0,
						Number arg1, Number arg2) {
					// TODO Auto-generated method stub

					textArea.setPrefWidth(console.getWidth());

				}

			});
			searchTextBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent ke) {
					if (ke.getCode().equals(KeyCode.ENTER)) {
						searchText();
					} else {
						resetSearchField();
					}
				}
			});

		} catch (Exception e) {
			System.err.println("Error : Initializing Console.\nError message  "
					+ e.getMessage() + "\t\nError Trace :" + e.getStackTrace());
		}
		// console = (ConsoleController) fxmlLoader.getController();
	}

	public void searchText() {
		// TODO Auto-generated method stub
		String searchString = searchTextBox.getText();
		if(StringUtils.isNotBlank(searchString)){
			int length = textArea.getLength();
			String text = textArea.getText();
			start = text.indexOf(searchString, start);
			if (start < 0) {
				start = 0;
			} else {
				textArea.selectRange(start, start + searchString.length());
				start += searchString.length();
			}
		}
	}

	protected void resetSearchField() {
		// TODO Auto-generated method stub
		textArea.deselect();
		start = 0;
	}

	private void appendText(Text text) {
		console.getChildren().add(text);
		if (!format) {
			textArea.appendText(text.getText());
		}
	}

	private void formatText(String txt, String subStringToMatch, Color color,
			String splitPatter, String actionType) {

		int index = 0;

		String name = txt.replace(subStringToMatch, "");
		name = name.replace("\n", "");
		String[] sections = name.split(splitPatter);

		if (actionType.equalsIgnoreCase("Molecule"))
			index = sections.length - 1;

		/*
		 * SimpleAttributeSet keyWord = new SimpleAttributeSet();
		 * StyleConstants.setForeground(keyWord, color);
		 * StyleConstants.setBold(keyWord, true);
		 */
		Font font = Font.font("Arial", FontWeight.BOLD, 12);
		Text text = new Text(name);
		text.setFont(font);
		text.setFill(color);

		appendText(new Text("\n"));

		if (actionType.equalsIgnoreCase("Molecule"))
			for (int i = 0; i < StringUtils.countMatches(name, "&"); i++)
				appendText(new Text("\t"));

		appendText(new Text(subStringToMatch + " "));

		if (actionType.equalsIgnoreCase("Testsuite")) {
			appendText(text);
		} else {
			Text text1 = new Text(sections[index]);
			text1.setFont(font);
			text1.setFill(color);
			appendText(text1);
		}

		if (!actionType.equalsIgnoreCase("Molecule")) {
			Text text2 = new Text(splitPatter + sections[1] + "\n");
			text2.setFont(font);
			text2.setFill(color);
			appendText(text2);
		}

	}

	private void format(String text) {

		if (text.replace("\n", "").startsWith(
				"Total time taken to initialize :")) {

			formatText(text, "Total time taken to initialize :",
					Color.rgb(12, 202, 66), "", "Testsuite");

		} else if (text
				.replace("\n", "")
				.startsWith(
						"Total time taken to execute all the test cases (End to End) :")) {

			formatText(
					text,
					"Total time taken to execute all the test cases (End to End) :",
					Color.rgb(12, 202, 66), "", "Testsuite");

		} else if (text.replace("\n", "").startsWith(
				"Reading the TestCases Input Sheet")) {

			formatText(text, "Reading the TestCases Input Sheet",
					Color.rgb(0x05, 0x88, 0xCA), "", "Testsuite");

		} else if (text.replace("\n", "").startsWith(
				"SUCCESSFULLY Read the TestCases Input Sheet")) {

			formatText(text, "SUCCESSFULLY Read the TestCases Input Sheet",
					Color.rgb(0x05, 0x88, 0xCA), "", "Testsuite");

		} else if (text.replace("\n", "").startsWith("External sheets :")) {

			formatText(text, "External sheets", Color.rgb(0x05, 0x88, 0x77),
					"", "Testsuite");

		} else if (text.replace("\n", "").startsWith("Running TestCase ID")) {

			formatText(text, "Running TestCase ID",
					Color.rgb(0x05, 0xCA, 0xCA), "On", "Testcase");

		} else if (text.replace("\n", "").startsWith("Running Molecule :")) {

			formatText(text, "Running Molecule :", Color.rgb(0x72, 0x05, 0xE2),
					"&", "Molecule");

		} else if (text.replace("\n", "").matches(
				".*(Execution STARTED With).*")) {

			String[] name = new String[0];
			String actionType = null;

			if (text.contains("Action")) {
				name = text.split("Action");
				actionType = "Action";
			} else if (text.contains("Verification")) {
				name = text.split("Verification");
				actionType = "Verification";
			}

			String[] atomName = name[1]
					.split("Execution STARTED With Arguments");

			/*
			 * SimpleAttributeSet AtomColor = new SimpleAttributeSet();
			 * StyleConstants .setForeground(AtomColor, Color.rgb(0xBD, 0x05,
			 * 0xE2)); StyleConstants.setBold(AtomColor, true);
			 * 
			 * SimpleAttributeSet ArgColor = new SimpleAttributeSet();
			 * StyleConstants.setForeground(ArgColor, Color.rgb(0xCB, 0xCB,
			 * 0x03)); StyleConstants.setBold(ArgColor, true);
			 */

			Font font = Font.font("Arial", FontWeight.BOLD, 12);
			Text argtext = new Text(atomName[1]);
			argtext.setFont(font);
			argtext.setFill(Color.rgb(0xCB, 0xCB, 0x03));
			Text atomText = new Text(atomName[0]);
			atomText.setFont(font);
			atomText.setFill(Color.rgb(0xBD, 0x05, 0xE2));

			for (int i = 0; i < StringUtils.countMatches(name[0], "&"); i++) {
				appendText(new Text("\t"));
			}
			appendText(new Text(actionType + " "));
			appendText(atomText);
			appendText(new Text("Execution STARTED With Arguments"));
			appendText(argtext);

		} else if (text.replace("\n", "").contains("SUCCESSFULLY Executed")) {

			String[] name = new String[0];
			String actionType = null;

			if (text.contains("Action")) {
				name = text.split("Action");
				actionType = "Action";
			} else if (text.contains("Verification")) {
				name = text.split("Verification");
				actionType = "Verification";
			}

			String atomName = name[1].replace("SUCCESSFULLY Executed", "");

			/*
			 * SimpleAttributeSet AtomColor = new SimpleAttributeSet();
			 * StyleConstants .setForeground(AtomColor, );
			 * StyleConstants.setBold(AtomColor, true);
			 */
			Font font = Font.font("Arial", FontWeight.BOLD, 12);
			Text atomText = new Text(atomName);
			atomText.setFont(font);
			atomText.setFill(Color.rgb(0xBD, 0x05, 0xE2));

			for (int i = 0; i < StringUtils.countMatches(name[0], "&"); i++) {
				appendText(new Text("\t"));
			}
			appendText(new Text(actionType));
			appendText(atomText);
			appendText(new Text("SUCCESSFULLY Executed "));

		} else if (text.startsWith("Molecule Execution Finished :")) {

			formatText(text, "Molecule Execution Finished :",
					Color.rgb(0x72, 0x05, 0xE2), "&", "Molecule");

		} else if (text.replace("\n", "").startsWith(
				"STATUS : PASS For TestCase ID")) {

			formatText(text, "STATUS : PASS For TestCase ID",
					Color.rgb(0x05, 0xCA, 0xCA), "On", "Testcase");

		} else if (text.replace("\n", "").startsWith(
				"STATUS : FAIL For TestCase ID")) {

			formatText(text, "STATUS : FAIL For TestCase ID",
					Color.rgb(0x05, 0xCA, 0xCA), "On", "Testcase");

		} else {

			if (text.replace("\n", "").startsWith("*"))
				text = text.replace("*", "");
			appendText(new Text(text));

		}
	}

	private void updateTextAreaWithOutput(final String value) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (format) {
					try {
						format(value);
					} catch (Exception e) {
						appendText(new Text(value));
					}
				} else {
					appendText(new Text(value));
				}
			}
		});
	}

	private void updateTextAreaWithError(final String value) {
		Platform.runLater(new Runnable() {
			public void run() {
				Text error = new Text(value);
				error.setFill(Color.RED);
				appendText(error);
			}
		});
	}

	public void redirectSystemStreams() {

		OutputStream out = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				updateTextAreaWithOutput(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextAreaWithOutput(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		OutputStream err = new OutputStream() {

			@Override
			public void write(int b) throws IOException {

				updateTextAreaWithError(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextAreaWithError(new String(b, off, len));

			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(err, true));
	}

	public void clear() {
		console.getChildren().clear();
		textArea.clear();
	}

	@Deprecated
	public void switchText() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				format = format == true ? false : true;
				// TODO Auto-generated method stub
				if (!format) {
					disableControls(format);
					textArea.clear();
					ObservableList titlePanes = console
							.getChildrenUnmodifiable();
					titlePanes.forEach(p -> textArea.appendText(((Text) p)
							.getText()));
					// textArea.setText(console.getChildrenUnmodifiable().toString());
					consoleVbox.getChildren().remove(scrollPane);
					consoleVbox.getChildren().add(textArea);
				} else {
					disableControls(format);
					consoleVbox.getChildren().remove(textArea);
					consoleVbox.getChildren().add(scrollPane);
					textArea.clear();
				}

			}
		});
	}

	public void switchToSimpleConsole() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// format=format==true?false:true;
				// TODO Auto-generated method stub
				if (!consoleVbox.getChildren().contains(textArea)) {
					disableControls(false);
					textArea.clear();
					ObservableList titlePanes = console
							.getChildrenUnmodifiable();
					titlePanes.forEach(p -> textArea.appendText(((Text) p)
							.getText()));
					// textArea.setText(console.getChildrenUnmodifiable().toString());
					consoleVbox.getChildren().remove(scrollPane);
					consoleVbox.getChildren().add(textArea);
				}

			}
		});
	}

	public void switchToFormattedConsole() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				if (!consoleVbox.getChildren().contains(scrollPane)) {
					disableControls(true);

					consoleVbox.getChildren().remove(textArea);
					consoleVbox.getChildren().add(scrollPane);
					textArea.clear();
				}
			}
		});
	}

	private void disableControls(final boolean b) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				searchTextBox.setDisable(b);
				searchTextBox.setVisible(!b);
				copyAllButton.setDisable(b);
				copyAllButton.setVisible(!b);
				searchButton.setDisable(b);
				searchButton.setVisible(!b);
			}
		});

	}

	public void copy(){
		textArea.selectAll();
		textArea.copy();
	}
}
