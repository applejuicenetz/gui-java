package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.DownloadDO;
import de.applejuicenet.client.shared.Version;
import java.util.HashSet;
import de.applejuicenet.client.gui.listener.DownloadListener;
import java.util.Iterator;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DataManager {   //Singleton-Implementierung
  private DownloadDO[] downloads;
  private HashSet downloadListener;
  private static DataManager instance = null;

  public void addDownloadListener(DownloadListener listener){
    if (!(downloadListener.contains(listener)))
      downloadListener.add(listener);
  }

  private DataManager(){
    downloadListener = new HashSet();
    downloads = getDownloads();
  }

  public static DataManager getInstance(){
    if (instance==null){
      instance = new DataManager();
    }
    return instance;
  }

  public DownloadDO[] getDownloads(){
    //Dummy-Implementierung
    Version version = new Version("0.27", "Java", "Win");
    DownloadDO source = new DownloadDO(false, "datei2.jpg", DownloadDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "Maj0r", null);
    HashSet sourcen = new HashSet();
    sourcen.add(source);
    DownloadDO[] downloads = new DownloadDO[2];
    downloads[0] = new DownloadDO(true, "dateiliste.mov", DownloadDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
    downloads[1] = new DownloadDO(true, "Film.avi", DownloadDO.WARTESCHLANGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
    //Dummy-Ende

    Iterator it = downloadListener.iterator();
    while (it.hasNext()){
      ((DownloadListener)it.next()).fireContentChanged();
    }
    return downloads;
  }
}