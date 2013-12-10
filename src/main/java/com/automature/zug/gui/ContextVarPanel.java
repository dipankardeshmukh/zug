package com.automature.zug.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import com.automature.zug.engine.Controller;

public class ContextVarPanel {
	public JPanel panel_3;
	JComboBox cvComboBox;
	JTextArea cvValueTextArea;
	private JButton btnSave=new JButton("SAVE");
	private ArrayList cvList=null;
	
	public ContextVarPanel(){
		initialization();
	}
	
	public void initialization(){

		panel_3 = new JPanel();
        panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		panel_3.setVisible(false);
		panel_3.setBackground(Color.white);

        cvValueTextArea=new JTextArea();
        cvValueTextArea.setRows(2);
		cvValueTextArea.setAutoscrolls(true);
		cvValueTextArea.setEditable(false);

		DefaultCaret caret1 = (DefaultCaret)cvValueTextArea.getCaret();
		caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		final JScrollPane scrollPane1 = new JScrollPane(cvValueTextArea);
		//scrollPane1.setPreferredSize(new Dimension(300,70));
		scrollPane1.setAutoscrolls(true);
        JPanel buttonPanel = new JPanel();
		final JButton btnEditCV=new JButton("EDIT");
        final JButton btnLoadCV=new JButton("LOAD");

        cvComboBox=new JComboBox();
		panel_3.add(cvComboBox);
		panel_3.add(scrollPane1);

        buttonPanel.add(btnLoadCV);
        buttonPanel.add(btnEditCV);
        buttonPanel.add(btnSave);

        panel_3.add(buttonPanel);
		btnSave.setVisible(false);
		
		cvComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSave.setVisible(false);
				String cvName=(String)cvComboBox.getSelectedItem();
				if(cvName!=null && !cvName.equals("")){
					String value=Controller.getContextVarValue(cvName);
					cvValueTextArea.setText(value);
				}else{
					cvValueTextArea.setText("");
				}
				//cvValueTextArea.setText((String)cvList.get(cvName));
				cvValueTextArea.setVisible(true);
				cvValueTextArea.setEditable(false);
			}
		});
		
		btnEditCV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cvValueTextArea.setEditable(true);
				btnSave.setVisible(true);
			}
		});	


		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name=(String)cvComboBox.getSelectedItem();
				String value=cvValueTextArea.getText();
				Controller.upDateContextVar(name, value);
				JOptionPane.showMessageDialog(panel_3, "Context Variable value updated");
			}
		});

        btnLoadCV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshList();
            }
        });

	}
	
	public void refreshList(){
		cvList=Controller.getAllContextVariables();
		cvComboBox.removeAllItems();
		if(cvList!=null){
			Iterator it=cvList.listIterator();
			cvComboBox.addItem("");
			while(it.hasNext()){
				cvComboBox.addItem(it.next());
			}
		}
	}
	
}
