package de.applejuicenet.client.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/ConnectionSettings.java,v 1.7 2004/04/06 14:44:31 loevenwong Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ConnectionSettings.java,v $
 * Revision 1.7  2004/04/06 14:44:31  loevenwong
 * Combobox zur Auswahl der letzten 3 Verbindungen eingebaut.
 *
 * Revision 1.6  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.5  2004/02/20 14:55:02  maj0r
 * Speicheroptimierungen.
 *
 * Revision 1.4  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.3  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.2  2003/10/14 15:44:01  maj0r
 * An pflegbaren Xml-Port angepasst.
 *
 * Revision 1.1  2003/08/22 10:55:36  maj0r
 * Klassen umbenannt.
 *
 * Revision 1.6  2003/08/19 12:38:47  maj0r
 * Passworteingabe und md5 korrigiert.
 *
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