package de.applejuicenet.client.gui;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.ArrayList;
import de.applejuicenet.client.shared.dac.ServerDO;
import java.util.Iterator;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ServerTableModel extends AbstractTableModel {
  final static String[] COL_NAMES =
      {"Name", "DynIP", "Port", "Letztes mal online"};

  ArrayList servers = new ArrayList();

  public ServerTableModel(HashMap content) {
      super();
      resetTable(content);
  }

  public Object getRow(int row) {
      if ((servers != null) && (row < servers.size())) {
          return servers.get(row);
      }
              return null;
  }

  public Object getValueAt(int row, int column) {
      if ((servers == null) || (row >= servers.size())) {
          return "";
      }

      ServerDO server = (ServerDO)servers.get(row);
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
      if (s == null)
          s = "";
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
    while (it.hasNext()){
      ServerDO server = (ServerDO) it.next();
      int index = servers.indexOf(server);
      if (index==-1)  // Der Server ist neu
        servers.add(server);
      else{     // Der Server hat sich verändert
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
    while (it.hasNext()){
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