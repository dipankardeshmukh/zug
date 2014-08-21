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

import com.automature.spark.util.Log;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
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
	private boolean format=true;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
		clearButton.setTooltip(new Tooltip("Clear Console"));
		redirectSystemStreams();

		    console.heightProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> arg0,
						Number arg1, Number arg2) {
					// TODO Auto-generated method stub
					scrollPane.setVvalue(scrollPane.getVmax());
				}
		    	
			});
		    

		// console = (ConsoleController) fxmlLoader.getController();
	}
	
	private void formatText(String txt, String subStringToMatch, Color color,
			String splitPatter, String actionType) {

		int index = 0;

		String name = txt.replace(subStringToMatch, "");
		name = name.replace("\n", "");
		String[] sections = name.split(splitPatter);

		if (actionType.equalsIgnoreCase("Molecule"))
			index = sections.length - 1;

		/*	SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, color);
		StyleConstants.setBold(keyWord, true);*/
		Font font=Font.font("Arial", FontWeight.BOLD, 12);
		Text text=new Text(name);
		text.setFont(font);
		text.setFill(color);



		console.getChildren().add(new Text("\n"));

		if (actionType.equalsIgnoreCase("Molecule"))
			for (int i = 0; i < StringUtils.countMatches(name, "&"); i++)
				console.getChildren().add(new Text("\t"));

		console.getChildren().add(new Text(	subStringToMatch + " "));

		if (actionType.equalsIgnoreCase("Testsuite")) {
			console.getChildren().add(text);
		} else {
			Text text1=new Text(sections[index]);
			text1.setFont(font);
			text1.setFill(color);
			console.getChildren().add(text1);
		}

		if (!actionType.equalsIgnoreCase("Molecule")){
			Text text2=new Text(splitPatter + sections[1] + "\n");
			text2.setFont(font);
			text2.setFill(color);
			console.getChildren().add(text2);
		}


	}

	private void format(String text) {

		if (text.replace("\n", "").startsWith(
				"Total time taken to initialize :")) {

			formatText(text, "Total time taken to initialize :", Color.rgb(12,
					202, 66), "", "Testsuite");

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

			formatText(text, "Reading the TestCases Input Sheet", Color.rgb(
					0x05, 0x88, 0xCA), "", "Testsuite");

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

		/*	SimpleAttributeSet AtomColor = new SimpleAttributeSet();
			StyleConstants
			.setForeground(AtomColor, Color.rgb(0xBD, 0x05, 0xE2));
			StyleConstants.setBold(AtomColor, true);

			SimpleAttributeSet ArgColor = new SimpleAttributeSet();
			StyleConstants.setForeground(ArgColor, Color.rgb(0xCB, 0xCB, 0x03));
			StyleConstants.setBold(ArgColor, true);*/
			
			Font font=Font.font("Arial", FontWeight.BOLD, 12);
			Text argtext=new Text( atomName[1]);
			argtext.setFont(font);
			argtext.setFill(Color.rgb(0xCB, 0xCB, 0x03));
			Text atomText=new Text(atomName[0]);
			atomText.setFont(font);
			atomText.setFill(Color.rgb(0xBD, 0x05, 0xE2));
			
			
				for (int i = 0; i < StringUtils.countMatches(name[0], "&"); i++){
					console.getChildren().add(new Text( "\t"));
				}
				console.getChildren().add(new Text(actionType + " "));
				console.getChildren().add(atomText);
				console.getChildren().add(new Text( "Execution STARTED With Arguments"));
				console.getChildren().add(argtext);
			

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

		/*	SimpleAttributeSet AtomColor = new SimpleAttributeSet();
			StyleConstants
			.setForeground(AtomColor, );
			StyleConstants.setBold(AtomColor, true);
			*/
			Font font=Font.font("Arial", FontWeight.BOLD, 12);
			Text atomText=new Text( atomName);
			atomText.setFont(font);
			atomText.setFill(Color.rgb(0xBD, 0x05, 0xE2));
			
			
				for (int i = 0; i < StringUtils.countMatches(name[0], "&"); i++){
					console.getChildren().add(new Text( "\t"));
				}
				console.getChildren().add(new Text(actionType));
				console.getChildren().add(atomText);
				console.getChildren().add(new Text( "SUCCESSFULLY Executed "));

			

		} else if (text.startsWith("Molecule Execution Finished :")) {

			formatText(text, "Molecule Execution Finished :", Color.rgb(0x72,
					0x05, 0xE2), "&", "Molecule");

		} else if (text.replace("\n", "").startsWith(
				"STATUS : PASS For TestCase ID")) {

			formatText(text, "STATUS : PASS For TestCase ID", Color.rgb(0x05,
					0xCA, 0xCA), "On", "Testcase");

		} else if (text.replace("\n", "").startsWith(
				"STATUS : FAIL For TestCase ID")) {

			formatText(text, "STATUS : FAIL For TestCase ID", Color.rgb(0x05,
					0xCA, 0xCA), "On", "Testcase");

		} else {
		
				if (text.replace("\n", "").startsWith("*"))
					text = text.replace("*", "");
				console.getChildren().add(new Text( text));
		
		}
	}

	private void updateTextAreaWithOutput(final String value){
		Platform.runLater(new Runnable() {
			public void run() {
				if(format){
					try{
						format(value);
					}catch(Exception e){
						console.getChildren().add(new Text(value));
					}
				}else{
					console.getChildren().add(new Text( value));
				}
			}
		});
	}

	private void updateTextAreaWithError(final String value){
		Platform.runLater(new Runnable() {
			public void run() {
				Text error=new Text(value);
				error.setFill(Color.RED);
				console.getChildren().add(error);
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
				updateTextAreaWithError(	new String(b, off, len));

			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(err, true));
	}

	public void clear(){
		console.getChildren().clear();
	}

	
}
