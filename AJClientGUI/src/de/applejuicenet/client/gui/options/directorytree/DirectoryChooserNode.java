package de.applejuicenet.client.gui.options.directorytree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.applejuicenet.client.fassade.controller.xml.DirectoryDO;
import de.applejuicenet.client.gui.components.tree.ApplejuiceNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/directorytree/DirectoryChooserNode.java,v 1.4 2005/01/19 11:03:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DirectoryChooserNode
    extends DefaultMutableTreeNode
    implements Node, ApplejuiceNode {
	private DirectoryDO directoryDO;
    private List children = null;
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
        //todo
        
        //AppleJuiceClient.getAjFassade().getDirectory(null, this);
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
            //todo
            //ApplejuiceFassade.getInstance().getDirectory(directoryDO.getPath(), this);
            sortChildren();
        }
        return children.toArray(new DirectoryChooserNode[children.size()]);
    }

}
