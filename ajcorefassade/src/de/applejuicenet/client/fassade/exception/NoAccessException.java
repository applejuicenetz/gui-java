package de.applejuicenet.client.fassade.exception;

public class NoAccessException extends Exception {

	public NoAccessException(String message, Exception exception) {
		super(message, exception);
	}

}
