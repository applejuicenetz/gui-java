/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODPluginOptionsDialog.java,v 1.6 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class ODPluginOptionsDialog extends JDialog {
    private PluginConnector pluginConnector;
    private final JButton schliessen = new JButton();
    private Logger logger;

    public ODPluginOptionsDialog(JDialog parent, PluginConnector pluginConnector) {
        super(parent, true);
        try {
            this.pluginConnector = pluginConnector;
            logger = LoggerFactory.getLogger(getClass());
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        schliessen.addActionListener(ae -> ODPluginOptionsDialog.this.dispose());

        String title = pluginConnector.getTitle() + " - ";

        title += languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.einstellungen");
        schliessen.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.schliessen"));
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pluginConnector.getOptionPanel(), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        southPanel.add(schliessen);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        pack();
        Dimension appDimension = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
    }
}
