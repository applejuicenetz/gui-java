package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/NetworkInfo.java,v 1.8 2003/08/02 12:03:38 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: NetworkInfo.java,v $
 * Revision 1.8  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.7  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class NetworkInfo {
  private long ajUserGesamt;
  private long ajAnzahlDateien;
  private String ajGesamtShare;
  private boolean firewalled;
  private String externeIP;
  private int tryConnectToServer;
  private int connectedWithServerId;

  public NetworkInfo(long ajUserGesamt, long ajAnzahlDateien,
                     String ajGesamtShare, boolean firewalled, String externeIP,
                     int tryConnectToServer, int connectedWithServerId) {
    this.ajUserGesamt = ajUserGesamt;
    this.ajAnzahlDateien = ajAnzahlDateien;
    this.ajGesamtShare = ajGesamtShare;
    this.firewalled = firewalled;
    this.externeIP = externeIP;
    this.tryConnectToServer = tryConnectToServer;
    this.connectedWithServerId = connectedWithServerId;
  }

  public NetworkInfo(String ajUserGesamt, String ajAnzahlDateien,
                     String ajGesamtShare, boolean firewalled, String externeIP,
                     int tryConnectToServer, int connectedWithServerId) {
    if (ajUserGesamt == null || ajUserGesamt.length() == 0) {
      this.ajUserGesamt = 0;
    }
    else {
      this.ajUserGesamt = Long.parseLong(ajUserGesamt);
    }
    if (ajAnzahlDateien == null || ajAnzahlDateien.length() == 0) {
      this.ajAnzahlDateien = 0;
    }
    else {
      this.ajAnzahlDateien = Long.parseLong(ajAnzahlDateien);
    }
    this.ajGesamtShare = ajGesamtShare;
    this.firewalled = firewalled;
    this.externeIP = externeIP;
    this.tryConnectToServer = tryConnectToServer;
    this.connectedWithServerId = connectedWithServerId;
  }

  public String getAJGesamtShare(long faktor) {
    ajGesamtShare = ajGesamtShare.replace(',', '.');
    double share = Double.parseDouble(ajGesamtShare);
    if (share == 0) {
      return "0,00 MB";
    }
    if (faktor == 0) { //selbst entscheiden
      if (share / 1024 < 1024) {
        faktor = 1024;
      }
      else if (share / 1048576 < 1024) {
        faktor = 1048576;
      }
      else {
        faktor = 1;
      }
    }
    share = share / faktor;
    String result = Double.toString(share);
    if (result.indexOf(".") + 3 < result.length())
    {
        result = result.substring(0, result.indexOf(".") + 3);
    }
    result = result.replace('.', ',');
    if (faktor == 1) {
      result += " MB";
    }
    else if (faktor == 1024) {
      result += " GB";
    }
    else if (faktor == 1048576) {
      result += " TB";
    }
    else {
      result += " ??";
    }
    return result;
  }

  public String getAJGesamtShareWithPoints(long faktor) {
    String result = getAJGesamtShare(faktor);
    return insertPoints(result);
  }

  public boolean isFirewalled() {
    return firewalled;
  }

  public String getExterneIP() {
    return externeIP;
  }

  public long getAJUserGesamt() {
    return ajUserGesamt;
  }

  public String getAJUserGesamtAsString() {
    return Long.toString(ajUserGesamt);
  }

  public String getAJUserGesamtAsStringWithPoints() {
    String result = getAJUserGesamtAsString();
    return insertPoints(result);
  }

  public long getAJAnzahlDateien() {
    return ajAnzahlDateien;
  }

  public String getAJAnzahlDateienAsString() {
    return Long.toString(ajAnzahlDateien);
  }

  public String getAJAnzahlDateienAsStringWithPoints() {
    return insertPoints(Long.toString(ajAnzahlDateien));
  }

  private String insertPoints(String tochange) {
    StringBuffer result = new StringBuffer(tochange);
    int laenge;
    if (result.indexOf(",") == -1) {
      if (result.indexOf(" ") == -1) {
        laenge = result.length();
      }
      else {
        laenge = result.indexOf(" ");
      }
    }
    else {
      laenge = result.indexOf(",");
    }
    int zaehler = 0;
    for (int i = laenge - 1; i > 0; i--) {
      zaehler++;
      if (zaehler == 3) {
        zaehler = 0;
        result.insert(i, '.');
      }
    }
    return result.toString();
  }

    public int getTryConnectToServer() {
        return tryConnectToServer;
    }

    public int getConnectedWithServerId() {
        return connectedWithServerId;
    }
}