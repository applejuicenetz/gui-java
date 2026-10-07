/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.connect;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.options.ODConnectionPanel;
import de.applejuicenet.client.shared.ConnectionSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/connect/QuickConnectionSettingsDialog.java,v 1.8 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class QuickConnectionSettingsDialog extends JDialog
{
   public static final int      ABGEBROCHEN = 1;
   private ODConnectionPanel    remotePanel;
   private ConnectionSettings   remote;
   private ConnectionSettings[] connectionSet;
   private JButton              ok                 = new JButton("OK");
   private JButton              abbrechen          = GuiText.button("javagui.quickconnect.cancel");
   private JCheckBox            cmbNieWiederZeigen = new JCheckBox();
   private JComboBox            connectionListe    = new JComboBox();
   private Logger logger;
   private boolean              dirty              = false;
   private int                  result = 0;

   private boolean switchConnection;

   public QuickConnectionSettingsDialog(Frame parent)
   {
      this(parent, false);
   }

   public QuickConnectionSettingsDialog(Frame parent, boolean switchConnection)
   {
      super(parent, true);
      this.switchConnection = switchConnection;
      logger = LoggerFactory.getLogger(getClass());
      try
      {
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private void init()
   {
      ConnectionSettings saved = OptionsManagerImpl.getInstance().getRemoteSettings();
      remote = new ConnectionSettings(saved.getHost(), saved.getOldPassword(), saved.getXmlPort());
      connectionSet = OptionsManagerImpl.getInstance().getConnectionsSet();
      for(int i = 0; i < connectionSet.length; i++)
      {
         this.connectionListe.addItem(connectionSet[i]);
         if(remote.getHost() != null && remote.getHost().equals(connectionSet[i].getHost()))
         {
            this.connectionListe.setSelectedItem(connectionSet[i]);
         }
      }

      remotePanel = new ODConnectionPanel(remote, this, true);
      setTitle("appleJuice Client");

      getContentPane().setLayout(new BorderLayout());

      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String           nachricht = languageSelector.getFirstAttrbuteByTagName("javagui.startup.ueberpruefeEinst");

      cmbNieWiederZeigen.setText(languageSelector.getFirstAttrbuteByTagName("javagui.startup.showdialog"));
      cmbNieWiederZeigen.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent ce)
            {
               dirty = true;
            }
         });

      connectionListe.addItemListener(new ItemListener()
         {
            public void itemStateChanged(ItemEvent itemEvent)
            {
               selChanged(itemEvent);
            }
         });

      cmbNieWiederZeigen.addKeyListener(new KeyAdapter()
         {
            public void keyPressed(KeyEvent ke)
            {
               if(ke.getKeyCode() == KeyEvent.VK_ENTER)
               {
                  pressOK();
               }
               else if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
               {
                  pressAbbrechen();
               }
               else
               {
                  super.keyPressed(ke);
               }
            }
         });
      connectionListe.addKeyListener(new KeyAdapter()
         {
            public void keyPressed(KeyEvent ke)
            {
               if(ke.getKeyCode() == KeyEvent.VK_ENTER)
               {
                  pressOK();
               }
               else if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
               {
                  pressAbbrechen();
               }
               else
               {
                  super.keyPressed(ke);
               }
            }
         });

      JPanel             panel2      = new JPanel(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor        = GridBagConstraints.NORTH;
      constraints.fill          = GridBagConstraints.BOTH;
      constraints.gridx         = 0;
      constraints.gridy         = 0;
      constraints.insets.top    = 5;
      constraints.insets.left   = 5;
      constraints.insets.bottom = 5;

      panel2.add(new JLabel(nachricht), constraints);
      constraints.gridx        = 1;
      constraints.weightx      = 1;
      constraints.insets.right = 5;
      panel2.add(new JLabel("           "), constraints);

      constraints.gridx     = 0;
      constraints.gridy     = 1;
      constraints.gridwidth = 2;
      panel2.add(connectionListe, constraints);

      getContentPane().add(panel2, BorderLayout.NORTH);
      getContentPane().add(remotePanel, BorderLayout.CENTER);

      JPanel panel3 = new JPanel(new BorderLayout());
      JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      panel1.add(ok);
      panel1.add(abbrechen);

      JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      cmbNieWiederZeigen.setVisible(!switchConnection);
      panel4.add(cmbNieWiederZeigen);
      panel3.add(panel4, BorderLayout.NORTH);
      panel3.add(panel1, BorderLayout.SOUTH);
      getContentPane().add(panel3, BorderLayout.SOUTH);

      ok.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               if(switchConnection)
               {
                  switchCore();
               }
               else
               {
                  speichereEinstellungen();
                  result = 0;
                  setVisible(false);
               }
            }
         });

      abbrechen.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               if(!ok.isEnabled()) return;
               result = ABGEBROCHEN;
               setVisible(false);
            }
         });
      addWindowListener(new WindowAdapter()
         {
            public void windowClosing(WindowEvent evt)
            {
               if(!ok.isEnabled()) return;
               result = ABGEBROCHEN;
               setVisible(false);
            }
         });
      pack();
      setResizable(false);
      DialogLocation.center(this);
      remotePanel.setFocusOnPassword();
   }

   public void pressOK()
   {
      ok.doClick();
   }

   public void setNieWiederAnzeigen()
   {
      cmbNieWiederZeigen.setSelected(true);
      dirty = true;
   }

   public void pressAbbrechen()
   {
      abbrechen.doClick();
   }

   public int getResult()
   {
      return result;
   }

   private void selChanged(ItemEvent itemEvent)
   {
      ConnectionSettings con = (ConnectionSettings) itemEvent.getItem();

      remotePanel.setHost(con.getHost());
      remotePanel.setXMLPort(Integer.toString(con.getXmlPort()));
      remotePanel.revalidate();
      remotePanel.repaint();
   }

   private void switchCore()
   {
      final String host = remotePanel.getHost().trim();
      final Integer port;
      final String password = remotePanel.getPassword();
      try
      {
         port = remotePanel.getPort();
         new CoreConnectionSettingsHolder(host, port, password, true);
      }
      catch(Exception e)
      {
         JOptionPane.showMessageDialog(this, GuiText.text("javagui.startup.fehlversuch"), getTitle(), JOptionPane.ERROR_MESSAGE);
         return;
      }
      ok.setEnabled(false);
      abbrechen.setEnabled(false);
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      new SwingWorker<Integer, Void>()
      {
         protected Integer doInBackground() throws Exception
         {
            CoreConnectionSettingsHolder candidate = new CoreConnectionSettingsHolder(host, port, password, true);
            ApplejuiceFassade probe = new ApplejuiceFassade(candidate);
            int available = probe.isCoreAvailable();
            candidate.removeListener(probe);
            return available;
         }

         protected void done()
         {
            try
            {
               int available = get();
               if(available != 0)
               {
                  JOptionPane.showMessageDialog(QuickConnectionSettingsDialog.this,
                        GuiText.text(available == 1 ? "mainform.msgdlgtext3" : "javagui.startup.fehlversuch"),
                        getTitle(), JOptionPane.ERROR_MESSAGE);
                  return;
               }
               remote.setHost(host);
               remote.setXmlPort(port);
               remote.setNewPassword(password);
               remote.setOldMD5Password(remote.getNewPassword());
               OptionsManagerImpl.getInstance().setConnectionsSet(getNeueConfigs(remote));
               OptionsManagerImpl.getInstance().onlySaveRemote(remote);
               AppleJuiceClient.restartGui();
               result = 0;
               setVisible(false);
            }
            catch(Exception e)
            {
               logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
               JOptionPane.showMessageDialog(QuickConnectionSettingsDialog.this,
                     GuiText.text("javagui.startup.fehlversuch"), getTitle(), JOptionPane.ERROR_MESSAGE);
            }
            finally
            {
               ok.setEnabled(true);
               abbrechen.setEnabled(true);
               setDefaultCloseOperation(HIDE_ON_CLOSE);
            }
         }
      }.execute();
   }

   private void speichereEinstellungen()
   {
      try
      {
         CoreConnectionSettingsHolder ajConn = AppleJuiceClient.getCoreConnectionSettingsHolder();

         if(ajConn != null)
         {
            ajConn.setCoreHost(remotePanel.getHost());
            ajConn.setCorePort(remotePanel.getPort());
            ajConn.setCorePassword(remotePanel.getPassword(), true);
         }

         OptionsManagerImpl.getInstance().setConnectionsSet(getNeueConfigs(remote));
         OptionsManagerImpl.getInstance().onlySaveRemote(remote);
         if(dirty)
         {
            OptionsManagerImpl.getInstance().showConnectionDialogOnStartup(!cmbNieWiederZeigen.isSelected());
         }
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   private ConnectionSettings[] getNeueConfigs(ConnectionSettings remote)
   {
      for(int i = 0; i < this.connectionSet.length; i++)
      {
         if(remote.getHost().equals(connectionSet[i].getHost()) && (remote.getXmlPort() == connectionSet[i].getXmlPort()))
         {
            connectionSet[i] = remote;
            return connectionSet;
         }
      }

      ArrayList<ConnectionSettings> targets2Save = new ArrayList<ConnectionSettings>();

      targets2Save.add(remote);
      for(ConnectionSettings curVal : connectionSet)
      {
         targets2Save.add(curVal);
      }

      return targets2Save.toArray(new ConnectionSettings[] {});
   }
}
