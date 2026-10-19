/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Share;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.facade.shared.FileType;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright: General Public License
 */
public class ShareNode implements Node
{
   private static final ImageIcon treeIcon;
   private static final Logger logger = LoggerFactory.getLogger(ShareNode.class);

   static
   {
      IconManager im = IconManager.getInstance();

      treeIcon = im.getIcon("tree");
   }

   private ImageIcon              leafIcon       = null;
   private Share                  share;
   private Map<String, ShareNode> children       = new HashMap<String, ShareNode>();
   private ShareNode              parent;
   private String                 path;
   private Object[]               sortedChildren = null;

   public ShareNode(ShareNode parent, Share share)
   {
      this.parent = parent;
      path        = "";
      if(parent != null)
      {
         String previousPath = getCompletePath();
         String restPath       = share.getFilename();

         while(restPath.indexOf(ApplejuiceFacade.separator) == 0)
         {
            restPath = restPath.substring(1);
         }

         if(previousPath.length() != 0 && restPath.substring(0, previousPath.length()).compareTo(previousPath) == 0)
         {
            restPath = restPath.substring(previousPath.length());
         }

         if(restPath.substring(0, 1).compareTo(ApplejuiceFacade.separator) == 0)
         {
            restPath = restPath.substring(1);
         }

         int pos = restPath.indexOf(ApplejuiceFacade.separator);

         if(pos != -1)
         {
            path = restPath.substring(0, pos);
         }
         else
         {
            this.share = share;
         }
      }
   }

   public ShareNode getParent()
   {
      return parent;
   }

   public boolean isLeaf()
   {
      return (share != null);
   }

   public String getPath()
   {
      return path;
   }

   public String getCompletePath()
   {
      if(parent != null)
      {
         String parentPath = parent.getCompletePath();

         if(parentPath.length() == 0)
         {
            return path;
         }
         else
         {
            return parentPath + ApplejuiceFacade.separator + path;
         }
      }
      else
      {
         return path;
      }
   }

   public Icon getConvenientIcon()
   {
      if(isLeaf())
      {
         if(leafIcon == null)
         {
            FileType fileType = FileType.calculatePossibleFileType(share.getShortfilename());

            leafIcon = IconManager.getInstance().getIcon(fileType.toString());
         }

         return leafIcon;
      }

      return treeIcon;
   }

   public ShareNode addChild(Share shareToAdd)
   {
      String previousPath = getCompletePath();
      String restPath       = shareToAdd.getFilename();

      while(restPath.indexOf(ApplejuiceFacade.separator) == 0)
      {
         restPath = restPath.substring(1);
      }

      restPath = restPath.substring(previousPath.length());
      int pos = restPath.indexOf(ApplejuiceFacade.separator);

      while(pos == 0)
      {
         restPath = restPath.substring(pos + 1);
         pos      = restPath.indexOf(ApplejuiceFacade.separator);
      }

      ShareNode childNode = null;

      if(pos != -1)
      {
         String tmpPath = restPath.substring(0, pos);
         String aKey    = tmpPath;

         if(children.containsKey(aKey))
         {
            childNode = children.get(aKey);
            childNode.addChild(shareToAdd);
         }
         else
         {
            childNode = new ShareNode(this, shareToAdd);
            children.put(aKey, childNode);
            sortedChildren = null;
            childNode.addChild(shareToAdd);
         }
      }
      else
      {
         String key = Integer.toString(shareToAdd.getId());

         if(!children.containsKey(key))
         {
            childNode = new ShareNode(this, shareToAdd);
            children.put(key, childNode);
            sortedChildren = null;
         }
      }

      return childNode;
   }

   public Share getShare()
   {
      return share;
   }

   public void setParent(ShareNode parentNode)
   {
      parent = parentNode;
   }

   public String toString()
   {
      if(isLeaf() && parent != null)
      {
         return getShare().getShortfilename();
      }
      else if(parent != null)
      {
         return path;
      }
      else
      {
         return "";
      }
   }

   @SuppressWarnings("unchecked")
   public Map getChildrenMap()
   {
      return children;
   }

   public void removeAllChildren()
   {
      children.clear();
      sortedChildren = null;
   }

   protected Object[] getChildren()
   {
      if(sortedChildren == null)
      {
         ShareNode[] shareNodes = children.values().toArray(new ShareNode[children.size()]);

         sortedChildren = sort(shareNodes);
      }

      return sortedChildren;
   }

   private Object[] sort(ShareNode[] childNodes)
   {
      int       n   = childNodes.length;
      ShareNode tmp;

      for(int i = 0; i < n - 1; i++)
      {
         int k = i;

         for(int j = i + 1; j < n; j++)
         {
            if(compare(childNodes[j], childNodes[k]) < 0)
            {
               k = j;
            }
         }

         tmp           = childNodes[i];
         childNodes[i] = childNodes[k];
         childNodes[k] = tmp;
      }

      return childNodes;
   }

   private int compare(ShareNode shareNode1, ShareNode shareNode2)
   {
      if(shareNode1.getShare() == null && shareNode2.getShare() != null)
      {
         return -1;
      }
      else if(shareNode1.getShare() != null && shareNode2.getShare() == null)
      {
         return 1;
      }
      else
      {
         return shareNode1.toString().compareToIgnoreCase(shareNode2.toString());
      }
   }

   @SuppressWarnings("unchecked")
   public void setPriority(int priorityTotal)
   {
      if(isLeaf())
      {
         try
         {
            AppleJuiceClient.getAjFacade().setPriority(share, Integer.valueOf(priorityTotal));
         }
         catch(IllegalArgumentException e)
         {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
         }
      }
      else
      {
         Iterator it = children.values().iterator();

         while(it.hasNext())
         {
            ((ShareNode) it.next()).setPriority(priorityTotal);
         }
      }
   }
}
