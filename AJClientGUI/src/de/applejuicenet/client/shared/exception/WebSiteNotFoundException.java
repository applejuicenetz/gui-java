package de.applejuicenet.client.shared.exception;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class WebSiteNotFoundException extends Exception {
  public static final int AUTHORIZATION_REQUIRED = 407;
  public static final int UNKNOWN_HOST = 1;
  public static final int INPUT_ERROR = 2;

  private int error;

  public WebSiteNotFoundException(int errorCode){
    super("Die Webseite konnte nicht aufgerufen werden.");
    error = errorCode;
  }

  public int getErrorCode(){
    return error;
  }

}