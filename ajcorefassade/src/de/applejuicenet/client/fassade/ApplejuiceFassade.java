package de.applejuicenet.client.fassade;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.controller.DataPropertyChangeInformer;
import de.applejuicenet.client.fassade.controller.DataUpdateInformer;
import de.applejuicenet.client.fassade.controller.dac.DirectoryDO;
import de.applejuicenet.client.fassade.controller.dac.DownloadDO;
import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.fassade.controller.dac.PartListDO;
import de.applejuicenet.client.fassade.controller.dac.ServerDO;
import de.applejuicenet.client.fassade.controller.dac.ShareDO;
import de.applejuicenet.client.fassade.controller.xml.DirectoryXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.GetObjectXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.InformationXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.ModifiedXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.NetworkServerXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.PartListXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.SettingsXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.ShareXMLHolder;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.Information;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
import de.applejuicenet.client.fassade.shared.Search;
import de.applejuicenet.client.fassade.shared.ShareEntry;
import de.applejuicenet.client.fassade.shared.Version;
import de.applejuicenet.client.fassade.tools.MD5Encoder;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/ApplejuiceFassade.java,v
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

public class ApplejuiceFassade {
	public static final String FASSADE_VERSION = "F-1.0";
	
	public static final String MIN_NEEDED_CORE_VERSION = "0.30.146.1203";
	public static final String ERROR_MESSAGE = "Unbehandelte Exception";

	public static String separator;

	private final CoreConnectionSettingsHolder coreHolder;

	private Map<String, DataUpdateInformer> informer = 
		new HashMap<String, DataUpdateInformer>();
	private ModifiedXMLHolder modifiedXML = null;
	private InformationXMLHolder informationXML = null;
	private ShareXMLHolder shareXML = null;
	private SettingsXMLHolder settingsXML = null;
	private DirectoryXMLHolder directoryXML = null;
	private Version coreVersion;
	private Map<String, ShareDO> share = null;
	private PartListXMLHolder partlistXML = null;
	
	private long sleepTime = 2000;

	// Thread
	private Thread workerThread;

