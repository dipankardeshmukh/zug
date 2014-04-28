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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.automature.zug.gui.components.SmallImageButton;
import com.automature.zug.gui.model.InprocessPackage;
import com.sun.media.sound.Toolkit;

import javax.swing.border.SoftBevelBorder;

public class InprocessPackagePanel extends JPanel {

	private JTextField textFieldPackageName;
	private JTextField textFieldLanguage;
	private JTextField textFieldJarPackage;
	private JTextField textFieldClass;
	private JTextField textFieldPath;

	/**
	 * Create the panel.
	 */
	public InprocessPackagePanel() {
		init();
	}

	public InprocessPackagePanel(InprocessPackage inprocessPackage) {

		init();
		textFieldPath.setText(inprocessPackage.getPath());
		textFieldPackageName.setText(inprocessPackage.getPackageName());
		textFieldLanguage.setText(inprocessPackage.getLanguage());
		textFieldJarPackage.setText(inprocessPackage.getJarPackage());
		textFieldClass.setText(inprocessPackage.getClassName());

		/*
		 * final JPanel panelAddAnotherPackage = new JPanel();
		 * panelAddAnotherPackage.setLayout(null);
		 * panelAddAnotherPackage.setBorder(new BevelBorder(BevelBorder.RAISED,
		 * null, null, null, null));
		 * panelAddAnotherPackage.setBackground(Color.DARK_GRAY);
		 * panelAddAnotherPackage.setBounds(22, 401, 445, 30);
		 * 
		 * 
		 * JLabel labelAddAnotherPackage = new JLabel("Add another Package");
		 * labelAddAnotherPackage.setForeground(Color.LIGHT_GRAY);
		 * labelAddAnotherPackage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		 * labelAddAnotherPackage.setBounds(284, 0, 122, 22);
		 * panelAddAnotherPackage.add(labelAddAnotherPackage);
		 * 
		 * final JButton btnAddAnotherLanguage = new JButton("+");
		 * btnAddAnotherLanguage.addActionListener(new ActionListener() { public
		 * void actionPerformed(ActionEvent e) { } });
		 * btnAddAnotherLanguage.setFont(new Font("Tahoma", Font.BOLD, 14));
		 * btnAddAnotherLanguage.setBounds(409, 3, 16, 14);
		 * panelAddAnotherPackage.add(btnAddAnotherLanguage);
		 */
	}

