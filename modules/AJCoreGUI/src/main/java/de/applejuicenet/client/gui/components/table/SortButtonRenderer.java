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
public class SortButtonRenderer extends JButton implements TableCellRenderer
{
   private LookAndFeel renderedLookAndFeel = UIManager.getLookAndFeel();
   private int         selectedColumn = -1;
   private boolean     curAscent      = true;
   private JButton     downButton;
   private JButton     upButton;

   public SortButtonRenderer()
   {
      setMargin(new Insets(0, 0, 0, 0));
      setHorizontalTextPosition(LEFT);
      setIcon(new BlankIcon());

      downButton = new JButton();
      downButton.setMargin(new Insets(0, 0, 0, 0));
      downButton.setHorizontalTextPosition(LEFT);

      upButton = new JButton();
      upButton.setMargin(new Insets(0, 0, 0, 0));
      upButton.setHorizontalTextPosition(LEFT);
      installArrowIcons();
   }

   /** The arrow colours come from the look and feel, so they are built again after a theme change. */
   private void installArrowIcons()
   {
      downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
      downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));
      upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
      upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      if(renderedLookAndFeel != UIManager.getLookAndFeel())
      {
         // Renderer buttons are not children of the table and miss tree UI updates.
         updateUI();
         downButton.updateUI();
         upButton.updateUI();
         installArrowIcons();
         renderedLookAndFeel = UIManager.getLookAndFeel();
      }
      JButton button = this;

      if(selectedColumn == column)
      {
         button = curAscent ? downButton : upButton;
      }

      button.setText(null == value ? "" : value.toString());
      button.setFont(table.getTableHeader().getFont());
      return button;
   }

   public void setSelectedColumn(int col, boolean isAscent)
   {
      if(col < 0)
      {
         return;
      }

      selectedColumn = col;
      curAscent      = isAscent;
   }

   public int getSelectedColumn()
   {
      return selectedColumn;
   }

   public boolean getState()
   {
      return curAscent;
   }
}
