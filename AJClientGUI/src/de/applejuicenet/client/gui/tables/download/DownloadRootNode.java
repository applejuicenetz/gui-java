package de.applejuicenet.client.gui.tables.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Icon;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DownloadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadRootNode.java,v 1.22 2004/02/12 16:42:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DownloadRootNode.java,v $
 * Revision 1.22  2004/02/12 16:42:42  maj0r
 * Bug #198 gefixt (Danke an froeschle567)
 * Sortierung nach Downloadstatus korrigiert.
 *
 * Revision 1.21  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.20  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.19  2004/01/12 14:37:26  maj0r
 * Bug #82 gefixt (Danke an hirsch.marcel)
 * Sortierung von Downloads innerhalb von Unterverzeichnissen der Downloadtabelle korrigiert.
 *
 * Revision 1.18  2004/01/12 07:27:44  maj0r
 * Sortierung an neue ausblendbare Header angepasst.
 *
 * Revision 1.17  2004/01/07 18:37:21  maj0r
 * Sortierung korrigiert.
 *
 * Revision 1.16  2003/12/30 20:52:19  maj0r
 * Umbenennen von Downloads und Aendern von Zielverzeichnissen vervollstaendigt.
 *
 * Revision 1.15  2003/12/30 09:01:59  maj0r
 * Bug #10 fixed (Danke an muhviestarr)
 * Wenn man keine Downloads hat, steht nun nicht mehr "bitte warten" in der Downloadtabelle.
 *
 * Revision 1.14  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.13  2003/12/29 15:38:59  maj0r
 * Wird nun immer als initialisiert markiert, wenn die Downloadmap gesetzt wird.
 *
 * Revision 1.12  2003/12/17 17:03:37  maj0r
 * In der Downloadtabelle nun ein Warteicon angezeigt, bis erstmalig Daten geholt wurden.
 *
 * Revision 1.11  2003/11/03 20:57:03  maj0r
 * Sortieren nach Status eingebaut.
 *
 * Revision 1.10  2003/10/21 11:36:32  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 * Revision 1.9  2003/10/16 09:57:21  maj0r
 * Sortierung korrigiert, Nichtbeachtung von Gross/Kleinschreibung.
 *
 * Revision 1.8  2003/10/12 16:34:59  maj0r
 * NullPointer behoben.
 *
 * Revision 1.7  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.6  2003/10/10 15:12:26  maj0r
 * Sortieren im Downloadbereich eingefuegt.
 *
 * Revision 1.5  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
 * Revision 1.4  2003/10/04 15:31:07  maj0r
 * Erste Version des Versteckens.
 *
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

    private HashMap downloads;

    private static boolean initialized = false;

    private HashMap childrenPath = new HashMap();
    private ArrayList children = new ArrayList();

    private int sort = SORT_NO_SORT;
    private boolean isAscent = true;

    private Object[] sortedChildNodes;
    private HashMap targetDirs = new HashMap();

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
        DownloadDO downloadDO;
        String key;
        DownloadDirectoryNode newNode;
        boolean sort = false;
        synchronized (this) {
            int childCount = children.size(); //alte Downloads entfernen
            Object obj;
            for (int i = childCount - 1; i >= 0; i--) {
                obj = children.get(i);
                if (obj.getClass() == DownloadMainNode.class) {
                    downloadDO = ( (DownloadMainNode) obj).getDownloadDO();
                    key = Integer.toString(downloadDO.getId());
                    if (!downloads.containsKey(key) ||
                        downloadDO.getTargetDirectory().length() > 0) {
                        children.remove(i);
                        sort = true;
                    }
                }
                else if (obj.getClass() == DownloadDirectoryNode.class) {
                    if ( ( (DownloadDirectoryNode) obj).getChildCount() == 0) {
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
                downloadDO = (DownloadDO) it.next();
                mapKey = Integer.toString(downloadDO.getId());
                pfad = downloadDO.getTargetDirectory();
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
                        childrenPath.put(mapKey, new PathEntry("", downloadDO));
                        children.add(new DownloadMainNode(downloadDO));
                    }
                    else {
                        key = downloadDO.getTargetDirectory();
                        if (!targetDirs.containsKey(key)) {
                            newNode = new DownloadDirectoryNode(downloadDO.
                                getTargetDirectory());
                            childrenPath.put(mapKey,
                                             new PathEntry(downloadDO.
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
            o1 = ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                getFilename();
            o2 = ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                getFilename();
        }
        else if (sort == SORT_GROESSE) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getGroesse());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getGroesse());
        }
        else if (sort == SORT_BEREITS_GELADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getBereitsGeladen());
        }
        else if (sort == SORT_RESTZEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getRestZeit());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getRestZeit());
        }
        else if (sort == SORT_PROZENT) {
            o1 = new Double( ( (DownloadMainNode) childNodes[row1]).
                            getDownloadDO().getProzentGeladen());
            o2 = new Double( ( (DownloadMainNode) childNodes[row2]).
                            getDownloadDO().getProzentGeladen());
        }
        else if (sort == SORT_PWDL) {
            o1 = new Integer( ( (DownloadMainNode) childNodes[row1]).
                             getDownloadDO().getPowerDownload());
            o2 = new Integer( ( (DownloadMainNode) childNodes[row2]).
                             getDownloadDO().getPowerDownload());
        }
        else if (sort == SORT_REST_ZU_LADEN) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getBereitsGeladen());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getGroesse() -
                          ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getBereitsGeladen());
        }
        else if (sort == SORT_GESCHWINDIGKEIT) {
            o1 = new Long( ( (DownloadMainNode) childNodes[row1]).getDownloadDO().
                          getSpeedInBytes());
            o2 = new Long( ( (DownloadMainNode) childNodes[row2]).getDownloadDO().
                          getSpeedInBytes());
        }
        else if (sort == SORT_STATUS) {
            o1 = ( (DownloadMainNode)
                  childNodes[row1]).getDownloadDO().getStatusAsString();
            o2 = ( (DownloadMainNode)
                  childNodes[row2]).getDownloadDO().getStatusAsString();
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

    public void setDownloadMap(HashMap downloadMap) {
        if (downloads == null) {
            initialized = true;
            downloads = downloadMap;
        }
    }

    public int getChildCount() {
        Object[] obj = getChildren();
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
