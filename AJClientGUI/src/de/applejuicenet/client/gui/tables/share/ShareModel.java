package de.applejuicenet.client.gui.tables.share;

import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;
import de.applejuicenet.client.gui.tables.download.DownloadModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/share/Attic/ShareModel.java,v 1.8 2004/02/05 23:11:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ShareModel.java,v $
 * Revision 1.8  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.7  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.6  2003/12/29 09:39:21  maj0r
 * Alte BugIDs entfernt, da auf neuen Bugtracker auf bugs.applejuicenet.de umgestiegen wurde.
 *
 * Revision 1.5  2003/12/28 10:48:44  maj0r
 * Dateigroessen in der Sharetabelle werden nun korrekt ausgegeben (Danke an schnigger und TuxHomer).
 *
 * Revision 1.4  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.3  2003/09/01 15:50:52  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.2  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.1  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 *
 */

public class ShareModel
    extends AbstractTreeTableModel {

    static protected String[] cNames = {
        "Name", "Size", "Type"};

    static protected Class[] cTypes = {
        TreeTableModel.class,
        String.class, Integer.class};

    public ShareModel(ShareNode rootNode) {
        super(rootNode);
    }

    public int getChildCount(Object node) {
        Object[] children = getChildren(node);
        return (children == null) ? 0 : children.length;
    }

    public Object getChild(Object node, int i) {
        return getChildren(node)[i];
    }

    public boolean isLeaf(Object node) {
        return ( (ShareNode) node).isLeaf();
    }

    public int getColumnCount() {
        return cNames.length;
    }

    public ShareNode getRootNode() {
        return (ShareNode) getRoot();
    }

    public String getColumnName(int column) {
        return cNames[column];
    }

    public Class getColumnClass(int column) {
        return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        ShareNode shareNode = (ShareNode) node;

        try {
            switch (column) {
                case 0: {
                    if (shareNode.isLeaf() && shareNode != getRoot()) {
                        return shareNode.getDO().getFilename();
                    }
                    else {
                        return "";
                    }
                }
                case 1: {
                    if (shareNode.isLeaf() && shareNode != getRoot()) {
                        return DownloadModel.parseGroesse(shareNode.getDO().
                            getSize());
                    }
                    else {
                        return "";
                    }
                }
                case 2: {
                    if (shareNode.isLeaf() && shareNode != getRoot()) {
                        return new Integer(shareNode.getDO().getPrioritaet());
                    }
                    else {
                        return null;
                    }
                }
                default:
                    return null;
            }
        }
        catch (SecurityException se) {
        }
        return null;
    }

    protected Object[] getChildren(Object node) {
        return ( (ShareNode) node).getChildren();
    }
}