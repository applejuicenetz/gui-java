package de.applejuicenet.client.gui;

import java.net.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import de.applejuicenet.client.gui.controller.*;
import java.io.File;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.exception.LanguageSelectorNotInstanciatedException;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class AppleJuiceDialog
    extends JFrame implements LanguageListener{
  RegisterPanel registerPane;
  JLabel[] statusbar = new JLabel[5];
  private JMenu sprachMenu;
  private JMenu optionenMenu;

  public AppleJuiceDialog() {
    super();
    try {
      jbInit();
      pack();
      LanguageSelector.getInstance().addLanguageListener(this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setTitle("AppleJuice Client");
    URL url = getClass().getResource("applejuice.gif");
    Image img = Toolkit.getDefaultToolkit().getImage(url);
    setIconImage(img);
    setJMenuBar(createMenuBar());
    registerPane = new RegisterPanel();
    addWindowListener(
        new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        closeDialog(evt);
      }
    });
    this.getContentPane().setLayout(new BorderLayout());
    getContentPane().add(registerPane, BorderLayout.CENTER);

    JPanel panel = new JPanel(new GridBagLayout());

    for (int i = 0; i < statusbar.length; i++) {
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
    constraints.weightx = 1;
    panel.add(statusbar[1], constraints);
    constraints.weightx = 0;
    constraints.gridx = 2;
    panel.add(statusbar[2], constraints);
    constraints.gridx = 3;
    panel.add(statusbar[3], constraints);
    constraints.gridx = 4;
    panel.add(statusbar[4], constraints);
    getContentPane().add(panel, BorderLayout.SOUTH);
  }

  public Dimension getPreferredSize() {
    Dimension systemDimension = getToolkit().getScreenSize();
    return new Dimension( (int) systemDimension.getWidth() / 4 * 3,
                         (int) systemDimension.getHeight() / 4 * 3);
  }

  private void closeDialog(WindowEvent evt) {
    setVisible(false);
    System.exit(1);
  }

  protected JMenuBar createMenuBar() {
    String path = System.getProperty("user.dir") + File.separator + "language" + File.separator;
    File languagePath = new File(path);
    String[] sprachDateien = languagePath.list();

    JMenuBar menuBar = new JMenuBar();
    optionenMenu = new JMenu("Optionen");
    menuBar.add(optionenMenu);
    JMenuItem menuItem;


    sprachMenu = new JMenu("Sprache");

    menuBar.add(sprachMenu);

    ButtonGroup lafGroup = new ButtonGroup();

    for (int i = 0; i < sprachDateien.length; i++) {
      String sprachText = LanguageSelector.getInstance(path + sprachDateien[i]).getFirstAttrbuteByTagName("Languageinfo", "name");
      JRadioButtonMenuItem rb = new JRadioButtonMenuItem(sprachText);
      sprachMenu.add(rb);
      rb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent ae) {
          JRadioButtonMenuItem rb2 = (JRadioButtonMenuItem)ae.
                                     getSource();
          if(rb2.isSelected()) {
            String path = System.getProperty("user.dir") + File.separator + "language" + File.separator;
            String dateiName = path + rb2.getText() + ".xml";
            LanguageSelector.getInstance(dateiName);
          }
        }
      });
      lafGroup.add(rb);
    }
    return menuBar;
  }

  public void fireLanguageChanged() {
    try {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      sprachMenu.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName("einstform", "languagesheet", "caption")));
      optionenMenu.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
          getFirstAttrbuteByTagName("einstform", "caption")));
    }
    catch (LanguageSelectorNotInstanciatedException ex) {
      ex.printStackTrace();
    }
  }
}