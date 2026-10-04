/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Set;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/RegisterPanel.java,v 1.65 2009/01/14 17:05:33 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
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
   private Logger logger;

   public RegisterPanel(AppleJuiceDialog parent)
   {
      super(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
      logger = LoggerFactory.getLogger(getClass());
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

   private void tabFocusGained(int index)
   {
      RegisterI register = (RegisterI) getComponentAt(index);

      if(register != null)
      {
         register.registerSelected();
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

               if(oldIndex != index)
               {
                  super.setSelectedIndex(index);
                  tabFocusGained(index);
               }
            }
         });
      startController = StartController.getInstance();
      shareController = ShareController.getInstance();
      downloadController = DownloadController.getInstance();
      uploadController = UploadController.getInstance();
      searchController = SearchController.getInstance();
      serverPanel = ServerPanel.getInstance();

      IconManager im = IconManager.getInstance();

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
            int       event = index < 10 ? KeyEvent.VK_1 + index - 1 : KeyEvent.VK_A + index - 10;

            addTab(curPlugin.getTitle() + " [" + ((char) event) + "]", icon, curPlugin);
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

         setTitleAt(0, languageSelector.getFirstAttrbuteByTagName("mainform.homesheet.caption") + " [Ctrl+1]");
         setTitleAt(1, languageSelector.getFirstAttrbuteByTagName("mainform.sharesheet.caption") + " [2]");
         setTitleAt(2, languageSelector.getFirstAttrbuteByTagName("mainform.seachsheet.caption") + " [3]");
         setTitleAt(3, languageSelector.getFirstAttrbuteByTagName("mainform.queuesheet.caption") + " [4]");
         setTitleAt(4, languageSelector.getFirstAttrbuteByTagName("mainform.uploadsheet.caption") + " [5]");
         setTitleAt(5, languageSelector.getFirstAttrbuteByTagName("mainform.serversheet.caption") + " [6]");
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }
}
