package de.applejuicenet.client.shared.dac;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import de.applejuicenet.client.gui.tables.upload.UploadColumnComponent;
import de.applejuicenet.client.shared.Version;
import javax.swing.JTable;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/UploadDO.java,v 1.17 2004/02/24 18:21:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UploadDO.java,v $
 * Revision 1.17  2004/02/24 18:21:51  maj0r
 * Schrift korrigiert.
 *
 * Revision 1.16  2004/02/24 15:38:11  maj0r
 * CellRenderer optimiert indem die Komponenten in den DOs gehalten werden.
 *
 * Revision 1.15  2004/02/24 08:49:32  maj0r
 * Bug #240 gefixt (Danke an computer.ist.org)
 * Bug behoben, der im VersionChecker zu einer NoSuchElementException fuehrte.
 *
 * Revision 1.14  2004/02/18 17:24:21  maj0r
 * Von DOM auf SAX umgebaut.
 *
 * Revision 1.13  2004/02/09 14:21:32  maj0r
 * Icons für Upload-DirectStates eingebaut.
 *
 * Revision 1.12  2004/02/09 07:30:13  maj0r
 * Max. Anzahl von Quellen pro Datei kann begrenzt werden.
 *
 * Revision 1.11  2004/02/05 23:11:28  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.10  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.9  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.8  2003/08/09 10:57:54  maj0r
 * Upload- und DownloadTabelle weitergeführt.
 *
 * Revision 1.7  2003/08/04 14:28:55  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.6  2003/08/03 19:54:05  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.5  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefï¿½gt.
 *
 *
 */

public class UploadDO implements UploadColumnComponent{
    public static final int AKTIVE_UEBERTRAGUNG = 1;
    public static final int WARTESCHLANGE = 2;
    public static final int VERSUCHE_ZU_VERBINDEN = 5;
    public static final int VERSUCHE_INDIREKTE_VERBINDUNG = 6;
    public static final int KEINE_VERBINDUNG_MOEGLICH = 7;

    public static final int STATE_UNBEKANNT = 0;
    public static final int STATE_DIREKT_VERBUNDEN = 1;
    public static final int STATE_VERSUCHE_INDIREKTE_VERBINDUNG = 2;
    public static final int STATE_INDIREKT_VERBUNDEN = 3;

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

    private JProgressBar progress;
    private JLabel progressbarLabel;
    private JLabel versionLabel;

    public UploadDO(int uploadID){
        this.uploadID = uploadID;
        init();
    }

    public UploadDO(int uploadID, int shareFileID, Version version, int status,
                    String nick, long uploadFrom, long uploadTo,
                    long actualUploadPosition, int speed, int prioritaet, int directstate) {
        this.uploadID = uploadID;
        this.shareFileID = shareFileID;
        this.version = version;
        this.status = status;
        this.nick = nick;
        this.uploadFrom = uploadFrom;
        this.uploadTo = uploadTo;
        this.actualUploadPosition = actualUploadPosition;
        this.speed = speed;
        this.prioritaet = prioritaet;
        this.directstate = directstate;
        init();
    }

    public UploadDO(int uploadID, int shareFileID, Version version,
                    String status,
                    String nick, long uploadFrom, long uploadTo,
                    long actualUploadPosition, int speed, int prioritaet, int directstate) {
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
        init();
    }

    private void init(){
        progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progress.setStringPainted(true);
        progressbarLabel = new JLabel();
        progressbarLabel.setOpaque(true);
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
    }

    public long getUploadTo() {
        return uploadTo;
    }

    public void setUploadTo(long uploadTo) {
        this.uploadTo = uploadTo;
    }

    public long getActualUploadPosition() {
        return actualUploadPosition;
    }

    public void setActualUploadPosition(long actualUploadPosition) {
        this.actualUploadPosition = actualUploadPosition;
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

    public void setDateiName(String dateiName) {
        this.dateiName = dateiName;
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
            String prozent = getDownloadPercentAsString();
            int pos = prozent.indexOf('.');
            String balken = prozent;
            if (pos != -1) {
                balken = balken.substring(0, pos);
            }
            progress.setValue(Integer.parseInt(balken));
            progress.setString(prozent + " %");
            return progress;
        }
        else {
            return progressbarLabel;
        }
    }

    public Component getVersionComponent(JTable table, Object value) {
        versionLabel.setFont(table.getFont());
        if (getVersion() == null) {
            versionLabel.setIcon(null);
            versionLabel.setText("");
        }
        else {
            versionLabel.setIcon(getVersion().getVersionIcon());
            versionLabel.setText(getVersion().getVersion());
        }
        return versionLabel;
    }
}
