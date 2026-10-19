/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Step1Panel.java,v 1.16 2009/01/14 13:20:55 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class Step1Panel extends WizardPanel
{
   private JTextArea explanation = new JTextArea();
   private JComboBox languages = new JComboBox();

   public Step1Panel()
   {
      super();
      init();
   }

   private void init()
   {
      explanation.setWrapStyleWord(true);
      explanation.setLineWrap(true);
      explanation.setEditable(false);

      String path         = System.getProperty("user.dir") + File.separator + "language" + File.separator;
      File   languagePath = new File(path);

      if(languagePath.isDirectory())
      {
         String[] tempList = languagePath.list();

         for(int i = 0; i < tempList.length; i++)
         {
            int pos = tempList[i].indexOf(".properties");

            if(pos != -1)
            {
               String temp = tempList[i].substring(0, pos);

               languages.addItem(temp);
            }
         }
      }

      languages.addItemListener(new ItemListener()
         {
            public void itemStateChanged(ItemEvent e)
            {
               Object selected = languages.getSelectedItem();

               if(selected != null)
               {
                  String path      = System.getProperty("user.dir") + File.separator + "language" + File.separator;
                  String fileName = path + (String) selected + ".properties";

                  LanguageSelector.getInstance(fileName);
               }
            }
         });

      setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor      = GridBagConstraints.NORTH;
      constraints.fill        = GridBagConstraints.BOTH;
      constraints.insets.top  = 5;
      constraints.insets.left = 5;
      constraints.gridx       = 0;
      constraints.gridy       = 0;
      constraints.gridwidth   = 2;
      add(explanation, constraints);
      constraints.gridwidth = 1;
      constraints.gridx     = 0;
      constraints.gridy     = 1;
      add(languages, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      add(new JLabel(), constraints);
      constraints.weightx = 0;
      constraints.gridy   = 2;
      constraints.weighty = 1;
      add(new JLabel(), constraints);
   }

   public void fireLanguageChanged()
   {
      explanation.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step1.label1"));
   }
}
