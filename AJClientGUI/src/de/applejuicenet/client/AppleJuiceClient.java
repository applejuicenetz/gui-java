package de.applejuicenet.client;

import java.awt.*;

import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.exception.*;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.shared.HtmlLoader;

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
    String test;
    try {
      System.out.println(HtmlLoader.getHtmlContent(
          "http://localhost:9851/xml/modified.xml"));
    }
    catch (WebSiteNotFoundException ex) {
      int i=0;
    }
    AppleJuiceDialog theApp = new AppleJuiceDialog();
    Dimension appDimension = theApp.getSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    theApp.setLocation((screenSize.width-appDimension.width)/2, (screenSize.height-appDimension.height)/2);
    theApp.show();
  }
}