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

public class SearchResultTableModel
    extends AbstractTableModel {
  final static String[] COL_NAMES = {
      "Dateiname", "Größe", "Anzahl", "Künstler", "Titel", "Bitrate"};

  Object[] results;

  public SearchResultTableModel() {
    super();
  }

  public Object getRow(int row) {
    if ( (results != null) && (row < results.length)) {
      return results[row];
    }
    return null;
  }

  public Object getValueAt(int row, int column) {
    if ( (results == null) || (row >= results.length)) {
      return "";
    }

    Object result = results[row];
    if (result == null) {
      return "";
    }

    String s = new String("");
    switch (column) {
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
    if (results == null) {
      return 0;
    }
    return results.length;
  }

  public Class getClass(int index) {
    return String.class;
  }

  public void setTable(Object[] resultArray) {
    results = resultArray;
    this.fireTableDataChanged();
  }

  public void clearTable() {
    results = null;
    this.fireTableDataChanged();
  }

}