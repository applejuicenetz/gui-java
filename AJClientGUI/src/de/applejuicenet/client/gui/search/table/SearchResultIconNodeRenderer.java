package de.applejuicenet.client.gui.search.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;

import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.components.util.IconGetter;

public class SearchResultIconNodeRenderer extends DefaultIconNodeRenderer{

	public SearchResultIconNodeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component c = null;
		if (value instanceof FileName) {
			c = super.getTreeCellRendererComponent(tree, ((FileName) value)
					.getDateiName(), sel, expanded, leaf, row, hasFocus);
		} else {
			c = super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
		}
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
