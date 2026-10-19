/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.controller.DataPropertyChangeInformer;
import de.applejuicenet.client.facade.controller.xml.SearchDO.SearchEntryDO;
import de.applejuicenet.client.facade.controller.xml.SearchDO.SearchEntryDO.FileNameDO;
import de.applejuicenet.client.facade.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.applejuicenet.client.facade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.facade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.facade.exception.CoreLostException;
import de.applejuicenet.client.facade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.facade.shared.HtmlLoader;
import de.applejuicenet.client.facade.shared.NetworkInfo;
import de.applejuicenet.client.facade.shared.StringConstants;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.*;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/ModifiedXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Title: AppleJuice Client-GUI
 * </p>
 * <p>
 * Description: Official GUI for the appleJuice core developed by muhviehstarr
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
public class ModifiedXMLHolder extends DefaultHandler
{
   private static final Logger logger = LoggerFactory.getLogger(ModifiedXMLHolder.class);

   private final CoreConnectionSettingsHolder coreHolder;
   private Map<Integer, Download>             sourcesToDownloads             = new HashMap<Integer, Download>();
   private Map<Integer, Share>                parsingShares;
   private XMLReader                          xr                             = null;
   private Map<Integer, Server>               serverMap                      = new HashMap<Integer, Server>();
   private Map<Integer, Download>             downloadMap                    = new HashMap<Integer, Download>();
   private Map<Integer, Upload>               uploadMap                      = new HashMap<Integer, Upload>();
   private Map<Integer, Search>               searchMap                      = new HashMap<Integer, Search>();
   private NetworkInfo                        netInfo;
   private InformationDO                      information;
   private int                                count                          = 0;
   private String                             filter                         = "";
   private String                             sessionContext                 = null;
   private int                                connectedWithServerId          = -1;
   private int                                tryConnectToServer             = -1;
   private boolean                            reloadInProgress               = false;
   private String                             zipMode                        = "";
   private String                             xmlCommand;
   private long                               timestamp                      = 0;
   private int                                checkCount                     = 0;
   private CharArrayWriter                    contents                       = new CharArrayWriter();
   private Map<String, String>                attributes                     = new HashMap<String, String>();
   private Vector<DataPropertyChangeEvent>    downloadEvents                 = new Vector<DataPropertyChangeEvent>();
   private boolean                            downloadSourceEvent;
   private DataPropertyChangeInformer         downloadPropertyChangeInformer;
   private ApplejuiceFacade                  ajFacade;
   private SearchEntryDO                      tmpSearchEntry                 = null;
   private Set<SearchEntryDO>                 searchEntriesToDo              = new HashSet<SearchEntryDO>();
   private Set<DownloadSource>                downloadSourcesToDo            = new HashSet<DownloadSource>();
   private boolean                            searchChanged                  = false;
   private boolean                            downloadChanged                = false;
   private boolean                            uploadChanged                  = false;
   private boolean                            serverChanged                  = false;
   private boolean                            speedChanged                   = false;
   private boolean                            informationChanged             = false;
   private boolean                            networkInfoChanged             = false;
   // Guards parsing and detached snapshots; never held during network I/O.
   private final Object                       stateLock                      = new Object();
   private final Object                       pollingLock                    = new Object();
   private boolean                            downloadPolling                = false;
   private boolean                            uploadPolling                  = false;
   private boolean                            downloadSyncPending            = false;
   private boolean                            uploadSyncPending              = false;

