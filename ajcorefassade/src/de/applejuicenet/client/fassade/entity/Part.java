package de.applejuicenet.client.fassade.entity;

public abstract class Part {

	public abstract long getFromPosition();

	public abstract int getType();

	public final boolean equals(Object obj) {
		if (obj.getClass() != getClass()) {
			return false;
		}
		if (((Part) obj).getFromPosition() != getFromPosition()) {
			return false;
		}
		if (((Part) obj).getType() != getType()) {
			return false;
		}
		return true;
	}
	
}
