package de.applejuicenet.client.gui.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.gui.plugins.speedgraph.GraphPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/speedgraph/src/de/applejuicenet/client/gui/plugins/Attic/SpeedGraphPlugin.java,v 1.8 2005/01/21 16:28:09 maj0r Exp $
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
            AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.SPEED_CHANGED);
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
    public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        if (type==DATALISTENER_TYPE.SPEED_CHANGED){
            graphPanel.update((HashMap) content);
        }
    }

    public void registerSelected() {
    }
}
