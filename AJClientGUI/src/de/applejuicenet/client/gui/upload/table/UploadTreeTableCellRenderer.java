package de.applejuicenet.client.gui.upload.table;

import javax.swing.tree.TreeModel;

import de.applejuicenet.client.gui.tables.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.tables.DefaultTreeTableCellRenderer;

public class UploadTreeTableCellRenderer extends DefaultTreeTableCellRenderer{

	private static final long serialVersionUID = 7183042985627590983L;
	
	public UploadTreeTableCellRenderer(TreeModel model) {
		super(model);
	}
	protected DefaultIconNodeRenderer getIconNodeRenderer() {
		return new UploadIconNodeRenderer();
	}
}
