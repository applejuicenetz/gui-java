package de.applejuicenet.client.fassade.controller.xml;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.controller.DataPropertyChangeInformer;
import de.applejuicenet.client.fassade.controller.xml.SearchDO.SearchEntryDO;
import de.applejuicenet.client.fassade.controller.xml.SearchDO.SearchEntryDO.FileNameDO;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.fassade.exception.CoreLostException;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.NetworkInfo;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/ModifiedXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r [aj@tkl-soft.de]
 * 
 */

public class ModifiedXMLHolder extends DefaultHandler {

	private final CoreConnectionSettingsHolder coreHolder;

	private Map<String, Download> sourcenZuDownloads = new HashMap<String, Download>();
	private XMLReader xr = null;
	private Map<String, Server> serverMap = new HashMap<String, Server>();
	private Map<String, Download> downloadMap = new HashMap<String, Download>();
	private Map<String, Upload> uploadMap = new HashMap<String, Upload>();
	private Map<String, SearchDO> searchMap = new HashMap<String, SearchDO>();
	private NetworkInfo netInfo;
	private InformationDO information;
	private int count = 0;
	private String filter = "";
	private String sessionKontext = null;
	private SecurerXMLHolder securerHolder;
	private int connectedWithServerId = -1;
	private int tryConnectToServer = -1;
	private boolean reloadInProgress = false;
	private String zipMode = "";
	private String xmlCommand;
	private long timestamp = 0;
	private int checkCount = 0;
	private CharArrayWriter contents = new CharArrayWriter();
	private Map<String, String> attributes = new HashMap<String, String>();
	private Vector<DownloadDataPropertyChangeEvent> downloadEvents = 
		new Vector<DownloadDataPropertyChangeEvent>();
	private boolean downloadSourceEvent;
	private DataPropertyChangeInformer downloadPropertyChangeInformer;
	private ApplejuiceFassade ajFassade;

