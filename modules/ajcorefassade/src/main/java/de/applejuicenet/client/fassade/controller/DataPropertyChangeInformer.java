package de.applejuicenet.client.fassade.controller;

import de.applejuicenet.client.fassade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.fassade.listener.DataPropertyChangeListener;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;

public final class DataPropertyChangeInformer {
	private final Set<DataPropertyChangeListener> listener =
		new CopyOnWriteArraySet<DataPropertyChangeListener>();

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
