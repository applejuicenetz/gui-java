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
 * @author Maj0r <aj@tkl-soft.de>
 * 
 */

public interface DataUpdateListener {
	
	enum DATALISTENER_TYPE {
		DOWNLOAD_CHANGED, UPLOAD_CHANGED, SERVER_CHANGED, SHARE_CHANGED,
		NETINFO_CHANGED, SETTINGS_CHANGED, CONNECTION_SETTINGS_CHANGED, 
		SPEED_CHANGED, SEARCH_CHANGED, INFORMATION_CHANGED
	}

	public void fireContentChanged(DATALISTENER_TYPE type, Object content);
}
