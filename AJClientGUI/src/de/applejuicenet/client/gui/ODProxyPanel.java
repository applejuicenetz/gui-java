package de.applejuicenet.client.gui;

import javax.swing.*;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import java.awt.*;
import javax.swing.event.*;
import de.applejuicenet.client.shared.NumberInputVerifier;
import java.awt.event.*;
import de.applejuicenet.client.shared.ProxyConfiguration;
import de.applejuicenet.client.gui.controller.OptionsManager;


/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ODProxyPanel extends JPanel {
  private JLabel label1;
  private JLabel label2;
  private JLabel label3;
  private JLabel label4;
  private JCheckBox verwenden;
  private JTextField ip = new JTextField();
  private JTextField port = new JTextField();
  private JTextField user = new JTextField();
  private JPasswordField passwort = new JPasswordField();
  private ProxyConfiguration proxy;

  public ODProxyPanel() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public ProxyConfiguration getProxy(){
    return proxy;
  }

  private void jbInit() throws Exception {
    setLayout(new BorderLayout());
    JPanel panel1 = new JPanel(new GridBagLayout());
    FlowLayout flowL = new FlowLayout();
    flowL.setAlignment(FlowLayout.RIGHT);
    JPanel panel2 = new JPanel(flowL);

    LanguageSelector languageSelector = LanguageSelector.getInstance();

    label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "proxy", "benutzername"})));
    label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "proxy", "passwort"})));
    label3 = new JLabel("IP");
    label4 = new JLabel("Port");
    verwenden = new JCheckBox();
    verwenden.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "options", "proxy", "verwenden"})));
    verwenden.addChangeListener(new ChangeListener(){
      public void stateChanged(ChangeEvent e){
        JCheckBox checkbox = (JCheckBox)e.getSource();
        enableControls(checkbox.isSelected());
        proxy.useProxy(checkbox.isSelected());
      }
    });
    port.setDocument(new NumberInputVerifier());

    proxy = OptionsManager.getInstance().getProxySettings();

    ip.setText(proxy.getIP());
    port.setText(Integer.toString(proxy.getPort()));
    user.setText(proxy.getUsername());
    passwort.setText(proxy.getPassword());


    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.insets.top = 5;
    constraints.insets.left = 5;

    ip.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        proxy.setIP(ip.getText());
      }
    });

    port.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        proxy.setPort(Integer.parseInt(port.getText()));
      }
    });

    user.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        proxy.setUsername(user.getText());
      }
    });

    passwort.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        proxy.setPassword(passwort.getText());
      }
    });

    panel1.add(label3, constraints);

    constraints.gridy = 1;
    panel1.add(label4, constraints);

    constraints.gridy = 2;
    panel1.add(label1, constraints);

    constraints.gridy = 3;
    panel1.add(label2, constraints);

    constraints.gridy = 0;
    constraints.gridx = 1;
    constraints.weightx = 1;
    constraints.insets.right = 5;

    panel1.add(ip, constraints);

    constraints.gridy = 1;
    panel1.add(port, constraints);

    constraints.gridy = 2;
    panel1.add(user, constraints);

    constraints.gridy = 3;
    panel1.add(passwort, constraints);

    panel2.add(verwenden);

    constraints.gridy = 4;
    constraints.gridx = 0;
    constraints.weightx = 0;
    constraints.gridwidth = 2;
    panel1.add(panel2, constraints);

    add(panel1, BorderLayout.NORTH);

    verwenden.setSelected(proxy.isProxyUsed());
    enableControls(proxy.isProxyUsed());
  }

  public void enableControls(boolean enable){
    ip.setEnabled(enable);
    port.setEnabled(enable);
    user.setEnabled(enable);
    passwort.setEnabled(enable);
    label1.setEnabled(enable);
    label2.setEnabled(enable);
    label3.setEnabled(enable);
    label4.setEnabled(enable);
  }
}