package de.applejuicenet.client.gui.trees.share;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import java.util.Vector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/ShareSelectionTreeModel.java,v 1.1 2003/08/15 14:43:38 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareSelectionTreeModel.java,v $
 * Revision 1.1  2003/08/15 14:43:38  maj0r
 * DirectoryTree eingefügt, aber noch nicht fertiggestellt.
 *
 *
 */

public class ShareSelectionTreeModel implements TreeModel {
    private DirectoryNode root;
    private Vector listener = new Vector();

    public ShareSelectionTreeModel() {
        root = new DirectoryNode();
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object obj, int i) {
        DirectoryNode ok = (DirectoryNode) obj;
        return ok.getChildren()[i];
    }

    public int getChildCount(Object obj) {
        DirectoryNode ok = (DirectoryNode) obj;
        return ok.getChildCount();
    }

    public boolean isLeaf(Object obj) {
        DirectoryNode ok = (DirectoryNode) obj;
        return ok.isLeaf();
    }

    public void valueForPathChanged(TreePath path, Object obj) {
        for (int i = 0; i < listener.size(); i++)
        {
            TreeModelListener ml = (TreeModelListener) listener.get(i);
            ml.treeNodesChanged(new TreeModelEvent(obj, path));
        }
    }

    public void fireStructureChangedEvent() {
        for (int i = 0; i < listener.size(); i++)
        {
            TreeModelListener ml = (TreeModelListener) listener.get(i);
            ml.treeStructureChanged(new TreeModelEvent(this, new TreePath(root)));
        }
    }

    public void fireNodeInsertedEvent(TreePath path, Object obj, int ind, Object child) {
        for (int i = 0; i < listener.size(); i++)
        {
            TreeModelListener ml = (TreeModelListener) listener.get(i);
            ml.treeNodesInserted(new TreeModelEvent(obj, path, new int[]{ind}, new Object[]{child}));
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        DirectoryNode[] node = (DirectoryNode[])((DirectoryNode)parent).getChildren();
        for (int i=0; i<node.length; i++){
            if (node[i]==child)
                return i;
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener modelListener) {
        listener.add(modelListener);
    }

    public void removeTreeModelListener(TreeModelListener modelListener) {
        listener.remove(modelListener);
    }

}
