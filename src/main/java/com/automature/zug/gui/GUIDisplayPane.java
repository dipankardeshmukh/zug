package com.automature.zug.gui;

import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.DefaultCaret;

public class GUIDisplayPane {

    private JTabbedPane tabbedPane;
    private static ConsoleDisplay consoleDisplay = new ConsoleDisplay();
    private static SheetDisplayPane sheetDisplay;

    public GUIDisplayPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        consoleDisplay=new ConsoleDisplay();
        this.addTab("Console", null, consoleDisplay.getConsole(), null);
        tabbedPane.setUI(new WindowsTabbedPaneUI());
        //tabbedPane.setBorder(BorderFactory.createEmptyBorder());

    }

    public void addTab(String title,Icon icon,Component component,String tip){

        if(tabbedPane.getTabCount()>0){
            for(Component c :tabbedPane.getComponents()){
                tabbedPane.remove(c);
            }
        }
        tabbedPane.addTab(title, icon, component, tip);
    }

    public void addSheetDisplayPane(String fileName){
        sheetDisplay =new SheetDisplayPane(fileName);
        this.addTab("Test Suite", null, sheetDisplay, "");
    }


    public void splitTab(){

        if(IconsPanel.getFileName()!=null){

            JSplitPane splitview;
            splitview = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                    consoleDisplay.getConsole(), sheetDisplay);
            splitview.setResizeWeight(.6d);
            this.addTab("Split view", null, splitview, null);
        }
        else {
            JOptionPane.showMessageDialog(null, "Select test suite first!");
        }
    }

    public JTabbedPane getDisplayPane()	{
        return tabbedPane;
    }
    public JTextArea getConsole()	{
        return consoleDisplay.getConsoleDisplay();
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

    public void showConsole(){
        this.addTab("Console", null, consoleDisplay.getConsole(), null);
    }

    public void showTestSuite(){
        if(IconsPanel.getFileName()!=null){
            this.addTab("Sheet", null, sheetDisplay, null);
        }
        else {
            JOptionPane.showMessageDialog(null, "Select test suite first!");
        }
    }

    SheetDisplayPane getSheetDisplayPane(){
        return sheetDisplay;
    }

    public void redirectSystemStreams(){
        consoleDisplay.redirectSystemStreams();
    }


}
