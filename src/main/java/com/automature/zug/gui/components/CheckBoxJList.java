package com.automature.zug.gui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;




public class CheckBoxJList extends JList{

	public CheckBoxJList() {
		super();
		setProperties();
		// TODO Auto-generated constructor stub
	}

	public CheckBoxJList(ListModel dataModel) {
		super(dataModel);
		setProperties();

		// TODO Auto-generated constructor stub
	}

	public CheckBoxJList(CheckListItem[] listData) {
		super(listData);
		setProperties();
		// TODO Auto-generated constructor stub
	}

	public CheckBoxJList(Vector<CheckListItem> listData) {
		super(listData);
		setProperties();
		// TODO Auto-generated constructor stub
	}

	private void setProperties(){
		setCellRenderer(new CheckListRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent event){
				JList list = (JList) event.getSource();
				try{
					int index = list.locationToIndex(event.getPoint());
					//System.out.println(list.getSelectedValue());
					refreshList(list, index);
					CheckListItem item = (CheckListItem)
							list.getModel().getElementAt(index);
					item.setSelected(! item.isSelected());
					list.repaint(list.getCellBounds(index, index));

				}catch(IndexOutOfBoundsException e){

				}
			}		
		});
	}
	private void refreshList(JList list, int index) {
		// TODO Auto-generated method stub
		ListModel lm=getModel();
		for(int i=0;i<lm.getSize();i++){
			if(i==index)
				continue;
			CheckListItem item = (CheckListItem)
					list.getModel().getElementAt(i);
			item.setSelected(false);
		}
	}
	
}
