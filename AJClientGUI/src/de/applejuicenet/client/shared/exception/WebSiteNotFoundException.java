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
  private Exception innerException;

  public WebSiteNotFoundException(Exception e){
    super("Die Webseite konnte nicht aufgerufen werden.");
    innerException = e;
  }

  public String getMessage(){
    printStackTrace();
    return super.getMessage() + " " + innerException.getMessage();
  }

  public void printStackTrace(){
    innerException.printStackTrace();
  }
}