package de.applejuicenet.client.gui.start;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/HyperlinkAdapter.java,v 1.2 2004/10/15 13:34:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class HyperlinkAdapter implements HyperlinkListener{
	private Logger logger;
	private JFrame parent;
	
	public HyperlinkAdapter(JFrame parent){
		logger = Logger.getLogger(getClass());
		this.parent = parent;
	}
	
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (e.getURL() != null) {
				String url = e.getURL().toString();
				if (url.length() != 0) {
					executeLink(url);
				}
			}
		}
	}

	private void executeLink(String link) {
		try {
			String browser = OptionsManagerImpl.getInstance()
					.getStandardBrowser();
			try {
				Runtime.getRuntime().exec(new String[] { browser, link });
			} catch (Exception ex) {
				LanguageSelector ls = LanguageSelector.getInstance();
				String nachricht = ZeichenErsetzer
						.korrigiereUmlaute(ls
								.getFirstAttrbuteByTagName(".root.javagui.startup.updatefehlernachricht"));
				String titel = ZeichenErsetzer.korrigiereUmlaute(ls
						.getFirstAttrbuteByTagName(".root.mainform.caption"));
				JOptionPane.showMessageDialog(parent, nachricht, titel,
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}	
}
