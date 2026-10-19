/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.share.table;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.facade.entity.Share;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: General Public License
 */
public class FileListTableModel extends AbstractTableModel
{
   static final String[] COL_KEYS = {"linklist.files.col0caption", "linklist.files.col1caption"};
   final static String[]      COL_NAMES      = {"Name", "Size"};
   private Map<String, Share> files        = new HashMap<String, Share>();
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
      if(row < files.size())
      {
         return sortedChildren[row];
      }

      return null;
   }

   private Share[] sortChildren()
   {
      Share[] children = files.values().toArray(new Share[files.size()]);
      Comparator<Share> byName = Comparator.comparing(Share::getShortfilename, String.CASE_INSENSITIVE_ORDER);

      Arrays.sort(children, descending ? byName.reversed() : byName);
      sortedChildren = children;
      return sortedChildren;
   }

   public Object getValueAt(int row, int column)
   {
      if(row >= files.size())
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
            return GuiText.textOr("javagui.error", "Error");
      }
   }

   public int getColumnCount()
   {
      return COL_NAMES.length;
   }

   public String getColumnName(int index)
   {
      return GuiText.textOr(COL_KEYS[index], COL_NAMES[index]);
   }

   public int getRowCount()
   {
      return files.size();
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
         files.put(Integer.toString(shareNode.getShare().getId()), shareNode.getShare());
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
         files.remove(Integer.toString(((Share) toRemove).getId()));
         sortChildren();
         fireTableDataChanged();
      }
   }

   public Share[] getShares()
   {
      return files.values().toArray(new Share[files.values().size()]);
   }
}
