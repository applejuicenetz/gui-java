package de.applejuicenet.client.gui.tables.download;

import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.tables.Node;

import javax.swing.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadRootNode.java,v 1.3 2003/10/02 15:01:00 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadRootNode.java,v $
 * Revision 1.3  2003/10/02 15:01:00  maj0r
 * Erste Version den Versteckens eingebaut.
 *
 * Revision 1.2  2003/09/02 19:29:26  maj0r
 * Einige Stellen synchronisiert und Nullpointer behoben.
 * Version 0.21 beta.
 *
 * Revision 1.1  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 *
 */

public class DownloadRootNode implements Node, DownloadNode {
    private HashMap downloads;

    private HashMap childrenPath = new HashMap();
    private ArrayList children = new ArrayList();
    private HashMap versteckteNodes = new HashMap();
    private boolean versteckt = false;

    public void verstecke(DownloadMainNode downloadMainNode, boolean hide){
        if (downloadMainNode.getType()==DownloadMainNode.ROOT_NODE){
            MapSetStringKey key = new MapSetStringKey(downloadMainNode.getDownloadDO().getId());
            if (hide){
                versteckteNodes.put(key, downloadMainNode);
            }
            else{
                Object node = versteckteNodes.get(key);
                versteckteNodes.remove(key);
                children.add(node);
            }
        }
    }

    public void enableVerstecke(boolean verstecke){
        versteckt = verstecke;
        if (!versteckt){
            Iterator it = versteckteNodes.values().iterator();
            while (it.hasNext()){
                children.add(it.next());
            }
        }
    }

    public boolean isVerstecktEnabled(){
        return versteckt;
    }

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
            for (int i=childCount-1; i>=0; i--){
                obj = children.get(i);
                if (obj.getClass()==DownloadMainNode.class){
                    key = new MapSetStringKey(((DownloadMainNode)obj).getDownloadDO().getId());
                    if (!downloads.containsKey(key) || (versteckt && versteckteNodes.containsKey(key))){
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
                mapKey = new MapSetStringKey(downloadDO.getId());
                if (versteckt && versteckteNodes.containsKey(mapKey)){
                    continue;
                }
                pfad = downloadDO.getTargetDirectory();
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
