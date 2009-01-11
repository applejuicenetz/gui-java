/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.tray;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import de.applejuicenet.client.shared.DesktopToolIF;

public class DesktopToolJava6 implements DesktopToolIF
{
   public DesktopToolJava6()
   {
      if(!Desktop.isDesktopSupported())
      {
         throw new RuntimeException("desktop not supported");
      }
   }

   @Override
   public void browse(URI uri)
   {
      try
      {
         Desktop.getDesktop().browse(uri);
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void open(File toOpen)
   {
      try
      {
         Desktop.getDesktop().open(toOpen);
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}
