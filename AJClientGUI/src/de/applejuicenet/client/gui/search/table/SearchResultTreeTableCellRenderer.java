package de.applejuicenet.client.gui.search.table;

import javax.swing.tree.TreeModel;

import de.applejuicenet.client.gui.tables.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.tables.DefaultTreeTableCellRenderer;

public class SearchResultTreeTableCellRenderer extends DefaultTreeTableCellRenderer{

	private static final long serialVersionUID = 609607530614270654L;
	
	public SearchResultTreeTableCellRenderer(TreeModel model) {
		super(model);
	}
	protected DefaultIconNodeRenderer getIconNodeRenderer() {
		return new SearchResultIconNodeRenderer();
	}
}
