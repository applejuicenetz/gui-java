package de.applejuice.client.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class AppleJuiceDialog extends JFrame {
  RegisterPanel registerPane = new RegisterPanel();

  public AppleJuiceDialog() {
    super();
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
    URL url = getClass().getResource("applejuice.gif");
    Image img = Toolkit.getDefaultToolkit().getImage(url);
    setIconImage(img);

    addWindowListener(
        new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                closeDialog(evt);
            }
        });
    this.getContentPane().setLayout(new BorderLayout());
    getContentPane().add(registerPane,  BorderLayout.CENTER);
    Dimension systemDimension = getToolkit().getScreenSize();
    setBounds(new Rectangle(0, 0, systemDimension.width/4*3, systemDimension.height/4*3));
    Dimension dialogDimension = getSize();
    setLocation((systemDimension.width - dialogDimension.width)/2,
                        (systemDimension.height - dialogDimension.height)/2);
    show();
  }

  private void closeDialog(WindowEvent evt) {
      setVisible(false);
      System.exit(1);
  }
}