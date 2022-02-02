/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.share.table;

import java.text.SimpleDateFormat;

import java.util.Date;

import de.applejuicenet.client.gui.components.treetable.AbstractTreeTableModel;
import de.applejuicenet.client.gui.components.treetable.TreeTableModel;
import de.applejuicenet.client.gui.download.table.DownloadsTableModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/ShareTableModel.java,v 1.1 2009/01/27 07:55:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class ShareTableModel extends AbstractTreeTableModel
{
   static protected String[] cNames = {"Name", "Size", "Type", "Last asked", "downloadrequests", "searchrequests"};
   @SuppressWarnings("unchecked")
   public static final Class[] CLASS_TYPES                               = 
                                                                            {
                                                                               TreeTableModel.class, String.class, Integer.class,
                                                                               String.class, Long.class, Long.class
                                                                            };
   private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

   public ShareTableModel(ShareNode rootNode)
   {
      super(rootNode);
   }

   public int getChildCount(Object node)
   {
      Object[] children = getChildren(node);

      return (children == null) ? 0 : children.length;
   }

   public Object getChild(Object node, int i)
   {
      return getChildren(node)[i];
   }

   public boolean isLeaf(Object node)
   {
      return ((ShareNode) node).isLeaf();
   }

   public int getColumnCount()
   {
      return cNames.length;
   }

   public ShareNode getRootNode()
   {
      return (ShareNode) getRoot();
   }

   public String getColumnName(int column)
   {
      return cNames[column];
   }

   @SuppressWarnings("unchecked")
   public Class getColumnClass(int column)
   {
      return CLASS_TYPES[column];
   }

   public Object getValueAt(Object node, int column)
   {
      ShareNode shareNode = (ShareNode) node;

      try
      {
         switch(column)
         {

            case 0:
            {
               if(shareNode.isLeaf() && shareNode != getRoot())
               {
                  return shareNode.getShare().getFilename();
               }
               else
               {
                  return "";
               }
            }

            case 1:
            {
               if(shareNode.isLeaf() && shareNode != getRoot())
               {
                  return DownloadsTableModel.parseGroesse(shareNode.getShare().getSize());
               }
               else
               {
                  return "";
               }
            }

            case 2:
            {
               if(shareNode.isLeaf() && shareNode != getRoot())
               {
                  return new Integer(shareNode.getShare().getPrioritaet());
               }
               else
               {
                  return null;
               }
            }

            case 3:
            {
               if(shareNode.isLeaf() && shareNode != getRoot())
               {
                  return formatter.format(new Date(shareNode.getShare().getLastAsked()));
               }
               else
               {
                  return null;
               }
            }

            case 4:
            {
               if(shareNode.isLeaf() && shareNode != getRoot())
               {
                  return new Long(shareNode.getShare().getAskCount());
               }
               else
               {
                  return null;
               }
            }

            case 5:
            {
               if(shareNode.isLeaf() && shareNode != getRoot())
               {
                  return new Long(shareNode.getShare().getSearchCount());
               }
               else
               {
                  return null;
               }
            }

            default:
               return null;
         }
      }
      catch(SecurityException se)
      {
         ;
      }

      return null;
   }

   protected Object[] getChildren(Object node)
   {
      return ((ShareNode) node).getChildren();
   }
}
