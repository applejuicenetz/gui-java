/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.tklsoft.gui.controls.TKLButton;
import de.tklsoft.gui.controls.TKLCheckBox;
import de.tklsoft.gui.controls.TKLTextField;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODVerbindungPanel.java,v 1.7 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class ODVerbindungPanel extends JPanel implements OptionsRegister
{
   private boolean       dirty                  = false;
   private JLabel        label1;
   private JLabel        label2;
   private JLabel        label3;
   private JLabel        label4;
   private JLabel        label5;
   private JLabel        label6;
   private JLabel        kbSlot;
   private TKLCheckBox   automaticConnect;
   private TKLTextField  maxSourcesPerFile      = new TKLTextField();
   private TKLTextField  maxVerbindungen        = new TKLTextField();
   private TKLTextField  maxUpload              = new TKLTextField();
   private TKLTextField  maxDownload            = new TKLTextField();
   private JSlider       kbSlider;
   private TKLTextField  maxVerbindungenProTurn = new TKLTextField();
   private AJSettings    ajSettings;
   private Logger        logger;
   private Icon          menuIcon;
   private String        menuText;
   private TKLButton     wizzard                = new TKLButton();
   private OptionsDialog parent;

   public ODVerbindungPanel(OptionsDialog parent, AJSettings ajSettings)
   {
      logger          = Logger.getLogger(getClass());
      this.parent     = parent;
      this.ajSettings = ajSettings;
      try
      {
         init();
      }
      catch(Exception ex)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
         }
      }
   }

   public boolean isDirty()
   {
      return dirty;
   }

   private void init() throws Exception
   {
      IconManager im = IconManager.getInstance();

      menuIcon = im.getIcon("opt_verbindung");
      setLayout(new BorderLayout());
      JPanel     panel1 = new JPanel(new GridBagLayout());

      JPanel     panel2  = new JPanel(new GridBagLayout());
      JPanel     panel3  = new JPanel(new GridBagLayout());
      JPanel     panel4  = new JPanel(new GridBagLayout());
      JPanel     panel5  = new JPanel(new GridBagLayout());
      JPanel     panel6  = new JPanel(new GridBagLayout());
      JPanel     panel7  = new JPanel(new GridBagLayout());
      JPanel     panel8  = new JPanel(new GridBagLayout());
      JPanel     panel10 = new JPanel(new GridBagLayout());
      JPanel     panel12 = new JPanel(new GridBagLayout());
      FlowLayout flowL   = new FlowLayout();

      flowL.setAlignment(FlowLayout.RIGHT);
      JPanel panel9 = new JPanel(flowL);

      maxVerbindungen.setDocument(new NumberInputVerifier());
      maxVerbindungen.setHorizontalAlignment(JLabel.RIGHT);
      maxVerbindungen.addFocusListener(new MaxVerbindungenFocusListener());
      maxUpload.setDocument(new NumberInputVerifier());
      maxUpload.setHorizontalAlignment(JLabel.RIGHT);
      maxUpload.addFocusListener(new MaxUploadFocusListener());
      maxDownload.setDocument(new NumberInputVerifier());
      maxDownload.setHorizontalAlignment(JLabel.RIGHT);
      maxDownload.addFocusListener(new MaxDownloadFocusListener());
      maxSourcesPerFile.setDocument(new NumberInputVerifier());
      maxSourcesPerFile.setHorizontalAlignment(JLabel.RIGHT);
      maxSourcesPerFile.addFocusListener(new MaxSourcesPerFileFocusListener());
      maxVerbindungenProTurn.setDocument(new NumberInputVerifier());
      maxVerbindungenProTurn.setHorizontalAlignment(JLabel.RIGHT);
      maxVerbindungenProTurn.addFocusListener(new MaxVerbindungenProTurnFocusListener());

      LanguageSelector languageSelector = LanguageSelector.getInstance();

      label1 = new JLabel(languageSelector.getFirstAttrbuteByTagName("einstform.Label4.caption"));
      label2 = new JLabel(languageSelector.getFirstAttrbuteByTagName("einstform.Label5.caption"));
      label3 = new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.verbindung.label3"));
      label4 = new JLabel(languageSelector.getFirstAttrbuteByTagName("einstform.Label13.caption"));
      label5 = new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.verbindung.label5"));
      label6 = new JLabel(languageSelector.getFirstAttrbuteByTagName("javagui.options.verbindung.label6"));
      menuText = languageSelector.getFirstAttrbuteByTagName("einstform.connectionsheet.caption");
      wizzard.setText(languageSelector.getFirstAttrbuteByTagName("javagui.options.verbindung.labelwizard"));
      kbSlot = new JLabel();

      int untereGrenze = (int) Math.pow((double) ajSettings.getMaxUploadInKB(), 0.2);
      int obereGrenze = (int) Math.pow((double) ajSettings.getMaxUploadInKB(), 0.6);

      kbSlider = new JSlider(untereGrenze, obereGrenze);
      kbSlider.setMajorTickSpacing(1);
      kbSlider.setMinorTickSpacing(1);
      kbSlider.setSnapToTicks(true);
      kbSlider.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               JSlider slider = (JSlider) e.getSource();

               kbSlot.setText(Integer.toString(slider.getValue()) + " kb/s");
               dirty = true;
               ajSettings.setSpeedPerSlot(slider.getValue());
            }
         });
      automaticConnect = new TKLCheckBox(languageSelector.getFirstAttrbuteByTagName("einstform.autoconn.caption"));
      automaticConnect.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent e)
            {
               dirty = true;
               ajSettings.setAutoConnect(automaticConnect.isSelected());
            }
         });
      wizzard.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               displayConnectionWizard();
            }
         });

      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor     = GridBagConstraints.NORTH;
      constraints.fill       = GridBagConstraints.BOTH;
      constraints.gridx      = 0;
      constraints.gridy      = 0;
      constraints.insets.top = 5;

      constraints.insets.left = 5;
      panel2.add(label1, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      panel2.add(maxVerbindungen, constraints);
      constraints.weightx = 0;

      constraints.gridx = 0;
      panel3.add(label2, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      panel3.add(maxUpload, constraints);
      constraints.weightx = 0;

      constraints.gridx = 0;
      panel4.add(label3, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      panel4.add(kbSlider, constraints);
      constraints.gridx   = 2;
      constraints.weightx = 0;
      panel4.add(kbSlot, constraints);

      constraints.gridx = 0;
      panel5.add(label4, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      panel5.add(maxDownload, constraints);
      constraints.weightx = 0;

      constraints.gridx = 0;
      panel10.add(label5, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      panel10.add(maxVerbindungenProTurn, constraints);
      constraints.weightx = 0;

      constraints.gridx = 0;
      panel12.add(label6, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      panel12.add(maxSourcesPerFile, constraints);
      constraints.weightx = 0;

      panel9.add(automaticConnect);

      constraints.gridx        = 0;
      constraints.weightx      = 1;
      constraints.gridy        = 0;
      constraints.insets.right = 5;
      panel1.add(panel2, constraints);
      constraints.gridy = 1;
      panel1.add(panel3, constraints);
      constraints.gridy = 2;
      panel1.add(panel4, constraints);
      constraints.gridy = 3;
      panel1.add(panel5, constraints);
      constraints.gridy = 4;
      panel1.add(panel6, constraints);
      constraints.gridy = 5;
      panel1.add(panel7, constraints);
      constraints.gridy = 6;
      panel1.add(panel8, constraints);
      constraints.gridy = 7;
      panel1.add(panel10, constraints);
      constraints.gridy = 8;
      panel1.add(panel12, constraints);
      constraints.gridy = 9;
      panel1.add(panel9, constraints);

      constraints.gridy = 10;
      JPanel panel11 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      panel11.add(wizzard);
      panel1.add(panel11, constraints);

      add(panel1, BorderLayout.NORTH);

      reloadSettings();

      maxSourcesPerFile.confirmNewValue();
      maxVerbindungen.confirmNewValue();
      maxUpload.confirmNewValue();
      maxDownload.confirmNewValue();
      maxVerbindungenProTurn.confirmNewValue();
      automaticConnect.confirmNewValue();
   }

   public Icon getIcon()
   {
      return menuIcon;
   }

   public String getMenuText()
   {
      return menuText;
   }

   public void displayConnectionWizard()
   {
      boolean regularClosed = AppleJuiceClient.showConnectionWizard(parent, ajSettings);

      if(regularClosed)
      {
         parent.reloadSettings();
      }
   }

   public void reloadSettings()
   {
      AJSettings ajSettings2 = AppleJuiceClient.getAjFassade().getAJSettings();

      /*
      ajSettings.setAutoConnect(ajSettings2.isAutoConnect());
      ajSettings.setIncomingDir(ajSettings2.getIncomingDir());
      ajSettings.setMaxConnections(ajSettings2.getMaxConnections());
      ajSettings.setMaxDownload(ajSettings2.getMaxDownload());
      ajSettings.setMaxNewConnectionsPerTurn(ajSettings2.getMaxNewConnectionsPerTurn());
      ajSettings.setMaxSourcesPerFile(ajSettings2.getMaxSourcesPerFile());
      ajSettings.setMaxUpload(ajSettings2.getMaxUpload());
      ajSettings.setNick(ajSettings2.getNick());
      ajSettings.setPort(ajSettings2.getPort());
      ajSettings.setShareDirs(ajSettings2.getShareDirs());
      ajSettings.setSpeedPerSlot(ajSettings2.getSpeedPerSlot());
      ajSettings.setTempDir(ajSettings2.getTempDir());
      ajSettings.setXMLPort(ajSettings2.getXMLPort());
      */
      maxUpload.setText(Long.toString(ajSettings2.getMaxUploadInKB()));
      maxDownload.setText(Long.toString(ajSettings2.getMaxDownloadInKB()));
      maxVerbindungen.setText(Long.toString(ajSettings2.getMaxConnections()));
      kbSlider.setValue(ajSettings2.getSpeedPerSlot());
      kbSlot.setText(Integer.toString(kbSlider.getValue()) + " kb/s");
      automaticConnect.setSelected(ajSettings2.isAutoConnect());
      maxVerbindungenProTurn.setText(Long.toString(ajSettings2.getMaxNewConnectionsPerTurn()));
      maxSourcesPerFile.setText(Long.toString(ajSettings2.getMaxSourcesPerFile()));
   }

   class MaxVerbindungenProTurnFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         long wert = Long.parseLong(maxVerbindungenProTurn.getText());

         if(wert != ajSettings.getMaxNewConnectionsPerTurn())
         {
            if(wert < 1 || wert > 200)
            {
               maxVerbindungenProTurn.setText(Long.toString(ajSettings.getMaxNewConnectionsPerTurn()));
            }
            else
            {
               dirty = true;
               ajSettings.setMaxNewConnectionsPerTurn(Long.parseLong(maxVerbindungenProTurn.getText()));
            }
         }
      }
   }


   class MaxSourcesPerFileFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         long wert = Long.parseLong(maxSourcesPerFile.getText());

         if(wert != ajSettings.getMaxSourcesPerFile())
         {
            if(wert < 1)
            {
               maxSourcesPerFile.setText(Long.toString(ajSettings.getMaxSourcesPerFile()));
            }
            else
            {
               dirty = true;
               ajSettings.setMaxSourcesPerFile(Long.parseLong(maxSourcesPerFile.getText()));
            }
         }
      }
   }


   class MaxDownloadFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         if(Long.parseLong(maxDownload.getText()) != ajSettings.getMaxDownloadInKB())
         {
            dirty = true;
            ajSettings.setMaxDownload(Long.parseLong(maxDownload.getText()) * 1024);
         }
      }
   }


   class MaxUploadFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         int untereGrenze = (int) Math.pow(Double.parseDouble(maxUpload.getText()), 0.2);
         int obereGrenze = (int) Math.pow(Double.parseDouble(maxUpload.getText()), 0.6);

         kbSlider.setMinimum(untereGrenze);
         kbSlider.setMaximum(obereGrenze);
         if(Long.parseLong(maxUpload.getText()) != ajSettings.getMaxUploadInKB())
         {
            dirty = true;
            ajSettings.setMaxUpload(Long.parseLong(maxUpload.getText()) * 1024);
         }
      }
   }


   class MaxVerbindungenFocusListener extends FocusAdapter
   {
      public void focusLost(FocusEvent e)
      {
         if(Long.parseLong(maxVerbindungen.getText()) != ajSettings.getMaxConnections())
         {
            dirty = true;
            ajSettings.setMaxConnections(Long.parseLong(maxVerbindungen.getText()));
         }
      }
   }
}
