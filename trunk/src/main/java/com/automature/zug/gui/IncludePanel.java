package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UIManager;



public class IncludePanel extends JPanel {
	
	JList list;
	DefaultListModel listModel= new DefaultListModel();
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private final Action action_2 = new SwingAction_2();
	String panelName=" Molecule Sheets";
	/**
	 * Create the panel.
	 */
	public IncludePanel() {

		{
			setBounds(100, 100, 480, 380);
			setBackground(Color.WHITE);
			setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(20, 15, 15, 15)));
			setLayout(new BorderLayout(0, 10));
//			JSplitPane splitPane = new JSplitPane();
//			splitPane.setResizeWeight(1.0);
//			splitPane.setForeground(Color.BLACK);
//			splitPane.setBackground(Color.WHITE);
//			add(splitPane, BorderLayout.CENTER);
			
		}
		{
			JLabel lblChooseMoleculeSheets = new JLabel(panelName);
			lblChooseMoleculeSheets.setBackground(Color.WHITE);
			lblChooseMoleculeSheets.setVerticalAlignment(SwingConstants.TOP);
			lblChooseMoleculeSheets.setFont(new Font("Tahoma", Font.BOLD, 12));
			add(lblChooseMoleculeSheets, BorderLayout.NORTH);
		}
		{
			JPanel panel = new JPanel();
			add(panel, BorderLayout.CENTER);
			panel.setBorder(new EmptyBorder(0, 0, 0, 0));
			panel.setBackground(Color.WHITE);
			panel.setLayout(new BorderLayout(0, 0));
			{
				list = new JList(listModel);
				list.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				list.setMinimumSize(new Dimension(310,200));
				JScrollPane scrollPane = new JScrollPane(list);
				panel.add(scrollPane, BorderLayout.CENTER);
			}
			
		}
		{
			JPanel panel = new JPanel();
			add(panel, BorderLayout.EAST);
			panel.setBackground(Color.WHITE);
			panel.setBorder(new EmptyBorder(20, 10, 10, 5));
			panel.setMaximumSize(new Dimension(160,200));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] {85, 0};
			gbl_panel.rowHeights = new int[] {30, 30, 30, 0};
			gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.setFont(new Font("Tahoma", Font.BOLD, 11));
				btnAdd.setAction(action);
				GridBagConstraints gbc_btnAdd = new GridBagConstraints();
				gbc_btnAdd.fill = GridBagConstraints.BOTH;
				gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
				gbc_btnAdd.gridx = 0;
				gbc_btnAdd.gridy = 0;
				panel.add(btnAdd, gbc_btnAdd);
			}
			{
				JButton btnRemove = new JButton("Remove");
				btnRemove.setFont(new Font("Tahoma", Font.BOLD, 11));
				btnRemove.setAction(action_1);
				GridBagConstraints gbc_btnRemove = new GridBagConstraints();
				gbc_btnRemove.fill = GridBagConstraints.BOTH;
				gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
				gbc_btnRemove.gridx = 0;
				gbc_btnRemove.gridy = 1;
				panel.add(btnRemove, gbc_btnRemove);
			}
			{
				JButton btnClearList = new JButton("Clear List");
				btnClearList.setFont(new Font("Tahoma", Font.BOLD, 11));
				btnClearList.setAction(action_2);
				GridBagConstraints gbc_btnClearList = new GridBagConstraints();
				gbc_btnClearList.fill = GridBagConstraints.BOTH;
				gbc_btnClearList.gridx = 0;
				gbc_btnClearList.gridy = 2;
				panel.add(btnClearList, gbc_btnClearList);
			}
		}
	}
	
	public void clearOptions(){
		listModel.clear();
	}
	
	public ArrayList<String> getOptions(){
		ArrayList<String> files=new ArrayList<String>();
		int size=listModel.getSize();
		if(size==0){
			return files;
		}
		String option="-include=";
		for(int i=0;i<size;i++){
			option+=listModel.get(i)+",";
		}
		option=option.substring(0,option.length()-1);
		files.add(option);
		return files;
		
	}
	
	private void chooseFile(){
		JFileChooser chooser = new JFileChooser();
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Microsoft Excel Documents", "xls");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		String fileName=null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fileName = chooser.getSelectedFile().getAbsolutePath();
		}
		if(fileName!=null){
			if(!listModel.contains(fileName)){
				listModel.addElement(fileName);
			}
		}
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Add");
			putValue(SHORT_DESCRIPTION, "Add Molecule Sheets");
		}
		public void actionPerformed(ActionEvent e) {
			chooseFile();
			/*JFileChooser chooser = new JFileChooser();
			
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Microsoft Excel Documents", "xls");
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			String fileName=null;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				fileName = chooser.getSelectedFile().getAbsolutePath();
			}
			if(fileName!=null){
				if(!listModel.contains(fileName)){
					listModel.addElement(fileName);
				}
			}*/
		}
	}
	
	private class SwingAction_1 extends AbstractAction {
		
		public SwingAction_1() {
			putValue(NAME, "remove");
			putValue(SHORT_DESCRIPTION, "remove molecule sheet from test suite");
		}
		public void actionPerformed(ActionEvent e) {
			if(listModel.getSize()==0){
				
			}else{
				int pos=list.getSelectedIndex();
				if(pos!=-1){
					listModel.remove(pos);
				}
			}			
		}
	}
	
	private class SwingAction_2 extends AbstractAction {
		public SwingAction_2() {
			putValue(NAME, "clear");
			putValue(SHORT_DESCRIPTION, "remove all Sheets");
		}
		public void actionPerformed(ActionEvent e) {
			clearOptions();
		}
	}
	
	
	
	public static void main(String[] args) {
		try {
			JFrame frame=new JFrame();
			frame.setPreferredSize(new Dimension(400, 450));
			frame.setBounds(100,100,400,450);
			frame.getContentPane().add(new IncludePanel(), BorderLayout.CENTER);
			
			
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
