/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugincontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

   protected abstract PluginConnector getPlugin(Properties properties, Map<String, Properties> languageFiles, ImageIcon icon,
                                                Map<String, ImageIcon> availableIcons);

   public final PluginConnector getPlugin() throws IOException
   {
      Properties      props = new Properties();
      FileInputStream fiS = new FileInputStream(path + File.separator + "plugin.properties");

      props.load(fiS);
      fiS.close();
      PluginConnector plugin = getPlugin(props, getLanguageFiles(), getIcon(), getAvailableIcons());

      return plugin;
   }

   private Map<String, Properties> getLanguageFiles() throws IOException
   {
      Map<String, Properties> languageProperties = new HashMap<String, Properties>();
      String[]                filenames = new File(path).list((dir, name) -> name.startsWith("language_") && name.endsWith(".properties"));

      if(null != filenames)
      {
         Properties      props;
         FileInputStream fiS;
         String          sprache;

         for(String curFilename : filenames)
         {
            props = new Properties();
            fiS   = new FileInputStream(path + File.separator + curFilename);

            props.load(fiS);
            fiS.close();
            sprache = props.getProperty("language");

            languageProperties.put(sprache, props);
         }
      }

      return languageProperties;
   }

   private ImageIcon getIcon()
   {
      ImageIcon icon = new ImageIcon(path + File.separator + "icons" + File.separator + "icon.png");

      return icon;
   }

   private Map<String, ImageIcon> getAvailableIcons()
   {
      Map<String, ImageIcon> availableIcons = new HashMap<String, ImageIcon>();
      File                   aFile = new File(path + File.separator + "icons" + File.separator);
      String[]               names = aFile.list((dir, name) -> name.endsWith(".png"));

      assert names != null;
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
            String       line;
            StringBuilder read = new StringBuilder();

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
