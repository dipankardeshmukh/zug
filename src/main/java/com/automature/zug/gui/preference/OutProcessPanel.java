package com.automature.zug.gui.preference;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.BoxLayout;

import com.automature.zug.gui.model.LanguagePackage;
import com.automature.zug.gui.model.ReporterParams;

public class OutProcessPanel extends JPanel {

	/**
	 * Create the panel.
	 */

	private ScriptLocationshandler locationHandler;
	private JPanel panel_1;

	public OutProcessPanel() {
		init();

	}
	public OutProcessPanel(Set<String> locations,Set<LanguagePackage> languages) {
		init();
		locationHandler=new ScriptLocationshandler(locations);
		panel_1.add(locationHandler);
		for(LanguagePackage pack:languages){
			panel_1.add(new LanguagePanel(pack));
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout(0, 0));


		JPanel panel_2 = new JPanel();
		//add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel_2.add(panel, BorderLayout.SOUTH);
		panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBackground(Color.DARK_GRAY);

		JButton btnNewButton = new JButton("Add Language");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEmptyPackage();
			}
		});
		btnNewButton.setForeground(Color.LIGHT_GRAY);
		btnNewButton.setBackground(Color.DARK_GRAY);
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setBorderPainted(false);
		panel.add(btnNewButton);


		panel_1 = new JPanel();
		panel_2.add(panel_1, BorderLayout.CENTER);
		panel_1.setBackground(Color.DARK_GRAY);
		//add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane=new JScrollPane(panel_2);
		scrollPane.setAutoscrolls(true);
		//scrollPane1.setAutoscrolls(true);
		scrollPane.setMinimumSize(new Dimension(200, 400));
		
		/*ScrollBarUI yourUI = new BasicScrollBarUI() {
		    @Override
		    protected JButton createDecreaseButton(int orientation) {
		        JButton button = new JButton();//super.createDecreaseButton(orientation);
		        button.setBackground(Color.DARK_GRAY);
		        button.setSize(0, 0);
		        return button;
		    }

		    @Override
		    protected JButton createIncreaseButton(int orientation) {
		        JButton button =new JButton();// super.createIncreaseButton(orientation);
		        button.setBackground(Color.DARK_GRAY);
		        button.setSize(0, 0);
		        return button;
		    }
		};
		
		scrollPane.getVerticalScrollBar().setUI(yourUI);
		scrollPane.getVerticalScrollBar().setBackground(Color.BLACK);
		scrollPane.getVerticalScrollBar().setForeground(Color.DARK_GRAY);*/
		//scrollPane.getVerticalScrollBar()
		this.add(scrollPane,BorderLayout.CENTER);

	}
	private void addEmptyPackage() {
		// TODO Auto-generated method stub
		panel_1.add(new LanguagePanel());
		this.getParent().validate();
		this.getParent().repaint();
	}

	public Set<String> getScriptLocations() {
		return locationHandler.getScriptLocations();
	}

	public Set<LanguagePackage> getLanguagePackages(){
		Component []panels=panel_1.getComponents();
		Set<LanguagePackage> lps=new LinkedHashSet<LanguagePackage>();
		for(Component panel:panels){
			if(panel instanceof LanguagePanel){
				LanguagePanel lpp=(LanguagePanel)panel;
				lps.add(lpp.getLanguagePackage());
			}
		}
		return lps;
	}

	
}
