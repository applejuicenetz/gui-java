package de.applejuicenet.client;

import java.awt.*;

import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.shared.HtmlLoader;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import javax.swing.JOptionPane;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import de.applejuicenet.client.gui.controller.WebXMPParser;

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
    if (OptionsManager.getInstance().getRemoteSettings().isRemoteUsed()) {

      try {
        //dummy
        System.out.println(HtmlLoader.getHtmlContent("localhost", HtmlLoader.GET, "/xml/information.xml"));
      }
      catch (WebSiteNotFoundException ex) {
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
    }

    AppleJuiceDialog theApp = new AppleJuiceDialog();
    Dimension appDimension = theApp.getSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    theApp.setLocation( (screenSize.width - appDimension.width) / 2,
                       (screenSize.height - appDimension.height) / 2);
    theApp.show();
  }
}