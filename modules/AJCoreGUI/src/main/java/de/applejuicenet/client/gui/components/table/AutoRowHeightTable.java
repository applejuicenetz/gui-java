package de.applejuicenet.client.gui.components.table;

import com.formdev.flatlaf.util.UIScale;
import de.applejuicenet.client.fassade.shared.FileType;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Font;

public class AutoRowHeightTable extends JTable
{
   private Icon[] rowIcons = new Icon[0];

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
      updateRowHeight();
   }

   @Override
   public void setFont(Font font)
   {
      super.setFont(font);
      updateRowHeight();
   }

   public void setRowIcons(Icon... icons)
   {
      rowIcons = icons.clone();
      updateRowHeight();
   }

   public void setFileTypeIcons()
   {
      FileType[] types = FileType.values();
      Icon[] icons = new Icon[types.length];

      for (int index = 0; index < types.length; index++)
      {
         icons[index] = IconManager.getInstance().getIcon(types[index].toString());
      }
      setRowIcons(icons);
   }

   private void updateRowHeight()
   {
      if (getFont() == null)
      {
         return;
      }
      int contentHeight = getFontMetrics(getFont()).getHeight();

      if (rowIcons != null)
      {
         for (Icon icon : rowIcons)
         {
            if (icon != null)
            {
               contentHeight = Math.max(contentHeight, icon.getIconHeight());
            }
         }
      }
      setRowHeight(Math.max(UIManager.getInt("Table.rowHeight"), contentHeight + UIScale.scale(4)));
   }
}
