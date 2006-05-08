package de.applejuicenet.client.shared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Constructor;

import java.security.SecureClassLoader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.PluginConnector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/PluginJarClassLoader.java,v 1.27 2006/05/08 16:08:38 maj0r Exp $
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
   private static Logger               logger = Logger.getLogger(PluginJarClassLoader.class);
   private XMLValueHolder              pluginsPropertiesXMLHolder = null;
   private ImageIcon                   pluginIcon = null;
   private Map<String, XMLValueHolder> languageXMLs = new HashMap<String, XMLValueHolder>();
   private Map<String, ImageIcon>      availableIcons = new HashMap<String, ImageIcon>();

   public PluginJarClassLoader()
   {
      super();
   }

   public PluginConnector getPlugin(File pluginJar) throws Exception
   {
      pluginsPropertiesXMLHolder = null;
      pluginIcon = null;
      languageXMLs.clear();

      try
      {
         loadClassBytesFromJar(pluginJar);
         String          theClassName = pluginsPropertiesXMLHolder.getXMLAttributeByTagName(".root.general.classname.value");
         Class           cl = loadClass(theClassName);
         Class[]         constructorHelper = {XMLValueHolder.class, Map.class, ImageIcon.class, Map.class};
         Constructor     con = cl.getConstructor(constructorHelper);
         PluginConnector aPlugin = (PluginConnector) con.newInstance(new Object[]
               {
                  pluginsPropertiesXMLHolder, languageXMLs, pluginIcon, availableIcons
               });

         return (PluginConnector) aPlugin;
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.INFO))
         {
            logger.info("Plugin " + pluginJar.getName() + " entspricht nicht dem Standard und wurde nicht geladen.", e);
         }

         return null;
      }
   }

   private void loadClassBytesFromJar(File jar) throws Exception
   {
      if(!jar.isFile())
      {
         return;
      }

      JarFile                 jf = new JarFile(jar);
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
         if(entryName.indexOf(".class") == -1 && !entryName.equals("plugin_properties.xml") && !entryName.endsWith(".gif") &&
               !entryName.endsWith(".png") && entryName.indexOf("language_xml_") == -1 && !entryName.endsWith(".jar") &&
               entryName.indexOf("$") == -1)
         {
            continue;
         }

         byte[] buf = readEntry(jf, entry);

         if(entryName.equals("plugin_properties.xml"))
         {
            String xmlString = new String(buf, 0, buf.length);

            pluginsPropertiesXMLHolder = new XMLValueHolder();
            pluginsPropertiesXMLHolder.parse(xmlString);
         }
         else if(entryName.indexOf("icon.gif") != -1)
         {
            pluginIcon = new ImageIcon(buf);
            availableIcons.put(entryName.substring(0, entryName.length() - 4), pluginIcon);
         }
         else if(entryName.endsWith(".gif") || entryName.endsWith(".png"))
         {
            ImageIcon icon = new ImageIcon(buf);

            availableIcons.put(entryName.substring(0, entryName.length() - 4), icon);
         }
         else if(entryName.indexOf("language_xml_") != -1)
         {
            String         xmlString = new String(buf, 0, buf.length);
            XMLValueHolder languageFile = new XMLValueHolder();

            languageFile.parse(xmlString);
            String sprache = languageFile.getXMLAttributeByTagName(".root.language.value");

            languageXMLs.put(sprache, languageFile);
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
      InputStream is = jf.getInputStream(entry);
      int         l = (int) entry.getSize();
      byte[]      buf = new byte[l];
      int         read = 0;

      while(read < l)
      {
         int incr = is.read(buf, read, l - read);

         read += incr;
      }

      return buf;
   }

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
               ZipEntry entry = jarFile.getEntry(className + ".class");
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
}
