/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.Icon;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.download.table.DownloadNodeComparator.SORT_TYPE;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadNode.java,v 1.5 2009/01/18 22:57:48 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <tkrall@tkl-soft.de>
 *
 */
public class DownloadNode implements Node
{
   private static Map<String, Download>   downloads   = null;
   private static DownloadNodeComparator  comparator  = new DownloadNodeComparator();
   private static HashMap<String, Object> allChildren = new HashMap<String, Object>();
   private static boolean                 initialized = false;
   private final String                   path;
   private TreeSet<Object>                children    = new TreeSet<Object>();

   DownloadNode(String path)
   {
      this.path = path;
   }

   DownloadNode()
   {
      this("");
      allChildren.put("", this);
   }

   public static void setDownloads(Map<String, Download> theDownloads)
   {
      if(downloads == null)
      {
         downloads = theDownloads;
         DownloadMainNode mainNode;

         for(Download curDownload : downloads.values())
         {
            String key = Integer.toString(curDownload.getId());

            mainNode = (DownloadMainNode) allChildren.get(key);
            if(mainNode == null)
            {
               mainNode = new DownloadMainNode(curDownload);
               allChildren.put(key, mainNode);
            }

            String path = curDownload.getTargetDirectory();

            if(!allChildren.containsKey(path))
            {
               DownloadNode node = new DownloadNode(path);

               allChildren.put(path, node);
            }
         }

         for(Object curObject : allChildren.values())
         {
            if(curObject.getClass() == DownloadNode.class)
            {
               ((DownloadNode) curObject).refresh();
            }
         }

         initialized = true;
      }
   }

   public Icon getConvenientIcon()
   {
      return IconManager.getInstance().getIcon("tree");
   }

   @SuppressWarnings("unchecked")
   public void refresh()
   {
      children = new TreeSet<Object>(comparator);
      if(downloads != null)
      {
         for(Object curObject : allChildren.values())
         {
            if(curObject.getClass() == DownloadNode.class)
            {
               if(path == "" && ((DownloadNode) curObject).getPath().length() > 0)
               {
                  children.add(curObject);
               }
            }
            else if(curObject.getClass() == DownloadMainNode.class &&
                       ((DownloadMainNode) curObject).getDownload().getTargetDirectory().equals(path))
            {
               children.add(curObject);
            }
         }
      }
   }

   public Object[] getChildren()
   {
      return (Object[]) children.toArray(new Object[children.size()]);
   }

   public int getChildCount()
   {
      return children.size();
   }

   public String getPath()
   {
      return path;
   }

   public boolean addChild(DownloadNode downloadNode)
   {
      return children.add(downloadNode);
   }

   public boolean addChild(DownloadMainNode downloadMainNode)
   {
      return children.add(downloadMainNode);
   }

   public boolean removeChild(DownloadNode downloadNode)
   {
      return children.remove(downloadNode);
   }

   public boolean removeChild(DownloadMainNode downloadMainNode)
   {
      return children.remove(downloadMainNode);
   }

   public static boolean isInitialized()
   {
      return initialized;
   }

   public static void setSortCriteria(SORT_TYPE sort_status, boolean isAscent)
   {
      comparator.setSortCriteria(sort_status, isAscent);
      for(Object curObject : allChildren.values())
      {
         if(curObject.getClass() == DownloadNode.class)
         {
            ((DownloadNode) curObject).refresh();
         }
      }
   }

   public static void fireDownloadDataPropertyChangeEvent(DownloadDataPropertyChangeEvent event)
   {
      if(event.getName().equals(DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED))
      {
         String           oldDir   = (String) event.getOldValue();
         String           newDir   = (String) event.getNewValue();
         Download         download = (Download) event.getSource();
         String           key      = Integer.toString(download.getId());
         DownloadMainNode mainNode = (DownloadMainNode) allChildren.get(key);
         DownloadNode     oldNode  = (DownloadNode) allChildren.get(oldDir);
         boolean          test;

         if(oldNode.getChildCount() == 1)
         {
            oldNode.removeAllChildren();
         }
         else
         {
            test                   = oldNode.removeChild(mainNode);
         }

         if(oldNode.getPath().length() > 0 && oldNode.getChildCount() == 0)
         {
            DownloadNode rootNode = (DownloadNode) allChildren.get("");

            test = rootNode.removeChild(oldNode);
            allChildren.remove(oldDir);
         }

         DownloadNode newNode = (DownloadNode) allChildren.get(newDir);

         if(newNode == null)
         {
            newNode = new DownloadNode(newDir);
            DownloadNode rootNode = (DownloadNode) allChildren.get("");

            allChildren.put(newDir, newNode);
            rootNode.addChild(newNode);
         }

         newNode.addChild(mainNode);
      }
      else if(event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED))
      {
         Download     download = (Download) event.getNewValue();
         String       thePath  = download.getTargetDirectory();
         DownloadNode node     = (DownloadNode) allChildren.get(thePath);

         if(node == null)
         {
            node = new DownloadNode(thePath);
            allChildren.put(thePath, node);
            DownloadNode rootNode = (DownloadNode) allChildren.get("");

            rootNode.addChild(node);
         }

         DownloadMainNode mainNode = new DownloadMainNode(download);

         allChildren.put(Integer.toString(download.getId()), mainNode);
         node.addChild(mainNode);
      }
      else if(event.getName().equals(DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED))
      {
         Download     download = (Download) event.getOldValue();
         String       key      = Integer.toString(download.getId());
         String       thePath  = download.getTargetDirectory();
         DownloadNode node     = (DownloadNode) allChildren.get(thePath);

         if(node == null)
         {
            return;
         }

         if(node.getChildCount() == 1)
         {
            node.removeAllChildren();
         }
         else
         {
            DownloadMainNode mainNode = (DownloadMainNode) allChildren.get(key);

            node.removeChild(mainNode);
         }

         if(node.getPath().length() > 0 && node.getChildCount() == 0)
         {
            DownloadNode rootNode = (DownloadNode) allChildren.get("");

            rootNode.removeChild(node);
            allChildren.remove(node.getPath());
         }

         allChildren.remove(key);
      }
   }

   private void removeAllChildren()
   {
      children.clear();
   }
}
