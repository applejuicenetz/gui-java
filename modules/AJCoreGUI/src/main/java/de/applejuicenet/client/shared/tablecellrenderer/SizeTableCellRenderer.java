/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared.tablecellrenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SizeTableCellRenderer extends DefaultTableCellRenderer
{
   public SizeTableCellRenderer()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      return super.getTableCellRendererComponent(table, parseGroesse(((Number) value).longValue()), isSelected, hasFocus, row, column);
   }

   public String parseGroesse(long groesse)
   {
      return de.applejuicenet.client.gui.download.table.DownloadsTableModel.parseGroesse(groesse);
   }
}