   @SuppressWarnings("unchecked")
   public ModifiedXMLHolder(CoreConnectionSettingsHolder coreHolder, ApplejuiceFacade ajFacade)
   {
      this.coreHolder = coreHolder;
      this.ajFacade  = ajFacade;
      try
      {
         init();
         xmlCommand = "/xml/modified.xml";
         xr = SaxReaders.create();
         xr.setContentHandler(this);
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public DataPropertyChangeInformer getDownloadPropertyChangeInformer()
   {
      return downloadPropertyChangeInformer;
   }

   private void init()
   {
      downloadPropertyChangeInformer = new DataPropertyChangeInformer();
      if(!coreHolder.isLocalhost())
      {
         zipMode = "mode=zip&";
      }
   }

   public void reinit()
   {
      init();
   }

   public Map<Integer, Server> getServer()
   {
      synchronized(stateLock)
      {
         Map<Integer, Server> snapshot = new HashMap<>();
         serverMap.forEach((key, value) -> snapshot.put(key, ((ServerDO) value).snapshot()));
         return snapshot;
      }
   }

   public Map<Integer, Upload> getUploads()
   {
      synchronized(stateLock)
      {
         Map<Integer, Upload> snapshot = new HashMap<>();
         uploadMap.forEach((key, value) -> snapshot.put(key, ((UploadDO) value).snapshot()));
         return snapshot;
      }
   }

   public Map<Integer, Download> getDownloads()
   {
      synchronized(stateLock)
      {
         Map<Integer, Download> snapshot = new HashMap<>();
         downloadMap.forEach((key, value) -> snapshot.put(key, ((DownloadDO) value).snapshot()));
         return snapshot;
      }
   }

   public Map<Integer, Search> getSearchs()
   {
      synchronized(stateLock)
      {
         Map<Integer, Search> snapshot = new HashMap<>();
         searchMap.forEach((key, value) -> snapshot.put(key, ((SearchDO) value).snapshot()));
         return snapshot;
      }
   }

   public NetworkInfo getNetworkInfo()
   {
      return netInfo;
   }

   public void setDownloadPolling(boolean enabled)
   {
      synchronized(pollingLock)
      {
         if(enabled && !downloadPolling)
         {
            downloadSyncPending = true;
         }

         downloadPolling = enabled;
      }
   }

   public void setUploadPolling(boolean enabled)
   {
      synchronized(pollingLock)
      {
         if(enabled && !uploadPolling)
         {
            uploadSyncPending = true;
         }

         uploadPolling = enabled;
      }
   }

   private String buildPollingFilter(boolean downloads, boolean uploads)
   {
      StringBuilder pollingFilter = new StringBuilder("&filter=informations;server;search;ids");

      if(downloads)
      {
         pollingFilter.append(";down;user");
      }

      if(uploads)
      {
         pollingFilter.append(";uploads");
      }

      return pollingFilter.append("&mode=zip").toString();
   }

   private void synchronize(String syncFilter)
   {
      String savedFilter    = filter;
      long   savedTimestamp = timestamp;

      filter    = syncFilter;
      timestamp = 0;
      try
      {
         doReload();
      }
      finally
      {
         filter    = savedFilter;
         timestamp = savedTimestamp;
      }
   }

   public boolean update()
   {
      if(reloadInProgress)
      {
         return false;
      }
      else
      {
         int stage = count;

         switch(count)
         {

            // lazy loading
            case 0:
            {
               count++;
               filter = "&filter=down;uploads;server;informations;search&mode=zip";
               break;
            }

            case 1:
            {
               count++;
               filter = "&filter=informations;user;search&mode=zip";
               break;
            }

            case 2:
            {
               count++;
               break;
            }

            default:
               break;
         }

         searchChanged      = false;
         uploadChanged      = false;
         downloadChanged    = false;
         serverChanged      = false;
         speedChanged       = false;
         informationChanged = false;
         networkInfoChanged = false;
         reloadInProgress   = true;
         try
         {
            boolean downloads;
            boolean uploads;
            boolean syncDownloads = false;
            boolean syncUploads   = false;

            synchronized(pollingLock)
            {
               downloads = downloadPolling;
               uploads   = uploadPolling;
               if(stage >= 2)
               {
                  syncDownloads       = downloads && downloadSyncPending;
                  syncUploads         = uploads && uploadSyncPending;
                  downloadSyncPending = downloadSyncPending && !downloads;
                  uploadSyncPending   = uploadSyncPending && !uploads;
               }
            }

            if(stage >= 2)
            {
               if(syncDownloads)
               {
                  synchronize("&filter=down;user;ids&mode=zip");
               }

               if(syncUploads)
               {
                  synchronize("&filter=uploads;ids&mode=zip");
               }

               filter = buildPollingFilter(downloads, uploads);
            }

            doReload();
         }
         finally
         {
            reloadInProgress = false;
         }

         return true;
      }
   }

   public Information getInformation()
   {
      return information;
   }

   public long getTimestamp()
   {
      return timestamp;
   }

   public Map<String, Long> getSpeeds()
   {
      Map<String, Long> speeds = new HashMap<String, Long>();

      if(information != null)
      {
         speeds.put(StringConstants.UPLOAD_SPEED, information.getUploadSpeed());
         speeds.put(StringConstants.DOWNLOAD_SPEED, information.getDownloadSpeed());
         speeds.put(StringConstants.CREDITS, information.getCredits());
         speeds.put(StringConstants.SESSION_UPLOAD, information.getSessionUpload());
         speeds.put(StringConstants.SESSION_DOWNLOAD, information.getSessionDownload());
      }

      return speeds;
   }

   private void checkInformationAttributes(Attributes attr)
   {
      if(information == null)
      {
         information = new InformationDO();
      }

      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.CREDITS))
         {
            speedChanged = true;
            information.setCredits(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.SESSION_UPLOAD))
         {
            speedChanged = true;
            information.setSessionUpload(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.SESSION_DOWNLOAD))
         {
            speedChanged = true;
            information.setSessionDownload(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.UPLOAD_SPEED))
         {
            speedChanged = true;
            information.setUploadSpeed(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.DOWNLOAD_SPEED))
         {
            speedChanged = true;
            information.setDownloadSpeed(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.OPEN_CONNECTIONS))
         {
            information.setOpenConnections(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.MAX_UPLOAD_POSITIONS))
         {
            information.setMaxUploadPositions(Long.parseLong(attr.getValue(i)));
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void checkNetworkMap(NetworkInfo netInfo, Map userAttributes)
   {
      netInfo.setAjTotalUsers(Long.parseLong((String) userAttributes.get(StringConstants.USERS)));
      netInfo.setAjFileCount(Long.parseLong((String) userAttributes.get(StringConstants.FILES)));
      netInfo.setAjTotalShare((String) userAttributes.get(StringConstants.FILESIZE));
      netInfo.setFirewalled(((String) userAttributes.get(StringConstants.FIREWALLED)).equals(StringConstants.TRUE));
      netInfo.setExternalIP((String) userAttributes.get(StringConstants.IP));
      netInfo.setTryConnectToServer(Integer.parseInt((String) userAttributes.get(StringConstants.TRYCONNECTTOSERVER)));
      netInfo.setConnectedWithServerId(Integer.parseInt((String) userAttributes.get(StringConstants.CONNECTEDWITHSERVERID)));
      netInfo.setConnectionTime(Long.parseLong((String) userAttributes.get(StringConstants.CONNECTEDSINCE)));
   }

   private void checkNetworkInfoAttributes(Attributes attr)
   {
      if(netInfo == null)
      {
         netInfo = new NetworkInfo();
      }

      attributes.clear();
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         attributes.put(attr.getLocalName(i), attr.getValue(i));
      }

      checkNetworkMap(netInfo, attributes);
      attributes.clear();
   }

   @SuppressWarnings("unchecked")
   private void checkServerMap(ServerDO serverDO, Map userAttributes)
   {
      serverDO.setName((String) userAttributes.get(StringConstants.NAME));
      serverDO.setHost((String) userAttributes.get(StringConstants.HOST));
      serverDO.setTimeLastSeen(Long.parseLong((String) userAttributes.get(StringConstants.LAST_SEEN)));
      serverDO.setPort((String) userAttributes.get(StringConstants.PORT));
      serverDO.setAttempts(Integer.parseInt((String) userAttributes.get(StringConstants.CONNECTIONTRY)));
   }

   private void checkServerAttributes(Attributes attr)
   {
      attributes.clear();
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         attributes.put(attr.getLocalName(i), attr.getValue(i));
      }

      int    id       = Integer.parseInt(attributes.get(StringConstants.ID));
      Server serverDO;

      if(serverMap.containsKey(id))
      {
         serverDO = serverMap.get(id);
      }
      else
      {
         serverDO = new ServerDO(id);
         serverMap.put(id, serverDO);
      }

      checkServerMap((ServerDO) serverDO, attributes);
      attributes.clear();
   }

