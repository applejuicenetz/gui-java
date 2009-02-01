/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.shared;

import java.awt.Color;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Settings.java,v 1.13 2009/02/01 14:45:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class Settings
{
   private boolean dirty;
   private Color   downloadFertigHintergrundColor = Color.GREEN;
   private Color   quelleHintergrundColor         = new Color(255, 255, 150);
   private boolean farbenAktiv                    = true;
   private boolean downloadUebersicht             = true;
   private boolean loadPlugins                    = true;
   private boolean enableToolTip                  = true;

   public Settings(Boolean farbenAktiv, Color downloadFertigHintergrundColor, Color quelleHintergrundColor,
                   Boolean downloadUebersicht, Boolean loadPlugins, Boolean enableToolTip)
   {
      if(farbenAktiv != null)
      {
         this.farbenAktiv = farbenAktiv.booleanValue();
      }

      if(downloadFertigHintergrundColor != null)
      {
         this.downloadFertigHintergrundColor = downloadFertigHintergrundColor;
      }

      if(quelleHintergrundColor != null)
      {
         this.quelleHintergrundColor = quelleHintergrundColor;
      }

      if(downloadUebersicht != null)
      {
         this.downloadUebersicht = downloadUebersicht.booleanValue();
      }

      if(loadPlugins != null)
      {
         this.loadPlugins = loadPlugins.booleanValue();
      }

      if(enableToolTip != null)
      {
         this.enableToolTip = enableToolTip.booleanValue();
      }
   }
   
   public Settings()
   {
   }

   public static Settings getSettings()
   {
      return OptionsManagerImpl.getInstance().getSettings();
   }

   public boolean save()
   {
      if(dirty)
      {
         OptionsManagerImpl.getInstance().saveSettings(this);
         dirty = false;
         return true;
      }

      return false;
   }

   public Color getDownloadFertigHintergrundColor()
   {
      return downloadFertigHintergrundColor;
   }

   public void setDownloadFertigHintergrundColor(Color downloadFertigHintergrundColor)
   {
      if(this.downloadFertigHintergrundColor.getRGB() != downloadFertigHintergrundColor.getRGB())
      {
         dirty                               = true;
         this.downloadFertigHintergrundColor = downloadFertigHintergrundColor;
      }
   }

   public boolean isDownloadUebersicht()
   {
      return downloadUebersicht;
   }

   public Color getQuelleHintergrundColor()
   {
      return quelleHintergrundColor;
   }

   public void setDownloadUebersicht(boolean downloadUebersicht)
   {
      if(this.downloadUebersicht != downloadUebersicht)
      {
         dirty                   = true;
         this.downloadUebersicht = downloadUebersicht;
      }
   }

   public void setQuelleHintergrundColor(Color quelleHintergrundColor)
   {
      if(this.quelleHintergrundColor.getRGB() != quelleHintergrundColor.getRGB())
      {
         dirty                       = true;
         this.quelleHintergrundColor = quelleHintergrundColor;
      }
   }

   public boolean isFarbenAktiv()
   {
      return farbenAktiv;
   }

   public void setFarbenAktiv(boolean farbenAktiv)
   {
      if(this.farbenAktiv != farbenAktiv)
      {
         dirty            = true;
         this.farbenAktiv = farbenAktiv;
      }
   }

   public boolean isDirty()
   {
      return dirty;
   }

   public boolean shouldLoadPluginsOnStartup()
   {
      return loadPlugins;
   }

   public void loadPluginsOnStartup(boolean loadPluginsOnStartup)
   {
      if(loadPlugins != loadPluginsOnStartup)
      {
         dirty       = true;
         loadPlugins = loadPluginsOnStartup;
      }
   }

   public boolean isToolTipEnabled()
   {
      return enableToolTip;
   }

   public void enableToolTipEnabled(boolean enable)
   {
      this.enableToolTip = enable;
      dirty              = true;
   }
}
