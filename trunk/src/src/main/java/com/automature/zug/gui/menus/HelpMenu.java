package com.automature.zug.gui.menus;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.automature.zug.engine.Controller;
import com.automature.zug.engine.SysEnv;

public class HelpMenu {
	
	private JMenu mnHelp;
	private JMenuItem mntmContents;
	private JMenuItem mntmAutomatureWebsite;
	private JMenuItem mntmAbout;

	public HelpMenu(){
		
		mnHelp = new JMenu("Help");

		mntmContents = new JMenuItem("Contents");
		mnHelp.add(mntmContents);

		mntmAutomatureWebsite = new JMenuItem("Automature Website");
		mnHelp.add(mntmAutomatureWebsite);
		
		mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		mnHelp.setEnabled(true);
		addActions();
	}

	public void addActions(){
		
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mntmAbout.getRootPane(), Controller.getVersionMessage());
			}
		});	
		
		mntmAutomatureWebsite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchURL("http://www.automature.com");
			}
		});
		
		mntmContents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
				    try {
				    	SysEnv env=new SysEnv();
				    	String filePath=System.getProperty("user.dir")+SysEnv.SLASH+"Input Files"+SysEnv.SLASH+"ZUG User Manual.pdf";
				        File myFile = new File(filePath);
				        Desktop.getDesktop().open(myFile);
				   
				    } catch (Exception ex) {
				        // no application registered for PDFs
				    }
				}
			}
		});
	}
	
	public JMenu getMnHelp() {
		return mnHelp;
	}
	
	private void launchURL(String s) {
		
        String s1 = System.getProperty("os.name");
        try {

            if (s1.startsWith("Windows")) {
                Runtime.getRuntime().exec((new StringBuilder()).append("rundll32 url.dll,FileProtocolHandler ").append(s).toString());
            } else {
                String as[] = {"firefox", "opera", "konqueror", "epiphany",
                    "mozilla", "netscape"};
                String s2 = null;
                for (int i = 0; i < as.length && s2 == null; i++) {
                    if (Runtime.getRuntime().exec(new String[]{"which", as[i]}).waitFor() == 0) {
                        s2 = as[i];
                    }
                }

                if (s2 == null) {
                    throw new Exception("Could not find web browser");
                }
                Runtime.getRuntime().exec(new String[]{s2, s});
            }
        } catch (Exception exception) {
            System.out.println("An error occured while trying to open the            web browser!\n");
        }
    }
}
