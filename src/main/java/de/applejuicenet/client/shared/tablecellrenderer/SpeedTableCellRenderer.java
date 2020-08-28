/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared.tablecellrenderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

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
      return super.getTableCellRendererComponent(table, getSpeedAsString((Integer) value), isSelected, hasFocus, row, column);
   }

   private String getSpeedAsString(Integer speed)
   {
      if(speed == 0)
      {
         return "";
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
}
