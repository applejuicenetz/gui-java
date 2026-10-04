/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.shared.tablecellrenderer;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

public class ProgressTableCellRenderer extends JProgressBar implements TableCellRenderer
{
   private static DecimalFormat formatter = new DecimalFormat("###,##0.00");
   private final JPanel paddedRenderer;

   public ProgressTableCellRenderer()
   {
      this(0);
   }

   public ProgressTableCellRenderer(int horizontalPadding)
   {
      super(JProgressBar.HORIZONTAL, 0, 100);
      setStringPainted(true);
      setOpaque(false);
      if(horizontalPadding > 0)
      {
         paddedRenderer = new JPanel(new BorderLayout());
         int padding = UIScale.scale(horizontalPadding);

         paddedRenderer.setBorder(BorderFactory.createEmptyBorder(0, padding, 0, padding));
         paddedRenderer.add(this, BorderLayout.CENTER);
      }
      else
      {
         paddedRenderer = null;
      }
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
      setFont(table.getFont());
      setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

      if(paddedRenderer != null)
      {
         paddedRenderer.setBackground(getBackground());
         return paddedRenderer;
      }
      return this;
   }
}
