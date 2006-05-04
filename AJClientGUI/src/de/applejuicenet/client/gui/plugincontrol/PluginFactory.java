package de.applejuicenet.client.gui.plugincontrol;

import java.io.File;

import java.util.HashSet;
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

         File delFile = new File(path + "ajIrcPlugin_1_3.jar");

         if(delFile.isFile())
         {
            delFile.delete();
         }

         delFile = new File(path + "ajIrcPlugin_1_31.jar");
         if(delFile.isFile())
         {
            delFile.delete();
         }

         delFile = new File(path + "IrcPlugin_1_31.jar");
         if(delFile.isFile())
         {
            delFile.delete();
         }

         File pluginPath = new File(path);

         if(!pluginPath.isDirectory())
         {
            pluginPath.mkdir();
            return plugins;
         }

         String[]             tempListe = pluginPath.list();
         PluginJarClassLoader jarLoader = null;

         for(int i = 0; i < tempListe.length; i++)
         {
            if(tempListe[i].toLowerCase().endsWith(".jar"))
            {
               try
               {
                  File pluginFile = new File(path + tempListe[i]);

                  if(pluginFile.isFile())
                  {
                     ZipFile  jf = new ZipFile(pluginFile);
                     ZipEntry entry = jf.getEntry("plugin_properties.xml");

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
}
