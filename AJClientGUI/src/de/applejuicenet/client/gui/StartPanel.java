package de.applejuicenet.client.gui;

import java.awt.*;
import javax.swing.*;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.gui.controller.DataManager;
import de.applejuicenet.client.shared.NetworkInfo;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class StartPanel extends JPanel implements LanguageListener, RegisterI{
  private static final Color APFEL_GRUEN = new Color(34, 146, 14);

  private JLabel warnungen;
  private JLabel deinClient;
  private JLabel label7;
  private JLabel nachrichten;
  private JLabel label8;
  private JLabel netzwerk;
  private JLabel label6;
  private JLabel label9;

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
    constraints.insets.left = 5;

    ImageIcon icon2 = im.getIcon("start");
    JLabel label2 = new JLabel(icon2);
    panel3.add(label2, constraints);

    constraints.gridx = 1;
    constraints.weightx = 1;
    deinClient = new JLabel("<html><font><h2>Dein Core</h2></font></html>");
    deinClient.setForeground(APFEL_GRUEN);
    panel3.add(deinClient, constraints);
    constraints.weightx = 0;

    constraints.gridy = 1;
    constraints.insets.left = 15;
    panel3.add(new JLabel("Version " + DataManager.getInstance().getCoreVersion().getVersion()), constraints);

    constraints.gridy = 2;
    constraints.insets.left = 5;
    constraints.gridx = 0;
    ImageIcon icon3 = im.getIcon("warnung");
    JLabel label3 = new JLabel(icon3);
    panel3.add(label3, constraints);

    NetworkInfo netInfo = DataManager.getInstance().getNetworkInfo();

    constraints.gridx = 1;
    warnungen = new JLabel("<html><font><h2>Warnungen</h2></font></html>");
    warnungen.setForeground(APFEL_GRUEN);
    panel3.add(warnungen, constraints);

    constraints.gridy = 3;
    constraints.insets.left = 15;
    label7 = new JLabel();
    if (netInfo.isFirewalled()){
      label7.setForeground(Color.RED);
      label7.setText("Es ist möglich, dass Du hinter einer Firewall, einem Router, Proxy oder ähnlichem sitzt. Dies vermidert die Chance was zu laden.");
    }
    else{
      label7.setForeground(Color.BLACK);
      label7.setText("Alles klar.");
    }

    panel3.add(label7, constraints);

    constraints.gridy = 4;
    constraints.insets.left = 5;
    constraints.gridx = 0;
    ImageIcon icon4 = im.getIcon("netzwerk");
    JLabel label4 = new JLabel(icon4);
    panel3.add(label4, constraints);


    constraints.gridx = 1;
    nachrichten = new JLabel("<html><font><h2>Netzwerk, Neuigkeiten und Nachrichten</h2></font></html>");
    nachrichten.setForeground(APFEL_GRUEN);
    panel3.add(nachrichten, constraints);

    constraints.gridy = 5;
    constraints.insets.left = 15;
    label8 = new JLabel();
    panel3.add(label8, constraints);

    constraints.gridy = 6;
    constraints.insets.left = 5;
    constraints.gridx = 0;
    ImageIcon icon5 = im.getIcon("server");
    JLabel label5 = new JLabel(icon5);
    panel3.add(label5, constraints);

    constraints.gridx = 1;
    netzwerk = new JLabel("<html><font><h2>appleJuice Netzwerk</h2></font></html>");
    netzwerk.setForeground(APFEL_GRUEN);
    panel3.add(netzwerk, constraints);

    constraints.gridy = 7;
    constraints.insets.left = 15;
    label9 = new JLabel("Du bist mit xxxx vielleicht verbunden.");
    panel3.add(label9, constraints);

    constraints.gridy = 8;
    constraints.insets.top = 5;
    label6 = new JLabel();
    label6.setText(netInfo.getAJUserGesamtAsString() + " Benutzer haben " + netInfo.getAJAnzahlDateienAsString() + " Dateien ( " + netInfo.getAJGesamtShare(0) + " )" );
    panel3.add(label6, constraints);

    constraints.insets.top = 0;

    add(panel1, BorderLayout.NORTH);
    panel4.add(panel3, BorderLayout.NORTH);
    add(panel4, BorderLayout.CENTER);
    LanguageSelector.getInstance().addLanguageListener(this);
  }

  public void registerSelected(){
    updateContent();
  }

  private void updateContent(){
    fireLanguageChanged();  //ein bischen missbraucht, aber schwachsinnig dies doppelt zu implementieren
  }

  public void fireLanguageChanged(){
    NetworkInfo netInfo = DataManager.getInstance().getNetworkInfo();
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    netzwerk.setText("<html><font><h2>" + ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "html7"})) + "</h2></font></html>");
    nachrichten.setText("<html><font><h2>" + ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "html13"})) + "</h2></font></html>");
    deinClient.setText("<html><font><h2>" + ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "html1"})) + "</h2></font></html>");
    if (netInfo.isFirewalled()){
      label7.setForeground(Color.RED);
      label7.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "firewallwarning", "caption"})));
    }
    else
      label7.setText("");
    String temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "html10"}));
    temp = temp.replaceFirst("%s", "<Kein Server>");
    temp = temp.replaceFirst("%d", Integer.toString(DataManager.getInstance().getAllServer().size()));
    temp = temp.replaceAll("%s", "-");
    label9.setText(temp);
    temp = ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "status", "status2"}));
    temp = temp.replaceFirst("%d", netInfo.getAJUserGesamtAsStringWithPoints());
    temp = temp.replaceFirst("%d", netInfo.getAJAnzahlDateienAsStringWithPoints());
    temp = temp.replaceFirst("%s", netInfo.getAJGesamtShareWithPoints(0));
    label6.setText(temp);
    warnungen.setText("<html><font><h2>" + ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(new String[] {"mainform", "html15"})) + "</h2></font></html>");
  }
}