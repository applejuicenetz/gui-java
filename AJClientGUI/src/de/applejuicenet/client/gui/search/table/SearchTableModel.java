/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;

public class SearchTableModel extends AbstractTableModel implements SortableTableModel<SearchEntry>
{
   final static String[]                                  COL_NAMES = {"Dateiname", "Groesze", "Anzahl"};
   @SuppressWarnings("unchecked")
   static protected Class[]                               cTypes = {SearchEntry.class, Long.class, Integer.class};
   private Logger                                         logger;
   private final Search                                   search;
   private TableSorter<SearchEntry>                       sorter;

   public SearchTableModel(Search search)
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
      return cTypes[column];
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
            return entry.getGroesse();

         case 2:
         {
            long anzahl = 0;

            for(FileName curFileName : entry.getFileNames())
            {
               anzahl += curFileName.getHaeufigkeit();
            }

            return anzahl;
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
         return ((SearchEntry) getValueAt(row, column)).getFileNames()[0].getDateiName();
      }
   }
}
