package de.applejuicenet.client.gui.tablerenderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import de.applejuicenet.client.shared.dac.ServerDO;
import java.util.Date;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.Version;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DownloadTableCellRenderer
    implements TableCellRenderer {
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    DownloadNode node = (DownloadNode)((TreeTableModelAdapter)table.getModel()).nodeForRow(row);
    DownloadSourceDO server = node.getDO();
    JPanel returnPanel = new JPanel(new BorderLayout());
    JLabel image = new JLabel();

    long aktuelleZeit = System.currentTimeMillis();
    long tag = 24 * 60 * 60 * 1000;
    JLabel serverName = new JLabel();

    if (isSelected){
      returnPanel.setBackground(table.getSelectionBackground());
      returnPanel.setForeground(table.getSelectionForeground());
      image.setBackground(table.getSelectionBackground());
      serverName.setBackground(table.getSelectionBackground());
      image.setForeground(table.getSelectionForeground());
      serverName.setBackground(table.getSelectionForeground());
    }
    else{
      returnPanel.setBackground(table.getBackground());
      returnPanel.setForeground(table.getForeground());
      image.setBackground(table.getBackground());
      serverName.setBackground(table.getBackground());
      image.setForeground(table.getForeground());
      serverName.setBackground(table.getForeground());
    }

    if (server.getVersion() == null ){
      return returnPanel;
    }
    else if (server.getVersion().getBetriebsSystem()== Version.WIN32)
      image.setIcon(IconManager.getInstance().getIcon("winsymbol"));
    else if (server.getVersion().getBetriebsSystem()== Version.LINUX)
      image.setIcon(IconManager.getInstance().getIcon("linuxsymbol"));

    //mehr Icons kommen, wenn der Core mehr kann
    serverName.setText("  " + server.getVersion().getVersion());
    serverName.setFont(table.getFont());
    returnPanel.add(image, BorderLayout.WEST);
    returnPanel.add(serverName, BorderLayout.CENTER);
    return returnPanel;
  }
}