package de.applejuicenet.client.gui.options.directorytree;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.entity.Directory;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.components.tree.ApplejuiceNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: General Public License
 */
public class DirectoryChooserNode extends DefaultMutableTreeNode implements Node, ApplejuiceNode
{
   private Directory                  directory;
   private List<DirectoryChooserNode> children = null;
   private DirectoryChooserNode       parent;

   public DirectoryChooserNode(DirectoryChooserNode parent, Directory directory)
   {
      this.parent = parent;
      this.directory = directory;
   }

   public DirectoryChooserNode()
   {
      this.parent = null;
      this.directory = null;
      children = new ArrayList<DirectoryChooserNode>();
      try
      {
         List<Directory> directories = AppleJuiceClient.getAjFacade().getDirectories(null);

         for(Directory curDirectory : directories)
         {
            addChild(curDirectory);
         }
      }
      catch(IllegalArgumentException e)
      {

         // should not happen
         throw new RuntimeException(e);
      }
   }

   public int getChildCount()
   {
      return getChildren().length;
   }

   public TreeNode getParent()
   {
      return parent;
   }

   public boolean isLeaf()
   {
      return false;
   }

   public Icon getConvenientIcon()
   {
      if(directory == null)
      {
         return null; //rootNode
      }

      IconManager im = IconManager.getInstance();

      switch(directory.getType())
      {

         case Directory.TYPE_DESKTOP:
            return im.getIcon("server");

         case Directory.TYPE_DISKETTE:
            return im.getIcon("diskette");

         case Directory.TYPE_DRIVE:
            return im.getIcon("laufwerk");

         case Directory.TYPE_FOLDER:
            return im.getIcon("tree");

         case Directory.TYPE_COMPUTER:
            return im.getIcon("server");

         default:
            return null;
      }
   }

   public Directory getDirectory()
   {
      return directory;
   }

   public String toString()
   {
      if(directory == null)
      {
         return "rootNode";
      }

      return directory.getName();
   }

   public ApplejuiceNode addChild(Directory childDirectory)
   {
      if(children == null)
      {
         children = new ArrayList<DirectoryChooserNode>();
      }

      DirectoryChooserNode childNode = new DirectoryChooserNode(this, childDirectory);

      children.add(childNode);
      return childNode;
   }

   private void sortChildren()
   {
      if(children == null || children.size() < 2)
      {
         return;
      }

      int n = children.size();
      int k;

      for(int i = 0; i < n - 1; i++)
      {
         k = i;
         for(int j = i + 1; j < n; j++)
         {
            if(!children.get(j).getDirectory().isFileSystem())
            {
               continue;
            }

            if(children.get(j).toString().compareToIgnoreCase(children.get(k).toString()) < 0)
            {
               k = j;
            }
         }

         DirectoryChooserNode tmp = children.get(i);

         children.set(i, children.get(k));
         children.set(k, tmp);
      }
   }

   protected Object[] getChildren()
   {
      if(children == null)
      {
         children = new ArrayList<DirectoryChooserNode>();
         List<Directory> directories;

         try
         {
            directories = AppleJuiceClient.getAjFacade().getDirectories(directory.getPath());
            for(Directory curDirectory : directories)
            {
               addChild(curDirectory);
            }

            sortChildren();
         }
         catch(IllegalArgumentException e)
         {

            // should not happen
            throw new RuntimeException(e);
         }
      }

      return children.toArray(new DirectoryChooserNode[children.size()]);
   }
}
