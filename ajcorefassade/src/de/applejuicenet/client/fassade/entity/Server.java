package de.applejuicenet.client.fassade.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Server {

	private static SimpleDateFormat formatter = new SimpleDateFormat(
	"dd.MM.yyyy HH:mm:ss");

	public abstract String getName();

	public abstract String getHost();

	public abstract String getPort();

	public abstract long getTimeLastSeen();

	public abstract int getVersuche();

	public abstract int getID();

	public abstract boolean isConnected();

	public abstract boolean isTryConnect();
	
	public final String getIDasString() {
		return Integer.toString(getID());
	}

	public final String getTimeLastSeenAsString() {
		if (getTimeLastSeen() == 0) {
			return "";
		} else {
			return formatter.format(new Date(getTimeLastSeen()));
		}
	}

	public final boolean equals(Object obj) {
		if (obj.getClass() != getClass()) {
			return false;
		}
		return (getID() == ((Server) obj).getID());
	}
}
