package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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

public class ODRemotePanel
    extends JPanel {
  private boolean dirty = false;
  private JLabel label1;
  private JLabel label2;
  private JLabel label3;
  private JTextField host = new JTextField();
  private JPasswordField passwortAlt = new JPasswordField();
  private JPasswordField passwortNeu = new JPasswordField();
  private JCheckBox verwenden = new JCheckBox();
  private RemoteConfiguration remote;

  public ODRemotePanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel panel1 = new JPanel(new GridBagLayout());
    FlowLayout flowL = new FlowLayout();
    flowL.setAlignment(FlowLayout.RIGHT);
    JPanel panel2 = new JPanel(flowL);

    LanguageSelector languageSelector = LanguageSelector.getInstance();
    remote = OptionsManager.getInstance().getRemoteSettings();

    label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "remote",
                                  "host"})));
    label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "remote",
                                  "passwortAlt"})));
    label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "remote",
                                  "passwortNeu"})));
    verwenden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "remote",
                                  "verwenden"})));

    host.setText(remote.getHost());
    host.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        if (remote.getHost().compareTo(host.getText()) != 0) {
          dirty = true;
          remote.setHost(host.getText());
        }
      }
    });
    passwortAlt.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        dirty = true;
        remote.setOldPassword(passwortAlt.getText());
      }
    });
    passwortNeu.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        dirty = true;
        remote.setNewPassword(passwortNeu.getText());
      }
    });

    panel2.add(verwenden);

    verwenden.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        remote.useRemote(verwenden.isSelected());
        enableControls(verwenden.isSelected());
        dirty = true;
      }
    });

    verwenden.setSelected(remote.isRemoteUsed());
    enableControls(remote.isRemoteUsed());

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.insets.top = 5;
    constraints.insets.left = 5;

    panel1.add(label1, constraints);

    constraints.gridy = 1;
    panel1.add(label2, constraints);

    constraints.gridy = 2;
    panel1.add(label3, constraints);

    constraints.insets.right = 5;
    constraints.gridy = 0;
    constraints.gridx = 1;
    constraints.weightx = 1;
    panel1.add(host, constraints);

    constraints.gridy = 1;
    panel1.add(passwortAlt, constraints);

    constraints.gridy = 2;
    panel1.add(passwortNeu, constraints);

    constraints.gridy = 3;
    constraints.gridx = 0;
    constraints.gridwidth = 2;
    panel1.add(panel2, constraints);

    add(panel1, BorderLayout.NORTH);
  }

  public RemoteConfiguration getRemoteConfiguration() {
    return remote;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void enableControls(boolean enable) {
    host.setEnabled(enable);
    passwortAlt.setEnabled(enable);
    passwortNeu.setEnabled(enable);
    label1.setEnabled(enable);
    label2.setEnabled(enable);
    label3.setEnabled(enable);
  }
}