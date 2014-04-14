package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument.Content;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.automature.zug.engine.Controller;

public class ContextVarPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	JComboBox cvComboBox;
	JTextArea cvValueTextArea;
	static String value="\n \n \n \n \n \n \n \n \n \n \n";
	private JButton btnSave=new JButton("SAVE");
	private ArrayList cvList=null;
	
	public ContextVarPanel(){
		initialization();
	}
	
	public void initialization(){
		Dimension d=new Dimension(200,350);
		setVisible(false);
		setBackground(Color.white);
		setMinimumSize(new Dimension(200, 330));
		setSize(new Dimension(200, 330));
		
        cvValueTextArea=new JTextArea();
        cvValueTextArea.setWrapStyleWord(true);
        cvValueTextArea.setRows(2);
		cvValueTextArea.setAutoscrolls(true);
		cvValueTextArea.setMinimumSize(d);
		cvValueTextArea.setEditable(false);
		cvValueTextArea.setSize(new Dimension(200, 350));
		cvValueTextArea.setText(value);
		cvValueTextArea.setLineWrap(true);

		DefaultCaret caret1 = (DefaultCaret)cvValueTextArea.getCaret();
		caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		final JScrollPane scrollPane1 = new JScrollPane(cvValueTextArea);
		//scrollPane1.setPreferredSize(new Dimension(300,70));
		scrollPane1.setAutoscrolls(true);
		scrollPane1.setMinimumSize(new Dimension(200, 300));
		scrollPane1.setSize(new Dimension(200, 300));
        JPanel buttonPanel = new JPanel();
		final JButton btnEditCV=new JButton("EDIT");
        final JButton btnLoadCV=new JButton("LOAD");
        setLayout(new BorderLayout(0, 0));

        cvComboBox=new JComboBox();
		add(cvComboBox, BorderLayout.NORTH);
		add(scrollPane1, BorderLayout.CENTER);

        buttonPanel.add(btnLoadCV);
        buttonPanel.add(btnEditCV);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
		btnSave.setVisible(false);
		
		cvComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSave.setVisible(false);
				String cvName=(String)cvComboBox.getSelectedItem();
				if(cvName!=null && !cvName.equals("")){
					final String value=Controller.getContextVarValue(cvName);
					final int length=value.length();
					if(length>2000){					
						cvValueTextArea.setText(value.substring(0, 2000)+"......value truncated");
						//ZugGUI.message(msg);
					}else{
						cvValueTextArea.setText(value);
					}
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
				JOptionPane.showMessageDialog(null, "Context Variable value updated");
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
