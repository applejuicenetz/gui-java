package de.applejuicenet.client.gui.plugins;

import java.util.HashMap;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.plugins.panels.MainPanel;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/versionchecker/src/de/applejuicenet/client/gui/plugins/Attic/VersionChecker.java,v 1.7 2004/03/03 15:35:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: VersionChecker.java,v $
 * Revision 1.7  2004/03/03 15:35:45  maj0r
 * PMD-Optimierung
 *
 * Revision 1.6  2004/03/03 11:55:50  maj0r
 * Sprachunterstuetzung eungebaut.
 *
 * Revision 1.5  2004/03/02 21:06:40  maj0r
 * Kleine Fehler behoben.
 *
 * Revision 1.4  2004/03/02 21:05:46  maj0r
 * Schnittstelle veraendert.
 *
 * Revision 1.3  2004/03/02 17:36:11  maj0r
 * An neue Plugin-Schnittstelle angepasst.
 *
 * Revision 1.2  2004/01/30 16:32:56  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.1  2004/01/27 15:45:45  maj0r
 * Erste Version des Plugins VersionChecker gebaut.
 *
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
            ApplejuiceFassade.getInstance().addDataUpdateListener(this,
                DataUpdateListener.DOWNLOAD_CHANGED);
            ApplejuiceFassade.getInstance().addDataUpdateListener(this,
                DataUpdateListener.UPLOAD_CHANGED);
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
}
