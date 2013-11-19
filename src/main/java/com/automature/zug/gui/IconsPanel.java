package com.automature.zug.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IconsPanel {

    static String  browse_icon_path;
    static String reload_icon_path;
    static String options_icon_path;
    static String more_options_icon_path;
    static String execute_icon_path;
    static String stop_icon_path;
    static String clear_screen_icon_path;
    static String debugger_icon_path;



    static String console_icon_path;
    static String sheet_icon_path;
    static String split_icon_path;
    static String sidebar_icon_path;


	static private boolean optionButtonProd = false,
			               optionButtonPerf = false,
                           optionButtonDev = false;
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
    //static JToolBar iconPanel = new JToolBar();
	static JButton debugger;
	static private JButton debuggerClicked;
	static private JButton btnExecute;
	private static JButton btnHide;
    static JButton split;
    static JButton console;
    static JButton sheet;
    static JButton toggleTaskPane;
	static OptionDialog od=new OptionDialog();



    public static void setBrowse_icon_path(String browse_icon_path) {

        IconsPanel.browse_icon_path = browse_icon_path;
    }

    public static void setReload_icon_path(String reload_icon_path) {
        IconsPanel.reload_icon_path = reload_icon_path;
    }

    public static void setOptions_icon_path(String options_icon_path) {
        IconsPanel.options_icon_path = options_icon_path;
    }

    public static void setMore_options_icon_path(String more_options_icon_path) {
        IconsPanel.more_options_icon_path = more_options_icon_path;
    }

    public static void setExecute_icon_path(String execute_icon_path) {
        IconsPanel.execute_icon_path = execute_icon_path;
    }

    public static void setStop_icon_path(String stop_icon_path) {
        IconsPanel.stop_icon_path = stop_icon_path;
    }

    public static void setClear_screen_icon_path(String clear_screen_icon_path) {
        IconsPanel.clear_screen_icon_path = clear_screen_icon_path;
    }

    public static void setDebugger_icon_path(String debugger_icon_path) {
        IconsPanel.debugger_icon_path = debugger_icon_path;
    }

    public static void setConsole_icon_path(String console_icon_path) {
        IconsPanel.console_icon_path = console_icon_path;
    }

    public static void setSheet_icon_path(String sheet_icon_path) {
        IconsPanel.sheet_icon_path = sheet_icon_path;
    }

    public static void setSplit_icon_path(String split_icon_path) {
        IconsPanel.split_icon_path = split_icon_path;
    }

    public static void setSidebar_icon_path(String sidebar_icon_path) {
        IconsPanel.sidebar_icon_path = sidebar_icon_path;
    }

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

		//mnNewMenu.setBounds(70, 11, 17, 47);
		mnNewMenu.setHorizontalAlignment(SwingConstants.LEFT);
		mnNewMenu.setBackground(Color.WHITE);
		mnNewMenu.setEnabled(true);
		mnNewMenu.validate();

		chckbxAutorecover = new JCheckBox("Autorecover  ");
		chckbxAutorecover.setHorizontalAlignment(SwingConstants.CENTER);
		mnNewMenu.add(chckbxAutorecover);
		chckbxAutorecover.setForeground(Color.DARK_GRAY);
		chckbxAutorecover.setBackground(Color.WHITE);

		chckbxNoExecute   = new JCheckBox("NoExecute    ");
		mnNewMenu.add(chckbxNoExecute);
		chckbxNoExecute.setForeground(Color.DARK_GRAY);
		chckbxNoExecute.setBackground(Color.WHITE);

		chckbxNoVerify    = new JCheckBox("NoVerify       ");
		mnNewMenu.add(chckbxNoVerify);
		chckbxNoVerify.setForeground(Color.DARK_GRAY);
		chckbxNoVerify.setBackground(Color.WHITE);

		chckbxVerbose     = new JCheckBox("Verbose       ");
		mnNewMenu.add(chckbxVerbose);
		chckbxVerbose.setForeground(Color.DARK_GRAY);
		chckbxVerbose.setBackground(Color.WHITE);
		
		chckbxDebugger    = new JCheckBox("Debugger     ");
		mnNewMenu.add(chckbxDebugger);
		chckbxDebugger.setForeground(Color.DARK_GRAY);
		chckbxDebugger.setBackground(Color.WHITE);
	}

	public static void setDevelopmentOptions() {
		
		chckbxNoVerify.setSelected(false);		
//		chckbxDebug.setSelected(true);
		chckbxVerbose.setSelected(true);
		chckbxAutorecover.setSelected(true);
	//	chckbxNoExecute.setSelected(false);
	//	chckbxDebugger.setSelected(true);
        optionButtonDev = true;

	}

	public static void setPerformanceOptions() {
		
		chckbxNoVerify.setSelected(true);		
//		chckbxDebug.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(false);
        chckbxDebugger.setSelected(false);
		//chckbxNoExecute.setSelected(true);
		optionButtonPerf = true;

	}

	public static void setProductionOptions() {
		
		chckbxNoVerify.setSelected(true);
	//	chckbxDebug.setSelected(false);
		chckbxVerbose.setSelected(false);
		chckbxAutorecover.setSelected(true);
        chckbxDebugger.setSelected(false);
	//	chckbxNoExecute.setSelected(false);
		optionButtonProd = true;

	}

	static void setIconsPanelProperty(final OptionsPanel optionPanel) {

		iconPanel.setBackground(Color.lightGray);
        iconPanel.setLayout(null);
        //iconPanel.setFloatable(true);

        GuiConfig.loadIcons();

        final JButton button = new JButton("");
		button.setToolTipText("Browse test suite file");
		button.setBounds(0, 0, 33, 33);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        iconPanel.add(button);


        button.setIcon(new ImageIcon(System.getProperty("user.dir")
                + browse_icon_path));
		//		+ "\\Images\\browse.png"));
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setBackground(Color.LIGHT_GRAY);


        JButton reloadButton=new JButton("");
        reloadButton.setToolTipText("reload test suite file");
        reloadButton.setBounds(35, 0, 33, 33);
        reloadButton.setContentAreaFilled(false);
        reloadButton.setBorderPainted(false);
        iconPanel.add(reloadButton);
        reloadButton.setIcon(new ImageIcon(System.getProperty("user.dir")
                + reload_icon_path));
        reloadButton.setEnabled(true);
        reloadButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        reloadButton.setBackground(Color.LIGHT_GRAY);
        reloadButton.setFocusable(false);
        reloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(fileName!=null && StringUtils.isNotBlank(fileName)){
                    try {
                        ZugGUI.loadFile(true);
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });


        JSeparator JV = new JSeparator(SwingConstants.VERTICAL);
        JV.setBounds(67,5,20, 20);
        iconPanel.add(JV);
        //iconPanel.addSeparator(new Dimension(20,20));

        JButton btnOptions = new JButton("");
		btnOptions.setToolTipText("Options");
		btnOptions.setBounds(65, 0, 33, 33);
        btnOptions.setContentAreaFilled(false);
        btnOptions.setBorderPainted(false);
		btnOptions.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ options_icon_path));
		iconPanel.add(btnOptions);
		btnOptions.setBackground(Color.LIGHT_GRAY);
		btnOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mnNewMenu.doClick();
			}
		});


        JMenuBar menuBar_1 = new JMenuBar();
        menuBar_1.setBounds(35, 30, 5, 4);
        iconPanel.add(menuBar_1);
        menuBar_1.setBackground(Color.LIGHT_GRAY);
        menuBar_1.setBorderPainted(false);
        menuBar_1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        menuBar_1.setMargin(new Insets(1, 1, 1, 1));
        initializeMenuOption();
        menuBar_1.add(mnNewMenu);


		final JButton btnNewButton = new JButton();
		btnNewButton.setToolTipText("More options");
		btnNewButton.setBounds(90, 0, 33, 33);
        btnNewButton.setContentAreaFilled(false);
        btnNewButton.setBorderPainted(false);
		btnNewButton.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ more_options_icon_path));
		iconPanel.add(btnNewButton);
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnNewButton.setForeground(Color.DARK_GRAY);
		btnNewButton.setBackground(Color.LIGHT_GRAY);
		btnNewButton.setBorder(null);
		btnNewButton.setFocusable(false);



		btnExecute = new JButton("");
		btnExecute.setToolTipText("Execute test");
		btnExecute.setBounds(120, 0, 33, 33);
        btnExecute.setContentAreaFilled(false);
        btnExecute.setBorderPainted(false);
		btnExecute.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ execute_icon_path));
		iconPanel.add(btnExecute);
		btnExecute.setBackground(Color.LIGHT_GRAY);


		final JButton btnStop = new JButton("");
		btnStop.setToolTipText("Stop");
		btnStop.setBounds(150, 0, 33, 33);
        btnStop.setContentAreaFilled(false);
        btnStop.setBorderPainted(false);
		btnStop.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ stop_icon_path));
		iconPanel.add(btnStop);
		btnStop.setBackground(Color.LIGHT_GRAY);


		JButton button_1 = new JButton("");
		button_1.setToolTipText("Clear Screen");
		button_1.setBounds(180, 0, 33, 33);
        button_1.setContentAreaFilled(false);
        button_1.setBorderPainted(false);
		button_1.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ clear_screen_icon_path));
		iconPanel.add(button_1);
		button_1.setForeground(Color.LIGHT_GRAY);
		button_1.setFocusable(false);
		button_1.setBorder(null);
		button_1.setBackground(Color.LIGHT_GRAY);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ZugGUI.clearConsole();
			}
		});


        JSeparator JV2 = new JSeparator(SwingConstants.VERTICAL);
        JV2.setBounds(213,5,20, 20);
        iconPanel.add(JV2);
        //iconPanel.addSeparator(new Dimension(20,20));

		final JButton btnProduction = new JButton("");
		btnProduction.setToolTipText("Production mode");
		btnProduction.setBounds(217, 0, 33, 33);
        btnProduction.setContentAreaFilled(false);
        btnProduction.setBorderPainted(false);
		btnProduction.setIcon(new ImageIcon(System.getProperty("user.dir") + "/Images/production.png"));
        iconPanel.add(btnProduction);
		btnProduction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setProductionOptions();
                optionButtonProd = true;
			    btnProduction.setSelected(true);
			}
		});



		final JButton btnPerformance = new JButton("");
		btnPerformance.setToolTipText("Performance mode");
		btnPerformance.setBounds(245, 0, 33, 33);
        btnPerformance.setContentAreaFilled(false);
        btnPerformance.setBorderPainted(false);
		btnPerformance.setIcon(new ImageIcon(System.getProperty("user.dir")	+ "/Images/performance.png"));
		iconPanel.add(btnPerformance);
		btnPerformance.setBackground(Color.LIGHT_GRAY);
        btnPerformance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                setPerformanceOptions();
                optionButtonPerf = true;
                btnPerformance.setSelected(true);
			}
		});


		final JButton btnDevelopment = new JButton("");
		btnDevelopment.setToolTipText("Development mode");
		btnDevelopment.setBounds(275, 0, 33, 33);
        btnDevelopment.setContentAreaFilled(false);
        btnDevelopment.setBorderPainted(false);
		btnDevelopment.setIcon(new ImageIcon(System.getProperty("user.dir")+ "/Images/development.png"));
		iconPanel.add(btnDevelopment);
		btnDevelopment.setBackground(Color.LIGHT_GRAY);
        btnDevelopment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setDevelopmentOptions();
                optionButtonDev = true;
                btnDevelopment.setSelected(true);
            }
        });





