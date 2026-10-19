package de.applejuicenet.client.facade.listener;

public interface CoreConnectionSettingsListener {
	
	enum ITEM {HOST, PORT, PASSWORD };
	
	void fireSettingsChanged(ITEM item, String oldValue, String newValue);
}
