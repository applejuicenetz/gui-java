package de.applejuicenet.client.gui.controller;

import java.util.*;

import org.w3c.dom.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/ModifiedXMLHolder.java,v 1.18 2003/08/14 20:08:42 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ModifiedXMLHolder.java,v $
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
 * Historie eingefï¿½gt.
 *
 *
 */

public class ModifiedXMLHolder
    extends WebXMLParser {
  private HashSet serverIDs = new HashSet();
  private HashSet downloadIDs = new HashSet();
  private HashSet uploadIDs = new HashSet();

  private HashMap sourcenZuDownloads = new HashMap();

  private HashMap serverMap = new HashMap();
  private HashMap downloadMap = new HashMap();
  private HashMap uploadMap = new HashMap();
  private NetworkInfo netInfo;
  private String[] status = new String[5];
  int i = 0;

  private int connectedWithServerId = -1;
  private int tryConnectToServer = -1;

  public ModifiedXMLHolder() {
    super("/xml/modified.xml", "");
  }

  public HashMap getServer() {
    //updateServer();
    return serverMap;
  }

  public HashMap getUploads() {
    updateUploads();
    return uploadMap;
  }

  public HashMap getDownloads() {
    updateDownloads();
    return downloadMap;
  }

  public NetworkInfo getNetworkInfo() {
    updateNetworkInfo();
    return netInfo;
  }

  public void update() {
    reload("");
    updateIDs();
    updateServer();
    updateDownloads();
    updateNetworkInfo();
    updateUploads();
  }

  public String[] getStatusBar() {
    NodeList nodes = document.getElementsByTagName("information");
      try{
    if (tryConnectToServer!=-1){
        ServerDO serverDO = (ServerDO) serverMap.get(new MapSetStringKey(Integer.toString(tryConnectToServer)));
        status[0] = "Verbinde...";
        status[1] = serverDO.getName();
    }
    else if (connectedWithServerId != -1){
        ServerDO serverDO = (ServerDO) serverMap.get(new MapSetStringKey(Integer.toString(connectedWithServerId)));
        status[0] = "Verbunden...";
        status[1] = serverDO.getName();
    }
    else{
        status[0] = "Nicht verbunden";
        status[1] = "<Kein Server>";
    }
          }
      catch(Exception ex){
          ex.printStackTrace();
      }
    status[3] = netInfo.getExterneIP();
    if (nodes.getLength() == 0) {
      return status; //Keine Verï¿½nderung seit dem letzten Abrufen
    }
    Element e = (Element) nodes.item(0); //Es gibt nur ein information-Element
    long credits = Long.parseLong(e.getAttribute("credits"));
    long up = Long.parseLong(e.getAttribute("sessionupload"));
    long down = Long.parseLong(e.getAttribute("sessiondownload"));
    status[2] = "in: " + creditsUmrechnen(down) + " out: " + creditsUmrechnen(up);
    status[3] = netInfo.getExterneIP();
    status[4] = "Credits: " + creditsUmrechnen(credits);

    return status;
  }

  private String creditsUmrechnen(long bytes) {
    if (bytes == 0) {
      return "0 MB";
    }
    long faktor = 1;
    if (bytes < 1024l) {
      faktor = 1;
    }
    else if (bytes / 1024l < 1024l) {
      faktor = 1024l;
    }
    else if (bytes / 1048576l < 1024l) {
      faktor = 1048576l;
    }
    else if (bytes / 1073741824l < 1024l) {
      faktor = 1073741824l;
    }
    else {
      faktor = 1099511627776l;
    }
    double umgerechnet = (double)bytes / (double)faktor;
    String result = Double.toString(umgerechnet);
    int pos = result.indexOf(".");
    if (pos != -1) {
      if (pos + 2 < result.length())
        result = result.substring(0, pos + 3);
      result = result.replace('.', ',');
    }
    if (faktor == 1) {
      result += " Bytes";
    }
    else if (faktor == 1024l) {
      result += " kb";
    }
    else if (faktor == 1048576l) {
      result += " MB";
    }
    else if (faktor == 1073741824l) {
      result += " GB";
    }
    else {
      result += " TB";
    }
    return result;
  }

  private String bytesUmrechnen(long bytes) {
    if (bytes == 0) {
      return "0 Bytes/s";
    }
    long faktor = 1;
    if (bytes < 1024) {
      faktor = 1;
    }
    else {
      faktor = 1024;
    }
    bytes = bytes / faktor;
    String result = Double.toString(bytes);
    int pos = result.indexOf(".");
    if (pos != -1) {
      if (pos + 2 < result.length())
        result = result.substring(0, pos + 3);
      result = result.replace('.', ',');
    }
    if (faktor == 1) {
      result += " Bytes/s";
    }
    else {
      result += " kb/s";
    }
    return result;
  }

  private void updateIDs(){
      serverIDs.clear();
      uploadIDs.clear();
      downloadIDs.clear();
      sourcenZuDownloads.clear();
      Element e = null;
      String id = null;
      NodeList nodes = document.getElementsByTagName("serverid");
      int size = nodes.getLength();
      for (int i = 0; i < size; i++) {
        e = (Element) nodes.item(i);
        id = e.getAttribute("id");
        serverIDs.add(new MapSetStringKey(id));
      }
      nodes = document.getElementsByTagName("uploadid");
      size = nodes.getLength();
      for (int i = 0; i < size; i++) {
        e = (Element) nodes.item(i);
        id = e.getAttribute("id");
        uploadIDs.add(new MapSetStringKey(id));
      }
      nodes = document.getElementsByTagName("downloadid");
      NodeList userNodes;
      size = nodes.getLength();
      int userSize;
      Element userElement = null;
      MapSetStringKey downloadKey;
      String userId;
      for (int i = 0; i < size; i++) {
        e = (Element) nodes.item(i);
        id = e.getAttribute("id");
        downloadKey = new MapSetStringKey(id);
        downloadIDs.add(downloadKey);
        userNodes = e.getElementsByTagName("userid");
        userSize = userNodes.getLength();
        for (int x=0; x<userSize; x++){
            userElement = (Element) userNodes.item(x);
            userId = userElement.getAttribute("id");
            sourcenZuDownloads.put(new MapSetStringKey(userId), downloadKey);
        }
      }
      Iterator it = downloadMap.keySet().iterator();
      MapSetStringKey key;
      ArrayList toRemoveDownload = new ArrayList();
      ArrayList toRemoveSources = new ArrayList();
      DownloadDO downloadDO = null;
      DownloadSourceDO[] sources = null;
      while (it.hasNext()){
          key = (MapSetStringKey)it.next();
          if (!downloadIDs.contains(key)){
              toRemoveDownload.add(key);
          }
          else{
              downloadDO = (DownloadDO) downloadMap.get(key);
              sources = downloadDO.getSources();
              if (sources!=null){
                for (int x=0; x<sources.length; x++){
                    key = new MapSetStringKey(sources[x].getId());
                    if (!sourcenZuDownloads.containsKey(key)){
                        toRemoveSources.add(key);
                    }
                }
              }
          }
      }
      size = toRemoveDownload.size();
      for (int i=0; i<size; i++){
          downloadMap.remove(toRemoveDownload.get(i));
      }
      size = toRemoveSources.size();
      for (int i=0; i<size; i++){
          key = (MapSetStringKey)toRemoveSources.get(i);
          downloadDO = (DownloadDO)sourcenZuDownloads.get(key);
          if (downloadDO!=null){
            downloadDO.removeSource(
                    key.getValue());
          }
      }
  }

  private void updateDownloads() {
      Element e = null;
      String id = null;
      String shareid = null;
      String hash = null;
      Long fileSize = null;
      Long sizeReady = null;
      String temp = null;
      int status;
      String filename = null;
      String targetDirectory = null;
      int powerDownload;
      int temporaryFileNumber;
      NodeList nodes = document.getElementsByTagName("download");
      int size = nodes.getLength();
      DownloadDO downloadDO = null;
      for (int i = 0; i < size; i++) {
        e = (Element) nodes.item(i);
        id = e.getAttribute("id");
        MapSetStringKey key = new MapSetStringKey(id);
        if (downloadMap.containsKey(key)) {
            downloadDO = (DownloadDO)downloadMap.get(key);
            downloadDO.setShareId(e.getAttribute("shareid"));
            downloadDO.setHash(e.getAttribute("hash"));
            downloadDO.setGroesse(new Long(e.getAttribute("size")));
            downloadDO.setReady(new Long(e.getAttribute("ready")));
            temp = e.getAttribute("status");
            downloadDO.setStatus(Integer.parseInt(temp));
            downloadDO.setFilename(e.getAttribute("filename"));
            downloadDO.setTargetDirectory(e.getAttribute("targetdirectory"));
            temp = e.getAttribute("powerdownload");
            downloadDO.setPowerDownload(Integer.parseInt(temp));
            temp = e.getAttribute("temporaryfilenumber");
            downloadDO.setTemporaryFileNumber(Integer.parseInt(temp));
        }
        else{
            shareid = e.getAttribute("shareid");
            hash = e.getAttribute("hash");
            fileSize = new Long(e.getAttribute("size"));
            sizeReady = new Long(e.getAttribute("ready"));
            temp = e.getAttribute("status");
            status = Integer.parseInt(temp);
            filename = e.getAttribute("filename");
            targetDirectory = e.getAttribute("targetdirectory");
            temp = e.getAttribute("powerdownload");
            powerDownload = Integer.parseInt(temp);
            temp = e.getAttribute("temporaryfilenumber");
            temporaryFileNumber = Integer.parseInt(temp);

            downloadDO = new DownloadDO(id, shareid, hash, fileSize, sizeReady, status, filename,
                    targetDirectory, powerDownload, temporaryFileNumber);

            downloadMap.put(new MapSetStringKey(id), downloadDO);
        }
      }

      int directstate;
      nodes = document.getElementsByTagName("user");
      size = nodes.getLength();
      Integer downloadFrom = null;
      Integer downloadTo = null;
      Integer actualDownloadPosition = null;
      Integer speed = null;
      Version version = null;
      String versionNr = null;
      String nickname = null;
      int queuePosition;
      int os;
      DownloadSourceDO downloadSourceDO = null;
      for (int i = 0; i < size; i++) {
        e = (Element) nodes.item(i);
        id = e.getAttribute("id");
        temp = e.getAttribute("status");
        status = Integer.parseInt(temp);
        temp = e.getAttribute("directstate");
        directstate = Integer.parseInt(temp);
        if (status==DownloadSourceDO.UEBERTRAGUNG){
            temp = e.getAttribute("downloadfrom");
            downloadFrom = new Integer(Integer.parseInt(temp));
            temp = e.getAttribute("downloadto");
            downloadTo = new Integer(Integer.parseInt(temp));
            temp = e.getAttribute("actualdownloadposition");
            actualDownloadPosition = new Integer(Integer.parseInt(temp));
            temp = e.getAttribute("speed");
            speed = new Integer(Integer.parseInt(temp));
        }
        else{
          downloadFrom = null;
          downloadTo = null;
          actualDownloadPosition = null;
          speed = new Integer(0);
        }
        versionNr = e.getAttribute("version");
        if (versionNr.compareToIgnoreCase("0.0.0.0")==0){
            version = null;
        }
        else{
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
        downloadSourceDO = new DownloadSourceDO(id, status, directstate, downloadFrom, downloadTo, actualDownloadPosition,
                speed, version, queuePosition, powerDownload, filename, nickname);
        MapSetStringKey key = (MapSetStringKey) sourcenZuDownloads.get(new MapSetStringKey(id));
        downloadDO = (DownloadDO) downloadMap.get(key);
        if (downloadDO!=null){
            downloadDO.addOrAlterSource(downloadSourceDO);
        }
      }
  }

  private void updateUploads() {
    NodeList nodes = document.getElementsByTagName("upload");
    int size = nodes.getLength();
    Element e = null;
    String shareId = null;
    UploadDO upload = null;
    String id = null;
    int os;
    String versionsNr = null;
    Version version = null;
    Integer prioritaet = null;
    String nick = null;
    String status = null;
    Long uploadFrom = null;
    Long uploadTo = null;
    Long actualUploadPos = null;
    Integer speed = null;
    MapSetStringKey idKey = null;
    HashMap share = DataManager.getInstance().getShare(false);
    for (int i = 0; i < size; i++) {
      e = (Element) nodes.item(i);
      id = e.getAttribute("id");
      idKey = new MapSetStringKey(id);
      if (uploadMap.containsKey(idKey)) {
        upload = (UploadDO) uploadMap.get(idKey);
        upload.setShareFileID(new Integer(e.getAttribute("shareid")).intValue());
        upload.setPrioritaet(new Integer(e.getAttribute("priority")));
        upload.setNick(e.getAttribute("nick"));
        upload.setStatus(Integer.parseInt(e.getAttribute("status")));
        upload.setUploadFrom(new Long(e.getAttribute("uploadfrom")));
        upload.setUploadTo(new Long(e.getAttribute("uploadto")));
        upload.setActualUploadPosition(new Long(e.getAttribute("actualuploadposition")));
        upload.setSpeed(new Integer(e.getAttribute("speed")));
      }
      else {
        shareId = e.getAttribute("shareid");
        versionsNr = e.getAttribute("version");
        if (versionsNr.compareToIgnoreCase("0.0.0.0")==0){
            version = null;
        }
        else{
            os = Integer.parseInt(e.getAttribute("operatingsystem"));
            version = new Version(versionsNr, os);
        }
        prioritaet = new Integer(e.getAttribute("priority"));
        nick = e.getAttribute("nick");
        status = e.getAttribute("status");
        uploadFrom = new Long(e.getAttribute("uploadfrom"));
        uploadTo = new Long(e.getAttribute("uploadto"));
        actualUploadPos = new Long(e.getAttribute("actualuploadposition"));
        speed = new Integer(e.getAttribute("speed"));
        upload = new UploadDO(id, shareId, version, status, nick,
                                       uploadFrom, uploadTo, actualUploadPos,
                                       speed, prioritaet);
        ShareDO shareDO = (ShareDO)share.get(new MapSetStringKey(shareId));
        upload.setDateiName(shareDO.getShortfilename());
        uploadMap.put(idKey, upload);
      }
    }
  }

  private void updateServer() {
    NodeList nodes = document.getElementsByTagName("server");
    Iterator it = serverMap.keySet().iterator();
    MapSetStringKey idKey = null;
    ArrayList toRemove = new ArrayList();
    while (it.hasNext()){
        idKey = (MapSetStringKey) it.next();
        if (!serverIDs.contains(idKey))
        {
            toRemove.add(idKey);
        }
    }
    for (int i=0; i<toRemove.size(); i++){
        serverMap.remove(toRemove.get(i));
    }
    int size = nodes.getLength();
    Element e = null;
    String id_key = null;
    int id;
    String name = null;
    String host = null;
    long lastseen;
    String port = null;
    ServerDO server = null;
    for (int i = 0; i < size; i++) {
      e = (Element) nodes.item(i);
      id_key = e.getAttribute("id");
      id = Integer.parseInt(id_key);
      name = e.getAttribute("name");
      host = e.getAttribute("host");
      lastseen = Long.parseLong(e.getAttribute("lastseen"));
      port = e.getAttribute("port");
      server = new ServerDO(id, name, host, port, lastseen);
      serverMap.put(new MapSetStringKey(id_key), server);
    }
  }

  private void updateNetworkInfo() {
    NodeList nodes = document.getElementsByTagName("networkinfo");
    if (nodes.getLength() == 0) {
      return; //Keine Verï¿½nderung seit dem letzten Abrufen
    }
    Element e = (Element) nodes.item(0); //Es gibt nur ein Netzerkinfo-Element
    String users = e.getAttribute("users");
    String dateien = e.getAttribute("files");
    String dateigroesse = e.getAttribute("filesize");
    int tryConnectToServer = Integer.parseInt(e.getAttribute("tryconnecttoserver"));
    int connectedWithServerId = Integer.parseInt(e.getAttribute("connectedwithserverid"));
    boolean firewalled = (e.getAttribute("firewalled").compareToIgnoreCase(
        "true") == 0) ? true : false;
    String externeIP = e.getAttribute("ip");
    if (this.tryConnectToServer != tryConnectToServer){
        Object alterServer = serverMap.get(new MapSetStringKey(Integer.toString(this.tryConnectToServer)));
        if (alterServer!=null){
            ((ServerDO)alterServer).setTryConnect(false);
        }
        if (tryConnectToServer!=-1){
            ServerDO serverDO = (ServerDO) serverMap.get(new MapSetStringKey(Integer.toString(tryConnectToServer)));
            serverDO.setTryConnect(true);
        }
        this.tryConnectToServer = tryConnectToServer;
    }
    //if (this.connectedWithServerId != connectedWithServerId){
        Object alterServer = serverMap.get(new MapSetStringKey(Integer.toString(this.connectedWithServerId)));
        if (alterServer!=null){
            ((ServerDO)alterServer).setConnected(false);
        }
        if (connectedWithServerId!=-1){
            ServerDO serverDO = (ServerDO) serverMap.get(new MapSetStringKey(Integer.toString(connectedWithServerId)));
            serverDO.setConnected(true);
        }
        this.connectedWithServerId = connectedWithServerId;
    //}
    netInfo = new NetworkInfo(users, dateien, dateigroesse, firewalled,
                              externeIP, tryConnectToServer, connectedWithServerId);
  }
}