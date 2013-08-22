package com.automature.zug.gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.SystemColor;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;

public class OptionButtonsPanel extends JPanel {
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private final Action action_2 = new SwingAction_2();
	private final Action action_3 = new SwingAction_3();

	/**
	 * Create the panel.
	 */
	public OptionButtonsPanel() {
		setBackground(SystemColor.control);
		setBorder(new EmptyBorder(15, 10, 10, 5));
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
			JButton btnAdd = new JButton("Test Cases");
			btnAdd.setFont(new Font("Tahoma", Font.BOLD, 11));
			btnAdd.setAction(action);
			//	btnAdd.setAction(action);
			GridBagConstraints gbc_btnAdd = new GridBagConstraints();
			gbc_btnAdd.fill = GridBagConstraints.BOTH;
			gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
			gbc_btnAdd.gridx = 0;
			gbc_btnAdd.gridy = 1;
			add(btnAdd, gbc_btnAdd);
		}
		{
			JButton btnRemove = new JButton("Include Sheets");
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

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Test Cases");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "Include Sheets");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class SwingAction_2 extends AbstractAction {
		public SwingAction_2() {
			putValue(NAME, "Reporting");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class SwingAction_3 extends AbstractAction {
		public SwingAction_3() {
			putValue(NAME, "Others");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}


