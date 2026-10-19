/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Settings.java,v 1.13 2009/02/01 14:45:03 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class Settings
{
   private boolean dirty;
   private boolean downloadOverview             = true;
   private boolean loadPlugins                    = true;
   private boolean enableToolTip                  = true;

   public Settings(Boolean downloadOverview, Boolean loadPlugins, Boolean enableToolTip)
   {
      if(downloadOverview != null)
      {
         this.downloadOverview = downloadOverview.booleanValue();
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

   public boolean isDownloadOverview()
   {
      return downloadOverview;
   }

   public void setDownloadOverview(boolean downloadOverview)
   {
      if(this.downloadOverview != downloadOverview)
      {
         dirty                   = true;
         this.downloadOverview = downloadOverview;
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
