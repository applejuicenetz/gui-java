package de.applejuicenet.client;

import java.awt.*;

import de.applejuicenet.client.gui.*;

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
    AppleJuiceDialog theApp = new AppleJuiceDialog();
    Dimension appDimension = theApp.getSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    theApp.setLocation((screenSize.width-appDimension.width)/2, (screenSize.height-appDimension.height)/2);
    theApp.show();
  }
}