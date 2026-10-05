package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.listener.CoreStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/** Plugin adapter: records incoming events even while tab is hidden. */
public class SpeedGraphPlugin extends PluginConnector implements CoreStatusListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedGraphPlugin.class);
    private final SpeedHistory history = new SpeedHistory(3600000, 20000);
    private final Properties settings = new Properties();
    private final Path settingsFile;
    private final ApplejuiceFassade facade;
    private final GraphPanel view;
    private final Timer refreshTimer;
    private boolean listening;

    public SpeedGraphPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles,
                            ImageIcon icon, Map<String, ImageIcon> availableIcons) {
        this(pluginsProperties, languageFiles, icon, availableIcons,
                Path.of(System.getProperty("user.home"), "appleJuice", "gui", "speedgraph.properties"), AppleJuiceClient.getAjFassade());
    }

    SpeedGraphPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles,
                     ImageIcon icon, Map<String, ImageIcon> availableIcons, Path settingsFile, ApplejuiceFassade facade) {
        super(pluginsProperties, languageFiles, icon, availableIcons);
        this.settingsFile = settingsFile;
        this.facade = facade;
        if (Files.isRegularFile(settingsFile)) {
            try (var input = Files.newInputStream(settingsFile)) { settings.load(input); }
            catch (IOException | IllegalArgumentException e) { LOGGER.warn("SpeedGraph-Einstellungen nicht lesbar", e); }
        }
        setLayout(new BorderLayout());
        view = new GraphPanel(history, settings, this::saveSettings);
        add(view, BorderLayout.CENTER);
        refreshTimer = new Timer(1000, e -> refresh());
        attachListeners();
    }

    private void attachListeners() {
        if (listening) return;
        if (facade != null) {
            facade.addDataUpdateListener(this, DATALISTENER_TYPE.SPEED_CHANGED);
            ApplejuiceFassade.addCoreStatusListener(this);
            listening = true;
        }
    }

    @Override public void addNotify() {
        super.addNotify();
        attachListeners();
        refreshTimer.start();
    }

    @Override public void removeNotify() {
        refreshTimer.stop();
        if (listening) {
            facade.removeDataUpdateListener(this, DATALISTENER_TYPE.SPEED_CHANGED);
            ApplejuiceFassade.removeCoreStatusListener(this);
            listening = false;
        }
        super.removeNotify();
    }

    @Override public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        if (type != DATALISTENER_TYPE.SPEED_CHANGED || !(content instanceof Map<?, ?> map)) return;
        if (!(map.get("uploadspeed") instanceof Long up) || !(map.get("downloadspeed") instanceof Long down)) return;
        if (up < 0 || down < 0) return;
        history.add(System.currentTimeMillis(), up, down);
        SwingUtilities.invokeLater(() -> {
            view.setDisconnected(false);
            if (isShowing()) refresh();
        });
    }

    @Override public void fireStatusChanged(STATUS status) {
        if (status == STATUS.CLOSED) history.markGap();
        SwingUtilities.invokeLater(() -> {
            view.setDisconnected(status == STATUS.CLOSED);
            refresh();
        });
    }

    private void refresh() {
        if (facade != null && facade.getCurrentAJSettings() != null) {
            var coreSettings = facade.getCurrentAJSettings();
            view.setLimits(coreSettings.getMaxUploadInKB() * 1024L, coreSettings.getMaxDownloadInKB() * 1024L);
        }
        view.refresh(System.currentTimeMillis());
    }

    private void saveSettings() {
        try {
            Files.createDirectories(settingsFile.getParent());
            try (var output = Files.newOutputStream(settingsFile)) { settings.store(output, "SpeedGraph"); }
        } catch (IOException e) { LOGGER.warn("SpeedGraph-Einstellungen nicht gespeichert", e); }
        refresh();
    }

    @Override public JPanel getOptionPanel() { return new SpeedGraphSettings(settings, this::saveSettings); }
    @Override public void registerSelected() { refresh(); }
    @Override public void fireLanguageChanged() { GuiText.refreshLanguage(); }
}
