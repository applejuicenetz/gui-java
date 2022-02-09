/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugincontrol;

import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.shared.PluginJarClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class PluginFactory {
    private static Set<PluginConnector> plugins = null;
    private static final Logger logger = LoggerFactory.getLogger(PluginFactory.class);

    public static Set<PluginConnector> getPlugins() {
        if (null == plugins) {
            boolean isDebugPlugins = System.getProperty("Plugins") != null;

            if (isDebugPlugins) {
                plugins = loadPluginsFromClasspath();
                return plugins;
            }

            plugins = new HashSet<>();

            Map<String, File> tempListe = new HashMap<>();

            String shippedPath = System.getProperty("user.dir") + File.separator + "plugins" + File.separator;
            String userPath = System.getProperty("user.home") + File.separator + "appleJuice" + File.separator + "gui" + File.separator + "plugins" + File.separator;

            File userPathFile = new File(userPath);
            File shippedPathFile = new File(shippedPath);

            File[] userPathFiles = userPathFile.listFiles();
            if (userPathFiles != null) {
                for (File pathFile : userPathFiles) {
                    if (pathFile.isFile() && pathFile.getName().endsWith(".jar")) {
                        tempListe.put(pathFile.getName(), pathFile);
                    }
                }
            }

            File[] shippedPathFiles = shippedPathFile.listFiles();
            if (shippedPathFiles != null) {
                for (File pathFile : shippedPathFiles) {
                    if (pathFile.isFile() && pathFile.getName().endsWith(".jar")) {
                        if (!tempListe.containsKey(pathFile.getName())) {
                            tempListe.put(pathFile.getName(), pathFile);
                        }
                    }
                }
            }

            PluginJarClassLoader jarLoader;

            for (Map.Entry<String, File> pluginFile : tempListe.entrySet()) {
                try {
                    ZipFile jf = new ZipFile(pluginFile.getValue());
                    ZipEntry entry = jf.getEntry("plugin.properties");

                    if (entry == null) {
                        continue;
                    }

                    jarLoader = new PluginJarClassLoader();
                    PluginConnector aPlugin = jarLoader.getPlugin(pluginFile.getValue());

                    if (aPlugin != null) {
                        plugins.add(aPlugin);
                        logger.info(String.format("Plugin %s (%s) geladen ...", aPlugin.getTitle(), aPlugin.getVersion()));
                    }
                } catch (Exception e) {
                    //Von einem Plugin lassen wir uns nicht beirren! ;-)
                    logger.error("Ein Plugin konnte nicht instanziert werden", e);
                }
            }
        }

        return plugins;
    }

    private static Set<PluginConnector> loadPluginsFromClasspath() {
        Set<PluginConnector> thePlugins = new HashSet<PluginConnector>();
        String[] which = new String[]{ //"de.applejuicenet.client.gui.plugins.jabber.JabberTestLoader",//
//                                                                       "de.applejuicenet.client.gui.plugins.serverwatcher.ServerWatcherTestLoader"
//                                                                       "de.applejuicenet.client.gui.plugins.logviewer.LogViewerTestLoader"
                "de.applejuicenet.client.gui.plugins.speedgraph.SpeedGraphPluginTestLoader"
        };

        for (String curWhich : which) {
            PluginConnector plugin = loadPlugin(curWhich);

            if (null != plugin) {
                thePlugins.add(plugin);
                String nachricht = "Plugin " + plugin.getTitle() + " geladen...";

                logger.info(nachricht);
            }
        }

        return thePlugins;
    }

    private static PluginConnector loadPlugin(String which) {
        try {
            Class pluginClass = Class.forName(which);
            TestLoader testLoader = (TestLoader) pluginClass.newInstance();
            PluginConnector pluginConnector = testLoader.getPlugin();

            return pluginConnector;
        } catch (Throwable e) {
            //Von einem Plugin lassen wir uns nicht beirren! ;-)
            logger.error("Ein Plugin konnte nicht instanziert werden", e);
        }

        return null;
    }
}
