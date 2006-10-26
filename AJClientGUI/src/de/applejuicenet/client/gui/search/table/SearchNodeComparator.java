package de.applejuicenet.client.gui.search.table;

import java.util.Comparator;

import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.SearchEntry;

public class SearchNodeComparator implements Comparator<SearchNode>
{
   public static enum SORT_TYPE
   {SORT_NO_SORT, SORT_FILENAME, SORT_GROESSE, SORT_ANZAHL;}
   private boolean   isAscending = true;
   private SORT_TYPE sortType = SORT_TYPE.SORT_FILENAME;

   public SearchNodeComparator()
   {
   }

   void setSortCriteria(SORT_TYPE sortType, boolean ascending)
   {
      this.sortType = sortType;
      isAscending   = ascending;
   }

   public int compare(SearchNode toCompare1, SearchNode toCompare2)
   {
      Object o1 = null;
      Object o2 = null;

      if(sortType == SORT_TYPE.SORT_FILENAME)
      {
         SearchEntry entry1 = (SearchEntry) toCompare1.getValueObject();
         FileName[]  filenames   = entry1.getFileNames();
         int         haeufigkeit = 0;
         String      dateiname   = "";

         for(int i = 0; i < filenames.length; i++)
         {
            if(filenames[i].getHaeufigkeit() > haeufigkeit)
            {
               haeufigkeit = filenames[i].getHaeufigkeit();
               dateiname   = filenames[i].getDateiName();
            }
         }

         o1 = dateiname;
         SearchEntry entry2 = (SearchEntry) toCompare2.getValueObject();

         filenames   = entry2.getFileNames();
         haeufigkeit = 0;
         dateiname   = "";
         for(int i = 0; i < filenames.length; i++)
         {
            if(filenames[i].getHaeufigkeit() > haeufigkeit)
            {
               haeufigkeit = filenames[i].getHaeufigkeit();
               dateiname   = filenames[i].getDateiName();
            }
         }

         o2 = dateiname;
      }
      else if(sortType == SORT_TYPE.SORT_GROESSE)
      {
         SearchEntry entry1 = (SearchEntry) toCompare1.getValueObject();

         o1 = new Long(entry1.getGroesse());
         SearchEntry entry2 = (SearchEntry) toCompare2.getValueObject();

         o2 = new Long(entry2.getGroesse());
      }
      else if(sortType == SORT_TYPE.SORT_ANZAHL)
      {
         SearchEntry entry1 = (SearchEntry) toCompare1.getValueObject();
         FileName[]  filenames   = entry1.getFileNames();
         int         haeufigkeit = 0;

         for(int i = 0; i < filenames.length; i++)
         {
            haeufigkeit += filenames[i].getHaeufigkeit();
         }

         o1 = new Integer(haeufigkeit);
         SearchEntry entry2 = (SearchEntry) ((SearchNode) toCompare2).getValueObject();

         filenames   = entry2.getFileNames();
         haeufigkeit = 0;
         for(int i = 0; i < filenames.length; i++)
         {
            haeufigkeit += filenames[i].getHaeufigkeit();
         }

         o2 = new Integer(haeufigkeit);
      }

      int result;

      if(o1 == null && o2 == null)
      {
         result = 0;
      }
      else if(o1 == null)
      {
         result = -1;
      }
      else if(o2 == null)
      {
         result = 1;
      }
      else
      {
         if(o1.getClass().getSuperclass() == Number.class)
         {
            result = compare((Number) o1, (Number) o2);
         }
         else if(o1.getClass() == Boolean.class)
         {
            result = compare((Boolean) o1, (Boolean) o2);
         }
         else
         {
            result = ((String) o1).compareToIgnoreCase((String) o2);
         }
      }

      if(result == 0)
      {
         result = 1;
      }

      return isAscending ? result : result * -1;
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
