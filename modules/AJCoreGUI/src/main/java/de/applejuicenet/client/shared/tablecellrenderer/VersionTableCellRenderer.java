/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared.tablecellrenderer;

import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class VersionTableCellRenderer extends DefaultTableCellRenderer
{
   public VersionTableCellRenderer()
   {
      super();
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Version version = (Version) value;

      String  text         = null == version ? null : version.getVersion();
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
