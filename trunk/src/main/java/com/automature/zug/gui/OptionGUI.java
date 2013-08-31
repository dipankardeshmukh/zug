package com.automature.zug.gui;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.border.LineBorder;

public class OptionGUI{

	private JWindow frame;
	private JComboBox textField;
	private JComboBox textField_1;
	private JComboBox textField_2;
	private JComboBox textField_3;
	private JComboBox textField_4;
	private JComboBox textField_5;
	private JComboBox textField_6;
	private JComboBox textField_7;
	static ArrayList<String> al=new ArrayList<String>(); 
	private static ArrayList<String> testcaseid=new ArrayList<String>();
	private static ArrayList<String> include=new ArrayList<String>();
	private static ArrayList<String> planid=new ArrayList<String>();
	private static ArrayList<String> cyclid=new ArrayList<String>();
	private static ArrayList<String> toposetid=new ArrayList<String>();
	private static ArrayList<String> macrocol=new ArrayList<String>();
	private static ArrayList<String> macros=new ArrayList<String>();
	private static ArrayList<String> others=new ArrayList<String>();
	private boolean tcidField=false;
	private boolean tpidField=false;
	private boolean topoidField=false;
	private static HashMap<String,ArrayList<String>> hm=new HashMap<String, ArrayList<String>>();
	Point mouseDownScreenCoords;
	Point mouseDownCompCoords;
	
	@SuppressWarnings("static-access")
	public OptionGUI(boolean tcid, boolean tpid, boolean topoid) {

		this.tcidField=tcid;
		this.tpidField=tpid;
		this.topoidField=topoid;
		initialize();
	}
	
	public OptionGUI() {
		initialize();
	}
	public static void clearOptions(){
		
		al.clear();
		testcaseid.clear();
		include.clear();
		planid.clear();
		cyclid.clear();
		toposetid.clear();
		macrocol.clear();
		macros.clear();
		others.clear();
		
	}
	
