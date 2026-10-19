package de.applejuicenet.client.gui.controller;

import ch.qos.logback.classic.Level;
import de.applejuicenet.client.facade.listener.DataUpdateListener;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.LookAFeel;
import de.applejuicenet.client.shared.Settings;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManagerImpl.java,v 1.6 2005/01/18 17:35:28 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
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

    public String getLanguage() {
        return propertiesManager.getLanguage();
    }

    public boolean getUpdateInfo() {
        return propertiesManager.getUpdateInfo();
    }

    public void setUpdateInfo(boolean UpdateInfo) {
        propertiesManager.setUpdateInfo(UpdateInfo);
    }

    public boolean isFirstStart() {
        return propertiesManager.isFirstStart();
    }

    public void setFirstStart(boolean firstStart) {
        propertiesManager.setFirstStart(firstStart);
    }

    public boolean isSoundEnabled() {
        return propertiesManager.isSoundEnabled();
    }

    public void enableSound(boolean enable) {
        propertiesManager.enableSound(enable);
    }

    public void setLanguage(String language) {
        propertiesManager.setLanguage(language);
    }

    public Level getLogLevel() {
        return propertiesManager.getLogLevel();
    }

    public void setLogLevel(String level) {
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

    public void saveRemote(ConnectionSettings remote) {
        propertiesManager.saveRemote(remote);
    }

    public void onlySaveRemote(ConnectionSettings remote) {
        propertiesManager.onlySaveRemote(remote);
    }

    public void saveAJSettings(AJSettings ajSettings) {
        propertiesManager.saveAJSettings(ajSettings);
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

    public String getIconSetName() {
        return propertiesManager.getIconSetName();
    }

    public String getSoundSetName() {
        return propertiesManager.getSoundSetName();
    }

    public void setIconSetName(String name) {
        propertiesManager.setIconSetName(name);
    }

    public void setSoundSetName(String name) {
        propertiesManager.setSoundSetName(name);
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

    public boolean shouldMaskIpInStatusbar() {
        return propertiesManager.shouldMaskIpInStatusbar();
    }

    public void maskIpInStatusbar(boolean mask) {
        propertiesManager.maskIpInStatusbar(mask);
    }

    public boolean shouldCloseToTray() {
        return propertiesManager.shouldCloseToTray();
    }

    public void closeToTray(boolean closeToTray) {
        propertiesManager.closeToTray(closeToTray);
    }

    public long getCorePollIntervalMs() {
        return propertiesManager.getCorePollIntervalMs();
    }

    public void setCorePollIntervalMs(long millis) {
        propertiesManager.setCorePollIntervalMs(millis);
    }

    public long getPartlistRefreshIntervalMs() {
        return propertiesManager.getPartlistRefreshIntervalMs();
    }

    public void setPartlistRefreshIntervalMs(long millis) {
        propertiesManager.setPartlistRefreshIntervalMs(millis);
    }

    public boolean shouldRememberColumnWidths() {
        return propertiesManager.shouldRememberColumnWidths();
    }

    public void rememberColumnWidths(boolean remember) {
        propertiesManager.rememberColumnWidths(remember);
    }

    public boolean isSpeedUnitMb(String field) {
        return propertiesManager.isSpeedUnitMb(field);
    }

    public void setSpeedUnitMb(String field, boolean mb) {
        propertiesManager.setSpeedUnitMb(field, mb);
    }

    public ConnectionSettings[] getConnectionsSet() {
        return propertiesManager.getConnectionsSet();
    }

    public void setConnectionsSet(ConnectionSettings[] set) {
        propertiesManager.setConnectionsSet(set);
    }
}
