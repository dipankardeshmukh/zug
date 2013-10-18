package com.automature.zug.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashSet;
import java.util.Set;

import com.automature.zug.engine.Controller;
import com.automature.zug.gui.menus.GuiMenuBar;
import com.automature.zug.gui.sheets.SpreadSheet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ZugGUI {

	private static JFrame frame;
	private static GUIDisplayPane guiDisplayPane;
	private static OptionsPanel ops;
	 
	private static GuiMenuBar guiMenuBar;
	static String cmdParams[];
	static boolean runningStatus=false;

    public static SpreadSheet spreadSheet;


	public void initialize(String []params) {
		 

		
/*		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
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
		}*/
		cmdParams=params;
		//LineBorder border = new LineBorder(Color.WHITE,4);
		frame = new JFrame();
        frame.setBackground(Color.lightGray);
		//frame.getRootPane().setBorder(border);
		frame.getContentPane().setFont(new Font("Tahoma", Font.BOLD, 11));
		frame.getContentPane().setBackground(Color.lightGray);
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

		
		guiMenuBar=new GuiMenuBar();
		//guiMenuBar.getMenuBar().setBorder(new LineBorder(new Color(245,252,254),4));
		frame.setJMenuBar(guiMenuBar.getMenuBar());

		//guiMenuBar.getMenuBar().setEnabled(false);
		//frame.setMenuBar(arg0)

		ops=new OptionsPanel();
		ops.createOptionsPanel();
		frame.getContentPane().add(ops.getOptionsPanel(),BorderLayout.NORTH);

        guiDisplayPane=new GUIDisplayPane();
        frame.getContentPane().add(guiDisplayPane.getDisplayPane(), BorderLayout.CENTER);
        guiDisplayPane.redirectSystemStreams();
        frame.setVisible(true);
	}
  
	static SheetDisplayPane getSheetDisplayPane(){
		return guiDisplayPane.getSheetDisplayPane();
	}

    static GUIDisplayPane getDisplayPane(){
        return guiDisplayPane;
    }

    static void spitDisplay(){
                guiDisplayPane.splitTab();
    }

	public void createDebugger(){
		
		ops.createDebugger(guiDisplayPane.getSheetDisplayPane().getSheetNames());
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
		ArrayList<String> params=new ArrayList<String>();

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
	
	public static void loadFile() throws Exception {
		String fileName=IconsPanel.getFileName();
		if (fileName != null) {
			ZugGUI.setTitle(fileName.substring(fileName
					.lastIndexOf("\\") + 1));
				ZugGUI.clearOptions();
				ZugGUI.initialize();
                ZugGUI.loadSpreadSheet(fileName);
				ZugGUI.addTestSuiteTabToDisplay(spreadSheet);
		}	
		
	}

    private static void loadSpreadSheet(String fileName) throws Exception {

        spreadSheet = new SpreadSheet();
        spreadSheet.readSpreadSheet(fileName);
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
		guiDisplayPane.sendConsoleMessage(msg);
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
		frame.setTitle("Automature ZUG - "+title);
	}
	
	public static void clearConsole(){
		guiDisplayPane.clearConsole();
	}
	
	public static DebuggerConsole getDebugger(){
		return ops.getDebugger();
	}

	public static void addTestSuiteTabToDisplay(SpreadSheet sh){
        guiDisplayPane.addSheetDisplayPane(sh);
		IconsPanel.od.createPanels();
	}

    public void showRunningTestCase(String id){
        guiDisplayPane.highlightTestCase(id);
    }

	public void showRunningTestStep(int n){

		guiDisplayPane.getSheetDisplayPane().showRunningTestStep(n);
	}

	public void showRunningMoleculeStep(String name,int n,int start){
		guiDisplayPane.getSheetDisplayPane().showRunningMoleculeStep(name,n,start);
	}
	
	public static void removeAllTabs(){
		guiDisplayPane.initialize();
	}
	
	public static void initialize(){
		ops.hideDebuggerControls();
		guiDisplayPane.clearConsole();
		IconsPanel.disableDebugger();
		updateFrame();
	}
	
	public static void showConsole(){
		guiDisplayPane.showConsole();
	}
	
	public static void showTestSuite(){
		guiDisplayPane.showTestSuite();
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