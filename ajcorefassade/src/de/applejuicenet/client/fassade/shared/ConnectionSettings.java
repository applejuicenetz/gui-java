package de.applejuicenet.client.fassade.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/shared/Attic/ConnectionSettings.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ConnectionSettings {
    private String host;
    private String oldPassword;
    private String newPassword;
    private int xmlPort;

    public ConnectionSettings(String host, String password, int xmlPort) {
        this.host = host;
        if (password.length() == 0) {
            password = getMD5("");
        }
        this.oldPassword = password;
        this.xmlPort = xmlPort;
    }

    public ConnectionSettings() {
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = getMD5(oldPassword);
    }

    public void setOldMD5Password(String oldMD5Password) {
        this.oldPassword = oldMD5Password;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = getMD5(newPassword);
    }

    public String getHost() {
        if (host == null) {
            host = "";
        }
        return host;
    }

    public String getOldPassword() {
        if (oldPassword == null) {
            oldPassword = "";
        }
        return oldPassword;
    }

    public String getNewPassword() {
        if (newPassword == null) {
            newPassword = "";
        }
        return newPassword;
    }

    private String getMD5(String text) {
        byte[] intext = text.getBytes();
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            ;
            //Gibbet nicht...
        }
        byte[] md5rslt = md5.digest(intext);

        StringBuffer verifyMsg = new StringBuffer();
        for (int i = 0; i < md5rslt.length; i++) {
            int hexChar = 0xFF & md5rslt[i];
            String hexString = Integer.toHexString(hexChar);
            hexString = (hexString.length() == 1) ? "0" + hexString : hexString;
            verifyMsg.append(hexString);
        }
        return verifyMsg.toString();
    }

    public int getXmlPort() {
        return xmlPort;
    }

    public void setXmlPort(int xmlPort) {
        this.xmlPort = xmlPort;
    }

    public String toString() {
        return this.getHost() + ":" + this.getXmlPort();
    }
}