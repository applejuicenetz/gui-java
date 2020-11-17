package de.applejuicenet.client.gui.controller;

import org.apache.log4j.Level;

import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManagerImpl.java,v 1.6 2005/01/18 17:35:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
**/

public class OptionsManagerImpl implements OptionsManager{
    private static OptionsManager instance = null;
    private PropertiesManager propertiesManager;

    private OptionsManagerImpl(){
        propertiesManager = PropertiesManager.getInstance();
    }

    public static synchronized OptionsManager getInstance(){
        if (instance==null){
            instance = new OptionsManagerImpl();
        }
        return instance;
    }

    public void addSettingsListener(DataUpdateListener listener) {
        propertiesManager.addSettingsListener(listener);
    }

    public void addConnectionSettingsListener(DataUpdateListener listener) {
        propertiesManager.addConnectionSettingsListener(listener);
    }

    public String getSprache() {
        return propertiesManager.getSprache();
    }

    public boolean getUpdateInfo() {
        return propertiesManager.getUpdateInfo();
    }

    public void setUpdateInfo(boolean UpdateInfo) {
        propertiesManager.setUpdateInfo(UpdateInfo);
    }

    public boolean isErsterStart() {
        return propertiesManager.isErsterStart();
    }

    public void setErsterStart(boolean ersterStart) {
        propertiesManager.setErsterStart(ersterStart);
    }

    public boolean isSoundEnabled() {
        return propertiesManager.isSoundEnabled();
    }

    public void enableSound(boolean enable) {
        propertiesManager.enableSound(enable);
    }

    public void setSprache(String sprache) {
        propertiesManager.setSprache(sprache);
    }

    public Level getLogLevel() {
        return propertiesManager.getLogLevel();
    }

    public void setLogLevel(Level level) {
        propertiesManager.setLogLevel(level);
    }

    public Settings getSettings() {
        return propertiesManager.getSettings();
    }

    public void saveSettings(Settings settings) {
        propertiesManager.saveSettings(settings);
    }

    public ConnectionSettings getRemoteSettings() {
        return propertiesManager.getRemoteSettings();
    }

    public void saveRemote(ConnectionSettings remote) throws InvalidPasswordException{
        propertiesManager.saveRemote(remote);
    }

    public void onlySaveRemote(ConnectionSettings remote) {
        propertiesManager.onlySaveRemote(remote);
    }

    public void saveAJSettings(AJSettings ajSettings) {
        propertiesManager.saveAJSettings(ajSettings);
    }

    public String getDefaultTheme() {
        return propertiesManager.getDefaultTheme();
    }

    public String getNewsURL() {
        return propertiesManager.getNewsURL();
    }

    public String getServerListURL() {
        return propertiesManager.getServerListURL();
    }

    public String getUpdateServerURL() {
        return propertiesManager.getUpdateServerURL();
    }

    public void setDefaultTheme(String themeShortName) {
        propertiesManager.setDefaultTheme(themeShortName);
    }

    public boolean isThemesSupported() {
        return propertiesManager.isThemesSupported();
    }

    public void enableThemeSupport(boolean enable) {
        propertiesManager.enableThemeSupport(enable);
    }

    public LookAFeel[] getLookAndFeels() {
        return propertiesManager.getLookAndFeels();
    }

    public LookAFeel getDefaultLookAndFeel() {
        return propertiesManager.getDefaultLookAndFeel();
    }

    public void setDefaultLookAndFeel(LookAFeel lookAFeel) {
        propertiesManager.setDefaultLookAndFeel(lookAFeel);
    }

    public int getLinkListenerPort() {
        return propertiesManager.getLinkListenerPort();
    }

    public void setLinkListenerPort(int port) {
        propertiesManager.setLinkListenerPort(port);
    }

	public void setOpenProgram(String path) {
        propertiesManager.setOpenProgram(path);
	}

	public String getOpenProgram() {
        return propertiesManager.getOpenProgram();
	}

	public void loadPluginsOnStartup(boolean loadPluginsOnStartup) {
        propertiesManager.loadPluginsOnStartup(loadPluginsOnStartup);
    }

    public boolean shouldLoadPluginsOnStartup() {
        return propertiesManager.shouldLoadPluginsOnStartup();
    }

    public boolean shouldShowConnectionDialogOnStartup() {
        return propertiesManager.shouldShowConnectionDialogOnStartup();
    }

    public void showConnectionDialogOnStartup(boolean show) {
        propertiesManager.showConnectionDialogOnStartup(show);
    }

    public ConnectionSettings[] getConnectionsSet() {
        return propertiesManager.getConnectionsSet();
    }

    public void setConnectionsSet(ConnectionSettings[] set) {
        propertiesManager.setConnectionsSet(set);
    }
}
