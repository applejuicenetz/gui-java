package de.applejuicenet.client.gui.plugins;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;

import de.applejuicenet.client.gui.plugins.ircplugin.XdccIrc;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/Attic/IrcPlugin.java,v 1.9 2004/03/03 15:35:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: IrcPlugin.java,v $
 * Revision 1.9  2004/03/03 15:35:45  maj0r
 * PMD-Optimierung
 *
 * Revision 1.8  2004/03/03 14:22:23  maj0r
 * Es wird nun Deutsch und Englisch unterstuetzt.
 *
 * Revision 1.7  2004/03/03 12:50:25  maj0r
 * Sprachunterstuetzung eungebaut.
 *
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
    private XdccIrc xdccIrc;

    public IrcPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon) {
        super(pluginsPropertiesXMLHolder, languageFiles, icon);
        setLayout(new BorderLayout());
        xdccIrc = new XdccIrc(this);
        add(xdccIrc, BorderLayout.CENTER);
    }

    public void fireLanguageChanged() {
        xdccIrc.fireLanguageChanged();
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
    }

    public void registerSelected() {
    }
}
