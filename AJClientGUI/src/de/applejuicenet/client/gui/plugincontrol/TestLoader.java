package de.applejuicenet.client.gui.plugincontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public abstract class TestLoader
{
   private String path = System.getProperty("user.dir") + File.separator + "plugin_src" + File.separator + getPath();

   public TestLoader()
   {
   }

   protected abstract String getPath();

   protected abstract PluginConnector getPlugin(XMLValueHolder pluginsPropertiesXMLHolder,
      Map<String, XMLValueHolder> languageFiles, ImageIcon icon, Map<String, ImageIcon> availableIcons);

   public final PluginConnector getPlugin()
   {
      PluginConnector plugin = getPlugin(new MyXMLValueHolder("plugin_properties.xml"), getLanguageXmls(), getIcon(),
            getAvailableIcons());

      return plugin;
   }

   private Map<String, XMLValueHolder> getLanguageXmls()
   {
      Map<String, XMLValueHolder> languageXMLs = new HashMap<String, XMLValueHolder>();
      String[]                    filenames = new File(path).list(new FilenameFilter()
            {
               public boolean accept(File dir, String name)
               {
                  return name.indexOf("language_xml_") != -1;
               }
            });

      if(null != filenames)
      {
         for(String curFilename : filenames)
         {
            MyXMLValueHolder languageFile = new MyXMLValueHolder(curFilename);
            String           sprache = languageFile.getXMLAttributeByTagName("language.value");

            languageXMLs.put(sprache, languageFile);
         }
      }

      return languageXMLs;
   }

   private ImageIcon getIcon()
   {
      ImageIcon icon = new ImageIcon(path + File.separator + "icons" + File.separator + "icon.gif");

      return icon;
   }

   private Map<String, ImageIcon> getAvailableIcons()
   {
      Map<String, ImageIcon> availableIcons = new HashMap<String, ImageIcon>();
      File                   aFile = new File(path + File.separator + "icons" + File.separator);
      String[]               names = aFile.list(new FilenameFilter()
            {
               public boolean accept(File dir, String name)
               {
                  return name.endsWith(".gif") || name.endsWith(".png");
               }
            });

      for(String curName : names)
      {
         String key = curName.substring(0, curName.length() - 4);

         availableIcons.put(key, new ImageIcon(path + File.separator + "icons" + File.separator + curName));
      }

      return availableIcons;
   }

   private class MyXMLValueHolder extends XMLValueHolder
   {
      public MyXMLValueHolder(String fileName)
      {
         super();

         try
         {
            String         fullPath = path + File.separator + fileName;
            BufferedReader reader;

            reader = new BufferedReader(new FileReader(fullPath));
            String       line = "";
            StringBuffer read = new StringBuffer();

            while(null != (line = reader.readLine()))
            {
               read.append(line);
            }

            reader.close();
            parse(read.toString());
         }
         catch(FileNotFoundException e)
         {
            throw new RuntimeException(e);
         }
         catch(IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
}
