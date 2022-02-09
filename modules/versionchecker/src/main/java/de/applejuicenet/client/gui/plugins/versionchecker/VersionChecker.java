/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.versionchecker;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.plugins.versionchecker.panels.MainPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/versionchecker/VersionChecker.java,v 1.5 2009/01/12 10:18:00 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: GPL</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class VersionChecker extends PluginConnector {
    private MainPanel mainPanel;
    private final Logger logger;

    public VersionChecker(Properties pluginProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                          Map<String, ImageIcon> availableIcons) {
        super(pluginProperties, languageFiles, icon, availableIcons);
        logger = LoggerFactory.getLogger(getClass());
        try {
            setLayout(new BorderLayout());
            mainPanel = new MainPanel();
            add(mainPanel, BorderLayout.CENTER);
            AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
            AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.UPLOAD_CHANGED);
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public void fireLanguageChanged() {
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      ueber den DataManger koennen diese abgerufen werden.*/
    @SuppressWarnings("unchecked")
    public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        try {
            if (type == DATALISTENER_TYPE.DOWNLOAD_CHANGED) {
                HashMap<String, Download> downloads = (HashMap<String, Download>) content;

                mainPanel.updateByDownload(downloads);
            } else if (type == DATALISTENER_TYPE.UPLOAD_CHANGED) {
                HashMap<String, Upload> uploads = (HashMap<String, Upload>) content;

                mainPanel.updateByUploads(uploads);
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public void registerSelected() {
    }
}
