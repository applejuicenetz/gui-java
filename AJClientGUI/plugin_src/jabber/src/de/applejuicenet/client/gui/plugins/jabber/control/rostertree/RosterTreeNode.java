/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.control.rostertree;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jivesoftware.smack.packet.Presence;

public class RosterTreeNode implements MutableTreeNode
{
   private Set<RosterTreeNode> users = new TreeSet<RosterTreeNode>(new Comparator<RosterTreeNode>()
      {
         public int compare(RosterTreeNode o1, RosterTreeNode o2)
         {
            return o1.getText().compareToIgnoreCase(o2.getText());
         }
      });
   private RosterTreeNode      parent   = null;
   private String              text     = "";
   private Presence            presence = null;

   public RosterTreeNode(String text)
   {
      this.text = text;
   }

   public RosterTreeNode()
   {
      this("");
   }

   public void removeAllChildren()
   {
      for(int i = users.size() - 1; i >= 0; i--)
      {
         remove(i);
      }
   }

   public void insert(MutableTreeNode child, int index)
   {
      users.add((RosterTreeNode) child);
      child.setParent(this);
   }

   public void remove(int index)
   {
      remove(users.toArray(new RosterTreeNode[users.size()])[index]);
   }

   public void remove(MutableTreeNode node)
   {
      users.remove(node);
      node.setParent(null);
   }

   public void removeFromParent()
   {
      if(null != parent)
      {
         parent.remove(this);
         parent = null;
      }
   }

   public void setParent(MutableTreeNode newParent)
   {
      parent = (RosterTreeNode) newParent;
   }

   public void setUserObject(Object object)
   {

      // ??
   }

   public Enumeration children()
   {
      return new Enumeration()
         {
            private int index = 0;

            public boolean hasMoreElements()
            {
               return users.size() > index;
            }

            public Object nextElement()
            {
               index++;
               return users.toArray(new RosterTreeNode[users.size()])[index];
            }
         };
   }

   public boolean getAllowsChildren()
   {
      return true;
   }

   public TreeNode getChildAt(int childIndex)
   {
      return users.toArray(new RosterTreeNode[users.size()])[childIndex];
   }

   public int getChildCount()
   {
      return users.size();
   }

   public int getIndex(TreeNode node)
   {
      RosterTreeNode[] allNodes = users.toArray(new RosterTreeNode[users.size()]);

      for(int i = 0; i < allNodes.length; i++)
      {
         if(allNodes[i] == node)
         {
            return i;
         }
      }

      return -1;
   }

   public TreeNode getParent()
   {
      return parent;
   }

   public boolean isLeaf()
   {
      return users.size() == 0;
   }

   @Override
   public String toString()
   {
      return getText();
   }

   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   public Presence getPresence()
   {
      return presence;
   }

   public void setPresence(Presence presence)
   {
      this.presence = presence;
   }
}
