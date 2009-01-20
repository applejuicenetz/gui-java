/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.upload.table;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.shared.IconManager;

public class UploadTableVersionCellRenderer extends DefaultTableCellRenderer
{
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Version version      = (Version) value;

      String  text         = null == version ? "" : version.getVersion();
      JLabel  versionLabel = (JLabel) super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

      versionLabel.setFont(table.getFont());
      if(null == text)
      {
         versionLabel.setIcon(null);
      }
      else
      {
         versionLabel.setIcon(getVersionIcon(version));
      }

      return versionLabel;
   }

   private ImageIcon getVersionIcon(Version version)
   {
      switch(version.getBetriebsSystem())
      {

         case Version.WIN32:
            return IconManager.getInstance().getIcon("winsymbol");

         case Version.LINUX:
            return IconManager.getInstance().getIcon("linuxsymbol");

         case Version.FREEBSD:
            return IconManager.getInstance().getIcon("freebsdsymbol");

         case Version.MACINTOSH:
            return IconManager.getInstance().getIcon("macsymbol");

         case Version.SOLARIS:
            return IconManager.getInstance().getIcon("sunossymbol");

         case Version.NETWARE:
            return IconManager.getInstance().getIcon("netwaresymbol");

         case Version.OS2:
            return IconManager.getInstance().getIcon("os2symbol");

         default:
            return IconManager.getInstance().getIcon("unbekanntsymbol");
      }
   }
}
