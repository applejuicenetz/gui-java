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
  public static final int PROXY_AUTHORIZATION_REQUIRED = 407;

  private Exception innerException;

  public WebSiteNotFoundException(Exception e){
    super("Die Webseite konnte nicht aufgerufen werden.");
    innerException = e;
  }

  public String getMessage(){
    printStackTrace();
    return super.getMessage() + " " + innerException.getMessage();
  }

  public int getErrorCode(){
    //toDo
    if (super.getMessage().indexOf("HTTP response code")!=-1)
    {

    }
    return 0;
  }

  public void printStackTrace(){
    innerException.printStackTrace();
  }
}