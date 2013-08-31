package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class GUIDisplayPane {

	private JTabbedPane tabbedPane;
	private ConsoleDisplay consoleDisplay;
	
	public GUIDisplayPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		consoleDisplay=new ConsoleDisplay();
		tabbedPane.addTab("Console", null, consoleDisplay.getConsole(), null);
		
	}

	public void addTab(String title,Icon icon,Component component,String tip){
		tabbedPane.addTab(title, icon, component, tip);
		int n=tabbedPane.getTabCount();
	}
	
	public JTabbedPane getDisplayPane()
	{
		return tabbedPane;
	}
	public JTextArea getConsole()
	{
		return consoleDisplay.getConsoleDisplay();	
	}

	public void sendConsoleMessage(String msg) {
		consoleDisplay.getConsoleDisplay().append(msg);
	}

	public void clearConsole() {
		consoleDisplay.getConsoleDisplay().setText(" ");
	}
	public void initialize(){
		tabbedPane.removeAll();
		clearConsole();
		tabbedPane.addTab("Console", null, consoleDisplay.getConsole(), null);
	}
	
		
	
	public void showConsole(){
		tabbedPane.setSelectedIndex(0);
	}
	public void showTestSuite(){
		int n = tabbedPane.getTabCount();
		if(n>1){
			tabbedPane.setSelectedIndex(1);
		}
	}
	 SheetDisplayPane getSheetDisplayPane(){
		return (SheetDisplayPane) tabbedPane.getComponentAt(1);
	 }
	
}
