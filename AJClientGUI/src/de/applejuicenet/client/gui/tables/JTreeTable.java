package de.applejuicenet.client.gui.tables;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.download.DownloadNode;
import de.applejuicenet.client.gui.tables.AbstractCellEditor;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/JTreeTable.java,v 1.1 2003/07/01 18:41:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: JTreeTable.java,v $
 * Revision 1.1  2003/07/01 18:41:39  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.12  2003/07/01 18:34:28  maj0r
 * Struktur ver�ndert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class JTreeTable
    extends JTable
    implements DataUpdateListener {
  protected TreeTableCellRenderer tree;

  public JTreeTable(TreeTableModel treeTableModel) {
    super();

    tree = new TreeTableCellRenderer(treeTableModel);

    super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

    tree.setSelectionModel(new DefaultTreeSelectionModel() {
      {
        setSelectionModel(listSelectionModel);
      }
    });
    tree.setRowHeight(getRowHeight());

    setDefaultRenderer(TreeTableModel.class, tree);
    setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

    setShowGrid(false);
    setIntercellSpacing(new Dimension(0, 0));
    DataManager.getInstance().addDataUpdateListener(this,
        DataUpdateListener.DOWNLOAD_CHANGED);
  }

  public void fireContentChanged(int type, Object content) {
    if (type != DataUpdateListener.DOWNLOAD_CHANGED) {
      return;
    }
    ( (TreeTableModelAdapter) getModel()).fillTree();
    repaint();
  }

  public int getEditingRow() {
    return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 :
        editingRow;
  }

  public class TreeTableCellRenderer
      extends JTree
      implements TableCellRenderer {

    protected int visibleRow;

    public TreeTableCellRenderer(TreeModel model) {
      super(model);
      this.setRootVisible(false);
      this.setCellRenderer(new IconNodeRenderer());
    }

    public void setBounds(int x, int y, int w, int h) {
      super.setBounds(x, 0, w, JTreeTable.this.getHeight());
    }

    public void paint(Graphics g) {
      g.translate(0, -visibleRow * getRowHeight());
      super.paint(g);
    }

    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row, int column) {
      if (isSelected) {
        setBackground(table.getSelectionBackground());
      }
      else {
        setBackground(table.getBackground());
      }
      visibleRow = row;
      return this;
    }
  }

  public class TreeTableCellEditor
      extends AbstractCellEditor
      implements TableCellEditor {
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int r,
                                                 int c) {
      return tree;
    }
  }

  public class IconNodeRenderer
      extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value,
                                         sel, expanded, leaf, row, hasFocus);

      Icon icon = ( (Node) value).getConvenientIcon();
      if (icon != null) {
        setIcon(icon);
      }
      return this;
    }
  }

}