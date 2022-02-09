/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.fassade.entity.Share;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/share/table/DateiListeTableModel.java,v 1.7 2009/01/18 22:57:48 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class DateiListeTableModel extends AbstractTableModel
{
   final static String[]      COL_NAMES      = {"Name", "Groesze"};
   private Map<String, Share> dateien        = new HashMap<String, Share>();
   private Share[]            sortedChildren;

   public Object getRow(int row)
   {
      if(row < dateien.size())
      {
         return sortedChildren[row];
      }

      return null;
   }

   private Share[] sortChildren()
   {
      Share[] children                       = dateien.values().toArray(new Share[dateien.size()]);
      Share   tmp;
      int     n                              = children.length;

      for(int i = 0; i < n - 1; i++)
      {
         int k = i;

         for(int j = i + 1; j < n; j++)
         {
            if((((Share) children[j]).getShortfilename().compareToIgnoreCase(((Share) children[k]).getShortfilename())) < 0)
            {
               k = j;
            }
         }

         tmp         = children[i];
         children[i] = children[k];
         children[k] = tmp;
      }

      sortedChildren = children;
      return sortedChildren;
   }

   public Object getValueAt(int row, int column)
   {
      if(row >= dateien.size())
      {
         return "";
      }

      Share share = sortedChildren[row];

      if(share == null)
      {
         return "";
      }

      switch(column)
      {

         case 0:
            return share.getShortfilename();

         case 1:
            return Long.toString(share.getSize());

         default:
            return "Fehler";
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
      return dateien.size();
   }

   @SuppressWarnings("unchecked")
   public Class getClass(int column)
   {
      return String.class;
   }

   public void addNodes(ShareNode shareNode)
   {
      if(shareNode.isLeaf())
      {
         dateien.put(Integer.toString(shareNode.getShare().getId()), shareNode.getShare());
      }
      else
      {
         for(Object curShareNode : shareNode.getChildrenMap().values())
         {
            addNodes((ShareNode) curShareNode);
         }
      }

      sortChildren();
      fireTableDataChanged();
   }

   public void removeRow(int row)
   {
      Object toRemove = getRow(row);

      if(toRemove != null)
      {
         dateien.remove(Integer.toString(((Share) toRemove).getId()));
         sortChildren();
         fireTableDataChanged();
      }
   }

   public Share[] getShares()
   {
      return dateien.values().toArray(new Share[dateien.values().size()]);
   }
}
