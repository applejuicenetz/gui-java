package de.applejuicenet.client.gui.tables.download;

import java.util.ArrayList;

import javax.swing.Icon;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadMainNode.java,v 1.6 2004/02/21 18:20:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadMainNode.java,v $
 * Revision 1.6  2004/02/21 18:20:30  maj0r
 * LanguageSelector auf SAX umgebaut.
 *
 * Revision 1.5  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.4  2003/12/30 20:15:55  maj0r
 * Kleine Anpassung, damit das Umbenennen von Downloads funktioniert.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/10/16 12:06:37  maj0r
 * Diverse Schoenheitskorrekturen und Optimierungen.
 *
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public class DownloadMainNode
    implements Node, DownloadNode, LanguageListener {
    public static final int ROOT_NODE = -1;
    public static final int LOADING_DOWNLOADS = 0;
    public static final int WAITING_DOWNLOADS = 1;
    public static final int REST_DOWNLOADS = 2;

    private int type;
    private String text = "";

    private DownloadMainNode[] children;
    private DownloadDO downloadDO;

    private static Icon waitingIcon = IconManager.getInstance().getIcon("cool");
    private static Icon loadingIcon = IconManager.getInstance().getIcon(
        "download");
    private static Icon restIcon = IconManager.getInstance().getIcon("eek");
    private static Icon rootIcon = IconManager.getInstance().getIcon("treeRoot");

    public DownloadMainNode(DownloadDO downloadDO) {
        type = ROOT_NODE;
        this.downloadDO = downloadDO;
        children = new DownloadMainNode[3];
        children[0] = new DownloadMainNode(downloadDO, LOADING_DOWNLOADS);
        children[1] = new DownloadMainNode(downloadDO, WAITING_DOWNLOADS);
        children[2] = new DownloadMainNode(downloadDO, REST_DOWNLOADS);
    }

    public DownloadMainNode(DownloadDO downloadDO, int type) {
        super();
        this.type = type;
        this.downloadDO = downloadDO;
        if (type == LOADING_DOWNLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
        else if (type == WAITING_DOWNLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
        else if (type == REST_DOWNLOADS) {
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
    }

    public Icon getConvenientIcon() {
        if (type == LOADING_DOWNLOADS) {
            return loadingIcon;
        }
        else if (type == WAITING_DOWNLOADS) {
            return waitingIcon;
        }
        else if (type == REST_DOWNLOADS) {
            return restIcon;
        }
        else if (type == ROOT_NODE) {
            return rootIcon;
        }
        else {
            return null;
        }
    }

    public int getChildCount() {
        Object[] obj = getChildren();
        if (obj == null) {
            return 0;
        }
        else {
            return obj.length;
        }
    }

    public boolean isLeaf() {
        return (getChildCount() == 0) ? true : false;
    }

    public Object[] getChildren() {
        switch (type) {
            case ROOT_NODE: {
                if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
                    return children;
                }
                else {
                    boolean childFound = false;
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].getChildCount() > 0) {
                            childFound = true;
                            break;
                        }
                    }
                    if (childFound) {
                        return children;
                    }
                    else {
                        return null;
                    }
                }
            }
            case LOADING_DOWNLOADS: {
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i = 0; i < downloadSourceDO.length; i++) {
                    if (downloadSourceDO[i].getStatus() ==
                        DownloadSourceDO.UEBERTRAGUNG) {
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[
                    kinder.size()]);
            }
            case WAITING_DOWNLOADS: {
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i = 0; i < downloadSourceDO.length; i++) {
                    if (downloadSourceDO[i].getStatus() ==
                        DownloadSourceDO.IN_WARTESCHLANGE) {
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[
                    kinder.size()]);
            }
            case REST_DOWNLOADS: {
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i = 0; i < downloadSourceDO.length; i++) {
                    if (downloadSourceDO[i].getStatus() !=
                        DownloadSourceDO.UEBERTRAGUNG
                        &&
                        downloadSourceDO[i].getStatus() != DownloadSourceDO.IN_WARTESCHLANGE) {
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[
                    kinder.size()]);
            }
            default: {
                return null;
            }
        }
    }

    public DownloadDO getDownloadDO() {
        return downloadDO;
    }

    public String toString() {
        if (type == ROOT_NODE) {
            return downloadDO.getFilename();
        }
        else {
            return text + " (" + getChildCount() + ")";
        }
    }

    public int getType() {
        return type;
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        if (type == LOADING_DOWNLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.ladendedownloads"));
        }
        else if (type == WAITING_DOWNLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.wartendedownloads"));
        }
        else if (type == REST_DOWNLOADS) {
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.javagui.downloadform.dreckigerrest"));
        }
    }
}