	private void initialize() {
		
		LineBorder border = new LineBorder(Color.GRAY,4);
		frame = new JWindow(ZugGUI.getFrame());
		frame.getRootPane().setBorder(border);
		frame.getContentPane().setPreferredSize(new Dimension(200,400));
		frame.setPreferredSize(new Dimension(200,400));
		frame.getContentPane().setLayout(null);
		
//		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		frame.setUndecorated(true);
//		frame.setResizable(false);
		frame.setBackground(Color.WHITE);
		frame.getContentPane().setBackground(Color.WHITE);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(10, 11, 424, 462);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel("Test Case Id");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(20, 22, 94, 24);
		panel_1.add(lblNewLabel);

		textField = new JComboBox();
		textField.setEditable(true);
		textField.setBounds(140, 26, 264, 20);
		panel_1.add(textField);
		if(!testcaseid.isEmpty())
		{
			if(testcaseid.get(0)!=null)
			{
				testcaseid.get(0);
			}
			else
			{
			}
			testcaseid=hm.get("testcaseid");
			for(int i=testcaseid.size()-1;i>=0;i--)
			{
				String item=testcaseid.get(i);
				textField.addItem(item);
			}
		}

		JLabel lblNewLabel_1 = new JLabel("Include");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(20, 53, 94, 24);
		panel_1.add(lblNewLabel_1);

		textField_1 = new JComboBox();
		textField_1.setEditable(true);
		textField_1.setBounds(140, 57, 264, 20);
		panel_1.add(textField_1);
		if(!include.isEmpty())
		{
			if(include.get(0)!=null)
				include.get(0);
			else {
			}
			include=hm.get("include");
			for(int i=include.size()-1;i>=0;i--)
			{
				String item=include.get(i);
				textField_1.addItem(item);
			}
		}
		JLabel lblTestPlanId = new JLabel("Test Plan Id");
		lblTestPlanId.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTestPlanId.setBounds(20, 84, 94, 24);
		panel_1.add(lblTestPlanId);

		textField_2 = new JComboBox();
		textField_2.setBackground(Color.WHITE);
		textField_2.setEditable(true);
		if(tpidField==true)
		{	
			textField_2.setBackground(Color.LIGHT_GRAY);
		}
		else 
		{
			textField_2.setBackground(new Color(255, 255, 255));
		}  
		panel_1.add(textField_2);
		textField_2.setBounds(140, 121, 264, 20);
		if(!planid.isEmpty())
		{
			if(planid.get(0)!=null)
				planid.get(0);
			else {
			}
			planid=hm.get("planid");
			for(int i=planid.size()-1;i>=0;i--)
			{
				String item=planid.get(i);
				textField_2.addItem(item);
			}
		}

		JLabel lblTestCycleId = new JLabel("Test Cycle Id");
		lblTestCycleId.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTestCycleId.setBounds(20, 119, 94, 20);
		panel_1.add(lblTestCycleId);

		textField_3 =  new JComboBox();
		textField_3.setEditable(true);
		if(tcidField==true)
		{	
			textField_3.setBackground(Color.LIGHT_GRAY);
		}
		else if(tcidField==false)
		{
			panel_1.add(textField_3);
			textField_3.setBackground(new Color(255, 255, 255));
		}
		panel_1.add(textField_3);
		textField_3.setBounds(140, 88, 264, 20);
		if(!cyclid.isEmpty())
		{
			if(cyclid.get(0)!=null)
				cyclid.get(0);
			else {
			}
			cyclid=hm.get("cyclid");
			for(int i=cyclid.size()-1;i>=0;i--)
			{
				String item=cyclid.get(i);
				textField_3.addItem(item);
			}
		}

		JLabel lblTopologySetId = new JLabel("Topology Set Id");
		lblTopologySetId.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTopologySetId.setBounds(20, 150, 115, 20);
		panel_1.add(lblTopologySetId);


		textField_4 = new JComboBox();
		textField_4.setEditable(true);
		if(topoidField==true)
		{	
			textField_4.setBackground(Color.LIGHT_GRAY);
		}
		else 
		{
			textField_4.setBackground(new Color(255, 255, 255));
		}
		panel_1.add(textField_4);
		textField_4.setBounds(140, 150, 264, 20);
		if(!toposetid.isEmpty())
		{
			if(toposetid.get(0)!=null)
				toposetid.get(0);
			else {
			}
			toposetid=hm.get("toposetid");
			for(int i=toposetid.size()-1;i>=0;i--)
			{
				String item=toposetid.get(i);
				textField_4.addItem(item);
			}
		}

		JLabel lblMacroColumn = new JLabel("Macro Column");
		lblMacroColumn.setForeground(Color.BLACK);
		lblMacroColumn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblMacroColumn.setBounds(20, 181, 115, 24);
		panel_1.add(lblMacroColumn);

		textField_5 = new JComboBox();
		textField_5.setEditable(true);
		textField_5.setBounds(140, 181, 264, 20);
		panel_1.add(textField_5);
		if(!macrocol.isEmpty())
		{
			if(macrocol.get(0)!=null)
				macrocol.get(0);
			else {
			}
			macrocol=hm.get("macrocol");
			for(int i=macrocol.size()-1;i>=0;i--)
			{
				String item=macrocol.get(i);
				textField_5.addItem(item);
			}
		}
		
		JLabel lblMacros = new JLabel("Macros");
		lblMacros.setForeground(Color.BLACK);
		lblMacros.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblMacros.setBounds(20, 210, 115, 24);
		panel_1.add(lblMacros);
		textField_6 = new JComboBox();
		textField_6.setEditable(true);
		textField_6.setBounds(140, 210, 264, 20);
		panel_1.add(textField_6);
		if(!macros.isEmpty())
		{
			if(macros.get(0)!=null)
				macros.get(0);
			else {
			}
			macros=hm.get("macros");
			for(int i=macros.size()-1;i>=0;i--)
			{
				String item=macros.get(i);
				textField_6.addItem(item);
			}
		}
		JLabel lblOthers = new JLabel("Others");
		lblOthers.setForeground(Color.BLACK);
		lblOthers.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOthers.setBounds(20, 240, 115, 24);
		panel_1.add(lblOthers);
		textField_7 = new JComboBox();
		textField_7.setEditable(true);
		textField_7.setBounds(140, 240, 264, 20);
		panel_1.add(textField_7);
		if(!others.isEmpty())
		{
			if(others.get(0)!=null)
				others.get(0);
			else {
			}
			others=hm.get("others");
			for(int i=others.size()-1;i>=0;i--)
			{
				String item=others.get(i);
				textField_7.addItem(item);
			}
		}


		JButton btnClickToAdd = new JButton("OK");
		btnClickToAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				al.clear();
				hm.put("testcaseid", testcaseid);
				hm.put("include", include);
				hm.put("planid", planid);
				hm.put("cyclid", cyclid);
				hm.put("toposetid", toposetid);
				hm.put("macrocol", macrocol);
				hm.put("macros", macros);
				hm.put("others", others);
				String tcid=(String)textField.getSelectedItem();
				String incl=(String)textField_1.getSelectedItem();
				String tpid=(String)textField_2.getSelectedItem();
				String tcyid=(String)textField_3.getSelectedItem();
				String tsid=(String)textField_4.getSelectedItem();
				String mcol=(String)textField_5.getSelectedItem();
				String macro=(String)textField_6.getSelectedItem();
				String other=(String)textField_7.getSelectedItem();
				if(!testcaseid.contains(tcid))
					testcaseid.add(tcid);
				if(!include.contains(incl))
					include.add(incl);
				if(!planid.contains(tpid))
					planid.add(tpid);
				if(!cyclid.contains(tcyid))
					cyclid.add(tcyid);
				if(!toposetid.contains(tsid))
					toposetid.add(tsid);
				if(!macrocol.contains(mcol))
					macrocol.add(mcol);
				if(!macros.contains(macro))
					macros.add(macro);
				if(!others.contains(other))
					others.add(other);
				al.add(tcid);
				al.add(incl);
				al.add(tpid);
				al.add(tcyid);
				al.add(tsid);
				al.add(mcol);
				al.add(macro);
				al.add(other);

				if(testcaseid!=null && !testcaseid.isEmpty())
					hm.put("testcaseid", testcaseid);
				if(include!=null && !include.isEmpty())
					hm.put("include", include);
				if(planid!=null && !planid.isEmpty())
					hm.put("planid", planid);
				if(cyclid!=null && !cyclid.isEmpty())
					hm.put("cyclid", cyclid);
				if(toposetid!=null && !toposetid.isEmpty())
					hm.put("toposetid", toposetid);
				if(macrocol!=null && !macrocol.isEmpty())
					hm.put("macrocol", macrocol);
				if(macros!=null && !macros.isEmpty())
					hm.put("macros", macros);
				if(others!=null && !others.isEmpty())
					hm.put("others", others);
				ZugGUI.enableFrame();
				frame.dispose();
			}
		});
		btnClickToAdd.setBounds(75, 285, 89, 23);
		panel_1.add(btnClickToAdd);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ZugGUI.enableFrame();
				frame.dispose();
			}
		});
		btnCancel.setBounds(168, 285, 89, 23);
		panel_1.add(btnCancel);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textField.removeAllItems();
				textField_1.removeAllItems();
				textField_2.removeAllItems();
				textField_3.removeAllItems();
				textField_4.removeAllItems();
				textField_5.removeAllItems();
				textField_6.removeAllItems();
				textField_7.removeAllItems();
			}
		});
		btnRefresh.setBounds(263, 285, 89, 23);
		panel_1.add(btnRefresh);
		
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mouseDownScreenCoords = e.getLocationOnScreen();
				mouseDownCompCoords = e.getPoint();
			}
		});
		frame.addMouseMotionListener(new MouseMotionListener() {  
			public void mouseDragged(MouseEvent e) {  
				if(!e.isMetaDown()){  
					Point currCoords = e.getLocationOnScreen();
					frame.setLocation(mouseDownScreenCoords.x + (currCoords.x - mouseDownScreenCoords.x) - mouseDownCompCoords.x,
							mouseDownScreenCoords.y + (currCoords.y - mouseDownScreenCoords.y) - mouseDownCompCoords.y);
				}  
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}  
		}); 

	}
	public void generateGUI()
	{
		JWindow newFrame = frame;
		newFrame.pack();
		newFrame.setBounds(ZugGUI.getFrame().getX()+50,ZugGUI.getFrame().getY()+ 50, 447, 400);
//		newFrame.setBounds(50, 50, 447, 400);
		newFrame.setVisible(true);
		newFrame.setFocusable(true);
		//newFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

	}
}
