/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.entity.Version;

/**
 *
 * Copyright: General Public License
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
