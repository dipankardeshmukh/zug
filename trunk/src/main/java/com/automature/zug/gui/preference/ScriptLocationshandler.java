package com.automature.zug.gui.preference;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.BoxLayout;

public class ScriptLocationshandler extends JPanel {

	private JPanel panel_1;
	/**
	 * Create the panel.
	 */
	public ScriptLocationshandler() {
		
		init();
	
	}
	
	public ScriptLocationshandler(Set<String> locations) {
		init();
		for(String dir:locations){
			
			panel_1.add(new ScriptLocation(dir));
		}
		addEmptyScriptLocationPanel();
	}

	private void init() {
		// TODO Auto-generated method stub
		setLayout(new BorderLayout(0, 0));
		Dimension size=new Dimension(200,50);
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setMinimumSize(size);
		panel.setSize(size);
		panel.setPreferredSize(size);
		add(panel, BorderLayout.NORTH);
		panel.setLayout(null);
		
		JLabel lblScriptLocations = new JLabel("Script Locations");
		lblScriptLocations.setBounds(10, 5, 165, 37);
		lblScriptLocations.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblScriptLocations.setForeground(Color.LIGHT_GRAY);
		lblScriptLocations.setBackground(Color.DARK_GRAY);
		panel.add(lblScriptLocations);
		
		JLabel label_1 = new JLabel("Add another path");
		label_1.setLocation(421, 5);
		label_1.setSize(115, 26);
		label_1.setForeground(Color.LIGHT_GRAY);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 12));

		final JButton AddanotherPathbtn = new JButton("");
		AddanotherPathbtn.setLocation(532, 9);
		AddanotherPathbtn.setSize(21, 18);
		AddanotherPathbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addEmptyScriptLocationPanel();
			
			}

			
		});
		AddanotherPathbtn.setIcon(new ImageIcon(System.getProperty("user.dir")+File.separator+"Images"+File.separator+"plus5.png"));
		AddanotherPathbtn.setFont(new Font("Tahoma", Font.BOLD, 8));
		panel.add(label_1);
		panel.add(AddanotherPathbtn);
		panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.PAGE_AXIS));

	}
	private void addEmptyScriptLocationPanel() {
		// TODO Auto-generated method stub
		panel_1.add(new ScriptLocation());
		JPanel panel=(JPanel)this.getParent();
		if(panel!=null){
			panel.revalidate();panel.repaint();
		}
		//this.getParent();
		
	}
	
	public Set<String> getScriptLocations() {
		Component []panels=panel_1.getComponents();
		Set<String> locations=new LinkedHashSet<String>();
		for(Component panel:panels){
			if(panel instanceof ScriptLocation){
				ScriptLocation sl=(ScriptLocation)panel;
				locations.add(sl.getScriptLocation());
			}
		}
		return locations;
	}
}
