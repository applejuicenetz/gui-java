/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.entity.DownloadSource;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/DownloadSourceDO.java,v
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
class DownloadSourceDO extends DownloadSource
{
   private final int id;
   private int       status;
   private int       directstate;
   private int       downloadFrom;
   private int       downloadTo;
   private int       actualDownloadPosition;
   private int       speed;
   private VersionDO version                = null;
   private int       queuePosition;
   private int       powerDownload;
   private String    filename;
   private String    nickname;
   private int       downloadId;
   private int       oldSize;
   private String    sizeAsString;
   private int       oldBereitsGeladen;
   private String    bereitsGeladenAsString;
   private int       oldNochZuLaden;
   private String    nochZuLadenAsString;
   private boolean   progressChanged        = false;
   private boolean   versionChanged         = false;
   private int       herkunft;

   public DownloadSourceDO(int id)
   {
      this.id = id;
   }

   public DownloadSourceDO(int id, int status, int directstate, int downloadFrom, int downloadTo, int actualDownloadPosition,
                           int speed, VersionDO version, int queuePosition, int powerDownload, String filename, String nickname,
                           int downloadId, int herkunft)
   {
      this.id                     = id;
      this.status                 = status;
      this.directstate            = directstate;
      this.downloadFrom           = downloadFrom;
      this.downloadTo             = downloadTo;
      this.actualDownloadPosition = actualDownloadPosition;
      this.speed                  = speed;
      this.version                = version;
      this.queuePosition          = queuePosition;
      this.powerDownload          = powerDownload;
      this.filename               = filename;
      this.nickname               = nickname;
      this.downloadId             = downloadId;
      progressChanged             = true;
      versionChanged              = true;
      this.herkunft               = herkunft;
   }

   public int getStatus()
   {
      return status;
   }

   public int getSize()
   {
      if(downloadTo == -1 || downloadFrom == -1)
      {
         return 0;
      }

      return downloadTo - downloadFrom;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public int getHerkunft()
   {
      return herkunft;
   }

   public void setHerkunft(int herkunft)
   {
      this.herkunft = herkunft;
   }

   public int getDirectstate()
   {
      return directstate;
   }

   public void setDirectstate(int directstate)
   {
      this.directstate = directstate;
   }

   public int getDownloadFrom()
   {
      return downloadFrom;
   }

   public void setDownloadFrom(int downloadFrom)
   {
      this.downloadFrom = downloadFrom;
      progressChanged   = true;
   }

   public int getDownloadTo()
   {
      return downloadTo;
   }

   public void setDownloadTo(int downloadTo)
   {
      this.downloadTo = downloadTo;
      progressChanged = true;
   }

   public int getActualDownloadPosition()
   {
      return actualDownloadPosition;
   }

   public void setActualDownloadPosition(int actualDownloadPosition)
   {
      this.actualDownloadPosition = actualDownloadPosition;
      progressChanged             = true;
   }

   public int getSpeed()
   {
      return speed;
   }

   public void setSpeed(int speed)
   {
      this.speed = speed;
   }

   public VersionDO getVersion()
   {
      return version;
   }

   public void setVersion(VersionDO version)
   {
      this.version   = version;
      versionChanged = true;
   }

   public int getQueuePosition()
   {
      return queuePosition;
   }

   public void setQueuePosition(int queuePosition)
   {
      this.queuePosition = queuePosition;
   }

   public int getPowerDownload()
   {
      return powerDownload;
   }

   public void setPowerDownload(int powerDownload)
   {
      this.powerDownload = powerDownload;
   }

   public String getFilename()
   {
      return filename;
   }

   public void setFilename(String filename)
   {
      this.filename = filename;
   }

   public String getNickname()
   {
      return nickname;
   }

   public void setNickname(String nickname)
   {
      this.nickname = nickname;
   }

   public int getId()
   {
      return id;
   }

   public int getDownloadId()
   {
      return downloadId;
   }

   public void setDownloadId(int downloadId)
   {
      this.downloadId = downloadId;
   }
}
