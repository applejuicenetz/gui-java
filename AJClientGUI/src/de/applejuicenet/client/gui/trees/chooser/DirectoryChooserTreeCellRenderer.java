package de.applejuicenet.client.gui.trees.chooser;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.applejuicenet.client.gui.tables.Node;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/chooser/Attic/DirectoryChooserTreeCellRenderer.java,v 1.9 2004/10/14 08:57:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class DirectoryChooserTreeCellRenderer
    extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = -324801254565075523L;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value,
            sel, expanded, leaf, row, hasFocus);
        Icon icon = ( (Node) value).getConvenientIcon();
        if (icon != null) {
            setIcon(icon);
        }
        return this;
    }
}
