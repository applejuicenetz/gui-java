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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
   private JLabel             label7                = new JLabel();
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
   private TKLComboBox        updateInfoModus;
   private TKLCheckBox        loadPlugins           = new TKLCheckBox();
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

   public int getVersionsinfoModus()
   {
      if(updateInfoModus.getSelectedIndex() == -1)
      {
         return 1;
      }
      else
      {
         UpdateInfoItem selectedItem = (UpdateInfoItem) updateInfoModus.getSelectedItem();

         return selectedItem.getModus();
      }
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
      browser.setEditable(false);
      browser.setBackground(Color.WHITE);
      browser.setText(optionsManager.getStandardBrowser());
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
      cmbLog.addItemListener(new ItemListener()
         {
            public void itemStateChanged(ItemEvent e)
            {
               dirty = true;
            }
         });

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

      updateInfoModus = new TKLComboBox();
      UpdateInfoItem item0 = new UpdateInfoItem(0,
                                                languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.updateinfo0"));
      UpdateInfoItem item1 = new UpdateInfoItem(1,
                                                languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.updateinfo1"));
      UpdateInfoItem item2 = new UpdateInfoItem(2,
                                                languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.updateinfo2"));

      updateInfoModus.addItem(item0);
      updateInfoModus.addItem(item1);
      updateInfoModus.addItem(item2);
      int infoModus = optionsManager.getVersionsinfoModus();

      switch(infoModus)
      {

         case 0:
         {
            updateInfoModus.setSelectedItem(item0);
            break;
         }

         case 1:
         {
            updateInfoModus.setSelectedItem(item1);
            break;
         }

         case 2:
         {
            updateInfoModus.setSelectedItem(item2);
            break;
         }

         default:
            updateInfoModus.setSelectedIndex(-1);
      }

      updateInfoModus.addItemListener(new ItemListener()
         {
            public void itemStateChanged(ItemEvent e)
            {
               dirty = true;
            }
         });

      JPanel panel9  = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      JLabel label10 = new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.updateinfotext"));

      panel9.add(label10);
      panel9.add(updateInfoModus);

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
      label7.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.standard.standardbrowser"));
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
      selectStandardBrowser = new JLabel(icon2);
      selectStandardBrowser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      selectStandardBrowser.addMouseListener(new SelectBrowserMouseListener());

      loadPlugins.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               dirty = true;
            }
         });

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor     = GridBagConstraints.NORTH;
      constraints.fill       = GridBagConstraints.BOTH;
      constraints.gridx      = 0;
      constraints.gridy      = 0;
      constraints.insets.top = 5;

      JPanel panel1          = new JPanel(new GridBagLayout());
      JPanel panel2  = new JPanel(new GridBagLayout());
      JPanel panel3  = new JPanel(new GridBagLayout());
      JPanel panel4  = new JPanel(new GridBagLayout());
      JPanel panel7  = new JPanel(new GridBagLayout());
      JPanel panel10 = new JPanel(new GridBagLayout());
      JPanel panel11 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      panel11.add(loadPlugins);

      constraints.insets.right = 5;
      constraints.insets.left  = 4;
      panel1.add(label1, constraints);
      panel2.add(label2, constraints);
      panel3.add(label3, constraints);
      panel4.add(label4, constraints);
      panel7.add(label6, constraints);
      panel10.add(label7, constraints);

      constraints.insets.left  = 0;
      constraints.insets.right = 2;
      constraints.gridx        = 1;
      constraints.weightx      = 1;
      panel1.add(temp, constraints);
      panel2.add(incoming, constraints);
      panel3.add(port, constraints);
      panel7.add(xmlPort, constraints);
      panel4.add(nick, constraints);
      panel10.add(browser, constraints);
      constraints.gridx   = 2;
      constraints.weightx = 0;
      panel1.add(openTemp, constraints);
      panel2.add(openIncoming, constraints);
      panel10.add(selectStandardBrowser, constraints);

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
      browser.confirmNewValue();
      cmbLog.confirmNewValue();
      updateInfoModus.confirmNewValue();
      loadPlugins.confirmNewValue();

      if(DesktopTools.isAdvancedSupported() && System.getProperty("os.name").toLowerCase().indexOf("linux") == -1)
      {
         selectStandardBrowser.setVisible(false);
         browser.setVisible(false);
         label7.setVisible(false);
      }
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


   class SelectBrowserMouseListener extends MouseAdapter
   {
      public void mouseEntered(MouseEvent e)
      {
         JLabel source = (JLabel) e.getSource();

         source.setBorder(BorderFactory.createLineBorder(Color.black));
      }

      public void mouseClicked(MouseEvent e)
      {
         JLabel       source      = (JLabel) e.getSource();
         JFileChooser fileChooser = new JFileChooser();

         fileChooser.setDialogType(JFileChooser.FILES_ONLY);
         fileChooser.setDialogTitle(label7.getText());
         if(browser.getText().length() != 0)
         {
            File tmpFile = new File(browser.getText());

            if(tmpFile.isFile())
            {
               fileChooser.setCurrentDirectory(tmpFile);
            }
         }

         int returnVal = fileChooser.showOpenDialog(source);

         if(returnVal == JFileChooser.APPROVE_OPTION)
         {
            File browserFile = fileChooser.getSelectedFile();

            if(browserFile.isFile())
            {
               browser.setText(browserFile.getPath());
               dirty = true;
               browser.fireCheckRules();
            }
         }
      }

      public void mouseExited(MouseEvent e)
      {
         JLabel source = (JLabel) e.getSource();

         source.setBorder(null);
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
