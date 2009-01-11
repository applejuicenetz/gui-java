/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.shared;

import java.io.File;

import java.net.URI;

@SuppressWarnings("unchecked")
public class DesktopTools
{
   private static DesktopToolIF desktopToolIF;

   static
   {
      try
      {
         Class aClass = Class.forName("de.applejuicenet.client.gui.tray.DesktopToolJava6");

         desktopToolIF = (DesktopToolIF) aClass.newInstance();
      }
      catch(Throwable e)
      {
         desktopToolIF = null;
      }
   }

   public static boolean isAdvancedSupported()
   {
      return null != desktopToolIF;
   }

   public static void browse(URI uri)
   {
      if(null != desktopToolIF)
      {
         desktopToolIF.browse(uri);
      }
   }

   public static void open(File toOpen)
   {
      if(null != desktopToolIF)
      {
         desktopToolIF.open(toOpen);
      }
   }
}
