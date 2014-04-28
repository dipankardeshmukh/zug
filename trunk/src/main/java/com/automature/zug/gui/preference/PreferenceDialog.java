package com.automature.zug.gui.preference;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.Color;

import javax.swing.BoxLayout;

import java.awt.Component;

import javax.swing.border.BevelBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.border.SoftBevelBorder;

import com.automature.zug.gui.ComponentMover;
import com.automature.zug.gui.ZugGUI;
import com.automature.zug.gui.model.InprocessPackage;
import com.automature.zug.gui.model.LanguagePackage;
import com.automature.zug.gui.model.ReporterParams;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;

public class PreferenceDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private IniHandler iniHandler;
	InprocessPackageHolderPanel iph;
	AdapterPanel adapterPanel;
	OutProcessPanel otp;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PreferenceDialog dialog = new PreferenceDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PreferenceDialog() {

		init();
		iniHandler=new IniHandler();
		iph=new InprocessPackageHolderPanel(iniHandler.readInprocessPackages());
		adapterPanel=new AdapterPanel(iniHandler.readConfigurations().getReporterParams());
		otp=new OutProcessPanel(iniHandler.readScriptLocation(), iniHandler.readOutProcess());
		addInprocesses();
		//addOutProcesses();
		ComponentMover cm=new ComponentMover(this);
		setSelectionButton(btnNewButton);


	}

	private void addAdapter() {
		// TODO Auto-generated method stub
		contentPanel.remove(iph);
		contentPanel.remove(otp);
		contentPanel.add(adapterPanel,BorderLayout.CENTER);
	}

	private void addOutProcesses() {
		// TODO Auto-generated method stub
		contentPanel.remove(adapterPanel);
		contentPanel.remove(iph);
		contentPanel.add(otp,BorderLayout.CENTER);
	}

	private void refereshContentPanel(){
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void addInprocesses() {
		// TODO Auto-generated method stub

		contentPanel.remove(adapterPanel);
		contentPanel.remove(otp);
		contentPanel.add(iph,BorderLayout.CENTER);

	}

	private void init() {

		// TODO Auto-generated method stub
		setBackground(Color.DARK_GRAY);
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 750, 600);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.DARK_GRAY);
		contentPanel.setBorder(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel.setBackground(Color.BLACK);
			contentPanel.add(panel, BorderLayout.NORTH);
			{
				JLabel lblNewLabel = new JLabel("Preferences");
				lblNewLabel.setForeground(Color.LIGHT_GRAY);
				panel.add(lblNewLabel);
				lblNewLabel.setBackground(Color.BLACK);
				lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
				lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
			panel.setBackground(Color.DARK_GRAY);
			contentPanel.add(panel, BorderLayout.WEST);

			Dimension buttonDimenstion=new Dimension(120,40);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{89, 0};
			gbl_panel.rowHeights = new int[]{19, 23, 23, 0};
			gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				btnNewButton = new JButton("In-Process");
				
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addInprocesses();
						refereshContentPanel();
						setSelectionButton(btnNewButton);
				
					}
				});
				setDefaultsForButton(btnNewButton);
				
				GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
				gbc_btnNewButton.anchor = GridBagConstraints.WEST;
				gbc_btnNewButton.insets = new Insets(0, 0, 2, 0);
				gbc_btnNewButton.gridx = 0;
				gbc_btnNewButton.gridy = 0;
				panel.add(btnNewButton, gbc_btnNewButton);
			}
			{
				btnNewButton_1 = new JButton("OutProcess");
			
				btnNewButton_1.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						addOutProcesses();
						refereshContentPanel();
						setSelectionButton(btnNewButton_1);
					}
				});
				setDefaultsForButton(btnNewButton_1);
				GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
				gbc_btnNewButton_1.anchor = GridBagConstraints.WEST;
				gbc_btnNewButton_1.insets = new Insets(0, 0, 2, 0);
				gbc_btnNewButton_1.gridx = 0;
				gbc_btnNewButton_1.gridy = 1;
				panel.add(btnNewButton_1, gbc_btnNewButton_1);
			}
			{
				btnNewButton_2 = new JButton("Adapter");
				btnNewButton_2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addAdapter();
						refereshContentPanel();
						setSelectionButton(btnNewButton_2);
					}
				});
				
				setDefaultsForButton(btnNewButton_2);
				
				GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
				gbc_btnNewButton_2.anchor = GridBagConstraints.WEST;
				gbc_btnNewButton_2.gridx = 0;
				gbc_btnNewButton_2.gridy = 2;
				panel.add(btnNewButton_2, gbc_btnNewButton_2);
			}

		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
			buttonPane.setBackground(Color.DARK_GRAY);

			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			Dimension buttonDimenstion=new Dimension(60,30);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						saveAndClose();						
					}
				});
				okButton.setForeground(Color.GRAY);
				okButton.setBackground(Color.DARK_GRAY);
				okButton.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
				okButton.setActionCommand("OK");
				okButton.setSize(buttonDimenstion);
				okButton.setMinimumSize(buttonDimenstion);
				okButton.setPreferredSize(buttonDimenstion);
				okButton.setFocusable(false);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						PreferenceDialog.this.dispose();
					}
				});
				cancelButton.setForeground(Color.LIGHT_GRAY);
				cancelButton.setBackground(Color.DARK_GRAY);
				cancelButton.setActionCommand("Cancel");
				cancelButton.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
				cancelButton.setSize(buttonDimenstion);
				cancelButton.setMinimumSize(buttonDimenstion);
				cancelButton.setPreferredSize(buttonDimenstion);
				cancelButton.setFocusable(false);
				buttonPane.add(cancelButton);
			}
		}

	}
	
	public void setSelectionButton(JButton button){
		setDefaultsForbuttons();
		button.setFont(new Font("Tahoma", Font.BOLD, 13));
		button.setForeground(Color.WHITE);
		button.repaint();
		
	}
	
	public void setDefaultsForbuttons(){
		setDefaultsForButton(this.btnNewButton);
		setDefaultsForButton(this.btnNewButton_1);
		setDefaultsForButton(this.btnNewButton_2);
	}
	
	public void setDefaultsForButton(JButton button){
		
		Dimension buttonDimenstion=new Dimension(120,40);
		button.setForeground(Color.LIGHT_GRAY);
		button.setBackground(Color.DARK_GRAY);
		button.setFont(new Font("Tahoma", Font.PLAIN, 11));
		button.setSize(buttonDimenstion);
		button.setMinimumSize(buttonDimenstion);
		button.setPreferredSize(buttonDimenstion);
		button.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		button.setFocusable(false);
		button.revalidate();
		button.repaint();
	}
	
	public void saveIniFile(){
	
			// TODO Auto-generated method stub
			Set<InprocessPackage>ipp=iph.getInprocessPackages();
			Set<String> scriptLocation=otp.getScriptLocations();
			Set<LanguagePackage> lps=otp.getLanguagePackages();
			ReporterParams rps=adapterPanel.getReporterParams();
			iniHandler.saveFile(ipp,scriptLocation,lps,rps);
	}
	
	public void saveAndClose(){
		try{
			saveIniFile();
			ZugGUI.updatePreferences();
			
		}catch(Exception e){
			System.err.println("Error saving file "+e.getMessage());
		}finally{
			PreferenceDialog.this.dispose();			
		}
	}
}
