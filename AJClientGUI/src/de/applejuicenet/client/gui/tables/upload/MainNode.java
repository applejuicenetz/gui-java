package de.applejuicenet.client.gui.tables.upload;

import javax.swing.Icon;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import java.util.HashMap;
import java.util.ArrayList;
import de.applejuicenet.client.shared.dac.UploadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/upload/Attic/MainNode.java,v 1.6 2004/02/21 18:20:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: MainNode.java,v $
 * Revision 1.6  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.5  2004/02/09 14:21:32  maj0r
 * Icons für Upload-DirectStates eingebaut.
 *
 * Revision 1.4  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.1  2003/08/30 19:44:32  maj0r
 * Auf JTreeTable umgebaut.
 *
 * Revision 1.1  2003/08/24 19:37:25  maj0r
 * no message
 *
 * Revision 1.1  2003/08/22 11:34:15  maj0r
 * WarteNode eingefuegt.
 *
 *
 */

public class MainNode
    implements Node, LanguageListener {
    public static final int ROOT_NODE = -1;
    public static final int LOADING_UPLOADS = 0;
    public static final int WAITING_UPLOADS = 1;
    public static final int REST_UPLOADS = 2;
    private static HashMap uploads = null;

    private String text;

    private int type;

    private MainNode[] children;

    public MainNode() {
        type = ROOT_NODE;
        children = new MainNode[3];
        children[0] = new MainNode(LOADING_UPLOADS);
        children[1] = new MainNode(WAITING_UPLOADS);
        children[2] = new MainNode(REST_UPLOADS);
    }

    public MainNode(int type) {
        super();
        this.type = type;
        if (type == LOADING_UPLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        else if (type == WAITING_UPLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
        }
        else if (type == REST_UPLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
        }
    }

    public static void setUploads(HashMap uploadMap){
        uploads = uploadMap;
    }

    public Icon getConvenientIcon() {
        if (type == LOADING_UPLOADS) {
            return IconManager.getInstance().getIcon("upload");
        }
        else if (type == WAITING_UPLOADS) {
            return IconManager.getInstance().getIcon("cool");
        }
        else if (type == REST_UPLOADS) {
            return IconManager.getInstance().getIcon("eek");
        }
        else {
            return null;
        }
    }

    public String toString() {
        return text;
    }

    public int getType() {
        return type;
    }

    public int getChildCount() {
        Object[] object = getChildren();
        if (object == null) {
            return 0;
        }
        else {
            return object.length;
        }
    }

    public Object[] getChildren() {
        if (type == ROOT_NODE) {
            return children;
        }
        else{
            if (getType() == MainNode.LOADING_UPLOADS) {
                if (uploads == null) {
                    return null;
                }
                else {
                    ArrayList children = new ArrayList();
                    UploadDO[] uploadsForThread = (UploadDO[]) uploads.values().
                        toArray(new UploadDO[uploads.size()]);
                    for (int i = 0; i < uploadsForThread.length; i++) {
                        if (uploadsForThread[i].getStatus() ==
                            UploadDO.AKTIVE_UEBERTRAGUNG) {
                            children.add(uploadsForThread[i]);
                        }
                    }
                    return (UploadDO[]) children.toArray(new UploadDO[children.
                        size()]);
                }
            }
            else if (getType() == MainNode.WAITING_UPLOADS) {
                if (uploads == null) {
                    return null;
                }
                else {
                    ArrayList children = new ArrayList();
                    UploadDO[] uploadsForThread = (UploadDO[]) uploads.values().
                        toArray(new UploadDO[uploads.size()]);
                    for (int i = 0; i < uploadsForThread.length; i++) {
                        if (uploadsForThread[i].getStatus() ==
                            UploadDO.WARTESCHLANGE) {
                            children.add(uploadsForThread[i]);
                        }
                    }
                    return (UploadDO[]) children.toArray(new UploadDO[children.
                        size()]);
                }
            }
            else if (getType() == MainNode.REST_UPLOADS) {
                if (uploads == null) {
                    return null;
                }
                else {
                    ArrayList children = new ArrayList();
                    UploadDO[] uploadsForThread = (UploadDO[]) uploads.values().
                        toArray(new UploadDO[uploads.size()]);
                    for (int i = 0; i < uploadsForThread.length; i++) {
                        if (uploadsForThread[i].getStatus() !=
                            UploadDO.AKTIVE_UEBERTRAGUNG &&
                            uploadsForThread[i].getStatus() !=
                            UploadDO.WARTESCHLANGE) {
                            children.add(uploadsForThread[i]);
                        }
                    }
                    return (UploadDO[]) children.toArray(new UploadDO[children.
                        size()]);
                }
            }
            else{
                return null;
            }
        }
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        if (type == LOADING_UPLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.uploadform.ladendeuploads"));
        }
        else if (type == WAITING_UPLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.uploadform.wartendeuploads"));
        }
        else if (type == REST_UPLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.uploadform.dreckigerrest"));
        }
    }
}
