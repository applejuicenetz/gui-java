package de.applejuicenet.client.gui.tables.server;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/server/Attic/ServerTableCellRenderer.java,v 1.3 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ServerTableCellRenderer.java,v $
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
 * Historie eingefügt.
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
    ServerDO server = (ServerDO) ( (ServerTableModel) table.getModel()).getRow(
        row);
    JPanel returnPanel = new JPanel(new BorderLayout());
    JLabel image = null;
    long aktuelleZeit = System.currentTimeMillis();
    long tag = 24 * 60 * 60 * 1000;

    if (server.isConnected()){
      image = new JLabel(IconManager.getInstance().getIcon("serververbunden"));
    }
    else if (server.isTryConnect()){
      image = new JLabel(IconManager.getInstance().getIcon("serverversuche"));
    }
    else if (server.getTimeLastSeen() == 0 ||
      server.getTimeLastSeen() < aktuelleZeit - tag) {
      image = new JLabel(IconManager.getInstance().getIcon("aelter24h"));
    }
    else {
      image = new JLabel(IconManager.getInstance().getIcon("juenger24h"));
      //mehr Icons kommen, wenn der Core mehr kann

    }
    JLabel serverName = new JLabel("  " + (String) value);
    serverName.setFont(table.getFont());
    returnPanel.add(image, BorderLayout.WEST);
    returnPanel.add(serverName, BorderLayout.CENTER);
    if (isSelected) {
      returnPanel.setBackground(table.getSelectionBackground());
      returnPanel.setForeground(table.getSelectionForeground());
      image.setBackground(table.getSelectionBackground());
      serverName.setBackground(table.getSelectionBackground());
      image.setForeground(table.getSelectionForeground());
      serverName.setBackground(table.getSelectionForeground());
    }
    else {
      returnPanel.setBackground(table.getBackground());
      returnPanel.setForeground(table.getForeground());
      image.setBackground(table.getBackground());
      serverName.setBackground(table.getBackground());
      image.setForeground(table.getForeground());
      serverName.setBackground(table.getForeground());
    }
    return returnPanel;
  }
}