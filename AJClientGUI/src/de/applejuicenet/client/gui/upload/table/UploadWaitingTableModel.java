/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

public class UploadWaitingTableModel extends AbstractTableModel implements LanguageListener, SortableTableModel<Upload>
{
   final static String[]                                  COL_NAMES = 
                                                                      {
                                                                         "Dateiname", "Status", "Nickname", "Wasserstand",
                                                                         "Prioritaet", "letzte Verbindung", "Client"
                                                                      };
   @SuppressWarnings("unchecked")
   static protected Class[]                               cTypes = 
                                                                   {
                                                                      String.class, Integer.class, String.class, Integer.class,
                                                                      String.class, Date.class, Version.class
                                                                   };
   private List<Upload>        uploads      = new ArrayList<Upload>();
   private SimpleDateFormat    formatter    = new SimpleDateFormat("HH:mm:ss");
   private String              uebertragung;
   private TableSorter<Upload> sorter       = null;

   public UploadWaitingTableModel()
   {
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   public void forceResort()
   {
      if(null != sorter)
      {
         sorter.forceResort();
      }
   }

   public boolean setUploads(Map<String, Upload> uploadMap)
   {
      boolean change = false;

      for(Upload curUpload : uploadMap.values())
      {
         if(curUpload.getStatus() == Upload.AKTIVE_UEBERTRAGUNG)
         {
            continue;
         }

         change = true;
         if(!uploads.contains(curUpload))
         {
            uploads.add(curUpload);
         }
      }

      int    count    = uploads.size();
      Upload anUpload;

      if(count > 0)
      {
         for(int x = count - 1; x >= 0; x--)
         {
            anUpload = uploads.get(x);
            if(anUpload.getStatus() == Upload.AKTIVE_UEBERTRAGUNG || !uploadMap.containsKey(anUpload.getId() + ""))
            {
               uploads.remove(x);
               change = true;
            }
         }
      }

      return change;
   }

   @Override
   public String getColumnName(int column)
   {
      return COL_NAMES[column];
   }

   @Override
   public Class<? > getColumnClass(int columnIndex)
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
            return upload.getDateiName();

         case 1:
            return upload.getDirectState();

         case 2:
            return upload.getNick();

         case 3:
            return upload.getLoaded();

         case 4:
            return upload.getPrioritaet();

         case 5:
            return upload.getLastConnection() > 0 ? new Date(upload.getLastConnection()) : null;

         case 6:
            return upload.getVersion();

         default:
            return "Fehler";
      }
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

   public List<Upload> getContent()
   {
      return uploads;
   }

   public Object getValueForSortAt(int row, int column)
   {
      return getValueAt(row, column);
   }

   public void sortByColumn(int column, boolean isAscent)
   {
      if(sorter == null)
      {
         sorter = new TableSorter<Upload>(this);
      }

      sorter.sort(column, isAscent);
      fireTableDataChanged();
   }
}
