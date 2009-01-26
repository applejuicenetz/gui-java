/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.fassade.controller.xml;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/DownloadDO.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
class DownloadDO implements Download
{
   private static DecimalFormat        formatter           = new DecimalFormat("###,##0.00");
   private final int                   id;
   private int                         shareId;
   private String                      hash;
   private int                        groesse;
   private int                        ready;
   private int                         status;
   private String                      filename;
   private String                      targetDirectory;
   private int                         powerDownload;
   private int                         temporaryFileNumber;
   private long                        oldSpeed;
   private String                      speedAsString;
   private Map<String, DownloadSource> sourcen             = new HashMap<String, DownloadSource>();

   public DownloadDO(int id)
   {
      this.id = id;
   }

   public DownloadDO(int id, int shareId, String hash, int groesse, int ready, int status, String filename,
                     String targetDirectory, int powerDownload, int temporaryFileNumber)
   {
      this.id                  = id;
      this.shareId             = shareId;
      this.hash                = hash;
      this.groesse             = groesse;
      this.ready               = ready;
      this.status              = status;
      this.filename            = filename;
      this.targetDirectory     = targetDirectory;
      this.powerDownload       = powerDownload;
      this.temporaryFileNumber = temporaryFileNumber;
   }

   public String getProzentGeladenAsString()
   {
      try
      {
         double temp = getProzentGeladen();

         return formatter.format(temp);
      }
      catch(Exception e)
      {
         return "";
      }
   }

   public double getProzentGeladen()
   {
      return (double) ready * 100 / groesse;
   }

   public DownloadSourceDO getSourceById(int sourceId)
   {
      String key = Integer.toString(sourceId);

      if(sourcen.containsKey(key))
      {
         return (DownloadSourceDO) sourcen.get(key);
      }
      else
      {
         return null;
      }
   }

   public void addSource(DownloadSourceDO downloadSourceDO)
   {
      String key = Integer.toString(downloadSourceDO.getId());

      if(!sourcen.containsKey(key))
      {
         sourcen.put(key, downloadSourceDO);
      }
   }

   public Map<String, DownloadSource> getSourcesMap()
   {
      return sourcen;
   }

   public DownloadSource[] getSources()
   {
      DownloadSource[] sources = null;

      synchronized(sourcen)
      {
         sources = (DownloadSource[]) sourcen.values().toArray(new DownloadSource[sourcen.size()]);
      }

      return sources;
   }

   public void removeSource(String id)
   {
      String key = id;

      if(sourcen.containsKey(key))
      {
         sourcen.remove(key);
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

   public int getGroesse()
   {
      return groesse;
   }

   public void setGroesse(int groesse)
   {
      this.groesse = groesse;
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

   public int getReady()
   {
      return ready;
   }

   public void setReady(int newReady)
   {
      if(ready != newReady)
      {
         ready = newReady;
      }
   }

   public long getRestZeit()
   {
      long speed = getSpeedInBytes();

      if(speed == 0)
      {
         return Long.MAX_VALUE;
      }

      return ((groesse - ready) / speed);
   }

   public String getRestZeitAsString()
   {
      try
      {
         long speed = getSpeedInBytes();

         if(speed == 0)
         {
            return "";
         }

         if(speed == oldSpeed)
         {
            return speedAsString;
         }

         oldSpeed = speed;
         int restZeit = (int) ((groesse - ready) / speed);
         int tage     = restZeit / 86400;

         restZeit -= tage * 86400;
         int stunden = restZeit / 3600;

         restZeit -= stunden * 3600;
         int minuten = restZeit / 60;

         restZeit -= minuten * 60;

         StringBuilder temp = new StringBuilder();

         if(tage < 10)
         {
            temp.append('0');
         }

         temp.append(tage);
         temp.append(':');
         if(stunden < 10)
         {
            temp.append('0');
         }

         temp.append(stunden);
         temp.append(':');
         if(minuten < 10)
         {
            temp.append('0');
         }

         temp.append(minuten);
         temp.append(':');
         if(restZeit < 10)
         {
            temp.append('0');
         }

         temp.append(restZeit);
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

      synchronized(sourcen)
      {
         for(DownloadSource curDownloadSourceDO : sourcen.values())
         {
            speed += curDownloadSourceDO.getSpeed();
         }
      }

      return speed;
   }

   public int getBereitsGeladen()
   {
      int geladen = ready;

      synchronized(sourcen)
      {
         for(DownloadSource curDownloadSourceDO : sourcen.values())
         {
            geladen += curDownloadSourceDO.getBereitsGeladen();
         }
      }

      return geladen;
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
}
