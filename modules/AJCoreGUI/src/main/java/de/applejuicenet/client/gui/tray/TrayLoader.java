/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.tray;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.shared.IconManager;
import dev.hivens.libtray.Tray;
import dev.hivens.libtray.TrayBuilder;
import dev.hivens.libtray.TrayEvent;
import kotlin.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowStateListener;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.util.Locale;

public class TrayLoader implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(TrayLoader.class);
    private String zeigen = "Anzeigen";
    private String verstecken = "Verstecken";
    private TrayIcon trayIcon;
    private Tray nativeTray;
    private SwingTrayMenu nativeMenu;
    private AwtTrayMenu awtMenu;
    private SwingTrayPopup awtPopup;
    private AppleJuiceDialog dialog;
    private JMenuItem showHideItem;
    private Icon zeigenIcon;
    private Icon versteckenIcon;
    private final WindowStateListener windowStateListener = event -> updateVisibilityLabel();
    private final ComponentAdapter visibilityListener = new ComponentAdapter() {
        @Override public void componentShown(ComponentEvent event) { updateVisibilityLabel(); }
        @Override public void componentHidden(ComponentEvent event) { updateVisibilityLabel(); }
    };

    public boolean makeTray(String title, AppleJuiceDialog dialog, JMenuItem showHideItem,
                            Icon zeigenIcon, Icon versteckenIcon, JPopupMenu popup) {
        this.dialog = dialog;
        this.showHideItem = showHideItem;
        this.zeigenIcon = TrayMenuIcon.fit(zeigenIcon);
        this.versteckenIcon = TrayMenuIcon.fit(versteckenIcon);
        updateVisibilityLabel();
        dialog.addComponentListener(visibilityListener);
        dialog.addWindowStateListener(windowStateListener);
        ImageIcon icon = IconManager.getInstance().getIcon("applejuice");
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("linux") &&
                createNativeTray(title, icon, popup)) return true;
        if (!SystemTray.isSupported()) {
            close();
            return false;
        }
        trayIcon = new TrayIcon(icon.getImage(), title);
        trayIcon.setImageAutoSize(true);
        if (osName.startsWith("mac")) {
            awtMenu = new AwtTrayMenu(popup);
            trayIcon.setPopupMenu(awtMenu.getPopupMenu());
        } else {
            awtPopup = new SwingTrayPopup(popup);
        }
        trayIcon.addMouseListener(new TrayMouseListener(() -> {
            updateVisibilityLabel();
            showHideItem.doClick();
        }, this::showTrayPopup));
        try {
            SystemTray.getSystemTray().add(trayIcon);
            if (awtMenu != null) logger.info("macOS-Tray mit nativem AWT-Kontextmenue gestartet");
            return true;
        } catch (AWTException e) {
            logger.info("AWT-Tray nicht verfügbar", e);
            close();
            return false;
        }
    }

    private void showTrayPopup(MouseEvent event) {
        updateVisibilityLabel();
        if (awtPopup != null) awtPopup.show(event.getXOnScreen(), event.getYOnScreen());
    }

    private boolean createNativeTray(String title, ImageIcon icon, JPopupMenu popup) {
        try {
            nativeMenu = new SwingTrayMenu(popup,
                    menu -> { if (nativeTray != null) nativeTray.setMenu(menu); });
            ByteArrayOutputStream png = new ByteArrayOutputStream();
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            try { icon.paintIcon(null, graphics, 0, 0); }
            finally { graphics.dispose(); }
            ImageIO.write(image, "png", png);
            nativeTray = Tray.Companion.create(new TrayBuilder(title, png.toByteArray(), title,
                    nativeMenu.snapshot(), null, "io.github.applejuicenetz.javagui.StatusNotifierItem"));
            if (nativeTray == null) {
                nativeMenu.close();
                nativeMenu = null;
                return false;
            }
            SwingTrayMenu menu = nativeMenu;
            nativeTray.onEvent(event -> {
                if (event instanceof TrayEvent.MenuItemSelected selected) menu.select(selected.getId());
                else if (event instanceof TrayEvent.Activated) SwingUtilities.invokeLater(() -> showHideItem.doClick());
                return Unit.INSTANCE;
            });
            logger.info("Linux-Tray mit libtray/StatusNotifierItem gestartet");
            return true;
        } catch (Exception | LinkageError e) {
            logger.info("libtray nicht verfügbar; versuche AWT-Tray", e);
            if (nativeTray != null) nativeTray.close();
            if (nativeMenu != null) nativeMenu.close();
            nativeTray = null;
            nativeMenu = null;
            return false;
        }
    }

    private void updateVisibilityLabel() {
        if (showHideItem != null) {
            boolean shown = dialog.isVisible() && (dialog.getExtendedState() & Frame.ICONIFIED) == 0;
            showHideItem.setText(shown ? verstecken : zeigen);
            showHideItem.setIcon(shown ? versteckenIcon : zeigenIcon);
        }
    }

    public void setTextZeigen(String text) { zeigen = text; updateVisibilityLabel(); }
    public void setTextVerstecken(String text) { verstecken = text; updateVisibilityLabel(); }

    public void showBallon(String caption, String message) {
        if (trayIcon != null && (caption != null || message != null))
            trayIcon.displayMessage(caption, message, TrayIcon.MessageType.INFO);
    }

    @Override public void close() {
        if (dialog != null) dialog.removeComponentListener(visibilityListener);
        if (dialog != null) dialog.removeWindowStateListener(windowStateListener);
        if (awtPopup != null) awtPopup.close();
        if (trayIcon != null) trayIcon.setPopupMenu(null);
        if (awtMenu != null) awtMenu.close();
        if (nativeMenu != null) nativeMenu.close();
        if (nativeTray != null) nativeTray.close();
        if (trayIcon != null && SystemTray.isSupported()) SystemTray.getSystemTray().remove(trayIcon);
        nativeMenu = null;
        awtPopup = null;
        awtMenu = null;
        nativeTray = null;
        trayIcon = null;
    }
}
