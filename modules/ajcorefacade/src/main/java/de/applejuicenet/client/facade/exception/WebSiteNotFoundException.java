/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.exception;

@SuppressWarnings("serial")
public class WebSiteNotFoundException extends RuntimeException
{
   public static final int AUTHORIZATION_REQUIRED = 407;
   public static final int UNKNOWN_HOST = 1;
   public static final int INPUT_ERROR = 2;
   private final int       error;

   public WebSiteNotFoundException(int errorCode)
   {
      super("The website could not be opened.");
      error = errorCode;
   }

   public WebSiteNotFoundException(int errorCode, Throwable t)
   {
      super("The website could not be opened.", t);
      error = errorCode;
   }

   public int getErrorCode()
   {
      return error;
   }
}
