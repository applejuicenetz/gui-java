package de.applejuicenet.client.facade.listener;

/**
 *
 * Copyright: General Public License
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
