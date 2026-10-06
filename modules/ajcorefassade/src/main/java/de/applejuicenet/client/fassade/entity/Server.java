package de.applejuicenet.client.fassade.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class Server implements IdOwner{

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

	public abstract String getName();

	public abstract String getHost();

	public abstract String getPort();

	public abstract long getTimeLastSeen();

	public abstract int getVersuche();

	public abstract int getId();

	public abstract boolean isConnected();

	public abstract boolean isTryConnect();
	
	public final String getIDasString() {
		return Integer.toString(getId());
	}

	public final String getTimeLastSeenAsString() {
		if (getTimeLastSeen() == 0) {
			return "";
		} else {
			return formatter.format(Instant.ofEpochMilli(getTimeLastSeen()).atZone(ZoneId.systemDefault()));
		}
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		return (getId() == ((Server) obj).getId());
	}

	@Override
	public final int hashCode() {
		return Integer.hashCode(getId());
	}
}
