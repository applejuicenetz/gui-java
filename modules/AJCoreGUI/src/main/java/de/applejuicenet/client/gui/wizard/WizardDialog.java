/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Copyright: General Public License
 */
public class WizardDialog extends JDialog implements LanguageListener {
    private Logger logger;
    private WizardPanel currentPanel;
    private WizardPanel step1 = new Step1Panel();
    private WizardPanel step2 = new Step2Panel();
    private WizardPanel step3;
    private WizardPanel step4 = new Step4Panel();
    private WizardPanel step5 = new Step5Panel();
    private JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private JButton back = new JButton();
    private JButton nextButton = new JButton();
    private JButton end = new JButton();
    private AJSettings ajSettings;
    private boolean regularClosed = false;

    public WizardDialog(Frame parent, boolean modal) {
        super(parent, modal);
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
            LanguageSelector ls = LanguageSelector.getInstance();

            ls.addLanguageListener(this);
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public WizardDialog(JDialog parent, boolean modal, AJSettings ajSettings) {
        super(parent, modal);
        logger = LoggerFactory.getLogger(getClass());
        try {
            this.ajSettings = ajSettings;
            init();
            LanguageSelector ls = LanguageSelector.getInstance();

            ls.addLanguageListener(this);
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        if (ajSettings == null) {
            ajSettings = AppleJuiceClient.getAjFacade().getAJSettings();
        }

        step3 = new Step3Panel(this, ajSettings);
        getContentPane().setLayout(new BorderLayout());
        ImageIcon icon1 = IconManager.getInstance().getIcon("wizardbanner");
        JLabel label1 = new JLabel(icon1);

        step1.setPreviousPanel(null);
        step1.setNextPanel(step2);
        step2.setPreviousPanel(step1);
        step2.setNextPanel(step3);
        step3.setPreviousPanel(step2);
        step3.setNextPanel(step4);
        step4.setPreviousPanel(step3);
        step4.setNextPanel(step5);
        step5.setPreviousPanel(step4);
        step5.setNextPanel(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                LanguageSelector.getInstance().removeLanguageListener(WizardDialog.this);
                OptionsManagerImpl.getInstance().setFirstStart(false);
                dispose();
            }
        });
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (currentPanel.getPreviousPanel() != null) {
                    getContentPane().remove(currentPanel);
                    currentPanel = currentPanel.getPreviousPanel();
                    getContentPane().add(currentPanel, BorderLayout.CENTER);
                    nextButton.setEnabled(true);
                    if (currentPanel.getPreviousPanel() == null) {
                        back.setEnabled(false);
                    } else {
                        back.setEnabled(true);
                    }

                    if (currentPanel == step3) {
                        if (((Step3Panel) step3).isValidNickname()) {
                            setNextEnabled(true);
                        } else {
                            setNextEnabled(false);
                        }
                    } else {
                        setNextEnabled(true);
                    }

                    WizardDialog.this.validate();
                    WizardDialog.this.repaint();
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (currentPanel.getNextPanel() != null) {
                    getContentPane().remove(currentPanel);
                    currentPanel = currentPanel.getNextPanel();
                    getContentPane().add(currentPanel, BorderLayout.CENTER);
                    back.setEnabled(true);
                    if (currentPanel.getNextPanel() == null) {
                        nextButton.setEnabled(false);
                    } else {
                        if (currentPanel == step3) {
                            if (((Step3Panel) step3).isValidNickname()) {
                                setNextEnabled(true);
                            } else {
                                setNextEnabled(false);
                            }
                        } else {
                            setNextEnabled(true);
                        }
                    }

                    WizardDialog.this.validate();
                    WizardDialog.this.repaint();
                }
            }
        });
        end.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                regularClosed = true;
                close();
            }
        });
        buttons.add(back);
        buttons.add(nextButton);
        buttons.add(end);
        back.setEnabled(false);

        getContentPane().add(label1, BorderLayout.NORTH);
        getContentPane().add(step1, BorderLayout.CENTER);
        currentPanel = step1;
        getContentPane().add(buttons, BorderLayout.SOUTH);
        setSize(icon1.getIconWidth(), icon1.getIconHeight() + 180 + buttons.getPreferredSize().height);
        setResizable(false);
        fireLanguageChanged();
    }

    public boolean isRegularClosed() {
        return regularClosed;
    }

    private void close() {
        LanguageSelector.getInstance().removeLanguageListener(this);
        int result = JOptionPane.showConfirmDialog(DialogLocation.getReference(this),
                LanguageSelector.getInstance().getFirstAttributeByTagName("connect.remember.caption") +
                        " ?", "appleJuice Client", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            if (((Step3Panel) step3).isValidNickname()) {
                ConnectionKind connection = ((Step4Panel) step4).getConnectionType();

                ajSettings.setNick(((Step3Panel) step3).getNickname());
                ajSettings.setMaxUpload(connection.getMaxUpload() * 1024);
                ajSettings.setMaxDownload(connection.getMaxDownload() * 1024);
                ajSettings.setMaxNewConnectionsPerTurn(connection.getMaxNewConnectionsPer10Sec());
                AppleJuiceClient.getAjFacade().saveAJSettings(ajSettings);
            }
        } else {
            regularClosed = false;
        }

        OptionsManagerImpl.getInstance().setFirstStart(false);
        dispose();
    }

    public void setNextEnabled(boolean enabled) {
        nextButton.setEnabled(enabled);
    }

    public void fireLanguageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        setTitle(languageSelector.getFirstAttributeByTagName("javagui.wizard.title"));
        back.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.back"));
        nextButton.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.next"));
        end.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.finish"));
        step1.fireLanguageChanged();
        step2.fireLanguageChanged();
        step3.fireLanguageChanged();
        step4.fireLanguageChanged();
        step5.fireLanguageChanged();
        repaint();
    }
}
