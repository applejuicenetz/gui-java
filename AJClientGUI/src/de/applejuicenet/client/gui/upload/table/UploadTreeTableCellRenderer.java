package de.applejuicenet.client.gui.upload.table;

import javax.swing.tree.TreeModel;

import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;

public class UploadTreeTableCellRenderer extends DefaultTreeTableCellRenderer{

	public UploadTreeTableCellRenderer(TreeModel model) {
		super(model);
	}
	protected DefaultIconNodeRenderer getIconNodeRenderer() {
		return new UploadIconNodeRenderer();
	}
}
