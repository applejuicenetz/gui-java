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

  public String getAJGesamtShare(long faktor){
    ajGesamtShare = ajGesamtShare.replace(',', '.');
    double share = Double.parseDouble(ajGesamtShare);
    if (share==0)
      return "0,00 MB";
    if (faktor==0){  //selbst entscheiden
      if (share /1024 < 1024)
        faktor=1024;
      else if (share /1048576 < 1000)
        faktor=1048576;
      else faktor = 1;
    }
    share = share / faktor;
    String result = Double.toString(share);
    result = result.substring(0, result.indexOf(".")+3);
    result = result.replace('.', ',');
    if (faktor==1)
      result += " MB";
    else if (faktor==1024)
      result += " GB";
    else if (faktor==1048576)
      result += " TB";
    else
      result += " ??";
    return result;
  }

  public String getAJGesamtShareWithPoints(long faktor){
    String result = getAJGesamtShare(faktor);
    return insertPoints(result);
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

  public String getAJUserGesamtAsStringWithPoints(){
    String result = getAJUserGesamtAsString();
    return insertPoints(result);
  }

  public long getAJAnzahlDateien(){
    return ajAnzahlDateien;
  }

  public String getAJAnzahlDateienAsString(){
    return Long.toString(ajAnzahlDateien);
  }

  public String getAJAnzahlDateienAsStringWithPoints(){
    return insertPoints(Long.toString(ajAnzahlDateien));
  }

  private String insertPoints(String tochange){
    StringBuffer result = new StringBuffer(tochange);
    int laenge;
    if (result.indexOf(",")==-1){
      if (result.indexOf(" ")==-1)
       laenge = result.length();
      else
        laenge=result.indexOf(" ");
    }
    else
      laenge=result.indexOf(",");
    int zaehler=0;
    for (int i = laenge-1; i>0; i--){
      zaehler++;
      if (zaehler==3){
        zaehler=0;
        result.insert(i, '.');
      }
    }
    return result.toString();
  }
}