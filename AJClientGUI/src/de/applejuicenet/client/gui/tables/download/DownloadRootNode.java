package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.tables.Node;

import javax.swing.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadRootNode.java,v 1.1 2003/09/02 16:06:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadRootNode.java,v $
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public class DownloadRootNode implements Node, DownloadNode {
    private HashMap downloads;

    HashMap childrenPath = new HashMap();
    ArrayList children = new ArrayList();

    public Object[] getChildren(){
        if (downloads==null)
            return null;
        DownloadDO downloadDO;
        HashMap targetDirs = new HashMap();
        MapSetStringKey key;
        DownloadDirectoryNode newNode;
        synchronized(this){
            int childCount = children.size();   //alte Downloads entfernen
            Object obj;
            for (int i=0; i<childCount; i++){
                obj = children.get(i);
                if (obj.getClass()==DownloadMainNode.class){
                    if (!downloads.containsKey(new MapSetStringKey(((DownloadMainNode)obj).getDownloadDO().getId()))){
                        children.remove(i);
                    }
                }
            }
            Iterator it = downloads.values().iterator();
            String pfad;
            PathEntry pathEntry;
            MapSetStringKey mapKey;
            while (it.hasNext()){
                downloadDO = (DownloadDO)it.next();
                pfad = downloadDO.getTargetDirectory();
                mapKey = new MapSetStringKey(downloadDO.getId());
                pathEntry = (PathEntry)childrenPath.get(mapKey);
                if (pathEntry!=null){
                    if (pathEntry.getPfad().compareToIgnoreCase(pfad)!=0){ //geaenderter Download
                        children.remove(pathEntry.getObj());
                        childrenPath.remove(mapKey);
                    }
                    else{
                        continue;
                    }
                }
                if (pathEntry==null ){ //neuer Download
                    if (pfad==null || pfad.length()==0){
                        childrenPath.put(mapKey, new PathEntry("", downloadDO));
                        children.add(new DownloadMainNode(downloadDO));
                    }
                    else{
                        key = new MapSetStringKey(downloadDO.getTargetDirectory());
                        if (!targetDirs.containsKey(key)){
                            childrenPath.put(mapKey, new PathEntry(downloadDO.getTargetDirectory(), downloadDO));
                            newNode = new DownloadDirectoryNode(downloadDO.getTargetDirectory());
                            targetDirs.put(key, newNode);
                            children.add(newNode);
                        }
                    }
                }
            }
        }
        return children.toArray(new Object[children.size()]);
    }

    public void setDownloadMap(HashMap downloadMap){
        if (downloads==null){
            downloads = downloadMap;
        }
    }

    public int getChildCount(){
        Object[] obj = getChildren();
        if (obj==null)
            return 0;
        return getChildren().length;
    }

    public boolean isLeaf(){
        return false;
    }

    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("tree");
    }

    class PathEntry{
        private String pfad;
        private Object obj;

        public PathEntry(String pfad, Object obj){
            this.pfad = pfad;
            this.obj = obj;
        }

        public String getPfad() {
            return pfad;
        }

        public Object getObj() {
            return obj;
        }
    }
}
