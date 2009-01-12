/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.server.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/server/table/ServerTableModel.java,v 1.9 2009/01/12 07:45:46 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class ServerTableModel extends AbstractTableModel implements SortableTableModel<Server>
{
   final static String[]       COL_NAMES = {"Name", "DynIP", "Port", "Verbindungsversuche", "Letztes mal online"};
   private TableSorter<Server> sorter;
   private List<Server>        servers = new ArrayList<Server>();

   public List<Server> getContent()
   {
      return servers;
   }

   public Object getRow(int row)
   {
      if((servers != null) && (row < servers.size()))
      {
         return servers.get(row);
      }

      return null;
   }

   public void sortByColumn(int column, boolean isAscent)
   {
      if(sorter == null)
      {
         sorter = new TableSorter<Server>(this);
      }

      sorter.sort(column, isAscent);
      fireTableDataChanged();
   }

   public Object getValueAt(int row, int column)
   {
      if((servers == null) || (row >= servers.size()))
      {
         return "";
      }

      Server server = servers.get(row);

      if(server == null)
      {
         return "";
      }

      switch(column)
      {

         case 0:
            return server.getName();

         case 1:
            return server.getHost();

         case 2:
            return server.getPort();

         case 3:
            return new Integer(server.getVersuche());

         case 4:
            return server.getTimeLastSeenAsString();

         default:
            return "";
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

   public int getRowCount()
   {
      if(servers == null)
      {
         return 0;
      }

      return servers.size();
   }

   @SuppressWarnings("unchecked")
   public Class getClass(int index)
   {
      if(index == 3)
      {
         return Number.class;
      }
      else
      {
         return String.class;
      }
   }

   public void setTable(Map<String, Server> changedContent)
   {

      //alte Server entfernen
      String            suchKey  = null;
      ArrayList<Server> toRemove = new ArrayList<Server>();

      for(Server curServer : servers)
      {
         if(!changedContent.containsKey(Integer.toString(curServer.getId())))
         {
            toRemove.add(curServer);
         }
      }

      for(Server curServer : toRemove)
      {
         servers.remove(curServer);
      }

      for(Server curServer : changedContent.values())
      {
         int index = servers.indexOf(curServer);

         if(index == -1)
         { // Der Server ist neu
            servers.add(curServer);
         }
      }

      this.fireTableDataChanged();
   }

   public Object getValueForSortAt(int row, int column)
   {
      if(column != 4)
      {
         return getValueAt(row, column);
      }
      else
      {
         if(servers == null || row >= servers.size())
         {
            return "";
         }

         Server server = servers.get(row);

         if(server == null)
         {
            return "";
         }

         return new Long(server.getTimeLastSeen());
      }
   }
}
