/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.upload.table;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.download.table.DownloadModel;
import de.applejuicenet.client.gui.listener.LanguageListener;

public class UploadActiveTableModel extends AbstractTableModel implements LanguageListener
{
   final static String[] COL_NAMES = 
                                     {
                                        "Dateiname", "Wer", "Geschwindigkeit", "Prozent geladen", "Gesamt geladen",
                                        "Prioritaet", "Client"
                                     };
   @SuppressWarnings("unchecked")
   static protected Class[] cTypes                               = 
                                                                   {
                                                                      Upload.class, String.class, Integer.class,
                                                                      Double.class, Double.class, String.class, Version.class
                                                                   };
   private List<Upload>     uploads      = new ArrayList<Upload>();
   private SimpleDateFormat formatter    = new SimpleDateFormat("HH:mm:ss");
   private String           uebertragung;

   public UploadActiveTableModel()
   {
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   public void setUploads(Map<String, Upload> uploadMap)
   {
      for(Upload curUpload : uploadMap.values())
      {
         if(!(curUpload.getStatus() == Upload.AKTIVE_UEBERTRAGUNG))
         {
            continue;
         }

         if(!uploads.contains(curUpload))
         {
            uploads.add(curUpload);
         }
      }

      int    count                       = uploads.size();
      Upload anUpload;

      if(count > 0)
      {
         for(int x = count - 1; x >= 0; x--)
         {
            anUpload = uploads.get(x);
            if(anUpload.getStatus() != Upload.AKTIVE_UEBERTRAGUNG || !uploadMap.containsKey(anUpload.getId() + ""))
            {
               uploads.remove(x);
            }
         }
      }
   }

   @Override
   public String getColumnName(int column)
   {
      return COL_NAMES[column];
   }

   @Override
   public Class<?> getColumnClass(int columnIndex)
   {
      return cTypes[columnIndex];
   }

   public int getColumnCount()
   {
      return COL_NAMES.length;
   }

   public int getRowCount()
   {
      return uploads.size();
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      Upload upload = uploads.get(rowIndex);

      switch(columnIndex)
      {

         case 0:
            return upload;

         case 1:
            return upload.getNick();

         case 2:
            return upload.getSpeed();

         case 3:
            return upload.getDownloadPercent();

         case 4:
            return upload.getLoaded();

         case 5:
            int prio = upload.getPrioritaet();

            return DownloadModel.powerdownload(prio) + " (" + prio + ")";

         case 6:
            return upload.getVersion();

         default:
            return "Fehler";
      }
   }

   private String getSpeedAsString(long speed)
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

      uebertragung = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.uplstat1");
   }

   public Upload getRow(int selected)
   {
      return uploads.get(selected);
   }
}
