/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.gui.download.table.DownloadModel;

public class UploadTablePrioCellRenderer extends DefaultTableCellRenderer
{
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Integer prio = (Integer) value;
      String  text = DownloadModel.powerdownload(prio) + " (" + prio + ")";

      return super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
   }
}
