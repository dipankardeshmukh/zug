package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.automature.zug.gui.menus.GuiMenuBar;

public class OptionsPanel {
	
	private JPanel optionsPanel;
	private CommandPanel cmdPanel;
	private LineBorder border1;
	DebuggerControls debuggerControls;
	
	public OptionsPanel() {
		border1 = new LineBorder(Color.GRAY,1);
		optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(767, 120));
		optionsPanel.setMaximumSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width, 520));
		optionsPanel.setBackground(Color.WHITE);
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
	}
	
	void createOptionsPanel()
	{
		debuggerControls=new DebuggerControls();
		cmdPanel = new CommandPanel();
		optionsPanel.add(IconsPanel.iconPanel);
		optionsPanel.add(cmdPanel.getPanel());
		IconsPanel.setIconsPanelProperty(this);
	}
	
	public JPanel getOptionsPanel(){
		return optionsPanel;
	}
	public JPanel getcmdPanel(){
		return cmdPanel.getPanel();
	}
	public CommandPanel getCommandPanel(){
		return cmdPanel;
	}
	
	public void showDebuggerControls(){
		
		if(cmdPanel.getPanel().isDisplayable()){
			optionsPanel.setPreferredSize(new Dimension(767,240));
			optionsPanel.add(debuggerControls.getDebuggerControlPanel());
			optionsPanel.remove(getcmdPanel());
			optionsPanel.add(getcmdPanel());
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,200));
			optionsPanel.add(debuggerControls.getDebuggerControlPanel());
		}
	}
	public void hideDebuggerControls(){
		if(cmdPanel.getPanel().isDisplayable()){
			optionsPanel.setPreferredSize(new Dimension(767,120));
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,80));
		}
		optionsPanel.remove(debuggerControls.getDebuggerControlPanel());
	}
	
	
	public void showRunCommand(){
		if(debuggerControls.getDebuggerControlPanel().isDisplayable()){
			optionsPanel.setPreferredSize(new Dimension(767,240));
			optionsPanel.remove(debuggerControls.getDebuggerControlPanel());
			optionsPanel.add(debuggerControls.getDebuggerControlPanel());
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,120));
		}
		optionsPanel.add(getcmdPanel());
	}
	
	public void hideRunCommand(){
		optionsPanel.remove(getcmdPanel());
		if(debuggerControls.getDebuggerControlPanel().isDisplayable()){
			optionsPanel.setPreferredSize(new Dimension(767,200));
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,80));
		}
	}
	
	public void enableDebuggerOptions(){
		IconsPanel.enableDebugger();
	}
	
	public void createDebugger(ArrayList<String> names){
		debuggerControls.createDebugger(names);
	}
	public DebuggerConsole getDebugger(){
		return debuggerControls.getDebugger();
	}
	
}

