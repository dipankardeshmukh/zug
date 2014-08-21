/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.components.menus;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author skhan
 */
public class FileMenu extends Menu implements GuiMenu{

    private MenuItem newMI;
    private MenuItem openMI;
    private Menu recentTestSuite;
    private MenuItem closeMI;
    private MenuItem saveMI;
    private MenuItem saveAsMI;
    private MenuItem exit;
    private MenuItem preferences;
    
    
    public FileMenu() {
        initialize();
    }

    public FileMenu(String string) {
        super(string);
         initialize();
    }

    public FileMenu(String string, Node node) {
        super(string, node);
         initialize();
    }

    @Override
    public void initialize() {
        newMI=new MenuItem("New");
        openMI=new MenuItem("Open…");
        closeMI=new MenuItem("Close");
        saveMI=new MenuItem("Save");
        saveAsMI=new MenuItem("Save As…" );
        preferences=new MenuItem("Preferences…");
        exit=new MenuItem("Quit");
    }
       
    
}
