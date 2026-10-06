package de.applejuicenet.client.shared;

import de.applejuicenet.client.fassade.tools.MD5Encoder;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/ConnectionSettings.java,v 1.8 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
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
        return MD5Encoder.getMD5(text);
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
