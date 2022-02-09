package de.applejuicenet.client.gui.components.treetable;

import de.applejuicenet.client.gui.components.util.IconGetter;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class DefaultIconNodeRenderer extends DefaultTreeCellRenderer {

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
