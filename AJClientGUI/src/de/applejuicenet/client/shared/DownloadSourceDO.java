package de.applejuicenet.client.shared;

import java.util.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class DownloadSourceDO {
  public static final int WARTESCHLANGE = 0;
  public static final int UEBERTRAGE = 1;
  public static final int VERSUCHEINDIREKT = 2;
  public static final int ROOT = 3;

  boolean root;
  private String dateiname;
  private int status;
  private String groesse;
  private String bereitsGeladen;
  private String prozentGeladen;
  private String nochZuLaden;
  private String geschwindigkeit;
  private String restlicheZeit;
  private String powerdownload;
  private Version version;
  private String nick;
  private HashSet sources = new HashSet(); //contains DownloadDO leafs

  public DownloadSourceDO(boolean isRoot, String dateiname, int status,
                    String groesse,
                    String bereitsGeladen, String prozentGeladen,
                    String nochZuLaden, String geschwindigkeit,
                    String restlicheZeit, String powerdownload,
                    Version version, String nick, HashSet sources) {
    root = isRoot;
    this.dateiname = dateiname;
    this.status = status;
    this.groesse = groesse;
    this.bereitsGeladen = bereitsGeladen;
    this.prozentGeladen = prozentGeladen;
    this.nochZuLaden = nochZuLaden;
    this.geschwindigkeit = geschwindigkeit;
    this.restlicheZeit = restlicheZeit;
    this.powerdownload = powerdownload;
    this.version = version;
    this.nick = nick;
    this.sources = sources;
  }

  public DownloadSourceDO(){ }

  public void setGroesse(String groesse){
    this.groesse=groesse;
  }

  public String getDateiname() {
    if (root) {
      return dateiname;
    }
    else {
      return dateiname + " (" + getNick() + ")";
    }
  }

  public String getNick() {
    if (root) {
      return "";
    }
    else {
      return nick;
    }
  }

  public int getIntStatus() {
    return status;
  }

  public String getStatus() {
    if (status==0)
      return "Warteschlange";
    else if (status==1)
      return "Übertrage";
    else if (status==2)
      return "Versuche indirekt zu verbinden";
    else if (status==3)
      return "";
    //to do
    return "";
  }

  public String getGroesse() {
    return groesse;
  }

  public String getBereitsGeladen() {
    return bereitsGeladen;
  }

  public String getProzentGeladen() {
    //to do
    return prozentGeladen;
  }

  public String getNochZuLaden() {
    //to do
    return nochZuLaden;
  }

  public String getGeschwindigkeit() {
    //to do
    return geschwindigkeit;
  }

  public String getRestlicheZeit() {
    //to do
    return restlicheZeit;
  }

  public String getPowerdownload() {
    return powerdownload;
  }

  public Version getVersion() {
    //to do
    return version;
  }

  public void addDownloadSource(DownloadDO source) {
    if (! (sources.contains(source))) {
      sources.add(source);
    }
  }

  public DownloadDO[] getSources() {
    if (sources==null)
      return null;
    return (DownloadDO[]) sources.toArray(new DownloadDO[sources.
                                                size()]);
  }

  public String toString() {
    return getDateiname();
  }
}