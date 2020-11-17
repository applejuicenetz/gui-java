/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.download.table.DownloadSourcesTableModel;
import de.applejuicenet.client.gui.download.table.DownloadsTableModel;
import de.applejuicenet.client.gui.search.table.SearchTableModel;
import de.applejuicenet.client.gui.server.table.ServerTableModel;
import de.applejuicenet.client.gui.share.table.ShareTableModel;
import de.applejuicenet.client.gui.upload.table.UploadActiveTableModel;
import de.applejuicenet.client.gui.upload.table.UploadWaitingTableModel;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/src/de/applejuicenet/client/gui/controller/PropertiesManager.java,v
 * 1.49 2004/07/09 11:34:00 loevenwong Exp $
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
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class PropertiesManager implements OptionsManager, PositionManager, ProxyManager
{
   private static PropertiesManager instance         = null;
   private static final String      PROPERTIES_ERROR = "Fehler beim Zugriff auf die ajgui.properties. " +
                                                       "Die Datei wird neu erstellt.";
   private static final String      PROPERTIES_ERROR_MESSAGE                   = "ajgui.properties neu erstellt";
   private static Logger            logger;
   private static String            path;
   private static final int         DEFAULT_UPLOADS_ACTIVE_TABLE_COLUMN_WIDTH  = 90;
   private static final int         DEFAULT_UPLOADS_WAITING_TABLE_COLUMN_WIDTH = 90;
   private static final int         DEFAULT_DOWNLOADS_TABLE_COLUMN_WIDTH       = 80;
   private static final int         DEFAULT_DOWNLOADSOURCES_TABLE_COLUMN_WIDTH = 80;
   private static final int         DEFAULT_SHARE_TABLE_COLUMN_WIDTH           = 194;
   private static final int         DEFAULT_SERVER_TABLE_COLUMN_WIDTH          = 175;
   private static final int         DEFAULT_SEARCH_TABLE_COLUMN_WIDTH          = 103;
   private Set<DataUpdateListener>  settingsListener                           = new HashSet<DataUpdateListener>();
   private Set<DataUpdateListener>  connectionSettingsListener                 = new HashSet<DataUpdateListener>();
   private Point                    mainXY;
   private Dimension                mainDimension;
   private ProxySettings            proxySettings;
   private ConnectionSettings       connectionSettings                         = null;
   private int[]                    downloadWidths;
   private int[]                    downloadSourcesWidths;
   private int[]                    uploadWidths;
   private int[]                    serverWidths;
   private int[]                    shareWidths;
   private boolean[]                downloadVisibilities;
   private boolean[]                downloadSourcesVisibilities;
   private boolean[]                uploadVisibilities;
   private boolean[]                uploadWaitingVisibilities;
   private int[]                    downloadIndex;
   private int[]                    downloadSourcesIndex;
   private int[]                    uploadIndex;
   private int[]                    uploadWaitingIndex;
   private Settings                 settings                                   = null;
   private boolean                  firstReadError                             = true;
   private boolean                  legal                                      = false;
   private PropertyHandler          propertyHandler;
   private int[]                    uploadWaitingWidths;
   private int[]                    downloadSort;
   private int[]                    uploadSort;
   private int[]                    searchSort;
   private int[]                    downloadSourcesSort;
   private int[]                    uploadWaitingSort;
   private int[]                    serverSort;

   private PropertiesManager(String propertiesPath)
   {
      PropertiesManager.path = propertiesPath;
      logger                 = Logger.getLogger(getClass());
      init();
   }

   static PropertiesManager getInstance()
   {
      if(instance == null)
      {
         instance = new PropertiesManager(AppleJuiceClient.getPropertiesPath());
      }

      return instance;
   }

   static PositionManager getPositionManager()
   {
      if(instance == null)
      {
         instance = new PropertiesManager(AppleJuiceClient.getPropertiesPath());
      }

      return instance;
   }

   private void saveFile()
   {
      try
      {
         propertyHandler.save();
      }
      catch(IllegalArgumentException e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(e.getMessage(), e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
      }
   }

   //ProxyManager-Interface
   public ProxySettings getProxySettings()
   {
      return proxySettings;
   }

   public void saveProxySettings(ProxySettings proxySettings)
   {
      this.proxySettings = proxySettings;
      propertyHandler.put("options_proxy_use", proxySettings.isUse());
      propertyHandler.put("options_proxy_host", proxySettings.getHost());
      propertyHandler.put("options_proxy_port", proxySettings.getPort());
      propertyHandler.put("options_proxy_userpass", proxySettings.getUserpass());
      saveFile();
   }

   //OptionsManager-Interface
   public void addSettingsListener(DataUpdateListener listener)
   {
      if(!(settingsListener.contains(listener)))
      {
         settingsListener.add(listener);
      }
   }

   public void addConnectionSettingsListener(DataUpdateListener listener)
   {
      if(!(connectionSettingsListener.contains(listener)))
      {
         connectionSettingsListener.add(listener);
      }
   }

   @SuppressWarnings("unchecked")
   private void informSettingsListener(Settings settings)
   {
      Iterator it = settingsListener.iterator();

      while(it.hasNext())
      {
         ((DataUpdateListener) it.next()).fireContentChanged(DATALISTENER_TYPE.SETTINGS_CHANGED, settings);
      }
   }

   @SuppressWarnings("unchecked")
   private void informConnectionSettingsListener(ConnectionSettings settings)
   {
      Iterator it = connectionSettingsListener.iterator();

      while(it.hasNext())
      {
         ((DataUpdateListener) it.next()).fireContentChanged(DATALISTENER_TYPE.CONNECTION_SETTINGS_CHANGED, settings);
      }
   }

   public String getSprache()
   {
      try
      {
         return propertyHandler.get("options_sprache", "deutsch");
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return null;
      }
   }

   public String getDefaultTheme()
   {
      try
      {
         String temp = propertyHandler.get("options_defaulttheme", "toxicthemepack");

         if(temp.length() == 0)
         {
            throw new Exception("Kein Defaulttheme vorhanden.");
         }

         return temp;
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return "";
      }
   }

   public void setDefaultTheme(String themeShortName)
   {
      propertyHandler.put("options_defaulttheme", themeShortName);
   }

   public String getNewsURL() {
      return propertyHandler.get("options_news_url", "https://applejuicenetz.github.io/news/%s.html");
   }

   public String getServerListURL() {
      return propertyHandler.get("options_server_list_url", "http://www.applejuicenet.cc/serverlist/xmllist.php");
   }

   public String getUpdateServerURL() {
      return propertyHandler.get("options_update_server_url", "https://api.github.com/repos/applejuicenetz/gui-java/releases/latest");
   }

   public LookAFeel[] getLookAndFeels()
   {
      try
      {
         ArrayList<LookAFeel> lookAndFeels = new ArrayList<LookAFeel>();
         String               temp  = ".";
         String               temp2;
         int                  i     = 1;

         String               test = propertyHandler.get("options_lookandfeels_laf" + 1 + "_value", null);

         if(null == test)
         {
            initLookAndFeels(propertyHandler);
         }
         while(temp != null && temp.length() > 0)
         {
            temp = propertyHandler.get("options_lookandfeels_laf" + i + "_value", "");
            if(temp.length() > 0)
            {
               temp2 = propertyHandler.get("options_lookandfeels_laf" + i + "_name", "");
               lookAndFeels.add(new LookAFeel(temp2, temp));
            }

            i++;
         }

         return (LookAFeel[]) lookAndFeels.toArray(new LookAFeel[lookAndFeels.size()]);

      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
      }

      return null;
   }

   private static void initLookAndFeels(PropertyHandler propertyHandler2)
   {
      propertyHandler2.put("options_lookandfeels_laf1_name", "JGoodies Plastic");
      propertyHandler2.put("options_lookandfeels_laf1_value", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
      int index = 2;

      if(System.getProperty("os.name").toLowerCase().indexOf("win") != -1)
      {
         propertyHandler2.put("options_lookandfeels_laf" + index + "_name", "JGoodies Windows");
         propertyHandler2.put("options_lookandfeels_laf" + index + "_value", "com.jgoodies.looks.windows.WindowsLookAndFeel");
         index++;
      }

      LookAndFeelInfo[] feels       = UIManager.getInstalledLookAndFeels();
      LookAndFeel       currentFeel = UIManager.getLookAndFeel();

      for(int i = 0; i < feels.length; i++)
      {
         try
         {
            UIManager.setLookAndFeel(feels[i].getClassName());
            propertyHandler2.put("options_lookandfeels_laf" + index + "_name", feels[i].getName());
            propertyHandler2.put("options_lookandfeels_laf" + index + "_value", feels[i].getClassName());
            index++;
         }
         catch(Exception e)
         {

            //unsupported
         }
      }

      try
      {
         UIManager.setLookAndFeel(currentFeel);
      }
      catch(Exception ex)
      {

         //muss klappen
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
         }
      }
   }

   public LookAFeel getDefaultLookAndFeel()
   {
      try
      {
         LookAFeel[] looks = this.getLookAndFeels();
         String      temp = propertyHandler.get("options_lookandfeels_default_name", "JGoodies Plastic");

         for(int i = 0; i < looks.length; i++)
         {
            if(temp.equals(looks[i].getName()))
            {
               return looks[i];
            }
         }
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
      }

      return null;
   }

   public void setDefaultLookAndFeel(LookAFeel lookAFeel)
   {
      propertyHandler.put("options_lookandfeels_default_name", lookAFeel.getName());
   }

   public void setOpenProgram(String path)
   {
      if(path == null || path.length() == 0)
      {
         path = "-1";
      }

      propertyHandler.put("options_program_file", path);
      String temp = getOpenProgram();

      if(temp.compareTo(path) != 0)
      {
         AppleJuiceDialog.rewriteProperties = true;
         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
      }
   }

   public String getOpenProgram()
   {
      try
      {
         String temp = propertyHandler.get("options_program_file", "-1");

         if(temp.compareTo("-1") == 0)
         {
            return "";
         }
         else if(temp.length() == 0)
         {
            return null;
         }
         else
         {
            return temp;
         }
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return "";
      }
   }

   public boolean isErsterStart()
   {
      try
      {
         return propertyHandler.getAsBoolean("options_firststart", true);
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return false;
      }
   }

   public void setErsterStart(boolean ersterStart)
   {
      propertyHandler.put("options_firststart", ersterStart);
   }

   public boolean shouldLoadPluginsOnStartup()
   {
      try
      {
         return propertyHandler.getAsBoolean("options_loadplugins", true);
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return false;
      }
   }

   public void loadPluginsOnStartup(boolean loadPluginsOnStartup)
   {
      propertyHandler.put("options_loadplugins", loadPluginsOnStartup);
      shouldLoadPluginsOnStartup();
   }

   public boolean isThemesSupported()
   {
      try
      {
         return propertyHandler.getAsBoolean("options_themes", false);
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return false;
      }
   }

   public void enableThemeSupport(boolean enable)
   {
      propertyHandler.put("options_themes", enable);
   }

   public boolean getUpdateInfo()
   {
      return propertyHandler.getAsBoolean("options_updateinfo", true);
   }

   public void setUpdateInfo(boolean UpdateInfo)
   {
      propertyHandler.put("options_updateinfo", UpdateInfo);
   }

   public int getLinkListenerPort()
   {
      return propertyHandler.getAsInt("options_linklistenerport", 8768);
   }

   public void setLinkListenerPort(int port)
   {
      propertyHandler.put("options_linklistenerport", port);
   }

   public boolean isSoundEnabled()
   {
      try
      {
         return propertyHandler.getAsBoolean("options_sound", true);
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return false;
      }
   }

   public void enableSound(boolean enable)
   {
      propertyHandler.put("options_sound", enable);
   }

   public void setSprache(String sprache)
   {
      propertyHandler.put("options_sprache", sprache.toLowerCase());
   }

   public boolean shouldShowConnectionDialogOnStartup()
   {
      try
      {
         return propertyHandler.getAsBoolean("options_dialogzeigen", true);
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return false;
      }
   }

   public void showConnectionDialogOnStartup(boolean show)
   {
      propertyHandler.put("options_dialogzeigen", show);
   }

   public Level getLogLevel()
   {
      try
      {
         String temp   = propertyHandler.get("options_logging_level", "INFO");
         Level  result = Level.OFF;

         if(temp.compareToIgnoreCase("INFO") == 0)
         {
            return Level.INFO;
         }
         else if(temp.compareToIgnoreCase("DEBUG") == 0)
         {
            return Level.DEBUG;
         }
         else if(temp.compareToIgnoreCase("WARN") == 0)
         {
            return Level.WARN;
         }
         else if(temp.compareToIgnoreCase("FATAL") == 0)
         {
            return Level.FATAL;
         }
         else if(temp.compareToIgnoreCase("ALL") == 0)
         {
            return Level.ALL;
         }
         else if(temp.compareToIgnoreCase("OFF") == 0)
         {
            return Level.OFF;
         }

         if(logger.isEnabledFor(Level.DEBUG))
         {
            logger.debug("Aktueller Loglevel: " + result.toString());
         }

         return result;
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return null;
      }
   }

   public void setLogLevel(Level level)
   {
      if(level == null)
      {
         level = Level.OFF;
      }

      String temp = "OFF";

      if(level == Level.ALL)
      {
         temp = "ALL";
      }
      else if(level == Level.INFO)
      {
         temp = "INFO";
      }
      else if(level == Level.DEBUG)
      {
         temp = "DEBUG";
      }
      else if(level == Level.WARN)
      {
         temp = "WARN";
      }
      else if(level == Level.FATAL)
      {
         temp = "FATAL";
      }

      propertyHandler.put("options_logging_level", temp);
      Logger rootLogger = Logger.getRootLogger();

      rootLogger.setLevel(level);
      rootLogger.removeAllAppenders();
      if(level != Level.OFF)
      {
         try
         {
            FileAppender fileAppender = new FileAppender(AppleJuiceClient.getLoggerHtmlLayout(),
                                                         AppleJuiceClient.getLoggerFileAppenderPath());

            rootLogger.addAppender(fileAppender);
         }
         catch(IOException ioe)
         {
            rootLogger.addAppender(new ConsoleAppender());
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ioe);
         }
      }
      else
      {
         rootLogger.addAppender(new ConsoleAppender());
      }

      if(logger.isEnabledFor(Level.DEBUG))
      {
         logger.debug("Loglevel geaendert in " + level.toString());
      }
   }

   public Settings getSettings()
   {
      try
      {
         if(settings == null)
         {
            settings = new Settings();
         }

         Color   downloadFertigHintergrundColor = null;
         Color   quelleHintergrundColor = null;
         boolean farbenAktiv;
         boolean downloadUebersicht;
         boolean loadPlugins;
         boolean enableToolTip;
         String  temp;

         farbenAktiv        = propertyHandler.get("options_farben_aktiv", "true").equals("true");
         temp               = propertyHandler.get("options_farben_hintergrund_downloadFertig", "-13382656");
         if(temp.length() != 0)
         {
            downloadFertigHintergrundColor = new Color(Integer.parseInt(temp));
         }

         temp = propertyHandler.get("options_farben_hintergrund_quelle", "-205");
         if(temp.length() != 0)
         {
            quelleHintergrundColor = new Color(Integer.parseInt(temp));
         }

         downloadUebersicht = propertyHandler.getAsBoolean("options_download_uebersicht", true);
         loadPlugins        = propertyHandler.getAsBoolean("options_loadplugins", true);
         enableToolTip      = propertyHandler.getAsBoolean("options_enableToolTip", true);
         settings.setFarbenAktiv(farbenAktiv);
         settings.setDownloadFertigHintergrundColor(downloadFertigHintergrundColor);
         settings.setQuelleHintergrundColor(quelleHintergrundColor);
         settings.loadPluginsOnStartup(loadPlugins);
         settings.enableToolTipEnabled(enableToolTip);
         settings.setDownloadUebersicht(downloadUebersicht);
         return settings;
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return null;
      }
   }

   public void saveSettings(Settings settings)
   {
      propertyHandler.put("options_farben_aktiv", settings.isFarbenAktiv());
      propertyHandler.put("options_farben_hintergrund_downloadFertig", settings.getDownloadFertigHintergrundColor().getRGB());
      propertyHandler.put("options_farben_hintergrund_quelle", settings.getQuelleHintergrundColor().getRGB());
      propertyHandler.put("options_loadplugins", settings.shouldLoadPluginsOnStartup());
      propertyHandler.put("options_enableToolTip", settings.isToolTipEnabled());
      propertyHandler.put("options_download_uebersicht", settings.isDownloadUebersicht());
      propertyHandler.save();
      informSettingsListener(settings);
   }

   public ConnectionSettings getRemoteSettings()
   {
      try
      {
         if(connectionSettings == null)
         {
            connectionSettings = new ConnectionSettings();
         }

         String host     = "localhost";
         String passwort = "";
         int    xmlPort  = 9851;

         host     = propertyHandler.get("options_remote_host", "localhost");
         passwort = propertyHandler.get("options_remote_passwort", "");
         xmlPort  = Integer.parseInt(propertyHandler.get("options_remote_port", "9851"));
         connectionSettings.setHost(host);
         if(passwort.length() == 0)
         {
            connectionSettings.setOldPassword("");
         }
         else
         {
            connectionSettings.setOldMD5Password(passwort);
         }

         connectionSettings.setXmlPort(xmlPort);
         return connectionSettings;
      }
      catch(Exception e)
      {
         AppleJuiceDialog.rewriteProperties = true;
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         return null;
      }
   }

   public void saveRemote(ConnectionSettings remote) throws InvalidPasswordException
   {
      propertyHandler.put("options_remote_host", remote.getHost());
      try
      {
         AppleJuiceClient.getAjFassade().setPassword(remote.getNewPassword(), false);
      }
      catch(de.applejuicenet.client.fassade.exception.IllegalArgumentException e)
      {
         logger.error(e);
      }

      propertyHandler.put("options_remote_passwort", remote.getNewPassword());
      propertyHandler.put("options_remote_port", remote.getXmlPort());
      informConnectionSettingsListener(getRemoteSettings());
   }

   public void onlySaveRemote(ConnectionSettings remote)
   {
      propertyHandler.put("options_remote_host", remote.getHost());
      propertyHandler.put("options_remote_passwort", remote.getNewPassword());
      propertyHandler.put("options_remote_port", remote.getXmlPort());
      connectionSettings = remote;
   }

   public void saveAJSettings(AJSettings ajSettings)
   {
      AppleJuiceClient.getAjFassade().saveAJSettings(ajSettings);
   }

   private boolean isVeraltet()
   {
      return false;
   }

   public static void restoreProperties()
   {
      PropertyHandler aPropertyHandler = null;

      try
      {
         aPropertyHandler = new PropertyHandler(AppleJuiceClient.getPropertiesPath(), "appleJuice-Java-GUI Propertyfile", false);
         aPropertyHandler.put("options_dialogzeigen", true);
         aPropertyHandler.put("options_firststart", true);
         aPropertyHandler.put("options_sound", true);
         aPropertyHandler.put("options_sprache", "deutsch");
         aPropertyHandler.put("options_themes", false);
         aPropertyHandler.put("options_defaulttheme", "toxicthemepack");
         aPropertyHandler.put("options_loadplugins", true);
         aPropertyHandler.put("options_enableToolTip", true);
         aPropertyHandler.put("options_linklistenerport", 8768);
         aPropertyHandler.put("options_updateinfo", true);
         aPropertyHandler.put("options_remote_host", "localhost");
         aPropertyHandler.put("options_remote_passwort", "");
         aPropertyHandler.put("options_remote_port", 9851);

         aPropertyHandler.put("options_logging_level", "INFO");
         aPropertyHandler.put("options_download_uebersicht", true);
         aPropertyHandler.put("options_farben_aktiv", true);

         aPropertyHandler.put("options_farben_hintergrund_downloadFertig", -13382656);
         aPropertyHandler.put("options_farben_hintergrund_quelle", -205);

         initLookAndFeels(aPropertyHandler);

         aPropertyHandler.put("options_lookandfeels_default_name", "JGoodies Plastic");
         aPropertyHandler.put("options_location_height", "");
         aPropertyHandler.put("options_location_width", "");
         aPropertyHandler.put("options_location_x", "");
         aPropertyHandler.put("options_location_y", "");
         aPropertyHandler.put("options_columns_download_column0_width", DEFAULT_DOWNLOADS_TABLE_COLUMN_WIDTH);
         aPropertyHandler.put("options_columns_download_column0_index", 0);
         for(int i = 1; i < DownloadsTableModel.CLASS_TYPES.length; i++)
         {
            aPropertyHandler.put("options_columns_download_column" + i + "_width", DEFAULT_DOWNLOADS_TABLE_COLUMN_WIDTH);
            aPropertyHandler.put("options_columns_download_column" + i + "_visibility", true);
            aPropertyHandler.put("options_columns_download_column" + i + "_index", i);
         }

         aPropertyHandler.put("options_download_sort_column", "0");
         aPropertyHandler.put("options_download_sort_order", "1");

         aPropertyHandler.put("options_download_sources_sort_column", "0");
         aPropertyHandler.put("options_download_sources_sort_order", "1");

         aPropertyHandler.put("options_upload_sort_column", "0");
         aPropertyHandler.put("options_upload_sort_order", "1");

         aPropertyHandler.put("options_search_sort_column", "0");
         aPropertyHandler.put("options_search_sort_order", "1");

         aPropertyHandler.put("options_upload_waiting_sort_column", "0");
         aPropertyHandler.put("options_upload_waiting_sort_order", "1");

         aPropertyHandler.put("options_server_sort_column", "0");
         aPropertyHandler.put("options_server_sort_order", "1");

         aPropertyHandler.put("options_columns_downloadsources_column0_width", DEFAULT_DOWNLOADSOURCES_TABLE_COLUMN_WIDTH);
         aPropertyHandler.put("options_columns_downloadsources_column0_index", 0);
         for(int i = 1; i < DownloadSourcesTableModel.CLASS_TYPES.length; i++)
         {
            aPropertyHandler.put("options_columns_downloadsources_column" + i + "_width", DEFAULT_DOWNLOADSOURCES_TABLE_COLUMN_WIDTH);
            aPropertyHandler.put("options_columns_downloadsources_column" + i + "_visibility", true);
            aPropertyHandler.put("options_columns_downloadsources_column" + i + "_index", i);
         }

         aPropertyHandler.put("options_columns_upload_column0_width", DEFAULT_UPLOADS_ACTIVE_TABLE_COLUMN_WIDTH);
         aPropertyHandler.put("options_columns_upload_column0_index", 0);
         for(int i = 1; i < UploadActiveTableModel.CLASS_TYPES.length; i++)
         {
            aPropertyHandler.put("options_columns_upload_column" + i + "_width", DEFAULT_UPLOADS_ACTIVE_TABLE_COLUMN_WIDTH);
            aPropertyHandler.put("options_columns_upload_column" + i + "_visibility", true);
            aPropertyHandler.put("options_columns_upload_column" + i + "_index", i);
         }

         aPropertyHandler.put("options_columns_uploadwaiting_column0_width", DEFAULT_UPLOADS_WAITING_TABLE_COLUMN_WIDTH);
         aPropertyHandler.put("options_columns_uploadwaiting_column0_index", 0);
         for(int i = 1; i < UploadWaitingTableModel.CLASS_TYPES.length; i++)
         {
            aPropertyHandler.put("options_columns_uploadwaiting_column" + i + "_width", DEFAULT_UPLOADS_WAITING_TABLE_COLUMN_WIDTH);
            aPropertyHandler.put("options_columns_uploadwaiting_column" + i + "_visibility", true);
            aPropertyHandler.put("options_columns_uploadwaiting_column" + i + "_index", i);
         }

         aPropertyHandler.put("options_columns_server_column0_width", DEFAULT_SERVER_TABLE_COLUMN_WIDTH);
         aPropertyHandler.put("options_columns_server_column0_index", 0);
         for(int i = 1; i < ServerTableModel.CLASS_TYPES.length; i++)
         {
            aPropertyHandler.put("options_columns_server_column" + i + "_width", DEFAULT_SERVER_TABLE_COLUMN_WIDTH);
            aPropertyHandler.put("options_columns_server_column" + i + "_visibility", true);
            aPropertyHandler.put("options_columns_server_column" + i + "_index", i);
         }

         aPropertyHandler.put("options_columns_search_column0_width", DEFAULT_SEARCH_TABLE_COLUMN_WIDTH);
         aPropertyHandler.put("options_columns_search_column0_index", 0);
         for(int i = 1; i < SearchTableModel.CLASS_TYPES.length; i++)
         {
            aPropertyHandler.put("options_columns_search_column" + i + "_width", DEFAULT_SEARCH_TABLE_COLUMN_WIDTH);
            aPropertyHandler.put("options_columns_search_column" + i + "_visibility", true);
            aPropertyHandler.put("options_columns_search_column" + i + "_index", i);
         }

         aPropertyHandler.put("options_columns_share_column0_width", DEFAULT_SHARE_TABLE_COLUMN_WIDTH);
         aPropertyHandler.put("options_columns_share_column0_index", 0);
         for(int i = 1; i < ShareTableModel.CLASS_TYPES.length; i++)
         {
            aPropertyHandler.put("options_columns_share_column" + i + "_width", DEFAULT_SHARE_TABLE_COLUMN_WIDTH);
            aPropertyHandler.put("options_columns_share_column" + i + "_visibility", true);
            aPropertyHandler.put("options_columns_share_column" + i + "_index", i);
         }

         aPropertyHandler.put("options_browser_file", "");
         aPropertyHandler.put("options_program_file", -1);
         aPropertyHandler.put("options_proxy_host", "");
         aPropertyHandler.put("options_proxy_port", "");
         aPropertyHandler.put("options_proxy_use", false);
         aPropertyHandler.put("options_proxy_userpass", "");
         aPropertyHandler.save();
      }
      catch(IllegalArgumentException e1)
      {
         // sollte eigentlich nie passieren
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e1);
      }
   }

   protected void init()
   {
      try
      {
         propertyHandler = new PropertyHandler(path, "appleJuice-Java-GUI Propertyfile", true);
         if(isVeraltet())
         {
            throw new Exception("ajgui.properties hat altes Format. Wird neu erstellt.");
         }

         String temp = propertyHandler.get("options_location_x", "");

         if(temp.length() != 0)
         {
            legal = true;

            int mainX = propertyHandler.getAsInt("options_location_x", 1);
            int mainY = propertyHandler.getAsInt("options_location_y", 1);

            mainXY = new Point(mainX, mainY);
            int mainWidth  = propertyHandler.getAsInt("options_location_width", 400);
            int mainHeight = propertyHandler.getAsInt("options_location_height", 400);

            mainDimension = new Dimension(mainWidth, mainHeight);
         }

         downloadWidths = new int[DownloadsTableModel.CLASS_TYPES.length];
         for(int i = 0; i < downloadWidths.length; i++)
         {
            downloadWidths[i] = propertyHandler.getAsInt("options_columns_download_column" + i + "_width",
                                                         DEFAULT_DOWNLOADS_TABLE_COLUMN_WIDTH);
         }

         downloadSort    = new int[2];
         downloadSort[0] = propertyHandler.getAsInt("options_download_sort_column", 0);
         downloadSort[1] = propertyHandler.getAsInt("options_download_sort_order", 1);

         downloadSourcesSort    = new int[2];
         downloadSourcesSort[0] = propertyHandler.getAsInt("options_download_sources_sort_column", 0);
         downloadSourcesSort[1] = propertyHandler.getAsInt("options_download_sources_sort_order", 1);

         uploadSort    = new int[2];
         uploadSort[0] = propertyHandler.getAsInt("options_upload_sort_column", 0);
         uploadSort[1] = propertyHandler.getAsInt("options_upload_sort_order", 1);

         searchSort    = new int[2];
         searchSort[0] = propertyHandler.getAsInt("options_search_sort_column", 0);
         searchSort[1] = propertyHandler.getAsInt("options_search_sort_order", 1);

         uploadWaitingSort = new int[2];
         uploadWaitingSort[0]     = propertyHandler.getAsInt("options_upload_waiting_sort_column", 0);
         uploadWaitingSort[1]     = propertyHandler.getAsInt("options_upload_waiting_sort_order", 1);

         serverSort    = new int[2];
         serverSort[0] = propertyHandler.getAsInt("options_server_sort_column", 0);
         serverSort[1] = propertyHandler.getAsInt("options_server_sort_order", 1);

         downloadSourcesWidths = new int[DownloadSourcesTableModel.CLASS_TYPES.length];
         for(int i = 0; i < downloadSourcesWidths.length; i++)
         {
            downloadSourcesWidths[i] = propertyHandler.getAsInt("options_columns_downloadsources_column" + i + "_width",
                                                                DEFAULT_DOWNLOADSOURCES_TABLE_COLUMN_WIDTH);
         }

         uploadWidths = new int[UploadActiveTableModel.CLASS_TYPES.length];
         for(int i = 0; i < uploadWidths.length; i++)
         {
            uploadWidths[i] = propertyHandler.getAsInt("options_columns_upload_column" + i + "_width",
                                                       DEFAULT_UPLOADS_ACTIVE_TABLE_COLUMN_WIDTH);
         }

         uploadWaitingWidths = new int[UploadWaitingTableModel.CLASS_TYPES.length];
         for(int i = 0; i < uploadWaitingWidths.length; i++)
         {
            uploadWaitingWidths[i] = propertyHandler.getAsInt("options_columns_uploadwaiting_column" + i + "_width",
                                                              DEFAULT_UPLOADS_WAITING_TABLE_COLUMN_WIDTH);
         }

         uploadWaitingVisibilities      = new boolean[UploadWaitingTableModel.CLASS_TYPES.length];
         uploadWaitingVisibilities[0]   = true;
         for(int i = 1; i < uploadWaitingVisibilities.length; i++)
         {
            uploadWaitingVisibilities[i] = propertyHandler.getAsBoolean("options_columns_uploadwaiting_column" + i + "_visibility",
                                                                        true);
         }

         serverWidths = new int[ServerTableModel.CLASS_TYPES.length];
         for(int i = 0; i < serverWidths.length; i++)
         {
            serverWidths[i] = propertyHandler.getAsInt("options_columns_server_column" + i + "_width",
                                                       DEFAULT_SERVER_TABLE_COLUMN_WIDTH);
         }

         shareWidths = new int[ShareTableModel.CLASS_TYPES.length];
         for(int i = 0; i < shareWidths.length; i++)
         {
            shareWidths[i] = propertyHandler.getAsInt("options_columns_share_column" + i + "_width",
                                                      DEFAULT_SHARE_TABLE_COLUMN_WIDTH);
         }

         downloadVisibilities           = new boolean[DownloadsTableModel.CLASS_TYPES.length];
         downloadVisibilities[0]        = true;
         for(int i = 1; i < downloadVisibilities.length; i++)
         {
            downloadVisibilities[i] = propertyHandler.getAsBoolean("options_columns_download_column" + i + "_visibility", true);
         }

         downloadSourcesVisibilities    = new boolean[DownloadSourcesTableModel.CLASS_TYPES.length];
         downloadSourcesVisibilities[0] = true;
         for(int i = 1; i < downloadSourcesVisibilities.length; i++)
         {
            downloadSourcesVisibilities[i] = propertyHandler.getAsBoolean("options_columns_downloadsources_column" + i +
                                                                          "_visibility", true);
         }

         uploadVisibilities    = new boolean[UploadActiveTableModel.CLASS_TYPES.length];
         uploadVisibilities[0] = true;
         for(int i = 1; i < uploadVisibilities.length; i++)
         {
            uploadVisibilities[i] = propertyHandler.getAsBoolean("options_columns_upload_column" + i + "_visibility", true);
         }

         downloadIndex = new int[DownloadsTableModel.CLASS_TYPES.length];
         for(int i = 0; i < downloadIndex.length; i++)
         {
            downloadIndex[i] = propertyHandler.getAsInt("options_columns_download_column" + i + "_index", i);
         }

         downloadSourcesIndex = new int[DownloadSourcesTableModel.CLASS_TYPES.length];
         for(int i = 0; i < downloadSourcesIndex.length; i++)
         {
            downloadSourcesIndex[i] = propertyHandler.getAsInt("options_columns_downloadsources_column" + i + "_index", i);
         }

         uploadIndex = new int[UploadActiveTableModel.CLASS_TYPES.length];
         for(int i = 1; i < uploadIndex.length; i++)
         {
            uploadIndex[i] = propertyHandler.getAsInt("options_columns_upload_column" + i + "_index", i);
         }

         uploadWaitingIndex = new int[UploadWaitingTableModel.CLASS_TYPES.length];
         for(int i = 1; i < uploadWaitingIndex.length; i++)
         {
            uploadWaitingIndex[i] = propertyHandler.getAsInt("options_columns_uploadwaiting_column" + i + "_index", i);
         }

         boolean use  = propertyHandler.getAsBoolean("options_proxy_use", false);
         int     port = propertyHandler.getAsInt("options_proxy_port", -1);

         String  userpass = propertyHandler.get("options_proxy_userpass", "");
         String  host     = propertyHandler.get("options_proxy_host", "");

         proxySettings = new ProxySettings(use, host, port, userpass);
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(PROPERTIES_ERROR_MESSAGE, e);
         }

         if(firstReadError == true)
         {
            PropertiesManager.restoreProperties();
            AppleJuiceDialog.showInformation(PROPERTIES_ERROR);
            firstReadError = false;
            init();
         }
         else
         {
            AppleJuiceDialog.rewriteProperties = true;
            AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
         }
      }
   }

   public void save()
   {
      try
      {
         propertyHandler.put("options_location_x", mainXY.x);
         propertyHandler.put("options_location_y", mainXY.y);
         propertyHandler.put("options_location_width", mainDimension.width);
         propertyHandler.put("options_location_height", mainDimension.height);

         propertyHandler.put("options_download_sort_column", downloadSort[0]);
         propertyHandler.put("options_download_sort_order", downloadSort[1]);

         propertyHandler.put("options_download_sources_sort_column", downloadSourcesSort[0]);
         propertyHandler.put("options_download_sources_sort_order", downloadSourcesSort[1]);

         propertyHandler.put("options_upload_sort_column", uploadSort[0]);
         propertyHandler.put("options_upload_sort_order", uploadSort[1]);

         propertyHandler.put("options_search_sort_column", searchSort[0]);
         propertyHandler.put("options_search_sort_order", searchSort[1]);

         propertyHandler.put("options_upload_waiting_sort_column", uploadWaitingSort[0]);
         propertyHandler.put("options_upload_waiting_sort_order", uploadWaitingSort[1]);

         propertyHandler.put("options_server_sort_column", serverSort[0]);
         propertyHandler.put("options_server_sort_order", serverSort[1]);

         for(int i = 0; i < downloadWidths.length; i++)
         {
            propertyHandler.put("options_columns_download_column" + i + "_width", downloadWidths[i]);
         }

         for(int i = 0; i < downloadSourcesWidths.length; i++)
         {
            propertyHandler.put("options_columns_downloadsources_column" + i + "_width", downloadSourcesWidths[i]);
         }

         for(int i = 0; i < uploadWidths.length; i++)
         {
            propertyHandler.put("options_columns_upload_column" + i + "_width", uploadWidths[i]);
         }

         for(int i = 0; i < uploadWaitingWidths.length; i++)
         {
            propertyHandler.put("options_columns_uploadwaiting_column" + i + "_width", uploadWaitingWidths[i]);
         }

         for(int i = 0; i < serverWidths.length; i++)
         {
            propertyHandler.put("options_columns_server_column" + i + "_width", serverWidths[i]);
         }

         for(int i = 0; i < shareWidths.length; i++)
         {
            propertyHandler.put("options_columns_share_column" + i + "_width", shareWidths[i]);
         }

         for(int i = 0; i < downloadVisibilities.length; i++)
         {
            propertyHandler.put("options_columns_download_column" + i + "_visibility", downloadVisibilities[i]);
         }

         for(int i = 0; i < downloadSourcesVisibilities.length; i++)
         {
            propertyHandler.put("options_columns_downloadsources_column" + i + "_visibility", downloadSourcesVisibilities[i]);
         }

         for(int i = 0; i < uploadVisibilities.length; i++)
         {
            propertyHandler.put("options_columns_upload_column" + i + "_visibility", uploadVisibilities[i]);
         }

         for(int i = 0; i < uploadWaitingVisibilities.length; i++)
         {
            propertyHandler.put("options_columns_uploadwaiting_column" + i + "_visibility", uploadWaitingVisibilities[i]);
         }

         for(int i = 0; i < downloadIndex.length; i++)
         {
            propertyHandler.put("options_columns_download_column" + i + "_index", downloadIndex[i]);
         }

         for(int i = 0; i < downloadSourcesIndex.length; i++)
         {
            propertyHandler.put("options_columns_downloadsources_column" + i + "_index", downloadSourcesIndex[i]);
         }

         for(int i = 0; i < uploadWaitingIndex.length; i++)
         {
            propertyHandler.put("options_columns_uploadwaiting_column" + i + "_index", uploadWaitingIndex[i]);
         }

         saveFile();
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public void setMainXY(Point p)
   {
      mainXY = p;
   }

   public Point getMainXY()
   {
      return mainXY;
   }

   public void setMainDimension(Dimension dimension)
   {
      mainDimension = dimension;
   }

   public Dimension getMainDimension()
   {
      return mainDimension;
   }

   public void setDownloadWidths(int[] widths)
   {
      downloadWidths = widths;
   }

   public boolean isLegal()
   {
      return legal;
   }

   public int[] getDownloadWidths()
   {
      return downloadWidths;
   }

   public int[] getUploadWidths()
   {
      return uploadWidths;
   }

   public void setUploadWidths(int[] uploadWidths)
   {
      this.uploadWidths = uploadWidths;
   }

   public int[] getUploadWaitingWidths()
   {
      return uploadWaitingWidths;
   }

   public void setUploadWaitingWidths(int[] uploadWaitingWidths)
   {
      this.uploadWaitingWidths = uploadWaitingWidths;
   }

   public int[] getServerWidths()
   {
      return serverWidths;
   }

   public void setServerWidths(int[] serverWidths)
   {
      this.serverWidths = serverWidths;
   }

   public int[] getShareWidths()
   {
      return shareWidths;
   }

   public void setShareWidths(int[] shareWidths)
   {
      this.shareWidths = shareWidths;
   }

   public void setDownloadColumnVisible(int column, boolean visible)
   {
      if(column != 0)
      {
         downloadVisibilities[column] = visible;
      }
   }

   public boolean[] getDownloadColumnVisibilities()
   {
      return downloadVisibilities;
   }

   public void setDownloadColumnIndex(int column, int index)
   {
      downloadIndex[column] = index;
   }

   public int[] getDownloadSort()
   {
      return downloadSort;
   }

   public void setDownloadSort(int column, boolean ascent)
   {
      downloadSort = new int[] {column, ascent ? 1 : 0};

   }

   public int[] getDownloadSourcesSort()
   {
      return downloadSourcesSort;
   }

   public int[] getSearchSort()
   {
      return searchSort;
   }

   public int[] getUploadSort()
   {
      return uploadSort;
   }

   public void setDownlodSourcesSort(int column, boolean ascent)
   {
      downloadSourcesSort = new int[] {column, ascent ? 1 : 0};

   }

   public void setSearchSort(int column, boolean ascent)
   {
      searchSort = new int[] {column, ascent ? 1 : 0};

   }

   public void setUploadSort(int column, boolean ascent)
   {
      uploadSort = new int[] {column, ascent ? 1 : 0};

   }

   public int[] getServerSort()
   {
      return serverSort;
   }

   public int[] getUploadWaitingSort()
   {
      return uploadWaitingSort;
   }

   public void setServerSort(int column, boolean ascent)
   {
      serverSort = new int[] {column, ascent ? 1 : 0};

   }

   public void setUploadWaitingSort(int column, boolean ascent)
   {
      uploadWaitingSort = new int[] {column, ascent ? 1 : 0};

   }

   public int[] getDownloadColumnIndizes()
   {
      return downloadIndex;
   }

   public void setUploadColumnIndex(int column, int index)
   {
      uploadIndex[column] = index;
   }

   public int[] getUploadColumnIndizes()
   {
      return uploadIndex;
   }

   public void setUploadColumnVisible(int column, boolean visible)
   {
      if(column != 0)
      {
         uploadVisibilities[column] = visible;
      }
   }

   public boolean[] getUploadColumnVisibilities()
   {
      return uploadVisibilities;
   }

   public ConnectionSettings[] getConnectionsSet()
   {
      ArrayList<ConnectionSettings> connectionSet = new ArrayList<ConnectionSettings>();

      for(int i = 0;; i++)
      {
         ConnectionSettings temp = new ConnectionSettings();

         temp.setHost(propertyHandler.get("options_remote" + i + "_host", ""));
         if(temp.getHost().length() == 0)
         {
            break;
         }

         int port = propertyHandler.getAsInt("options_remote" + i + "_port", -1);

         temp.setXmlPort(port);
         connectionSet.add(temp);
      }

      return (ConnectionSettings[]) connectionSet.toArray(new ConnectionSettings[] {});
   }

   public void setConnectionsSet(ConnectionSettings[] set)
   {
      for(int i = 0; i < set.length; i++)
      {
         if((set.length - 1 < i) || ("".equals(set[i].getHost())))
         {
            propertyHandler.put("options_remote" + i + "_host", "");
            propertyHandler.put("options_remote" + i + "_port", 0);
         }
         else
         {
            propertyHandler.put("options_remote" + i + "_host", set[i].getHost());
            propertyHandler.put("options_remote" + i + "_port", set[i].getXmlPort());
         }
      }
   }

   public int[] getUploadWaitingColumnIndizes()
   {
      return uploadWaitingIndex;
   }

   public boolean[] getUploadWaitingColumnVisibilities()
   {
      return uploadWaitingVisibilities;
   }

   public void setUploadWaitingColumnIndex(int column, int index)
   {
      uploadWaitingIndex[column] = index;
   }

   public void setUploadWaitingColumnVisible(int column, boolean visible)
   {
      if(column != 0)
      {
         uploadWaitingVisibilities[column] = visible;
      }
   }

   public int[] getDownloadSourcesColumnIndizes()
   {
      return downloadSourcesIndex;
   }

   public boolean[] getDownloadSourcesColumnVisibilities()
   {
      return downloadSourcesVisibilities;
   }

   public int[] getDownloadSourcesWidths()
   {
      return downloadSourcesWidths;
   }

   public void setDownloadSourcesColumnIndex(int column, int index)
   {
      downloadSourcesIndex[column] = index;

   }

   public void setDownloadSourcesColumnVisible(int column, boolean visible)
   {
      if(column != 0)
      {
         downloadSourcesVisibilities[column] = visible;
      }
   }

   public void setDownloadSourcesWidths(int[] widths)
   {
      downloadSourcesWidths = widths;
   }
}
