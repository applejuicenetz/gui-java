package de.applejuicenet.client.gui.plugins;

import javax.swing.ImageIcon;
import java.util.HashMap;
import java.awt.BorderLayout;

import de.applejuicenet.client.gui.plugins.ircplugin.XdccIrc;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: ajIRC-Plugin</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class IrcPlugin1_1 extends PluginConnector {

    public IrcPlugin1_1() {
        setLayout(new BorderLayout());
        add(new XdccIrc(), BorderLayout.CENTER);
    }

    public void fireLanguageChanged() {
//    System.out.println("Sprache wurde geändert");
    }

    /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
      Über den DataManger können diese abgerufen werden.*/
    public void fireContentChanged(int type, Object content) {
    }

    public void registerSelected() {
//    System.out.println("Reiter wurde angeklickt.");
    }

    public String getTitle() {
        return "ajIRC";
    }

    public String getAutor() {
        return "Maj0r";
    }

    public String getBeschreibung() {
        return "Dies ist das absolut erste Plugin für die appleJuice-Java-GUI.\r\n\r\n"
                + "Der IRC-Client unterstützt das Besuchen mehrerer Räume, Queries, usw.";
    }

    public String getVersion() {
        return "1.1";
    }

    public boolean istReiter() {
        return true;
    }
}