package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/Information.java,v 1.1 2003/10/21 11:35:41 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: Information.java,v $
 * Revision 1.1  2003/10/21 11:35:41  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 *
 */

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

public class Information implements LanguageListener {

    public static final int VERBUNDEN = 0;
    public static final int NICHT_VERBUNDEN = 1;
    public static final int VERSUCHE_ZU_VERBINDEN = 2;

    private static String verbunden;
    private static String verbinden;
    private static String nichtVerbunden;
    private static LanguageSelector languageSelector;

    private long sessionUpload;
    private long sessionDownload;
    private long credits;
    private long uploadSpeed;
    private long downloadSpeed;
    private long openConnections;
    private String serverName;
    private int verbindungsStatus;
    private String externeIP;

    static{
        languageSelector = LanguageSelector.getInstance();
        Information info = new Information();
        languageSelector.addLanguageListener(info);
        info.fireLanguageChanged();
    }

    private Information(){} //nur fuer den LanguageSelector

    public Information(long sessionUpload, long sessionDownload, long credits, long uploadSpeed, long downloadSpeed,
                       long openConnections, int verbindungsStatus, String serverName, String externeIP) {
        this.sessionUpload = sessionUpload;
        this.sessionDownload = sessionDownload;
        this.credits = credits;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.openConnections = openConnections;
        this.verbindungsStatus = verbindungsStatus;
        if (verbindungsStatus==NICHT_VERBUNDEN || serverName==null){
            this.serverName = "";
        }
        else{
            this.serverName = serverName;
        }
        this.externeIP = externeIP;
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

    public String getCreditsAsString(){
        return " Credits: " + bytesUmrechnen(credits);
    }

    public String getUpDownSessionAsString(){
        return " in: " + bytesUmrechnen(sessionDownload) + " out: " + bytesUmrechnen(sessionUpload);
    }

    public String getUpDownAsString(){
        return " in: " + getBytesSpeed(downloadSpeed) + " out: " + getBytesSpeed(uploadSpeed);
    }

    public String getVerbindungsStatusAsString(){
        switch (verbindungsStatus){
            case VERBUNDEN: return verbunden;
            case NICHT_VERBUNDEN: return nichtVerbunden;
            case VERSUCHE_ZU_VERBINDEN: return verbinden;
            default: return "";
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
            if (pos + 2 < result.length())
                result = result.substring(0, pos + 3);
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
        verbunden = languageSelector.getFirstAttrbuteByTagName(new String[]{
            "javagui", "mainform", "verbunden"});
        verbinden = languageSelector.getFirstAttrbuteByTagName(new String[]{
            "javagui", "mainform", "verbinden"});
        nichtVerbunden = languageSelector.getFirstAttrbuteByTagName(new String[]{
            "javagui", "mainform", "nichtverbunden"});
    }

}