   @SuppressWarnings("unchecked")
   private void checkUploadMap(UploadDO uploadDO, Map userAttributes)
   {
      uploadDO.setShareFileID(Integer.parseInt((String) userAttributes.get(StringConstants.SHAREID)));
      uploadDO.setStatus(Integer.parseInt((String) userAttributes.get(StringConstants.STATUS)));
      uploadDO.setDirectState(Integer.parseInt((String) userAttributes.get(StringConstants.DIRECTSTATE)));
      uploadDO.setPriority(Integer.parseInt((String) userAttributes.get(StringConstants.PRIORITY)));
      uploadDO.setUploadFrom(Long.parseLong((String) userAttributes.get(StringConstants.UPLOADFROM)));
      uploadDO.setActualUploadPosition(Long.parseLong((String) userAttributes.get(StringConstants.ACTUALUPLOADPOSITION)));
      uploadDO.setUploadTo(Long.parseLong((String) userAttributes.get(StringConstants.UPLOADTO)));
      uploadDO.setSpeed(Integer.parseInt((String) userAttributes.get(StringConstants.SPEED)));
      uploadDO.setNick((String) userAttributes.get(StringConstants.NICK));
      uploadDO.setLastConnection(Long.parseLong((String) userAttributes.get(StringConstants.LASTCONNECTION)));
      uploadDO.setLoaded(Double.parseDouble((String) userAttributes.get(StringConstants.LOADED)));
      String versionNr = (String) userAttributes.get(StringConstants.VERSION);
      int    os = Integer.parseInt((String) userAttributes.get(StringConstants.OPERATINGSYSTEM));

      if(!versionNr.equals(StringConstants._0_0_0_0) && os != -1)
      {
         VersionDO version = new VersionDO(versionNr, os);

         uploadDO.setVersion(version);
      }
   }

   private void checkUploadAttributes(Attributes attr)
   {
      attributes.clear();
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         attributes.put(attr.getLocalName(i), attr.getValue(i));
      }

      int    id = Integer.parseInt(attributes.get(StringConstants.ID));

      Upload uploadDO = uploadMap.get(id);

      if(null == uploadDO)
      {
         uploadDO = new UploadDO(id);
         uploadMap.put(id, uploadDO);
      }

      checkUploadMap((UploadDO) uploadDO, attributes);
      attributes.clear();
      Share shareDO = (parsingShares == null ? ajFacade.getShare(false) : parsingShares).get(uploadDO.getShareFileID());

