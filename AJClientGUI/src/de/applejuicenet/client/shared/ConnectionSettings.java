package de.applejuicenet.client.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/ConnectionSettings.java,v 1.1 2003/08/22 10:55:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ConnectionSettings.java,v $
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

    public ConnectionSettings(String host, String password) {
        this.host = host;
        if (password.length() == 0)
            password = getMD5("");
        this.oldPassword = password;
    }

    public ConnectionSettings() {
    }

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
        if (host == null)
            host = "";
        return host;
    }

    public String getOldPassword() {
        if (oldPassword == null)
            oldPassword = "";
        return oldPassword;
    }

    public String getNewPassword() {
        if (newPassword == null)
            newPassword = "";
        return newPassword;
    }

    private String getMD5(String text) {
        byte[] intext = text.getBytes();
        MessageDigest md5 = null;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        byte[] md5rslt = md5.digest(intext);

        StringBuffer verifyMsg = new StringBuffer();
        for (int i = 0; i < md5rslt.length; i++)
        {
            int hexChar = 0xFF & md5rslt[i];
            String hexString = Integer.toHexString(hexChar);
            hexString = (hexString.length() == 1) ? "0" + hexString : hexString;
            verifyMsg.append(hexString);
        }
        return verifyMsg.toString();
    }
}
