package de.applejuicenet.client.gui;

import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Iterator;
import de.applejuicenet.client.shared.dac.UploadDO;
import java.util.HashMap;


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

  ArrayList uploads = new ArrayList();

  public UploadDataTableModel() {
      super();
  }

  public Object getRow(int row) {
      if ((uploads != null) && (row < uploads.size())) {
          return uploads.get(row);
      }
              return null;
  }

  public Object getValueAt(int row, int column) {
      if ((uploads == null) || (row >= uploads.size())) {
          return "";
      }

      UploadDO upload = (UploadDO)uploads.get(row);
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
          s= upload.getPrioritaet();
          break;
        case 6:
          s = upload.getVersion().getVersion();
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
    while (it.hasNext()){
      UploadDO upload = (UploadDO) it.next();
      int index = uploads.indexOf(upload);
      if (index==-1)  // Der Upload ist neu
        uploads.add(upload);
      else{     // Der Upload hat sich verändert
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