package de.applejuicenet.client.shared;

import java.util.Set;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/AJSettings.java,v 1.9 2004/03/03 15:33:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AJSettings.java,v $
 * Revision 1.9  2004/03/03 15:33:31  maj0r
 * PMD-Optimierung
 *
 * Revision 1.8  2004/02/09 07:28:24  maj0r
 * Max. Anzahl von Quellen pro Datei kann begrenzt werden.
 *
 * Revision 1.7  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.6  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.5  2003/09/10 13:16:28  maj0r
 * Veraltete Option "Browsen erlauben" entfernt und neue Option MaxNewConnectionsPerTurn hinzugefuegt.
 *
 * Revision 1.4  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class AJSettings {
    private String nick;
    private long port;
    private long xmlPort;
    private long maxUpload;
    private long maxDownload;
    private long maxConnections;
    private boolean autoConnect;
    private int speedPerSlot;
    private String incomingDir;
    private String tempDir;
    private Set shareDirs;
    private long maxNewConnectionsPerTurn;
    private long maxSourcesPerFile;

    public AJSettings(String nick, long port, long xmlPort,
                      long maxUpload, long maxDownload,
                      int speedPerSlot, String incomingDir, String tempDir,
                      Set shareDirs, long maxConnections,
                      boolean autoConnect,
                      long maxNewConnectionsPerTurn,
                      long maxSourcesPerFile) {
        this.nick = nick;
        this.port = port;
        this.xmlPort = xmlPort;
        this.maxUpload = maxUpload;
        this.maxDownload = maxDownload;
        this.speedPerSlot = speedPerSlot;
        this.incomingDir = incomingDir;
        this.tempDir = tempDir;
        this.shareDirs = shareDirs;
        this.maxConnections = maxConnections;
        this.autoConnect = autoConnect;
        this.maxNewConnectionsPerTurn = maxNewConnectionsPerTurn;
        this.maxSourcesPerFile = maxSourcesPerFile;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public long getMaxSourcesPerFile(){
        return maxSourcesPerFile;
    }

    public void setMaxSourcesPerFile(long maxSourcesPerFile) {
        this.maxSourcesPerFile = maxSourcesPerFile;
    }

    public long getPort() {
        return port;
    }

    public void setPort(long port) {
        this.port = port;
    }

    public long getXMLPort() {
        return xmlPort;
    }

    public void setXMLPort(long xmlPort) {
        this.xmlPort = xmlPort;
    }

    public long getMaxUpload() {
        return maxUpload;
    }

    public long getMaxUploadInKB() {
        return maxUpload / 1024;
    }

    public void setMaxUpload(long maxUpload) {
        this.maxUpload = maxUpload;
    }

    public long getMaxDownload() {
        return maxDownload;
    }

    public long getMaxDownloadInKB() {
        return maxDownload / 1024;
    }

    public void setMaxDownload(long maxDownload) {
        this.maxDownload = maxDownload;
    }

    public int getSpeedPerSlot() {
        return speedPerSlot;
    }

    public void setSpeedPerSlot(int speedPerSlot) {
        this.speedPerSlot = speedPerSlot;
    }

    public String getIncomingDir() {
        return incomingDir;
    }

    public void setIncomingDir(String incomingDir) {
        this.incomingDir = incomingDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public Set getShareDirs() {
        return shareDirs;
    }

    public void setShareDirs(Set shareDirs) {
        this.shareDirs = shareDirs;
    }

    public long getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(long maxConnections) {
        this.maxConnections = maxConnections;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public long getMaxNewConnectionsPerTurn() {
        return maxNewConnectionsPerTurn;
    }

    public void setMaxNewConnectionsPerTurn(long maxNewConnectionsPerTurn) throws
        IllegalArgumentException {
        if (maxNewConnectionsPerTurn < 1 || maxNewConnectionsPerTurn > 200) {
            throw new IllegalArgumentException();
        }
        this.maxNewConnectionsPerTurn = maxNewConnectionsPerTurn;
    }
}
