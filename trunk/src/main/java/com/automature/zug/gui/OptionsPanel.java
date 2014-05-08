package com.automature.zug.gui;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.automature.zug.gui.menus.GuiMenuBar;

public class OptionsPanel extends JPanel{
	
	//private JPanel optionsPanel;
	private CommandPanel cmdPanel;
	private LineBorder border1;
	DebuggerControls debuggerControls;
	
	public OptionsPanel() {
		border1 = new LineBorder(Color.GRAY,1);
		
		setMinimumSize(new Dimension(767, 80));
		//optionsPanel.setPreferredSize(new Dimension(767, 130));
        setBorder(border1);
		setMaximumSize(new Dimension(3000, 110));
		setBackground(Color.lightGray);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		debuggerControls=new DebuggerControls();
		cmdPanel = new CommandPanel();
        add(IconsPanel.iconPanel);
        add(cmdPanel);
        IconsPanel.setIconsPanelProperty(this);
		
        //optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

	}
	
	
	
	
	public JPanel getcmdPanel(){
		return cmdPanel;
	}
	public CommandPanel getCommandPanel(){
		return cmdPanel;
	}
	
	public void showDebuggerControls(){
		
		if(cmdPanel.isDisplayable()){
		//	optionsPanel.setPreferredSize(new Dimension(767,80));
			add(debuggerControls.getDebuggerControlPanel());
			remove(getcmdPanel());
			add(getcmdPanel());
		}else{
		//	optionsPanel.setPreferredSize(new Dimension(767,120));
			add(debuggerControls.getDebuggerControlPanel());
		}
//		ZugGUI.updateFrame();
		refreshPanel();
	}
	public void hideDebuggerControls(){
		if(cmdPanel.isDisplayable()){
		//	optionsPanel.setPreferredSize(new Dimension(767,75));
		}else{
		//	optionsPanel.setPreferredSize(new Dimension(767,45));
		}
		remove(debuggerControls.getDebuggerControlPanel());
//		ZugGUI.updateFrame();
		refreshPanel();
	}
	
	
	public void showRunCommand(){
		if(debuggerControls.getDebuggerControlPanel().isDisplayable()){
		//	optionsPanel.setPreferredSize(new Dimension(767,120));
			remove(debuggerControls.getDebuggerControlPanel());
			add(debuggerControls.getDebuggerControlPanel());
		}else{
			//optionsPanel.setPreferredSize(new Dimension(767,80));
		}
		add(getcmdPanel());
		//ZugGUI.updateFrame();
		refreshPanel();
	}
	
	public void hideRunCommand(){
		remove(getcmdPanel());
		if(debuggerControls.getDebuggerControlPanel().isDisplayable()){
		//	optionsPanel.setPreferredSize(new Dimension(767,80));
		}else{
		//	optionsPanel.setPreferredSize(new Dimension(767,45));
		}
//		ZugGUI.updateFrame();
		refreshPanel();
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
	
	public void refreshPanel(){
		revalidate();
		repaint();
	}
	
}