      if(shareDO != null)
      {
         ((UploadDO) uploadDO).setFileName(shareDO.getShortfilename());
      }
      else
      {
         // if the matching share file does not exist for some reason,
         // this upload is not shown either
         uploadMap.remove(id);
      }
   }

   @SuppressWarnings("unchecked")
   private void checkUserMap(DownloadSourceDO downloadSourceDO, Map userAttributes)
   {
      downloadSourceDO.setStatus(Integer.parseInt((String) userAttributes.get(StringConstants.STATUS)));
      downloadSourceDO.setDirectstate(Integer.parseInt((String) userAttributes.get(StringConstants.DIRECTSTATE)));
      downloadSourceDO.setDownloadFrom(Long.parseLong((String) userAttributes.get(StringConstants.DOWNLOADFROM)));
      downloadSourceDO.setDownloadTo(Long.parseLong((String) userAttributes.get(StringConstants.DOWNLOADTO)));
      downloadSourceDO.setActualDownloadPosition(Long.parseLong((String) userAttributes.get(StringConstants.ACTUALDOWNLOADPOSITION)));
      downloadSourceDO.setSpeed(Integer.parseInt((String) userAttributes.get(StringConstants.SPEED)));
      downloadSourceDO.setQueuePosition(Integer.parseInt((String) userAttributes.get(StringConstants.QUEUEPOSITION)));
      downloadSourceDO.setPowerDownload(Integer.parseInt((String) userAttributes.get(StringConstants.POWERDOWNLOAD)));
      downloadSourceDO.setFilename((String) userAttributes.get(StringConstants.FILENAME));
      downloadSourceDO.setNickname((String) userAttributes.get(StringConstants.NICKNAME));
      String versionNr = (String) userAttributes.get(StringConstants.VERSION);

      downloadSourceDO.setOrigin(Integer.parseInt((String) userAttributes.get(StringConstants.SOURCE)));
      int os = Integer.parseInt((String) userAttributes.get(StringConstants.OPERATINGSYSTEM));

      if(!versionNr.equals(StringConstants._0_0_0_0) && os != -1)
      {
         VersionDO version = new VersionDO(versionNr, os);

         downloadSourceDO.setVersion(version);
      }
   }

   private void checkUserAttributes(Attributes attr)
   {
      attributes.clear();
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         attributes.put(attr.getLocalName(i), attr.getValue(i));
      }

      int            id         = Integer.parseInt(attributes.get(StringConstants.ID));
      int            downloadId = Integer.parseInt(attributes.get(StringConstants.DOWNLOADID));

      Download       downloadDO       = downloadMap.get(downloadId);
      DownloadSource downloadSourceDO = null;

      if(downloadDO != null)
      {
         downloadSourceDO = downloadDO.getSourceById(id);
         if(downloadSourceDO == null)
         {
            downloadSourceDO = new DownloadSourceDO(id);
            ((DownloadDO) downloadDO).addSource(downloadSourceDO);
            sourcesToDownloads.put(id, downloadDO);
         }
      }
      else
      {
         downloadSourceDO = new DownloadSourceDO(id);
         downloadSourcesToDo.add(downloadSourceDO);
      }

      ((DownloadSourceDO) downloadSourceDO).setDownloadId(downloadId);
      checkUserMap((DownloadSourceDO) downloadSourceDO, attributes);
      attributes.clear();
      downloadSourceEvent = true;
   }

   @SuppressWarnings("unchecked")
   private void checkDownloadMap(DownloadDO downloadDO, Map userAttributes, boolean newDownload)
   {
      downloadDO.setShareId(Integer.parseInt((String) userAttributes.get(StringConstants.SHAREID)));
      downloadDO.setSize(Long.parseLong((String) userAttributes.get(StringConstants.SIZE)));
      downloadDO.setHash((String) userAttributes.get(StringConstants.HASH));
      downloadDO.setTemporaryFileNumber(Integer.parseInt((String) userAttributes.get(StringConstants.TEMPORARYFILENUMBER)));
      if(newDownload)
      {
         downloadDO.setStatus(Integer.parseInt((String) userAttributes.get(StringConstants.STATUS)));
         downloadDO.setFilename((String) userAttributes.get(StringConstants.FILENAME));
         downloadDO.setTargetDirectory((String) userAttributes.get(StringConstants.TARGETDIRECTORY));
         downloadDO.setPowerDownload(Integer.parseInt((String) userAttributes.get(StringConstants.POWERDOWNLOAD)));
         downloadDO.setReady(Long.parseLong((String) userAttributes.get(StringConstants.READY)));
      }
      else
      {
         Integer tmpInt = Integer.parseInt((String) userAttributes.get(StringConstants.STATUS));
         Integer oldInt;

         if(tmpInt != downloadDO.getStatus())
         {
            oldInt = downloadDO.getStatus();
            downloadDO.setStatus(tmpInt);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.STATUS_CHANGED,
                                                                   oldInt, tmpInt));
         }

         String tmpString = (String) userAttributes.get(StringConstants.FILENAME);
         String oldText = downloadDO.getFilename();

         if(oldText == null || !oldText.equals(tmpString))
         {
            downloadDO.setFilename(tmpString);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.FILENAME_CHANGED,
                                                                   oldText, tmpString));
         }

         tmpString = (String) userAttributes.get(StringConstants.TARGETDIRECTORY);
         oldText   = downloadDO.getTargetDirectory();
         if(oldText == null || !oldText.equals(tmpString))
         {
            downloadDO.setTargetDirectory(tmpString);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED,
                                                                   oldText, tmpString));
         }

         tmpInt = Integer.parseInt((String) userAttributes.get(StringConstants.POWERDOWNLOAD));
         if(tmpInt != downloadDO.getPowerDownload())
         {
            oldInt = downloadDO.getPowerDownload();
            downloadDO.setPowerDownload(tmpInt);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.POWERDOWNLOAD_CHANGED,
                                                                   oldInt, tmpInt));
         }

         long tmpReady = Long.parseLong((String) userAttributes.get(StringConstants.READY));

         if(tmpReady != downloadDO.getReady())
         {
            long oldReady = downloadDO.getReady();
            downloadDO.setReady(tmpReady);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.READY_CHANGED,
                                                                   oldReady, tmpReady));
         }
      }
   }

   private void checkDownloadAttributes(Attributes attr)
   {
      attributes.clear();
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         attributes.put(attr.getLocalName(i), attr.getValue(i));
      }

      int      id          = Integer.parseInt(attributes.get(StringConstants.ID));
      Download downloadDO;
      boolean  newDownload;

      if(downloadMap.containsKey(id))
      {
         downloadDO  = downloadMap.get(id);
         newDownload = false;
      }
      else
      {
         downloadDO = new DownloadDO(id);
         downloadMap.put(id, downloadDO);
         newDownload = true;
      }

      checkDownloadMap((DownloadDO) downloadDO, attributes, newDownload);
      attributes.clear();
      if(newDownload)
      {
         downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap, DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED, null,
                                                                downloadDO));
      }
   }

   private boolean removeDownload(Integer id)
   {
      Download download = downloadMap.get(id);

      if(download != null)
      {
         for(DownloadSource curDownloadSource : download.getSources())
         {
            sourcesToDownloads.remove(curDownloadSource.getId());
         }

         downloadMap.remove(id);
         downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap, DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED,
                                                                download, null));
         return true;
      }
      else
      {
         return false;
      }
   }

   private void checkRemovedAttributes(Attributes attr)
   {
      int length = attr.getLength();
      int id;

      for(int i = 0; i < length; i++)
      {
         if(!attr.getLocalName(i).equals(StringConstants.ID))
         {
            continue;
         }

         id = Integer.parseInt(attr.getValue(i));
         if(removeDownload(id))
         {
            downloadChanged = true;
         }

         if(null != uploadMap.remove(id))
         {
            uploadChanged = true;
         }

         if(null != serverMap.remove(id))
         {
            serverChanged = true;
         }

         if(sourcesToDownloads.containsKey(id))
         {
            downloadChanged = true;
            DownloadDO downloadDO = (DownloadDO) sourcesToDownloads.get(id);

            downloadDO.removeSource(id);
            sourcesToDownloads.remove(id);
            downloadSourceEvent = true;
            continue;
         }
         else if(searchMap.containsKey(id))
         {
            searchChanged = true;
            searchMap.remove(id);
            Search.currentSearchCount = searchMap.size();
            continue;
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void checkSearchMap(SearchDO aSearch, Map userAttributes)
   {
      aSearch.setSearchText((String) userAttributes.get(StringConstants.SEARCHTEXT));
      aSearch.setOpenSearches(Integer.parseInt((String) userAttributes.get(StringConstants.OPENSEARCHES)));
      aSearch.setFoundFiles(Integer.parseInt((String) userAttributes.get(StringConstants.FOUNDFILES)));
      aSearch.setSearchedClients(Integer.parseInt((String) userAttributes.get(StringConstants.SUMSEARCHES)));
      aSearch.setRunning(((String) userAttributes.get(StringConstants.RUNNING)).equals(StringConstants.TRUE));
   }

   private void checkSearchAttributes(Attributes attr)
   {
      attributes.clear();
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         attributes.put(attr.getLocalName(i), attr.getValue(i));
      }

      int    id = Integer.parseInt(attributes.get(StringConstants.ID));

      Search aSearch;

      if(searchMap.containsKey(id))
      {
         aSearch = searchMap.get(id);
         aSearch.setChanged(false);
      }
      else
      {
         aSearch = new SearchDO(id);
         searchMap.put(id, aSearch);
      }

      checkSearchMap((SearchDO) aSearch, attributes);
      attributes.clear();
   }

   private void checkSearchEntryAttributes(Attributes attr)
   {
      int    searchId = -1;
      int    id       = -1;
      String checksum = "";
      long   size  = -1;

      int    length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.ID))
         {
            id = Integer.parseInt(attr.getValue(i));
         }
         else if(attr.getLocalName(i).equals(StringConstants.SEARCHID))
         {
            searchId = Integer.parseInt(attr.getValue(i));
         }
         else if(attr.getLocalName(i).equals(StringConstants.CHECKSUM))
         {
            checksum = attr.getValue(i);
         }
         else if(attr.getLocalName(i).equals(StringConstants.SIZE))
         {
            size = Long.parseLong(attr.getValue(i));
         }
      }

      SearchDO aSearch;

      if(searchMap.containsKey(searchId))
      {
         aSearch        = (SearchDO) searchMap.get(searchId);
         tmpSearchEntry = (SearchEntryDO) aSearch.getSearchEntryById(id);
         if(tmpSearchEntry == null)
         {
            tmpSearchEntry = aSearch.new SearchEntryDO(id, searchId, checksum, size);
            aSearch.addSearchEntry(tmpSearchEntry);
         }
      }
      else
      {
         tmpSearchEntry = new SearchDO(-1).new SearchEntryDO(id, searchId, checksum, size);
         searchEntriesToDo.add(tmpSearchEntry);
      }
   }

   private void checkSearchEntryFilenameAttributes(Attributes attr)
   {
      if(tmpSearchEntry == null)
      {
         return;
      }

      int    frequency = -1;
      String fileName = "";

      int    length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.NAME))
         {
            fileName = attr.getValue(i);
         }
         else if(attr.getLocalName(i).equals(StringConstants.USER))
         {
            frequency = Integer.parseInt(attr.getValue(i));
         }
      }

      FileNameDO filename = tmpSearchEntry.new FileNameDO(fileName, frequency);

      tmpSearchEntry.addFileName(filename);
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      contents.reset();
      if(localName.equals(StringConstants.DOWNLOAD))
      {
         downloadChanged = true;
         checkDownloadAttributes(attr);
      }
      else if(localName.equals(StringConstants.UPLOAD))
      {
         uploadChanged = true;
         checkUploadAttributes(attr);
      }
      else if(localName.equals(StringConstants.SERVER))
      {
         serverChanged = true;
         checkServerAttributes(attr);
      }
      else if(localName.equals(StringConstants.INFORMATION))
      {
         informationChanged = true;
         checkInformationAttributes(attr);
      }
      else if(localName.equals(StringConstants.NETWORKINFO))
      {
         networkInfoChanged = true;
         checkNetworkInfoAttributes(attr);
      }
      else if(localName.equals(StringConstants.OBJECT))
      {
         // <removed><object id="188"/></removed>
         checkRemovedAttributes(attr);
      }
      else if(localName.equals(StringConstants.USER))
      {
         downloadChanged = true;
         checkUserAttributes(attr);
      }
      else if(localName.equals(StringConstants.SEARCH))
      {
         searchChanged = true;
         checkSearchAttributes(attr);
      }
      else if(localName.equals(StringConstants.SEARCHENTRY))
      {
         searchChanged = true;
         checkSearchEntryAttributes(attr);
      }
      else if(localName.equals(StringConstants.FILENAME))
      {
         searchChanged = true;
         checkSearchEntryFilenameAttributes(attr);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
                   throws SAXException
   {
      if(localName.equals(StringConstants.SEARCHENTRY))
      {
         tmpSearchEntry = null;
      }
      else if(localName.equals(StringConstants.WELCOMEMESSAGE))
      {
         if(netInfo == null)
         {
            netInfo = new NetworkInfo();
         }

         String tmp = contents.toString();

         netInfo.setWelcomeMessage(tmp);
      }
      else if(checkCount > 1)
      {
         if(localName.equals(StringConstants.TIME))
         {
            timestamp = Long.parseLong(contents.toString());
         }
      }
   }

   public void characters(char[] ch, int start, int length)
                   throws SAXException
   {
      contents.write(ch, start, length);
   }

   private String getXMLString(String parameters) throws Exception
   {
      String        xmlData = null;
      StringBuilder command = new StringBuilder();

      command.append(xmlCommand);
      command.append(StringConstants.QUESTIONMARK);

      if(parameters.indexOf(StringConstants.MODE_ZIP) == -1)
      {
         command.append(zipMode);
      }

      command.append(StringConstants.PASSWORD);
      command.append(coreHolder.getCorePassword());
      command.append(StringConstants.TIMESTAMP);
      command.append(timestamp);
      command.append(parameters);
      command.append(sessionContext);
      xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET, command.toString());
      if(xmlData.length() == 0)
      {
         throw new IllegalArgumentException();
      }

      return xmlData;
   }

   private int updateTryConnect()
   {
      int      connectionStatus = Information.NOT_CONNECTED;
      ServerDO serverDO = null;

      if(tryConnectToServer != -1)
      {
         Object previousServer = serverMap.get(tryConnectToServer);

         if(previousServer != null)
         {
            ((ServerDO) previousServer).setTryConnect(false);
         }

         information.setServer(null);
      }

      if(netInfo.getTryConnectToServer() != -1)
      {
         serverDO          = (ServerDO) serverMap.get(netInfo.getTryConnectToServer());
         connectionStatus = Information.TRYING_TO_CONNECT;
         if(serverDO != null)
         {
            serverDO.setTryConnect(true);
         }
      }

      tryConnectToServer = netInfo.getTryConnectToServer();
      information.setServer(serverDO);
      information.setConnectionStatus(connectionStatus);
      return connectionStatus;
   }

   private int updateConnected()
   {
      int      connectionStatus = Information.NOT_CONNECTED;
      ServerDO serverDO = null;

      if(connectedWithServerId != -1)
      {
         Object previousServer = serverMap.get(connectedWithServerId);

         if(previousServer != null)
         {
            ((ServerDO) previousServer).setConnected(false);
         }

         information.setServer(null);
      }

      if(netInfo.getConnectedWithServerId() != -1)
      {
         serverDO          = (ServerDO) serverMap.get(netInfo.getConnectedWithServerId());
         connectionStatus = Information.CONNECTED;
         if(serverDO != null)
         {
            serverDO.setConnected(true);
         }
      }

      connectedWithServerId = netInfo.getConnectedWithServerId();
      information.setServer(serverDO);
      information.setConnectionStatus(connectionStatus);
      return connectionStatus;
   }

   private void parseRemaining()
   {
      int connectionStatus = Information.NOT_CONNECTED;

      if(tryConnectToServer != netInfo.getTryConnectToServer())
      {
         connectionStatus = updateTryConnect();
      }

      if(connectedWithServerId != netInfo.getConnectedWithServerId())
      {
         connectionStatus = updateConnected();
      }

      information.setExternalIP(netInfo.getExternalIP());

      if(searchEntriesToDo.size() > 0)
      {
         SearchDO      aSearch;
         SearchEntryDO searchEntry;

         for(SearchEntryDO curSearchEntryDO : searchEntriesToDo)
         {
            if(!searchMap.containsKey(curSearchEntryDO.getSearchId()))
            {
               continue;
            }

            aSearch = (SearchDO) searchMap.get(curSearchEntryDO.getSearchId());
            aSearch.addSearchEntry(curSearchEntryDO);
         }

         searchEntriesToDo.clear();
      }

      if(downloadSourcesToDo.size() > 0)
      {
         Download downloadDO;

         for(DownloadSource downloadSourceDO : downloadSourcesToDo)
         {
            if(!downloadMap.containsKey(downloadSourceDO.getDownloadId()))
            {
               continue;
            }

            downloadDO = downloadMap.get(downloadSourceDO.getDownloadId());
            ((DownloadDO) downloadDO).addSource(downloadSourceDO);
            sourcesToDownloads.put(downloadSourceDO.getDownloadId(), downloadDO);

         }

         downloadSourcesToDo.clear();
      }
   }

   private void checkForValidSession()
   {
      if(sessionContext == null)
      {
         SessionXMLHolder sessionHolder = new SessionXMLHolder(coreHolder);
         String           sessionId = sessionHolder.getNewSessionId();

         sessionContext = "&session=" + sessionId;
      }
   }

   public void reload()
   {
      reloadInProgress = true;
      try
      {
         doReload();
      }
      finally
      {
         reloadInProgress = false;
      }
   }

   private void applyDiff(String xmlString) throws Exception
   {
      // Resolve lazy share loading before taking the state lock.
      Map<Integer, Share> shares = ajFacade == null ? Map.of() : ajFacade.getShare(false);
      synchronized(stateLock)
      {
         Map<Integer, Download> oldDownloads = downloadMap;
         Map<Integer, Upload> oldUploads = uploadMap;
         Map<Integer, Server> oldServers = serverMap;
         Map<Integer, Search> oldSearches = searchMap;
         Map<Integer, Download> oldSources = sourcesToDownloads;
         InformationDO oldInformation = information;
         NetworkInfo oldNetwork = netInfo;
         long oldTimestamp = timestamp;
         int oldConnected = connectedWithServerId;
         int oldConnecting = tryConnectToServer;
         int oldSearchCount = Search.currentSearchCount;
         boolean[] oldChanged = {searchChanged, downloadChanged, uploadChanged, serverChanged,
                                 speedChanged, informationChanged, networkInfoChanged, downloadSourceEvent};
         Set<SearchEntryDO> oldEntries = searchEntriesToDo;
         Set<DownloadSource> oldPendingSources = downloadSourcesToDo;
         SearchEntryDO oldEntry = tmpSearchEntry;
         Vector<DataPropertyChangeEvent> oldEvents = downloadEvents;
         downloadMap = getDownloads();
         uploadMap = getUploads();
         serverMap = getServer();
         searchMap = getSearchs();
         sourcesToDownloads = new HashMap<>();
         oldSources.forEach((key, value) -> sourcesToDownloads.put(key, downloadMap.get(value.getId())));
         information = oldInformation == null ? null : oldInformation.snapshot();
         netInfo = oldNetwork == null ? null : oldNetwork.snapshot();
         searchEntriesToDo = new HashSet<>();
         downloadSourcesToDo = new HashSet<>();
         tmpSearchEntry = null;
         downloadEvents = new Vector<>();
         parsingShares = shares;
         try
         {
            if(filter.indexOf("down;") != -1)
            {
               downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap,
                       DownloadDataPropertyChangeEvent.DOWNLOADMAP_CHECKED, null, null));
            }
            downloadSourceEvent = false;
            for(Search search : searchMap.values())
            {
               search.setChanged(false);
            }
            xr.parse(new InputSource(new StringReader(xmlString)));
            parseRemaining();
            if(downloadSourceEvent)
            {
               downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap,
                       DownloadDataPropertyChangeEvent.A_SOURCE_CHANGED, null, null));
            }
         }
         catch(Exception ex)
         {
            downloadMap = oldDownloads;
            uploadMap = oldUploads;
            serverMap = oldServers;
            searchMap = oldSearches;
            sourcesToDownloads = oldSources;
            information = oldInformation;
            netInfo = oldNetwork;
            timestamp = oldTimestamp;
            connectedWithServerId = oldConnected;
            tryConnectToServer = oldConnecting;
            Search.currentSearchCount = oldSearchCount;
            searchChanged = oldChanged[0];
            downloadChanged = oldChanged[1];
            uploadChanged = oldChanged[2];
            serverChanged = oldChanged[3];
            speedChanged = oldChanged[4];
            informationChanged = oldChanged[5];
            networkInfoChanged = oldChanged[6];
            downloadSourceEvent = oldChanged[7];
            searchEntriesToDo = oldEntries;
            downloadSourcesToDo = oldPendingSources;
            tmpSearchEntry = oldEntry;
            downloadEvents = oldEvents;
            logger.warn("modified.xml diff could not be applied; previous state restored", ex);
            throw ex;
         }
         finally
         {
            parsingShares = null;
         }
      }
   }

   private void doReload()
   {
      boolean reloadSession = false;

      try
      {
         checkForValidSession();
         String xmlString = getXMLString(filter);

         try
         {
            applyDiff(xmlString);
         }
         catch(Exception ex)
         {
            throw new RuntimeException("modified.xml could not be applied", ex);
         }
         if(checkCount < 2)
         {
            checkCount++;
         }
         if(downloadEvents.size() > 0)
         {
            downloadPropertyChangeInformer.propertyChanged(new DownloadDataPropertyChangeEvent(downloadEvents));
         }
      }
      catch(WebSiteNotFoundException webSiteNotFound)
      {
         logger.debug("modified.xml not available ({}); requesting a new session", webSiteNotFound.toString());
         reloadSession = true;
      }
      catch(IllegalArgumentException webSiteNotFound)
      {
         logger.debug("modified.xml was rejected by the core; requesting a new session");
         reloadSession = true;
      }
      catch(RuntimeException rE)
      {
         throw rE;
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }

      if(reloadSession)
      {
         try
         {
            resetState();
            SessionXMLHolder sessionHolder = new SessionXMLHolder(coreHolder);
            String           sessionId = sessionHolder.getNewSessionId();

            sessionContext   = "&session=" + sessionId;
            logger.debug("New core session established");
         }
         catch(CoreLostException clE)
         {
            throw clE;
         }
      }
   }

   private void resetState()
   {
      Vector<DataPropertyChangeEvent> removedEvents = new Vector<DataPropertyChangeEvent>();

      synchronized(stateLock)
      {
      for(Download curDownload : downloadMap.values())
      {
         removedEvents.add(new DownloadDataPropertyChangeEvent(downloadMap, DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED,
                                                               curDownload, null));
      }

      downloadMap.clear();
      sourcesToDownloads.clear();
      downloadSourcesToDo.clear();
      uploadMap.clear();
      serverMap.clear();
      searchMap.clear();
      searchEntriesToDo.clear();
      timestamp             = 0;
      count                 = 0;
      connectedWithServerId = -1;
      tryConnectToServer    = -1;
      downloadChanged       = true;
      uploadChanged         = true;
      serverChanged         = true;
      searchChanged         = true;
      }
      if(removedEvents.size() > 0)
      {
         downloadPropertyChangeInformer.propertyChanged(new DownloadDataPropertyChangeEvent(removedEvents));
      }
   }

   public boolean isSearchChanged()
   {
      return searchChanged;
   }

   public boolean isDownloadChanged()
   {
      return downloadChanged;
   }

   public boolean isUploadChanged()
   {
      return uploadChanged;
   }

   public boolean isServerChanged()
   {
      return serverChanged;
   }

   public boolean isSpeedChanged()
   {
      return speedChanged;
   }

   public boolean isInformationChanged()
   {
      return informationChanged;
   }

   public boolean isNetworkInfoChanged()
   {
      return networkInfoChanged;
   }
}
