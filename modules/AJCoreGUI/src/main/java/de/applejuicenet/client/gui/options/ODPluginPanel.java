/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
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
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class ODPluginPanel extends JPanel implements OptionsRegister
{
   private JList            pluginList;
   private JTextArea        beschreibung            = new JTextArea();
   private JLabel           label1                  = new JLabel();
   private AppleJuiceDialog theApp;
   private JButton          einstellungen           = new JButton();
   private String           name;
   private String           version;
   private String           autor;
   private String           erlaeuterung;
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
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private void init() {
      IconManager im = IconManager.getInstance();

      menuIcon = im.getIcon("opt_plugins");
      Set<PluginConnector> plugins = PluginFactory.getPlugins();

      einstellungen.setVisible(false);
      Vector<PluginContainer> v = new Vector<PluginContainer>();

      for(PluginConnector curPlugin : plugins)
      {
         v.add(new PluginContainer(curPlugin));
      }

      Dimension parentSize = theApp.getSize();

      beschreibung.setBackground(label1.getBackground());
      beschreibung.setPreferredSize(new Dimension(parentSize.width / 3, beschreibung.getPreferredSize().height));
      beschreibung.setEditable(false);
      beschreibung.setLineWrap(true);
      beschreibung.setWrapStyleWord(true);
      pluginList = new JList(v);
      pluginList.setPreferredSize(new Dimension(190, pluginList.getPreferredSize().height));
      pluginList.addListSelectionListener(e -> pluginList_valueChanged(e));
      setLayout(new BorderLayout());
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      label1.setText(languageSelector.getFirstAttrbuteByTagName("einstform.Label11.caption") + ":");
      name    = languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.name");
      version = languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.version");
      erlaeuterung = languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.beschreibung");
      autor = languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.autor");
      einstellungen.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.plugins.einstellungen"));
      menuText = languageSelector.getFirstAttrbuteByTagName("einstform.TabSheet1.caption");

      add(label1, BorderLayout.NORTH);
      add(pluginList, BorderLayout.WEST);
      JPanel panel1 = new JPanel(new BorderLayout());
      JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      panel2.add(einstellungen);
      JScrollPane sp = new JScrollPane(beschreibung);

      panel1.add(sp, BorderLayout.CENTER);
      panel1.add(panel2, BorderLayout.SOUTH);
      add(panel1, BorderLayout.CENTER);
      einstellungen.addActionListener(ae -> {
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

      beschreibung.setText(selected.getBeschreibung());
      if(selected.getPluginOptionPanel() == null)
      {
         einstellungen.setVisible(false);
      }
      else
      {
         selectedPluginConnector = selected.getPlugin();
         einstellungen.setVisible(true);
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

      public String getBeschreibung()
      {
         String text;

         text = name + ":\r\n" + plugin.getTitle() + "\r\n\r\n" + autor + ":\r\n" + plugin.getAutor() + " [" + plugin.getContact() +
                "]" + "\r\n\r\n" + version + ":\r\n" + plugin.getVersion() + "\r\n\r\n" + erlaeuterung + ":\r\n" +
                plugin.getBeschreibung();
         return text;
      }

      public JPanel getPluginOptionPanel()
      {
         return plugin.getOptionPanel();
      }
   }
}
