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
  private String versionNr;
  private String programmierSprache;
  private String betriebsSystem;

  public Version(String versionNr, String programmierSprache,
                 String betriebsSystem) {
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

  public String getBetriebsSystem() {
    return betriebsSystem;
  }

  public void setBetriebsSystem(String betriebsSystem) {
    this.betriebsSystem = betriebsSystem;
  }

  public void setVersion(String versionNr) {
    this.versionNr = versionNr;
  }

  public void setProgrammierSprache(String programmierSprache) {
    this.programmierSprache = programmierSprache;
  }

}