package de.applejuicenet.client.gui.listener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/listener/Attic/DataUpdateListener.java,v 1.13 2003/10/21 11:36:32 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: DataUpdateListener.java,v $
 * Revision 1.13  2003/10/21 11:36:32  maj0r
 * Infos werden nun ueber einen Listener geholt.
 *
 * Revision 1.12  2003/09/30 16:35:11  maj0r
 * Suche begonnen und auf neues ID-Listen-Prinzip umgebaut.
 *
 * Revision 1.11  2003/09/13 11:30:41  maj0r
 * Neuen Listener fuer Geschwindigkeitsanzeigen eingebaut.
 *
 * Revision 1.10  2003/08/22 10:54:25  maj0r
 * Klassen umbenannt.
 * ConnectionSettings ueberarbeitet.
 *
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
  public static final int SETTINGS_CHANGED = 6;
  public static final int CONNECTION_SETTINGS_CHANGED = 7;
  public static final int SPEED_CHANGED = 8;
  public static final int SEARCH_CHANGED = 9;
  public static final int INFORMATION_CHANGED = 10;

  public void fireContentChanged(int type, Object content);
}