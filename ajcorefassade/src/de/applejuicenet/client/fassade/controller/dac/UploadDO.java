package de.applejuicenet.client.fassade.controller.dac;

import de.applejuicenet.client.fassade.shared.Version;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/Attic/UploadDO.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class UploadDO {
    public static final int AKTIVE_UEBERTRAGUNG = 1;
    public static final int WARTESCHLANGE = 2;

    public static final int STATE_UNBEKANNT = 0;
    public static final int STATE_DIREKT_VERBUNDEN = 1;
    public static final int STATE_INDIREKT_VERBUNDEN = 2;

    private final int uploadID;
    private String dateiName;
    private int shareFileID;
    private Version version = null;
    private int status;
    private String nick;
    private long uploadFrom;
    private long uploadTo;
    private long actualUploadPosition;
    private int speed;
    private int prioritaet;
    private int directstate;
    private long lastConnection;

    private boolean progressChanged = false;
    private boolean wholeLoadedProgressChanged = false;
    private boolean versionChanged = false;
    private int loaded;

    public UploadDO(int uploadID){
        this.uploadID = uploadID;
    }

    public UploadDO(int uploadID, int shareFileID, Version version,
                    String status,
                    String nick, long uploadFrom, long uploadTo,
                    long actualUploadPosition, int speed, int prioritaet,
                    int directstate, long lastConnection, double loaded) {
        this.uploadID = uploadID;
        this.shareFileID = shareFileID;
        this.version = version;
        this.status = Integer.parseInt(status);
        this.nick = nick;
        this.uploadFrom = uploadFrom;
        this.uploadTo = uploadTo;
        this.actualUploadPosition = actualUploadPosition;
        this.speed = speed;
        this.prioritaet = prioritaet;
        this.directstate = directstate;
        this.lastConnection = lastConnection;
        this.setLoaded(loaded);
        progressChanged = true;
        versionChanged = true;
    }

    public int getUploadID() {
        return uploadID;
    }

    public String getUploadIDAsString() {
        return Integer.toString(uploadID);
    }

    public int getShareFileID() {
        return shareFileID;
    }

    public String getShareFileIDAsString() {
        return Integer.toString(shareFileID);
    }

    public void setShareFileID(int shareFileID) {
        this.shareFileID = shareFileID;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
        versionChanged = true;
    }

    public int getStatus() {
        return status;
    }

    public int getDirectState() {
        return directstate;
    }

    public void setDirectState(int directstate){
        this.directstate = directstate;
    }

    public String getStatusAsString() {
        return Integer.toString(status);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public long getUploadFrom() {
        return uploadFrom;
    }

    public void setUploadFrom(long uploadFrom) {
        this.uploadFrom = uploadFrom;
        progressChanged = true;
    }

    public long getUploadTo() {
        return uploadTo;
    }

    public void setUploadTo(long uploadTo) {
        this.uploadTo = uploadTo;
        progressChanged = true;
    }

    public long getActualUploadPosition() {
        return actualUploadPosition;
    }

    public void setActualUploadPosition(long actualUploadPosition) {
        this.actualUploadPosition = actualUploadPosition;
        progressChanged = true;
    }

    public int getSpeed() {
        return speed;
    }

    public void setPrioritaet(int prioritaet) {
        this.prioritaet = prioritaet;
    }

    public int getPrioritaet() {
        return prioritaet;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getDateiName() {
        return dateiName;
    }

    public long getLastConnection() {
        return lastConnection;
    }

    public int getLoaded() {
        return loaded;
    }

    public void setDateiName(String dateiName) {
        this.dateiName = dateiName;
    }

    public void setLastConnection(long lastConnection) {
        this.lastConnection = lastConnection;
    }

    public void setLoaded(double newValue) {
        if (newValue == -1){
            if (newValue != loaded){
                wholeLoadedProgressChanged = true;
                loaded = -1;
            }
        }
        else{
            int tmp = (int) (newValue * 100);
            if (tmp != loaded){
                wholeLoadedProgressChanged = true;
                loaded = tmp;
            }
        }
    }

    public String getDownloadPercentAsString() {
        if (actualUploadPosition == -1 || uploadFrom == -1) {
            return "0";
        }
        double temp = actualUploadPosition - uploadFrom;
        if (temp == 0.0) {
            return "0";
        }
        temp = temp * 100 / getSize();
        String result = Double.toString(temp);
        if (result.indexOf(".") + 3 < result.length()) {
            result = result.substring(0, result.indexOf(".") + 3);
        }
        return result;
    }

    public long getSize() {
        if (uploadTo == -1 || uploadFrom == -1) {
            return 0;
        }
        return uploadTo - uploadFrom;
    }
}
