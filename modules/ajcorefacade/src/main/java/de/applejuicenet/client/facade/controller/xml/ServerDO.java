/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.Server;

/**
 *
 * Copyright: General Public License
 *
 */
class ServerDO extends Server implements Cloneable
{
   ServerDO snapshot()
   {
      try
      {
         return (ServerDO) super.clone();
      }
      catch(CloneNotSupportedException ex)
      {
         throw new AssertionError(ex);
      }
   }

   private final int id;
   private String    host;
   private String    name;
   private String    port;
   private long      timeLastSeen;
   private int       attempts;
   private boolean   connected  = false;
   private boolean   tryConnect = false;

   public ServerDO(int id)
   {
      this.id = id;
   }

   public ServerDO(int id, String name, String host, String port, long lastSeen, int attempts)
   {
      this.id       = id;
      this.name     = name;
      this.host     = host;
      this.port     = port;
      this.attempts = attempts;
      timeLastSeen  = lastSeen;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getHost()
   {
      return host;
   }

   public void setHost(String host)
   {
      this.host = host;
   }

   public String getPort()
   {
      return port;
   }

   public void setPort(String port)
   {
      this.port = port;
   }

   public long getTimeLastSeen()
   {
      return timeLastSeen;
   }

   public int getAttempts()
   {
      return attempts;
   }

   public void setAttempts(int attempts)
   {
      this.attempts = attempts;
   }

   public void setTimeLastSeen(long timeLastSeen)
   {
      this.timeLastSeen = timeLastSeen;
   }

   public int getId()
   {
      return id;
   }

   public boolean isConnected()
   {
      return connected;
   }

   public void setConnected(boolean connected)
   {
      this.connected = connected;
   }

   public boolean isTryConnect()
   {
      return tryConnect;
   }

   public void setTryConnect(boolean tryConnect)
   {
      this.tryConnect = tryConnect;
   }
}
