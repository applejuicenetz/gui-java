package de.applejuicenet.client.gui.controller;

import org.apache.log4j.Level;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.39 2004/03/09 16:25:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public interface OptionsManager {
    public void addSettingsListener(DataUpdateListener listener);

    public void addConnectionSettingsListener(DataUpdateListener listener);

    public String getSprache();

    public int getVersionsinfoModus();

    public void setVersionsinfoModus(int versionsinfoModus);

    public boolean isErsterStart();

    public void setErsterStart(boolean ersterStart);

    public boolean isSoundEnabled();

    public void enableSound(boolean enable);

    public void setSprache(String sprache);

    public Level getLogLevel();

    public void setLogLevel(Level level);

    public Settings getSettings();

    public void saveSettings(Settings settings);

    public ConnectionSettings getRemoteSettings();

    public void saveRemote(ConnectionSettings remote) throws
        InvalidPasswordException;

    public void onlySaveRemote(ConnectionSettings remote);

    public void saveAJSettings(AJSettings ajSettings);

    public String getDefaultTheme();

    public void setDefaultTheme(String themeShortName);

    public boolean isThemesSupported();

    public void enableThemeSupport(boolean enable);

    public LookAFeel[] getLookAndFeels();

    public int getLinkListenerPort();

    public void setLinkListenerPort(int port);

    public String getStandardBrowser();

    public void setStandardBrowser(String browser);

    public void loadPluginsOnStartup(boolean loadPluginsOnStartup);

    public boolean shouldLoadPluginsOnStartup();

    public boolean shouldShowConnectionDialogOnStartup();

    public void showConnectionDialogOnStartup(boolean show);

}
