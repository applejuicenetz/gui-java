package de.applejuicenet.client.gui.controller;

import java.io.*;

import org.apache.xerces.impl.dv.util.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/OptionsManager.java,v 1.11 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: OptionsManager.java,v $
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class OptionsManager
    extends XMLDecoder {
  private static OptionsManager instance = null;

  private OptionsManager(String path) {
    super(path);
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

  public RemoteConfiguration getRemoteSettings() {
    String host = getFirstAttrbuteByTagName(new String[] {"options", "remote",
                                            "host"});
    String passwort = new String(Base64.decode(getFirstAttrbuteByTagName(new
        String[] {"options", "remote", "passwort"})));
    boolean use = getFirstAttrbuteByTagName(new String[] {"options", "remote",
                                            "use"}).equals("1");
    return new RemoteConfiguration(host, passwort, use);
  }

  public void saveRemote(RemoteConfiguration remote) throws
      InvalidPasswordException {
    if (!remote.isRemoteUsed()) {
      setAttributeByTagName(new String[] {"options", "remote", "use"}
                            , (remote.isRemoteUsed() ? "1" : "0"));
      return;
    }
    if (!remote.getNewPassword().equalsIgnoreCase("")) {
      String altPasswortBase64FromXML = new String(Base64.decode(
          getFirstAttrbuteByTagName(new String[] {"options", "remote",
                                    "passwort"})));
      if (altPasswortBase64FromXML.compareTo(remote.getOldPassword()) == 0) {
        setAttributeByTagName(new String[] {"options", "remote", "passwort"}
                              ,
                              new String(Base64.encode(remote.getNewPassword().
            getBytes())));
      }
      else {
        throw new InvalidPasswordException();
      }
    }
    setAttributeByTagName(new String[] {"options", "remote", "host"}
                          , remote.getHost());
    if (remote.isRemoteUsed()) {
      setAttributeByTagName(new String[] {"options", "remote", "use"}
                            , (remote.isRemoteUsed() ? "1" : "0"));
    }
  }

  public boolean saveAJSettings(AJSettings ajSettings) {
    return DataManager.getInstance().saveAJSettings(ajSettings);
  }
}