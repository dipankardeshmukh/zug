package com.automature.zug.gui;

import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;

import com.automature.zug.gui.components.MacroColumns;
import com.automature.zug.gui.sheets.SpreadSheet;
import com.automature.zug.util.Log;

import org.apache.commons.lang.StringUtils;

public class OtherOptionPanel extends JPanel {
	
	private JTextField textField_1;
	String []repeatDurationsUnit={"day","hour","minute","seconds"};
	private final Action action = new SwingAction();
	private JCheckBox chckbxRepeat;
	private JRadioButton rdbtnCount;
	private JRadioButton rdbtnDuration;
	private JSpinner spinner;
	private JSpinner spinner_1;
	private JComboBox comboBox_2;
	private JComboBox comboBox_1;
	private final Action action_1 = new SwingAction_1();
	private final Action action_2 = new SwingAction_2();
	private final Action action_3 = new SwingAction_3();
	private MacroColumnPanel panel;
	

	/**
	 * Create the panel.
	 */
	public OtherOptionPanel() {
		setBackground(Color.WHITE);
		setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(10, 10, 10, 10)));
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(10, 0, 10, 0));
		panel_2.setBackground(Color.WHITE);
		panel_1.add(panel_2, BorderLayout.NORTH);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] {0, 0};
		gbl_panel_2.rowHeights = new int[] {30, 30, 30};
		gbl_panel_2.columnWeights = new double[]{0.0, 1.0};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel lblAtomPath = new JLabel("Atom Path");
		GridBagConstraints gbc_lblAtomPath = new GridBagConstraints();
		gbc_lblAtomPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblAtomPath.gridx = 0;
		gbc_lblAtomPath.gridy = 0;
		panel_2.add(lblAtomPath, gbc_lblAtomPath);
		
		textField_1 = new JTextField();
		textField_1.setBackground(Color.WHITE);
		textField_1.setEditable(false);
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setColumns(10);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 0;
		panel_2.add(textField_1, gbc_textField_1);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setAction(action_3);
		btnBrowse.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.anchor = GridBagConstraints.EAST;
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridx = 1;
		gbc_btnBrowse.gridy = 1;
		panel_2.add(btnBrowse, gbc_btnBrowse);
		
		JLabel lblNewLabel_2 = new JLabel("Macro/Other");
		lblNewLabel_2.setToolTipText("Maco values and other values can be send e.g, -$test=Testing -logfileName=tuesday-16-05-13");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_2.ipady = 5;
		gbc_lblNewLabel_2.ipadx = 5;
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setEditable(true);
		comboBox_1.addItem("");
		comboBox_1.setUI(new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				return new JButton() {
					@Override
					public int getWidth() {
						return 0;
					}
				};
			}
		});
		comboBox_1.setBorder(new LineBorder(getForeground()));
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.ipady = 5;
		gbc_comboBox_1.ipadx = 5;
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridx = 1;
		gbc_comboBox_1.gridy = 2;
		panel_2.add(comboBox_1, gbc_comboBox_1);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(null);
		panel_3.setMinimumSize(new Dimension(200, 100));
		panel_3.setPreferredSize(new Dimension(200, 100));
		chckbxRepeat = new JCheckBox("Repeat");
		chckbxRepeat.setAction(action);
		chckbxRepeat.setBounds(6, 7, 97, 23);
		panel_3.add(chckbxRepeat);
		
		rdbtnCount = new JRadioButton("Count");
		rdbtnCount.setAction(action_1);
	//	rdbtnCount.setSelected(true);
		rdbtnCount.setEnabled(false);
		rdbtnCount.setBounds(26, 33, 60, 23);
		panel_3.add(rdbtnCount);
		
		spinner = new JSpinner();
		spinner.setEnabled(false);
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinner.setBounds(105, 33, 54, 23);
		panel_3.add(spinner);
		
		rdbtnDuration = new JRadioButton("Duration");
		rdbtnDuration.setAction(action_2);
		rdbtnDuration.setEnabled(false);
		rdbtnDuration.setBounds(26, 70, 76, 23);
		panel_3.add(rdbtnDuration);
		
		spinner_1 = new JSpinner();
		spinner_1.setEnabled(false);
		spinner_1.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinner_1.setBounds(105, 67, 54, 23);
		panel_3.add(spinner_1);
		
		comboBox_2 = new JComboBox(repeatDurationsUnit);
		comboBox_2.setEnabled(false);
		comboBox_2.setEditable(true);
		comboBox_2.setBounds(169, 67, 127, 23);
		panel_3.add(comboBox_2);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnCount);
		group.add(rdbtnDuration);
		
		  panel = new MacroColumnPanel();
		panel.setBackground(Color.WHITE);
		panel_1.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		

	}
	
	
	void refreshMacroinfo(){
		List<MacroColumns> l=new ArrayList<MacroColumns>();
		if(ZugGUI.spreadSheet!=null){
		 Map<String,SpreadSheet> sheetmap=ZugGUI.spreadSheet.getUniqueSheets();
		 Iterator<String> it=sheetmap.keySet().iterator();
		 while(it.hasNext()){
			 
			 String name=it.next();
			 List<String> list=sheetmap.get(name).getMacroSheet().getHeader();
			 l.add(new MacroColumns(name, list)); 
		 }
		// System.out.println(l);
		 List<String> list=ZugGUI.spreadSheet.getMacroSheet().getHeader();
		panel.refreshData(new MacroColumns(ZugGUI.spreadSheet.getFileName(), list),l);
		}
		
	}
	private void enableRepeatCountOption(){
		spinner.setEnabled(true);
	}
	
	private void disableRepeatCountOption(){
		spinner.setEnabled(false);
	}
	private void enableRepeatDurationOption(){
		spinner_1.setEnabled(true);
		comboBox_2.setEnabled(true);
	}
	
	private void disableRepeatDurationOption(){
		spinner_1.setEnabled(false);
		comboBox_2.setEnabled(false);
	}
	
	private void enableRepeatOptions(){
		rdbtnCount.setEnabled(true);
		if(rdbtnCount.isSelected()){
			enableRepeatCountOption();
		}
		else if(rdbtnDuration.isSelected()){
			enableRepeatDurationOption();
		}
		rdbtnDuration.setEnabled(true);
	}
	
	private void disableRepeatOptions(){
		rdbtnCount.setEnabled(false);
		disableRepeatCountOption();
		rdbtnDuration.setEnabled(false);
		disableRepeatDurationOption();
	}
	
	public void chooseDirectory(){
		JFileChooser chooser=new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal==JFileChooser.APPROVE_OPTION){
			textField_1.setText(chooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void updateCBValues(JComboBox cbox){
		String value=cbox.getSelectedItem().toString();
		cbox.setSelectedIndex(0);
		if(value!=null && !value.isEmpty() && StringUtils.isNotBlank(value)){
			cbox.setSelectedItem(value);   
			if(cbox.getSelectedIndex()==-1){ 
				cbox.addItem(value);
			}
		}
	}
	
	public void updateComboBoxOptions(){
		//updateCBValues(comboBox);
		updateCBValues(comboBox_1);
	}
	
	public ArrayList<String> getOptions(){
		
		ArrayList<String> options=new ArrayList<String>();
		String atomPath=textField_1.getText();
		if(atomPath!=null && StringUtils.isNotBlank(atomPath)){
			options.add("-atompath="+atomPath);
		}
		String op=panel.getOptions();
		
		if(op!=null&&!op.isEmpty()){
			options.add(op);
		}
		/*String macroCol=(String)comboBox.getSelectedItem();
		if(macroCol!=null && StringUtils.isNotBlank(macroCol)){
			options.add("-macroColumn="+macroCol);
			updateCBValues(comboBox);
		}*/
		String others=(String)comboBox_1.getSelectedItem();
		if(others!=null && StringUtils.isNotBlank(others)){
			String str[]=others.split(" -");
			for(String val:str){
				options.add(val.startsWith("-")?val:"-"+val);
			}
			updateCBValues(comboBox_1);
		}
		if(chckbxRepeat.isSelected()){
			if(rdbtnCount.isSelected()){
				int n=(Integer)spinner.getValue();
				options.add("-repeat");
				options.add("-count="+n);
			}else if(rdbtnDuration.isSelected()){
				int n=(Integer)spinner.getValue();
				String unit=(String)comboBox_2.getSelectedItem();
				options.add("-repeat");
				if(unit.equals(repeatDurationsUnit[0])){
					unit=n+"d";
				}else if(unit.equals(repeatDurationsUnit[1])){
					unit=n+"h";
				}
				else if(unit.equals(repeatDurationsUnit[0])){
					unit=n+"m";
				}else {
					unit=n+"s";
				}
				options.add("-Duration="+unit);
			}	
		}
		return options;
	}
	
	public static void main(String[] args) {
		try {
			JFrame frame=new JFrame();
			frame.setPreferredSize(new Dimension(400, 450));
			frame.setBounds(100,100,400,450);
			final OtherOptionPanel opp=new OtherOptionPanel();
			frame.getContentPane().add(opp, BorderLayout.CENTER);
			JButton button=new JButton("view optionssss");
			frame.getContentPane().add(button,BorderLayout.SOUTH);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("button pressed");
					System.out.println("options"+opp.getOptions().toString());
				}
			});
			frame.setVisible(true);
		} catch (Exception e) {
			Log.Error(e.getMessage());
		}
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Repeat");
			putValue(SHORT_DESCRIPTION, "");
		}
		public void actionPerformed(ActionEvent e) {
			if(chckbxRepeat.isSelected()){
				enableRepeatOptions();
			}else{
				disableRepeatOptions();
			}
		}
	}
	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "Count");
			putValue(SHORT_DESCRIPTION, "");
		}
		public void actionPerformed(ActionEvent e) {
			if(rdbtnCount.isSelected()){
				enableRepeatCountOption();
				disableRepeatDurationOption();
			}
			else{
//				disableRepeatCountOption();
			}
		}
	}
	private class SwingAction_2 extends AbstractAction {
		public SwingAction_2() {
			putValue(NAME, "Duration");
			putValue(SHORT_DESCRIPTION, "");
		}
		public void actionPerformed(ActionEvent e) {
			if(rdbtnDuration.isSelected()){
				enableRepeatDurationOption();
				disableRepeatCountOption();
			}else{
	//			disableRepeatDurationOption();
			}
		}
	}
	private class SwingAction_3 extends AbstractAction {
		public SwingAction_3() {
			putValue(NAME, "Browse..");
			putValue(SHORT_DESCRIPTION, "Choose atom path (scripts location directory)");
		}
		public void actionPerformed(ActionEvent e) {
			chooseDirectory();
		}
	}
}
