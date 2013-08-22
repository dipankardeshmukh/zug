package com.automature.zug.gui;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class TestCaseIDPanel extends JPanel {


	//DefaultListModel listModel= new DefaultListModel();
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnNewRadioButton;
	private JList list;
	private JLabel lblTestCases;
	private JPanel panel_1;
	private JButton btnNewButton;
	private JButton btnClear;
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	/**
	 * Create the panel.
	 */
	public TestCaseIDPanel(ArrayList<String> testCaseID) {
		setBackground(Color.WHITE);
		setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(20, 15, 15, 15)));

		setLayout(new BorderLayout(0, 10));

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.SOUTH);

		rdbtnNewRadioButton = new JRadioButton("Include");
		rdbtnNewRadioButton.setBackground(Color.WHITE);
		rdbtnNewRadioButton.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnNewRadioButton.setSelected(true);
		panel.add(rdbtnNewRadioButton);

		rdbtnNewRadioButton_1 = new JRadioButton("Exclude");
		rdbtnNewRadioButton_1.setBackground(Color.WHITE);
		rdbtnNewRadioButton_1.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(rdbtnNewRadioButton_1);

		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnNewRadioButton_1);
		group.add(rdbtnNewRadioButton);
		

		list = new JList();
		list.setForeground(Color.BLUE);
		list.setFont(new Font("Tahoma", Font.BOLD, 11));
		list.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		if(testCaseID!=null && testCaseID.size()!=0){
			Vector <CheckListItem> v=new Vector<CheckListItem>();
			for(String id:testCaseID){
				if(id.equalsIgnoreCase("init")||id.equalsIgnoreCase("cleanup")){
					continue;
				}
				CheckListItem jcb=new CheckListItem(id);
				v.add(jcb);
			}
			list.setListData(v);
		}else{
			//System.out.println("id is null");
		}
		list.setCellRenderer(new CheckListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent event){
				JList list = (JList) event.getSource();
				int index = list.locationToIndex(event.getPoint());
				CheckListItem item = (CheckListItem)
						list.getModel().getElementAt(index);
				item.setSelected(! item.isSelected());
				list.repaint(list.getCellBounds(index, index));
			}
		});
		JScrollPane scrollPane=new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);
		
		lblTestCases = new JLabel("Test Cases");
		lblTestCases.setBackground(Color.WHITE);
		lblTestCases.setVerticalAlignment(SwingConstants.TOP);
		lblTestCases.setMinimumSize(new Dimension(171, 80));
		lblTestCases.setFont(new Font("Tahoma", Font.BOLD, 12));
		add(lblTestCases, BorderLayout.NORTH);
		
		panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBorder(new EmptyBorder(0, 8, 0, 0));
		add(panel_1, BorderLayout.EAST);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		btnNewButton = new JButton("Select All");
		btnNewButton.setAction(action);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.ipady = 5;
		gbc_btnNewButton.fill = GridBagConstraints.BOTH;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		panel_1.add(btnNewButton, gbc_btnNewButton);
		
		btnClear = new JButton("Deselect all");
		btnClear.setAction(action_1);
		btnClear.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.ipady = 5;
		gbc_btnClear.fill = GridBagConstraints.BOTH;
		gbc_btnClear.gridx = 0;
		gbc_btnClear.gridy = 1;
		panel_1.add(btnClear, gbc_btnClear);
	}

	public ArrayList<String> getOptions(){
		ArrayList<String> files=new ArrayList<String>();
		String option;
		int size=list.getModel().getSize();
		if(size==0){
			return files;
		}
		option=rdbtnNewRadioButton.isSelected()?"-testcaseid=":"-excludetestcaseid=";
		for(int i=0;i<size;i++){
			CheckListItem item=(CheckListItem)list.getModel().getElementAt(i);
			if(item.isSelected()){
				option+=item.toString()+",";
			}
		}
		option=option.substring(0,option.length()-1);
		files.add(option);
		return files;
	}
	
	class CheckListItem{
		private String  label;
		private boolean isSelected = false;

		public CheckListItem(String label){
			this.label = label;
		}
		public boolean isSelected(){
			return isSelected;
		}
		public void setSelected(boolean isSelected){
			this.isSelected = isSelected;
		}
		public String toString(){
			return label;
		}
	}
	
	class CheckListRenderer extends JCheckBox  implements ListCellRenderer{
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus)
		{
			setEnabled(list.isEnabled());
			setSelected(((CheckListItem)value).isSelected());
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(value.toString());
			setBorder(new EmptyBorder(5, 3,3, 0));
			return this;
		}
	}

	public static void main(String[] args) {
		try {
			JFrame frame=new JFrame();
			frame.setPreferredSize(new Dimension(400, 450));
			frame.setBounds(100,100,400,450);
			ArrayList<String> al=new ArrayList<String>();
			al.add("abcef");
			al.add("abcef");
			frame.getContentPane().add(new TestCaseIDPanel(al), BorderLayout.CENTER);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void SetSelectionForAll(boolean selected){
		int size=list.getModel().getSize();
		if(size==0){
			return ;
		}
		for(int i=0;i<size;i++){
			CheckListItem item=(CheckListItem)list.getModel().getElementAt(i);
			item.setSelected(selected);
		}
		this.repaint();
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Select All");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			SetSelectionForAll(true);
			
		}
	}
	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "Deselect all");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			SetSelectionForAll(false);
		}
	}
}
