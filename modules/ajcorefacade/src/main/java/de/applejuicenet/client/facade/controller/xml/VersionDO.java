/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.Version;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/VersionDO.java,v
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
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
class VersionDO extends Version
{
   private String versionNr;
   private int    operatingSystem;

   public VersionDO(String versionNr, int operatingSystem)
   {
      this.versionNr      = versionNr;
      this.operatingSystem = operatingSystem;
   }

   VersionDO snapshot()
   {
      return new VersionDO(versionNr, operatingSystem);
   }

   public VersionDO()
   {
   }

   public String getVersion()
   {
      return versionNr;
   }

   public int getOperatingSystem()
   {
      return operatingSystem;
   }

   public void setOperatingSystem(int operatingSystem)
   {
      this.operatingSystem = operatingSystem;
   }

   public void setVersion(String versionNr)
   {
      this.versionNr = versionNr;
   }
}
