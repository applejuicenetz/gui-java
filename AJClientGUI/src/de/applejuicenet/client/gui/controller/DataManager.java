package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DataManager { //Singleton-Implementierung
  private HashSet downloadListener;
  private HashSet shareListener;
  private HashSet uploadListener;
  private HashSet serverListener;
  private static DataManager instance = null;
  private static int x = 0;
  private JLabel[] statusbar;
  private ModifiedXMLHolder modifiedXML = null;
  private InformationXMLHolder informationXML = null;
  private ShareXMLHolder shareXML = null;
  private SettingsXMLHolder settingsXML = null;
  private Version coreVersion;

  public void addDownloadListener(DataUpdateListener listener) {
    if (! (downloadListener.contains(listener))) {
      downloadListener.add(listener);
    }
  }

  public void addUploadListener(DataUpdateListener listener) {
    if (! (uploadListener.contains(listener))) {
      uploadListener.add(listener);
    }
  }

  public void addShareListener(DataUpdateListener listener) {
    if (! (shareListener.contains(listener))) {
      shareListener.add(listener);
    }
  }

  public void addServerListener(DataUpdateListener listener) {
    if (! (serverListener.contains(listener))) {
      serverListener.add(listener);
    }
  }

  private DataManager() {
    downloadListener = new HashSet();
    serverListener = new HashSet();
    uploadListener = new HashSet();
    shareListener = new HashSet();

    //load XMLs
    modifiedXML = new ModifiedXMLHolder();
    informationXML = new InformationXMLHolder();
    shareXML = new ShareXMLHolder();

    informationXML.reload("");
    String versionsTag = informationXML.getFirstAttrbuteByTagName(new String[] {
        "applejuice", "generalinformation", "version"}
        , true);
    coreVersion = new Version(versionsTag, "Java",
                              Version.getOSTypByOSName( (String) System.
        getProperties().get("os.name")));
  }

  public AJSettings getAJSettings() {
    if (settingsXML == null) {
      settingsXML = new SettingsXMLHolder();
    }
    return settingsXML.getAJSettings();
  }

  public boolean saveAJSettings(AJSettings ajSettings) {
    String parameters = "";
    try {
      parameters = "Nickname=" +
          URLEncoder.encode(ajSettings.getNick(), "UTF-8");
      parameters += "&XMLPort=" + Long.toString(ajSettings.getXMLPort());
      parameters += "&AllowBrowse=" +
          (ajSettings.isBrowseAllowed() ? "true" : "false");
      parameters += "&MaxUpload=" + Long.toString(ajSettings.getMaxUpload());
      parameters += "&MaxDownload=" + Long.toString(ajSettings.getMaxDownload());
      parameters += "&Speedperslot=" +
          Integer.toString(ajSettings.getSpeedPerSlot());
      parameters += "&Incomingdirectory=" +
          URLEncoder.encode(ajSettings.getIncomingDir(), "UTF-8");
      parameters += "&Temporarydirectory=" +
          URLEncoder.encode(ajSettings.getTempDir(), "UTF-8");
    }
    catch (UnsupportedEncodingException ex1) {
      int i = 0;
    }
    String result;
    try {
      result = HtmlLoader.getHtmlContent(getHost(), HtmlLoader.POST,
                                         "/function/setsettings?" + parameters);
    }
    catch (WebSiteNotFoundException ex) {
      return false;
    }
    return true;
  }

  public HashMap getAllServer() {
    return modifiedXML.getServer();
  }

  public void updateModifiedXML() {
    modifiedXML.update();
    informServerListener();
    informDownloadListener();
    informUploadListener();
  }

  public static boolean connectToServer(int id) {
    String result;
    try {
      result = HtmlLoader.getHtmlContent(getHost(), HtmlLoader.POST,
                                         "/function/serverlogin?id=" + id);
    }
    catch (WebSiteNotFoundException ex) {
      return false;
    }
    return true;
  }

  private static String getHost() {
    OptionsManager om = OptionsManager.getInstance();
    String savedHost = "localhost";
    if (om.getRemoteSettings().isRemoteUsed()) {
      savedHost = OptionsManager.getInstance().getRemoteSettings().getHost();
      if (savedHost.length() == 0) {
        savedHost = "localhost";
      }
    }
    return savedHost;
  }

  public NetworkInfo getNetworkInfo() {
    modifiedXML.update();
    return modifiedXML.getNetworkInfo();
  }

  public static boolean istCoreErreichbar() {
    try {
      String testData = HtmlLoader.getHtmlContent(getHost(), HtmlLoader.GET,
                                                  "/xml/information.xml");
    }
    catch (WebSiteNotFoundException ex) {
      return false;
    }
    return true;
  }

  public static DataManager getInstance() {
    if (instance == null) {
      instance = new DataManager();
    }
    return instance;
  }

  public Version getCoreVersion() {
    return coreVersion;
  }

  private void informDownloadListener() {
    HashMap content = modifiedXML.getDownloads();
    if (content.size() == 0) {
      return;
    }
    Iterator it = downloadListener.iterator();
    while (it.hasNext()) {
      ( (DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
          DOWNLOAD_CHANGED, content);
    }
  }

  private void informUploadListener() {
    HashMap content = modifiedXML.getUploads();
    if (content.size() == 0) {
      return;
    }
    Iterator it = uploadListener.iterator();
    while (it.hasNext()) {
      ( (DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
          UPLOAD_CHANGED, content);
    }
  }

  private void informShareListener() {
    HashMap content = shareXML.getShare();
    if (content.size() == 0) {
      return;
    }
    Iterator it = shareListener.iterator();
    while (it.hasNext()) {
      ( (DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
          SHARE_CHANGED, content);
    }
  }

  private void informServerListener() {
    HashMap content = modifiedXML.getServer();
    if (content.size() == 0) {
      return;
    }
    Iterator it = serverListener.iterator();
    while (it.hasNext()) {
      ( (DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
          SERVER_CHANGED, content);
    }
  }

  public void addStatusbarForListen(JLabel[] statusbar) {
    this.statusbar = statusbar;
    updateStatusbar();
  }

  public void updateStatusbar() {
    //dummy
    if (statusbar != null) {
      statusbar[0].setText("Nicht verbunden");
      statusbar[3].setText("Credits: 0,00 MB");
      statusbar[4].setText("Version 0.01PreA");
    }
  }

  public HashMap getDownloads() {
    return modifiedXML.getDownloads();
  }

  public HashMap getUploads() {
    return modifiedXML.getUploads();
  }

  public HashMap getShare() {
    return shareXML.getShare();
  }
}