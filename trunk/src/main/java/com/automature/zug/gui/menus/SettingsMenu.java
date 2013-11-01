package com.automature.zug.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.automature.zug.gui.IconsPanel;
import com.automature.zug.gui.actionlistener.MoreOptionActionListener;

public class SettingsMenu {
	
	private JMenu mnSettings;
	private JMenu mnMode;
	private JCheckBoxMenuItem mntmPerformance;
	private JCheckBoxMenuItem mntmProduction;
	private JCheckBoxMenuItem mntmDevelopment;
/*
	static private JCheckBox chckbxDebug;
	static private JCheckBox chckbxVerbose;
	static private JCheckBox chckbxAutorecover;
	static private JCheckBox chckbxNoVerify;
	static private JCheckBox chckbxRepeat;
	static private JCheckBox chckbxDebugger;
*/	
	private JCheckBoxMenuItem chckbxVerbose;
	private JCheckBoxMenuItem chckbxAutorecover;
//	private JCheckBoxMenuItem chckbxDebug;
	private JCheckBoxMenuItem chckbxNoVerify;
	private JCheckBoxMenuItem chckbxRepeat;
	private JCheckBoxMenuItem chckbxDebugger;
	private JMenuItem mntmMacros;
	private JMenuItem mntmIncludes;
	private JMenuItem mntmLogfile;
	private MoreOptionActionListener opgui;

	public SettingsMenu(){

		mnSettings = new JMenu("Options");

		mnMode = new JMenu("Mode");
		mnSettings.add(mnMode);

		mntmPerformance = new JCheckBoxMenuItem("Performance");
		mnMode.add(mntmPerformance);

		mntmProduction = new JCheckBoxMenuItem("Production");
		mnMode.add(mntmProduction);

		mntmDevelopment = new JCheckBoxMenuItem("Development");
		mnMode.add(mntmDevelopment);

		mnSettings.addSeparator();
		
		chckbxVerbose = new JCheckBoxMenuItem("Verbose");		
		mnSettings.add(chckbxVerbose);

		chckbxAutorecover = new JCheckBoxMenuItem("Autorecover");
		mnSettings.add(chckbxAutorecover);

	//	chckbxDebug = new JCheckBoxMenuItem("Debug");
	//	mnSettings.add(chckbxDebug);

		chckbxNoVerify = new JCheckBoxMenuItem("NoVerify");
		mnSettings.add(chckbxNoVerify);

	//	chckbxRepeat = new JCheckBoxMenuItem("Repeat");
	//	mnSettings.add(chckbxRepeat);
		
		chckbxDebugger=new JCheckBoxMenuItem("Debugger");
		mnSettings.add(chckbxDebugger);

		mnSettings.addSeparator();
		
		//mntmMacros = new JMenuItem("Macro Column");
		//mnSettings.add(mntmMacros);

		//mntmIncludes = new JMenuItem("Include Files");
		//mnSettings.add(mntmIncludes);

	//	mntmLogfile = new JMenuItem("Logfile");
	//	mnSettings.add(mntmLogfile);
		
		mnSettings.setEnabled(true);
		
		addActions();

	}

	private void addActions(){
		
		mntmPerformance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IconsPanel.setPerformanceOptions();
				setPerformanceOptions();
				mntmPerformance.setSelected(true);
				mntmProduction.setSelected(false);
				mntmDevelopment.setSelected(false);
			}
		});
		
		mntmProduction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IconsPanel.setProductionOptions();
				setProductionOptions();
				mntmPerformance.setSelected(false);
				mntmProduction.setSelected(true);
				mntmDevelopment.setSelected(false);
			}
		});
		
		mntmDevelopment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IconsPanel.setDevelopmentOptions();
				setDevelopmentOptions();
				mntmPerformance.setSelected(false);
				mntmProduction.setSelected(false);
				mntmDevelopment.setSelected(true);
			}
		});
		
		chckbxVerbose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxVerbose.isSelected())
					IconsPanel.chckbxVerbose.setSelected(true);
				else{
					IconsPanel.chckbxVerbose.setSelected(false);
				}
			}
		});
		
		chckbxAutorecover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxAutorecover.isSelected())
					IconsPanel.chckbxAutorecover.setSelected(true);
				else{
					IconsPanel.chckbxAutorecover.setSelected(false);
				}
			}
		});
		
	/*	chckbxDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxDebug.isSelected())
					IconsPanel.chckbxDebug.setSelected(true);
				else{
					IconsPanel.chckbxDebug.setSelected(false);
				}
			}
		});
*/
		chckbxDebugger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxDebugger.isSelected())
					IconsPanel.chckbxDebugger.setSelected(true);
				else{
					IconsPanel.chckbxDebugger.setSelected(false);
				}
			}
		});
		
		chckbxNoVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxNoVerify.isSelected())
					IconsPanel.chckbxNoVerify.setSelected(true);
				else{
					IconsPanel.chckbxNoVerify.setSelected(false);
				}
			}
		});
		
/*		chckbxRepeat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxRepeat.isSelected())
					IconsPanel.chckbxNoExecute.setSelected(true);
				else{
					IconsPanel.chckbxNoExecute.setSelected(false);
				}
			}
		});*/
		
		opgui= new MoreOptionActionListener(); 
		//mntmMacros.addActionListener(opgui);
		//mntmIncludes.addActionListener(opgui);
		
	}
	
	public JMenu getMnSettings() {
		return mnSettings;
	}
	
	public void setDevelopmentOptions() {
		
		chckbxNoVerify.setSelected(false);		
	//	chckbxDebug.setSelected(true);
		chckbxVerbose.setSelected(true);
		chckbxAutorecover.setSelected(false);
	//	chckbxRepeat.setSelected(false);
	

	}

	public void setPerformanceOptions() {
		
		chckbxNoVerify.setSelected(true);		
	//	chckbxDebug.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(false);
	//	chckbxRepeat.setSelected(true);
	

	}

	public void setProductionOptions() {
		
		chckbxNoVerify.setSelected(false);		
	//	chckbxDebug.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(true);
	//	chckbxRepeat.setSelected(false);
	

	}
	
	public void clearOptions(){
		
		mntmPerformance.setSelected(false);
		mntmProduction.setSelected(false);
		mntmDevelopment.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(false);
	//	chckbxDebug.setSelected(false);
		chckbxNoVerify.setSelected(false);
	//	chckbxRepeat.setSelected(false);
	}
}
