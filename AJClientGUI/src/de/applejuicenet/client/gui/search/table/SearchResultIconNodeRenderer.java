package de.applejuicenet.client.gui.search.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;

import de.applejuicenet.client.gui.download.table.IconGetter;
import de.applejuicenet.client.gui.tables.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;

public class SearchResultIconNodeRenderer extends DefaultIconNodeRenderer{

	private static final long serialVersionUID = -6263988615720145890L;

	public SearchResultIconNodeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component c = null;
		if (value.getClass() == FileName.class) {
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
