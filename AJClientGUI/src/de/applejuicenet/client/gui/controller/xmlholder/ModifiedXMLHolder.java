package de.applejuicenet.client.gui.controller.xmlholder;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.DataPropertyChangeInformer;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.shared.Search;
import de.applejuicenet.client.shared.Search.SearchEntry;
import de.applejuicenet.client.shared.Search.SearchEntry.FileName;
import de.applejuicenet.client.shared.Version;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.dac.ShareDO;
import de.applejuicenet.client.shared.dac.UploadDO;
import de.applejuicenet.client.shared.exception.WebSiteNotFoundException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/ModifiedXMLHolder.java,v 1.53 2004/12/08 14:21:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class ModifiedXMLHolder
    extends DefaultHandler  {
    private Map sourcenZuDownloads = new HashMap();
    private XMLReader xr = null;

    private Map serverMap = new HashMap();
    private static Map downloadMap = new HashMap();
    private Map uploadMap = new HashMap();
    private Map searchMap = new HashMap();
    private NetworkInfo netInfo;
    private Information information;
    private int count = 0;
    private String filter = "";
    private String sessionKontext = null;

    private SecurerXMLHolder securerHolder = SecurerXMLHolder.getInstance();

    private int connectedWithServerId = -1;
    private int tryConnectToServer = -1;

    private boolean reloadInProgress = false;
    private Logger logger;

    private String host;
    private String password;
    private String zipMode = "";
    private String xmlCommand;
    private long timestamp = 0;
    private int checkCount = 0;
    private CharArrayWriter contents = new CharArrayWriter();
    private static ModifiedXMLHolder instance = null;

    private Map attributes = new HashMap();
    
	private Vector downloadEvents = new Vector();
	private boolean downloadSourceEvent;

    private static DataPropertyChangeInformer downloadPropertyChangeInformer;
    
    static{
    	downloadPropertyChangeInformer = new DataPropertyChangeInformer();
    }

    private ModifiedXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            init();
            xmlCommand = "/xml/modified.xml";
            Class parser = SAXParser.class;
            xr = XMLReaderFactory.createXMLReader(parser.getName());
            xr.setContentHandler(this);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public static ModifiedXMLHolder getInstance(){
        if (instance == null){
            instance = new ModifiedXMLHolder();
        }
        return instance;

    }

    public static DataPropertyChangeInformer getDownloadPropertyChangeInformer(){
    	return downloadPropertyChangeInformer;
    }
    
    private void init() {
        try {
            ConnectionSettings rc = OptionsManagerImpl.getInstance().
                getRemoteSettings();
            host = rc.getHost();
            password = rc.getOldPassword();
            if (host == null || host.length() == 0) {
                host = "localhost";
            }
            if (host.compareToIgnoreCase("localhost") != 0 &&
                host.compareTo("127.0.0.1") != 0) {
                zipMode = "mode=zip&";
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
    }

    public void reinit(){
        init();
    }

    public Map getServer() {
        return serverMap;
    }

    public Map getUploads() {
        return uploadMap;
    }

    public static Map getDownloads() {
        return downloadMap;
    }

    public Map getSearchs() {
        return searchMap;
    }

    public NetworkInfo getNetworkInfo() {
        return netInfo;
    }

    public synchronized boolean update() {
        return tryToReload();
    }

    private boolean tryToReload() {
        if (reloadInProgress) {
            return false;
        }
        else {
            switch (count) {
                //lazy loading
                case 0: {
                    count++;
                    filter =
                        "&filter=ids;down;uploads;server;informations;search&mode=zip";
                    break;
                }
                case 1: {
                    count++;
                    filter = "&filter=ids;informations;user;search&mode=zip";
                    break;
                }
                case 2: {
                    count++;
                    filter = ""; // kein Filter
                    break;
                }
                default: {
                    break;
                }
            }
            reload();
            return true;
        }
    }

    public Information getInformation(){
        return information;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map getSpeeds() {
        Map speeds = new HashMap();
        if (information != null) {
            speeds.put("uploadspeed",
                       new Long(information.getUploadSpeed()));
            speeds.put("downloadspeed",
                       new Long(information.getDownloadSpeed()));
            speeds.put("credits",
                       new Long(information.getCredits()));
            speeds.put("sessionupload",
                       new Long(information.getSessionUpload()));
            speeds.put("sessiondownload",
                       new Long(information.getSessionDownload()));
        }
        return speeds;
    }

    private void checkInformationAttributes(Attributes attr){
        if (information == null){
            information = new Information();
        }
        for (int i = 0; i < attr.getLength(); i++) {
            if (attr.getLocalName(i).equals("credits")){
                information.setCredits(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("sessionupload")){
                information.setSessionUpload(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("sessiondownload")){
                information.setSessionDownload(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("uploadspeed")){
                information.setUploadSpeed(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("downloadspeed")){
                information.setDownloadSpeed(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("openconnections")){
                information.setOpenConnections(Long.parseLong(attr.getValue(i)));
            }
            else if (attr.getLocalName(i).equals("maxuploadpositions")){
                information.setMaxUploadPositions(Long.parseLong(attr.getValue(i)));
            }
        }
    }

    private void checkNetworkMap(NetworkInfo netInfo, Map userAttributes){
        netInfo.setAjUserGesamt(Long.parseLong((String)userAttributes.get("users")));
        netInfo.setAjAnzahlDateien(Long.parseLong((String)userAttributes.get("files")));
        netInfo.setAjGesamtShare((String)userAttributes.get("filesize"));
        netInfo.setFirewalled(((String)userAttributes.get("firewalled")).equals("true"));
        netInfo.setExterneIP((String)userAttributes.get("ip"));
        netInfo.setTryConnectToServer(Integer.parseInt((String)userAttributes.get("tryconnecttoserver")));
        netInfo.setConnectedWithServerId(Integer.parseInt((String)userAttributes.get("connectedwithserverid")));
        netInfo.setConnectionTime(Long.parseLong((String)userAttributes.get("connectedsince")));
    }

    private void checkNetworkInfoAttributes(Attributes attr){
        if (netInfo == null){
            netInfo = new NetworkInfo();
        }
        attributes.clear();
        for (int i = 0; i < attr.getLength(); i++) {
            attributes.put(attr.getLocalName(i), attr.getValue(i));
        }
        checkNetworkMap(netInfo, attributes);
        attributes.clear();
    }

    private void checkServerMap(ServerDO serverDO, Map userAttributes){
        serverDO.setName((String)userAttributes.get("name"));
        serverDO.setHost((String)userAttributes.get("host"));
        serverDO.setTimeLastSeen(Long.parseLong((String)userAttributes.get("lastseen")));
        serverDO.setPort((String)userAttributes.get("port"));
        serverDO.setVersuche(Integer.parseInt((String)userAttributes.get("connectiontry")));
    }

    private void checkServerAttributes(Attributes attr){
        attributes.clear();
    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            attributes.put(attr.getLocalName(i), attr.getValue(i));
        }
        int id = Integer.parseInt((String)attributes.get("id"));
        String key = Integer.toString(id);
        ServerDO serverDO;
        if (serverMap.containsKey(key)) {
            serverDO = (ServerDO) serverMap.get(key);
        }
        else {
            serverDO = new ServerDO(id);
            serverMap.put(key, serverDO);
        }
        checkServerMap(serverDO, attributes);
        attributes.clear();
    }

    private Map shareMap = null;

    private void checkUploadMap(UploadDO uploadDO, Map userAttributes){
        uploadDO.setShareFileID(Integer.parseInt((String)userAttributes.get("shareid")));
        uploadDO.setStatus(Integer.parseInt((String)userAttributes.get("status")));
        uploadDO.setDirectState(Integer.parseInt((String)userAttributes.get("directstate")));
        uploadDO.setPrioritaet(Integer.parseInt((String)userAttributes.get("priority")));
        uploadDO.setUploadFrom(Integer.parseInt((String)userAttributes.get("uploadfrom")));
        uploadDO.setActualUploadPosition(Integer.parseInt((String)userAttributes.get("actualuploadposition")));
        uploadDO.setUploadTo(Integer.parseInt((String)userAttributes.get("uploadto")));
        uploadDO.setSpeed(Integer.parseInt((String)userAttributes.get("speed")));
        uploadDO.setNick((String)userAttributes.get("nick"));
        uploadDO.setLastConnection(Long.parseLong((String)userAttributes.get("lastconnection")));
        uploadDO.setLoaded(Double.parseDouble((String)userAttributes.get("loaded")));
        String versionNr = (String)userAttributes.get("version");
        int os = Integer.parseInt((String)userAttributes.get("operatingsystem"));
        if (!versionNr.equals("0.0.0.0") && os != -1) {
            Version version = new Version(versionNr, os);
            uploadDO.setVersion(version);
        }
    }

    private void checkUploadAttributes(Attributes attr){
        attributes.clear();
    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            attributes.put(attr.getLocalName(i), attr.getValue(i));
        }
        int id = Integer.parseInt((String)attributes.get("id"));

        String key = Integer.toString(id);
        UploadDO uploadDO;
        if (uploadMap.containsKey(key)) {
            uploadDO = (UploadDO) uploadMap.get(key);
        }
        else{
            uploadDO = new UploadDO(id);
            uploadMap.put(key, uploadDO);
        }
        checkUploadMap(uploadDO, attributes);
        attributes.clear();
        if (shareMap == null){
            shareMap = ApplejuiceFassade.getInstance().getShare(false);
        }
        ShareDO shareDO = (ShareDO) shareMap.get(uploadDO.getShareFileIDAsString());
        if (shareDO != null) {
            uploadDO.setDateiName(shareDO.getShortfilename());
        }
        else{
            //wenns die passende Sharedatei aus irgendeinem Grund nicht geben sollte,
            //wird dieser Upload auch nicht angezeigt
            uploadMap.remove(key);
        }
    }

    private void checkUserMap(DownloadSourceDO downloadSourceDO, Map userAttributes){
        downloadSourceDO.setStatus(Integer.parseInt((String)userAttributes.get("status")));
        downloadSourceDO.setDirectstate(Integer.parseInt((String)userAttributes.get("directstate")));
        downloadSourceDO.setDownloadFrom(Integer.parseInt((String)userAttributes.get("downloadfrom")));
        downloadSourceDO.setDownloadTo(Integer.parseInt((String)userAttributes.get("downloadto")));
        downloadSourceDO.setActualDownloadPosition(Integer.parseInt((String)userAttributes.get("actualdownloadposition")));
        downloadSourceDO.setSpeed(Integer.parseInt((String)userAttributes.get("speed")));
        downloadSourceDO.setQueuePosition(Integer.parseInt((String)userAttributes.get("queueposition")));
        downloadSourceDO.setPowerDownload(Integer.parseInt((String)userAttributes.get("powerdownload")));
        downloadSourceDO.setFilename((String)userAttributes.get("filename"));
        downloadSourceDO.setNickname((String)userAttributes.get("nickname"));
        downloadSourceDO.setHerkunft(Integer.parseInt((String)userAttributes.get("source")));
        String versionNr = (String)userAttributes.get("version");
        int os = Integer.parseInt((String)userAttributes.get("operatingsystem"));
        if (!versionNr.equals("0.0.0.0") && os != -1) {
            Version version = new Version(versionNr, os);
            downloadSourceDO.setVersion(version);
        }
    }

    private void checkUserAttributes(Attributes attr){
        attributes.clear();
    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            attributes.put(attr.getLocalName(i), attr.getValue(i));
        }
        int id = Integer.parseInt((String)attributes.get("id"));
        int downloadId = Integer.parseInt((String)attributes.get("downloadid"));

        String downloadKey = Integer.toString(downloadId);
        DownloadDO downloadDO = (DownloadDO) downloadMap.get(downloadKey);
        DownloadSourceDO downloadSourceDO = null;
        if (downloadDO != null) {
            downloadSourceDO = downloadDO.getSourceById(id);
            if (downloadSourceDO == null) {
                downloadSourceDO = new DownloadSourceDO(id);
                downloadDO.addSource(downloadSourceDO);
                sourcenZuDownloads.put(Integer.toString(id),
                                       downloadDO);
            }
        }
        else{
            downloadSourceDO = new DownloadSourceDO(id);
            downloadSourcesToDo.add(downloadSourceDO);
        }
        downloadSourceDO.setDownloadId(downloadId);
        checkUserMap(downloadSourceDO, attributes);
        attributes.clear();
        downloadSourceEvent = true;
    }

    private void checkDownloadMap(DownloadDO downloadDO, Map userAttributes, boolean newDownload){
        downloadDO.setShareId(Integer.parseInt((String)userAttributes.get("shareid")));
        downloadDO.setGroesse(Long.parseLong((String)userAttributes.get("size")));
        downloadDO.setHash((String)userAttributes.get("hash"));
        downloadDO.setTemporaryFileNumber(Integer.parseInt((String)userAttributes.get("temporaryfilenumber")));
        if (newDownload){
            downloadDO.setStatus(Integer.parseInt((String)userAttributes.get("status")));
            downloadDO.setFilename((String)userAttributes.get("filename"));
            downloadDO.setTargetDirectory((String)userAttributes.get("targetdirectory"));
            downloadDO.setPowerDownload(Integer.parseInt((String)userAttributes.get("powerdownload")));
            downloadDO.setReady(Long.parseLong((String)userAttributes.get("ready")));
        }
        else{
        	int tmpInt = Integer.parseInt((String)userAttributes.get("status"));
        	String old;
        	if (tmpInt != downloadDO.getStatus()){
        		old = Integer.toString(downloadDO.getStatus());
        		downloadDO.setStatus(tmpInt);
        		downloadEvents.add(new DownloadDataPropertyChangeEvent(
        				downloadDO, DownloadDataPropertyChangeEvent.STATUS_CHANGED, 
						old, Integer.toString(tmpInt)));
        	}
        	String tmpString = (String)userAttributes.get("filename");
        	old = downloadDO.getFilename();
        	if (old == null || !old.equals(tmpString)){
        		downloadDO.setFilename(tmpString);
        		downloadEvents.add(new DownloadDataPropertyChangeEvent(
        				downloadDO, DownloadDataPropertyChangeEvent.FILENAME_CHANGED, 
						old, tmpString));
        	}
        	tmpString = (String)userAttributes.get("targetdirectory");
        	old = downloadDO.getTargetDirectory();
        	if (old == null || !old.equals(tmpString)){
        		downloadDO.setTargetDirectory(tmpString);
        		downloadEvents.add(new DownloadDataPropertyChangeEvent(
        				downloadDO, DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED, 
						old, tmpString));
        	}
        	tmpInt = Integer.parseInt((String)userAttributes.get("powerdownload"));
        	if (tmpInt != downloadDO.getPowerDownload()){
        		old = Integer.toString(downloadDO.getPowerDownload());
        		downloadDO.setPowerDownload(tmpInt);
        		downloadEvents.add(new DownloadDataPropertyChangeEvent(
        				downloadDO, DownloadDataPropertyChangeEvent.PWDL_CHANGED, 
						old, Integer.toString(tmpInt)));
        	}        	
        	long tmpLong = Long.parseLong((String)userAttributes.get("ready"));
        	if (tmpLong != downloadDO.getReady()){
        		old = Long.toString(downloadDO.getReady());
        		downloadDO.setReady(tmpLong);
        		downloadEvents.add(new DownloadDataPropertyChangeEvent(
        				downloadDO, DownloadDataPropertyChangeEvent.READY_CHANGED, 
						old, Long.toString(tmpLong)));
        	}        	
        }
    }

    private void checkDownloadAttributes(Attributes attr){
        attributes.clear();
    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            attributes.put(attr.getLocalName(i), attr.getValue(i));
        }
        int id = Integer.parseInt((String)attributes.get("id"));
        String key = Integer.toString(id);
        DownloadDO downloadDO;
        boolean newDownload;
        if (downloadMap.containsKey(key)) {
            downloadDO = (DownloadDO) downloadMap.get(key);
            newDownload = false;
        }
        else{
            downloadDO = new DownloadDO(id);
            downloadMap.put(key, downloadDO);
            newDownload = true;
        }
        checkDownloadMap(downloadDO, attributes, newDownload);
        attributes.clear();
        if (newDownload){
        	downloadEvents.add(
	        		new DownloadDataPropertyChangeEvent(
	        				downloadMap, DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED, 
							null, downloadDO));
        }
    }

    private void removeDownload(String id){
        DownloadDO downloadDO = (DownloadDO) downloadMap.get(
            sourcenZuDownloads.get(id));
        if (downloadDO != null) {
            DownloadSourceDO[] sourcen = downloadDO.getSources();
            if (sourcen != null) {
                for (int y = 0; y < sourcen.length; y++) {
                    sourcenZuDownloads.remove(Integer.toString(
                        sourcen[y].getId()));
                }
            }
        }
        downloadMap.remove(id);
    	downloadEvents.add(
        		new DownloadDataPropertyChangeEvent(
        				downloadMap, DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED, 
        				id, null));
    }

    private void checkRemovedAttributes(Attributes attr){
    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            if (attr.getLocalName(i).equals("id")){
                String id = attr.getValue(i);
                removeDownload(id);
                uploadMap.remove(id);
                serverMap.remove(id);
                if (sourcenZuDownloads.containsKey(id)) {
                    DownloadDO downloadDO = (DownloadDO) sourcenZuDownloads.get(
                        id);
                    downloadDO.removeSource(id);
                    sourcenZuDownloads.remove(id);
                    downloadSourceEvent = true;
                    continue;
                }
                else if (searchMap.containsKey(id)) {
                    searchMap.remove(id);
                    Search.currentSearchCount = searchMap.size();
                    continue;
                }
            }
        }
    }

    private void checkSearchMap(Search aSearch, Map userAttributes){
        aSearch.setSuchText((String)userAttributes.get("searchtext"));
        aSearch.setOffeneSuchen(Integer.parseInt((String)userAttributes.get("opensearches")));
        aSearch.setGefundenDateien(Integer.parseInt((String)userAttributes.get("foundfiles")));
        aSearch.setDurchsuchteClients(Integer.parseInt((String)userAttributes.get("sumsearches")));
        aSearch.setRunning(((String)userAttributes.get("running")).equals("true"));
    }

    private void checkSearchAttributes(Attributes attr){
        attributes.clear();
    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            attributes.put(attr.getLocalName(i), attr.getValue(i));
        }
        int id = Integer.parseInt((String)attributes.get("id"));
        String key = Integer.toString(id);
        Search aSearch;
        if (searchMap.containsKey(key)) {
            aSearch = (Search) searchMap.get(key);
        }
        else {
            aSearch = new Search(id);
            searchMap.put(key, aSearch);
        }
        checkSearchMap(aSearch, attributes);
        attributes.clear();
    }

    private SearchEntry tmpSearchEntry = null;

    private void checkSearchEntryAttributes(Attributes attr){
        int searchId = -1;
        int id = -1;
        String checksum = "";
        long groesse = -1;

    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            if (attr.getLocalName(i).equals("id")) {
                id = Integer.parseInt(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("searchid")) {
                searchId = Integer.parseInt(attr.getValue(i));
            }
            else if (attr.getLocalName(i).equals("checksum")) {
                checksum = attr.getValue(i);
            }
            else if (attr.getLocalName(i).equals("size")) {
                groesse = Long.parseLong(attr.getValue(i));
            }
        }
        String key = Integer.toString(searchId);
        Search aSearch;
        if (searchMap.containsKey(key)) {
            aSearch = (Search) searchMap.get(key);
            tmpSearchEntry = aSearch.getSearchEntryById(id);
            if (tmpSearchEntry == null){
                tmpSearchEntry = aSearch.new SearchEntry(id, searchId, checksum, groesse);
                aSearch.addSearchEntry(tmpSearchEntry);
            }
        }
        else{
            tmpSearchEntry = new Search(-1).new SearchEntry(id, searchId, checksum, groesse);
            searchEntriesToDo.add(tmpSearchEntry);
        }
    }

    private Set searchEntriesToDo = new HashSet();
    private Set downloadSourcesToDo = new HashSet();

    private void checkSearchEntryFilenameAttributes(Attributes attr){
        if (tmpSearchEntry == null){
            return;
        }
        int haeufigkeit = -1;
        String dateiName = "";

    	int laenge = attr.getLength();
        for (int i = 0; i < laenge; i++) {
            if (attr.getLocalName(i).equals("name")) {
                dateiName = attr.getValue(i);
            }
            else if (attr.getLocalName(i).equals("user")) {
                haeufigkeit = Integer.parseInt(attr.getValue(i));
            }
        }
        FileName filename = tmpSearchEntry.new FileName(
                                                dateiName, haeufigkeit);
        tmpSearchEntry.addFileName(filename);
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        contents.reset();
        if (localName.equals("download")){
            checkDownloadAttributes(attr);
        }
        else if (localName.equals("upload")){
            checkUploadAttributes(attr);
        }
        else if (localName.equals("server")){
            checkServerAttributes(attr);
        }
        else if (localName.equals("information")){
            checkInformationAttributes(attr);
        }
        else if (localName.equals("networkinfo")){
            checkNetworkInfoAttributes(attr);
        }
        else if (localName.equals("object")){
            checkRemovedAttributes(attr);
        }
        else if (localName.equals("user")){
            checkUserAttributes(attr);
        }
        else if (localName.equals("search")){
            checkSearchAttributes(attr);
        }
        else if (localName.equals("searchentry")){
            checkSearchEntryAttributes(attr);
        }
        else if (localName.equals("filename")){
            checkSearchEntryFilenameAttributes(attr);
        }
    }

    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        if (localName.equals("searchentry")){
            tmpSearchEntry = null;
        }
        else if (localName.equals("welcomemessage")){
            if (netInfo == null){
                netInfo = new NetworkInfo();
            }
            String tmp = contents.toString();
            netInfo.setWelcomeMessage(tmp);
        }
        else if (checkCount>1){
            if (localName.equals("time")) {
                timestamp = Long.parseLong(contents.toString());
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws
        SAXException {
        contents.write(ch, start, length);
    }

    private String getXMLString(String parameters) throws
        Exception {
        String xmlData = null;
        String command = xmlCommand + "?";
        if (parameters.indexOf("mode=zip") == -1) {
            command += zipMode;
        }
        command += "password=" + password + "&timestamp=" +
            timestamp + parameters + sessionKontext;
        xmlData = HtmlLoader.getHtmlXMLContent(host, HtmlLoader.GET,
                                               command);
        if (xmlData.length() == 0) {
            throw new IllegalArgumentException();
        }
        return xmlData;
    }

    private int updateTryConnect(){
        int verbindungsStatus = Information.NICHT_VERBUNDEN;
        ServerDO serverDO = null;
        if (tryConnectToServer != -1){
            Object alterServer = serverMap.get(Integer.toString(
                tryConnectToServer));
            if (alterServer != null) {
                ( (ServerDO) alterServer).setTryConnect(false);
            }
            information.setServer(null);
        }
        if (netInfo.getTryConnectToServer() != -1) {
            serverDO = (ServerDO) serverMap.get(Integer.
                toString(netInfo.getTryConnectToServer()));
            verbindungsStatus = Information.VERSUCHE_ZU_VERBINDEN;
            if (serverDO != null){
                serverDO.setTryConnect(true);
            }
        }
        tryConnectToServer = netInfo.getTryConnectToServer();
        information.setServer(serverDO);
        information.setVerbindungsStatus(verbindungsStatus);
        return verbindungsStatus;
    }

    private int updateConnected(){
        int verbindungsStatus = Information.NICHT_VERBUNDEN;
        ServerDO serverDO = null;
        if (connectedWithServerId != -1){
            Object alterServer = serverMap.get(Integer.toString(
                connectedWithServerId));
            if (alterServer != null) {
                ( (ServerDO) alterServer).setConnected(false);
            }
            information.setServer(null);
        }
        if (netInfo.getConnectedWithServerId() != -1) {
            serverDO = (ServerDO) serverMap.get(Integer.toString(
                netInfo.getConnectedWithServerId()));
            verbindungsStatus = Information.VERBUNDEN;
            if (serverDO != null){
                serverDO.setConnected(true);
            }
        }
        connectedWithServerId = netInfo.getConnectedWithServerId();
        information.setServer(serverDO);
        information.setVerbindungsStatus(verbindungsStatus);
        return verbindungsStatus;
    }

    private void parseRest(){
        int verbindungsStatus = Information.NICHT_VERBUNDEN;
        if (tryConnectToServer != netInfo.getTryConnectToServer()) {
            verbindungsStatus = updateTryConnect();
        }
        if (connectedWithServerId != netInfo.getConnectedWithServerId() ){
            verbindungsStatus = updateConnected();
        }
        information.setExterneIP(netInfo.getExterneIP());

        if (searchEntriesToDo.size()>0){
            Search aSearch;
            SearchEntry searchEntry;
            Iterator it = searchEntriesToDo.iterator();
            while(it.hasNext()){
                searchEntry = (SearchEntry)it.next();
                if (searchMap.containsKey(Integer.toString(searchEntry.getSearchId()))) {
                    aSearch = (Search) searchMap.get(Integer.toString(searchEntry.getSearchId()));
                    aSearch.addSearchEntry(searchEntry);
                }

            }
            searchEntriesToDo.clear();
        }
        if (downloadSourcesToDo.size()>0){
            DownloadDO downloadDO;
            DownloadSourceDO downloadSourceDO;
            Iterator it = downloadSourcesToDo.iterator();
            while(it.hasNext()){
                downloadSourceDO = (DownloadSourceDO)it.next();
                String downloadKey = Integer.toString(downloadSourceDO.getDownloadId());
                if (downloadMap.containsKey(downloadKey)){
                    downloadDO = (DownloadDO) downloadMap.get(
                        downloadKey);
                    downloadDO.addSource(downloadSourceDO);
                    sourcenZuDownloads.put(Integer.toString(downloadSourceDO.getId()),
                                           downloadDO);
                }
            }
            downloadSourcesToDo.clear();
        }
    }

    private void checkForValidSession(){
        if (sessionKontext == null){
            SessionXMLHolder sessionHolder = SessionXMLHolder.getInstance();
            String sessionId = sessionHolder.getNewSessionId();
            sessionKontext = "&session=" + sessionId;
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug(
                    "Neue SessionId: " + sessionId);
            }
        }
    }

    private void checkForValidResult(String xmlString, Securer securer){
        if (xmlString.indexOf("wrong password") != -1){
            if (!securer.isInterrupted()) {
                securer.interrupt();
            }
            if (logger.isEnabledFor(Level.INFO)){
                logger.info("Das Passwort wurde coreseitig geaendert.\r\nDas GUI wird beendet.");
            }
            AppleJuiceDialog.closeWithErrormessage(
                "Das Passwort wurde coreseitig geaendert.\r\nDas GUI wird beendet.", true);
        }
    }

    public synchronized void reload() {
        boolean reloadSession = false;
        try {
            reloadInProgress = true;
            checkForValidSession();
            Securer securer = new Securer();
            securer.start();
            String xmlString = getXMLString(filter);
            checkForValidResult(xmlString, securer);
            downloadEvents.clear();
            downloadSourceEvent = false;
            xr.parse(new InputSource(
                new StringReader(xmlString)));
            parseRest();
            if (!securer.isInterrupted()) {
                securer.interrupt();
            }
            if (!securer.isOK()) {
                checkForValidSession();
            }
            if (checkCount<2){
                checkCount++;
            }
            securer = null;
            if (downloadSourceEvent){
            	downloadEvents.add(
    	        		new DownloadDataPropertyChangeEvent(
    	        				downloadMap, DownloadDataPropertyChangeEvent.A_SOURCE_CHANGED, 
    	        				null, null));

            }
    		if (downloadEvents.size() > 0){
    	        downloadPropertyChangeInformer.propertyChanged(
    	        		new DownloadDataPropertyChangeEvent(downloadEvents));
    		}
            reloadInProgress = false;
        }
        catch (WebSiteNotFoundException webSiteNotFound) {
            reloadSession = true;
        }
        catch (IllegalArgumentException webSiteNotFound) {
            reloadSession = true;
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
        }
        if (reloadSession){
            SessionXMLHolder sessionHolder = SessionXMLHolder.getInstance();
            String sessionId = sessionHolder.getNewSessionId();
            reloadInProgress = false;
            sessionKontext = "&session=" + sessionId;
            if (logger.isEnabledFor(Level.DEBUG)) {
                logger.debug(
                    "Neue SessionId: " + sessionId);
            }
            throw new RuntimeException();
        }
    }

    private boolean secureSession() {
        try {
            return securerHolder.secure(sessionKontext, information);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
            }
            return false;
        }
    }

    private class Securer
        extends Thread {
        private boolean ok = true;

        public void run() {
            while (!interrupted()) {
                try {
                    sleep(10000);
                    if (!secureSession()) {
                        ok = false;
                        interrupt();
                    }
                }
                catch (InterruptedException ex) {
                    interrupt();
                }
            }
        }

        public boolean isOK() {
            return ok;
        }
    }

}
