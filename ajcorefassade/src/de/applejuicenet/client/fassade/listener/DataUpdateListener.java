package de.applejuicenet.client.fassade.listener;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/listener/DataUpdateListener.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r <aj@tkl-soft.de>
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