/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared.tablecellrenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SizeTableCellRenderer extends DefaultTableCellRenderer
{
   public SizeTableCellRenderer()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      return super.getTableCellRendererComponent(table, parseGroesse((Integer) value), isSelected, hasFocus, row, column);
   }

   public String parseGroesse(long groesse)
   {
      double share  = Double.parseDouble(Long.toString(groesse));
      int    faktor;

      if(share == 0)
      {
         return "";
      }

      if(share < 1024)
      {
         return groesse + " Bytes";
      }
      else if(share / 1024 < 1024)
      {
         faktor = 1024;
      }
      else if(share / 1048576 < 1024)
      {
         faktor = 1048576;
      }
      else if(share / 1073741824 < 1024)
      {
         faktor = 1073741824;
      }
      else
      {
         faktor = 1;
      }

      share = share / faktor;
      String result = Double.toString(share);

      if(result.indexOf('.') != -1 && (result.indexOf('.') + 3 < result.length()))
      {
         result = result.substring(0, result.indexOf('.') + 3);
      }

      result = result.replace('.', ',');
      if(faktor == 1024)
      {
         result += " KB";
      }
      else if(faktor == 1048576)
      {
         result += " MB";
      }
      else if(faktor == 1073741824)
      {
         result += " GB";
      }
      else
      {
         result += " ??";
      }

      return result;
   }
}
