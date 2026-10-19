package de.applejuicenet.client.shared;

import de.applejuicenet.client.facade.tools.MD5Encoder;

/**
 * Copyright: General Public License
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
