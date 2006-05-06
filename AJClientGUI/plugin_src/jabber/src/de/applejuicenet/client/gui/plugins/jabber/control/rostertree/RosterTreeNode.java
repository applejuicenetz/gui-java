/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.control.rostertree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jivesoftware.smack.packet.Presence;

public class RosterTreeNode implements MutableTreeNode
{
   private List<RosterTreeNode> users    = new ArrayList<RosterTreeNode>();
   private RosterTreeNode       parent   = null;
   private String               text     = "";
   private Presence             presence = null;

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
      users.add(index, (RosterTreeNode) child);
      child.setParent(this);
   }

   public void remove(int index)
   {
      remove(users.get(index));
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
               return users.get(index);
            }
         };
   }

   public boolean getAllowsChildren()
   {
      return true;
   }

   public TreeNode getChildAt(int childIndex)
   {
      return users.get(childIndex);
   }

   public int getChildCount()
   {
      return users.size();
   }

   public int getIndex(TreeNode node)
   {
      return users.indexOf(node);
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
