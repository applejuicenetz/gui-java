package de.applejuicenet.client.gui.controller.xmlholder;

import java.util.HashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.WebXMLParser;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.MapSetStringKey;
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
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/xmlholder/Attic/ModifiedXMLHolder.java,v 1.9 2004/01/29 13:47:57 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ModifiedXMLHolder.java,v $
 * Revision 1.9  2004/01/29 13:47:57  maj0r
 * Während des ersten Holens der Quellen wird nun alle 5 Seks die Statuszeile aktualisiert.
 *
 * Revision 1.8  2004/01/29 11:07:57  maj0r
 * Alte Objekte werden wieder korrekt entfernt.
 *
 * Revision 1.7  2004/01/28 12:34:21  maj0r
 * Beim Start Filter eingebaut.
 * Session wird nun besser aufrecht erhalten.
 *
 * Revision 1.6  2004/01/24 08:10:24  maj0r
 * Anzahl der Verbindungsversuche eingebaut.
 *
 * Revision 1.5  2004/01/07 16:15:20  maj0r
 * Warnmeldung bezueglich 30-Minuten-Sperre bei manuellem Serverwechsel eingebaut.
 *
 * Revision 1.4  2004/01/06 17:32:50  maj0r
 * Es wird nun zweimal versucht den Core erneut zu erreichen, wenn die Verbindung unterbrochen wurde.
 *
 * Revision 1.3  2004/01/01 15:36:52  maj0r
 * Information-ID wird nicht richtig uebertragen...
 *
 * Revision 1.2  2004/01/01 14:25:25  maj0r
 * Information-Id wird nun auch ausgelesen.
 *
 * Revision 1.1  2003/12/31 16:13:31  maj0r
 * Refactoring.
 *
 * Revision 1.44  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.43  2003/12/16 14:51:46  maj0r
 * Suche kann nun GUI-seitig abgebrochen werden.
 *
 * Revision 1.42  2003/11/04 15:55:05  maj0r
 * gc eingefuehrt.
 *
 * Revision 1.41  2003/11/03 14:29:16  maj0r
 * Speicheroptimierung.
 *
 * Revision 1.40  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.39  2003/10/21 11:36:32  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 * Revision 1.38  2003/10/12 15:57:55  maj0r
 * Kleinere Bugs behoben.
 * Sortiert wird nun nur noch bei Klick auf den Spaltenkopf um CPU-Zeit zu sparen.
 *
 * Revision 1.37  2003/10/04 15:53:40  maj0r
 * Kompatibilitaet zur naechsten Coreversion hergestellt.
 *
 * Revision 1.36  2003/10/01 07:25:44  maj0r
 * Suche weiter gefuehrt.
 *
 * Revision 1.35  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.34  2003/09/14 06:37:39  maj0r
 * Moeglichen NullPointer behoben.
 *
 * Revision 1.33  2003/09/13 11:30:41  maj0r
 * Neuen Listener fuer Geschwindigkeitsanzeigen eingebaut.
 *
 * Revision 1.32  2003/09/11 09:41:16  maj0r
 * Nullpointer behoben.
 *
 * Revision 1.31  2003/09/11 06:54:15  maj0r
 * Auf neues Sessions-Prinzip umgebaut.
 * Sprachenwechsel korrigert, geht nun wieder flott.
 *
 * Revision 1.30  2003/09/10 15:30:48  maj0r
 * Begonnen auf neue Session-Struktur umzubauen.
 *
 * Revision 1.29  2003/09/06 08:34:23  maj0r
 * Nullpointer behoben.
 * Dank an Fumpi.
 *
 * Revision 1.28  2003/09/02 19:29:26  maj0r
 * Einige Stellen synchronisiert und Nullpointer behoben.
 * Version 0.21 beta.
 *
 * Revision 1.27  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.26  2003/08/31 11:06:22  maj0r
 * CheckInProgress geaendert.
 *
 * Revision 1.25  2003/08/22 10:03:11  maj0r
 * Threadverwendung korrigiert.
 *
 * Revision 1.24  2003/08/21 15:13:29  maj0r
 * Auf Thread umgebaut.
 *
 * Revision 1.23  2003/08/19 15:57:21  maj0r
 * Gesamtgeschwindigkeit wird nun angezeigt.
 *
 * Revision 1.22  2003/08/18 17:11:26  maj0r
 * Alte Uploads wurden nicht entfernt. Korrigiert.
 *
 * Revision 1.21  2003/08/18 14:54:11  maj0r
 * Alte Eintraege loeschen.
 *
 * Revision 1.20  2003/08/16 17:50:06  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.19  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.18  2003/08/14 20:08:42  maj0r
 * Tree fuer Shareauswahl eingefuegt, aber noch nicht fertiggestellt.
 *
 * Revision 1.17  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.16  2003/08/09 10:57:54  maj0r
 * Upload- und DownloadTabelle weitergeführt.
 *
 * Revision 1.15  2003/08/08 05:35:52  maj0r
 * Nullpointer behoben.
 *
 * Revision 1.14  2003/08/05 20:47:06  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.13  2003/08/05 05:11:59  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.12  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.11  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.10  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.9  2003/07/01 14:59:28  maj0r
 * Keyverwendung bei HashSets und HashMaps korrigiert.
 * Server-IDs werden nun abgeglichen, alte werden entfernt.
 *
 * Revision 1.8  2003/06/30 20:35:50  maj0r
 * Code optimiert.
 *
 * Revision 1.7  2003/06/22 19:00:27  maj0r
 * Basisklasse umbenannt.
 *
 * Revision 1.6  2003/06/22 16:24:09  maj0r
 * Umrechnung korrigiert.
 *
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefuegt.
 *
 *
 */

