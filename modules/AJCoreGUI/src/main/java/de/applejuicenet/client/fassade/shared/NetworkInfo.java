/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;


/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/NetworkInfo.java,v
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
public class NetworkInfo
{
   private long    ajUserGesamt;
   private long    ajAnzahlDateien;
   private String  ajGesamtShare;
   private boolean firewalled;
   private String  externeIP;
   private int     tryConnectToServer;
   private int     connectedWithServerId;
   private String  welcomeMessage;
   private long    connectionTime;

   public NetworkInfo()
   {
   }

   public NetworkInfo(long ajUserGesamt, long ajAnzahlDateien, String ajGesamtShare, boolean firewalled, String externeIP,
                      int tryConnectToServer, int connectedWithServerId, String welcomeMessage, long connectionTime)
   {
      this.ajUserGesamt          = ajUserGesamt;
      this.ajAnzahlDateien       = ajAnzahlDateien;
      this.ajGesamtShare         = ajGesamtShare;
      this.firewalled            = firewalled;
      this.externeIP             = externeIP;
      this.tryConnectToServer    = tryConnectToServer;
      this.connectedWithServerId = connectedWithServerId;
      this.connectionTime        = connectionTime;
      setWelcomeMessage(welcomeMessage);
   }

   public NetworkInfo(String ajUserGesamt, String ajAnzahlDateien, String ajGesamtShare, boolean firewalled, String externeIP,
                      int tryConnectToServer, int connectedWithServerId)
   {
      if(ajUserGesamt == null || ajUserGesamt.length() == 0)
      {
         this.ajUserGesamt = 0;
      }
      else
      {
         this.ajUserGesamt = Long.parseLong(ajUserGesamt);
      }

      if(ajAnzahlDateien == null || ajAnzahlDateien.length() == 0)
      {
         this.ajAnzahlDateien = 0;
      }
      else
      {
         this.ajAnzahlDateien = Long.parseLong(ajAnzahlDateien);
      }

      this.ajGesamtShare         = ajGesamtShare;
      this.firewalled            = firewalled;
      this.externeIP             = externeIP;
      this.tryConnectToServer    = tryConnectToServer;
      this.connectedWithServerId = connectedWithServerId;
   }

   public void setAjUserGesamt(long ajUserGesamt)
   {
      this.ajUserGesamt = ajUserGesamt;
   }

   public void setAjAnzahlDateien(long ajAnzahlDateien)
   {
      this.ajAnzahlDateien = ajAnzahlDateien;
   }

   public void setAjGesamtShare(String ajGesamtShare)
   {
      this.ajGesamtShare = ajGesamtShare;
   }

   public void setFirewalled(boolean firewalled)
   {
      this.firewalled = firewalled;
   }

   public void setExterneIP(String externeIP)
   {
      this.externeIP = externeIP;
   }

   public void setTryConnectToServer(int tryConnectToServer)
   {
      this.tryConnectToServer = tryConnectToServer;
   }

   public void setConnectedWithServerId(int connectedWithServerId)
   {
      this.connectedWithServerId = connectedWithServerId;
   }

   public void setWelcomeMessage(String welcomeMessage)
   {
      if(welcomeMessage.toLowerCase().indexOf(StringConstants.HTML_START) == -1)
      {
         welcomeMessage = StringConstants.HTML_START + welcomeMessage + StringConstants.HTML_END;
      }

      this.welcomeMessage = welcomeMessage;
   }

   public void setConnectionTime(long connectionTime)
   {
      this.connectionTime = connectionTime;
   }

   public String getAJGesamtShare(long faktor)
   {
      ajGesamtShare = ajGesamtShare.replace(',', '.');
      double share = Double.parseDouble(ajGesamtShare);

      if(share == 0)
      {
         return StringConstants._0_00_MB;
      }

      if(faktor == 0)
      { // selbst entscheiden
         if(share / 1024 < 1024)
         {
            faktor = 1024;
         }
         else if(share / 1048576 < 1024)
         {
            faktor = 1048576;
         }
         else
         {
            faktor = 1;
         }
      }

      share = share / faktor;
      String result = Double.toString(share);

      if(result.indexOf(StringConstants.POINT) + 3 < result.length())
      {
         result = result.substring(0, result.indexOf(StringConstants.POINT) + 3);
      }

      result = result.replace('.', ',');
      if(faktor == 1)
      {
         result += StringConstants.MB;
      }
      else if(faktor == 1024)
      {
         result += StringConstants.GB;
      }
      else if(faktor == 1048576)
      {
         result += StringConstants.TB;
      }
      else
      {
         result += StringConstants.TWO_QUESTIONMARKS;
      }

      return result;
   }

   public String getAJGesamtShareWithPoints(long faktor)
   {
      String result = getAJGesamtShare(faktor);

      return insertPoints(result);
   }

   public boolean isFirewalled()
   {
      return firewalled;
   }

   public String getExterneIP()
   {
      return externeIP;
   }

   public long getAJUserGesamt()
   {
      return ajUserGesamt;
   }

   public String getAJUserGesamtAsString()
   {
      return Long.toString(ajUserGesamt);
   }

   public String getAJUserGesamtAsStringWithPoints()
   {
      String result = getAJUserGesamtAsString();

      return insertPoints(result);
   }

   public long getAJAnzahlDateien()
   {
      return ajAnzahlDateien;
   }

   public String getAJAnzahlDateienAsString()
   {
      return Long.toString(ajAnzahlDateien);
   }

   public String getAJAnzahlDateienAsStringWithPoints()
   {
      return insertPoints(Long.toString(ajAnzahlDateien));
   }

   private String insertPoints(String tochange)
   {
      StringBuffer result = new StringBuffer(tochange);
      int          laenge;

      if(result.indexOf(StringConstants.COMMA) == -1)
      {
         if(result.indexOf(StringConstants.SPACE) == -1)
         {
            laenge = result.length();
         }
         else
         {
            laenge = result.indexOf(StringConstants.SPACE);
         }
      }
      else
      {
         laenge = result.indexOf(StringConstants.COMMA);
      }

      int zaehler = 0;

      for(int i = laenge - 1; i > 0; i--)
      {
         zaehler++;
         if(zaehler == 3)
         {
            zaehler = 0;
            result.insert(i, '.');
         }
      }

      return result.toString();
   }

   public int getTryConnectToServer()
   {
      return tryConnectToServer;
   }

   public int getConnectedWithServerId()
   {
      return connectedWithServerId;
   }

   public String getWelcomeMessage()
   {
      if(welcomeMessage == null)
      {
         return StringConstants.EMPTY;
      }

      return welcomeMessage;
   }

   public long getConnectionTime()
   {
      return connectionTime;
   }
}