	private void init() {
		// TODO Auto-generated method stub
		Dimension size = new Dimension(549, 220);
		this.setLayout(null);

		this.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		this.setBackground(Color.DARK_GRAY);
		this.setSize(size);
		this.setMinimumSize(size);
		this.setPreferredSize(size);
		Dimension maxSize=new Dimension(1020,220);
		this.setMaximumSize(maxSize);
		// inprocesspackagePanel.setBounds(22, inprocesspackagePanelInitialpos,
		// 445, 191);

		final JLabel labelInterpreterPath = new JLabel("Path");
		labelInterpreterPath.setForeground(Color.LIGHT_GRAY);
		labelInterpreterPath.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelInterpreterPath.setBounds(87, 100, 36, 14);
		this.add(labelInterpreterPath);

		textFieldPath = new JTextField();
		textFieldPath.setColumns(10);
		textFieldPath.setBounds(172, 97, 238, 22);

		this.add(textFieldPath);

		final SmallImageButton btnInterpreterPath = new SmallImageButton("");
		btnInterpreterPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc1 = new JFileChooser();
				fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int return_val = fc1.showOpenDialog(null);
				if (return_val == JFileChooser.APPROVE_OPTION) {
					File file = fc1.getSelectedFile();
					textFieldPath.setText(file.getAbsolutePath());
				/*	System.out.println("Opening: " + file.getAbsolutePath()
							+ ".");
					System.out.println(textFieldPath.hashCode());
*/
				} else {
					//System.out.println("Open command cancelled by user.");
				}

			}
		});
		
		btnInterpreterPath.setLocation(418, 95);
		btnInterpreterPath.setIcon(new ImageIcon(System.getProperty("user.dir")+File.separator+"Images"+File.separator+"browse1.png"));
		btnInterpreterPath.setToolTipText("Open Directory");
		
		this.add(btnInterpreterPath);

		JLabel labelLanguage = new JLabel("Package name");
		labelLanguage.setForeground(Color.LIGHT_GRAY);
		labelLanguage.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelLanguage.setBounds(15, 35, 89, 14);
		this.add(labelLanguage);

		textFieldPackageName = new JTextField();
		textFieldPackageName.setColumns(10);
		textFieldPackageName.setBounds(113, 32, 360, 22);
		this.add(textFieldPackageName);

		final SmallImageButton btnRemovePackage = new SmallImageButton();
		
		 //btnRemoveLanguage.setBounds(500, 30, 30, 28);
		btnRemovePackage.setToolTipText("Delete");
		btnRemovePackage.setIcon(new ImageIcon(System.getProperty("user.dir")+File.separator+"Images"+File.separator+"delete.png"));
		
		
		btnRemovePackage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// panelLanguages.setVisible(false);
				/*
				 * int clickedBrowseButton=btnRemovePackage.hashCode(); Set keys
				 * = InprocessPanelOnLoad.inprocessPanelObjects.keySet();
				 * Identifies the minus button clicked and panel to be removed
				 * from panelright for (Iterator i = keys.iterator();
				 * i.hasNext();) { int key = (Integer) i.next(); JButton
				 * jb=(JButton
				 * )(inprocessPanelObjects.get(key)).getComponentAt(409,5);
				 * if(jb.hashCode()==clickedBrowseButton) { removePanelkey=key;
				 * inprocesspackagePanel.setVisible(false);
				 * remove(inprocesspackagePanel); revalidate(); repaint(); } }
				 * removePanel(removePanelkey);
				 */
				Container con = InprocessPackagePanel.this.getParent();
				con.remove(InprocessPackagePanel.this);
				con.getParent().getParent().validate();
				con.getParent().getParent().repaint();

			}
		});
		
		btnRemovePackage.setLocation(495, 30);
		
		this.add(btnRemovePackage);

		JLabel labelInterpreter = new JLabel("Language");
		labelInterpreter.setForeground(Color.LIGHT_GRAY);
		labelInterpreter.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelInterpreter.setBounds(88, 70, 71, 14);
		this.add(labelInterpreter);

		textFieldLanguage = new JTextField();
		textFieldLanguage.setColumns(10);
		textFieldLanguage.setBounds(172, 67, 276, 22);
		this.add(textFieldLanguage);

		JLabel labelOption = new JLabel("JAR Package");
		labelOption.setForeground(Color.LIGHT_GRAY);
		labelOption.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelOption.setBounds(87, 128, 79, 14);
		this.add(labelOption);

		textFieldJarPackage = new JTextField();
		textFieldJarPackage.setColumns(10);
		textFieldJarPackage.setBounds(172, 125, 276, 22);
		this.add(textFieldJarPackage);

		JLabel labelExtensions = new JLabel("Class");
		labelExtensions.setForeground(Color.LIGHT_GRAY);
		labelExtensions.setFont(new Font("Tahoma", Font.BOLD, 11));
		labelExtensions.setBounds(87, 156, 36, 14);
		this.add(labelExtensions);

		textFieldClass = new JTextField();
		textFieldClass.setColumns(10);
		textFieldClass.setBounds(172, 153, 276, 22);
		this.add(textFieldClass);

	}

	public InprocessPackage getInprocessPackage() {
		InprocessPackage inprocessPackage = new InprocessPackage();
		inprocessPackage.setPath(textFieldPath.getText());
		inprocessPackage.setPackageName(textFieldPackageName.getText());
		inprocessPackage.setLanguage(textFieldLanguage.getText());
		inprocessPackage.setJarPackage(textFieldJarPackage.getText());
		inprocessPackage.setClassName(textFieldClass.getText());
		return inprocessPackage;
	}

}
