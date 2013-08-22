package com.automature.zug.gui.menus;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.automature.zug.gui.actionlistener.MoreOptionActionListener;

public class ReportingMenu {
	
	private JMenu mnReporting;
	private JMenuItem mntmTestcycle;
	private JMenuItem mntmTestplan;
	private JMenuItem mntmTopologyset;
	private JMenu mnLogfiles;
	private JMenuItem mntmAtomlog;
	private JMenuItem mntmExecutionlog;
	private MoreOptionActionListener opgui;

	public ReportingMenu(){
		
		mnReporting = new JMenu("Reporting");

		mntmTestcycle = new JMenuItem("TestCycle");
		mnReporting.add(mntmTestcycle);

		mntmTestplan = new JMenuItem("TestPlan");
		mnReporting.add(mntmTestplan);

		mntmTopologyset = new JMenuItem("TopologySet");
		mnReporting.add(mntmTopologyset);

//		mnLogfiles = new JMenu("Logfiles");
//		mnReporting.add(mnLogfiles);
//
//		mntmAtomlog = new JMenuItem("AtomLog");
//		mnLogfiles.add(mntmAtomlog);
//
//		mntmExecutionlog = new JMenuItem("ExecutionLog");
//		mnLogfiles.add(mntmExecutionlog);
		addActions();
	}

	public void addActions(){
		opgui= new MoreOptionActionListener(); 
		mntmTestcycle.addActionListener(opgui);
		mntmTestplan.addActionListener(opgui);
		mntmTopologyset.addActionListener(opgui);
	}
	
	public JMenu getMnReporting() {
		return mnReporting;
	}
}
