package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadModel.java,v 1.30 2004/04/15 16:06:59 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
 *
 */

public class DownloadModel
    extends AbstractTreeTableModel
    implements LanguageListener {

    static protected String[] cNames = {
        "", "", "", "", "", "", "", "", "", ""};

    //Download-Stati
    public static String suchen = "";
    public static String laden = "";
    public static String keinPlatz = "";
    public static String fertigstellen = "";
    public static String fehlerBeimFertigstellen = "";
    public static String fertig = "";
    public static String abbrechen = "";
    public static String abgebrochen = "";
    public static String dataWirdErstellt = "";

    //Source-Stati
    public static String ungefragt = "";
    public static String versucheZuVerbinden = "";
    public static String ggstZuAlteVersion = "";
    public static String kannDateiNichtOeffnen = "";
    public static String warteschlange = "";
    public static String keineBrauchbarenParts = "";
    public static String uebertragung = "";
    public static String nichtGenugPlatz = "";
    public static String fertiggestellt = "";
    public static String keineVerbindungMoeglich = "";
    public static String pausiert = "";
    public static String position = "";
    public static String versucheIndirekt = "";
    public static String warteschlangeVoll = "";
    public static String eigenesLimitErreicht = "";
    public static String indirekteVerbindungAbgelehnt = "";

    static protected Class[] cTypes = {
        TreeTableModel.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class};

    public DownloadModel() {
        super(new DownloadRootNode());
        LanguageSelector.getInstance().addLanguageListener(this);
    }

    protected Object[] getChildren(Object node) {
        if (node.getClass() != DownloadDO.class &&
            node.getClass() != DownloadSourceDO.class
            && node.getClass() != WaitNode.class) {
            return ( (DownloadNode) node).getChildren();
        }
        else if (node.getClass() == DownloadDO.class) {
            return ( (DownloadDO) node).getSources();
        }
        return null;
    }

    public int getChildCount(Object node) {
        if (node.getClass() != DownloadDO.class &&
            node.getClass() != DownloadSourceDO.class
            && node.getClass() != WaitNode.class) {
            return ( (DownloadNode) node).getChildCount();
        }
        else if (node.getClass() == DownloadDO.class) {
            return ( (DownloadDO) node).getSources().length;
        }
        return 0;
    }

    public Object getChild(Object node, int i) {
        Object[] obj = getChildren(node);
        if (obj == null || i > obj.length - 1) {
            return null;
        }
        return obj[i];
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
        DownloadColumnValue columnValue = null;
        if (node.getClass() == DownloadMainNode.class &&
            ( (DownloadMainNode) node).getType() == DownloadMainNode.ROOT_NODE) {
            columnValue = (DownloadColumnValue) ( (DownloadMainNode) node).
                getDownloadDO();
        }
        else if (node instanceof DownloadColumnValue) {
            columnValue = (DownloadColumnValue) node;
        }
        if (columnValue != null) {
            switch (column) {
                case 0:
                    return columnValue.getColumn0();
                case 1:
                    return columnValue.getColumn1();
                case 2:
                    return columnValue.getColumn2();
                case 3:
                    return columnValue.getColumn3();
                case 4:
                    return columnValue.getColumn4();
                case 5:
                    return columnValue.getColumn5();
                case 6:
                    return columnValue.getColumn6();
                case 7:
                    return columnValue.getColumn7();
                case 8:
                    return columnValue.getColumn8();
                case 9:
                    return columnValue.getColumn9();
                default:
                    return "";
            }

        }
        return "";
    }

    public static String powerdownload(int pwdl) {
        if (pwdl == 0) {
            return "1:1,0";
        }
        double power = pwdl;
        power = power / 10 + 1;
        String temp = Double.toString(power);
        temp = temp.replace('.', ',');
        return "1:" + temp;
    }

    public static String parseGroesse(long groesse) {
        double share = Double.parseDouble(Long.toString(groesse));
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
        if (result.indexOf(".") + 3 < result.length()) {
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

    public static String getSpeedAsString(long speed) {
        if (speed == 0) {
            return "0 Bytes/s";
        }
        double size = speed;
        int faktor = 1;
        if (size < 1024) {
            faktor = 1;
        }
        else {
            faktor = 1024;

        }
        size = size / faktor;
        String s = Double.toString(size);
        if (s.indexOf(".") + 3 < s.length()) {
            s = s.substring(0, s.indexOf(".") + 3);
        }
        if (faktor == 1) {
            s += " Bytes/s";
        }
        else {
            s += " kb/s";
        }
        return s;
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        ungefragt = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat1"));
        versucheZuVerbinden = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.queue.userstat2"));
        ggstZuAlteVersion = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat3"));
        kannDateiNichtOeffnen = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.queue.userstat4"));
        warteschlange = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat5"));
        keineBrauchbarenParts = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.queue.userstat6"));
        uebertragung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat7"));
        nichtGenugPlatz = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat8"));
        fertiggestellt = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.queuestat14"));
        keineVerbindungMoeglich = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.verbindungunmoeglich"));
        pausiert = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat13"));
        position = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat51"));
        versucheIndirekt = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.userstat10"));
        eigenesLimitErreicht = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.eigeneslimiterreicht"));
        indirekteVerbindungAbgelehnt = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.indverbindungabgelehnt"));
        suchen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.queuestatlook"));
        laden = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(
            ".root.mainform.queue.queuestattransfer"));
        keinPlatz = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.queuestat1"));
        fertigstellen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.queuestat12"));
        fertig = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.queuestat14"));
        abbrechen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.queuestat15"));
        abgebrochen = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.queue.queuestat17"));
        warteschlangeVoll = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.downloadform.warteschlangevoll"));
        fehlerBeimFertigstellen = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.fehlerbeimfertigstellen"));
        dataWirdErstellt = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.javagui.downloadform.datawirderstellt"));
    }
}
