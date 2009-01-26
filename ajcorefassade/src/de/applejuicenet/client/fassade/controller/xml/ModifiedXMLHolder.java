/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import java.io.CharArrayWriter;
import java.io.StringReader;

import java.util.HashMap;
import java.util.HashSet;
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
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.fassade.event.DownloadDataPropertyChangeEvent;
import de.applejuicenet.client.fassade.exception.CoreLostException;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.NetworkInfo;
import de.applejuicenet.client.fassade.shared.StringConstants;

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
public class ModifiedXMLHolder extends DefaultHandler
{
   private final CoreConnectionSettingsHolder coreHolder;
   private Map<String, Download>              sourcenZuDownloads = new HashMap<String, Download>();
   private XMLReader                          xr                 = null;
   private Map<String, Server>                serverMap          = new HashMap<String, Server>();
   private Map<String, Download>              downloadMap        = new HashMap<String, Download>();
   private Map<String, Upload>                uploadMap          = new HashMap<String, Upload>();
   private Map<String, SearchDO>              searchMap          = new HashMap<String, SearchDO>();
   private NetworkInfo                        netInfo;
   private InformationDO                      information;
   private int                                count              = 0;
   private String                             filter             = "";
   private String                             sessionKontext     = null;
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
   private ApplejuiceFassade                  ajFassade;
   private Map<String, Share>                 shareMap                       = null;
   private SearchEntryDO                      tmpSearchEntry                 = null;
   private Set<SearchEntryDO>                 searchEntriesToDo              = new HashSet<SearchEntryDO>();
   private Set<DownloadSourceDO>              downloadSourcesToDo            = new HashSet<DownloadSourceDO>();

