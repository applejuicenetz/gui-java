/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import de.applejuicenet.client.gui.plugins.jabber.control.IdentityController;
import de.applejuicenet.client.gui.plugins.jabber.control.MultiUserChatController;
import de.applejuicenet.client.gui.plugins.jabber.view.IdentityPanel;

import de.tklsoft.gui.controls.InvalidRule;
import de.tklsoft.gui.controls.ModifyableComponent;
import de.tklsoft.gui.controls.StatusHolder.STATUSFLAG;
import de.tklsoft.gui.controls.TKLPasswordField;
import de.tklsoft.gui.controls.TKLTextField;

public class JabberPlugin extends PluginConnector
{
   private final String     CMD_VERBINDEN  = "Verbinden";
   private final String     CMD_TRENNEN    = "Trennen";
   private Logger           logger;
   private JButton          connectButton  = new JButton(CMD_VERBINDEN);
   private TKLTextField     user           = new TKLTextField(15);
   private TKLPasswordField passwort       = new TKLPasswordField();
   private TKLTextField     nickname       = new TKLTextField(15);
   private JTabbedPane      tabbedPane     = new JTabbedPane();
   private CardLayout       registerLayout = new CardLayout();
   private JPanel           registerPanel  = new JPanel(registerLayout);
   private XMPPConnection   connection     = null;

   public JabberPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon)
   {
      super(pluginsPropertiesXMLHolder, languageFiles, icon);
      SmackConfiguration.setPacketReplyTimeout(20000);
      logger = Logger.getLogger(getClass());
      initGUI();
   }

   private void initGUI()
   {
      user.addInvalidRule(InvalidRule.EmptyInvalidRule.getInstance());
      user.addInvalidRule(new InvalidRule()
         {
            public boolean isInvalid(ModifyableComponent comp)
            {
               String text = user.getText().trim();

               if(text.length() <= 2)
               {
                  return true;
               }

               if(text.indexOf("@") == -1)
               {
                  return true;
               }

               if(text.indexOf(".") == -1 || text.endsWith("."))
               {
                  return true;
               }

               return false;
            }
         });

      passwort.setColumns(15);
      //       passwort.addInvalidRule(InvalidRule.EmptyInvalidRule.getInstance());
      nickname.addInvalidRule(InvalidRule.EmptyInvalidRule.getInstance());

      user.ignoreInvalidRules(false);
      nickname.ignoreInvalidRules(false);

      user.ignoreStatus(STATUSFLAG.MODIFIED, true);
      //      passwort.ignoreStatus(STATUSFLAG.MODIFIED, true);
      nickname.ignoreStatus(STATUSFLAG.MODIFIED, true);

      user.fireCheckRules();
      nickname.fireCheckRules();

      setLayout(new BorderLayout());
      JPanel buttonpanel = new JPanel(new FlowLayout());

      buttonpanel.add(new JLabel("Benutzer:"));
      buttonpanel.add(user);
      buttonpanel.add(new JLabel("Passwort:"));
      buttonpanel.add(passwort);
      buttonpanel.add(new JLabel("Nickname:"));
      buttonpanel.add(nickname);
      buttonpanel.add(connectButton);
      add(buttonpanel, BorderLayout.NORTH);

      if(System.getProperty("Debug") != null)
      {
         initForTest();
      }

      connectButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               doConnect();
            }
         });

      IdentityPanel identityPanel = IdentityController.getInstance().getPanel();

      JSplitPane    eastSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, identityPanel, registerPanel);

      eastSplitPane.setOneTouchExpandable(true);

      JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, eastSplitPane);

      centerSplitPane.setOneTouchExpandable(true);
      centerSplitPane.setResizeWeight(1);
      add(centerSplitPane, BorderLayout.CENTER);
   }

   private boolean isInputValid()
   {
      user.fireCheckRules();
      nickname.fireCheckRules();

      //       password.fireCheckRules();
      if(user.isInvalid())
      {
         return false;
      }

      if(nickname.isInvalid())
      {
         return false;
      }

      return true;
   }

   protected void doConnect()
   {
      if(!isInputValid())
      {
         return;
      }

      connectButton.setEnabled(false);
      if(CMD_VERBINDEN.equals(connectButton.getText()))
      {
         user.setEnabled(false);
         passwort.setEnabled(false);
         nickname.setEnabled(false);

         // neue Verbindung aufbauen
         String       tmp    = user.getText().trim();
         int          index  = tmp.indexOf("@");
         final String server = tmp.substring(index + 1);

         final String username = tmp.substring(0, index);
         final String nick     = nickname.getText().trim();

         new Thread(new Runnable()
            {
               public void run()
               {
                  try
                  {
                     connection = new XMPPConnection(server);
                     String password = new String(passwort.getPassword());

                     connection.login(username, password);
                     IdentityController.getInstance().setConnection(connection);
                     joinMultiUserChat("applejuice@chat.debianforum.de", nick);
                     connectButton.setText(CMD_TRENNEN);
                     connectButton.setEnabled(true);
                  }
                  catch(XMPPException e)
                  {
                     e.printStackTrace();
                     connectButton.setEnabled(true);
                  }
               }
            }).start();
      }
      else
      {

         //        neue Verbindung trennen
         if(null != connection)
         {
            new Thread(new Runnable()
               {
                  public void run()
                  {
                     connection.close();
                     connection = null;
                     IdentityController.getInstance().setConnection(null);
                     connectButton.setText(CMD_VERBINDEN);
                     user.setEnabled(true);
                     passwort.setEnabled(true);
                     nickname.setEnabled(true);
                     connectButton.setEnabled(true);
                  }
               }).start();
         }
      }
   }

   private void initForTest()
   {
      user.setText("ajtest2@jabber.org");
      nickname.setText("ajtest2");
      user.fireCheckRules();
      nickname.fireCheckRules();
   }

   @Override
   public void fireContentChanged(DATALISTENER_TYPE type, Object content)
   {
   }

   @Override
   public void fireLanguageChanged()
   {
   }

   @Override
   public void registerSelected()
   {
   }

   private void joinMultiUserChat(final String room, String nick)
                           throws XMPPException
   {
      MultiUserChat                 muc                     = new MultiUserChat(connection, room);
      final MultiUserChatController multiUserChatController = new MultiUserChatController(muc, nick);

      SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               tabbedPane.addTab(room, multiUserChatController.getMultiUserChatPanel());
               registerPanel.add(multiUserChatController.getRoomName(), multiUserChatController.getUserListPane());
            }
         });
   }
}
