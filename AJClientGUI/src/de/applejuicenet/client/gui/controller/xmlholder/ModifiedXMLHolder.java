package de.applejuicenet.client.gui.controller.xmlholder;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
import de.applejuicenet.client.gui.controller.PropertiesManager;
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
import java.util.Map;
import java.util.Set;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/ModifiedXMLHolder.java,v 1.31 2004/03/03 17:27:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ModifiedXMLHolder.java,v $
 * Revision 1.31  2004/03/03 17:27:55  maj0r
 * PMD-Optimierung
 *
 * Revision 1.30  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.29  2004/03/01 15:41:43  maj0r
 * IP wird nun korrekt angezeigt.
 *
 * Revision 1.28  2004/02/27 13:19:38  maj0r
 * Pruefung auf gueltigen Core eingebaut.
 * Um das zu pruefen, duerfen die Nachrichten im Startbereich erst spaeter geladen werden.
 *
 * Revision 1.27  2004/02/24 14:12:53  maj0r
 * DOM->SAX-Umstellung
 *
 * Revision 1.26  2004/02/24 08:49:32  maj0r
 * Bug #240 gefixt (Danke an computer.ist.org)
 * Bug behoben, der im VersionChecker zu einer NoSuchElementException fuehrte.
 *
 * Revision 1.25  2004/02/21 20:37:28  maj0r
 * Beim ersten Abrufen der Userliste muss Timestamp=0 verwendet werden.
 *
 * Revision 1.24  2004/02/19 17:13:46  maj0r
 * Serververwendung korrigiert.
 *
 * Revision 1.22  2004/02/19 09:53:13  maj0r
 * Serverauswertung korrigiert.
 *
 * Revision 1.21  2004/02/18 20:44:37  maj0r
 * Bugs #223 und #224 behoben.
 *
 * Revision 1.20  2004/02/18 18:57:23  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.19  2004/02/18 18:43:04  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.18  2004/02/18 17:24:21  maj0r
 * Von DOM auf SAX umgebaut.
 *
 *
 */

public class ModifiedXMLHolder
    extends DefaultHandler  {
    private Map sourcenZuDownloads = new HashMap();
    private XMLReader xr = null;

    private Map serverMap = new HashMap();
    private Map downloadMap = new HashMap();
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

    private ModifiedXMLHolder() {
        logger = Logger.getLogger(getClass());
        try {
            ConnectionSettings rc = PropertiesManager.getOptionsManager().
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
            xmlCommand = "/xml/modified.xml";
            Class parser = SAXParser.class;
            xr = XMLReaderFactory.createXMLReader(parser.getName());
            xr.setContentHandler(this);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public static ModifiedXMLHolder getInstance(){
        if (instance == null){
            instance = new ModifiedXMLHolder();
        }
        return instance;

    }

    public Map getServer() {
        return serverMap;
    }

    public Map getUploads() {
        return uploadMap;
    }

    public Map getDownloads() {
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
        }
    }

    private void checkNetworkMap(NetworkInfo netInfo, Map userAttributes){
        netInfo.setAjUserGesamt(Long.parseLong((String)userAttributes.get("users")));
        netInfo.setAjAnzahlDateien(Long.parseLong((String)userAttributes.get("files")));
        netInfo.setAjGesamtShare((String)userAttributes.get("filesize"));
        netInfo.setFirewalled(((String)userAttributes.get("filesize")).equals("true"));
        netInfo.setExterneIP((String)userAttributes.get("ip"));
        netInfo.setTryConnectToServer(Integer.parseInt((String)userAttributes.get("tryconnecttoserver")));
        netInfo.setConnectedWithServerId(Integer.parseInt((String)userAttributes.get("connectedwithserverid")));
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
        for (int i = 0; i < attr.getLength(); i++) {
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
        String versionNr = (String)userAttributes.get("version");
        int os = Integer.parseInt((String)userAttributes.get("operatingsystem"));
        if (!versionNr.equals("0.0.0.0") && os != -1) {
            Version version = new Version(versionNr, os);
            uploadDO.setVersion(version);
        }
    }

    private void checkUploadAttributes(Attributes attr){
        attributes.clear();
        for (int i = 0; i < attr.getLength(); i++) {
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
        String versionNr = (String)userAttributes.get("version");
        int os = Integer.parseInt((String)userAttributes.get("operatingsystem"));
        if (!versionNr.equals("0.0.0.0") && os != -1) {
            Version version = new Version(versionNr, os);
            downloadSourceDO.setVersion(version);
        }
    }

    private void checkUserAttributes(Attributes attr){
        attributes.clear();
        for (int i = 0; i < attr.getLength(); i++) {
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
    }

    private void checkDownloadMap(DownloadDO downloadDO, Map userAttributes){
        downloadDO.setShareId(Integer.parseInt((String)userAttributes.get("shareid")));
        downloadDO.setGroesse(Long.parseLong((String)userAttributes.get("size")));
        downloadDO.setStatus(Integer.parseInt((String)userAttributes.get("status")));
        downloadDO.setPowerDownload(Integer.parseInt((String)userAttributes.get("powerdownload")));
        downloadDO.setTemporaryFileNumber(Integer.parseInt((String)userAttributes.get("temporaryfilenumber")));
        downloadDO.setReady(Long.parseLong((String)userAttributes.get("ready")));
        downloadDO.setHash((String)userAttributes.get("hash"));
        downloadDO.setFilename((String)userAttributes.get("filename"));
        downloadDO.setTargetDirectory((String)userAttributes.get("targetdirectory"));
    }

    private void checkDownloadAttributes(Attributes attr){
        attributes.clear();
        for (int i = 0; i < attr.getLength(); i++) {
            attributes.put(attr.getLocalName(i), attr.getValue(i));
        }
        int id = Integer.parseInt((String)attributes.get("id"));
        String key = Integer.toString(id);
        DownloadDO downloadDO;
        if (downloadMap.containsKey(key)) {
            downloadDO = (DownloadDO) downloadMap.get(key);
        }
        else{
            downloadDO = new DownloadDO(id);
            downloadMap.put(key, downloadDO);
        }
        checkDownloadMap(downloadDO, attributes);
        attributes.clear();
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
    }

    private void checkRemovedAttributes(Attributes attr){
        for (int i = 0; i < attr.getLength(); i++) {
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
    }

    private void checkSearchAttributes(Attributes attr){
        attributes.clear();
        for (int i = 0; i < attr.getLength(); i++) {
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

        for (int i = 0; i < attr.getLength(); i++) {
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
            tmpSearchEntry = aSearch.new SearchEntry(id, searchId, checksum, groesse);
            aSearch.addSearchEntry(tmpSearchEntry);
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

        for (int i = 0; i < attr.getLength(); i++) {
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
        if (checkCount>1){
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
                logger.info("Das Passwort wurde coreseitig geändert.\r\nDas GUI wird beendet.");
            }
            AppleJuiceDialog.closeWithErrormessage(
                "Das Passwort wurde coreseitig geändert.\r\nDas GUI wird beendet.", true);
        }
    }

    public void reload() {
        try {
            reloadInProgress = true;
            checkForValidSession();
            Securer securer = new Securer();
            securer.start();
            String xmlString = getXMLString(filter);
            checkForValidResult(xmlString, securer);
            xr.parse( new InputSource(
               new StringReader( xmlString )) );
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
            reloadInProgress = false;
        }
        catch (WebSiteNotFoundException webSiteNotFound) {
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
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    private boolean secureSession() {
        try {
            return securerHolder.secure(sessionKontext, information);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
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
