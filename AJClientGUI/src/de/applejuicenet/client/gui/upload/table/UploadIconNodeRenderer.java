package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.components.util.IconGetter;

public class UploadIconNodeRenderer extends DefaultIconNodeRenderer{

	public UploadIconNodeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component c = null;
		if (value instanceof Upload) {
			c = super.getTreeCellRendererComponent(tree, ((Upload) value)
					.getDateiName(), sel, expanded, leaf, row, hasFocus);
		} else if (value.getClass() == UploadMainNode.class) {
			c = super.getTreeCellRendererComponent(tree, ((UploadMainNode) value)
					.toString()
					+ " (" + ((UploadMainNode) value).getChildCount() + ")", sel,
					expanded, leaf, row, hasFocus);
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
