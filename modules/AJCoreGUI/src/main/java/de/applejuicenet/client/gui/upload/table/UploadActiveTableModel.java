/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.facade.entity.Upload;
import de.applejuicenet.client.facade.entity.Version;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadActiveTableModel extends AbstractTableModel implements LanguageListener, SortableTableModel<Upload>
{
   static final String[] COL_KEYS = {"mainform.uploads.col0caption", "mainform.uploads.col1caption", "javagui.uploadform.columnchunksize", "mainform.uploads.col2caption", "mainform.queue.col6caption", "javagui.uploadform.columntotalloaded", "mainform.uploads.col4caption", "mainform.uploads.col5caption"};
   final static String[] COL_NAMES = 
                                     {
                                        "File name", "Nickname", "Chunk", "Speed", "Percent loaded",
                                        "Total loaded", "Priority", "Client"
                                     };
   @SuppressWarnings("unchecked")
   public static final Class[] CLASS_TYPES                               = 
                                                                           {
                                                                              Upload.class, String.class, Long.class,
                                                                              Integer.class, Double.class, Double.class,
                                                                              Integer.class, Version.class
                                                                           };
   private List<Upload>        uploads      = new ArrayList<Upload>();
   private SimpleDateFormat    formatter    = new SimpleDateFormat("HH:mm:ss");
   private String              transferText;
   private TableSorter<Upload> sorter       = null;

   public UploadActiveTableModel()
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

   public boolean setUploads(Map<Integer, Upload> uploadMap)
   {
      boolean change = false;
      uploads.replaceAll(old -> uploadMap.getOrDefault(old.getId(), old));

      for(Upload curUpload : uploadMap.values())
      {
         if(!(curUpload.getStatus() == Upload.ACTIVE_TRANSFER))
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
            if(anUpload.getStatus() != Upload.ACTIVE_TRANSFER || !uploadMap.containsKey(anUpload.getId()))
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
      return GuiText.textOr(COL_KEYS[column], COL_NAMES[column]);
   }

   @Override
   public Class<? > getColumnClass(int columnIndex)
   {
      return CLASS_TYPES[columnIndex];
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
            return upload.getSize();

         case 3:
            return upload.getSpeed();

         case 4:
            return upload.getDownloadPercent();

         case 5:
            return upload.getLoaded();

         case 6:
            return upload.getPriority();

         case 7:
            return upload.getVersion();

         default:
            return GuiText.textOr("javagui.error", "Error");
      }
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      transferText = languageSelector.getFirstAttributeByTagName("mainform.uploads.uplstat1");
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
      if(column == 0)
      {
         return ((Upload) getValueAt(row, column)).getFileName();
      }

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
