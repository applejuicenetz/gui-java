/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.io.File;

import java.net.URI;

import javax.swing.JOptionPane;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

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
      if(null != desktopToolIF && System.getProperty("os.name").toLowerCase().indexOf("linux") == -1)
      {
         /**
          * unter Linux ist #browse() teilweise nicht funktionstuechtig...
          */
         desktopToolIF.browse(uri);
      }
      else
      {
         String browser = OptionsManagerImpl.getInstance().getStandardBrowser();

         try
         {
            Runtime.getRuntime().exec(new String[] {browser, uri.toURL().toString()});
         }
         catch(Exception ex)
         {
            LanguageSelector ls        = LanguageSelector.getInstance();
            String           nachricht = ls.getFirstAttrbuteByTagName("javagui.startup.updatefehlernachricht");
            String           titel     = ls.getFirstAttrbuteByTagName("mainform.caption");

            JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), nachricht, titel, JOptionPane.INFORMATION_MESSAGE);
         }
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
