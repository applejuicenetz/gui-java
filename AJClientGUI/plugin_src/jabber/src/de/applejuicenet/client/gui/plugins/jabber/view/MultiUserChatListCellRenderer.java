/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.view;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import de.applejuicenet.client.gui.plugins.jabber.control.MultiChatUserMode;
import de.applejuicenet.client.gui.plugins.jabber.control.MutliUserChatUser;

public class MultiUserChatListCellRenderer extends DefaultListCellRenderer
{
   public static ImageIcon owner;
   public static ImageIcon moderator;
   public static ImageIcon member;
   public static ImageIcon voice;

   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
   {
      MultiChatUserMode mode = ((MutliUserChatUser) value).getHighestMode();
      JLabel            comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      if(mode == MultiChatUserMode.MODERATOR)
      {
         comp.setIcon(moderator);
      }
      else if(mode == MultiChatUserMode.OWNER)
      {
         comp.setIcon(owner);
      }
      else if(mode == MultiChatUserMode.MEMBER)
      {
         comp.setIcon(member);
      }
      else if(mode == MultiChatUserMode.VOICE)
      {
         comp.setIcon(voice);
      }
      else if(mode == MultiChatUserMode.NOTHING)
      {
         comp.setIcon(null);
      }

      return comp;
   }
}
