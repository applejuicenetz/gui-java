/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.tray.DesktopTool;

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class DesktopTools {
    private static final DesktopTool desktopToolIF;

    static {
        desktopToolIF = new DesktopTool();
    }

    public static boolean isAdvancedSupported() {
        return null != desktopToolIF;
    }

    public static void browse(URI uri) {
        Thread.ofVirtual().name("DesktopBrowse").start(() -> {
            try {
                browseBlocking(uri);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(DesktopTools::showBrowseError);
            }
        });
    }

    private static void browseBlocking(URI uri) throws Exception {
        if (desktopToolIF.isBrowseSupported()) {
            desktopToolIF.browse(uri);
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            // Desktop.browse is missing e.g. without GTK in the Flatpak runtime
            new ProcessBuilder("xdg-open", uri.toString()).inheritIO().start();
        } else {
            throw new UnsupportedOperationException("browse not supported");
        }
    }

    private static void showBrowseError() {
        LanguageSelector ls = LanguageSelector.getInstance();
        String message = ls.getFirstAttributeByTagName("javagui.startup.updateerrormessage");
        String title = ls.getFirstAttributeByTagName("mainform.caption");

        JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void open(File toOpen) {
        if (null != desktopToolIF) {
            desktopToolIF.open(toOpen);
        }
    }
}
