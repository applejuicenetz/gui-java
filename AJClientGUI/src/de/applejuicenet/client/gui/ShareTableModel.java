package de.applejuicenet.client.gui;

import java.io.*;
import java.util.*;

import javax.swing.table.*;

import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ShareTableModel.java,v 1.3 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareTableModel.java,v $
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
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
    resetTable(content);
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
    Iterator it = changedContent.values().iterator();
    while (it.hasNext()) {
      ShareDO share = (ShareDO) it.next();
      int index = shares.indexOf(share);
      if (index == -1) { // Der Share ist neu
        shares.add(share);
      }
      else { // Der Share hat sich verändert
        ShareDO oldShare = (ShareDO) shares.get(index);
        oldShare.setChecksum(share.getCheckSum());
        oldShare.setFilename(share.getFilename());
        oldShare.setSize(share.getSize());
      }
    }
    this.fireTableDataChanged();
  }

  public void resetTable(HashMap changedContent) {
    Iterator it = changedContent.values().iterator();
    shares.clear();
    while (it.hasNext()) {
      ShareDO server = (ShareDO) it.next();
      shares.add(server);
    }
    this.fireTableDataChanged();
  }

  public void clearTable() {
    shares = null;
    this.fireTableDataChanged();
  }
}