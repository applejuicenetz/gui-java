/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

public class DownloadsTableModel extends AbstractTableModel implements LanguageListener, SortableTableModel<Download>
{

   //   public static String[]                                    cNames = {"", "", "", "", "", "", "", "", "", ""};
   @SuppressWarnings("unchecked")
   public static final Class[]                               CLASS_TYPES = 
                                                                           {
                                                                              Download.class, String.class, Integer.class,
                                                                              Integer.class, Integer.class, String.class,
                                                                              Double.class, Integer.class, Integer.class,
                                                                              String.class
                                                                           };

   //Download-Stati
   public static String          suchen                  = "";
   public static String          laden                   = "";
   public static String          keinPlatz               = "";
   public static String          fertigstellen           = "";
   public static String          fehlerBeimFertigstellen = "";
   public static String          fertig                  = "";
   public static String          abbrechen               = "";
   public static String          abgebrochen             = "";
   public static String          dataWirdErstellt        = "";
   public static String          pausiert                = "";
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
   public Class<? > getColumnClass(int columnIndex)
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
            return download.getGroesse();

         case 3:
            return download.getBereitsGeladen();

         case 4:
            return download.getSpeedInBytes();

         case 5:
            return download.getRestZeitAsString();

         case 6:
            return download.getProzentGeladen();

         case 7:
            return download.getGroesse() - download.getBereitsGeladen();

         case 8:
            return download.getPowerDownload();

         case 9:
            return download.getTargetDirectory();

         default:
            return "";
      }
   }

   public boolean setDownloads(Map<String, Download> downloadMap)
   {
      boolean change = false;

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
            if(!downloadMap.containsKey(anDownload.getId() + ""))
            {
               downloads.remove(x);
               change = true;
            }
         }
      }

      return change;
   }

   public static String parseGroesse(long groesse)
   {
      double share  = Double.parseDouble(Long.toString(groesse));
      int    faktor;

      if(share == 0)
      {
         return "";
      }

      if(share < 1024)
      {
         return groesse + " Bytes";
      }
      else if(share / 1024 < 1024)
      {
         faktor = 1024;
      }
      else if(share / 1048576 < 1024)
      {
         faktor = 1048576;
      }
      else if(share / 1073741824 < 1024)
      {
         faktor = 1073741824;
      }
      else
      {
         faktor = 1;
      }

      share = share / faktor;
      String result = Double.toString(share);

      if(result.indexOf('.') != -1 && (result.indexOf('.') + 3 < result.length()))
      {
         result = result.substring(0, result.indexOf('.') + 3);
      }

      result = result.replace('.', ',');
      if(faktor == 1024)
      {
         result += " KB";
      }
      else if(faktor == 1048576)
      {
         result += " MB";
      }
      else if(faktor == 1073741824)
      {
         result += " GB";
      }
      else
      {
         result += " ??";
      }

      return result;
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      suchen                  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestatlook");
      laden                   = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestattransfer");
      keinPlatz               = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat1");
      fertigstellen           = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat12");
      fertig                  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat14");
      abbrechen               = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat15");
      abgebrochen             = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat17");
      fehlerBeimFertigstellen = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.fehlerbeimfertigstellen");
      dataWirdErstellt        = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.datawirderstellt");
      pausiert                = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat13");
   }

   public static String getStatusAsString(Download download)
   {
      try
      {
         switch(download.getStatus())
         {

            case Download.PAUSIERT:
               return pausiert;

            case Download.ABBRECHEN:
               return abbrechen;

            case Download.ABGEGROCHEN:
               return abgebrochen;

            case Download.FERTIG:
               return fertig;

            case Download.FEHLER_BEIM_FERTIGSTELLEN:
               return fehlerBeimFertigstellen;

            case Download.NICHT_GENUG_PLATZ_FEHLER:
               return keinPlatz;

            case Download.DATA_WIRD_ERSTELLT:
               return dataWirdErstellt;

            case Download.SUCHEN_LADEN:
            {
               DownloadSource[] sources       = download.getSources();
               String           result        = "";
               int              uebertragung  = 0;
               int              warteschlange = 0;
               int              status;

               for(int i = 0; i < sources.length; i++)
               {
                  status = sources[i].getStatus();
                  if(status == DownloadSource.UEBERTRAGUNG)
                  {
                     uebertragung++;
                     result = laden;
                  }
                  else if(status == DownloadSource.IN_WARTESCHLANGE)
                  {
                     warteschlange++;
                  }
               }

               if(result.length() == 0)
               {
                  result = suchen;
               }

               return result + " " + (warteschlange + uebertragung) + "/" + sources.length + " (" + uebertragung + ")";
            }

            case Download.FERTIGSTELLEN:
               return fertigstellen;

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
