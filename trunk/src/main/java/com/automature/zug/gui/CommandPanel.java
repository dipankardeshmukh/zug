package com.automature.zug.gui;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class CommandPanel extends JPanel{

	
	JTextArea textArea_1;
	
	CommandPanel(){
		
		
       // cmdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(0,0));
		setMinimumSize(new Dimension(300,30));
		setPreferredSize(new Dimension(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,30)));
		setMaximumSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,30));
		JScrollPane scrollPane_1 = new JScrollPane();
		textArea_1 = new JTextArea();
		add(scrollPane_1,BorderLayout.CENTER);
		scrollPane_1.setAutoscrolls(true);
		scrollPane_1.setBackground(Color.WHITE);
		textArea_1.setBackground(Color.lightGray);
		textArea_1.setLineWrap(true);
		textArea_1.setFont(new Font("Monospaced", Font.BOLD, 12));
		textArea_1.setForeground(Color.DARK_GRAY);
		textArea_1.setEditable(false);
		scrollPane_1.setViewportView(textArea_1);
	}
	
	
	public void setCommand(ArrayList command){
		String cmd="zug";
		if(command!=null){
			for(Object ob:command){
				if(!ob.toString().startsWith("-pwd"))
					cmd+=" "+ob.toString(); 
			}
		}	
		textArea_1.setText(cmd);
	}
	
	public void setCommand(String command){
		
		textArea_1.setText("zug "+command);
	}
	public void appendCommand(String param){
		textArea_1.append(param);
	}
}
