package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Point;

import com.automature.zug.engine.AtomHandler;


import javax.swing.JLabel;
import java.awt.Insets;
import java.awt.Component;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

public class ViewAtoms extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JComboBox comboBox_1;
	private JComboBox comboBox;
	private JLabel lblPackages;
	private JLabel lblAtoms;
	Point mouseDownScreenCoords;
	Point mouseDownCompCoords;
	static ViewAtoms va=null;
	/**
	 * Launch the application.
	 */
//		public static void main(String[] args) {
//			JFrame jf=new JFrame();
//			try {
//				ViewAtoms vAtoms=ViewAtoms.getInstance(jf);
//				va.setVisible(true);
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

	public static ViewAtoms getInstance(JFrame owner){
		if(va==null){
			return createInstance(owner);
		}else{
			return va;
		}
	}

	private static ViewAtoms createInstance(JFrame owner){
		va=new ViewAtoms(owner);
		va.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		va.setUndecorated(true);
		//	ComponentMover cm=new ComponentMover(va,va.contentPanel);
		va.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				va.mouseDownScreenCoords = e.getLocationOnScreen();
				va.mouseDownCompCoords = e.getPoint();
			}
		});
		va.addMouseMotionListener(new MouseMotionListener() {  
			public void mouseDragged(MouseEvent e) {  
				if(!e.isMetaDown()){  
					Point currCoords = e.getLocationOnScreen();
					va.setLocation(va.mouseDownScreenCoords.x + (currCoords.x - va.mouseDownScreenCoords.x) - va.mouseDownCompCoords.x,
							va.mouseDownScreenCoords.y + (currCoords.y - va.mouseDownScreenCoords.y) - va.mouseDownCompCoords.y);
				}  
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}  
		}); 

		return va;
	}

	/**
	 * Create the dialog.
	 */
	private ViewAtoms(JFrame owner){
		super(owner);
		
		setUndecorated(true);
		setBackground(Color.WHITE);
		getContentPane().setBackground(Color.BLACK);
		
		setBounds((int)owner.getX()+50, (int)owner.getY()+50, 500, 250);
		BorderLayout borderLayout = new BorderLayout();
		getContentPane().setLayout(borderLayout);
		contentPanel.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(20, 15, 15, 20)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setBackground(Color.WHITE);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {100, 350, 30};
		gbl_contentPanel.rowHeights = new int[]{63, 30, 63, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			lblPackages = new JLabel("Package");
			lblPackages.setFont(new Font("Tahoma", Font.BOLD, 14));
			lblPackages.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_lblPackages = new GridBagConstraints();
			gbc_lblPackages.fill = GridBagConstraints.VERTICAL;
			gbc_lblPackages.insets = new Insets(0, 0, 5, 5);
			gbc_lblPackages.gridx = 0;
			gbc_lblPackages.gridy = 0;
			contentPanel.add(lblPackages, gbc_lblPackages);
		}
		comboBox = new JComboBox();
	//	contentPanel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{lblPackages, comboBox, lblAtoms, comboBox_1}));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.fill = GridBagConstraints.BOTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		contentPanel.add(comboBox, gbc_comboBox);
		{
			lblAtoms = new JLabel("Atom");
			lblAtoms.setFont(new Font("Tahoma", Font.BOLD, 14));
			lblAtoms.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_lblAtoms = new GridBagConstraints();
			gbc_lblAtoms.fill = GridBagConstraints.VERTICAL;
			gbc_lblAtoms.insets = new Insets(0, 0, 0, 5);
			gbc_lblAtoms.gridx = 0;
			gbc_lblAtoms.gridy = 2;
			contentPanel.add(lblAtoms, gbc_lblAtoms);
		}
		{
			Iterator it=new AtomHandler().getAllPackages().iterator();
			while(it.hasNext()){
				comboBox.addItem((String)it.next());
			}
		}

		{
			comboBox_1 = new JComboBox();
			comboBox_1.setAlignmentY(1.0f);

		}
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.gridwidth = 2;
		gbc_comboBox_1.fill = GridBagConstraints.BOTH;
		gbc_comboBox_1.gridx = 1;
		gbc_comboBox_1.gridy = 2;
		contentPanel.add(comboBox_1, gbc_comboBox_1);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(Color.WHITE);
			buttonPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("Close");
				okButton.setFont(new Font("Tahoma", Font.BOLD, 12));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						va.dispose();
					}
				});			
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		addActions();


	}

	public void addActions(){


		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			
				String name=(String)comboBox.getSelectedItem();	
				comboBox_1.removeAllItems();
				Iterator li=new AtomHandler().getAtoms(name).listIterator();
				while(li.hasNext()){
					comboBox_1.addItem(li.next());
				}
			}
		});
		ListCellRenderer comboRenderer = new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if(value!=null){
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					setToolTipText(value.toString());
				}
				return this;
			}

		};
		comboBox_1.setRenderer(comboRenderer);
	}

}
