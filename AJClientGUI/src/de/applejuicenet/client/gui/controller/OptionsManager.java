package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.28 2003/11/16 12:34:23 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: OptionsManager.java,v $
 * Revision 1.28  2003/11/16 12:34:23  maj0r
 * Themes einngebaut (Danke an LinuxDoc)
 *
 * Revision 1.27  2003/10/31 11:31:45  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt. Kommen noch mehr.
 *
 * Revision 1.26  2003/10/14 15:43:40  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.25  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
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

    public void saveAJSettings(AJSettings ajSettings);

    public String[] getActualServers();

    public String getDefaultTheme();

    public void setDefaultTheme(String themeShortName);
}
