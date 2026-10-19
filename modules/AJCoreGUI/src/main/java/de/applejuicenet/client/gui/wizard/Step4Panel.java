/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;


import javax.swing.*;
import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Step4Panel.java,v 1.14 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class Step4Panel extends WizardPanel
{
   private JTextArea label1         = new JTextArea();
   private JTextArea label2         = new JTextArea();
   private JComboBox<ConnectionKind> connectionType = new JComboBox<>();

   public Step4Panel()
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

      ConnectionKind[] connections = ConnectionPresets.getConnections();

      if(connections != null)
      {
         for(int i = 0; i < connections.length; i++)
         {
            connectionType.addItem(connections[i]);
         }

         connectionType.setSelectedIndex(0);
      }

      setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor      = GridBagConstraints.NORTH;
      constraints.fill        = GridBagConstraints.BOTH;
      constraints.insets.top  = 5;
      constraints.insets.left = 5;
      constraints.gridx       = 0;
      constraints.gridy       = 0;
      constraints.gridwidth   = 2;
      add(label1, constraints);
      constraints.gridx      = 0;
      constraints.gridy      = 1;
      constraints.insets.top = 10;
      add(label2, constraints);
      constraints.gridwidth = 1;
      constraints.gridy     = 2;
      add(connectionType, constraints);
      constraints.gridx   = 1;
      constraints.weightx = 1;
      add(new JLabel(), constraints);
      constraints.weightx = 0;
      constraints.weighty = 1;
      constraints.gridy   = 3;
      add(new JLabel(), constraints);
   }

   public ConnectionKind getConnectionType()
   {
      return (ConnectionKind) connectionType.getSelectedItem();
   }

   public void fireLanguageChanged()
   {
      label1.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step4.label1"));
      label2.setText(languageSelector.getFirstAttributeByTagName("javagui.wizard.step4.label2"));
   }
}
