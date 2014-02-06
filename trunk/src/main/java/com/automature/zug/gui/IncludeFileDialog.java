package com.automature.zug.gui;


import com.automature.zug.util.Log;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JDesktopPane;
import javax.swing.JList;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.Color;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.SystemColor;
import javax.swing.border.BevelBorder;

public class IncludeFileDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	JList list;
	DefaultListModel listModel= new DefaultListModel();
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private final Action action_2 = new SwingAction_2();
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			IncludeFileDialog dialog = new IncludeFileDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			ComponentMover cmpMover=new ComponentMover(dialog);
		} catch (Exception e) {
			Log.Error(e.getMessage());
		}
	}

	/**
	 * Create the dialog.
	 */
	public IncludeFileDialog() {
		setUndecorated(true);
		getContentPane().setBackground(Color.BLACK);
		setBounds(100, 100, 480, 380);
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(1);
		getContentPane().setLayout(borderLayout);
		contentPanel.setBackground(SystemColor.control);
		contentPanel.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(20, 15, 15, 15)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			splitPane.setResizeWeight(1.0);
			splitPane.setForeground(Color.BLACK);
			splitPane.setBackground(Color.WHITE);
			contentPanel.add(splitPane, BorderLayout.CENTER);
			{
				JPanel panel = new JPanel();
				panel.setBackground(SystemColor.control);
				panel.setBorder(new EmptyBorder(20, 10, 10, 5));
				panel.setMaximumSize(new Dimension(160,200));
				splitPane.setRightComponent(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[] {85, 0};
				gbl_panel.rowHeights = new int[] {30, 30, 30, 0};
				gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					JButton btnAdd = new JButton("Add");
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
					btnClearList.setAction(action_2);
					GridBagConstraints gbc_btnClearList = new GridBagConstraints();
					gbc_btnClearList.fill = GridBagConstraints.BOTH;
					gbc_btnClearList.gridx = 0;
					gbc_btnClearList.gridy = 2;
					panel.add(btnClearList, gbc_btnClearList);
				}
			}
			{
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				splitPane.setLeftComponent(panel);
				panel.setLayout(new BorderLayout(0, 0));
				{
					list = new JList(listModel);
					list.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
					list.setMinimumSize(new Dimension(310,200));
					panel.add(list);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(10, 20, 10, 20)));
			buttonPane.setBackground(SystemColor.control);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Add");
			putValue(SHORT_DESCRIPTION, "Add Molecule Sheets");
		}
		public void actionPerformed(ActionEvent e) {
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
		}
	}
}
