package de.applejuicenet.client.shared.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/ShareDO.java,v 1.4 2003/07/02 13:54:34 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareDO.java,v $
 * Revision 1.4  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ShareDO {
  private String id;
  private String filename;
  private String shortfilename;
  private String size;
  private String checksum;

  public ShareDO(String id, String filename, String shortfilename, String size, String checksum) {
    this.id = id;
    this.filename = filename;
    this.shortfilename = shortfilename;
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

  public String getShortfilename() {
      return shortfilename;
  }

  public void setShortfilename(String shortfilename) {
      this.shortfilename = shortfilename;
  }
}