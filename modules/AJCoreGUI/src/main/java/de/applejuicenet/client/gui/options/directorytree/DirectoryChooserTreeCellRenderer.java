package de.applejuicenet.client.gui.options.directorytree;

import de.applejuicenet.client.gui.components.treetable.Node;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Copyright: General Public License
 */
public class DirectoryChooserTreeCellRenderer extends DefaultTreeCellRenderer
{
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row,
      boolean hasFocus)
   {
      super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
      Icon icon = value instanceof Node ? ((Node) value).getConvenientIcon() : null;

      if(icon != null)
      {
         setIcon(icon);
      }

      return this;
   }
}
