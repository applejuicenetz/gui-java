/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.wizard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;

import de.applejuicenet.client.fassade.shared.AJSettings;
import de.tklsoft.gui.controls.InvalidRule;
import de.tklsoft.gui.controls.ModifyableComponent;
import de.tklsoft.gui.controls.TKLTextArea;
import de.tklsoft.gui.controls.TKLTextField;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt3Panel.java,v 1.18 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class Schritt3Panel extends WizardPanel
{
   private TKLTextArea  erlaeuterung = new TKLTextArea();
   private TKLTextField nickname = new TKLTextField();
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
      erlaeuterung.setBackground(Color.WHITE);
      erlaeuterung.setEditable(false);
      nickname.setColumns(20);

      InvalidRule rule = new InvalidRule()
      {
         public boolean isInvalid(ModifyableComponent component)
         {
            return (nickname.getText().toLowerCase().startsWith("nonick") || nickname.getText().length() == 0) ? true : false;
         }
      };

      nickname.addInvalidRule(rule);

      nickname.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent e)
            {
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
      nickname.ignoreInvalidRules(false);

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

      nickname.confirmNewValue();
   }

   public boolean isValidNickname()
   {
      return !nickname.isInvalid();
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
