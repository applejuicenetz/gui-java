/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import org.apache.log4j.Level;

import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.46 2009/01/05 09:26:44 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General  License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public interface OptionsManager
{
   void addSettingsListener(DataUpdateListener listener);

   void addConnectionSettingsListener(DataUpdateListener listener);

   String getSprache();

   boolean getUpdateInfo();

   void setUpdateInfo(boolean UpdateInfo);

   boolean isErsterStart();

   void setErsterStart(boolean ersterStart);

   boolean isSoundEnabled();

   void enableSound(boolean enable);

   void setSprache(String sprache);

   Level getLogLevel();

   void setLogLevel(Level level);

   Settings getSettings();

   void saveSettings(Settings settings);

   ConnectionSettings getRemoteSettings();

   void saveRemote(ConnectionSettings remote) throws InvalidPasswordException;

   void onlySaveRemote(ConnectionSettings remote);

   void saveAJSettings(AJSettings ajSettings);

   String getDefaultTheme();

   void setDefaultTheme(String themeShortName);

   String getNewsURL();

   String getServerListURL();

   String getUpdateServerURL();

   boolean isThemesSupported();

   void enableThemeSupport(boolean enable);

   LookAFeel[] getLookAndFeels();

   LookAFeel getDefaultLookAndFeel();

   void setDefaultLookAndFeel(LookAFeel lookAFeel);

   int getLinkListenerPort();

   void setLinkListenerPort(int port);

   void loadPluginsOnStartup(boolean loadPluginsOnStartup);

   boolean shouldLoadPluginsOnStartup();

   boolean shouldShowConnectionDialogOnStartup();

   void showConnectionDialogOnStartup(boolean show);

   ConnectionSettings[] getConnectionsSet();

   void setConnectionsSet(ConnectionSettings[] set);

   void setOpenProgram(String path);

   String getOpenProgram();
}
