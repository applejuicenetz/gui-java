package de.applejuicenet.client.gui.trees.share;

import de.applejuicenet.client.shared.dac.DirectoryDO;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/DirectoryNode.java,v 1.2 2003/08/15 14:44:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryNode.java,v $
 * Revision 1.2  2003/08/15 14:44:20  maj0r
 * DirectoryTree eingefügt, aber noch nicht fertiggestellt.
 *
 *
 */

public class DirectoryNode extends DefaultMutableTreeNode implements Node{
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
        DirectoryDO[] childDirectoryDO = ApplejuiceFassade.getInstance().getDirectory(null);
        if (childDirectoryDO!=null && childDirectoryDO.length!=0){
            for(int i=0; i<childDirectoryDO.length; i++){
                children.add(new DirectoryNode(this, childDirectoryDO[i]));
            }
        }
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

    public String getFullPath(){
        if (directoryDO==null)
            return "";
        String path = parent.getFullPath();
        if (path.length()!=0){
            path += directoryDO.getSeparator();
        }
        return path + directoryDO.getPath();
    }

    protected Object[] getChildren() {
        if (children==null){
            children = new ArrayList();
            DirectoryDO[] childDirectoryDO = ApplejuiceFassade.getInstance().getDirectory(getFullPath());
            if (childDirectoryDO!=null && childDirectoryDO.length!=0){
                for(int i=0; i<childDirectoryDO.length; i++){
                    children.add(new DirectoryNode(this, childDirectoryDO[i]));
                }
            }
        }
        return children.toArray(new DirectoryNode[children.size()]);
    }

}
