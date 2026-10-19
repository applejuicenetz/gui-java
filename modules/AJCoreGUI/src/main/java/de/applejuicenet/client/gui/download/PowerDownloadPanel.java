/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.shared.CoreCall;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.Information;
import de.applejuicenet.client.facade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownload;
import de.applejuicenet.client.gui.powerdownload.SettingsDialog;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.NumberInputVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/PowerDownloadPanel.java,v 1.20 2009/01/26 13:31:36 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 */
public class PowerDownloadPanel extends JPanel implements LanguageListener, DataUpdateListener {
    private final Color BLUE_BACKGROUND = new Color(118, 112, 148);
    public JRadioButton btnInactive = new JRadioButton();
    public JRadioButton btnActive = new JRadioButton();
    private JRadioButton btnAutoInactive = new JRadioButton();
    private JRadioButton btnAutoActive = new JRadioButton();
    private JLabel btnHint;
    private JLabel btnHint2;
    private JLabel btnPowerdownloadUp;
    private JLabel btnPowerdownloadDown;
    private float ratioValue = 2.2f;
    public JTextField ratio = new JTextField("2.2");
    private JTextField autoFrom = new JTextField();
    private JTextField autoTo = new JTextField();
    private JButton btnPowerdownload = new JButton();
    private JButton btnAutoPowerdownload = new JButton();
    private JLabel powerdownload = new JLabel();
    private JLabel label6 = new JLabel();
    private JLabel label7 = new JLabel();
    private JLabel label8 = new JLabel();
    private JLabel label9 = new JLabel();
    private JLabel label10 = new JLabel();
    private JLabel label11 = new JLabel();
    private Logger logger;
    private RatioFocusAdapter ratioFocusAdapter;
    private SettingsDialog autoPowerdownloadDialog;
    private JButton autoPowerdownloadSettings = new JButton();
    private int defaultAutomaticPowerdownloadFrom = 200;
    private int defaultAutomaticPowerdownloadTo = 30;
    private DownloadController downloadController;
    private volatile AutomaticPowerdownload autoPowerdownloadThread;
    private volatile Information lastInformation;
    /** Credit limits in bytes, set on the EDT when the automatic powerdownload starts. */
    private volatile long creditThresholdFrom = Long.MAX_VALUE;
    private volatile long creditThresholdTo = 0;
    private JPanel backPanel = new JPanel();
    private JScrollPane scrollPane;

    public PowerDownloadPanel(DownloadController downloadController) {
        logger = LoggerFactory.getLogger(getClass());
        try {
            this.downloadController = downloadController;
            btnPowerdownload.setEnabled(false);
            init();
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
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
        KeyAdapter ratioKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                switch (ke.getKeyCode()) {

                    case KeyEvent.VK_ENTER: {
                        ratioFocusAdapter.focusLost(null);
                        btnPowerdownload.doClick();
                        break;
                    }

                    case KeyEvent.VK_UP: {
                        btnActive.setSelected(true);
                        changeRatio(true);
                        break;
                    }

                    case KeyEvent.VK_DOWN: {
                        btnActive.setSelected(true);
                        changeRatio(false);
                        break;
                    }

                    default:
                        break;
                }
            }
        };

        btnInactive.addKeyListener(ratioKeyListener);
        btnActive.addKeyListener(ratioKeyListener);
        ratio.addKeyListener(ratioKeyListener);

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

        buttonGroup.add(btnInactive);
        buttonGroup.add(btnActive);
        btnInactive.setSelected(true);
        backPanel.add(btnInactive, constraints);
        constraints.gridy = 3;
        backPanel.add(btnActive, constraints);

        JPanel tempPanel3 = new JPanel(new GridBagLayout());
        GridBagConstraints constraints2 = new GridBagConstraints();

        constraints2.anchor = GridBagConstraints.NORTH;
        constraints2.fill = GridBagConstraints.BOTH;
        constraints2.gridx = 0;
        constraints2.gridy = 0;

        tempPanel3.add(label7, constraints2);
        ImageIcon icon2 = im.getIcon("increase");

