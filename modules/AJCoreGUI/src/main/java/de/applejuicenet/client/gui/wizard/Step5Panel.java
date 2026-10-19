/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;


import javax.swing.*;
import java.awt.*;

/**
 * Copyright: General Public License
 */
public class Step5Panel extends WizardPanel
{
   private JTextArea label1 = new JTextArea();
   private JTextArea label2 = new JTextArea();

   public Step5Panel()
   {
      super();
      init();
   }

   private void init()
   {
      label1.setWrapStyleWord(true);
      label1.setLineWrap(true);
      label1.setEditable(false);
      label2.setWrapStyleWord(true);
      label2.setLineWrap(true);
      label2.setEditable(false);

      setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor      = GridBagConstraints.NORTH;
      constraints.fill        = GridBagConstraints.BOTH;
      constraints.insets.top  = 5;
      constraints.insets.left = 5;
      constraints.gridx       = 0;
      constraints.gridy       = 0;
      constraints.gridwidth   = 1;
      add(label1, constraints);
      constraints.gridx      = 0;
      constraints.gridy      = 1;
      constraints.insets.top = 10;
      add(label2, constraints);
      constraints.gridx   = 0;
      constraints.gridy   = 2;
      constraints.weightx = 1;
      constraints.weighty = 1;
      add(new JLabel(), constraints);
   }

   public void fireLanguageChanged()
   {
      label1.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step5.label1"));
      label2.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step5.label2"));
   }
}
