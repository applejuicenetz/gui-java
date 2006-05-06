/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.view;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.applejuicenet.client.gui.plugins.jabber.control.rostertree.RosterTreeNode;

public class RosterUserTreeCellRenderer extends DefaultTreeCellRenderer
{
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                                                 int row, boolean hasFocus)
   {
      Component      comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      RosterTreeNode node = (RosterTreeNode) value;

      if(null == node.getParent())
      {

         // rootnode
         return comp;
      }

      if(leaf)
      {
         if(null == node.getPresence())
         {
            ((JLabel) comp).setText(node.getText() + " (off)");
         }
         else
         {
            ((JLabel) comp).setText(node.getText());
         }
         ((JLabel) comp).setIcon(null);
         return comp;
      }

      return comp;
   }
}
