/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class UploadTablePrioCellRenderer extends DefaultTableCellRenderer
{
   public UploadTablePrioCellRenderer()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Integer prio = (Integer) value;
      String  text = prio + "";

      return super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
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
}
