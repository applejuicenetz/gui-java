/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.start;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.exception.NoAccessException;
import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/NachrichtenWorker.java,v 1.5 2009/02/12 13:11:40 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class NachrichtenWorker extends Thread {
    private final Logger logger;
    private StartController startController;
    private JLabel version;
    private JTextPane nachrichten;

    public NachrichtenWorker(StartController startController, JLabel version, JTextPane nachrichten) {
        logger = LoggerFactory.getLogger(getClass());
        this.startController = startController;
        this.version = version;
        this.nachrichten = nachrichten;
        this.setName("NachrichtenWorker");
    }

    public void run() {
        logger.debug("NachrichtenWorkerThread gestartet.");

        try {
            final String coreVersion = AppleJuiceClient.getAjFassade().getCoreVersion().getVersion();
            logger.info("verwendeter Core: " + coreVersion);

            String htmlText = null;
            String newsURL = OptionsManagerImpl.getInstance().getNewsURL();
            String newsURLFormatted = String.format(newsURL, AppleJuiceClient.getAjFassade().getCoreVersion().getVersion());
            logger.debug(String.format("GET %s", newsURLFormatted));

            try {
                htmlText = WebsiteContentLoader.getWebsiteContent(newsURLFormatted);
            } catch (NoAccessException e) {
                newsURLFormatted = String.format(newsURL, "index"); // fallback to show regular news
                htmlText = WebsiteContentLoader.getWebsiteContent(newsURLFormatted);
            }

            int pos = htmlText.toLowerCase().indexOf("<html>");

            StringBuilder buffer = new StringBuilder();

            if (pos != -1) {
                buffer.append(htmlText.substring(pos));
            } else {
                buffer.append("<html>");
                buffer.append(htmlText);
                buffer.append("</html>");
            }

            int index;

            while ((index = buffer.indexOf(". ")) != -1) {
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
        } catch (Exception e) {
            logger.error("Versionsabhängige Nachrichten konnten nicht geladen werden.");
        }

        logger.debug("NachrichtenWorkerThread beendet.");
    }
}
