/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.table;

import java.util.List;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/SortableTableModel.java,v 1.3 2009/01/22 22:18:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
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
