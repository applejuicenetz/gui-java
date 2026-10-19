package de.applejuicenet.client.facade.listener;

public interface CoreStatusListener {
	
	enum STATUS {STARTED, CLOSED};
	
	void fireStatusChanged(STATUS newStatus);
	
}
