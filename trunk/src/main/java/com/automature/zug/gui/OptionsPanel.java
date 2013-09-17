package com.automature.zug.gui;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
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
		optionsPanel.setPreferredSize(new Dimension(767, 55));
		//optionsPanel.setMaximumSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width, 10));
		optionsPanel.setBackground(Color.lightGray);
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        //optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

	}
	
	void createOptionsPanel()
	{
		debuggerControls=new DebuggerControls();
		cmdPanel = new CommandPanel();
        optionsPanel.add(IconsPanel.iconPanel);
        //IconsPanel.iconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
			optionsPanel.setPreferredSize(new Dimension(767,150));
			optionsPanel.add(debuggerControls.getDebuggerControlPanel());
			optionsPanel.remove(getcmdPanel());
			optionsPanel.add(getcmdPanel());
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,150));
			optionsPanel.add(debuggerControls.getDebuggerControlPanel());
		}
	}
	public void hideDebuggerControls(){
		if(cmdPanel.getPanel().isDisplayable()){
			optionsPanel.setPreferredSize(new Dimension(767,55));
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,55));
		}
		optionsPanel.remove(debuggerControls.getDebuggerControlPanel());
	}
	
	
	public void showRunCommand(){
		if(debuggerControls.getDebuggerControlPanel().isDisplayable()){
			optionsPanel.setPreferredSize(new Dimension(767,55));
			optionsPanel.remove(debuggerControls.getDebuggerControlPanel());
			optionsPanel.add(debuggerControls.getDebuggerControlPanel());
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,55));
		}
		optionsPanel.add(getcmdPanel());
	}
	
	public void hideRunCommand(){
		optionsPanel.remove(getcmdPanel());
		if(debuggerControls.getDebuggerControlPanel().isDisplayable()){
			optionsPanel.setPreferredSize(new Dimension(767,55));
		}else{
			optionsPanel.setPreferredSize(new Dimension(767,55));
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

