/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.view;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jivesoftware.smack.packet.Presence;

import de.applejuicenet.client.gui.plugins.jabber.control.rostertree.RosterTreeNode;

public class RosterUserTreeCellRenderer extends DefaultTreeCellRenderer
{
   public static ImageIcon available;
   public static ImageIcon offline;
   public static ImageIcon awk;

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
         ImageIcon icon;

         Presence  presence    = node.getPresence();
         String    toolTipText;

         if(null == presence)
         {
            icon        = offline;
            toolTipText = "offline";
         }
         else if(presence.getMode() == Presence.Mode.AVAILABLE)
         {
            icon        = available;
            toolTipText = presence.getMode().toString();
         }
         else
         {
            icon        = awk;
            toolTipText = presence.getMode().toString();
         }

         ((JLabel) comp).setIcon(icon);
         ((JLabel) comp).setToolTipText(toolTipText);
         return comp;
      }

      return comp;
   }
}
