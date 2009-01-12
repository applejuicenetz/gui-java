/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.start;

import java.awt.Component;
import java.awt.Cursor;
import java.net.URI;

import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.DesktopTools;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/HyperlinkAdapter.java,v 1.7 2009/01/12 09:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class HyperlinkAdapter implements HyperlinkListener
{
   private Logger    logger;
   private Component parent;

   public HyperlinkAdapter(Component parent)
   {
      logger      = Logger.getLogger(getClass());
      this.parent = parent;
   }

   public void hyperlinkUpdate(HyperlinkEvent e)
   {
      HyperlinkEvent.EventType type = e.getEventType();

      if(type == HyperlinkEvent.EventType.ENTERED)
      {
         parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      }
      else if(type == HyperlinkEvent.EventType.EXITED)
      {
         parent.setCursor(Cursor.getDefaultCursor());
      }
      else if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
      {
         if(e.getURL() != null)
         {
            String url = e.getURL().toString();

            if(url.length() != 0)
            {
               executeLink(url);
            }
         }
      }
   }

   private void executeLink(String link)
   {
      try
      {
         if(DesktopTools.isAdvancedSupported() && System.getProperty("os.name").toLowerCase().indexOf("linux") == -1)
         {
            DesktopTools.browse(new URI(link));
         }
         else
         {
            String browser = OptionsManagerImpl.getInstance().getStandardBrowser();

            try
            {
               Runtime.getRuntime().exec(new String[] {browser, link});
            }
            catch(Exception ex)
            {
               LanguageSelector ls        = LanguageSelector.getInstance();
               String           nachricht = ZeichenErsetzer.korrigiereUmlaute(ls.getFirstAttrbuteByTagName("javagui.startup.updatefehlernachricht"));
               String           titel     = ZeichenErsetzer.korrigiereUmlaute(ls.getFirstAttrbuteByTagName("mainform.caption"));

               JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), nachricht, titel, JOptionPane.INFORMATION_MESSAGE);
            }
         }
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }
}
