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
  private HashSet globalListener;
  private HashMap serverMap;
  private static DataManager instance = null;
  private static int x=0;
  private JLabel[] statusbar;
  private WebXMPParser modifiedXML = null;
  private WebXMPParser informationXML = null;
  private WebXMPParser shareXML = null;
  private Version coreVersion;

  public void addDownloadListener(DataUpdateListener listener){
    if (!(downloadListener.contains(listener)))
      downloadListener.add(listener);
  }

  public void addServerListener(DataUpdateListener listener){
    if (!(serverListener.contains(listener)))
      serverListener.add(listener);
  }

  public void addGlobalListener(DataUpdateListener listener){
    if (!(globalListener.contains(listener)))
      globalListener.add(listener);
  }

  private DataManager(){
    downloadListener = new HashSet();
    globalListener = new HashSet();
    serverListener = new HashSet();
    serverMap = new HashMap();
   //Dummy-Implementierung
   Version version = new Version("0.27", "Java", "Win");
   String versionText;
   DownloadSourceDO source = new DownloadSourceDO(false, "datei2.jpg", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "Maj0r", null);
   HashSet sourcen = new HashSet();
   sourcen.add(source);
   downloads = new DownloadSourceDO[2];
   downloads[0] = new DownloadSourceDO(true, "dateiliste.mov", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
   downloads[1] = new DownloadSourceDO(true, "Film.avi", DownloadSourceDO.WARTESCHLANGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
   //Dummy-Ende

   //load XMLs
   modifiedXML = new WebXMPParser("/xml/modified.xml", "");
   updateServer();
   informationXML = new WebXMPParser("/xml/information.xml", "");
   shareXML = new WebXMPParser("/xml/share.xml", "");

   String versionsTag = informationXML.getFirstAttrbuteByTagName(new String[]{"applejuice", "generalinformation", "version"}, true);
   coreVersion = new Version(versionsTag, "Java", (String) System.getProperties().get("os.name"));
//   updateDownloads();
  }

  public HashMap getAllServer(){
    updateServer(false);
    return serverMap;
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

  public static boolean istCoreErreichbar(){
    try {
      String testData = HtmlLoader.getHtmlContent(getHost(), HtmlLoader.GET, "/xml/information.xml");
    }
    catch (WebSiteNotFoundException ex) {
      return false;
    }
    return true;
  }

  public void updateServer(){
    updateServer(true);
  }

  protected void updateServer(boolean informListener){
    modifiedXML.reload("");
    NodeList nodes = modifiedXML.getDocument().getElementsByTagName("server");
    HashMap changedServer = new HashMap();
    for (int i=0; i<nodes.getLength(); i++){
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
    if (informListener)
      informServerListener(changedServer);
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
      ((DataUpdateListener)it.next()).fireContentChanged(new HashMap());
    }
  }

  private void informServerListener(HashMap changedContent){
    Iterator it = serverListener.iterator();
    while (it.hasNext()){
      ((DataUpdateListener)it.next()).fireContentChanged(changedContent);
    }
  }

  private void informGlobalListener(){
    Iterator it = globalListener.iterator();
    while (it.hasNext()){
      ((DataUpdateListener)it.next()).fireContentChanged(new HashMap());
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

  public void updateModifiedXML(){
    modifiedXML.reload("");
    System.out.print(modifiedXML.getFirstAttrbuteByTagName(new String[]{"applejuice", "time"}, true));
  }

  public DownloadSourceDO[] getDownloads(){
    updateDownloads();
    return downloads;
  }

  public void updateDownloads(){
    //dummy
    x++;
    downloads[1].setGroesse(Integer.toString(x));
    informGlobalListener();
    informDownloadListener();
  }
}