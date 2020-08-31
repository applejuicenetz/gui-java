package de.applejuicenet.client.gui.share.table;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/ShareTableCellRenderer.java,v 1.2 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ShareTableCellRenderer
    implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        ShareNode node = (ShareNode) ( (TreeTableModelAdapter) table.getModel()).
            nodeForRow(row);

        JLabel aLabel = new JLabel();
        if (column == 1) {
            aLabel.setFont(table.getFont());
            aLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            aLabel.setOpaque(true);
            if (node.getPath().length() == 0) {
                aLabel.setText( (String) value);
            }
        }
        if (isSelected) {
            aLabel.setBackground(table.getSelectionBackground());
            aLabel.setForeground(table.getSelectionForeground());
        }
        else {
            aLabel.setBackground(table.getBackground());
            aLabel.setForeground(table.getForeground());
        }
        return aLabel;
    }
}
