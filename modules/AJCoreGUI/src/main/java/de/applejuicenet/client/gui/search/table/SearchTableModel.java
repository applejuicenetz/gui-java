/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search.table;

import de.applejuicenet.client.facade.entity.FileName;
import de.applejuicenet.client.facade.entity.Search;
import de.applejuicenet.client.facade.entity.SearchEntry;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class SearchTableModel extends AbstractTableModel implements SortableTableModel<SearchEntry>
{
   static final String[] COL_KEYS = {"mainform.searchs.col0caption", "mainform.searchs.col1caption", "mainform.searchs.col2caption"};
   final static String[]                                     COL_NAMES   = {"File name", "Size", "Count"};
   @SuppressWarnings("unchecked")
   public static final Class[]                               CLASS_TYPES = {SearchEntry.class, Long.class, Integer.class};
   private Search                                            search;
   private TableSorter<SearchEntry>                          sorter;

   public SearchTableModel(Search search)
   {
      this.search = search;
   }

   public void setSearchSnapshot(Search search)
   {
      this.search = search;
   }

   public int getColumnCount()
   {
      return COL_NAMES.length;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Class getColumnClass(int column)
   {
      return CLASS_TYPES[column];
   }

   public int getRowCount()
   {
      return search.getSearchEntries().size();
   }

   public SearchEntry getRow(int rowIndex)
   {
      return search.getSearchEntries().get(rowIndex);
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      SearchEntry entry = search.getSearchEntries().get(rowIndex);

      switch(columnIndex)
      {

         case 0:
            return entry;

         case 1:
            return entry.getSize();

         case 2:
         {
            long count = 0;

            for(FileName curFileName : entry.getFileNames())
            {
               count += curFileName.getFrequency();
            }

            return count;
         }
      }

      return null;
   }

   public List<SearchEntry> getContent()
   {
      return search.getSearchEntries();
   }

   public void sortByColumn(int column, boolean isAscent)
   {
      if(sorter == null)
      {
         sorter = new TableSorter<SearchEntry>(this);
      }

      sorter.sort(column, isAscent);
      fireTableDataChanged();

   }

   public Object getValueForSortAt(int row, int column)
   {
      if(column != 0)
      {
         return getValueAt(row, column);
      }
      else
      {
         return ((SearchEntry) getValueAt(row, column)).getFileNames()[0].getFileName();
      }
   }

   public void forceResort()
   {
      if(null != sorter)
      {
         sorter.forceResort();
      }
   }
}
