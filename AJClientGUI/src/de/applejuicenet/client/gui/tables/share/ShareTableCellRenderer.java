package de.applejuicenet.client.gui.tables.share;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/ShareTableCellRenderer.java,v 1.4 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareTableCellRenderer.java,v $
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
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
