package de.applejuicenet.client.gui.search.table;

import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;

import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.gui.components.treetable.DefaultIconNodeRenderer;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.components.util.IconGetter;

public class SearchResultIconNodeRenderer extends DefaultIconNodeRenderer{

    private static HashSet<String> md5Sums = new HashSet<String>();
    
    public static void addMd5Sum(String md5sum){
        md5Sums.add(md5sum);
    }

    public static void removeMd5Sum(String md5sum){
        md5Sums.remove(md5sum);
    }
    
	public SearchResultIconNodeRenderer() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

        String newValue = null;
		if (value instanceof FileName) {
            newValue = ((FileName) value).getDateiName();
		} else {
            newValue = value.toString();
		}
        Component c = super.getTreeCellRendererComponent(tree, newValue, sel, expanded,
                leaf, row, hasFocus);
        if (value instanceof SearchNode
                && ((SearchNode)value).getNodeType() == SearchNode.ENTRY_NODE
                && md5Sums.contains(((SearchEntry)((SearchNode)value).getValueObject()).getChecksumme())){
                c.setBackground(Color.GREEN);
                ((JLabel)c).setOpaque(true);
        }
        else{
            c.setBackground(tree.getBackground());
            ((JLabel)c).setOpaque(false);
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
