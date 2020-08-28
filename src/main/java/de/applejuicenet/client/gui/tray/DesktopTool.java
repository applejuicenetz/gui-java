/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.tray;


import java.awt.*;
import java.io.File;

import java.io.IOException;
import java.net.URI;

public class DesktopTool
{
   public DesktopTool()
   {
      if(!Desktop.isDesktopSupported())
      {
         throw new RuntimeException("desktop not supported");
      }
   }

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
