package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/NetworkInfo.java,v 1.12 2004/05/22 20:39:30 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: NetworkInfo.java,v $
 * Revision 1.12  2004/05/22 20:39:30  maj0r
 * ServerWelcomemessage eingebaut.
 *
 * Revision 1.11  2004/02/18 17:24:21  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.10  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.9  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.8  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.7  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class NetworkInfo {
    private long ajUserGesamt;
    private long ajAnzahlDateien;
    private String ajGesamtShare;
    private boolean firewalled;
    private String externeIP;
    private int tryConnectToServer;
    private int connectedWithServerId;
    private String welcomeMessage;

    public NetworkInfo(){

    }

    public NetworkInfo(long ajUserGesamt, long ajAnzahlDateien,
                       String ajGesamtShare, boolean firewalled,
                       String externeIP,
                       int tryConnectToServer, int connectedWithServerId,
                       String welcomeMessage) {
        this.ajUserGesamt = ajUserGesamt;
        this.ajAnzahlDateien = ajAnzahlDateien;
        this.ajGesamtShare = ajGesamtShare;
        this.firewalled = firewalled;
        this.externeIP = externeIP;
        this.tryConnectToServer = tryConnectToServer;
        this.connectedWithServerId = connectedWithServerId;
        setWelcomeMessage(welcomeMessage);
    }

    public void setAjUserGesamt(long ajUserGesamt){
        this.ajUserGesamt = ajUserGesamt;
    }

    public void setAjAnzahlDateien(long ajAnzahlDateien){
        this.ajAnzahlDateien = ajAnzahlDateien;
    }

    public void setAjGesamtShare(String ajGesamtShare){
        this.ajGesamtShare = ajGesamtShare;
    }

    public void setFirewalled(boolean firewalled){
        this.firewalled = firewalled;
    }

    public void setExterneIP(String externeIP){
        this.externeIP = externeIP;
    }

    public void setTryConnectToServer(int tryConnectToServer){
        this.tryConnectToServer = tryConnectToServer;
    }

    public void setConnectedWithServerId(int connectedWithServerId){
        this.connectedWithServerId = connectedWithServerId;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        if (welcomeMessage.toLowerCase().indexOf("<html>") == -1){
            welcomeMessage = "<html>" + welcomeMessage + "</html>";
        }
        welcomeMessage = welcomeMessage.replaceAll("\n", "<br>");
        this.welcomeMessage = welcomeMessage;
    }

    public NetworkInfo(String ajUserGesamt, String ajAnzahlDateien,
                       String ajGesamtShare, boolean firewalled,
                       String externeIP,
                       int tryConnectToServer, int connectedWithServerId) {
        if (ajUserGesamt == null || ajUserGesamt.length() == 0) {
            this.ajUserGesamt = 0;
        }
        else {
            this.ajUserGesamt = Long.parseLong(ajUserGesamt);
        }
        if (ajAnzahlDateien == null || ajAnzahlDateien.length() == 0) {
            this.ajAnzahlDateien = 0;
        }
        else {
            this.ajAnzahlDateien = Long.parseLong(ajAnzahlDateien);
        }
        this.ajGesamtShare = ajGesamtShare;
        this.firewalled = firewalled;
        this.externeIP = externeIP;
        this.tryConnectToServer = tryConnectToServer;
        this.connectedWithServerId = connectedWithServerId;
    }

    public String getAJGesamtShare(long faktor) {
        ajGesamtShare = ajGesamtShare.replace(',', '.');
        double share = Double.parseDouble(ajGesamtShare);
        if (share == 0) {
            return "0,00 MB";
        }
        if (faktor == 0) { //selbst entscheiden
            if (share / 1024 < 1024) {
                faktor = 1024;
            }
            else if (share / 1048576 < 1024) {
                faktor = 1048576;
            }
            else {
                faktor = 1;
            }
        }
        share = share / faktor;
        String result = Double.toString(share);
        if (result.indexOf(".") + 3 < result.length()) {
            result = result.substring(0, result.indexOf(".") + 3);
        }
        result = result.replace('.', ',');
        if (faktor == 1) {
            result += " MB";
        }
        else if (faktor == 1024) {
            result += " GB";
        }
        else if (faktor == 1048576) {
            result += " TB";
        }
        else {
            result += " ??";
        }
        return result;
    }

    public String getAJGesamtShareWithPoints(long faktor) {
        String result = getAJGesamtShare(faktor);
        return insertPoints(result);
    }

    public boolean isFirewalled() {
        return firewalled;
    }

    public String getExterneIP() {
        return externeIP;
    }

    public long getAJUserGesamt() {
        return ajUserGesamt;
    }

    public String getAJUserGesamtAsString() {
        return Long.toString(ajUserGesamt);
    }

    public String getAJUserGesamtAsStringWithPoints() {
        String result = getAJUserGesamtAsString();
        return insertPoints(result);
    }

    public long getAJAnzahlDateien() {
        return ajAnzahlDateien;
    }

    public String getAJAnzahlDateienAsString() {
        return Long.toString(ajAnzahlDateien);
    }

    public String getAJAnzahlDateienAsStringWithPoints() {
        return insertPoints(Long.toString(ajAnzahlDateien));
    }

    private String insertPoints(String tochange) {
        StringBuffer result = new StringBuffer(tochange);
        int laenge;
        if (result.indexOf(",") == -1) {
            if (result.indexOf(" ") == -1) {
                laenge = result.length();
            }
            else {
                laenge = result.indexOf(" ");
            }
        }
        else {
            laenge = result.indexOf(",");
        }
        int zaehler = 0;
        for (int i = laenge - 1; i > 0; i--) {
            zaehler++;
            if (zaehler == 3) {
                zaehler = 0;
                result.insert(i, '.');
            }
        }
        return result.toString();
    }

    public int getTryConnectToServer() {
        return tryConnectToServer;
    }

    public int getConnectedWithServerId() {
        return connectedWithServerId;
    }

    public String getWelcomeMessage() {
        if (welcomeMessage == null){
            return "";
        }
        return welcomeMessage;
    }
}
