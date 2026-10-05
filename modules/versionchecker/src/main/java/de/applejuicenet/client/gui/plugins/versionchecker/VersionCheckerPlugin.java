package de.applejuicenet.client.gui.plugins.versionchecker;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.listener.CoreStatusListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.plugins.versionchecker.panels.MainPanel;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Properties;

public class VersionCheckerPlugin extends PluginConnector implements CoreStatusListener {
    private final MainPanel mainPanel = new MainPanel();
    private final ApplejuiceFassade facade;
    private boolean listening;

    public VersionCheckerPlugin(Properties properties, Map<String, Properties> languages, ImageIcon icon,
                                Map<String, ImageIcon> icons) {
        this(properties, languages, icon, icons, AppleJuiceClient.getAjFassade());
    }

    VersionCheckerPlugin(Properties properties, Map<String, Properties> languages, ImageIcon icon,
                         Map<String, ImageIcon> icons, ApplejuiceFassade facade) {
        super(properties, languages, icon, icons);
        this.facade = facade;
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        attach();
    }

    private void attach() {
        if (listening || facade == null) return;
        facade.addDataUpdateListener(this, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
        facade.addDataUpdateListener(this, DATALISTENER_TYPE.UPLOAD_CHANGED);
        ApplejuiceFassade.addCoreStatusListener(this);
        listening = true;
    }

    @Override public void addNotify() { super.addNotify(); attach(); }
    @Override public void removeNotify() {
        if (listening) {
            facade.removeDataUpdateListener(this, DATALISTENER_TYPE.DOWNLOAD_CHANGED);
            facade.removeDataUpdateListener(this, DATALISTENER_TYPE.UPLOAD_CHANGED);
            ApplejuiceFassade.removeCoreStatusListener(this);
            listening = false;
        }
        super.removeNotify();
    }

    @Override public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        if (!(content instanceof Map<?, ?> map)) return;
        if (type == DATALISTENER_TYPE.DOWNLOAD_CHANGED) {
            Map<String, Download> downloads = new java.util.HashMap<>();
            synchronized (map) {
                map.forEach((key, value) -> { if (value instanceof Download download) downloads.put(String.valueOf(key), download); });
            }
            mainPanel.updateByDownload(downloads);
        } else if (type == DATALISTENER_TYPE.UPLOAD_CHANGED) {
            Map<String, Upload> uploads = new java.util.HashMap<>();
            synchronized (map) {
                map.forEach((key, value) -> { if (value instanceof Upload upload) uploads.put(String.valueOf(key), upload); });
            }
            mainPanel.updateByUploads(uploads);
        }
    }

    @Override public void fireStatusChanged(STATUS status) {
        // Core may reuse transfer IDs after reconnect; do not merge different sessions.
        if (status == STATUS.CLOSED) SwingUtilities.invokeLater(mainPanel::reset);
    }
    @Override public void registerSelected() { mainPanel.refresh(); }
    @Override public void fireLanguageChanged() { GuiText.refreshLanguage(); }
}
