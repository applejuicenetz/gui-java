/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.ShareEntry;

/**
 *
 * Copyright: General Public License
 *
 */
class ShareEntryDO implements ShareEntry
{
   private static final String sSUBDIRECTORY    = "subdirectory";
   private static final String sSINGLEDIRECTORY = "singledirectory";
   private String              dir;
   private SHAREMODE           shareMode;

   public ShareEntryDO(String dir, SHAREMODE shareMode)
   {
      this.dir       = dir;
      this.shareMode = shareMode;
   }

   public ShareEntryDO(String dir, String shareMode)
   {
      this.dir = dir;
      setShareMode(shareMode);
   }

   public String toString()
   {
      return dir;
   }

   public void setDir(String dir)
   {
      this.dir = dir;
   }

   public void setShareMode(SHAREMODE shareMode)
   {
      this.shareMode = shareMode;
   }

   private void setShareMode(String shareMode)
   {
      if(shareMode.compareToIgnoreCase(sSUBDIRECTORY) == 0)
      {
         this.shareMode = SHAREMODE.SUBDIRECTORY;
      }
      else if(shareMode.compareToIgnoreCase(sSINGLEDIRECTORY) == 0)
      {
         this.shareMode = SHAREMODE.SINGLEDIRECTORY;
      }
      else
      {
         this.shareMode = SHAREMODE.NOT_SHARED;
      }
   }

   public String getDir()
   {
      return dir;
   }

   public SHAREMODE getShareMode()
   {
      return shareMode;
   }
}
