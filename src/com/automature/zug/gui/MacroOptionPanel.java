package com.automature.zug.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class MacroOptionPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public MacroOptionPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblMacroColumn = new JLabel("Macro Column");
		GridBagConstraints gbc_lblMacroColumn = new GridBagConstraints();
		gbc_lblMacroColumn.insets = new Insets(0, 0, 5, 0);
		gbc_lblMacroColumn.gridx = 0;
		gbc_lblMacroColumn.gridy = 0;
		add(lblMacroColumn, gbc_lblMacroColumn);

	}

}
