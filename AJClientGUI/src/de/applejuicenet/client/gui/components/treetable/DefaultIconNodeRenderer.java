package de.applejuicenet.client.gui.components.treetable;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.applejuicenet.client.gui.download.table.IconGetter;

public class DefaultIconNodeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1476701693980518291L;

	public DefaultIconNodeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded,
				leaf, row, hasFocus);
		if (value instanceof Node) {
			Icon icon = ((Node) value).getConvenientIcon();
			if (icon != null) {
				setIcon(icon);
			}
		} else {
			Icon icon = IconGetter.getConvenientIcon(value);
			if (icon != null) {
				setIcon(icon);
			}
		}
		return this;
	}
}
