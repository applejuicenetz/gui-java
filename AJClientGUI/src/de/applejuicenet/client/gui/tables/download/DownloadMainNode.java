package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import java.util.ArrayList;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadMainNode.java,v 1.1 2003/09/02 16:06:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadMainNode.java,v $
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public class DownloadMainNode implements Node, DownloadNode, LanguageListener{
    public static final int ROOT_NODE = -1;
    public static final int LOADING_DOWNLOADS = 0;
    public static final int WAITING_DOWNLOADS = 1;
    public static final int REST_DOWNLOADS = 2;

    private int type;
    private String text;

    private DownloadMainNode[] children;
    private DownloadDO downloadDO;

    public DownloadMainNode(DownloadDO downloadDO){
        type = ROOT_NODE;
        text = downloadDO.getFilename();
        this.downloadDO = downloadDO;
        children = new DownloadMainNode[3];
        children[0] = new DownloadMainNode(downloadDO, LOADING_DOWNLOADS);
        children[1] = new DownloadMainNode(downloadDO, WAITING_DOWNLOADS);
        children[2] = new DownloadMainNode(downloadDO, REST_DOWNLOADS);
    }

    public DownloadMainNode(DownloadDO downloadDO, int type){
        super();
        this.type = type;
        this.downloadDO = downloadDO;
        if (type==LOADING_DOWNLOADS){
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
        else if (type==WAITING_DOWNLOADS){
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
        else if (type==REST_DOWNLOADS){
            LanguageSelector.getInstance().addLanguageListener(this);
            fireLanguageChanged();
        }
    }

    public Icon getConvenientIcon() {
        if (type==LOADING_DOWNLOADS){
            return IconManager.getInstance().getIcon("download");
        }
        else if (type==WAITING_DOWNLOADS){
            return IconManager.getInstance().getIcon("cool");
        }
        else if (type==REST_DOWNLOADS){
            return IconManager.getInstance().getIcon("eek");
        }
        else if (type==ROOT_NODE){
            return IconManager.getInstance().getIcon("treeRoot");
        }
        else
            return null;
    }

    public int getChildCount(){
        if (type==ROOT_NODE){
            return 3;
        }
        else {
            Object[] obj = getChildren();
            if (obj==null)
                return 0;
            else
                return obj.length;
        }
    }

    public boolean isLeaf() {
        return (getChildCount() == 0) ? true : false;
    }

    public Object[] getChildren() {
        switch (type){
            case ROOT_NODE:{
                return children;
            }
            case LOADING_DOWNLOADS:{
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i=0; i<downloadSourceDO.length; i++){
                    if (downloadSourceDO[i].getStatus()==DownloadSourceDO.UEBERTRAGUNG){
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[kinder.size()]);
            }
            case WAITING_DOWNLOADS:{
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i=0; i<downloadSourceDO.length; i++){
                    if (downloadSourceDO[i].getStatus()==DownloadSourceDO.IN_WARTESCHLANGE){
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[kinder.size()]);
            }
            case REST_DOWNLOADS:{
                ArrayList kinder = new ArrayList();
                DownloadSourceDO[] downloadSourceDO = downloadDO.getSources();
                for (int i=0; i<downloadSourceDO.length; i++){
                    if (downloadSourceDO[i].getStatus()!=DownloadSourceDO.UEBERTRAGUNG
                        && downloadSourceDO[i].getStatus()!=DownloadSourceDO.IN_WARTESCHLANGE){
                        kinder.add(downloadSourceDO[i]);
                    }
                }
                return (DownloadSourceDO[]) kinder.toArray(new DownloadSourceDO[kinder.size()]);
            }
            default:{
                return null;
            }
        }
    }

    public DownloadDO getDownloadDO() {
        return downloadDO;
    }

    public String toString() {
        if (type==ROOT_NODE)
            return text;
        else{
            return text + " (" + getChildCount() + ")";
        }
    }

    public int getType() {
        return type;
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        if (type==LOADING_DOWNLOADS){
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(
                    new String[] {"javagui", "downloadform", "ladendedownloads"}));
        }
        else if (type==WAITING_DOWNLOADS){
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(
                    new String[] {"javagui", "downloadform", "wartendedownloads"}));
        }
        else if (type==REST_DOWNLOADS){
            text = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(
                    new String[] {"javagui", "downloadform", "dreckigerrest"}));
        }
    }
}
