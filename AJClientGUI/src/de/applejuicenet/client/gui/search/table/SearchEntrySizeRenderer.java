/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.gui.download.table.DownloadModel;

public class SearchEntrySizeRenderer extends DefaultTableCellRenderer
{
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      JLabel comp = (JLabel) super.getTableCellRendererComponent(table, DownloadModel.parseGroesse((Long) value), isSelected,
                                                                 hasFocus, row, column);

      comp.setHorizontalAlignment(SwingConstants.RIGHT);
      return comp;
   }
}
