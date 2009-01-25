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
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

public class DownloadActiveTableModel extends AbstractTableModel implements LanguageListener
{
   static protected String[]                              cNames = {"", "", "", "", "", "", "", "", "", ""};
   @SuppressWarnings("unchecked")
   static protected Class[]                               cTypes = 
                                                                   {
                                                                      String.class, String.class, String.class, String.class,
                                                                      String.class, String.class, Double.class, String.class,
                                                                      String.class, String.class
                                                                   };

   //Download-Stati
   public static String   suchen                  = "";
   public static String   laden                   = "";
   public static String   keinPlatz               = "";
   public static String   fertigstellen           = "";
   public static String   fehlerBeimFertigstellen = "";
   public static String   fertig                  = "";
   public static String   abbrechen               = "";
   public static String   abgebrochen             = "";
   public static String   dataWirdErstellt        = "";
   public static String   pausiert                = "";
   private List<Download> downloads               = new ArrayList<Download>();

   public DownloadActiveTableModel()
   {
      super();
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   public int getColumnCount()
   {
      return cNames.length;
   }

   @Override
   public Class<?> getColumnClass(int columnIndex)
   {
      return cTypes[columnIndex];
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
            return download.getFilename();

         case 1:
            return getStatusAsString(download);

         case 2:
            return parseGroesse(download.getGroesse());

         case 3:
            return parseGroesse(download.getBereitsGeladen());

         case 4:
            return getSpeedAsString(download.getSpeedInBytes());

         case 5:
            return download.getRestZeitAsString();

         case 6:
            return download.getProzentGeladen();

         case 7:
            return parseGroesse(download.getGroesse() - download.getBereitsGeladen());

         case 8:
            return powerdownload(download.getPowerDownload());

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

   public static String powerdownload(int pwdl)
   {
      if(pwdl == 0)
      {
         return "1:1,0";
      }

      double power = pwdl;

      power = power / 10 + 1;
      String temp = Double.toString(power);

      temp = temp.replace('.', ',');
      return "1:" + temp;
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
         faktor     = 1024;
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

   public static String getSpeedAsString(long speed)
   {
      if(speed == 0)
      {
         return "0 Bytes/s";
      }

      double size   = speed;
      int    faktor = 1;

      if(size < 1024)
      {
         faktor = 1;
      }
      else
      {
         faktor = 1024;

      }

      size = size / faktor;
      String s = Double.toString(size);

      if(s.indexOf(".") + 3 < s.length())
      {
         s = s.substring(0, s.indexOf(".") + 3);
      }

      if(faktor == 1)
      {
         s += " Bytes/s";
      }
      else
      {
         s += " kb/s";
      }

      return s;
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
                  else if(status == DownloadSource.IN_WARTESCHLANGE || status == DownloadSource.WARTESCHLANGE_VOLL)
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
}
