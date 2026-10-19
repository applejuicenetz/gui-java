package de.applejuicenet.client.shared.exception;

/**
 * Copyright: General Public License
 */

public class InvalidPasswordException
    extends Exception {

	public InvalidPasswordException() {
        super("Invalid password");
    }
}
