package de.applejuicenet.client.gui.listener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/listener/Attic/DataUpdateListener.java,v 1.9 2003/08/16 17:50:15 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DataUpdateListener.java,v $
 * Revision 1.9  2003/08/16 17:50:15  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.8  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public interface DataUpdateListener {
  public static final int DOWNLOAD_CHANGED = 0;
  public static final int UPLOAD_CHANGED = 1;
  public static final int SERVER_CHANGED = 2;
  public static final int SHARE_CHANGED = 3;
  public static final int NETINFO_CHANGED = 4;
  public static final int STATUSBAR_CHANGED = 5;
  public static final int SETTINGS_CHANGED = 6;

  public void fireContentChanged(int type, Object content);
}