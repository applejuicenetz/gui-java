/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.Settings;

public class DownloadTableDownloadFilenameCellRenderer extends DownloadTableFilenameCellRenderer implements DataUpdateListener
{
   private Settings settings;

   public DownloadTableDownloadFilenameCellRenderer()
   {
      super();
      setOpaque(true);
      settings = Settings.getSettings();
      OptionsManagerImpl.getInstance().addSettingsListener(this);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Download download = (Download) value;
      JLabel   label = (JLabel) super.getTableCellRendererComponent(table, download.getFilename(), isSelected, hasFocus, row, column);

      if(!isSelected && settings.isFarbenAktiv() && download.getStatus() == Download.FERTIG)
      {
         setBackground(settings.getDownloadFertigHintergrundColor());
      }
      else if(!isSelected && settings.isFarbenAktiv() &&
                 (download.getStatus() == Download.ABBRECHEN || download.getStatus() == Download.ABGEGROCHEN))
      {
         setBackground(Color.RED);
      }
      else
      {
         setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
      }

      return label;
   }

   public void fireContentChanged(DATALISTENER_TYPE type, Object content)
   {
      if(type == DATALISTENER_TYPE.SETTINGS_CHANGED)
      {
         settings = (Settings) content;
      }
   }
}
