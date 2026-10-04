/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.controller.DataPropertyChangeInformer;
import de.applejuicenet.client.fassade.controller.DataUpdateInformer;
import de.applejuicenet.client.fassade.controller.xml.*;
import de.applejuicenet.client.fassade.entity.*;
import de.applejuicenet.client.fassade.entity.ShareEntry.SHAREMODE;
import de.applejuicenet.client.fassade.exception.CoreLostException;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.exception.WrongPasswordException;
import de.applejuicenet.client.fassade.listener.CoreConnectionSettingsListener;
import de.applejuicenet.client.fassade.listener.CoreStatusListener;
import de.applejuicenet.client.fassade.listener.CoreStatusListener.STATUS;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
import de.applejuicenet.client.fassade.shared.StringConstants;
import de.applejuicenet.client.fassade.tools.MD5Encoder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class ApplejuiceFassade implements CoreConnectionSettingsListener {
    public static final String MIN_NEEDED_CORE_VERSION = "0.31.149.112";
    public static final String ERROR_MESSAGE = "Unbehandelte Exception";
    public static String separator;

    private static final int MAX_SUBDIR_DEPTH = 5;
    private static final String WEB_LINK_PREFIX = "web+ajfsp://";
    private static HashSet<CoreStatusListener> coreListener = new HashSet<CoreStatusListener>();
    private final CoreConnectionSettingsHolder coreHolder;
    private Map<DATALISTENER_TYPE, DataUpdateInformer> informer = new HashMap<DATALISTENER_TYPE, DataUpdateInformer>();
    private ModifiedXMLHolder modifiedXML;
    private InformationXMLHolder informationXML = null;
    private ShareXMLHolder shareXML = null;
    private SettingsXMLHolder settingsXML = null;
    private DirectoryXMLHolder directoryXML = null;
    private Version coreVersion;
    private Map<Integer, Share> share = null;
    private PartListXMLHolder partlistXML = null;
    private long sleepTime = 1000;

    // Thread
    private final Object workerLock = new Object();
    private Thread workerThread;
    private final Map<Integer, CancelThread> cancelWorkers = new HashMap<>();
    private static final System.Logger LOGGER = System.getLogger(ApplejuiceFassade.class.getName());

    public ApplejuiceFassade(CoreConnectionSettingsHolder coreConnectionSettingsHolder)
            throws IllegalArgumentException {
        coreHolder = coreConnectionSettingsHolder;
        coreHolder.addListener(this);
        modifiedXML = new ModifiedXMLHolder(coreHolder, this);
        DataUpdateInformer downloadInformer = new DataUpdateInformer(DATALISTENER_TYPE.DOWNLOAD_CHANGED) {
            protected Object getContentObject() {
                return modifiedXML.getDownloads();
            }
        };

        informer.put(downloadInformer.getDataUpdateListenerType(), downloadInformer);
        DataUpdateInformer searchInformer = new DataUpdateInformer(DATALISTENER_TYPE.SEARCH_CHANGED) {
            protected Object getContentObject() {
                return modifiedXML.getSearchs();
            }
        };

        informer.put(searchInformer.getDataUpdateListenerType(), searchInformer);
        DataUpdateInformer serverInformer = new DataUpdateInformer(DATALISTENER_TYPE.SERVER_CHANGED) {
            protected Object getContentObject() {
                return modifiedXML.getServer();
            }
        };

        informer.put(serverInformer.getDataUpdateListenerType(), serverInformer);
        DataUpdateInformer uploadInformer = new DataUpdateInformer(DATALISTENER_TYPE.UPLOAD_CHANGED) {
            protected Object getContentObject() {
                return modifiedXML.getUploads();
            }
        };

        informer.put(uploadInformer.getDataUpdateListenerType(), uploadInformer);
        DataUpdateInformer shareInformer = new DataUpdateInformer(DATALISTENER_TYPE.SHARE_CHANGED) {
            protected Object getContentObject() {
                return shareXML.getShare();
            }
        };

        informer.put(shareInformer.getDataUpdateListenerType(), shareInformer);
        DataUpdateInformer networkInformer = new DataUpdateInformer(DATALISTENER_TYPE.NETINFO_CHANGED) {
            protected Object getContentObject() {
                return modifiedXML.getNetworkInfo();
            }
        };

        informer.put(networkInformer.getDataUpdateListenerType(), networkInformer);
        DataUpdateInformer speedInformer = new DataUpdateInformer(DATALISTENER_TYPE.SPEED_CHANGED) {
            protected Object getContentObject() {
                return modifiedXML.getSpeeds();
            }
        };

        informer.put(speedInformer.getDataUpdateListenerType(), speedInformer);
        DataUpdateInformer informationInformer = new DataUpdateInformer(DATALISTENER_TYPE.INFORMATION_CHANGED) {
            protected Object getContentObject() {
                return modifiedXML.getInformation();
            }
        };

        informer.put(informationInformer.getDataUpdateListenerType(), informationInformer);
    }

    public DataPropertyChangeInformer getDownloadPropertyChangeInformer() {
        return modifiedXML.getDownloadPropertyChangeInformer();
    }

    public Long getLastCoreTimestamp() {
        if (modifiedXML != null) {
            return modifiedXML.getTimestamp();
        } else {
            return null;
        }
    }

    public void addDataUpdateListener(DataUpdateListener listener, DATALISTENER_TYPE type) {
        if (informer.containsKey(type)) {
            DataUpdateInformer anInformer = informer.get(type);

            anInformer.addDataUpdateListener(listener);
        }
    }

    public void removeDataUpdateListener(DataUpdateListener listener, DATALISTENER_TYPE type) {
        if (informer.containsKey(type)) {
            DataUpdateInformer anInformer = informer.get(type);

            anInformer.removeDataUpdateListener(listener);
        }
    }

    public static boolean addCoreStatusListener(CoreStatusListener listener) {
        return coreListener.add(listener);
    }

    public static boolean removeCoreStatusListener(CoreStatusListener listener) {
        return coreListener.remove(listener);
    }

    private void informCoreStatusListener(STATUS newStatus) {
        for (CoreStatusListener curListener : coreListener) {
            curListener.fireStatusChanged(newStatus);
        }
    }

    public boolean isLocalhost() {
        return coreHolder.isLocalhost();
    }

    private void checkForValidCoreversion() {
        if (getCoreVersion() == null) {
            return;
        }

        if (getCoreVersion().checkForValidCoreVersion() < 0) {
            throw new RuntimeException("invalid coreversion");
        }
    }

    private int tryUpdate(int versuch) {
        int anzahl = versuch;

        if (updateModifiedXML()) {
            anzahl = 0;
        } else {
            if (Thread.currentThread().isInterrupted()) {
                return anzahl;
            }
            anzahl++;
            if (anzahl == 3) {
                throw new CoreLostException();
            }
        }

        return anzahl;
    }

    public void startXMLCheck() {
        synchronized (workerLock) {
            if (workerThread != null && workerThread.isAlive()) {
                return;
            }
            workerThread = createWorkerThread();
            workerThread.start();
        }
    }

    private Thread createWorkerThread() {
        return new Thread("ApplejuiceFassadeXMLCheckThread") {
            public void run() {
                setPriority(Thread.NORM_PRIORITY);
                int versuch = 0;

                if (isInterrupted()) {
                    return;
                }
                try {
                    informationXML = new InformationXMLHolder(coreHolder);
                    directoryXML = new DirectoryXMLHolder(coreHolder);
                    shareXML = new ShareXMLHolder(coreHolder);
                    if (coreVersion == null) {
                        coreVersion = informationXML.getCoreVersion();
                        checkForValidCoreversion();
                    }
                } catch (RuntimeException error) {
                    if (isInterrupted()) {
                        return;
                    }
                    throw error;
                }
                if (isInterrupted()) {
                    return;
                }

                informCoreStatusListener(STATUS.STARTED);
                while (!isInterrupted()) {
                    try {
                        versuch = tryUpdate(versuch);
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
    }

    public void setDownloadPolling(boolean enabled) {
        modifiedXML.setDownloadPolling(enabled);
    }

    public void setUploadPolling(boolean enabled) {
        modifiedXML.setUploadPolling(enabled);
    }

    public void setUpdateInterval(long millis) {
        if (millis > 0) {
            sleepTime = millis;
        }
    }

    public void stopXMLCheck() {
        synchronized (workerLock) {
            for (CancelThread cancelWorker : cancelWorkers.values()) {
                cancelWorker.interrupt();
            }
            if (workerThread == null) {
                return;
            }
            workerThread.interrupt();
            workerThread = null;
        }
        informCoreStatusListener(STATUS.CLOSED);
    }

    public String[] getCurrentIncomingDirs() {
        Map<Integer, Download> download = getDownloadsSnapshot();
        ArrayList<String> incomingDirs = new ArrayList<String>();
        boolean found;

        synchronized (download) {
            for (Download curDownload : download.values()) {
                if (curDownload.getTargetDirectory().length() == 0) {
                    continue;
                }

                found = false;
                for (int i = 0; i < incomingDirs.size(); i++) {
                    if (incomingDirs.get(i).compareToIgnoreCase(curDownload.getTargetDirectory()) == 0) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    incomingDirs.add(curDownload.getTargetDirectory());
                }
            }
        }

        addExistingIncomingSubDirs(incomingDirs);
        incomingDirs.add("");
        return incomingDirs.toArray(new String[incomingDirs.size()]);
    }

    private void addExistingIncomingSubDirs(List<String> incomingDirs) {
        try {
            AJSettings settings = getAJSettings();
            String incomingDir = settings == null ? null : settings.getIncomingDir();

            if (incomingDir == null || incomingDir.length() == 0) {
                return;
            }

            collectSubDirs(incomingDir, incomingDir, incomingDirs, 0);
        } catch (RuntimeException | IllegalArgumentException ex) {
        }
    }

    private void collectSubDirs(String rootDir, String currentDir, List<String> incomingDirs, int depth)
            throws IllegalArgumentException {
        if (depth >= MAX_SUBDIR_DEPTH) {
            return;
        }

        String dirSeparator = Directory.getSeparator();

        if (dirSeparator == null || dirSeparator.length() == 0) {
            dirSeparator = File.separator;
        }

        String rootPrefix = rootDir.endsWith(dirSeparator) ? rootDir : rootDir + dirSeparator;

        for (Directory child : getDirectories(currentDir)) {
            if (child.getType() != Directory.TYPE_ORDNER) {
                continue;
            }

            String childPath = child.getPath();

            if (childPath == null || childPath.length() <= rootPrefix.length() || !childPath.startsWith(rootPrefix)) {
                continue;
            }

            String relativePath = childPath.substring(rootPrefix.length());
            boolean found = false;

            for (String known : incomingDirs) {
                if (known.compareToIgnoreCase(relativePath) == 0) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                incomingDirs.add(relativePath);
            }

            collectSubDirs(rootDir, childPath, incomingDirs, depth + 1);
        }
    }

    public Information getInformation() {
        return modifiedXML.getInformation();
    }

    public PartList getPartList(DownloadSource downloadSource)
            throws WebSiteNotFoundException {
        if (partlistXML == null) {
            partlistXML = new PartListXMLHolder(coreHolder);
        }

        return partlistXML.getPartList(downloadSource);
    }

    public PartList getPartList(Download download) throws WebSiteNotFoundException {
        if (partlistXML == null) {
            partlistXML = new PartListXMLHolder(coreHolder);
        }

        return partlistXML.getPartList(download);
    }

    public String[] getNetworkKnownServers(String ServerListURL) {
        NetworkServerXMLHolder getServerXMLHolder = NetworkServerXMLHolder.getInstance();

        return getServerXMLHolder.getNetworkKnownServers(ServerListURL);
    }

    public AJSettings getAJSettings() {
        if (settingsXML == null) {
            settingsXML = new SettingsXMLHolder(coreHolder);
        }

        return settingsXML.getAJSettings();
    }

    public AJSettings getCurrentAJSettings() {
        if (settingsXML == null) {
            return getAJSettings();
        } else {
            return settingsXML.getCurrentAJSettings();
        }
    }

    public void setMaxUpAndDown(final Long maxUp, final Long maxDown)
            throws IllegalArgumentException {
        if (maxUp == null || maxUp.longValue() <= 0) {
            throw new IllegalArgumentException("invalid maxUp");
        }

        if (maxDown == null || maxDown.longValue() <= 0) {
            throw new IllegalArgumentException("invalid maxDown");
        }

        new Thread() {
            public void run() {
                StringBuilder parameters = new StringBuilder("MaxUpload=");

                parameters.append(maxUp.toString());
                parameters.append("&MaxDownload=");
                parameters.append(maxDown.toString());
                HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                        "/function/setsettings?password=" + coreHolder.getCorePassword() + "&" +
                                parameters.toString(), false);
            }
        }.start();
    }

    public void saveAJSettings(AJSettings ajSettings) {
        StringBuilder parameters = new StringBuilder();

        try {
            parameters.append("Nickname=" + URLEncoder.encode(ajSettings.getNick(), "UTF-8"));
            parameters.append("&XMLPort=" + Long.toString(ajSettings.getXMLPort()));
            parameters.append("&Port=" + Long.toString(ajSettings.getPort()));
            parameters.append("&MaxUpload=" + Long.toString(ajSettings.getMaxUpload()));
            parameters.append("&MaxDownload=" + Long.toString(ajSettings.getMaxDownload()));
            parameters.append("&Speedperslot=" + Integer.toString(ajSettings.getSpeedPerSlot()));
            parameters.append("&Incomingdirectory=" + URLEncoder.encode(ajSettings.getIncomingDir(), "UTF-8"));
            parameters.append("&Temporarydirectory=" + URLEncoder.encode(ajSettings.getTempDir(), "UTF-8"));
            parameters.append("&maxconnections=" + URLEncoder.encode(Long.toString(ajSettings.getMaxConnections()), "UTF-8"));
            parameters.append("&maxsourcesperfile=" + URLEncoder.encode(Long.toString(ajSettings.getMaxSourcesPerFile()), "UTF-8"));
            parameters.append("&autoconnect=" + URLEncoder.encode(Boolean.toString(ajSettings.isAutoConnect()), "UTF-8"));
            parameters.append("&maxnewconnectionsperturn=" +
                    URLEncoder.encode(Long.toString(ajSettings.getMaxNewConnectionsPerTurn()), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                "/function/setsettings?password=" + coreHolder.getCorePassword() + "&" + parameters.toString(),
                false);
    }

    public Map<Integer, Server> getAllServer() {
        if (modifiedXML != null) {
            return modifiedXML.getServer();
        }

        return null;
    }

    public boolean updateModifiedXML() {
        try {
            if (modifiedXML.update()) {
                if (modifiedXML.isServerChanged()) {
                    informDataUpdateListener(DATALISTENER_TYPE.SERVER_CHANGED);
                }

                if (modifiedXML.isDownloadChanged()) {
                    informDataUpdateListener(DATALISTENER_TYPE.DOWNLOAD_CHANGED);
                }

                if (modifiedXML.isUploadChanged()) {
                    informDataUpdateListener(DATALISTENER_TYPE.UPLOAD_CHANGED);
                }

                if (modifiedXML.isNetworkInfoChanged()) {
                    informDataUpdateListener(DATALISTENER_TYPE.NETINFO_CHANGED);
                }

                if (modifiedXML.isSpeedChanged()) {
                    informDataUpdateListener(DATALISTENER_TYPE.SPEED_CHANGED);
                }

                if (modifiedXML.isSearchChanged()) {
                    informDataUpdateListener(DATALISTENER_TYPE.SEARCH_CHANGED);
                }

                if (modifiedXML.isInformationChanged()) {
                    informDataUpdateListener(DATALISTENER_TYPE.INFORMATION_CHANGED);
                }
            }

            return true;
        } catch (WrongPasswordException wpE) {
            stopXMLCheck();
            throw wpE;
        } catch (Exception re) {

            // connection to core lost, next try
            return false;
        }
    }

    public void resumeDownload(List<Download> downloads)
            throws IllegalArgumentException {
        if (downloads == null) {
            throw new IllegalArgumentException("invalid download-list");
        }

        if (downloads.size() == 0) {
            return;
        }

        for (Download curDownload : downloads) {
            if (curDownload == null) {
                throw new IllegalArgumentException("invalid download-list");
            }
        }

        StringBuffer parameters = new StringBuffer();
        int index = 0;

        for (Download curDownload : downloads) {
            parameters.append("&id");
            if (index != 0) {
                parameters.append(Integer.toString(index));
            }

            parameters.append("=" + curDownload.getId());
            index++;
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/resumedownload?password=" + coreHolder.getCorePassword() + parameters, false);
    }

    public void startSearch(String searchString) throws IllegalArgumentException {
        if (searchString == null) {
            throw new IllegalArgumentException("invalid search-phrase");
        }

        String toSearch = searchString.trim();

        if (toSearch.length() == 0) {
            throw new IllegalArgumentException("invalid search-phrase");
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/search?password=" + coreHolder.getCorePassword() + "&search=" + toSearch, false);
    }

    public void cancelSearch(Search search)
            throws IllegalArgumentException {
        if (search == null) {
            throw new IllegalArgumentException("invalid search");
        }

        synchronized (workerLock) {
            if (!cancelWorkers.containsKey(search.getId())) {
                CancelThread cancelWorker = new CancelThread(search);
                cancelWorkers.put(search.getId(), cancelWorker);
                cancelWorker.start();
            }
        }
    }

    public void renameDownload(Download download, String newFilename)
            throws IllegalArgumentException {
        if (download == null) {
            throw new IllegalArgumentException("invalid download");
        }

        if (newFilename == null || newFilename.length() == 0 || newFilename.trim().length() == 0) {
            throw new IllegalArgumentException("invalid filename");
        }

        String encodedName = newFilename;

        try {
            StringBuffer tempLink = new StringBuffer(encodedName);

            for (int i = 0; i < tempLink.length(); i++) {
                if (tempLink.charAt(i) == ' ') {
                    tempLink.setCharAt(i, '.');
                }
            }

            encodedName = URLEncoder.encode(tempLink.toString(), "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            ;

            // gibbet, also nix zu behandeln...
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/renamedownload?password=" + coreHolder.getCorePassword() + "&id=" + download.getId() +
                        "&name=" + encodedName, false);
    }

    public void setTargetDir(List<Download> downloads, String newDirectoryName)
            throws IllegalArgumentException {
        if (downloads == null) {
            throw new IllegalArgumentException("invalid download-list");
        }

        if (downloads.size() == 0) {
            return;
        }

        newDirectoryName = processSubdir(newDirectoryName);
        for (Download curDownload : downloads) {
            HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                    "/function/settargetdir?password=" + coreHolder.getCorePassword() + "&id=" +
                            curDownload.getId() + "&dir=" + newDirectoryName, false);
        }
    }

    public void shutdownCore() {
        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/exitcore?password=" + coreHolder.getCorePassword(), false);
    }

    public void setPassword(String password, boolean passwordIsPlaintext)
            throws IllegalArgumentException {
        if (password == null || (!passwordIsPlaintext && (password.length() == 0 || password.trim().length() == 0))) {
            throw new IllegalArgumentException("invalid password");
        }

        String newPassword = passwordIsPlaintext ? MD5Encoder.getMD5(password) : password;

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                "/function/setpassword?password=" + coreHolder.getCorePassword() + "&newpassword=" +
                        newPassword, false);
        coreHolder.setCorePassword(password, false);
    }

    public void cancelDownload(List<Download> downloads)
            throws IllegalArgumentException {
        if (downloads == null) {
            throw new IllegalArgumentException("invalid download-list");
        }

        if (downloads.size() == 0) {
            return;
        }

        for (Download curDownload : downloads) {
            if (curDownload == null) {
                throw new IllegalArgumentException("invalid download-list");
            }
        }

        StringBuffer parameters = new StringBuffer();
        int index = 0;

        for (Download curDownload : downloads) {
            parameters.append("&id");
            if (index != 0) {
                parameters.append(Integer.toString(index));
            }

            parameters.append("=" + curDownload.getId());
            index++;
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/canceldownload?password=" + coreHolder.getCorePassword() + parameters, false);
    }

    public void cleanDownloadList() {
        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/cleandownloadlist?password=" + coreHolder.getCorePassword(), false);
    }

    public void pauseDownload(List<Download> downloads)
            throws IllegalArgumentException {
        if (downloads == null) {
            throw new IllegalArgumentException("invalid download-list");
        }

        if (downloads.size() == 0) {
            return;
        }

        for (Download curDownload : downloads) {
            if (curDownload == null) {
                throw new IllegalArgumentException("invalid download-list");
            }
        }

        StringBuffer parameters = new StringBuffer();
        int index = 0;

        for (Download curDownload : downloads) {
            parameters.append("&id");
            if (index != 0) {
                parameters.append(Integer.toString(index));
            }

            parameters.append("=" + curDownload.getId());
            index++;
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/pausedownload?password=" + coreHolder.getCorePassword() + parameters, false);
    }

    public void connectToServer(Server server) throws IllegalArgumentException {
        if (server == null) {
            throw new IllegalArgumentException("invalid server");
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                "/function/serverlogin?password=" + coreHolder.getCorePassword() + "&id=" + server.getId(), false);
    }

    public Object getObjectById(Integer id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("invalid id");
        }

        GetObjectXMLHolder getObjectXMLHolder = new GetObjectXMLHolder(coreHolder);

        return getObjectXMLHolder.getObjectByID(id.intValue());
    }

    public void entferneServer(List<Server> server) throws IllegalArgumentException {
        if (server == null) {
            throw new IllegalArgumentException("invalid server");
        }

        if (server.size() == 0) {
            return;
        }

        for (Server curServer : server) {
            HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                    "/function/removeserver?password=" + coreHolder.getCorePassword() + "&id=" +
                            curServer.getId(), false);
        }
    }

    public void setPrioritaet(Share share, Integer priority)
            throws IllegalArgumentException {
        if (share == null) {
            throw new IllegalArgumentException("invalid share");
        }

        setPrioritaet(share.getId(), priority);
    }

    public void setPrioritaet(Download download, Integer priority)
            throws IllegalArgumentException {
        if (download == null) {
            throw new IllegalArgumentException("invalid download");
        }

        setPrioritaet(download.getId(), priority);
    }

    private void setPrioritaet(int id, Integer priority)
            throws IllegalArgumentException {
        if (priority == null) {
            throw new IllegalArgumentException("invalid priority");
        }

        if (priority.intValue() < 1 || priority.intValue() > 250) {
            throw new IllegalArgumentException("invalid priority: has to be 1<= x <=250");
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                "/function/setpriority?password=" + coreHolder.getCorePassword() + "&id=" + id + "&priority=" +
                        priority.intValue(), false);
    }

    private String processSubdir(String subdir) {
        if (subdir == null) {
            subdir = "";
        } else {
            subdir = subdir.trim();
            if (subdir.indexOf(File.separator) == 0 || subdir.indexOf(ApplejuiceFassade.separator) == 0) {
                subdir = subdir.substring(1);
            }

            subdir = subdir.replace(".", "_");
            subdir = subdir.replace(":", "_");
        }

        return subdir;
    }

    public synchronized String processLink(String link, String subdir)
            throws IllegalArgumentException {
        if (link == null || link.length() == 0) {
            throw new IllegalArgumentException("invalid link");
        }

        link = link.trim();
        if (link.regionMatches(true, 0, WEB_LINK_PREFIX, 0, WEB_LINK_PREFIX.length())) {
            link = link.substring("web+".length());
        }

        subdir = processSubdir(subdir);
        String encodedLink = link;

        try {
            StringBuffer tempLink = new StringBuffer(link);

            for (int i = 0; i < tempLink.length(); i++) {
                if (tempLink.charAt(i) == ' ') {
                    tempLink.setCharAt(i, '.');
                }
            }

            encodedLink = URLEncoder.encode(tempLink.toString(), "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            ;

            //gibbet nicht, also nix zu behandeln...
        }

        return HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                "/function/processlink?password=" + coreHolder.getCorePassword() + "&link=" +
                        encodedLink + "&subdir=" + subdir, true);
    }

    public void setPowerDownload(List<Download> downloads, Integer powerDownload)
            throws IllegalArgumentException {
        if (downloads == null || downloads.size() == 0) {
            throw new IllegalArgumentException("invalid downloadlist");
        }

        if (downloads.size() == 0) {
            return;
        }

        for (Download curDownload : downloads) {
            if (curDownload == null) {
                throw new IllegalArgumentException("invalid download-array");
            }
        }

        if (powerDownload.intValue() < 0 || powerDownload.intValue() > 490) {
            throw new IllegalArgumentException("invalid priority: has to be 1<= x <=490");
        }

        StringBuffer parameters = new StringBuffer(StringConstants.AND_PWDL + powerDownload);
        int index = 0;

        for (Download curDownload : downloads) {
            parameters.append("&id");
            if (index != 0) {
                parameters.append(Integer.toString(index));
            }

            parameters.append("=" + curDownload.getId());
            index++;
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                StringConstants.SET_PWDL_URL + coreHolder.getCorePassword() + parameters, false);
    }

    /**
     * 0 = connection 1 = wrong password 2 = no connection
     */
    public synchronized int isCoreAvailable() {
        try {
            String result = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                    StringConstants.GET_INFORMATION_URL + coreHolder.getCorePassword());

            if (result.indexOf("<applejuice>") == -1) {
                return 2;
            }
        } catch (WebSiteNotFoundException ex) {
            return 2;
        } catch (WrongPasswordException wpE) {
            return 1;
        }

        return 0;
    }

    public Version getCoreVersion() {
        return coreVersion;
    }

    public Map<Integer, Download> getDownloadsSnapshot() {
        return modifiedXML.getDownloads();
    }

    public void informDataUpdateListener(DATALISTENER_TYPE type) {
        if (informer.containsKey(type)) {
            DataUpdateInformer anInformer = informer.get(type);

            anInformer.informDataUpdateListener();
        }
    }

    public Map<Integer, Share> getShare(boolean reinit) {
        if (share == null || reinit) {
            share = shareXML.getShare();
        }

        return share;
    }

    public void addShareEntry(List<String> paths, SHAREMODE shareMode) {
        Set<ShareEntry> shareDirs = getAJSettings().getShareDirs();
        StringBuilder parameters = new StringBuilder();

        parameters.append("countshares=" + shareDirs.size() + paths.size());
        int i = 1;

        for (ShareEntry curShareEntry : shareDirs) {
            try {
                parameters.append(StringConstants.AND_SHAREDDIRECTORY);
                parameters.append(i);
                parameters.append(StringConstants.GLEICH);
                parameters.append(URLEncoder.encode(curShareEntry.getDir(), StringConstants.UTF_8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            parameters.append(StringConstants.AND_SHARESUB);
            parameters.append(i);
            parameters.append(StringConstants.GLEICH);
            parameters.append(curShareEntry.getShareMode() == SHAREMODE.SUBDIRECTORY ? StringConstants.TRUE : StringConstants.FALSE);
            i++;
        }

        for (String curPath : paths) {
            try {
                parameters.append(StringConstants.AND_SHAREDDIRECTORY);
                parameters.append(i);
                parameters.append(StringConstants.GLEICH);
                parameters.append(URLEncoder.encode(curPath, StringConstants.UTF_8));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            parameters.append(StringConstants.AND_SHARESUB);
            parameters.append(i);
            parameters.append(StringConstants.GLEICH);
            parameters.append(shareMode == SHAREMODE.SUBDIRECTORY ? StringConstants.TRUE : StringConstants.FALSE);

            i++;
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                StringConstants.SET_SETTINGS_URL + coreHolder.getCorePassword() + StringConstants.AND +
                        parameters, false);
    }

    public void removeShareEntry(List<String> paths) {
        Set<ShareEntry> shareDirs = getAJSettings().getShareDirs();
        ArrayList<ShareEntry> toRemove = new ArrayList<ShareEntry>();

        for (String curPath : paths) {
            for (ShareEntry curShareEntry : shareDirs) {
                if (curShareEntry.getDir().compareToIgnoreCase(curPath) == 0) {
                    toRemove.add(curShareEntry);
                    break;
                }
            }
        }

        for (ShareEntry curShareEntry : toRemove) {
            shareDirs.remove(curShareEntry);
        }

        setShare(shareDirs);
    }

    public void setShare(Set<ShareEntry> newShare) {
        if (newShare == null) {
            return;
        }

        String parameters = "countshares=" + newShare.size();
        int i = 1;

        for (ShareEntry curShareEntry : newShare) {
            try {
                parameters += "&sharedirectory" + i + "=" + URLEncoder.encode(curShareEntry.getDir(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            parameters += "&sharesub" + i + "=" + (curShareEntry.getShareMode() == SHAREMODE.SUBDIRECTORY ? "true" : "false");
            i++;
        }

        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                "/function/setsettings?password=" + coreHolder.getCorePassword() + "&" + parameters, false);
    }

    public List<Directory> getDirectories(String directory)
            throws IllegalArgumentException {
        return directoryXML.getDirectories(directory);
    }

    public NetworkInfo getNetworkInfo() {
        return modifiedXML.getNetworkInfo();
    }

    public void fireSettingsChanged(ITEM item, String oldValue, String newValue) {
    }

    private class CancelThread extends Thread {
        private final Search search;

        public CancelThread(Search search) {
            super("ApplejuiceFassadeCancelSearch-" + search.getId());
            this.search = search;
        }

        public void run() {
            try {
                if (search.getCreationTime() > System.currentTimeMillis() - 10000) {
                    sleep(10000);
                }
                while (!isInterrupted()) {
                    try {
                        HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.POST,
                                "/function/cancelsearch?password=" + coreHolder.getCorePassword() + "&id=" +
                                        search.getId(), true);
                        return;
                    } catch (WebSiteNotFoundException error) {
                        if (isInterrupted()) {
                            return;
                        }
                        LOGGER.log(System.Logger.Level.WARNING,
                                "Search cancellation failed for search {0}; retrying", search.getId());
                        sleep(4000);
                    }
                }
            } catch (InterruptedException interrupted) {
                interrupt();
            } finally {
                synchronized (workerLock) {
                    cancelWorkers.remove(search.getId(), this);
                }
            }
        }
    }
}
