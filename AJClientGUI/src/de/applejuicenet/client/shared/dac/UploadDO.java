package de.applejuicenet.client.shared.dac;

import de.applejuicenet.client.shared.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class UploadDO {
  public static int AKTIVE_UEBERTRAGUNG = 1;
  public static int WARTESCHLANGE = 2;

  private int uploadID;
  private int shareFileID;
  private Version version;
  private int status;
  private String nick;
  private String uploadFrom;
  private String uploadTo;
  private String actualUploadPosition;
  private String speed;
  private String prioritaet;

  public UploadDO(int uploadID, int shareFileID, Version version, int status,
                  String nick, String uploadFrom, String uploadTo,
                  String actualUploadPosition, String speed, String prioritaet) {
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
                  String nick, String uploadFrom, String uploadTo,
                  String actualUploadPosition, String speed, String prioritaet) {
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

  public String getUploadFrom() {
    return uploadFrom;
  }

  public void setUploadFrom(String uploadFrom) {
    this.uploadFrom = uploadFrom;
  }

  public String getUploadTo() {
    return uploadTo;
  }

  public void setUploadTo(String uploadTo) {
    this.uploadTo = uploadTo;
  }

  public String getActualUploadPosition() {
    return actualUploadPosition;
  }

  public void setActualUploadPosition(String actualUploadPosition) {
    this.actualUploadPosition = actualUploadPosition;
  }

  public String getSpeed() {
    return speed;
  }

  public void setPrioritaet(String prioritaet) {
    this.prioritaet = prioritaet;
  }

  public String getPrioritaet() {
    return prioritaet;
  }

  public void setSpeed(String speed) {
    this.speed = speed;
  }
}