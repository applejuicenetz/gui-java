package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/ShareEntry.java,v 1.5 2003/12/29 16:04:17 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI f�r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ShareEntry.java,v $
 * Revision 1.5  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.4  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.3  2003/06/10 12:31:03  maj0r
 * Historie eingef�gt.
 *
 *
 */

public class ShareEntry {
  public static final int SUBDIRECTORY = 0;
  public static final int SINGLEDIRECTORY = 1;

  private static final String sSUBDIRECTORY = "subdirectory";
  private static final String sSINGLEDIRECTORY = "singledirectory";

  private String dir;
  private int shareMode;

  public ShareEntry(String dir, int shareMode) {
    this.dir = dir;
    this.shareMode = shareMode;
  }

  public ShareEntry(String dir, String shareMode) {
    this.dir = dir;
    setShareMode(shareMode);
  }

  public String toString() {
    return dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public void setShareMode(int shareMode) {
    this.shareMode = shareMode;
  }

  private void setShareMode(String shareMode) {
    if (shareMode.compareToIgnoreCase(sSUBDIRECTORY) == 0) {
      this.shareMode = SUBDIRECTORY;
    }
    else if (shareMode.compareToIgnoreCase(sSINGLEDIRECTORY) == 0) {
      this.shareMode = SINGLEDIRECTORY;
    }
    else {
      this.shareMode = -1;
    }
  }

  public String getDir() {
    return dir;
  }

  public int getShareMode() {
    return shareMode;
  }
}