package de.applejuicenet.client.gui.tables.upload;

import java.util.*;

import javax.swing.table.*;

import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/upload/Attic/UploadDataTableModel.java,v 1.3 2003/08/09 10:57:14 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UploadDataTableModel.java,v $
 * Revision 1.3  2003/08/09 10:57:14  maj0r
 * UploadTabelle weitergeführt.
 *
 * Revision 1.2  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class UploadDataTableModel
    extends AbstractTableModel implements LanguageListener{
  final static String[] COL_NAMES = {
      "Dateiname", "Status", "Wer", "Geschwindigkeit", "Prozent geladen", "Priorität", "Client"};

    private String uebertragung;
    private String keineVerbindungMoeglich;
    private String versucheIndirekteVerbindung;
    private String versucheZuVerbinden;
    private String warteschlange;

  ArrayList uploads = new ArrayList();

  public UploadDataTableModel() {
    super();
    LanguageSelector.getInstance().addLanguageListener(this);
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

    switch (column) {
      case 0:
        return upload.getDateiName();
      case 1:
        switch (upload.getStatus()){
            case UploadDO.AKTIVE_UEBERTRAGUNG:
                return uebertragung;
            case UploadDO.KEINE_VERBINDUNG_MOEGLICH:
                return keineVerbindungMoeglich;
            case UploadDO.VERSUCHE_INDIREKTE_VERBINDUNG:
                return versucheIndirekteVerbindung;
            case UploadDO.VERSUCHE_ZU_VERBINDEN:
                return versucheZuVerbinden;
            case UploadDO.WARTESCHLANGE:
                return warteschlange;
            default:
                return "";
        }
        case 2:
          return upload.getNick();
        case 3:
          {
              if (upload.getStatus()==UploadDO.AKTIVE_UEBERTRAGUNG){
                  return getSpeedAsString(upload.getSpeed().intValue());
              }
              else{
                  return "";
              }
          }
        case 4:
          {
              if (upload.getStatus()==UploadDO.AKTIVE_UEBERTRAGUNG){
                  return getSpeedAsString(upload.getSpeed().intValue());
              }
              else{
                  return "";
              }
          }
      case 5:
        return upload.getPrioritaet();
      case 6:
        return upload.getVersion().getVersion();
      default:
        return "Fehler";
    }
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

  public Class getClass(int column) {
    if (column==5)
        return Integer.class;
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
        oldUpload.setPrioritaet(upload.getPrioritaet());
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

    private String getSpeedAsString(long speed){
        if (speed==0)
            return "0 Bytes/s";
        double size = speed;
        int faktor = 1;
        if (size < 1024)
            faktor = 1;
        else
            faktor = 1024;

        size = size / faktor;
        String s = Double.toString(size);
        if (s.indexOf(".") + 3 < s.length()){
            s = s.substring(0, s.indexOf(".") + 3);
        }
        if (faktor==1){
            s += " Bytes/s";
        }
        else{
            s += " kb/s";
        }
        return s;
    }
    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        uebertragung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads", "uplstat1"}));
        keineVerbindungMoeglich = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads", "uplstat8"}));
        versucheIndirekteVerbindung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads", "uplstat7"}));
        versucheZuVerbinden = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads", "uplstat6"}));
        warteschlange = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "uploads", "uplstat3"}));
    }
}