/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.controllers;

import java.util.List;

/**
 *
 * @author skhan
 */
public interface GuiController {
	
     public void showRunningTestCase(String testCaseID, boolean b) ;
     public void showRunningMolecule(String MoleculeID,String nameSpace, boolean b) ;
    public void showRunningTestStep(int lineNumber);

    public void updateTestCaseStatus(String testCaseID, boolean b) ;

    public void showRunningMoleculeStep(String molecules, int lineNumber, int start) ;
    public void setParams(List<String> args);
}
