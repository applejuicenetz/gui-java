package de.applejuicenet.client.gui.plugins.jabber.control;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

public class MultiUserChatUserListModel extends AbstractListModel
{
   private List<String> users = new ArrayList<String>();

   public Object getElementAt(int index)
   {
      return users.get(index);
   }

   public int getSize()
   {
      return users.size();
   }

   public void addParticipant(String participant)
   {
      users.add(participant);
   }
}
