package de.applejuicenet.client.gui.options.directorytree;

import de.applejuicenet.client.gui.components.treetable.Node;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/directorytree/DirectoryChooserTreeCellRenderer.java,v 1.3 2006/05/03 14:52:22 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class DirectoryChooserTreeCellRenderer extends DefaultTreeCellRenderer
{
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
      boolean hasFocus)
   {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      Icon icon = ((Node) value).getConvenientIcon();

      if(icon != null)
      {
         setIcon(icon);
      }

      return this;
   }
}
