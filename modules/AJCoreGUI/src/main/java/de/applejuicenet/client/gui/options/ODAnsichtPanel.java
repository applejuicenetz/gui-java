/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODAnsichtPanel.java,v 1.12 2009/01/26 13:31:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class ODAnsichtPanel extends JPanel implements OptionsRegister {
    private JLabel farbeFertigerDownload = new JLabel("      ");
    private JLabel farbeQuelle = new JLabel("      ");
    private Settings settings;
    private JCheckBox cmbAktiv = new JCheckBox();
    private JCheckBox cmbStartscreenZeigen = new JCheckBox();
    private JCheckBox cmbTabellenbreiten = new JCheckBox();
    private JCheckBox cmbIpMaskieren = new JCheckBox();
    private final Logger logger;
    private Icon menuIcon;
    private String menuText;
    private boolean dirty = false;
    private Border emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

    public ODAnsichtPanel() {
        logger = LoggerFactory.getLogger(getClass());
        try {
            settings = Settings.getSettings();
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        IconManager im = IconManager.getInstance();

        menuIcon = im.getIcon("opt_ansicht");
        cmbAktiv.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.aktiv"));
        cmbStartscreenZeigen.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.zeigestartscreen"));

        cmbTabellenbreiten.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.tabellenbreitenmerken"));

        cmbIpMaskieren.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.ipmaskieren"));

        setLayout(new BorderLayout());
        farbeFertigerDownload.setOpaque(true);
        farbeFertigerDownload.setBorder(emptyBorder);
        farbeFertigerDownload.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        farbeFertigerDownload.addMouseListener(new ColorChooserMouseAdapter());
        farbeQuelle.setOpaque(true);
        farbeQuelle.setBorder(emptyBorder);
        farbeQuelle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        farbeQuelle.addMouseListener(new ColorChooserMouseAdapter());
        OptionsManager om = OptionsManagerImpl.getInstance();

        cmbStartscreenZeigen.setSelected(om.shouldShowConnectionDialogOnStartup());
        cmbStartscreenZeigen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbIpMaskieren.setSelected(om.shouldMaskIpInStatusbar());
        cmbIpMaskieren.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbTabellenbreiten.setSelected(om.shouldRememberColumnWidths());
        cmbTabellenbreiten.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbAktiv.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
                settings.setFarbenAktiv(cmbAktiv.isSelected());
            }
        });

        ImageIcon icon = im.getIcon("hint");
        JLabel hint1 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();

                tip.setComponent(this);
                return tip;
            }
        };

        JLabel hint2 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();

                tip.setComponent(this);
                return tip;
            }
        };

        String tooltipp = languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.ttipp_farbewaehlen");

        hint1.setToolTipText(tooltipp);
        hint2.setToolTipText(tooltipp);

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.bottom = 5;

        JPanel panel1 = new JPanel();

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel1.setLayout(new GridBagLayout());
        panel1.setBorder(BorderFactory.createTitledBorder(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.hintergrundfarben")));
        panel1.add(cmbAktiv, constraints);
        constraints.gridy = 1;
        constraints.insets.left = 5;
        constraints.insets.right = 5;
        panel1.add(new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.fertigerdownload")), constraints);
        constraints.gridy = 2;
        panel1.add(new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.quelle")), constraints);
        menuText = languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.caption");
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel1.add(farbeFertigerDownload, constraints);
        constraints.gridy = 2;
        panel1.add(farbeQuelle, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        panel1.add(hint1, constraints);
        constraints.gridy = 2;
        panel1.add(hint2, constraints);
        JPanel panel2 = new JPanel(new BorderLayout());

        panel2.add(panel1, BorderLayout.NORTH);
        JPanel panel3 = new JPanel(new GridBagLayout());

        constraints.insets.bottom = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        panel3.add(cmbStartscreenZeigen, constraints);
        constraints.gridy = 1;
        panel3.add(cmbTabellenbreiten, constraints);
        constraints.gridy = 2;
        panel3.add(cmbIpMaskieren, constraints);
        panel2.add(panel3, BorderLayout.SOUTH);

        add(panel2, BorderLayout.WEST);

        reloadSettings();

    }

    public boolean save() {
        try {
            boolean bRet = false;
            OptionsManager om = OptionsManagerImpl.getInstance();

            if (om.shouldShowConnectionDialogOnStartup() != shouldShowStartcreen()) {
                om.showConnectionDialogOnStartup(shouldShowStartcreen());
                bRet = true;
            }

            if (om.shouldMaskIpInStatusbar() != cmbIpMaskieren.isSelected()) {
                om.maskIpInStatusbar(cmbIpMaskieren.isSelected());
                AppleJuiceDialog.getApp().updateExternalIp();
                bRet = true;
            }

            if (om.shouldRememberColumnWidths() != cmbTabellenbreiten.isSelected()) {
                om.rememberColumnWidths(cmbTabellenbreiten.isSelected());
                bRet = true;
            }

            if (settings.save()) {
                bRet = true;
            }

            return bRet;
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);

            return false;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean shouldShowStartcreen() {
        return cmbStartscreenZeigen.isSelected();
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }

    public void reloadSettings() {
        settings = Settings.getSettings();
        farbeQuelle.setBackground(settings.getQuelleHintergrundColor());
        farbeFertigerDownload.setBackground(settings.getDownloadFertigHintergrundColor());
        cmbAktiv.setSelected(settings.isFarbenAktiv());
    }

    class ColorChooserMouseAdapter extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();

            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            JLabel source = (JLabel) e.getSource();
            Color newColor = JColorChooser.showDialog(null,
                    languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.hintergrundfarbewaehlen"),
                    source.getBackground());

            if (newColor != null && newColor.getRGB() != source.getBackground().getRGB()) {
                source.setBackground(newColor);
                if (source == farbeQuelle) {
                    settings.setQuelleHintergrundColor(newColor);
                } else {
                    settings.setDownloadFertigHintergrundColor(newColor);
                }
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel) e.getSource();

            source.setBorder(emptyBorder);
        }
    }
}
