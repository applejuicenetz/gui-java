package de.applejuicenet.client.gui.tablerenderer;

import java.net.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

public class JTreeTable
    extends JTable {
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

}
