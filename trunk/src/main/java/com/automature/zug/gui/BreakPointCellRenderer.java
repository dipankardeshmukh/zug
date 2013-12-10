package com.automature.zug.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BreakPointCellRenderer extends DefaultTableCellRenderer {

    int row;
    boolean highlight_cell;
    public BreakPointCellRenderer(int rowNo, boolean highlight){
        row = rowNo;
        highlight_cell = highlight;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);

        if(row==rowIndex){
            if(highlight_cell){
                c.setBackground(new Color(0xFF, 0x00, 0x00));
            }else{
                c.setBackground(Color.WHITE);
            }
        }

        return this;
    }
}
