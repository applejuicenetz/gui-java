package de.applejuicenet.client.gui.upload.table;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.Icon;

import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.UploadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/upload/table/Attic/MainNode.java,v 1.2 2004/10/29 11:58:43 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class MainNode
    implements Node, LanguageListener {
    public static final int ROOT_NODE = -1;
    public static final int LOADING_UPLOADS = 0;
    public static final int WAITING_UPLOADS = 1;
    public static final int REST_UPLOADS = 2;
    private static Map uploads = null;

    private String text;

    private int type;

    private MainNode[] children;

    public MainNode() {
        type = ROOT_NODE;
        children = new MainNode[2];
        children[0] = new MainNode(LOADING_UPLOADS);
        children[1] = new MainNode(WAITING_UPLOADS);
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
    }

    public static void setUploads(Map uploadMap){
        uploads = uploadMap;
    }

    public Icon getConvenientIcon() {
        if (type == LOADING_UPLOADS) {
            return IconManager.getInstance().getIcon("upload");
        }
        else if (type == WAITING_UPLOADS) {
            return IconManager.getInstance().getIcon("cool");
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

    public UploadDO[] getChildrenByStatus(int statusToCheck){
        if (uploads == null) {
            return null;
        }
        else {
            ArrayList children = new ArrayList();
            UploadDO[] uploadsForThread = (UploadDO[]) uploads.values().
                toArray(new UploadDO[uploads.size()]);
            for (int i = 0; i < uploadsForThread.length; i++) {
                if (uploadsForThread[i].getStatus() ==
                    statusToCheck) {
                    children.add(uploadsForThread[i]);
                }
            }
            return (UploadDO[]) children.toArray(new UploadDO[children.
                size()]);
        }
    }

    public Object[] getChildren() {
        if (type == ROOT_NODE) {
            return children;
        }
        else{
            if (getType() == MainNode.LOADING_UPLOADS) {
                return getChildrenByStatus(UploadDO.AKTIVE_UEBERTRAGUNG);
            }
            else if (getType() == MainNode.WAITING_UPLOADS) {
                return getChildrenByStatus(UploadDO.WARTESCHLANGE);
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
    }
}
