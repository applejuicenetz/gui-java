package de.applejuicenet.client.shared.exception;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class InvalidPasswordException
    extends Exception {

  public InvalidPasswordException() {
    super("Ungültiges Kennwort");
  }
}