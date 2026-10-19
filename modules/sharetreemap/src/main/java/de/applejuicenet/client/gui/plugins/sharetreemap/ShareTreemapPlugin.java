package de.applejuicenet.client.gui.plugins.sharetreemap;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Share;
import de.applejuicenet.client.facade.entity.Upload;
import de.applejuicenet.client.facade.listener.CoreStatusListener;
import de.applejuicenet.client.gui.plugins.PluginConnector;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

public class ShareTreemapPlugin extends PluginConnector implements CoreStatusListener {
    private final TreemapView view = new TreemapView();
    private final UploadActivity activity = new UploadActivity();
    private final ApplejuiceFacade facade;
    private boolean listening;
    private volatile boolean selected;

    public ShareTreemapPlugin(Properties properties, Map<String, Properties> languages,
                              ImageIcon icon, Map<String, ImageIcon> icons) {
        this(properties, languages, icon, icons, AppleJuiceClient.getAjFacade());
    }

    ShareTreemapPlugin(Properties properties, Map<String, Properties> languages,
                       ImageIcon icon, Map<String, ImageIcon> icons, ApplejuiceFacade facade) {
        super(properties, languages, icon, icons);
        this.facade = facade;
        setLayout(new BorderLayout()); add(view, BorderLayout.CENTER);
        attach();
    }

    private void attach() {
        if (listening || facade == null) return;
        facade.addDataUpdateListener(this, DATALISTENER_TYPE.SHARE_CHANGED);
        facade.addDataUpdateListener(this, DATALISTENER_TYPE.UPLOAD_CHANGED);
        ApplejuiceFacade.addCoreStatusListener(this);
        listening = true;
    }

    @Override public void addNotify() { super.addNotify(); attach(); }
    @Override public void removeNotify() {
        if (selected) lostSelection();
        if (listening) {
            facade.removeDataUpdateListener(this, DATALISTENER_TYPE.SHARE_CHANGED);
            facade.removeDataUpdateListener(this, DATALISTENER_TYPE.UPLOAD_CHANGED);
            ApplejuiceFacade.removeCoreStatusListener(this);
            listening = false;
        }
        activity.clear();
        super.removeNotify();
    }

    @Override public void registerSelected() {
        selected = true;
        activity.gap();
        if (facade != null) {
            facade.setUploadPolling(true);
            fireContentChanged(DATALISTENER_TYPE.SHARE_CHANGED, facade.getShare(true));
        }
    }

    @Override public void lostSelection() {
        selected = false;
        if (facade != null) facade.setUploadPolling(false);
        activity.gap();
    }

    @Override public void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        if (!(content instanceof Map<?, ?> map)) return;
        if (type == DATALISTENER_TYPE.SHARE_CHANGED) {
            var files = new ArrayList<String[]>();
            for (Object value : map.values()) {
                if (!(value instanceof Share share) || share.getFilename() == null) continue;
                files.add(new String[]{Integer.toString(share.getId()), share.getFilename(),
                        Long.toString(share.getSize()), share.getAjfspLink() == null ? "" : share.getAjfspLink()});
            }
            ShareTree tree = ShareTree.fromFiles(files);
            SwingUtilities.invokeLater(() -> view.setTree(tree));
        } else if (type == DATALISTENER_TYPE.UPLOAD_CHANGED && selected) {
            HashSet<Integer> seen = new HashSet<>();
            for (Object value : map.values()) {
                if (!(value instanceof Upload upload)) continue;
                seen.add(upload.getId());
                activity.observe(upload.getId(), upload.getShareFileID(), upload.getUploadFrom(), upload.getActualUploadPosition());
            }
            activity.retain(seen);
            Map<Integer, Long> bytes = activity.snapshot();
            SwingUtilities.invokeLater(() -> view.setActivity(bytes));
        }
    }

    @Override public void fireStatusChanged(STATUS status) {
        if (status != STATUS.CLOSED) return;
        activity.clear();
        SwingUtilities.invokeLater(() -> {
            view.setActivity(Map.of());
            view.setTree(ShareTree.fromFiles(java.util.List.of()));
            view.showLanguageMessage("plugins.treemap.disconnected");
        });
    }
    @Override public void fireLanguageChanged() { GuiText.refreshLanguage(); }
}
