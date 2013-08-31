package com.automature.zug.gui.menus;

import javax.swing.JMenuBar;

public class GuiMenuBar {
	
	private JMenuBar menuBar;
	private FileMenu fileMenu;
	private EditMenu editMenu;
	private ViewMenu viewMenu;
	private RunMenu runMenu;
	private SettingsMenu settingsMenu;
	private ReportingMenu reportingMenu;
	private HelpMenu helpMenu;
	
	public GuiMenuBar() {
		
		super();
		menuBar = new JMenuBar();
		
		fileMenu=new FileMenu();
		menuBar.add(fileMenu.getMnFile());
		
		editMenu=new EditMenu();
		menuBar.add(editMenu.getMnEdit());
		
		viewMenu=new ViewMenu();
		menuBar.add(viewMenu.getMnView());
		
		runMenu=new RunMenu();
		menuBar.add(runMenu.getMnRun());
		
		settingsMenu=new SettingsMenu();
		menuBar.add(settingsMenu.getMnSettings());
		
		reportingMenu=new ReportingMenu();
		menuBar.add(reportingMenu.getMnReporting());
		
		helpMenu=new HelpMenu();
		menuBar.add(helpMenu.getMnHelp());
		
	}

	public void clearOptions(){
		
		disableDebuggerOptions();
		settingsMenu.clearOptions();
	}
	
	public void disableDebuggerOptions(){
		
		editMenu.disableDebuggerOPtions();
		viewMenu.disableDebuggerOptions();
		runMenu.disableDebuggerOPtions();
	}
	
	public void enableDebuggerOptions(){
		
		editMenu.enableDebuggerOPtions();
		viewMenu.enableDebuggerOptions();
		runMenu.enableDebuggerOPtions();
	
	}
	
	public JMenuBar getMenuBar(){
		return menuBar;
	}
}
