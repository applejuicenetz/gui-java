package de.applejuicenet.client.gui.tablerenderer;

import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Graphics;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DownloadDateiNameCellRenderer extends JTree implements TableCellRenderer {
  // This method is called each time a cell in a column
  // using this renderer needs to be rendered.
  public DownloadDateiNameCellRenderer(JTable table){
    super(new DefaultTreeModel(new DefaultMutableTreeNode("root label")));
    this.table = table;
  }

  protected int visibleRow;
  protected JTable table;

  public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, 0, w, table.getHeight());
        }

  public void paint(Graphics g) {
            g.translate(0, -visibleRow * getRowHeight());
            super.paint(g);
        }

  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
      // 'value' is value contained in the cell located at
      // (rowIndex, vColIndex)

      visibleRow = rowIndex;

      if (isSelected) {
          // cell (and perhaps other cells) are selected
      }

      if (hasFocus) {
          // this cell is the anchor and the table has the focus
      }

      // Configure the component with the specified value
      ((DefaultMutableTreeNode)getModel().getRoot()).add(new DefaultMutableTreeNode("Node Label"));

//      setText(value.toString());

      // Set tool tip if desired
      setToolTipText((String)value);

      // Since the renderer is a component, return itself
      return this;
  }
}