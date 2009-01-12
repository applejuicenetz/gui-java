/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.components.treetable.AbstractTreeTableModel;
import de.applejuicenet.client.gui.components.treetable.TreeTableModel;
import de.applejuicenet.client.gui.download.table.DownloadModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/search/table/Attic/SearchResultTableModel.java,v 1.5 2009/01/12 07:45:46 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class SearchResultTableModel extends AbstractTreeTableModel
{
   final static String[]                                  COL_NAMES = {"Dateiname", "Groesze", "Anzahl"};
   @SuppressWarnings("unchecked")
   static protected Class[]                               cTypes = {TreeTableModel.class, String.class, String.class};
   private Logger                                         logger;

   public SearchResultTableModel(Search aSearch)
   {
      super(new SearchNode(aSearch));
      logger = Logger.getLogger(getClass());
   }

   protected Object[] getChildren(Object node)
   {
      try
      {
         if(node.getClass() == SearchNode.class)
         {
            return ((SearchNode) node).getChildren();
         }
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }

      return null;
   }

   public int getChildCount(Object node)
   {
      try
      {
         if(node.getClass() == SearchNode.class)
         {
            return ((SearchNode) node).getChildCount();
         }
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }

      return 0;
   }

   public Object getChild(Object node, int i)
   {
      try
      {
         Object[] obj = getChildren(node);

         if(obj == null || i > obj.length - 1)
         {
            return null;
         }

         return obj[i];
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }

         return null;
      }
   }

   public int getColumnCount()
   {
      return COL_NAMES.length;
   }

   public String getColumnName(int index)
   {
      return COL_NAMES[index];
   }

   @SuppressWarnings("unchecked")
   public Class getColumnClass(int column)
   {
      return cTypes[column];
   }

   public Object getValueAt(Object node, int column)
   {
      try
      {
         if(node.getClass() == WaitNode.class)
         {
            return "";
         }
         else if(node.getClass() == SearchNode.class)
         {
            Object o = ((SearchNode) node).getValueObject();

            if(((SearchNode) node).getNodeType() == SearchNode.ROOT_NODE)
            {
               return "";
            }
            else
            {
               SearchEntry entry = (SearchEntry) o;

               switch(column)
               {

                  case 0:
                  {
                     FileName[] filenames   = entry.getFileNames();
                     int        haeufigkeit = 0;
                     String     dateiname   = "";

                     for(int i = 0; i < filenames.length; i++)
                     {
                        if(filenames[i].getHaeufigkeit() > haeufigkeit)
                        {
                           haeufigkeit = filenames[i].getHaeufigkeit();
                           dateiname   = filenames[i].getDateiName();
                        }
                     }

                     return dateiname;
                  }

                  case 1:
                     return DownloadModel.parseGroesse(entry.getGroesse());

                  case 2:
                  {
                     FileName[] filenames   = entry.getFileNames();
                     int        haeufigkeit = 0;

                     for(int i = 0; i < filenames.length; i++)
                     {
                        haeufigkeit += filenames[i].getHaeufigkeit();
                     }

                     return Integer.toString(haeufigkeit);
                  }

                  default:
                     return "";
               }
            }
         }
         else if(node instanceof FileName)
         {
            FileName filename = (FileName) node;

            switch(column)
            {

               case 0:
                  return filename.getDateiName();

               case 1:
                  return "";

               case 2:
                  return Integer.toString(filename.getHaeufigkeit());

               default:
                  return "";
            }
         }
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }

      return null;
   }
}
