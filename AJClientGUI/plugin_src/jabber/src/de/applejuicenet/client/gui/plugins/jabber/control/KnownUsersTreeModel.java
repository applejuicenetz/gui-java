package de.applejuicenet.client.gui.plugins.jabber.control;

import javax.swing.tree.DefaultTreeModel;

import de.applejuicenet.client.gui.plugins.jabber.control.rostertree.RosterTreeNode;

public class KnownUsersTreeModel extends DefaultTreeModel
{
   public KnownUsersTreeModel()
   {
      super(new RosterTreeNode());
   }

   public RosterTreeNode getRootNode()
   {
      return (RosterTreeNode) getRoot();
   }
}
