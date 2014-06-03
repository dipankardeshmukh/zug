package com.automature.zug.gui;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;

import com.automature.zug.gui.components.CheckBoxJList;
import com.automature.zug.gui.components.CheckListItem;
import com.automature.zug.gui.components.MacroColumns;

public class MacroColumnPanel extends JPanel {

	private JCheckBox chckbxUseMainTest;
	private JComboBox comboBox;
	private CheckBoxJList list;
	private Map<String,CheckBoxJList> macroOptions;
	private JScrollPane scrollPane;
	private String mainTS;
	/**
	 * Create the panel.
	 */
	public MacroColumnPanel() {
		setBackground(Color.WHITE);
		setLayout(null);
		setMinimumSize(new Dimension(500, 200));
		setPreferredSize(new Dimension(500, 200));
		macroOptions=new HashMap<String,CheckBoxJList>();
		chckbxUseMainTest = new JCheckBox("Use main Test Suite column for all");
		chckbxUseMainTest.setBackground(Color.WHITE);
		chckbxUseMainTest.setBounds(6, 7, 254, 23);
		add(chckbxUseMainTest);

		list = new CheckBoxJList();



		scrollPane = new JScrollPane(list);
		scrollPane.setBounds(230, 44, 200, 80);
		add(scrollPane);

		comboBox = new JComboBox();
		comboBox.setBounds(10, 44, 190, 23);
		add(comboBox);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				showListForSelection();
				//				scrollPane.removeAll();
			}
		});


	}
	
	public void showListForSelection(){
		String item=(String)comboBox.getSelectedItem();
		//System.out.println("item "+item+"\nmap "+macroOptions);
		scrollPane.setViewportView(macroOptions.get(item));
		//	scrollPane.setViewportView(macroOptions.get(item));
		MacroColumnPanel.this.revalidate();MacroColumnPanel.this.repaint();

	}
	
	public MacroColumnPanel(List<MacroColumns> macCols){
		this();
		refreshData(macCols);
	}

	public void refreshData(List<MacroColumns> l) {
		// TODO Auto-generated method stub
		for(MacroColumns macs:l){
			if(!macroOptions.containsKey(macs.getFileName())){
				comboBox.addItem(macs.getFileName());
				CheckBoxJList clist=new CheckBoxJList(macs.getColumns());
				macroOptions.put(macs.getFileName(), clist);
			}
		}
		showListForSelection();

	}

	public void refreshData(MacroColumns macroColumns, List<MacroColumns> l) {
		// TODO Auto-generated method stub
		mainTS=macroColumns.getFileName();
		if(!macroOptions.containsKey(macroColumns.getFileName())){
			comboBox.addItem(macroColumns.getFileName());
			CheckBoxJList clist=new CheckBoxJList(macroColumns.getColumns());
			macroOptions.put(macroColumns.getFileName(), clist);
		}
		refreshData(l);
	}

	public String getOptions(){
		String tmp="-macroColumn=";
		Iterator<String> it=macroOptions.keySet().iterator();
		if(chckbxUseMainTest.isSelected()){
			CheckListItem clitem=(CheckListItem) macroOptions.get(mainTS).getSelectedValue();
			if(clitem!=null&&clitem.isSelected()){
				String value=clitem.toString();
				if(value!=null && !value.isEmpty()){
					while(it.hasNext()){
						String name=it.next();
						tmp+=name+":"+value+",";
					}
				}else{
					return "";
				}
			}else{
				return "";
			}

		}else{
			while(it.hasNext()){
				String name=it.next();
				CheckListItem clitem=(CheckListItem)macroOptions.get(name).getSelectedValue();
				if(clitem!=null&&clitem.isSelected()){
					String value=clitem.toString();
					if(value!=null && !value.isEmpty())
						tmp+=name+":"+value+",";
				}
			}
		}
		return tmp.equals("-macroColumn=")?"":tmp.substring(0, tmp.length()-1);
	}
}
