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
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODPluginPanel.java,v 1.8 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class ODPluginPanel extends JPanel implements OptionsRegister
{
   private JList            pluginList;
   private JTextArea        description            = new JTextArea();
   private JLabel           label1                  = new JLabel();
   private AppleJuiceDialog theApp;
   private JButton          settings           = new JButton();
   private String           name;
   private String           version;
   private String           author;
   private String           explanation;
   private Logger logger;
   private PluginConnector  selectedPluginConnector = null;
   private JDialog          parentDialog;
   private Icon             menuIcon;
   private String           menuText;

   public ODPluginPanel(JDialog parent)
   {
      logger = LoggerFactory.getLogger(getClass());
      try
      {
         theApp       = AppleJuiceDialog.getApp();
         parentDialog = parent;
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

      settings.setVisible(false);
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
      pluginList = new JList(v);
      pluginList.setPreferredSize(new Dimension(190, pluginList.getPreferredSize().height));
      pluginList.addListSelectionListener(e -> pluginList_valueChanged(e));
      setLayout(new BorderLayout());
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      label1.setText(languageSelector.getFirstAttributeByTagName("einstform.Label11.caption") + ":");
      name    = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.name");
      version = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.version");
      explanation = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.description");
      author = languageSelector.getFirstAttributeByTagName("javagui.options.plugins.author");
      settings.setText(languageSelector.getFirstAttributeByTagName("javagui.options.plugins.settings"));
      menuText = languageSelector.getFirstAttributeByTagName("einstform.TabSheet1.caption");

      add(label1, BorderLayout.NORTH);
      add(pluginList, BorderLayout.WEST);
      JPanel panel1 = new JPanel(new BorderLayout());
      JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      panel2.add(settings);
      JScrollPane sp = new JScrollPane(description);

      panel1.add(sp, BorderLayout.CENTER);
      panel1.add(panel2, BorderLayout.SOUTH);
      add(panel1, BorderLayout.CENTER);
      settings.addActionListener(ae -> {
         if(selectedPluginConnector != null)
         {
            ODPluginOptionsDialog pluginOptionsDialog = new ODPluginOptionsDialog(parentDialog, selectedPluginConnector);

            pluginOptionsDialog.setResizable(true);
            pluginOptionsDialog.setVisible(true);
         }
      });
   }

   void pluginList_valueChanged(ListSelectionEvent e)
   {
      PluginContainer selected = (PluginContainer) ((JList) e.getSource()).getSelectedValue();

      description.setText(selected.getDescription());
      if(selected.getPluginOptionPanel() == null)
      {
         settings.setVisible(false);
      }
      else
      {
         selectedPluginConnector = selected.getPlugin();
         settings.setVisible(true);
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

         text = name + ":\r\n" + plugin.getTitle() + "\r\n\r\n" + author + ":\r\n" + plugin.getAuthor() + " [" + plugin.getContact() +
                "]" + "\r\n\r\n" + version + ":\r\n" + plugin.getVersion() + "\r\n\r\n" + explanation + ":\r\n" +
                plugin.getDescription();
         return text;
      }

      public JPanel getPluginOptionPanel()
      {
         return plugin.getOptionPanel();
      }
   }
}
