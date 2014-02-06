package com.automature.zug.gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;

import com.automature.zug.util.Log;
import org.apache.commons.lang.StringUtils;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JRadioButton;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class ReportingOptionPanel extends JPanel {

	JPanel contentPanel=new JPanel();
	HashMap <String,ArrayList<String>> options;
	private JComboBox comboBox_2;
	private JComboBox comboBox; 
	String optionLabels[]={"topologysetid","testplanid","testcycleid"};
	private final Action action = new SwingAction();
	private JComboBox comboBox_1;
	private JCheckBox rdbtnNewRadioButton;
	private JLabel lblTopologySetName;
	private JComboBox comboBox_3;
	private JLabel lblBuildTag;
	private JComboBox comboBox_4;
	/**
	 * Create the panel.
	 */
	public ReportingOptionPanel() {
		setBackground(Color.WHITE);

		setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(20, 15, 15, 15)));
		setLayout(new BorderLayout(0, 10));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {100, 150, 0};
		gridBagLayout.rowHeights = new int[] {45, 45, 45, 45, 45, 45, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		contentPanel.setLayout(gridBagLayout);
		
		JLabel lblTestPlan = new JLabel("Test Plan");
		lblTestPlan.setToolTipText("e.g: <Product Name>:<Release Name>:<Sprint Name>:<Build Tag>:<Testplan Name>");
		lblTestPlan.setHorizontalAlignment(SwingConstants.CENTER);
		lblTestPlan.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTestPlan.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblTestPlan = new GridBagConstraints();
		gbc_lblTestPlan.fill = GridBagConstraints.VERTICAL;
		gbc_lblTestPlan.insets = new Insets(0, 0, 5, 5);
		gbc_lblTestPlan.gridx = 0;
		gbc_lblTestPlan.gridy = 0;
		contentPanel.add(lblTestPlan, gbc_lblTestPlan);
		
		comboBox_1 = new JComboBox();
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridx = 1;
		gbc_comboBox_1.gridy = 0;
		contentPanel.add(comboBox_1, gbc_comboBox_1);
		comboBox_1.setBackground(Color.WHITE);
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
		
		lblTopologySetName = new JLabel("Topology Set Name");
		lblTopologySetName.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTopologySetName.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblTopologySetName = new GridBagConstraints();
		gbc_lblTopologySetName.anchor = GridBagConstraints.EAST;
		gbc_lblTopologySetName.insets = new Insets(0, 0, 5, 5);
		gbc_lblTopologySetName.gridx = 0;
		gbc_lblTopologySetName.gridy = 1;
		contentPanel.add(lblTopologySetName, gbc_lblTopologySetName);
		
		comboBox_3 = new JComboBox();
		comboBox_3.setBackground(Color.WHITE);
		comboBox_3.setEditable(true);
		comboBox_3.addItem("");
		comboBox_3.setUI(new BasicComboBoxUI() {
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
		comboBox_3.setBorder(new LineBorder(getForeground()));
		GridBagConstraints gbc_comboBox_3 = new GridBagConstraints();
		gbc_comboBox_3.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_3.gridx = 1;
		gbc_comboBox_3.gridy = 1;
		

		contentPanel.add(comboBox_3, gbc_comboBox_3);
		
		lblBuildTag = new JLabel("Build Tag");
		lblBuildTag.setHorizontalAlignment(SwingConstants.CENTER);
		lblBuildTag.setBackground(Color.WHITE);
		lblBuildTag.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblBuildTag = new GridBagConstraints();
		gbc_lblBuildTag.fill = GridBagConstraints.BOTH;
		gbc_lblBuildTag.insets = new Insets(0, 0, 5, 5);
		gbc_lblBuildTag.gridx = 0;
		gbc_lblBuildTag.gridy = 2;
		contentPanel.add(lblBuildTag, gbc_lblBuildTag);
		
		comboBox_4 = new JComboBox();
		comboBox_4.setBackground(Color.WHITE);
		comboBox_4.setEditable(true);
		comboBox_4.addItem("");
		comboBox_4.setUI(new BasicComboBoxUI() {
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
		comboBox_4.setBorder(new LineBorder(getForeground()));
		GridBagConstraints gbc_comboBox_4 = new GridBagConstraints();
		gbc_comboBox_4.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_4.gridx = 1;
		gbc_comboBox_4.gridy = 2;
		contentPanel.add(comboBox_4, gbc_comboBox_4);
		

		
		
		rdbtnNewRadioButton = new JCheckBox("Existing test cycle");
		rdbtnNewRadioButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		rdbtnNewRadioButton.setForeground(Color.BLUE);
		rdbtnNewRadioButton.setVerticalAlignment(SwingConstants.BOTTOM);
		rdbtnNewRadioButton.setToolTipText("Report to an existing test cycle");
		rdbtnNewRadioButton.setBackground(Color.WHITE);
		rdbtnNewRadioButton.setAction(action);
		GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
		gbc_rdbtnNewRadioButton.ipady = 5;
		gbc_rdbtnNewRadioButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton.gridx = 0;
		gbc_rdbtnNewRadioButton.gridy = 3;
		contentPanel.add(rdbtnNewRadioButton, gbc_rdbtnNewRadioButton);

		JLabel lblNewLabel = new JLabel("Topology Set ID");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 4;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);

		comboBox = new JComboBox();
		comboBox.setBackground(Color.WHITE);
		comboBox.setEditable(true);
		comboBox.addItem("");
		comboBox.setUI(new BasicComboBoxUI() {
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
		comboBox.setBorder(new LineBorder(getForeground()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 4;
		contentPanel.add(comboBox, gbc_comboBox);

		JLabel lblNewLabel_2 = new JLabel("Test Cycle ID");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 5;
		contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);

		comboBox_2 = new JComboBox();
		comboBox_2.setEditable(true);
		comboBox_2.addItem("");
		comboBox_2.setUI(new BasicComboBoxUI() {
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
		comboBox_2.setBorder(new LineBorder(getForeground()));
		GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
		gbc_comboBox_2.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_2.gridx = 1;
		gbc_comboBox_2.gridy = 5;
		contentPanel.add(comboBox_2, gbc_comboBox_2);

		JLabel lblReportingOptions = new JLabel("Reporting Options");
		lblReportingOptions.setBackground(SystemColor.control);
		lblReportingOptions.setFont(new Font("Tahoma", Font.BOLD, 13));
		add(lblReportingOptions, BorderLayout.NORTH);
		disableExisitingTestCycleOption();

	}
	public void initializeMap(){
		options=new HashMap <String,ArrayList<String>>();

		for(String prop:optionLabels){
			ArrayList<String> al=new ArrayList<String>();
			al.add("");
			options.put(prop, al);
		}
	}

	public ArrayList<String> getOptions(){

		ArrayList<String> opt=new ArrayList<String>();
		if(rdbtnNewRadioButton.isSelected()){
			String option=comboBox.getSelectedItem().toString();
			if(option!=null && StringUtils.isNotBlank(option)){
				opt.add("-topologysetid="+option);
			}			
			option=comboBox_2.getSelectedItem().toString();
			if(option!=null && StringUtils.isNotBlank(option)){
				opt.add("-testcycleid="+option);
			}
		}else{
			String option=comboBox_1.getSelectedItem().toString();
			if(option!=null && StringUtils.isNotBlank(option)){
				opt.add("-testplan=\""+option+"\"");
			}
			option=comboBox_3.getSelectedItem().toString();
			if(option!=null && StringUtils.isNotBlank(option)){
				opt.add("-topologyset="+option);
			}
			option=comboBox_4.getSelectedItem().toString();
			if(option!=null && StringUtils.isNotBlank(option)){
				opt.add("-buildtag="+option);
			}
		}
		//saveNewValues();
		return opt;
	}

	public void updateComboBoxOptions(){

		updateCBValues(comboBox);
		updateCBValues(comboBox_1);
		updateCBValues(comboBox_2);
		updateCBValues(comboBox_3);
		updateCBValues(comboBox_4);

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


	/*	private void updateListValues(JComboBox cbox,ArrayList al){
		String value=cbox.getSelectedItem().toString();
		if(value!=null && !value.isEmpty() && StringUtils.isNotBlank(value) && !al.contains(value)){
			al.add(value);
		}
	}

	public void saveNewValues(){

		ArrayList<String> al=options.get(optionLabels[0]);
		updateListValues(comboBox, al);
		al=options.get(optionLabels[1]);
		updateListValues(comboBox_1, al);
		al=options.get(optionLabels[2]);
		updateListValues(comboBox_2, al);
	}*/

	public void clearOptions(){

		comboBox.setSelectedIndex(0);
		comboBox_1.setSelectedIndex(0);
		comboBox_2.setSelectedIndex(0);
		comboBox_3.setSelectedIndex(0);
		comboBox_4.setSelectedIndex(0);

	}

	/*public boolean validateOptions(){

		String topologysetID=comboBox.getSelectedItem().toString();
		//String testPlanID=comboBox_1.getSelectedItem().toString();
		String testCycleID=comboBox_2.getSelectedItem().toString();
		if((topologysetID.isEmpty()||StringUtils.isNotBlank(topologysetID))&&(StringUtils.isNotBlank(testCycleID))){
			return false;
		}else
			return true;
	}*/

	public static void main(String[] args) {
		try {
			final JFrame frame=new JFrame();
			frame.setPreferredSize(new Dimension(400, 450));
			frame.setBounds(100,100,400,450);
			final ReportingOptionPanel reportingOpts=new ReportingOptionPanel();
			frame.getContentPane().add(reportingOpts, BorderLayout.CENTER);
			JButton button=new JButton("view optionssss");
			frame.getContentPane().add(button,BorderLayout.SOUTH);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					reportingOpts.updateComboBoxOptions();
					frame.validate();
					frame.repaint();
				}
			});
			frame.setVisible(true);
		} catch (Exception e) {
			Log.Error(e.getMessage());
		}
	}
	
	private void enableTestPlan(){
		comboBox_1.setEnabled(true);
		comboBox_3.setEnabled(true);
		comboBox_4.setEnabled(true);
	}
	
	private void disableTestPlan(){
		comboBox_1.setEnabled(false);
		comboBox_3.setEnabled(false);
		comboBox_4.setEnabled(false);
	}
	
	private void enableExisitingTestCycleOption(){
		comboBox.setEnabled(true);
		comboBox_2.setEnabled(true);
	}

	private void disableExisitingTestCycleOption(){
		comboBox.setEnabled(false);
		comboBox_2.setEnabled(false);
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Existing test cycle");
			putValue(SHORT_DESCRIPTION, "Report to an existing test cycle");
		}
		public void actionPerformed(ActionEvent e) {
			JCheckBox rb=(JCheckBox)e.getSource();
			if(rb.isSelected()){
				enableExisitingTestCycleOption();
				disableTestPlan();
			}else{
				disableExisitingTestCycleOption();
				enableTestPlan();
				
			}
		}
	}
}
