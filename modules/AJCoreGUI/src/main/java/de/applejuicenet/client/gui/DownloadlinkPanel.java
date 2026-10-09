/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

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
   private JTextField txtDownloadLink  = new JTextField();
   private JButton      btnStartDownload = new JButton();
   private JComboBox  cmbTargetDir     = new JComboBox();
   private JLabel       lblTargetDir     = new JLabel();
   private java.util.function.Predicate<String> linkRule;

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
      linkRule = raw -> {
         String text = raw.replace("%7C", "|").trim().toLowerCase();

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

      txtDownloadLink.getDocument().addDocumentListener(new javax.swing.event.DocumentListener()
      {
         public void insertUpdate(javax.swing.event.DocumentEvent e) { markInvalid(); }
         public void removeUpdate(javax.swing.event.DocumentEvent e) { markInvalid(); }
         public void changedUpdate(javax.swing.event.DocumentEvent e) { markInvalid(); }
      });

      java.util.function.Predicate<Object> targetDirRule = obj -> {
         if(obj == null)
         {
            return false;
         }

         String subdir = (String) obj;

         return subdir.contains(File.separator) || subdir.indexOf(ApplejuiceFassade.separator) != -1 ||
                 subdir.contains("..") || subdir.contains(":");
      };

      cmbTargetDir.addItemListener(e -> cmbTargetDir.putClientProperty("JComponent.outline",
         targetDirRule.test(cmbTargetDir.getSelectedItem()) ? "error" : null));
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

   private void markInvalid()
   {
      txtDownloadLink.putClientProperty("JComponent.outline", isLinkInvalid() ? "error" : null);
   }

   public boolean isLinkInvalid()
   {
      return splitLinks(txtDownloadLink.getText()).stream().anyMatch(linkRule);
   }

   /**
    * Teilt Text mit mehreren Links (Whitespace, Zeilenumbrueche oder direkt
    * aneinandergereiht) in einzelne Links auf. Jeder Link beginnt bei
    * {@code ajfsp://} bzw. {@code web+ajfsp://}.
    */
   public static java.util.List<String> splitLinks(String text)
   {
      java.util.List<String> links = new java.util.ArrayList<>();

      if(text == null)
      {
         return links;
      }

      for(String part : text.split("(?i)(?<!web\\+)(?=(web\\+)?ajfsp://)"))
      {
         String link = part.trim();

         if(!link.isEmpty())
         {
            links.add(link);
         }
      }

      return links;
   }

   public JTextField getTxtDownloadLink()
   {
      return txtDownloadLink;
   }

   public JButton getBtnStartDownload()
   {
      return btnStartDownload;
   }

   public JComboBox getCmbTargetDir()
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
