/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.control;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

public class MultiUserChatUserListModel extends AbstractListModel
{
   private List<MutliUserChatUser> users = new ArrayList<MutliUserChatUser>();

   public Object getElementAt(int index)
   {
      return users.get(index);
   }

   public int getSize()
   {
      return users.size();
   }

   public void addParticipant(MutliUserChatUser participant)
   {
      users.add(participant);
   }

   public MutliUserChatUser getMutliUserChatUserByName(String name)
   {
      for(MutliUserChatUser curMutliUserChatUser : users)
      {
         if(curMutliUserChatUser.getName().equals(name))
         {
            return curMutliUserChatUser;
         }
      }

      return null;
   }

   public void removeParticipant(MutliUserChatUser participant)
   {
      users.remove(participant);
   }
}
