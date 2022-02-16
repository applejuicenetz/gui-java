/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins;

import de.applejuicenet.client.gui.plugincontrol.TestLoader;

import javax.swing.*;
import java.util.Map;
import java.util.Properties;

public class SpeedGraphPluginTestLoader extends TestLoader {
    @Override
    protected String getPath() {
        return "speedgraph";
    }

    @Override
    protected PluginConnector getPlugin(Properties pluginProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                                        Map<String, ImageIcon> availableIcons) {
        return new SpeedGraphPlugin(pluginProperties, languageFiles, icon, availableIcons);
    }
}
