package de.applejuicenet.client.gui.trees.share;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/DirectoryNode.java,v 1.1 2003/08/14 20:08:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryNode.java,v $
 * Revision 1.1  2003/08/14 20:08:42  maj0r
 * Tree fuer Shareauswahl eingefuegt, aber noch nicht fertiggestellt.
 *
 *
 */

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.dac.DirectoryDO;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;

public class DirectoryNode extends DefaultMutableTreeNode implements Node {
    private DirectoryDO directoryDO;

    private ArrayList children = new ArrayList();
    private DirectoryNode parent;

    public DirectoryNode(DirectoryNode parent, DirectoryDO directoryDO) {
        this.parent = parent;
        this.directoryDO = directoryDO;
    }

    public Icon getConvenientIcon() {
        IconManager im = IconManager.getInstance();
        switch (directoryDO.getType()) {
            case DirectoryDO.TYPE_DESKTOP:
                return im.getIcon("laufwerk");
            case DirectoryDO.TYPE_DISKETTENLAUFWERK:
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

    public TreeNode getChildAt(int childIndex) {
        return (DirectoryNode)children.get(childIndex);
    }

    public int getChildCount() {
        return children.size();
    }

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return false;
    }

    public void addChild(DirectoryNode shareNode){
        children.add(shareNode);
    }
}
