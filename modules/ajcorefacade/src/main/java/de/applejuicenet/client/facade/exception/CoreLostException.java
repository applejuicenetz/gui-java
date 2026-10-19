/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.exception;

@SuppressWarnings("serial")
public class CoreLostException extends RuntimeException
{
   public CoreLostException(Throwable t)
   {
      super(t);
   }

   public CoreLostException()
   {
      super();
   }
}
