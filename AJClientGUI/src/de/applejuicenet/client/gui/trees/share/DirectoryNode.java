package de.applejuicenet.client.gui.trees.share;

import de.applejuicenet.client.shared.dac.DirectoryDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.trees.ApplejuiceNode;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/DirectoryNode.java,v 1.6 2003/08/24 14:59:59 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryNode.java,v $
 * Revision 1.6  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.5  2003/08/17 16:13:11  maj0r
 * Erstellen des DirectoryNode-Baumes korrigiert.
 *
 * Revision 1.4  2003/08/16 20:53:40  maj0r
 * Kleinen Fehler korrigiert
 *
 * Revision 1.3  2003/08/15 17:53:54  maj0r
 * Tree fuer Shareauswahl fortgefuehrt, aber noch nicht fertiggestellt.
 *
 * Revision 1.2  2003/08/15 14:44:20  maj0r
 * DirectoryTree eingefügt, aber noch nicht fertiggestellt.
 *
 *
 */

public class DirectoryNode extends DefaultMutableTreeNode implements Node, ApplejuiceNode{
    private DirectoryDO directoryDO;
    private ArrayList children = null;
    private DirectoryNode parent;

    public DirectoryNode(DirectoryNode parent, DirectoryDO directoryDO) {
        this.parent = parent;
        this.directoryDO = directoryDO;
    }

    public DirectoryNode() {
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
        if (directoryDO==null)
            return null;      //rootNode

        IconManager im = IconManager.getInstance();
        switch (directoryDO.getType()){
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
        if (directoryDO==null)
            return "rootNode";
        return directoryDO.getName();
    }

    public ApplejuiceNode addChild(DirectoryDO childDirectoryDO){
        if (children==null){
            children = new ArrayList();
        }
        DirectoryNode childNode = new DirectoryNode(this, childDirectoryDO);
        children.add(childNode);
        return childNode;
    }

    protected Object[] getChildren() {
        if (children==null){
            children = new ArrayList();
            ApplejuiceFassade.getInstance().getDirectory(directoryDO.getPath(), this);
        }
        return children.toArray(new DirectoryNode[children.size()]);
    }

}
