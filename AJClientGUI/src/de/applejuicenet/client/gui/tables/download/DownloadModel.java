package de.applejuicenet.client.gui.tables.download;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadModel.java,v 1.9 2003/08/18 18:19:18 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadModel.java,v $
 * Revision 1.9  2003/08/18 18:19:18  maj0r
 * DownloadStatus in der Anzeige verfeinert.
 *
 * Revision 1.8  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.7  2003/08/09 10:56:38  maj0r
 * DownloadTabelle weitergeführt.
 *
 * Revision 1.6  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.5  2003/07/04 15:25:38  maj0r
 * Version erhöht.
 * DownloadModel erweitert.
 *
 * Revision 1.4  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.2  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.17  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadModel
    extends AbstractTreeTableModel implements LanguageListener{

  static protected String[] cNames = {"", "", "", "", "", "", "", "", "", ""};

  //Download-Stati
  private String suchen = "";
  private String laden = "";
  private String keinPlatz = "";
  private String fertigstellen = "";
  private String fertig = "";
  private String abbrechen = "";
  private String abgebrochen = "";

  //Source-Stati
  private String ungefragt = "";
  private String versucheZuVerbinden = "";
  private String ggstZuAlteVersion = "";
  private String kannDateiNichtOeffnen = "";
  private String warteschlange = "";
  private String keineBrauchbarenParts = "";
  private String uebertragung = "";
  private String nichtGenugPlatz = "";
  private String fertiggestellt = "";
  private String keineVerbindungMoeglich = "";
  private String pausiert = "";
  private String position = "";
  private String versucheIndirekt = "";

  static protected Class[] cTypes = {
      TreeTableModel.class, String.class, String.class, String.class, String.class,
      String.class, String.class, String.class, String.class, String.class};

  public DownloadModel() {
    super(new DownloadNode());
    LanguageSelector.getInstance().addLanguageListener(this);
  }

  protected Object[] getChildren(Object node) {
    DownloadNode downloadNode = ( (DownloadNode) node);
    return downloadNode.getChildren();
  }

  public int getChildCount(Object node) {
    Object[] children = getChildren(node);
    return (children == null) ? 0 : children.length;
  }

  public Object getChild(Object node, int i) {
    return getChildren(node)[i];
  }

  public int getColumnCount() {
    return cNames.length;
  }

  public String getColumnName(int column) {
    return cNames[column];
  }

  public Class getColumnClass(int column) {
    return cTypes[column];
  }

  public Object getValueAt(Object node, int column) {
    DownloadNode downloadNode = (DownloadNode)node;
    if (downloadNode.getNodeType()==DownloadNode.DOWNLOAD_NODE){
        DownloadDO downloadDO = downloadNode.getDownloadDO();
        switch (column) {
          case 0:
                return downloadDO.getFilename();
          case 1:
                return getStatus(downloadDO);
          case 2:
                return parseGroesse(downloadDO.getGroesse());
          case 3:
                return parseGroesse(downloadDO.getBereitsGeladen());
          case 4:
                return getSpeedAsString(downloadDO.getSpeedInBytes());
          case 5:
                return downloadDO.getRestZeitAsString();
          case 7:
                return parseGroesse(new Long(downloadDO.getGroesse().longValue()-downloadDO.getBereitsGeladen().longValue()));
          case 8:
                return powerdownload(downloadDO.getPowerDownload());
          default:
            return "";
        }
    }
    else if (downloadNode.getNodeType()==DownloadNode.DIRECTORY_NODE){
        DownloadSourceDO downloadSourceDO = downloadNode.getDownloadSourceDO();
        if (downloadSourceDO == null) {
          return "";
        }
          switch (column) {
            case 0:
                return downloadNode.getPfad();
            default:
              return "";
          }
    }
    else if (downloadNode.getNodeType()==DownloadNode.SOURCE_NODE){
        DownloadSourceDO downloadSourceDO = downloadNode.getDownloadSourceDO();
        if (downloadSourceDO == null) {
          return "";
        }
          switch (column) {
            case 0:
                return downloadSourceDO.getFilename();
            case 1:
                return getStatus(downloadSourceDO);
            case 2:
                return parseGroesse(new Long(downloadSourceDO.getSize()));
            case 3:
                return parseGroesse(new Long(downloadSourceDO.getBereitsGeladen()));
            case 4:
                  {
                      if (downloadSourceDO.getStatus()!=DownloadSourceDO.UEBERTRAGUNG)
                          return "";
                      else
                          return getSpeedAsString((long)downloadSourceDO.getSpeed().intValue());
                  }
            case 5:
                  return downloadSourceDO.getRestZeitAsString();
            case 6:
                  break;
            case 7:
                  return parseGroesse(new Long(downloadSourceDO.getNochZuLaden()));
            case 8:
                  return powerdownload(downloadSourceDO.getPowerDownload());
            case 9:
              if (downloadSourceDO.getVersion() != null) {
                return downloadSourceDO.getVersion().getVersion();
              }
              else {
                return "";
              }
            default:
              return "";
          }
    }
    return null;
  }

    private String getStatus(Object objectDO){
        if (objectDO instanceof DownloadDO){
            return getStatusForDownload((DownloadDO)objectDO);
        }
        else if (objectDO instanceof DownloadSourceDO){
            return getStatusForSource((DownloadSourceDO)objectDO);
        }
        else
            return "";
    }

    private String getStatusForSource(DownloadSourceDO downloadSourceDO){
        switch(downloadSourceDO.getStatus()){
            case DownloadSourceDO.UNGEFRAGT:
                    return ungefragt;
            case DownloadSourceDO.VERSUCHE_ZU_VERBINDEN:
                    return versucheZuVerbinden;
            case DownloadSourceDO.GEGENSTELLE_HAT_ZU_ALTE_VERSION:
                    return ggstZuAlteVersion;
            case DownloadSourceDO.GEGENSTELLE_KANN_DATEI_NICHT_OEFFNEN:
                    return kannDateiNichtOeffnen;
            case DownloadSourceDO.IN_WARTESCHLANGE:
                {
                    String temp = position;
                    temp = temp.replaceFirst("%d", Integer.toString(downloadSourceDO.getQueuePosition()));
                    return warteschlange + " " + temp;
                }
            case DownloadSourceDO.KEINE_BRAUCHBAREN_PARTS:
                    return keineBrauchbarenParts;
            case DownloadSourceDO.UEBERTRAGUNG:
                    return uebertragung;
            case DownloadSourceDO.NICHT_GENUEGEND_PLATZ_AUF_DER_PLATTE:
                    return nichtGenugPlatz;
            case DownloadSourceDO.FERTIGGESTELLT:
                    return fertiggestellt;
            case DownloadSourceDO.KEINE_VERBINDUNG_MOEGLICH:
                    return keineVerbindungMoeglich;
            case DownloadSourceDO.PAUSIERT:
                    return pausiert;
            case DownloadSourceDO.VERSUCHE_INDIREKT:
                    return versucheIndirekt;
            default:
                return "";
        }
    }

    private String getStatusForDownload(DownloadDO downloadDO){
        switch(downloadDO.getStatus()){
            case DownloadDO.PAUSIERT:
                return pausiert;
            case DownloadDO.ABBRECHEN:
                return abbrechen;
            case DownloadDO.AGBEGROCHEN:
                return abgebrochen;
            case DownloadDO.FERTIG:
                return fertig;
            case DownloadDO.NICHT_GENUG_PLATZ_FEHLER:
                return keinPlatz;
            case DownloadDO.SUCHEN_LADEN:
                {
                    DownloadSourceDO[] sources = downloadDO.getSources();
                    String result = "";
                    int uebertragung = 0;
                    int warteschlange = 0;
                    int status;
                    for (int i=0; i<sources.length; i++){
                        status = sources[i].getStatus();
                        if (status==DownloadSourceDO.UEBERTRAGUNG){
                            uebertragung++;
                            result =  laden;
                        }
                        else if (status==DownloadSourceDO.IN_WARTESCHLANGE){
                            warteschlange++;
                        }
                    }
                    if (result.length()==0)
                        result = suchen;
                    if (warteschlange!=0 || uebertragung!=0)
                        return result + " " + warteschlange + " (" + uebertragung +")";
                    else
                        return result;
                }
            case DownloadDO.FERTIGSTELLEN:
                return fertigstellen;
            default:
                return "";
        }
    }

    private String powerdownload(int pwdl){
        if (pwdl==0)
            return "1:1,0";
        double power = pwdl;
        power = power / 10 + 1;
        String temp = Double.toString(power);
        temp = temp.replace('.', ',');
        return "1:" + temp;
    }

    private String parseGroesse(Long groesse){
        double share = Double.parseDouble(groesse.toString());
        int faktor;
        if (share == 0) {
          return "";
        }
        if (share < 1024) {
          return groesse + " Bytes";
        }
        else if (share / 1024 < 1024) {
          faktor = 1024;
        }
        else if (share / 1048576 < 1024) {
          faktor = 1048576;
        }
        else if (share / 1073741824 < 1024) {
          faktor = 1073741824;
        }
        else {
          faktor = 1;
        }
        share = share / faktor;
        String result = Double.toString(share);
        if (result.indexOf(".") + 3 < result.length())
        {
            result = result.substring(0, result.indexOf(".") + 3);
        }
        result = result.replace('.', ',');
        if (faktor == 1024) {
          result += " KB";
        }
        else if (faktor == 1048576) {
          result += " MB";
        }
        else if (faktor == 1073741824) {
          result += " GB";
        }
        else {
          result += " ??";
        }
        return result;
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
        ungefragt = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat1"}));
        versucheZuVerbinden = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat2"}));
        ggstZuAlteVersion = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat3"}));
        kannDateiNichtOeffnen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat4"}));
        warteschlange = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat5"}));
        keineBrauchbarenParts = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat6"}));
        uebertragung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat7"}));
        nichtGenugPlatz = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat8"}));
        fertiggestellt = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestat14"}));
        keineVerbindungMoeglich = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", ""}));
        pausiert = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat13"}));
        position = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat51"}));
        versucheIndirekt = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "userstat10"}));

        suchen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestatlook"}));
        laden = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestattransfer"}));
        keinPlatz = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestat1"}));
        fertigstellen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestat12"}));
        fertig = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestat14"}));
        abbrechen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestat15"}));
        abgebrochen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "queue", "queuestat17"}));
    }
}