/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.server.table;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/server/table/ServerTableCellRenderer.java,v 1.4 2009/01/22 22:18:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
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
         long aktuelleZeit = System.currentTimeMillis();
         long tag          = 24 * 60 * 60 * 1000;

         if(server.getTimeLastSeen() == 0 || server.getTimeLastSeen() < aktuelleZeit - tag)
         {
            icon = IconManager.getInstance().getIcon("aelter24h");
         }
         else
         {
            icon = IconManager.getInstance().getIcon("juenger24h");

            //mehr Icons kommen, wenn der Core mehr kann
         }
      }

      label.setIcon(icon);

      return label;
   }
}
