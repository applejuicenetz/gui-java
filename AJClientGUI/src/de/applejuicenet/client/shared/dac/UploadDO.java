package de.applejuicenet.client.shared.dac;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import de.applejuicenet.client.gui.tables.upload.UploadColumnComponent;
import de.applejuicenet.client.shared.Version;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/UploadDO.java,v 1.19 2004/05/23 17:58:29 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <Maj0r@applejuicenet.de>
 *
 */

public class UploadDO implements UploadColumnComponent{
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

    private JProgressBar progress;
    private JLabel progressbarLabel;
    private JLabel versionLabel;
    private boolean progressChanged = false;
    private boolean versionChanged = false;

    public UploadDO(int uploadID){
        this.uploadID = uploadID;
        init();
    }

    public UploadDO(int uploadID, int shareFileID, Version version,
                    String status,
                    String nick, long uploadFrom, long uploadTo,
                    long actualUploadPosition, int speed, int prioritaet,
                    int directstate, long lastConnection) {
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
        progressChanged = true;
        versionChanged = true;
        init();
    }

    private void init(){
        progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progress.setStringPainted(true);
        progressbarLabel = new JLabel();
        progressbarLabel.setOpaque(true);
        progress.setOpaque(false);
        versionLabel = new JLabel();
        versionLabel.setOpaque(true);
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

    public void setDateiName(String dateiName) {
        this.dateiName = dateiName;
    }

    public void setLastConnection(long lastConnection) {
        this.lastConnection = lastConnection;
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

    public Component getProgressbarComponent(JTable table, Object value) {
        if (status == UploadDO.AKTIVE_UEBERTRAGUNG) {
            if (progressChanged){
                String prozent = getDownloadPercentAsString();
                int pos = prozent.indexOf('.');
                String balken = prozent;
                if (pos != -1) {
                    balken = balken.substring(0, pos);
                }
                progress.setValue(Integer.parseInt(balken));
                progress.setString(prozent + " %");
                progressChanged = false;
            }
            return progress;
        }
        else {
            return progressbarLabel;
        }
    }

    public Component getVersionComponent(JTable table, Object value) {
        if(versionChanged){
            versionLabel.setFont(table.getFont());
            if (getVersion() == null) {
                versionLabel.setIcon(null);
                versionLabel.setText("");
            }
            else {
                versionLabel.setIcon(getVersion().getVersionIcon());
                versionLabel.setText(getVersion().getVersion());
            }
            versionChanged = false;
        }
        return versionLabel;
    }
}
