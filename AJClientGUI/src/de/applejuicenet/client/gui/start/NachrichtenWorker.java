package de.applejuicenet.client.gui.start;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.shared.WebsiteContentLoader;

public class NachrichtenWorker extends Thread {
	private final Logger logger;
	private StartController startController;
	private JLabel version;
	private JTextPane nachrichten;
	
	public NachrichtenWorker(StartController startController, JLabel version,
			JTextPane nachrichten){
		logger = Logger.getLogger(getClass());
		this.startController = startController;
		this.version = version;
		this.nachrichten = nachrichten;
	}
	
	public void run() {
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("NachrichtenWorkerThread gestartet. " + this);
		}
		try {
			String coreVersion = ApplejuiceFassade.getInstance()
					.getCoreVersion().getVersion();
			version.setText("<html>GUI: " + ApplejuiceFassade.GUI_VERSION
					+ "<br>Core: " + coreVersion + "</html>");
			String nachricht = "verwendeter Core: " + coreVersion;
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(nachricht);
			}
			String htmlText = WebsiteContentLoader.getWebsiteContent(
					"http://www.applejuicenet.org", 80,
					"/inprog/news.php?version="
							+ ApplejuiceFassade.getInstance()
									.getCoreVersion().getVersion());

			int pos = htmlText.toLowerCase().indexOf("<html>");
			if (pos != -1) {
				htmlText = htmlText.substring(pos);
			} else {
				htmlText = "<html>" + htmlText + "</html>";
			}
			StringBuffer buffer = new StringBuffer(htmlText);
			int index;
			while ((index = buffer.indexOf(". ")) != -1) {
				buffer.replace(index, index + 1, ".<br>");
			}
			htmlText = buffer.toString();
			final String htmlContent = htmlText;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					nachrichten.setContentType("text/html");
					nachrichten.setText(htmlContent);
					nachrichten.setFont(version.getFont());
				}
			});
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("Versionsabhaengige Nachrichten konnten nicht geladen werden. Server down?");
			}
		}
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("NachrichtenWorkerThread beendet. " + this);
		}
	}

}
