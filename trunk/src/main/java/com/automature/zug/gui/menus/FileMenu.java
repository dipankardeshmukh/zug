package com.automature.zug.gui.menus;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import com.automature.zug.gui.IconsPanel;
import com.automature.zug.gui.ZugGUI;
import com.automature.zug.util.Log;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class FileMenu {

	private JMenu mnFile;
	private JMenuItem mntmOpen;
	private JMenuItem mntmClose;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmSave;
	private JMenuItem mntmOpenIniFile;
	private JMenuItem mntmExit;
	private JMenuItem mntmRecentlyUsed;
	private ActionListener al;
	
	public JMenu getMnFile() {
		return mnFile;
	}

	public FileMenu(){

        mnFile = new JMenu("File");

		mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);

//		mntmClose = new JMenuItem("Close");
//		mnFile.add(mntmClose);
//
//		mntmSaveAs = new JMenuItem("Save As");
//		mnFile.add(mntmSaveAs);
//
//		mntmSave = new JMenuItem("Save");
//		mnFile.add(mntmSave);
//
//		mntmOpenIniFile = new JMenuItem("Open INI File");
//		mnFile.add(mntmOpenIniFile);
		
	

		mnFile.addSeparator();
		mntmRecentlyUsed = new JMenu("Recently Used");
	/*	mntmRecentlyUsed.setAutoscrolls(true);
		mntmRecentlyUsed.setRolloverEnabled(true);
		mntmRecentlyUsed.setMaximumSize(new Dimension(300, 9));*/
		
		 al=new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JMenuItem item=(JMenuItem)e.getSource();
				/*int confirm = JOptionPane.showOptionDialog(null,
                        "Load File with its previous execution options",
                        "Load Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) {
                	try {
						ZugGUI.loadFileWithExecutionOptions(item.getName());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
                else{*/
                    try {
                    	//System.out.println(item.getActionCommand());
						ZugGUI.loadAndSetFileName(item.getActionCommand());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
             //   }
  
			}
		};
		updateSessionData();
		mnFile.add(mntmRecentlyUsed);
		mnFile.addSeparator();
		
		mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		//mnFile.setEnabled(false);
		addListeners();
	}
	
	public void addListeners(){
		
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                try {
                    IconsPanel.chooseFile();
                } catch (Exception e1) {
                    Log.Error(e1.getMessage());
                }
            }
		});
		
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	public void updateSessionData() {
		// TODO Auto-generated method stub

		mntmRecentlyUsed.removeAll();
		Set<String> files=ZugGUI.getRecntlyUsedFiles();
		if(files.size()<1){
			mntmRecentlyUsed.setEnabled(false);
			return;
		}else{
			mntmRecentlyUsed.setEnabled(true);
		}
		Object []fileList=files.toArray();
		 FileSystemView fsv = FileSystemView.getFileSystemView();
		for(int i=fileList.length-1,counter=0;i>=0 && counter<10;i--,counter++){
			File f=new File((String)fileList[i]);
			if(f!=null && f.exists()){
				JMenuItem jmItem= new JMenuItem((String)fileList[i],fsv.getSystemIcon(f));
				
				jmItem.setActionCommand((String)fileList[i]);
				mntmRecentlyUsed.add(jmItem);
				jmItem.addActionListener(al);
				
			}
		}
	
		mntmRecentlyUsed.revalidate();
		mntmRecentlyUsed.repaint();
	}
}
