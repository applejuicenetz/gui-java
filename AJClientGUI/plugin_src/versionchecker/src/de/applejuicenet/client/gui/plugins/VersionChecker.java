package de.applejuicenet.client.gui.plugins;

import java.awt.BorderLayout;
import de.applejuicenet.client.gui.plugins.panels.MainPanel;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/Attic/VersionChecker.java,v 1.1 2004/01/27 15:45:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: VersionChecker.java,v $
 * Revision 1.1  2004/01/27 15:45:45  maj0r
 * Erste Version des Plugins VersionChecker gebaut.
 *
 *
 */

public class VersionChecker extends PluginConnector {
    private MainPanel mainPanel;
    private Logger logger;

    public VersionChecker() {
        setLayout(new BorderLayout());
        logger = Logger.getLogger(getClass());
        mainPanel = new MainPanel();
        add(mainPanel, BorderLayout.CENTER);
        initIcon();
        ApplejuiceFassade.getInstance().addDataUpdateListener(this, DataUpdateListener.DOWNLOAD_CHANGED);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this, DataUpdateListener.UPLOAD_CHANGED);
    }

    public void fireLanguageChanged() {
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.DOWNLOAD_CHANGED) {
                HashMap downloads = (HashMap) content;
                mainPanel.updateByDownload(downloads);
            }
            else if (type == DataUpdateListener.UPLOAD_CHANGED) {
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

    public String getTitle() {
        return "VersionChecker";
    }

    public String getAutor() {
        return "Maj0r";
    }

    public String getBeschreibung() {
        return "Welche Core-Versionen sind zurzeit im Einsatz?\r\n" +
            "Welche Betriebssysteme werden eingesetzt?\r\n" +
            "Dieses Plugin beantwortet Deine Fragen.";
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean istReiter() {
        return true;
    }
}