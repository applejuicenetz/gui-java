/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.exception;

@SuppressWarnings("serial")
public class NoAccessException extends Exception
{
   public NoAccessException(String message, Exception exception)
   {
      super(message, exception);
   }
}
