package de.applejuicenet.client.gui.controller;

import java.util.*;

import org.w3c.dom.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ModifiedXMLHolder
    extends WebXMPParser {
  private HashMap serverMap;
  private HashMap downloadMap;
  private HashMap uploadMap;
  private NetworkInfo netInfo;

  public ModifiedXMLHolder() {
    super("/xml/modified.xml", "");
  }

  public HashMap getServer() {
    updateServer();
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
    updateServer();
    updateDownloads();
    updateNetworkInfo();
    updateUploads();
  }

  private void updateDownloads() {
    if (downloadMap == null) {
      downloadMap = new HashMap();
      //Dummy-Implementierung
    }
    Version version = new Version("0.28.501", "Java",
                                  Version.WIN32);
    Version version2 = new Version("0.27.511", "Java",
                                   Version.LINUX);
    String versionText;
    DownloadSourceDO source = new DownloadSourceDO(false, "1000", "datei2.jpg",
        DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?",
        "1:1", version, "Maj0r", null);
    DownloadSourceDO source2 = new DownloadSourceDO(false, "1001", "datei3.jpg",
        DownloadSourceDO.VERSUCHEINDIREKT, "1GB", "nix", "0", "100", "0 Kb",
        "?", "1:1", version2, "Maj0r", null);
    HashSet sourcen1 = new HashSet();
    sourcen1.add(source);
    HashSet sourcen2 = new HashSet();
    sourcen2.add(source);
    sourcen2.add(source2);
    DownloadSourceDO[] downloads = new DownloadSourceDO[2];
    downloadMap.put("1002", new DownloadSourceDO(true, "1002", "dateiliste.mov",
                                                 DownloadSourceDO.UEBERTRAGE,
                                                 "1GB",
                                                 "nix", "43", "100", "0 Kb",
                                                 "?", "1:1", null,
                                                 "", sourcen1));
    downloadMap.put("1003", new DownloadSourceDO(true, "1003", "Film.avi",
                                                 DownloadSourceDO.WARTESCHLANGE,
                                                 "1GB",
                                                 "nix", "0", "100", "0 Kb", "?",
                                                 "1:1", null,
                                                 "", sourcen2));
    //Dummy-Ende

  }

  private void updateUploads() {
    if (uploadMap == null) {
      uploadMap = new HashMap();
    }
    NodeList nodes = document.getElementsByTagName("upload");
    HashMap changedUploads = new HashMap();
    for (int i = 0; i < nodes.getLength(); i++) {
      Element e = (Element) nodes.item(i);
      String shareId = e.getAttribute("shareid");
      if (uploadMap.containsKey(shareId)) {
        UploadDO upload = (UploadDO) uploadMap.get(shareId);
        upload.setPrioritaet(e.getAttribute("priority"));
        upload.setNick(e.getAttribute("nick"));
        upload.setStatus(Integer.parseInt(e.getAttribute("status")));
        upload.setUploadFrom(e.getAttribute("uploadfrom"));
        upload.setUploadTo(e.getAttribute("uploadto"));
        upload.setActualUploadPosition(e.getAttribute("actualuploadposition"));
        upload.setSpeed(e.getAttribute("speed"));
      }
      else {
        String id = e.getAttribute("id");
        int os = Integer.parseInt(e.getAttribute("operatingsystem"));
        String versionsNr = e.getAttribute("version");
        Version version = new Version(versionsNr, "Java", os);

        String prioritaet = e.getAttribute("priority");
        String nick = e.getAttribute("nick");
        String status = e.getAttribute("status");
        String uploadFrom = e.getAttribute("uploadfrom");
        String uploadTo = e.getAttribute("uploadto");
        String actualUploadPos = e.getAttribute("actualuploadposition");
        String speed = e.getAttribute("speed");
        UploadDO upload = new UploadDO(id, shareId, version, status, nick,
                                       uploadFrom, uploadTo, actualUploadPos,
                                       speed, prioritaet);
        changedUploads.put(shareId, upload);
      }
    }
    uploadMap.putAll(changedUploads);
  }

  private void updateServer() {
    if (serverMap == null) {
      serverMap = new HashMap();
    }
    NodeList nodes = document.getElementsByTagName("server");
    HashMap changedServer = new HashMap();
    for (int i = 0; i < nodes.getLength(); i++) {
      Element e = (Element) nodes.item(i);
      String id_key = e.getAttribute("id");
      int id = Integer.parseInt(id_key);
      String name = e.getAttribute("name");
      String host = e.getAttribute("host");
      long lastseen = Long.parseLong(e.getAttribute("lastseen"));
      String port = e.getAttribute("port");
      ServerDO server = new ServerDO(id, name, host, port, lastseen);
      changedServer.put(id_key, server);
    }
    serverMap.putAll(changedServer);
  }

  private void updateNetworkInfo() {
    NodeList nodes = document.getElementsByTagName("networkinfo");
    if (nodes.getLength() == 0) {
      return; //Keine Veränderung seit dem letzten Abrufen
    }
    Element e = (Element) nodes.item(0); //Es gibt nur ein Netzerkinfo-Element
    String users = e.getAttribute("users");
    String dateien = e.getAttribute("files");
    String dateigroesse = e.getAttribute("filesize");
    boolean firewalled = (e.getAttribute("firewalled").compareToIgnoreCase(
        "true") ==
                          0) ? true : false;
    String externeIP = e.getAttribute("ip");
    netInfo = new NetworkInfo(users, dateien, dateigroesse, firewalled,
                              externeIP);
  }
}