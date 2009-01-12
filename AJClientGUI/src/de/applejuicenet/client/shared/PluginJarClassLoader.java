/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Constructor;

import java.net.MalformedURLException;
import java.net.URL;

import java.security.SecureClassLoader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.plugins.PluginConnector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/PluginJarClassLoader.java,v 1.31 2009/01/12 10:33:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r aj@tkl-soft.de>
 *
 */
public class PluginJarClassLoader extends SecureClassLoader
{
   private static Logger           logger              = Logger.getLogger(PluginJarClassLoader.class);
   private Properties              pluginProperties    = null;
   private ImageIcon               pluginIcon          = null;
   private Map<String, Properties> languageFiles       = new HashMap<String, Properties>();
   private Map<String, ImageIcon>  availableIcons      = new HashMap<String, ImageIcon>();
   private Map<String, File>       availableIcons2File = new HashMap<String, File>();

   public PluginJarClassLoader()
   {
      super();
   }

   @SuppressWarnings("unchecked")
   public PluginConnector getPlugin(File pluginJar) throws Exception
   {
      pluginProperties = null;
      pluginIcon       = null;
      languageFiles.clear();

      try
      {
         loadClassBytesFromJar(pluginJar);
         String          theClassName      = pluginProperties.getProperty("general.classname");
         Class           cl                = loadClass(theClassName);
         Class[]         constructorHelper = {Properties.class, Map.class, ImageIcon.class, Map.class};
         Constructor     con               = cl.getConstructor(constructorHelper);
         PluginConnector aPlugin           = (PluginConnector) con.newInstance(new Object[]
                                                                               {
                                                                                  pluginProperties, languageFiles, pluginIcon,
                                                                                  availableIcons
                                                                               });

         return (PluginConnector) aPlugin;
      }
      catch(Throwable e)
      {
         if(logger.isEnabledFor(Level.INFO))
         {
            logger.info("Plugin " + pluginJar.getName() + " entspricht nicht dem Standard und wurde nicht geladen.", e);
         }

         return null;
      }
   }

   @SuppressWarnings("unchecked")
   private void loadClassBytesFromJar(File jar) throws Exception
   {
      if(!jar.isFile())
      {
         return;
      }

      JarFile                 jf        = new JarFile(jar);
      String                  entryName;
      HashMap<String, byte[]> lazyLoad = new HashMap<String, byte[]>();

      for(Enumeration e = jf.entries(); e.hasMoreElements();)
      {
         ZipEntry entry = (ZipEntry) e.nextElement();

         entryName = entry.getName();
         if(entryName.indexOf("$") == -1)
         {
            continue;
         }

         String name = entryName.replace('/', '.');

         name = name.replaceAll(".class", "");
         byte[] buf = readEntry(jf, entry);

         lazyLoad.put(name, buf);
      }

      for(Enumeration e = jf.entries(); e.hasMoreElements();)
      {
         ZipEntry entry = (ZipEntry) e.nextElement();

         entryName = entry.getName();
         if(entryName.indexOf(".class") == -1 && !entryName.equals("plugin.properties") && !entryName.endsWith(".gif") &&
               !entryName.endsWith(".png") && !entryName.startsWith("language_") && !entryName.endsWith(".jar") &&
               entryName.indexOf("$") == -1)
         {
            continue;
         }

         byte[] buf = readEntry(jf, entry);

         if(entryName.equals("plugin.properties"))
         {
            InputStream iS = jf.getInputStream(entry);

            pluginProperties = new Properties();
            pluginProperties.load(iS);
         }
         else if(entryName.indexOf("icon.gif") != -1)
         {
            pluginIcon = new ImageIcon(buf);
            availableIcons.put(entryName.substring(0, entryName.length() - 4), pluginIcon);
            availableIcons2File.put(entryName, jar);
         }
         else if(entryName.endsWith(".gif") || entryName.endsWith(".png"))
         {
            ImageIcon icon = new ImageIcon(buf);

            availableIcons.put(entryName.substring(0, entryName.length() - 4), icon);
            availableIcons2File.put(entryName, jar);
         }
         else if(entryName.startsWith("language_") && entryName.endsWith(".properties"))
         {
            InputStream iS = jf.getInputStream(entry);

            Properties  curLanguageProperties = new Properties();

            curLanguageProperties.load(iS);

            String sprache = curLanguageProperties.getProperty("language");

            languageFiles.put(sprache, curLanguageProperties);
         }
         else if(entryName.endsWith(".jar"))
         {
            File             aFile = File.createTempFile("ajg", null);
            FileOutputStream os = new FileOutputStream(aFile);

            os.write(buf);
            os.close();
            loadClassBytesFromJar(aFile);
            aFile.delete();
         }
         else
         {
            String name = entryName.replace('/', '.');

            name = name.replaceAll(".class", "");
            defineMyClass(buf, name, jf);
            String key = name + "$";

            if(lazyLoad.containsKey(key + "1"))
            {
               int i = 1;

               while(true)
               {
                  byte[] tmp = lazyLoad.get(key + i);

                  if(null == tmp)
                  {
                     break;
                  }

                  defineMyClass(tmp, key + i, jf);
                  i++;
               }
            }
         }
      }

      lazyLoad.clear();
   }

   private byte[] readEntry(JarFile jf, ZipEntry entry)
                     throws IOException
   {
      InputStream is   = jf.getInputStream(entry);
      int         l    = (int) entry.getSize();
      byte[]      buf  = new byte[l];
      int         read = 0;

      while(read < l)
      {
         int incr = is.read(buf, read, l - read);

         read += incr;
      }

      return buf;
   }

   @SuppressWarnings("unchecked")
   private void defineMyClass(byte[] buf, String name, JarFile jarFile)
                       throws IOException
   {
      try
      {
         Class clazz = findLoadedClass(name);

         if(null != clazz)
         {
            return;
         }

         while(true)
         {
            try
            {
               clazz = defineClass(name, buf, 0, buf.length);
               break;
            }
            catch(NoClassDefFoundError clfE)
            {

               // rekursiv probieren
               String   className = clfE.getMessage();
               ZipEntry entry    = jarFile.getEntry(className + ".class");
               byte[]   innerBuf = readEntry(jarFile, entry);

               className = className.replace('/', '.');
               defineMyClass(innerBuf, className, jarFile);
            }
         }

         resolveClass(clazz);
      }
      catch(LinkageError lE)
      {
         logger.debug("Mist im Plugin", lE);

         //Klasse wurde aus irgendeinem Grund bereits geladen
      }
   }

   @Override
   public URL getResource(String name)
   {
      File entry = availableIcons2File.get(name);

      if(null == entry)
      {
         return null;
      }

      URL url = null;

      try
      {
         url = new URL("jar:file:" + entry.getAbsolutePath() + "!/" + name);
      }
      catch(MalformedURLException e)
      {

         // bloed, aber nicht soooo schlimm
         ;
      }

      return url;
   }
}
