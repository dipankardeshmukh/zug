package com.automature.zug.gui.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

public class SmallImageButton extends JButton {
	static private int width=23;
	static private int height=23;
	
	public SmallImageButton(){
		super();
		init();
	}
	
	public SmallImageButton(String label){
		super(label);
		init();
	}
	
	private void init(){
		setForeground(Color.LIGHT_GRAY);
		setBackground(Color.GRAY);
		Dimension size=new Dimension(width,height);
		setMinimumSize(size);
		setSize(size);
		setPreferredSize(size);
		setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
	}
}
