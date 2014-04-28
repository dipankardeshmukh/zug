package com.automature.zug.gui.preference;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import com.automature.zug.gui.components.SmallImageButton;
import com.automature.zug.gui.model.LanguagePackage;

public class LanguagePanel extends JPanel {

	private JTextField textFieldExtensions;
	private JTextField textFieldOption;
	private JTextField textFieldInterpreter;
	private JTextField textFieldLanguage;
	private JTextField textFieldInterpreterPath;

	/**
	 * Create the panel.
	 */
	public LanguagePanel() {
		
		 this.setLayout(null);
		 this.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		 this.setBackground(Color.DARK_GRAY);
		 Dimension size = new Dimension(490, 240);
		 this.setSize(size);
		 this.setMinimumSize(size);
		 this.setPreferredSize(new Dimension(555, 240));
		// this.setBounds(22, thisInitialpos, 445, 191);

		 final JLabel labelInterpreterPath = new JLabel("Interpreter path");
		 labelInterpreterPath.setForeground(Color.LIGHT_GRAY);
		 labelInterpreterPath.setFont(new Font("Tahoma", Font.PLAIN, 11));
		 labelInterpreterPath.setBounds(63, 101, 79, 14);
		 this.add(labelInterpreterPath);

		 textFieldInterpreterPath = new JTextField();
		 textFieldInterpreterPath.setColumns(10);
		 textFieldInterpreterPath.setBounds(165, 98, 245, 22);
		//
		 this.add(textFieldInterpreterPath);
		 SmallImageButton btnInterpreterPath = new SmallImageButton("");
		 btnInterpreterPath.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 final JFileChooser fc = new JFileChooser();
				 fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 int return_val=fc.showOpenDialog(null);
				 if (return_val == JFileChooser.APPROVE_OPTION) {
					 File file = fc.getSelectedFile();
					 textFieldInterpreterPath.setText(file.getAbsolutePath());

				 } else {
					// System.out.println("Open command cancelled by user.");
				 }
			 }
		 });
		/* btnInterpreterPath.setForeground(Color.LIGHT_GRAY);
		 btnInterpreterPath.setBackground(Color.DARK_GRAY);
		 btnInterpreterPath.setBounds(422, 94, 79, 23);*/
		 btnInterpreterPath.setLocation(422, 94);
		 btnInterpreterPath.setIcon(new ImageIcon(System.getProperty("user.dir")+File.separator+"Images"+File.separator+"browse1.png"));
			btnInterpreterPath.setToolTipText("Open Directory");
			//btnInterpreterPath.setSize(30, 28);
		 this.add(btnInterpreterPath);

		 JLabel labelLanguage = new JLabel("Language");
		 labelLanguage.setForeground(Color.LIGHT_GRAY);
		 labelLanguage.setFont(new Font("Tahoma", Font.BOLD, 13));
		 labelLanguage.setBounds(18, 35, 71, 19);
		 this.add(labelLanguage);

		 textFieldLanguage = new JTextField();
		 textFieldLanguage.setBackground(Color.WHITE);
		 textFieldLanguage.setColumns(10);
		 textFieldLanguage.setBounds(120, 32, 370, 22);
		 //
		 this.add(textFieldLanguage);

		 final SmallImageButton btnRemoveLanguage = new SmallImageButton();
		// btnRemoveLanguage.setIcon(new ImageIcon(OutprocessPanelOnLoad.class.getResource("/com/automature/zug/zugINI/minus.png")));
		 btnRemoveLanguage.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent arg0) {
				 Container con = LanguagePanel.this.getParent();
					con.remove(LanguagePanel.this);
					con.getParent().getParent().validate();
					con.getParent().getParent().repaint();
			 }
		 });
		/* btnRemoveLanguage.setFont(new Font("Tahoma", Font.BOLD, 14));
		 btnRemoveLanguage.setForeground(Color.DARK_GRAY);
		 btnRemoveLanguage.setBackground(Color.DARK_GRAY);
		 btnRemoveLanguage.setBounds(500, 30, 30, 28);*/
		 btnRemoveLanguage.setLocation(500, 30);
		 btnRemoveLanguage.setToolTipText("Delete");
		 btnRemoveLanguage.setIcon(new ImageIcon(System.getProperty("user.dir")+File.separator+"Images"+File.separator+"delete.png"));
		
			
		 this.add(btnRemoveLanguage);

		 JLabel labelInterpreter = new JLabel("Interpreter");
		 labelInterpreter.setForeground(Color.LIGHT_GRAY);
		 labelInterpreter.setFont(new Font("Tahoma", Font.PLAIN, 11));
		 labelInterpreter.setBounds(63, 71, 71, 14);
		 this.add(labelInterpreter);

		 textFieldInterpreter = new JTextField();
		 textFieldInterpreter.setColumns(10);
		 textFieldInterpreter.setBounds(165, 68, 287, 22);
		// 
		 this.add(textFieldInterpreter);

		 JLabel labelOption = new JLabel("Option");
		 labelOption.setForeground(Color.LIGHT_GRAY);
		 labelOption.setFont(new Font("Tahoma", Font.PLAIN, 11));
		 labelOption.setBounds(63, 129, 71, 14);
		 this.add(labelOption);

		 textFieldOption = new JTextField();
		 textFieldOption.setColumns(10);
		 textFieldOption.setBounds(165, 126, 287, 22);
		 //
		 this.add(textFieldOption);

		 JLabel labelExtensions = new JLabel("Extensions");
		 labelExtensions.setForeground(Color.LIGHT_GRAY);
		 labelExtensions.setFont(new Font("Tahoma", Font.PLAIN, 11));
		 labelExtensions.setBounds(63, 157, 71, 14);
		 this.add(labelExtensions);

		 textFieldExtensions = new JTextField();
		 textFieldExtensions.setColumns(10);
		 textFieldExtensions.setBounds(165, 154, 287, 22);
		// 
		 this.add(textFieldExtensions);
		 this.add(new JButton());

		// return this;

	}
	
	public LanguagePanel(LanguagePackage lp){
		this();
		 textFieldInterpreterPath.setText(lp.getPath());
		 textFieldLanguage.setText(lp.getLanguage());
		 textFieldInterpreter.setText(lp.getInterpreter());
		 textFieldOption.setText(lp.getOptions());
		 textFieldExtensions.setText(lp.getExtension());
	}
	
	public LanguagePackage getLanguagePackage() {
		LanguagePackage lp=new LanguagePackage();
		lp.setExtension(textFieldExtensions.getText());
		lp.setInterpreter(textFieldInterpreter.getText());
		lp.setLanguage(textFieldLanguage.getText());
		lp.setOptions(textFieldOption.getText());
		lp.setPath(textFieldInterpreterPath.getText());
		return lp;
		
	}

}
