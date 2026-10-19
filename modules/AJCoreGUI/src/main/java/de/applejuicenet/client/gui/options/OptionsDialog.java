/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.shared.AJSettings;
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
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class OptionsDialog extends JDialog
{
   private static final Logger logger = LoggerFactory.getLogger(OptionsDialog.class);
   private JFrame             parent;
   private JButton            saveButton;
   private JButton            cancel;
   private AJSettings         ajSettings;
   private ConnectionSettings remote;
   private JList<Object>          menuList;
   private ODStandardPanel    standardPanel;
   private ODCoreConnectionPanel  coreConnectionPanel;
   private ODConnectionPanel  connectionPanel;
   private ODProxyPanel       proxyPanel;
   private ODViewPanel     viewPanel;
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
         ajSettings  = AppleJuiceClient.getAjFacade().getAJSettings();
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
      }
   }

   private void init() throws Exception
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      remote = OptionsManagerImpl.getInstance().getRemoteSettings();

      setTitle(languageSelector.getFirstAttributeByTagName("einstform.caption"));
      standardPanel   = new ODStandardPanel(this, ajSettings, remote);
      coreConnectionPanel = new ODCoreConnectionPanel(this, ajSettings);
      connectionPanel = new ODConnectionPanel(remote, null);
      proxyPanel      = new ODProxyPanel();
      viewPanel    = new ODViewPanel();
      pluginPanel     = new ODPluginPanel(this);

      // Section 1: settings that are sent to the core
      OptionsRegister[] coreSection = {standardPanel, coreConnectionPanel, connectionPanel};
      // Section 2: settings that only affect the JavaGUI
      OptionsRegister[] guiSection = {viewPanel, proxyPanel, pluginPanel};

      DefaultListModel<Object> model = new DefaultListModel<>();

      model.addElement(new SectionHeader(languageSelector.getFirstAttributeByTagName("javagui.options.section.core")));
      for(OptionsRegister r : coreSection)
      {
         model.addElement(r);
      }

      model.addElement(new SectionHeader(languageSelector.getFirstAttributeByTagName("javagui.options.section.gui")));
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
      // headings are not selectable
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
      saveButton = new JButton(languageSelector.getFirstAttributeByTagName("einstform.Button1.caption"));
      cancel = new JButton(languageSelector.getFirstAttributeByTagName("einstform.Button2.caption"));
      cancel.addActionListener(e -> dispose());
      saveButton.addActionListener(e -> saveButton());

      JPanel     panel = new JPanel();
      FlowLayout flowL = new FlowLayout();

      flowL.setAlignment(FlowLayout.RIGHT);
      panel.setLayout(flowL);
      panel.add(saveButton);
      panel.add(cancel);
      getContentPane().add(registerPanel, BorderLayout.CENTER);
      getContentPane().add(panel, BorderLayout.SOUTH);
      getContentPane().add(new JScrollPane(menuList), BorderLayout.WEST);
      pack();
      setResizable(false);

   }

   private void saveButton()
   {
      try
      {
         OptionsManager om             = OptionsManagerImpl.getInstance();
         boolean        somethingChanged = false;

         // JavaGUI settings
         if(viewPanel.isDirty())
         {
            viewPanel.save();
            somethingChanged = true;
         }

         if(proxyPanel.isDirty())
         {
            ProxyManagerImpl.getInstance().saveProxySettings(proxyPanel.getProxySettings());
            somethingChanged = true;
         }

         // Capture Swing state before dispatching the ordered core saves.
         boolean saveCore = standardPanel.isDirty() || coreConnectionPanel.isDirty();
         boolean saveRemote = connectionPanel.isDirty() || standardPanel.isXmlPortDirty();
         if(saveCore || saveRemote)
         {
            new javax.swing.SwingWorker<Void, Void>()
            {
               protected Void doInBackground() throws Exception
               {
                  if(saveCore) om.saveAJSettings(ajSettings);
                  if(saveRemote) om.saveRemote(remote);
                  return null;
               }

               protected void done()
               {
                  try
                  {
                     get();
                     SoundPlayer.getInstance().playSound(SoundPlayer.SAVED);
                     dispose();
                  }
                  catch(Exception error)
                  {
                     logger.error("Saving core settings failed", error);
                     saveButton.setEnabled(true);
                     LanguageSelector texts = LanguageSelector.getInstance();
                     JOptionPane.showMessageDialog(parent,
                           texts.getFirstAttributeByTagName("javagui.options.remote.errortext"),
                           texts.getFirstAttributeByTagName("javagui.inputerror"), JOptionPane.ERROR_MESSAGE);
                  }
               }
            }.execute();
            saveButton.setEnabled(false);
            return;
         }

         if(somethingChanged)
         {
            SoundPlayer.getInstance().playSound(SoundPlayer.SAVED);
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
      }

      dispose();
   }

   public void reloadSettings()
   {
      for(int i = 0; i < optionPanels.length; i++)
      {
         optionPanels[i].reloadSettings();
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