   @SuppressWarnings("unchecked")
   public ModifiedXMLHolder(CoreConnectionSettingsHolder coreHolder, ApplejuiceFassade ajFassade)
   {
      this.coreHolder = coreHolder;
      this.ajFassade  = ajFassade;
      try
      {
         init();
         xmlCommand = "/xml/modified.xml";
         Class parser = SAXParser.class;

         xr = XMLReaderFactory.createXMLReader(parser.getName());
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

   public Map<String, Server> getServer()
   {
      return serverMap;
   }

   public Map<String, Upload> getUploads()
   {
      return uploadMap;
   }

   public Map<String, Download> getDownloads()
   {
      return downloadMap;
   }

   public Map<String, SearchDO> getSearchs()
   {
      return searchMap;
   }

   public NetworkInfo getNetworkInfo()
   {
      return netInfo;
   }

   public boolean update()
   {
      if(reloadInProgress)
      {
         return false;
      }
      else
      {
         switch(count)
         {

            // lazy loading
            case 0:
            {
               count++;
               filter = "&filter=ids;down;uploads;server;informations;search&mode=zip";
               break;
            }

            case 1:
            {
               count++;
               filter = "&filter=ids;informations;user;search&mode=zip";
               break;
            }

            case 2:
            {
               count++;
               filter = ""; // kein Filter
               break;
            }

            default:
               break;
         }

         reload();
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
            information.setCredits(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.SESSION_UPLOAD))
         {
            information.setSessionUpload(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.SESSION_DOWNLOAD))
         {
            information.setSessionDownload(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.UPLOAD_SPEED))
         {
            information.setUploadSpeed(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals(StringConstants.DOWNLOAD_SPEED))
         {
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
      netInfo.setAjUserGesamt(Long.parseLong((String) userAttributes.get(StringConstants.USERS)));
      netInfo.setAjAnzahlDateien(Long.parseLong((String) userAttributes.get(StringConstants.FILES)));
      netInfo.setAjGesamtShare((String) userAttributes.get(StringConstants.FILESIZE));
      netInfo.setFirewalled(((String) userAttributes.get(StringConstants.FIREWALLED)).equals(StringConstants.TRUE));
      netInfo.setExterneIP((String) userAttributes.get(StringConstants.IP));
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
      serverDO.setVersuche(Integer.parseInt((String) userAttributes.get(StringConstants.CONNECTIONTRY)));
   }

   private void checkServerAttributes(Attributes attr)
   {
      attributes.clear();
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         attributes.put(attr.getLocalName(i), attr.getValue(i));
      }

      String   key      = (String) attributes.get(StringConstants.ID);
      int      id       = Integer.parseInt(key);
      ServerDO serverDO;

      if(serverMap.containsKey(key))
      {
         serverDO = (ServerDO) serverMap.get(key);
      }
      else
      {
         serverDO = new ServerDO(id);
         serverMap.put(key, serverDO);
      }

      checkServerMap(serverDO, attributes);
      attributes.clear();
   }

   @SuppressWarnings("unchecked")
   private void checkUploadMap(UploadDO uploadDO, Map userAttributes)
   {
      uploadDO.setShareFileID(Integer.parseInt((String) userAttributes.get(StringConstants.SHAREID)));
      uploadDO.setStatus(Integer.parseInt((String) userAttributes.get(StringConstants.STATUS)));
      uploadDO.setDirectState(Integer.parseInt((String) userAttributes.get(StringConstants.DIRECTSTATE)));
      uploadDO.setPrioritaet(Integer.parseInt((String) userAttributes.get(StringConstants.PRIORITY)));
      uploadDO.setUploadFrom(Integer.parseInt((String) userAttributes.get(StringConstants.UPLOADFROM)));
      uploadDO.setActualUploadPosition(Integer.parseInt((String) userAttributes.get(StringConstants.ACTUALUPLOADPOSITION)));
      uploadDO.setUploadTo(Integer.parseInt((String) userAttributes.get(StringConstants.UPLOADTO)));
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

      String   key = (String) attributes.get(StringConstants.ID);
      int      id = Integer.parseInt(key);

      UploadDO uploadDO;

      if(uploadMap.containsKey(key))
      {
         uploadDO = (UploadDO) uploadMap.get(key);
      }
      else
      {
         uploadDO = new UploadDO(id);
         uploadMap.put(key, uploadDO);
      }

      checkUploadMap(uploadDO, attributes);
      attributes.clear();
      if(shareMap == null)
      {
         shareMap = ajFassade.getShare(false);
      }

      ShareDO shareDO = (ShareDO) shareMap.get(uploadDO.getShareFileIDAsString());

      if(shareDO != null)
      {
         uploadDO.setDateiName(shareDO.getShortfilename());
      }
      else
      {
         // wenns die passende Sharedatei aus irgendeinem Grund nicht geben
         // sollte,
         // wird dieser Upload auch nicht angezeigt
         uploadMap.remove(key);
      }
   }

   @SuppressWarnings("unchecked")
   private void checkUserMap(DownloadSourceDO downloadSourceDO, Map userAttributes)
   {
      downloadSourceDO.setStatus(Integer.parseInt((String) userAttributes.get(StringConstants.STATUS)));
      downloadSourceDO.setDirectstate(Integer.parseInt((String) userAttributes.get(StringConstants.DIRECTSTATE)));
      downloadSourceDO.setDownloadFrom(Integer.parseInt((String) userAttributes.get(StringConstants.DOWNLOADFROM)));
      downloadSourceDO.setDownloadTo(Integer.parseInt((String) userAttributes.get(StringConstants.DOWNLOADTO)));
      downloadSourceDO.setActualDownloadPosition(Integer.parseInt((String) userAttributes.get(StringConstants.ACTUALDOWNLOADPOSITION)));
      downloadSourceDO.setSpeed(Integer.parseInt((String) userAttributes.get(StringConstants.SPEED)));
      downloadSourceDO.setQueuePosition(Integer.parseInt((String) userAttributes.get(StringConstants.QUEUEPOSITION)));
      downloadSourceDO.setPowerDownload(Integer.parseInt((String) userAttributes.get(StringConstants.POWERDOWNLOAD)));
      downloadSourceDO.setFilename((String) userAttributes.get(StringConstants.FILENAME));
      downloadSourceDO.setNickname((String) userAttributes.get(StringConstants.NICKNAME));
      String versionNr = (String) userAttributes.get(StringConstants.VERSION);

      downloadSourceDO.setHerkunft(Integer.parseInt((String) userAttributes.get(StringConstants.SOURCE)));
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

      int              id          = Integer.parseInt((String) attributes.get(StringConstants.ID));
      String           downloadKey = (String) attributes.get(StringConstants.DOWNLOADID);
      int              downloadId  = Integer.parseInt(downloadKey);

      DownloadDO       downloadDO       = (DownloadDO) downloadMap.get(downloadKey);
      DownloadSourceDO downloadSourceDO = null;

      if(downloadDO != null)
      {
         downloadSourceDO = downloadDO.getSourceById(id);
         if(downloadSourceDO == null)
         {
            downloadSourceDO = new DownloadSourceDO(id);
            downloadDO.addSource(downloadSourceDO);
            sourcenZuDownloads.put(Integer.toString(id), downloadDO);
         }
      }
      else
      {
         downloadSourceDO = new DownloadSourceDO(id);
         downloadSourcesToDo.add(downloadSourceDO);
      }

      downloadSourceDO.setDownloadId(downloadId);
      checkUserMap(downloadSourceDO, attributes);
      attributes.clear();
      downloadSourceEvent = true;
   }

   @SuppressWarnings("unchecked")
   private void checkDownloadMap(DownloadDO downloadDO, Map userAttributes, boolean newDownload)
   {
      downloadDO.setShareId(Integer.parseInt((String) userAttributes.get(StringConstants.SHAREID)));
      downloadDO.setGroesse(Integer.parseInt((String) userAttributes.get(StringConstants.SIZE)));
      downloadDO.setHash((String) userAttributes.get(StringConstants.HASH));
      downloadDO.setTemporaryFileNumber(Integer.parseInt((String) userAttributes.get(StringConstants.TEMPORARYFILENUMBER)));
      if(newDownload)
      {
         downloadDO.setStatus(Integer.parseInt((String) userAttributes.get(StringConstants.STATUS)));
         downloadDO.setFilename((String) userAttributes.get(StringConstants.FILENAME));
         downloadDO.setTargetDirectory((String) userAttributes.get(StringConstants.TARGETDIRECTORY));
         downloadDO.setPowerDownload(Integer.parseInt((String) userAttributes.get(StringConstants.POWERDOWNLOAD)));
         downloadDO.setReady(Integer.parseInt((String) userAttributes.get(StringConstants.READY)));
      }
      else
      {
         int    tmpInt = Integer.parseInt((String) userAttributes.get(StringConstants.STATUS));
         String old;

         if(tmpInt != downloadDO.getStatus())
         {
            old = Integer.toString(downloadDO.getStatus());
            downloadDO.setStatus(tmpInt);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.STATUS_CHANGED, old,
                                                                   Integer.toString(tmpInt)));
         }

         String tmpString = (String) userAttributes.get(StringConstants.FILENAME);

         old = downloadDO.getFilename();
         if(old == null || !old.equals(tmpString))
         {
            downloadDO.setFilename(tmpString);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.FILENAME_CHANGED,
                                                                   old, tmpString));
         }

         tmpString = (String) userAttributes.get(StringConstants.TARGETDIRECTORY);
         old       = downloadDO.getTargetDirectory();
         if(old == null || !old.equals(tmpString))
         {
            downloadDO.setTargetDirectory(tmpString);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.DIRECTORY_CHANGED,
                                                                   old, tmpString));
         }

         tmpInt = Integer.parseInt((String) userAttributes.get(StringConstants.POWERDOWNLOAD));
         if(tmpInt != downloadDO.getPowerDownload())
         {
            old = Integer.toString(downloadDO.getPowerDownload());
            downloadDO.setPowerDownload(tmpInt);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.PWDL_CHANGED, old,
                                                                   Integer.toString(tmpInt)));
         }

         int tmpLong = Integer.parseInt((String) userAttributes.get(StringConstants.READY));

         if(tmpLong != downloadDO.getReady())
         {
            old = Integer.toString(downloadDO.getReady());
            downloadDO.setReady(tmpLong);
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadDO, DownloadDataPropertyChangeEvent.READY_CHANGED, old,
                                                                   Long.toString(tmpLong)));
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

      String     key         = (String) attributes.get(StringConstants.ID);
      int        id          = Integer.parseInt(key);
      DownloadDO downloadDO;
      boolean    newDownload;

      if(downloadMap.containsKey(key))
      {
         downloadDO  = (DownloadDO) downloadMap.get(key);
         newDownload = false;
      }
      else
      {
         downloadDO = new DownloadDO(id);
         downloadMap.put(key, downloadDO);
         newDownload = true;
      }

      checkDownloadMap(downloadDO, attributes, newDownload);
      attributes.clear();
      if(newDownload)
      {
         downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap, DownloadDataPropertyChangeEvent.DOWNLOAD_ADDED, null,
                                                                downloadDO));
      }
   }

   private void removeDownload(String id)
   {
      Download download = downloadMap.get(id);

      if(download != null)
      {
         for(DownloadSource curDownloadSource : download.getSources())
         {
            sourcenZuDownloads.remove(Integer.toString(curDownloadSource.getId()));
         }

         downloadMap.remove(id);
         downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap, DownloadDataPropertyChangeEvent.DOWNLOAD_REMOVED,
                                                                download, null));
      }
   }

   private void checkRemovedAttributes(Attributes attr)
   {
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.ID))
         {
            String id = attr.getValue(i);

            removeDownload(id);
            uploadMap.remove(id);
            serverMap.remove(id);
            if(sourcenZuDownloads.containsKey(id))
            {
               DownloadDO downloadDO = (DownloadDO) sourcenZuDownloads.get(id);

               downloadDO.removeSource(id);
               sourcenZuDownloads.remove(id);
               downloadSourceEvent = true;
               continue;
            }
            else if(searchMap.containsKey(id))
            {
               searchMap.remove(id);
               Search.currentSearchCount = searchMap.size();
               continue;
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void checkSearchMap(SearchDO aSearch, Map userAttributes)
   {
      aSearch.setSuchText((String) userAttributes.get(StringConstants.SEARCHTEXT));
      aSearch.setOffeneSuchen(Integer.parseInt((String) userAttributes.get(StringConstants.OPENSEARCHES)));
      aSearch.setGefundenDateien(Integer.parseInt((String) userAttributes.get(StringConstants.FOUNDFILES)));
      aSearch.setDurchsuchteClients(Integer.parseInt((String) userAttributes.get(StringConstants.SUMSEARCHES)));
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

      String   key     = (String) attributes.get(StringConstants.ID);
      int      id      = Integer.parseInt(key);
      SearchDO aSearch;

      if(searchMap.containsKey(key))
      {
         aSearch = searchMap.get(key);
         aSearch.setChanged(false);
      }
      else
      {
         aSearch = new SearchDO(id);
         searchMap.put(key, aSearch);
      }

      checkSearchMap(aSearch, attributes);
      attributes.clear();
   }

   private void checkSearchEntryAttributes(Attributes attr)
   {
      int    searchId = -1;
      int    id       = -1;
      String checksum = "";
      long   groesse  = -1;

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
            groesse = Long.parseLong(attr.getValue(i));
         }
      }

      String   key     = Integer.toString(searchId);
      SearchDO aSearch;

      if(searchMap.containsKey(key))
      {
         aSearch        = (SearchDO) searchMap.get(key);
         tmpSearchEntry = (SearchEntryDO) aSearch.getSearchEntryById(id);
         if(tmpSearchEntry == null)
         {
            tmpSearchEntry = aSearch.new SearchEntryDO(id, searchId, checksum, groesse);
            aSearch.addSearchEntry(tmpSearchEntry);
         }
      }
      else
      {
         tmpSearchEntry = new SearchDO(-1).new SearchEntryDO(id, searchId, checksum, groesse);
         searchEntriesToDo.add(tmpSearchEntry);
      }
   }

   private void checkSearchEntryFilenameAttributes(Attributes attr)
   {
      if(tmpSearchEntry == null)
      {
         return;
      }

      int    haeufigkeit = -1;
      String dateiName = "";

      int    length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.NAME))
         {
            dateiName = attr.getValue(i);
         }
         else if(attr.getLocalName(i).equals(StringConstants.USER))
         {
            haeufigkeit = Integer.parseInt(attr.getValue(i));
         }
      }

      FileNameDO filename = tmpSearchEntry.new FileNameDO(dateiName, haeufigkeit);

      tmpSearchEntry.addFileName(filename);
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      contents.reset();
      if(localName.equals(StringConstants.DOWNLOAD))
      {
         checkDownloadAttributes(attr);
      }
      else if(localName.equals(StringConstants.UPLOAD))
      {
         checkUploadAttributes(attr);
      }
      else if(localName.equals(StringConstants.SERVER))
      {
         checkServerAttributes(attr);
      }
      else if(localName.equals(StringConstants.INFORMATION))
      {
         checkInformationAttributes(attr);
      }
      else if(localName.equals(StringConstants.NETWORKINFO))
      {
         checkNetworkInfoAttributes(attr);
      }
      else if(localName.equals(StringConstants.OBJECT))
      {
         checkRemovedAttributes(attr);
      }
      else if(localName.equals(StringConstants.USER))
      {
         checkUserAttributes(attr);
      }
      else if(localName.equals(StringConstants.SEARCH))
      {
         checkSearchAttributes(attr);
      }
      else if(localName.equals(StringConstants.SEARCHENTRY))
      {
         checkSearchEntryAttributes(attr);
      }
      else if(localName.equals(StringConstants.FILENAME))
      {
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
      command.append(sessionKontext);
      xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET, command.toString());
      if(xmlData.length() == 0)
      {
         throw new IllegalArgumentException();
      }

      return xmlData;
   }

   private int updateTryConnect()
   {
      int      verbindungsStatus = Information.NICHT_VERBUNDEN;
      ServerDO serverDO = null;

      if(tryConnectToServer != -1)
      {
         Object alterServer = serverMap.get(Integer.toString(tryConnectToServer));

         if(alterServer != null)
         {
            ((ServerDO) alterServer).setTryConnect(false);
         }

         information.setServer(null);
      }

      if(netInfo.getTryConnectToServer() != -1)
      {
         serverDO          = (ServerDO) serverMap.get(Integer.toString(netInfo.getTryConnectToServer()));
         verbindungsStatus = Information.VERSUCHE_ZU_VERBINDEN;
         if(serverDO != null)
         {
            serverDO.setTryConnect(true);
         }
      }

      tryConnectToServer = netInfo.getTryConnectToServer();
      information.setServer(serverDO);
      information.setVerbindungsStatus(verbindungsStatus);
      return verbindungsStatus;
   }

   private int updateConnected()
   {
      int      verbindungsStatus = Information.NICHT_VERBUNDEN;
      ServerDO serverDO = null;

      if(connectedWithServerId != -1)
      {
         Object alterServer = serverMap.get(Integer.toString(connectedWithServerId));

         if(alterServer != null)
         {
            ((ServerDO) alterServer).setConnected(false);
         }

         information.setServer(null);
      }

      if(netInfo.getConnectedWithServerId() != -1)
      {
         serverDO          = (ServerDO) serverMap.get(Integer.toString(netInfo.getConnectedWithServerId()));
         verbindungsStatus = Information.VERBUNDEN;
         if(serverDO != null)
         {
            serverDO.setConnected(true);
         }
      }

      connectedWithServerId = netInfo.getConnectedWithServerId();
      information.setServer(serverDO);
      information.setVerbindungsStatus(verbindungsStatus);
      return verbindungsStatus;
   }

   private void parseRest()
   {
      int verbindungsStatus = Information.NICHT_VERBUNDEN;

      if(tryConnectToServer != netInfo.getTryConnectToServer())
      {
         verbindungsStatus = updateTryConnect();
      }

      if(connectedWithServerId != netInfo.getConnectedWithServerId())
      {
         verbindungsStatus = updateConnected();
      }

      information.setExterneIP(netInfo.getExterneIP());

      if(searchEntriesToDo.size() > 0)
      {
         SearchDO      aSearch;
         SearchEntryDO searchEntry;
         String        searchKey;

         for(SearchEntryDO curSearchEntryDO : searchEntriesToDo)
         {
            searchKey = Integer.toString(curSearchEntryDO.getSearchId());
            if(!searchMap.containsKey(searchKey))
            {
               continue;
            }

            aSearch = (SearchDO) searchMap.get(searchKey);
            aSearch.addSearchEntry(curSearchEntryDO);
         }

         searchEntriesToDo.clear();
      }

      if(downloadSourcesToDo.size() > 0)
      {
         DownloadDO downloadDO;
         String     downloadKey;

         for(DownloadSourceDO downloadSourceDO : downloadSourcesToDo)
         {
            downloadKey = Integer.toString(downloadSourceDO.getDownloadId());

            if(!downloadMap.containsKey(downloadKey))
            {
               continue;
            }

            downloadDO = (DownloadDO) downloadMap.get(downloadKey);
            downloadDO.addSource(downloadSourceDO);
            sourcenZuDownloads.put(Integer.toString(downloadSourceDO.getId()), downloadDO);

         }

         downloadSourcesToDo.clear();
      }
   }

   private void checkForValidSession()
   {
      if(sessionKontext == null)
      {
         SessionXMLHolder sessionHolder = new SessionXMLHolder(coreHolder);
         String           sessionId = sessionHolder.getNewSessionId();

         sessionKontext = "&session=" + sessionId;
      }
   }

   public void reload()
   {
      boolean reloadSession = false;

      //      Securer securer = new Securer();
      try
      {
         reloadInProgress = true;
         checkForValidSession();

         //         securer.start();
         String xmlString = getXMLString(filter);

         //         if(!securer.isInterrupted())
         //         {
         //            securer.interrupt();
         //         }
         downloadEvents.clear();
         if(filter.indexOf("down;") != -1)
         {
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap,
                                                                   DownloadDataPropertyChangeEvent.DOWNLOADMAP_CHECKED, null, null));
         }

         downloadSourceEvent = false;
         for(Search curSearch : searchMap.values())
         {
            curSearch.setChanged(false);
         }

         xr.parse(new InputSource(new StringReader(xmlString)));
         parseRest();

         if(checkCount < 2)
         {
            checkCount++;
         }

         if(downloadSourceEvent)
         {
            downloadEvents.add(new DownloadDataPropertyChangeEvent(downloadMap, DownloadDataPropertyChangeEvent.A_SOURCE_CHANGED,
                                                                   null, null));

         }

         if(downloadEvents.size() > 0)
         {
            downloadPropertyChangeInformer.propertyChanged(new DownloadDataPropertyChangeEvent(downloadEvents));
         }

         reloadInProgress = false;
      }
      catch(WebSiteNotFoundException webSiteNotFound)
      {
         reloadSession = true;
      }
      catch(IllegalArgumentException webSiteNotFound)
      {
         reloadSession = true;
      }
      catch(RuntimeException rE)
      {

         //         if(!securer.isInterrupted())
         //         {
         //            securer.interrupt();
         //         }
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
            SessionXMLHolder sessionHolder = new SessionXMLHolder(coreHolder);
            String           sessionId = sessionHolder.getNewSessionId();

            reloadInProgress = false;
            sessionKontext   = "&session=" + sessionId;
         }
         catch(CoreLostException clE)
         {
            reloadInProgress = false;
            throw clE;
         }
      }
   }

   private boolean secureSession()
   {
      try
      {

         //         return securerHolder.secure(sessionKontext, information);
         return true;
      }
      catch(Exception ex)
      {
         return false;
      }
   }

   private class Securer extends Thread
   {
      private boolean ok = true;

      public Securer()
      {
         setName("SessionLifeThread");
         setDaemon(true);
      }

      public void run()
      {
         while(true)
         {
            try
            {
               sleep(10000);
               if(!secureSession())
               {
                  ok = false;
                  break;
               }
            }
            catch(InterruptedException ex)
            {
               break;
            }
         }
      }

      public boolean isOK()
      {
         return ok;
      }
   }
}
