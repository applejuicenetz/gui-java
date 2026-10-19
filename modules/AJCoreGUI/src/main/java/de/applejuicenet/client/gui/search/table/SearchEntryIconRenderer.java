/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.search.table;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.facade.entity.FileName;
import de.applejuicenet.client.facade.entity.SearchEntry;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SearchEntryIconRenderer extends DefaultTableCellRenderer
{
   // Read by the renderer (EDT) and filled by the core thread
   private static final Set<String> md5Sums = ConcurrentHashMap.newKeySet();

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      SearchEntry entry    = (SearchEntry) value;
      String      filename = entry.getFileNames()[0].getFileName();
      JLabel      label    = (JLabel) super.getTableCellRendererComponent(table, filename, isSelected, hasFocus, row, column);

      label.setOpaque(true);
      boolean isAlreadyLoaded = entry.getChecksum() != null && md5Sums.contains(entry.getChecksum());

      if(isAlreadyLoaded)
      {
         label.setBackground(Color.GREEN);
         label.setForeground(Color.BLACK);
      }
      else
      {
         label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
      }

      label.setIcon(IconManager.getInstance().getIcon((entry.getFileType().toString())));
      StringBuilder tooltip = new StringBuilder("<html><table><tr><th align=\"left\">Dateiname</th><th align=\"left\">Quellen</th></tr>");

      for(FileName curFileName : entry.getFileNames())
      {
         tooltip.append("<tr><td>");
         tooltip.append(curFileName.getFileName());
         tooltip.append("</td><td>");
         tooltip.append(curFileName.getFrequency());
         tooltip.append("</td>");
         tooltip.append("</tr>");
      }

      tooltip.append("</table>");
      if(isAlreadyLoaded)
      {
         tooltip.append("<div align=\"center\" color=\"green\">" + GuiText.text("javagui.searchform.tooltip.loaded") + "</div>");
      }
      else
      {
         tooltip.append("<div align=\"center\" color=\"red\">" + GuiText.text("javagui.searchform.tooltip.notloaded") + "</div>");
      }

      tooltip.append("</html>");
      label.setToolTipText(tooltip.toString());
      return label;
   }

   public static void addMd5Sum(String md5sum)
   {
      if(md5sum != null)
      {
         md5Sums.add(md5sum);
      }
   }

   public static void removeMd5Sum(String md5sum)
   {
      if(md5sum != null)
      {
         md5Sums.remove(md5sum);
      }
   }
}
