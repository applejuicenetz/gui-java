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

public class DownloadDO {
  boolean root;
  private String dateiname;
  private String status;
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

  public DownloadDO(boolean isRoot, String dateiname, String status,
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
    this.sources = sources;
  }

  public String getDateiname() {
    if (root) {
      return dateiname;
    }
    else {
      return getNick() + " (" + getDateiname() + ")";
    }
  }

  public String getNick() {
    if (root) {
      return null;
    }
    else {
      return nick;
    }
  }

  public String getStatus() {
    return status;
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

  public Version getClientVersion() {
    //to do
    return version;
  }

  public void addDownloadSource(DownloadDO source) {
    if (! (sources.contains(source))) {
      sources.add(source);
    }
  }

  public DownloadDO[] getSources() {
    return (DownloadDO[]) sources.toArray(new DownloadDO[sources.
                                                size()]);
  }

  public String toString() {
    return getDateiname();
  }
}