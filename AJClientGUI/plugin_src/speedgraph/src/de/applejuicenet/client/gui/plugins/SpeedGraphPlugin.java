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
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/speedgraph/src/de/applejuicenet/client/gui/plugins/Attic/SpeedGraphPlugin.java,v 1.7 2004/10/14 08:57:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
