package de.applejuicenet.client.gui.plugins.jabber.control;

import java.util.Iterator;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;

import de.applejuicenet.client.gui.plugins.jabber.control.rostertree.RosterTreeNode;
import de.applejuicenet.client.gui.plugins.jabber.view.IdentityPanel;

public class IdentityController
{
   private static IdentityController instance = null;
   private XMPPConnection            connection;
   private IdentityPanel             identityPanel = null;
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
         new Thread(new Runnable()
            {
               public void run()
               {
                  RosterTreeNode rootNode = getKnownUsersTreeModel().getRootNode();

                  rootNode.setText(connection.getUser());
                  rootNode.removeAllChildren();
                  Roster      roster = connection.getRoster();
                  Iterator    it = roster.getGroups();
                  RosterGroup curGroup;

                  while(it.hasNext())
                  {
                     curGroup = (RosterGroup) it.next();
                     RosterTreeNode groupNode = new RosterTreeNode(curGroup.getName());

                     rootNode.insert(groupNode, 0);
                     Iterator it2 = curGroup.getEntries();

                     while(it2.hasNext())
                     {
                        RosterEntry    curRosterEntry = (RosterEntry) it2.next();
                        RosterTreeNode rosterEntryNode = new RosterTreeNode(curRosterEntry.getUser());

                        groupNode.insert(rosterEntryNode, 0);
                     }
                  }

                  SwingUtilities.invokeLater(new Runnable()
                     {
                        public void run()
                        {
                           getPanel().getKnownUsersTree().updateUI();
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
}
