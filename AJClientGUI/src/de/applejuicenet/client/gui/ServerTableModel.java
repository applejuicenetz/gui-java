package de.applejuicenet.client.gui;

import java.util.*;

import javax.swing.table.*;

import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ServerTableModel.java,v 1.4 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ServerTableModel.java,v $
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ServerTableModel
    extends AbstractTableModel {
  final static String[] COL_NAMES = {
      "Name", "DynIP", "Port", "Letztes mal online"};

  ArrayList servers = new ArrayList();

  public ServerTableModel(HashMap content) {
    super();
    resetTable(content);
  }

  public Object getRow(int row) {
    if ( (servers != null) && (row < servers.size())) {
      return servers.get(row);
    }
    return null;
  }

  public Object getValueAt(int row, int column) {
    if ( (servers == null) || (row >= servers.size())) {
      return "";
    }

    ServerDO server = (ServerDO) servers.get(row);
    if (server == null) {
      return "";
    }

    String s = new String("");
    switch (column) {
      case 0:
        s = server.getName();
        break;
      case 1:
        s = server.getHost();
        break;
      case 2:
        s = server.getPort();
        break;
      case 3:
        s = server.getTimeLastSeenAsString();
        break;
      default:
        s = new String("Fehler");
    }
    if (s == null) {
      s = "";
    }
    return s;
  }

  public int getColumnCount() {
    return COL_NAMES.length;
  }

  public String getColumnName(int index) {
    return COL_NAMES[index];
  }

  public int getRowCount() {
    if (servers == null) {
      return 0;
    }
    return servers.size();
  }

  public Class getClass(int index) {
    return String.class;
  }

  public void setTable(HashMap changedContent) {
    Iterator it = changedContent.values().iterator();
    while (it.hasNext()) {
      ServerDO server = (ServerDO) it.next();
      int index = servers.indexOf(server);
      if (index == -1) { // Der Server ist neu
        servers.add(server);
      }
      else { // Der Server hat sich verändert
        ServerDO oldServer = (ServerDO) servers.get(index);
        oldServer.setHost(server.getHost());
        oldServer.setName(server.getName());
        oldServer.setPort(server.getPort());
        oldServer.setTimeLastSeen(server.getTimeLastSeen());
      }
    }
    this.fireTableDataChanged();
  }

  public void resetTable(HashMap changedContent) {
    Iterator it = changedContent.values().iterator();
    servers.clear();
    while (it.hasNext()) {
      ServerDO server = (ServerDO) it.next();
      servers.add(server);
    }
    this.fireTableDataChanged();
  }

  public void clearTable() {
    servers = null;
    this.fireTableDataChanged();
  }
}