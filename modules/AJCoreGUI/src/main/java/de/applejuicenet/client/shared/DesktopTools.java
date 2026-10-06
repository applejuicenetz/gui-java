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
            // Desktop.browse fehlt z.B. ohne GTK in der Flatpak-Laufzeit
            new ProcessBuilder("xdg-open", uri.toString()).inheritIO().start();
        } else {
            throw new UnsupportedOperationException("browse not supported");
        }
    }

    private static void showBrowseError() {
        LanguageSelector ls = LanguageSelector.getInstance();
        String nachricht = ls.getFirstAttrbuteByTagName("javagui.startup.updatefehlernachricht");
        String titel = ls.getFirstAttrbuteByTagName("mainform.caption");

        JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), nachricht, titel, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void open(File toOpen) {
        if (null != desktopToolIF) {
            desktopToolIF.open(toOpen);
        }
    }
}