/*
		btnHide = new JButton("");
		btnHide.setBounds(515, 0, 33, 33);
        btnHide.setContentAreaFilled(false);
        btnHide.setBorderPainted(false);
		btnHide.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\max.png"));
		btnHide.setToolTipText("Hide command");
		iconPanel.add(btnHide);
		show = new JButton();
		show.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ "\\Images\\min.jpg"));
		show.setBackground(Color.LIGHT_GRAY);
		show.setToolTipText("show  command");
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
		btnHide.setBackground(Color.LIGHT_GRAY);

*/


        JSeparator JV3 = new JSeparator(SwingConstants.VERTICAL);
        JV3.setBounds(315,5,20, 20);
        iconPanel.add(JV3);
        //iconPanel.addSeparator(new Dimension(20,20));

		debugger = new JButton();
		debugger.setToolTipText("Show debugger options");
		debuggerClicked = new JButton();
		debuggerClicked.setToolTipText("Hide debugger options");
		debuggerClicked.setBackground(Color.BLACK);
		debuggerClicked.setBounds(315, 0, 33, 33);
        debuggerClicked.setContentAreaFilled(false);
        debuggerClicked.setBorderPainted(false);
		debuggerClicked.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ debugger_icon_path));
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
		debugger.setBackground(Color.LIGHT_GRAY);
		debugger.setIcon(new ImageIcon(System.getProperty("user.dir")
				+ debugger_icon_path));
		debugger.setBounds(315, 0, 33, 33);
        debugger.setContentAreaFilled(false);
        debugger.setBorderPainted(false);
		iconPanel.add(debugger);

        JSeparator JV4 = new JSeparator(SwingConstants.VERTICAL);
        JV4.setBounds(345,5,20, 20);
        iconPanel.add(JV4);
        //iconPanel.addSeparator(new Dimension(20,20));


        split = new JButton("");
        split.setToolTipText("Split the display");
        split.setBounds(350, 0, 33, 33);
        split.setContentAreaFilled(false);
        split.setBorderPainted(false);
        split.setIcon(new ImageIcon(System.getProperty("user.dir")
                + split_icon_path));
        iconPanel.add(split);
        split.setBackground(Color.LIGHT_GRAY);


        console = new JButton("");
        //console.setMargin(new Insets(0,0,0,0));
        console.setToolTipText("Show Console only");
        console.setBounds(380, 0, 33, 33);
        console.setContentAreaFilled(false);
        console.setBorderPainted(false);
        console.setIcon(new ImageIcon(System.getProperty("user.dir")
                + console_icon_path));
        iconPanel.add(console);
        console.setFont(new Font("Tahoma", Font.PLAIN, 12));
        console.setForeground(Color.DARK_GRAY);
        console.setBackground(Color.LIGHT_GRAY);
        console.setBorder(null);
        console.setFocusable(false);


        sheet = new JButton("");
        //sheet.setMargin(new Insets(0,0,0,0));
        sheet.setToolTipText("Show sheet only");
        sheet.setBounds(410, 0, 33, 33);
        sheet.setContentAreaFilled(false);
        sheet.setBorderPainted(false);
        sheet.setIcon(new ImageIcon(System.getProperty("user.dir")
                + sheet_icon_path));
        iconPanel.add(sheet);
        sheet.setFont(new Font("Tahoma", Font.PLAIN, 12));
        sheet.setForeground(Color.DARK_GRAY);
        sheet.setBackground(Color.LIGHT_GRAY);
        sheet.setBorder(null);
        sheet.setFocusable(false);

        JSeparator JV5 = new JSeparator(SwingConstants.VERTICAL);
        JV5.setBounds(445,5,20, 20);
        iconPanel.add(JV5);

        toggleTaskPane = new JButton();
        //toggleTaskPane.setMargin(new Insets(0,0,0,0));
        toggleTaskPane.setToolTipText("Toggle Property Bar Display");
        toggleTaskPane.setBounds(450, 0, 33, 33);
        toggleTaskPane.setContentAreaFilled(false);
        toggleTaskPane.setBorderPainted(false);
        toggleTaskPane.setIcon(new ImageIcon(System.getProperty("user.dir")
                + sidebar_icon_path));
        iconPanel.add(toggleTaskPane);

		mnNewMenu.setVisible(true);

/*		btnDevelopment.addActionListener(new ActionListener() {
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
		});*/


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
                try {
                    chooseFile();
                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
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

        split.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ZugGUI.spitDisplay();
            }
        });

        console.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ZugGUI.showConsole();
            }
        });

        sheet.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ZugGUI.showTestSuite();
            }
        });

        toggleTaskPane.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if(ZugGUI.getDisplayPane().getDisplayPane().getComponentCount()>1){

                    ZugGUI.getDisplayPane().removeTaskPane();
                }else
                {
                    ZugGUI.getDisplayPane().addTaskPane();
                }
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
	
	
	
	public static void chooseFile() throws Exception {
		JFileChooser chooser = new JFileChooser();
		if(fileName!=null && !fileName.isEmpty()){
			chooser.setCurrentDirectory(new File(fileName).getParentFile());
		}
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Microsoft Excel Documents", "xls", "xlsx");

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(iconPanel.getRootPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fileName = chooser.getSelectedFile().getAbsolutePath();
			ZugGUI.loadFile(false);
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