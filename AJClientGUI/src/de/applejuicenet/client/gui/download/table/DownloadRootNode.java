package de.applejuicenet.client.gui.download.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.util.DownloadCalculator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadRootNode.java,v 1.7 2005/01/19 11:03:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DownloadRootNode
    implements Node, DownloadNode {
    public static int SORT_NO_SORT = -1;
    public static int SORT_DOWNLOADNAME = 0;
    public static int SORT_GROESSE = 1;
    public static int SORT_BEREITS_GELADEN = 2;
    public static int SORT_RESTZEIT = 3;
    public static int SORT_PROZENT = 4;
    public static int SORT_PWDL = 5;
    public static int SORT_REST_ZU_LADEN = 6;
    public static int SORT_GESCHWINDIGKEIT = 7;
    public static int SORT_STATUS = 8;

    private Map downloads;

    private static boolean initialized = false;

    private Map childrenPath = new HashMap();
    private List children = new ArrayList();

    private int sort = SORT_DOWNLOADNAME;
    private boolean isAscent = true;

    private Object[] sortedChildNodes;
    private Map targetDirs = new HashMap();

    public static boolean isInitialized() {
        return initialized;
    }
    
    public Object[] getChildren() {
        if (!initialized) {
            return new Object[] {
                new WaitNode()};
        }
        if (downloads == null) {
            return null;
        }
        else{
        	return sortedChildNodes;
        }
    }

    public Object[] getChildrenWithSort() {
        if (downloads == null) {
            return null;
        }
        Download download;
        String key;
        DownloadDirectoryNode newNode;
        boolean sort = false;
        synchronized (this) {
            int childCount = children.size(); //alte Downloads entfernen
            Object obj;
            for (int i = childCount - 1; i >= 0; i--) {
                obj = children.get(i);
                if (obj.getClass() == DownloadMainNode.class) {
                    download = ( (DownloadMainNode) obj).getDownload();
                    key = Integer.toString(download.getId());
                    if (!downloads.containsKey(key) ||
                        download.getTargetDirectory().length() > 0) {
                        children.remove(i);
                        sort = true;
                    }
                }
                else if (obj.getClass() == DownloadDirectoryNode.class) {
                    if ( ( (DownloadDirectoryNode) obj).getChildCount(false) == 0) {
                        children.remove(i);
                        sort = true;
                    }
                }
            }
            Iterator it = downloads.values().iterator();
            String pfad;
            PathEntry pathEntry;
            String mapKey;
            while (it.hasNext()) {
                download = (Download) it.next();
                mapKey = Integer.toString(download.getId());
                pfad = download.getTargetDirectory();
                pathEntry = (PathEntry) childrenPath.get(mapKey);
                if (pathEntry != null) {
                    if (pathEntry.getPfad().compareToIgnoreCase(pfad) != 0) { //geaenderter Download
                        childrenPath.remove(mapKey);
                    }
                    else {
                        continue;
                    }
                }
                if (pathEntry == null) { //neuer Download
                    sort = true;
                    if (pfad == null || pfad.length() == 0) {
                        childrenPath.put(mapKey, new PathEntry("", download));
                        children.add(new DownloadMainNode(download));
                    }
                    else {
                        key = download.getTargetDirectory();
                        if (!targetDirs.containsKey(key)) {
                            newNode = new DownloadDirectoryNode(download.
                                getTargetDirectory());
                            childrenPath.put(mapKey,
                                             new PathEntry(download.
                                getTargetDirectory(),
                                newNode));
                            targetDirs.put(key, newNode);
                            children.add(newNode);
                        }
                    }
                }
            }
        }
        if (sort || sortedChildNodes == null) {
            return sort( (Object[]) children.toArray(new Object[children.size()]));
        }
        else {
            return sortedChildNodes;
        }
    }

    public void setSortCriteria(int sortCriteria, boolean isAscent) {
        sort = sortCriteria;
        this.isAscent = isAscent;
        DownloadDirectoryNode.setSortCriteria(sortCriteria, isAscent);
        if (sortedChildNodes != null) {
            sort(sortedChildNodes);
        }
    }

    private Object[] sort(Object[] childNodes) {
        if (sort == SORT_NO_SORT) {
            sortedChildNodes = childNodes;
            return childNodes;
        }
        else {
            int n = childNodes.length;
            Object tmp;
            for (int i = 0; i < n - 1; i++) {
                int k = i;
                for (int j = i + 1; j < n; j++) {
                    if (isAscent) {
                        if (compare(childNodes, j, k, isAscent) < 0) {
                            k = j;
                        }
                    }
                    else {
                        if (compare(childNodes, j, k, isAscent) > 0) {
                            k = j;
                        }
                    }
                }
                tmp = childNodes[i];
                childNodes[i] = childNodes[k];
                childNodes[k] = tmp;
            }
            sortedChildNodes = childNodes;
            return sortedChildNodes;
        }
    }

    private int compare(Object[] childNodes, int row1, int row2,
                        boolean isAscent) {
        Object o1 = childNodes[row1];
        Object o2 = childNodes[row2];
        if (o1.getClass() == DownloadDirectoryNode.class
            && o2.getClass() == DownloadMainNode.class) {
            if (isAscent) {
                return -1;
            }
            else {
                return 1;
            }
        }
        else if (o1.getClass() == DownloadMainNode.class
                 && o2.getClass() == DownloadDirectoryNode.class) {
            if (isAscent) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else if (o1.getClass() == DownloadDirectoryNode.class
                 && o2.getClass() == DownloadDirectoryNode.class) {
            return ( (DownloadDirectoryNode) o1).getVerzeichnis().
                compareToIgnoreCase( ( (DownloadDirectoryNode) o2).
                                    getVerzeichnis());
        }
        o1 = null;
        o2 = null;

        if (sort == SORT_DOWNLOADNAME) {
            o1 = ( (DownloadMainNode) childNodes[row1]).getDownload().
                getFilename();
            o2 = ( (DownloadMainNode) childNodes[row2]).getDownload().
                getFilename();
        }
        else if (sort == SORT_GROESSE) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getGroesse());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getGroesse());
        }
        else if (sort == SORT_BEREITS_GELADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getBereitsGeladen());
        }
        else if (sort == SORT_RESTZEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getRestZeit());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getRestZeit());
        }
        else if (sort == SORT_PROZENT) {
            o1 = new Double( ( (DownloadMainNode) childNodes[row1]).
                            getDownload().getProzentGeladen());
            o2 = new Double( ( (DownloadMainNode) childNodes[row2]).
                            getDownload().getProzentGeladen());
        }
        else if (sort == SORT_PWDL) {
            o1 = new Integer( ( (DownloadMainNode) childNodes[row1]).
                             getDownload().getPowerDownload());
            o2 = new Integer( ( (DownloadMainNode) childNodes[row2]).
                             getDownload().getPowerDownload());
        }
        else if (sort == SORT_REST_ZU_LADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getBereitsGeladen());
        }
        else if (sort == SORT_GESCHWINDIGKEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownload().
                          getSpeedInBytes());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownload().
                          getSpeedInBytes());
        }
        else if (sort == SORT_STATUS) {
            o1 = ( DownloadCalculator.getStatusAsString(((DownloadMainNode)
            		childNodes[row1]).getDownload()));
            o2 = ( DownloadCalculator.getStatusAsString(((DownloadMainNode)
            		childNodes[row2]).getDownload()));
        }

        if (o1 == null && o2 == null) {
            return 0;
        }
        else if (o1 == null) {
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        else {
            if (o1.getClass().getSuperclass() == Number.class) {
                return compare( (Number) o1, (Number) o2);
            }
            else if (o1.getClass() == Boolean.class) {
                return compare( (Boolean) o1, (Boolean) o2);
            }
            else {
                return ( (String) o1).compareToIgnoreCase( (String) o2);
            }
        }
    }

    public int compare(Number o1, Number o2) {
        double n1 = o1.doubleValue();
        double n2 = o2.doubleValue();
        if (n1 < n2) {
            return -1;
        }
        else if (n1 > n2) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public int compare(Boolean o1, Boolean o2) {
        boolean b1 = o1.booleanValue();
        boolean b2 = o2.booleanValue();
        if (b1 == b2) {
            return 0;
        }
        else if (b1) {
            return 1;
        }
        else {
            return -1;
        }
    }

    public void setDownloadMap(Map downloadMap) {
        if (downloads == null) {
            initialized = true;
            downloads = downloadMap;
        }
    }

    public int getChildCount(boolean sort) {
    	Object[] obj;
    	if ((sort || sortedChildNodes == null) && downloads != null){
    		obj = getChildrenWithSort();
    	}
    	else{
    		obj = getChildren();
    	}
        if (obj == null) {
            return 0;
        }
        return getChildren().length;
    }

    public boolean isLeaf() {
        return false;
    }

    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("tree");
    }

    class PathEntry {
        private String pfad;
        private Object obj;

        public PathEntry(String pfad, Object obj) {
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
