package com.automature.zug.gui.actionlistener;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.automature.zug.gui.sheets.SheetSaver;

public class SheetTableModelListener implements TableModelListener {

	private SheetSaver sheetSaver;

	public SheetTableModelListener(SheetSaver sheetSaver) {
		super();
		this.sheetSaver = sheetSaver;
	}



	@Override
	public void tableChanged(TableModelEvent tme) {
		// TODO Auto-generated method stub	
		TableModel tm=(TableModel)tme.getSource();			
		if (tme.getType() == TableModelEvent.UPDATE) {
			/* System.out.println("");
                 System.out.println("Cell " + tme.getFirstRow() + ", "
                         + tme.getColumn() + " changed. The new value: ");
                        // +  table.getModel().getValueAt(tme.getFirstRow(),
                         ///tme.getColumn()));
			 */                
			if(tme.getColumn()!=0){
				sheetSaver.SaveChange((String)tm.getValueAt(tme.getFirstRow(), tme.getColumn()), tme.getFirstRow(), tme.getColumn());
			}
		}
	}

}
