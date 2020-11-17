/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugincontrol;

import java.io.File;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.shared.PluginJarClassLoader;

public abstract class PluginFactory
{
   private static Set<PluginConnector> plugins = null;
   private static Logger               logger = Logger.getLogger(PluginFactory.class);

   public static Set<PluginConnector> getPlugins()
   {
      if(null == plugins)
      {
         boolean isDebugPlugins = System.getProperty("Plugins") != null;

         if(isDebugPlugins)
         {
            plugins = loadPluginsFromClasspath();
            return plugins;
         }

         plugins = new HashSet<PluginConnector>();

         String path;

         if(System.getProperty("os.name").toLowerCase().indexOf("windows") == -1)
         {
            path = System.getProperty("user.home") + File.separator + "appleJuice" + File.separator + "gui" + File.separator +
                   "plugins" + File.separator;
         }
         else
         {
            path = System.getProperty("user.dir") + File.separator + "plugins" + File.separator;
         }

         File pluginPath = new File(path);

         String[]             tempListe = pluginPath.list();
         PluginJarClassLoader jarLoader = null;

         for(int i = 0; i < Objects.requireNonNull(tempListe).length; i++)
         {
            if(tempListe[i].toLowerCase().endsWith(".jar"))
            {
               try
               {
                  File pluginFile = new File(path + tempListe[i]);

                  if(pluginFile.isFile())
                  {
                     ZipFile  jf    = new ZipFile(pluginFile);
                     ZipEntry entry = jf.getEntry("plugin.properties");

                     if(entry == null)
                     {
                        continue;
                     }
                  }
                  else
                  {
                     continue;
                  }

                  jarLoader = new PluginJarClassLoader();
                  PluginConnector aPlugin = jarLoader.getPlugin(new File(path + tempListe[i]));

                  if(aPlugin != null)
                  {
                     plugins.add(aPlugin);
                     String nachricht = "Plugin " + aPlugin.getTitle() + " geladen...";

                     if(logger.isEnabledFor(Level.INFO))
                     {
                        logger.info(nachricht);
                     }
                  }
               }
               catch(Exception e)
               {
                  //Von einem Plugin lassen wir uns nicht beirren! ;-)
                  logger.error("Ein Plugin konnte nicht instanziert werden", e);
                  continue;
               }
            }
         }
      }

      return plugins;
   }

   private static Set<PluginConnector> loadPluginsFromClasspath()
   {
      Set<PluginConnector> thePlugins = new HashSet<PluginConnector>();
      String[]             which = new String[] { //"de.applejuicenet.client.gui.plugins.jabber.JabberTestLoader",//
//              "de.applejuicenet.client.gui.plugins.versionchecker.VersioncheckerTestLoader"
//                                                                       "de.applejuicenet.client.gui.plugins.serverwatcher.ServerWatcherTestLoader"
//                                                                       "de.applejuicenet.client.gui.plugins.logviewer.LogViewerTestLoader"
              "de.applejuicenet.client.gui.plugins.speedgraph.SpeedGraphPluginTestLoader"
//         "de.applejuicenet.client.gui.plugins.ircplugin.IrcPluginTestLoader"
              };

      for(String curWhich : which)
      {
         PluginConnector plugin = loadPlugin(curWhich);

         if(null != plugin)
         {
            thePlugins.add(plugin);
            String nachricht = "Plugin " + plugin.getTitle() + " geladen...";

            if(logger.isEnabledFor(Level.INFO))
            {
               logger.info(nachricht);
            }
         }
      }

      return thePlugins;
   }

   @SuppressWarnings("unchecked")
   private static PluginConnector loadPlugin(String which)
   {
      try
      {
         Class           pluginClass     = Class.forName(which);
         TestLoader      testLoader      = (TestLoader) pluginClass.newInstance();
         PluginConnector pluginConnector = testLoader.getPlugin();

         return pluginConnector;
      }
      catch(Throwable e)
      {
         //Von einem Plugin lassen wir uns nicht beirren! ;-)
         logger.error("Ein Plugin konnte nicht instanziert werden", e);
      }

      return null;
   }
}
