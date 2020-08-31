/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.entity.ShareEntry;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/ShareEntryDO.java,v
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
 * @author: Maj0r <aj@tkl-soft.de>
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
