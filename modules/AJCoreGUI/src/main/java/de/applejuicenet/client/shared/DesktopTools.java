/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.io.File;

import java.net.URI;

import javax.swing.JOptionPane;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.tray.DesktopTool;

public class DesktopTools {
    private static DesktopTool desktopToolIF;

    static {
        desktopToolIF = new DesktopTool();
    }

    public static boolean isAdvancedSupported() {
        return null != desktopToolIF;
    }

    public static void browse(URI uri) {
        if (null != desktopToolIF && !System.getProperty("os.name").toLowerCase().contains("linux")) {
            desktopToolIF.browse(uri);
        } else {
            try {
                Runtime.getRuntime().exec(new String[]{"xdg-open", uri.toURL().toString()});
            } catch (Exception ex) {
                LanguageSelector ls = LanguageSelector.getInstance();
                String nachricht = ls.getFirstAttrbuteByTagName("javagui.startup.updatefehlernachricht");
                String titel = ls.getFirstAttrbuteByTagName("mainform.caption");

                JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), nachricht, titel, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void open(File toOpen) {
        if (null != desktopToolIF) {
            desktopToolIF.open(toOpen);
        }
    }
}
