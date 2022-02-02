/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.tklsoft.gui.controls.TKLButton;
import de.tklsoft.gui.controls.TKLComboBox;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/IncomingDirSelectionDialog.java,v 1.7 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class IncomingDirSelectionDialog extends JDialog
{
   private String[]    selectionValues;
   private TKLButton   schliessen        = new TKLButton();
   private TKLComboBox incomingDirs      = new TKLComboBox();
   private boolean     somethingSelected = false;

   public IncomingDirSelectionDialog(JFrame parentDialog, String[] selectionValues, String selected)
   {
      super(parentDialog, true);
      this.selectionValues = selectionValues;
      init(selected);
   }

   private void init(String selected)
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

      schliessen.addKeyListener(disposeKeyAdapter);
      incomingDirs.addKeyListener(disposeKeyAdapter);
      incomingDirs.getEditor().getEditorComponent().addKeyListener(disposeKeyAdapter);
      addKeyListener(disposeKeyAdapter);
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      setTitle(languageSelector.getFirstAttrbuteByTagName("mainform.changetarget.caption"));
      schliessen.setText(languageSelector.getFirstAttrbuteByTagName("einstform.Button1.caption"));
      schliessen.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               somethingSelected = true;
               IncomingDirSelectionDialog.this.dispose();
            }
         });

      JLabel label1 = new JLabel();

      label1.setText(languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.neuesverzeichnis"));
      for(int i = 0; i < selectionValues.length; i++)
      {
         incomingDirs.addItem(selectionValues[i]);
         if(selectionValues[i].compareTo("") == 0)
         {
            incomingDirs.setSelectedItem(selectionValues[i]);
         }
      }

      incomingDirs.setEditable(true);
      incomingDirs.getEditor().addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ke)
            {
               schliessen.doClick();
            }
         });
      if(selected != null)
      {
         incomingDirs.setSelectedItem(selected);
      }

      incomingDirs.confirmNewValue();
      JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      panel1.add(label1);
      panel1.add(incomingDirs);
      getContentPane().setLayout(new BorderLayout());
      JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

      southPanel.add(schliessen);
      getContentPane().add(panel1, BorderLayout.NORTH);
      getContentPane().add(southPanel, BorderLayout.SOUTH);
      pack();
      Dimension appDimension = getSize();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
   }

   public String getSelectedIncomingDir()
   {
      if(somethingSelected)
      {
         return (String) incomingDirs.getSelectedItem();
      }
      else
      {
         return null;
      }
   }
}
