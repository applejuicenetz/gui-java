package de.applejuicenet.client.gui.tables.search;

import java.util.HashMap;

import javax.swing.Icon;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.Search.SearchEntry;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/search/Attic/SearchNode.java,v 1.12 2004/03/03 15:33:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SearchNode.java,v $
 * Revision 1.12  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.11  2004/02/28 15:01:42  maj0r
 * Suche um Filter erweitert
 * Die Filter in der Suchergebnistabelle wirken sich NICHT auf die Suche aus, lediglich die Treffer werden gefiltert.
 *
 * Revision 1.10  2004/02/27 16:48:27  maj0r
 * Suchergebnisse werden nun, wenn moeglich mit einem sprechenden Icon angezeigt.
 *
 * Revision 1.9  2004/02/12 17:04:01  maj0r
 * Bug #167 gefixt (Danke an arnoldfake)
 * Sortierung nach Anzahl in der Suchtabelle korrigiert.
 *
 * Revision 1.8  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.7  2004/01/30 16:32:47  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.6  2004/01/12 14:20:31  maj0r
 * Sortierung eingebaut.
 *
 * Revision 1.5  2004/01/08 07:47:11  maj0r
 * 98%-CPU-Last Bug durch Suche gefixt.
 *
 * Revision 1.4  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.3  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.2  2003/10/01 14:45:40  maj0r
 * Suche fortgesetzt.
 *
 * Revision 1.1  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 *
 */

public class SearchNode
    implements Node {
    public static int SORT_NO_SORT = -1;
    public static int SORT_FILENAME = 0;
    public static int SORT_GROESSE = 1;
    public static int SORT_ANZAHL = 2;

    private int sort = SORT_NO_SORT;
    private boolean isAscent = true;

    private Object valueObject;
    public static int ROOT_NODE = 0;
    public static int ENTRY_NODE = 1;
    private int type;
    private Map children;
    private Object[] sortedChildNodes;
    private boolean forceSort = false;

    public SearchNode(Search aSearch) {
        valueObject = aSearch;
        type = ROOT_NODE;
    }

    public SearchNode(Search.SearchEntry aSearchEntry) {
        valueObject = aSearchEntry;
        type = ENTRY_NODE;
    }

    public Icon getConvenientIcon() {
        if (type != ENTRY_NODE) {
            return IconManager.getInstance().getIcon("treeRoot");
        }
        else{
            return ( (Search.SearchEntry) valueObject).getTypeIcon();
        }
    }

    public String toString() {
        if (type == ROOT_NODE) {
            return "";
        }
        else if (type == ENTRY_NODE) {
            return ( (Search.SearchEntry) valueObject).getFileNames()[0].
                getDateiName();
        }
        else {
            return "";
        }
    }

    public int getChildCount() {
        Object o = getChildren();
        if (o == null) {
            return 0;
        }
        return getChildren().length;
    }

    public void forceSort(){
        forceSort = true;
    }

    public Object[] getChildren() {
        if (type == ROOT_NODE) {
            if (children == null &&
                ( (Search) valueObject).getSearchEntries().length == 0) {
                WaitNode[] waitNode = new WaitNode[1];
                waitNode[0] = new WaitNode();
                return waitNode;
            }
            boolean sort = false;
            if (forceSort || children == null){
                forceSort = false;
                if (children == null){
                    children = new HashMap();
                }
                else{
                    children.clear();
                    sortedChildNodes = null;
                }
                Search.SearchEntry[] entries = ( (Search) valueObject).
                    getSearchEntries();
                for (int i = 0; i < entries.length; i++) {
                    children.put(Integer.toString(entries[i].getId()),
                                 new SearchNode(entries[i]));
                }
                ( (Search) valueObject).setChanged(false);
            }
            else {
                if ( ( (Search) valueObject).isChanged()) {
                    Search.SearchEntry[] entries = ( (Search) valueObject).
                        getSearchEntries();
                    ( (Search) valueObject).setChanged(false);
                    String key;
                    for (int i = 0; i < entries.length; i++) {
                        key = Integer.toString(entries[i].getId());
                        if (!children.containsKey(key)) {
                            children.put(key, new SearchNode(entries[i]));
                            sort = true;
                        }
                    }
                }
            }
            if (sort || (sortedChildNodes == null)) {
                return sort( (Object[]) children.values().toArray(new
                    SearchNode[children.size()]));
            }
            else {
                return sortedChildNodes;
            }
        }
        else if (type == ENTRY_NODE) {
            return ( (Search.SearchEntry) valueObject).getFileNames();
        }
        else {
            return null;
        }
    }

    public int getNodeType() {
        return type;
    }

    public Object getValueObject() {
        return valueObject;
    }

    public void setSortCriteria(int sortCriteria, boolean isAscent) {
        sort = sortCriteria;
        this.isAscent = isAscent;
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
                        if (compare(childNodes, j, k) < 0) {
                            k = j;
                        }
                    }
                    else {
                        if (compare(childNodes, j, k) > 0) {
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

    private int compare(Object[] childNodes, int row1, int row2) {
        Object o1 = null;
        Object o2 = null;

        if (sort == SORT_FILENAME) {
            Search.SearchEntry entry1 = (Search.SearchEntry) ( (SearchNode)
                childNodes[row1]).getValueObject();
            Search.SearchEntry.FileName[] filenames = entry1.getFileNames();
            int haeufigkeit = 0;
            String dateiname = "";
            for (int i = 0; i < filenames.length; i++) {
                if (filenames[i].getHaeufigkeit() > haeufigkeit) {
                    haeufigkeit = filenames[i].getHaeufigkeit();
                    dateiname = filenames[i].getDateiName();
                }
            }
            o1 = dateiname;
            Search.SearchEntry entry2 = (Search.SearchEntry) ( (SearchNode)
                childNodes[row2]).getValueObject();
            filenames = entry2.getFileNames();
            haeufigkeit = 0;
            dateiname = "";
            for (int i = 0; i < filenames.length; i++) {
                if (filenames[i].getHaeufigkeit() > haeufigkeit) {
                    haeufigkeit = filenames[i].getHaeufigkeit();
                    dateiname = filenames[i].getDateiName();
                }
            }
            o2 = dateiname;
        }
        else if (sort == SORT_GROESSE) {
            Search.SearchEntry entry1 = (Search.SearchEntry) ( (SearchNode)
                childNodes[row1]).getValueObject();
            o1 = new Long(entry1.getGroesse());
            Search.SearchEntry entry2 = (Search.SearchEntry) ( (SearchNode)
                childNodes[row2]).getValueObject();
            o2 = new Long(entry2.getGroesse());
        }
        else if (sort == SORT_ANZAHL) {
            Search.SearchEntry entry1 = (Search.SearchEntry) ( (SearchNode)
                childNodes[row1]).getValueObject();
            Search.SearchEntry.FileName[] filenames = entry1.getFileNames();
            int haeufigkeit = 0;
            for (int i = 0; i < filenames.length; i++) {
                haeufigkeit += filenames[i].getHaeufigkeit();
            }
            o1 = new Integer(haeufigkeit);
            Search.SearchEntry entry2 = (Search.SearchEntry) ( (SearchNode)
                childNodes[row2]).getValueObject();
            filenames = entry2.getFileNames();
            haeufigkeit = 0;
            for (int i = 0; i < filenames.length; i++) {
                haeufigkeit += filenames[i].getHaeufigkeit();
            }
            o2 = new Integer(haeufigkeit);
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

}
