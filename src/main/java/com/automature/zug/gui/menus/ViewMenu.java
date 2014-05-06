package com.automature.zug.gui.menus;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.automature.zug.engine.SysEnv;
import com.automature.zug.gui.ViewAtoms;
import com.automature.zug.gui.ZugGUI;

public class ViewMenu {

	private JMenu mnView;
	private JCheckBoxMenuItem chckbxmntmConsole;
	private JCheckBoxMenuItem chckbxmntmTestSuite;
	private JCheckBoxMenuItem chckbxmntmCommandLine;
	private JCheckBoxMenuItem chckbxmntmDebugger;
	private JCheckBoxMenuItem chckbxmntmBreakPoints;
	private JCheckBoxMenuItem formatOutput;
	private JMenuItem chckbxmntmAtoms;
	private JMenuItem chckbxmntmLogs;

	public ViewMenu(){

		mnView = new JMenu("View");

		chckbxmntmConsole = new JCheckBoxMenuItem("Console");
		chckbxmntmConsole.setSelected(true);
		mnView.add(chckbxmntmConsole);


		chckbxmntmTestSuite = new JCheckBoxMenuItem("Test Suite");
		mnView.add(chckbxmntmTestSuite);


		chckbxmntmCommandLine = new JCheckBoxMenuItem("Command Line");
		chckbxmntmCommandLine.setSelected(true);
		mnView.add(chckbxmntmCommandLine);

		formatOutput = new JCheckBoxMenuItem("Format Console");
		formatOutput.setSelected(true);
		mnView.add(formatOutput);
		//mnView.addSeparator();

		chckbxmntmDebugger = new JCheckBoxMenuItem("Debugger Controls");
		mnView.add(chckbxmntmDebugger);

		chckbxmntmAtoms= new JMenuItem("Inprocess Atoms");
		mnView.add(chckbxmntmAtoms);

		chckbxmntmLogs= new JMenuItem("Logs");
		mnView.add(chckbxmntmLogs);

		addActions();
		disableDebuggerOptions();
		mnView.setEnabled(true);
	}

	public void addActions(){

		chckbxmntmConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxmntmConsole.isSelected()){
					chckbxmntmTestSuite.setSelected(false);
					ZugGUI.showConsole();
					ZugGUI.updateFrame();
				}else{
					if(chckbxmntmTestSuite.isEnabled()){
						chckbxmntmTestSuite.setSelected(true);
						ZugGUI.showTestSuite();
						ZugGUI.updateFrame();
					}
				}
			}
		});

		chckbxmntmTestSuite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxmntmTestSuite.isSelected()){
					chckbxmntmConsole.setSelected(false);
					ZugGUI.showTestSuite();
					ZugGUI.updateFrame();
				}else{
					chckbxmntmConsole.setSelected(true);
					ZugGUI.showConsole();
					ZugGUI.updateFrame();
				}
			}
		});

		chckbxmntmCommandLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxmntmCommandLine.isSelected()){
					ZugGUI.showRunZUGCommand();
					ZugGUI.updateFrame();
				}else{
					ZugGUI.hideRunZUGCommand();
					ZugGUI.updateFrame();
				}
			}
		});

		chckbxmntmDebugger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxmntmDebugger.isSelected()){
					ZugGUI.showDebugger();
					ZugGUI.updateFrame();
				}else{
					ZugGUI.hideDebugger();
					ZugGUI.updateFrame();
				}
			}
		});

		chckbxmntmAtoms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewAtoms va=ViewAtoms.getInstance(ZugGUI.getFrame());
				va.setVisible(true);
			}
		});

		formatOutput.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(formatOutput.isSelected()){
					ZugGUI.setFormatOutput(true);
				}else{
					ZugGUI.setFormatOutput(false);
				}
			}
		});

		chckbxmntmLogs.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLogDirectory();
			}
		});

	}

	public JMenu getMnView() {
		return mnView;
	}

	public void enableDebuggerOptions(){

		chckbxmntmTestSuite.setEnabled(true);
		chckbxmntmDebugger.setEnabled(true);
		chckbxmntmDebugger.setSelected(true);
	}

	public void disableDebuggerOptions(){

		chckbxmntmTestSuite.setEnabled(false);
		chckbxmntmDebugger.setEnabled(false);

	}
	private void openLogDirectory() {
		// TODO Auto-generated method stub
		try {			
			Desktop.getDesktop().open(new File(SysEnv.LOG_DIR+File.separator+"ZUG Logs"));		        	
		} catch (Exception ex) {
			// no application registered for PDFs
			System.err.println("Warn: Error while opening log location "+ex.getMessage());
		}
	}
}
