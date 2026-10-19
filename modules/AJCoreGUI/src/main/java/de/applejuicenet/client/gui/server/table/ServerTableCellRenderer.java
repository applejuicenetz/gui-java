/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.server.table;

import de.applejuicenet.client.facade.entity.Server;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Copyright: General Public License
 */
public class ServerTableCellRenderer extends DefaultTableCellRenderer
{
   public ServerTableCellRenderer()
   {
      super();
      setOpaque(true);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                  int column)
   {
      Server server = (Server) value;
      JLabel label  = (JLabel) super.getTableCellRendererComponent(table, server.getName(), isSelected, hasFocus, row, column);

      Icon   icon;

      if(server.isConnected())
      {
         icon       = IconManager.getInstance().getIcon("serververbunden");
      }
      else if(server.isTryConnect())
      {
         icon = IconManager.getInstance().getIcon("serverversuche");
      }
      else
      {
         long currentTime = System.currentTimeMillis();
         long tag          = 24 * 60 * 60 * 1000;

         if(server.getTimeLastSeen() == 0 || server.getTimeLastSeen() < currentTime - tag)
         {
            icon = IconManager.getInstance().getIcon("aelter24h");
         }
         else
         {
            icon = IconManager.getInstance().getIcon("juenger24h");

            //more icons will follow when the core can do more
         }
      }

      label.setIcon(icon);

      return label;
   }
}
