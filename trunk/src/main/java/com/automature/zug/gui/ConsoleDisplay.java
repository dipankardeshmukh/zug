package com.automature.zug.gui;

import com.automature.zug.util.Log;

import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.text.*;

public class ConsoleDisplay {

	private JPanel outputPanel;

	// private static JTextArea textArea= new JTextArea();
	private JTextPane textPane;
	private JTextArea textArea;

	boolean formatOutput = true;

	ConsoleDisplay() {

		outputPanel = new JPanel();
		outputPanel.setBackground(Color.black);
		outputPanel.setLocation(0, 300);
		outputPanel.setPreferredSize(new Dimension(767, 278));
		outputPanel.setLayout(new BorderLayout(0, 0));
		initializeDisplay();

	}

	public void initializeDisplay() {

		outputPanel.removeAll();
		if (formatOutput) {
			textPane = new JTextPane();
			JScrollPane scrollPane = new JScrollPane();
			outputPanel.add(scrollPane);
			scrollPane.setAutoscrolls(true);
			scrollPane.setViewportView(textPane);
			textPane.setForeground(Color.white);
			textPane.setBackground(Color.black);
			// textPane.setOpaque(false);
			textPane.setFont(Font.getFont(Font.SANS_SERIF));
			textPane.setEditable(false);
			textPane.setAutoscrolls(true);
			DefaultCaret caret = (DefaultCaret) textPane.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		} else {
			textArea = new JTextArea();
			JScrollPane scrollPane = new JScrollPane();
			outputPanel.add(scrollPane);
			scrollPane.setAutoscrolls(true);
			scrollPane.setViewportView(textArea);
			textArea.setForeground(Color.white);
			textArea.setBackground(Color.black);
			// textPane.setOpaque(false);
			textArea.setFont(Font.getFont(Font.SANS_SERIF));
			textArea.setEditable(false);
			textArea.setAutoscrolls(true);

			DefaultCaret caret = (DefaultCaret) textArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		}
		outputPanel.revalidate();
		outputPanel.repaint();
	}

	public void setFormatOutput(boolean value) {
		formatOutput = value;
	}

	public void clearDisplay() {
		if (textPane.isVisible() && textPane.isShowing()) {
			textPane.setDocument(new DefaultStyledDocument());
		} else {
			textArea.setText("");
		}

	}

	public JPanel getConsole() {
		return outputPanel;
	}

	public JTextPane getConsoleDisplay() {
		return textPane;
	}

	private void formatText(String txt, String subStringToMatch, Color color,
			String splitPatter, String actionType) {

		int index = 0;

		String name = txt.replace(subStringToMatch, "");
		name = name.replace("\n", "");
		String[] sections = name.split(splitPatter);

		if (actionType.equalsIgnoreCase("Molecule"))
			index = sections.length - 1;

		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, color);
		StyleConstants.setBold(keyWord, true);
		try {

			textPane.getStyledDocument().insertString(
					textPane.getStyledDocument().getLength(), "\n", null);

			if (actionType.equalsIgnoreCase("Molecule"))
				for (int i = 0; i < StringUtils.countMatches(name, "&"); i++)
					textPane.getStyledDocument().insertString(
							textPane.getStyledDocument().getLength(), "\t",
							null);

			textPane.getStyledDocument().insertString(
					textPane.getStyledDocument().getLength(),
					subStringToMatch + " ", null);

			if (actionType.equalsIgnoreCase("Testsuite")) {
				textPane.getStyledDocument()
						.insertString(textPane.getStyledDocument().getLength(),
								name, keyWord);
			} else {
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(),
						sections[index], keyWord);
			}

