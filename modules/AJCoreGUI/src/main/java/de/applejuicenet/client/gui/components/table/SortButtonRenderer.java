/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.table;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/table/SortButtonRenderer.java,v 1.4 2009/02/12 13:03:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class SortButtonRenderer extends JButton implements TableCellRenderer
{
   private static Font textFont       = new JTable().getFont();
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
      downButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
      downButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

      upButton = new JButton();
      upButton.setMargin(new Insets(0, 0, 0, 0));
      upButton.setHorizontalTextPosition(LEFT);
      upButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
      upButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      JButton button = this;

      if(selectedColumn == column)
      {
         button = curAscent ? downButton : upButton;
      }

      button.setText(null == value ? "" : value.toString());
      button.setFont(textFont);
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
