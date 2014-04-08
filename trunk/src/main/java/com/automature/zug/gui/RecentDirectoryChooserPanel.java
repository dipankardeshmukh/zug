package com.automature.zug.gui;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

public class RecentDirectoryChooserPanel extends JPanel {

	static final int SIZE=6;
	JFileChooser fileChooser;
	JList list ;
	private  File[] recentDirectories;


	public RecentDirectoryChooserPanel(Set directories,JFileChooser fileChooser){
		
		ArrayList<File> files=new ArrayList<File>();
		if(directories!=null){
			Iterator it=directories.iterator();
			
			while(it.hasNext()){
				String str=(String)it.next();
				//System.out.println(str);
				File f=new File(str);
				if(f!=null&&f.exists()){
					files.add(f);
//				}else{
//					System.out.println(str+" is not a directory");
				}
			}

//		}else{
//			System.out.println("empty or null");
		}
		Collections.reverse(files);
		int max=files.size()> SIZE?SIZE:files.size();
		if(max==0){
			recentDirectories=new File[0];
		}else{
			//System.out.println(max);
			recentDirectories=(File [])files.subList(0, max).toArray(new File[max]);			
		}

		//recentDirectories=(File [])files.toArray(new File[files.size()]);
		init(fileChooser);
	}

	public RecentDirectoryChooserPanel(File []recentDirectories,JFileChooser fileChooser) {

		this.recentDirectories=recentDirectories;
		init(fileChooser);

	}

	private void init(JFileChooser jFileChooser){
		
		this.fileChooser=jFileChooser;
		this.setLayout(new BorderLayout());
		JPanel labelPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Recent Directories:");
		labelPanel.add(label, BorderLayout.LINE_START);
		labelPanel.setBackground(Color.LIGHT_GRAY);
		labelPanel.setBorder(new EmptyBorder(5, 10, 5, 0));

		list = new JList(recentDirectories);

		list.setCellRenderer(new DefaultListCellRenderer() {
			FileSystemView fsv = FileSystemView.getFileSystemView();
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof File) {
					label.setIcon(fsv.getSystemIcon((File)value));
				}
				return label;
			}
		});

		list.setBorder(new EmptyBorder(0, 5, 5, 0));
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// respond to selection here

			//	System.out.println("selected value "+list.getSelectedValue());
				fileChooser.setCurrentDirectory((File)list.getSelectedValue());

			}
		});
		JScrollPane scrollPane=new  JScrollPane(list); 
		scrollPane.setAutoscrolls(true);
		add(labelPanel, BorderLayout.PAGE_START);
		add(scrollPane, BorderLayout.CENTER);
		this.setVisible(true);
	}


}