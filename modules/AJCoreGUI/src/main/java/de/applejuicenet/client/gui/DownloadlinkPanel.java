/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.tklsoft.gui.controls.InvalidRule;
import de.tklsoft.gui.controls.StatusHolder.STATUSFLAG;
import de.tklsoft.gui.controls.TKLComboBox;
import de.tklsoft.gui.controls.TKLTextField;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class DownloadlinkPanel extends JPanel implements LanguageListener
{
   private JLabel       lblLink          = new JLabel();
   private TKLTextField txtDownloadLink  = new TKLTextField();
   private JButton      btnStartDownload = new JButton();
   private TKLComboBox  cmbTargetDir     = new TKLComboBox();
   private JLabel       lblTargetDir     = new JLabel();

   public DownloadlinkPanel()
   {
      KeyListener keyListener = new KeyAdapter()
      {
         public void keyPressed(KeyEvent ke)
         {
            if(ke.getKeyCode() == KeyEvent.VK_ENTER)
            {
               btnStartDownload.doClick();
            }
         }
      };

      txtDownloadLink.addKeyListener(keyListener);
      InvalidRule downloadloadlinkRule = component -> {
         String text = ((TKLTextField) component).getText().replace("%7C", "|").trim().toLowerCase();

         if(text.startsWith("web+ajfsp://"))
         {
            text = text.substring("web+".length());
         }

         if(text.length() == 0)
         {
            return false;
         }

         if(!text.startsWith("ajfsp://"))
         {
            return true;
         }

         text = text.substring("ajfsp://".length());
         if(!text.startsWith("file") && !text.startsWith("server"))
         {
            return true;
         }

         int count;

         if(text.startsWith("file"))
         {
            count = 3;
         }
         else
         {
            count = 2;
         }

         for(int i = 0; i < text.length(); i++)
         {
            if(text.charAt(i) == '|')
            {
               count--;
            }
         }

         if(count > 0)
         {
            return true;
         }

         return false;
      };

      txtDownloadLink.ignoreStatus(STATUSFLAG.MODIFIED, true);
      txtDownloadLink.addInvalidRule(downloadloadlinkRule);
      txtDownloadLink.ignoreInvalidRules(false);

      cmbTargetDir.ignoreStatus(STATUSFLAG.MODIFIED, true);
      InvalidRule targetDirRule = component -> {
         Object obj = ((TKLComboBox) component).getSelectedItem();

         if(obj == null)
         {
            return false;
         }

         String subdir = (String) obj;

         return subdir.contains(File.separator) || subdir.indexOf(ApplejuiceFassade.separator) != -1 ||
                 subdir.contains("..") || subdir.contains(":");
      };

      cmbTargetDir.addInvalidRule(targetDirRule);
      setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.fill   = GridBagConstraints.BOTH;
      constraints.insets = new Insets(5, 5, 0, 0);
      add(lblLink, constraints);
      constraints.weightx = 1;
      add(txtDownloadLink, constraints);
      constraints.weightx = 0;
      constraints.insets  = new Insets(5, 5, 0, 5);
      add(btnStartDownload, constraints);
      fireLanguageChanged();
   }

   public TKLTextField getTxtDownloadLink()
   {
      return txtDownloadLink;
   }

   public JButton getBtnStartDownload()
   {
      return btnStartDownload;
   }

   public TKLComboBox getCmbTargetDir()
   {
      return cmbTargetDir;
   }

   public JLabel getTxtTargetDir()
   {
      return lblTargetDir;
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String           text = languageSelector.getFirstAttrbuteByTagName("mainform.Label14.caption");

      lblLink.setText(text);
      btnStartDownload.setText(languageSelector.getFirstAttrbuteByTagName("mainform.downlajfsp.caption"));
      btnStartDownload.setToolTipText(languageSelector.getFirstAttrbuteByTagName("mainform.downlajfsp.hint"));
      lblTargetDir.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.zielverzeichnis"));

   }
}
