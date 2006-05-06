package de.applejuicenet.client.gui.plugins.jabber.control;

public class MutliUserChatUser
{
   private String name;

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
}