			if (!actionType.equalsIgnoreCase("Molecule"))
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(),
						splitPatter + sections[1] + "\n", null);

		} catch (BadLocationException e) {
			Log.Error(e.getMessage()); // To change body of catch statement use
										// File | Settings | File Templates.
		}
	}

	private void format(String text) {

		if (text.replace("\n", "").startsWith(
				"Total time taken to initialize :")) {

			formatText(text, "Total time taken to initialize :", new Color(12,
					202, 66), "", "Testsuite");

		} else if (text
				.replace("\n", "")
				.startsWith(
						"Total time taken to execute all the test cases (End to End) :")) {

			formatText(
					text,
					"Total time taken to execute all the test cases (End to End) :",
					new Color(12, 202, 66), "", "Testsuite");

		} else if (text.replace("\n", "").startsWith(
				"Reading the TestCases Input Sheet")) {

			formatText(text, "Reading the TestCases Input Sheet", new Color(
					0x05, 0x88, 0xCA), "", "Testsuite");

		} else if (text.replace("\n", "").startsWith(
				"SUCCESSFULLY Read the TestCases Input Sheet")) {

			formatText(text, "SUCCESSFULLY Read the TestCases Input Sheet",
					new Color(0x05, 0x88, 0xCA), "", "Testsuite");

		} else if (text.replace("\n", "").startsWith("External sheets :")) {

			formatText(text, "External sheets", new Color(0x05, 0x88, 0x77),
					"", "Testsuite");

		} else if (text.replace("\n", "").startsWith("Running TestCase ID")) {

			formatText(text, "Running TestCase ID",
					new Color(0x05, 0xCA, 0xCA), "On", "Testcase");

		} else if (text.replace("\n", "").startsWith("Running Molecule :")) {

			formatText(text, "Running Molecule :", new Color(0x72, 0x05, 0xE2),
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

			SimpleAttributeSet AtomColor = new SimpleAttributeSet();
			StyleConstants
					.setForeground(AtomColor, new Color(0xBD, 0x05, 0xE2));
			StyleConstants.setBold(AtomColor, true);

			SimpleAttributeSet ArgColor = new SimpleAttributeSet();
			StyleConstants.setForeground(ArgColor, new Color(0xCB, 0xCB, 0x03));
			StyleConstants.setBold(ArgColor, true);

			try {
				for (int i = 0; i < StringUtils.countMatches(name[0], "&"); i++)
					textPane.getStyledDocument().insertString(
							textPane.getStyledDocument().getLength(), "\t",
							null);
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(),
						actionType + " ", null);
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(), atomName[0],
						AtomColor);
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(),
						"Execution STARTED With Arguments", null);
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(), atomName[1],
						ArgColor);
			} catch (BadLocationException e) {
				Log.Error(e.getMessage());
			}

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

			SimpleAttributeSet AtomColor = new SimpleAttributeSet();
			StyleConstants
					.setForeground(AtomColor, new Color(0xBD, 0x05, 0xE2));
			StyleConstants.setBold(AtomColor, true);

			try {
				for (int i = 0; i < StringUtils.countMatches(name[0], "&"); i++)
					textPane.getStyledDocument().insertString(
							textPane.getStyledDocument().getLength(), "\t",
							null);
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(), actionType,
						null);
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(), atomName,
						AtomColor);
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(),
						"SUCCESSFULLY Executed ", null);

			} catch (BadLocationException e) {
				Log.Error(e.getMessage()); // To change body of catch statement
											// use File | Settings | File
											// Templates.
			}

		} else if (text.startsWith("Molecule Execution Finished :")) {

			formatText(text, "Molecule Execution Finished :", new Color(0x72,
					0x05, 0xE2), "&", "Molecule");

		} else if (text.replace("\n", "").startsWith(
				"STATUS : PASS For TestCase ID")) {

			formatText(text, "STATUS : PASS For TestCase ID", new Color(0x05,
					0xCA, 0xCA), "On", "Testcase");

		} else if (text.replace("\n", "").startsWith(
				"STATUS : FAIL For TestCase ID")) {

			formatText(text, "STATUS : FAIL For TestCase ID", new Color(0x05,
					0xCA, 0xCA), "On", "Testcase");

		} else {
			try {
				if (text.replace("\n", "").startsWith("*"))
					text = text.replace("*", "");
				textPane.getStyledDocument().insertString(
						textPane.getStyledDocument().getLength(), text, null);
			} catch (BadLocationException e) {
				Log.Error(e.getMessage()); // To change body of catch statement
											// use File | Settings | File
											// Templates.
			}
		}
	}

	public void updateTextArea(String text) {
		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {

		if (formatOutput) {
			try {
				format(text);
			} catch (Exception e) {
				try {
					// textPane.getStyledDocument().insertString(textPane.getStyledDocument().getLength(),"Display Error"+e.getMessage()+" "+e.getCause(),
					// null);
					textPane.getStyledDocument().insertString(
							textPane.getStyledDocument().getLength(), text,
							null);
				} catch (Exception ex) {
					// text
				}
			}

		} else {

			// textPane.getStyledDocument().insertString(textPane.getStyledDocument().getLength(),
			// text, null);
			textArea.append(text);

		}

		// });
	}

	public void redirectSystemStreams() {

		OutputStream out = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		OutputStream err = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				SimpleAttributeSet errColor = new SimpleAttributeSet();
				if (formatOutput)
					StyleConstants.setForeground(errColor, Color.RED);
				StyleConstants.setBold(errColor, true);
				try {
					textPane.getStyledDocument().insertString(
							textPane.getStyledDocument().getLength(),
							String.valueOf((char) b), errColor);
				} catch (BadLocationException e) {
					Log.Error(e.getMessage());
				}
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				SimpleAttributeSet errColor = new SimpleAttributeSet();
				if (formatOutput)
					StyleConstants.setForeground(errColor, Color.RED);
				StyleConstants.setBold(errColor, true);
				try {
					textPane.getStyledDocument().insertString(
							textPane.getStyledDocument().getLength(),
							new String(b, off, len), errColor);
				} catch (BadLocationException e) {
					Log.Error(e.getMessage());
				}
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(err, true));
	}

	public void resetConsole() {
		initializeDisplay();
	}
}
