package de.applejuicenet.client.gui;

import javax.swing.table.AbstractTableModel;

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

  Object[] servers;

  public ServerTableModel() {
      super();
  }

  public Object getRow(int row) {
      if ((servers != null) && (row < servers.length)) {
          return servers[row];
      }
              return null;
  }

  public Object getValueAt(int row, int column) {
      if ((servers == null) || (row >= servers.length)) {
          return "";
      }

      Object server = servers[row];
      if (server == null) {
          return "";
      }

      String s = new String("");
      switch (column) {
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
      return servers.length;
  }

  public Class getClass(int index) {
      return String.class;
  }

  public void setTable(Object[] serverArray) {
      servers = serverArray;
      this.fireTableDataChanged();
  }

  public void clearTable() {
      servers = null;
      this.fireTableDataChanged();
  }
}