	public ApplejuiceFassade(String host, Integer port, String password,
			boolean passwordIsPlaintext) throws IllegalArgumentException {
		coreHolder = new CoreConnectionSettingsHolder(host, port, password,
				passwordIsPlaintext);
		modifiedXML = new ModifiedXMLHolder(coreHolder, this);
		DataUpdateInformer downloadInformer = new DataUpdateInformer(
				DataUpdateListener.DOWNLOAD_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getDownloads();
			}
		};
		informer.put(Integer.toString(downloadInformer
				.getDataUpdateListenerType()), downloadInformer);
		DataUpdateInformer searchInformer = new DataUpdateInformer(
				DataUpdateListener.SEARCH_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getSearchs();
			}
		};
		informer.put(Integer.toString(searchInformer
				.getDataUpdateListenerType()), searchInformer);
		DataUpdateInformer serverInformer = new DataUpdateInformer(
				DataUpdateListener.SERVER_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getServer();
			}
		};
		informer.put(Integer.toString(serverInformer
				.getDataUpdateListenerType()), serverInformer);
		DataUpdateInformer uploadInformer = new DataUpdateInformer(
				DataUpdateListener.UPLOAD_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getUploads();
			}
		};
		informer.put(Integer.toString(uploadInformer
				.getDataUpdateListenerType()), uploadInformer);
		DataUpdateInformer shareInformer = new DataUpdateInformer(
				DataUpdateListener.SHARE_CHANGED) {
			protected Object getContentObject() {
				return shareXML.getShare();
			}
		};
		informer.put(Integer
				.toString(shareInformer.getDataUpdateListenerType()),
				shareInformer);
		DataUpdateInformer networkInformer = new DataUpdateInformer(
				DataUpdateListener.NETINFO_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getNetworkInfo();
			}
		};
		informer.put(Integer.toString(networkInformer
				.getDataUpdateListenerType()), networkInformer);
		DataUpdateInformer speedInformer = new DataUpdateInformer(
				DataUpdateListener.SPEED_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getSpeeds();
			}
		};
		informer.put(Integer
				.toString(speedInformer.getDataUpdateListenerType()),
				speedInformer);
		DataUpdateInformer informationInformer = new DataUpdateInformer(
				DataUpdateListener.INFORMATION_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getInformation();
			}
		};
		informer.put(Integer.toString(informationInformer
				.getDataUpdateListenerType()), informationInformer);
	}

	public DataPropertyChangeInformer getDownloadPropertyChangeInformer() {
		return modifiedXML.getDownloadPropertyChangeInformer();
	}

	public Long getLastCoreTimestamp() {
		if (modifiedXML != null) {
			return new Long(modifiedXML.getTimestamp());
		} else {
			return null;
		}
	}

	public void addDataUpdateListener(DataUpdateListener listener, int type) {
		String key = Integer.toString(type);
		if (informer.containsKey(key)) {
			DataUpdateInformer anInformer = informer.get(key);
			anInformer.addDataUpdateListener(listener);
		}
	}

	public void removeDataUpdateListener(DataUpdateListener listener, int type) {
		String key = Integer.toString(type);
		if (informer.containsKey(key)) {
			DataUpdateInformer anInformer = informer.get(key);
			anInformer.removeDataUpdateListener(listener);
		}
	}

	public boolean isLocalhost() {
		return coreHolder.isLocalhost();
	}

	private void checkForValidCoreversion() {
		if (getCoreVersion() == null) {
			return;
		}
		Version neededVersion = new Version();
		neededVersion.setVersion(ApplejuiceFassade.MIN_NEEDED_CORE_VERSION);
		if (getCoreVersion().compareTo(neededVersion) < 0) {
			throw new RuntimeException("invalid coreversion");
		}
	}

	private int tryUpdate(int versuch) {
		int anzahl = versuch;
		if (updateModifiedXML()) {
			anzahl = 0;
		} else {
			anzahl++;
			if (anzahl == 3) {
				throw new RuntimeException("core-connection lost");
			}
		}
		return anzahl;
	}

	public void startXMLCheck() {
		workerThread = new Thread() {
			public void run() {
				setPriority(Thread.NORM_PRIORITY);
				int versuch = 0;
				// load XMLs
				informationXML = new InformationXMLHolder(coreHolder);
				directoryXML = new DirectoryXMLHolder(coreHolder);
				shareXML = new ShareXMLHolder(coreHolder);
				if (coreVersion == null) {
					coreVersion = informationXML.getCoreVersion();
					checkForValidCoreversion();
				}
				while (!isInterrupted()) {
					try {
						versuch = tryUpdate(versuch);
						sleep(sleepTime);
					} catch (InterruptedException e) {
						interrupt();
					}
				}
			}
		};
		workerThread.start();
	}
	
	public void setUpdateInterval(long millis){
		if (millis > 0) {
			sleepTime = millis;
		}
	}

	public void stopXMLCheck() {
		if (workerThread != null) {
			workerThread.interrupt();
		}
	}

	public String[] getCurrentIncomingDirs() {
		Map download = getDownloadsSnapshot();
		DownloadDO downloadDO = null;
		ArrayList<String> incomingDirs = new ArrayList<String>();
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
					if (((String) incomingDirs.get(i))
							.compareToIgnoreCase(downloadDO
									.getTargetDirectory()) == 0) {
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

	public PartListDO getPartList(DownloadSourceDO downloadSourceDO)
			throws WebSiteNotFoundException {
		if (partlistXML == null) {
			partlistXML = new PartListXMLHolder(coreHolder);
		}
		return partlistXML.getPartList(downloadSourceDO);
	}

	public PartListDO getPartList(DownloadDO downloadDO)
			throws WebSiteNotFoundException {
		if (partlistXML == null) {
			partlistXML = new PartListXMLHolder(coreHolder);
		}
		return partlistXML.getPartList(downloadDO);
	}

	public String[] getNetworkKnownServers() {
		NetworkServerXMLHolder getServerXMLHolder = NetworkServerXMLHolder
				.getInstance();
		return getServerXMLHolder.getNetworkKnownServers();
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
				String parameters = "";
				parameters += "MaxUpload=" + maxUp.toString();
				parameters += "&MaxDownload=" + maxDown.toString();
				HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(),
						coreHolder.getCorePort(), HtmlLoader.GET,
						"/function/setsettings?password="
								+ coreHolder.getCorePassword() + "&"
								+ parameters, false);
			}
		}.start();
	}

	public void saveAJSettings(AJSettings ajSettings) {
		String parameters = "";
		try {
			parameters = "Nickname="
					+ URLEncoder.encode(ajSettings.getNick(), "UTF-8");
			parameters += "&XMLPort=" + Long.toString(ajSettings.getXMLPort());
			parameters += "&Port=" + Long.toString(ajSettings.getPort());
			parameters += "&MaxUpload="
					+ Long.toString(ajSettings.getMaxUpload());
			parameters += "&MaxDownload="
					+ Long.toString(ajSettings.getMaxDownload());
			parameters += "&Speedperslot="
					+ Integer.toString(ajSettings.getSpeedPerSlot());
			parameters += "&Incomingdirectory="
					+ URLEncoder.encode(ajSettings.getIncomingDir(), "UTF-8");
			parameters += "&Temporarydirectory="
					+ URLEncoder.encode(ajSettings.getTempDir(), "UTF-8");
			parameters += "&maxconnections="
					+ URLEncoder.encode(Long.toString(ajSettings
							.getMaxConnections()), "UTF-8");
			parameters += "&maxsourcesperfile="
					+ URLEncoder.encode(Long.toString(ajSettings
							.getMaxSourcesPerFile()), "UTF-8");
			parameters += "&autoconnect="
					+ URLEncoder.encode(Boolean.toString(ajSettings
							.isAutoConnect()), "UTF-8");
			parameters += "&maxnewconnectionsperturn="
					+ URLEncoder.encode(Long.toString(ajSettings
							.getMaxNewConnectionsPerTurn()), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET,
				"/function/setsettings?password="
						+ coreHolder.getCorePassword() + "&" + parameters,
				false);
	}

	public Map<String, ServerDO> getAllServer() {
		if (modifiedXML != null) {
			return modifiedXML.getServer();
		}
		return null;
	}

	public synchronized boolean updateModifiedXML() {
		try {
			if (modifiedXML.update()) {
				informDataUpdateListener(DataUpdateListener.SERVER_CHANGED);
				informDataUpdateListener(DataUpdateListener.DOWNLOAD_CHANGED);
				informDataUpdateListener(DataUpdateListener.UPLOAD_CHANGED);
				informDataUpdateListener(DataUpdateListener.NETINFO_CHANGED);
				informDataUpdateListener(DataUpdateListener.SPEED_CHANGED);
				informDataUpdateListener(DataUpdateListener.SEARCH_CHANGED);
				informDataUpdateListener(DataUpdateListener.INFORMATION_CHANGED);
			}
			return true;
		} catch (RuntimeException re) {
			// connection to core lost, next try
			re.printStackTrace();
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public void resumeDownload(DownloadDO[] downloadDOs)
			throws IllegalArgumentException {
		if (downloadDOs == null) {
			throw new IllegalArgumentException("invalid download-array");
		}
		for (int i = 0; i < downloadDOs.length; i++) {
			if (downloadDOs[i] == null) {
				throw new IllegalArgumentException("invalid download-array");
			}
		}
		String parameters = "&id=" + downloadDOs[0].getId();
		if (downloadDOs.length > 1) {
			for (int i = 1; i < downloadDOs.length; i++) {
				parameters += "&id" + i + "=" + downloadDOs[i].getId();
			}
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/resumedownload?password="
						+ coreHolder.getCorePassword() + parameters, false);
	}

	public void startSearch(String searchString)
			throws IllegalArgumentException {
		if (searchString == null) {
			throw new IllegalArgumentException("invalid search-phrase");
		}
		String toSearch = searchString.trim();
		if (toSearch.length() == 0) {
			throw new IllegalArgumentException("invalid search-phrase");
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST, "/function/search?password="
				+ coreHolder.getCorePassword() + "&search=" + toSearch, false);
	}

	public synchronized void cancelSearch(Search search)
			throws IllegalArgumentException {
		if (search == null) {
			throw new IllegalArgumentException("invalid search");
		}
		new CancelThread(search).start();
	}

	private class CancelThread extends Thread {
		private Search search;

		private boolean cancel = false;

		private Thread innerThread;

		public CancelThread(Search search) {
			this.search = search;
		}

		public void run() {
			try {
				if (search.getCreationTime() > System.currentTimeMillis() - 10000) {
					sleep(10000);
				}
				while (!cancel) {
					innerThread = new Thread() {
						public void run() {
							HtmlLoader.getHtmlXMLContent(coreHolder
									.getCoreHost(), coreHolder.getCorePort(),
									HtmlLoader.POST,
									"/function/cancelsearch?password="
											+ coreHolder.getCorePassword()
											+ "&id=" + search.getId(), true);
							cancel = true;

						}
					};
					innerThread.start();
					sleep(4000);
					innerThread.interrupt();
				}
			} catch (InterruptedException iE) {
				innerThread.interrupt();
				interrupt();
			}
		}
	}

	public void renameDownload(DownloadDO downloadDO, String newFilename)
			throws IllegalArgumentException {
		if (downloadDO == null) {
			throw new IllegalArgumentException("invalid downloadDO");
		}
		if (newFilename == null || newFilename.length() == 0
				|| newFilename.trim().length() == 0) {
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
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/renamedownload?password="
						+ coreHolder.getCorePassword() + "&id="
						+ downloadDO.getId() + "&name=" + encodedName, false);
	}

	public void setTargetDir(DownloadDO downloadDO, String newDirectoryName)
			throws IllegalArgumentException {
		if (downloadDO == null) {
			throw new IllegalArgumentException("invalid downloadDO");
		}
		if (newDirectoryName == null || newDirectoryName.length() == 0
				|| newDirectoryName.trim().length() == 0) {
			throw new IllegalArgumentException("invalid directoryname");
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/settargetdir?password="
						+ coreHolder.getCorePassword() + "&id="
						+ downloadDO.getId() + "&dir=" + newDirectoryName,
				false);
	}

	public void shutdownCore() {
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST, "/function/exitcore?password="
				+ coreHolder.getCorePassword(), false);
	}

	public void setPassword(String password, boolean passwordIsPlaintext)
			throws IllegalArgumentException {
		if (password == null
				|| (!passwordIsPlaintext && (password.length() == 0 || password
						.trim().length() == 0))) {
			throw new IllegalArgumentException("invalid password");
		}
		String newPassword = passwordIsPlaintext ? MD5Encoder.getMD5(password)
				: password;
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET,
				"/function/setpassword?password="
						+ coreHolder.getCorePassword() + "&newpassword="
						+ newPassword, false);
	}

	public void cancelDownload(DownloadDO[] downloadDOs)
			throws IllegalArgumentException {
		if (downloadDOs == null) {
			throw new IllegalArgumentException("invalid download-array");
		}
		for (int i = 0; i < downloadDOs.length; i++) {
			if (downloadDOs[i] == null) {
				throw new IllegalArgumentException("invalid download-array");
			}
		}
		String parameters = "&id=" + downloadDOs[0].getId();
		if (downloadDOs.length > 1) {
			for (int i = 1; i < downloadDOs.length; i++) {
				parameters += "&id" + i + "=" + downloadDOs[0].getId();
			}
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/canceldownload?password="
						+ coreHolder.getCorePassword() + parameters, false);
	}

	public void cleanDownloadList() {
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/cleandownloadlist?password="
						+ coreHolder.getCorePassword(), false);
	}

	public void pauseDownload(DownloadDO[] downloadDOs)
			throws IllegalArgumentException {
		if (downloadDOs == null) {
			throw new IllegalArgumentException("invalid download-array");
		}
		for (int i = 0; i < downloadDOs.length; i++) {
			if (downloadDOs[i] == null) {
				throw new IllegalArgumentException("invalid download-array");
			}
		}
		String parameters = "&id=" + downloadDOs[0].getId();
		if (downloadDOs.length > 1) {
			for (int i = 1; i < downloadDOs.length; i++) {
				parameters += "&id" + i + "=" + downloadDOs[0].getId();
			}
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/pausedownload?password="
						+ coreHolder.getCorePassword() + parameters, false);
	}

	public void connectToServer(ServerDO serverDO)
			throws IllegalArgumentException {
		if (serverDO == null) {
			throw new IllegalArgumentException("invalid server");
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/serverlogin?password="
						+ coreHolder.getCorePassword() + "&id="
						+ serverDO.getID(), false);
	}

	public Object getObjectById(Integer id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("invalid id");
		}
		GetObjectXMLHolder getObjectXMLHolder = new GetObjectXMLHolder(
				coreHolder);
		return getObjectXMLHolder.getObjectByID(id.intValue());
	}

	public void entferneServer(ServerDO serverDO)
			throws IllegalArgumentException {
		if (serverDO == null) {
			throw new IllegalArgumentException("invalid server");
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/removeserver?password="
						+ coreHolder.getCorePassword() + "&id="
						+ serverDO.getID(), false);
	}

	public void setPrioritaet(ShareDO shareDO, Integer priority) 
		throws IllegalArgumentException {
		if (shareDO == null) {
			throw new IllegalArgumentException("invalid shareDO");
		}
		setPrioritaet(shareDO.getId(), priority);
	}
	
	
	public void setPrioritaet(DownloadDO downloadDO, Integer priority)
			throws IllegalArgumentException {
		if (downloadDO == null) {
			throw new IllegalArgumentException("invalid downloadDO");
		}
		setPrioritaet(downloadDO.getId(), priority);
	}
	
	private void setPrioritaet(int id, Integer priority)
			throws IllegalArgumentException {
		if (priority == null) {
			throw new IllegalArgumentException("invalid priority");
		}
		if (priority.intValue() < 1 || priority.intValue() > 250) {
			throw new IllegalArgumentException(
					"invalid priority: has to be 1<= x <=250");
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET,
				"/function/setpriority?password="
				+ coreHolder.getCorePassword() + "&id="
				+ id + "&priority="
				+ priority.intValue(), false);
	}	

	public synchronized String processLink(final String link, String subdir)
			throws IllegalArgumentException {
		if (link == null || link.length() == 0) {
			throw new IllegalArgumentException("invalid link");
		}
/*        if (isCoreAvailable() != 0){
                if (links == null){
                    links = new HashSet();
                }
                links.add(link);
        }*/
		if (subdir == null) {
			subdir = "";
		} else {
			subdir = subdir.trim();
			if (subdir.indexOf(File.separator) == 0
					|| subdir.indexOf(ApplejuiceFassade.separator) == 0) {
				subdir = subdir.substring(1);
			}
			subdir = subdir.replaceAll("..", "_");
			subdir = subdir.replaceAll(":", "_");
		}

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
        return HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET, "/function/processlink?password=" +
				coreHolder.getCorePassword() + "&link=" + 
				encodedLink + "&subdir=" + subdir, true);
	}

	public void setPowerDownload(DownloadDO[] downloadDOs, Integer powerDownload)
			throws IllegalArgumentException {
		if (downloadDOs == null) {
			throw new IllegalArgumentException("invalid download-array");
		}
		for (int i = 0; i < downloadDOs.length; i++) {
			if (downloadDOs[i] == null) {
				throw new IllegalArgumentException("invalid download-array");
			}
		}
		if (powerDownload.intValue() < 0 || powerDownload.intValue() > 490) {
			throw new IllegalArgumentException(
					"invalid priority: has to be 1<= x <=490");
		}
		String parameters = "&powerdownload=" + powerDownload + "&id="
				+ downloadDOs[0].getId();
		if (downloadDOs.length > 1) {
			for (int i = 1; i < downloadDOs.length; i++) {
				parameters += "&id" + i + "=" + downloadDOs[i].getId();
			}
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/setpowerdownload?password="
						+ coreHolder.getCorePassword() + parameters, false);
	}

	/**
	 * 
	 * 0 = connection 1 = wrong password 2 = no connection
	 * 
	 */
	public synchronized int isCoreAvailable() {
		try {
			String result = HtmlLoader.getHtmlXMLContent(coreHolder
					.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
					"/xml/information.xml?password="
							+ coreHolder.getCorePassword());
			if (result.startsWith("wrong password")) {
				return 1;
			}
			if (result.indexOf("<applejuice>") == -1) {
				return 2;
			}
		} catch (WebSiteNotFoundException ex) {
			return 2;
		}
		return 0;
	}

	public Version getCoreVersion() {
		return coreVersion;
	}

	public Map<String, DownloadDO> getDownloadsSnapshot() {
		return modifiedXML.getDownloads();
	}

	public void informDataUpdateListener(int type) {
		String key = Integer.toString(type);
		if (informer.containsKey(key)) {
			DataUpdateInformer anInformer = informer.get(key);
			anInformer.informDataUpdateListener();
		}
	}

	public Map<String, ShareDO> getShare(boolean reinit) {
		if (share == null || reinit) {
			share = shareXML.getShare();
		}
		return share;
	}

	public void setShare(Set<ShareEntry> newShare) {
		if (newShare == null) {
			return;
		}
		String parameters = "countshares=" + newShare.size();
		int i = 1;
		for (ShareEntry curShareEntry : newShare) {
			try {
				parameters += "&sharedirectory" + i + "="
						+ URLEncoder.encode(curShareEntry.getDir(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			parameters += "&sharesub"
					+ i
					+ "="
					+ (curShareEntry.getShareMode() == ShareEntry.SUBDIRECTORY ? "true"
							: "false");
			i++;
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET,
				"/function/setsettings?password="
						+ coreHolder.getCorePassword() + "&" + parameters,
				false);
	}

	public void getDirectory(String directory, DirectoryDO directoryDO)
			throws IllegalArgumentException {
		if (directoryDO == null) {
			throw new IllegalArgumentException("invalid directoryDO");
		}
		if (directory == null || directory.length() == 0) {
			throw new IllegalArgumentException("invalid directory");
		}
		directoryXML.getDirectory(directory, directoryDO);
	}

	public NetworkInfo getNetworkInfo() {
		return modifiedXML.getNetworkInfo();
	}
}
