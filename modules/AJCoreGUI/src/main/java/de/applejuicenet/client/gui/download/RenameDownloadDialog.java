/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download;

import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

/**
 * Copyright: General Public License
 */
public class RenameDownloadDialog extends JDialog
{
   private Download    download;
   private JButton     close        = new JButton();
   private JComboBox<String> possibleNames = new JComboBox<>();
   private boolean     somethingSelected = false;

   public RenameDownloadDialog(JFrame parentDialog, Download selectedDownload)
   {
      super(parentDialog, true);
      download = selectedDownload;
      init();
   }

   private void init()
   {
      KeyListener disposeKeyAdapter = new KeyAdapter()
      {
         @Override
         public void keyPressed(KeyEvent e)
         {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
            {
               setVisible(false);
            }
         }
      };

      LanguageSelector languageSelector = LanguageSelector.getInstance();

      setTitle(languageSelector.getFirstAttributeByTagName("mainform.renamefile.caption"));
      close.setText(languageSelector.getFirstAttributeByTagName("einstform.Button1.caption"));
      close.addKeyListener(disposeKeyAdapter);
      close.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               somethingSelected = true;
               RenameDownloadDialog.this.dispose();
            }
         });

      JLabel label1 = new JLabel();

      label1.setText(languageSelector.getFirstAttributeByTagName("javagui.downloadform.newfilename"));
      HashSet<String> set = new HashSet<String>();

      set.add(download.getFilename());
      for(DownloadSource curDownloadSource : download.getSources())
      {
         set.add(curDownloadSource.getFilename());
      }

      for(String curname : set)
      {
         possibleNames.addItem(curname);
         if(curname.compareTo(download.getFilename()) == 0)
         {
            possibleNames.setSelectedItem(curname);
         }
      }

      possibleNames.setEditable(true);
      possibleNames.getEditor().addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ke)
            {
               close.doClick();
            }
         });
      possibleNames.getEditor().getEditorComponent().addKeyListener(disposeKeyAdapter);
      possibleNames.addKeyListener(disposeKeyAdapter);
      JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      panel1.add(label1);
      panel1.add(possibleNames);
      getContentPane().setLayout(new BorderLayout());
      JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

      southPanel.add(close);
      getContentPane().add(panel1, BorderLayout.NORTH);
      getContentPane().add(southPanel, BorderLayout.SOUTH);
      pack();
      setResizable(false);
      DialogLocation.center(this);

      addKeyListener(disposeKeyAdapter);
   }

   public String getNewName()
   {
      if(somethingSelected)
      {
         String newName = ((String) possibleNames.getSelectedItem()).trim();

         if(newName.indexOf(File.separator) == 0 || newName.indexOf(ApplejuiceFacade.separator) == 0)
         {
            newName = newName.substring(1);
         }

         if(newName.length() == 0)
         {
            newName = null;
         }

         StringBuilder tempLink = new StringBuilder(newName);

         for(int i = 0; i < tempLink.length(); i++)
         {
            if(tempLink.charAt(i) == ' ')
            {
               tempLink.setCharAt(i, '.');
            }
         }

         return URLEncoder.encode(tempLink.toString(), StandardCharsets.ISO_8859_1);
      }
      else
      {
         return null;
      }
   }
}
