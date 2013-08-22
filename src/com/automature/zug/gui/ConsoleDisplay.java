package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class ConsoleDisplay {
	
	private JPanel outputPanel;
	private JScrollPane scrollPane;
	private JTextArea textArea= new JTextArea();
//	private CmdGUI cmd;
	private DefaultCaret caret;
	
	ConsoleDisplay(){
	
		outputPanel = new JPanel();
		outputPanel.setBackground(Color.WHITE);
		outputPanel.setLocation(0, 300);
		outputPanel.setPreferredSize(new Dimension(767,278));
		outputPanel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		outputPanel.add(scrollPane);
		scrollPane.setAutoscrolls(true);
		scrollPane.setViewportView(textArea);
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(Color.BLACK);
		textArea.setEditable(false);
		textArea.setAutoscrolls(true);
		caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	/*	
		textArea.addMouseListener(new MouseAdapter() {
			private boolean flag=true;

			public void mouseClicked(MouseEvent event)
			{
				if(event.getClickCount()==2)
				{
					if(flag==true)
					{
						outputPanel.removeAll();
						cmd = new CmdGUI(textArea);
						flag=false;

						outputPanel.revalidate();
						outputPanel.repaint();
					}
					else if(flag==false)
					{
						cmd.getFrame().dispose();
						textArea.setVisible(true);
						outputPanel.removeAll();
						scrollPane.removeAll();
						scrollPane = new JScrollPane(textArea);
						textArea.setEditable(false);
						textArea.setBounds(0, 0, 737, 311);
						caret = (DefaultCaret)textArea.getCaret();
						caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
						scrollPane.setBounds(0, 0, 737, 349);
						scrollPane.setAutoscrolls(true);
						outputPanel.add(scrollPane);
						outputPanel.revalidate();
						outputPanel.repaint();
						ZugGUI.updateFrame();
						flag=true;
					}
				}
				event.consume();
			}
		});*/

	}
	
	public void clearDisplay(){
		textArea.setText("");
	}
	
	public JPanel getConsole(){
		return outputPanel;
	}
	public JTextArea getConsoleDisplay(){
		return textArea;
	}
}
