package de.applejuicenet.client.gui.listener;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public interface DataUpdateListener {
  public static final int DOWNLOAD_CHANGED = 0;
  public static final int UPLOAD_CHANGED = 1;
  public static final int SERVER_CHANGED = 2;
  public static final int SHARE_CHANGED = 3;
  public static final int NETINFO_CHANGED = 4;

  public void fireContentChanged(int type, Object content);
}