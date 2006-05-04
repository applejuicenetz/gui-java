package de.applejuicenet.client.gui.plugins.jabber;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.PluginConnector;

public class JabberPlugin extends PluginConnector
{
   private Logger         logger;
   private JButton        connectButton = new JButton("Verbinden");
   private JTextField     user = new JTextField(15);
   private JPasswordField passwort = new JPasswordField(15);
   private JTextField     nickname = new JTextField(15);

   public JabberPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map languageFiles, ImageIcon icon)
   {
      super(pluginsPropertiesXMLHolder, languageFiles, icon);
      logger = Logger.getLogger(getClass());
      initGUI();
   }

   private void initGUI()
   {
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
   }

   protected void doConnect()
   {
      String username = user.getText().trim();

      if(username.length() == 0)
      {
         return;
      }

      XMPPConnection connection;

      try
      {
         connection = new XMPPConnection("debianforum.de");
         String password = new String(passwort.getPassword());

         if(password.length() == 0)
         {
            connection.loginAnonymously();
         }
         else
         {
            connection.login(username, password);
         }

//         Roster      roster = connection.getRoster();
//         Iterator    it = roster.getEntries();
//         RosterEntry curEntry;
//
//         while(it.hasNext())
//         {
//            curEntry = (RosterEntry) it.next();
//         }

         MultiUserChat muc = new MultiUserChat(connection, "applejuice@chat.debianforum.de");
         muc.create("applejuice");

         // Get the the room's configuration form
         Form form = muc.getConfigurationForm();
         // Create a new form to submit based on the original form
         Form submitForm = form.createAnswerForm();
         // Add default answers to the form to submit
         for (Iterator fields = form.getFields(); fields.hasNext();) {
             FormField field = (FormField) fields.next();
             System.out.println(field.getDescription());
             if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
                 // Sets the default value as the answer
                 submitForm.setDefaultAnswer(field.getVariable());
             }
         }
         // Sets the new owner of the room
         List<String> owners = new ArrayList<String>();
         owners.add("Maj0r@debianforum.de");
         submitForm.setAnswer("muc#roomconfig_roomowners", owners);
         // Send the completed form (with default values) to the server to configure the room
         muc.sendConfigurationForm(submitForm);
      }
      catch(XMPPException e)
      {
         e.printStackTrace();
      }
   }

   private void initForTest()
   {
      user.setText("Maj0r");
      nickname.setText("Maj0r");
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
}
