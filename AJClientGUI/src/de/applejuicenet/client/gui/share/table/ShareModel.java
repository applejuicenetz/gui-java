package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.gui.tables.AbstractTreeTableModel;
import de.applejuicenet.client.gui.tables.TreeTableModel;
import de.applejuicenet.client.gui.tables.download.DownloadModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/Attic/ShareModel.java,v 1.1 2004/10/15 13:39:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
            ;
        }
        return null;
    }

    protected Object[] getChildren(Object node) {
        return ( (ShareNode) node).getChildren();
    }
}