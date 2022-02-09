/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search.table;

import de.applejuicenet.client.fassade.entity.FileName;
import de.applejuicenet.client.fassade.entity.SearchEntry;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashSet;

public class SearchEntryIconRenderer extends DefaultTableCellRenderer
{
   private static HashSet<String> md5Sums = new HashSet<String>();

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      SearchEntry entry    = (SearchEntry) value;
      String      filename = entry.getFileNames()[0].getDateiName();
      JLabel      label    = (JLabel) super.getTableCellRendererComponent(table, filename, isSelected, hasFocus, row, column);

      label.setOpaque(true);
      boolean wirdBereitsGeladen = md5Sums.contains(entry.getChecksumme());

      if(wirdBereitsGeladen)
      {
         label.setBackground(Color.GREEN);
      }
      else
      {
         label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
      }

      label.setIcon(IconManager.getInstance().getIcon((entry.getFileType().toString())));
      StringBuilder tooltipp = new StringBuilder("<html><table><tr><th align=\"left\">Dateiname</th><th align=\"left\">Quellen</th></tr>");

      for(FileName curFileName : entry.getFileNames())
      {
         tooltipp.append("<tr><td>");
         tooltipp.append(curFileName.getDateiName());
         tooltipp.append("</td><td>");
         tooltipp.append(curFileName.getHaeufigkeit());
         tooltipp.append("</td>");
         tooltipp.append("</tr>");
      }

      tooltipp.append("</table>");
      if(wirdBereitsGeladen)
      {
         tooltipp.append("<div align=\"center\" color=\"green\">wird herunter geladen bzw. bereits im Share...</div>");
      }
      else
      {
         tooltipp.append("<div align=\"center\" color=\"red\">nicht im Download...</div>");
      }

      tooltipp.append("</html>");
      label.setToolTipText(tooltipp.toString());
      return label;
   }

   public static void addMd5Sum(String md5sum)
   {
      md5Sums.add(md5sum);
   }

   public static void removeMd5Sum(String md5sum)
   {
      md5Sums.remove(md5sum);
   }
}
