package com.automature.zug.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.automature.zug.engine.Controller;

public class AddBreakPointPanel {
	
	public JPanel panel_1;
	private JLabel lblNewLabel;
	JLabel lblChooseTestStep ;
	JComboBox comboBox,list;
	JTextArea testStepTextArea;
	private JButton btnAdd;
	
	
	
	public AddBreakPointPanel() {
		initialize();
	}
	public void initialize(){
		panel_1 = new JPanel();
		panel_1.setVisible(false);
		panel_1.setBackground(Color.white);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		panel_1.setMaximumSize(new Dimension(350,350));

		lblNewLabel = new JLabel("Choose Excel");
		panel_1.add(lblNewLabel);

		lblChooseTestStep = new JLabel("Choose Test Step");
		//	 lblChooseTestStep.setBounds(32, 58, 119, 26);
		lblChooseTestStep.setVisible(false);
		
		comboBox = new JComboBox();
		//Set s=DebuggerConsole.testCaseSteps.keySet();
		Iterator it=DebuggerConsole.names.listIterator();
		while(it.hasNext()){
			comboBox.addItem((String)it.next());
		}
		
		panel_1.add(comboBox);
		panel_1.add(lblChooseTestStep);
		list=new JComboBox();
		//
		list.setVisible(false);
		panel_1.add(list);		

		btnAdd = new JButton("Add ");
		btnAdd.setVisible(false);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String lineNumber=(String)list.getSelectedItem();
				String name=(String)comboBox.getSelectedItem();
				DebuggerConsole.addBreakPoint(name,lineNumber);
				JOptionPane.showMessageDialog(panel_1, "Break point added");
			}
		});
		panel_1.add(btnAdd);

			comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String name=(String)comboBox.getSelectedItem();
					//System.out.println("Stirng selected");
					panel_1.remove(list);
					DebuggerConsole.showTab(name);
				//	DebuggerConsole.updateSheetTable(name);
					String steps[]=DebuggerConsole.getSteps(name);
					list=new JComboBox();
					for(String item:steps){
						list.addItem(item);
					}
					btnAdd.setVisible(true);
					panel_1.add(list,3);
					panel_1.revalidate();
					panel_1.repaint();
					lblChooseTestStep.setVisible(true);
					list.setVisible(true);
					list.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							String step=(String)list.getSelectedItem();
							String name=(String)comboBox.getSelectedItem();
							ZugGUI.getSheetDisplayPane().showSelectedStep(name, Integer.parseInt(step));
						}
					});
				}
			});
	}

}
