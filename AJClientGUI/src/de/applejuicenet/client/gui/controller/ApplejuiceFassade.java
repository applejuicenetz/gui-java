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
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/ApplejuiceFassade.java,v 1.17 2003/08/29 19:34:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ApplejuiceFassade.java,v $
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
 * Version erh�ht.
 *
 * Revision 1.37  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingef�gt.
 * Diverse �nderungen.
 *
 * Revision 1.36  2003/08/10 21:08:18  maj0r
 * Diverse �nderungen.
 *
 * Revision 1.35  2003/08/09 16:47:42  maj0r
 * Diverse �nderungen.
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
 * DownloadTable �berarbeitet.
 *
 * Revision 1.26  2003/07/01 15:00:00  maj0r
 * Keyverwendung bei HashSets und HashMaps korrigiert.
 *
 * Revision 1.25  2003/07/01 06:17:16  maj0r
 * Code optimiert.
 *
 * Revision 1.24  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingef�gt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.23  2003/06/24 12:06:49  maj0r
 * log4j eingef�gt (inkl. Bedienung �ber Einstellungsdialog).
 *
 * Revision 1.22  2003/06/22 20:34:25  maj0r
 * Konsolenausgaben hinzugef�gt.
 *
 * Revision 1.21  2003/06/13 15:07:30  maj0r
 * Versionsanzeige hinzugef�gt.
 * Da der Controllerteil refactort werden kann, haben Controller und GUI separate Versionsnummern.
 *
 * Revision 1.20  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ApplejuiceFassade { //Singleton-Implementierung
    public static final String GUI_VERSION = "0.17 Beta";

    private HashSet downloadListener;
    private HashSet shareListener;
    private HashSet uploadListener;
    private HashSet serverListener;
    private HashSet networkInfoListener;
    private HashSet statusbarListener;
    private static ApplejuiceFassade instance = null;
    private ModifiedXMLHolder modifiedXML = null;
    private InformationXMLHolder informationXML = null;
    private ShareXMLHolder shareXML = null;
    private SettingsXMLHolder settingsXML = null;
    private DirectoryXMLHolder directoryXML = null;
    private Version coreVersion;
    private HashMap share = null;

    //Thread
    private boolean runThread;
    private SwingWorker workerThread;

    private static int checkInProgress = 0;

    private Logger logger;

    public void addDataUpdateListener(DataUpdateListener listener, int type) {
        HashSet listenerSet = null;
        if (type == DataUpdateListener.DOWNLOAD_CHANGED)
        {
            listenerSet = downloadListener;
        }
        else if (type == DataUpdateListener.NETINFO_CHANGED)
        {
            listenerSet = networkInfoListener;
        }
        else if (type == DataUpdateListener.SERVER_CHANGED)
        {
            listenerSet = serverListener;
        }
        else if (type == DataUpdateListener.SHARE_CHANGED)
        {
            listenerSet = shareListener;
        }
        else if (type == DataUpdateListener.UPLOAD_CHANGED)
        {
            listenerSet = uploadListener;
        }
        else if (type == DataUpdateListener.STATUSBAR_CHANGED)
        {
            listenerSet = statusbarListener;
        }
        else
        {
            return;
        }
        if (!(listenerSet.contains(listener)))
        {
            listenerSet.add(listener);
        }
    }

    private ApplejuiceFassade() {
        logger = Logger.getLogger(getClass());
        try
        {
            downloadListener = new HashSet();
            serverListener = new HashSet();
            uploadListener = new HashSet();
            shareListener = new HashSet();
            networkInfoListener = new HashSet();
            statusbarListener = new HashSet();

            //load XMLs
            modifiedXML = new ModifiedXMLHolder();
            informationXML = new InformationXMLHolder();
            directoryXML = new DirectoryXMLHolder();
            informationXML.reload("");
            shareXML = new ShareXMLHolder();

            String versionsTag = informationXML.getFirstAttrbuteByTagName(new String[]{
                "applejuice", "generalinformation", "version"}
                                                                          , true);
            coreVersion = new Version(versionsTag, Version.getOSTypByOSName((String) System.getProperties().get("os.name")));
        }
        catch (Exception e)
        {
            if (logger.isEnabledFor(Level.FATAL))
                logger.fatal("Unbehandelte Exception", e);
        }
    }

    public void startXMLCheck() {
        runThread = true;
        final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        while (runThread){
                            updateModifiedXML();
                        }
                        return null;
                    }
                    public void finished() {
                        runThread = false;
                    }
                };
        workerThread = worker;
        worker.start();
        if (logger.isEnabledFor(Level.INFO))
            logger.info("WorkerThread gestartet...");
    }

    public void stopXMLCheck() {
        workerThread.interrupt();
        if (logger.isEnabledFor(Level.INFO))
            logger.info("WorkerThread beendet...");
    }

    public PartListDO getDownloadPartList(DownloadDO downloadDO) {
        DownloadPartListXMLHolder partlistXML = new DownloadPartListXMLHolder(downloadDO);
        return partlistXML.getPartList();
    }

    public AJSettings getAJSettings() {
        if (settingsXML == null)
        {
            settingsXML = new SettingsXMLHolder();
        }
        return settingsXML.getAJSettings();
    }

    public boolean saveAJSettings(AJSettings ajSettings) {
        String parameters = "";
        try
        {
            parameters = "Nickname=" +
                    URLEncoder.encode(ajSettings.getNick(), "UTF-8");
            parameters += "&XMLPort=" + Long.toString(ajSettings.getXMLPort());
            parameters += "&AllowBrowse=" +
                    (ajSettings.isBrowseAllowed() ? "true" : "false");
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
                    URLEncoder.encode(new Boolean(ajSettings.isAutoConnect()).toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException ex1)
        {
        }
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setsettings?password=" + password + "&" + parameters, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public HashMap getAllServer() {
        return modifiedXML.getServer();
    }

    public synchronized void updateModifiedXML() {
        try
        {
            if (checkInProgress != 0)
                return;
            checkInProgress++;
            modifiedXML.update();
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    informDataUpdateListener(DataUpdateListener.SERVER_CHANGED);
                    informDataUpdateListener(DataUpdateListener.DOWNLOAD_CHANGED);
                    informDataUpdateListener(DataUpdateListener.UPLOAD_CHANGED);
                    informDataUpdateListener(DataUpdateListener.NETINFO_CHANGED);
                    informDataUpdateListener(DataUpdateListener.STATUSBAR_CHANGED);
                }
            });
            checkInProgress--;
            wait(2000);
        }
        catch (InterruptedException e)
        {}
    }

    public boolean resumeDownload(String id) {
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/resumedownload?password=" + password + "&id=" + id, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public static boolean setPassword(String passwordAsMD5) {
        String result;
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            result = HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setpassword?password=" + password + "&newpassword=" + passwordAsMD5, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean cancelDownload(String id) {
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/canceldownload?password=" + password + "&id=" + id, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean cleanDownloadList() {
        logger.info("Clear list...");
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/cleandownloadlist?password=" + password, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean pauseDownload(String id) {
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/pausedownload?password=" + password + "&id=" + id, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean connectToServer(int id) {
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/serverlogin?password=" + password + "&id=" + id, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean entferneServer(int id) {
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                                  "/function/removeserver?password=" + password + "&id=" + id, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean setPrioritaet(int id, int prioritaet) {
        if (prioritaet < 1 || prioritaet > 250)
        {
            System.out.print("Warnung: Prioritaet muss 1<= x <=250 sein!");
            return false;
        }
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setpriority?password=" + password + "&id=" + id + "&priority=" + prioritaet, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean processLink(String link) {
        if (link == null || link.length() == 0)
        {
            System.out.print("Warnung: Ungueltiger Link uebergeben!");
            return false;
        }
        logger.info("Downloade '" + link + "...");
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/processlink?password=" + password + "&link=" + link, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public boolean setPowerDownload(int id, int powerDownload) {
        if (powerDownload < 0 || powerDownload > 490)
        {
            System.out.print("Warnung: PowerDownload muss 0<= x <=490 sein!");
            return false;
        }
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setpowerdownload?password=" + password + "&id=" + id + "&powerdownload=" + powerDownload
                                         , false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    private static String getHost() {
        String savedHost = OptionsManager.getInstance().getRemoteSettings().getHost();
        if (savedHost.length() == 0)
        {
            savedHost = "localhost";
        }
        return savedHost;
    }

    public static boolean istCoreErreichbar() {
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                           "/xml/information.xml?password=" + password);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public static ApplejuiceFassade getInstance() {
        if (instance == null)
        {
            instance = new ApplejuiceFassade();
        }
        return instance;
    }

    public Version getCoreVersion() {
        return coreVersion;
    }

    private void informDataUpdateListener(int type) {
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
            case DataUpdateListener.STATUSBAR_CHANGED:
                {
                    String[] content = modifiedXML.getStatusBar();
                    Iterator it = statusbarListener.iterator();
                    while (it.hasNext())
                    {
                        ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.STATUSBAR_CHANGED, content);
                    }
                    break;
                }
            default:
                break;
        }
    }

    public HashMap getShare(boolean reinit) {
        if (share == null || reinit)
            share = shareXML.getShare();
        return share;
    }

    public boolean setShare(HashSet newShare){
        int shareSize = newShare.size();
        if (newShare==null)
            return false;
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
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
            parameters += "&sharesub" + i + "=" +
                    (shareEntry.getShareMode()==ShareEntry.SUBDIRECTORY ? "true" : "false");
            i++;
        }
        try
        {
            String password = OptionsManager.getInstance().getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                  "/function/setsettings?password=" + password + "&" + parameters, false);
        }
        catch (WebSiteNotFoundException ex)
        {
            return false;
        }
        return true;
    }

    public static boolean isCheckInProgress() {
        return checkInProgress != 0;
    }

    public void getDirectory(String directory, ApplejuiceNode directoryNode) {
        checkInProgress++;
        directoryXML.getDirectory(directory, directoryNode);
        checkInProgress--;
    }
}