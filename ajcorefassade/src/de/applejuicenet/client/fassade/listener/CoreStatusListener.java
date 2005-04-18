package de.applejuicenet.client.fassade.listener;

public interface CoreStatusListener {
	
	enum STATUS {STARTED, CLOSED};
	
	void fireStatusChanged(STATUS newStatus);
	
}
