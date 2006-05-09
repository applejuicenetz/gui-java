/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.jabber.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.MUCUser;

import de.applejuicenet.client.gui.plugins.jabber.view.MultiUserChatListCellRenderer;
import de.applejuicenet.client.gui.plugins.jabber.view.MultiUserChatPanel;

public class MultiUserChatController implements SubjectUpdatedListener, ActionListener
{
   private static final Color         JOIN_GREEN                 = new Color(0, 128, 0);
   public static final String         HTTP_IDENTIFIER            = "http://";
   public static final String         WWW_IDENTIFIER             = "www.";
   private final MultiUserChat        muc;
   private MultiUserChatPanel         multiUserChatPanel         = null;
   private boolean                    selected                   = false;
   private boolean                    marked                     = false;
   private Logger                     logger                     = Logger.getLogger(MultiUserChatController.class);
   private SimpleDateFormat           dateFormatter              = new SimpleDateFormat("HH:mm:ss");
   private ArrayList<String>          befehle                    = new ArrayList<String>();
   private int                        befehlPos                  = -1;
   private JScrollPane                userScrollPane             = null;
   private MultiUserChatUserListModel multiUserChatUserListModel;
   private JList                      userList;
   private String                     nick;
   private MyMessageListener          myMessageListener;
   private MyParticipantListener      myParticipantListener;

   public MultiUserChatController(MultiUserChat multiUserChat, String nick)
                           throws XMPPException
   {
      this.nick = nick;
      muc       = multiUserChat;
      Calendar cal = Calendar.getInstance();

      // Historie der letzten 2 Tage holen (wenns der Server zulaesst)
      cal.add(Calendar.DATE, -2);
      DiscussionHistory history = new DiscussionHistory();

      history.setSince(cal.getTime());
      myMessageListener     = new MyMessageListener(muc);
      myParticipantListener = new MyParticipantListener(muc);
      muc.addParticipantListener(new PacketListener()
         {
            public void processPacket(Packet packet)
            {
               Presence presence = (Presence) packet;
               Iterator it = presence.getExtensions();

               while(it.hasNext())
               {
                  Object obj = it.next();

                  if(obj instanceof MUCUser)
                  {
                     MUCUser user = (MUCUser) obj;
                     String  nick = user.getItem().getJid();

                     if(null != nick && nick.indexOf("@") != -1)
                     {
                        String role       = user.getItem().getRole();
                        String affilation = user.getItem().getAffiliation();

                        nick = nick.substring(0, nick.indexOf("@"));
                        System.out.println(role);
                        MultiChatUserMode mode      = MultiChatUserMode.getByRole(role);
                        MutliUserChatUser multiUser = getMultiUserChatUserListModel().getMutliUserChatUserByName(nick);

                        if(null == multiUser)
                        {
                           multiUser = new MutliUserChatUser(nick);
                           getMultiUserChatUserListModel().addParticipant(multiUser);
                        }

                        multiUser.setMode(mode);
                        updateUserList();
                     }
                  }
               }
            }
         });
      muc.addSubjectUpdatedListener(this);
      muc.join(nick, null, history, SmackConfiguration.getPacketReplyTimeout());
      getMultiUserChatUserListModel().addParticipant(new MutliUserChatUser(nick));
      updateUserList();
   }

   public String getRoomName()
   {
      return muc.getRoom();
   }

