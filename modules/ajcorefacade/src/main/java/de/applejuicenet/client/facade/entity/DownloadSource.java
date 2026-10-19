/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.entity;

import java.text.DecimalFormat;

public abstract class DownloadSource implements IdOwner
{

   // Status - IDs
   public static final int UNASKED                            = 1;
   public static final int TRYING_TO_CONNECT                = 2;
   public static final int PEER_HAS_OLD_VERSION      = 3;
   public static final int PEER_CANNOT_OPEN_FILE = 4;
   public static final int IN_QUEUE                     = 5;
   public static final int NO_USABLE_PARTS              = 6;
   public static final int TRANSFER                         = 7;
   public static final int NOT_ENOUGH_SPACE_ON_DISK = 8;
   public static final int COMPLETED                       = 9;
   public static final int NO_CONNECTION_POSSIBLE            = 11;
   public static final int TRYING_INDIRECT                    = 12;
   public static final int PAUSED                             = 13;
   public static final int QUEUE_FULL                   = 14;
   public static final int OWN_LIMIT_REACHED               = 15;
   public static final int INDIRECT_CONNECTION_REJECTED       = 16;

   // directstate - IDs
   public static final int UNKNOWN                         = 0;
   public static final int DIRECT_CONNECTION                = 1;
   public static final int INDIRECT_CONNECTION_UNCONFIRMED = 2;
   public static final int INDIRECT_CONNECTION              = 3;

   // herkunft
   public static final int      SOURCE_AJFSP                = 1;
   public static final int      SOURCE_CLIENT2CLIENT        = 2;
   public static final int      SOURCE_UPLOADLIST           = 3;
   public static final int      SOURCE_LAST_START           = 4;
   public static final int      SOURCE_TEXTSEARCH           = 5;
   public static final int      SOURCE_SERVER_SOURCE_SEARCH = 6;
   private static DecimalFormat formatter                   = new DecimalFormat("###,##0.00");

   public abstract int getStatus();

   public abstract long getSize();

   public abstract int getOrigin();

   public abstract int getDirectstate();

   public abstract long getDownloadFrom();

   public abstract long getDownloadTo();

   public abstract long getActualDownloadPosition();

   public abstract int getSpeed();

   public abstract Version getVersion();

   public abstract int getQueuePosition();

   public abstract int getPowerDownload();

   public abstract String getFilename();

   public abstract String getNickname();

   public abstract int getId();

   public abstract int getDownloadId();

   public final double getReadyPercent()
   {
      if(getActualDownloadPosition() == -1 || getDownloadFrom() == -1 || getSize() <= 0)
      {
         return 0;
      }

      return (double) (getActualDownloadPosition() - getDownloadFrom()) * 100 / getSize();
   }

   public final String getDownloadPercentAsString()
   {
      try
      {
         if(getActualDownloadPosition() == -1 || getDownloadFrom() == -1 || getSize() <= 0)
         {
            return "0";
         }

         double temp = getActualDownloadPosition() - getDownloadFrom();

         temp = temp * 100 / getSize();
         return formatter.format(temp);
      }
      catch(Exception e)
      {
         return "";
      }
   }

   public final String getRemainingTimeAsString()
   {
      try
      {
         int speed = getSpeed();

         if(speed == 0 || speed == -1)
         {
            return "";
         }

         long remainingTime = getRemainingToLoad() / speed;
         long days     = remainingTime / 86400;

         remainingTime -= days * 86400;
         long hours = remainingTime / 3600;

         remainingTime -= hours * 3600;
         long minutes = remainingTime / 60;

         remainingTime -= minutes * 60;

         StringBuffer temp = new StringBuffer();

         if(days < 10)
         {
            temp.append('0');
         }

         temp.append(Long.toString(days));
         temp.append(':');
         if(hours < 10)
         {
            temp.append('0');
         }

         temp.append(Long.toString(hours));
         temp.append(':');
         if(minutes < 10)
         {
            temp.append('0');
         }

         temp.append(Long.toString(minutes));
         temp.append(':');
         if(remainingTime < 10)
         {
            temp.append('0');
         }

         temp.append(Long.toString(remainingTime));
         return temp.toString();
      }
      catch(Exception e)
      {
         return "";
      }
   }

   public final long getAlreadyLoaded()
   {
      if(getActualDownloadPosition() == -1 || getDownloadFrom() == -1)
      {
         return 0;
      }

      return getActualDownloadPosition() - getDownloadFrom();
   }

   public final long getRemainingToLoad()
   {
      if(getDownloadTo() == -1 || getActualDownloadPosition() == -1)
      {
         return 0;
      }

      return getDownloadTo() - getActualDownloadPosition();
   }

   @Override
   public final boolean equals(Object obj)
   {
      if(!(obj instanceof DownloadSource))
      {
         return false;
      }

      return getId() == ((DownloadSource) obj).getId();
   }

   @Override
   public final int hashCode()
   {
      return Integer.hashCode(getId());
   }
}
