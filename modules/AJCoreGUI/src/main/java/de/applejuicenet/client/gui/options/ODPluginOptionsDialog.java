/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODPluginOptionsDialog.java,v 1.6 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class ODPluginOptionsDialog extends JDialog {
    private PluginConnector pluginConnector;
    private final JButton close = new JButton();
    private Logger logger;

    public ODPluginOptionsDialog(JDialog parent, PluginConnector pluginConnector) {
        super(parent, true);
        try {
            this.pluginConnector = pluginConnector;
            logger = LoggerFactory.getLogger(getClass());
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        close.addActionListener(ae -> ODPluginOptionsDialog.this.dispose());

        String title = pluginConnector.getTitle() + " - ";

        title += languageSelector.getFirstAttributeByTagName("javagui.options.plugins.settings");
        close.setText(languageSelector.getFirstAttributeByTagName("javagui.options.plugins.close"));
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pluginConnector.getOptionPanel(), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        southPanel.add(close);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        pack();
        DialogLocation.center(this);
    }
}
