/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.fassade.shared.AJSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt3Panel.java,v 1.18 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
public class Schritt3Panel extends WizardPanel
{
   private JTextArea  erlaeuterung = new JTextArea();
   private JTextField nickname = new JTextField();
   private WizardDialog parent;

   public Schritt3Panel(WizardDialog parent, AJSettings settings)
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
      erlaeuterung.setWrapStyleWord(true);
      erlaeuterung.setLineWrap(true);
      erlaeuterung.setEditable(false);
      nickname.setColumns(20);

      nickname.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent e)
            {
               markNickname();
               if(isValidNickname())
               {
                  parent.setWeiterEnabled(true);
               }
               else
               {
                  parent.setWeiterEnabled(false);
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
      add(erlaeuterung, constraints);
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
      erlaeuterung.setText(languageSelector.getFirstAttrbuteByTagName("javagui.wizard.schritt3.label1"));
   }

   public String getNickname()
   {
      return nickname.getText();
   }
}
