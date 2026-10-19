/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.table;

import java.util.List;

/**
 * Copyright: General Public License
 */
public interface SortableTableModel<T>
{
   int getRowCount();

   List<T> getContent();

   Object getValueForSortAt(int row, int column);

   @SuppressWarnings("unchecked")
   Class getColumnClass(int column);

   void sortByColumn(int column, boolean isAscent);
   
   void forceResort();
}
