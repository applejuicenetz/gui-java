package de.applejuicenet.client.gui.trees.chooser;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.trees.ApplejuiceNode;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.dac.DirectoryDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/chooser/Attic/DirectoryChooserNode.java,v 1.4 2004/02/09 20:12:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryChooserNode.java,v $
 * Revision 1.4  2004/02/09 20:12:28  maj0r
 * Sortierung verbessert bzw. eingebaut.
 *
 * Revision 1.3  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/08/24 19:37:25  maj0r
 * no message
 *
 *
 */

public class DirectoryChooserNode
    extends DefaultMutableTreeNode
    implements Node, ApplejuiceNode {
    private DirectoryDO directoryDO;
    private ArrayList children = null;
    private DirectoryChooserNode parent;

    public DirectoryChooserNode(DirectoryChooserNode parent,
                                DirectoryDO directoryDO) {
        this.parent = parent;
        this.directoryDO = directoryDO;
    }

    public DirectoryChooserNode() {
        this.parent = null;
        this.directoryDO = null;
        children = new ArrayList();
        ApplejuiceFassade.getInstance().getDirectory(null, this);
    }

    public int getChildCount() {
        return getChildren().length;
    }

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return false;
    }

    public Icon getConvenientIcon() {
        if (directoryDO == null) {
            return null; //rootNode
        }

        IconManager im = IconManager.getInstance();
        switch (directoryDO.getType()) {
            case DirectoryDO.TYPE_DESKTOP:
                return im.getIcon("server");
            case DirectoryDO.TYPE_DISKETTE:
                return im.getIcon("diskette");
            case DirectoryDO.TYPE_LAUFWERK:
                return im.getIcon("laufwerk");
            case DirectoryDO.TYPE_ORDNER:
                return im.getIcon("tree");
            case DirectoryDO.TYPE_RECHNER:
                return im.getIcon("server");
            default:
                return null;
        }
    }

    public DirectoryDO getDO() {
        return directoryDO;
    }

    public String toString() {
        if (directoryDO == null) {
            return "rootNode";
        }
        return directoryDO.getName();
    }

    public ApplejuiceNode addChild(DirectoryDO childDirectoryDO) {
        if (children == null) {
            children = new ArrayList();
        }
        DirectoryChooserNode childNode = new DirectoryChooserNode(this,
            childDirectoryDO);
        children.add(childNode);
        return childNode;
    }

    private void sortChildren() {
        if (children == null || children.size() < 2) {
            return;
        }
        int n = children.size();
        int k;
        for (int i = 0; i < n - 1; i++) {
            k = i;
            for (int j = i + 1; j < n; j++) {
                if (!((DirectoryChooserNode)children.get(j)).getDO().isFileSystem()){
                    continue;
                }
                if (children.get(j).toString().compareToIgnoreCase(children.get(
                    k).toString()) < 0) {
                    k = j;
                }
            }
            Object tmp = children.get(i);
            children.set(i, children.get(k));
            children.set(k, tmp);
        }
    }

    protected Object[] getChildren() {
        if (children == null) {
            children = new ArrayList();
            ApplejuiceFassade.getInstance().getDirectory(directoryDO.getPath(), this);
            sortChildren();
        }
        return children.toArray(new DirectoryChooserNode[children.size()]);
    }

}
