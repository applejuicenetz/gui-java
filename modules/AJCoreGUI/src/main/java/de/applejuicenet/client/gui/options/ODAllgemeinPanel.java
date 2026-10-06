package de.applejuicenet.client.gui.options;

import ch.qos.logback.classic.Level;
import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;

import javax.swing.*;
import java.awt.*;

/**
 * Einstellungen, die nur das JavaGUI betreffen (nicht an den Core gesendet).
 */
public class ODAllgemeinPanel extends JPanel implements OptionsRegister {
    private boolean dirty = false;
    private final JComboBox<LevelItem> cmbLog;
    private final JCheckBox updateNotification = new JCheckBox();
    private final JCheckBox loadPlugins = new JCheckBox();
    private final Icon menuIcon;
    private final String menuText;

    public ODAllgemeinPanel() {
        OptionsManager optionsManager = OptionsManagerImpl.getInstance();
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        IconManager im = IconManager.getInstance();

        menuIcon = im.getIcon("opt_standard");
        menuText = languageSelector.getFirstAttrbuteByTagName("javagui.options.allgemein.caption");

        LevelItem[] levelItems = {
            new LevelItem("INFO", "Info"), new LevelItem("WARN", "Warn"), new LevelItem("ERROR", "Error"),
            new LevelItem("DEBUG", "Debug"), new LevelItem("OFF", "Off")
        };
        cmbLog = new JComboBox<>(levelItems);
        Level logLevel = optionsManager.getLogLevel();
        int index = 0;
        for (int i = 0; i < levelItems.length; i++) {
            if (levelItems[i].getLevel().equals(logLevel.toString())) {
                index = i;
            }
        }
        cmbLog.setSelectedIndex(index);
        cmbLog.addItemListener(e -> dirty = true);

        updateNotification.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.updateinfotext"));
        updateNotification.setSelected(optionsManager.getUpdateInfo());
        updateNotification.addItemListener(e -> dirty = true);

        loadPlugins.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.ladeplugins"));
        loadPlugins.setSelected(optionsManager.shouldLoadPluginsOnStartup());
        loadPlugins.addItemListener(e -> dirty = true);

        JLabel hint = new JLabel(im.getIcon("hint")) {
            @Override
            public JToolTip createToolTip() {
                MultiLineToolTip tip = new MultiLineToolTip();
                tip.setComponent(this);
                return tip;
            }
        };
        hint.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.options.logging.ttip"));

        JPanel logPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logPanel.add(GuiText.label("javagui.options.logging"));
        logPanel.add(cmbLog);
        logPanel.add(hint);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[] {logPanel, updateNotification, loadPlugins}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            box.add(c);
        }

        setLayout(new BorderLayout());
        add(box, BorderLayout.NORTH);
    }

    public boolean isDirty() {
        return dirty;
    }

    public String getLogLevel() {
        return ((LevelItem) cmbLog.getSelectedItem()).getLevel();
    }

    public boolean getUpdateInfo() {
        return updateNotification.isSelected();
    }

    public boolean shouldLoadPluginsOnStartup() {
        return loadPlugins.isSelected();
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
