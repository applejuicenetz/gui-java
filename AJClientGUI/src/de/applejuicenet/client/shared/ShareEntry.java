package de.applejuicenet.client.shared;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ShareEntry {
  public final int SUBDIRECTORY = 0;
  public final int SINGLEDIRECTORY = 1;

  public final String sSUBDIRECTORY = "subdirectory";
  public final String sSINGLEDIRECTORY = "singledirectory";

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

  public void setShareMode(String shareMode) {
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