package de.applejuicenet.client.gui;

import java.io.*;
import java.util.*;

import javax.swing.table.*;

import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ShareTableModel.java,v 1.6 2003/07/01 18:34:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareTableModel.java,v $
 * Revision 1.6  2003/07/01 18:34:28  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.5  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.4  2003/06/22 19:01:55  maj0r
 * Laden des Shares nun erst nach Bet�tigen des Buttons "Erneut laden".
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ShareTableModel
    extends AbstractTableModel {
  final static String[] COL_NAMES = {
      "Dateiname", "Größe", "Priorität"};

  ArrayList shares = new ArrayList();

  public ShareTableModel(HashMap content) {
    super();
    setTable(content);
  }

  public Object getRow(int row) {
    if ( (shares != null) && (row < shares.size())) {
      return shares.get(row);
    }
    return null;
  }

  public Object getValueAt(int row, int column) {
    if ( (shares == null) || (row >= shares.size())) {
      return "";
    }

    ShareDO share = (ShareDO) shares.get(row);
    if (share == null) {
      return "";
    }

    String s = new String("");
    switch (column) {
      case 0:
        s = share.getFilename();
        s = s.substring(s.lastIndexOf(File.separator) + 1);
        break;
      case 1:
        double size = Long.parseLong(share.getSize());
        size = size / 1048576;
        s = Double.toString(size);
        s = s.substring(0, s.indexOf(".") + 3) + " MB";
        break;
      case 2:
        s = "";
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
    if (shares == null) {
      return 0;
    }
    return shares.size();
  }

  public Class getClass(int index) {
    return String.class;
  }

  public void setTable(HashMap changedContent) {
    if (changedContent==null){
      shares.clear();
    }
    else{
      shares.clear();
      Iterator it = changedContent.values().iterator();
      while (it.hasNext()) {
        shares.add(it.next());
      }
    }
    this.fireTableDataChanged();
  }

  public void clearTable() {
    shares = null;
    this.fireTableDataChanged();
  }
}