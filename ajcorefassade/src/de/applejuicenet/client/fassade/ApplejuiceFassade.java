package de.applejuicenet.client.fassade;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.controller.DataPropertyChangeInformer;
import de.applejuicenet.client.fassade.controller.DataUpdateInformer;
import de.applejuicenet.client.fassade.controller.xml.DirectoryXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.GetObjectXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.InformationXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.ModifiedXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.NetworkServerXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.PartListXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.SettingsXMLHolder;
import de.applejuicenet.client.fassade.controller.xml.ShareXMLHolder;
import de.applejuicenet.client.fassade.entity.Directory;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.PartList;
import de.applejuicenet.client.fassade.entity.Search;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.entity.ShareEntry;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.fassade.entity.ShareEntry.SHAREMODE;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.exception.WrongPasswordException;
import de.applejuicenet.client.fassade.listener.CoreConnectionSettingsListener;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
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

public final class ApplejuiceFassade implements CoreConnectionSettingsListener{
	public static final String FASSADE_VERSION = "F-1.02";
	
	public static final String MIN_NEEDED_CORE_VERSION = "0.30.146.1203";
	public static final String ERROR_MESSAGE = "Unbehandelte Exception";

	public static String separator;

	private final CoreConnectionSettingsHolder coreHolder;

	private Map<DATALISTENER_TYPE, DataUpdateInformer> informer = 
		new HashMap<DATALISTENER_TYPE, DataUpdateInformer>();
	private ModifiedXMLHolder modifiedXML = null;
	private InformationXMLHolder informationXML = null;
	private ShareXMLHolder shareXML = null;
	private SettingsXMLHolder settingsXML = null;
	private DirectoryXMLHolder directoryXML = null;
	private Version coreVersion;
	private Map<String, Share> share = null;
	private PartListXMLHolder partlistXML = null;
	
	private long sleepTime = 2000;

	// Thread
	private Thread workerThread;

