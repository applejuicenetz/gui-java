package de.applejuicenet.client.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.applejuicenet.client.gui.controller.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.gui.tables.download.DownloadMainNode;
import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.shared.dac.DownloadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/PowerDownloadPanel.java,v 1.23 2003/10/13 19:13:21 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fï¿½r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PowerDownloadPanel.java,v $
 * Revision 1.23  2003/10/13 19:13:21  maj0r
 * Wert fuer Powerdownload kann nun auch manuell eingetragen werden.
 *
 * Revision 1.22  2003/09/02 16:08:12  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.21  2003/09/01 15:50:51  maj0r
 * Wo es moeglich war, DOs auf primitive Datentypen umgebaut.
 *
 * Revision 1.20  2003/08/24 14:59:59  maj0r
 * Version 0.14
 * Diverse Aenderungen.
 *
 * Revision 1.19  2003/08/15 14:46:30  maj0r
 * Refactoring.
 *
 * Revision 1.18  2003/08/12 16:22:51  maj0r
 * Kleine Farbaenderung.
 *
 * Revision 1.17  2003/08/09 10:56:25  maj0r
 * DownloadTabelle weitergeführt.
 *
 * Revision 1.16  2003/08/05 20:47:06  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.15  2003/08/05 05:11:59  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.14  2003/06/30 19:46:11  maj0r
 * Sourcestil verbessert.
 *
 * Revision 1.13  2003/06/10 12:31:03  maj0r
 * Historie eingefï¿½gt.
 *
 *
 */

