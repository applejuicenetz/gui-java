package de.applejuicenet.client.gui.plugins.jabber.view;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class IdentityPanel extends JScrollPane
{
   private JTree knownUsersTree = null;

   public IdentityPanel()
   {
      setViewportView(getKnownUsersTree());
      setBorder(BorderFactory.createTitledBorder("Kontakte"));
      setMinimumSize(new Dimension(140, 10));
   }

   public JTree getKnownUsersTree()
   {
      if(null == knownUsersTree)
      {
         knownUsersTree = new JTree();
         knownUsersTree.setRootVisible(false);
      }

      return knownUsersTree;
   }
}
