package de.applejuicenet.client.fassade.event;

import java.util.Vector;

public abstract class DataPropertyChangeEvent {
	private final Object source;

	private final Object name;

	private final Object oldValue;

	private final Object newValue;

	private final DataPropertyChangeEvent[] events;

	public DataPropertyChangeEvent(Object source, Object name, Object oldValue,
			Object newValue) {
		this.source = source;
		this.name = name;
		this.oldValue = oldValue;
		this.newValue = newValue;
		events = null;
	}

	public DataPropertyChangeEvent(Vector dataPropertyChangeEvents) {
		events = (DataPropertyChangeEvent[]) dataPropertyChangeEvents
				.toArray(new DataPropertyChangeEvent[dataPropertyChangeEvents
						.size()]);
		source = null;
		name = null;
		oldValue = null;
		newValue = null;
	}

	public boolean isEventContainer() {
		return events != null;
	}

	public Object getName() {
		return name;
	}

	public Object getNewValue() {
		return newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getSource() {
		return source;
	}

	public DataPropertyChangeEvent[] getNestedEvents() {
		return events;
	}
}
