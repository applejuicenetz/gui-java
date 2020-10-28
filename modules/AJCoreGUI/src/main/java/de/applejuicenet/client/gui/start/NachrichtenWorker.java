/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.start;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/NachrichtenWorker.java,v 1.5 2009/02/12 13:11:40 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class NachrichtenWorker extends Thread
{
   private final Logger    logger;
   private StartController startController;
   private JLabel          version;
   private JTextPane       nachrichten;

   public NachrichtenWorker(StartController startController, JLabel version, JTextPane nachrichten)
   {
      logger               = Logger.getLogger(getClass());
      this.startController = startController;
      this.version         = version;
      this.nachrichten     = nachrichten;
   }

   public void run()
   {
      if(logger.isEnabledFor(Level.DEBUG))
      {
         logger.debug("NachrichtenWorkerThread gestartet. " + this);
      }

      try
      {
         final String coreVersion = AppleJuiceClient.getAjFassade().getCoreVersion().getVersion();
         String       nachricht = "verwendeter Core: " + coreVersion;

         if(logger.isEnabledFor(Level.INFO))
         {
            logger.info(nachricht);
         }

         ProxySettings proxy    = ProxyManagerImpl.getInstance().getProxySettings();
         String        htmlText = WebsiteContentLoader.getWebsiteContent(proxy, "https://www.applejuicenet.de", 443,
                                                                         "/inprog/news.php?version=" +
                                                                         AppleJuiceClient.getAjFassade().getCoreVersion()
                                                                         .getVersion());

         int           pos = htmlText.toLowerCase().indexOf("<html>");

         StringBuilder buffer = new StringBuilder();

         if(pos != -1)
         {
            buffer.append(htmlText.substring(pos));
         }
         else
         {
            buffer.append("<html>");
            buffer.append(htmlText);
            buffer.append("</html>");
         }

         int index;

         while((index = buffer.indexOf(". ")) != -1)
         {
            buffer.replace(index, index + 1, ".<br>");
         }

         htmlText = buffer.toString();
         final String htmlContent = htmlText;

         SwingUtilities.invokeLater(() -> {
            version.setText("<html>GUI: " + AppleJuiceDialog.getVersion() + "<br>Core: " + coreVersion + "</html>");
            nachrichten.setContentType("text/html");
            nachrichten.setText(htmlContent);
            nachrichten.setFont(version.getFont());
         });
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.INFO))
         {
            logger.info("Versionsabhaengige Nachrichten konnten nicht geladen werden. Server down?");
         }
      }

      if(logger.isEnabledFor(Level.DEBUG))
      {
         logger.debug("NachrichtenWorkerThread beendet. " + this);
      }
   }
}
