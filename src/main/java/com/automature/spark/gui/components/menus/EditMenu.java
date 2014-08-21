/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.components.menus;

import javafx.scene.Node;
import javafx.scene.control.Menu;

/**
 *
 * @author skhan
 */
public class EditMenu extends Menu implements GuiMenu{

    public EditMenu() {
         initialize();
    }

    public EditMenu(String string) {
        super(string);
         initialize();
    }

    public EditMenu(String string, Node node) {
        super(string, node);
         initialize();
    }

    @Override
    public void initialize() {
    }
    
}
