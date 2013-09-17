package com.automature.zug.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class CommandPanel {

	JPanel cmdPanel;
	JTextArea textArea_1;
	
	CommandPanel(){
		
		cmdPanel = new JPanel();
		cmdPanel.setBackground(Color.WHITE);
		cmdPanel.setLayout(new GridLayout(0, 1, 0, 0));
		cmdPanel.setPreferredSize(new Dimension(967,20));
		cmdPanel.setMaximumSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,5));
		JScrollPane scrollPane_1 = new JScrollPane();
		textArea_1 = new JTextArea();
		cmdPanel.add(scrollPane_1);
		scrollPane_1.setAutoscrolls(true);
		scrollPane_1.setBackground(Color.WHITE);
		textArea_1.setBackground(Color.lightGray);
		textArea_1.setLineWrap(true);
		textArea_1.setFont(new Font("Monospaced", Font.BOLD, 12));
		textArea_1.setForeground(Color.DARK_GRAY);
		textArea_1.setEditable(false);
		scrollPane_1.setViewportView(textArea_1);
	}
	
	JPanel getPanel(){
		return cmdPanel;
	}
	public void setCommand(ArrayList command){
		String cmd="runzug";
		if(command!=null){
			for(Object ob:command){
				if(!ob.toString().startsWith("-pwd"))
					cmd+=" "+ob.toString(); 
			}
		}	
		textArea_1.setText(cmd);
	}
	
	public void setCommand(String command){
		
		textArea_1.setText("runzug "+command);
	}
	public void appendCommand(String param){
		textArea_1.append(param);
	}
}
