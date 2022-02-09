/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search.table;

import de.applejuicenet.client.gui.download.table.DownloadsTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SearchEntrySizeRenderer extends DefaultTableCellRenderer
{
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      JLabel comp = (JLabel) super.getTableCellRendererComponent(table, DownloadsTableModel.parseGroesse((Long) value), isSelected,
                                                                 hasFocus, row, column);

      comp.setHorizontalAlignment(SwingConstants.RIGHT);
      return comp;
   }
}
