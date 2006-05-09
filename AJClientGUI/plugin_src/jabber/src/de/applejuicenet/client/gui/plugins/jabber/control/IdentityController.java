/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.control;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import de.applejuicenet.client.gui.plugins.jabber.control.rostertree.RosterTreeNode;
import de.applejuicenet.client.gui.plugins.jabber.view.IdentityPanel;
import de.applejuicenet.client.gui.plugins.jabber.view.RosterUserTreeCellRenderer;

public class IdentityController
{
   private static IdentityController instance            = null;
   private XMPPConnection            connection;
   private IdentityPanel             identityPanel       = null;
   private KnownUsersTreeModel       knownUsersTreeModel = null;

   private IdentityController()
   {
   }

   public static synchronized IdentityController getInstance()
   {
      if(null == instance)
      {
         instance = new IdentityController();
      }

      return instance;
   }

   public void setConnection(XMPPConnection aConnection)
   {
      connection = aConnection;
      if(null == connection)
      {
         getKnownUsersTreeModel().getRootNode().removeAllChildren();
         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  getPanel().getKnownUsersTree().updateUI();
               }
            });
      }
      else
      {
         final Roster roster = connection.getRoster();

         new MyRosterListener(roster);
         new Thread(new Runnable()
            {
               public void run()
               {
                  RosterTreeNode rootNode = getKnownUsersTreeModel().getRootNode();

                  rootNode.setText(connection.getUser());
                  rootNode.removeAllChildren();
                  RosterGroup     curGroup;
                  RosterEntry     curRosterEntry;
                  RosterTreeNode  groupNode;
                  RosterTreeNode  rosterEntryNode;
                  String          name;

                  HashSet<String> allUsers = new HashSet<String>();
                  Iterator        it       = roster.getEntries();

                  while(it.hasNext())
                  {
                     curRosterEntry = (RosterEntry) it.next();
                     allUsers.add(curRosterEntry.getName());
                  }

                  it = roster.getGroups();
                  while(it.hasNext())
                  {
                     curGroup  = (RosterGroup) it.next();
                     groupNode = new RosterTreeNode(curGroup.getName());

                     rootNode.insert(groupNode, 0);
                     Iterator it2 = curGroup.getEntries();

                     while(it2.hasNext())
                     {
                        curRosterEntry  = (RosterEntry) it2.next();
                        name            = curRosterEntry.getName();
                        rosterEntryNode = new RosterTreeNode(name);
                        allUsers.remove(name);
                        groupNode.insert(rosterEntryNode, 0);
                     }
                  }

                  if(allUsers.size() > 0)
                  {
                     groupNode = new RosterTreeNode("Sonstige");
                     rootNode.insert(groupNode, rootNode.getChildCount());
                     for(String curUser : allUsers)
                     {
                        rosterEntryNode = new RosterTreeNode(curUser);

                        groupNode.insert(rosterEntryNode, 0);
                     }
                  }

                  SwingUtilities.invokeLater(new Runnable()
                     {
                        public void run()
                        {
                           getPanel().getKnownUsersTree().updateUI();
                           int count = getKnownUsersTreeModel().getRootNode().getChildCount();

                           for(int i = count - 1; i >= 0; i--)
                           {
                              getPanel().getKnownUsersTree().expandRow(i);
                           }
                        }
                     });
               }
            }).start();
      }
   }

   public IdentityPanel getPanel()
   {
      if(null == identityPanel)
      {
         identityPanel = new IdentityPanel();
         identityPanel.getKnownUsersTree().setModel(getKnownUsersTreeModel());

         identityPanel.getKnownUsersTree().setCellRenderer(new RosterUserTreeCellRenderer());
      }

      return identityPanel;
   }

   public KnownUsersTreeModel getKnownUsersTreeModel()
   {
      if(null == knownUsersTreeModel)
      {
         knownUsersTreeModel = new KnownUsersTreeModel();
      }

      return knownUsersTreeModel;
   }

   private class MyRosterListener implements RosterListener
   {
      private final Roster roster;

      public MyRosterListener(Roster roster)
      {
         this.roster = roster;
         roster.addRosterListener(this);
      }

      public void entriesAdded(Collection arg0)
      {
      }

      public void entriesDeleted(Collection arg0)
      {
      }

      public void entriesUpdated(Collection arg0)
      {

         // nichts zu tun
         ;
      }

      public void presenceChanged(String nick)
      {
         Presence presence = roster.getPresence(nick);

         nick = nick.substring(0, nick.indexOf("@"));
         RosterTreeNode rootNode = getKnownUsersTreeModel().getRootNode();
         int            count = rootNode.getChildCount();
         boolean        found = false;

         for(int i = 0; i < count; i++)
         {
            RosterTreeNode child = (RosterTreeNode) rootNode.getChildAt(i);

            if(child.isLeaf())
            {
               if(child.getText().equalsIgnoreCase(nick))
               {
                  child.setPresence(presence);
                  found = true;
               }
            }
            else
            {
               int count2 = child.getChildCount();

               for(int x = 0; x < count2; x++)
               {
                  RosterTreeNode subChild = (RosterTreeNode) child.getChildAt(x);

                  if(subChild.getText().equalsIgnoreCase(nick))
                  {
                     subChild.setPresence(presence);
                     found = true;
                     break;
                  }
               }
            }

            if(found)
            {
               break;
            }
         }

         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  identityPanel.getKnownUsersTree().updateUI();
               }
            });
      }
   }
}
