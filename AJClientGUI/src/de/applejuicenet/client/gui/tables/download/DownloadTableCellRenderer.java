package de.applejuicenet.client.gui.tables.download;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.gui.tables.download.DownloadNode;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadTableCellRenderer.java,v 1.3 2003/07/02 13:54:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadTableCellRenderer.java,v $
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.2  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadTableCellRenderer
    implements TableCellRenderer {
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    DownloadNode node = (DownloadNode) ( (TreeTableModelAdapter) table.getModel()).
        nodeForRow(row);
    DownloadSourceDO server = node.getDO();
    if (column == 9) {
      JPanel returnPanel = new JPanel(new BorderLayout());
      JLabel image = new JLabel();

      long aktuelleZeit = System.currentTimeMillis();
      long tag = 24 * 60 * 60 * 1000;
      JLabel serverName = new JLabel();

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

      if (server.getVersion() == null) {
        return returnPanel;
      }
      else if (server.getVersion().getBetriebsSystem() == Version.WIN32) {
        image.setIcon(IconManager.getInstance().getIcon("winsymbol"));
      }
      else if (server.getVersion().getBetriebsSystem() == Version.LINUX) {
        image.setIcon(IconManager.getInstance().getIcon("linuxsymbol"));

        //mehr Icons kommen, wenn der Core mehr kann
      }
      serverName.setText("  " + server.getVersion().getVersion());
      serverName.setFont(table.getFont());
      returnPanel.add(image, BorderLayout.WEST);
      returnPanel.add(serverName, BorderLayout.CENTER);
      return returnPanel;
    }
    else if (column == 6) {
      if (server.isRoot()) {
        String prozent = server.getProzentGeladen();
        int i;
        if ( (i = prozent.indexOf(",")) != -1) {
          prozent = prozent.substring(0, i);
        }
        JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                                                 100);
        progress.setValue(Integer.parseInt(prozent));
        progress.setString(server.getProzentGeladen() + " %");
        progress.setStringPainted(true);
        return progress;
      }
      else {
        JLabel label1 = new JLabel();
        label1.setOpaque(true);
        if (isSelected) {
          label1.setBackground(table.getSelectionBackground());
          label1.setForeground(table.getSelectionForeground());
        }
        else {
          label1.setBackground(table.getBackground());
          label1.setForeground(table.getForeground());
        }
        return label1;
      }
    }
    else {
      return new JLabel("");
    }
  }
}