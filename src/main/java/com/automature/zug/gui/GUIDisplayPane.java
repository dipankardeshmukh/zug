package com.automature.zug.gui;



import com.automature.zug.gui.sheets.SpreadSheet;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.plaf.multi.MultiTabbedPaneUI;
import javax.swing.text.DefaultCaret;

public class GUIDisplayPane {

    JPanel mainPanel = new JPanel();
    private JTabbedPane tabbedPane;

    private static ConsoleDisplay consoleDisplay = new ConsoleDisplay();
    private static SheetDisplayPane sheetDisplay;
    private static TaskPane taskPane;

    private enum DisplayMode {
        CONSOLE, SHEET, SPLIT
    }

    private static DisplayMode displayMode = null;

    public GUIDisplayPane() {

        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        tabbedPane.setBackground(Color.LIGHT_GRAY);

        consoleDisplay=new ConsoleDisplay();
        this.addTab("Console", null, consoleDisplay.getConsole(), null);

        mainPanel.add(tabbedPane);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));


    }

    public void addTab(String title,Icon icon,Component component,String tip){

        if(tabbedPane.getTabCount()>0){
            for(Component c :tabbedPane.getComponents()){
                tabbedPane.remove(c);
            }
        }

        tabbedPane.addTab(title, icon, component, tip);
    }

    public void addSheetDisplayPane(SpreadSheet sp) throws Exception {

        sheetDisplay =new SheetDisplayPane(sp);
        this.addTab(new File(sp.getAbsolutePath()).getName(), null, sheetDisplay, "");

        ArrayList<JCheckBox> TestCaseSelectState = new ArrayList<JCheckBox>();
        if(taskPane!=null){
            TestCaseSelectState = taskPane.getSelectedTestCaseIds();
            mainPanel.remove(taskPane);
            taskPane=null;
        }

        if(taskPane==null){
            taskPane = new TaskPane();
            taskPane.setSelectedTestCaseIds(TestCaseSelectState);
            Toolkit tk = Toolkit.getDefaultToolkit();
            int xSize = ((int) tk.getScreenSize().getWidth());
            int gameWidth = (int) (Math.round(xSize * .23));
            taskPane.setPreferredSize(new Dimension(gameWidth,(int)tk.getScreenSize().getHeight()));
            taskPane.setMaximumSize(new Dimension(350,(int)tk.getScreenSize().getHeight()));
            //taskPane.setAlignmentY(SwingConstants.TOP);
            mainPanel.add(taskPane);
        }

        if(displayMode==null){
            splitTab();
        }
        else{
            setPreviousDisplayMode();
        }

    }

    private void setPreviousDisplayMode() {
        if(displayMode.compareTo(DisplayMode.SPLIT)==0){
            splitTab();
        }else if(displayMode.compareTo(DisplayMode.SHEET)==0){
            showTestSuite();
        }else{
            showConsole();
        }
    }




    public SpreadSheet getSpreadSheet(){
        return sheetDisplay.getSpreadSheet();
    }


    public void bringSheetDisplayPane(SpreadSheet sp) throws Exception {

        sheetDisplay =new SheetDisplayPane(sp);
        //this.addTab(new File(sp.getAbsolutePath()).getName(), null, sheetDisplay, "");
        if((displayMode==null) || (displayMode.compareTo(DisplayMode.CONSOLE)==0)){
            splitTab();
        }
        else{
            setPreviousDisplayMode();
        }

    }

    public void splitTab(){

        if(IconsPanel.getFileName()!=null){

            JSplitPane splitview;
            splitview = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                    consoleDisplay.getConsole(), sheetDisplay);
            splitview.setResizeWeight(.5d);
            this.addTab("Split view", null, splitview, null);
            displayMode = DisplayMode.SPLIT;
        }
        else {
            JOptionPane.showMessageDialog(null, "Select test suite first!");
        }
    }

    public void showConsole(){
        this.addTab("Console", null, consoleDisplay.getConsole(), null);
        displayMode = DisplayMode.CONSOLE;
    }

    public void showTestSuite(){
        if(IconsPanel.getFileName()!=null){
            this.addTab("Excel", null, sheetDisplay, null);
            displayMode = DisplayMode.SHEET;
        }
        else {
            JOptionPane.showMessageDialog(null, "Select test suite first!");
        }
    }




    public JPanel getDisplayPane(){
        return mainPanel;
    }

    public void removeTaskPane(){

        mainPanel.remove(taskPane);
        ZugGUI.updateFrame();
    }

    public void addTaskPane(){

        if(IconsPanel.getFileName()!=null){

            mainPanel.add(taskPane);
            ZugGUI.updateFrame();

        }else {
            JOptionPane.showMessageDialog(null, "Select test suite first!");
        }
    }

    public TaskPane getTaskPane(){
        return taskPane;
    }

    public JTextPane getConsole(){
        return consoleDisplay.getConsoleDisplay();
    }

    public void setFormatOuput(boolean val){
        consoleDisplay.setFormatOutput(val);
    }

    public void sendConsoleMessage(String msg) {
        consoleDisplay.updateTextArea(msg);
    }

    public void clearConsole() {
        consoleDisplay.getConsoleDisplay().setText(" ");
    }

    public void initialize(){
        tabbedPane.removeAll();
        clearConsole();
        tabbedPane.addTab("Console", null, consoleDisplay.getConsole(), null);
    }



    SheetDisplayPane getSheetDisplayPane(){
        return sheetDisplay;
    }

    public void redirectSystemStreams(){
        consoleDisplay.redirectSystemStreams();
    }

    public void highlightTestCase(String id, boolean selected){
        taskPane.highlightTestCase(id, selected);
    }

    public void updateTestCaseStatus(String id, boolean status){
        taskPane.updateTestCaseStatus(id,status);
    }

    public String getSelectedTestCases(){
        return taskPane.getSelectedTestCases();
    }
}