   public synchronized MultiUserChatPanel getMultiUserChatPanel()
   {
      if(null == multiUserChatPanel)
      {
         multiUserChatPanel = new MultiUserChatPanel();
         getMultiUserChatPanel().getTextArea().addKeyListener(new KeyAdapter()
            {
               public void keyTyped(KeyEvent ke)
               {
                  getMultiUserChatPanel().getTextField().setText(getMultiUserChatPanel().getTextField().getText() +
                                                                 ke.getKeyChar());
                  getMultiUserChatPanel().getTextField().requestFocus();
               }
            });
         getMultiUserChatPanel().getTextField().addKeyListener(new KeyAdapter()
            {
               public void keyReleased(KeyEvent ke)
               {
                  super.keyReleased(ke);

                  //               if(ke.getKeyCode() == KeyEvent.VK_TAB)
                  //               {
                  //                  String text = textField.getText();
                  //
                  //                  if(text.length() > 0)
                  //                  {
                  //                     int index = text.lastIndexOf(' ');
                  //                     int index2 = text.lastIndexOf(',');
                  //
                  //                     if(index2 > index)
                  //                     {
                  //                        index = index2;
                  //                     }
                  //
                  //                     String searchString;
                  //
                  //                     if(index != -1)
                  //                     {
                  //                        searchString = text.substring(index + 1).toLowerCase();
                  //                     }
                  //                     else
                  //                     {
                  //                        searchString = text.toLowerCase();
                  //                     }
                  //
                  //                     String treffer = "";
                  //                     int    count = 0;
                  //
                  //                     synchronized(usernameList)
                  //                     {
                  //                        User[] users = usernameList.getValues();
                  //                        String compareValue;
                  //
                  //                        for(int i = 0; i < users.length; i++)
                  //                        {
                  //                           if(users[i].getName().toLowerCase().startsWith(searchString))
                  //                           {
                  //                              treffer += users[i].getName() + " ";
                  //                              count++;
                  //                           }
                  //                        }
                  //                     }
                  //
                  //                     if(treffer.length() > 0)
                  //                     {
                  //                        treffer = treffer.substring(0, treffer.length() - 1);
                  //                        if(count == 1)
                  //                        {
                  //                           if(index != -1)
                  //                           {
                  //                              String newText = text.subSequence(0, index + 1) + treffer;
                  //
                  //                              textField.setText(newText);
                  //                           }
                  //                           else
                  //                           {
                  //                              textField.setText(treffer);
                  //                           }
                  //                        }
                  //                        else if(count > 1)
                  //                        {
                  //                           updateTextArea("\t" + treffer, false);
                  //                        }
                  //                     }
                  //                  }
                  //               }
                  if(befehlPos != -1)
                  {
                     if(ke.getKeyCode() == KeyEvent.VK_UP)
                     {
                        getMultiUserChatPanel().getTextField().setText(befehle.get(befehlPos));
                        if(befehlPos > 0)
                        {
                           befehlPos--;
                        }
                     }
                     else if(ke.getKeyCode() == KeyEvent.VK_DOWN)
                     {
                        if(befehlPos < befehle.size() - 1)
                        {
                           befehlPos++;
                        }

                        getMultiUserChatPanel().getTextField().setText(befehle.get(befehlPos));
                     }
                  }
               }
            });
         getMultiUserChatPanel().getCloseButton().addActionListener(this);
         getMultiUserChatPanel().getTextField().addActionListener(this);
      }

      return multiUserChatPanel;
   }

