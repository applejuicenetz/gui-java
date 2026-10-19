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
    private final JLabel version;
    private final JTextPane messages;
    private final java.util.function.Supplier<String> coreVersionSupplier;
    private final java.util.function.Supplier<String> newsUrlSupplier;
    private final Loader loader;

    interface Loader {
        String load(String url) throws NoAccessException;
    }


    public MessagesWorker(StartController startController, JLabel version, JTextPane messages) {
        this(version, messages, () -> AppleJuiceClient.getAjFacade().getCoreVersion().getVersion(),
                () -> OptionsManagerImpl.getInstance().getNewsURL(), WebsiteContentLoader::getWebsiteContent);
    }

    MessagesWorker(JLabel version, JTextPane messages, java.util.function.Supplier<String> coreVersionSupplier,
                   java.util.function.Supplier<String> newsUrlSupplier, Loader loader) {
        super("MessagesWorker");
        logger = LoggerFactory.getLogger(getClass());
        this.version = version;
        this.messages = messages;
        this.coreVersionSupplier = coreVersionSupplier;
        this.newsUrlSupplier = newsUrlSupplier;
        this.loader = loader;
    }

    public void run() {
        if (isInterrupted()) return;
        logger.debug("MessagesWorkerThread started.");

        java.util.concurrent.atomic.AtomicBoolean coreClosed = new java.util.concurrent.atomic.AtomicBoolean();
        boolean publicationQueued = false;
        de.applejuicenet.client.facade.listener.CoreStatusListener statusListener = status -> {
            if (status == de.applejuicenet.client.facade.listener.CoreStatusListener.STATUS.CLOSED) {
                coreClosed.set(true);
                interrupt();
            }
        };
        de.applejuicenet.client.facade.ApplejuiceFacade.addCoreStatusListener(statusListener);
        try {
            if (isInterrupted()) return;
            final String coreVersion = coreVersionSupplier.get();

            logger.info("Using core {}", coreVersion);

            String htmlText = null;
            String newsURL = newsUrlSupplier.get();
            String newsURLFormatted = String.format(newsURL, coreVersion);
            logger.debug(String.format("GET %s", newsURLFormatted));

            try {
                htmlText = loader.load(newsURLFormatted);
            } catch (NoAccessException e) {
                if (isInterrupted()) return;
                newsURLFormatted = String.format(newsURL, "index"); // fallback to show regular news
                htmlText = loader.load(newsURLFormatted);
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

            if (isInterrupted()) return;
            SwingUtilities.invokeLater(() -> {
                try {
                    if (coreClosed.get() || isInterrupted()) return;
                    version.setText("<html>GUI: " + AppleJuiceDialog.getVersion() + "<br>Core: " + coreVersion + "</html>");
                    messages.setContentType("text/html");
                    messages.setText(htmlContent);
                    messages.setFont(version.getFont());
                } finally {
                    de.applejuicenet.client.facade.ApplejuiceFacade.removeCoreStatusListener(statusListener);
                }
            });
            publicationQueued = true;
        } catch (Exception e) {
            if (!isInterrupted()) {
                logger.warn("Version-dependent messages could not be loaded.", e);
            }
        } finally {
            if (!publicationQueued) {
                de.applejuicenet.client.facade.ApplejuiceFacade.removeCoreStatusListener(statusListener);
            }
        }


        logger.debug("MessagesWorkerThread finished.");
    }
}
