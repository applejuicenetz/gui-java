package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.dac.UploadDO;

public class UploadTableWholeLoadedPercentCellRenderer
    extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 7794122867796993848L;

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
            Component c = ((UploadDO) obj).getWholeLoadedProgressbarComponent(table, value);
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
