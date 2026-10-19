/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Copyright: General Public License
 *
 */
class DownloadDO implements Download, Cloneable
{
   private static DecimalFormat         formatter           = new DecimalFormat("###,##0.00");
   private final int                    id;
   private int                          shareId;
   private String                       hash;
   private long                          size;
   private long                          ready;
   private int                          status;
   private String                       filename;
   private String                       targetDirectory;
   private int                          powerDownload;
   private int                          temporaryFileNumber;
   private long                         oldSpeed;
   private long                         oldReady = -1;
   private String                       speedAsString;
   private Map<Integer, DownloadSource> sourceMap = new HashMap<Integer, DownloadSource>();

   DownloadDO snapshot()
   {
      try
      {
         DownloadDO copy = (DownloadDO) super.clone();
         copy.sourceMap = new HashMap<>();
         sourceMap.forEach((key, value) -> copy.sourceMap.put(key, ((DownloadSourceDO) value).snapshot()));
         return copy;
      }
      catch(CloneNotSupportedException ex)
      {
         throw new AssertionError(ex);
      }
   }

   public DownloadDO(int id)
   {
      this.id = id;
   }

   public DownloadDO(int id, int shareId, String hash, long size, long ready, int status, String filename, String targetDirectory,
                     int powerDownload, int temporaryFileNumber)
   {
      this.id                  = id;
      this.shareId             = shareId;
      this.hash                = hash;
      this.size             = size;
      this.ready               = ready;
      this.status              = status;
      this.filename            = filename;
      this.targetDirectory     = targetDirectory;
      this.powerDownload       = powerDownload;
      this.temporaryFileNumber = temporaryFileNumber;
   }

   public String getPercentLoadedAsString()
   {
      try
      {
         double temp = getPercentLoaded();

         return formatter.format(temp);
      }
      catch(Exception e)
      {
         return "";
      }
   }

   public double getPercentLoaded()
   {
      return size > 0 ? (double) ready * 100 / size : 0;
   }

   public DownloadSourceDO getSourceById(int sourceId)
   {
      if(sourceMap.containsKey(sourceId))
      {
         return (DownloadSourceDO) sourceMap.get(sourceId);
      }
      else
      {
         return null;
      }
   }

   public void addSource(DownloadSource downloadSource)
   {
      if(!sourceMap.containsKey(downloadSource.getId()))
      {
         sourceMap.put(downloadSource.getId(), downloadSource);
      }
   }

   public Map<Integer, DownloadSource> getSourcesMap()
   {
      return sourceMap;
   }

   public DownloadSource[] getSources()
   {
      DownloadSource[] sources = null;

      synchronized(sourceMap)
      {
         sources = sourceMap.values().toArray(new DownloadSource[sourceMap.size()]);
      }

      return sources;
   }

   public void removeSource(Integer id)
   {
      if(sourceMap.containsKey(id))
      {
         sourceMap.remove(id);
      }
   }

   public int getShareId()
   {
      return shareId;
   }

   public void setShareId(int shareId)
   {
      this.shareId = shareId;
   }

   public String getHash()
   {
      return hash;
   }

   public void setHash(String hash)
   {
      this.hash = hash;
   }

   public long getSize()
   {
      return size;
   }

   public void setSize(long size)
   {
      this.size = size;
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int newStatus)
   {
      if(status != newStatus)
      {
         status = newStatus;
      }
   }

   public String getFilename()
   {
      return filename;
   }

   public void setFilename(String newFilename)
   {
      if(filename == null || !filename.equals(newFilename))
      {
         filename = newFilename;
      }
   }

   public String getTargetDirectory()
   {
      return targetDirectory;
   }

   public void setTargetDirectory(String newTargetDirectory)
   {
      if(targetDirectory == null || !targetDirectory.equals(newTargetDirectory))
      {
         targetDirectory = newTargetDirectory;
      }
   }

   public int getPowerDownload()
   {
      return powerDownload;
   }

   public void setPowerDownload(int newPowerDownload)
   {
      if(powerDownload != newPowerDownload)
      {
         powerDownload = newPowerDownload;
      }
   }

   public int getId()
   {
      return id;
   }

   public int getTemporaryFileNumber()
   {
      return temporaryFileNumber;
   }

   public void setTemporaryFileNumber(int temporaryFileNumber)
   {
      this.temporaryFileNumber = temporaryFileNumber;
   }

   public long getReady()
   {
      return ready;
   }

   public void setReady(long newReady)
   {
      if(ready != newReady)
      {
         ready = newReady;
      }
   }

   public long getRemainingTime()
   {
      long speed = getSpeedInBytes();

      if(speed == 0)
      {
         return Long.MAX_VALUE;
      }

      return ((size - ready) / speed);
   }

   public String getRemainingTimeAsString()
   {
      try
      {
         long speed = getSpeedInBytes();

         if(speed == 0)
         {
            return "";
         }

         if(speed == oldSpeed && ready == oldReady)
         {
            return speedAsString;
         }

         oldSpeed = speed;
         oldReady = ready;
         long remainingTime = ((size - ready) / speed);
         long days = remainingTime / 86400;

         remainingTime -= days * 86400;
         long hours = remainingTime / 3600;

         remainingTime -= hours * 3600;
         long minutes = remainingTime / 60;

         remainingTime -= minutes * 60;

         StringBuilder temp = new StringBuilder();

         if(days < 10)
         {
            temp.append('0');
         }

         temp.append(days);
         temp.append(':');
         if(hours < 10)
         {
            temp.append('0');
         }

         temp.append(hours);
         temp.append(':');
         if(minutes < 10)
         {
            temp.append('0');
         }

         temp.append(minutes);
         temp.append(':');
         if(remainingTime < 10)
         {
            temp.append('0');
         }

         temp.append(remainingTime);
         speedAsString = temp.toString();
         return speedAsString;
      }
      catch(Exception e)
      {
         return "";
      }
   }

   public int getSpeedInBytes()
   {
      int speed = 0;

      synchronized(sourceMap)
      {
         for(DownloadSource curDownloadSourceDO : sourceMap.values())
         {
            speed += curDownloadSourceDO.getSpeed();
         }
      }

      return speed;
   }

   public long getAlreadyLoaded()
   {
      long loaded = ready;

      synchronized(sourceMap)
      {
         for(DownloadSource curDownloadSourceDO : sourceMap.values())
         {
            loaded += curDownloadSourceDO.getAlreadyLoaded();
         }
      }

      return loaded;
   }

   @Override
   public final boolean equals(Object obj)
   {
      if(!(obj instanceof Download))
      {
         return false;
      }

      return getId() == ((Download) obj).getId();
   }

   @Override
   public final int hashCode()
   {
      return Integer.hashCode(getId());
   }
}
