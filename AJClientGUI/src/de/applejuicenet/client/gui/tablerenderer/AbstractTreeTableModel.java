package de.applejuicenet.client.gui.tablerenderer;

import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tablerenderer/Attic/AbstractTreeTableModel.java,v 1.5 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AbstractTreeTableModel.java,v $
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public abstract class AbstractTreeTableModel
    extends DefaultTreeModel
    implements TreeTableModel {
  protected Object root;
  protected EventListenerList listenerList = new EventListenerList();

  public AbstractTreeTableModel(Object root) {
    super( (TreeNode) root);
    this.root = root;
  }

  public Object getRoot() {
    return root;
  }

  public boolean isLeaf(Object node) {
    return getChildCount(node) == 0;
  }

  public void valueForPathChanged(TreePath path, Object newValue) {}

  public int getIndexOfChild(Object parent, Object child) {
    for (int i = 0; i < getChildCount(parent); i++) {
      if (getChild(parent, i).equals(child)) {
        return i;
      }
    }
    return -1;
  }

  public void addTreeModelListener(TreeModelListener l) {
    listenerList.add(TreeModelListener.class, l);
  }

  public void removeTreeModelListener(TreeModelListener l) {
    listenerList.remove(TreeModelListener.class, l);
  }

  protected void fireTreeNodesChanged(Object source, Object[] path,
                                      int[] childIndices,
                                      Object[] children) {
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        if (e == null) {
          e = new TreeModelEvent(source, path,
                                 childIndices, children);
        }
        ( (TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
      }
    }
  }

  protected void fireTreeNodesInserted(Object source, Object[] path,
                                       int[] childIndices,
                                       Object[] children) {
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        if (e == null) {
          e = new TreeModelEvent(source, path,
                                 childIndices, children);
        }
        ( (TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
      }
    }
  }

  protected void fireTreeNodesRemoved(Object source, Object[] path,
                                      int[] childIndices,
                                      Object[] children) {
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        if (e == null) {
          e = new TreeModelEvent(source, path,
                                 childIndices, children);
        }
        ( (TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
      }
    }
  }

  protected void fireTreeStructureChanged(Object source, Object[] path,
                                          int[] childIndices,
                                          Object[] children) {
    Object[] listeners = listenerList.getListenerList();
    TreeModelEvent e = null;
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TreeModelListener.class) {
        if (e == null) {
          e = new TreeModelEvent(source, path,
                                 childIndices, children);
        }
        ( (TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
      }
    }
  }

  public Class getColumnClass(int column) {
    return Object.class;
  }

  public boolean isCellEditable(Object node, int column) {
    return getColumnClass(column) == TreeTableModel.class;
  }

  public void setValueAt(Object aValue, Object node, int column) {}

  // Left to be implemented in the subclass:

  /*
   *   public Object getChild(Object parent, int index)
   *   public int getChildCount(Object parent)
   *   public int getColumnCount()
   *   public String getColumnName(Object node, int column)
   *   public Object getValueAt(Object node, int column)
   */

}
