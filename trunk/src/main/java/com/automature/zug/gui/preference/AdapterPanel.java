package com.automature.zug.gui.preference;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.automature.zug.gui.components.SmallImageButton;
import com.automature.zug.gui.model.ReporterParams;

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

public class AdapterPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	private JTextField textFieldHost;
	private JTextField textFieldAdapterUsername;
	private JPasswordField textFieldAdapterPassword;
	private JComboBox dropdownDatabasename;
	
	
	public AdapterPanel(ReporterParams params){
		this();
		textFieldAdapterUsername.setText(params.getDbusername());
		textFieldHost.setText(params.getDbhostname());
		textFieldAdapterPassword.setText(params.getDbuserpassword());
		//dropdownDatabasename.setSelectedItem(params.getDbname());
		if(params.getDbname().equalsIgnoreCase("Framework")){
			dropdownDatabasename.setSelectedItem("Zermatt");
		}else{
			dropdownDatabasename.setSelectedItem(params.getDbname());
		}
		
	}
	
	public AdapterPanel() {
		setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
	


		this.setBackground(Color.DARK_GRAY);
		this.setLayout(null);

		JLabel LabelAdapter = new JLabel("Adapter");
		LabelAdapter.setFont(new Font("Arial", Font.BOLD, 16));
		LabelAdapter.setForeground(Color.LIGHT_GRAY);
		LabelAdapter.setBounds(40, 19, 107, 25);
		this.add(LabelAdapter);

		JLabel LabelHost = new JLabel("Host");
		LabelHost.setFont(new Font("Arial", Font.BOLD, 13));
		LabelHost.setForeground(Color.LIGHT_GRAY);
		LabelHost.setBounds(43, 71, 40, 13);
		this.add(LabelHost);

		JLabel LabelDatabaseName = new JLabel("Name");
		LabelDatabaseName.setFont(new Font("Arial", Font.BOLD, 13));
		LabelDatabaseName.setForeground(Color.LIGHT_GRAY);
		LabelDatabaseName.setBounds(43, 105, 76, 14);
		this.add(LabelDatabaseName);

		JLabel LabelAdapterUsername = new JLabel("Username");
		LabelAdapterUsername.setFont(new Font("Arial", Font.BOLD, 13));
		LabelAdapterUsername.setForeground(Color.LIGHT_GRAY);
		LabelAdapterUsername.setBounds(43, 141, 91, 14);
		this.add(LabelAdapterUsername);

		JLabel LabelAdapterPassword = new JLabel("Password");
		LabelAdapterPassword.setFont(new Font("Arial", Font.BOLD, 13));
		LabelAdapterPassword.setForeground(Color.LIGHT_GRAY);
		LabelAdapterPassword.setBounds(43, 175, 92, 14);
		this.add(LabelAdapterPassword);


		dropdownDatabasename= new JComboBox();
		dropdownDatabasename.setBounds(155, 102, 361, 25);   	
		dropdownDatabasename.addItem("Zermatt");
		dropdownDatabasename.addItem("Jira");
		dropdownDatabasename.addItem("TestLink");

		this.add(dropdownDatabasename);

		textFieldAdapterUsername = new JTextField();
		textFieldAdapterUsername.setBounds(155, 138, 361, 25);
		this.add(textFieldAdapterUsername);
		textFieldAdapterUsername.setColumns(10);

		textFieldAdapterPassword = new JPasswordField(100);
		textFieldAdapterPassword.setBounds(155, 172, 361, 25);
		textFieldAdapterPassword.setForeground(Color.BLACK);
		this.add(textFieldAdapterPassword);
		textFieldAdapterPassword.setEchoChar('*');

		textFieldHost = new JTextField();
		textFieldHost.setBounds(155, 67, 361, 25);
		this.add(textFieldHost);
		textFieldHost.setColumns(10);

		//JButton btnOK = new JButton("OK");
		
	}
	public ReporterParams getReporterParams() {
		
		ReporterParams rp=new ReporterParams();
		rp.setDbusername(textFieldAdapterUsername.getText());
		rp.setDbuserpassword(textFieldAdapterPassword.getPassword());
		rp.setDbhostname(textFieldHost.getText());
		String dbName=(String)dropdownDatabasename.getSelectedItem();
		if(dbName.equalsIgnoreCase("Zermatt")){
			rp.setDbname("Framework");
		}else{
			rp.setDbname(dbName);
		}
		return rp;
	}
}
