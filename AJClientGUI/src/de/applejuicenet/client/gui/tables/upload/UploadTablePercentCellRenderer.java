package de.applejuicenet.client.gui.tables.upload;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.dac.UploadDO;

public class UploadTablePercentCellRenderer
    extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        Object obj = ( (TreeTableModelAdapter) table.getModel()).nodeForRow(row);

        if (obj.getClass() == MainNode.class) {
            return super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        }
        else {
            Component c = ((UploadDO) obj).getProgressbarComponent(table, value);
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
            }
            else {
                c.setBackground(table.getBackground());
            }
            return c;
        }
    }
}
