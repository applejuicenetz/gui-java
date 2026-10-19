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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Copyright: General Public License
 */
public class OptionsDialog extends JDialog
{
   private static final Logger logger = LoggerFactory.getLogger(OptionsDialog.class);
   private JFrame             parent;
   private JButton            saveButton;
   private JButton            cancel;
   private AJSettings         ajSettings;
   private ConnectionSettings remote;
   private JTabbedPane        tabs;
   private ODStandardPanel    standardPanel;
   private ODCoreConnectionPanel  coreConnectionPanel;
   private ODConnectionPanel  connectionPanel;
   private ODProxyPanel       proxyPanel;
   private ODViewPanel     viewPanel;
   private ODPluginPanel      pluginPanel;
   private OptionsRegister[]  optionPanels;


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

      connectionPanel.setBorder(BorderFactory.createTitledBorder(
            languageSelector.getFirstAttributeByTagName("einstform.pwsheet.caption")));
      JPanel coreContent = new JPanel(new BorderLayout(0, 16));
      Component fields = standardPanel.getComponent(0);
      standardPanel.remove(fields);
      coreContent.add(fields, BorderLayout.NORTH);
      coreContent.add(connectionPanel, BorderLayout.SOUTH);
      standardPanel.add(coreContent, BorderLayout.NORTH);
      optionPanels = new OptionsRegister[] {standardPanel, coreConnectionPanel, connectionPanel,
            viewPanel, proxyPanel, pluginPanel};
      tabs = createTabs(new OptionsRegister[] {viewPanel, standardPanel,
            coreConnectionPanel, proxyPanel, pluginPanel});
      Dimension contentSize = tabs.getPreferredSize();
      // Approximate the previous sidebar width when reducing the complete dialog to 70 percent.
      tabs.setPreferredSize(compactSize(new Dimension(contentSize.width + 180, contentSize.height)));
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
      getContentPane().add(tabs, BorderLayout.CENTER);
      getContentPane().add(panel, BorderLayout.SOUTH);
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

   static JTabbedPane createTabs(OptionsRegister[] panels)
   {
      JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
      for(OptionsRegister panel : panels)
      {
         JPanel content = new JPanel(new BorderLayout());
         content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
         content.add((JPanel) panel, BorderLayout.CENTER);
         if(panel instanceof ODPluginPanel)
         {
            tabs.addTab(panel.getMenuText(), panel.getIcon(), content);
         }
         else
         {
            JScrollPane scroll = new JScrollPane(content);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            tabs.addTab(panel.getMenuText(), panel.getIcon(), scroll);
         }
      }
      return tabs;
   }

   static Dimension compactSize(Dimension previous)
   {
      return new Dimension(Math.max(560, (int) Math.ceil(previous.width * 0.7 / 10) * 10),
            (int) Math.ceil((previous.height * 0.77 + 90) / 10) * 10);
   }
}
