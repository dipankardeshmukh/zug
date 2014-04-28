package com.automature.zug.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.automature.zug.gui.ZugGUI;
import com.automature.zug.gui.preference.PreferenceDialog;

public class EditMenu {
	
	private JMenu mnEdit;
	private JMenuItem mntmCopy;
	private JMenuItem mntmPaste;
	private JMenuItem mnContextVariables;
	private JMenu mnBreakPoints;
	private JMenuItem mntmNew;
	//private JMenuItem mntmSet;
	private JMenuItem mntmRemove;
	private JMenuItem mnPreference;
	
	public EditMenu(){
		
		mnEdit = new JMenu("Edit");
		mnEdit.setEnabled(true);

//		mntmCopy = new JMenuItem("Copy");
//		mnEdit.add(mntmCopy);
//
//		mntmPaste = new JMenuItem("Paste");
//		mnEdit.add(mntmPaste);

		mnContextVariables = new JMenuItem("Context Variables");
		mnEdit.add(mnContextVariables);
		
		mnBreakPoints = new JMenu("Break Points");
		mnEdit.add(mnBreakPoints);

		mntmNew = new JMenuItem("New");
		mnBreakPoints.add(mntmNew);
//
//		mntmSet = new JMenuItem("Set");
//		mnContextVariables.add(mntmSet);
//
		mntmRemove = new JMenuItem("Remove");
		mnBreakPoints.add(mntmRemove);
		disableDebuggerOPtions();
		mnPreference = new JMenuItem("Preferences");
		mnEdit.add(mnPreference);
		addActions();
	}

	private void addActions(){
		
		mnContextVariables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ZugGUI.showDebugger();
				ZugGUI.getDebugger().showContextVaraiblePane();
			}
		});
		
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ZugGUI.showDebugger();
				ZugGUI.getDebugger().showAddBreakPointPanel();
				
			}
		});
		
		mntmRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ZugGUI.showDebugger();
				ZugGUI.getDebugger().showViewBreakPointPane();
			}
		});
			
		mnPreference.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PreferenceDialog pd=new PreferenceDialog();
				pd.setVisible(true);
			}
		});
	}
	
	public JMenu getMnEdit() {
		return mnEdit;
	}
	
	public void disableDebuggerOPtions(){
		mnContextVariables.setEnabled(false);
		mnBreakPoints.setEnabled(false);
	}
	
	public void enableDebuggerOPtions(){
		mnContextVariables.setEnabled(true);
		mnBreakPoints.setEnabled(true);
	}
	
	public void clearOptions(){
		
	}
}
