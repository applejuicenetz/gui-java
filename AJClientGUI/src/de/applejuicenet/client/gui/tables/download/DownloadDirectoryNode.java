package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadDO;

import javax.swing.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadDirectoryNode.java,v 1.2 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadDirectoryNode.java,v $
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public class DownloadDirectoryNode implements Node, DownloadNode {
    private static HashMap downloads;
    private String verzeichnis;

    public DownloadDirectoryNode(String targetDir){
        verzeichnis = targetDir;
    }

    public static void setDownloads(HashMap downloadsMap){
        if (downloads==null){
            downloads = downloadsMap;
        }
    }

    public Object[] getChildren(){
        if (downloads==null)
            return null;
        ArrayList children = new ArrayList();
        DownloadDO downloadDO;
        synchronized(this){
            Iterator it = downloads.values().iterator();
            while (it.hasNext()){
                downloadDO = (DownloadDO)it.next();
                if (downloadDO.getTargetDirectory().compareToIgnoreCase(verzeichnis)==0){
                    children.add(new DownloadMainNode(downloadDO));
                }
            }
        }
        return children.toArray(new Object[children.size()]);
    }

    public int getChildCount() {
        Object[] obj = getChildren();
        if (obj==null)
            return 0;
        return getChildren().length;
    }

    public String getVerzeichnis() {
        return verzeichnis;
    }

    public boolean isLeaf(){
        return false;
    }

    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("tree");
    }
}
