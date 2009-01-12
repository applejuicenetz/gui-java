/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client;

import java.awt.Frame;

import javax.swing.JFrame;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.exception.CoreLostException;
import de.applejuicenet.client.fassade.exception.WrongPasswordException;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClientTG.java,v 1.11 2009/01/12 09:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class AppleJuiceClientTG extends ThreadGroup
{
   private Logger logger;

   public AppleJuiceClientTG()
   {
      super("AppleJuiceClientThreadGroup");
      logger = Logger.getLogger(getClass());
   }

   public void uncaughtException(Thread t, Throwable e)
   {
      if(e.getClass() == ClassCastException.class &&
            e.getMessage().equals("java.awt.TrayIcon cannot be cast to java.awt.Component"))
      {

         /**
          * "insignificantly error in java6 -> ignoring"
          */
      }
      else if(e.getClass() == ArrayIndexOutOfBoundsException.class)
      {
         if(logger.isEnabledFor(Level.DEBUG))
         {
            logger.debug(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
      else if(e.getClass() == WrongPasswordException.class)
      {
         AppleJuiceDialog.getApp().informWrongPassword();
      }
      else if(e.getClass() == CoreLostException.class)
      {
         LanguageSelector languageSelector = LanguageSelector.getInstance();
         String           nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName("javagui.startup.verbindungsfehler"));

         nachricht = nachricht.replaceFirst("%s", OptionsManagerImpl.getInstance().getRemoteSettings().getHost());
         AppleJuiceDialog.closeWithErrormessage(nachricht, true);
      }
      else
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private Frame findActiveFrame()
   {
      Frame[] frames = JFrame.getFrames();

      for(int i = 0; i < frames.length; i++)
      {
         Frame frame = frames[i];

         if(frame.isVisible())
         {
            return frame;
         }
      }

      return null;
   }
}
