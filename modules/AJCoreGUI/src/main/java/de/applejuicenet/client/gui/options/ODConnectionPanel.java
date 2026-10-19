/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.gui.connect.QuickConnectionSettingsDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberInputVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Copyright: General Public License
 */
public class ODConnectionPanel extends JPanel implements OptionsRegister
{
   private boolean                       dirty                         = false;
   private JLabel                        label1;
   private JLabel                        label3;
   private JLabel                        label4;
   private JTextField                  host                          = new JTextField();
   private JTextField                  port                          = new JTextField();
   private JPasswordField                passwordNew                   = new JPasswordField();
   private ConnectionSettings            remote;
   private boolean                       showPort                      = false;
   private QuickConnectionSettingsDialog quickConnectionSettingsDialog;
   private Icon                          menuIcon;
   private String                        menuText;

   public ODConnectionPanel(ConnectionSettings remote, QuickConnectionSettingsDialog quickConnectionSettingsDialog, boolean showPort)
   {
      Logger logger = LoggerFactory.getLogger(getClass());
      try
      {
         this.showPort                      = showPort;
         this.quickConnectionSettingsDialog = quickConnectionSettingsDialog;
         this.remote                        = remote;
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
      }
   }

   public ODConnectionPanel(ConnectionSettings remote, QuickConnectionSettingsDialog quickConnectionSettingsDialog)
   {
      this(remote, quickConnectionSettingsDialog, false);
   }

   private void init() {
      setLayout(new BorderLayout());
      IconManager im = IconManager.getInstance();

      menuIcon = im.getIcon("opt_passwort");
      JPanel     panel1 = new JPanel(new GridBagLayout());
      FlowLayout flowL = new FlowLayout();

      flowL.setAlignment(FlowLayout.RIGHT);
      JPanel           panel2 = new JPanel(flowL);

      LanguageSelector languageSelector = LanguageSelector.getInstance();

      label1 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.remote.host"));
      label3 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.remote.newpassword"));
      menuText = languageSelector.getFirstAttributeByTagName("einstform.pwsheet.caption");
      label4   = new JLabel(GuiText.text("javagui.options.remote.port"));

      host.setText(remote.getHost());
      host.addFocusListener(new HostFocusListener());
      passwordNew.addFocusListener(new PasswordNewFocusListener());

      KeyAdapter keyAdapter = new ShortcutKeyListener();

      if(quickConnectionSettingsDialog != null)
      {
         host.addKeyListener(keyAdapter);
         port.addKeyListener(keyAdapter);
         passwordNew.addKeyListener(keyAdapter);
      }

      port.setDocument(new NumberInputVerifier());
      port.setText(Integer.toString(remote.getXmlPort()));
      port.addFocusListener(new PortFocusListener());

      enableControls(true);

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor      = GridBagConstraints.NORTH;
      constraints.fill        = GridBagConstraints.BOTH;
      constraints.gridx       = 0;
      constraints.gridy       = 0;
      constraints.insets.top  = 5;
      constraints.insets.left = 5;

      if(quickConnectionSettingsDialog != null) panel1.add(label1, constraints);

      int gridy = quickConnectionSettingsDialog != null ? 1 : 0;

      if(showPort)
      {
         constraints.gridy = gridy;
         gridy++;
         panel1.add(label4, constraints);
      }

      constraints.gridy = gridy;
      gridy++;
      if(quickConnectionSettingsDialog == null)
      {
         JPanel passwordLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
         passwordLabel.add(label3);
         JLabel hint = new JLabel(im.getIcon("hint")) {
            @Override public JToolTip createToolTip() {
               JToolTip tip = new de.applejuicenet.client.shared.MultiLineToolTip();
               tip.setComponent(this);
               return tip;
            }
         };
         hint.setToolTipText(languageSelector.getFirstAttributeByTagName("javagui.options.remote.passwordhint"));
         passwordLabel.add(Box.createHorizontalStrut(5));
         passwordLabel.add(hint);
         panel1.add(passwordLabel, constraints);
      }
      else panel1.add(label3, constraints);

      constraints.insets.right = 5;
      constraints.gridy        = 0;
      constraints.gridx        = 1;
      constraints.weightx      = 1;
      if(quickConnectionSettingsDialog != null) panel1.add(host, constraints);

      gridy = quickConnectionSettingsDialog != null ? 1 : 0;
      if(showPort)
      {
         constraints.gridy = gridy;
         gridy++;
         panel1.add(port, constraints);
      }

      constraints.gridy = gridy;
      gridy++;
      panel1.add(passwordNew, constraints);

      constraints.gridy = gridy;
      gridy++;
      constraints.gridx     = 0;
      constraints.gridwidth = 2;
      panel1.add(panel2, constraints);

      add(panel1, BorderLayout.NORTH);
      if(quickConnectionSettingsDialog != null)
      {
         label3.setText(languageSelector.getFirstAttributeByTagName("einstform.pwsheet.caption"));
      }
   }

   public void setFocusOnPassword()
   {
      this.passwordNew.requestFocus();
   }

   public boolean isDirty()
   {
      return dirty;
   }

   public void enableControls(boolean enable)
   {
      host.setEnabled(enable);
      passwordNew.setEnabled(enable);
      label1.setEnabled(enable);
      label3.setEnabled(enable);
   }

   public Icon getIcon()
   {
      return menuIcon;
   }

   public String getMenuText()
   {
      return menuText;
   }

   public void setHost(String host)
   {
      this.host.setText(host);
      this.remote.setHost(host);
   }

   public void setXMLPort(String port)
   {
      this.port.setText(port);
      this.remote.setXmlPort(Integer.parseInt(port));
   }

   public void reloadSettings()
   {

      // nothing to do...
   }

   public String getPassword()
   {
      return new String(passwordNew.getPassword());
   }

   public String getHost()
   {
      return host.getText();
   }

   public Integer getPort()
   {
      return Integer.valueOf(port.getText());
   }

   class PortFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         dirty = true;
         remote.setXmlPort(Integer.parseInt(port.getText()));
      }
   }


   class ShortcutKeyListener extends KeyAdapter
   {
      public void keyPressed(KeyEvent ke)
      {
         if(ke.getKeyCode() == KeyEvent.VK_ENTER)
         {
            dirty = true;
            remote.setHost(host.getText());
            remote.setXmlPort(Integer.parseInt(port.getText()));
            remote.setNewPassword(new String(passwordNew.getPassword()));
            quickConnectionSettingsDialog.pressOK();
         }
         else if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
         {
            quickConnectionSettingsDialog.pressCancel();
         }
         else
         {
            super.keyPressed(ke);
         }
      }
   }


   class PasswordNewFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         dirty = true;
         remote.setNewPassword(new String(passwordNew.getPassword()));
      }
   }


   class HostFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         if(remote.getHost().compareTo(host.getText()) != 0)
         {
            dirty = true;
            remote.setHost(host.getText());
         }
      }
   }
}
