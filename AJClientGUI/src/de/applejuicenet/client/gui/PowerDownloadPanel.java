package de.applejuicenet.client.gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownloadPolicy;
import de.applejuicenet.client.gui.tables.download.DownloadMainNode;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.Information;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.applejuicenet.client.shared.PolicyJarClassLoader;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/PowerDownloadPanel.java,v 1.38 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: PowerDownloadPanel.java,v $
 * Revision 1.38  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.37  2004/01/31 08:49:04  maj0r
 * PwdlPolicies werden jetzt wie Plugins als jars eingebunden.
 *
 * Revision 1.36  2004/01/12 07:26:44  maj0r
 * Auf JSplitPane umgebaut.
 *
 * Revision 1.35  2004/01/05 15:11:19  maj0r
 * Bug #13 umgesetzt (Danke an HabkeineMail)
 * Powerdownload-Werte werden jetzt bei Klick auf einen Download / Quelle im Powerdownloadfeld angezeigt.
 *
 * Revision 1.34  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.33  2003/12/29 09:39:21  maj0r
 * Alte BugIDs entfernt, da auf neuen Bugtracker auf bugs.applejuicenet.de umgestiegen wurde.
 *
 * Revision 1.32  2003/11/24 21:12:35  maj0r
 * Hier trat merkwuerdigerweise ein Bug auf, spezielleres Logging fuer bessere Auswertung eingebaut.
 *
 * Revision 1.31  2003/11/19 17:05:20  maj0r
 * Autom. Pwdl ueberarbeitet.
 *
 * Revision 1.30  2003/11/17 14:44:10  maj0r
 * Erste funktionierende Version des automatischen Powerdownloads eingebaut.
 *
 * Revision 1.29  2003/11/17 07:32:30  maj0r
 * Automatischen Pwdl begonnen.
 *
 * Revision 1.28  2003/11/03 21:17:16  maj0r
 * Kleinen Fehler im Pwdl-Textfeld behoben.
 *
 * Revision 1.27  2003/10/31 16:24:58  maj0r
 * Soundeffekte fuer diverse Ereignisse eingefuegt.
 *
 * Revision 1.26  2003/10/27 12:40:48  maj0r
 * Buttons entsprechend dem Standard vertauscht (Danke an lova).
 *
 * Revision 1.25  2003/10/16 12:06:51  maj0r
 * Diverse Schoenheitskorrekturen.
 *
 * Revision 1.24  2003/10/14 15:43:24  maj0r
 * Logger eingebaut.
 * Powerdownloads werden nun innerhalb einer Connection gesetzt,
 *
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
 * Historie eingefuegt.
 *
 *
 */