	public ApplejuiceFassade(CoreConnectionSettingsHolder coreConnectionSettingsHolder) 
			throws IllegalArgumentException {
		coreHolder = coreConnectionSettingsHolder;
		coreHolder.addListener(this);
		modifiedXML = new ModifiedXMLHolder(coreHolder, this);
		DataUpdateInformer downloadInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.DOWNLOAD_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getDownloads();
			}
		};
		informer.put(downloadInformer
				.getDataUpdateListenerType(), downloadInformer);
		DataUpdateInformer searchInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.SEARCH_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getSearchs();
			}
		};
		informer.put(searchInformer
				.getDataUpdateListenerType(), searchInformer);
		DataUpdateInformer serverInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.SERVER_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getServer();
			}
		};
		informer.put(serverInformer
				.getDataUpdateListenerType(), serverInformer);
		DataUpdateInformer uploadInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.UPLOAD_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getUploads();
			}
		};
		informer.put(uploadInformer
				.getDataUpdateListenerType(), uploadInformer);
		DataUpdateInformer shareInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.SHARE_CHANGED) {
			protected Object getContentObject() {
				return shareXML.getShare();
			}
		};
		informer.put(shareInformer.getDataUpdateListenerType(),
				shareInformer);
		DataUpdateInformer networkInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.NETINFO_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getNetworkInfo();
			}
		};
		informer.put(networkInformer
				.getDataUpdateListenerType(), networkInformer);
		DataUpdateInformer speedInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.SPEED_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getSpeeds();
			}
		};
		informer.put(speedInformer.getDataUpdateListenerType(),
				speedInformer);
		DataUpdateInformer informationInformer = new DataUpdateInformer(
				DATALISTENER_TYPE.INFORMATION_CHANGED) {
			protected Object getContentObject() {
				return modifiedXML.getInformation();
			}
		};
		informer.put(informationInformer
				.getDataUpdateListenerType(), informationInformer);
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
			workerThread = null;
		}
	}

	public String[] getCurrentIncomingDirs() {
		Map<String, Download> download = getDownloadsSnapshot();
		ArrayList<String> incomingDirs = new ArrayList<String>();
		boolean found;
		synchronized (download) {
			for (Download curDownload : download.values()) {
				if (curDownload.getTargetDirectory().length() == 0) {
					continue;
				}
				found = false;
				for (int i = 0; i < incomingDirs.size(); i++) {
					if (((String) incomingDirs.get(i))
							.compareToIgnoreCase(curDownload
									.getTargetDirectory()) == 0) {
						found = true;
						break;
					}
				}
				if (!found) {
					incomingDirs.add(curDownload.getTargetDirectory());
				}
			}
		}
		incomingDirs.add("");
		return (String[]) incomingDirs.toArray(new String[incomingDirs.size()]);
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

	public PartList getPartList(Download download)
			throws WebSiteNotFoundException {
		if (partlistXML == null) {
			partlistXML = new PartListXMLHolder(coreHolder);
		}
		return partlistXML.getPartList(download);
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

	public Map<String, Server> getAllServer() {
		if (modifiedXML != null) {
			return modifiedXML.getServer();
		}
		return null;
	}

	public synchronized boolean updateModifiedXML() {
		try {
			if (modifiedXML.update()) {
				informDataUpdateListener(DATALISTENER_TYPE.SERVER_CHANGED);
				informDataUpdateListener(DATALISTENER_TYPE.DOWNLOAD_CHANGED);
				informDataUpdateListener(DATALISTENER_TYPE.UPLOAD_CHANGED);
				informDataUpdateListener(DATALISTENER_TYPE.NETINFO_CHANGED);
				informDataUpdateListener(DATALISTENER_TYPE.SPEED_CHANGED);
				informDataUpdateListener(DATALISTENER_TYPE.SEARCH_CHANGED);
				informDataUpdateListener(DATALISTENER_TYPE.INFORMATION_CHANGED);
			}
			return true;
		}
		catch (WrongPasswordException wpE){
			stopXMLCheck();
			throw wpE;
		}
		catch (RuntimeException re) {
			// connection to core lost, next try
			return false;
		} catch (Exception e) {
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
		for(Download curDownload : downloads) {
			if (curDownload == null) {
				throw new IllegalArgumentException("invalid download-list");
			}
		}
		StringBuffer parameters = new StringBuffer();
		int index = 0;
		for(Download curDownload : downloads) {
			parameters.append("&id");
			if (index != 0){
				parameters.append(Integer.toString(index));
			}
			parameters.append("=" + curDownload.getId());
			index++;
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

	public void renameDownload(Download download, String newFilename)
			throws IllegalArgumentException {
		if (download == null) {
			throw new IllegalArgumentException("invalid download");
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
						+ download.getId() + "&name=" + encodedName, false);
	}

	public void setTargetDir(List<Download> downloads, String newDirectoryName)
			throws IllegalArgumentException {
		if (downloads == null) {
			throw new IllegalArgumentException("invalid download-list");
		}
		if (downloads.size() == 0) {
			return;
		}
		if (newDirectoryName == null || newDirectoryName.length() == 0
				|| newDirectoryName.trim().length() == 0) {
			throw new IllegalArgumentException("invalid directoryname");
		}
		for(Download curDownload : downloads) {
			HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
					.getCorePort(), HtmlLoader.POST,
					"/function/settargetdir?password="
							+ coreHolder.getCorePassword() + "&id="
							+ curDownload.getId() + "&dir=" + newDirectoryName,
					false);
		}
	}

	public void shutdownCore() {
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST, "/function/exitcore?password="
				+ coreHolder.getCorePassword(), false);
	}

	public void setPassword(String password, boolean passwordIsPlaintext)
			throws IllegalArgumentException {
		if (password == null
				|| (!passwordIsPlaintext 
				&& (password.length() == 0 || password.trim().length() == 0))) {
			throw new IllegalArgumentException("invalid password");
		}
		String newPassword = passwordIsPlaintext ? MD5Encoder.getMD5(password)
				: password;
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET,
				"/function/setpassword?password="
						+ coreHolder.getCorePassword() + "&newpassword="
						+ newPassword, false);
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
		for(Download curDownload : downloads) {
			if (curDownload == null) {
				throw new IllegalArgumentException("invalid download-list");
			}
		}
		StringBuffer parameters = new StringBuffer();
		int index = 0;
		for(Download curDownload : downloads) {
			parameters.append("&id");
			if (index != 0){
				parameters.append(Integer.toString(index));
			}
			parameters.append("=" + curDownload.getId());
			index++;
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

	public void pauseDownload(List<Download> downloads)
			throws IllegalArgumentException {
		if (downloads == null) {
			throw new IllegalArgumentException("invalid download-list");
		}
		if (downloads.size() == 0) {
			return;
		}
		for(Download curDownload : downloads) {
			if (curDownload == null) {
				throw new IllegalArgumentException("invalid download-list");
			}
		}
		StringBuffer parameters = new StringBuffer();
		int index = 0;
		for(Download curDownload : downloads) {
			parameters.append("&id");
			if (index != 0){
				parameters.append(Integer.toString(index));
			}
			parameters.append("=" + curDownload.getId());
			index++;
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/pausedownload?password="
						+ coreHolder.getCorePassword() + parameters, false);
	}

	public void connectToServer(Server server)
			throws IllegalArgumentException {
		if (server == null) {
			throw new IllegalArgumentException("invalid server");
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.POST,
				"/function/serverlogin?password="
						+ coreHolder.getCorePassword() + "&id="
						+ server.getID(), false);
	}

	public Object getObjectById(Integer id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("invalid id");
		}
		GetObjectXMLHolder getObjectXMLHolder = new GetObjectXMLHolder(
				coreHolder);
		return getObjectXMLHolder.getObjectByID(id.intValue());
	}

	public void entferneServer(List<Server> server)
			throws IllegalArgumentException {
		if (server == null) {
			throw new IllegalArgumentException("invalid server");
		}
		if (server.size() == 0) {
			return;
		}
		for(Server curServer : server) {
			HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
					.getCorePort(), HtmlLoader.POST,
					"/function/removeserver?password="
							+ coreHolder.getCorePassword() + "&id="
							+ curServer.getID(), false);
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
			throw new IllegalArgumentException(
					"invalid priority: has to be 1<= x <=490");
		}
		StringBuffer parameters = new StringBuffer("&powerdownload=" + powerDownload);
		int index = 0;
		for(Download curDownload : downloads) {
			parameters.append("&id");
			if (index != 0){
				parameters.append(Integer.toString(index));
			}
			parameters.append("=" + curDownload.getId());
			index++;
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
			if (result.indexOf("<applejuice>") == -1) {
				return 2;
			}
		} catch (WebSiteNotFoundException ex) {
			return 2;
		}
		catch (WrongPasswordException wpE){
			return 1;
		}
		return 0;
	}

	public Version getCoreVersion() {
		return coreVersion;
	}

	public Map<String, Download> getDownloadsSnapshot() {
		return modifiedXML.getDownloads();
	}

	public void informDataUpdateListener(DATALISTENER_TYPE type) {
		if (informer.containsKey(type)) {
			DataUpdateInformer anInformer = informer.get(type);
			anInformer.informDataUpdateListener();
		}
	}

	public Map<String, Share> getShare(boolean reinit) {
		if (share == null || reinit) {
			share = shareXML.getShare();
		}
		return share;
	}
	
	public void addShareEntry(List<String> paths, SHAREMODE shareMode){
		Set<ShareEntry> shareDirs = getAJSettings().getShareDirs();
		String parameters = "countshares=" + shareDirs.size() + paths.size();
		int i = 1;
		for (ShareEntry curShareEntry : shareDirs) {
			try {
				parameters += "&sharedirectory" + i + "="
						+ URLEncoder.encode(curShareEntry.getDir(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			parameters += "&sharesub"
					+ i
					+ "="
					+ (curShareEntry.getShareMode() == SHAREMODE.SUBDIRECTORY ? "true"
							: "false");
			i++;
		}
		for (String curPath : paths) {
			try {
				parameters += "&sharedirectory" + i + "="
						+ URLEncoder.encode(curPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			parameters += "&sharesub"
					+ i
					+ "="
					+ (shareMode == SHAREMODE.SUBDIRECTORY ? "true"
							: "false");
			i++;
		}		
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET,
				"/function/setsettings?password="
						+ coreHolder.getCorePassword() + "&" + parameters,
				false);
	}

	public void removeShareEntry(List<String> paths){
		Set<ShareEntry> shareDirs = getAJSettings().getShareDirs();
        ArrayList<ShareEntry> toRemove = new ArrayList<ShareEntry>();
    	for(String curPath : paths) {
	        for (ShareEntry curShareEntry : shareDirs) {
                if (curShareEntry.getDir().compareToIgnoreCase(curPath) == 0) {
                	toRemove.add(curShareEntry);
                	break;
                }
	        }
    	}
		for(ShareEntry curShareEntry : toRemove) {
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
				parameters += "&sharedirectory" + i + "="
						+ URLEncoder.encode(curShareEntry.getDir(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			parameters += "&sharesub"
					+ i
					+ "="
					+ (curShareEntry.getShareMode() == SHAREMODE.SUBDIRECTORY ? "true"
							: "false");
			i++;
		}
		HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder
				.getCorePort(), HtmlLoader.GET,
				"/function/setsettings?password="
						+ coreHolder.getCorePassword() + "&" + parameters,
				false);
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
}
