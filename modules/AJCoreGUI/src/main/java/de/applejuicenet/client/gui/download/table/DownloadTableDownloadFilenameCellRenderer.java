/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import de.applejuicenet.client.facade.entity.Download;

import javax.swing.*;
import java.awt.*;

public class DownloadTableDownloadFilenameCellRenderer extends DownloadTableFilenameCellRenderer
{
   private static final Color FINISHED_COLOR = new Color(51, 255, 0);

   public DownloadTableDownloadFilenameCellRenderer()
   {
      super();
      setOpaque(true);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Download download = (Download) value;

      // DefaultTableCellRenderer keeps setForeground permanently; reset it before every render
      setForeground(null);

      JLabel   label = (JLabel) super.getTableCellRendererComponent(table, download.getFilename(), isSelected, hasFocus, row, column);

      if(download.getStatus() == Download.FINISHED)
      {
         setBackground(FINISHED_COLOR);
         label.setForeground(Color.BLACK);
      }
      else if(download.getStatus() == Download.CANCEL || download.getStatus() == Download.ABORTED_BY_PEER)
      {
         setBackground(Color.RED);
         label.setForeground(Color.BLACK);
      }
      else
      {
         setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
      }

      return label;
   }
}
