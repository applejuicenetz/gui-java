package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;

import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.components.util.IconGetter;
import de.applejuicenet.client.shared.dac.UploadDO;

public class UploadIconNodeRenderer extends DefaultIconNodeRenderer{

	private static final long serialVersionUID = -2877797102156404846L;

	public UploadIconNodeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component c = null;
		if (value.getClass() == UploadDO.class) {
			c = super.getTreeCellRendererComponent(tree, ((UploadDO) value)
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
