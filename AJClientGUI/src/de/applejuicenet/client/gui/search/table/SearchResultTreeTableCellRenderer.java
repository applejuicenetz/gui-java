package de.applejuicenet.client.gui.search.table;

import javax.swing.tree.TreeModel;

import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;

public class SearchResultTreeTableCellRenderer extends DefaultTreeTableCellRenderer{

	public SearchResultTreeTableCellRenderer(TreeModel model) {
		super(model);
	}
	protected DefaultIconNodeRenderer getIconNodeRenderer() {
		return new SearchResultIconNodeRenderer();
	}
}
