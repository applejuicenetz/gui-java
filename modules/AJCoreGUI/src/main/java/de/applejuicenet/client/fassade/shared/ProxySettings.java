/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;


import java.util.Base64;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/ProxySettings.java,v
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
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class ProxySettings
{
   private String  host;
   private int     port;
   private String  userpass;
   private boolean use;

   public ProxySettings(boolean use, String host, int port, String user, String pass)
   {
      this.host = host;
      this.port = port;
      this.use  = use;
      setUserpass(user, pass);
   }

   public ProxySettings(boolean use, String host, int port, String userpass)
   {
      this.host     = host;
      this.port     = port;
      this.use      = use;
      this.userpass = userpass;
   }

   public String getHost()
   {
      return host;
   }

   public int getPort()
   {
      return port;
   }

   public String getUserpass()
   {
      return userpass;
   }

   public void setUserpass(String user, String passwort)
   {
      Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding();
      this.userpass = base64Encoder.encodeToString((user + ":" + passwort).getBytes());
   }

   public boolean isUse()
   {
      return use;
   }

   public void setUse(boolean use)
   {
      this.use = use;
   }

   public void setHost(String host)
   {
      this.host = host;
   }

   public void setPort(int port)
   {
      this.port = port;
   }
}
