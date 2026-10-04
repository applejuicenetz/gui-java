/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared.tablecellrenderer;

import de.applejuicenet.client.gui.download.table.DownloadsTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SpeedTableCellRenderer extends DefaultTableCellRenderer
{
   public SpeedTableCellRenderer()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      return super.getTableCellRendererComponent(table, getSpeedAsString((Number) value), isSelected, hasFocus, row, column);
   }

   private String getSpeedAsString(Number speed)
   {
      if(speed == null || speed.longValue() == 0)
      {
         return "";
      }

      return DownloadsTableModel.parseGroesse(speed.longValue()) + "/s";
   }
}
