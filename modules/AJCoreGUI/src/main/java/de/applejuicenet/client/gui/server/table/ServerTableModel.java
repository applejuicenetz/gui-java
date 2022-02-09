/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.server.table;

import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/server/table/ServerTableModel.java,v 1.11 2009/01/27 07:55:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
public class ServerTableModel extends AbstractTableModel implements SortableTableModel<Server>
{
   final static String[] COL_NAMES = {"Name", "DynIP", "Port", "Verbindungsversuche", "Letztes mal online"};
   @SuppressWarnings("unchecked")
   public static final Class[] CLASS_TYPES                               = 
                                                                           {
                                                                              Server.class, String.class, Integer.class,
                                                                              Integer.class, Date.class
                                                                           };
   private TableSorter<Server> sorter;
   private List<Server>        servers = new ArrayList<Server>();

   public List<Server> getContent()
   {
      return servers;
   }

   @Override
   public Class<? > getColumnClass(int columnIndex)
   {
      return CLASS_TYPES[columnIndex];
   }

   public Server getRow(int row)
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
            return server;

         case 1:
            return server.getHost();

         case 2:
            return Integer.parseInt(server.getPort());

         case 3:
            return server.getVersuche();

         case 4:
            return new Date(server.getTimeLastSeen());

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

   public void forceResort()
   {
      if(null != sorter)
      {
         sorter.forceResort();
      }
   }

   public boolean setTable(Map<String, Server> changedContent)
   {
      boolean changed = changedContent.size() > 0;

      //alte Server entfernen
      String            suchKey  = null;
      ArrayList<Server> toRemove = new ArrayList<Server>();
      int               count    = servers.size();

      if(count > 0)
      {
         Server curServer;

         for(int x = count - 1; x >= 0; x--)
         {
            curServer = servers.get(x);
            if(!changedContent.containsKey(Integer.toString(curServer.getId())))
            {
               servers.remove(x);
               changed = true;
            }
         }
      }

      for(Server curServer : changedContent.values())
      {
         if(servers.indexOf(curServer) == -1)
         { // Der Server ist neu
            servers.add(curServer);
            changed = true;
         }
      }

      return changed;
   }

   public Object getValueForSortAt(int row, int column)
   {
      if(column == 0)
      {
         return servers.get(row).getName();
      }
      else
      {
         return getValueAt(row, column);
      }
   }
}
