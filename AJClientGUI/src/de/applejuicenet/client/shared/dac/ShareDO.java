package de.applejuicenet.client.shared.dac;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ShareDO {
  private String id;
  private String filename;
  private String size;
  private String checksum;

  public ShareDO(String id, String filename, String size, String checksum) {
    this.id = id;
    this.filename = filename;
    this.size = size;
    this.checksum = checksum;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public String getSize() {
    return size;
  }

  public String getCheckSum() {
    return checksum;
  }

}