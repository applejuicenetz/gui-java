package de.applejuicenet.client.gui.components.table;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import javax.swing.table.TableModel;

public class AutoRowHeightTable extends JTable
{
   public AutoRowHeightTable()
   {
      super();
   }

   public AutoRowHeightTable(TableModel model)
   {
      super(model);
   }

   @Override
   public void updateUI()
   {
      super.updateUI();
      int contentHeight = Math.max(getFontMetrics(getFont()).getHeight(), UIScale.scale(16));

      setRowHeight(Math.max(getRowHeight(), contentHeight + UIScale.scale(2)));
   }

   public void refresh()
   {
      revalidate();
      repaint();
   }
}
