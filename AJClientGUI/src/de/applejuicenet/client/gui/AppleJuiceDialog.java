package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.border.BevelBorder;
import de.applejuicenet.client.gui.controller.DataManager;

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
  JLabel[] statusbar = new JLabel[5];

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
    getContentPane().add(registerPane, BorderLayout.CENTER);

    JPanel panel = new JPanel(new GridBagLayout());

    for(int i=0; i<statusbar.length; i++){
      statusbar[i] = new JLabel();
      statusbar[i].setHorizontalAlignment(JLabel.RIGHT);
      statusbar[i].setBorder(new BevelBorder(BevelBorder.LOWERED));
      statusbar[i].setFont(new java.awt.Font("SansSerif", 0, 11));
    }
    DataManager.getInstance().addStatusbarForListen(statusbar);
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    panel.add(statusbar[0], constraints);
    constraints.gridx = 1;
    constraints.weightx=1;
    panel.add(statusbar[1], constraints);
    constraints.weightx=0;
    constraints.gridx = 2;
    panel.add(statusbar[2], constraints);
    constraints.gridx = 3;
    panel.add(statusbar[3], constraints);
    constraints.gridx = 4;
    panel.add(statusbar[4], constraints);
    getContentPane().add(panel, BorderLayout.SOUTH);
  }

  public Dimension getPreferredSize(){
    Dimension systemDimension = getToolkit().getScreenSize();
    return new Dimension((int)systemDimension.getWidth()/4*3, (int)systemDimension.getHeight()/4*3);
  }


  private void closeDialog(WindowEvent evt) {
      setVisible(false);
      System.exit(1);
  }
}