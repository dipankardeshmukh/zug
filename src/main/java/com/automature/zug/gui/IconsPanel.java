package com.automature.zug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class IconsPanel {

	static private boolean optionButtonProd = false,
			optionButtonPerf = false;
	static private JButton show;
	//static public JCheckBox chckbxDebug;
	static public JCheckBox chckbxVerbose;
	static public JCheckBox chckbxAutorecover;
	static public JCheckBox chckbxNoVerify;
	static public JCheckBox chckbxNoExecute;
	static public JCheckBox chckbxDebugger;
	static private JMenu mnNewMenu = new JMenu();
	static private String fileName=null;
	static JPanel iconPanel = new JPanel();
	static JButton debugger;
	static private JButton debuggerClicked;
	static private JButton btnExecute;
	private static JButton btnHide;
	static OptionDialog od=new OptionDialog();
	
	public static String getFileName() {
		return fileName;
	}

	
	
	public static ArrayList<String> getOptions() {
		
		ArrayList<String> arr = new ArrayList<String>();
		if(chckbxAutorecover.isSelected()){
			arr.add("-Autorecover");
		}
		if(chckbxVerbose.isSelected()){
			arr.add("-verbose");
		}
	//	arr.add(chckbxDebug.isSelected());
		if(chckbxNoVerify.isSelected()){
			arr.add("-noverify");
		}
		if(chckbxNoExecute.isSelected()){
			arr.add("-NoExecute");
		}
		if(chckbxDebugger.isSelected()){
			arr.add("-debugger");
		}
		
		return arr;
	}

	public static void clearOptions(){
		
		chckbxNoVerify.setSelected(false);		
	//	chckbxDebug.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(false);
		chckbxNoExecute.setSelected(false);
		chckbxDebugger.setSelected(false);

		optionButtonProd = false;
		optionButtonPerf = false;
		
		od.clearOption();
		
	}
	
	
	
	
	public static ArrayList<String> getList() {
		
		//return OptionGUI.al;
		return od.getOptions();
	}

	static void initializeMenuOption() {

		mnNewMenu.setBounds(530, 11, 17, 47);
		mnNewMenu.setHorizontalAlignment(SwingConstants.LEFT);
		mnNewMenu.setBackground(Color.WHITE);
		mnNewMenu.setEnabled(true);
		mnNewMenu.validate();

		chckbxAutorecover = new JCheckBox("Autorecover  ");
		chckbxAutorecover.setHorizontalAlignment(SwingConstants.CENTER);
		mnNewMenu.add(chckbxAutorecover);
		chckbxAutorecover.setForeground(Color.DARK_GRAY);
		chckbxAutorecover.setBackground(Color.WHITE);

		chckbxNoExecute = new JCheckBox("NoExecute");
		mnNewMenu.add(chckbxNoExecute);
		chckbxNoExecute.setForeground(Color.DARK_GRAY);
		chckbxNoExecute.setBackground(Color.WHITE);

		chckbxNoVerify = new JCheckBox("NoVerify");
		mnNewMenu.add(chckbxNoVerify);
		chckbxNoVerify.setForeground(Color.DARK_GRAY);
		chckbxNoVerify.setBackground(Color.WHITE);

		chckbxVerbose = new JCheckBox("Verbose");
		mnNewMenu.add(chckbxVerbose);
		chckbxVerbose.setForeground(Color.DARK_GRAY);
		chckbxVerbose.setBackground(Color.WHITE);
		
		chckbxDebugger=new JCheckBox("Debugger");
		mnNewMenu.add(chckbxDebugger);
		chckbxDebugger.setForeground(Color.DARK_GRAY);
		chckbxDebugger.setBackground(Color.WHITE);
	}

	public static void setDevelopmentOptions() {
		
		chckbxNoVerify.setSelected(false);		
//		chckbxDebug.setSelected(true);
		chckbxVerbose.setSelected(true);
		chckbxAutorecover.setSelected(false);
	//	chckbxNoExecute.setSelected(false);
		chckbxDebugger.setSelected(false);

	}

	public static void setPerformanceOptions() {
		
		chckbxNoVerify.setSelected(true);		
//		chckbxDebug.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(false);
		//chckbxNoExecute.setSelected(true);
		optionButtonPerf = true;

	}

	public static void setProductionOptions() {
		
		chckbxNoVerify.setSelected(false);		
	//	chckbxDebug.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(true);
	//	chckbxNoExecute.setSelected(false);
		optionButtonProd = true;

	}

	static void setIconsPanelProperty(final OptionsPanel optionPanel) {

		Border border1 = new LineBorder(Color.GRAY, 1);
		//iconPanel.setBorder(border1);
		iconPanel.setBackground(Color.WHITE);
		iconPanel.setLayout(null);
		iconPanel.setMinimumSize(new Dimension(400,30));

		final JButton button = new JButton("");
		button.setToolTipText("Browse testsuite file");
		button.setBounds(0, 0, 33, 33);
		iconPanel.add(button);
		button.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\browse.jpg"));
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setBackground(Color.WHITE);

		JButton btnOptions = new JButton("");
		btnOptions.setToolTipText("Options");
		btnOptions.setBounds(35, 0, 33, 33);
		btnOptions.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\options.jpg"));
		iconPanel.add(btnOptions);
		btnOptions.setBackground(Color.WHITE);
		btnOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mnNewMenu.doClick();
			}
		});
		final JButton btnNewButton = new JButton();
		btnNewButton.setToolTipText("More options");
		btnNewButton.setBounds(69, 0, 33, 33);
		btnNewButton.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\moreops.png"));
		iconPanel.add(btnNewButton);
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setForeground(Color.DARK_GRAY);
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setBorder(null);
		btnNewButton.setFocusable(false);

		btnExecute = new JButton("");
		btnExecute.setToolTipText("Execute");
		btnExecute.setBounds(103, 0, 33, 33);
		btnExecute.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\play.jpg"));
		iconPanel.add(btnExecute);
		btnExecute.setBackground(Color.WHITE);

		final JButton btnStop = new JButton("");
		btnStop.setToolTipText("Stop");
		btnStop.setBounds(138, 0, 33, 33);
		btnStop.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\stop.jpg"));
		iconPanel.add(btnStop);
		btnStop.setBackground(Color.WHITE);

		JButton button_1 = new JButton("");
		button_1.setToolTipText("Clear Screen");
		button_1.setBounds(172, 0, 33, 33);
		button_1.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\ClearScreen.png"));
		iconPanel.add(button_1);
		button_1.setForeground(Color.WHITE);
		button_1.setFocusable(false);
		button_1.setBorder(null);
		button_1.setBackground(Color.WHITE);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ZugGUI.clearConsole();
			}
		});

		final JButton btnProduction = new JButton("");
		btnProduction.setToolTipText("Production");
		btnProduction.setBounds(209, 0, 33, 33);
		btnProduction.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\production.jpg"));
		iconPanel.add(btnProduction);
		btnProduction.setBackground(Color.WHITE);
		btnProduction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setProductionOptions();
				optionButtonPerf = false;
				btnNewButton.doClick();
			}
		});
		btnProduction.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				btnProduction.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent e) {
				btnProduction.setBackground(Color.LIGHT_GRAY);
			}
		});

		final JButton btnPerformance = new JButton("");
		btnPerformance.setToolTipText("Performance");
		btnPerformance.setBounds(243, 0, 33, 33);
		btnPerformance.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\performance.png"));
		iconPanel.add(btnPerformance);
		btnPerformance.setBackground(Color.WHITE);

		btnPerformance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				optionButtonProd = false;

				btnNewButton.doClick();
			}
		});
		btnPerformance.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				btnPerformance.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent e) {
				btnPerformance.setBackground(Color.LIGHT_GRAY);
			}
		});

		final JButton btnDevelopment = new JButton("");
		btnDevelopment.setToolTipText("Development");
		btnDevelopment.setBounds(278, 0, 33, 33);
		btnDevelopment.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\development.jpg"));
		iconPanel.add(btnDevelopment);
		btnDevelopment.setBackground(Color.WHITE);

		JMenuBar menuBar_1 = new JMenuBar() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {

				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.WHITE);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		menuBar_1.setBounds(35, 30, 5, 4);
		iconPanel.add(menuBar_1);
		menuBar_1.setBorderPainted(false);
		menuBar_1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar_1.setMargin(new Insets(1, 1, 1, 1));
		initializeMenuOption();
		menuBar_1.add(mnNewMenu);

		btnHide = new JButton("");
		btnHide.setBounds(313, 0, 33, 33);
		btnHide.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\max.png"));
		btnHide.setToolTipText("Hide runzug command");
		iconPanel.add(btnHide);
		show = new JButton();
		show.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\min.jpg"));
		show.setBackground(Color.WHITE);
		show.setToolTipText("show runzug command");
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				optionPanel.showRunCommand();
				hideZugCommandButton();
				ZugGUI.updateFrame();
			}
		});
		btnHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				optionPanel.hideRunCommand();
				
				showZugCommandButton();
				ZugGUI.updateFrame();
			}
		});
		btnHide.setBackground(Color.WHITE);

		debugger = new JButton();
		debugger.setToolTipText("Show debugger options");
		debuggerClicked = new JButton();
		debuggerClicked.setToolTipText("Hide debugger options");
		debuggerClicked.setBackground(Color.BLACK);
		debuggerClicked.setBounds(351, 0, 33, 33);
		debuggerClicked.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\bug_invert.png"));
		debugger.setEnabled(false);
		debugger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				optionPanel.showDebuggerControls();
				showDebuggerButton();
			}
		});
		debuggerClicked.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				optionPanel.hideDebuggerControls();
				hideDebuggerButton();
			}
		});
		debugger.setBackground(Color.WHITE);
		debugger.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\bug.png"));
		debugger.setBounds(351, 0, 33, 33);
		iconPanel.add(debugger);
		
		JButton reloadButton=new JButton("");
		reloadButton.setToolTipText("reload test suite file");
		reloadButton.setBounds(386, 0, 33, 33);
		iconPanel.add(reloadButton);
		reloadButton.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\reload1.jpg"));
		reloadButton.setEnabled(true);
		reloadButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		reloadButton.setForeground(Color.DARK_GRAY);
		reloadButton.setBackground(Color.WHITE);
		reloadButton.setFocusable(false);

		reloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(fileName!=null && StringUtils.isNotBlank(fileName)){
					ZugGUI.loadFile();
				}
			}
		});
		
		
		
		mnNewMenu.setVisible(true);
		btnDevelopment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDevelopmentOptions();
			}
		});
		btnDevelopment.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				btnDevelopment.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent e) {
				btnDevelopment.setBackground(Color.LIGHT_GRAY);
			}
		});
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ZugGUI.stopRunningTestSuite();
				
			}
		});
		btnStop.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				btnStop.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent e) {
				btnStop.setBackground(Color.LIGHT_GRAY);
			}
		});
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						if (ZugGUI.runningStatus) {
							System.out
							.println("A test suite is already running.Please stop it then run again");
						} else {
							ZugGUI.runZug();
						}
					}
				});
				t.start();
			}
		});
		btnExecute.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				btnExecute.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent e) {
				btnExecute.setBackground(Color.LIGHT_GRAY);
			}
		});
		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				od.setVisible(true);
				od.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			/*	ZugGUI.disableFrame();
				if (optionButtonProd == false && optionButtonPerf == false) {
					OptionGUI og = new OptionGUI(false, false, false);
					og.generateGUI();
				} else if (optionButtonProd == true
						&& optionButtonPerf == false) {
					OptionGUI og = new OptionGUI(true, true, true);
					og.generateGUI();
					optionButtonProd = false;
				} else if (optionButtonProd == false
						&& optionButtonPerf == true) {
					OptionGUI og = new OptionGUI(false, true, true);
					og.generateGUI();
					optionButtonPerf = false;
				}*/
			}
		});
		btnNewButton.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				btnNewButton.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent e) {
				btnNewButton.setBackground(Color.LIGHT_GRAY);
			}
		});
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});
		button.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				button.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent e) {
				button.setBackground(Color.LIGHT_GRAY);
			}
		});

	}

	public static void enableDebugger(){
		if(debuggerClicked!=null)	
			debuggerClicked.setEnabled(true);
	}
	
	public static void disableDebugger(){
			try{
				iconPanel.remove(debugger);
			}catch(Exception e){
				
			}
			iconPanel.add(debuggerClicked);
			debuggerClicked.setEnabled(false);
	}
	
	static void enableExecuteButton(){
		btnExecute.setEnabled(true);
	}
	
	static void disableExecuteButton(){
		btnExecute.setEnabled(false);
	}
	
	
	
	public static void chooseFile() {
		JFileChooser chooser = new JFileChooser();
		if(fileName!=null && !fileName.isEmpty()){
			chooser.setCurrentDirectory(new File(fileName).getParentFile());
		}
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Microsoft Excel Documents", "xls");
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(iconPanel.getRootPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fileName = chooser.getSelectedFile().getAbsolutePath();
			ZugGUI.loadFile();
		}
		
	}
	
	
	public static void showDebuggerButton()
	{
		
		iconPanel.add(debuggerClicked);
		debuggerClicked.setEnabled(true);
		iconPanel.remove(debugger);
		ZugGUI.updateFrame();	
	}
	public static void hideDebuggerButton(){

		iconPanel.remove(debuggerClicked);
		iconPanel.add(debugger);
		debugger.setEnabled(true);
		ZugGUI.updateFrame();

	}
	public static void hideZugCommandButton(){
		iconPanel.remove(show);		
		iconPanel.add(btnHide);
		btnHide.setBounds(313, 0, 33, 33);
	}
	
	public static void showZugCommandButton(){
		iconPanel.remove(btnHide);
		iconPanel.add(show);
		show.setBounds(313, 0, 33, 33);
	}
	
}