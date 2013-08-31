package com.automature.zug.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.automature.zug.engine.Controller;

public class ViewBreakPointPanel {
	public JPanel panel_5;
	JComboBox breakPointComboBox;
	JButton btnRemoveBP;
	JList steplist;
	JScrollPane listScroller;
	
	public ViewBreakPointPanel(){
		initialize();
	}
	
	public void initialize(){
		panel_5=new JPanel();
		panel_5.setVisible(false);
		panel_5.setBackground(Color.white);
		breakPointComboBox=new JComboBox();
	
		panel_5.add(breakPointComboBox);
		btnRemoveBP=new JButton("Remove BP");
		btnRemoveBP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object ob[]=steplist.getSelectedValues();
//				List steps=new ArrayList();
				String id=(String)breakPointComboBox.getSelectedItem();
				Controller.removeBreakPoints(id, ob);
				JOptionPane.showMessageDialog(panel_5, "Break point removed");
				refreshList();
			}
		});
		
		breakPointComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					panel_5.remove(listScroller);
					panel_5.remove(btnRemoveBP);
				}catch(Exception e){
					
				}						
				String id=(String)breakPointComboBox.getSelectedItem();
				if(id!=null){
				DebuggerConsole.showTab(id);
				ArrayList<Integer> al=Controller.getBPSteps(id);
				String steps[]=new String[al.size()];
				for(int i=0;i<al.size();i++){
					steps[i]=""+al.get(i);
				}
				steplist = new JList(steps); //data has type Object[]
				steplist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				steplist.setLayoutOrientation(JList.VERTICAL);
				steplist.setVisibleRowCount(-1);
			
				
				listScroller = new JScrollPane(steplist);
				listScroller.setPreferredSize(new Dimension(80, 80));
				panel_5.add(listScroller);
				panel_5.add(btnRemoveBP);
				panel_5.revalidate();
				panel_5.repaint();
				}
			}
		});
	}
	
	public void refreshList(){
		breakPointComboBox.removeAllItems();
		Set s=Controller.getBreakPointsIDs();
		Iterator setIt=s.iterator();
		while(setIt.hasNext()){
			breakPointComboBox.addItem(setIt.next());
		}
		panel_5.setVisible(true);
		panel_5.revalidate();
		panel_5.repaint();
	}
	
}
