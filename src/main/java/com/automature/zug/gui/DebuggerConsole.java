package com.automature.zug.gui;


import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;


import com.automature.zug.engine.Controller;
import com.automature.zug.util.Log;


import java.awt.Color;
import java.awt.Dimension;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Toolkit;


public class DebuggerConsole {

	String tcIds;
	private JPanel debuggerPanel;
	static ArrayList<String> names=new ArrayList<String>();

	private AddBreakPointPanel abpp;
	private ContextVarPanel cvpane;
	private ViewBreakPointPanel vbpp;
	
	public DebuggerConsole(ArrayList names) {
		DebuggerConsole.names=names;
		try{
			initialize();
		}catch(Exception e){
			Log.Error(e.getMessage());
		}
		debuggerPanel.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {


		debuggerPanel = new JPanel();
		debuggerPanel.setBackground(Color.white);
		abpp=new AddBreakPointPanel();
		cvpane=new ContextVarPanel();
		vbpp=new ViewBreakPointPanel();
		debuggerPanel.setLayout(new BorderLayout(0, 0));	
	}

	public JPanel getDebuggerPanel() {
		return debuggerPanel;
	}

	public static void sendStepOverSignal(){
		Controller.setStepOver();
	}

	public static void addBreakPoint(String name,String lineNumber){
		//breakPoints.add(id+","+index);
		Controller.setBreakPoint(name, lineNumber);
	}

	public static String[] getSteps(String name){
		int n=ZugGUI.getSheetDisplayPane().getMaxLine(name);
		String []stepValues=new String[n];
		for(int i=1;i<=n;i++){
			stepValues[i-1]=""+i;
		}
//		String steps=(String)testCaseSteps.get(id);
//		int n=Integer.parseInt(steps);
//		String []stepValues=new String[n];
//		for(int i=1;i<=n;i++){
//			stepValues[i-1]=""+i;
//		}
		return stepValues;//new String[3];
	}

//	public  void setTestCaseData(Vector data,Vector header,List bps){
//	//	System.out.println(data.toString());
//		sdp.refreshTestCaseData(data, header,bps);
//	}
//	
//	public  void setMoleculeData(Vector data,Vector header){
//		sdp.refreshMoleculeData(data, header);
//	}
//	
//	public void currentTestStep(int n){
//		sdp.showRunningTestStep(n);
//	}
//	public void currentMoleculeStep(int n){
//		sdp.showRunningMoleculeStep(n);
//	}
//	
//	public static void updateSheetTable(String id){
//		Controller.getSheetData(id);
//	}
//	
	public void showAddBreakPointPanel(){
		debuggerPanel.removeAll();
		debuggerPanel.add(abpp.panel_1, BorderLayout.CENTER);
		Controller.setPauseSignal();
		abpp.panel_1.setVisible(true);
		//debuggerPanel.repaint();
	}
	
	public void showContextVaraiblePane(){
		debuggerPanel.removeAll();
		cvpane.refreshList();
		cvpane.panel_3.setVisible(true);
		debuggerPanel.add(cvpane.panel_3, BorderLayout.CENTER);
	//	debuggerPanel.revalidate();
	//	debuggerPanel.repaint();
	}
	
	public void showViewBreakPointPane(){
		debuggerPanel.removeAll();
		vbpp.refreshList();
		debuggerPanel.add(vbpp.panel_5, BorderLayout.CENTER);
		//debuggerPanel.revalidate();
		//debuggerPanel.repaint();
	}
	
	public void singleStep(){
		sendStepOverSignal();
		cvpane.refreshList();
	}
	
//	public void setTestCaseStep(int n){
//		sdp.showSelectedTestCaseStep(n);
//	}
//
//	public void setMoleculeStep(int n){
//		sdp.showSelectedMoleculeStep(n);
//	}

	public void setPauseSignal(){
		if (ZugGUI.runningStatus) {
			Controller.setPauseSignal();
		}
	}
	
	public void sendResumeSignal(){
		Controller.setResumeSignal();
	}
	
	public static void showTab(String name){
		ZugGUI.getSheetDisplayPane().showTab(name);
	}
			
}
