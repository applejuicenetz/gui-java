package de.applejuicenet.client.gui.search.table;

import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.Icon;

import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/search/table/Attic/SearchNode.java,v 1.6 2005/05/02 14:23:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class SearchNode
    implements Node {

    public static int ROOT_NODE = 0;
    public static int ENTRY_NODE = 1;

    private Object valueObject;
    private int type;
    private TreeSet<SearchNode> children;
    private ArrayList<String> keys = null;
    private SearchNodeComparator comparator; 

    public SearchNode(Search aSearch) {
        valueObject = aSearch;
        type = ROOT_NODE;
        comparator = new SearchNodeComparator();
        keys = new ArrayList<String>();
        refresh();
    }

    public SearchNode(SearchEntry aSearchEntry) {
        valueObject = aSearchEntry;
        type = ENTRY_NODE;
    }
    
    public void setSortCriteria(SearchNodeComparator.SORT_TYPE sortType, boolean ascending){
        comparator.setSortCriteria(sortType, ascending);
        updateFilter();
    }
    
    public void updateFilter(){
        children.clear();
        children = new TreeSet<SearchNode>(comparator);
        SearchEntry[] entries = ((Search) valueObject).getSearchEntries();
        for (int i = 0; i < entries.length; i++) {
            SearchNode aSearchNode = new SearchNode(entries[i]);
            children.add(aSearchNode);
        }
    }
    
    public Icon getConvenientIcon() {
        if (type != ENTRY_NODE) {
            return IconManager.getInstance().getIcon("treeRoot");
        }
        else{
            return IconManager.getInstance().getIcon(((SearchEntry)valueObject).getFileType());
        }
    }

    public String toString() {
        if (type == ROOT_NODE) {
            return "";
        }
        else if (type == ENTRY_NODE) {
            FileName[] filenames = ( (SearchEntry) valueObject).
                getFileNames();
            int haeufigkeit = 0;
            String dateiname = "";
            for (int i = 0; i < filenames.length; i++) {
                if (filenames[i].getHaeufigkeit() > haeufigkeit) {
                    haeufigkeit = filenames[i].getHaeufigkeit();
                    dateiname = filenames[i].getDateiName();
                }
            }
            return dateiname;
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
    
    public void refresh(){
        if (type != ROOT_NODE) {
            return;
        }
        if (children == null){
            children = new TreeSet<SearchNode>(comparator);
            SearchEntry[] entries = ( (Search) valueObject).
                getSearchEntries();
            for (int i = 0; i < entries.length; i++) {
                SearchNode aSearchNode = new SearchNode(entries[i]);
                children.add(aSearchNode);
            }
            ( (Search) valueObject).setChanged(false);
        }
        else {
            if ( ( (Search) valueObject).isChanged()) {
                SearchEntry[] entries = ( (Search) valueObject).
                    getSearchEntries();
                ( (Search) valueObject).setChanged(false);
                String key;
                for (int i = 0; i < entries.length; i++) {
                    key = Integer.toString(entries[i].getId());
                    if (!keys.contains(key)) {
                        keys.add(key);
                        SearchNode aSearchNode = new SearchNode(entries[i]);
                        children.add(aSearchNode);
                    }
                }
            }
        }
    }

    public Object[] getChildren() {
        if (type == ROOT_NODE) {
            if (children == null &&
                ( (Search) valueObject).getSearchEntries().length == 0) {
                WaitNode[] waitNode = new WaitNode[1];
                waitNode[0] = new WaitNode();
                return waitNode;
            }
            return (SearchNode[])children.toArray(new SearchNode[children.size()]);
        }
        else if (type == ENTRY_NODE) {
            return ( (SearchEntry) valueObject).getFileNames();
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
}
