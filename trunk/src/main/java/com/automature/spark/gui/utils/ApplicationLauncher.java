/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.automature.spark.engine.SysEnv;

import javafx.application.HostServices;

/**
 *
 * @author skhan
 */
public class ApplicationLauncher {
    
    private static HostServices hostServices;
    
    public static void launchLogDirectory(){
        //java.awt.Desktop.getDesktop().open(new File());
    }
    public static void launchHelpWiki(){
        launchBrowser("http://automature.boards.net/board/3/spark-zug");
    }
    public static void launchUserCommunity(){
        launchBrowser("http://automature.boards.net/board/3/spark-zug");
    }
    public static void launchFAQs(){
        launchBrowser("http://automature.boards.net/board/3/spark-zug");
    }
    private static void launchBrowser(String url){
      
            hostServices.showDocument(url);
           // java.awt.Desktop.getDesktop().browse(new URI(url));
    }

    public static void showLogs(){
    	hostServices.showDocument(SysEnv.LOG_DIR+File.separator+"ZUG Logs");
    }
    
    public static void setHostServices(HostServices hostServices) {
        ApplicationLauncher.hostServices = hostServices;
    }   
}
