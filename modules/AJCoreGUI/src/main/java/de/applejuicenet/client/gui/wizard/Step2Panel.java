/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;


import javax.swing.*;
import java.awt.*;

/**
 * Copyright: General Public License
 */
public class Step2Panel extends WizardPanel
{
   private JLabel    label1    = new JLabel();
   private JTextArea textArea1 = new JTextArea();
   private JTextArea textArea2 = new JTextArea();

   public Step2Panel()
   {
      super();
      init();
   }

   private void init()
   {
      textArea1.setWrapStyleWord(true);
      textArea2.setWrapStyleWord(true);
      textArea1.setLineWrap(true);
      textArea1.setEditable(false);
      textArea2.setLineWrap(true);
      textArea2.setEditable(false);
      setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor      = GridBagConstraints.NORTH;
      constraints.fill        = GridBagConstraints.BOTH;
      constraints.insets.top  = 5;
      constraints.insets.left = 5;
      constraints.gridx       = 0;
      constraints.gridy       = 0;
      add(label1, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      add(new JLabel(), constraints);
      constraints.weightx   = 1;
      constraints.gridx     = 0;
      constraints.gridy     = 1;
      constraints.gridwidth = 2;
      add(textArea1, constraints);
      constraints.gridy = 2;
      add(textArea2, constraints);
      constraints.gridy   = 3;
      constraints.weighty = 1;
      add(new JLabel(), constraints);
   }

   public void fireLanguageChanged()
   {
      label1.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step2.label1"));
      textArea1.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step2.label2"));
      textArea2.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step2.label3"));
   }
}
