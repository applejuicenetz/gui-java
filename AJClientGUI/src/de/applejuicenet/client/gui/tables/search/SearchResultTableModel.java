package de.applejuicenet.client.gui.tables.search;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;
import de.applejuicenet.client.gui.trees.WaitNode;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.Search.SearchEntry;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/search/Attic/SearchResultTableModel.java,v 1.9 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SearchResultTableModel.java,v $
 * Revision 1.9  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.8  2004/01/08 07:47:11  maj0r
 * 98%-CPU-Last Bug durch Suche gefixt.
 *
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.5  2003/10/01 16:52:53  maj0r
 * Suche weiter gefuehrt.
 * Version 0.32
 *
 * Revision 1.4  2003/10/01 14:45:40  maj0r
 * Suche fortgesetzt.
 *
 * Revision 1.3  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 * Revision 1.2  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class SearchResultTableModel
    extends AbstractTreeTableModel {

    private Logger logger;

    final static String[] COL_NAMES = {
        "Dateiname", "Größe", "Anzahl"};

    static protected Class[] cTypes = {
        TreeTableModel.class, String.class, String.class};

    public SearchResultTableModel(Search aSearch) {
        super(new SearchNode(aSearch));
        logger = Logger.getLogger(getClass());
    }

    protected Object[] getChildren(Object node) {
        try {
            if (node.getClass() == SearchNode.class) {
                return ( (SearchNode) node).getChildren();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        return null;
    }

    public int getChildCount(Object node) {
        try {
            if (node.getClass() == SearchNode.class) {
                return ( (SearchNode) node).getChildCount();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        return 0;
    }

    public Object getChild(Object node, int i) {
        try {
            Object[] obj = getChildren(node);
            if (obj == null || i > obj.length - 1) {
                return null;
            }
            return obj[i];
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return null;
        }
    }

    public int getColumnCount() {
        return COL_NAMES.length;
    }

    public String getColumnName(int index) {
        return COL_NAMES[index];
    }

    public Class getColumnClass(int column) {
        return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        try {
            if (node.getClass() == WaitNode.class) {
                return "";
            }
            else if (node.getClass() == SearchNode.class) {
                Object o = ( (SearchNode) node).getValueObject();
                if ( ( (SearchNode) node).getNodeType() == SearchNode.ROOT_NODE) {
                    return "";
                }
                else {
                    Search.SearchEntry entry = (Search.SearchEntry) o;
                    switch (column) {
                        case 0: {
                            Search.SearchEntry.FileName[] filenames = entry.
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
                        case 1:
                            return entry.getGroesseAsString();
                        case 2: {
                            Search.SearchEntry.FileName[] filenames = entry.
                                getFileNames();
                            int haeufigkeit = 0;
                            for (int i = 0; i < filenames.length; i++) {
                                haeufigkeit += filenames[i].getHaeufigkeit();
                            }
                            return Integer.toString(haeufigkeit);
                        }
                        default:
                            return "";
                    }
                }
            }
            else if (node.getClass() == Search.SearchEntry.FileName.class) {
                Search.SearchEntry.FileName filename = (Search.SearchEntry.
                    FileName) node;
                switch (column) {
                    case 0:
                        return filename.getDateiName();
                    case 1:
                        return "";
                    case 2:
                        return Integer.toString(filename.getHaeufigkeit());
                    default:
                        return "";
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        return null;
    }
}