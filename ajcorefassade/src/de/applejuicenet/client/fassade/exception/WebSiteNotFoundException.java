package de.applejuicenet.client.fassade.exception;

public class WebSiteNotFoundException extends RuntimeException {
	public static final int AUTHORIZATION_REQUIRED = 407;

	public static final int UNKNOWN_HOST = 1;

	public static final int INPUT_ERROR = 2;

	private final int error;

	public WebSiteNotFoundException(int errorCode) {
		super("Die Webseite konnte nicht aufgerufen werden.");
		error = errorCode;
	}

	public WebSiteNotFoundException(int errorCode, Throwable t) {
		super("Die Webseite konnte nicht aufgerufen werden.", t);
		error = errorCode;
	}

	public int getErrorCode() {
		return error;
	}
}