   public synchronized void updateTextArea(String message, boolean withTimeStamp, Date stamp)
   {
      Document           doc              = getMultiUserChatPanel().getTextArea().getDocument();
      int                oldCaretPosition = getMultiUserChatPanel().getTextArea().getCaretPosition();
      SimpleAttributeSet attributes       = new SimpleAttributeSet();

      StyleConstants.setBackground(attributes, Color.WHITE);
      StyleConstants.setForeground(attributes, Color.BLACK);
      int     index        = message.indexOf('>');
      String  compareValue;
      boolean eigenerName = false;
      boolean doMark      = false;

      if(index != -1 && message.length() - 1 > index)
      {
         compareValue = message.substring(index + 1);
      }
      else
      {
         compareValue = message;
      }

      boolean isAction = false;

      //      if(compareValue.indexOf(parentPanel.getNickname()) != -1)
      //      {
      //         StyleConstants.setBackground(attributes, Color.ORANGE);
      //         eigenerName = true;
      //         doMark = true;
      //      }
      //      if(message.indexOf("---> JOIN:") != -1)
      //      {
      //         StyleConstants.setForeground(attributes, JOIN_GREEN);
      //      }
      //      else if(message.indexOf("<--- PART:") != -1)
      //      {
      //         StyleConstants.setForeground(attributes, Color.RED);
      //      }
      //      else if(message.indexOf("<--- QUIT:") != -1)
      //      {
      //         StyleConstants.setForeground(attributes, Color.RED);
      //      }
      //      else if(message.indexOf('>') == 0)
      //      {
      //         StyleConstants.setForeground(attributes, Color.MAGENTA);
      //      }
      //      else if(message.startsWith("* "))
      //      {
      //         isAction = true;
      //      }
      //      else if(!withTimeStamp || index == -1)
      //      {
      //         StyleConstants.setForeground(attributes, Color.GRAY);
      //      }
      if(withTimeStamp)
      {
         StyleConstants.setForeground(attributes, Color.BLACK);
         doMark = true;
      }
      else
      {
         StyleConstants.setForeground(attributes, Color.GRAY);
      }

      try
      {
         if(withTimeStamp)
         {
            String zeit;

            if(null != stamp)
            {
               zeit = dateFormatter.format(stamp);
            }
            else
            {
               zeit = dateFormatter.format(new Date(System.currentTimeMillis()));
            }

            doc.insertString(doc.getLength(), "[" + zeit + "]\t", attributes);
         }

         if(isAction)
         {
            Color color = StyleConstants.getForeground(attributes);

            StyleConstants.setForeground(attributes, Color.MAGENTA);
            doc.insertString(doc.getLength(), "\t*", attributes);
            StyleConstants.setForeground(attributes, color);
            message = message.substring(1).trim();
         }

         parseLinks(attributes, doc, message);
      }
      catch(BadLocationException blE)
      {
         logger.error("Fehler im JabberPlugin", blE);
      }

      if(!selected && doMark)
      {
         if(eigenerName)
         {

            //            for(int i = 0; i < tabbedPane.getTabCount(); i++)
            //            {
            //               Object tab = tabbedPane.getComponentAt(i);
            //
            //               if(tab == this)
            //               {
            //                  tabbedPane.setForegroundAt(i, Color.GREEN);
            //               }
            //            }
         }

         if(!eigenerName && !marked)
         {

            //            for(int i = 0; i < tabbedPane.getTabCount(); i++)
            //            {
            //               Object tab = tabbedPane.getComponentAt(i);
            //
            //               if(tab == this)
            //               {
            //                  tabbedPane.setForegroundAt(i, Color.RED);
            //               }
            //            }
         }

         marked = true;
      }

      final int newCaretPosition = doc.getLength();

      if(newCaretPosition != oldCaretPosition)
      {
         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  getMultiUserChatPanel().getTextArea().setCaretPosition(newCaretPosition);
               }
            });
      }
   }

   public void updateTextArea(String message)
   {
      updateTextArea(message, true, null);
   }

   private void parseLinks(SimpleAttributeSet attributes, Document doc, String message)
                    throws BadLocationException
   {
      while(message.toLowerCase().indexOf(MultiUserChatController.HTTP_IDENTIFIER) != -1 ||
               message.toLowerCase().indexOf(MultiUserChatController.WWW_IDENTIFIER) != -1)
      {
         String httpIdentifier;
         int    indexHttp = message.toLowerCase().indexOf(HTTP_IDENTIFIER);
         int    indexWww  = message.toLowerCase().indexOf(WWW_IDENTIFIER);

         if(indexHttp == -1)
         {
            httpIdentifier = WWW_IDENTIFIER;
         }
         else if(indexWww == -1)
         {
            httpIdentifier = HTTP_IDENTIFIER;
         }
         else
         {
            if(indexWww == indexHttp + HTTP_IDENTIFIER.length())
            {
               httpIdentifier = HTTP_IDENTIFIER;
            }
            else
            {
               httpIdentifier = WWW_IDENTIFIER;
            }
         }

         doc.insertString(doc.getLength(), message.substring(0, message.toLowerCase().indexOf(httpIdentifier)), attributes);
         message = message.substring(message.toLowerCase().indexOf(httpIdentifier));
         int index  = message.indexOf(" ");
         int index2 = message.indexOf(">");

         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         index2 = message.indexOf(")");
         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         index2 = message.indexOf("]");
         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         index2 = message.indexOf("}");
         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         if(index != -1)
         {
            doc.insertString(doc.getLength(), message.substring(0, index), setLink(message.substring(0, index), attributes));
            message = message.substring(index);
            if(message.indexOf(httpIdentifier, index) == -1)
            {
               break;
            }
         }
         else
         {
            doc.insertString(doc.getLength(), message, setLink(message, attributes));
            message = "";
         }
      }

      doc.insertString(doc.getLength(), message + "\n", attributes);
   }

   private MutableAttributeSet setLink(String sUrl, SimpleAttributeSet attributeSet)
   {
      MutableAttributeSet a = new SimpleAttributeSet(attributeSet);

      try
      {
         if(!sUrl.toLowerCase().startsWith(MultiUserChatController.HTTP_IDENTIFIER))
         {
            sUrl = MultiUserChatController.HTTP_IDENTIFIER + sUrl;
         }

         URL url = new URL(sUrl);

         a.addAttribute(LinkEditorKit.LINK, new URL(sUrl));
      }
      catch(Exception e)
      {
         ;

         // nothing to do
      }

      a.addAttribute(StyleConstants.Bold, Boolean.TRUE);
      a.addAttribute(StyleConstants.Underline, Boolean.TRUE);
      return a;
   }

   private synchronized void setTitle(final String title)
   {
      SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               StyledDocument     doc        = getMultiUserChatPanel().getTitleArea().getStyledDocument();
               SimpleAttributeSet attributes = new SimpleAttributeSet();

               StyleConstants.setBackground(attributes, Color.WHITE);
               try
               {
                  doc.remove(0, doc.getLength());
                  int startIndex = 0;

                  for(int i = 0; i < title.length(); i++)
                  {
                     if(title.charAt(i) == 3 || i == title.length() - 1)
                     {
                        if(title.charAt(i) == 3)
                        {
                           String toWrite = title.substring(startIndex, i);

                           if(toWrite.length() > 0)
                           {
                              attributes = writeString(doc, attributes, toWrite);
                           }

                           startIndex = i + 1;
                           i++;
                        }
                        else
                        {
                           String toWrite = title.substring(startIndex);

                           if(toWrite.length() > 0)
                           {
                              attributes = writeString(doc, attributes, toWrite);
                           }
                        }
                     }
                  }
               }
               catch(BadLocationException ex)
               {
                  ex.printStackTrace();
               }
            }
         });
   }

   private SimpleAttributeSet writeString(StyledDocument doc, SimpleAttributeSet attributes, String toWrite)
   {
      boolean istNachkomma = false;
      boolean parsEnde = false;
      int     index    = 0;

      if(toWrite.length() > 1)
      {
         while(!parsEnde)
         {
            try
            {
               if(toWrite.charAt(index) == ',')
               {
                  istNachkomma = true;
                  index++;
                  continue;
               }

               int colorCode = Integer.parseInt(toWrite.substring(index, index + 2));

               index += 2;
               Color color = getColor(colorCode);

               if(!istNachkomma)
               {
                  StyleConstants.setForeground(attributes, color);
               }
            }
            catch(NumberFormatException nfE)
            {
               if(toWrite.charAt(index + 1) != ',')
               {
                  parsEnde = true;
               }

               try
               {
                  int colorCode = Integer.parseInt(toWrite.substring(index, index + 1));

                  index++;
                  Color color = getColor(colorCode);

                  if(!istNachkomma)
                  {
                     StyleConstants.setForeground(attributes, color);
                  }
               }
               catch(NumberFormatException nfE2)
               {
                  parsEnde = true;
                  StyleConstants.setForeground(attributes, Color.BLACK);
               }
            }
            catch(StringIndexOutOfBoundsException sioobE)
            {
               parsEnde = true;
               try
               {
                  int   colorCode = Integer.parseInt(toWrite.substring(index, index + 1));
                  Color color = getColor(colorCode);

                  if(!istNachkomma)
                  {
                     StyleConstants.setForeground(attributes, color);
                  }
               }
               catch(NumberFormatException nfE2)
               {
                  StyleConstants.setForeground(attributes, Color.BLUE);
               }
               catch(StringIndexOutOfBoundsException nfE2)
               {
                  StyleConstants.setForeground(attributes, Color.BLACK);
               }

               return attributes;
            }
         }
      }

      try
      {
         doc.insertString(doc.getLength(), toWrite.substring(index), attributes);
      }
      catch(BadLocationException ex)
      {
         ex.printStackTrace();
      }

      return attributes;
   }

   private Color getColor(int code)
   {
      switch(code)
      {

         case 0:
            return Color.BLACK;

         case 1:
            return Color.BLACK;

         case 2:
            return Color.BLUE;

         case 3:
            return Color.GREEN;

         case 4:
            return Color.RED;

         case 5:
            return Color.BLACK;

         case 6:
            return Color.PINK;

         case 7:
            return Color.ORANGE;

         case 8:
            return Color.YELLOW;

         case 9:
            return Color.GREEN;

         case 10:
            return Color.GREEN;

         case 11:
            return Color.CYAN;

         case 12:
            return Color.BLUE;

         case 13:
            return Color.PINK;

         case 14:
            return Color.GRAY;

         case 15:
            return Color.LIGHT_GRAY;

         default:
            return Color.BLACK;
      }
   }

   public void actionPerformed(ActionEvent e)
   {
      Object source = e.getSource();

      if(source == getMultiUserChatPanel().getTextField())
      {
         final String messageText = getMultiUserChatPanel().getTextField().getText();

         getMultiUserChatPanel().getTextField().setText("");

         if(null != messageText && messageText.length() > 0)
         {
            befehle.add(messageText);
            if(befehle.size() > 40)
            {
               befehle.remove(0);
            }

            befehlPos = befehle.size() - 1;
         }

         new Thread(new Runnable()
            {
               public void run()
               {

                  //                Message message = muc.createMessage();
                  //                message.setBody(messageText);
                  try
                  {
                     muc.sendMessage(messageText);
                  }
                  catch(XMPPException e)
                  {
                     e.printStackTrace();
                  }
               }
            }).start();
      }
      else if(source == getMultiUserChatPanel().getCloseButton())
      {
         logger.debug("todo: close room");
      }
   }

   public void subjectUpdated(final String subject, String from)
   {
      setTitle(subject);
   }

   public JScrollPane getUserListPane()
   {
      if(null == userScrollPane)
      {
         userScrollPane = new JScrollPane(getUserList());
      }

      return userScrollPane;
   }

   private MultiUserChatUserListModel getMultiUserChatUserListModel()
   {
      if(null == multiUserChatUserListModel)
      {
         multiUserChatUserListModel = new MultiUserChatUserListModel();

      }

      return multiUserChatUserListModel;
   }

   private JList getUserList()
   {
      if(null == userList)
      {
         userList = new JList(getMultiUserChatUserListModel());
         userList.setCellRenderer(new MultiUserChatListCellRenderer());

      }

      return userList;
   }

   private void updateUserList()
   {
      SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               getUserList().updateUI();
            }
         });
   }

   private class MyParticipantListener extends DefaultParticipantStatusListener
   {
      public MyParticipantListener(MultiUserChat muc)
      {
         muc.addParticipantStatusListener(this);
      }

      public void banned(String arg0, String arg1, String arg2)
      {
      }

      public void joined(String participant)
      {
         participant = participant.substring(participant.indexOf("/") + 1);
         getMultiUserChatUserListModel().addParticipant(new MutliUserChatUser(participant));
         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  getUserList().updateUI();
               }
            });
      }

      public void kicked(String participant, String arg1, String arg2)
      {
         removeParticipant(participant);
      }

      private void removeParticipant(String participant)
      {
         participant = participant.substring(participant.indexOf("/") + 1);
         MutliUserChatUser user = getMultiUserChatUserListModel().getMutliUserChatUserByName(participant);

         if(null == user)
         {
            return;
         }

         getMultiUserChatUserListModel().removeParticipant(user);
         updateUserList();
      }

      public void left(String participant)
      {
         removeParticipant(participant);
      }

      public void nicknameChanged(String arg0, String arg1)
      {
      }
   }


   private class MyMessageListener implements PacketListener
   {
      public MyMessageListener(MultiUserChat muc)
      {
         muc.addMessageListener(this);
      }

      public void processPacket(Packet packet)
      {
         final Message message = (Message) packet;

         SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  String from  = message.getFrom();
                  int    index = message.getFrom().indexOf("/");

                  if(index != -1)
                  {
                     from = from.substring(index + 1);
                     Iterator it    = message.getExtensions();
                     Date     stamp = null;

                     while(it.hasNext())
                     {
                        PacketExtension ext = (PacketExtension) it.next();

                        if(ext instanceof DelayInformation)
                        {
                           stamp = ((DelayInformation) ext).getStamp();
                           break;
                        }
                     }

                     updateTextArea(from + ": " + message.getBody(), true, stamp);
                  }
                  else
                  {
                     updateTextArea(message.getBody(), false, null);
                  }
               }
            });
      }
   }
}
