/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui;

import java.util.Set;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.download.DownloadController;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.plugincontrol.PluginFactory;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.search.SearchController;
import de.applejuicenet.client.gui.server.ServerPanel;
import de.applejuicenet.client.gui.share.ShareController;
import de.applejuicenet.client.gui.start.StartController;
import de.applejuicenet.client.gui.upload.UploadController;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/RegisterPanel.java,v 1.60 2009/01/11 22:04:04 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class RegisterPanel extends JTabbedPane implements LanguageListener
{
   private StartController    startController;
   private DownloadController downloadController;
   private SearchController   searchController;
   private UploadController   uploadController;
   private ServerPanel        serverPanel;
   private ShareController    shareController;
   private AppleJuiceDialog   parent;
   private Logger             logger;

   public RegisterPanel(AppleJuiceDialog parent)
   {
      logger = Logger.getLogger(getClass());
      try
      {
         this.parent = parent;
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private void tabFocusLost(int index)
   {
      RegisterI register = (RegisterI) getComponentAt(index);

      if(register != null)
      {
         register.lostSelection();
      }
   }

   private void init()
   {
      LanguageSelector.getInstance().addLanguageListener(this);
      setModel(new DefaultSingleSelectionModel()
         {
            public void setSelectedIndex(int index)
            {
               int oldIndex = getSelectedIndex();

               if(oldIndex != -1)
               {
                  tabFocusLost(oldIndex);
               }

               super.setSelectedIndex(index);
            }
         });
      startController = StartController.getInstance();
      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(40, "Lade Sharepanel...");
      }

      shareController = ShareController.getInstance();
      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(50, "Lade Downloadpanel...");
      }

      downloadController = DownloadController.getInstance();
      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(60, "Lade Uploadpanel...");
      }

      uploadController = UploadController.getInstance();
      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(70, "Lade Searchpanel...");
      }

      searchController = SearchController.getInstance();
      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(80, "Lade Serverpanel...");
      }

      serverPanel = ServerPanel.getInstance();

      IconManager im   = IconManager.getInstance();

      ImageIcon   icon = im.getIcon("start");

      addTab("Start", icon, startController.getComponent());

      ImageIcon icon6 = im.getIcon("meinshare");

      addTab("Share", icon6, shareController.getComponent());

      ImageIcon icon2 = im.getIcon("suchen");

      addTab("Suchen", icon2, searchController.getComponent());

      ImageIcon icon3 = im.getIcon("download");

      addTab("Download", icon3, downloadController.getComponent());

      ImageIcon icon4 = im.getIcon("upload");

      addTab("Upload", icon4, uploadController.getComponent());

      ImageIcon icon5 = im.getIcon("server");

      addTab("Server", icon5, serverPanel);

      if(AppleJuiceClient.splash != null)
      {
         AppleJuiceClient.splash.setProgress(90, "Lade Plugins...");
      }

      if(OptionsManagerImpl.getInstance().shouldLoadPluginsOnStartup())
      {
         loadPlugins();
      }
   }

   private void loadPlugins()
   {
      Set<PluginConnector> plugins = PluginFactory.getPlugins();

      for(PluginConnector curPlugin : plugins)
      {
         if(curPlugin.istReiter())
         {
            ImageIcon icon  = curPlugin.getIcon();
            int       index = getTabCount() + 1;

            addTab(curPlugin.getTitle() + " [Ctrl+" + index + "]", icon, curPlugin);
         }
      }

      if(plugins.size() > 0)
      {
         LanguageSelector.getInstance().addPluginsToWatch(plugins);
      }
   }

   public void fireLanguageChanged()
   {
      try
      {
         LanguageSelector languageSelector = LanguageSelector.getInstance();

         setTitleAt(0,
                    ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.homesheet.caption")) +
                    " [Ctrl+1]");
         setTitleAt(1,
                    ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.sharesheet.caption")) +
                    " [Ctrl+2]");
         setTitleAt(2,
                    ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.seachsheet.caption")) +
                    " [Ctrl+3]");
         setTitleAt(3,
                    ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.queuesheet.caption")) +
                    " [Ctrl+4]");
         setTitleAt(4,
                    ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.uploadsheet.caption")) +
                    " [Ctrl+5]");
         setTitleAt(5,
                    ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(".root.mainform.serversheet.caption")) +
                    " [Ctrl+6]");
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }
}
