package de.applejuicenet.client.facade.controller;

import de.applejuicenet.client.facade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.facade.listener.DataPropertyChangeListener;

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
