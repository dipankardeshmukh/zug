package com.automature.zug.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.LineBorder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;

import com.automature.zug.engine.Controller;
import com.automature.zug.gui.menus.GuiMenuBar;

public class ZugGUI {

	private static JFrame frame;
	private static GUIDisplayPane displayPane;
	private static OptionsPanel ops;
	 
	private static GuiMenuBar guiMenuBar;
	static String cmdParams[];
	static boolean runningStatus=false;

	public void initialize(String []params) {
		 

		
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					
				//	UIManager.put("nimbusBase", new Color(100,255,255));
				//	UIManager.put("nimbusBlueGrey", new Color(200,220,230));
				//	UIManager.put("control", new Color(240,240,240));
					UIManager.setLookAndFeel(info.getClassName());
				//	SwingUtilities.updateComponentTreeUI(frame);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
		cmdParams=params;
		LineBorder border = new LineBorder(Color.WHITE,4);
		frame = new JFrame();
		frame.getRootPane().setBorder(border);
		frame.getContentPane().setFont(new Font("Tahoma", Font.BOLD, 11));
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(System.getProperty("user.dir")+"\\Images\\automature.png"));
		frame.setSize(783, 595);
		frame.setPreferredSize(new Dimension(783,590));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Automature Zug");
		frame.setMaximumSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(600,600));
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setLocation(30, 30);
	//	frame.setUndecorated(true);
		
	//	ComponentMover cm=new ComponentMover(frame,frame);
		
		
		guiMenuBar=new GuiMenuBar();
		guiMenuBar.getMenuBar().setBorder(new LineBorder(new Color(245,252,254),4));
		frame.setJMenuBar(guiMenuBar.getMenuBar());
		
		//guiMenuBar.getMenuBar().setEnabled(false);
		//frame.setMenuBar(arg0)
		displayPane=new GUIDisplayPane();
		frame.getContentPane().add(displayPane.getDisplayPane(), BorderLayout.CENTER);
		ops=new OptionsPanel();
		ops.createOptionsPanel();
		frame.getContentPane().add(ops.getOptionsPanel(),BorderLayout.NORTH);
		
		frame.setVisible(true);
	}
  
	static SheetDisplayPane getSheetDisplayPane(){
		return displayPane.getSheetDisplayPane();
	}
	
	public void createDebugger(){
		
		ops.createDebugger(displayPane.getSheetDisplayPane().getSheetNames());
		ops.enableDebuggerOptions();
		ops.showDebuggerControls();
		guiMenuBar.enableDebuggerOptions();
		updateFrame();
		//ops.showDebuggerControls();
	}
	
	public static JFrame getFrame(){
		return frame;
	}
	
	private static ArrayList<String> createRunCommand(){
		
		String fileName;
		ArrayList<String> al=IconsPanel.getList();
		ArrayList<String> params=new ArrayList<String>();;
		
		fileName=IconsPanel.getFileName();
		params.add(fileName);
		params.addAll(IconsPanel.getOptions());
		params.addAll(al);
	/*	al=IconsPanel.getList();
		if(al!=null && al.size()!=0)
		{
			if(al.get(0)!=null && !al.get(0).equals(""))
				params.add("-testcaseid="+al.get(0));
			if(al.get(1)!=null && !al.get(1).equals(""))
				params.add("-include="+"\""+al.get(1)+"\"");
			if(al.get(2)!=null && !al.get(2).equals(""))
				params.add("-testcycleid="+al.get(2));
			if(al.get(3)!=null && !al.get(3).equals(""))
				params.add("-testplanid="+al.get(3));
			if(al.get(4)!=null && !al.get(4).equals(""))
				params.add("-topologysetid="+al.get(4));
			if(al.get(5)!=null && !al.get(5).equals(""))
				params.add("-macrocolumn="+al.get(5));
			if(al.get(6)!=null && !al.get(6).equals("")){
				String str[]=al.get(6).split(" -");
				for(String val:str){
					params.add(val.startsWith("-")?val:"-"+val);
				}
			}
			if(al.get(7)!=null && !al.get(7).equals("")){
				String str[]=al.get(7).split(" -");
				for(String val:str){
					params.add(val.startsWith("-")?val:"-"+val);
				}
			}
		}
		*/
		return params;
	}
	
	public static void loadFile(){
		String fileName=IconsPanel.getFileName();
		if (fileName != null) {
			ZugGUI.setTitle(fileName.substring(fileName
					.lastIndexOf("\\") + 1));
				ZugGUI.clearOptions();
				ZugGUI.initialize();
				ZugGUI.addTestSuiteTabToDisplay(fileName);	
		}	
		
	}
	
	static void clearOptions(){
		guiMenuBar.clearOptions();
		IconsPanel.clearOptions();
		OptionGUI.clearOptions();
	}
	
	public static void runZug(){
		if(IconsPanel.getFileName()==null){
			JOptionPane.showMessageDialog(frame, "Please choose a test suite first");
			return;
		}
		if(runningStatus){
			JOptionPane.showMessageDialog(frame, "A test suite is already running.Please stop it then run again");
			return;
		}
		IconsPanel.disableExecuteButton();
		runningStatus=true;
		initialize();
		ArrayList<String> params=createRunCommand();
		for (String param:cmdParams){
			if(param.startsWith("-pwd")){
				params.add(param);
			}
		}
		ops.getCommandPanel().setCommand(params);
		//updateFrame();
		try{
			Controller.oldmain(params.toArray(new String[params.size()]));
		}catch(Throwable t){
			t.printStackTrace();
		}finally{
			ZugGUI.message("\n\nExecution Finished\n\n");
			runningStatus=false;
			ops.hideDebuggerControls();
			IconsPanel.disableDebugger();
			guiMenuBar.disableDebuggerOptions();
			updateFrame();
			IconsPanel.enableExecuteButton();
		}
	}

	public static void stopRunningTestSuite(){
		if (!runningStatus) {
			System.out.println("No Test suite is running");
			return;
		}
		Controller.stopExecution();
		ZugGUI.message("\n\nStopping the execution please wait........\n\n");
		ops.hideDebuggerControls();
		IconsPanel.disableDebugger();
		updateFrame();
	}
	
	public static void message(String msg){
		displayPane.sendConsoleMessage(msg);
	}

	public static void enableFrame(){
		frame.setEnabled(true);
		frame.repaint();
	}
	public static void disableFrame(){
		frame.setEnabled(false);
	}
	public static void updateFrame(){
		frame.validate();
		//frame.getContentPane().repaint();
		frame.repaint();
		//frame.doLayout();
	}
	public static void setTitle(String title){
		frame.setTitle("Automature ZUG        -----      "+title);
	}
	
	public static void clearConsole(){
		displayPane.clearConsole();
	}
	
	public static DebuggerConsole getDebugger(){
		return ops.getDebugger();
	}
	
	
	public static void addTestSuiteTabToDisplay(String fileName){
		SheetDisplayPane sdp=new SheetDisplayPane(fileName);
		displayPane.addTab("Test Suite", null, sdp, "");
		IconsPanel.od.createPanels();
	}
 
	public void showRunningTestStep(int n){
		displayPane.getSheetDisplayPane().showRunnigTestStep(n);
	}
	public void showRunningMoleculeStep(String name,int n,int start){
		displayPane.getSheetDisplayPane().showRunningMoleculeStep(name,n,start);
	}
	
	public static void removeAllTabs(){
		displayPane.initialize();
	}
	
	public static void initialize(){
		ops.hideDebuggerControls();
		displayPane.clearConsole();
		IconsPanel.disableDebugger();
		updateFrame();
	}
	
	public static void showConsole(){
		displayPane.showConsole();
	}
	
	public static void showTestSuite(){
		displayPane.showTestSuite();
	}
	
	public static void showRunZUGCommand(){
		ops.showRunCommand();
		IconsPanel.hideZugCommandButton();
		
	}
	public static void hideRunZUGCommand(){
		ops.hideRunCommand();
		IconsPanel.showZugCommandButton();
	}
	
	public static void showDebugger(){
		ops.showDebuggerControls();
		IconsPanel.showDebuggerButton();
		
	}
	public static void hideDebugger(){
		ops.hideDebuggerControls();
		IconsPanel.hideDebuggerButton();
	}

	public void addSheets(HashMap<String, String> nameSpace, String[] sheets) {
		ZugGUI.getSheetDisplayPane().addExtraSheets(nameSpace, sheets);
		
	}
}