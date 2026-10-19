package de.applejuicenet.client.facade.controller;

import de.applejuicenet.client.facade.event.DataPropertyChangeEvent;
import de.applejuicenet.client.facade.listener.DataPropertyChangeListener;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;

public final class DataPropertyChangeInformer {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataPropertyChangeInformer.class);
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
            try {
                curListener.propertyChanged(dataPropertyChangeEvent);
            } catch (RuntimeException exception) {
                logger.warn("Property listener {} failed", curListener.getClass().getName(), exception);
            }
		}
	}
}
