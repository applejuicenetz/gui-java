package de.applejuicenet.client.fassade.controller;

import java.util.HashSet;
import java.util.Set;

import de.applejuicenet.client.fassade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.fassade.listener.DataPropertyChangeListener;

public final class DataPropertyChangeInformer {
	private Set<DataPropertyChangeListener> listener = 
		new HashSet<DataPropertyChangeListener>();

	public void addDataPropertyChangeListener(
			DataPropertyChangeListener dataPropertyChangeListener) {
		listener.add(dataPropertyChangeListener);
	}

	public void removeDataPropertyChangeListener(
			DataPropertyChangeListener dataPropertyChangeListener) {
		listener.remove(dataPropertyChangeListener);
	}

	public void propertyChanged(DataPropertyChangeEvent dataPropertyChangeEvent) {
		for (DataPropertyChangeListener curListener : listener) {
			curListener.propertyChanged(dataPropertyChangeEvent);
		}
	}
}