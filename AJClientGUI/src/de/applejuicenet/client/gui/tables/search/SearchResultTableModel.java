package de.applejuicenet.client.gui.tables.search;

import javax.swing.table.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/search/Attic/SearchResultTableModel.java,v 1.1 2003/07/01 18:41:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchResultTableModel.java,v $
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
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