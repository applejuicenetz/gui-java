package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.DownloadSourceDO;
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
  private DownloadSourceDO[] downloads;
  private HashSet downloadListener;
  private static DataManager instance = null;
  private static int x=0;

  public void addDownloadListener(DownloadListener listener){
    if (!(downloadListener.contains(listener)))
      downloadListener.add(listener);
  }

  private DataManager(){
    downloadListener = new HashSet();
   //Dummy-Implementierung
   Version version = new Version("0.27", "Java", "Win");
   DownloadSourceDO source = new DownloadSourceDO(false, "datei2.jpg", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "Maj0r", null);
   HashSet sourcen = new HashSet();
   sourcen.add(source);
   downloads = new DownloadSourceDO[2];
   downloads[0] = new DownloadSourceDO(true, "dateiliste.mov", DownloadSourceDO.UEBERTRAGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
   downloads[1] = new DownloadSourceDO(true, "Film.avi", DownloadSourceDO.WARTESCHLANGE, "1GB", "nix", "0", "100", "0 Kb", "?", "1:1", version, "", sourcen);
   //Dummy-Ende
    downloads = getDownloads();
  }

  public static DataManager getInstance(){
    if (instance==null){
      instance = new DataManager();
    }
    return instance;
  }

  public DownloadSourceDO[] getDownloads(){
    Iterator it = downloadListener.iterator();
      x++;
      downloads[1].setGroesse(Integer.toString(x));
    while (it.hasNext()){
      ((DownloadListener)it.next()).fireContentChanged();
    }
    return downloads;
  }
}