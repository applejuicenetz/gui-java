package de.applejuicenet.client.gui.tables.server;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.ServerDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/server/Attic/ServerTableCellRenderer.java,v 1.6 2004/02/24 18:26:41 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ServerTableCellRenderer.java,v $
 * Revision 1.6  2004/02/24 18:26:41  maj0r
 * Schrift korrigiert.
 *
 * Revision 1.5  2004/02/24 15:38:11  maj0r
 * CellRenderer optimiert indem die Komponenten in den DOs gehalten werden.
 *
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class ServerTableCellRenderer
    implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        ServerDO server = (ServerDO) ( (ServerTableModel) table.getModel()).
            getRow(
            row);
        long aktuelleZeit = System.currentTimeMillis();
        long tag = 24 * 60 * 60 * 1000;
        JLabel serverLabel = new JLabel();
        if (server.isConnected()) {
            serverLabel.setIcon(IconManager.getInstance().getIcon(
                "serververbunden"));
        }
        else if (server.isTryConnect()) {
            serverLabel.setIcon(IconManager.getInstance().getIcon(
                "serverversuche"));
        }
        else if (server.getTimeLastSeen() == 0 ||
                 server.getTimeLastSeen() < aktuelleZeit - tag) {
            serverLabel.setIcon(IconManager.getInstance().getIcon("aelter24h"));
        }
        else {
            serverLabel.setIcon(IconManager.getInstance().getIcon("juenger24h"));
            //mehr Icons kommen, wenn der Core mehr kann
        }
        serverLabel.setFont(table.getFont());
        serverLabel.setText((String) value);
        serverLabel.setOpaque(true);
        if (isSelected) {
            serverLabel.setBackground(table.getSelectionBackground());
            serverLabel.setForeground(table.getSelectionForeground());
        }
        else {
            serverLabel.setBackground(table.getBackground());
            serverLabel.setForeground(table.getForeground());
        }
        return serverLabel;
    }
}