public class PowerDownloadPanel
    extends JPanel
    implements LanguageListener {
  private final Color BLUE_BACKGROUND = new Color(118, 112, 148);
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JRadioButton btnInaktiv = new JRadioButton();
  private JRadioButton btnAktiv = new JRadioButton();
  private JRadioButton btnAutoInaktiv = new JRadioButton();
  private JRadioButton btnAutoAktiv = new JRadioButton();
  private JLabel btnHint;
  private JLabel btnHint2;
  private JLabel btnPdlUp;
  private JLabel btnPdlDown;
  private float ratioWert = 2.2f;
  private JTextField ratio = new JTextField("2.2");
  private JTextField autoAb = new JTextField();
  private JTextField autoBis = new JTextField();
  public JButton btnPdl = new JButton("ï¿½bernehmen");
  private JButton btnAutoPdl = new JButton("ï¿½bernehmen");
  private JLabel powerdownload = new JLabel("Powerdownload");
  private JLabel label6 = new JLabel(
      "Wieviel willst Du maximal fï¿½r 1 Byte bezahlen?");
  private JLabel label7 = new JLabel("Fï¿½r 1 Byte zahle");
  private JLabel label8 = new JLabel("Credits");
  private JLabel label9 = new JLabel("Automatischer Powerdownload");
  private JLabel label10 = new JLabel("ab ");
  private JLabel label11 = new JLabel("bis ");

  private DownloadPanel parentPanel;

  public PowerDownloadPanel(DownloadPanel parentPanel) {
    this.parentPanel = parentPanel;
    btnPdl.setEnabled(false);
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    //todo
    btnAutoPdl.setEnabled(false);

    setLayout(new BorderLayout());
    LanguageSelector.getInstance().addLanguageListener(this);
    JPanel backPanel = new JPanel();
    backPanel.setLayout(gridBagLayout1);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridheight = 1;
    constraints.gridwidth = 3;
    JPanel tempPanel = new JPanel();
    tempPanel.setLayout(new BorderLayout());
    powerdownload.setForeground(Color.white);
    powerdownload.setOpaque(true);
    powerdownload.setBackground(BLUE_BACKGROUND);
    ratio.setBackground(Color.white);
    ratio.setMinimumSize(new Dimension(50, 21));
    ratio.setPreferredSize(new Dimension(50, 21));
    ratio.setHorizontalAlignment(SwingConstants.RIGHT);
    btnPdl.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnPdl_actionPerformed(e);
      }
    });
    tempPanel.add(powerdownload, BorderLayout.CENTER);

    IconManager im = IconManager.getInstance();
    ImageIcon icon = im.getIcon("hint");

    btnHint = new JLabel(icon) {
      public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
      }
    };
    btnHint.setOpaque(true);
    btnHint.setBackground(BLUE_BACKGROUND);
    tempPanel.add(btnHint, BorderLayout.EAST);
    backPanel.add(tempPanel, constraints);
    constraints.gridy = 1;
    backPanel.add(label6, constraints);
    constraints.gridwidth = 1;
    constraints.gridy = 2;
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(btnInaktiv);
    buttonGroup.add(btnAktiv);
    btnInaktiv.setText("Powerdownload inaktiv");
    btnInaktiv.setSelected(true);
    btnAktiv.setText("Powerdownload aktiv");
    backPanel.add(btnInaktiv, constraints);
    constraints.gridy = 3;
    backPanel.add(btnAktiv, constraints);

    JPanel tempPanel3 = new JPanel(new GridBagLayout());
    GridBagConstraints constraints2 = new GridBagConstraints();
    constraints2.anchor = GridBagConstraints.NORTH;
    constraints2.fill = GridBagConstraints.BOTH;
    constraints2.gridx = 0;
    constraints2.gridy = 0;

    tempPanel3.add(label7, constraints2);
    ImageIcon icon2 = im.getIcon("increase");
    btnPdlUp = new JLabel(icon2);
    ImageIcon icon3 = im.getIcon("decrease");
    btnPdlDown = new JLabel(icon3);

    btnPdlUp.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        btnAktiv.setSelected(true);
        alterRatio(true);
      }
    });
    btnPdlDown.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        btnAktiv.setSelected(true);
        alterRatio(false);
      }
    });
    ratio.addFocusListener(new FocusAdapter(){
        public void focusLost(FocusEvent fe){
            try{
                String temp = ratio.getText();
                temp = temp.replaceAll(",", ".");
                int pos = temp.lastIndexOf('.');
                if (pos!=-1){
                    temp = temp.substring(0, pos+2);
                }
                double pwdl = new Double(temp).doubleValue();
                if (pwdl<2.2 || pwdl>50){
                    ratio.setText("2.2");
                }
                else{
                    ratio.setText(temp);
                }
                btnAktiv.setSelected(true);
            }
            catch (Exception ex){
                ratio.setText("2.2");
            }
        }
    });
    constraints2.gridx = 1;
    tempPanel3.add(btnPdlUp, constraints2);
    constraints2.gridx = 2;
    tempPanel3.add(ratio, constraints2);
    constraints2.gridx = 3;
    tempPanel3.add(btnPdlDown, constraints2);
    constraints2.gridx = 4;
    constraints2.insets.left = 5;
    tempPanel3.add(label8, constraints2);

    constraints.gridy = 4;
    backPanel.add(tempPanel3, constraints);
    constraints.gridy = 5;
    constraints.gridwidth = 3;
    backPanel.add(btnPdl, constraints);

    constraints.gridy = 6;
    backPanel.add(new JLabel(" "), constraints);
    constraints.gridx = 0;
    constraints.gridy = 7;
    constraints.gridheight = 1;
    constraints.gridwidth = 3;
    JPanel tempPanel2 = new JPanel();
    tempPanel2.setLayout(new BorderLayout());
    label9.setForeground(Color.white);
    label9.setOpaque(true);
    label9.setBackground(BLUE_BACKGROUND);
    tempPanel2.add(label9, BorderLayout.CENTER);
    btnHint2 = new JLabel(icon) {
      public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
      }
    };
    btnHint2.setOpaque(true);
    btnHint2.setBackground(BLUE_BACKGROUND);

    tempPanel2.add(btnHint2, BorderLayout.EAST);
    backPanel.add(tempPanel2, constraints);

    constraints.gridwidth = 1;
    constraints.gridy = 8;
    ButtonGroup buttonGroup2 = new ButtonGroup();
    buttonGroup2.add(btnAutoInaktiv);
    buttonGroup2.add(btnAutoAktiv);
    btnAutoInaktiv.setText("inaktiv");
    btnAutoInaktiv.setSelected(true);
    btnAutoAktiv.setText("aktiv");
    backPanel.add(btnAutoInaktiv, constraints);
    constraints.gridy = 9;
    backPanel.add(btnAutoAktiv, constraints);
    constraints.gridy = 10;
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(label10);
    autoAb.setDocument(new NumberInputVerifier());
    autoAb.setPreferredSize(new Dimension(40, 21));
    autoAb.setText("200");
    autoBis.setDocument(new NumberInputVerifier());
    autoBis.setPreferredSize(new Dimension(40, 21));
    autoBis.setText("50");
    panel.add(autoAb);
    panel.add(new JLabel("MB "));
    panel.add(label11);
    panel.add(autoBis);
    panel.add(new JLabel("MB "));
    backPanel.add(panel, constraints);
    constraints.gridy = 11;
    constraints.gridwidth = 3;
    backPanel.add(btnAutoPdl, constraints);
    add(backPanel, BorderLayout.NORTH);
  }

  private void alterRatio(boolean increase) {
    String temp = ratio.getText();
    int pos = temp.indexOf('.');
    int ganzZahl;
    int nachKomma;
    if (pos == -1){
        ganzZahl = Integer.parseInt(temp);
        nachKomma = 0;
    }
    else{
        ganzZahl = Integer.parseInt(temp.substring(0, pos));
        nachKomma = Integer.parseInt(temp.substring(pos+1));
    }
    if (increase) {
      if (ratioWert < 50f) {
        if (nachKomma == 9) {
          nachKomma = 0;
          ganzZahl += 1;
        }
        else {
          nachKomma += 1;
        }
      }
      else {
        return;
      }
    }
    else {
      if (ratioWert > 2.2f) {
        if (nachKomma == 0) {
          nachKomma = 9;
          ganzZahl -= 1;
        }
        else {
          nachKomma -= 1;
        }
      }
      else {
        return;
      }
    }
    ratio.setText(ganzZahl + "." + nachKomma);
    ratioWert = Float.parseFloat(ratio.getText());
  }

  void btnPdl_actionPerformed(ActionEvent e) {
      Object[] selectedItems = parentPanel.getSelectedDownloadItems();
      if (selectedItems!=null && selectedItems.length!=0){
          for (int i=0; i<selectedItems.length; i++){
              if (selectedItems[i].getClass() == DownloadMainNode.class){
                  int powerDownload = 0;
                  if (!btnInaktiv.isSelected()){
                      String temp = ratio.getText();
                      double power = Double.parseDouble(temp);
                      powerDownload = (int) (power * 10 - 10);
                  }
                  ApplejuiceFassade.getInstance().setPowerDownload(((DownloadMainNode)selectedItems[i]).getDownloadDO().getId(), powerDownload);
              }
          }
      }
  }

  public void fireLanguageChanged() {
    LanguageSelector languageSelector = LanguageSelector.getInstance();
    powerdownload.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "powerdownload",
                                  "caption"})));
    label6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label6", "caption"})));
    btnInaktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "powerinactive",
                                  "caption"})));
    btnAktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "poweractive",
                                  "caption"})));
    label7.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label7", "caption"})));
    label8.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"mainform", "Label8", "caption"})));
    label9.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "downloadtab",
                                  "label1"})));
    btnAutoInaktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "downloadtab",
                                  "rbInaktiv"})));
    btnAutoAktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "downloadtab",
                                  "rbAktiv"})));
    label10.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "downloadtab",
                                  "pdlAb"})));
    label11.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "downloadtab",
                                  "pdlBis"})));
    String ok = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                                                  getFirstAttrbuteByTagName(new
        String[] {"javagui", "downloadtab", "btnOK"}));
    btnAutoPdl.setText(ok);
    btnPdl.setText(ok);
    btnHint.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "tooltipps",
                                  "powerdownload"})));
    btnHint2.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
        getFirstAttrbuteByTagName(new String[] {"javagui", "tooltipps",
                                  "autopowerdownload"})));
  }
}
