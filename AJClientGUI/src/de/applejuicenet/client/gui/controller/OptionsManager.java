package de.applejuicenet.client.gui.controller;

import java.io.*;

import org.apache.xerces.impl.dv.util.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.16 2003/08/02 12:03:38 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: OptionsManager.java,v $
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

  public String getSprache() {
    return getFirstAttrbuteByTagName(new String[] {"options", "sprache"});
  }

  public void setSprache(String sprache) {
    setAttributeByTagName(new String[] {"options", "sprache"}
                          , sprache.toLowerCase());
  }

  public Level getLogLevel(){
    String temp = getFirstAttrbuteByTagName(new String[] {"options", "logging", "level"});
    Level result = Level.OFF;
    if (temp.compareToIgnoreCase("INFO")==0)
      return Level.INFO;
    else if (temp.compareToIgnoreCase("DEBUG")==0)
      return Level.DEBUG;
    else if (temp.compareToIgnoreCase("WARN")==0)
      return Level.WARN;
    else if (temp.compareToIgnoreCase("FATAL")==0)
      return Level.FATAL;
    else if (temp.compareToIgnoreCase("ALL")==0)
      return Level.ALL;

    if (logger.isEnabledFor(Level.DEBUG))
      logger.debug("Aktueller Loglevel: " + result.toString());
    return result;
  }

  public void setLogLevel(Level level) {
    if (level==null)
      level = Level.OFF;
    String temp = "OFF";
    if (level==Level.ALL)
      temp="ALL";
    else if (level==Level.INFO)
      temp="INFO";
    else if (level==Level.DEBUG)
      temp="DEBUG";
    else if (level==Level.WARN)
      temp="WARN";
    else if (level==Level.FATAL)
      temp="FATAL";
    setAttributeByTagName(new String[] {"options", "logging", "level"}, temp);
    Logger.getRootLogger().setLevel(level);
    if (logger.isEnabledFor(Level.DEBUG))
      logger.debug("Loglevel geändert in " + level.toString());
  }

  public RemoteConfiguration getRemoteSettings() {
    String host = "localhost";
    String passwort = "";
    host = getFirstAttrbuteByTagName(new String[] {"options", "remote",
                                          "host"});
    passwort = getFirstAttrbuteByTagName(new String[] {"options",
                                              "remote", "passwort"});
    return new RemoteConfiguration(host, passwort);
  }

  public void saveRemote(RemoteConfiguration remote) throws
      InvalidPasswordException {
    if (!remote.getNewPassword().equalsIgnoreCase("")) {
      String altPasswort = getFirstAttrbuteByTagName(new String[] {"options", "remote",
                                    "passwort"});
      if (altPasswort.compareTo(remote.getOldPassword()) == 0) {
        setAttributeByTagName(new String[] {"options", "remote", "passwort"},
                              remote.getNewPassword());
      }
      else {
        throw new InvalidPasswordException();
      }
    }
    setAttributeByTagName(new String[] {"options", "remote", "host"}
                          , remote.getHost());
  }

  public boolean saveAJSettings(AJSettings ajSettings) {
    return DataManager.getInstance().saveAJSettings(ajSettings);
  }
}