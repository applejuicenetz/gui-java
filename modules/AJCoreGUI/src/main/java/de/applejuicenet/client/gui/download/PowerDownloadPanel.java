/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownload;
import de.applejuicenet.client.gui.powerdownload.EinstellungenDialog;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.NumberInputVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/PowerDownloadPanel.java,v 1.20 2009/01/26 13:31:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class PowerDownloadPanel extends JPanel implements LanguageListener, DataUpdateListener {
    private final Color BLUE_BACKGROUND = new Color(118, 112, 148);
    public JRadioButton btnInaktiv = new JRadioButton();
    public JRadioButton btnAktiv = new JRadioButton();
    private JRadioButton btnAutoInaktiv = new JRadioButton();
    private JRadioButton btnAutoAktiv = new JRadioButton();
    private JLabel btnHint;
    private JLabel btnHint2;
    private JLabel btnPdlUp;
    private JLabel btnPdlDown;
    private float ratioWert = 2.2f;
    public JTextField ratio = new JTextField("2.2");
    private JTextField autoAb = new JTextField();
    private JTextField autoBis = new JTextField();
    private JButton btnPdl = new JButton();
    private JButton btnAutoPdl = new JButton();
    private JLabel powerdownload = new JLabel();
    private JLabel label6 = new JLabel();
    private JLabel label7 = new JLabel();
    private JLabel label8 = new JLabel();
    private JLabel label9 = new JLabel();
    private JLabel label10 = new JLabel();
    private JLabel label11 = new JLabel();
    private Logger logger;
    private RatioFocusAdapter ratioFocusAdapter;
    private EinstellungenDialog autoPwdlDialog;
    private JButton autoPwdlEinstellungen = new JButton();
    private int standardAutomaticPwdlAb = 200;
    private int standardAutomaticPwdlBis = 30;
    private DownloadController downloadController;
    private volatile AutomaticPowerdownload autoPwdlThread;
    private Information lastInformation;
    private JPanel backPanel = new JPanel();
    private JScrollPane scrollPane;

    public PowerDownloadPanel(DownloadController downloadController) {
        logger = LoggerFactory.getLogger(getClass());
        try {
            this.downloadController = downloadController;
            btnPdl.setEnabled(false);
            init();
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        LanguageSelector.getInstance().addLanguageListener(this);
        backPanel.setLayout(new GridBagLayout());

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
        ratioFocusAdapter = new RatioFocusAdapter();
        ratio.addFocusListener(ratioFocusAdapter);
        ratio.setColumns(4);
        ratio.setHorizontalAlignment(SwingConstants.RIGHT);
        KeyAdapter ratioKlicker = new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                switch (ke.getKeyCode()) {

                    case KeyEvent.VK_ENTER: {
                        ratioFocusAdapter.focusLost(null);
                        btnPdl.doClick();
                        break;
                    }

                    case KeyEvent.VK_UP: {
                        btnAktiv.setSelected(true);
                        alterRatio(true);
                        break;
                    }

                    case KeyEvent.VK_DOWN: {
                        btnAktiv.setSelected(true);
                        alterRatio(false);
                        break;
                    }

                    default:
                        break;
                }
            }
        };

        btnInaktiv.addKeyListener(ratioKlicker);
        btnAktiv.addKeyListener(ratioKlicker);
        ratio.addKeyListener(ratioKlicker);

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
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        constraints.gridy = 1;
        backPanel.add(label6, constraints);
        constraints.gridwidth = 1;
        constraints.gridy = 2;
        ButtonGroup buttonGroup = new ButtonGroup();

        buttonGroup.add(btnInaktiv);
        buttonGroup.add(btnAktiv);
        btnInaktiv.setSelected(true);
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
        constraints2.gridx = 1;
        tempPanel3.add(btnPdlDown, constraints2);
        constraints2.gridx = 2;
        tempPanel3.add(ratio, constraints2);
        constraints2.gridx = 3;
        tempPanel3.add(btnPdlUp, constraints2);
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
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        backPanel.add(tempPanel2, constraints);
        constraints.insets.left = 5;
        constraints.insets.right = 5;

        constraints.gridwidth = 1;
        constraints.gridy = 8;
        ButtonGroup buttonGroup2 = new ButtonGroup();

        buttonGroup2.add(btnAutoInaktiv);
        buttonGroup2.add(btnAutoAktiv);
        btnAutoInaktiv.setSelected(true);
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        JPanel panel2 = new JPanel(new GridBagLayout());

        constraints.gridy = 0;
        panel2.add(btnAutoInaktiv, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel2.add(new JLabel(), constraints);
        constraints.weightx = 0;
        constraints.gridx = 2;
        panel2.add(autoPwdlEinstellungen, constraints);
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        backPanel.add(panel2, constraints);
        constraints.gridy = 10;
        backPanel.add(btnAutoAktiv, constraints);
        constraints.gridy = 11;
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(label10);
        autoAb.setDocument(new NumberInputVerifier());
        autoAb.setColumns(3);
        autoAb.setText(Integer.toString(standardAutomaticPwdlAb));
        autoBis.setDocument(new NumberInputVerifier());
        autoBis.setColumns(3);
        autoBis.setText(Integer.toString(standardAutomaticPwdlBis));
        autoAb.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent fe) {
                int eingegeben = Integer.parseInt(autoAb.getText());

                if (eingegeben < Integer.parseInt(autoBis.getText())) {
                    autoAb.setText(Integer.toString(standardAutomaticPwdlAb));
                }
            }
        });
        autoBis.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent fe) {
                int eingegeben = Integer.parseInt(autoBis.getText());

                if (eingegeben < standardAutomaticPwdlBis || eingegeben > Integer.parseInt(autoAb.getText())) {
                    autoBis.setText(Integer.toString(standardAutomaticPwdlBis));
                }
            }
        });
        panel.add(autoAb);
        panel.add(new JLabel("MB "));
        panel.add(label11);
        panel.add(autoBis);
        panel.add(new JLabel("MB "));
        backPanel.add(panel, constraints);
        constraints.gridy = 12;
        constraints.gridwidth = 3;
        constraints.insets.bottom = 5;
        backPanel.add(btnAutoPdl, constraints);
        btnAutoPdl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                alterAutoPwdl();
            }
        });
        constraints.gridy = 13;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        backPanel.add(new JLabel(), constraints);

        scrollPane = new JScrollPane(backPanel);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        autoPwdlEinstellungen.setVisible(false);
        autoPwdlEinstellungen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                oeffneAutomPwdlEinstellungen();
            }
        });
        AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.INFORMATION_CHANGED);
        AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
    }

    public void setPwdlValue(int pwdlValue) {
        if (pwdlValue < 12) {
            ratio.setText("2.2");
            ratioWert = 2.2f;
            btnInaktiv.setSelected(true);
        } else {
            float wert = (float) ((float) (pwdlValue + 10) / 10);

            ratio.setText(Float.toString(wert));
            ratioWert = wert;
            btnAktiv.setSelected(true);
        }
    }

    private void alterAutoPwdl() {
        if (btnAutoAktiv.isSelected()) {
            if (autoPwdlThread == null && autoAb.isEnabled()) {
                if (!openAutoPwdlSettings()) {
                    btnAutoInaktiv.setSelected(true);
                    return;
                }
                autoAb.setEnabled(false);
                autoBis.setEnabled(false);
                btnPdl.setEnabled(false);
                startAutoPwdl();
                downloadController.updateDownloadPolling();
                AppleJuiceDialog.getApp().informAutomaticPwdlEnabled(true);
            }
        } else {
            if (!autoAb.isEnabled()) {
                autoAb.setEnabled(true);
                autoBis.setEnabled(true);
                if (autoPwdlThread != null) {
                    autoPwdlThread.interrupt();
                    autoPwdlThread = null;
                }
                downloadController.updateDownloadPolling();
            }
        }
    }

    public boolean isAutomaticPwdlActive() {
        return (!autoAb.isEnabled() && autoPwdlThread != null);
    }

    private void startAutoPwdl() {
        AutomaticPowerdownload pwdl = new AutomaticPowerdownload(AppleJuiceClient.getAjFassade());

        applyAutoPwdlSettings(pwdl);
        pwdl.setParentToInform(this);
        autoPwdlThread = pwdl;
        checkForPause(pwdl);
        pwdl.start();
        autoPwdlEinstellungen.setVisible(true);
    }

    private void checkForPause(AutomaticPowerdownload pwdlThread) {
        long eingegebenBis = Integer.parseInt(autoBis.getText()) * 1048576L;

        if (lastInformation == null) {
            lastInformation = AppleJuiceClient.getAjFassade().getInformation();
        }

        if (lastInformation.getCredits() < eingegebenBis) {
            pwdlThread.setPaused(true);
        }
    }

    /** Oeffnet den modalen Einstellungsdialog; false, wenn er nicht bestaetigt wurde. */
    private boolean openAutoPwdlSettings() {
        if (autoPwdlDialog == null) {
            autoPwdlDialog = new EinstellungenDialog(AppleJuiceDialog.getApp());
        }

        autoPwdlDialog.setVisible(true);
        return autoPwdlDialog.isConfirmed();
    }

    private void applyAutoPwdlSettings(AutomaticPowerdownload pwdl) {
        pwdl.applySettings(autoPwdlDialog.getAnzahlDownloads(), autoPwdlDialog.getPowerDownload(),
                autoPwdlDialog.getSleeptime(), autoPwdlDialog.getReihenfolge());
    }

    private void oeffneAutomPwdlEinstellungen() {
        AutomaticPowerdownload pwdl = autoPwdlThread;

        if (pwdl != null && openAutoPwdlSettings()) {
            applyAutoPwdlSettings(pwdl);
        }
    }

    public void autoPwdlFinished(AutomaticPowerdownload completedPwdl) {
        if (completedPwdl != null && autoPwdlThread == completedPwdl) {
            autoPwdlFinished();
        }
    }

    public void autoPwdlFinished() {
        autoPwdlEinstellungen.setVisible(false);
        autoPwdlThread = null;
        downloadController.updateDownloadPolling();
        btnAutoInaktiv.setSelected(true);
        btnAutoPdl.doClick();
        AppleJuiceDialog.getApp().informAutomaticPwdlEnabled(false);
    }

    public Dimension getPreferredSize() {
        Dimension preferred = super.getPreferredSize();
        if (scrollPane != null) {
            Insets border = scrollPane.getInsets();
            Insets outer = getInsets();
            int contentWidth = backPanel.getPreferredSize().width + border.left + border.right
                    + outer.left + outer.right + scrollPane.getVerticalScrollBar().getPreferredSize().width;
            preferred.width = Math.max(preferred.width, contentWidth);
        }
        return preferred;
    }

    public Dimension getMinimumSize() {
        Dimension minimum = super.getMinimumSize();
        minimum.width = getPreferredSize().width;
        return minimum;
    }

    private void alterRatio(boolean increase) {
        try {
            String temp = ratio.getText();
            int pos = temp.indexOf('.');
            int ganzZahl;
            int nachKomma;

            if (pos == -1) {
                ganzZahl = Integer.parseInt(temp);
                nachKomma = 0;
            } else {
                ganzZahl = Integer.parseInt(temp.substring(0, pos));
                nachKomma = Integer.parseInt(temp.substring(pos + 1));
            }

            if (increase) {
                if (ratioWert < 50f) {
                    if (nachKomma == 9) {
                        nachKomma = 0;
                        ganzZahl += 1;
                    } else {
                        nachKomma += 1;
                    }
                } else {
                    return;
                }
            } else {
                if (ratioWert > 2.2f) {
                    if (nachKomma == 0) {
                        nachKomma = 9;
                        ganzZahl -= 1;
                    } else {
                        nachKomma -= 1;
                    }
                } else {
                    return;
                }
            }

            ratio.setText(ganzZahl + "." + nachKomma);
            ratioWert = Float.parseFloat(ratio.getText());
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();

            powerdownload.setText(" " + languageSelector.getFirstAttrbuteByTagName("mainform.powerdownload.caption"));
            label6.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Label6.caption"));
            btnInaktiv.setText(languageSelector.getFirstAttrbuteByTagName("mainform.powerinactive.caption"));
            btnAktiv.setText(languageSelector.getFirstAttrbuteByTagName("mainform.poweractive.caption"));
            label7.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Label7.caption"));
            label8.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Label8.caption"));
            label9.setText(" " + languageSelector.getFirstAttrbuteByTagName("javagui.downloadtab.label1"));
            btnAutoInaktiv.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadtab.rbInaktiv"));
            btnAutoAktiv.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadtab.rbAktiv"));
            label10.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadtab.pdlAb"));
            label11.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadtab.pdlBis"));
            String ok = languageSelector.getFirstAttrbuteByTagName("javagui.downloadtab.btnOK");

            btnAutoPdl.setText(ok);
            btnPdl.setText(ok);
            btnHint.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.tooltipps.powerdownload"));
            btnHint2.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.tooltipps.autopowerdownload"));
            autoPwdlEinstellungen.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.einstellungen"));
        } catch (Exception ex) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        try {
            if (type == DATALISTENER_TYPE.INFORMATION_CHANGED) {
                lastInformation = (Information) content;
                if (autoPwdlThread != null && lastInformation != null) {
                    long eingegebenBis = Long.parseLong(autoBis.getText()) * 1048576L;
                    long eingegebenAb = Long.parseLong(autoAb.getText()) * 1048576L;

                    if (lastInformation.getCredits() > eingegebenAb) {
                        autoPwdlThread.setPaused(false);
                    } else if (lastInformation.getCredits() < eingegebenBis) {
                        autoPwdlThread.setPaused(true);
                    }
                }
            } else if (type == DATALISTENER_TYPE.DOWNLOAD_CHANGED && autoPwdlThread != null && autoPwdlThread.isPaused()) {
                HashMap<String, Download> downloads = (HashMap<String, Download>) content;

                synchronized (downloads) {
                    List<Download> toPause = new Vector<Download>();

                    for (Download curDownload : downloads.values()) {
                        if (curDownload.getStatus() == Download.SUCHEN_LADEN) {
                            toPause.add(curDownload);
                        }
                    }

                    AppleJuiceClient.getAjFassade().pauseDownload(toPause);
                }
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public JButton getBtnPdl() {
        return btnPdl;
    }

    private class RatioFocusAdapter extends FocusAdapter {
        public void focusLost(FocusEvent fe) {
            try {
                String temp = ratio.getText();

                temp = temp.replaceAll(",", ".");
                int pos = temp.lastIndexOf('.');

                if (pos != -1) {
                    temp = temp.substring(0, pos + 2);
                }

                double pwdl = Double.parseDouble(temp);

                if (pwdl < 2.2 || pwdl > 50) {
                    ratio.setText("2.2");
                } else {
                    ratio.setText(temp);
                }

                btnAktiv.setSelected(true);
            } catch (Exception ex) {
                ratio.setText("2.2");
            }
        }
    }
}
