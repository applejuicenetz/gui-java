package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.Version;
import java.util.HashSet;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import java.util.Iterator;
import javax.swing.JLabel;
import de.applejuicenet.client.shared.HtmlLoader;
import de.applejuicenet.client.shared.exception.*;
import java.io.File;
import java.io.FileWriter;
import java.io.*;
import de.applejuicenet.client.shared.XMLDecoder;
import java.util.HashMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import de.applejuicenet.client.shared.dac.ServerDO;
import de.applejuicenet.client.shared.NetworkInfo;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DataManager {   //Singleton-Implementierung
  private DownloadSourceDO[] downloads;
  private HashSet downloadListener;
  private HashSet serverListener;
  private static DataManager instance = null;
  private static int x=0;
  private JLabel[] statusbar;
  private ModifiedXMLHolder modifiedXML = null;
  private InformationXMLHolder informationXML = null;
  private ShareXMLHolder shareXML = null;
  private Version coreVersion;

  public void addDownloadListener(DataUpdateListener listener){
    if (!(downloadListener.contains(listener)))
      downloadListener.add(listener);
  }

  public void addServerListener(DataUpdateListener listener){
    if (!(serverListener.contains(listener)))
      serverListener.add(listener);
  }

  private DataManager(){
    downloadListener = new HashSet();
    serverListener = new HashSet();

   //load XMLs
   modifiedXML = new ModifiedXMLHolder();
   reloadModifiedXML(false);
   informationXML = new InformationXMLHolder();
   shareXML = new ShareXMLHolder();

   String versionsTag = informationXML.getFirstAttrbuteByTagName(new String[]{"applejuice", "generalinformation", "version"}, true);
   coreVersion = new Version(versionsTag, "Java", Version.getOSTypByOSName((String) System.getProperties().get("os.name")));


   //Dummy-Implementierung
   Version version = new Version(getCoreVersion().getVersion(), "Java", Version.WIN32);
   Version version2 = new Version(getCoreVersion().getVersion(), "Java", Version.LINUX);
   String versionText;
   DownloadSourceDO source = new DownloadSourceDO(false, "datei2.jpg", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "Maj0r", null);
   DownloadSourceDO source2 = new DownloadSourceDO(false, "datei3.jpg", DownloadSourceDO.VERSUCHEINDIREKT, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version2, "Maj0r", null);
   HashSet sourcen1 = new HashSet();
   sourcen1.add(source);
   HashSet sourcen2 = new HashSet();
   sourcen2.add(source);
   sourcen2.add(source2);
   downloads = new DownloadSourceDO[2];
   downloads[0] = new DownloadSourceDO(true, "dateiliste.mov", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", null, "", sourcen1);
   downloads[1] = new DownloadSourceDO(true, "Film.avi", DownloadSourceDO.WARTESCHLANGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", null, "", sourcen2);
   //Dummy-Ende
//   updateDownloads();
  }

  public HashMap getAllServer(){
    return modifiedXML.getServer();
  }

  public void updateModifiedXML(){
    modifiedXML.update();
    informServerListener();
    informDownloadListener();
  }


  public static boolean connectToServer(int id){
    String result;
    try {
      result = HtmlLoader.getHtmlContent(getHost(), HtmlLoader.POST, "/function/serverlogin?id=" + id);
    }
    catch (WebSiteNotFoundException ex) {
      return false;
    }
    return true;
  }

  private static String getHost(){
    OptionsManager om = OptionsManager.getInstance();
    String savedHost = "localhost";
    if (om.getRemoteSettings().isRemoteUsed()){
      savedHost = OptionsManager.getInstance().getRemoteSettings().getHost();
      if (savedHost.length() == 0)
        savedHost = "localhost";
    }
    return savedHost;
  }

  public NetworkInfo getNetworkInfo(){
    reloadModifiedXML(true);
    return modifiedXML.getNetworkInfo();
  }

  public void reloadModifiedXML(boolean informListener){
    modifiedXML.reload("");
    updateServer(informListener);
  }

  public static boolean istCoreErreichbar(){
    try {
      String testData = HtmlLoader.getHtmlContent(getHost(), HtmlLoader.GET, "/xml/information.xml");
    }
    catch (WebSiteNotFoundException ex) {
      return false;
    }
    return true;
  }

  protected void updateServer(boolean informListener){
  }

  public static DataManager getInstance(){
    if (instance==null){
      instance = new DataManager();
    }
    return instance;
  }

  public Version getCoreVersion(){
    return coreVersion;
  }

  private void informDownloadListener(){
    Iterator it = downloadListener.iterator();
    while (it.hasNext()){
      ((DataUpdateListener)it.next()).fireContentChanged();
    }
  }

  private void informServerListener(){
    Iterator it = serverListener.iterator();
    while (it.hasNext()){
      ((DataUpdateListener)it.next()).fireContentChanged();
    }
  }

  public void addStatusbarForListen(JLabel[] statusbar){
    this.statusbar = statusbar;
    updateStatusbar();
  }

  public void updateStatusbar(){
    //dummy
    if (statusbar!=null){
      statusbar[0].setText("Nicht verbunden");
      statusbar[3].setText("Credits: 0,00 MB");
      statusbar[4].setText("Version 0.01PreA");
    }
  }

  public DownloadSourceDO[] getDownloads(){
    updateDownloads();
    return downloads;
  }

  public void updateDownloads(){
    //dummy
    x++;
    downloads[1].setGroesse(Integer.toString(x));
    informDownloadListener();
  }
}