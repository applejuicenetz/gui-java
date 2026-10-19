/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.download.table;

import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadsTableModel extends AbstractTableModel implements LanguageListener, SortableTableModel<Download>
{

   @SuppressWarnings("unchecked")
   public static final Class[]                               CLASS_TYPES = 
                                                                           {
                                                                              Download.class, String.class, Long.class,
                                                                              Long.class, Integer.class, String.class,
                                                                              Double.class, Long.class, Integer.class,
                                                                              String.class
                                                                           };

   //Download-Stati
   public static String          searchLabel                  = "";
   public static String          load                   = "";
   public static String          noSpace               = "";
   public static String          complete           = "";
   public static String          errorOnCompleting = "";
   public static String          finished                  = "";
   public static String          cancel               = "";
   public static String          aborted             = "";
   public static String          dataBeingCreated        = "";
   public static String          paused                = "";
   private TableSorter<Download> sorter;
   private List<Download>        downloads               = new ArrayList<Download>();

   public DownloadsTableModel()
   {
      super();
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   public int getColumnCount()
   {
      return CLASS_TYPES.length;
   }

   @Override
   public Class<?> getColumnClass(int columnIndex)
   {
      return CLASS_TYPES[columnIndex];
   }

   public int getRowCount()
   {
      return downloads.size();
   }

   public Download getRow(int row)
   {
      return downloads.get(row);
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      Download download = downloads.get(rowIndex);

      switch(columnIndex)
      {

         case 0:
            return download;

         case 1:
            return getStatusAsString(download);

         case 2:
            return download.getSize();

         case 3:
            if(download.getStatus() == Download.FINISHED || download.getStatus() == Download.FINISHING)
            {
               return download.getSize();
            }

            return download.getAlreadyLoaded();

         case 4:
            return download.getSpeedInBytes();

         case 5:
            return download.getRemainingTimeAsString();

         case 6:
            if(download.getStatus() == Download.FINISHED || download.getStatus() == Download.FINISHING)
            {
               return 100d;
            }

            return download.getPercentLoaded();

         case 7:
            if(download.getStatus() == Download.FINISHED || download.getStatus() == Download.FINISHING)
            {
               return 0L;
            }

            return download.getSize() - download.getAlreadyLoaded();

         case 8:
            if(download.getStatus() == Download.FINISHED || download.getStatus() == Download.FINISHING)
            {
               return -1;
            }

            return download.getPowerDownload();

         case 9:
            return download.getTargetDirectory();

         default:
            return null;
      }
   }

   public boolean setDownloads(Map<Integer, Download> downloadMap)
   {
      boolean change = false;
      downloads.replaceAll(old -> downloadMap.getOrDefault(old.getId(), old));

      for(Download curDownload : downloadMap.values())
      {
         change = true;
         if(!downloads.contains(curDownload))
         {
            downloads.add(curDownload);
         }
      }

      int      count      = downloads.size();
      Download anDownload;

      if(count > 0)
      {
         for(int x = count - 1; x >= 0; x--)
         {
            anDownload = downloads.get(x);
            if(!downloadMap.containsKey(anDownload.getId()))
            {
               downloads.remove(x);
               change = true;
            }
         }
      }

      return change;
   }

   public static String parseSize(long size)
   {
      if(size == 0)
      {
         return "";
      }
      if(size < 1024)
      {
         return size + " Bytes";
      }

      String[] units = {"Bytes", "KB", "MB", "GB", "TB", "PB", "EB"};
      double share = size;
      int unit = 0;
      while(share >= 1024 && unit < units.length - 1)
      {
         share /= 1024;
         unit++;
      }
      String result = Double.toString(share);
      int dot = result.indexOf('.');
      if(dot != -1 && dot + 3 < result.length())
      {
         result = result.substring(0, dot + 3);
      }
      return result.replace('.', ',') + " " + units[unit];
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      searchLabel                  = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestatlook");
      load                   = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestattransfer");
      noSpace               = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestat1");
      complete           = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestat12");
      finished                  = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestat14");
      cancel               = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestat15");
      aborted             = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestat17");
      errorOnCompleting = languageSelector.getFirstAttributeByTagName("javagui.downloadform.errorfinalizing");
      dataBeingCreated        = languageSelector.getFirstAttributeByTagName("javagui.downloadform.databeingcreated");
      paused                = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat13");
   }

   public static String getStatusAsString(Download download)
   {
      try
      {
         switch(download.getStatus())
         {

            case Download.PAUSED:
               return paused;

            case Download.CANCEL:
               return cancel;

            case Download.ABORTED_BY_PEER:
               return aborted;

            case Download.FINISHED:
               return finished;

            case Download.ERROR_ON_COMPLETING:
               return errorOnCompleting;

            case Download.NOT_ENOUGH_SPACE_ERROR:
               return noSpace;

            case Download.DATA_BEING_CREATED:
               return dataBeingCreated;

            case Download.SEARCHING_LOADING:
            {
               DownloadSource[] sources       = download.getSources();
               String           result        = "";
               int              transferText  = 0;
               int              queue = 0;
               int              status;

               for(int i = 0; i < sources.length; i++)
               {
                  status = sources[i].getStatus();
                  if(status == DownloadSource.TRANSFER)
                  {
                     transferText++;
                     result = load;
                  }
                  else if(status == DownloadSource.IN_QUEUE)
                  {
                     queue++;
                  }
               }

               if(result.length() == 0)
               {
                  result = searchLabel;
               }

               return result + " " + (queue + transferText) + "/" + sources.length + " (" + transferText + ")";
            }

            case Download.FINISHING:
               return complete;

            default:
               return "";
         }
      }
      catch(Exception e)
      {
         return "";
      }
   }

   public void forceResort()
   {
      if(null != sorter)
      {
         sorter.forceResort();
      }
   }

   public List<Download> getContent()
   {
      return downloads;
   }

   public Object getValueForSortAt(int row, int column)
   {
      if(column == 0)
      {
         return ((Download) getValueAt(row, column)).getFilename();
      }

      return getValueAt(row, column);
   }

   public void sortByColumn(int column, boolean isAscent)
   {
      if(sorter == null)
      {
         sorter = new TableSorter<Download>(this);
      }

      sorter.sort(column, isAscent);
      fireTableDataChanged();
   }
}
