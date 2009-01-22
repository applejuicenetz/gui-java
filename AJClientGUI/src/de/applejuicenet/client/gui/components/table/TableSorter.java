/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.table;

import java.util.Date;
import java.util.List;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/TableSorter.java,v 1.4 2009/01/22 22:18:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class TableSorter<T>
{
   private SortableTableModel<T> model;
   private int                   lastColumn = -1;
   private boolean               lastAscent = true;

   public TableSorter(SortableTableModel<T> model)
   {
      this.model = model;
   }

   public void forceResort()
   {
      if(lastColumn == -1)
      {
         return;
      }

      int     curColumn = lastColumn;
      boolean curAscent = lastAscent;

      lastColumn = -1;
      sort(curColumn, curAscent);
   }

   public void sort(int column, boolean isAscent)
   {
      if(lastColumn != -1 && lastColumn == column && lastAscent == isAscent)
      {
         return;
      }

      lastColumn = column;
      lastAscent = isAscent;
      List<T> content = model.getContent();
      int     n = content.size();

      for(int i = 0; i < n - 1; i++)
      {
         int k = i;

         for(int j = i + 1; j < n; j++)
         {
            if(isAscent)
            {
               if(compare(column, j, k) < 0)
               {
                  k = j;
               }
            }
            else
            {
               if(compare(column, j, k) > 0)
               {
                  k = j;
               }
            }
         }

         T tmp = content.get(i);

         content.set(i, content.get(k));
         content.set(k, tmp);
      }
   }

   public int compare(int column, int row1, int row2)
   {
      Object o1 = model.getValueForSortAt(row1, column);
      Object o2 = model.getValueForSortAt(row2, column);

      if(o1 == null && o2 == null)
      {
         return 0;
      }
      else if(o1 == null)
      {
         return -1;
      }
      else if(o2 == null)
      {
         return 1;
      }
      else
      {
         if(o1.getClass().getSuperclass() == Number.class)
         {
            return compare((Number) o1, (Number) o2);
         }
         else if(o1.getClass() == String.class)
         {
            return ((String) o1).compareToIgnoreCase((String) o2);
         }
         else if(o1.getClass() == Date.class)
         {
            return compare((Date) o1, (Date) o2);
         }
         else if(o1.getClass() == Boolean.class)
         {
            return compare((Boolean) o1, (Boolean) o2);
         }
         else
         {
            return o1.toString().compareToIgnoreCase(o2.toString());
         }
      }
   }

   public int compare(Number o1, Number o2)
   {
      double n1 = o1.doubleValue();
      double n2 = o2.doubleValue();

      if(n1 < n2)
      {
         return -1;
      }
      else if(n1 > n2)
      {
         return 1;
      }
      else
      {
         return 0;
      }
   }

   public int compare(Date o1, Date o2)
   {
      long n1 = o1.getTime();
      long n2 = o2.getTime();

      if(n1 < n2)
      {
         return -1;
      }
      else if(n1 > n2)
      {
         return 1;
      }
      else
      {
         return 0;
      }
   }

   public int compare(Boolean o1, Boolean o2)
   {
      boolean b1 = o1.booleanValue();
      boolean b2 = o2.booleanValue();

      if(b1 == b2)
      {
         return 0;
      }
      else if(b1)
      {
         return 1;
      }
      else
      {
         return -1;
      }
   }
}
