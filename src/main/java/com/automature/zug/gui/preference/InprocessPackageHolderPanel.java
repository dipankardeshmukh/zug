package com.automature.zug.gui.preference;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;

import java.awt.FlowLayout;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import com.automature.zug.gui.model.InprocessPackage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InprocessPackageHolderPanel extends JPanel {

	private JPanel centralPanel;
	/**
	 * Create the panel.
	 */
	public InprocessPackageHolderPanel(Set<InprocessPackage> inprocesses){
		init();
		/*if(inprocesses==null){
			return;
		}*/
		for(InprocessPackage ip:inprocesses){
			InprocessPackagePanel ipp=new InprocessPackagePanel(ip);
			centralPanel.add(ipp);
		}
		
	}
	private void init() {
		// TODO Auto-generated method stub
		setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		//add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBackground(Color.DARK_GRAY);
		//add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Add Package");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEmptyPackagePanel();
			}
		});
		btnNewButton.setForeground(Color.LIGHT_GRAY);
		btnNewButton.setBackground(Color.DARK_GRAY);
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setBorderPainted(false);
		panel.add(btnNewButton);
		panel_1.add(panel,BorderLayout.SOUTH);
		centralPanel = new JPanel();
		//centralPanel.setBorder(null);
		centralPanel.setBackground(Color.DARK_GRAY);
		centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.PAGE_AXIS));
		//centralPanel.setMinimumSize(new Dimension(200, 400));
		//centralPanel.setSize(new Dimension(200, 400));
		panel_1.add(centralPanel,BorderLayout.CENTER);
		JScrollPane scrollPane=new JScrollPane(panel_1);
		scrollPane.setAutoscrolls(true);

		//scrollPane1.setAutoscrolls(true);
		scrollPane.setMinimumSize(new Dimension(200, 400));
		add(scrollPane,BorderLayout.CENTER);
		
		
	}
	public InprocessPackageHolderPanel() {
		init();

	}

	private void addEmptyPackagePanel(){
		centralPanel.add(new InprocessPackagePanel());
		this.getParent().validate();
		this.getParent().repaint();
	}
	
	public Set<InprocessPackage> getInprocessPackages() {
		Component []panels=centralPanel.getComponents();
		Set<InprocessPackage> ipps=new LinkedHashSet<InprocessPackage>();
		for(Component panel:panels){
			if(panel instanceof InprocessPackagePanel){
				InprocessPackagePanel ipp=(InprocessPackagePanel)panel;
				ipps.add(ipp.getInprocessPackage());
			}
		}
		return ipps;
	}
	
	
}
