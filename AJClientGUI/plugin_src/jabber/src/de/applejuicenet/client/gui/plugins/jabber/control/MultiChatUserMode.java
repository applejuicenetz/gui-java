/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.control;

public enum MultiChatUserMode
{OWNER(0), MODERATOR(1), MEMBER(2), VOICE(3), NOTHING(4);private int value;

   MultiChatUserMode(int aValue)
   {
      value = aValue;
   }

   public Integer getValue()
   {
      return value;
   }

   public static MultiChatUserMode getByRole(String role)
   {
      if(role.equalsIgnoreCase("moderator"))
      {
         return MODERATOR;
      }
      else if(role.equalsIgnoreCase("member"))
      {
         return MEMBER;
      }
      else if(role.equalsIgnoreCase("participant"))
      {
         return VOICE;
      }
      else
      {
         return NOTHING;
      }
   }
}
