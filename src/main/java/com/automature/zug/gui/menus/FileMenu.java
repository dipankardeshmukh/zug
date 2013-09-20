package com.automature.zug.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.automature.zug.gui.IconsPanel;

public class FileMenu {

	private JMenu mnFile;
	private JMenuItem mntmOpen;
	private JMenuItem mntmClose;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmSave;
	private JMenuItem mntmOpenIniFile;
	private JMenuItem mntmExit;
	
	public JMenu getMnFile() {
		return mnFile;
	}

	public FileMenu(){

        mnFile = new JMenu("File");

		mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);

//		mntmClose = new JMenuItem("Close");
//		mnFile.add(mntmClose);
//
//		mntmSaveAs = new JMenuItem("Save As");
//		mnFile.add(mntmSaveAs);
//
//		mntmSave = new JMenuItem("Save");
//		mnFile.add(mntmSave);
//
//		mntmOpenIniFile = new JMenuItem("Open INI File");
//		mnFile.add(mntmOpenIniFile);
		mnFile.addSeparator();
		mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		//mnFile.setEnabled(false);
		addListeners();
	}
	
	public void addListeners(){
		
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IconsPanel.chooseFile();
			}
		});
		
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}
}