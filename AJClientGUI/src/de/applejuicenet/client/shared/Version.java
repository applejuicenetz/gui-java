package de.applejuicenet.client.shared;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class Version {
  public static int LINUX = 0;
  public static int WIN32 = 1;

  private String versionNr;
  private String programmierSprache;
  private int betriebsSystem;

  public Version(String versionNr, String programmierSprache,
                 int betriebsSystem) {
    this.versionNr = versionNr;
    this.programmierSprache = programmierSprache;
    this.betriebsSystem = betriebsSystem;
  }

  public Version() {
  }

  public String getVersion() {
    return versionNr;
  }

  public String getProgrammierSprache() {
    return programmierSprache;
  }

  public int getBetriebsSystem() {
    return betriebsSystem;
  }

  public String getBetriebsSystemAsString() {
    String result = "";
    if (betriebsSystem == LINUX)
      result = "Linux";
    else if (betriebsSystem == WIN32)
      result = "Win32";
    return result;
  }

  public void setBetriebsSystem(int betriebsSystem) {
    this.betriebsSystem = betriebsSystem;
  }

  public void setVersion(String versionNr) {
    this.versionNr = versionNr;
  }

  public void setProgrammierSprache(String programmierSprache) {
    this.programmierSprache = programmierSprache;
  }

  public static int getOSTypByOSName(String OSName){
    int result = -1;
    if (OSName.compareToIgnoreCase("Windows NT")==0)
      result = WIN32;
    else if (OSName.compareToIgnoreCase("Linux")==0)
      result = LINUX;
    return result;
  }
}