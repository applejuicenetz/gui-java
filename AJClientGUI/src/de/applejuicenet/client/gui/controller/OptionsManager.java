package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.xerces.impl.dv.util.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.21 2003/08/19 12:38:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: OptionsManager.java,v $
 * Revision 1.21  2003/08/19 12:38:47  maj0r
 * Passworteingabe und md5 korrigiert.
 *
 * Revision 1.20  2003/08/16 18:40:25  maj0r
 * Passworteingabe korrigiert.
 *
 * Revision 1.19  2003/08/16 17:50:06  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.18  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.17  2003/08/11 14:10:28  maj0r
 * DownloadPartList eingefügt.
 * Diverse Änderungen.
 *
 * Revision 1.16  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.15  2003/07/01 14:55:06  maj0r
 * Unnütze Abfrage entfernt.
 *
 * Revision 1.14  2003/06/24 14:32:27  maj0r
 * Klassen zum Sortieren von Tabellen eingefügt.
 * Servertabelle kann nun spaltenweise sortiert werden.
 *
 * Revision 1.13  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
 * Revision 1.12  2003/06/22 19:01:22  maj0r
 * Hostverwendung korrigiert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class OptionsManager
        extends XMLDecoder {
    private static OptionsManager instance = null;
    private Logger logger;
    private HashSet settingsListener = new HashSet();

    private OptionsManager(String path) {
        super(path);
        logger = Logger.getLogger(getClass());
    }

    public static OptionsManager getInstance() {
        if (instance == null) {
            String path = System.getProperty("user.dir") + File.separator +
                    "properties.xml";
            instance = new OptionsManager(path);
        }
        return instance;
    }

    public void addSettingsListener(DataUpdateListener listener) {
        if (!(settingsListener.contains(listener))) {
            settingsListener.add(listener);
        }
    }

    private void informSettingsListener(Settings settings) {
        Iterator it = settingsListener.iterator();
        while (it.hasNext()) {
            ((DataUpdateListener) it.next()).fireContentChanged(DataUpdateListener.SETTINGS_CHANGED, settings);
        }
    }

    public String getSprache() {
        return getFirstAttrbuteByTagName(new String[]{"options", "sprache"});
    }

    public void setSprache(String sprache) {
        setAttributeByTagName(new String[]{"options", "sprache"}
                , sprache.toLowerCase());
    }

    public Level getLogLevel() {
        String temp = getFirstAttrbuteByTagName(new String[]{"options", "logging", "level"});
        Level result = Level.OFF;
        if (temp.compareToIgnoreCase("INFO") == 0)
            return Level.INFO;
        else if (temp.compareToIgnoreCase("DEBUG") == 0)
            return Level.DEBUG;
        else if (temp.compareToIgnoreCase("WARN") == 0)
            return Level.WARN;
        else if (temp.compareToIgnoreCase("FATAL") == 0)
            return Level.FATAL;
        else if (temp.compareToIgnoreCase("ALL") == 0)
            return Level.ALL;

        if (logger.isEnabledFor(Level.DEBUG))
            logger.debug("Aktueller Loglevel: " + result.toString());
        return result;
    }

    public void setLogLevel(Level level) {
        if (level == null)
            level = Level.OFF;
        String temp = "OFF";
        if (level == Level.ALL)
            temp = "ALL";
        else if (level == Level.INFO)
            temp = "INFO";
        else if (level == Level.DEBUG)
            temp = "DEBUG";
        else if (level == Level.WARN)
            temp = "WARN";
        else if (level == Level.FATAL)
            temp = "FATAL";
        setAttributeByTagName(new String[]{"options", "logging", "level"}, temp);
        Logger.getRootLogger().setLevel(level);
        if (logger.isEnabledFor(Level.DEBUG))
            logger.debug("Loglevel geändert in " + level.toString());
    }

    public Settings getSettings() {
        Color downloadFertigHintergrundColor = null;
        Color quelleHintergrundColor = null;
        Boolean farbenAktiv = null;
        Boolean downloadUebersicht = null;
        String temp;
        temp = getFirstAttrbuteByTagName(new String[]{"options", "farben",
                                                      "aktiv"});
        if (temp.length() != 0) {
            farbenAktiv = new Boolean(temp);
        }
        temp = getFirstAttrbuteByTagName(new String[]{"options", "farben",
                                                      "hintergrund", "downloadFertig"});
        if (temp.length() != 0) {
            downloadFertigHintergrundColor = new Color(Integer.parseInt(temp));
        }
        temp = getFirstAttrbuteByTagName(new String[]{"options", "farben",
                                                      "hintergrund", "quelle"});
        if (temp.length() != 0) {
            quelleHintergrundColor = new Color(Integer.parseInt(temp));
        }
        temp = getFirstAttrbuteByTagName(new String[]{"options", "download",
                                                      "uebersicht"});
        if (temp.length() != 0) {
            downloadUebersicht = new Boolean(temp);
        }
        return new Settings(farbenAktiv, downloadFertigHintergrundColor, quelleHintergrundColor, downloadUebersicht);
    }


    public void saveSettings(Settings settings) {
        setAttributeByTagName(new String[]{"options", "farben", "aktiv"},
                Boolean.toString(settings.isFarbenAktiv()));
        setAttributeByTagName(new String[]{"options", "farben", "hintergrund", "downloadFertig"},
                Integer.toString(settings.getDownloadFertigHintergrundColor().getRGB()));
        setAttributeByTagName(new String[]{"options", "farben", "hintergrund", "quelle"},
                Integer.toString(settings.getQuelleHintergrundColor().getRGB()));
        setAttributeByTagName(new String[]{"options", "download", "uebersicht"},
                Boolean.toString(settings.isDownloadUebersicht()));
        informSettingsListener(settings);
    }

    public RemoteConfiguration getRemoteSettings() {
        String host = "localhost";
        String passwort = "";
        host = getFirstAttrbuteByTagName(new String[]{"options", "remote",
                                                      "host"});
        passwort = getFirstAttrbuteByTagName(new String[]{"options",
                                                          "remote", "passwort"});
        return new RemoteConfiguration(host, passwort);
    }

    public void saveRemote(RemoteConfiguration remote) throws
            InvalidPasswordException {
        setAttributeByTagName(new String[]{"options", "remote", "host"}
                , remote.getHost());
        ApplejuiceFassade.setPassword(remote.getNewPassword());
        setAttributeByTagName(new String[]{"options", "remote", "passwort"},
                remote.getNewPassword());
    }

    public boolean saveAJSettings(AJSettings ajSettings) {
        return ApplejuiceFassade.getInstance().saveAJSettings(ajSettings);
    }
}