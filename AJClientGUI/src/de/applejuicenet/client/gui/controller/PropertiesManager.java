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
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.gui.AppleJuiceDialog;
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

public class PropertiesManager implements OptionsManager, PositionManager,
		ProxyManager {
	private static PropertiesManager instance = null;

	private static final String PROPERTIES_ERROR = "Fehler beim Zugriff auf die ajgui.properties. "
			+ "Die Datei wird neu erstellt.";

	private static final String PROPERTIES_ERROR_MESSAGE = "ajgui.properties neu erstellt";

	private static Logger logger;

	private Set settingsListener = new HashSet();

	private Set connectionSettingsListener = new HashSet();

	private Point mainXY;

	private Dimension mainDimension;

	private ProxySettings proxySettings;

	private ConnectionSettings connectionSettings = null;

	private int[] downloadWidths;

	private int[] uploadWidths;

	private int[] serverWidths;

	private int[] shareWidths;

	private boolean[] downloadVisibilities;

	private boolean[] uploadVisibilities;

	private int[] downloadIndex;

	private int[] uploadIndex;

	private Settings settings = null;

	private static String path;

	private boolean firstReadError = true;

	private boolean legal = false;

	private PropertyHandler propertyHandler;

	private PropertiesManager(String propertiesPath) {
		PropertiesManager.path = propertiesPath;
		logger = Logger.getLogger(getClass());
		init();
	}

	static PropertiesManager getInstance() {
		if (instance == null) {
			instance = new PropertiesManager(AppleJuiceClient
					.getPropertiesPath());
		}
		return instance;
	}

	static PositionManager getPositionManager() {
		if (instance == null) {
			instance = new PropertiesManager(AppleJuiceClient
					.getPropertiesPath());
		}
		return instance;
	}

	private void saveFile() {

		try {
			propertyHandler.save();
		} catch (IllegalArgumentException e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(e.getMessage(),
								e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
		}
	}

	//ProxyManager-Interface

	public ProxySettings getProxySettings() {
		return proxySettings;
	}

	public void saveProxySettings(ProxySettings proxySettings) {
		this.proxySettings = proxySettings;
		propertyHandler.put("options_proxy_use", proxySettings.isUse());
		propertyHandler.put("options_proxy_host", proxySettings.getHost());
		propertyHandler.put("options_proxy_port", proxySettings.getPort());
		propertyHandler.put("options_proxy_userpass", proxySettings
				.getUserpass());
		saveFile();
	}

	//OptionsManager-Interface

	public void addSettingsListener(DataUpdateListener listener) {
		if (!(settingsListener.contains(listener))) {
			settingsListener.add(listener);
		}
	}

	public void addConnectionSettingsListener(DataUpdateListener listener) {
		if (!(connectionSettingsListener.contains(listener))) {
			connectionSettingsListener.add(listener);
		}
	}

	private void informSettingsListener(Settings settings) {
		Iterator it = settingsListener.iterator();
		while (it.hasNext()) {
			((DataUpdateListener) it.next()).fireContentChanged(
					DataUpdateListener.SETTINGS_CHANGED, settings);
		}
	}

	private void informConnectionSettingsListener(ConnectionSettings settings) {
		Iterator it = connectionSettingsListener.iterator();
		while (it.hasNext()) {
			((DataUpdateListener) it.next()).fireContentChanged(
					DataUpdateListener.CONNECTION_SETTINGS_CHANGED, settings);
		}
	}

	public String getSprache() {
		try {
			return propertyHandler.get("options_sprache");
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return null;
		}
	}

	public String getDefaultTheme() {
		try {
			String temp = propertyHandler.get("options_defaulttheme");
			if (temp.length() == 0) {
				throw new Exception("Kein Defaulttheme vorhanden.");
			}
			return temp;
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return "";
		}
	}

	public void setDefaultTheme(String themeShortName) {
		propertyHandler.put("options_defaulttheme", themeShortName);
	}

	public LookAFeel[] getLookAndFeels() {
		try {
			ArrayList lookAndFeels = new ArrayList();
			String temp = ".";
			String temp2;
			int i = 1;
			while (temp != null && temp.length() > 0) {
				temp = propertyHandler.get("options_lookandfeels_laf" + i
						+ "_value");
				if (temp.length() > 0) {
					temp2 = propertyHandler.get("options_lookandfeels_laf" + i
							+ "_name");
					lookAndFeels.add(new LookAFeel(temp2, temp));
				}
				i++;
			}
			return (LookAFeel[]) lookAndFeels
					.toArray(new LookAFeel[lookAndFeels.size()]);

		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
		}
		return null;
	}

	public LookAFeel getDefaultLookAndFeel() {
		try {
			LookAFeel[] looks = this.getLookAndFeels();
			String temp = propertyHandler
					.get("options_lookandfeels_default_name");
			for (int i = 0; i < looks.length; i++) {
				if (temp.equals(looks[i].getName())) {
					return looks[i];
				}
			}
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
		}
		return null;
	}

	public void setDefaultLookAndFeel(LookAFeel lookAFeel) {
		propertyHandler.put("options_lookandfeels_default_name", lookAFeel
				.getName());
	}

	public void setOpenProgram(String path) {
		if (path == null || path.length() == 0) {
			path = "-1";
		}
		propertyHandler.put("options_program_file", path);
		String temp = getOpenProgram();
		if (temp.compareTo(path) != 0) {
			AppleJuiceDialog.rewriteProperties = true;
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
		}
	}

	public String getOpenProgram() {
		try {
			String temp = propertyHandler.get("options_program_file");
			if (temp.compareTo("-1") == 0) {
				return "";
			} else {
				if (temp.length() == 0) {
					return null;
				} else {
					return temp;
				}
			}
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return "";
		}
	}

	public String getStandardBrowser() {
		try {
			String temp = propertyHandler.get("options_browser_file");
			if (temp == null) {
				return "";
			} else {
				return temp;
			}
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return "";
		}
	}

	public void setStandardBrowser(String browser) {
		propertyHandler.put("options_browser_file", browser);
		String temp = getStandardBrowser();
		if (temp.compareTo(browser) != 0) {
			AppleJuiceDialog.rewriteProperties = true;
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
		}
	}

	public boolean isErsterStart() {
		try {
			Boolean start = propertyHandler.getAsBoolean("options_firststart");
			if (start == null) {
				return true;
			}
			return start.booleanValue();
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return false;
		}
	}

	public void setErsterStart(boolean ersterStart) {
		propertyHandler.put("options_firststart", ersterStart);
	}

	public boolean shouldLoadPluginsOnStartup() {
		try {
			Boolean tmp = propertyHandler.getAsBoolean("options_loadplugins");
			if (tmp == null) {
				return true;
			}
			return tmp.booleanValue();
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return false;
		}
	}

	public void loadPluginsOnStartup(boolean loadPluginsOnStartup) {
		propertyHandler.put("options_loadplugins", loadPluginsOnStartup);
		shouldLoadPluginsOnStartup();
	}

	public boolean isThemesSupported() {
		try {
			Boolean tmp = propertyHandler.getAsBoolean("options_themes");
			if (tmp == null) {
				return true;
			}
			return tmp.booleanValue();
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return false;
		}
	}

	public void enableThemeSupport(boolean enable) {
		propertyHandler.put("options_themes", enable);
	}

	public int getVersionsinfoModus() {
		Integer tmp = propertyHandler.getAsInt("options_versionsinfo");
		if (tmp != null) {
			return tmp.intValue();
		} else {
			AppleJuiceDialog.rewriteProperties = true;
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return 1;
		}
	}

	public void setVersionsinfoModus(int versionsinfoModus) {
		if (versionsinfoModus < 0 || versionsinfoModus > 2) {
			versionsinfoModus = 1;
		}
		propertyHandler.put("options_versionsinfo", versionsinfoModus);
	}

	public int getLinkListenerPort() {
		Integer tmp = propertyHandler.getAsInt("options_linklistenerport");
		if (tmp != null) {
			return tmp.intValue();
		} else {
			AppleJuiceDialog.rewriteProperties = true;
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return -1;
		}
	}

	public void setLinkListenerPort(int port) {
		propertyHandler.put("options_linklistenerport", port);
	}

	public boolean isSoundEnabled() {
		try {
			String temp = propertyHandler.get("options_sound");
			if (temp.length() == 0) {
				return true;
			}
			return new Boolean(temp).booleanValue();
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return false;
		}
	}

	public void enableSound(boolean enable) {
		propertyHandler.put("options_sound", enable);
	}

	public void setSprache(String sprache) {
		propertyHandler.put("options_sprache", sprache.toLowerCase());
	}

	public boolean shouldShowConnectionDialogOnStartup() {
		try {
			String temp = propertyHandler.get("options_dialogzeigen");
			if (temp.length() == 0) {
				throw new Exception("Veraltete xml");
			}
			return new Boolean(temp).booleanValue();
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return false;
		}
	}

	public void showConnectionDialogOnStartup(boolean show) {
		propertyHandler.put("options_dialogzeigen", show);
	}

	public Level getLogLevel() {
		try {
			String temp = propertyHandler.get("options_logging_level");
			Level result = Level.OFF;
			if (temp.compareToIgnoreCase("INFO") == 0) {
				return Level.INFO;
			} else if (temp.compareToIgnoreCase("DEBUG") == 0) {
				return Level.DEBUG;
			} else if (temp.compareToIgnoreCase("WARN") == 0) {
				return Level.WARN;
			} else if (temp.compareToIgnoreCase("FATAL") == 0) {
				return Level.FATAL;
			} else if (temp.compareToIgnoreCase("ALL") == 0) {
				return Level.ALL;
			} else if (temp.compareToIgnoreCase("OFF") == 0) {
				return Level.OFF;
			}

			if (logger.isEnabledFor(Level.DEBUG)) {
				logger.debug("Aktueller Loglevel: " + result.toString());
			}
			return result;
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return null;
		}
	}

	public void setLogLevel(Level level) {
		if (level == null) {
			level = Level.OFF;
		}
		String temp = "OFF";
		if (level == Level.ALL) {
			temp = "ALL";
		} else if (level == Level.INFO) {
			temp = "INFO";
		} else if (level == Level.DEBUG) {
			temp = "DEBUG";
		} else if (level == Level.WARN) {
			temp = "WARN";
		} else if (level == Level.FATAL) {
			temp = "FATAL";
		}
		propertyHandler.put("options_logging_level", temp);
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(level);
		rootLogger.removeAllAppenders();
		if (level != Level.OFF) {
			try {
				FileAppender fileAppender = new FileAppender(AppleJuiceClient
						.getLoggerHtmlLayout(), AppleJuiceClient
						.getLoggerFileAppenderPath());
				rootLogger.addAppender(fileAppender);
			} catch (IOException ioe) {
				rootLogger.addAppender(new ConsoleAppender());
				ioe.printStackTrace();
			}
		} else {
			rootLogger.addAppender(new ConsoleAppender());
		}
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("Loglevel geaendert in " + level.toString());
		}
	}

	public Settings getSettings() {
		try {
			if (settings == null) {
				settings = new Settings();
			}
			Color downloadFertigHintergrundColor = null;
			Color quelleHintergrundColor = null;
			boolean farbenAktiv;
			boolean downloadUebersicht;
			boolean loadPlugins;
			boolean enableToolTip;
			String temp;
			farbenAktiv = (propertyHandler.get("options_farben_aktiv"))
					.equals("true");
			temp = propertyHandler
					.get("options_farben_hintergrund_downloadFertig");
			if (temp.length() != 0) {
				downloadFertigHintergrundColor = new Color(Integer
						.parseInt(temp));
			}
			temp = propertyHandler.get("options_farben_hintergrund_quelle");
			if (temp.length() != 0) {
				quelleHintergrundColor = new Color(Integer.parseInt(temp));
			}
			downloadUebersicht = propertyHandler.getAsBoolean(
					"options_download_uebersicht").booleanValue();
			loadPlugins = propertyHandler.getAsBoolean("options_loadplugins")
					.booleanValue();
			enableToolTip = propertyHandler.getAsBoolean(
					"options_enableToolTip").booleanValue();
			settings.setFarbenAktiv(farbenAktiv);
			settings
					.setDownloadFertigHintergrundColor(downloadFertigHintergrundColor);
			settings.setQuelleHintergrundColor(quelleHintergrundColor);
			settings.setDownloadUebersicht(downloadUebersicht);
			settings.loadPluginsOnStartup(loadPlugins);
			settings.enableToolTipEnabled(enableToolTip);
			return settings;
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return null;
		}
	}

	public void saveSettings(Settings settings) {
		propertyHandler.put("options_farben_aktiv", settings.isFarbenAktiv());
		propertyHandler.put("options_farben_hintergrund_downloadFertig",
				settings.getDownloadFertigHintergrundColor().getRGB());
		propertyHandler.put("options_farben_hintergrund_quelle", settings
				.getQuelleHintergrundColor().getRGB());
		propertyHandler.put("options_download_uebersicht", settings
				.isDownloadUebersicht());
		propertyHandler.put("options_loadplugins", settings
				.shouldLoadPluginsOnStartup());
		propertyHandler.put("options_enableToolTip", settings
				.isToolTipEnabled());
		informSettingsListener(settings);
	}

	public ConnectionSettings getRemoteSettings() {
		try {
			if (connectionSettings == null) {
				connectionSettings = new ConnectionSettings();
			}
			String host = "localhost";
			String passwort = "";
			int xmlPort = 9851;
			host = propertyHandler.get("options_remote_host");
			passwort = propertyHandler.get("options_remote_passwort");
			xmlPort = Integer.parseInt(propertyHandler
					.get("options_remote_port"));
			connectionSettings.setHost(host);
			if (passwort.length() == 0) {
				connectionSettings.setOldPassword("");
			} else {
				connectionSettings.setOldMD5Password(passwort);
			}
			connectionSettings.setXmlPort(xmlPort);
			return connectionSettings;
		} catch (Exception e) {
			AppleJuiceDialog.rewriteProperties = true;
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			return null;
		}
	}

	public void saveRemote(ConnectionSettings remote)
			throws InvalidPasswordException {
		propertyHandler.put("options_remote_host", remote.getHost());
		try {
			AppleJuiceClient.getAjFassade().setPassword(remote.getNewPassword(), true);
		} catch (de.applejuicenet.client.fassade.exception.IllegalArgumentException e) {
			logger.error(e);
		}
		propertyHandler.put("options_remote_passwort", remote.getNewPassword());
		propertyHandler.put("options_remote_port", remote.getXmlPort());
		informConnectionSettingsListener(getRemoteSettings());
	}

	public void onlySaveRemote(ConnectionSettings remote) {
		propertyHandler.put("options_remote_host", remote.getHost());
		propertyHandler.put("options_remote_passwort", remote.getNewPassword());
		propertyHandler.put("options_remote_port", remote.getXmlPort());
		connectionSettings = remote;
	}

	public void saveAJSettings(AJSettings ajSettings) {
		AppleJuiceClient.getAjFassade().saveAJSettings(ajSettings);
	}

	private boolean isVeraltet() {
		return false;
	}

	public static void restoreProperties() {
		PropertyHandler aPropertyHandler = null;
		try {
			aPropertyHandler = new PropertyHandler(AppleJuiceClient
					.getPropertiesPath(), "appleJuice-Java-GUI Propertyfile",
					false);
			aPropertyHandler.put("options_dialogzeigen", true);
			aPropertyHandler.put("options_firststart", true);
			aPropertyHandler.put("options_sound", true);
			aPropertyHandler.put("options_sprache", "deutsch");
			aPropertyHandler.put("options_themes", false);
			aPropertyHandler.put("options_defaulttheme", "toxicthemepack");
			aPropertyHandler.put("options_loadplugins", true);
			aPropertyHandler.put("options_enableToolTip", true);
			aPropertyHandler.put("options_linklistenerport", 8768);
			aPropertyHandler.put("options_versionsinfo", 1);
			aPropertyHandler.put("options_remote_host", "localhost");
			aPropertyHandler.put("options_remote_passwort", "");
			aPropertyHandler.put("options_remote_port", 9851);

			aPropertyHandler.put("options_logging_level", "INFO");
			aPropertyHandler.put("options_download_uebersicht", true);
			aPropertyHandler.put("options_farben_aktiv", true);

			aPropertyHandler.put("options_farben_hintergrund_downloadFertig",
					-13382656);
			aPropertyHandler.put("options_farben_hintergrund_quelle", -205);

			aPropertyHandler.put("options_lookandfeels_laf1_name",
					"JGoodies Plastic");
			aPropertyHandler.put("options_lookandfeels_laf1_value",
					"com.jgoodies.plaf.plastic.Plastic3DLookAndFeel");
			int index = 2;
			if (System.getProperty("os.name").toLowerCase().indexOf("win") != -1) {
				aPropertyHandler.put("options_lookandfeels_laf" + index
						+ "_name", "JGoodies Windows");
				aPropertyHandler.put("options_lookandfeels_laf" + index
						+ "_value",
						"com.jgoodies.plaf.windows.ExtWindowsLookAndFeel");
				index++;
			}
			LookAndFeelInfo[] feels = UIManager.getInstalledLookAndFeels();
			LookAndFeel currentFeel = UIManager.getLookAndFeel();
			for (int i = 0; i < feels.length; i++) {
				try {
					UIManager.setLookAndFeel(feels[i].getClassName());
					aPropertyHandler.put("options_lookandfeels_laf" + index
							+ "_name", feels[i].getName());
					aPropertyHandler.put("options_lookandfeels_laf" + index
							+ "_value", feels[i].getClassName());
					index++;
				} catch (Exception e) {
					//unsupported
				}
			}
			try {
				UIManager.setLookAndFeel(currentFeel);
			} catch (Exception ex) {
				//muss klappen
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
				}
			}
			aPropertyHandler.put("options_lookandfeels_default_name",
					"JGoodies Plastic");
			aPropertyHandler.put("options_location_height", "");
			aPropertyHandler.put("options_location_width", "");
			aPropertyHandler.put("options_location_x", "");
			aPropertyHandler.put("options_location_y", "");
			aPropertyHandler
					.put("options_columns_download_column0_width", 80);
			aPropertyHandler.put("options_columns_download_column0_index", 0);
			for (int i = 1; i < 10; i++) {
				aPropertyHandler.put("options_columns_download_column" + i
						+ "_width", 80);
				aPropertyHandler.put("options_columns_download_column" + i
						+ "_visibility", true);
				aPropertyHandler.put("options_columns_download_column" + i
						+ "_index", i);
			}
			aPropertyHandler.put("options_columns_upload_column0_width", 90);
			aPropertyHandler.put("options_columns_upload_column0_index", 0);
			for (int i = 1; i < 8; i++) {
				aPropertyHandler.put("options_columns_upload_column" + i
						+ "_width", 90);
				aPropertyHandler.put("options_columns_upload_column" + i
						+ "_visibility", true);
				aPropertyHandler.put("options_columns_upload_column" + i
						+ "_index", i);
			}
			aPropertyHandler.put("options_columns_server_column0_width", 175);
			aPropertyHandler.put("options_columns_server_column0_index", 0);
			for (int i = 1; i < 5; i++) {
				aPropertyHandler.put("options_columns_server_column" + i
						+ "_width", 175);
				aPropertyHandler.put("options_columns_server_column" + i
						+ "_visibility", true);
				aPropertyHandler.put("options_columns_server_column" + i
						+ "_index", i);
			}
			aPropertyHandler.put("options_columns_search_column0_width", 103);
			aPropertyHandler.put("options_columns_search_column0_index", 0);
			for (int i = 1; i < 6; i++) {
				aPropertyHandler.put("options_columns_search_column" + i
						+ "_width", 103);
				aPropertyHandler.put("options_columns_search_column" + i
						+ "_visibility", true);
				aPropertyHandler.put("options_columns_search_column" + i
						+ "_index", i);
			}
			aPropertyHandler.put("options_columns_share_column0_width", 194);
			aPropertyHandler.put("options_columns_share_column0_index", 0);
			for (int i = 1; i < 6; i++) {
				aPropertyHandler.put("options_columns_share_column" + i
						+ "_width", 194);
				aPropertyHandler.put("options_columns_share_column" + i
						+ "_visibility", true);
				aPropertyHandler.put("options_columns_share_column" + i
						+ "_index", i);
			}
			aPropertyHandler.put("options_browser_file", "");
			aPropertyHandler.put("options_program_file", -1);
			aPropertyHandler.put("options_proxy_host", "");
			aPropertyHandler.put("options_proxy_port", "");
			aPropertyHandler.put("options_proxy_use", false);
			aPropertyHandler.put("options_proxy_userpass", "");
			aPropertyHandler.save();
		} catch (IllegalArgumentException e1) {
			// sollte eigentlich nie passieren
			e1.printStackTrace();
		}
	}

	protected void init() {
		try {
			propertyHandler = new PropertyHandler(path,
					"appleJuice-Java-GUI Propertyfile", true);
			if (isVeraltet()) {
				throw new Exception(
						"ajgui.properties hat altes Format. Wird neu erstellt.");
			}
			String temp = propertyHandler.get("options_location_x");
			if (temp.length() != 0) {
				legal = true;

				int mainX = propertyHandler.getAsInt("options_location_x")
						.intValue();
				int mainY = propertyHandler.getAsInt("options_location_y")
						.intValue();

				mainXY = new Point(mainX, mainY);
				int mainWidth = propertyHandler.getAsInt(
						"options_location_width").intValue();
				int mainHeight = propertyHandler.getAsInt(
						"options_location_height").intValue();
				mainDimension = new Dimension(mainWidth, mainHeight);
			}
			String xmlTest = propertyHandler
					.get("options_columns_download_column0_width");
			if (xmlTest.length() == 0) {
				throw new Exception(
						"Properties.xml hat altes Format. Wird neu erstellt.");
			}
			downloadWidths = new int[10];
			downloadWidths[0] = Integer.parseInt(xmlTest);
			for (int i = 1; i < downloadWidths.length; i++) {
				downloadWidths[i] = propertyHandler.getAsInt(
						"options_columns_download_column" + i + "_width")
						.intValue();
			}
			uploadWidths = new int[8];
			for (int i = 0; i < uploadWidths.length; i++) {
				uploadWidths[i] = propertyHandler.getAsInt(
						"options_columns_upload_column" + i + "_width")
						.intValue();
			}
			serverWidths = new int[5];
			for (int i = 0; i < serverWidths.length; i++) {
				serverWidths[i] = propertyHandler.getAsInt(
						"options_columns_server_column" + i + "_width")
						.intValue();
			}
			shareWidths = new int[6];
			for (int i = 0; i < shareWidths.length; i++) {
				shareWidths[i] = propertyHandler.getAsInt(
						"options_columns_share_column" + i + "_width")
						.intValue();
			}
			downloadVisibilities = new boolean[10];
			downloadVisibilities[0] = true;
			xmlTest = propertyHandler
					.get("options_columns_download_column1_visibility");
			if (xmlTest.length() == 0) {
				throw new Exception(
						"Properties.xml hat altes Format. Wird neu erstellt.");
			}
			downloadVisibilities[1] = new Boolean(xmlTest).booleanValue();
			for (int i = 2; i < downloadVisibilities.length; i++) {
				downloadVisibilities[i] = propertyHandler.getAsBoolean(
						"options_columns_download_column" + i + "_visibility")
						.booleanValue();
			}
			uploadVisibilities = new boolean[8];
			uploadVisibilities[0] = true;
			for (int i = 1; i < uploadVisibilities.length; i++) {
				uploadVisibilities[i] = propertyHandler.getAsBoolean(
						"options_columns_upload_column" + i + "_visibility")
						.booleanValue();
			}
			downloadIndex = new int[10];
			xmlTest = propertyHandler
					.get("options_columns_download_column0_index");
			if (xmlTest.length() == 0) {
				throw new Exception(
						"Properties.xml hat altes Format. Wird neu erstellt.");
			}
			downloadIndex[0] = Integer.parseInt(xmlTest);
			for (int i = 1; i < downloadIndex.length; i++) {
				downloadIndex[i] = propertyHandler.getAsInt(
						"options_columns_download_column" + i + "_index")
						.intValue();
			}
			uploadIndex = new int[8];
			for (int i = 1; i < uploadIndex.length; i++) {
				uploadIndex[i] = propertyHandler.getAsInt(
						"options_columns_upload_column" + i + "_index")
						.intValue();
			}
			boolean use = propertyHandler.getAsBoolean("options_proxy_use")
					.booleanValue();
			int port;
			Integer tmp = propertyHandler.getAsInt("options_proxy_port");
			if (tmp != null) {
				port = tmp.intValue();
			} else {
				port = -1;
			}
			String userpass = propertyHandler.get("options_proxy_userpass");
			String host = propertyHandler.get("options_proxy_host");
			proxySettings = new ProxySettings(use, host, port, userpass);
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(PROPERTIES_ERROR_MESSAGE, e);
			}
			if (firstReadError == true) {
				PropertiesManager.restoreProperties();
				AppleJuiceDialog.showInformation(PROPERTIES_ERROR);
				firstReadError = false;
				init();
			} else {
				AppleJuiceDialog.rewriteProperties = true;
				AppleJuiceDialog.closeWithErrormessage(PROPERTIES_ERROR, false);
			}
		}
	}

	public void save() {
		try {
			propertyHandler.put("options_location_x", mainXY.x);
			propertyHandler.put("options_location_y", mainXY.y);
			propertyHandler.put("options_location_width", mainDimension.width);
			propertyHandler
					.put("options_location_height", mainDimension.height);

			for (int i = 0; i < downloadWidths.length; i++) {
				propertyHandler.put("options_columns_download_column" + i
						+ "_width", downloadWidths[i]);
			}
			for (int i = 0; i < uploadWidths.length; i++) {
				propertyHandler.put("options_columns_upload_column" + i
						+ "_width", uploadWidths[i]);
			}
			for (int i = 0; i < serverWidths.length; i++) {
				propertyHandler.put("options_columns_server_column" + i
						+ "_width", serverWidths[i]);
			}
			for (int i = 0; i < shareWidths.length; i++) {
				propertyHandler.put("options_columns_share_column" + i
						+ "_width", shareWidths[i]);
			}
			for (int i = 0; i < downloadVisibilities.length; i++) {
				propertyHandler.put("options_columns_download_column" + i
						+ "_visibility", downloadVisibilities[i]);
			}
			for (int i = 0; i < uploadVisibilities.length; i++) {
				propertyHandler.put("options_columns_upload_column" + i
						+ "_visibility", uploadVisibilities[i]);
			}
			for (int i = 0; i < downloadIndex.length; i++) {
				propertyHandler.put("options_columns_download_column" + i
						+ "_index", downloadIndex[i]);
			}
			for (int i = 0; i < uploadIndex.length; i++) {
				propertyHandler.put("options_columns_upload_column" + i
						+ "_index", uploadIndex[i]);
			}
			saveFile();
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void setMainXY(Point p) {
		mainXY = p;
	}

	public Point getMainXY() {
		return mainXY;
	}

	public void setMainDimension(Dimension dimension) {
		mainDimension = dimension;
	}

	public Dimension getMainDimension() {
		return mainDimension;
	}

	public void setDownloadWidths(int[] widths) {
		downloadWidths = widths;
	}

	public boolean isLegal() {
		return legal;
	}

	public int[] getDownloadWidths() {
		return downloadWidths;
	}

	public int[] getUploadWidths() {
		return uploadWidths;
	}

	public void setUploadWidths(int[] uploadWidths) {
		this.uploadWidths = uploadWidths;
	}

	public int[] getServerWidths() {
		return serverWidths;
	}

	public void setServerWidths(int[] serverWidths) {
		this.serverWidths = serverWidths;
	}

	public int[] getShareWidths() {
		return shareWidths;
	}

	public void setShareWidths(int[] shareWidths) {
		this.shareWidths = shareWidths;
	}

	public void setDownloadColumnVisible(int column, boolean visible) {
		if (column != 0) {
			downloadVisibilities[column] = visible;
		}
	}

	public boolean[] getDownloadColumnVisibilities() {
		return downloadVisibilities;
	}

	public void setDownloadColumnIndex(int column, int index) {
		downloadIndex[column] = index;
	}

	public int[] getDownloadColumnIndizes() {
		return downloadIndex;
	}

	public void setUploadColumnIndex(int column, int index) {
		uploadIndex[column] = index;
	}

	public int[] getUploadColumnIndizes() {
		return uploadIndex;
	}

	public void setUploadColumnVisible(int column, boolean visible) {
		if (column != 0) {
			uploadVisibilities[column] = visible;
		}
	}

	public boolean[] getUploadColumnVisibilities() {
		return uploadVisibilities;
	}

	public ConnectionSettings[] getConnectionsSet() {
		ArrayList connectionSet = new ArrayList();
		for (int i = 0;; i++) {
			ConnectionSettings temp = new ConnectionSettings();
			temp.setHost(propertyHandler.get("options_remote" + i + "_host"));
			if (temp.getHost().length() == 0){
				break;
			}
			Integer tmpPort = propertyHandler.getAsInt(
					"options_remote" + i + "_port");
			if (tmpPort != null){
				temp.setXmlPort(tmpPort.intValue());				
			}
			else{
				temp.setXmlPort(-1);				
			}
			connectionSet.add(temp);
		}
		return (ConnectionSettings[]) connectionSet
				.toArray(new ConnectionSettings[] {});
	}

	public void setConnectionsSet(ConnectionSettings[] set) {
		for (int i = 0; i < set.length; i++) {
			if ((set.length - 1 < i) || ("".equals(set[i].getHost()))) {
				propertyHandler.put("options_remote" + i + "_host", "");
				propertyHandler.put("options_remote" + i + "_port", 0);
			} else {
				propertyHandler.put("options_remote" + i + "_host", set[i]
						.getHost());
				propertyHandler.put("options_remote" + i + "_port", set[i]
						.getXmlPort());
			}
		}
	}
}
