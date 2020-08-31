/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.serverwatcher;


import java.util.Base64;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/serverwatcher/src/de/applejuicenet/client/gui/plugins/serverwatcher/ServerConfig.java,v 1.3 2009/01/12 10:19:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class ServerConfig
{
   private String bezeichnung;
   private String dyn;
   private String port;
   private String userpass;

   public ServerConfig(String bezeichnung, String dyn, String port, String userpass)
   {
      this.bezeichnung = bezeichnung;
      this.dyn         = dyn;
      this.port        = port;
      this.userpass    = userpass;
   }

   public ServerConfig(String dyn, String port, String userpass)
   {
      this.dyn      = dyn;
      bezeichnung   = "a";
      int value;

      for(int i = 0; i < dyn.length(); i++)
      {
         value = (int) dyn.charAt(i);
         if(value > 96 && value < 123)
         {
            bezeichnung += dyn.charAt(i);
         }
      }

      this.port     = port;
      this.userpass = Base64.getEncoder().withoutPadding().encodeToString(userpass.getBytes());
   }

   public String getBezeichnung()
   {
      return bezeichnung;
   }

   public String getDyn()
   {
      return dyn;
   }

   public String getPort()
   {
      return port;
   }

   public String getUserPass()
   {
      return userpass;
   }

   public String toString()
   {
      return dyn + ":" + port;
   }
}
