package de.applejuicenet.client.shared.exception;

public class NodeAlreadyExistsException
    extends Exception {
    private static final long serialVersionUID = 2999552544896825684L;

	public NodeAlreadyExistsException(String text) {
        super(text);
    }
}
