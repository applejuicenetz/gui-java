package de.applejuicenet.client.gui.controller;

import java.io.*;
import java.util.*;

import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.gui.AppleJuiceDialog;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/LanguageSelector.java,v 1.7 2003/06/22 19:54:45 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: LanguageSelector.java,v $
 * Revision 1.7  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefügt.
 *
 * Revision 1.6  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class LanguageSelector
    extends XMLDecoder {
  private HashSet languageListener = new HashSet();
  private static LanguageSelector instance = null;

  private LanguageSelector(String path) {
    super(path);
  }

  public static LanguageSelector getInstance() {
    if (instance == null) {
      String path = System.getProperty("user.dir") + File.separator +
          "language" +
          File.separator;
      OptionsManager op = OptionsManager.getInstance();
      String datei = op.getSprache();
      path += datei + ".xml";
      //zZ werden die Header der TableModel nicht aktualisiert, deshalb hier schon
      return new LanguageSelector(path);
    }
    return instance;
  }

  public static LanguageSelector getInstance(String path) {
    if (instance == null) {
      instance = new LanguageSelector(path);
    }
    else {
      File sprachDatei = new File(path);
      if (!sprachDatei.isFile()){
        AppleJuiceDialog.closeWithErrormessage("Die in der settings.xml hinterlegte Sprachdatei wurde nicht gefunden.\r\nappleJuice wird beendet.", false);
      }
      instance.reload(sprachDatei);
      instance.informLanguageListener();
    }
    return instance;
  }

  public void addLanguageListener(LanguageListener listener) {
    if (! (languageListener.contains(listener))) {
      languageListener.add(listener);
    }
  }

  private void informLanguageListener() {
    Iterator it = languageListener.iterator();
    while (it.hasNext()) {
      ( (LanguageListener) it.next()).fireLanguageChanged();
    }
  }
}