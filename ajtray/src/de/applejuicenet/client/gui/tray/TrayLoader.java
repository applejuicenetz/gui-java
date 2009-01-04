/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.applejuicenet.client.gui.AppleJuiceDialog;

public class TrayLoader
{
   public boolean makeTray(Image image, String title, final AppleJuiceDialog dialog, final JMenuItem popupShowHideMenuItem,
                           final String zeigen, final Icon zeigenIcon, final String verstecken, final Icon versteckenIcon,
                           final JPopupMenu popup)
   {
      SystemTray     tray     = SystemTray.getSystemTray();

      final TrayIcon trayIcon = new TrayIcon(image, title, null);

      trayIcon.setImageAutoSize(true);
      trayIcon.addMouseListener(new MouseAdapter()
         {
            public void mouseReleased(MouseEvent e)
            {
               e.consume();
               if(!dialog.isVisible())
               {
                  popupShowHideMenuItem.setText(zeigen);
                  popupShowHideMenuItem.setIcon(zeigenIcon);
               }
               else
               {
                  popupShowHideMenuItem.setText(verstecken);
                  popupShowHideMenuItem.setIcon(versteckenIcon);
               }

               if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
               {
                  AppleJuiceDialog dialog = AppleJuiceDialog.getApp();

                  if(!dialog.isVisible())
                  {
                     dialog.setVisible(true);
                     dialog.toFront();
                  }
                  else
                  {
                     dialog.setVisible(false);
                  }
               }

               if(e.getButton() == MouseEvent.BUTTON3)
               {
                  if(popup.isVisible())
                  {
                     popup.setVisible(false);
                  }
                  else
                  {
                     popup.setLocation(e.getX(), e.getY());
                     popup.setInvoker(popup);
                     popup.setVisible(true);
                  }
               }
            }
         });

      try
      {
         tray.add(trayIcon);
      }
      catch(AWTException e)
      {
         return false;
      }

      return true;
   }
}
