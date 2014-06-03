package com.automature.zug.gui;

import com.automature.zug.util.Log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;



public class OptionDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JPanel optionButtonpanel;
	private JPanel childConentPanel;
	private IncludePanel includePanel;
	private final ReportingOptionPanel reportingOptions;
	private TestCaseIDPanel tcIDPanel; 
	private OtherOptionPanel otherOptPanel;
 
	//private IncludePanel includePanel;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			OptionDialog dialog = new OptionDialog();
			dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			Log.Error(e.getMessage());
		}
	}

	/**
	 * Create the dialog.
	 */
	public OptionDialog() {
		setUndecorated(true);
		setBackground(Color.WHITE);
		getContentPane().setBackground(Color.WHITE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setBounds(100, 100, 650, 450);
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(20);
		getContentPane().setLayout(borderLayout);
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(20, 15, 15, 20)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			optionButtonpanel=new OptionButtonsPanel();
			contentPanel.add(optionButtonpanel,BorderLayout.WEST);
			
		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(Color.WHITE);
			buttonPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.setFont(new Font("Tahoma", Font.BOLD, 12));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
						updateValues();
					}
				});
				okButton.setActionCommand("Close");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
			//	buttonPane.add(cancelButton);
			}
		}
		{
			reportingOptions=new ReportingOptionPanel();
		}
		createPanels();
		ComponentMover mover=new ComponentMover(this);
	}
	
	private void updateValues(){
		reportingOptions.updateComboBoxOptions();
		otherOptPanel.updateComboBoxOptions();
	}
	
	public void createPanels(){
	//	System.out.println("OPD:"+SheetDisplayPane.testCaseIds);
		tcIDPanel=new TestCaseIDPanel(SheetDisplayPane.testCaseIds);
		includePanel=new IncludePanel();
		otherOptPanel=new OtherOptionPanel();
		viewIncludeSheetPanel();
	}

	public void updateChildPanel(JPanel panel){
		if(childConentPanel!=null){
			contentPanel.remove(childConentPanel);
		}
		childConentPanel=panel;
		contentPanel.add(childConentPanel,BorderLayout.CENTER);
		this.validate();
		this.repaint();
	}
	
	public void viewIncludeSheetPanel(){
		updateChildPanel(includePanel);
	}

	public void viewReportingPanel(){
		updateChildPanel(reportingOptions);
	}
	
	public void viewOtherOptionPanel(){
		otherOptPanel.refreshMacroinfo();
		updateChildPanel(otherOptPanel);
	}
	
	public void clearOption(){
		reportingOptions.clearOptions();
	}
	
	public ArrayList<String> getOptions(){
		ArrayList<String> options=new ArrayList<String>();
		options.addAll(includePanel.getOptions());
		//options.addAll(tcIDPanel.getOptions());
		options.addAll(reportingOptions.getOptions());
		options.addAll(otherOptPanel.getOptions());
		return options;
	}
	
	public class OptionButtonsPanel extends JPanel {
		private final Action action_1 = new SwingAction_1();
		private final Action action_2 = new SwingAction_2();
		private final Action action_3 = new SwingAction_3();

		/**
		 * Create the panel.
		 */
		public OptionButtonsPanel() {
			setBackground(SystemColor.WHITE);
			setBorder(new EmptyBorder(5, 10, 10, 15));
			setMaximumSize(new Dimension(160,200));

			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] {85, 0};
			gbl_panel.rowHeights = new int[] {0, 30, 30, 30, 0, 0, 0};
			gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			setLayout(gbl_panel);

			{
				JLabel lblZugOptions = new JLabel("ZUG Options");
				lblZugOptions.setForeground(Color.BLUE);
				lblZugOptions.setFont(new Font("Tahoma", Font.BOLD, 13));
				lblZugOptions.setHorizontalAlignment(SwingConstants.CENTER);
				GridBagConstraints gbc_lblZugOptions = new GridBagConstraints();
				gbc_lblZugOptions.ipady = 30;
				gbc_lblZugOptions.ipadx = 5;
				gbc_lblZugOptions.fill = GridBagConstraints.BOTH;
				gbc_lblZugOptions.insets = new Insets(0, 0, 5, 0);
				gbc_lblZugOptions.gridx = 0;
				gbc_lblZugOptions.gridy = 0;
				add(lblZugOptions, gbc_lblZugOptions);
			}

			{
				JButton btnRemove = new JButton("Include Files");
				btnRemove.setFont(new Font("Tahoma", Font.BOLD, 11));
				btnRemove.setAction(action_1);
				//	btnRemove.setAction(action_1);
				GridBagConstraints gbc_btnRemove = new GridBagConstraints();
				gbc_btnRemove.fill = GridBagConstraints.BOTH;
				gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
				gbc_btnRemove.gridx = 0;
				gbc_btnRemove.gridy = 2;
				add(btnRemove, gbc_btnRemove);
			}
			{
				JButton btnClearList = new JButton("Reporting");
				btnClearList.setFont(new Font("Tahoma", Font.BOLD, 11));
				btnClearList.setAction(action_2);
				//	btnClearList.setAction(action_2);
				GridBagConstraints gbc_btnClearList = new GridBagConstraints();
				gbc_btnClearList.insets = new Insets(0, 0, 5, 0);
				gbc_btnClearList.fill = GridBagConstraints.BOTH;
				gbc_btnClearList.gridx = 0;
				gbc_btnClearList.gridy = 3;
				add(btnClearList, gbc_btnClearList);
			}
			{
				JButton btnMacros = new JButton("Others");
				btnMacros.setFont(new Font("Tahoma", Font.BOLD, 11));
				btnMacros.setAction(action_3);
				GridBagConstraints gbc_btnMacros = new GridBagConstraints();
				gbc_btnMacros.insets = new Insets(0, 0, 5, 0);
				gbc_btnMacros.fill = GridBagConstraints.BOTH;
				gbc_btnMacros.gridx = 0;
				gbc_btnMacros.gridy = 4;
				add(btnMacros, gbc_btnMacros);
			}
			
		}

		private class SwingAction_1 extends AbstractAction {
			public SwingAction_1() {
				putValue(NAME, "Include Files");
				putValue(SHORT_DESCRIPTION, "Command line Include Files");
			}
			public void actionPerformed(ActionEvent e) {
				viewIncludeSheetPanel();
			}
		}
		private class SwingAction_2 extends AbstractAction {
			public SwingAction_2() {
				putValue(NAME, "Reporting");
				putValue(SHORT_DESCRIPTION, "Reporting options");
			}
			public void actionPerformed(ActionEvent e) {
				viewReportingPanel();
			}
		}
		private class SwingAction_3 extends AbstractAction {
			public SwingAction_3() {
				putValue(NAME, "Others");
				putValue(SHORT_DESCRIPTION, "Other options");
			}
			public void actionPerformed(ActionEvent e) {
				viewOtherOptionPanel();
			}
		}
	}
}
