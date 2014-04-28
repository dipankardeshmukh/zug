package com.automature.zug.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ComponentMover extends MouseAdapter implements MouseMotionListener{
	
	Point mouseDownScreenCoords;
	Point mouseDownCompCoords;
	Component comp;
	public ComponentMover(Component cmp){
		comp=cmp;
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
	}
	
	public void mousePressed(MouseEvent e) {
		this.mouseDownScreenCoords = e.getLocationOnScreen();
		this.mouseDownCompCoords = e.getPoint();
	}
	
	public void mouseDragged(MouseEvent e) {  
		if(!e.isMetaDown()){  
			Point currCoords = e.getLocationOnScreen();
			comp.setLocation(this.mouseDownScreenCoords.x + (currCoords.x - this.mouseDownScreenCoords.x) - this.mouseDownCompCoords.x,
					this.mouseDownScreenCoords.y + (currCoords.y - this.mouseDownScreenCoords.y) - this.mouseDownCompCoords.y);
		}  
	}
	
}
