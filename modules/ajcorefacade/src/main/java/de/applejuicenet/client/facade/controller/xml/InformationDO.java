/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.Information;
import de.applejuicenet.client.facade.entity.Server;

/**
 *
 * Copyright: General Public License
 *
 */
class InformationDO extends Information implements Cloneable
{
   InformationDO snapshot()
   {
      try
      {
         InformationDO copy = (InformationDO) super.clone();
         copy.server = server == null ? null : ((ServerDO) server).snapshot();
         return copy;
      }
      catch(CloneNotSupportedException ex)
      {
         throw new AssertionError(ex);
      }
   }

   private int    connectionStatus = NOT_CONNECTED;
   private int    id;
   private long   sessionUpload;
   private long   sessionDownload;
   private long   credits;
   private long   uploadSpeed;
   private long   downloadSpeed;
   private long   openConnections;
   private long   maxUploadPositions;
   private String serverName;
   private String externalIP;
   private Server server;

   public InformationDO()
   {
   }

   public InformationDO(int id, long sessionUpload, long sessionDownload, long credits, long uploadSpeed, long downloadSpeed,
                        long openConnections, long maxUploadPositions)
   {
      this.id                 = id;
      this.sessionUpload      = sessionUpload;
      this.sessionDownload    = sessionDownload;
      this.credits            = credits;
      this.uploadSpeed        = uploadSpeed;
      this.downloadSpeed      = downloadSpeed;
      this.openConnections    = openConnections;
      this.maxUploadPositions = maxUploadPositions;
   }

   public int getId()
   {
      return id;
   }

   public long getSessionUpload()
   {
      return sessionUpload;
   }

   public long getSessionDownload()
   {
      return sessionDownload;
   }

   public long getCredits()
   {
      return credits;
   }

   public long getUploadSpeed()
   {
      return uploadSpeed;
   }

   public long getDownloadSpeed()
   {
      return downloadSpeed;
   }

   public long getOpenConnections()
   {
      return openConnections;
   }

   public void setExternalIP(String externalIP)
   {
      this.externalIP = externalIP;
   }

   public void setSessionUpload(long sessionUpload)
   {
      this.sessionUpload = sessionUpload;
   }

   public void setSessionDownload(long sessionDownload)
   {
      this.sessionDownload = sessionDownload;
   }

   public void setCredits(long credits)
   {
      this.credits = credits;
   }

   public void setServer(Server server)
   {
      this.server = server;
   }

   public Server getServer()
   {
      return server;
   }

   public void setConnectionStatus(int connectionStatus)
   {
      this.connectionStatus = connectionStatus;
      if(connectionStatus == NOT_CONNECTED)
      {
         this.serverName = "";
      }
   }

   public void setUploadSpeed(long uploadSpeed)
   {
      this.uploadSpeed = uploadSpeed;
   }

   public void setDownloadSpeed(long downloadSpeed)
   {
      this.downloadSpeed = downloadSpeed;
   }

   public void setOpenConnections(long openConnections)
   {
      this.openConnections = openConnections;
   }

   public void setMaxUploadPositions(long maxUploadPositions)
   {
      this.maxUploadPositions = maxUploadPositions;
   }

   public String getServerName()
   {
      return serverName;
   }

   public int getConnectionStatus()
   {
      return connectionStatus;
   }

   public String getExternalIP()
   {
      return externalIP;
   }

   public long getMaxUploadPositions()
   {
      return maxUploadPositions;
   }
}
