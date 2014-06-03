package com.automature.zug.gui;

import com.automature.zug.util.Log;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;

public class GuiUtils {

    public static JFileChooser chooseFile() throws Exception {

        JFileChooser chooser = null;
        int returnVal = 0;

        try {

            chooser = new JFileChooser();
         
    		RecentDirectoryChooserPanel chooserHelper=new RecentDirectoryChooserPanel(ZugGUI.getRecenDirectories(),chooser);
    		chooser.setAccessory(chooserHelper);
  
    		
            //chooser.setCurrentDirectory(new File(fileName).getParentFile());

            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Microsoft Excel Documents", "xls", "xlsx");

            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setFileFilter(filter);

            returnVal = chooser.showOpenDialog(null);

        } catch (HeadlessException e) {
            Log.Error("GuiUtils/chooseFile Error while selecting file. "+e.getMessage());
        }

        if (returnVal == JFileChooser.APPROVE_OPTION)
            return chooser;

        return null;

    }
}
