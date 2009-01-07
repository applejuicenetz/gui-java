/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.ircplugin;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/UserListCellRenderer.java,v 1.12 2009/01/07 15:21:33 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class UserListCellRenderer extends JLabel implements ListCellRenderer
{
   public UserListCellRenderer()
   {
      setOpaque(true);
   }

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
   {
      setFont(list.getFont());
      if(value != null)
      {
         User user = (User) value;

         setText(user.getName());
         if(isSelected)
         {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
         }
         else
         {
            setForeground(list.getForeground());
            setBackground(list.getBackground());
         }

         if(user.isAdmin())
         {
            setIcon(IconManager.getInstance().getIcon("irc_red", true, this.getClass()));
            setToolTipText("Administrator");
         }
         else if(user.isOp())
         {
            setIcon(IconManager.getInstance().getIcon("irc_blue", true, this.getClass()));
            setToolTipText("Operator");
         }
         else if(user.isHalfop())
         {
            setIcon(IconManager.getInstance().getIcon("irc_green", true, this.getClass()));
            setToolTipText("Half-Operator");
         }
         else if(user.isVoice())
         {
            setIcon(IconManager.getInstance().getIcon("irc_yellow", true, this.getClass()));
            setToolTipText("Voice");
         }
         else
         {
            setIcon(null);
            setToolTipText(null);
         }
      }

      return this;
   }
}
