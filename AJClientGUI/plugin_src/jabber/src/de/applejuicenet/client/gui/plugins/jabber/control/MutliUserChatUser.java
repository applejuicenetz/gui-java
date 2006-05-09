/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.control;

public class MutliUserChatUser
{
   private String            name;
   private MultiChatUserMode mode = MultiChatUserMode.NOTHING;

   public MutliUserChatUser(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public boolean equals(Object obj)
   {
      return name.equals(obj.toString());
   }

   @Override
   public String toString()
   {
      return name;
   }

   public void setMode(MultiChatUserMode mode)
   {
      this.mode = mode;
   }

   public MultiChatUserMode getHighestMode()
   {
      return mode;
   }
}
