/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;

import java.text.DecimalFormat;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class UploadTablePercentCellRenderer extends JProgressBar implements TableCellRenderer
{
   private static DecimalFormat formatter = new DecimalFormat("###,##0.00");

   public UploadTablePercentCellRenderer()
   {
      super(JProgressBar.HORIZONTAL, 0, 100);
      setStringPainted(true);
      setOpaque(false);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Double percent = (Double) value;

      setString(formatter.format(percent) + " %");
      setValue(percent.intValue());
      setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

      return this;
   }
}
