package de.applejuicenet.client.gui.server.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/server/table/ServerTableCellRenderer.java,v 1.3 2005/01/19 11:03:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
        Server server = (Server) ( (ServerTableModel) table.getModel()).
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
