package com.automature.zug.gui.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.automature.zug.gui.OptionGUI;
import com.automature.zug.gui.ZugGUI;

public class MoreOptionActionListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		Object ob=event.getSource();
		ZugGUI.disableFrame();
		OptionGUI og = new OptionGUI();
		og.generateGUI();
		
	}
}
