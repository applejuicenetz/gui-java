package de.applejuicenet.client;

import java.awt.*;
import javax.swing.*;

import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.14 2003/06/22 20:03:54 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AppleJuiceClient.java,v $
 * Revision 1.14  2003/06/22 20:03:54  maj0r
 * Konsolenausgaben hinzugefügt.
 *
 * Revision 1.13  2003/06/22 19:54:45  maj0r
 * Behandlung von fehlenden Verzeichnissen und fehlenden xml-Dateien hinzugefügt.
 *
 * Revision 1.12  2003/06/22 19:02:58  maj0r
 * Fehlernachricht bei nicht erreichbarem Core geändert.
 *
 * Revision 1.11  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class AppleJuiceClient {
  public static void main(String[] args) {
    System.out.println("appleJuice-Core-GUI wird gestartet...");
    if (!DataManager.istCoreErreichbar()) {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName(new String[] {"mainform", "caption"}));
      String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName(new String[] {"javagui", "startup",
                                    "verbindungsfehler"}));
      nachricht = nachricht.replaceFirst("%s", OptionsManager.getInstance().getRemoteSettings().getHost());
      JOptionPane.showMessageDialog(new Frame(), nachricht, titel,
                                    JOptionPane.OK_OPTION);
      System.out.println("Fehler: " + nachricht);
      System.exit( -1);
    }
    AppleJuiceDialog theApp = new AppleJuiceDialog();
    Dimension appDimension = theApp.getSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    theApp.setLocation( (screenSize.width - appDimension.width) / 2,
                       (screenSize.height - appDimension.height) / 2);
    theApp.show();
    System.out.println("appleJuice-Core-GUI läuft...");
  }
}