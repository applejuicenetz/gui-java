package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.DownloadSourceDO;
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
  private HashSet globalListener;
  private static DataManager instance = null;
  private static int x=0;
  private JLabel[] statusbar;

  public void addDownloadListener(DataUpdateListener listener){
    if (!(downloadListener.contains(listener)))
      downloadListener.add(listener);
  }

  public void addGlobalListener(DataUpdateListener listener){
    if (!(globalListener.contains(listener)))
      globalListener.add(listener);
  }

  private DataManager(){
    downloadListener = new HashSet();
    globalListener = new HashSet();
   //Dummy-Implementierung
   Version version = new Version("0.27", "Java", "Win");
   String versionText;
/*  File tempFile = null;
  FileWriter fw = null;
  try {
    tempFile = File.createTempFile("temp", "xml");
    fw = new FileWriter(tempFile);
    versionText = HtmlLoader.getHtmlContent("http://localhost:9851/xml/information.xml");
    fw.write(versionText);
    fw.close();
  }
  catch (IOException ex2) {
    ex2.printStackTrace();
  }
  catch (WebSiteNotFoundException ex) {
    ex.printStackTrace();
  }
  XMLDecoder info = new XMLDecoder(tempFile);
  String test = info.getFirstAttrbuteByTagName(new String[]{"applejuice", "generalinformation", "version"});*/
   DownloadSourceDO source = new DownloadSourceDO(false, "datei2.jpg", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "Maj0r", null);
   HashSet sourcen = new HashSet();
   sourcen.add(source);
   downloads = new DownloadSourceDO[2];
   downloads[0] = new DownloadSourceDO(true, "dateiliste.mov", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
   downloads[1] = new DownloadSourceDO(true, "Film.avi", DownloadSourceDO.WARTESCHLANGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
   //Dummy-Ende
   updateDownloads();
  }

  public static DataManager getInstance(){
    if (instance==null){
      instance = new DataManager();
    }
    return instance;
  }

  private void informDownloadListener(){
    Iterator it = downloadListener.iterator();
    while (it.hasNext()){
      ((DataUpdateListener)it.next()).fireContentChanged();
    }
  }

  private void informGlobalListener(){
    Iterator it = globalListener.iterator();
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
    informGlobalListener();
    informDownloadListener();
  }
}