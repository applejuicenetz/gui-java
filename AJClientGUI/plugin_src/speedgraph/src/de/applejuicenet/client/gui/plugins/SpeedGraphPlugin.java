package de.applejuicenet.client.gui.plugins;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.plugins.speedgraph.GraphPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/speedgraph/src/de/applejuicenet/client/gui/plugins/Attic/SpeedGraphPlugin.java,v 1.6 2004/03/03 12:46:58 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: SpeedGraphPlugin.java,v $
 * Revision 1.6  2004/03/03 12:46:58  maj0r
 * Sprachunterstuetzung eungebaut.
 *
 * Revision 1.5  2004/03/02 21:14:13  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.4  2004/01/30 16:35:00  maj0r
 * MapSetStringKey ausgebaut.
 *
 * Revision 1.3  2003/12/22 16:25:35  maj0r
 * Version erhoeht.
 *
 * Revision 1.2  2003/09/15 07:28:45  maj0r
 * Plugin zeigt nun ein Raster und die Zeit auf der x-Achse.
 *
 * Revision 1.1  2003/09/13 11:33:17  maj0r
 * Neues Plugin SpeedGraph.
 *
 *
 */

public class SpeedGraphPlugin extends PluginConnector {
    private GraphPanel graphPanel = new GraphPanel();
    private static Logger logger;

    public SpeedGraphPlugin(XMLValueHolder pluginsPropertiesXMLHolder, HashMap languageFiles, ImageIcon icon) {
        super(pluginsPropertiesXMLHolder, languageFiles, icon);
        logger = Logger.getLogger(getClass());
        try{
            setLayout(new BorderLayout());
            graphPanel.setBackground(Color.BLACK);
            add(new JScrollPane(graphPanel), BorderLayout.CENTER);
            setBackground(Color.BLACK);
            ApplejuiceFassade.getInstance().addDataUpdateListener(this, DataUpdateListener.SPEED_CHANGED);
        }
        catch (Exception e){
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }
    }

    public void fireLanguageChanged() {
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
        if (type==DataUpdateListener.SPEED_CHANGED){
            graphPanel.update((HashMap) content);
        }
    }

    public void registerSelected() {
    }
}
