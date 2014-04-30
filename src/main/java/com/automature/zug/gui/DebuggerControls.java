package com.automature.zug.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import com.automature.zug.engine.Controller;

public class DebuggerControls {

	private JButton breakPoint;
	private JButton resume;
	private JButton singleStep;
	private JButton viewCV;
	private JButton pause;

    private JButton viewBP;
    private JButton clearBP;

	private JPanel panel;
    private JPanel debuugerToolBar;
	private DebuggerConsole dc;
	
	
	DebuggerControls(){
		
		panel=new JPanel(new GridLayout(0, 2, 1, 1));
        debuugerToolBar = new JPanel();
        debuugerToolBar.setBackground(Color.WHITE);
        debuugerToolBar.setLayout(null);
        debuugerToolBar.setVisible(true);
		
		breakPoint = new JButton("");
        breakPoint.setContentAreaFilled(false);
        breakPoint.setBorderPainted(false);
		//initializeBreakPointButton();
		//debuugerToolBar.add(breakPoint);
        //controlPanel.add(breakPoint);
		
		resume = new JButton("");
        resume.setContentAreaFilled(false);
        resume.setBorderPainted(false);
		initializeResumeButton();
		debuugerToolBar.add(resume);
        //controlPanel.add(resume);
		
		singleStep = new JButton("");
        singleStep.setContentAreaFilled(false);
        singleStep.setBorderPainted(false);
		initializeSingleStepButton();
		debuugerToolBar.add(singleStep);
        //controlPanel.add(singleStep);
		
		viewCV = new JButton();
        viewCV.setContentAreaFilled(false);
        viewCV.setBorderPainted(false);
		//initializeViewCVButton();
        //debuugerToolBar.add(viewCV);
		//controlPanel.add(viewCV);
		
		pause = new JButton();
        pause.setContentAreaFilled(false);
        pause.setBorderPainted(false);
		initializePauseButton();
		debuugerToolBar.add(pause);
		//controlPanel.add(pause);
		
		viewBP = new JButton("");
        viewBP.setContentAreaFilled(false);
        viewBP.setBorderPainted(false);
		//initializeViewBPButton();
		//debuugerToolBar.add(viewBP);
		//controlPanel.add(viewBP);

        clearBP = new JButton("");
        clearBP.setContentAreaFilled(false);
        clearBP.setBorderPainted(false);
        initializeClearBPButton();
        debuugerToolBar.add(clearBP);


		panel.add(debuugerToolBar);
		panel.setMinimumSize(new Dimension(300,40));
		panel.setPreferredSize(new Dimension(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,40)));
		panel.setMaximumSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,40));
	//	panel.setPreferredSize(new Dimension(767,60));
		panel.setVisible(true);
		panel.setBackground(Color.WHITE);
	}
	
	private void initializeBreakPointButton(){
		
		breakPoint.setToolTipText("Add Break Point");
		breakPoint.setBackground(Color.WHITE);
		breakPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(dc !=null){
					dc.showAddBreakPointPanel();
					ZugGUI.updateFrame();
				}
			}
		});
		breakPoint.setIcon(new ImageIcon(System.getProperty("user.dir")+"/Images/BreakPoint.jpg"));
		breakPoint.setBounds(0, 0, 33, 33);
	
	}
	
	private void initializeResumeButton(){
		
		resume.setToolTipText("Resume");
		resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(dc !=null)
					dc.sendResumeSignal();
			}
		});
		resume.setIcon(new ImageIcon(System.getProperty("user.dir")+"/Images/resume.png"));
		resume.setBounds(0, 0, 33, 33);
	}
	
	private void initializeSingleStepButton(){

		singleStep.setToolTipText("Single Step");
		singleStep.setBackground(Color.WHITE);
		singleStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(dc !=null){
					dc.singleStep();
				}
			}
		});
		
		singleStep.setIcon(new ImageIcon(System.getProperty("user.dir")+"/Images/singlestep.png"));
		singleStep.setBounds(40, 0, 33, 33);
	}
	
	private void initializeViewCVButton(){
		viewCV.setToolTipText("View Context Variables");
		viewCV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(dc !=null){
					dc.showContextVaraiblePane();
					ZugGUI.updateFrame();
				}
			}
		});
		viewCV.setIcon(new ImageIcon(System.getProperty("user.dir")+"/Images/view.png"));
		viewCV.setBackground(Color.WHITE);
		viewCV.setBounds(120, 0, 33, 33);
	}
	
	private void initializePauseButton(){
		
		pause.setBackground(Color.WHITE);
		pause.setToolTipText("Pause");
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(dc !=null)
					dc.setPauseSignal();
			}
		});
		
		pause.setIcon(new ImageIcon(System.getProperty("user.dir")+"/Images/pause.png"));
		pause.setBounds(80, 0, 33, 33);
	}
	
	private void initializeViewBPButton(){
		
		viewBP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(dc !=null){
					dc.showViewBreakPointPane();
					ZugGUI.updateFrame();
				}
			}
		});
		viewBP.setIcon(new ImageIcon(System.getProperty("user.dir")+"/Images/viewBP.png"));
		viewBP.setToolTipText("View break points");
		viewBP.setBackground(Color.WHITE);
		viewBP.setBounds(200, 0, 33, 33);
	}

    private void initializeClearBPButton(){

        clearBP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(dc !=null){
                    ZugGUI.removeAllBreakPoints();
                    Controller.removeAllBreakPoints();
                    ZugGUI.updateFrame();
                }
            }
        });
        clearBP.setIcon(new ImageIcon(System.getProperty("user.dir")+"/Images/clearbreakpoint.png"));
        clearBP.setToolTipText("Clear break points");
        clearBP.setBackground(Color.WHITE);
        clearBP.setBounds(120, 0, 33, 33);
    }
	
	public JPanel getDebuggerControlPanel(){
		return panel;
	}
	
	public void createDebugger(ArrayList names){
		try{
			panel.removeAll();
			panel.add(debuugerToolBar);
		}catch(Exception e){
			
		}
		dc = new DebuggerConsole(names);
		panel.add(dc.getDebuggerPanel());
	}
	
	public DebuggerConsole getDebugger(){
		return dc;
	}
}
