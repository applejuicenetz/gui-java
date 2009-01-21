/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class UploadTableDateCellRenderer extends DefaultTableCellRenderer
{
   private static final SimpleDateFormat FORMATER_LONG = new SimpleDateFormat("dd.MM. HH:mm:ss");

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Date date = (Date) value;

      return super.getTableCellRendererComponent(table, null == date ? null : FORMATER_LONG.format(date), isSelected, hasFocus,
                                                 row, column);
   }
}
