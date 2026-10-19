/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.start;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.exception.NoAccessException;
import de.applejuicenet.client.facade.shared.WebsiteContentLoader;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/MessagesWorker.java,v 1.5 2009/02/12 13:11:40 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class MessagesWorker extends Thread {
    private final Logger logger;
    private StartController startController;
    private JLabel version;
    private JTextPane messages;

    public MessagesWorker(StartController startController, JLabel version, JTextPane messages) {
        logger = LoggerFactory.getLogger(getClass());
        this.startController = startController;
        this.version = version;
        this.messages = messages;
        this.setName("NachrichtenWorker");
    }

    public void run() {
        logger.debug("MessagesWorkerThread started.");

        try {
            final String coreVersion = AppleJuiceClient.getAjFacade().getCoreVersion().getVersion();
            logger.info("verwendeter Core: " + coreVersion);

            String htmlText = null;
            String newsURL = OptionsManagerImpl.getInstance().getNewsURL();
            String newsURLFormatted = String.format(newsURL, AppleJuiceClient.getAjFacade().getCoreVersion().getVersion());
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
                messages.setContentType("text/html");
                messages.setText(htmlContent);
                messages.setFont(version.getFont());
            });
        } catch (Exception e) {
            logger.error("Version-dependent messages could not be loaded.");
        }

        logger.debug("NachrichtenWorkerThread beendet.");
    }
}
