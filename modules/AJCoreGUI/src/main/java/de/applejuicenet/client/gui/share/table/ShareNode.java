/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share.table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.shared.FileType;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/ShareNode.java,v 1.10 2009/01/18 22:57:48 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
public class ShareNode implements Node
{
   private static ImageIcon treeIcon;
   private static Logger    logger = Logger.getLogger(ShareNode.class);

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
         String bisherigerPath = getCompletePath();
         String restPath       = share.getFilename();

         while(restPath.indexOf(ApplejuiceFassade.separator) == 0)
         {
            restPath = restPath.substring(1);
         }

         if(bisherigerPath.length() != 0 && restPath.substring(0, bisherigerPath.length()).compareTo(bisherigerPath) == 0)
         {
            restPath = restPath.substring(bisherigerPath.length());
         }

         if(restPath.substring(0, 1).compareTo(ApplejuiceFassade.separator) == 0)
         {
            restPath = restPath.substring(1);
         }

         int pos = restPath.indexOf(ApplejuiceFassade.separator);

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
            return parentPath + ApplejuiceFassade.separator + path;
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
      String bisherigerPath = getCompletePath();
      String restPath       = shareToAdd.getFilename();

      while(restPath.indexOf(ApplejuiceFassade.separator) == 0)
      {
         restPath = restPath.substring(1);
      }

      restPath = restPath.substring(bisherigerPath.length());
      int pos = restPath.indexOf(ApplejuiceFassade.separator);

      while(pos == 0)
      {
         restPath = restPath.substring(pos + 1);
         pos      = restPath.indexOf(ApplejuiceFassade.separator);
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
   public void setPriority(int prio)
   {
      if(isLeaf())
      {
         try
         {
            AppleJuiceClient.getAjFassade().setPrioritaet(share, new Integer(prio));
         }
         catch(IllegalArgumentException e)
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
      else
      {
         Iterator it = children.values().iterator();

         while(it.hasNext())
         {
            ((ShareNode) it.next()).setPriority(prio);
         }
      }
   }
}
