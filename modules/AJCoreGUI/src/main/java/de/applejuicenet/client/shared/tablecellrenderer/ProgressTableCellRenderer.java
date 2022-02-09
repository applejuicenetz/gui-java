/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.shared.tablecellrenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class ProgressTableCellRenderer extends JProgressBar implements TableCellRenderer
{
   private static DecimalFormat formatter = new DecimalFormat("###,##0.00");

   public ProgressTableCellRenderer()
   {
      super(JProgressBar.HORIZONTAL, 0, 100);
      setStringPainted(true);
      setOpaque(false);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Double percent = (Double) value;
      if (null == percent)
      {
          percent = 0.0;
      }
      setString(formatter.format(percent) + " %");
      setValue(percent.intValue());
      setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

      return this;
   }
}
