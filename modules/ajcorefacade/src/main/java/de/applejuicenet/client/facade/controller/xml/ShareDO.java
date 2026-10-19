/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.Share;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/ShareDO.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Title: AppleJuice Client-GUI
 * </p>
 * <p>
 * Description: Official GUI for the appleJuice core developed by muhviehstarr
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
class ShareDO implements Share
{
   private final int id;
   private String    filename;
   private String    shortfilename;
   private long      size;
   private String    checksum;
   private int       priority;
   private long      lastAsked;
   private long      askCount;
   private long      searchCount;

   public ShareDO(int id)
   {
      this.id = id;
   }

   public ShareDO(int id, String filename, String shortfilename, long size, String checksum, int priority, long lastAsked,
                  long askCount, long searchCount)
   {
      this.id            = id;
      this.filename      = filename;
      this.shortfilename = shortfilename;
      this.size          = size;
      this.checksum      = checksum;
      this.priority    = priority;
      this.lastAsked     = lastAsked;
      this.askCount      = askCount;
      this.searchCount   = searchCount;
   }

   public void setFilename(String filename)
   {
      this.filename = filename;
   }

   public void setSize(long size)
   {
      this.size = size;
   }

   public void setChecksum(String checksum)
   {
      this.checksum = checksum;
   }

   public int getId()
   {
      return id;
   }

   public String getFilename()
   {
      return filename;
   }

   public long getSize()
   {
      return size;
   }

   public String getCheckSum()
   {
      return checksum;
   }

   public String getShortfilename()
   {
      return shortfilename;
   }

   public void setShortfilename(String shortfilename)
   {
      this.shortfilename = shortfilename;
   }

   public int getPriority()
   {
      return priority;
   }

   public void setPriority(int priority)
   {
      this.priority = priority;
   }

   public long getAskCount()
   {
      return askCount;
   }

   public void setAskCount(long askCount)
   {
      this.askCount = askCount;
   }

   public long getLastAsked()
   {
      return lastAsked;
   }

   public void setLastAsked(long lastAsked)
   {
      this.lastAsked = lastAsked;
   }

   public long getSearchCount()
   {
      return searchCount;
   }

   public void setSearchCount(long searchCount)
   {
      this.searchCount = searchCount;
   }

   public String getAjfspLink() {
      return String.format("ajfsp://file|%s|%s|%s/", getShortfilename(), getCheckSum(), getSize());
   }
}
