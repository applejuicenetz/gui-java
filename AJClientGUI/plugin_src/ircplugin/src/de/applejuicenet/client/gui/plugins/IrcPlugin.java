package de.applejuicenet.client.gui.plugins;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.plugins.ircplugin.XdccIrc;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/Attic/IrcPlugin.java,v 1.6 2004/03/02 21:17:35 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: IrcPlugin.java,v $
 * Revision 1.6  2004/03/02 21:17:35  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.5  2003/10/27 18:26:58  maj0r
 * Bugs behoben...
 *
 * Revision 1.4  2003/10/27 14:10:07  maj0r
 * Header eingefuegt.
 *
 *
 */

public class IrcPlugin extends PluginConnector {

    public IrcPlugin(PluginsPropertiesXMLHolder pluginsPropertiesXMLHolder, ImageIcon icon) {
        super(pluginsPropertiesXMLHolder, icon);
        setLayout(new BorderLayout());
        add(new XdccIrc(), BorderLayout.CENTER);
    }

    public void fireLanguageChanged() {
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
    }

    public void registerSelected() {
    }
}
