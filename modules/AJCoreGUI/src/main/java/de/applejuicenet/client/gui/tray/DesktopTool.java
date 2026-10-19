/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.tray;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class DesktopTool
{
   private static final Logger logger = LoggerFactory.getLogger(DesktopTool.class);

   public DesktopTool()
   {
      if(!Desktop.isDesktopSupported())
      {
         throw new RuntimeException("desktop not supported");
      }
   }

   public boolean isBrowseSupported()
   {
      return Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
   }

   public void browse(URI uri) throws IOException
   {
      Desktop.getDesktop().browse(uri);
   }

   public void open(File toOpen)
   {
      try
      {
         Desktop.getDesktop().open(toOpen);
      }
      catch(IOException e)
      {
         logger.warn("Cannot open {}", toOpen, e);
      }
   }
}
