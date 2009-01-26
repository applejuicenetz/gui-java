/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.fassade.shared.FileType;
import de.applejuicenet.client.shared.IconManager;

public class DownloadTableFilenameCellRenderer extends DefaultTableCellRenderer
{
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if(null != value)
      {
         FileType fileType = FileType.calculatePossibleFileType((String) value);

         Icon     icon = IconManager.getInstance().getIcon(fileType.toString());

         label.setIcon(icon);
      }

      return label;
   }
}
