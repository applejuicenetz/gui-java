package de.applejuicenet.client.gui.tables.search;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.tables.upload.MainNode;
import de.applejuicenet.client.gui.tables.download.DownloadNode;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.MapSetStringKey;
import de.applejuicenet.client.shared.dac.DirectoryDO;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/search/Attic/SearchNode.java,v 1.1 2003/10/01 07:25:44 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: SearchNode.java,v $
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
        return IconManager.getInstance().getIcon("upload");
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
        if (type==ROOT_NODE){
            return ((Search)valueObject).getSearchEntries().length;
        }
        else if (type==ENTRY_NODE){
            return ((Search.SearchEntry)valueObject).getFileNames().length;
        }
        else return 0;
    }

    public Object[] getChildren() {
        if (type==ROOT_NODE){
            if (children==null){
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
            SearchNode[] result =(SearchNode[]) children.values().toArray(new SearchNode[children.size()]);
            System.out.println("test");
            return result;
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
