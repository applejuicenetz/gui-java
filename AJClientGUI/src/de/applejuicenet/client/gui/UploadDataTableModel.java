package de.applejuicenet.client.gui;

import javax.swing.table.*;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class UploadDataTableModel extends AbstractTableModel {
  final static String[] COL_NAMES =
      {"Dateiname", "Wer", "Geschwindigkeit", "Status", "Priorität", "Client"};

  Object[] uploads;

  public UploadDataTableModel() {
      super();
  }

  public Object getRow(int row) {
      if ((uploads != null) && (row < uploads.length)) {
          return uploads[row];
      }
              return null;
  }

  public Object getValueAt(int row, int column) {
      if ((uploads == null) || (row >= uploads.length)) {
          return "";
      }

      Object upload = uploads[row];
      if (upload == null) {
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
      if (uploads == null) {
          return 0;
      }
      return uploads.length;
  }

  public Class getClass(int index) {
      return String.class;
  }

  public void setTable(Object[] uploadArray) {
      uploads = uploadArray;
      this.fireTableDataChanged();
  }

  public void clearTable() {
      uploads = null;
      this.fireTableDataChanged();
  }

}