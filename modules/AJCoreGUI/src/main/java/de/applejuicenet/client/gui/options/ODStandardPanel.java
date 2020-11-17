/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManager;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.DesktopTools;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.NumberInputVerifier;

import de.tklsoft.gui.controls.TKLCheckBox;
import de.tklsoft.gui.controls.TKLComboBox;
import de.tklsoft.gui.controls.TKLTextField;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODStandardPanel.java,v 1.10 2009/01/14 15:54:31 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class ODStandardPanel extends JPanel implements OptionsRegister
{
   private boolean            dirty                 = false;
   private boolean            xmlPortDirty          = false;
   private JLabel             label1                = new JLabel();
   private JLabel             label2                = new JLabel();
   private JLabel             label3                = new JLabel();
   private JLabel             label4                = new JLabel();
   private JLabel             label6                = new JLabel();
   private JLabel             selectStandardBrowser;
   private JLabel             openTemp;
   private JLabel             openIncoming;
   private TKLTextField       temp                  = new TKLTextField();
   private TKLTextField       incoming              = new TKLTextField();
   private TKLTextField       port                  = new TKLTextField();
   private TKLTextField       xmlPort               = new TKLTextField();
   private TKLTextField       nick                  = new TKLTextField();
   private TKLTextField       browser               = new TKLTextField();
   private JLabel             hint1;
   private JLabel             hint2;
   private JLabel             hint3;
   private JLabel             hint4;
   private JLabel             hint5;
   private JLabel             hint6;
   private JDialog            parent;
   private AJSettings         ajSettings;
   private TKLComboBox        cmbLog;
   private TKLCheckBox        updateNotification = new TKLCheckBox();
   private TKLCheckBox        loadPlugins        = new TKLCheckBox();
   private Logger             logger;
   private ConnectionSettings remote;
   private Icon               menuIcon;
   private String             menuText;

   public ODStandardPanel(JDialog parent, AJSettings ajSettings, ConnectionSettings remote)
   {
      logger = Logger.getLogger(getClass());
      try
      {
         this.remote     = remote;
         this.parent     = parent;
         this.ajSettings = ajSettings;
         init();
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public boolean isDirty()
   {
      return dirty;
   }

   public Level getLogLevel()
   {
      return ((LevelItem) cmbLog.getSelectedItem()).getLevel();
   }

   public boolean getUpdateInfo()
   {
      return updateNotification.isSelected();
   }

   public String getBrowserPfad()
   {
      return browser.getText();
   }

   public boolean shouldLoadPluginsOnStartup()
   {
      return loadPlugins.isSelected();
   }

   private void init() throws Exception
   {
      OptionsManager optionsManager = OptionsManagerImpl.getInstance();
      IconManager    im = IconManager.getInstance();

      menuIcon = im.getIcon("opt_standard");
      port.setDocument(new NumberInputVerifier());
      xmlPort.setDocument(new NumberInputVerifier());
      temp.setEditable(false);
      temp.setBackground(Color.WHITE);
      incoming.setEditable(false);
      incoming.setBackground(Color.WHITE);
      port.addFocusListener(new PortFocusListener());
      xmlPort.addFocusListener(new XmlPortFocusListener());
      nick.addFocusListener(new NickFocusListener());

      JPanel panel8 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      panel8.add(new JLabel("Logging: "));
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      Level            logLevel = optionsManager.getLogLevel();

      LevelItem[]      levelItems = new LevelItem[3]; //{ "Info", "Debug", "keins"};

      levelItems[0] = new LevelItem(Level.INFO, languageSelector.getFirstAttrbuteByTagName("javagui.options.logging.info"));
      levelItems[1] = new LevelItem(Level.DEBUG, languageSelector.getFirstAttrbuteByTagName("javagui.options.logging.debug"));
      levelItems[2] = new LevelItem(Level.OFF, languageSelector.getFirstAttrbuteByTagName("javagui.options.logging.off"));
      menuText      = languageSelector.getFirstAttrbuteByTagName("einstform.standardsheet.caption");
      cmbLog        = new TKLComboBox(levelItems);
      cmbLog.addItemListener(e -> dirty = true);

      int index = 0;

      if(logLevel == Level.INFO)
      {
         index = 0;
      }
      else if(logLevel == Level.DEBUG)
      {
         index = 1;
      }
      else if(logLevel == Level.OFF)
      {
         index = 2;
      }

      cmbLog.setSelectedIndex(index);

      panel8.add(cmbLog);

      updateNotification.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.updateinfotext"));
      updateNotification.setSelected(optionsManager.getUpdateInfo());
      updateNotification.addItemListener(e -> dirty = true);



      setLayout(new BorderLayout());
      port.setHorizontalAlignment(JLabel.RIGHT);
      xmlPort.setHorizontalAlignment(JLabel.RIGHT);
      reloadSettings();
      JPanel panel6 = new JPanel(new GridBagLayout());

      label1.setText(languageSelector.getFirstAttrbuteByTagName("einstform.Label2.caption"));
      label2.setText(languageSelector.getFirstAttrbuteByTagName("einstform.Label7.caption"));
      label3.setText(languageSelector.getFirstAttrbuteByTagName("einstform.Label3.caption"));
      label4.setText(languageSelector.getFirstAttrbuteByTagName("einstform.Label8.caption"));
      label6.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.xmlport"));
      loadPlugins.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.ladeplugins"));

      loadPlugins.setSelected(optionsManager.shouldLoadPluginsOnStartup());

      ImageIcon icon = im.getIcon("hint");

      hint1 = new HintLabel(icon);
      hint2 = new HintLabel(icon);
      hint3 = new HintLabel(icon);
      hint4 = new HintLabel(icon);
      hint5 = new HintLabel(icon);
      hint6 = new HintLabel(icon);
      hint1.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.ttipp_temp"));
      hint2.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.ttipp_port"));
      hint3.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.ttipp_nick"));
      hint4.setToolTipText(languageSelector.getFirstAttrbuteByTagName("einstform.Label1.caption"));
      hint5.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.options.logging.ttip"));
      hint6.setToolTipText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.ttipp_xmlport"));

      Icon                         icon2          = im.getIcon("folderopen");
      DirectoryChooserMouseAdapter dcMouseAdapter = new DirectoryChooserMouseAdapter();

      openTemp = new JLabel(icon2);
      openTemp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      openTemp.addMouseListener(dcMouseAdapter);
      openIncoming = new JLabel(icon2);
      openIncoming.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      openIncoming.addMouseListener(dcMouseAdapter);

      loadPlugins.addChangeListener(e -> dirty = true);

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor     = GridBagConstraints.NORTH;
      constraints.fill       = GridBagConstraints.BOTH;
      constraints.gridx      = 0;
      constraints.gridy      = 0;
      constraints.insets.top = 5;

      JPanel panel1  = new JPanel(new GridBagLayout());
      JPanel panel2  = new JPanel(new GridBagLayout());
      JPanel panel3  = new JPanel(new GridBagLayout());
      JPanel panel4  = new JPanel(new GridBagLayout());
      JPanel panel7  = new JPanel(new GridBagLayout());
      JPanel panel10 = new JPanel(new GridBagLayout());
      JPanel panel9  = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JPanel panel11 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      panel9.add(updateNotification);
      panel11.add(loadPlugins);

      constraints.insets.right = 5;
      constraints.insets.left  = 4;
      panel1.add(label1, constraints);
      panel2.add(label2, constraints);
      panel3.add(label3, constraints);
      panel4.add(label4, constraints);
      panel7.add(label6, constraints);

      constraints.insets.left  = 0;
      constraints.insets.right = 2;
      constraints.gridx        = 1;
      constraints.weightx      = 1;
      panel1.add(temp, constraints);
      panel2.add(incoming, constraints);
      panel3.add(port, constraints);
      panel7.add(xmlPort, constraints);
      panel4.add(nick, constraints);
      constraints.gridx   = 2;
      constraints.weightx = 0;
      panel1.add(openTemp, constraints);
      panel2.add(openIncoming, constraints);

      constraints.gridx   = 0;
      constraints.gridy   = 0;
      constraints.weightx = 1;
      panel6.add(panel1, constraints);
      constraints.gridy = 1;
      panel6.add(panel2, constraints);
      constraints.gridy = 2;
      panel6.add(panel3, constraints);
      constraints.gridy = 3;
      panel6.add(panel7, constraints);
      constraints.gridy = 4;
      panel6.add(panel4, constraints);
      constraints.gridy = 5;
      panel6.add(panel10, constraints);

      constraints.insets.top = 10;
      constraints.gridx      = 1;
      constraints.gridy      = 0;
      constraints.weightx    = 0;
      panel6.add(hint1, constraints);
      constraints.gridy = 2;
      panel6.add(hint2, constraints);
      constraints.gridy = 3;
      panel6.add(hint6, constraints);
      constraints.gridy = 4;
      panel6.add(hint3, constraints);

      constraints.gridy     = 6;
      constraints.gridx     = 0;
      constraints.gridwidth = 1;
      panel6.add(panel8, constraints);
      constraints.gridx = 1;
      panel6.add(hint5, constraints);
      constraints.gridy = 7;
      constraints.gridx = 0;
      constraints.gridwidth = 5;
      panel6.add(panel9, constraints);
      constraints.gridy = 8;
      constraints.gridx = 0;
      panel6.add(panel11, constraints);

      add(panel6, BorderLayout.NORTH);

      temp.confirmNewValue();
      incoming.confirmNewValue();
      port.confirmNewValue();
      xmlPort.confirmNewValue();
      nick.confirmNewValue();
      cmbLog.confirmNewValue();
      updateNotification.confirmNewValue();
      loadPlugins.confirmNewValue();
   }

   public boolean isXmlPortDirty()
   {
      return xmlPortDirty;
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
      AJSettings ajSettings2 = AppleJuiceClient.getAjFassade().getAJSettings();

      temp.setText(ajSettings2.getTempDir());
      incoming.setText(ajSettings2.getIncomingDir());
      port.setText(Long.toString(ajSettings2.getPort()));
      xmlPort.setText(Long.toString(ajSettings2.getXMLPort()));
      nick.setText(ajSettings2.getNick());
   }

   class PortFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         int portNr = Integer.parseInt(port.getText());

         if(ajSettings.getPort() != portNr)
         {
            if(portNr > 1024 && portNr <= 32000)
            {
               dirty = true;
               ajSettings.setPort(Integer.parseInt(port.getText()));
            }
            else
            {
               port.setText(Long.toString(ajSettings.getPort()));
            }
         }
      }
   }


   class NickFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         if(ajSettings.getNick().compareTo(nick.getText()) != 0)
         {
            dirty = true;
            ajSettings.setNick(nick.getText());
         }
      }
   }


   class XmlPortFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         int xmlPortNr = Integer.parseInt(xmlPort.getText());

         if(ajSettings.getXMLPort() != Long.parseLong(xmlPort.getText()))
         {
            if(xmlPortNr > 1024 && xmlPortNr <= 32000)
            {
               dirty        = true;
               xmlPortDirty = true;
               remote.setXmlPort(Integer.parseInt(xmlPort.getText()));
               ajSettings.setXMLPort(Long.parseLong(xmlPort.getText()));
            }
            else
            {
               xmlPort.setText(Long.toString(ajSettings.getXMLPort()));
            }
         }
      }
   }


   class HintLabel extends JLabel
   {
      HintLabel(Icon image)
      {
         super(image);
      }

      public JToolTip createToolTip()
      {
         MultiLineToolTip tip = new MultiLineToolTip();

         tip.setComponent(this);
         return tip;
      }
   }

   class DirectoryChooserMouseAdapter extends MouseAdapter
   {
      public void mouseEntered(MouseEvent e)
      {
         JLabel source = (JLabel) e.getSource();

         source.setBorder(BorderFactory.createLineBorder(Color.black));
      }

      public void mouseClicked(MouseEvent e)
      {
         JLabel source = (JLabel) e.getSource();
         String title = "";

         if(source == openTemp)
         {
            title = label1.getText();
         }
         else
         {
            title = label2.getText();
         }

         ODDirectoryChooser chooser = new ODDirectoryChooser(parent, title);

         chooser.setLocation(parent.getLocation());
         chooser.setVisible(true);
         if(chooser.isNewPathSelected())
         {
            dirty = true;
            String path = chooser.getSelectedPath();

            if(source == openTemp)
            {
               temp.setText(path);
               temp.fireCheckRules();
               ajSettings.setTempDir(path);
            }
            else
            {
               incoming.setText(path);
               incoming.fireCheckRules();
               ajSettings.setIncomingDir(path);
            }
         }
      }

      public void mouseExited(MouseEvent e)
      {
         JLabel source = (JLabel) e.getSource();

         source.setBorder(null);
      }
   }


   class LevelItem
   {
      private Level  level;
      private String bezeichnung;

      public LevelItem(Level level, String bezeichnung)
      {
         this.level       = level;
         this.bezeichnung = bezeichnung;
      }

      public Level getLevel()
      {
         return level;
      }

      public String toString()
      {
         return bezeichnung;
      }
   }


   class UpdateInfoItem
   {
      private int    modus;
      private String bezeichnung;

      public UpdateInfoItem(int modus, String bezeichnung)
      {
         this.modus       = modus;
         this.bezeichnung = bezeichnung;
      }

      public int getModus()
      {
         return modus;
      }

      public String toString()
      {
         return bezeichnung;
      }
   }
}
