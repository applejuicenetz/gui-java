package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;
import de.applejuicenet.client.shared.IconManager;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class StartPanel extends JPanel implements RegisterI{

  public StartPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    setLayout(new BorderLayout());

    JPanel panel3 = new JPanel(new GridBagLayout());
    panel3.setBackground(Color.WHITE);
    JPanel panel4 = new JPanel(new BorderLayout());
    panel4.setBackground(Color.WHITE);

    JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel1.setBackground(Color.WHITE);
    JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel1.setBackground(Color.WHITE);

    IconManager im = IconManager.getInstance();
    ImageIcon icon1 = im.getIcon("applejuicebanner");
    JLabel label1 = new JLabel(icon1);
    panel1.add(label1);


    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;

    ImageIcon icon2 = im.getIcon("start");
    JLabel label2 = new JLabel(icon2);
    panel3.add(label2, constraints);

    constraints.gridx = 1;
    constraints.weightx = 1;
    JLabel deinClient = new JLabel("<html><font><u>mehr Server gibt es hier</u></font></html>");
    panel3.add(deinClient, constraints);

    add(panel1, BorderLayout.NORTH);
    panel4.add(panel3, BorderLayout.NORTH);
    add(panel4, BorderLayout.CENTER);
  }

  public void registerSelected(){
  }
}