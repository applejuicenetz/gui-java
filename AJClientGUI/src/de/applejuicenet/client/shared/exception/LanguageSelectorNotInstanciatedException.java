package de.applejuicenet.client.shared.exception;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class LanguageSelectorNotInstanciatedException
    extends Exception {
  public LanguageSelectorNotInstanciatedException() {
    super("Der LanguageSelector muss erst initialisiert werden.");
  }
}