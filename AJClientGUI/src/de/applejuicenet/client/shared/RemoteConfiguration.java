package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/RemoteConfiguration.java,v 1.3 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: RemoteConfiguration.java,v $
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class RemoteConfiguration {
  private String host;
  private boolean use;
  private String oldPassword;
  private String newPassword;

  public RemoteConfiguration(String host, String oldPassword, boolean use) {
    this.host = host;
    this.oldPassword = oldPassword;
    this.use = use;
  }

  public RemoteConfiguration() {}

  public void setHost(String host) {
    this.host = host;
  }

  public void useRemote(boolean use) {
    this.use = use;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getHost() {
    return host;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public boolean isRemoteUsed() {
    return use;
  }
}
