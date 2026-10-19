/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Copyright: General Public License
 */
public class NormalHeaderRenderer extends JButton implements TableCellRenderer
{
   private LookAndFeel renderedLookAndFeel = UIManager.getLookAndFeel();


   public NormalHeaderRenderer()
   {
      setMargin(new Insets(0, 0, 0, 0));
      setHorizontalTextPosition(LEFT);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      if(renderedLookAndFeel != UIManager.getLookAndFeel())
      {
         updateUI();
         renderedLookAndFeel = UIManager.getLookAndFeel();
      }
      setText((value == null) ? "" : value.toString());
      setFont(table.getTableHeader().getFont());
      return this;
   }
}
