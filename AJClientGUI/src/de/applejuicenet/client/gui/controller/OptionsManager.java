package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.35 2004/01/29 15:52:33 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: OptionsManager.java,v $
 * Revision 1.35  2004/01/29 15:52:33  maj0r
 * Bug #153 umgesetzt (Danke an jr17)
 * Verbindungsdialog kann nun per Option beim naechsten GUI-Start erzwungen werden.
 *
 * Revision 1.34  2004/01/05 19:17:18  maj0r
 * Bug #56 gefixt (Danke an MeineR)
 * Das Laden der Plugins beim Start kann über das Optionenmenue deaktiviert werden.
 *
 * Revision 1.33  2004/01/05 07:28:59  maj0r
 * Begonnen einen Standardwebbrowser einzubauen.
 *
 * Revision 1.32  2004/01/02 16:48:30  maj0r
 * Serverliste holen geaendert.
 *
 * Revision 1.31  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.30  2003/12/29 07:23:18  maj0r
 * Begonnen, auf neues Versionupdateinformationssystem umzubauen.
 *
 * Revision 1.29  2003/11/18 16:41:50  maj0r
 * Erste Version des LinkListener eingebaut.
 * Themes koennen nun ueber die properties.xml komplett deaktiviert werden.
 *
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

    public void saveAJSettings(AJSettings ajSettings);

    public String getDefaultTheme();

    public void setDefaultTheme(String themeShortName);

    public boolean isThemesSupported();

    public void enableThemeSupport(boolean enable);

    public int getLinkListenerPort();

    public void setLinkListenerPort(int port);

    public String getStandardBrowser();

    public void setStandardBrowser(String browser);

    public void loadPluginsOnStartup(boolean loadPluginsOnStartup);

    public boolean shouldLoadPluginsOnStartup();

    public boolean shouldShowConnectionDialogOnStartup();

    public void showConnectionDialogOnStartup(boolean show);
}
