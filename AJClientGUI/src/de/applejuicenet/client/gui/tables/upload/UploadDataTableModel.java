package de.applejuicenet.client.gui.tables.upload;

import java.util.*;

import javax.swing.table.*;

import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/upload/Attic/UploadDataTableModel.java,v 1.1 2003/07/01 18:41:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UploadDataTableModel.java,v $
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class UploadDataTableModel
    extends AbstractTableModel {
  final static String[] COL_NAMES = {
      "Dateiname", "Wer", "Geschwindigkeit", "Status", "Priorität", "Client"};

  ArrayList uploads = new ArrayList();

  public UploadDataTableModel() {
    super();
  }

  public Object getRow(int row) {
    if ( (uploads != null) && (row < uploads.size())) {
      return uploads.get(row);
    }
    return null;
  }

  public Object getValueAt(int row, int column) {
    if ( (uploads == null) || (row >= uploads.size())) {
      return "";
    }

    UploadDO upload = (UploadDO) uploads.get(row);
    if (upload == null) {
      return "";
    }

    String s = new String("");
    switch (column) {
      case 1:
        s = Integer.toString(upload.getShareFileID());
        break;
      case 2:
        s = upload.getNick();
        break;
      case 3:
        s = upload.getSpeed();
        break;
      case 4:
        s = upload.getStatusAsString();
        break;
      case 5:
        s = upload.getPrioritaet();
        break;
      case 6:
        s = upload.getVersion().getVersion();
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
    if (uploads == null) {
      return 0;
    }
    return uploads.size();
  }

  public Class getClass(int index) {
    return String.class;
  }

  public void setTable(HashMap changedContent) {
    Iterator it = changedContent.values().iterator();
    while (it.hasNext()) {
      UploadDO upload = (UploadDO) it.next();
      int index = uploads.indexOf(upload);
      if (index == -1) { // Der Upload ist neu
        uploads.add(upload);
      }
      else { // Der Upload hat sich verändert
        UploadDO oldUpload = (UploadDO) uploads.get(index);
        oldUpload.setActualUploadPosition(upload.getActualUploadPosition());
        oldUpload.setNick(upload.getNick());
        oldUpload.setPrioritaet(upload.getNick());
        oldUpload.setShareFileID(upload.getShareFileID());
        oldUpload.setSpeed(upload.getSpeed());
        oldUpload.setStatus(upload.getStatus());
        oldUpload.setUploadFrom(upload.getUploadFrom());
        oldUpload.setUploadID(upload.getUploadID());
        oldUpload.setUploadTo(upload.getUploadTo());
        oldUpload.setVersion(upload.getVersion());
      }
    }
    this.fireTableDataChanged();
  }

  public void clearTable() {
    uploads = null;
    this.fireTableDataChanged();
  }
}