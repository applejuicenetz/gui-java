package de.applejuicenet.client.gui.trees.share;

import de.applejuicenet.client.gui.tables.Node;
import de.applejuicenet.client.gui.tables.share.ShareNode;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/trees/share/Attic/ShareSelectionTreeCellRenderer.java,v 1.2 2003/08/26 06:20:10 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareSelectionTreeCellRenderer.java,v $
 * Revision 1.2  2003/08/26 06:20:10  maj0r
 * Anpassungen an muhs neuen Tree.
 *
 * Revision 1.1  2003/08/15 14:43:38  maj0r
 * DirectoryTree eingef�gt, aber noch nicht fertiggestellt.
 *
 *
 */

public class ShareSelectionTreeCellRenderer extends DefaultTreeCellRenderer{
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
/*      if (value instanceof DirectoryNode){
          JPanel panel1 = new JPanel(new FlowLayout());
          JLabel iconLabel = new JLabel();
          iconLabel.setIcon(((DirectoryNode)value).getShareModeIcon());
          panel1.add(iconLabel);
          panel1.add(c);
          return panel1;
      }*/
      return this;
    }
}
