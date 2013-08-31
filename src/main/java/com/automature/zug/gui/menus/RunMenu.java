package com.automature.zug.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.automature.zug.gui.ZugGUI;

public class RunMenu {
	
	private JMenu mnRun;
	private JMenuItem mntmExecute;
	private JMenuItem chckbxmntmPause;
	private JMenuItem chckbxmntmStop;
	private JMenuItem chckbxmntmDebug;
	private JMenuItem chckbxmntmStep;
	private JMenuItem chckbxmntmResume;
	private JMenu mnBreakpoint;
	private JMenuItem mntmSet_1;
	private JMenuItem mntmClear;

	public RunMenu(){
		
		mnRun = new JMenu("Run");
		
		mntmExecute = new JMenuItem("Execute");
		mnRun.add(mntmExecute);

		chckbxmntmPause = new JMenuItem("Pause");
		mnRun.add(chckbxmntmPause);
		
		chckbxmntmStop = new JMenuItem("Stop");
		mnRun.add(chckbxmntmStop);
		
		
//		chckbxmntmDebug = new JCheckBoxMenuItem("Debug");
//		mnRun.add(chckbxmntmDebug);

		chckbxmntmStep = new JMenuItem("Step");
		mnRun.add(chckbxmntmStep);

		chckbxmntmResume = new JMenuItem("Resume");
		mnRun.add(chckbxmntmResume);

//		mnBreakpoint = new JMenu("Breakpoint");
//		mnRun.add(mnBreakpoint);
//
//		mntmSet_1 = new JMenuItem("Set");
//		mnBreakpoint.add(mntmSet_1);
//
//		mntmClear = new JMenuItem("Clear");
//		mnBreakpoint.add(mntmClear);
		
		disableDebuggerOPtions();
		mnRun.setEnabled(true);
		
		addActions();
	}
	
	public void addActions(){
		
		mntmExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						mntmExecute.setEnabled(false);
						ZugGUI.runZug();
						mntmExecute.setEnabled(true);
					}
				});
				t.start();
			}
		});
		
		chckbxmntmPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ZugGUI.getDebugger().setPauseSignal();
			}
		});
		
		chckbxmntmResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ZugGUI.getDebugger().sendResumeSignal();
			}
		});
		
		chckbxmntmStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					ZugGUI.stopRunningTestSuite();
			}
		});
		
		chckbxmntmStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ZugGUI.getDebugger().sendStepOverSignal();
			}
		});
	}

	public JMenu getMnRun() {
		return mnRun;
	}
	
	public void disableDebuggerOPtions(){
		chckbxmntmPause.setEnabled(false);
		chckbxmntmStep.setEnabled(false);
		chckbxmntmResume.setEnabled(false);
	//	mnBreakpoint.setEnabled(false);
	}
	
	public void enableDebuggerOPtions(){
		
		chckbxmntmPause.setEnabled(true);
		chckbxmntmStep.setEnabled(true);
		chckbxmntmResume.setEnabled(true);
	//	mnBreakpoint.setEnabled(true);
	}
	
	public void clearOptions(){
		
	}
	
}
