package de.applejuicenet.client.shared.dac;

import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/UploadDO.java,v 1.8 2003/08/09 10:57:54 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: UploadDO.java,v $
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

public class UploadDO {
  public static final int AKTIVE_UEBERTRAGUNG = 1;
  public static final int WARTESCHLANGE = 2;
  public static final int VERSUCHE_ZU_VERBINDEN = 5;
  public static final int VERSUCHE_INDIREKTE_VERBINDUNG = 6;
  public static final int KEINE_VERBINDUNG_MOEGLICH = 7;

  private int uploadID;
  private String dateiName;
  private int shareFileID;
  private Version version;
  private int status;
  private String nick;
  private Long uploadFrom;
  private Long uploadTo;
  private Long actualUploadPosition;
  private Integer speed;
  private Integer prioritaet;

  public UploadDO(int uploadID, int shareFileID, Version version, int status,
                  String nick, Long uploadFrom, Long uploadTo,
                  Long actualUploadPosition, Integer speed, Integer prioritaet) {
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
  }

  public UploadDO(String uploadID, String shareFileID, Version version,
                  String status,
                  String nick, Long uploadFrom, Long uploadTo,
                  Long actualUploadPosition, Integer speed, Integer prioritaet) {
    this.uploadID = Integer.parseInt(uploadID);
    this.shareFileID = Integer.parseInt(shareFileID);
    this.version = version;
    this.status = Integer.parseInt(status);
    this.nick = nick;
    this.uploadFrom = uploadFrom;
    this.uploadTo = uploadTo;
    this.actualUploadPosition = actualUploadPosition;
    this.speed = speed;
    this.prioritaet = prioritaet;
  }

  public int getUploadID() {
    return uploadID;
  }

  public String getUploadIDAsString() {
    return Integer.toString(uploadID);
  }

  public void setUploadID(int uploadID) {
    this.uploadID = uploadID;
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

  public Long getUploadFrom() {
    return uploadFrom;
  }

  public void setUploadFrom(Long uploadFrom) {
    this.uploadFrom = uploadFrom;
  }

  public Long getUploadTo() {
    return uploadTo;
  }

  public void setUploadTo(Long uploadTo) {
    this.uploadTo = uploadTo;
  }

  public Long getActualUploadPosition() {
    return actualUploadPosition;
  }

  public void setActualUploadPosition(Long actualUploadPosition) {
    this.actualUploadPosition = actualUploadPosition;
  }

  public Integer getSpeed() {
    return speed;
  }

  public void setPrioritaet(Integer prioritaet) {
    this.prioritaet = prioritaet;
  }

  public Integer getPrioritaet() {
    return prioritaet;
  }

  public void setSpeed(Integer speed) {
    this.speed = speed;
  }

    public String getDateiName() {
        return dateiName;
    }

    public void setDateiName(String dateiName) {
        this.dateiName = dateiName;
    }

    public String getDownloadPercentAsString(){
        if (actualUploadPosition==null || uploadFrom==null)
            return "0";
        double temp = actualUploadPosition.intValue() - uploadFrom.intValue();
        if (temp==0.0){
            return "0";
        }
        temp =  temp * 100 / getSize();
        String result = Double.toString(temp);
        if (result.indexOf(".") + 3 < result.length())
        {
            result = result.substring(0, result.indexOf(".") + 3);
        }
        return result;
    }

    public int getSize(){
        if (uploadTo==null || uploadFrom==null)
            return 0;
        return uploadTo.intValue() - uploadFrom.intValue();
    }

}