package de.applejuice.client.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    addWindowListener(
        new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });
    this.getContentPane().setLayout(new BorderLayout());
    getContentPane().add(registerPane,  BorderLayout.CENTER);
    show();
  }

  private void closeDialog(WindowEvent evt) {
      setVisible(false);
      System.exit(1);
  }
}