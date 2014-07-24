package com.automature.zug.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableRowRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);

        c.setBackground(table.getModel().getValueAt(rowIndex,2).toString().equalsIgnoreCase("comment")? new Color(0x00, 0xB0, 0x00) : Color.WHITE);
        setText(value !=null ? value.toString() : "");
        setForeground(Color.BLACK);
        return this;
    }
}

