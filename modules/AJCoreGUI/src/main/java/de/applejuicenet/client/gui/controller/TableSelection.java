package de.applejuicenet.client.gui.controller;

import javax.swing.JTable;

public final class TableSelection {
    private TableSelection() {
    }

    public static boolean selectOnlyRow(JTable table) {
        if (table.getRowCount() != 1 || table.getSelectedRow() == 0) {
            return false;
        }
        table.setRowSelectionInterval(0, 0);
        return true;
    }
}
