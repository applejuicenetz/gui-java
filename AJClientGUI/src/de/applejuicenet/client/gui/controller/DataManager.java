package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/Attic/DataManager.java,v 1.20 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DataManager.java,v $
 * Revision 1.20  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DataManager { //Singleton-Implementierung
  private HashSet downloadListener;
  private HashSet shareListener;
  private HashSet uploadListener;
  private HashSet serverListener;
  private HashSet networkInfoListener;
  private static DataManager instance = null;
  private static int x = 0;
  private JLabel[] statusbar;
  private ModifiedXMLHolder modifiedXML = null;
  private InformationXMLHolder informationXML = null;
  private ShareXMLHolder shareXML = null;
  private SettingsXMLHolder settingsXML = null;
  private Version coreVersion;
  private Timer modifiedTimer;

  public void addDataUpdateListener(DataUpdateListener listener, int type) {
    HashSet listenerSet = null;
    if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
      listenerSet = downloadListener;
    }
    else if (type == DataUpdateListener.NETINFO_CHANGED) {
      listenerSet = networkInfoListener;
    }
    else if (type == DataUpdateListener.SERVER_CHANGED) {
      listenerSet = serverListener;
    }
    else if (type == DataUpdateListener.SHARE_CHANGED) {
      listenerSet = shareListener;
    }
    else if (type == DataUpdateListener.UPLOAD_CHANGED) {
      listenerSet = uploadListener;
    }
    else {
      return;
    }
    if (! (listenerSet.contains(listener))) {
      listenerSet.add(listener);
    }
  }

  private DataManager() {
    downloadListener = new HashSet();
    serverListener = new HashSet();
    uploadListener = new HashSet();
    shareListener = new HashSet();
    networkInfoListener = new HashSet();

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

    ActionListener modifiedAction = new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        updateModifiedXML();
      }
    };
    modifiedTimer = new Timer(1000, modifiedAction);
  }

  public void startXMLCheck() {
    if (!modifiedTimer.isRunning()) {
      modifiedTimer.start();
    }
  }

  public void stopXMLCheck() {
    if (modifiedTimer.isRunning()) {
      modifiedTimer.stop();
    }
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
    informDataUpdateListener(DataUpdateListener.SERVER_CHANGED);
    informDataUpdateListener(DataUpdateListener.DOWNLOAD_CHANGED);
    informDataUpdateListener(DataUpdateListener.UPLOAD_CHANGED);
    informDataUpdateListener(DataUpdateListener.NETINFO_CHANGED);
    updateStatusbar();
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

  private void informDataUpdateListener(int type) {
    if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
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
    else if (type == DataUpdateListener.UPLOAD_CHANGED) {
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
    else if (type == DataUpdateListener.SERVER_CHANGED) {
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
    else if (type == DataUpdateListener.SHARE_CHANGED) {
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
    else if (type == DataUpdateListener.NETINFO_CHANGED) {
      NetworkInfo content = modifiedXML.getNetworkInfo();
      Iterator it = networkInfoListener.iterator();
      while (it.hasNext()) {
        ( (DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.
            NETINFO_CHANGED, content);
      }
    }
  }

  public void addStatusbarForListen(JLabel[] statusbar) {
    this.statusbar = statusbar;
    updateStatusbar();
  }

  public void updateStatusbar() {
    //dummy
    String[] status = modifiedXML.getStatusBar();
    if (statusbar != null) {
      statusbar[0].setText(status[0]);
      statusbar[1].setText(status[1]);
      statusbar[2].setText(status[2]);
      statusbar[3].setText(status[3]);
      statusbar[4].setText(status[4]);
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