package de.applejuicenet.client.gui.trees.chooser;

import de.applejuicenet.client.gui.tables.Node;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/chooser/Attic/DirectoryChooserTreeCellRenderer.java,v 1.1 2003/08/24 19:37:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DirectoryChooserTreeCellRenderer.java,v $
 * Revision 1.1  2003/08/24 19:37:25  maj0r
 * no message
 *
 *
 */

public class DirectoryChooserTreeCellRenderer extends DefaultTreeCellRenderer{
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded,
                                                  boolean leaf,
                                                  int row, boolean hasFocus) {

      Component c = super.getTreeCellRendererComponent(tree, value,
                                         sel, expanded, leaf, row, hasFocus);
      Icon icon = ( (Node) value).getConvenientIcon();
      if (icon != null) {
        setIcon(icon);
      }
      return this;
    }
}
