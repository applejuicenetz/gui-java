package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/ProxySettings.java,v 1.1 2003/09/12 13:19:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ProxySettings.java,v $
 * Revision 1.1  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
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
        this.userpass = new sun.misc.BASE64Encoder().encode((user + ":" + passwort).getBytes());
    }
}