        btnPowerdownloadUp = new JLabel(icon2);
        ImageIcon icon3 = im.getIcon("decrease");

        btnPowerdownloadDown = new JLabel(icon3);

        btnPowerdownloadUp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                btnActive.setSelected(true);
                changeRatio(true);
            }
        });
        btnPowerdownloadDown.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                btnActive.setSelected(true);
                changeRatio(false);
            }
        });
        constraints2.gridx = 1;
        tempPanel3.add(btnPowerdownloadDown, constraints2);
        constraints2.gridx = 2;
        tempPanel3.add(ratio, constraints2);
        constraints2.gridx = 3;
        tempPanel3.add(btnPowerdownloadUp, constraints2);
        constraints2.gridx = 4;
        constraints2.insets.left = 5;
        tempPanel3.add(label8, constraints2);

        constraints.gridy = 4;
        backPanel.add(tempPanel3, constraints);
        constraints.gridy = 5;
        constraints.gridwidth = 3;
        backPanel.add(btnPowerdownload, constraints);

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

        buttonGroup2.add(btnAutoInactive);
        buttonGroup2.add(btnAutoActive);
        btnAutoInactive.setSelected(true);
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        JPanel panel2 = new JPanel(new GridBagLayout());

        constraints.gridy = 0;
        panel2.add(btnAutoInactive, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel2.add(new JLabel(), constraints);
        constraints.weightx = 0;
        constraints.gridx = 2;
        panel2.add(autoPowerdownloadSettings, constraints);
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        backPanel.add(panel2, constraints);
        constraints.gridy = 10;
        backPanel.add(btnAutoActive, constraints);
        constraints.gridy = 11;
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(label10);
        autoFrom.setDocument(new NumberInputVerifier());
        autoFrom.setColumns(3);
        autoFrom.setText(Integer.toString(defaultAutomaticPowerdownloadFrom));
        autoTo.setDocument(new NumberInputVerifier());
        autoTo.setColumns(3);
        autoTo.setText(Integer.toString(defaultAutomaticPowerdownloadTo));
        autoFrom.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent fe) {
                int entered = Integer.parseInt(autoFrom.getText());

                if (entered < Integer.parseInt(autoTo.getText())) {
                    autoFrom.setText(Integer.toString(defaultAutomaticPowerdownloadFrom));
                }
            }
        });
        autoTo.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent fe) {
                int entered = Integer.parseInt(autoTo.getText());

                if (entered < defaultAutomaticPowerdownloadTo || entered > Integer.parseInt(autoFrom.getText())) {
                    autoTo.setText(Integer.toString(defaultAutomaticPowerdownloadTo));
                }
            }
        });
        panel.add(autoFrom);
        panel.add(new JLabel("MB "));
        panel.add(label11);
        panel.add(autoTo);
        panel.add(new JLabel("MB "));
        backPanel.add(panel, constraints);
        constraints.gridy = 12;
        constraints.gridwidth = 3;
        constraints.insets.bottom = 5;
        backPanel.add(btnAutoPowerdownload, constraints);
        btnAutoPowerdownload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                toggleAutoPowerdownload();
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
        autoPowerdownloadSettings.setVisible(false);
        autoPowerdownloadSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                openAutomaticPowerdownloadSettings();
            }
        });
        AppleJuiceClient.getAjFacade().addDataUpdateListener(this, DATALISTENER_TYPE.INFORMATION_CHANGED);
        AppleJuiceClient.getAjFacade().addDataUpdateListener(this, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
    }

    public void setPowerdownloadValue(int powerdownloadValue) {
        if (powerdownloadValue < 12) {
            ratio.setText("2.2");
            ratioValue = 2.2f;
            btnInactive.setSelected(true);
        } else {
            float value = (float) (powerdownloadValue + 10) / 10;

            ratio.setText(Float.toString(value));
            ratioValue = value;
            btnActive.setSelected(true);
        }
    }

    private void toggleAutoPowerdownload() {
        if (btnAutoActive.isSelected()) {
            if (autoPowerdownloadThread == null && autoFrom.isEnabled()) {
                if (!openAutoPowerdownloadSettings()) {
                    btnAutoInactive.setSelected(true);
                    return;
                }
                autoFrom.setEnabled(false);
                autoTo.setEnabled(false);
                btnPowerdownload.setEnabled(false);
                startAutoPowerdownload();
                downloadController.updateDownloadPolling();
                AppleJuiceDialog.getApp().informAutomaticPowerdownloadEnabled(true);
            }
        } else {
            if (!autoFrom.isEnabled()) {
                autoFrom.setEnabled(true);
                autoTo.setEnabled(true);
                if (autoPowerdownloadThread != null) {
                    autoPowerdownloadThread.interrupt();
                    autoPowerdownloadThread = null;
                }
                downloadController.updateDownloadPolling();
            }
        }
    }

    public boolean isAutomaticPowerdownloadActive() {
        return (!autoFrom.isEnabled() && autoPowerdownloadThread != null);
    }

    private void startAutoPowerdownload() {
        AutomaticPowerdownload powerdownload = new AutomaticPowerdownload(AppleJuiceClient.getAjFacade());

        creditThresholdFrom = Long.parseLong(autoFrom.getText()) * 1048576L;
        creditThresholdTo = Long.parseLong(autoTo.getText()) * 1048576L;
        applyAutoPowerdownloadSettings(powerdownload);
        powerdownload.setParentToInform(this);
        autoPowerdownloadThread = powerdownload;
        checkForPause(powerdownload);
        powerdownload.start();
        autoPowerdownloadSettings.setVisible(true);
    }

    private void checkForPause(AutomaticPowerdownload powerdownloadThread) {
        long enteredTo = Integer.parseInt(autoTo.getText()) * 1048576L;

        if (lastInformation == null) {
            lastInformation = AppleJuiceClient.getAjFacade().getInformation();
        }

        if (lastInformation.getCredits() < enteredTo) {
            powerdownloadThread.setPaused(true);
        }
    }

    /** Opens the modal settings dialog; false if it was not confirmed. */
    private boolean openAutoPowerdownloadSettings() {
        if (autoPowerdownloadDialog == null) {
            autoPowerdownloadDialog = new SettingsDialog(AppleJuiceDialog.getApp());
        }

        autoPowerdownloadDialog.setVisible(true);
        return autoPowerdownloadDialog.isConfirmed();
    }

    private void applyAutoPowerdownloadSettings(AutomaticPowerdownload powerdownload) {
        powerdownload.applySettings(autoPowerdownloadDialog.getDownloadCount(), autoPowerdownloadDialog.getPowerDownload(),
                autoPowerdownloadDialog.getSleepTime(), autoPowerdownloadDialog.getOrder());
    }

    private void openAutomaticPowerdownloadSettings() {
        AutomaticPowerdownload powerdownload = autoPowerdownloadThread;

        if (powerdownload != null && openAutoPowerdownloadSettings()) {
            applyAutoPowerdownloadSettings(powerdownload);
        }
    }

    public void autoPowerdownloadFinished(AutomaticPowerdownload completedPowerdownload) {
        if (completedPowerdownload != null && autoPowerdownloadThread == completedPowerdownload) {
            autoPowerdownloadFinished();
        }
    }

    public void autoPowerdownloadFinished() {
        autoPowerdownloadSettings.setVisible(false);
        autoPowerdownloadThread = null;
        downloadController.updateDownloadPolling();
        btnAutoInactive.setSelected(true);
        btnAutoPowerdownload.doClick();
        AppleJuiceDialog.getApp().informAutomaticPowerdownloadEnabled(false);
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

    private void changeRatio(boolean increase) {
        try {
            String temp = ratio.getText();
            int pos = temp.indexOf('.');
            int integer;
            int decimals;

            if (pos == -1) {
                integer = Integer.parseInt(temp);
                decimals = 0;
            } else {
                integer = Integer.parseInt(temp.substring(0, pos));
                decimals = Integer.parseInt(temp.substring(pos + 1));
            }

            if (increase) {
                if (ratioValue < 50f) {
                    if (decimals == 9) {
                        decimals = 0;
                        integer += 1;
                    } else {
                        decimals += 1;
                    }
                } else {
                    return;
                }
            } else {
                if (ratioValue > 2.2f) {
                    if (decimals == 0) {
                        decimals = 9;
                        integer -= 1;
                    } else {
                        decimals -= 1;
                    }
                } else {
                    return;
                }
            }

            ratio.setText(integer + "." + decimals);
            ratioValue = Float.parseFloat(ratio.getText());
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();

            powerdownload.setText(" " + languageSelector.getFirstAttributeByTagName("mainform.powerdownload.caption"));
            label6.setText(languageSelector.getFirstAttributeByTagName("mainform.Label6.caption"));
            btnInactive.setText(languageSelector.getFirstAttributeByTagName("mainform.powerinactive.caption"));
            btnActive.setText(languageSelector.getFirstAttributeByTagName("mainform.poweractive.caption"));
            label7.setText(languageSelector.getFirstAttributeByTagName("mainform.Label7.caption"));
            label8.setText(languageSelector.getFirstAttributeByTagName("mainform.Label8.caption"));
            label9.setText(" " + languageSelector.getFirstAttributeByTagName("javagui.downloadtab.label1"));
            btnAutoInactive.setText(languageSelector.getFirstAttributeByTagName("javagui.downloadtab.rbInactive"));
            btnAutoActive.setText(languageSelector.getFirstAttributeByTagName("javagui.downloadtab.rbActive"));
            label10.setText(languageSelector.getFirstAttributeByTagName("javagui.downloadtab.powerdownloadFrom"));
            label11.setText(languageSelector.getFirstAttributeByTagName("javagui.downloadtab.powerdownloadTo"));
            String ok = languageSelector.getFirstAttributeByTagName("javagui.downloadtab.btnOK");

            btnAutoPowerdownload.setText(ok);
            btnPowerdownload.setText(ok);
            btnHint.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.tooltips.powerdownload"));
            btnHint2.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.tooltips.autopowerdownload"));
            autoPowerdownloadSettings.setText(languageSelector.getFirstAttributeByTagName("javagui.options.plugins.settings"));
        } catch (Exception ex) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        try {
            if (type == DATALISTENER_TYPE.INFORMATION_CHANGED) {
                lastInformation = (Information) content;
                AutomaticPowerdownload policy = autoPowerdownloadThread;
                if (policy != null && lastInformation != null) {
                    // thresholds were read on the EDT; the poller thread must not touch Swing fields
                    long enteredTo = creditThresholdTo;
                    long enteredFrom = creditThresholdFrom;

                    if (lastInformation.getCredits() > enteredFrom) {
                        policy.setPaused(false);
                    } else if (lastInformation.getCredits() < enteredTo) {
                        policy.setPaused(true);
                    }
                }
            } else if (type == DATALISTENER_TYPE.DOWNLOAD_CHANGED && autoPowerdownloadThread != null && autoPowerdownloadThread.isPaused()) {
                Map<Integer, Download> downloads = (Map<Integer, Download>) content;
                List<Download> toPause = new Vector<Download>();

                synchronized (downloads) {
                    for (Download curDownload : downloads.values()) {
                        if (curDownload.getStatus() == Download.SEARCHING_LOADING) {
                            toPause.add(curDownload);
                        }
                    }
                }

                if (!toPause.isEmpty()) {
                    // Core command: never on the poller thread, a slow core would stop all data updates
                    CoreCall.runInBackground("PausePolicyDownloads",
                            () -> AppleJuiceClient.getAjFacade().pauseDownload(toPause));
                }
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public JButton getBtnPowerdownload() {
        return btnPowerdownload;
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

                double powerdownload = Double.parseDouble(temp);

                if (powerdownload < 2.2 || powerdownload > 50) {
                    ratio.setText("2.2");
                } else {
                    ratio.setText(temp);
                }

                btnActive.setSelected(true);
            } catch (Exception ex) {
                ratio.setText("2.2");
            }
        }
    }
}
