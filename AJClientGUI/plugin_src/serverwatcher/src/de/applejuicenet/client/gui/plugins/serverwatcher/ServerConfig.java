package de.applejuicenet.client.gui.plugins.serverwatcher;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/serverwatcher/src/de/applejuicenet/client/gui/plugins/serverwatcher/ServerConfig.java,v 1.1 2003/09/12 11:15:49 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ServerConfig.java,v $
 * Revision 1.1  2003/09/12 11:15:49  maj0r
 * Server lassen sich nun speichern und entfernen.
 * Version 1.1
 *
 *
 */

public class ServerConfig {
    private String bezeichnung;
    private String dyn;
    private String port;
    private String userpass;

    public ServerConfig(String bezeichnung, String dyn, String port, String userpass) {
        this.bezeichnung = bezeichnung;
        this.dyn = dyn;
        this.port = port;
        this.userpass = userpass;
    }

    public ServerConfig(String dyn, String port, String userpass) {
        this.dyn = dyn;
        bezeichnung = "a";
        int value;
        for (int i=0; i<dyn.length(); i++){
            value = (int) dyn.charAt(i);
            if (value>96 && value<123){
                bezeichnung += dyn.charAt(i);
            }
        }
        this.port = port;
        this.userpass = new sun.misc.BASE64Encoder().encode(userpass.getBytes());
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public String getDyn() {
        return dyn;
    }

    public String getPort() {
        return port;
    }

    public String getUserPass() {
        return userpass;
    }

    public String toString(){
        return dyn + ":" + port;
    }
}
