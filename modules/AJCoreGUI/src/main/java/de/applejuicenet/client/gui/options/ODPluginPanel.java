/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.plugincontrol.PluginFactory;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Set;
import java.util.Vector;

/**
 * Copyright: General Public License
 */
public class ODPluginPanel extends JPanel implements OptionsRegister
{
   private JList<PluginContainer> pluginList;
   private JTextArea        description            = new JTextArea();
   private JLabel           label1                  = new JLabel();
   private AppleJuiceDialog theApp;
   private String           name;
   private String           version;
   private String           author;
   private String           explanation;
   private Logger logger;
   private Icon             menuIcon;
   private String           menuText;

   public ODPluginPanel(JDialog parent)
   {
      logger = LoggerFactory.getLogger(getClass());
      try
      {
         theApp       = AppleJuiceDialog.getApp();
         init();
      }
      catch(Exception e)
      {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
      }
   }

   private void init() {
      IconManager im = IconManager.getInstance();

      menuIcon = im.getIcon("opt_plugins");
      Set<PluginConnector> plugins = PluginFactory.getPlugins();

      Vector<PluginContainer> v = new Vector<PluginContainer>();

      for(PluginConnector curPlugin : plugins)
      {
         v.add(new PluginContainer(curPlugin));
      }

      Dimension parentSize = theApp.getSize();

      description.setBackground(label1.getBackground());
      description.setPreferredSize(new Dimension(parentSize.width / 3, description.getPreferredSize().height));
      description.setEditable(false);
      description.setLineWrap(true);
      description.setWrapStyleWord(true);
      pluginList = new JList<>(v);
      pluginList.setPreferredSize(new Dimension(190, pluginList.getPreferredSize().height));
      pluginList.addListSelectionListener(e -> pluginList_valueChanged(e));
      setLayout(new BorderLayout());
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      label1.setText(languageSelector.getFirstAttributeByTagName("einstform.Label11.caption") + ":");
      name    = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.name");
      version = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.version");
      explanation = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.description");
      author = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.author");
      menuText = languageSelector.getFirstAttributeByTagName("einstform.TabSheet1.caption");

      add(label1, BorderLayout.NORTH);
      add(pluginList, BorderLayout.WEST);
      add(new JScrollPane(description), BorderLayout.CENTER);
   }

   void pluginList_valueChanged(ListSelectionEvent e)
   {
      PluginContainer selected = (PluginContainer) ((JList) e.getSource()).getSelectedValue();

      if(selected != null)
      {
         description.setText(selected.getDescription());
      }
   }

   public Icon getIcon()
   {
      return menuIcon;
   }

   public String getMenuText()
   {
      return menuText;
   }

   public void reloadSettings()
   {

      // nothing to do...
   }

   class PluginContainer
   {
      private PluginConnector plugin;

      public PluginContainer(PluginConnector plugin)
      {
         this.plugin = plugin;
      }

      public String toString()
      {
         return plugin.getTitle();
      }

      public PluginConnector getPlugin()
      {
         return plugin;
      }

      public String getDescription()
      {
         String text;

         text = name + ":\r\n" + plugin.getTitle() + "\r\n\r\n" + author + ":\r\n" + plugin.getAuthor() + "\r\n\r\n" + version + ":\r\n" + plugin.getVersion() + "\r\n\r\n" + explanation + ":\r\n" +
                plugin.getDescription();
         return text;
      }
   }
}
