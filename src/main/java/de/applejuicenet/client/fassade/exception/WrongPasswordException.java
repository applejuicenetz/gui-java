/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.exception;

@SuppressWarnings("serial")
public class WrongPasswordException extends RuntimeException
{
   public WrongPasswordException()
   {
      super("corepassword invalid");
   }
}
