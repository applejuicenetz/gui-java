/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.GuiText;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.shared.RefreshIntervals;
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

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODViewPanel.java,v 1.12 2009/01/26 13:31:36 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class ODViewPanel extends JPanel implements OptionsRegister {
    private JCheckBox cmbShowStartscreen = new JCheckBox();
    private JCheckBox cmbTableWidths = new JCheckBox();
    private JCheckBox cmbMaskIp = new JCheckBox();
    private JCheckBox cmbCloseToTray = new JCheckBox();
    private JCheckBox updateNotification = new JCheckBox();
    private JCheckBox loadPlugins = new JCheckBox();
    private final JSpinner corePollSeconds = seconds(RefreshIntervals.CORE_POLL_MIN_MS, RefreshIntervals.CORE_POLL_MAX_MS);
    private final JSpinner partlistSeconds = seconds(RefreshIntervals.PARTLIST_REFRESH_MIN_MS, RefreshIntervals.PARTLIST_REFRESH_MAX_MS);
    private JComboBox<LevelItem> cmbLog;
    private final Logger logger;
    private Icon menuIcon;
    private String menuText;
    private boolean dirty = false;

    public ODViewPanel() {
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        IconManager im = IconManager.getInstance();

        menuIcon = im.getIcon("opt_standard");
        menuText = languageSelector.getFirstAttributeByTagName("javagui.options.general.caption");
        cmbShowStartscreen.setText(languageSelector.getFirstAttributeByTagName("javagui.options.view.showstartscreen"));

        cmbTableWidths.setText(languageSelector.getFirstAttributeByTagName("javagui.options.view.remembertablewidths"));

        cmbMaskIp.setText(languageSelector.getFirstAttributeByTagName("javagui.options.view.maskip"));

        cmbCloseToTray.setText(languageSelector.getFirstAttributeByTagName("javagui.options.view.closetotray"));

        setLayout(new BorderLayout());
        OptionsManager om = OptionsManagerImpl.getInstance();

        cmbShowStartscreen.setSelected(om.shouldShowConnectionDialogOnStartup());
        cmbShowStartscreen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbCloseToTray.setSelected(om.shouldCloseToTray());
        cmbCloseToTray.addItemListener(e -> dirty = true);
        cmbMaskIp.setSelected(om.shouldMaskIpInStatusbar());
        cmbMaskIp.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });
        cmbTableWidths.setSelected(om.shouldRememberColumnWidths());
        cmbTableWidths.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });

        updateNotification.setText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.updatenotice"));
        updateNotification.setSelected(om.getUpdateInfo());
        updateNotification.addItemListener(e -> dirty = true);
        loadPlugins.setText(languageSelector.getFirstAttributeByTagName("javagui.options.standard.loadplugins"));
        loadPlugins.setSelected(om.shouldLoadPluginsOnStartup());
        loadPlugins.addItemListener(e -> dirty = true);

        corePollSeconds.setValue(om.getCorePollIntervalMs() / 1000.0);
        partlistSeconds.setValue(om.getPartlistRefreshIntervalMs() / 1000.0);
        corePollSeconds.addChangeListener(e -> dirty = true);
        partlistSeconds.addChangeListener(e -> dirty = true);

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
        hint3.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.options.logging.tooltip"));

        JPanel logRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logRow.add(GuiText.label("javagui.options.logging"));
        logRow.add(Box.createHorizontalStrut(5));
        logRow.add(cmbLog);
        logRow.add(Box.createHorizontalStrut(5));
        logRow.add(hint3);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(group("javagui.options.group.start", cmbShowStartscreen, loadPlugins, updateNotification, cmbCloseToTray));
        box.add(group("javagui.options.group.display", cmbTableWidths, cmbMaskIp));
        box.add(group("javagui.options.group.refresh",
                intervalRow("javagui.options.refresh.corepoll", corePollSeconds),
                intervalRow("javagui.options.refresh.partlist", partlistSeconds)));
        box.add(group("javagui.options.group.logging", logRow));

        add(box, BorderLayout.NORTH);

        reloadSettings();

    }

    /** Seconds with half-second steps; the limits come from {@link RefreshIntervals}. */
    private static JSpinner seconds(long minMs, long maxMs) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(minMs / 1000.0, minMs / 1000.0, maxMs / 1000.0, 0.5));
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.0"));
        return spinner;
    }

    private static JPanel intervalRow(String labelKey, JSpinner spinner) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel label = GuiText.label(labelKey);
        label.setLabelFor(spinner);
        // same label width in both rows, so the spinners line up
        label.setPreferredSize(new Dimension(Math.max(label.getPreferredSize().width, 110), label.getPreferredSize().height));
        row.add(label);
        row.add(Box.createHorizontalStrut(8));
        row.add(spinner);
        row.add(Box.createHorizontalStrut(4));
        row.add(GuiText.label("javagui.options.refresh.unit"));
        return row;
    }

    JSpinner corePollSpinner() {
        return corePollSeconds;
    }

    JSpinner partlistSpinner() {
        return partlistSeconds;
    }

    private static long toMillis(JSpinner spinner) {
        return Math.round(((Number) spinner.getValue()).doubleValue() * 1000);
    }

    private static JPanel group(String titleKey, JComponent... components) {
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(LanguageSelector.getInstance().getFirstAttributeByTagName(titleKey)));
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

            if (om.shouldShowConnectionDialogOnStartup() != shouldShowStartscreen()) {
                om.showConnectionDialogOnStartup(shouldShowStartscreen());
                bRet = true;
            }

            if (om.shouldCloseToTray() != cmbCloseToTray.isSelected()) {
                om.closeToTray(cmbCloseToTray.isSelected());
                bRet = true;
            }

            if (om.shouldMaskIpInStatusbar() != cmbMaskIp.isSelected()) {
                om.maskIpInStatusbar(cmbMaskIp.isSelected());
                AppleJuiceDialog.getApp().updateExternalIp();
                bRet = true;
            }

            if (om.shouldRememberColumnWidths() != cmbTableWidths.isSelected()) {
                om.rememberColumnWidths(cmbTableWidths.isSelected());
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

            if (om.getCorePollIntervalMs() != toMillis(corePollSeconds)) {
                om.setCorePollIntervalMs(toMillis(corePollSeconds));
                AppleJuiceClient.getAjFacade().setUpdateInterval(om.getCorePollIntervalMs());
                bRet = true;
            }

            if (om.getPartlistRefreshIntervalMs() != toMillis(partlistSeconds)) {
                om.setPartlistRefreshIntervalMs(toMillis(partlistSeconds));
                RefreshIntervals.setPartlistRefreshMs(om.getPartlistRefreshIntervalMs());
                bRet = true;
            }

            String level = ((LevelItem) cmbLog.getSelectedItem()).getLevel();

            if (!om.getLogLevel().toString().equals(level)) {
                om.setLogLevel(level);
                bRet = true;
            }

            return bRet;
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);

            return false;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean shouldShowStartscreen() {
        return cmbShowStartscreen.isSelected();
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
        private final String caption;

        LevelItem(String level, String caption) {
            this.level = level;
            this.caption = caption;
        }

        String getLevel() {
            return level;
        }

        @Override
        public String toString() {
            return caption;
        }
    }
}
