package de.applejuicenet.client.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/RemoteConfiguration.java,v 1.5 2003/08/02 12:03:38 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: RemoteConfiguration.java,v $
 * Revision 1.5  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.4  2003/07/01 14:51:56  maj0r
 * Fehler bei null-Werten korrigiert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class RemoteConfiguration {
  private String host;
  private String oldPassword;
  private String newPassword;

  public RemoteConfiguration(String host, String oldPassword) {
    this.host = host;
    this.oldPassword = oldPassword;
  }

  public RemoteConfiguration() {}

  public void setHost(String host) {
    this.host = host;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = getMD5(oldPassword);
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = getMD5(newPassword);
  }

  public String getHost() {
      if (host==null)
          host = "";
    return host;
  }

  public String getOldPassword() {
      if (oldPassword==null)
          oldPassword = "";
    return oldPassword;
  }

  public String getNewPassword() {
    if (newPassword==null)
        newPassword = "";
    return newPassword;
  }

  private String getMD5 (String text){
    byte[] intext = text.getBytes();
    StringBuffer sb = new StringBuffer();
    MessageDigest md5 = null;
    try {
        md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
    byte[] md5rslt = md5.digest(intext);
    for( int i = 0 ; i < md5rslt.length ; i++ ){
        sb.append(Integer.toHexString( (0xff & md5rslt[i])));
    }
    return sb.toString();
  }
}
