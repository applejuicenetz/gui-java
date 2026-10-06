/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.SoundPlayer;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/OptionsDialog.java,v 1.9 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class OptionsDialog extends JDialog
{
   private static final Logger logger = LoggerFactory.getLogger(OptionsDialog.class);
   private JFrame             parent;
   private JButton            speichern;
   private JButton            abbrechen;
   private AJSettings         ajSettings;
   private ConnectionSettings remote;
   private JList<Object>          menuList;
   private ODStandardPanel    standardPanel;
   private ODVerbindungPanel  verbindungPanel;
   private ODConnectionPanel  connectionPanel;
   private ODAllgemeinPanel   allgemeinPanel;
   private ODProxyPanel       proxyPanel;
   private ODAnsichtPanel     ansichtPanel;
   private ODPluginPanel      pluginPanel;
   private OptionsRegister[]  optionPanels;
   private CardLayout         registerLayout = new CardLayout();
   private JPanel             registerPanel  = new JPanel(registerLayout);

   public OptionsDialog(JFrame parent) throws HeadlessException
   {
      super(parent, true);
      try
      {
         this.parent = parent;
         ajSettings  = AppleJuiceClient.getAjFassade().getAJSettings();
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private void init() throws Exception
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      remote = OptionsManagerImpl.getInstance().getRemoteSettings();

      setTitle(languageSelector.getFirstAttrbuteByTagName("einstform.caption"));
      standardPanel   = new ODStandardPanel(this, ajSettings, remote);
      verbindungPanel = new ODVerbindungPanel(this, ajSettings);
      connectionPanel = new ODConnectionPanel(remote, null);
      allgemeinPanel  = new ODAllgemeinPanel();
      proxyPanel      = new ODProxyPanel();
      ansichtPanel    = new ODAnsichtPanel();
      pluginPanel     = new ODPluginPanel(this);

      // Abschnitt 1: Einstellungen, die an den Core gehen
      OptionsRegister[] coreSection = {standardPanel, verbindungPanel, connectionPanel};
      // Abschnitt 2: Einstellungen, die nur das JavaGUI betreffen
      OptionsRegister[] guiSection = {allgemeinPanel, ansichtPanel, proxyPanel, pluginPanel};

      DefaultListModel<Object> model = new DefaultListModel<>();

      model.addElement(new SectionHeader(languageSelector.getFirstAttrbuteByTagName("javagui.options.section.core")));
      for(OptionsRegister r : coreSection)
      {
         model.addElement(r);
      }

      model.addElement(new SectionHeader(languageSelector.getFirstAttrbuteByTagName("javagui.options.section.gui")));
      for(OptionsRegister r : guiSection)
      {
         model.addElement(r);
      }

      optionPanels = new OptionsRegister[coreSection.length + guiSection.length];
      System.arraycopy(coreSection, 0, optionPanels, 0, coreSection.length);
      System.arraycopy(guiSection, 0, optionPanels, coreSection.length, guiSection.length);

      menuList = new JList<>(model);
      menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      menuList.setCellRenderer(new MenuListCellRenderer());
      for(OptionsRegister r : optionPanels)
      {
         registerPanel.add(r.getMenuText(), (JPanel) r);
      }

      menuList.addListSelectionListener(listSelectionEvent -> {
         Object selected = menuList.getSelectedValue();

         if(selected instanceof OptionsRegister)
         {
            registerLayout.show(registerPanel, ((OptionsRegister) selected).getMenuText());
         }
      });
      // Ueberschriften nicht auswaehlbar
      menuList.setSelectionModel(new DefaultListSelectionModel()
         {
            @Override
            public void setSelectionInterval(int index0, int index1)
            {
               if(index0 >= 0 && model.get(index0) instanceof OptionsRegister)
               {
                  super.setSelectionInterval(index0, index1);
               }
            }
         });
      menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      menuList.setSelectedValue(standardPanel, true);
      speichern = new JButton(languageSelector.getFirstAttrbuteByTagName("einstform.Button1.caption"));
      abbrechen = new JButton(languageSelector.getFirstAttrbuteByTagName("einstform.Button2.caption"));
      abbrechen.addActionListener(e -> dispose());
      speichern.addActionListener(e -> speichern());

      JPanel     panel = new JPanel();
      FlowLayout flowL = new FlowLayout();

      flowL.setAlignment(FlowLayout.RIGHT);
      panel.setLayout(flowL);
      panel.add(speichern);
      panel.add(abbrechen);
      getContentPane().add(registerPanel, BorderLayout.CENTER);
      getContentPane().add(panel, BorderLayout.SOUTH);
      getContentPane().add(new JScrollPane(menuList), BorderLayout.WEST);
      pack();
      setResizable(false);

   }

   private void speichern()
   {
      try
      {
         OptionsManager om             = OptionsManagerImpl.getInstance();
         boolean        etwasGeaendert = false;

         // JavaGUI-Einstellungen
         if(ansichtPanel.isDirty())
         {
            ansichtPanel.save();
            etwasGeaendert = true;
         }

         if(allgemeinPanel.isDirty())
         {
            om.loadPluginsOnStartup(allgemeinPanel.shouldLoadPluginsOnStartup());
            om.setUpdateInfo(allgemeinPanel.getUpdateInfo());
            om.setLogLevel(allgemeinPanel.getLogLevel());
            etwasGeaendert = true;
         }

         if(proxyPanel.isDirty())
         {
            ProxyManagerImpl.getInstance().saveProxySettings(proxyPanel.getProxySettings());
            etwasGeaendert = true;
         }

         // Core-Einstellungen
         if(standardPanel.isDirty() || verbindungPanel.isDirty())
         {
            om.saveAJSettings(ajSettings);
            etwasGeaendert = true;
         }

         if(connectionPanel.isDirty() || standardPanel.isXmlPortDirty())
         {
            try
            {
               om.saveRemote(remote);
               etwasGeaendert = true;
            }
            catch(InvalidPasswordException ex)
            {
               LanguageSelector languageSelector = LanguageSelector.getInstance();
               String           titel = languageSelector.getFirstAttrbuteByTagName("javagui.eingabefehler");
               String           nachricht = languageSelector.getFirstAttrbuteByTagName("javagui.options.remote.fehlertext");

               JOptionPane.showMessageDialog(parent, nachricht, titel, JOptionPane.OK_OPTION);
            }
         }

         if(etwasGeaendert)
         {
            SoundPlayer.getInstance().playSound(SoundPlayer.GESPEICHERT);
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }

      dispose();
   }

   public void reloadSettings()
   {
      for(int i = 0; i < optionPanels.length; i++)
      {
         ((OptionsRegister) optionPanels[i]).reloadSettings();
      }
   }

   record SectionHeader(String text)
   {
   }

   class MenuListCellRenderer implements ListCellRenderer<Object>
   {
      private final JLabel label = new JLabel();

      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
         label.setOpaque(true);
         label.setFont(list.getFont());
         label.setEnabled(list.isEnabled());
         if(value instanceof SectionHeader header)
         {
            label.setText(header.text());
            label.setIcon(null);
            label.setFont(list.getFont().deriveFont(Font.BOLD));
            label.setBackground(UIManager.getColor("Panel.background"));
            label.setForeground(UIManager.getColor("Label.disabledForeground"));
            label.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")),
                  BorderFactory.createEmptyBorder(6, 4, 2, 4)));
            return label;
         }

         OptionsRegister r = (OptionsRegister) value;

         label.setText(r.getMenuText() + "   ");
         label.setIcon(r.getIcon());
         label.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 0));
         label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
         label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
         return label;
      }
   }
}
