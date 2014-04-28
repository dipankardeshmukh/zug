package com.automature.zug.gui.preference;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.automature.zug.gui.components.SmallImageButton;

import javax.swing.ImageIcon;

public class ScriptLocation extends JPanel {

	private JTextField textFieldPath;

	/**
	 * Create the panel.
	 */
	
	public ScriptLocation() {
	
	init();	
	}
	public ScriptLocation(String dir){
		init();
		textFieldPath.setText(dir);
	}
	
	private void init() {
		// TODO Auto-generated method stub
	
		setBackground(Color.DARK_GRAY);
		setLayout(null);
		//setBounds(22, 51, 445, 108);
		Dimension size=new Dimension(415,49);
		setSize(size);
		setMinimumSize(size);
		setPreferredSize(new Dimension(571, 49));
		Dimension maxSize=new Dimension(1015,60);
		setMaximumSize(maxSize);
		JLabel label_2 = new JLabel("Path");
		label_2.setSize(47, 28);
		label_2.setLocation(51, 9);
		label_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		label_2.setForeground(Color.LIGHT_GRAY);
		this.add(label_2);
		textFieldPath = new JTextField();
		textFieldPath.setSize(360, 24);
		textFieldPath.setLocation(108, 10);
		textFieldPath.setColumns(10);
		this.add(textFieldPath);
		SmallImageButton button_1 = new SmallImageButton();
		button_1.setToolTipText("Open Directory");
		button_1.setIcon(new ImageIcon(System.getProperty("user.dir")+File.separator+"Images"+File.separator+"browse1.png"));
		button_1.setLocation(478, 9);
		//button_1.setSize(30, 28);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int return_val=fc.showOpenDialog(null);
				if (return_val == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					textFieldPath.setText(file.getAbsolutePath());

				} else {
					//System.out.println("Open command cancelled by user.");
				}
			}
		});
		/*button_1.setForeground(Color.DARK_GRAY);
		button_1.setFont(new Font("Tahoma", Font.BOLD, 10));
		button_1.setBackground(Color.DARK_GRAY);*/
		this.add(button_1);
		
		SmallImageButton btnNewButton = new SmallImageButton("");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 Container con = ScriptLocation.this.getParent();
					con.remove(ScriptLocation.this);
					con.getParent().getParent().getParent().getParent().validate();
					con.getParent().getParent().getParent().getParent().repaint();
			}
		});
		btnNewButton.setLocation(510, 9);
		btnNewButton.setToolTipText("Delete");
		btnNewButton.setIcon(new ImageIcon(System.getProperty("user.dir")+File.separator+"Images"+File.separator+"delete.png"));
		/*btnNewButton.setForeground(Color.DARK_GRAY);
		btnNewButton.setBackground(Color.DARK_GRAY);
		btnNewButton.setBounds(518, 9, 30, 28);*/
		add(btnNewButton);
	}
	
	public String getScriptLocation() {
		return textFieldPath.getText();
	}

}
