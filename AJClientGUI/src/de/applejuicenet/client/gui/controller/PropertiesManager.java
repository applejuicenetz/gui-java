package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/PropertiesManager.java,v 1.9 2003/10/21 14:08:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: PropertiesManager.java,v $
 * Revision 1.9  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.8  2003/10/17 13:33:02  maj0r
 * properties.xml wird nun im Fehlerfall automatisch generiert.
 *
 * Revision 1.7  2003/10/14 15:42:05  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.6  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
 * Revision 1.5  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.4  2003/09/12 13:46:41  maj0r
 * Bug behoben.
 *
 * Revision 1.3  2003/09/12 13:31:55  maj0r
 * Bugs behoben.
 *
 * Revision 1.2  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 * Revision 1.1  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.23  2003/09/08 06:27:11  maj0r
 * Um Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.22  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
 * Revision 1.21  2003/08/19 12:38:47  maj0r
 * Passworteingabe und md5 korrigiert.
 *
 * Revision 1.20  2003/08/16 18:40:25  maj0r
 * Passworteingabe korrigiert.
 *
 * Revision 1.19  2003/08/16 17:50:06  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.18  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.17  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 * Revision 1.16  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.15  2003/07/01 14:55:06  maj0r
 * Unnütze Abfrage entfernt.
 *
 * Revision 1.14  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.13  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.12  2003/06/22 19:01:22  maj0r
 * Hostverwendung korrigiert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class PropertiesManager
        extends XMLDecoder implements OptionsManager, PositionManager, ProxyManager{
    private static PropertiesManager instance = null;
    private Logger logger;
    private HashSet settingsListener = new HashSet();
    private HashSet connectionSettingsListener = new HashSet();
    private Point mainXY;
    private Dimension mainDimension;
    private ProxySettings proxySettings;

    private int[] downloadWidths;
    private int[] uploadWidths;
    private int[] serverWidths;
    private int[] shareWidths;

    private static String path;

    private boolean legal = false;

    private PropertiesManager(String path) {
        super(path);
        logger = Logger.getLogger(getClass());
        init();
    }

    public static OptionsManager getOptionsManager() {
        if (instance == null) {
            path = System.getProperty("user.dir") + File.separator +
                    "properties.xml";
            instance = new PropertiesManager(path);
        }
        return instance;
    }

    public static PositionManager getPositionManager() {
        if (instance == null) {
            String path = System.getProperty("user.dir") + File.separator +
                    "properties.xml";
            instance = new PropertiesManager(path);
        }
        return instance;
    }

    public static ProxyManager getProxyManager() {
        if (instance == null) {
            String path = System.getProperty("user.dir") + File.separator +
                    "properties.xml";
            instance = new PropertiesManager(path);
        }
        return instance;
    }

   private void saveDom() {
        try
        {
            XMLSerializer xs = new XMLSerializer(new FileWriter(path),
                                                 new OutputFormat(document,
                                                                  "UTF-8", true));
            xs.serialize(document);
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("xml-Serialisierung fehlgeschlagen. properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
        }
    }

    //ProxyManager-Interface

    public ProxySettings getProxySettings(){
        return proxySettings;
    }

    public void saveProxySettings(ProxySettings proxySettings){
        this.proxySettings = proxySettings;
        setAttributeByTagName(new String[]{"options", "proxy", "use"}, Boolean.toString(proxySettings.isUse()));
        setAttributeByTagName(new String[]{"options", "proxy", "host"}, proxySettings.getHost());
        setAttributeByTagName(new String[]{"options", "proxy", "port"}, Integer.toString(proxySettings.getPort()));
        setAttributeByTagName(new String[]{"options", "proxy", "userpass"}, proxySettings.getUserpass());
        saveDom();
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
            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.SETTINGS_CHANGED, settings);
        }
    }

    private void informConnectionSettingsListener(ConnectionSettings settings) {
        Iterator it = connectionSettingsListener.iterator();
        while (it.hasNext()) {
            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.CONNECTION_SETTINGS_CHANGED, settings);
        }
    }

    public String getSprache() {
        try{
            return getFirstAttrbuteByTagName(new String[]{"options", "sprache"});
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
            return null;
        }
    }

    public boolean isErsterStart() {
        try{
            String temp = getFirstAttrbuteByTagName(new String[]{"options", "firststart"});;
            if (temp==null || temp.length()==0)
                return true;
            return new Boolean(temp).booleanValue();
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
            return false;
        }
    }

    public void setErsterStart(boolean ersterStart) {
        setAttributeByTagName(new String[]{"options", "firststart"}, Boolean.toString(ersterStart));
    }

    public void setSprache(String sprache) {
        setAttributeByTagName(new String[]{"options", "sprache"}
                , sprache.toLowerCase());
    }

    public Level getLogLevel() {
        try{
            String temp = getFirstAttrbuteByTagName(new String[]{"options", "logging", "level"});
            Level result = Level.OFF;
            if (temp.compareToIgnoreCase("INFO") == 0)
                return Level.INFO;
            else if (temp.compareToIgnoreCase("DEBUG") == 0)
                return Level.DEBUG;
            else if (temp.compareToIgnoreCase("WARN") == 0)
                return Level.WARN;
            else if (temp.compareToIgnoreCase("FATAL") == 0)
                return Level.FATAL;
            else if (temp.compareToIgnoreCase("ALL") == 0)
                return Level.ALL;

            if (logger.isEnabledFor(Level.DEBUG))
                logger.debug("Aktueller Loglevel: " + result.toString());
            return result;
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
            return null;
        }
    }

    public void setLogLevel(Level level) {
        if (level == null)
            level = Level.OFF;
        String temp = "OFF";
        if (level == Level.ALL)
            temp = "ALL";
        else if (level == Level.INFO)
            temp = "INFO";
        else if (level == Level.DEBUG)
            temp = "DEBUG";
        else if (level == Level.WARN)
            temp = "WARN";
        else if (level == Level.FATAL)
            temp = "FATAL";
        setAttributeByTagName(new String[]{"options", "logging", "level"}, temp);
        Logger.getRootLogger().setLevel(level);
        if (logger.isEnabledFor(Level.DEBUG))
            logger.debug("Loglevel geändert in " + level.toString());
    }

    public Settings getSettings() {
        try{
            Color downloadFertigHintergrundColor = null;
            Color quelleHintergrundColor = null;
            Boolean farbenAktiv = null;
            Boolean downloadUebersicht = null;
            String temp;
            temp = getFirstAttrbuteByTagName(new String[]{"options", "farben",
                                                          "aktiv"});
            if (temp.length() != 0) {
                farbenAktiv = new Boolean(temp);
            }
            temp = getFirstAttrbuteByTagName(new String[]{"options", "farben",
                                                          "hintergrund", "downloadFertig"});
            if (temp.length() != 0) {
                downloadFertigHintergrundColor = new Color(Integer.parseInt(temp));
            }
            temp = getFirstAttrbuteByTagName(new String[]{"options", "farben",
                                                          "hintergrund", "quelle"});
            if (temp.length() != 0) {
                quelleHintergrundColor = new Color(Integer.parseInt(temp));
            }
            temp = getFirstAttrbuteByTagName(new String[]{"options", "download",
                                                          "uebersicht"});
            if (temp.length() != 0) {
                downloadUebersicht = new Boolean(temp);
            }
            return new Settings(farbenAktiv, downloadFertigHintergrundColor, quelleHintergrundColor, downloadUebersicht);
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
            return null;
        }
    }


    public void saveSettings(Settings settings) {
        setAttributeByTagName(new String[]{"options", "farben", "aktiv"},
                Boolean.toString(settings.isFarbenAktiv()));
        setAttributeByTagName(new String[]{"options", "farben", "hintergrund", "downloadFertig"},
                Integer.toString(settings.getDownloadFertigHintergrundColor().getRGB()));
        setAttributeByTagName(new String[]{"options", "farben", "hintergrund", "quelle"},
                Integer.toString(settings.getQuelleHintergrundColor().getRGB()));
        setAttributeByTagName(new String[]{"options", "download", "uebersicht"},
                Boolean.toString(settings.isDownloadUebersicht()));
        informSettingsListener(settings);
    }

    public ConnectionSettings getRemoteSettings() {
        try{
            String host = "localhost";
            String passwort = "";
            int xmlPort = 9851;
            host = getFirstAttrbuteByTagName(new String[]{"options", "remote",
                                                          "host"});
            passwort = getFirstAttrbuteByTagName(new String[]{"options",
                                                              "remote", "passwort"});
            xmlPort = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "remote",
                                                          "port"}));
            return new ConnectionSettings(host, passwort, xmlPort);
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
            return null;
        }
    }

    public void saveRemote(ConnectionSettings remote) throws
            InvalidPasswordException {
        setAttributeByTagName(new String[]{"options", "remote", "host"}
                , remote.getHost());
        ApplejuiceFassade.setPassword(remote.getNewPassword());
        setAttributeByTagName(new String[]{"options", "remote", "passwort"},
                remote.getNewPassword());
        setAttributeByTagName(new String[]{"options", "remote", "port"},
                Integer.toString(remote.getXmlPort()));
        informConnectionSettingsListener(getRemoteSettings());
    }

    public void saveAJSettings(AJSettings ajSettings) {
        ApplejuiceFassade.getInstance().saveAJSettings(ajSettings);
    }

    public String[] getActualServers() {
        try{
            String serverUrl;
            String serverPfad;
            serverUrl = getFirstAttrbuteByTagName(new String[]{"options", "server",
                                                          "url"});
            serverPfad = getFirstAttrbuteByTagName(new String[]{"options",
                                                              "server", "pfad"});
            String webContent = WebsiteContentLoader.getWebsiteContent(serverUrl, 80, serverPfad);
            StringBuffer temp = new StringBuffer(webContent);
            int pos = 0;
            int endIndex;
            ArrayList servers = new ArrayList();
            while ((pos = temp.indexOf("ajfsp", pos))!=-1){
                endIndex = temp.indexOf("\"", pos);
                String test = temp.substring(pos, endIndex);
                servers.add(test);
                pos = endIndex;
            }
            return (String[]) servers.toArray(new String[servers.size()]);
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
            return null;
        }
    }

    //PositionManager-Interface

    protected void init(){
        try{
            String temp = getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "x"});
            if (temp.length()!=0){
                legal = true;
                int mainX = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "x"}));
                int mainY = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "y"}));
                mainXY = new Point(mainX, mainY);
                int mainWidth = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "width"}));
                int mainHeight = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "main", "height"}));
                mainDimension = new Dimension(mainWidth, mainHeight);

                downloadWidths = new int[10];
                downloadWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column0"}));
                downloadWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column1"}));
                downloadWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column2"}));
                downloadWidths[3] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column3"}));
                downloadWidths[4] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column4"}));
                downloadWidths[5] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column5"}));
                downloadWidths[6] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column6"}));
                downloadWidths[7] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column7"}));
                downloadWidths[8] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column8"}));
                downloadWidths[9] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "download", "column9"}));

                uploadWidths = new int[7];
                uploadWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column0"}));
                uploadWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column1"}));
                uploadWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column2"}));
                uploadWidths[3] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column3"}));
                uploadWidths[4] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column4"}));
                uploadWidths[5] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column5"}));
                uploadWidths[6] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "upload", "column6"}));

                serverWidths = new int[4];
                serverWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column0"}));
                serverWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column1"}));
                serverWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column2"}));
                serverWidths[3] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "server", "column3"}));

                shareWidths = new int[3];
                shareWidths[0] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "share", "column0"}));
                shareWidths[1] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "share", "column1"}));
                shareWidths[2] = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "location", "share", "column2"}));
            }
            boolean use = new Boolean(getFirstAttrbuteByTagName(new String[]{"options", "proxy", "use"})).booleanValue();
            String host = getFirstAttrbuteByTagName(new String[]{"options", "proxy", "host"});
            int port;
            try{
                port = Integer.parseInt(getFirstAttrbuteByTagName(new String[]{"options", "proxy", "port"}));
            }
            catch (Exception e){
                port = -1;
            }
            String userpass = getFirstAttrbuteByTagName(new String[]{"options", "proxy", "userpass"});
            proxySettings = new ProxySettings(use, host, port, userpass);
        }
        catch (Exception e)
        {
            AppleJuiceDialog.rewriteProperties = true;
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("properties.xml neu erstellt", e);
            AppleJuiceDialog.closeWithErrormessage("Fehler beim Zugriff auf die properties.xml. " +
                                                   "Die Datei wird neu erstellt.", false);
        }
    }

    public void save(){
        try{
            setAttributeByTagName(new String[]{"options", "location", "main", "x"}, mainXY.x);
            setAttributeByTagName(new String[]{"options", "location", "main", "y"}, mainXY.y);
            setAttributeByTagName(new String[]{"options", "location", "main", "width"}, mainDimension.width);
            setAttributeByTagName(new String[]{"options", "location", "main", "height"}, mainDimension.height);

            setAttributeByTagName(new String[]{"options", "location", "download", "column0"}, downloadWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column1"}, downloadWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column2"}, downloadWidths[2]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column3"}, downloadWidths[3]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column4"}, downloadWidths[4]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column5"}, downloadWidths[5]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column6"}, downloadWidths[6]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column7"}, downloadWidths[7]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column8"}, downloadWidths[8]);
            setAttributeByTagName(new String[]{"options", "location", "download", "column9"}, downloadWidths[9]);

            setAttributeByTagName(new String[]{"options", "location", "upload", "column0"}, uploadWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column1"}, uploadWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column2"}, uploadWidths[2]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column3"}, uploadWidths[3]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column4"}, uploadWidths[4]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column5"}, uploadWidths[5]);
            setAttributeByTagName(new String[]{"options", "location", "upload", "column6"}, uploadWidths[6]);

            setAttributeByTagName(new String[]{"options", "location", "server", "column0"}, serverWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "server", "column1"}, serverWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "server", "column2"}, serverWidths[2]);
            setAttributeByTagName(new String[]{"options", "location", "server", "column3"}, serverWidths[3]);

            setAttributeByTagName(new String[]{"options", "location", "share", "column0"}, shareWidths[0]);
            setAttributeByTagName(new String[]{"options", "location", "share", "column1"}, shareWidths[1]);
            setAttributeByTagName(new String[]{"options", "location", "share", "column2"}, shareWidths[2]);
            saveDom();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void setMainXY(Point p) {
        mainXY = p;
    }

    public Point getMainXY(){
        return mainXY;
    }

    public void setMainDimension(Dimension dimension) {
        mainDimension = dimension;
    }

    public Dimension getMainDimension(){
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
}