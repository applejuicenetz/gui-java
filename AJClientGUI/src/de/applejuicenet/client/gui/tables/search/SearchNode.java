package de.applejuicenet.client.gui.tables.search;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.MapSetStringKey;

import javax.swing.*;
import java.util.HashMap;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/search/Attic/SearchNode.java,v 1.4 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SearchNode.java,v $
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

public class SearchNode implements Node{

    private Object valueObject;
    public static int ROOT_NODE = 0;
    public static int ENTRY_NODE = 1;
    private int type;
    private HashMap children;

    public SearchNode(Search aSearch){
        valueObject = aSearch;
        type = ROOT_NODE;
    }

    public SearchNode(Search.SearchEntry aSearchEntry){
        valueObject = aSearchEntry;
        type = ENTRY_NODE;
    }

    public Icon getConvenientIcon() {
        return IconManager.getInstance().getIcon("treeRoot");
    }

    public String toString() {
        if (type==ROOT_NODE)
            return "";
        else if (type==ENTRY_NODE){
            return ((Search.SearchEntry)valueObject).getFileNames()[0].getDateiName();
        }
        else return "";
    }

    public int getChildCount(){
        Object o = getChildren();
        if (o==null){
            return 0;
        }
        return getChildren().length;
    }

    public Object[] getChildren() {
        if (type==ROOT_NODE){
            if (children==null && ((Search)valueObject).getSearchEntries().length==0){
                WaitNode[] waitNode = new WaitNode[1];
                waitNode[0] = new WaitNode();
                return waitNode;
            }
            if (children==null){
                children = new HashMap();
                Search.SearchEntry[] entries = ((Search)valueObject).getSearchEntries();
                for (int i=0; i<entries.length; i++){
                    children.put(new MapSetStringKey(entries[i].getId()), new SearchNode(entries[i]));
                }
            }
            else{
                Search.SearchEntry[] entries = ((Search)valueObject).getSearchEntries();
                MapSetStringKey key;
                for (int i=0; i<entries.length; i++){
                    key = new MapSetStringKey(entries[i].getId());
                    if (!children.containsKey(key)){
                        children.put(key, new SearchNode(entries[i]));
                    }
                }
            }
            return (SearchNode[]) children.values().toArray(new SearchNode[children.size()]);
        }
        else if (type==ENTRY_NODE){
            return ((Search.SearchEntry)valueObject).getFileNames();
        }
        else return null;
    }

    public int getNodeType() {
        return type;
    }

    public Object getValueObject() {
        return valueObject;
    }
}
