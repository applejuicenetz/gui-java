/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.server.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerTableDateCellRenderer extends DefaultTableCellRenderer
{
   private static final SimpleDateFormat FORMATER_LONG = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

   public ServerTableDateCellRenderer()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Date date = (Date) value;

      return super.getTableCellRendererComponent(table, null == date ? null : FORMATER_LONG.format(date), isSelected, hasFocus,
                                                 row, column);
   }
}