public class ModifiedXMLHolder
    extends WebXMLParser {
    private HashMap sourcenZuDownloads = new HashMap();

    private HashMap serverMap = new HashMap();
    private HashMap downloadMap = new HashMap();
    private HashMap uploadMap = new HashMap();
    private HashMap searchMap = new HashMap();
    private NetworkInfo netInfo;
    private Information information;
    private int count = 0;
    private String filter = "";
    private String sessionKontext = "";

    private SecurerXMLHolder securerHolder = new SecurerXMLHolder();

    private int connectedWithServerId = -1;
    private int tryConnectToServer = -1;

    private boolean reloadInProgress = false;
    private int gcCounter = 0;
    private Logger logger;

    public ModifiedXMLHolder() {
        super("/xml/modified.xml", "");
        logger = Logger.getLogger(getClass());
    }

    public HashMap getServer() {
        return serverMap;
    }

    public HashMap getUploads() {
        return uploadMap;
    }

    public HashMap getDownloads() {
        return downloadMap;
    }

    public HashMap getSearchs() {
        return searchMap;
    }

    public NetworkInfo getNetworkInfo() {
        return netInfo;
    }

    public synchronized boolean update(String sessionId) {
        sessionKontext = "&session=" + sessionId;
        if (tryToReload(sessionKontext)){
            switch (count){
                case 0:{
                    updateIDs();
                    updateDownloads();
                    updateUploads();
                    updateServer();
                    updateNetworkInfo();
                    getInformation(true);
                    break;
                }
                case 1:{
                    updateIDs();
                    updateDownloads();
                    updateUploads();
                    updateServer();
                    updateNetworkInfo();
                    getInformation(true);
                    break;
                }
                default:{
                    updateIDs();
                    updateDownloads();
                    updateUploads();
                    updateServer();
                    updateNetworkInfo();
                    getInformation(true);
                    updateSuche();
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    private boolean tryToReload(String parameters){
        if (reloadInProgress) {
            return false;
        }
        else {
            switch (count){
                //lazy loading
                case 0:{
                    count ++;
                    filter = "&filter=ids;down;uploads;server;informations";
                    break;
                }
                case 1:{
                    count ++;
                    filter = "&filter=ids;informations;user";
                    break;
                }
                case 2:{
                    count ++;
                    filter = ""; // kein Filter
                    break;
                }
                default:{
                    break;
                }
            }
            reload(parameters + filter);
            return true;
        }
    }

    private void secureSession() {
        try{
            securerHolder.secure(sessionKontext, information);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public void reload(String parameters) {
        try {
            reloadInProgress = true;
            Thread securer = new Thread(){
                public void run(){
                    while (!interrupted()) {
                        try {
                            sleep(5000);
                            secureSession();
//                            System.out.println("session secured");
                        }
                        catch (InterruptedException ex) {
                            interrupt();
                        }
                    }
                }
            };
            securer.start();
            super.reload(parameters, true);
            securer.interrupt();
            securer = null;
            reloadInProgress = false;
        }
        catch (WebSiteNotFoundException webSiteNotFound) {
            reloadInProgress = false;
            throw new RuntimeException();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public void update() {
        throw new RuntimeException();
    }

    public Information getInformation(boolean reload) {
        try {
            if (reload || information == null) {
                NodeList nodes = document.getElementsByTagName("information");
                long sessionUpload;
                long sessionDownload;
                long credits;
                long uploadSpeed;
                long downloadSpeed;
                long openConnections;
                String serverName = null;
                String externeIP;
                int verbindungsStatus = Information.NICHT_VERBUNDEN;
                if (tryConnectToServer != -1) {
                    ServerDO serverDO = (ServerDO) serverMap.get(new
                        MapSetStringKey(tryConnectToServer));
                    if (serverDO != null) {
                        verbindungsStatus = Information.VERSUCHE_ZU_VERBINDEN;
                        serverName = serverDO.getName();
                    }
                }
                else if (connectedWithServerId != -1) {
                    ServerDO serverDO = (ServerDO) serverMap.get(new
                        MapSetStringKey(connectedWithServerId));
                    if (serverDO != null) {
                        verbindungsStatus = Information.VERBUNDEN;
                        serverName = serverDO.getName();
                    }
                }
                else {
                    verbindungsStatus = Information.NICHT_VERBUNDEN;
                }
                externeIP = netInfo.getExterneIP();
                if (nodes.getLength() != 0) {
                    Element e = (Element) nodes.item(0);
//                int id = Integer.parseInt(e.getAttribute("id"));
                    credits = Long.parseLong(e.getAttribute("credits"));
                    uploadSpeed = Long.parseLong(e.getAttribute("uploadspeed"));
                    downloadSpeed = Long.parseLong(e.getAttribute(
                        "downloadspeed"));
                    openConnections = Long.parseLong(e.getAttribute(
                        "openconnections"));
                    sessionUpload = Long.parseLong(e.getAttribute(
                        "sessionupload"));
                    sessionDownload = Long.parseLong(e.getAttribute(
                        "sessiondownload"));
                    information = new Information( -1, sessionUpload,
                                                  sessionDownload,
                                                  credits, uploadSpeed,
                                                  downloadSpeed,
                                                  openConnections,
                                                  verbindungsStatus, serverName,
                                                  externeIP);
                }
                else {
                    information = new Information(information.getId(),
                                                  information.getSessionUpload(),
                                                  information.
                                                  getSessionDownload(),
                                                  information.getCredits(),
                                                  information.getUploadSpeed(),
                                                  information.getDownloadSpeed(),
                                                  information.
                                                  getOpenConnections(),
                                                  verbindungsStatus, serverName,
                                                  externeIP);
                }
            }
            return information;
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
            return null;
        }
    }

    public HashMap getSpeeds() {
        HashMap speeds = new HashMap();
        try {
            NodeList nodes = document.getElementsByTagName("information");
            if (nodes.getLength() > 0) {
                Element e = (Element) nodes.item(0);
                if (e != null) {
                    speeds.put(new MapSetStringKey("uploadspeed"),
                               new Long(e.getAttribute("uploadspeed")));
                    speeds.put(new MapSetStringKey("downloadspeed"),
                               new Long(e.getAttribute("downloadspeed")));
                    speeds.put(new MapSetStringKey("credits"),
                               new Long(e.getAttribute("credits")));
                    speeds.put(new MapSetStringKey("sessionupload"),
                               new Long(e.getAttribute("sessionupload")));
                    speeds.put(new MapSetStringKey("sessiondownload"),
                               new Long(e.getAttribute("sessiondownload")));
                }
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
        return speeds;
    }

    private void updateIDs() {
        try {
            NodeList nodes = document.getElementsByTagName("removed");
            if (nodes == null || nodes.getLength() == 0){
                return;
            }
            nodes = nodes.item(0).getChildNodes();
            if (nodes == null || nodes.getLength() == 0){
                return;
            }
            Element e = null;
            String id = null;
            int size = nodes.getLength();
            MapSetStringKey toRemoveKey;
            DownloadDO downloadDO;
            DownloadSourceDO[] sourcen;
            for (int i = 0; i < size; i++) {
                if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                    e = (Element) nodes.item(i);
                    id = e.getAttribute("id");
                    toRemoveKey = new MapSetStringKey(id);
                    if (uploadMap.containsKey(toRemoveKey)) {
                        uploadMap.remove(toRemoveKey);
                        continue;
                    }
                    else if (downloadMap.containsKey(toRemoveKey)) {
                        downloadDO = (DownloadDO) downloadMap.get(
                            sourcenZuDownloads.get(toRemoveKey));
                        if (downloadDO != null) {
                            sourcen = downloadDO.getSources();
                            if (sourcen != null) {
                                for (int y = 0; y < sourcen.length; y++) {
                                    sourcenZuDownloads.remove(new
                                        MapSetStringKey(
                                        sourcen[y].getId()));
                                }
                            }
                        }
                        downloadMap.remove(toRemoveKey);
                        continue;
                    }
                    else if (serverMap.containsKey(toRemoveKey)) {
                        serverMap.remove(toRemoveKey);
                        continue;
                    }
                    else if (sourcenZuDownloads.containsKey(toRemoveKey)) {
                        downloadDO = (DownloadDO) sourcenZuDownloads.get(
                            toRemoveKey);
                        downloadDO.removeSource(id);
                        sourcenZuDownloads.remove(toRemoveKey);
                        continue;
                    }
                    else if (searchMap.containsKey(toRemoveKey)) {
                        searchMap.remove(toRemoveKey);
                        Search.currentSearchCount = searchMap.size();
                        continue;
                    }
                }
            }
            gcCounter++;
            if (gcCounter - 30 == 0) {
                gcCounter = 0;
                if (logger.isEnabledFor(Level.DEBUG)) {
                    Runtime runtime = Runtime.getRuntime();
                    float freeMemoryOld = (float) runtime.freeMemory();
                    runtime.gc();
                    float freeMemoryNew = (float) runtime.freeMemory();
                    logger.debug(String.valueOf( (int) (freeMemoryNew -
                        freeMemoryOld) / 1024) + "K freed");
                }
                else {
                    Runtime.getRuntime().gc();
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void updateSuche() {
        try {
            NodeList nodes = document.getElementsByTagName("search");
            int size = nodes.getLength();
            if (size > 0) {
                Element e;
                int id;
                String suchtext;
                int offeneSuchen;
                int gefundeneDateien;
                int durchsuchteClients;
                MapSetStringKey key;
                Search aSearch;
                String temp;
                for (int i = 0; i < size; i++) {
                    e = (Element) nodes.item(i);
                    id = Integer.parseInt(e.getAttribute("id"));
                    key = new MapSetStringKey(id);
                    suchtext = e.getAttribute("searchtext");
                    temp = e.getAttribute("opensearchs");
                    if (temp.length() == 0) {
                        temp = e.getAttribute("opensearches");
                    }
                    offeneSuchen = Integer.parseInt(temp);
                    temp = e.getAttribute("sumsearches");
                    durchsuchteClients = Integer.parseInt(temp);
                    temp = e.getAttribute("foundfiles");
                    gefundeneDateien = Integer.parseInt(temp);
                    if (searchMap.containsKey(key)) {
                        aSearch = (Search) searchMap.get(key);
                        aSearch.setDurchsuchteClients(durchsuchteClients);
                        aSearch.setGefundenDateien(gefundeneDateien);
                        aSearch.setOffeneSuchen(offeneSuchen);
                        aSearch.setSuchText(suchtext);
                    }
                    else {
                        aSearch = new Search(id);
                        aSearch.setDurchsuchteClients(durchsuchteClients);
                        aSearch.setGefundenDateien(gefundeneDateien);
                        aSearch.setOffeneSuchen(offeneSuchen);
                        aSearch.setSuchText(suchtext);
                        searchMap.put(key, aSearch);
                    }
                }
                Search.currentSearchCount = searchMap.size();
            }
            nodes = document.getElementsByTagName("searchentry");
            size = nodes.getLength();
            if (size > 0) {
                Element e;
                int id;
                int searchid;
                String checksum;
                long groesse;
                MapSetStringKey key;
                Search aSearch;
                Search.SearchEntry searchEntry;
                Element innerElement;
                NodeList childNodes;
                String dateiName;
                int haeufigkeit;
                Search.SearchEntry.FileName filename;
                for (int i = 0; i < size; i++) {
                    e = (Element) nodes.item(i);
                    id = Integer.parseInt(e.getAttribute("id"));
                    searchid = Integer.parseInt(e.getAttribute("searchid"));
                    key = new MapSetStringKey(searchid);
                    checksum = e.getAttribute("checksum");
                    groesse = Long.parseLong(e.getAttribute("size"));
                    aSearch = (Search) searchMap.get(key);
                    if (aSearch != null) {
                        searchEntry = aSearch.new SearchEntry(id, checksum,
                            groesse);
                        childNodes = nodes.item(i).getChildNodes();
                        int nodesSize = childNodes.getLength();
                        for (int y = 0; y < nodesSize; y++) {
                            if (childNodes.item(y).getNodeType() ==
                                Node.ELEMENT_NODE) {
                                innerElement = (Element) childNodes.item(y);
                                if (innerElement.getNodeName().
                                    compareToIgnoreCase("filename") == 0) {
                                    dateiName = innerElement.getAttribute(
                                        "name");
                                    haeufigkeit = Integer.parseInt(innerElement.
                                        getAttribute("user"));
                                    filename = searchEntry.new FileName(
                                        dateiName, haeufigkeit);
                                    searchEntry.addFileName(filename);
                                }
                            }
                        }
                        aSearch.addSearchEntry(searchEntry);
                    }
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void updateDownloads() {
        try {
            Element e = null;
            int id;
            int shareid;
            String hash = null;
            long fileSize;
            long sizeReady;
            String temp = null;
            int status;
            String filename = null;
            String targetDirectory = null;
            int powerDownload;
            int temporaryFileNumber;
            NodeList nodes = document.getElementsByTagName("download");
            int size = nodes.getLength();
            DownloadDO downloadDO = null;
            MapSetStringKey key;
            synchronized (downloadMap) {
                for (int i = 0; i < size; i++) {
                    e = (Element) nodes.item(i);
                    id = Integer.parseInt(e.getAttribute("id"));
                    key = new MapSetStringKey(id);
                    if (downloadMap.containsKey(key)) {
                        downloadDO = (DownloadDO) downloadMap.get(key);
                        downloadDO.setShareId(Integer.parseInt(e.getAttribute(
                            "shareid")));
                        downloadDO.setHash(e.getAttribute("hash"));
                        downloadDO.setGroesse(Long.parseLong(e.getAttribute(
                            "size")));
                        downloadDO.setReady(Long.parseLong(e.getAttribute(
                            "ready")));
                        temp = e.getAttribute("status");
                        downloadDO.setStatus(Integer.parseInt(temp));
                        downloadDO.setFilename(e.getAttribute("filename"));
                        downloadDO.setTargetDirectory(e.getAttribute(
                            "targetdirectory"));
                        temp = e.getAttribute("powerdownload");
                        downloadDO.setPowerDownload(Integer.parseInt(temp));
                        temp = e.getAttribute("temporaryfilenumber");
                        downloadDO.setTemporaryFileNumber(Integer.parseInt(temp));
                    }
                    else {
                        shareid = Integer.parseInt(e.getAttribute("shareid"));
                        hash = e.getAttribute("hash");
                        fileSize = Long.parseLong(e.getAttribute("size"));
                        sizeReady = Long.parseLong(e.getAttribute("ready"));
                        temp = e.getAttribute("status");
                        status = Integer.parseInt(temp);
                        filename = e.getAttribute("filename");
                        targetDirectory = e.getAttribute("targetdirectory");
                        temp = e.getAttribute("powerdownload");
                        powerDownload = Integer.parseInt(temp);
                        temp = e.getAttribute("temporaryfilenumber");
                        temporaryFileNumber = Integer.parseInt(temp);

                        downloadDO = new DownloadDO(id, shareid, hash, fileSize,
                            sizeReady, status, filename,
                            targetDirectory, powerDownload, temporaryFileNumber);

                        downloadMap.put(new MapSetStringKey(id), downloadDO);
                    }
                }
            }
            int directstate;
            nodes = document.getElementsByTagName("user");
            size = nodes.getLength();
            int downloadFrom;
            int downloadTo;
            int actualDownloadPosition;
            int speed;
            int downloadId;
            Version version = null;
            String versionNr = null;
            String nickname = null;
            int queuePosition;
            int os;
            DownloadSourceDO downloadSourceDO = null;
            for (int i = 0; i < size; i++) {
                e = (Element) nodes.item(i);
                id = Integer.parseInt(e.getAttribute("id"));
                temp = e.getAttribute("status");
                status = Integer.parseInt(temp);
                temp = e.getAttribute("directstate");
                directstate = Integer.parseInt(temp);
                if (status == DownloadSourceDO.UEBERTRAGUNG) {
                    temp = e.getAttribute("downloadfrom");
                    downloadFrom = Integer.parseInt(temp);
                    temp = e.getAttribute("downloadto");
                    downloadTo = Integer.parseInt(temp);
                    temp = e.getAttribute("actualdownloadposition");
                    actualDownloadPosition = Integer.parseInt(temp);
                    temp = e.getAttribute("speed");
                    speed = Integer.parseInt(temp);
                }
                else {
                    downloadFrom = -1;
                    downloadTo = -1;
                    actualDownloadPosition = -1;
                    speed = 0;
                }
                versionNr = e.getAttribute("version");
                if (versionNr.compareToIgnoreCase("0.0.0.0") == 0) {
                    version = null;
                }
                else {
                    temp = e.getAttribute("operatingsystem");
                    os = Integer.parseInt(temp);
                    version = new Version(versionNr, os);
                }
                temp = e.getAttribute("queueposition");
                queuePosition = Integer.parseInt(temp);
                temp = e.getAttribute("powerdownload");
                powerDownload = Integer.parseInt(temp);
                filename = e.getAttribute("filename");
                nickname = e.getAttribute("nickname");
                temp = e.getAttribute("downloadid");
                downloadId = Integer.parseInt(temp);
                key = new MapSetStringKey(downloadId);
                downloadDO = (DownloadDO) downloadMap.get(key);
                if (downloadDO != null) {
                    downloadSourceDO = downloadDO.getSourceById(id);
                    if (downloadSourceDO != null) {
                        downloadSourceDO.setActualDownloadPosition(
                            actualDownloadPosition);
                        downloadSourceDO.setDirectstate(directstate);
                        downloadSourceDO.setDownloadFrom(downloadFrom);
                        downloadSourceDO.setDownloadTo(downloadTo);
                        downloadSourceDO.setFilename(filename);
                        downloadSourceDO.setNickname(nickname);
                        downloadSourceDO.setPowerDownload(powerDownload);
                        downloadSourceDO.setQueuePosition(queuePosition);
                        downloadSourceDO.setSpeed(speed);
                        downloadSourceDO.setStatus(status);
                        downloadSourceDO.setVersion(version);
                        downloadSourceDO.setDownloadId(downloadId);
                    }
                    else {
                        downloadSourceDO = new DownloadSourceDO(id, status,
                            directstate, downloadFrom, downloadTo,
                            actualDownloadPosition, speed, version,
                            queuePosition,
                            powerDownload, filename, nickname, downloadId);
                        downloadDO.addSource(downloadSourceDO);
                        sourcenZuDownloads.put(new MapSetStringKey(id),
                                               downloadDO);
                    }
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void updateUploads() {
        try {
            NodeList nodes = document.getElementsByTagName("upload");
            int size = nodes.getLength();
            Element e = null;
            int shareId;
            UploadDO upload = null;
            int id;
            int os;
            String versionsNr = null;
            Version version = null;
            int prioritaet;
            String nick = null;
            String status = null;
            long uploadFrom;
            long uploadTo;
            long actualUploadPos;
            int speed;
            MapSetStringKey idKey = null;
            synchronized (uploadMap) {
                ShareDO shareDO;
                HashMap share = null;
                for (int i = 0; i < size; i++) {
                    e = (Element) nodes.item(i);
                    id = Integer.parseInt(e.getAttribute("id"));
                    idKey = new MapSetStringKey(id);
                    if (uploadMap.containsKey(idKey)) {
                        upload = (UploadDO) uploadMap.get(idKey);
                        upload.setShareFileID(Integer.parseInt(e.getAttribute(
                            "shareid")));
                        upload.setPrioritaet(Integer.parseInt(e.getAttribute(
                            "priority")));
                        upload.setNick(e.getAttribute("nick"));
                        upload.setStatus(Integer.parseInt(e.getAttribute(
                            "status")));
                        upload.setUploadFrom(Long.parseLong(e.getAttribute(
                            "uploadfrom")));
                        upload.setUploadTo(Long.parseLong(e.getAttribute(
                            "uploadto")));
                        upload.setActualUploadPosition(Long.parseLong(e.
                            getAttribute("actualuploadposition")));
                        upload.setSpeed(Integer.parseInt(e.getAttribute("speed")));
                    }
                    else {
                        shareId = Integer.parseInt(e.getAttribute("shareid"));
                        versionsNr = e.getAttribute("version");
                        if (versionsNr.compareToIgnoreCase("0.0.0.0") == 0) {
                            version = null;
                        }
                        else {
                            os = Integer.parseInt(e.getAttribute(
                                "operatingsystem"));
                            version = new Version(versionsNr, os);
                        }
                        prioritaet = Integer.parseInt(e.getAttribute("priority"));
                        nick = e.getAttribute("nick");
                        status = e.getAttribute("status");
                        uploadFrom = Long.parseLong(e.getAttribute("uploadfrom"));
                        uploadTo = Long.parseLong(e.getAttribute("uploadto"));
                        actualUploadPos = Long.parseLong(e.getAttribute(
                            "actualuploadposition"));
                        speed = Integer.parseInt(e.getAttribute("speed"));
                        upload = new UploadDO(id, shareId, version, status,
                                              nick,
                                              uploadFrom, uploadTo,
                                              actualUploadPos,
                                              speed, prioritaet);
                        if (share == null) {
                            share = ApplejuiceFassade.getInstance().getShare(false);
                        }
                        shareDO = (ShareDO) share.get(new MapSetStringKey(
                            shareId));
                        if (upload != null && shareDO != null) {
                            //wenns die passende Sharedatei aus irgendeinem Grund nicht geben sollte,
                            //wird dieser Upload auch nicht angezeigt
                            upload.setDateiName(shareDO.getShortfilename());
                            uploadMap.put(idKey, upload);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void updateServer() {
        try {
            NodeList nodes = document.getElementsByTagName("server");
            int size = nodes.getLength();
            Element e = null;
            String id_key = null;
            int id;
            String name = null;
            String host = null;
            long lastseen;
            int versuche;
            String port = null;
            ServerDO server = null;
            MapSetStringKey key;
            ServerDO serverDO;
            for (int i = 0; i < size; i++) {
                e = (Element) nodes.item(i);
                id_key = e.getAttribute("id");
                key = new MapSetStringKey(id_key);
                if (serverMap.containsKey(key)) {
                    serverDO = (ServerDO) serverMap.get(key);
                    serverDO.setName(e.getAttribute("name"));
                    serverDO.setHost(e.getAttribute("host"));
                    serverDO.setTimeLastSeen(Long.parseLong(e.getAttribute(
                        "lastseen")));
                    serverDO.setVersuche(Integer.parseInt(e.getAttribute("connectiontry")));
                    serverDO.setPort(e.getAttribute("port"));
                }
                else {
                    id = Integer.parseInt(id_key);
                    name = e.getAttribute("name");
                    host = e.getAttribute("host");
                    lastseen = Long.parseLong(e.getAttribute("lastseen"));
                    port = e.getAttribute("port");
                    versuche = Integer.parseInt(e.getAttribute("connectiontry"));
                    server = new ServerDO(id, name, host, port, lastseen, versuche);
                    serverMap.put(new MapSetStringKey(id_key), server);
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private void updateNetworkInfo() {
        try {
            NodeList nodes = document.getElementsByTagName("networkinfo");
            if (nodes.getLength() == 0) {
                return; //Keine Verï¿½nderung seit dem letzten Abrufen
            }
            Element e = (Element) nodes.item(0); //Es gibt nur ein Netzerkinfo-Element
            String users = e.getAttribute("users");
            String dateien = e.getAttribute("files");
            String dateigroesse = e.getAttribute("filesize");
            int tryConnectToServer = Integer.parseInt(e.getAttribute(
                "tryconnecttoserver"));
            int connectedWithServerId = Integer.parseInt(e.getAttribute(
                "connectedwithserverid"));
            boolean firewalled = (e.getAttribute("firewalled").
                                  compareToIgnoreCase(
                "true") == 0) ? true : false;
            String externeIP = e.getAttribute("ip");
            if (this.tryConnectToServer != tryConnectToServer) {
                Object alterServer = serverMap.get(new MapSetStringKey(this.
                    tryConnectToServer));
                if (alterServer != null) {
                    ( (ServerDO) alterServer).setTryConnect(false);
                }
                if (tryConnectToServer != -1) {
                    ServerDO serverDO = (ServerDO) serverMap.get(new
                        MapSetStringKey(tryConnectToServer));
                    serverDO.setTryConnect(true);
                }
                this.tryConnectToServer = tryConnectToServer;
            }
            //if (this.connectedWithServerId != connectedWithServerId){
            Object alterServer = serverMap.get(new MapSetStringKey(this.
                connectedWithServerId));
            if (alterServer != null) {
                ( (ServerDO) alterServer).setConnected(false);
            }
            if (connectedWithServerId != -1) {
                ServerDO serverDO = (ServerDO) serverMap.get(new
                    MapSetStringKey(connectedWithServerId));
                serverDO.setConnected(true);
            }
            this.connectedWithServerId = connectedWithServerId;
            //}
            netInfo = new NetworkInfo(users, dateien, dateigroesse, firewalled,
                                      externeIP, tryConnectToServer,
                                      connectedWithServerId);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }
}