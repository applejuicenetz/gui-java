package de.applejuicenet.client.gui.tables.upload;

import java.util.*;

import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/upload/Attic/UploadDataTableModel.java,v 1.7 2003/09/01 15:50:52 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UploadDataTableModel.java,v $
 * Revision 1.7  2003/09/01 15:50:52  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.6  2003/08/30 19:44:32  maj0r
 * Auf JTreeTable umgebaut.
 *
 * Revision 1.5  2003/08/18 17:37:08  maj0r
 * UploadTabelle wesentlich vereinfacht.
 *
 * Revision 1.4  2003/08/18 14:51:52  maj0r
 * Alte Eintraege loeschen.
 *
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
        extends AbstractTreeTableModel implements LanguageListener {
    final static String[] COL_NAMES = {
        "Dateiname", "Status", "Wer", "Geschwindigkeit", "Prozent geladen", "Priorität", "Client"};

    private String uebertragung;
    private String keineVerbindungMoeglich;
    private String versucheIndirekteVerbindung;
    private String versucheZuVerbinden;
    private String warteschlange;

    HashMap uploads = null;

    public UploadDataTableModel() {
        super(new MainNode());
        LanguageSelector.getInstance().addLanguageListener(this);
    }

    public Object getRow(int row) {
        if ((uploads != null) && (row < uploads.size()))
        {
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
        if (node.getClass()==MainNode.class && ( ((MainNode)node).getType()==MainNode.LOADING_UPLOADS
            || ((MainNode)node).getType()==MainNode.WAITING_UPLOADS
            || ((MainNode)node).getType()==MainNode.REST_UPLOADS)) {
            if (column==0){
                return node.toString() + " (" + getChildCount(node) + ")";
            }
            else{
                return "";
            }
        }
        else if (node.getClass()==UploadDO.class){
            UploadDO upload = (UploadDO) node;
            switch (column)
            {
                case 0:
                    return upload.getDateiName();
                case 1:
                    switch (upload.getStatus())
                    {
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
                        if (upload.getStatus() == UploadDO.AKTIVE_UEBERTRAGUNG)
                        {
                            return getSpeedAsString(upload.getSpeed());
                        }
                        else
                        {
                            return "";
                        }
                    }
                case 4:
                    {
                        if (upload.getStatus() == UploadDO.AKTIVE_UEBERTRAGUNG)
                        {
                            return getSpeedAsString(upload.getSpeed());
                        }
                        else
                        {
                            return "";
                        }
                    }
                case 5:
                    return new Integer(upload.getPrioritaet());
                case 6:
                    return upload.getVersion().getVersion();
                default:
                    return "Fehler";
            }
        }
        return null;
    }

    public int getRowCount() {
        if (uploads == null)
        {
            return 0;
        }
        return uploads.size();
    }

    public Class getClass(int column) {
        if (column == 0)
            return TreeTableModel.class;
        else if (column == 5)
            return Integer.class;
        return String.class;
    }

    private String getSpeedAsString(long speed) {
        if (speed == 0)
            return "0 Bytes/s";
        double size = speed;
        int faktor = 1;
        if (size < 1024)
            faktor = 1;
        else
            faktor = 1024;

        size = size / faktor;
        String s = Double.toString(size);
        if (s.indexOf(".") + 3 < s.length())
        {
            s = s.substring(0, s.indexOf(".") + 3);
        }
        if (faktor == 1)
        {
            s += " Bytes/s";
        }
        else
        {
            s += " kb/s";
        }
        return s;
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        uebertragung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform", "uploads", "uplstat1"}));
        keineVerbindungMoeglich = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform", "uploads", "uplstat8"}));
        versucheIndirekteVerbindung = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform", "uploads", "uplstat7"}));
        versucheZuVerbinden = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform", "uploads", "uplstat6"}));
        warteschlange = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[]{"mainform", "uploads", "uplstat3"}));
    }

    public Object getChild(Object parent, int index) {
        Object[] obj = getChildren(parent);
        if (obj!=null && obj.length>index)
            return obj[index];
        else
            return null;
    }

    public int getChildCount(Object parent) {
        Object[] obj = getChildren(parent);
        if (obj!=null)
            return obj.length;
        else
            return 0;
    }

    private Object[] getChildren(Object parent){
        if (parent.getClass()==MainNode.class){
            MainNode mainNode = (MainNode)parent;
            if (mainNode.getType()==MainNode.ROOT_NODE)
                return mainNode.getChildren();
            else if (mainNode.getType()==MainNode.LOADING_UPLOADS){
                if (uploads==null)
                    return null;
                else{
                    ArrayList children = new ArrayList();
                    UploadDO[] uploadsForThread = (UploadDO[])uploads.values().toArray(new UploadDO[uploads.size()]);
                    for (int i=0; i<uploadsForThread.length; i++){
                        if (uploadsForThread[i].getStatus()==UploadDO.AKTIVE_UEBERTRAGUNG)
                            children.add(uploadsForThread[i]);
                    }
                    return (UploadDO[]) children.toArray(new UploadDO[children.size()]);
                }
            }
            else if (mainNode.getType()==MainNode.WAITING_UPLOADS){
                if (uploads==null)
                    return null;
                else{
                    ArrayList children = new ArrayList();
                    UploadDO[] uploadsForThread = (UploadDO[])uploads.values().toArray(new UploadDO[uploads.size()]);
                    for (int i=0; i<uploadsForThread.length; i++){
                        if (uploadsForThread[i].getStatus()==UploadDO.WARTESCHLANGE)
                            children.add(uploadsForThread[i]);
                    }
                    return (UploadDO[]) children.toArray(new UploadDO[children.size()]);
                }
            }
            else if (mainNode.getType()==MainNode.REST_UPLOADS){
                if (uploads==null)
                    return null;
                else{
                    ArrayList children = new ArrayList();
                    UploadDO[] uploadsForThread = (UploadDO[])uploads.values().toArray(new UploadDO[uploads.size()]);
                    for (int i=0; i<uploadsForThread.length; i++){
                        if (uploadsForThread[i].getStatus()!=UploadDO.AKTIVE_UEBERTRAGUNG &&
                                uploadsForThread[i].getStatus()!=UploadDO.WARTESCHLANGE)
                            children.add(uploadsForThread[i]);
                    }
                    return (UploadDO[]) children.toArray(new UploadDO[children.size()]);
                }
            }
        }
        return null;
    }

    public void setTable(HashMap content){
        if (uploads==null){
            uploads = content;
        }
    }
}