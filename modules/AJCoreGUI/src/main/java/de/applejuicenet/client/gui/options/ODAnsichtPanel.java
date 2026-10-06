/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.DialogLocation;
import de.applejuicenet.client.gui.controller.GuiText;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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
    private JCheckBox cmbStartscreenZeigen = new JCheckBox();
    private JCheckBox cmbTabellenbreiten = new JCheckBox();
    private JCheckBox cmbIpMaskieren = new JCheckBox();
    private JCheckBox updateNotification = new JCheckBox();
    private JCheckBox loadPlugins = new JCheckBox();
    private JComboBox<LevelItem> cmbLog;
    private final Logger logger;
    private Icon menuIcon;
    private String menuText;
    private boolean dirty = false;

    public ODAnsichtPanel() {
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        IconManager im = IconManager.getInstance();

        menuIcon = im.getIcon("opt_standard");
        menuText = languageSelector.getFirstAttrbuteByTagName("javagui.options.allgemein.caption");
        cmbStartscreenZeigen.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.zeigestartscreen"));

        cmbTabellenbreiten.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.tabellenbreitenmerken"));

        cmbIpMaskieren.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.ansicht.ipmaskieren"));

        setLayout(new BorderLayout());
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

        updateNotification.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.updateinfotext"));
        updateNotification.setSelected(om.getUpdateInfo());
        updateNotification.addItemListener(e -> dirty = true);
        loadPlugins.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.ladeplugins"));
        loadPlugins.setSelected(om.shouldLoadPluginsOnStartup());
        loadPlugins.addItemListener(e -> dirty = true);

        LevelItem[] levelItems = {
            new LevelItem("INFO", "Info"), new LevelItem("WARN", "Warn"), new LevelItem("ERROR", "Error"),
            new LevelItem("DEBUG", "Debug"), new LevelItem("OFF", "Off")
        };
        cmbLog = new JComboBox<>(levelItems);
        String currentLevel = om.getLogLevel().toString();
        for (int i = 0; i < levelItems.length; i++) {
            if (levelItems[i].getLevel().equals(currentLevel)) {
                cmbLog.setSelectedIndex(i);
            }
        }
        cmbLog.addItemListener(e -> dirty = true);

        ImageIcon icon = im.getIcon("hint");
        JLabel hint3 = new JLabel(icon) {
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();

                tip.setComponent(this);
                return tip;
            }
        };
        hint3.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.options.logging.ttip"));

        JPanel logRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logRow.add(GuiText.label("javagui.options.logging"));
        logRow.add(Box.createHorizontalStrut(5));
        logRow.add(cmbLog);
        logRow.add(Box.createHorizontalStrut(5));
        logRow.add(hint3);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(group("javagui.options.group.start", cmbStartscreenZeigen, loadPlugins, updateNotification));
        box.add(group("javagui.options.group.anzeige", cmbTabellenbreiten, cmbIpMaskieren));
        box.add(group("javagui.options.group.logging", logRow));

        add(box, BorderLayout.NORTH);

        reloadSettings();

    }

    private static JPanel group(String titleKey, JComponent... components) {
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(LanguageSelector.getInstance().getFirstAttrbuteByTagName(titleKey)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JComponent c : components) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(c);
        }
        return panel;
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

            if (om.shouldLoadPluginsOnStartup() != loadPlugins.isSelected()) {
                om.loadPluginsOnStartup(loadPlugins.isSelected());
                bRet = true;
            }

            if (om.getUpdateInfo() != updateNotification.isSelected()) {
                om.setUpdateInfo(updateNotification.isSelected());
                bRet = true;
            }

            String level = ((LevelItem) cmbLog.getSelectedItem()).getLevel();

            if (!om.getLogLevel().toString().equals(level)) {
                om.setLogLevel(level);
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
        // nothing to do...
    }

    static class LevelItem {
        private final String level;
        private final String bezeichnung;

        LevelItem(String level, String bezeichnung) {
            this.level = level;
            this.bezeichnung = bezeichnung;
        }

        String getLevel() {
            return level;
        }

        @Override
        public String toString() {
            return bezeichnung;
        }
    }
}
