package de.applejuicenet.client;

import java.awt.*;
import javax.swing.*;

import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.shared.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class AppleJuiceClient {
  public static void main(String[] args) {
    if (!DataManager.istCoreErreichbar()) {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String titel = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName(new String[] {"mainform", "caption"}));
      String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName(new String[] {"javagui", "startup",
                                    "verbindungsfehler"}));
      JOptionPane.showMessageDialog(new Frame(), nachricht, titel,
                                    JOptionPane.OK_OPTION);
      System.exit( -1);
    }
    AppleJuiceDialog theApp = new AppleJuiceDialog();
    Dimension appDimension = theApp.getSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    theApp.setLocation( (screenSize.width - appDimension.width) / 2,
                       (screenSize.height - appDimension.height) / 2);
    theApp.show();
  }
}