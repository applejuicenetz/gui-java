/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.facade.shared.AJSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Copyright: General Public License
 */
public class Step3Panel extends WizardPanel
{
   private JTextArea  explanation = new JTextArea();
   private JTextField nickname = new JTextField();
   private WizardDialog parent;

   public Step3Panel(WizardDialog parent, AJSettings settings)
   {
      super();
      this.parent = parent;
      if(settings != null)
      {
         String nick = settings.getNick();

         nickname.setText(nick);
         if(!isValidNickname())
         {
            nickname.setText("nonick");
         }
      }

      init();
   }

   private void init()
   {
      explanation.setWrapStyleWord(true);
      explanation.setLineWrap(true);
      explanation.setEditable(false);
      nickname.setColumns(20);

      nickname.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent e)
            {
               markNickname();
               if(isValidNickname())
               {
                  parent.setNextEnabled(true);
               }
               else
               {
                  parent.setNextEnabled(false);
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
      constraints.gridx      = 0;
      constraints.gridy      = 1;
      constraints.gridwidth  = 1;
      constraints.insets.top = 10;
      add(nickname, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      add(new JLabel(), constraints);
      constraints.weightx = 0;
      constraints.gridy   = 2;
      constraints.weighty = 1;
      add(new JLabel(), constraints);

      markNickname();
   }

   public boolean isValidNickname()
   {
      String text = nickname.getText();

      return !(text.toLowerCase().startsWith("nonick") || text.length() == 0);
   }

   private void markNickname()
   {
      nickname.putClientProperty("JComponent.outline", isValidNickname() ? null : "error");
   }

   public void fireLanguageChanged()
   {
      explanation.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step3.label1"));
   }

   public String getNickname()
   {
      return nickname.getText();
   }
}
