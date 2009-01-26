/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

public class DownloadTableStatusCellRenderer extends DefaultTableCellRenderer implements LanguageListener
{
   public static String suchen                  = "";
   public static String laden                   = "";
   public static String keinPlatz               = "";
   public static String fertigstellen           = "";
   public static String fehlerBeimFertigstellen = "";
   public static String fertig                  = "";
   public static String abbrechen               = "";
   public static String abgebrochen             = "";
   public static String dataWirdErstellt        = "";
   public static String pausiert                = "";

   public DownloadTableStatusCellRenderer()
   {
      super();
      LanguageSelector.getInstance().addLanguageListener(this);

   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      return super.getTableCellRendererComponent(table, getStatusAsString((Download) value), isSelected, hasFocus, row, column);
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
}
