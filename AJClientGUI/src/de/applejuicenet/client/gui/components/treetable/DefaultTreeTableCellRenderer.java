package de.applejuicenet.client.gui.components.treetable;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;


public class DefaultTreeTableCellRenderer extends JTree implements
		TableCellRenderer  {

	private static final long serialVersionUID = -7215158508387389379L;

	protected int visibleRow;

	protected JTreeTable treeTable;

	public DefaultTreeTableCellRenderer(TreeModel model) {
		super(model);
	}
	
	public void setTreeTable(JTreeTable treeTable){
		this.treeTable = treeTable;
		setCellRenderer(getIconNodeRenderer());
	}

	/**
	 * Ueberschreiben, um den IconNodeRenderer anzupassen
	 */
	protected DefaultIconNodeRenderer getIconNodeRenderer(){
		return new DefaultIconNodeRenderer();
	}
		
	public void updateUI() {
		super.updateUI();
		TreeCellRenderer tcr = getCellRenderer();
		if (tcr instanceof DefaultTreeCellRenderer) {
			DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
			dtcr.setTextSelectionColor(UIManager
					.getColor("Table.selectionForeground"));
			dtcr.setBackgroundSelectionColor(UIManager
					.getColor("Table.selectionBackground"));
		}
	}
	
	public void setRowHeight(int rowHeight) {
		if (rowHeight > 0) {
			super.setRowHeight(rowHeight);
			if (treeTable != null && treeTable.getRowHeight() != rowHeight) {
				treeTable.setRowHeight(getRowHeight());
			}
		}
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, 0, w, treeTable.getHeight());
	}

	public void paint(Graphics g) {
		g.translate(0, -visibleRow * getRowHeight());
		super.paint(g);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Object node = ((TreeTableModelAdapter) table.getModel())
				.nodeForRow(row);
		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}
		visibleRow = row;
		return this;
	}

}
