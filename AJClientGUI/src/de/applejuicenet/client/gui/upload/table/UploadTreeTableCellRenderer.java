package de.applejuicenet.client.gui.upload.table;

import javax.swing.tree.TreeModel;

import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;

public class UploadTreeTableCellRenderer extends DefaultTreeTableCellRenderer{

	private static final long serialVersionUID = 7183042985627590983L;
	
	public UploadTreeTableCellRenderer(TreeModel model) {
		super(model);
	}
	protected DefaultIconNodeRenderer getIconNodeRenderer() {
		return new UploadIconNodeRenderer();
	}
}
