package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.24 2003/09/09 12:28:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: OptionsManager.java,v $
 * Revision 1.24  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 *
 */

public interface OptionsManager {
    public void addSettingsListener(DataUpdateListener listener);

    public void addConnectionSettingsListener(DataUpdateListener listener);

    public String getSprache();

    public boolean isErsterStart();

    public void setErsterStart(boolean ersterStart);

    public void setSprache(String sprache);

    public Level getLogLevel();

    public void setLogLevel(Level level);

    public Settings getSettings();

    public void saveSettings(Settings settings);

    public ConnectionSettings getRemoteSettings();

    public void saveRemote(ConnectionSettings remote) throws
            InvalidPasswordException;

    public boolean saveAJSettings(AJSettings ajSettings);

}