	public ModifiedXMLHolder(CoreConnectionSettingsHolder coreHolder, ApplejuiceFassade ajFassade) {
		this.coreHolder = coreHolder;
		this.ajFassade = ajFassade;
		try {
			securerHolder = new SecurerXMLHolder(coreHolder);
			init();
			xmlCommand = "/xml/modified.xml";
			Class parser = SAXParser.class;
			xr = XMLReaderFactory.createXMLReader(parser.getName());
			xr.setContentHandler(this);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public DataPropertyChangeInformer getDownloadPropertyChangeInformer() {
		return downloadPropertyChangeInformer;
	}

	private void init() {
		downloadPropertyChangeInformer = new DataPropertyChangeInformer();
		if (!coreHolder.isLocalhost()) {
			zipMode = "mode=zip&";
		}
	}

	public void reinit() {
		init();
	}

	public Map<String, Server> getServer() {
		return serverMap;
	}

	public Map<String, Upload> getUploads() {
		return uploadMap;
	}

	public Map<String, Download> getDownloads() {
		return downloadMap;
	}

	public Map<String, SearchDO> getSearchs() {
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
		} else {
			switch (count) {
			// lazy loading
			case 0: {
				count++;
				filter = "&filter=ids;down;uploads;server;informations;search&mode=zip";
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

	public Information getInformation() {
		return information;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Map<String, Long> getSpeeds() {
		Map<String, Long> speeds = new HashMap<String, Long>();
		if (information != null) {
			speeds.put("uploadspeed", new Long(information.getUploadSpeed()));
			speeds.put("downloadspeed",
					new Long(information.getDownloadSpeed()));
			speeds.put("credits", new Long(information.getCredits()));
			speeds.put("sessionupload",
					new Long(information.getSessionUpload()));
			speeds.put("sessiondownload", new Long(information
					.getSessionDownload()));
		}
		return speeds;
	}

	private void checkInformationAttributes(Attributes attr) {
		if (information == null) {
			information = new InformationDO();
		}
		for (int i = 0; i < attr.getLength(); i++) {
			if (attr.getLocalName(i).equals("credits")) {
				information.setCredits(Long.parseLong(attr.getValue(i)));
			} else if (attr.getLocalName(i).equals("sessionupload")) {
				information.setSessionUpload(Long.parseLong(attr.getValue(i)));
			} else if (attr.getLocalName(i).equals("sessiondownload")) {
				information
						.setSessionDownload(Long.parseLong(attr.getValue(i)));
			} else if (attr.getLocalName(i).equals("uploadspeed")) {
				information.setUploadSpeed(Long.parseLong(attr.getValue(i)));
			} else if (attr.getLocalName(i).equals("downloadspeed")) {
				information.setDownloadSpeed(Long.parseLong(attr.getValue(i)));
			} else if (attr.getLocalName(i).equals("openconnections")) {
				information
						.setOpenConnections(Long.parseLong(attr.getValue(i)));
			} else if (attr.getLocalName(i).equals("maxuploadpositions")) {
				information.setMaxUploadPositions(Long.parseLong(attr
						.getValue(i)));
			}
		}
	}

	private void checkNetworkMap(NetworkInfo netInfo, Map userAttributes) {
		netInfo.setAjUserGesamt(Long.parseLong((String) userAttributes
				.get("users")));
		netInfo.setAjAnzahlDateien(Long.parseLong((String) userAttributes
				.get("files")));
		netInfo.setAjGesamtShare((String) userAttributes.get("filesize"));
		netInfo.setFirewalled(((String) userAttributes.get("firewalled"))
				.equals("true"));
		netInfo.setExterneIP((String) userAttributes.get("ip"));
		netInfo.setTryConnectToServer(Integer.parseInt((String) userAttributes
				.get("tryconnecttoserver")));
		netInfo
				.setConnectedWithServerId(Integer
						.parseInt((String) userAttributes
								.get("connectedwithserverid")));
		netInfo.setConnectionTime(Long.parseLong((String) userAttributes
				.get("connectedsince")));
	}

	private void checkNetworkInfoAttributes(Attributes attr) {
		if (netInfo == null) {
			netInfo = new NetworkInfo();
		}
		attributes.clear();
		for (int i = 0; i < attr.getLength(); i++) {
			attributes.put(attr.getLocalName(i), attr.getValue(i));
		}
		checkNetworkMap(netInfo, attributes);
		attributes.clear();
	}

	private void checkServerMap(ServerDO serverDO, Map userAttributes) {
		serverDO.setName((String) userAttributes.get("name"));
		serverDO.setHost((String) userAttributes.get("host"));
		serverDO.setTimeLastSeen(Long.parseLong((String) userAttributes
				.get("lastseen")));
		serverDO.setPort((String) userAttributes.get("port"));
		serverDO.setVersuche(Integer.parseInt((String) userAttributes
				.get("connectiontry")));
	}

	private void checkServerAttributes(Attributes attr) {
		attributes.clear();
		for (int i = 0; i < attr.getLength(); i++) {
			attributes.put(attr.getLocalName(i), attr.getValue(i));
		}
		int id = Integer.parseInt((String) attributes.get("id"));
		String key = Integer.toString(id);
		ServerDO serverDO;
		if (serverMap.containsKey(key)) {
			serverDO = (ServerDO)serverMap.get(key);
		} else {
			serverDO = new ServerDO(id);
			serverMap.put(key, serverDO);
		}
		checkServerMap(serverDO, attributes);
		attributes.clear();
	}

	private Map shareMap = null;

	private void checkUploadMap(UploadDO uploadDO, Map userAttributes) {
		uploadDO.setShareFileID(Integer.parseInt((String) userAttributes
				.get("shareid")));
		uploadDO.setStatus(Integer.parseInt((String) userAttributes
				.get("status")));
		uploadDO.setDirectState(Integer.parseInt((String) userAttributes
				.get("directstate")));
		uploadDO.setPrioritaet(Integer.parseInt((String) userAttributes
				.get("priority")));
		uploadDO.setUploadFrom(Integer.parseInt((String) userAttributes
				.get("uploadfrom")));
		uploadDO.setActualUploadPosition(Integer
				.parseInt((String) userAttributes.get("actualuploadposition")));
		uploadDO.setUploadTo(Integer.parseInt((String) userAttributes
				.get("uploadto")));
		uploadDO.setSpeed(Integer
				.parseInt((String) userAttributes.get("speed")));
		uploadDO.setNick((String) userAttributes.get("nick"));
		uploadDO.setLastConnection(Long.parseLong((String) userAttributes
				.get("lastconnection")));
		uploadDO.setLoaded(Double.parseDouble((String) userAttributes
				.get("loaded")));
		String versionNr = (String) userAttributes.get("version");
		int os = Integer.parseInt((String) userAttributes
				.get("operatingsystem"));
		if (!versionNr.equals("0.0.0.0") && os != -1) {
			VersionDO version = new VersionDO(versionNr, os);
			uploadDO.setVersion(version);
		}
	}

	private void checkUploadAttributes(Attributes attr) {
		attributes.clear();
		for (int i = 0; i < attr.getLength(); i++) {
			attributes.put(attr.getLocalName(i), attr.getValue(i));
		}
		int id = Integer.parseInt((String) attributes.get("id"));

		String key = Integer.toString(id);
		UploadDO uploadDO;
		if (uploadMap.containsKey(key)) {
			uploadDO = (UploadDO)uploadMap.get(key);
		} else {
			uploadDO = new UploadDO(id);
			uploadMap.put(key, uploadDO);
		}
		checkUploadMap(uploadDO, attributes);
		attributes.clear();
		if (shareMap == null){ 
			shareMap = ajFassade.getShare(false); 
		}
		ShareDO shareDO = (ShareDO) shareMap.get(uploadDO
				.getShareFileIDAsString());
		if (shareDO != null) {
			uploadDO.setDateiName(shareDO.getShortfilename());
		} else {
			// wenns die passende Sharedatei aus irgendeinem Grund nicht geben
			// sollte,
			// wird dieser Upload auch nicht angezeigt
			uploadMap.remove(key);
		}
	}

	private void checkUserMap(DownloadSourceDO downloadSourceDO,
			Map userAttributes) {
		downloadSourceDO.setStatus(Integer.parseInt((String) userAttributes
				.get("status")));
		downloadSourceDO.setDirectstate(Integer
				.parseInt((String) userAttributes.get("directstate")));
		downloadSourceDO.setDownloadFrom(Integer
				.parseInt((String) userAttributes.get("downloadfrom")));
		downloadSourceDO.setDownloadTo(Integer.parseInt((String) userAttributes
				.get("downloadto")));
		downloadSourceDO
				.setActualDownloadPosition(Integer
						.parseInt((String) userAttributes
								.get("actualdownloadposition")));
		downloadSourceDO.setSpeed(Integer.parseInt((String) userAttributes
				.get("speed")));
		downloadSourceDO.setQueuePosition(Integer
				.parseInt((String) userAttributes.get("queueposition")));
		downloadSourceDO.setPowerDownload(Integer
				.parseInt((String) userAttributes.get("powerdownload")));
		downloadSourceDO.setFilename((String) userAttributes.get("filename"));
		downloadSourceDO.setNickname((String) userAttributes.get("nickname"));
		String versionNr = (String) userAttributes.get("version");
		downloadSourceDO.setHerkunft(
				Integer.parseInt((String)userAttributes.get("source")));
		int os = Integer.parseInt((String) userAttributes
				.get("operatingsystem"));
		if (!versionNr.equals("0.0.0.0") && os != -1) {
			VersionDO version = new VersionDO(versionNr, os);
			downloadSourceDO.setVersion(version);
		}
	}

	private void checkUserAttributes(Attributes attr) {
		attributes.clear();
		for (int i = 0; i < attr.getLength(); i++) {
			attributes.put(attr.getLocalName(i), attr.getValue(i));
		}
		int id = Integer.parseInt((String) attributes.get("id"));
		int downloadId = Integer
				.parseInt((String) attributes.get("downloadid"));

		String downloadKey = Integer.toString(downloadId);
		DownloadDO downloadDO = (DownloadDO)downloadMap.get(downloadKey);
		DownloadSourceDO downloadSourceDO = null;
		if (downloadDO != null) {
			downloadSourceDO = downloadDO.getSourceById(id);
			if (downloadSourceDO == null) {
				downloadSourceDO = new DownloadSourceDO(id);
				downloadDO.addSource(downloadSourceDO);
				sourcenZuDownloads.put(Integer.toString(id), downloadDO);
			}
		} else {
			downloadSourceDO = new DownloadSourceDO(id);
			downloadSourcesToDo.add(downloadSourceDO);
		}
		downloadSourceDO.setDownloadId(downloadId);
		checkUserMap(downloadSourceDO, attributes);
		attributes.clear();
		downloadSourceEvent = true;
	}

	private void checkDownloadMap(DownloadDO downloadDO, Map userAttributes,
			boolean newDownload) {
        downloadDO.setShareId(Integer.parseInt((String) userAttributes
				.get("shareid")));
        downloadDO.setGroesse(Long.parseLong((String) userAttributes
				.get("size")));
        downloadDO.setHash((String) userAttributes.get("hash"));
        downloadDO.setTemporaryFileNumber(Integer
				.parseInt((String) userAttributes.get("temporaryfilenumber")));
		if (newDownload) {
            downloadDO.setStatus(Integer.parseInt((String) userAttributes
					.get("status")));
            downloadDO.setFilename((String) userAttributes.get("filename"));
            downloadDO.setTargetDirectory((String) userAttributes
					.get("targetdirectory"));
            downloadDO.setPowerDownload(Integer
					.parseInt((String) userAttributes.get("powerdownload")));
            downloadDO.setReady(Long.parseLong((String) userAttributes
					.get("ready")));
		} else {
			int tmpInt = Integer
					.parseInt((String) userAttributes.get("status"));
			String old;
			if (tmpInt != downloadDO.getStatus()) {
				old = Integer.toString(downloadDO.getStatus());
				downloadDO.setStatus(tmpInt);
				downloadEvents.add(new DownloadDataPropertyChangeEvent(
						downloadDO,
						DownloadDataPropertyChangeEvent.STATUS_CHANGED, old,
						Integer.toString(tmpInt)));
			}
			String tmpString = (String) userAttributes.get("filename");
			old = downloadDO.getFilename();
			if (old == null || !old.equals(tmpString)) {
                downloadDO.setFilename(tmpString);
				downloadEvents.add(new DownloadDataPropertyChangeEvent(
						downloadDO,
						DownloadDataPropertyChangeEvent.FILENAME_CHANGED, old,
						tmpString));
			}
			tmpString = (String) userAttributes.get("targetdirectory");
			old = downloadDO.getTargetDirectory();
			if (old == null || !old.equals(tmpString)) {
                downloadDO.setTargetDirectory(tmpString);
				downloadEvents.add(new DownloadDataPropertyChangeEvent(
						downloadDO,
						DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED, old,
						tmpString));
			}
			tmpInt = Integer.parseInt((String) userAttributes
					.get("powerdownload"));
			if (tmpInt != downloadDO.getPowerDownload()) {
				old = Integer.toString(downloadDO.getPowerDownload());
				downloadDO.setPowerDownload(tmpInt);
				downloadEvents.add(new DownloadDataPropertyChangeEvent(
						downloadDO,
						DownloadDataPropertyChangeEvent.PWDL_CHANGED, old,
						Integer.toString(tmpInt)));
			}
			long tmpLong = Long.parseLong((String) userAttributes.get("ready"));
			if (tmpLong != downloadDO.getReady()) {
				old = Long.toString(downloadDO.getReady());
				downloadDO.setReady(tmpLong);
				downloadEvents.add(new DownloadDataPropertyChangeEvent(
						downloadDO,
						DownloadDataPropertyChangeEvent.READY_CHANGED, old,
						Long.toString(tmpLong)));
			}
		}
	}

	private void checkDownloadAttributes(Attributes attr) {
		attributes.clear();
		for (int i = 0; i < attr.getLength(); i++) {
			attributes.put(attr.getLocalName(i), attr.getValue(i));
		}
		int id = Integer.parseInt((String) attributes.get("id"));
		String key = Integer.toString(id);
		DownloadDO downloadDO;
		boolean newDownload;
		if (downloadMap.containsKey(key)) {
			downloadDO = (DownloadDO)downloadMap.get(key);
			newDownload = false;
		} else {
			downloadDO = new DownloadDO(id);
			downloadMap.put(key, downloadDO);
			newDownload = true;
		}
		checkDownloadMap(downloadDO, attributes, newDownload);
		attributes.clear();
		if (newDownload) {
			downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap,
					DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED, null,
					downloadDO));
		}
	}

	private void removeDownload(String id) {
		Download download = downloadMap.get(sourcenZuDownloads
				.get(id));
		if (download != null) {
			for (DownloadSource curDownloadSource : download.getSources()) {
				sourcenZuDownloads.remove(Integer.toString(curDownloadSource
						.getId()));
			}
		}
		downloadMap.remove(id);
		downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap,
				DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED, download, null));
	}

	private void checkRemovedAttributes(Attributes attr) {
		for (int i = 0; i < attr.getLength(); i++) {
			if (attr.getLocalName(i).equals("id")) {
				String id = attr.getValue(i);
				removeDownload(id);
				uploadMap.remove(id);
				serverMap.remove(id);
				if (sourcenZuDownloads.containsKey(id)) {
					DownloadDO downloadDO = (DownloadDO) sourcenZuDownloads
							.get(id);
					downloadDO.removeSource(id);
					sourcenZuDownloads.remove(id);
					downloadSourceEvent = true;
					continue;
				} else if (searchMap.containsKey(id)) {
					searchMap.remove(id);
					Search.currentSearchCount = searchMap.size();
					continue;
				}
			}
		}
	}

	private void checkSearchMap(SearchDO aSearch, Map userAttributes) {
		aSearch.setSuchText((String) userAttributes.get("searchtext"));
		aSearch.setOffeneSuchen(Integer.parseInt((String) userAttributes
				.get("opensearches")));
		aSearch.setGefundenDateien(Integer.parseInt((String) userAttributes
				.get("foundfiles")));
		aSearch.setDurchsuchteClients(Integer.parseInt((String) userAttributes
				.get("sumsearches")));
		aSearch.setRunning(((String) userAttributes.get("running"))
				.equals("true"));
	}

	private void checkSearchAttributes(Attributes attr) {
		attributes.clear();
		for (int i = 0; i < attr.getLength(); i++) {
			attributes.put(attr.getLocalName(i), attr.getValue(i));
		}
		int id = Integer.parseInt((String) attributes.get("id"));
		String key = Integer.toString(id);
		SearchDO aSearch;
		if (searchMap.containsKey(key)) {
			aSearch = searchMap.get(key);
		} else {
			aSearch = new SearchDO(id);
			searchMap.put(key, aSearch);
		}
		checkSearchMap(aSearch, attributes);
		attributes.clear();
	}

	private SearchEntryDO tmpSearchEntry = null;

	private void checkSearchEntryAttributes(Attributes attr) {
		int searchId = -1;
		int id = -1;
		String checksum = "";
		long groesse = -1;

		for (int i = 0; i < attr.getLength(); i++) {
			if (attr.getLocalName(i).equals("id")) {
				id = Integer.parseInt(attr.getValue(i));
			} else if (attr.getLocalName(i).equals("searchid")) {
				searchId = Integer.parseInt(attr.getValue(i));
			} else if (attr.getLocalName(i).equals("checksum")) {
				checksum = attr.getValue(i);
			} else if (attr.getLocalName(i).equals("size")) {
				groesse = Long.parseLong(attr.getValue(i));
			}
		}
		String key = Integer.toString(searchId);
		SearchDO aSearch;
		if (searchMap.containsKey(key)) {
			aSearch = (SearchDO) searchMap.get(key);
			tmpSearchEntry = (SearchEntryDO)aSearch.getSearchEntryById(id);
			if (tmpSearchEntry == null) {
				tmpSearchEntry = aSearch.new SearchEntryDO(id, searchId,
						checksum, groesse);
				aSearch.addSearchEntry(tmpSearchEntry);
			}
		} else {
			tmpSearchEntry = new SearchDO(-1).new SearchEntryDO(id, searchId,
					checksum, groesse);
			searchEntriesToDo.add(tmpSearchEntry);
		}
	}

	private Set<SearchEntryDO> searchEntriesToDo = new HashSet<SearchEntryDO>();

	private Set<DownloadSourceDO> downloadSourcesToDo = new HashSet<DownloadSourceDO>();

	private void checkSearchEntryFilenameAttributes(Attributes attr) {
		if (tmpSearchEntry == null) {
			return;
		}
		int haeufigkeit = -1;
		String dateiName = "";

		for (int i = 0; i < attr.getLength(); i++) {
			if (attr.getLocalName(i).equals("name")) {
				dateiName = attr.getValue(i);
			} else if (attr.getLocalName(i).equals("user")) {
				haeufigkeit = Integer.parseInt(attr.getValue(i));
			}
		}
		FileNameDO filename = tmpSearchEntry.new FileNameDO(dateiName, haeufigkeit);
		tmpSearchEntry.addFileName(filename);
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		contents.reset();
		if (localName.equals("download")) {
			checkDownloadAttributes(attr);
		} else if (localName.equals("upload")) {
			checkUploadAttributes(attr);
		} else if (localName.equals("server")) {
			checkServerAttributes(attr);
		} else if (localName.equals("information")) {
			checkInformationAttributes(attr);
		} else if (localName.equals("networkinfo")) {
			checkNetworkInfoAttributes(attr);
		} else if (localName.equals("object")) {
			checkRemovedAttributes(attr);
		} else if (localName.equals("user")) {
			checkUserAttributes(attr);
		} else if (localName.equals("search")) {
			checkSearchAttributes(attr);
		} else if (localName.equals("searchentry")) {
			checkSearchEntryAttributes(attr);
		} else if (localName.equals("filename")) {
			checkSearchEntryFilenameAttributes(attr);
		}
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("searchentry")) {
			tmpSearchEntry = null;
		} else if (localName.equals("welcomemessage")) {
			if (netInfo == null) {
				netInfo = new NetworkInfo();
			}
			String tmp = contents.toString();
			netInfo.setWelcomeMessage(tmp);
		} else if (checkCount > 1) {
			if (localName.equals("time")) {
				timestamp = Long.parseLong(contents.toString());
			}
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		contents.write(ch, start, length);
	}

	private String getXMLString(String parameters) throws Exception {
		String xmlData = null;
		String command = xmlCommand + "?";
		if (parameters.indexOf("mode=zip") == -1) {
			command += zipMode;
		}
		command += "password=" + coreHolder.getCorePassword() + "&timestamp="
				+ timestamp + parameters + sessionKontext;
		xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(),
				coreHolder.getCorePort(), HtmlLoader.GET, command);
		if (xmlData.length() == 0) {
			throw new IllegalArgumentException();
		}
		return xmlData;
	}

	private int updateTryConnect() {
		int verbindungsStatus = Information.NICHT_VERBUNDEN;
		ServerDO serverDO = null;
		if (tryConnectToServer != -1) {
			Object alterServer = serverMap.get(Integer
					.toString(tryConnectToServer));
			if (alterServer != null) {
				((ServerDO) alterServer).setTryConnect(false);
			}
			information.setServer(null);
		}
		if (netInfo.getTryConnectToServer() != -1) {
			serverDO = (ServerDO) serverMap.get(Integer.toString(netInfo
					.getTryConnectToServer()));
			verbindungsStatus = Information.VERSUCHE_ZU_VERBINDEN;
			if (serverDO != null) {
				serverDO.setTryConnect(true);
			}
		}
		tryConnectToServer = netInfo.getTryConnectToServer();
		information.setServer(serverDO);
		information.setVerbindungsStatus(verbindungsStatus);
		return verbindungsStatus;
	}

	private int updateConnected() {
		int verbindungsStatus = Information.NICHT_VERBUNDEN;
		ServerDO serverDO = null;
		if (connectedWithServerId != -1) {
			Object alterServer = serverMap.get(Integer
					.toString(connectedWithServerId));
			if (alterServer != null) {
				((ServerDO) alterServer).setConnected(false);
			}
			information.setServer(null);
		}
		if (netInfo.getConnectedWithServerId() != -1) {
			serverDO = (ServerDO) serverMap.get(Integer.toString(netInfo
					.getConnectedWithServerId()));
			verbindungsStatus = Information.VERBUNDEN;
			if (serverDO != null) {
				serverDO.setConnected(true);
			}
		}
		connectedWithServerId = netInfo.getConnectedWithServerId();
		information.setServer(serverDO);
		information.setVerbindungsStatus(verbindungsStatus);
		return verbindungsStatus;
	}

	private void parseRest() {
		int verbindungsStatus = Information.NICHT_VERBUNDEN;
		if (tryConnectToServer != netInfo.getTryConnectToServer()) {
			verbindungsStatus = updateTryConnect();
		}
		if (connectedWithServerId != netInfo.getConnectedWithServerId()) {
			verbindungsStatus = updateConnected();
		}
		information.setExterneIP(netInfo.getExterneIP());

		if (searchEntriesToDo.size() > 0) {
			SearchDO aSearch;
			SearchEntryDO searchEntry;
			Iterator it = searchEntriesToDo.iterator();
			while (it.hasNext()) {
				searchEntry = (SearchEntryDO) it.next();
				if (searchMap.containsKey(Integer.toString(searchEntry
						.getSearchId()))) {
					aSearch = (SearchDO) searchMap.get(Integer
							.toString(searchEntry.getSearchId()));
					aSearch.addSearchEntry(searchEntry);
				}

			}
			searchEntriesToDo.clear();
		}
		if (downloadSourcesToDo.size() > 0) {
			DownloadDO downloadDO;
			DownloadSourceDO downloadSourceDO;
			Iterator it = downloadSourcesToDo.iterator();
			while (it.hasNext()) {
				downloadSourceDO = (DownloadSourceDO) it.next();
				String downloadKey = Integer.toString(downloadSourceDO
						.getDownloadId());
				if (downloadMap.containsKey(downloadKey)) {
					downloadDO = (DownloadDO) downloadMap.get(downloadKey);
					downloadDO.addSource(downloadSourceDO);
					sourcenZuDownloads.put(Integer.toString(downloadSourceDO
							.getId()), downloadDO);
				}
			}
			downloadSourcesToDo.clear();
		}
	}

	private void checkForValidSession() {
		if (sessionKontext == null) {
			SessionXMLHolder sessionHolder = new SessionXMLHolder(coreHolder);
			String sessionId = sessionHolder.getNewSessionId();
			sessionKontext = "&session=" + sessionId;
		}
	}

	private void checkForValidResult(String xmlString, Securer securer) {
		if (xmlString.indexOf("wrong password") != -1) {
			if (!securer.isInterrupted()) {
				securer.interrupt();
			}
			// todo ordentliche Exception werfen
			throw new RuntimeException(
					"Das Passwort wurde coreseitig geaendert.\r\nDas GUI wird beendet.");
		}
	}

	public synchronized void reload() {
		boolean reloadSession = false;
		Securer securer = new Securer();
		try {
			reloadInProgress = true;
			checkForValidSession();
			securer.start();
			String xmlString = getXMLString(filter);
			if (!securer.isInterrupted()) {
				securer.interrupt();
			}
			downloadEvents.clear();
			if (filter.indexOf("down;") != -1) {
				downloadEvents.add(new DownloadDataPropertyChangeEvent(
						downloadMap,
						DownloadDataPropertyChangeEvent.DOWNLOADMAP_CHECKED, 
						null, null));
			}
			downloadSourceEvent = false;
			xr.parse(new InputSource(new StringReader(xmlString)));
			parseRest();
			if (!securer.isInterrupted()) {
				securer.interrupt();
			}
			if (!securer.isOK()) {
				checkForValidSession();
			}
			if (checkCount < 2) {
				checkCount++;
			}
			securer = null;
			if (downloadSourceEvent) {
				downloadEvents.add(new DownloadDataPropertyChangeEvent(
						downloadMap,
						DownloadDataPropertyChangeEvent.A_SOURCE_CHANGED, null,
						null));

			}
			if (downloadEvents.size() > 0) {
				downloadPropertyChangeInformer
						.propertyChanged(new DownloadDataPropertyChangeEvent(
								downloadEvents));
			}
			reloadInProgress = false;
		} catch (WebSiteNotFoundException webSiteNotFound) {
			reloadSession = true;
		} catch (IllegalArgumentException webSiteNotFound) {
			reloadSession = true;
		} catch (RuntimeException rE) {
			if (!securer.isInterrupted()) {
				securer.interrupt();
			}
			throw rE;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (reloadSession) {
			try{
				SessionXMLHolder sessionHolder = new SessionXMLHolder(coreHolder);
				String sessionId = sessionHolder.getNewSessionId();
				reloadInProgress = false;
				sessionKontext = "&session=" + sessionId;
			}
			catch (CoreLostException clE){
				reloadInProgress = false;
				throw clE;
			}
		}
	}

	private boolean secureSession() {
		try {
			return securerHolder.secure(sessionKontext, information);
		} catch (Exception ex) {
			return false;
		}
	}

	private class Securer extends Thread {
		private boolean ok = true;

		public void run() {
			while (!interrupted()) {
				try {
					sleep(10000);
					if (!secureSession()) {
						ok = false;
						interrupt();
					}
				} catch (InterruptedException ex) {
					interrupt();
				}
			}
		}

		public boolean isOK() {
			return ok;
		}
	}

}
