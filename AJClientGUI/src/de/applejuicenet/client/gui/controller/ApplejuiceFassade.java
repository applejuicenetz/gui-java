package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.trees.ApplejuiceNode;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.shared.dac.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/ApplejuiceFassade.java,v 1.60 2003/11/03 13:11:35 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ApplejuiceFassade.java,v $
 * Revision 1.60  2003/11/03 13:11:35  maj0r
 * Version erhoeht.
 *
 * Revision 1.59  2003/10/31 18:01:45  maj0r
 * Ungueltige Zeichen entfernt.
 *
 * Revision 1.58  2003/10/31 17:33:30  maj0r
 * Neue Version 0.40
 *
 * Revision 1.57  2003/10/27 17:23:37  maj0r
 * Neue Version 0.39
 *
 * Revision 1.56  2003/10/24 19:35:05  maj0r
 * Neue Version 0.38
 *
 * Revision 1.55  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.54  2003/10/21 11:36:32  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 * Revision 1.53  2003/10/18 19:24:21  maj0r
 * Neue Version 0.37
 *
 * Revision 1.52  2003/10/14 19:21:23  maj0r
 * Korrekturen zur Xml-Port-Verwendung.
 *
 * Revision 1.51  2003/10/14 15:44:32  maj0r
 * Unnoetige Returnwerte ausgebaut.
 * Powerdownloads werden nun innerhalb einer Connection gesetzt,
 *
 * Revision 1.50  2003/10/13 12:37:48  maj0r
 * Bug #1003 behoben.
 *
 * Revision 1.49  2003/10/12 16:12:19  maj0r
 * Neue Version 0.35
 *
 * Revision 1.48  2003/10/10 19:16:04  maj0r
 * Neue Version 0.34
 *
 * Revision 1.47  2003/10/09 15:42:52  maj0r
 * Bug behoben, dass nicht immer der aktuell verbundene Server angezeigt wurde.
 *
 * Revision 1.46  2003/10/06 12:08:01  maj0r
 * Unnoetiges Logging entfernt.
 *
 * Revision 1.45  2003/10/05 11:48:36  maj0r
 * Server koennen nun direkt durch Laden einer Homepage hinzugefuegt werden.
 * Userpartlisten werden angezeigt.
 * Downloadpartlisten werden alle 15 Sek. aktualisiert.
 *
 * Revision 1.44  2003/10/04 15:29:12  maj0r
 * Userpartliste hinzugefuegt.
 *
 * Revision 1.43  2003/10/01 16:52:53  maj0r
 * Suche weiter gefuehrt.
 * Version 0.32
 *
 * Revision 1.42  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.41  2003/09/13 11:30:40  maj0r
 * Neuen Listener fuer Geschwindigkeitsanzeigen eingebaut.
 *
 * Revision 1.40  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 * Revision 1.39  2003/09/11 09:41:16  maj0r
 * Nullpointer behoben.
 *
 * Revision 1.38  2003/09/11 08:39:30  maj0r
 * Start durch Einbau von Threads beschleunigt.
 *
 * Revision 1.37  2003/09/11 06:54:15  maj0r
 * Auf neues Sessions-Prinzip umgebaut.
 * Sprachenwechsel korrigert, geht nun wieder flott.
 *
 * Revision 1.36  2003/09/10 15:30:48  maj0r
 * Begonnen auf neue Session-Struktur umzubauen.
 *
 * Revision 1.35  2003/09/10 13:18:01  maj0r
 * Version 0.28
 *
 * Revision 1.34  2003/09/10 13:16:28  maj0r
 * Veraltete Option "Browsen erlauben" entfernt und neue Option MaxNewConnectionsPerTurn hinzugefuegt.
 *
 * Revision 1.33  2003/09/09 12:47:21  maj0r
 * Version 0.27
 *
 * Revision 1.32  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.31  2003/09/07 09:29:55  maj0r
 * Position des Hauptfensters und Breite der Tabellenspalten werden gespeichert.
 *
 * Revision 1.30  2003/09/06 15:01:06  maj0r
 * Version 0.25
 *
 * Revision 1.29  2003/09/06 14:48:50  maj0r
 * Core-Dateisystem-Separator statisch verwendbar.
 *
 * Revision 1.28  2003/09/05 09:47:22  maj0r
 * Version 0.24
 *
 * Revision 1.27  2003/09/05 09:02:26  maj0r
 * Threadverwendung verbessert.
 *
 * Revision 1.26  2003/09/04 22:12:45  maj0r
 * Logger verfeinert.
 * Threadbeendigung korrigiert.
 *
 * Revision 1.25  2003/09/04 14:00:09  maj0r
 * Version 0.23.
 *
 * Revision 1.24  2003/09/04 06:34:54  maj0r
 * Version erhoeht.
 * v 0.22
 *
 * Revision 1.23  2003/09/02 19:29:26  maj0r
 * Einige Stellen synchronisiert und Nullpointer behoben.
 * Version 0.21 beta.
 *
 * Revision 1.22  2003/09/02 16:08:56  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.21  2003/09/01 19:57:10  maj0r
 * Version 0.19 Beta
 *
 * Revision 1.20  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.19  2003/08/31 11:06:22  maj0r
 * CheckInProgress geaendert.
 *
 * Revision 1.18  2003/08/30 19:46:10  maj0r
 * Version 0.18 Beta
 *
 * Revision 1.17  2003/08/29 19:34:03  maj0r
 * Einige Aenderungen.
 * Version 0.17 Beta
 *
 * Revision 1.16  2003/08/28 15:47:26  maj0r
 * Warten auf Antwort vom Core entfernt.
 *
 * Revision 1.15  2003/08/28 10:57:04  maj0r
 * Versionierung geaendert und erhoeht.
 * Version 0.16 Beta.
 *
 * Revision 1.14  2003/08/28 06:56:48  maj0r
 * Methode setShares korrigiert.
 *
 * Revision 1.13  2003/08/26 19:46:34  maj0r
 * Sharebereich weiter vervollstaendigt.
 *
 * Revision 1.12  2003/08/25 13:17:46  maj0r
 * Muell entfernt.
 *
 * Revision 1.11  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.10  2003/08/22 14:16:00  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.9  2003/08/22 12:52:12  maj0r
 * Version auf 0.13 Beta erhoeht.
 *
 * Revision 1.8  2003/08/22 10:03:11  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.7  2003/08/21 15:13:29  maj0r
 * Auf Thread umgebaut.
 *
 * Revision 1.6  2003/08/20 20:08:25  maj0r
 * Version auf 0.11 erhoeht.
 *
 * Revision 1.5  2003/08/20 16:18:51  maj0r
 * Server koennen nun entfernt werden.
 *
 * Revision 1.4  2003/08/20 07:49:50  maj0r
 * Programmstart beschleunigt.
 *
 * Revision 1.3  2003/08/17 16:13:11  maj0r
 * Erstellen des DirectoryNode-Baumes korrigiert.
 *
 * Revision 1.2  2003/08/16 18:40:25  maj0r
 * Passworteingabe korrigiert.
 *
 * Revision 1.1  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.38  2003/08/12 06:12:19  maj0r
 * Version erhoeht.
 *
 * Revision 1.37  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingefuegt.
 * Diverse Aenderungen.
 *
 * Revision 1.36  2003/08/10 21:08:18  maj0r
 * Diverse Aenderungen.
 *
 * Revision 1.35  2003/08/09 16:47:42  maj0r
 * Diverse Aenderungen.
 *
 * Revision 1.34  2003/08/09 10:57:54  maj0r
 * Upload- und DownloadTabelle weitergef�hrt.
 *
 * Revision 1.33  2003/08/05 20:47:06  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.32  2003/08/05 05:11:59  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.31  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.30  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.29  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.28  2003/07/04 15:25:38  maj0r
 * Version erh�ht.
 * DownloadModel erweitert.
 *
 * Revision 1.27  2003/07/03 19:11:16  maj0r
 * DownloadTable ueberarbeitet.
 *
 * Revision 1.26  2003/07/01 15:00:00  maj0r
 * Keyverwendung bei HashSets und HashMaps korrigiert.
 *
 * Revision 1.25  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.24  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefuegt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.23  2003/06/24 12:06:49  maj0r
 * log4j eingefuegt (inkl. Bedienung ueber Einstellungsdialog).
 *
 * Revision 1.22  2003/06/22 20:34:25  maj0r
 * Konsolenausgaben hinzugefuegt.
 *
 * Revision 1.21  2003/06/13 15:07:30  maj0r
 * Versionsanzeige hinzugefuegt.
 * Da der Controllerteil refactort werden kann, haben Controller und GUI separate Versionsnummern.
 *
 * Revision 1.20  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class ApplejuiceFassade { //Singleton-Implementierung
    public static final String GUI_VERSION = "0.41 Beta";

    private HashSet downloadListener;
    private HashSet searchListener;
    private HashSet shareListener;
    private HashSet uploadListener;
    private HashSet serverListener;
    private HashSet networkInfoListener;
    private HashSet speedListener;
    private HashSet informationListener;
    public static String separator;
    private ModifiedXMLHolder modifiedXML = null;
    private InformationXMLHolder informationXML = null;
    private ShareXMLHolder shareXML = null;
    private SettingsXMLHolder settingsXML = null;
    private DirectoryXMLHolder directoryXML = null;
    private Version coreVersion;
    private HashMap share = null;

    private static ApplejuiceFassade instance = null;

    //Thread
    private Thread workerThread;

    private Logger logger;

    public void addDataUpdateListener(DataUpdateListener listener, int type) {
        try{
            if (type == DataUpdateListener.DOWNLOAD_CHANGED)
            {
                downloadListener.add(listener);
            }
            else if (type == DataUpdateListener.NETINFO_CHANGED)
            {
                networkInfoListener.add(listener);
            }
            else if (type == DataUpdateListener.SERVER_CHANGED)
            {
                serverListener.add(listener);
            }
            else if (type == DataUpdateListener.SHARE_CHANGED)
            {
                shareListener.add(listener);
            }
            else if (type == DataUpdateListener.UPLOAD_CHANGED)
            {
                uploadListener.add(listener);
            }
            else if (type == DataUpdateListener.SPEED_CHANGED)
            {
                speedListener.add(listener);
            }
            else if (type == DataUpdateListener.SEARCH_CHANGED)
            {
                searchListener.add(listener);
            }
            else if (type == DataUpdateListener.INFORMATION_CHANGED)
            {
                informationListener.add(listener);
            }
            else
            {
                return;
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private ApplejuiceFassade() {
        logger = Logger.getLogger(getClass());
        try
        {
            downloadListener = new HashSet();
            searchListener = new HashSet();
            serverListener = new HashSet();
            uploadListener = new HashSet();
            shareListener = new HashSet();
            networkInfoListener = new HashSet();
            speedListener = new HashSet();
            informationListener = new HashSet();

            //load XMLs
            modifiedXML = new ModifiedXMLHolder();
            informationXML = new InformationXMLHolder();
            directoryXML = new DirectoryXMLHolder();
            informationXML.reload("");
            shareXML = new ShareXMLHolder();

            String versionsTag = informationXML.getFirstAttrbuteByTagName(new String[]{
                "applejuice", "generalinformation", "version"} , true);
            separator = informationXML.getFirstAttrbuteByTagName(new String[]{
                "applejuice", "generalinformation", "filesystem", "seperator"} , false);
            coreVersion = new Version(versionsTag, Version.getOSTypByOSName((String) System.getProperties().get("os.name")));
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void startXMLCheck() {
        workerThread = new Thread() {
                    public void run() {
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("MainWorkerThread gestartet. " + workerThread);
                        try{
                            SessionXMLHolder session = new SessionXMLHolder();
                            session.reload("");
                            String sessionId = session.getFirstAttrbuteByTagName(new String[]{
                                "applejuice", "session", "id" }, false);
                            long time = System.currentTimeMillis();
                            if (logger.isEnabledFor(Level.DEBUG))
                                logger.debug("SessionID = " + sessionId);
                            while (!isInterrupted()){
                                if (System.currentTimeMillis() > time + 20000){
                                    session.reload("");
                                    sessionId = session.getFirstAttrbuteByTagName(new String[]{
                                        "applejuice", "session", "id" }, false);
                                    if (logger.isEnabledFor(Level.DEBUG))
                                        logger.debug("SessionID = " + sessionId);
                                }
                                time = System.currentTimeMillis();
                                updateModifiedXML(sessionId);
                                try{
                                    sleep(2000);
                                }
                                catch (InterruptedException e)
                                {
                                    interrupt();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            if (logger.isEnabledFor(Level.ERROR))
                                logger.error("Unbehandelte Exception", e);
                        }
                        if (logger.isEnabledFor(Level.DEBUG))
                            logger.debug("MainWorkerThread beendet. " + workerThread);
                    }
                };
        workerThread.start();
    }

    public void stopXMLCheck() {
        try{
            if (workerThread!=null){
                workerThread.interrupt();
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public Information getInformation(){
        return modifiedXML.getInformation();
    }

    public PartListDO getPartList(DownloadDO downloadDO) {
        PartListHolder partlistXML = new DownloadPartListXMLHolder(downloadDO);
        return partlistXML.getPartList();
    }

    public PartListDO getPartList(DownloadSourceDO downloadSourceDO) {
        PartListHolder partlistXML = new UserPartListXMLHolder(downloadSourceDO);
        return partlistXML.getPartList();
    }

    public AJSettings getAJSettings() {
        try{
            if (settingsXML == null)
            {
                settingsXML = new SettingsXMLHolder();
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
        return settingsXML.getAJSettings();
    }

    public void saveAJSettings(AJSettings ajSettings) {
        try{
            String parameters = "";
            try
            {
                parameters = "Nickname=" +
                        URLEncoder.encode(ajSettings.getNick(), "UTF-8");
                parameters += "&XMLPort=" + Long.toString(ajSettings.getXMLPort());
                parameters += "&Port=" + Long.toString(ajSettings.getPort());
                parameters += "&MaxUpload=" + Long.toString(ajSettings.getMaxUpload());
                parameters += "&MaxDownload=" + Long.toString(ajSettings.getMaxDownload());
                parameters += "&Speedperslot=" +
                        Integer.toString(ajSettings.getSpeedPerSlot());
                parameters += "&Incomingdirectory=" +
                        URLEncoder.encode(ajSettings.getIncomingDir(), "UTF-8");
                parameters += "&Temporarydirectory=" +
                        URLEncoder.encode(ajSettings.getTempDir(), "UTF-8");
                parameters += "&maxconnections=" +
                        URLEncoder.encode(Long.toString(ajSettings.getMaxConnections()), "UTF-8");
                parameters += "&autoconnect=" +
                        URLEncoder.encode(Boolean.toString(ajSettings.isAutoConnect()), "UTF-8");
                parameters += "&maxnewconnectionsperturn=" +
                        URLEncoder.encode(Long.toString(ajSettings.getMaxNewConnectionsPerTurn()), "UTF-8");
            }
            catch (UnsupportedEncodingException ex1)
            {
                if (logger.isEnabledFor(Level.ERROR))
                    logger.error("Unbehandelte Exception", ex1);
            }
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setsettings?password=" + password + "&" + parameters, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public HashMap getAllServer() {
        return modifiedXML.getServer();
    }

    public synchronized void updateModifiedXML(String sessionId) {
        try
        {
            modifiedXML.update(sessionId);
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    informDataUpdateListener(DataUpdateListener.SERVER_CHANGED);
                    informDataUpdateListener(DataUpdateListener.DOWNLOAD_CHANGED);
                    informDataUpdateListener(DataUpdateListener.UPLOAD_CHANGED);
                    informDataUpdateListener(DataUpdateListener.NETINFO_CHANGED);
                    informDataUpdateListener(DataUpdateListener.SPEED_CHANGED);
                    informDataUpdateListener(DataUpdateListener.SEARCH_CHANGED);
                    informDataUpdateListener(DataUpdateListener.INFORMATION_CHANGED);
                }
            });
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void resumeDownload(int[] id) {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            String parameters = "&id=" + id[0];
            if (id.length>1){
                for (int i=1; i<id.length; i++){
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/resumedownload?password=" + password + parameters, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void startSearch(String searchString) {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/search?password=" + password + "&search=" + searchString, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public static void setPassword(String passwordAsMD5) {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setpassword?password=" + password + "&newpassword="
                                                  + passwordAsMD5, false);
        }
        catch (WebSiteNotFoundException ex)
        {
        }
    }

    public void cancelDownload(int[] id) {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            String parameters = "&id=" + id[0];
            if (id.length>1){
                for (int i=1; i<id.length; i++){
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/canceldownload?password=" + password + parameters, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void cleanDownloadList() {
        logger.info("Clear list...");
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/cleandownloadlist?password=" + password, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void pauseDownload(int[] id) {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            String parameters = "&id=" + id[0];
            if (id.length>1){
                for (int i=1; i<id.length; i++){
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/pausedownload?password=" + password + parameters, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void connectToServer(int id) {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/serverlogin?password=" + password + "&id=" + id, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void entferneServer(int id) {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/removeserver?password=" + password + "&id=" + id, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void setPrioritaet(int id, int prioritaet) {
        try{
            if (prioritaet < 1 || prioritaet > 250)
            {
                System.out.print("Warnung: Prioritaet muss 1<= x <=250 sein!");
                return;
            }
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setpriority?password=" + password + "&id=" + id +
                                                  "&priority=" + prioritaet, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void processLink(String link) {
        try{
            if (link == null || link.length() == 0)
            {
                System.out.print("Warnung: Ungueltiger Link uebergeben!");
                return;
            }
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/processlink?password=" + password + "&link=" + link, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void setPowerDownload(int[] id, int powerDownload) {
        try{
            if (powerDownload < 0 || powerDownload > 490)
            {
                System.out.print("Warnung: PowerDownload muss 0<= x <=490 sein!");
                return;
            }
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            String parameters = "&powerdownload=" + powerDownload + "&id=" + id[0];
            if (id.length>1){
                for (int i=1; i<id.length; i++){
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/setpowerdownload?password=" + password + parameters, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    private static String getHost() {
        String savedHost = PropertiesManager.getOptionsManager().getRemoteSettings().getHost();
        if (savedHost.length() == 0)
        {
            savedHost = "localhost";
        }
        return savedHost;
    }

    public static boolean istCoreErreichbar() {
        try
        {
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                           "/xml/information.xml?password=" + password);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public static synchronized ApplejuiceFassade getInstance() {
        if (instance==null){
            instance = new ApplejuiceFassade();
        }
        return instance;
    }

    public Version getCoreVersion() {
        return coreVersion;
    }

    private void informDataUpdateListener(int type) {
        try{
            switch (type)
            {
                case DataUpdateListener.DOWNLOAD_CHANGED:
                    {
                        HashMap content = modifiedXML.getDownloads();
                        if (content.size() == 0)
                        {
                            return;
                        }
                        Iterator it = downloadListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
                                                                                DOWNLOAD_CHANGED, content);
                        }
                        break;
                    }
                case DataUpdateListener.UPLOAD_CHANGED:
                    {
                        HashMap content = modifiedXML.getUploads();
                        if (content.size() == 0)
                        {
                            return;
                        }
                        Iterator it = uploadListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
                                                                                UPLOAD_CHANGED, content);
                        }
                        break;
                    }
                case DataUpdateListener.SERVER_CHANGED:
                    {
                        HashMap content = modifiedXML.getServer();
                        if (content.size() == 0)
                        {
                            return;
                        }
                        Iterator it = serverListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
                                                                                SERVER_CHANGED, content);
                        }
                        break;
                    }
                case DataUpdateListener.SHARE_CHANGED:
                    {
                        HashMap content = shareXML.getShare();
                        if (content.size() == 0)
                        {
                            return;
                        }
                        Iterator it = shareListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
                                                                                SHARE_CHANGED, content);
                        }
                        break;
                    }
                case DataUpdateListener.NETINFO_CHANGED:
                    {
                        NetworkInfo content = modifiedXML.getNetworkInfo();
                        Iterator it = networkInfoListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
                                                                                NETINFO_CHANGED, content);
                        }
                        break;
                    }
                case DataUpdateListener.SPEED_CHANGED:
                    {
                        HashMap content = modifiedXML.getSpeeds();
                        Iterator it = speedListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.SPEED_CHANGED, content);
                        }
                        break;
                    }
                case DataUpdateListener.SEARCH_CHANGED:
                    {
                        HashMap content = modifiedXML.getSearchs();
                        Iterator it = searchListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.SEARCH_CHANGED, content);
                        }
                        break;
                    }
                case DataUpdateListener.INFORMATION_CHANGED:
                    {
                        Information content = modifiedXML.getInformation();
                        Iterator it = informationListener.iterator();
                        while (it.hasNext())
                        {
                            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.INFORMATION_CHANGED, content);
                        }
                        break;
                    }
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public HashMap getShare(boolean reinit) {
        try{
            if (share == null || reinit)
                share = shareXML.getShare();
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
        return share;
    }

    public void setShare(HashSet newShare){
        try{
            int shareSize = newShare.size();
            if (newShare==null)
                return;
            String parameters = "countshares=" + shareSize;
            ShareEntry shareEntry = null;
            Iterator it = newShare.iterator();
            int i = 1;
            while (it.hasNext()){
                shareEntry = (ShareEntry)it.next();
                try {
                    parameters += "&sharedirectory" + i + "=" + URLEncoder.encode(shareEntry.getDir(), "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    logger.error("Unbehandelte Exception", e);
                }
                parameters += "&sharesub" + i + "=" +
                        (shareEntry.getShareMode()==ShareEntry.SUBDIRECTORY ? "true" : "false");
                i++;
            }
            String password = PropertiesManager.getOptionsManager().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setsettings?password=" + password + "&" + parameters, false);
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void getDirectory(String directory, ApplejuiceNode directoryNode) {
        directoryXML.getDirectory(directory, directoryNode);
    }
}