package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/Information.java,v 1.7 2004/02/18 17:24:21 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: Information.java,v $
 * Revision 1.7  2004/02/18 17:24:21  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.6  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.5  2004/02/04 13:10:04  maj0r
 * Aktuell verwendeter Server kann nun direkt ausgegeben werden.
 *
 * Revision 1.4  2004/01/29 13:47:06  maj0r
 * Setter eingefuegt.
 *
 * Revision 1.3  2004/01/01 14:24:37  maj0r
 * Id wird nun auch ausgelesen.
 *
 * Revision 1.2  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.1  2003/10/21 11:35:41  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 *
 */

import java.util.HashMap;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.dac.ServerDO;

public class Information
    implements LanguageListener {

    public static final int VERBUNDEN = 0;
    public static final int NICHT_VERBUNDEN = 1;
    public static final int VERSUCHE_ZU_VERBINDEN = 2;

    private static String verbunden;
    private static String verbinden;
    private static String nichtVerbunden;
    private static LanguageSelector languageSelector;

    private int id;
    private long sessionUpload;
    private long sessionDownload;
    private long credits;
    private long uploadSpeed;
    private long downloadSpeed;
    private long openConnections;
    private String serverName;
    private int verbindungsStatus;
    private String externeIP;
    private int serverId;

    static {
        languageSelector = LanguageSelector.getInstance();
        Information info = new Information();
        languageSelector.addLanguageListener(info);
        info.fireLanguageChanged();
    }

    public Information() {} //nur fuer den LanguageSelector

    public void setExterneIP(String externeIP){
        this.externeIP = externeIP;
    }

    public void setVerbindungsStatus(int verbindungsStatus){
        this.verbindungsStatus = verbindungsStatus;
        if (verbindungsStatus == NICHT_VERBUNDEN) {
            this.serverName = "";
        }
    }

    public Information(int id, long sessionUpload, long sessionDownload,
                       long credits, long uploadSpeed, long downloadSpeed,
                       long openConnections, int verbindungsStatus,
                       String externeIP, ServerDO serverDO) {
        this.id = id;
        this.sessionUpload = sessionUpload;
        this.sessionDownload = sessionDownload;
        this.credits = credits;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.openConnections = openConnections;
        setVerbindungsStatus(verbindungsStatus);
        this.externeIP = externeIP;
        setServer(serverDO);
    }

    public int getId() {
        return id;
    }

    public void setSessionUpload(long sessionUpload) {
        this.sessionUpload = sessionUpload;
    }

    public void setSessionDownload(long sessionDownload) {
        this.sessionDownload = sessionDownload;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public void setServer(ServerDO serverDO) {
        if (serverDO == null) {
            serverId = -1;
        }
        else {
            serverId = serverDO.getID();
            serverName = serverDO.getName();
        }
    }

    public ServerDO getServerDO() {
        if (serverId == -1) {
            return null;
        }
        else {
            HashMap server = ApplejuiceFassade.getInstance().getAllServer();
            ServerDO serverDO = (ServerDO) server.get(Integer.toString(serverId));
            if (serverDO != null) {
                return serverDO;
            }
            else {
                serverId = -1;
                return null;
            }
        }
    }

    public void setUploadSpeed(long uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public void setOpenConnections(long openConnections) {
        this.openConnections = openConnections;
    }

    public long getSessionUpload() {
        return sessionUpload;
    }

    public long getSessionDownload() {
        return sessionDownload;
    }

    public long getCredits() {
        return credits;
    }

    public long getUploadSpeed() {
        return uploadSpeed;
    }

    public long getDownloadSpeed() {
        return downloadSpeed;
    }

    public long getOpenConnections() {
        return openConnections;
    }

    public String getServerName() {
        return serverName;
    }

    public int getVerbindungsStatus() {
        return verbindungsStatus;
    }

    public String getExterneIP() {
        return externeIP;
    }

    public String getCreditsAsString() {
        return " Credits: " + bytesUmrechnen(credits);
    }

    public String getUpDownSessionAsString() {
        return " in: " + bytesUmrechnen(sessionDownload) + " out: " +
            bytesUmrechnen(sessionUpload);
    }

    public String getUpDownAsString() {
        return " in: " + getBytesSpeed(downloadSpeed) + " out: " +
            getBytesSpeed(uploadSpeed);
    }

    public String getVerbindungsStatusAsString() {
        switch (verbindungsStatus) {
            case VERBUNDEN:
                return verbunden;
            case NICHT_VERBUNDEN:
                return nichtVerbunden;
            case VERSUCHE_ZU_VERBINDEN:
                return verbinden;
            default:
                return "";
        }
    }

    private String getBytesSpeed(long bytes) {
        if (bytes == 0) {
            return "0 KB/s";
        }
        String result = bytesUmrechnen(bytes) + "/s";
        return result;
    }

    private String bytesUmrechnen(long bytes) {
        boolean minus = false;
        if (bytes < 0) {
            minus = true;
            bytes *= -1;
        }
        if (bytes == 0) {
            return "0 MB";
        }
        long faktor = 1;
        if (bytes < 1024l) {
            faktor = 1;
        }
        else if (bytes / 1024l < 1024l) {
            faktor = 1024l;
        }
        else if (bytes / 1048576l < 1024l) {
            faktor = 1048576l;
        }
        else if (bytes / 1073741824l < 1024l) {
            faktor = 1073741824l;
        }
        else {
            faktor = 1099511627776l;
        }
        if (minus) {
            bytes *= -1;
        }
        double umgerechnet = (double) bytes / (double) faktor;
        String result = Double.toString(umgerechnet);
        int pos = result.indexOf(".");
        if (pos != -1) {
            if (pos + 2 < result.length()) {
                result = result.substring(0, pos + 3);
            }
            result = result.replace('.', ',');
        }
        if (faktor == 1) {
            result += " Bytes";
        }
        else if (faktor == 1024l) {
            result += " kb";
        }
        else if (faktor == 1048576l) {
            result += " MB";
        }
        else if (faktor == 1073741824l) {
            result += " GB";
        }
        else {
            result += " TB";
        }
        return result;
    }

    public void fireLanguageChanged() {
        verbunden = languageSelector.getFirstAttrbuteByTagName(new String[] {
            "javagui", "mainform", "verbunden"});
        verbinden = languageSelector.getFirstAttrbuteByTagName(new String[] {
            "javagui", "mainform", "verbinden"});
        nichtVerbunden = languageSelector.getFirstAttrbuteByTagName(new String[] {
            "javagui", "mainform", "nichtverbunden"});
    }

}
