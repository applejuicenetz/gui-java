package de.applejuicenet.client.shared;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/Version.java,v 1.10 2003/08/11 14:42:13 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: Version.java,v $
 * Revision 1.10  2003/08/11 14:42:13  maj0r
 * Versions-Icon-Beschaffung in die Klasse Version verschoben.
 *
 * Revision 1.9  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.8  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class Version {
  public static final int UNBEKANNT = 0;
  public static final int WIN32 = 1;
  public static final int LINUX = 2;
  public static final int MACINTOSH = 3;
  public static final int SOLARIS = 4;
  public static final int OS2 = 5;
  public static final int FREEBSD = 6;
  public static final int NETWARE = 7;

  private String versionNr;
  private int betriebsSystem;

  public Version(String versionNr, int betriebsSystem) {
    this.versionNr = versionNr;
    this.betriebsSystem = betriebsSystem;
  }

  public Version() {
  }

  public String getVersion() {
    return versionNr;
  }

  public int getBetriebsSystem() {
    return betriebsSystem;
  }

  public String getBetriebsSystemAsString() {
    String result = "";
    if (betriebsSystem == LINUX) {
      result = "Linux";
    }
    else if (betriebsSystem == WIN32) {
      result = "Win32";
    }
    return result;
  }

  public void setBetriebsSystem(int betriebsSystem) {
    this.betriebsSystem = betriebsSystem;
  }

  public void setVersion(String versionNr) {
    this.versionNr = versionNr;
  }

  public static int getOSTypByOSName(String OSName) {
    int result = -1;
    if (OSName.compareToIgnoreCase("Windows NT") == 0) {
      result = WIN32;
    }
    else if (OSName.compareToIgnoreCase("Linux") == 0) {
      result = LINUX;
    }
    return result;
  }

  public ImageIcon getVersionIcon(){
      switch (betriebsSystem){
          case Version.WIN32:
              {
                  return IconManager.getInstance().getIcon("winsymbol");
              }
          case Version.LINUX:
              {
                  return IconManager.getInstance().getIcon("linuxsymbol");
              }
          case Version.FREEBSD:
              {
                  return IconManager.getInstance().getIcon("freebsdsymbol");
              }
          case Version.MACINTOSH:
              {
                  return IconManager.getInstance().getIcon("macsymbol");
              }
          case Version.SOLARIS:
              {
                  return IconManager.getInstance().getIcon("sunossymbol");
              }
          default:
              {
                  return IconManager.getInstance().getIcon("unbekanntsymbol");
              }
      }
  }
}