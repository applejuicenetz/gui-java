package de.applejuicenet.client;

import java.io.*;
import java.text.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import org.apache.log4j.*;
import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClient.java,v 1.15 2003/06/24 12:06:49 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: AppleJuiceClient.java,v $
 * Revision 1.15  2003/06/24 12:06:49  maj0r
 * log4j eingefügt (inkl. Bedienung über Einstellungsdialog).
 *
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
  private static Logger logger;

  public static void main(String[] args) {
    Logger rootLogger = Logger.getRootLogger();
    logger = Logger.getLogger(AppleJuiceClient.class.getName());

    String datum = new SimpleDateFormat("ddMMyyyy_hhmmss").format(new Date(
        System.currentTimeMillis()));
    String dateiName;
    dateiName = datum + ".html";
    HTMLLayout layout = new HTMLLayout();
    layout.setTitle("appleJuice-Core-GUI-Log " + datum);
    layout.setLocationInfo(true);

    try {
      String path = System.getProperty("user.dir") + File.separator +
          "logs";
      File aFile = new File(path);
      if (!aFile.exists()) {
        aFile.mkdir();
      }
      FileAppender fileAppender = new FileAppender(layout,
          path + File.separator + dateiName);
      rootLogger.addAppender(fileAppender);
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    rootLogger.setLevel(OptionsManager.getInstance().getLogLevel());

    try {
      String nachricht = "appleJuice-Core-GUI wird gestartet...";
      if (logger.isEnabledFor(Level.INFO))
        logger.info(nachricht);
      System.out.println(nachricht);

      if (!DataManager.istCoreErreichbar()) {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"mainform", "caption"}));
        nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(new String[] {"javagui", "startup",
                                      "verbindungsfehler"}));
        nachricht = nachricht.replaceFirst("%s",
                                           OptionsManager.getInstance().
                                           getRemoteSettings().
                                           getHost());
        JOptionPane.showMessageDialog(new Frame(), nachricht, titel,
                                      JOptionPane.OK_OPTION);
        logger.fatal(nachricht);
        System.out.println("Fehler: " + nachricht);
        System.exit( -1);
      }
      AppleJuiceDialog theApp = new AppleJuiceDialog();
      Dimension appDimension = theApp.getSize();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      theApp.setLocation( (screenSize.width - appDimension.width) / 2,
                         (screenSize.height - appDimension.height) / 2);
      theApp.show();
      nachricht = "appleJuice-Core-GUI läuft...";
      if (logger.isEnabledFor(Level.INFO))
        logger.info(nachricht);
      System.out.println(nachricht);
    }
    catch (Exception e) {
      if (logger.isEnabledFor(Level.INFO))
        logger.fatal("Programmabbruch", e);
      System.exit( -1);
    }
  }
}