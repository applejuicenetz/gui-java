package de.applejuicenet.client.gui.trees.chooser;

import de.applejuicenet.client.gui.tables.Node;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/chooser/Attic/DirectoryChooserTreeCellRenderer.java,v 1.3 2003/10/27 12:35:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DirectoryChooserTreeCellRenderer.java,v $
 * Revision 1.3  2003/10/27 12:35:26  maj0r
 * Bug #1034 fixed (Danke an lov und akkut):
 * Auf den ersten Blick wars ueberfluessig, muss aber weiterhin drin bleiben.
 *
 * Revision 1.2  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
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
