package de.applejuice.client.gui;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class AppleJuiceDialog extends JDialog {
  RegisterPanel registerPane = new RegisterPanel();

  public AppleJuiceDialog(Frame frame) {
    super(frame, true);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setTitle("AppleJuice Client");
    this.getContentPane().setLayout(new BorderLayout());
    getContentPane().add(registerPane,  BorderLayout.CENTER);
    show();
  }
}