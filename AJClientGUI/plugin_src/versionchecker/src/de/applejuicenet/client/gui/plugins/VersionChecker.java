package de.applejuicenet.client.gui.plugins;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.panels.MainPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/Attic/VersionChecker.java,v 1.10 2005/01/24 18:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: GPL</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class VersionChecker extends PluginConnector {
    private MainPanel mainPanel;
    private Logger logger;

    public VersionChecker(XMLValueHolder xMLValueHolder, Map languageFiles, ImageIcon icon) {
        super(xMLValueHolder, languageFiles, icon);
        logger = Logger.getLogger(getClass());
        try{
            setLayout(new BorderLayout());
            mainPanel = new MainPanel();
            add(mainPanel, BorderLayout.CENTER);
            AppleJuiceClient.getAjFassade().addDataUpdateListener(this,
            		DATALISTENER_TYPE.DOWNLOAD_CHANGED);
            AppleJuiceClient.getAjFassade().addDataUpdateListener(this,
            		DATALISTENER_TYPE.UPLOAD_CHANGED);
        }
        catch(Exception e){
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void fireLanguageChanged() {
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        try {
            if (type == DATALISTENER_TYPE.DOWNLOAD_CHANGED) {
                HashMap downloads = (HashMap) content;
                mainPanel.updateByDownload(downloads);
            }
            else if (type == DATALISTENER_TYPE.UPLOAD_CHANGED) {
                HashMap uploads = (HashMap) content;
                mainPanel.updateByUploads(uploads);
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void registerSelected() {
    }
}
