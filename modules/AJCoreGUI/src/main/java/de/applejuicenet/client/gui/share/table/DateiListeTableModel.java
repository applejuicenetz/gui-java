/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.fassade.entity.Share;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.Comparator;
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
   private boolean            descending     = false;

   public boolean isDescending()
   {
      return descending;
   }

   public void setDescending(boolean descending)
   {
      this.descending = descending;
      sortChildren();
      fireTableDataChanged();
   }

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
      Share[] children = dateien.values().toArray(new Share[dateien.size()]);
      Comparator<Share> byName = Comparator.comparing(Share::getShortfilename, String.CASE_INSENSITIVE_ORDER);

      Arrays.sort(children, descending ? byName.reversed() : byName);
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
            return share.getSize();

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

   @Override
   public Class<?> getColumnClass(int column)
   {
      return column == 1 ? Long.class : String.class;
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
