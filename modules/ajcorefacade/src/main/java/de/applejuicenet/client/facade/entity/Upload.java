/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.facade.entity;

import java.text.DecimalFormat;

public abstract class Upload implements IdOwner
{
   public static final int      ACTIVE_TRANSFER      = 1;
   public static final int      QUEUE            = 2;
   public static final int      STATE_UNKNOWN          = 0;
   public static final int      STATE_DIRECT_CONNECTED   = 1;
   public static final int      STATE_INDIRECT_CONNECTED = 2;
   private static DecimalFormat formatter                = new DecimalFormat("###,##0.00");

   public abstract int getId();

   public abstract int getShareFileID();

   public abstract Version getVersion();

   public abstract int getStatus();

   public abstract int getDirectState();

   public abstract String getNick();

   public abstract long getUploadFrom();

   public abstract long getUploadTo();

   public abstract long getActualUploadPosition();

   public abstract int getSpeed();

   public abstract int getPriority();

   public abstract String getFileName();

   public abstract long getLastConnection();

   public abstract double getLoaded();

   public final String getUploadIDAsString()
   {
      return Integer.toString(getId());
   }

   public final String getShareFileIDAsString()
   {
      return Integer.toString(getShareFileID());
   }

   public final String getStatusAsString()
   {
      return Integer.toString(getStatus());
   }

   public final double getDownloadPercent()
   {
      if(getActualUploadPosition() == -1 || getUploadFrom() == -1 || getSize() <= 0)
      {
         return 0;
      }

      double temp = getActualUploadPosition() - getUploadFrom();

      temp = temp * 100 / getSize();
      
      return temp < 0 ? 0 : temp;
   }

   public final String getDownloadPercentAsString()
   {
      return formatter.format(getDownloadPercent());
   }

   public final String getLoadedPercentAsString()
   {
      double loaded = getLoaded();

      return formatter.format( getLoaded());
   }

   public final long getSize()
   {
      if(getUploadTo() == -1 || getUploadFrom() == -1)
      {
         return 0;
      }

      return getUploadTo() - getUploadFrom();
   }

   @Override
   public final boolean equals(Object obj)
   {
      if(!(obj instanceof Upload))
      {
         return false;
      }

      return getId() == ((Upload) obj).getId();
   }

   @Override
   public final int hashCode()
   {
      return Integer.hashCode(getId());
   }
}
