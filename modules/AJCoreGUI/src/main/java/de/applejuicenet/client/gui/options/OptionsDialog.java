/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import org.apache.log4j.Logger;

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

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/OptionsDialog.java,v 1.9 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class OptionsDialog extends JDialog
{
   private static final Logger logger = Logger.getLogger(OptionsDialog.class);
   private JFrame             parent;
   private JButton            speichern;
   private JButton            abbrechen;
   private AJSettings         ajSettings;
   private ConnectionSettings remote;
   private JList              menuList;
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
      optionPanels = new OptionsRegister[]
                     {
                        new ODStandardPanel(this, ajSettings, remote), new ODVerbindungPanel(this, ajSettings),
                        new ODConnectionPanel(remote, null), new ODProxyPanel(), new ODAnsichtPanel(), new ODPluginPanel(this)
                     };

      menuList = new JList(optionPanels);
      menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      menuList.setCellRenderer(new MenuListCellRenderer());
      for(int i = 0; i < optionPanels.length; i++)
      {
         registerPanel.add(optionPanels[i].getMenuText(), (JPanel) optionPanels[i]);
      }

      menuList.addListSelectionListener(listSelectionEvent -> {
         Object selected = menuList.getSelectedValue();

         registerLayout.show(registerPanel, ((OptionsRegister) selected).getMenuText());
      });
      menuList.setSelectedValue(optionPanels[0], true);
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

   }

   private void speichern()
   {
      try
      {
         OptionsManager om             = OptionsManagerImpl.getInstance();
         boolean        etwasGeaendert = false;

         if(((ODAnsichtPanel) optionPanels[4]).isDirty())
         {
            etwasGeaendert = ((ODAnsichtPanel) optionPanels[4]).save();
            etwasGeaendert = true;
         }

         if(((ODStandardPanel) optionPanels[0]).isDirty() || ((ODVerbindungPanel) optionPanels[1]).isDirty())
         {
            om.saveAJSettings(ajSettings);
            om.loadPluginsOnStartup(((ODStandardPanel) optionPanels[0]).shouldLoadPluginsOnStartup());
            om.setUpdateInfo(((ODStandardPanel) optionPanels[0]).getUpdateInfo());
            if(((ODStandardPanel) optionPanels[0]).isDirty())
            {
               om.setLogLevel(((ODStandardPanel) optionPanels[0]).getLogLevel());
            }

            etwasGeaendert = true;
         }

         if(((ODConnectionPanel) optionPanels[2]).isDirty() || ((ODStandardPanel) optionPanels[0]).isXmlPortDirty())
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

         if(((ODProxyPanel) optionPanels[3]).isDirty())
         {
            ProxyManagerImpl.getInstance().saveProxySettings(((ODProxyPanel) optionPanels[3]).getProxySettings());
            etwasGeaendert = true;
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

   class MenuListCellRenderer extends JLabel implements ListCellRenderer
   {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
         setText(((OptionsRegister) value).getMenuText() + "   ");
         setIcon(((OptionsRegister) value).getIcon());
         if(isSelected)
         {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
         }
         else
         {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
         }

         setEnabled(list.isEnabled());
         setFont(list.getFont());
         setOpaque(true);
         return this;
      }

      public Dimension getPreferredSize()
      {
         Dimension size = super.getPreferredSize();

         return new Dimension(size.width, size.height * 2);
      }
   }
}
