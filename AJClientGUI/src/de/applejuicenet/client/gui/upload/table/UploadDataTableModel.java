package de.applejuicenet.client.gui.upload.table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import de.applejuicenet.client.fassade.controller.dac.UploadDO;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.components.treetable.AbstractTreeTableModel;
import de.applejuicenet.client.gui.components.treetable.TreeTableModel;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/upload/table/Attic/UploadDataTableModel.java,v 1.4 2005/01/18 17:35:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class UploadDataTableModel
    extends AbstractTreeTableModel
    implements LanguageListener {

    final static String[] COL_NAMES = {
        "Dateiname", "Status", "Wer", "Geschwindigkeit", "Prozent geladen", "Gesamt geladen",
        "Prioritaet", "Client"};

    static protected Class[] cTypes = {
        TreeTableModel.class, String.class, String.class, String.class, String.class, String.class,
        Integer.class, String.class};

    private SimpleDateFormat formatter = new SimpleDateFormat(
        "HH:mm:ss");

    private String uebertragung;
    private String keineVerbindungMoeglich;
    private String versucheIndirekteVerbindung;
    private String versucheZuVerbinden;
    private String warteschlange;
    private UploadMainNode mainNode;

    private Map uploads = null;

    public UploadDataTableModel() {
        super(new UploadMainNode());
        mainNode = (UploadMainNode)getRoot();
        LanguageSelector.getInstance().addLanguageListener(this);
    }

    public Object getRow(int row) {
        if ( (uploads != null) && (row < uploads.size())) {
            return uploads.values().toArray()[row];
        }
        return null;
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public Object getValueAt(Object node, int column) {
        if (node.getClass() == UploadMainNode.class &&
            ( ( (UploadMainNode) node).getType() == UploadMainNode.LOADING_UPLOADS
             || ( (UploadMainNode) node).getType() == UploadMainNode.WAITING_UPLOADS
             || ( (UploadMainNode) node).getType() == UploadMainNode.REST_UPLOADS)) {
            if (column == 0) {
                return node.toString() + " (" + getChildCount(node) + ")";
            }
            else {
                return "";
            }
        }
        else if (node.getClass() == UploadDO.class) {
            UploadDO upload = (UploadDO) node;
            switch (column) {
                case 0:
                    return upload.getDateiName();
                case 1:
                    switch (upload.getStatus()) {
                        case UploadDO.AKTIVE_UEBERTRAGUNG:
                            return uebertragung;
                        case UploadDO.WARTESCHLANGE:
                            return warteschlange + " (" +formatter.format(new Date(upload.getLastConnection())) + ")";
                        default:
                            return "";
                    }
                case 2:
                    return upload.getNick();
                case 3: {
                    if (upload.getStatus() == UploadDO.AKTIVE_UEBERTRAGUNG) {
                        return getSpeedAsString(upload.getSpeed());
                    }
                    else {
                        return "";
                    }
                }
                case 4: {
                    return "";
                }
                case 5: {
                    return "";
                }
                case 6:
                    return new Integer(upload.getPrioritaet());
                case 7:
                    return upload.getVersion().getVersion();
                default:
                    return "Fehler";
            }
        }
        return null;
    }

    public int getRowCount() {
        if (uploads == null) {
            return 0;
        }
        return uploads.size();
    }

    public Class getColumnClass(int column) {
        return cTypes[column];
    }

    private String getSpeedAsString(long speed) {
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
        uebertragung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.uplstat1"));
        keineVerbindungMoeglich = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.uploads.uplstat8"));
        versucheIndirekteVerbindung = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.uploads.uplstat7"));
        versucheZuVerbinden = ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.mainform.uploads.uplstat6"));
        warteschlange = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.uploads.uplstat3"));
    }

    public Object getChild(Object parent, int index) {
        Object[] obj = getChildren(parent);
        if (obj != null && obj.length > index) {
            return obj[index];
        }
        else {
            return null;
        }
    }

    public int getChildCount(Object parent) {
        if (parent.getClass() == UploadMainNode.class) {
            return ((UploadMainNode)parent).getChildCount();
        }
        return 0;
    }

    private Object[] getChildren(Object parent) {
        if (parent.getClass() == UploadMainNode.class) {
            return ((UploadMainNode)parent).getChildren();
        }
        return null;
    }

    public void setTable(Map content) {
        if (uploads == null) {
            uploads = content;
            UploadMainNode.setUploads(content);
        }
    }
}
