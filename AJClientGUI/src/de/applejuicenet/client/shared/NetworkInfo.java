package de.applejuicenet.client.shared;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class NetworkInfo {
  private long ajUserGesamt;
  private long ajAnzahlDateien;
  private String ajGesamtShare;
  private boolean firewalled;
  private String externeIP;

  public NetworkInfo(long ajUserGesamt, long ajAnzahlDateien, String ajGesamtShare, boolean firewalled, String externeIP){
    this.ajUserGesamt = ajUserGesamt;
    this.ajAnzahlDateien = ajAnzahlDateien;
    this.ajGesamtShare = ajGesamtShare;
    this.firewalled = firewalled;
    this.externeIP = externeIP;
  }

  public NetworkInfo(String ajUserGesamt, String ajAnzahlDateien, String ajGesamtShare, boolean firewalled, String externeIP){
    if (ajUserGesamt==null || ajUserGesamt.length()==0)
      this.ajUserGesamt = 0;
    else
      this.ajUserGesamt = Long.parseLong(ajUserGesamt);
    if (ajAnzahlDateien==null || ajAnzahlDateien.length()==0)
      this.ajAnzahlDateien = 0;
    else
      this.ajAnzahlDateien = Long.parseLong(ajAnzahlDateien);
    this.ajGesamtShare = ajGesamtShare;
    this.firewalled = firewalled;
    this.externeIP = externeIP;
  }

  public String getAJGesamtShare(){
    return ajGesamtShare;
  }

  public boolean isFirewalled(){
    return firewalled;
  }

  public String getExterneIP(){
    return externeIP;
  }

  public long getAJUserGesamt(){
    return ajUserGesamt;
  }

  public String getAJUserGesamtAsString(){
    return Long.toString(ajUserGesamt);
  }

  public long getAJAnzahlDateien(){
    return ajAnzahlDateien;
  }

  public String getAJAnzahlDateienAsString(){
    return Long.toString(ajAnzahlDateien);
  }
}