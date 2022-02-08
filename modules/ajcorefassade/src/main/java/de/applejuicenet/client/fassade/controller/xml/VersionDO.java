/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.entity.Version;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/VersionDO.java,v
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
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
class VersionDO extends Version
{
   private String versionNr;
   private int    betriebsSystem;

   public VersionDO(String versionNr, int betriebsSystem)
   {
      this.versionNr      = versionNr;
      this.betriebsSystem = betriebsSystem;
   }

   public VersionDO()
   {
   }

   public String getVersion()
   {
      return versionNr;
   }

   public int getBetriebsSystem()
   {
      return betriebsSystem;
   }

   public void setBetriebsSystem(int betriebsSystem)
   {
      this.betriebsSystem = betriebsSystem;
   }

   public void setVersion(String versionNr)
   {
      this.versionNr = versionNr;
   }
}
