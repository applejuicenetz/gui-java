package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/ProxySettings.java,v 1.4 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ProxySettings {
    private boolean use;
    private String host;
    private int port;
    private String userpass;

    public ProxySettings(boolean use, String host, int port, String userpass) {
        this.use = use;
        this.host = host;
        this.port = port;
        this.userpass = userpass;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String user, String passwort) {
        this.userpass = new sun.misc.BASE64Encoder().encode( (user + ":" +
            passwort).getBytes());
    }
}