public class PowerDownloadPanel
    extends JPanel
    implements LanguageListener, DataUpdateListener {
    private final Color BLUE_BACKGROUND = new Color(118, 112, 148);
    private JRadioButton btnInaktiv = new JRadioButton();
    private JRadioButton btnAktiv = new JRadioButton();
    private JRadioButton btnAutoInaktiv = new JRadioButton();
    private JRadioButton btnAutoAktiv = new JRadioButton();
    private JLabel btnHint;
    private JLabel btnHint2;
    private JLabel btnHint3;
    private JLabel btnPdlUp;
    private JLabel btnPdlDown;
    private float ratioWert = 2.2f;
    private JTextField ratio = new JTextField("2.2");
    private JTextField autoAb = new JTextField();
    private JTextField autoBis = new JTextField();
    public JButton btnPdl = new JButton("Uebernehmen");
    private JButton btnAutoPdl = new JButton("Uebernehmen");
    private JLabel powerdownload = new JLabel("Powerdownload");
    private JLabel label6 = new JLabel(
        "Wieviel willst Du maximal fuer 1 Byte bezahlen?");
    private JLabel label7 = new JLabel("Fuer 1 Byte zahle");
    private JLabel label8 = new JLabel("Credits");
    private JLabel label9 = new JLabel("Automatischer Powerdownload");
    private JLabel label10 = new JLabel("ab ");
    private JLabel label11 = new JLabel("bis ");
    private Logger logger;
    private RatioFocusAdapter ratioFocusAdapter;
    private JComboBox pwdlPolicies = new JComboBox();

    private int standardAutomaticPwdlAb = 200;
    private int standardAutomaticPwdlBis = 30;

    private DownloadPanel parentPanel;

    private AutomaticPowerdownloadPolicy autoPwdlThread;
    private Information lastInformation;

    public PowerDownloadPanel(DownloadPanel parentPanel) {
        logger = Logger.getLogger(getClass());
        this.parentPanel = parentPanel;
        btnPdl.setEnabled(false);
        try {
            jbInit();
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    private void jbInit() throws Exception {
        setLayout(new BorderLayout());
        LanguageSelector.getInstance().addLanguageListener(this);
        JPanel backPanel = new JPanel();
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
        ratio.setBackground(Color.white);
        ratio.setMinimumSize(new Dimension(50, 21));
        ratio.setPreferredSize(new Dimension(50, 21));
        ratio.setHorizontalAlignment(SwingConstants.RIGHT);
        KeyAdapter ratioKlicker = new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    ratioFocusAdapter.focusLost(null);
                    btnPdl.doClick();
                }
            }
        };
        btnInaktiv.addKeyListener(ratioKlicker);
        btnAktiv.addKeyListener(ratioKlicker);
        ratio.addKeyListener(ratioKlicker);

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
        btnHint3 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        tempPanel2.add(btnHint2, BorderLayout.EAST);
        backPanel.add(tempPanel2, constraints);

        constraints.gridwidth = 1;
        constraints.gridy = 8;
        ButtonGroup buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(btnAutoInaktiv);
        buttonGroup2.add(btnAutoAktiv);
        btnAutoInaktiv.setText("inaktiv");
        btnAutoAktiv.setText("aktiv");
        btnAutoAktiv.setSelected(true);
        pwdlPolicies.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                AutomaticPowerdownloadPolicy policy = (
                    AutomaticPowerdownloadPolicy) pwdlPolicies.getSelectedItem();
                StringBuffer text = new StringBuffer(policy.getDescription());
                int i = 0;
                while (i < text.length()) {
                    if (text.charAt(i) == '.') {
                        text.insert(i + 1, '|');
                        i++;
                    }
                    i++;
                }
                text.insert(0, policy.toString() + "|" +
                            "Autor: " + policy.getAuthor() + "|" +
                            "Beschreibung:|");
                btnHint3.setToolTipText(text.toString());
            }
        });
        fillPwdlPolicies();
        JPanel panel1 = new JPanel(new FlowLayout());
        panel1.add(pwdlPolicies);
        panel1.add(btnHint3);
        backPanel.add(panel1, constraints);
        constraints.gridy = 9;
        backPanel.add(btnAutoInaktiv, constraints);
        constraints.gridy = 10;
        backPanel.add(btnAutoAktiv, constraints);
        constraints.gridy = 11;
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(label10);
        autoAb.setDocument(new NumberInputVerifier());
        autoAb.setPreferredSize(new Dimension(40, 21));
        autoAb.setText(Integer.toString(standardAutomaticPwdlAb));
        autoBis.setDocument(new NumberInputVerifier());
        autoBis.setPreferredSize(new Dimension(40, 21));
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
                if (eingegeben < standardAutomaticPwdlBis ||
                    eingegeben > Integer.parseInt(autoAb.getText())) {
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
        backPanel.add(btnAutoPdl, constraints);
        btnAutoPdl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                alterAutoPwdl();
            }
        });
        add(new JScrollPane(backPanel), BorderLayout.NORTH);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
            DataUpdateListener.INFORMATION_CHANGED);
        ApplejuiceFassade.getInstance().addDataUpdateListener(this,
            DataUpdateListener.DOWNLOAD_CHANGED);
    }

    public void setPwdlValue(int pwdlValue) {
        if (pwdlValue < 12) {
            ratio.setText("2.2");
            btnInaktiv.setSelected(true);
        }
        else {
            float wert = (float) ( (float) (pwdlValue + 10) / 10);
            ratio.setText(Float.toString(wert));
            btnAktiv.setSelected(true);
        }
    }

    private void alterAutoPwdl() {
        if (btnAutoAktiv.isSelected()) {
            if (pwdlPolicies.isEnabled()) {
                pwdlPolicies.setEnabled(false);
                autoAb.setEnabled(false);
                autoBis.setEnabled(false);
                btnPdl.setEnabled(false);
                AutomaticPowerdownloadPolicy selectedPolicy = (
                    AutomaticPowerdownloadPolicy) pwdlPolicies.getSelectedItem();
                manageAutoPwdl(selectedPolicy);
                AppleJuiceDialog.getApp().informAutomaticPwdlEnabled(true);
            }
        }
        else {
            if (!pwdlPolicies.isEnabled()) {
                pwdlPolicies.setEnabled(true);
                autoAb.setEnabled(true);
                autoBis.setEnabled(true);
                if (autoPwdlThread != null) {
                    autoPwdlThread.interrupt();
                    autoPwdlThread = null;
                }
                AppleJuiceDialog.getApp().informAutomaticPwdlEnabled(false);
            }
        }
    }

    public boolean isAutomaticPwdlActive() {
        if (!pwdlPolicies.isEnabled() && autoPwdlThread != null) {
            return true;
        }
        else {
            return false;
        }
    }

    private void manageAutoPwdl(final AutomaticPowerdownloadPolicy
                                selectedPolicy) {
        if (autoPwdlThread != null) {
            autoPwdlThread.interrupt();
            autoPwdlThread = null;
        }
        AutomaticPowerdownloadPolicy policy = null;
        try {
            policy = (AutomaticPowerdownloadPolicy) selectedPolicy.getClass().
                newInstance();
        }
        catch (InstantiationException e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        catch (IllegalAccessException e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
        autoPwdlThread = policy;
        autoPwdlThread.setPaused(true);
        autoPwdlThread.start();
    }

    private void fillPwdlPolicies() {
        AutomaticPowerdownloadPolicy[] policies = loadPolicies();
        for (int i = 0; i < policies.length; i++) {
            pwdlPolicies.addItem(policies[i]);
        }
        if (pwdlPolicies.getItemCount() > 0) {
            pwdlPolicies.setSelectedIndex(0);
        }
    }

    private AutomaticPowerdownloadPolicy[] loadPolicies() {
        try {
            String path = System.getProperty("user.dir") + File.separator +
                "pwdlpolicies" +
                File.separator;
            File policyPath = new File(path);
            if (!policyPath.isDirectory()) {
                if (logger.isEnabledFor(Level.INFO)) {
                    logger.info(
                        "Warnung: Kein Verzeichnis 'pwdlpolicies' vorhanden!");
                }
                return new AutomaticPowerdownloadPolicy[0];
            }
            String[] tempListe = policyPath.list();
            PolicyJarClassLoader jarLoader = null;
            ArrayList policies = new ArrayList();
            for (int i = 0; i < tempListe.length; i++) {
                if (tempListe[i].toLowerCase().endsWith(".jar")) {
                    URL url = null;
                    try {
                        url = new URL("file://" + path + tempListe[i]);
                        jarLoader = new PolicyJarClassLoader(url);
                        AutomaticPowerdownloadPolicy aPolicy = jarLoader.
                            getPolicy(path + tempListe[i]);
                        if (aPolicy != null) {
                            policies.add(aPolicy);
                        }
                    }
                    catch (Exception e) {
                        //Von einer Policy lassen wir uns nicht beirren! ;-)
                        if (logger.isEnabledFor(Level.ERROR)) {
                            logger.error(
                                "Eine PowerdownloadPolicy konnte nicht instanziert werden",
                                e);
                        }
                        continue;
                    }
                }
            }
            return (AutomaticPowerdownloadPolicy[]) policies.toArray(new
                AutomaticPowerdownloadPolicy[policies.size()]);
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
            return new AutomaticPowerdownloadPolicy[0];
        }
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
            }
            else {
                ganzZahl = Integer.parseInt(temp.substring(0, pos));
                nachKomma = Integer.parseInt(temp.substring(pos + 1));
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
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    void btnPdl_actionPerformed(ActionEvent e) {
        try {
            Object[] selectedItems = parentPanel.getSelectedDownloadItems();
            if (selectedItems != null && selectedItems.length != 0) {
                int powerDownload = 0;
                if (!btnInaktiv.isSelected()) {
                    String temp = ratio.getText();
                    double power = 2.2;
                    try {
                        power = Double.parseDouble(temp);
                    }
                    catch (NumberFormatException nfE) {
                        if (logger.isEnabledFor(Level.ERROR)) {
                            logger.error("Unbehandelte Exception", nfE);
                        }
                        ratio.setText("2.2");
                    }
                    powerDownload = (int) (power * 10 - 10);
                }
                ArrayList temp = new ArrayList();
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i].getClass() == DownloadMainNode.class) {
                        temp.add(new Integer( ( (DownloadMainNode)
                                               selectedItems[i]).getDownloadDO().
                                             getId()));
                    }
                }
                int[] ids = new int[temp.size()];
                for (int i = 0; i < temp.size(); i++) {
                    ids[i] = ( (Integer) temp.get(i)).intValue();
                }
                ApplejuiceFassade.getInstance().setPowerDownload(ids,
                    powerDownload);
                if (btnAktiv.isSelected()) {
                    SoundPlayer.getInstance().playSound(SoundPlayer.POWER);
                }
            }
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public void fireLanguageChanged() {
        try {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            powerdownload.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "powerdownload",
                                          "caption"})));
            label6.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "Label6",
                                          "caption"})));
            btnInaktiv.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "powerinactive",
                                          "caption"})));
            btnAktiv.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform",
                                          "poweractive",
                                          "caption"})));
            label7.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "Label7",
                                          "caption"})));
            label8.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"mainform", "Label8",
                                          "caption"})));
            label9.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui",
                                          "downloadtab",
                                          "label1"})));
            btnAutoInaktiv.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui",
                                          "downloadtab",
                                          "rbInaktiv"})));
            btnAutoAktiv.setText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui",
                                          "downloadtab",
                                          "rbAktiv"})));
            label10.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui",
                                          "downloadtab",
                                          "pdlAb"})));
            label11.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui",
                                          "downloadtab",
                                          "pdlBis"})));
            String ok = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(new
                                          String[] {"javagui", "downloadtab",
                                          "btnOK"}));
            btnAutoPdl.setText(ok);
            btnPdl.setText(ok);
            btnHint.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "tooltipps",
                                          "powerdownload"})));
            btnHint2.setToolTipText(ZeichenErsetzer.korrigiereUmlaute(
                languageSelector.
                getFirstAttrbuteByTagName(new String[] {"javagui", "tooltipps",
                                          "autopowerdownload"})));
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public void fireContentChanged(int type, Object content) {
        try {
            if (type == DataUpdateListener.INFORMATION_CHANGED) {
                lastInformation = (Information) content;
                if (!pwdlPolicies.isEnabled() && autoPwdlThread != null &&
                    lastInformation != null) {
                    long eingegebenBis = Integer.parseInt(autoBis.getText()) *
                        1048576l;
                    long eingegebenAb = Integer.parseInt(autoAb.getText()) *
                        1048576l;
                    if (lastInformation.getCredits() > eingegebenAb &&
                        autoPwdlThread.isPaused()) {
                        autoPwdlThread.setPaused(false);
                    }
                    else if (lastInformation.getCredits() < eingegebenBis &&
                             !autoPwdlThread.isPaused()) {
                        autoPwdlThread.setPaused(true);
                    }
                }
            }
            else if (type == DataUpdateListener.DOWNLOAD_CHANGED &&
                     autoPwdlThread != null &&
                     !pwdlPolicies.isEnabled() && autoPwdlThread.isPaused()) {
                HashMap downloads = (HashMap) content;
                DownloadDO downloadDO;
                synchronized (downloads) {
                    Iterator it = downloads.values().iterator();
                    while (it.hasNext()) {
                        downloadDO = (DownloadDO) it.next();
                        if (downloadDO.getStatus() == DownloadDO.SUCHEN_LADEN) {
                            ApplejuiceFassade.getInstance().pauseDownload(new int[] {
                                downloadDO.getId()});
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    private class RatioFocusAdapter
        extends FocusAdapter {
        public void focusLost(FocusEvent fe) {
            try {
                String temp = ratio.getText();
                temp = temp.replaceAll(",", ".");
                int pos = temp.lastIndexOf('.');
                if (pos != -1) {
                    temp = temp.substring(0, pos + 2);
                }
                double pwdl = new Double(temp).doubleValue();
                if (pwdl < 2.2 || pwdl > 50) {
                    ratio.setText("2.2");
                }
                else {
                    ratio.setText(temp);
                }
                btnAktiv.setSelected(true);
            }
            catch (Exception ex) {
                ratio.setText("2.2");
            }
        }
    }
}
