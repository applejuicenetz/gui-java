package de.applejuicenet.client.gui.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.xmlholder.DirectoryXMLHolder;
import de.applejuicenet.client.gui.controller.xmlholder.GetObjectXMLHolder;
import de.applejuicenet.client.gui.controller.xmlholder.InformationXMLHolder;
import de.applejuicenet.client.gui.controller.xmlholder.ModifiedXMLHolder;
import de.applejuicenet.client.gui.controller.xmlholder.NetworkServerXMLHolder;
import de.applejuicenet.client.gui.controller.xmlholder.PartListXMLHolder;
import de.applejuicenet.client.gui.controller.xmlholder.SettingsXMLHolder;
import de.applejuicenet.client.gui.controller.xmlholder.ShareXMLHolder;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.trees.ApplejuiceNode;
import de.applejuicenet.client.shared.AJSettings;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.ShareEntry;
import de.applejuicenet.client.shared.Version;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.PartListDO;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/ApplejuiceFassade.java,v 1.126 2004/03/03 15:33:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ApplejuiceFassade {
    public static final String GUI_VERSION = "0.56.1";
    public static final String MIN_NEEDED_CORE_VERSION = "0.29.135.208";

    private Set downloadListener;
    private Set searchListener;
    private Set shareListener;
    private Set uploadListener;
    private Set serverListener;
    private Set networkInfoListener;
    private Set speedListener;
    private Set informationListener;
    public static String separator;
    private ModifiedXMLHolder modifiedXML = null;
    private InformationXMLHolder informationXML = null;
    private ShareXMLHolder shareXML = null;
    private SettingsXMLHolder settingsXML = null;
    private DirectoryXMLHolder directoryXML = null;
    private Version coreVersion;
    private Map share = null;
    private PartListXMLHolder partlistXML = null;

    private static ApplejuiceFassade instance = null;

    //Thread
    private Thread workerThread;

    private Logger logger;

    public static synchronized ApplejuiceFassade getInstance() {
        if (instance == null) {
            instance = new ApplejuiceFassade();
        }
        return instance;
    }

    private ApplejuiceFassade() {
        logger = Logger.getLogger(getClass());
        try {
            downloadListener = new HashSet();
            searchListener = new HashSet();
            serverListener = new HashSet();
            uploadListener = new HashSet();
            shareListener = new HashSet();
            networkInfoListener = new HashSet();
            speedListener = new HashSet();
            informationListener = new HashSet();

            //load XMLs
            modifiedXML = ModifiedXMLHolder.getInstance();
            informationXML = InformationXMLHolder.getInstance();
            directoryXML = new DirectoryXMLHolder();
            shareXML = new ShareXMLHolder();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void addDataUpdateListener(DataUpdateListener listener, int type) {
        try {
            if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
                downloadListener.add(listener);
            }
            else if (type == DataUpdateListener.NETINFO_CHANGED) {
                networkInfoListener.add(listener);
            }
            else if (type == DataUpdateListener.SERVER_CHANGED) {
                serverListener.add(listener);
            }
            else if (type == DataUpdateListener.SHARE_CHANGED) {
                shareListener.add(listener);
            }
            else if (type == DataUpdateListener.UPLOAD_CHANGED) {
                uploadListener.add(listener);
            }
            else if (type == DataUpdateListener.SPEED_CHANGED) {
                speedListener.add(listener);
            }
            else if (type == DataUpdateListener.SEARCH_CHANGED) {
                searchListener.add(listener);
            }
            else if (type == DataUpdateListener.INFORMATION_CHANGED) {
                informationListener.add(listener);
            }
            else {
                return;
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void checkForValidCore(){
        if (getCoreVersion() == null){
            return;
        }
        StringTokenizer token1 = new StringTokenizer(
            getCoreVersion().getVersion(), ".");
        StringTokenizer token2 = new StringTokenizer(
            ApplejuiceFassade.MIN_NEEDED_CORE_VERSION, ".");
        if (token1.countTokens() != 4 ||
            token2.countTokens() != 4) {
            return;
        }
        String[] foundCore = new String[4];
        String[] neededCore = new String[4];
        for (int i = 0; i < 4; i++) {
            foundCore[i] = token1.nextToken();
            neededCore[i] = token2.nextToken();
        }
        boolean coreTooOld = false;
        for (int i = 0; i < 4; i++) {
            if (Integer.parseInt(foundCore[i]) <
                Integer.parseInt(neededCore[i])) {
                coreTooOld = true;
                break;
            }
        }
        if (coreTooOld) {
            StringBuffer fehlerText = new StringBuffer(ZeichenErsetzer.korrigiereUmlaute(
                LanguageSelector.getInstance().getFirstAttrbuteByTagName(".root.javagui.startup.corezualt")));
            int pos = fehlerText.indexOf("%s");
            if (pos !=-1){
                fehlerText.replace(pos, pos + 2, MIN_NEEDED_CORE_VERSION);
            }
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info(fehlerText.toString());
            }
            AppleJuiceDialog.closeWithErrormessage(fehlerText.toString(), false);
        }
    }

    public void startXMLCheck() {
        workerThread = new Thread() {
            public void run() {
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("MainWorkerThread gestartet. " + workerThread);
                }
                try {
                    int versuch = 0;
                    if (coreVersion == null){
                        coreVersion = informationXML.getCoreVersion();
                        checkForValidCore();
                    }
                    while (!isInterrupted()) {
                        if (updateModifiedXML()) {
                            versuch = 0;
                        }
                        else {
                            versuch++;
                            if (versuch == 3) {
                                if (logger.isEnabledFor(Level.INFO)) {
                                    logger.info("Die Verbindung zum Core ist abgebrochen.\r\nDas GUI wird beendet.");
                                }
                                AppleJuiceDialog.closeWithErrormessage(
                                    "Die Verbindung zum Core ist abgebrochen.\r\nDas GUI wird beendet.", true);
                            }
                        }
                        try {
                            sleep(2000);
                        }
                        catch (InterruptedException e) {
                            interrupt();
                        }
                    }
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error("Unbehandelte Exception", e);
                    }
                }
                if (logger.isEnabledFor(Level.DEBUG)) {
                    logger.debug("MainWorkerThread beendet. " + workerThread);
                }
            }
        };
        workerThread.start();
    }

    public void stopXMLCheck() {
        try {
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public String[] getCurrentIncomingDirs() {
        Map download = getDownloadsSnapshot();
        DownloadDO downloadDO = null;
        ArrayList incomingDirs = new ArrayList();
        boolean found;
        synchronized (download) {
            Iterator it = download.values().iterator();
            while (it.hasNext()) {
                downloadDO = (DownloadDO) it.next();
                if (downloadDO.getTargetDirectory().length() == 0) {
                    continue;
                }
                found = false;
                for (int i = 0; i < incomingDirs.size(); i++) {
                    if ( ( (String) incomingDirs.get(i)).compareToIgnoreCase(
                        downloadDO.getTargetDirectory()) == 0) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    incomingDirs.add(downloadDO.getTargetDirectory());
                }
            }
        }
        incomingDirs.add("");
        return (String[]) incomingDirs.toArray(new String[incomingDirs.size()]);
    }

    public Information getInformation() {
        return modifiedXML.getInformation();
    }

    public PartListDO getPartList(Object object) {
        if (partlistXML == null){
            partlistXML = PartListXMLHolder.getInstance();
        }
        return partlistXML.getPartList(object);
    }

    public String[] getNetworkKnownServers() {
        NetworkServerXMLHolder getServerXMLHolder = NetworkServerXMLHolder.
            getInstance();
        return getServerXMLHolder.getNetworkKnownServers();
    }

    public AJSettings getAJSettings() {
        try {
            if (settingsXML == null) {
                settingsXML = new SettingsXMLHolder();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        return settingsXML.getAJSettings();
    }

    public AJSettings getCurrentAJSettings() {
        try {
            if (settingsXML == null) {
                return getAJSettings();
            }
            else {
                return settingsXML.getCurrentAJSettings();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return null;
        }
    }

    public synchronized void setMaxUpAndDown(final long maxUp,
                                             final long maxDown) {
        new Thread() {
            public void run() {
                try {
                    String parameters = "";
                    parameters += "MaxUpload=" +
                        Long.toString(maxUp);
                    parameters += "&MaxDownload=" +
                        Long.toString(maxDown);
                    String password = PropertiesManager.getOptionsManager().
                        getRemoteSettings().getOldPassword();
                    HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                                 "/function/setsettings?password=" +
                                                 password + "&" + parameters, false);
                }
                catch (Exception e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error("Unbehandelte Exception", e);
                    }
                }
            }
        }

        .start();
    }

    public void saveAJSettings(AJSettings ajSettings) {
        try {
            String parameters = "";
            try {
                parameters = "Nickname=" +
                    URLEncoder.encode(ajSettings.getNick(), "UTF-8");
                parameters += "&XMLPort=" + Long.toString(ajSettings.getXMLPort());
                parameters += "&Port=" + Long.toString(ajSettings.getPort());
                parameters += "&MaxUpload=" +
                    Long.toString(ajSettings.getMaxUpload());
                parameters += "&MaxDownload=" +
                    Long.toString(ajSettings.getMaxDownload());
                parameters += "&Speedperslot=" +
                    Integer.toString(ajSettings.getSpeedPerSlot());
                parameters += "&Incomingdirectory=" +
                    URLEncoder.encode(ajSettings.getIncomingDir(), "UTF-8");
                parameters += "&Temporarydirectory=" +
                    URLEncoder.encode(ajSettings.getTempDir(), "UTF-8");
                parameters += "&maxconnections=" +
                    URLEncoder.encode(Long.toString(ajSettings.
                    getMaxConnections()), "UTF-8");
                parameters += "&maxsourcesperfile=" +
                    URLEncoder.encode(Long.toString(ajSettings.
                    getMaxSourcesPerFile()), "UTF-8");
                parameters += "&autoconnect=" +
                    URLEncoder.encode(Boolean.toString(ajSettings.isAutoConnect()),
                                      "UTF-8");
                parameters += "&maxnewconnectionsperturn=" +
                    URLEncoder.encode(Long.toString(ajSettings.
                    getMaxNewConnectionsPerTurn()), "UTF-8");
            }
            catch (UnsupportedEncodingException ex1) {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Unbehandelte Exception", ex1);
                }
            }
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                         "/function/setsettings?password=" +
                                         password + "&" + parameters, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public Map getAllServer() {
        return modifiedXML.getServer();
    }

    public synchronized boolean updateModifiedXML() {
        try {
            if (modifiedXML.update()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        informDataUpdateListener(DataUpdateListener.
                                                 SERVER_CHANGED);
                        informDataUpdateListener(DataUpdateListener.
                                                 DOWNLOAD_CHANGED);
                        informDataUpdateListener(DataUpdateListener.
                                                 UPLOAD_CHANGED);
                        informDataUpdateListener(DataUpdateListener.
                                                 NETINFO_CHANGED);
                        informDataUpdateListener(DataUpdateListener.
                                                 SPEED_CHANGED);
                        informDataUpdateListener(DataUpdateListener.
                                                 SEARCH_CHANGED);
                        informDataUpdateListener(DataUpdateListener.
                                                 INFORMATION_CHANGED);
                    }
                });
            }
            return true;
        }
        catch (RuntimeException re) {
            // Verbindung zum Core verloren
            // neuer Versuch
            return false;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return false;
        }
    }

    public void resumeDownload(int[] id) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            String parameters = "&id=" + id[0];
            if (id.length > 1) {
                for (int i = 1; i < id.length; i++) {
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/resumedownload?password=" +
                                         password + parameters, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void startSearch(String searchString) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/search?password=" +
                                         password + "&search=" + searchString, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public synchronized void cancelSearch(Search search) {
        new CancelThread(search).start();
    }

    private class CancelThread extends Thread{
        private Search search;
        private boolean cancel = false;
        private Thread innerThread;

        public CancelThread(Search search){
            this.search = search;
        }

        public void run() {
            try{
                if (search.getCreationTime() >
                    System.currentTimeMillis() - 10000) {
                    sleep(10000);
                }
                while(!cancel){
                    innerThread = new Thread() {
                        public void run() {
                            try {
                                String password = PropertiesManager.
                                    getOptionsManager().
                                    getRemoteSettings().getOldPassword();
                                HtmlLoader.getHtmlXMLContent(getHost(),
                                    HtmlLoader.POST,
                                    "/function/cancelsearch?password=" +
                                    password + "&id=" + search.getId(), true);
                                cancel = true;
                            }
                            catch (Exception e) {
                                if (logger.isEnabledFor(Level.ERROR)) {
                                    logger.error("Unbehandelte Exception", e);
                                }
                            }

                        }
                    };
                    innerThread.start();
                    sleep(4000);
                    innerThread.interrupt();
                }
            }
            catch(InterruptedException iE){
                innerThread.interrupt();
                interrupt();
            }
        }
    }

    public void renameDownload(int downloadId, String neuerName) {
        try {
            String encodedName = neuerName;
            try {
                StringBuffer tempLink = new StringBuffer(encodedName);
                for (int i = 0; i < tempLink.length(); i++) {
                    if (tempLink.charAt(i) == ' ') {
                        tempLink.setCharAt(i, '.');
                    }
                }
                encodedName = URLEncoder.encode(tempLink.toString(),
                                                "ISO-8859-1");
            }
            catch (UnsupportedEncodingException ex) {
                ;
                //gibbet, also nix zu behandeln...
            }
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/renamedownload?password=" +
                                         password + "&id=" + downloadId
                                         + "&name=" + encodedName, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void setTargetDir(int downloadId, String verzeichnisName) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/settargetdir?password=" +
                                         password + "&id=" + downloadId
                                         + "&dir=" + verzeichnisName, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void exitCore() {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/exitcore?password=" +
                                         password, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public static void setPassword(String passwordAsMD5) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                         "/function/setpassword?password=" +
                                         password + "&newpassword="
                                         + passwordAsMD5, false);
        }
        catch (WebSiteNotFoundException ex) {
            ;
        }
    }

    public void cancelDownload(int[] id) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            String parameters = "&id=" + id[0];
            if (id.length > 1) {
                for (int i = 1; i < id.length; i++) {
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/canceldownload?password=" +
                                         password + parameters, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void cleanDownloadList() {
        logger.info("Clear list...");
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/cleandownloadlist?password=" +
                                         password, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void pauseDownload(int[] id) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            String parameters = "&id=" + id[0];
            if (id.length > 1) {
                for (int i = 1; i < id.length; i++) {
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/pausedownload?password=" +
                                         password + parameters, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void connectToServer(int id) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/serverlogin?password=" +
                                         password + "&id=" + id, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public Object getObjectById(int id) {
        GetObjectXMLHolder getObjectXMLHolder = new GetObjectXMLHolder();
        return getObjectXMLHolder.getObjectByID(id);
    }

    public void entferneServer(int id) {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/removeserver?password=" +
                                         password + "&id=" + id, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void setPrioritaet(int id, int prioritaet) {
        try {
            if (prioritaet < 1 || prioritaet > 250) {
                System.out.print("Warnung: Prioritaet muss 1<= x <=250 sein!");
                return;
            }
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                         "/function/setpriority?password=" +
                                         password + "&id=" + id +
                                         "&priority=" + prioritaet, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public synchronized void processLink(final String link) {
        try {
            if (link == null || link.length() == 0) {
                if (logger.isEnabledFor(Level.INFO)) {
                    logger.info("Ungueltiger Link uebergeben: " + link);
                }
                return;
            }
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            String encodedLink = link;
            try {
                StringBuffer tempLink = new StringBuffer(link);
                for (int i = 0; i < tempLink.length(); i++) {
                    if (tempLink.charAt(i) == ' ') {
                        tempLink.setCharAt(i, '.');
                    }
                }
                encodedLink = URLEncoder.encode(tempLink.toString(),
                    "ISO-8859-1");
            }
            catch (UnsupportedEncodingException ex) {
                ;
                //gibbet nicht, also nix zu behandeln...
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                         "/function/processlink?password=" +
                                         password + "&link=" + encodedLink, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void setPowerDownload(int[] id, int powerDownload) {
        try {
            if (powerDownload < 0 || powerDownload > 490) {
                System.out.print(
                    "Warnung: PowerDownload muss 0<= x <=490 sein!");
                return;
            }
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            String parameters = "&powerdownload=" + powerDownload + "&id=" +
                id[0];
            if (id.length > 1) {
                for (int i = 1; i < id.length; i++) {
                    parameters += "&id" + i + "=" + id[i];
                }
            }
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.POST,
                                         "/function/setpowerdownload?password=" +
                                         password + parameters, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private static String getHost() {
        String savedHost = PropertiesManager.getOptionsManager().
            getRemoteSettings().getHost();
        if (savedHost.length() == 0) {
            savedHost = "localhost";
        }
        return savedHost;
    }

    public static boolean istCoreErreichbar() {
        try {
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            String result = HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                         "/xml/information.xml?password=" +
                                         password);
            if (result.compareTo("wrong password") == 0){
                return false;
            }
        }
        catch (WebSiteNotFoundException ex) {
            return false;
        }
        return true;
    }

    public Version getCoreVersion() {
        return coreVersion;
    }

    public Map getDownloadsSnapshot() {
        return modifiedXML.getDownloads();
    }

    public void informDataUpdateListener(int type) {
        try {
            switch (type) {
                case DataUpdateListener.DOWNLOAD_CHANGED: {
                    Map content = modifiedXML.getDownloads();
                    Iterator it = downloadListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.
                            DOWNLOAD_CHANGED, content);
                    }
                    break;
                }
                case DataUpdateListener.UPLOAD_CHANGED: {
                    Map content = modifiedXML.getUploads();
                    Iterator it = uploadListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.
                            UPLOAD_CHANGED, content);
                    }
                    break;
                }
                case DataUpdateListener.SERVER_CHANGED: {
                    Map content = modifiedXML.getServer();
                    Iterator it = serverListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.
                            SERVER_CHANGED, content);
                    }
                    break;
                }
                case DataUpdateListener.SHARE_CHANGED: {
                    Map content = shareXML.getShare();
                    Iterator it = shareListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.
                            SHARE_CHANGED, content);
                    }
                    break;
                }
                case DataUpdateListener.NETINFO_CHANGED: {
                    NetworkInfo content = modifiedXML.getNetworkInfo();
                    Iterator it = networkInfoListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.
                            NETINFO_CHANGED, content);
                    }
                    break;
                }
                case DataUpdateListener.SPEED_CHANGED: {
                    Map content = modifiedXML.getSpeeds();
                    Iterator it = speedListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.SPEED_CHANGED, content);
                    }
                    break;
                }
                case DataUpdateListener.SEARCH_CHANGED: {
                    Map content = modifiedXML.getSearchs();
                    Iterator it = searchListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.SEARCH_CHANGED, content);
                    }
                    break;
                }
                case DataUpdateListener.INFORMATION_CHANGED: {
                    Information content = modifiedXML.getInformation();
                    Iterator it = informationListener.iterator();
                    while (it.hasNext()) {
                        ( (DataUpdateListener) it.next()).fireContentChanged(
                            DataUpdateListener.INFORMATION_CHANGED, content);
                    }
                    break;
                }
                default:
                    break;
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public Map getShare(boolean reinit) {
        try {
            if (share == null || reinit) {
                share = shareXML.getShare();
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        return share;
    }

    public void setShare(Set newShare) {
        try {
            int shareSize = newShare.size();
            if (newShare == null) {
                return;
            }
            String parameters = "countshares=" + shareSize;
            ShareEntry shareEntry = null;
            Iterator it = newShare.iterator();
            int i = 1;
            while (it.hasNext()) {
                shareEntry = (ShareEntry) it.next();
                try {
                    parameters += "&sharedirectory" + i + "=" +
                        URLEncoder.encode(shareEntry.getDir(), "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    logger.error("Unbehandelte Exception", e);
                }
                parameters += "&sharesub" + i + "=" +
                    (shareEntry.getShareMode() == ShareEntry.SUBDIRECTORY ?
                     "true" : "false");
                i++;
            }
            String password = PropertiesManager.getOptionsManager().
                getRemoteSettings().getOldPassword();
            HtmlLoader.getHtmlXMLContent(getHost(), HtmlLoader.GET,
                                         "/function/setsettings?password=" +
                                         password + "&" + parameters, false);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void getDirectory(String directory, ApplejuiceNode directoryNode) {
        directoryXML.getDirectory(directory, directoryNode);
    }
}
